package org.beigesoft.accounting.service;

/*
 * Copyright (c) 2016 Beigesoft ™
 *
 * Licensed under the GNU General Public License (GPL), Version 2.0
 * (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html
 */

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.math.BigDecimal;

import org.beigesoft.service.ISrvOrm;
import org.beigesoft.service.ISrvDatabase;
import org.beigesoft.exception.ExceptionWithCode;
import org.beigesoft.accounting.model.ETaxType;
import org.beigesoft.accounting.persistable.EmployeeYearWage;
import org.beigesoft.accounting.persistable.Wage;
import org.beigesoft.accounting.persistable.WageType;
import org.beigesoft.accounting.persistable.WageLine;
import org.beigesoft.accounting.persistable.WageTaxLine;
import org.beigesoft.accounting.persistable.WageTaxTable;
import org.beigesoft.accounting.persistable.WageTaxTableLine;
import org.beigesoft.accounting.persistable.WageTaxTableEmployee;
import org.beigesoft.accounting.persistable.WageTaxTableType;

/**
 * <pre>
 * Wide used Wage Tax Table Percentage method.
 * </pre>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class SrvWageTaxPercentageTable<RS> implements ISrvFillWageLines {

  /**
   * <p>ORM service.</p>
   **/
  private ISrvOrm<RS> srvOrm;

  /**
   * <p>Database service.</p>
   **/
  private ISrvDatabase<RS> srvDatabase;

  /**
   * <p>Business service for accounting settings.</p>
   **/
  private ISrvAccSettings srvAccSettings;

  /**
   * <p>minimum constructor.</p>
   **/
  public SrvWageTaxPercentageTable() {
  }

  /**
   * <p>Useful constructor.</p>
   * @param pSrvOrm ORM service
   * @param pSrvDatabase Database service
   * @param pSrvAccSettings AccSettings service
   **/
  public SrvWageTaxPercentageTable(final ISrvOrm<RS> pSrvOrm,
    final ISrvDatabase<RS> pSrvDatabase,
      final ISrvAccSettings pSrvAccSettings) {
    this.srvDatabase = pSrvDatabase;
    this.srvOrm = pSrvOrm;
    this.srvAccSettings = pSrvAccSettings;
  }

  /**
   * <p>Fill wage tax lines according Table Percentage method.</p>
   * @param pAddParam additional param
   * @param pWage Wage document
   * @throws Exception - an exception
   **/
  @Override
  public final void fillWageLines(final Map<String, Object> pAddParam,
    final Wage pWage) throws Exception {
    List<WageTaxTableEmployee> wttel = getSrvOrm()
      .retrieveListWithConditions(pAddParam, WageTaxTableEmployee.class,
        "where EMPLOYEE=" + pWage.getEmployee().getItsId());
    if (wttel != null && wttel.size() > 0) {
      String queryTotalWageYear =
        "select sum(TOTALWAGEYEAR) as TOTALWAGEYEAR from "
          + EmployeeYearWage.class.getSimpleName().toUpperCase()
            + " where ITSOWNER=" + pWage.getEmployee().getItsId();
      Double totalWageYearDbl = getSrvDatabase()
        .evalDoubleResult(queryTotalWageYear, "TOTALWAGEYEAR");
      if (totalWageYearDbl == null) {
        totalWageYearDbl = 0d;
      }
      BigDecimal totalWageYear = BigDecimal.valueOf(totalWageYearDbl);
      getSrvOrm().deleteEntityWhere(pAddParam, WageTaxLine.class,
        "ITSOWNER=" + pWage.getItsId());
      BigDecimal bigDecimal100 = new BigDecimal("100.00");
      BigDecimal totalTaxesEmployee = BigDecimal.ZERO;
      BigDecimal totalTaxesEmployer = BigDecimal.ZERO;
      //BeigeORM refresh lines:
      WageLine wlfr = new WageLine();
      wlfr.setItsOwner(pWage);
      String ownerFldName = "itsOwner";
      pWage.setItsLines(getSrvOrm()
        .retrieveListForField(pAddParam, wlfr, ownerFldName));
      Map<WageType, BigDecimal> empleeTotTaxLnMap =
        new  HashMap<WageType, BigDecimal>();
      for (WageLine wl : pWage.getItsLines()) {
        empleeTotTaxLnMap.put(wl.getWageType(), BigDecimal.ZERO);
      }
      for (WageTaxTableEmployee wtte : wttel) {
        //BeigeORM refresh lines:
        wtte.getItsOwner().setTax(getSrvOrm()
          .retrieveEntity(pAddParam, wtte.getItsOwner().getTax()));
        //BeigeORM refresh lines:
        WageTaxTableLine wttlfr = new WageTaxTableLine();
        wttlfr.setItsOwner(wtte.getItsOwner());
        wtte.getItsOwner().setItsLines(getSrvOrm()
          .retrieveListForField(pAddParam, wttlfr, ownerFldName));
        //BeigeORM refresh lines:
        WageTaxTableType wttt = new WageTaxTableType();
        wttt.setItsOwner(wtte.getItsOwner());
        wtte.getItsOwner().setWageTypes(getSrvOrm()
          .retrieveListForField(pAddParam, wttt, ownerFldName));
        BigDecimal totalTaxableForTax = BigDecimal.ZERO;
        for (WageLine wl : pWage.getItsLines()) {
          if (isWageApplied(wl.getWageType(), wtte.getItsOwner())) {
            totalTaxableForTax = totalTaxableForTax.add(wl.getGrossWage());
          }
        }
        if (totalTaxableForTax.doubleValue() > 0) {
          BigDecimal wageMinusAllowance = totalTaxableForTax
            .subtract(wtte.getAllowance());
          boolean isFilled = false;
          for (WageTaxTableLine wttl : wtte.getItsOwner().getItsLines()) {
            if (wageMinusAllowance.compareTo(wttl.getWageFrom()) >= 0
              && wageMinusAllowance.compareTo(wttl.getWageTo()) < 0
                && totalWageYear
                  .compareTo(wttl.getYearWageFrom()) >= 0
                    && totalWageYear
                      .compareTo(wttl.getYearWageTo()) < 0) {
              WageTaxLine wtl = new WageTaxLine();
              wtl.setIsNew(true);
              wtl.setIdDatabaseBirth(getSrvOrm().getIdDatabase());
              wtl.setItsOwner(pWage);
              wtl.setAllowance(wtte.getAllowance());
              wtl.setPlusAmount(wttl.getPlusAmount());
              wtl.setTax(wtte.getItsOwner().getTax());
              wtl.setItsPercentage(wttl.getItsPercentage());
              wtl.setItsTotal(wageMinusAllowance.subtract(wttl.getAllowance())
                .multiply(wttl.getItsPercentage()).divide(bigDecimal100,
                  getSrvAccSettings().lazyGetAccSettings(pAddParam)
                    .getPricePrecision(), getSrvAccSettings()
                      .lazyGetAccSettings(pAddParam).getRoundingMode())
                        .add(wttl.getPlusAmount()));
              wtl.setDescription("TableID/Name/taxable: " + wtte.getItsOwner()
                .getItsId() + "/" + wtte.getItsOwner().getItsName() + "/"
                  + totalTaxableForTax);
              getSrvOrm().insertEntity(pAddParam, wtl);
              if (wtl.getTax().getItsType()
                .equals(ETaxType.EMPLOYMENT_TAX_EMPLOYEE)) {
                totalTaxesEmployee = totalTaxesEmployee.add(wtl.getItsTotal());
                for (WageLine wl : pWage.getItsLines()) {
                  BigDecimal newTotalTaxEmpleeLn = empleeTotTaxLnMap.get(wl
                    .getWageType()).add(wl.getGrossWage().multiply(wtl
                      .getItsTotal()).divide(totalTaxableForTax,
                        getSrvAccSettings().lazyGetAccSettings(pAddParam)
                          .getPricePrecision(), getSrvAccSettings()
                            .lazyGetAccSettings(pAddParam).getRoundingMode()));
                  empleeTotTaxLnMap.put(wl.getWageType(), newTotalTaxEmpleeLn);
                }
              } else if (wtl.getTax().getItsType()
                .equals(ETaxType.EMPLOYMENT_TAX_EMPLOYER)) {
                totalTaxesEmployer = totalTaxesEmployer.add(wtl.getItsTotal());
              }
              isFilled = true;
              break;
            }
          }
          if (!isFilled) {
            throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
              "where_is_no_suitable_tax_percent_entry_for::"
                + wtte.getItsOwner().getTax().getItsName());
          }
        }
      }
      for (WageLine wl : pWage.getItsLines()) {
        wl.setTaxesEmployee(empleeTotTaxLnMap.get(wl.getWageType()));
        getSrvOrm().updateEntity(pAddParam, wl);
      }
      pWage.setTotalTaxesEmployee(totalTaxesEmployee);
      pWage.setTotalTaxesEmployer(totalTaxesEmployer);
      pWage.setNetWage(pWage.getItsTotal()
        .subtract(pWage.getTotalTaxesEmployee()));
      getSrvOrm().updateEntity(pAddParam, pWage);
    }
  }

  /**
   * <p>Check if wage type applied for tax.</p>
   * @param pWageType Wage Type
   * @param pWageTaxTable Wage Tax Table
   * @return is applied
   **/
  public final boolean isWageApplied(final WageType pWageType,
    final WageTaxTable pWageTaxTable) {
    for (WageTaxTableType wttt : pWageTaxTable.getWageTypes()) {
      if (wttt.getWageType().getItsId().equals(pWageType.getItsId())) {
        return true;
      }
    }
    return false;
  }

  //Simple getters and setters:
  /**
   * <p>Getter for srvOrm.</p>
   * @return ISrvOrm<RS>
   **/
  public final ISrvOrm<RS> getSrvOrm() {
    return this.srvOrm;
  }

  /**
   * <p>Setter for srvOrm.</p>
   * @param pSrvOrm reference
   **/
  public final void setSrvOrm(final ISrvOrm<RS> pSrvOrm) {
    this.srvOrm = pSrvOrm;
  }

  /**
   * <p>Geter for srvDatabase.</p>
   * @return ISrvDatabase
   **/
  public final ISrvDatabase<RS> getSrvDatabase() {
    return this.srvDatabase;
  }

  /**
   * <p>Setter for srvDatabase.</p>
   * @param pSrvDatabase reference
   **/
  public final void setSrvDatabase(final ISrvDatabase<RS> pSrvDatabase) {
    this.srvDatabase = pSrvDatabase;
  }

  /**
   * <p>Getter for srvAccSettings.</p>
   * @return ISrvAccSettings
   **/
  public final ISrvAccSettings getSrvAccSettings() {
    return this.srvAccSettings;
  }

  /**
   * <p>Setter for srvAccSettings.</p>
   * @param pSrvAccSettings reference
   **/
  public final void setSrvAccSettings(final ISrvAccSettings pSrvAccSettings) {
    this.srvAccSettings = pSrvAccSettings;
  }
}

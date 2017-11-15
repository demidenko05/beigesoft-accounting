package org.beigesoft.accounting.processor;

/*
 * Copyright (c) 2017 Beigesoft ™
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
import java.util.List;

import org.beigesoft.factory.IFactoryAppBeans;
import org.beigesoft.model.IRequestData;
import org.beigesoft.accounting.persistable.Wage;
import org.beigesoft.accounting.persistable.WageLine;
import org.beigesoft.accounting.persistable.EmployeeYearWage;
import org.beigesoft.accounting.service.ISrvFillWageLines;

/**
 * <p>Service that save Wage into DB.</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class PrcWageSave<RS> extends APrcAccDocSave<RS, Wage> {

  /**
   * <p>App beans factort.</p>
   **/
  private IFactoryAppBeans factoryAppBeans;

  //To override:
  /**
   * <p>Make save preparations before insert/update block if it's need.</p>
   * @param pAddParam additional param
   * @param pEntity entity
   * @param pRequestData Request Data
   * @throws Exception - an exception
   **/
  @Override
  public final void makeFirstPrepareForSave(final Map<String, Object> pAddParam,
    final Wage pEntity, final IRequestData pRequestData) throws Exception {
    // nothing
  }

  /**
   * <p>Make other entries include reversing if it's need when save.</p>
   * @param pAddParam additional param
   * @param pEntity entity
   * @param pRequestData Request Data
   * @param pIsNew if entity was new
   * @throws Exception - an exception
   **/
  @Override
  public final void makeOtherEntries(final Map<String, Object> pAddParam,
    final Wage pEntity, final IRequestData pRequestData,
      final boolean pIsNew) throws Exception {
    String actionAdd = pRequestData.getParameter("actionAdd");
    if ("fill".equals(actionAdd)) {
      //User can change method as he want
      String srvFillWgLnNm = getSrvAccSettings().lazyGetAccSettings(pAddParam)
        .getWageTaxesMethod().getServiceName();
      ISrvFillWageLines srvFillWageLines = (ISrvFillWageLines) this
        .factoryAppBeans.lazyGet(srvFillWgLnNm);
      srvFillWageLines.fillWageLines(pAddParam, pEntity);
    } else if ("makeAccEntries".equals(actionAdd)) {
      if (pEntity.getReversedId() == null) {
        WageLine wl = new WageLine();
        wl.setItsOwner(pEntity);
        List<WageLine> wageLines = getSrvOrm().
          retrieveListForField(pAddParam, wl, "itsOwner");
        for (WageLine wageLine : wageLines) {
          String whereStr = " where ITSOWNER=" + pEntity.getEmployee()
            .getItsId() + " and WAGETYPE=" + wageLine.getWageType().getItsId();
          EmployeeYearWage employeeYearWage = getSrvOrm()
            .retrieveEntityWithConditions(pAddParam,
              EmployeeYearWage.class, whereStr);
          if (employeeYearWage == null) {
            employeeYearWage = new EmployeeYearWage();
            employeeYearWage.setItsOwner(pEntity.getEmployee());
            employeeYearWage.setIsNew(true);
            employeeYearWage.setIdDatabaseBirth(getSrvOrm().getIdDatabase());
            employeeYearWage.setWageType(wageLine.getWageType());
          }
          employeeYearWage.setTotalWageYear(employeeYearWage.getTotalWageYear()
            .add(wageLine.getGrossWage())
              .subtract(wageLine.getTaxesEmployee()));
          if (employeeYearWage.getIsNew()) {
            getSrvOrm().insertEntity(pAddParam, employeeYearWage);
          } else {
            getSrvOrm().updateEntity(pAddParam, employeeYearWage);
          }
        }
      } else {
        WageLine wl = new WageLine();
        Wage reversed = getSrvOrm().
          retrieveEntityById(pAddParam, Wage.class, pEntity.getReversedId());
        wl.setItsOwner(reversed);
        List<WageLine> wageLines = getSrvOrm().
          retrieveListForField(pAddParam, wl, "itsOwner");
        for (WageLine wageLine : wageLines) {
          String whereStr = " where ITSOWNER=" + pEntity.getEmployee()
            .getItsId() + " and WAGETYPE=" + wageLine.getWageType().getItsId();
          EmployeeYearWage employeeYearWage = getSrvOrm()
            .retrieveEntityWithConditions(pAddParam,
              EmployeeYearWage.class, whereStr);
          employeeYearWage.setTotalWageYear(employeeYearWage.getTotalWageYear()
            .subtract(wageLine.getGrossWage())
              .add(wageLine.getTaxesEmployee()));
          getSrvOrm().updateEntity(pAddParam, employeeYearWage);
        }
      }
    }
  }

  /**
   * <p>Check other fraud update e.g. prevent change completed unaccounted
   * manufacturing process.</p>
   * @param pAddParam additional param
   * @param pEntity entity
   * @param pRequestData Request Data
   * @param pOldEntity old saved entity
   * @throws Exception - an exception
   **/
  @Override
  public final void checkOtherFraudUpdate(final Map<String, Object> pAddParam,
    final Wage pEntity, final IRequestData pRequestData,
      final Wage pOldEntity) throws Exception {
    // nothing
  }

  /**
   * <p>Additional check document for ready to account (make acc.entries).</p>
   * @param pAddParam additional param
   * @param pEntity entity
   * @param pRequestData Request Data
   * @throws Exception - an exception if don't
   **/
  @Override
  public final void addCheckIsReadyToAccount(
    final Map<String, Object> pAddParam,
      final Wage pEntity,
        final IRequestData pRequestData) throws Exception {
    // nothing
  }

  //Simple getters and setters:
  /**
   * <p>Getter for factoryAppBeans.</p>
   * @return IFactoryAppBeans
   **/
  public final IFactoryAppBeans getFactoryAppBeans() {
    return this.factoryAppBeans;
  }

  /**
   * <p>Setter for factoryAppBeans.</p>
   * @param pFactoryAppBeans reference
   **/
  public final void setFactoryAppBeans(
    final IFactoryAppBeans pFactoryAppBeans) {
    this.factoryAppBeans = pFactoryAppBeans;
  }
}

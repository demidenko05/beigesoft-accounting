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
import java.math.BigDecimal;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.beigesoft.model.IRecordSet;
import org.beigesoft.model.IRequestData;
import org.beigesoft.exception.ExceptionWithCode;
import org.beigesoft.service.IEntityProcessor;
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.service.ISrvDatabase;
import org.beigesoft.service.ISrvNumberToString;
import org.beigesoft.accounting.model.ETaxType;
import org.beigesoft.accounting.persistable.SalesReturn;
import org.beigesoft.accounting.persistable.SalesReturnLine;
import org.beigesoft.accounting.persistable.SalesReturnTaxLine;
import org.beigesoft.accounting.persistable.Tax;
import org.beigesoft.accounting.persistable.InvItemTaxCategoryLine;
import org.beigesoft.accounting.service.ISrvWarehouseEntry;
import org.beigesoft.accounting.service.ISrvAccSettings;

/**
 * <p>Service that save SalesReturnLine into DB.</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class PrcSalesReturnLineSave<RS>
  implements IEntityProcessor<SalesReturnLine, Long> {

  /**
   * <p>Database service.</p>
   **/
  private ISrvDatabase<RS> srvDatabase;

  /**
   * <p>Query Vendor Invoice Line Taxes.</p>
   **/
  private String querySalesReturnLineTaxes;

  /**
   * <p>ORM service.</p>
   **/
  private ISrvOrm<RS> srvOrm;

  /**
   * <p>Business service for warehouse.</p>
   **/
  private ISrvWarehouseEntry srvWarehouseEntry;

  /**
   * <p>Business service for accounting settings.</p>
   **/
  private ISrvAccSettings srvAccSettings;

  /**
   * <p>Service print number.</p>
   **/
  private ISrvNumberToString srvNumberToString;

  /**
   * <p>Process entity request.</p>
   * @param pAddParam additional param, e.g. return this line's
   * document in "nextEntity" for farther process
   * @param pRequestData Request Data
   * @param pEntity Entity to process
   * @return Entity processed for farther process or null
   * @throws Exception - an exception
   **/
  @Override
  public final SalesReturnLine process(
    final Map<String, Object> pAddParam,
      final SalesReturnLine pEntity,
        final IRequestData pRequestData) throws Exception {
    if (pEntity.getIsNew()) {
      // Beige-Orm refresh:
      pEntity.setItsOwner(getSrvOrm()
        .retrieveEntity(pAddParam, pEntity.getItsOwner()));
      // optimistic locking (dirty check):
      Long ownerVersion = Long.valueOf(pRequestData
        .getParameter(SalesReturn.class.getSimpleName() + ".ownerVersion"));
      pEntity.getItsOwner().setItsVersion(ownerVersion);
      if (pEntity.getReversedId() != null) {
        SalesReturnLine reversed = getSrvOrm().retrieveEntityById(pAddParam,
          SalesReturnLine.class, pEntity.getReversedId());
        if (reversed.getReversedId() != null) {
          throw new ExceptionWithCode(ExceptionWithCode.FORBIDDEN,
            "Attempt to double reverse" + pAddParam.get("user"));
        }
        if (!reversed.getItsQuantity().equals(reversed.getTheRest())) {
          throw new ExceptionWithCode(ExceptionWithCode
            .WRONG_PARAMETER, "where_is_withdrawals_from_this_source");
        }
        pEntity.setTheRest(BigDecimal.ZERO);
        pEntity.setInvItem(reversed.getInvItem());
        pEntity.setUnitOfMeasure(reversed.getUnitOfMeasure());
        pEntity.setWarehouseSite(reversed.getWarehouseSite());
        pEntity.setTaxesDescription(reversed.getTaxesDescription());
        pEntity.setTotalTaxes(reversed.getTotalTaxes().negate());
        pEntity.setItsQuantity(reversed.getItsQuantity().negate());
        pEntity.setItsCost(reversed.getItsCost());
        pEntity.setItsPrice(reversed.getItsPrice());
        pEntity.setSubtotal(reversed.getSubtotal().negate());
        pEntity.setItsTotal(reversed.getItsTotal().negate());
        getSrvOrm().insertEntity(pAddParam, pEntity);
        reversed.setTheRest(BigDecimal.ZERO);
        reversed.setReversedId(pEntity.getItsId());
        getSrvOrm().updateEntity(pAddParam, reversed);
      } else {
        if (pEntity.getItsQuantity().doubleValue() == 0) {
          throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
            "quantity_is_0");
        }
        if (pEntity.getItsCost().doubleValue() <= 0) {
          throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
            "cost_less_or_eq_zero" + pAddParam.get("user"));
        }
        if (pEntity.getItsPrice().doubleValue() <= 0) {
          throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
            "price_less_eq_0");
        }
        // Beige-Orm refresh:
        pEntity.setInvItem(getSrvOrm()
          .retrieveEntity(pAddParam, pEntity.getInvItem()));
        //rounding:
        pEntity.setItsQuantity(pEntity.getItsQuantity().setScale(
          getSrvAccSettings().lazyGetAccSettings(pAddParam)
            .getQuantityPrecision(), getSrvAccSettings()
              .lazyGetAccSettings(pAddParam).getRoundingMode()));
        pEntity.setItsPrice(pEntity.getItsPrice().setScale(getSrvAccSettings()
        .lazyGetAccSettings(pAddParam).getPricePrecision(), getSrvAccSettings()
          .lazyGetAccSettings(pAddParam).getRoundingMode()));
        pEntity.setItsCost(pEntity.getItsCost().setScale(getSrvAccSettings()
          .lazyGetAccSettings(pAddParam).getCostPrecision(), getSrvAccSettings()
            .lazyGetAccSettings(pAddParam).getRoundingMode()));
        //without taxes:
        pEntity.setSubtotal(pEntity.getItsQuantity().multiply(pEntity
          .getItsPrice()).setScale(getSrvAccSettings()
            .lazyGetAccSettings(pAddParam).getPricePrecision(),
              getSrvAccSettings().lazyGetAccSettings(pAddParam)
                .getRoundingMode()));
        pEntity.setTheRest(pEntity.getItsQuantity());
        BigDecimal totalTaxes = BigDecimal.ZERO;
        String taxesDescription = "";
        if (!pEntity.getItsOwner().getCustomer().getIsForeigner()
          && getSrvAccSettings().lazyGetAccSettings(pAddParam)
            .getIsExtractSalesTaxFromSales()
              && pEntity.getInvItem().getTaxCategory() != null) {
          List<InvItemTaxCategoryLine> pstl = getSrvOrm()
            .retrieveListWithConditions(pAddParam,
              InvItemTaxCategoryLine.class, "where ITSOWNER="
                + pEntity.getInvItem().getTaxCategory().getItsId());
          BigDecimal bigDecimal100 = new BigDecimal("100.00");
          StringBuffer sb = new StringBuffer();
          int i = 0;
          for (InvItemTaxCategoryLine pst : pstl) {
            if (ETaxType.SALES_TAX_OUTITEM.equals(pst.getTax().getItsType())
              || ETaxType.SALES_TAX_INITEM.equals(pst.getTax().getItsType())) {
              BigDecimal addTx = pEntity.getSubtotal().multiply(pst
                .getItsPercentage()).divide(bigDecimal100, getSrvAccSettings()
                  .lazyGetAccSettings(pAddParam).getPricePrecision(),
                    getSrvAccSettings().lazyGetAccSettings(pAddParam)
                      .getRoundingMode());
              totalTaxes = totalTaxes.add(addTx);
              if (i++ > 0) {
                sb.append(", ");
              }
              sb.append(pst.getTax().getItsName() + " " + pst.getItsPercentage()
                + "%=" + prn(pAddParam, addTx));
            }
          }
          taxesDescription = sb.toString();
        }
        pEntity.setTaxesDescription(taxesDescription);
        pEntity.setTotalTaxes(totalTaxes);
        pEntity.setItsTotal(pEntity.getSubtotal().add(totalTaxes));
        getSrvOrm().insertEntity(pAddParam, pEntity);
      }
      srvWarehouseEntry.load(pAddParam, pEntity, pEntity.getWarehouseSite());
      String query =
        "select sum(SUBTOTAL) as SUBTOTAL, sum(TOTALTAXES) as TOTALTAXES from"
        + " SALESRETURNLINE where ITSOWNER=" + pEntity.getItsOwner().getItsId();
      String[] columns = new String[]{"SUBTOTAL", "TOTALTAXES"};
      Double[] totals = getSrvDatabase().evalDoubleResults(query, columns);
      pEntity.getItsOwner().setSubtotal(BigDecimal.valueOf(totals[0]).setScale(
        getSrvAccSettings().lazyGetAccSettings(pAddParam).getPricePrecision(),
          getSrvAccSettings().lazyGetAccSettings(pAddParam).getRoundingMode()));
      pEntity.getItsOwner().setTotalTaxes(BigDecimal.valueOf(totals[1])
        .setScale(getSrvAccSettings().lazyGetAccSettings(pAddParam)
          .getPricePrecision(), getSrvAccSettings()
            .lazyGetAccSettings(pAddParam).getRoundingMode()));
      pEntity.getItsOwner().setItsTotal(pEntity.getItsOwner().getSubtotal().
        add(pEntity.getItsOwner().getTotalTaxes()));
      getSrvOrm().updateEntity(pAddParam, pEntity.getItsOwner());
      updateTaxLines(pAddParam,  pEntity.getItsOwner());
    } else {
      throw new ExceptionWithCode(ExceptionWithCode.FORBIDDEN,
        "Attempt to update Sales Return line by " + pAddParam.get("user"));
    }
    pAddParam.put("nextEntity", pEntity.getItsOwner());
    pAddParam.put("nameOwnerEntity", SalesReturn.class.getSimpleName());
    return null;
  }

  //Utils:
  /**
   * <p>Lazy get querySalesReturnLineTaxes.</p>
   * @return querySalesReturnLineTaxes
   * @throws Exception - an exception
   **/
  public final String lazyGetQuerySalesReturnLineTaxes() throws Exception {
    if (this.querySalesReturnLineTaxes == null) {
      String flName = "/" + "accounting" + "/" + "trade"
        + "/" + "salesReturnLineTaxes.sql";
      this.querySalesReturnLineTaxes = loadString(flName);
    }
    return this.querySalesReturnLineTaxes;
  }

  /**
   * <p>Load string file (usually SQL query).</p>
   * @param pFileName file name
   * @return String usually SQL query
   * @throws IOException - IO exception
   **/
  public final String loadString(final String pFileName)
        throws IOException {
    URL urlFile = PrcSalesReturnLineSave.class
      .getResource(pFileName);
    if (urlFile != null) {
      InputStream inputStream = null;
      try {
        inputStream = PrcSalesReturnLineSave.class
          .getResourceAsStream(pFileName);
        byte[] bArray = new byte[inputStream.available()];
        inputStream.read(bArray, 0, inputStream.available());
        return new String(bArray, "UTF-8");
      } finally {
        if (inputStream != null) {
          inputStream.close();
        }
      }
    }
    return null;
  }

  /**
   * <p>Update invoice Tax Lines.</p>
   * @param pAddParam additional param
   * @param pItsOwner SalesReturn
   * @throws Exception - an exception
   **/
  public final void updateTaxLines(final Map<String, Object> pAddParam,
    final SalesReturn pItsOwner) throws Exception {
    List<SalesReturnTaxLine> sitl = getSrvOrm().retrieveListWithConditions(
      pAddParam, SalesReturnTaxLine.class, "where ITSOWNER="
        + pItsOwner.getItsId());
    if (!pItsOwner.getCustomer().getIsForeigner()
      && getSrvAccSettings().lazyGetAccSettings(pAddParam)
        .getIsExtractSalesTaxFromSales()) {
      String query = lazyGetQuerySalesReturnLineTaxes().replace(":ITSOWNER",
        pItsOwner.getItsId().toString());
      int countUpdatedSitl = 0;
      IRecordSet<RS> recordSet = null;
      try {
        recordSet = getSrvDatabase().retrieveRecords(query);
        if (recordSet.moveToFirst()) {
          do {
            Long taxId = recordSet.getLong("TAXID");
            Double totalTax = recordSet.getDouble("TOTALTAX");
            SalesReturnTaxLine sit;
            if (sitl.size() > countUpdatedSitl) {
              sit = sitl.get(countUpdatedSitl);
              countUpdatedSitl++;
            } else {
              sit = new SalesReturnTaxLine();
              sit.setIdDatabaseBirth(this.srvOrm.getIdDatabase());
              sit.setItsOwner(pItsOwner);
              sit.setIsNew(true);
            }
            Tax tax = new Tax();
            tax.setItsId(taxId);
            sit.setTax(tax);
            sit.setItsTotal(BigDecimal.valueOf(totalTax).setScale(
              getSrvAccSettings().lazyGetAccSettings(pAddParam)
                .getPricePrecision(), getSrvAccSettings()
                  .lazyGetAccSettings(pAddParam).getRoundingMode()));
            if (sit.getIsNew()) {
              getSrvOrm().insertEntity(pAddParam, sit);
            } else {
              getSrvOrm().updateEntity(pAddParam, sit);
            }
          } while (recordSet.moveToNext());
        }
      } finally {
        if (recordSet != null) {
          recordSet.close();
        }
      }
      if (countUpdatedSitl < sitl.size()) {
        for (int j = countUpdatedSitl; j < sitl.size(); j++) {
          getSrvOrm().deleteEntity(pAddParam, sitl.get(j));
        }
      }
    } else if (sitl.size() > 0) {
      for (SalesReturnTaxLine srtln : sitl) {
        getSrvOrm().deleteEntity(pAddParam, srtln);
      }
    }
  }

  /**
   * <p>Simple delegator to print number.</p>
   * @param pAddParam additional param
   * @param pVal value
   * @return String
   **/
  public final String prn(final Map<String, Object> pAddParam,
    final BigDecimal pVal) {
    return this.srvNumberToString.print(pVal.toString(),
      (String) pAddParam.get("dseparatorv"),
        (String) pAddParam.get("dgseparatorv"),
          (Integer) pAddParam.get("balancePrecision"),
            (Integer) pAddParam.get("digitsInGroup"));
  }

  //Simple getters and setters:
  /**
   * <p>Geter for srvWarehouseEntry.</p>
   * @return ISrvWarehouseEntry
   **/
  public final ISrvWarehouseEntry getSrvWarehouseEntry() {
    return this.srvWarehouseEntry;
  }

  /**
   * <p>Setter for srvWarehouseEntry.</p>
   * @param pSrvWarehouseEntry reference
   **/
  public final void setSrvWarehouseEntry(
    final ISrvWarehouseEntry pSrvWarehouseEntry) {
    this.srvWarehouseEntry = pSrvWarehouseEntry;
  }

  /**
   * <p>Setter for querySalesReturnLineTaxes.</p>
   * @param pQuerySalesReturnLineTaxes reference
   **/
  public final void setQuerySalesReturnLineTaxes(
    final String pQuerySalesReturnLineTaxes) {
    this.querySalesReturnLineTaxes = pQuerySalesReturnLineTaxes;
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

  /**
   * <p>Getter for srvNumberToString.</p>
   * @return ISrvNumberToString
   **/
  public final ISrvNumberToString getSrvNumberToString() {
    return this.srvNumberToString;
  }

  /**
   * <p>Setter for srvNumberToString.</p>
   * @param pSrvNumberToString reference
   **/
  public final void setSrvNumberToString(
    final ISrvNumberToString pSrvNumberToString) {
    this.srvNumberToString = pSrvNumberToString;
  }
}

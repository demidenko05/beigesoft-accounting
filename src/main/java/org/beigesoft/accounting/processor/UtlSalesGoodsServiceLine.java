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

import org.beigesoft.service.ISrvOrm;
import org.beigesoft.service.ISrvDatabase;
import org.beigesoft.model.IRecordSet;
import org.beigesoft.accounting.persistable.SalesInvoice;
import org.beigesoft.accounting.persistable.SalesInvoiceTaxLine;
import org.beigesoft.accounting.persistable.Tax;
import org.beigesoft.accounting.service.ISrvAccSettings;

/**
 * <p>Utility for Sales Invoice Goods/Service Line.
 * It makes total for owner.</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class UtlSalesGoodsServiceLine<RS> {

  /**
   * <p>Database service.</p>
   **/
  private ISrvDatabase<RS> srvDatabase;

  /**
   * <p>ORM service.</p>
   **/
  private ISrvOrm<RS> srvOrm;

  /**
   * <p>File with Query Sales Invoice Taxes.</p>
   **/
  private String fileQuerySalesInvoiceTaxes = "salesInvoiceTaxes.sql";

  /**
   * <p>Query Sales Invoice Taxes.</p>
   **/
  private String querySalesInvoiceTaxes;

  /**
   * <p>File with Query Sales Invoice Totals.</p>
   **/
  private String fileQuerySalesInvoiceTotals = "salesInvoiceTotals.sql";

  /**
   * <p>Query Sales Invoice Totals.</p>
   **/
  private String querySalesInvoiceTotals;

  /**
   * <p>Business service for accounting settings.</p>
   **/
  private ISrvAccSettings srvAccSettings;

  /**
   * <p>Insert immutable line into DB.</p>
   * @param pAddParam additional param
   * @param pItsOwner SalesInvoice
   * @throws Exception - an exception
   **/
  public final void updateOwner(final Map<String, Object> pAddParam,
    final SalesInvoice pItsOwner) throws Exception {
    String query = lazyGetQuerySalesInvoiceTotals();
    query = query.replace(":ITSOWNER", pItsOwner.getItsId().toString());
    String[] columns = new String[]{"SUBTOTAL", "TOTALTAXES"};
    Double[] totals = getSrvDatabase().evalDoubleResults(query, columns);
    if (totals[0] == null) {
      totals[0] = 0d;
    }
    if (totals[1] == null) {
      totals[1] = 0d;
    }
    pItsOwner.setSubtotal(BigDecimal.valueOf(totals[0]).setScale(
      getSrvAccSettings().lazyGetAccSettings(pAddParam).getPricePrecision(),
        getSrvAccSettings().lazyGetAccSettings(pAddParam).getRoundingMode()));
    pItsOwner.setTotalTaxes(BigDecimal.valueOf(totals[1]).setScale(
      getSrvAccSettings().lazyGetAccSettings(pAddParam).getPricePrecision(),
        getSrvAccSettings().lazyGetAccSettings(pAddParam).getRoundingMode()));
    pItsOwner.setItsTotal(pItsOwner.getSubtotal().
      add(pItsOwner.getTotalTaxes()));
    getSrvOrm().updateEntity(pAddParam, pItsOwner);
    if (getSrvAccSettings().lazyGetAccSettings(pAddParam)
      .getIsExtractSalesTaxFromSales()) {
      updateTaxLines(pAddParam, pItsOwner);
    }
  }

  /**
   * <p>Lazy get querySalesInvoiceTaxes.</p>
   * @return querySalesInvoiceTaxes
   * @throws Exception - an exception
   **/
  public final String lazyGetQuerySalesInvoiceTaxes() throws Exception {
    if (this.querySalesInvoiceTaxes == null) {
      String flName = "/accounting/trade/" + this.fileQuerySalesInvoiceTaxes;
      this.querySalesInvoiceTaxes = loadString(flName);
    }
    return this.querySalesInvoiceTaxes;
  }

  /**
   * <p>Lazy get querySalesInvoiceTotals.</p>
   * @return querySalesInvoiceTotals
   * @throws Exception - an exception
   **/
  public final String lazyGetQuerySalesInvoiceTotals() throws Exception {
    if (this.querySalesInvoiceTotals == null) {
      String flName = "/accounting/trade/" + this.fileQuerySalesInvoiceTotals;
      this.querySalesInvoiceTotals = loadString(flName);
    }
    return this.querySalesInvoiceTotals;
  }

  /**
   * <p>Load string file (usually SQL query).</p>
   * @param pFileName file name
   * @return String usually SQL query
   * @throws IOException - IO exception
   **/
  public final String loadString(final String pFileName)
        throws IOException {
    URL urlFile = UtlSalesGoodsServiceLine.class
      .getResource(pFileName);
    if (urlFile != null) {
      InputStream inputStream = null;
      try {
        inputStream = UtlSalesGoodsServiceLine.class
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
   * @param pOwner SalesInvoice
   * @throws Exception - an exception
   **/
  public final void updateTaxLines(final Map<String, Object> pAddParam,
    final SalesInvoice pOwner) throws Exception {
    List<SalesInvoiceTaxLine> sitl = getSrvOrm().retrieveListWithConditions(
        pAddParam, SalesInvoiceTaxLine.class, "where ITSOWNER="
          + pOwner.getItsId());
    String query = lazyGetQuerySalesInvoiceTaxes().replace(":ITSOWNER",
      pOwner.getItsId().toString());
    int countUpdatedSitl = 0;
    IRecordSet<RS> recordSet = null;
    try {
      recordSet = getSrvDatabase().retrieveRecords(query);
      if (recordSet.moveToFirst()) {
        do {
          Long taxId = recordSet.getLong("TAXID");
          Double totalTax = recordSet.getDouble("TOTALTAX");
          SalesInvoiceTaxLine sit;
          if (sitl.size() > countUpdatedSitl) {
            sit = sitl.get(countUpdatedSitl);
            countUpdatedSitl++;
          } else {
            sit = new SalesInvoiceTaxLine();
            sit.setItsOwner(pOwner);
            sit.setIsNew(true);
            sit.setIdDatabaseBirth(this.srvOrm.getIdDatabase());
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
  }

  //Simple getters and setters:
  /**
   * <p>Getter for srvDatabase.</p>
   * @return ISrvDatabase<RS>
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
   * <p>Geter for srvOrm.</p>
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
   * <p>Getter for fileQuerySalesInvoiceTaxes.</p>
   * @return String
   **/
  public final String getFileQuerySalesInvoiceTaxes() {
    return this.fileQuerySalesInvoiceTaxes;
  }

  /**
   * <p>Setter for fileQuerySalesInvoiceTaxes.</p>
   * @param pFileQuerySalesInvoiceTaxes reference
   **/
  public final void setFileQuerySalesInvoiceTaxes(
    final String pFileQuerySalesInvoiceTaxes) {
    this.fileQuerySalesInvoiceTaxes = pFileQuerySalesInvoiceTaxes;
  }

  /**
   * <p>Getter for querySalesInvoiceTaxes.</p>
   * @return String
   **/
  public final String getQuerySalesInvoiceTaxes() {
    return this.querySalesInvoiceTaxes;
  }

  /**
   * <p>Setter for querySalesInvoiceTaxes.</p>
   * @param pQuerySalesInvoiceTaxes reference
   **/
  public final void setQuerySalesInvoiceTaxes(
    final String pQuerySalesInvoiceTaxes) {
    this.querySalesInvoiceTaxes = pQuerySalesInvoiceTaxes;
  }

  /**
   * <p>Getter for fileQuerySalesInvoiceTotals.</p>
   * @return String
   **/
  public final String getFileQuerySalesInvoiceTotals() {
    return this.fileQuerySalesInvoiceTotals;
  }

  /**
   * <p>Setter for fileQuerySalesInvoiceTotals.</p>
   * @param pFileQuerySalesInvoiceTotals reference
   **/
  public final void setFileQuerySalesInvoiceTotals(
    final String pFileQuerySalesInvoiceTotals) {
    this.fileQuerySalesInvoiceTotals = pFileQuerySalesInvoiceTotals;
  }

  /**
   * <p>Getter for querySalesInvoiceTotals.</p>
   * @return String
   **/
  public final String getQuerySalesInvoiceTotals() {
    return this.querySalesInvoiceTotals;
  }

  /**
   * <p>Setter for querySalesInvoiceTotals.</p>
   * @param pQuerySalesInvoiceTotals reference
   **/
  public final void setQuerySalesInvoiceTotals(
    final String pQuerySalesInvoiceTotals) {
    this.querySalesInvoiceTotals = pQuerySalesInvoiceTotals;
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

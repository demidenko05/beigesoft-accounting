package org.beigesoft.accounting.processor;

/*
 * Copyright (c) 2017 Beigesoft™
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
import org.beigesoft.accounting.persistable.AccSettings;
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
   * <p>File with Query Sales Invoice Taxes item basis method.</p>
   **/
  private String fileQuSalInvSaTaxItBas = "salesInvSalTaxItemBasis.sql";

  /**
   * <p>Query Sales Invoice Taxes invoice basis method.</p>
   **/
  private String quSalInvSaTaxItBas;

  /**
   * <p>File with Query Sales Invoice Taxes invoice basis method.</p>
   **/
  private String fileQuSalInvSaTaxInvBas = "salesInvSalTaxInvBasis.sql";

  /**
   * <p>Query Sales Invoice Taxes item basis method.</p>
   **/
  private String quSalInvSaTaxInvBas;

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
    updateTaxLines(pAddParam, pItsOwner);
    String query = lazyGetQuerySalesInvoiceTotals();
    query = query.replace(":ITSOWNER", pItsOwner.getItsId().toString());
    String[] columns = new String[]
      {"SUBTOTAL", "TOTALTAXES", "FOREIGNSUBTOTAL", "FOREIGNTOTALTAXES"};
    Double[] totals = getSrvDatabase().evalDoubleResults(query, columns);
    if (totals[0] == null) {
      totals[0] = 0d;
    }
    if (totals[1] == null) {
      totals[1] = 0d;
    }
    if (totals[2] == null) {
      totals[2] = 0d;
    }
    if (totals[3] == null) {
      totals[3] = 0d;
    }
    AccSettings as = getSrvAccSettings().lazyGetAccSettings(pAddParam);
    pItsOwner.setSubtotal(BigDecimal.valueOf(totals[0]).setScale(
      as.getPricePrecision(), as.getRoundingMode()));
    pItsOwner.setTotalTaxes(BigDecimal.valueOf(totals[1]).setScale(
      as.getPricePrecision(), as.getSalTaxRoundMode()));
    pItsOwner.setItsTotal(pItsOwner.getSubtotal().
      add(pItsOwner.getTotalTaxes()));
    pItsOwner.setForeignSubtotal(BigDecimal.valueOf(totals[2]).setScale(
      as.getPricePrecision(), as.getRoundingMode()));
    pItsOwner.setForeignTotalTaxes(BigDecimal.valueOf(totals[3]).setScale(
      as.getPricePrecision(), as.getSalTaxRoundMode()));
    pItsOwner.setForeignTotal(pItsOwner.getForeignSubtotal().
      add(pItsOwner.getForeignTotalTaxes()));
    getSrvOrm().updateEntity(pAddParam, pItsOwner);
  }

  /**
   * <p>Lazy get quSalInvSaTaxItBas.</p>
   * @return quSalInvSaTaxItBas
   * @throws Exception - an exception
   **/
  public final String lazyGetQuSalInvSaTaxItBas() throws Exception {
    if (this.quSalInvSaTaxItBas == null) {
      String flName = "/accounting/trade/" + this.fileQuSalInvSaTaxItBas;
      this.quSalInvSaTaxItBas = loadString(flName);
    }
    return this.quSalInvSaTaxItBas;
  }

  /**
   * <p>Lazy get quSalInvSaTaxInvBas.</p>
   * @return quSalInvSaTaxInvBas
   * @throws Exception - an exception
   **/
  public final String lazyGetQuSalInvSaTaxInvBas() throws Exception {
    if (this.quSalInvSaTaxInvBas == null) {
      String flName = "/accounting/trade/" + this.fileQuSalInvSaTaxInvBas;
      this.quSalInvSaTaxInvBas = loadString(flName);
    }
    return this.quSalInvSaTaxInvBas;
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
    AccSettings as = getSrvAccSettings().lazyGetAccSettings(pAddParam);
    if (!pOwner.getCustomer().getIsForeigner()
      && as.getIsExtractSalesTaxFromSales()) {
      String query;
      if (as.getSalTaxIsInvoiceBase()) {
        query = lazyGetQuSalInvSaTaxInvBas();
      } else {
        query = lazyGetQuSalInvSaTaxItBas();
      }
      query = query.replace(":INVOICEID", pOwner.getItsId().toString());
      int countUpdatedSitl = 0;
      IRecordSet<RS> recordSet = null;
      try {
        recordSet = getSrvDatabase().retrieveRecords(query);
        if (recordSet.moveToFirst()) {
          do {
            Long taxId = recordSet.getLong("TAXID");
            Double totalTax;
            Double foreignTotalTaxes;
            Double taxable = null;
            Double forTaxable = null;
            if (as.getSalTaxIsInvoiceBase()) {
              Double percent = recordSet.getDouble("ITSPERCENTAGE");
              taxable = recordSet.getDouble("TAXABLE");
              forTaxable = recordSet.getDouble("FOREIGNTAXABLE");
              totalTax = taxable * percent / 100.0d;
              foreignTotalTaxes = forTaxable * percent / 100.0d;
            } else {
              totalTax = recordSet.getDouble("TOTALTAX");
              foreignTotalTaxes = recordSet.getDouble("FOREIGNTOTALTAXES");
            }
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
              as.getPricePrecision(), as.getSalTaxRoundMode()));
            sit.setForeignTotalTaxes(BigDecimal.valueOf(foreignTotalTaxes)
              .setScale(as.getPricePrecision(), as.getSalTaxRoundMode()));
            if (as.getSalTaxIsInvoiceBase()) {
              sit.setTaxableInvBas(BigDecimal.valueOf(taxable).setScale(
                as.getPricePrecision(), as.getRoundingMode()));
              sit.setTaxableInvBasFc(BigDecimal.valueOf(forTaxable).setScale(
                as.getPricePrecision(), as.getRoundingMode()));
            }
            if (sit.getIsNew()) {
              getSrvOrm().insertEntity(pAddParam, sit);
              sit.setIsNew(false);
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
      for (SalesInvoiceTaxLine sitln : sitl) {
        getSrvOrm().deleteEntity(pAddParam, sitln);
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
   * <p>Getter for fileQuSalInvSaTaxItBas.</p>
   * @return String
   **/
  public final String getFileQuSalInvSaTaxItBas() {
    return this.fileQuSalInvSaTaxItBas;
  }

  /**
   * <p>Setter for fileQuSalInvSaTaxItBas.</p>
   * @param pFileQuSalInvSaTaxItBas reference
   **/
  public final void setFileQuSalInvSaTaxItBas(
    final String pFileQuSalInvSaTaxItBas) {
    this.fileQuSalInvSaTaxItBas = pFileQuSalInvSaTaxItBas;
  }

  /**
   * <p>Getter for quSalInvSaTaxItBas.</p>
   * @return String
   **/
  public final String getQuSalInvSaTaxItBas() {
    return this.quSalInvSaTaxItBas;
  }

  /**
   * <p>Setter for quSalInvSaTaxItBas.</p>
   * @param pQuSalInvSaTaxItBas reference
   **/
  public final void setQuSalInvSaTaxItBas(
    final String pQuSalInvSaTaxItBas) {
    this.quSalInvSaTaxItBas = pQuSalInvSaTaxItBas;
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

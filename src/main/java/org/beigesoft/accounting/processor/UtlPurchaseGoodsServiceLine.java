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

import org.beigesoft.model.IRecordSet;
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.service.ISrvDatabase;
import org.beigesoft.accounting.persistable.AccSettings;
import org.beigesoft.accounting.persistable.PurchaseInvoice;
import org.beigesoft.accounting.persistable.PurchaseInvoiceTaxLine;
import org.beigesoft.accounting.persistable.Tax;
import org.beigesoft.accounting.service.ISrvAccSettings;

/**
 * <p>Utility for Vendor Invoice Goods/Service Line.
 * It makes total for owner.</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class UtlPurchaseGoodsServiceLine<RS> {

  /**
   * <p>Database service.</p>
   **/
  private ISrvDatabase<RS> srvDatabase;

  /**
   * <p>ORM service.</p>
   **/
  private ISrvOrm<RS> srvOrm;

  /**
   * <p>File with Query Vendor Invoice Taxes item basis method.</p>
   **/
  private String fileQuPurInvSaTaxItBas = "purchInvSalTaxItemBasis.sql";

  /**
   * <p>Query Vendor Invoice Taxes item basis method.</p>
   **/
  private String quPurInvSaTaxItBas;

  /**
   * <p>File with Query Vendor Invoice Taxes invoice basis method.</p>
   **/
  private String fileQuPurInvSaTaxInvBas = "purchInvSalTaxInvBasis.sql";

  /**
   * <p>Query Vendor Invoice Taxes invoice basis method.</p>
   **/
  private String quPurInvSaTaxInvBas;

  /**
   * <p>File with Query Vendor Invoice Totals.</p>
   **/
  private String fileQueryPurchaseInvoiceTotals = "purchaseInvoiceTotals.sql";

  /**
   * <p>Query Vendor Invoice Totals.</p>
   **/
  private String queryPurchaseInvoiceTotals;

  /**
   * <p>Business service for accounting settings.</p>
   **/
  private ISrvAccSettings srvAccSettings;

  /**
   * <p>Insert immutable line into DB.</p>
   * @param pAddParam additional param
   * @param pItsOwner PurchaseInvoice
   * @throws Exception - an exception
   **/
  public final void updateOwner(final Map<String, Object> pAddParam,
    final PurchaseInvoice pItsOwner) throws Exception {
    updateTaxLines(pAddParam, pItsOwner);
    String query = lazyGetQueryPurchaseInvoiceTotals();
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
   * <p>Lazy get quPurInvSaTaxItBas.</p>
   * @return quPurInvSaTaxItBas
   * @throws Exception - an exception
   **/
  public final String lazyGetQuPurchInvSalTaxItBas() throws Exception {
    if (this.quPurInvSaTaxItBas == null) {
      String flName = "/accounting/trade/" + this.fileQuPurInvSaTaxItBas;
      this.quPurInvSaTaxItBas = loadString(flName);
    }
    return this.quPurInvSaTaxItBas;
  }

  /**
   * <p>Lazy get quPurInvSaTaxInvBas.</p>
   * @return quPurInvSaTaxInvBas
   * @throws Exception - an exception
   **/
  public final String lazyGetQuPurchInvSalTaxInvBas() throws Exception {
    if (this.quPurInvSaTaxInvBas == null) {
      String flName = "/accounting/trade/" + this.fileQuPurInvSaTaxInvBas;
      this.quPurInvSaTaxInvBas = loadString(flName);
    }
    return this.quPurInvSaTaxInvBas;
  }

  /**
   * <p>Lazy get queryPurchaseInvoiceTotals.</p>
   * @return queryPurchaseInvoiceTotals
   * @throws Exception - an exception
   **/
  public final String lazyGetQueryPurchaseInvoiceTotals() throws Exception {
    if (this.queryPurchaseInvoiceTotals == null) {
      String flName = "/accounting/trade/"
        + this.fileQueryPurchaseInvoiceTotals;
      this.queryPurchaseInvoiceTotals = loadString(flName);
    }
    return this.queryPurchaseInvoiceTotals;
  }

  /**
   * <p>Load string file (usually SQL query).</p>
   * @param pFileName file name
   * @return String usually SQL query
   * @throws IOException - IO exception
   **/
  public final String loadString(final String pFileName)
        throws IOException {
    URL urlFile = UtlPurchaseGoodsServiceLine.class
      .getResource(pFileName);
    if (urlFile != null) {
      InputStream inputStream = null;
      try {
        inputStream = UtlPurchaseGoodsServiceLine.class
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
   * @param pItsOwner Owner
   * @throws Exception - an exception
   **/
  public final void updateTaxLines(final Map<String, Object> pAddParam,
    final PurchaseInvoice pItsOwner) throws Exception {
    PurchaseInvoiceTaxLine pit = new PurchaseInvoiceTaxLine();
    pit.setItsOwner(pItsOwner);
    List<PurchaseInvoiceTaxLine> pitl = getSrvOrm()
      .retrieveListForField(pAddParam, pit, "itsOwner");
    AccSettings as = getSrvAccSettings().lazyGetAccSettings(pAddParam);
    if (!pItsOwner.getVendor().getIsForeigner()
      && as.getIsExtractSalesTaxFromPurchase()) {
      String query;
      if (as.getSalTaxIsInvoiceBase()) {
        query = lazyGetQuPurchInvSalTaxInvBas();
      } else {
        query = lazyGetQuPurchInvSalTaxItBas();
      }
      query = query.replace(":INVOICEID", pItsOwner.getItsId().toString());
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
            if (pitl.size() > countUpdatedSitl) {
              pit = pitl.get(countUpdatedSitl);
              countUpdatedSitl++;
            } else {
              pit = new PurchaseInvoiceTaxLine();
              pit.setItsOwner(pItsOwner);
              pit.setIsNew(true);
              pit.setIdDatabaseBirth(this.srvOrm.getIdDatabase());
            }
            Tax tax = new Tax();
            tax.setItsId(taxId);
            pit.setTax(tax);
            pit.setItsTotal(BigDecimal.valueOf(totalTax).setScale(
              as.getPricePrecision(), as.getSalTaxRoundMode()));
            pit.setForeignTotalTaxes(BigDecimal.valueOf(foreignTotalTaxes)
              .setScale(as.getPricePrecision(), as.getSalTaxRoundMode()));
            if (as.getSalTaxIsInvoiceBase()) {
              pit.setTaxableInvBas(BigDecimal.valueOf(taxable).setScale(
                as.getPricePrecision(), as.getRoundingMode()));
              pit.setTaxableInvBasFc(BigDecimal.valueOf(forTaxable).setScale(
                as.getPricePrecision(), as.getRoundingMode()));
            }
            if (pit.getIsNew()) {
              getSrvOrm().insertEntity(pAddParam, pit);
              pit.setIsNew(false);
            } else {
              getSrvOrm().updateEntity(pAddParam, pit);
            }
          } while (recordSet.moveToNext());
        }
      } finally {
        if (recordSet != null) {
          recordSet.close();
        }
      }
      if (countUpdatedSitl < pitl.size()) {
        for (int j = countUpdatedSitl; j < pitl.size(); j++) {
          getSrvOrm().deleteEntity(pAddParam, pitl.get(j));
        }
      }
    } else if (pitl.size() > 0) {
      for (PurchaseInvoiceTaxLine pitln : pitl) {
        getSrvOrm().deleteEntity(pAddParam, pitln);
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
   * <p>Getter for fileQuPurInvSaTaxItBas.</p>
   * @return String
   **/
  public final String getFileQuPurInvSaTaxItBas() {
    return this.fileQuPurInvSaTaxItBas;
  }

  /**
   * <p>Setter for fileQuPurInvSaTaxItBas.</p>
   * @param pFileQuPurInvSaTaxItBas reference
   **/
  public final void setFileQuPurInvSaTaxItBas(
    final String pFileQuPurInvSaTaxItBas) {
    this.fileQuPurInvSaTaxItBas = pFileQuPurInvSaTaxItBas;
  }

  /**
   * <p>Getter for quPurInvSaTaxItBas.</p>
   * @return String
   **/
  public final String getQueryPurchInvSalTaxItemBas() {
    return this.quPurInvSaTaxItBas;
  }

  /**
   * <p>Setter for quPurInvSaTaxItBas.</p>
   * @param pQueryPurchInvSalTaxItemBas reference
   **/
  public final void setQueryPurchInvSalTaxItemBas(
    final String pQueryPurchInvSalTaxItemBas) {
    this.quPurInvSaTaxItBas = pQueryPurchInvSalTaxItemBas;
  }

  /**
   * <p>Getter for fileQueryPurchaseInvoiceTotals.</p>
   * @return String
   **/
  public final String getFileQueryPurchaseInvoiceTotals() {
    return this.fileQueryPurchaseInvoiceTotals;
  }

  /**
   * <p>Setter for fileQueryPurchaseInvoiceTotals.</p>
   * @param pFileQueryPurchaseInvoiceTotals reference
   **/
  public final void setFileQueryPurchaseInvoiceTotals(
    final String pFileQueryPurchaseInvoiceTotals) {
    this.fileQueryPurchaseInvoiceTotals = pFileQueryPurchaseInvoiceTotals;
  }

  /**
   * <p>Getter for queryPurchaseInvoiceTotals.</p>
   * @return String
   **/
  public final String getQueryPurchaseInvoiceTotals() {
    return this.queryPurchaseInvoiceTotals;
  }

  /**
   * <p>Setter for queryPurchaseInvoiceTotals.</p>
   * @param pQueryPurchaseInvoiceTotals reference
   **/
  public final void setQueryPurchaseInvoiceTotals(
    final String pQueryPurchaseInvoiceTotals) {
    this.queryPurchaseInvoiceTotals = pQueryPurchaseInvoiceTotals;
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

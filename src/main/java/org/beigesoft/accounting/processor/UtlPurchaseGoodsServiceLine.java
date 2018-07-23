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
import java.util.ArrayList;
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
   * <p>File with Query Vendor Invoice Taxes item basis method
   * aggregate tax rate.</p>
   **/
  private String fileQuPurInvSaTaxItBasAggr = "purchInvSalTaxItBasAggr.sql";

  /**
   * <p>Query Vendor Invoice Taxes item basis method
   * aggregate tax rate.</p>
   **/
  private String quPurInvSaTaxItBasAggr;

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
   * <p>Lazy get quPurInvSaTaxItBasAggr.</p>
   * @return quPurInvSaTaxItBasAggr
   * @throws Exception - an exception
   **/
  public final String lazyGetQuPurchInvSalTaxItBasAggr() throws Exception {
    if (this.quPurInvSaTaxItBasAggr == null) {
      String flName = "/accounting/trade/" + this.fileQuPurInvSaTaxItBasAggr;
      this.quPurInvSaTaxItBasAggr = loadString(flName);
    }
    return this.quPurInvSaTaxItBasAggr;
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
    PurchaseInvoiceTaxLine itl = new PurchaseInvoiceTaxLine();
    itl.setItsOwner(pItsOwner);
    List<PurchaseInvoiceTaxLine> itls = getSrvOrm()
      .retrieveListForField(pAddParam, itl, "itsOwner");
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
      int countUpdatedItl = 0;
      IRecordSet<RS> recordSet = null;
      List<Long> taxesOrCats = new ArrayList<Long>();
      List<Double> dbResults = new ArrayList<Double>();
      try {
        recordSet = getSrvDatabase().retrieveRecords(query);
        if (recordSet.moveToFirst()) {
          do {
            if (as.getSalTaxIsInvoiceBase()) {
              taxesOrCats.add(recordSet.getLong("TAXID"));
              Double percent = recordSet.getDouble("ITSPERCENTAGE");
              Double taxable = recordSet.getDouble("TAXABLE");
              Double forTaxable = recordSet.getDouble("FOREIGNTAXABLE");
              dbResults.add(taxable * percent / 100.0d);
              dbResults.add(forTaxable * percent / 100.0d);
              dbResults.add(taxable);
              dbResults.add(forTaxable);
            } else {
              if (as.getSalTaxUseAggregItBas()) {
                taxesOrCats.add(recordSet.getLong("TAXCATEGORY"));
                dbResults.add(recordSet.getDouble("TOTALTAXES"));
                dbResults.add(recordSet.getDouble("FOREIGNTOTALTAXES"));
              } else {
                taxesOrCats.add(recordSet.getLong("TAXID"));
                dbResults.add(recordSet.getDouble("TOTALTAX"));
                dbResults.add(recordSet.getDouble("FOREIGNTOTALTAXES"));
              }
            }
          } while (recordSet.moveToNext());
        }
      } finally {
        if (recordSet != null) {
          recordSet.close();
        }
      }
      int i = 0;
      int aggrTaxCatRound = 0;
      for (Long taxOrCat : taxesOrCats) {
        Long taxId;
        Double totalTax;
        Double totalTaxFc;
        Double taxable = null;
        Double taxableFc = null;
        if (as.getSalTaxIsInvoiceBase()) {
          taxId = taxOrCat;
          totalTax = dbResults.get(i * 4);
          totalTaxFc = dbResults.get(i * 4 + 1);
        } else {
          if (as.getSalTaxUseAggregItBas()) {
            aggrTaxCatRound++;
            Long taxCatId = taxOrCat;
  taxId = taxOrCat;
            totalTax = dbResults.get(i * 2);
            totalTaxFc = dbResults.get(i * 2 + 1);
          } else {
            taxId = taxOrCat;
            totalTax = dbResults.get(i * 2);
            totalTaxFc = dbResults.get(i * 2 + 1);
          }
        }
        if (aggrTaxCatRound > 1) {
          
        } else if (itls.size() > countUpdatedItl) {
          itl = itls.get(countUpdatedItl);
          countUpdatedItl++;
        } else {
          itl = new PurchaseInvoiceTaxLine();
          itl.setItsOwner(pItsOwner);
          itl.setIsNew(true);
          itl.setIdDatabaseBirth(this.srvOrm.getIdDatabase());
        }
        makeItl(pAddParam, itl, pItsOwner, taxId, totalTax, totalTaxFc,
          taxable, taxableFc, as);
        i++;
      }
      if (countUpdatedItl < itls.size()) {
        for (int j = countUpdatedItl; j < itls.size(); j++) {
          getSrvOrm().deleteEntity(pAddParam, itls.get(j));
        }
      }
    } else if (itls.size() > 0) {
      for (PurchaseInvoiceTaxLine itln : itls) {
        getSrvOrm().deleteEntity(pAddParam, itln);
      }
    }
  }

  /**
   * <p>Makes invoice tax line line.</p>
   * @param pAddParam additional param
   * @param pItl PurchaseInvoiceTaxLine
   * @param pItsOwner invoice
   * @param pTaxId Tax ID
   * @param pTotalTax Total Tax
   * @param pTotalTax Total Tax in foreign currency
   * @param pTaxable Taxable
   * @param pTaxableFc Taxable in foreign currency
   * @param pAs ACC Settings
   * @throws Exception an Exception
   **/
  public final void makeItl(final Map<String, Object> pAddParam,
    final PurchaseInvoiceTaxLine pItl, final PurchaseInvoice pItsOwner,
      final Long pTaxId, final Double pTotalTax, final Double pTotalTaxFc,
        final Double pTaxable, final Double pTaxableFc,
          final AccSettings pAs) throws Exception {
    Tax tax = new Tax();
    tax.setItsId(pTaxId);
    pItl.setTax(tax);
    pItl.setItsTotal(BigDecimal.valueOf(pTotalTax).setScale(
      pAs.getPricePrecision(), pAs.getSalTaxRoundMode()));
    pItl.setForeignTotalTaxes(BigDecimal.valueOf(pTotalTaxFc)
      .setScale(pAs.getPricePrecision(), pAs.getSalTaxRoundMode()));
    if (pAs.getSalTaxIsInvoiceBase()) {
      pItl.setTaxableInvBas(BigDecimal.valueOf(pTaxable).setScale(
        pAs.getPricePrecision(), pAs.getRoundingMode()));
      pItl.setTaxableInvBasFc(BigDecimal.valueOf(pTaxableFc).setScale(
        pAs.getPricePrecision(), pAs.getRoundingMode()));
    }
    if (pItl.getIsNew()) {
      getSrvOrm().insertEntity(pAddParam, pItl);
      pItl.setIsNew(false);
    } else {
      getSrvOrm().updateEntity(pAddParam, pItl);
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
   * <p>Getter for fileQuPurInvSaTaxItBasAggr.</p>
   * @return String
   **/
  public final String getFileQuPurInvSaTaxItBasAggr() {
    return this.fileQuPurInvSaTaxItBasAggr;
  }

  /**
   * <p>Setter for fileQuPurInvSaTaxItBasAggr.</p>
   * @param pFileQuPurInvSaTaxItBasAggr reference
   **/
  public final void setFileQuPurInvSaTaxItBasAggr(
    final String pFileQuPurInvSaTaxItBasAggr) {
    this.fileQuPurInvSaTaxItBasAggr = pFileQuPurInvSaTaxItBasAggr;
  }

  /**
   * <p>Setter for quPurInvSaTaxItBasAggr.</p>
   * @param pQuPurInvSaTaxItBasAggr reference
   **/
  public final void setQuPurInvSaTaxItBasAggr(
    final String pQuPurInvSaTaxItBasAggr) {
    this.quPurInvSaTaxItBasAggr = pQuPurInvSaTaxItBasAggr;
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

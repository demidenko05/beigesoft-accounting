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
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.service.ISrvDatabase;
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
   * <p>File with Query Vendor Invoice Taxes.</p>
   **/
  private String fileQueryPurchaseInvoiceTaxes = "purchaseInvoiceTaxes.sql";

  /**
   * <p>Query Vendor Invoice Taxes.</p>
   **/
  private String queryPurchaseInvoiceTaxes;

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
    String query = lazyGetQueryPurchaseInvoiceTotals();
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
    updateTaxLines(pAddParam, pItsOwner);
  }

  /**
   * <p>Lazy get queryPurchaseInvoiceTaxes.</p>
   * @return queryPurchaseInvoiceTaxes
   * @throws Exception - an exception
   **/
  public final String lazyGetQueryPurchaseInvoiceTaxes() throws Exception {
    if (this.queryPurchaseInvoiceTaxes == null) {
      String flName = "/accounting/trade/" + this.fileQueryPurchaseInvoiceTaxes;
      this.queryPurchaseInvoiceTaxes = loadString(flName);
    }
    return this.queryPurchaseInvoiceTaxes;
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
    if (!pItsOwner.getVendor().getIsForeigner() && getSrvAccSettings()
      .lazyGetAccSettings(pAddParam).getIsExtractSalesTaxFromPurchase()) {
      String query = lazyGetQueryPurchaseInvoiceTaxes().replace(":INVOICEID",
        pItsOwner.getItsId().toString());
      int countUpdatedSitl = 0;
      IRecordSet<RS> recordSet = null;
      try {
        recordSet = getSrvDatabase().retrieveRecords(query);
        if (recordSet.moveToFirst()) {
          do {
            Long taxId = recordSet.getLong("TAXID");
            Double totalTax = recordSet.getDouble("TOTALTAX");
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
              getSrvAccSettings().lazyGetAccSettings(pAddParam)
                .getPricePrecision(), getSrvAccSettings()
                  .lazyGetAccSettings(pAddParam).getRoundingMode()));
            if (pit.getIsNew()) {
              getSrvOrm().insertEntity(pAddParam, pit);
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
   * <p>Getter for fileQueryPurchaseInvoiceTaxes.</p>
   * @return String
   **/
  public final String getFileQueryPurchaseInvoiceTaxes() {
    return this.fileQueryPurchaseInvoiceTaxes;
  }

  /**
   * <p>Setter for fileQueryPurchaseInvoiceTaxes.</p>
   * @param pFileQueryPurchaseInvoiceTaxes reference
   **/
  public final void setFileQueryPurchaseInvoiceTaxes(
    final String pFileQueryPurchaseInvoiceTaxes) {
    this.fileQueryPurchaseInvoiceTaxes = pFileQueryPurchaseInvoiceTaxes;
  }

  /**
   * <p>Getter for queryPurchaseInvoiceTaxes.</p>
   * @return String
   **/
  public final String getQueryPurchaseInvoiceTaxes() {
    return this.queryPurchaseInvoiceTaxes;
  }

  /**
   * <p>Setter for queryPurchaseInvoiceTaxes.</p>
   * @param pQueryPurchaseInvoiceTaxes reference
   **/
  public final void setQueryPurchaseInvoiceTaxes(
    final String pQueryPurchaseInvoiceTaxes) {
    this.queryPurchaseInvoiceTaxes = pQueryPurchaseInvoiceTaxes;
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

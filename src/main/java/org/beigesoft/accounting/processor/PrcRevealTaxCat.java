package org.beigesoft.accounting.processor;

/*
 * Copyright (c) 2018 Beigesoft™
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
import java.math.BigDecimal;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;

import org.beigesoft.exception.ExceptionWithCode;
import org.beigesoft.model.IRequestData;
import org.beigesoft.model.IRecordSet;
import org.beigesoft.log.ILogger;
import org.beigesoft.service.ISrvDatabase;
import org.beigesoft.service.IProcessor;
import org.beigesoft.accounting.persistable.SalesInvoiceLine;
import org.beigesoft.accounting.persistable.SalesReturnLine;
import org.beigesoft.accounting.persistable.SalesInvoiceServiceLine;
import org.beigesoft.accounting.persistable.PurchaseInvoiceLine;
import org.beigesoft.accounting.persistable.PurchaseReturnLine;
import org.beigesoft.accounting.persistable.PurchaseInvoiceServiceLine;
import org.beigesoft.accounting.persistable.InvItem;
import org.beigesoft.accounting.persistable.InvItemTaxCategory;
import org.beigesoft.accounting.persistable.ServiceToSale;
import org.beigesoft.accounting.persistable.ServicePurchased;
import org.beigesoft.accounting.persistable.DestTaxGoodsLn;
import org.beigesoft.accounting.persistable.DestTaxServPurchLn;
import org.beigesoft.accounting.persistable.DestTaxServSelLn;

/**
 * <p>Transactional service that retrieves destination or origin tax category
 * by given item's type, item's ID, destination's ID.</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class PrcRevealTaxCat<RS> implements IProcessor {

  /**
   * <p>Logger.</p>
   **/
  private ILogger logger;

  /**
   * <p>Database service.</p>
   */
  private ISrvDatabase<RS> srvDatabase;

  /**
   * <p>Query reveal tax category.</p>
   */
  private String queryRevealTaxCat;

  /**
   * <p>Process entity request.</p>
   * @param pAddParam additional param
   * @param pRequestData Request Data
   * @throws Exception - an exception
   **/
  @Override
  public final void process(final Map<String, Object> pAddParam,
    final IRequestData pRequestData) throws Exception {
    String taxDestIdStr = pRequestData.getParameter("taxDestId");
    Long taxDestId = Long.parseLong(taxDestIdStr);
    String itemIdStr = pRequestData.getParameter("itemId");
    Long itemId = Long.parseLong(itemIdStr);
    String nmEnt = pRequestData.getParameter("nmEnt");
    String destTaxItemLnNm;
    String itemNm;
    if (SalesInvoiceServiceLine.class.getSimpleName().equals(nmEnt)) {
      destTaxItemLnNm = DestTaxServSelLn.class.getSimpleName();
      itemNm = ServiceToSale.class.getSimpleName();
    } else if (PurchaseInvoiceServiceLine.class.getSimpleName().equals(nmEnt)) {
      destTaxItemLnNm = DestTaxServPurchLn.class.getSimpleName();
      itemNm = ServicePurchased.class.getSimpleName();
    } else if (PurchaseInvoiceLine.class.getSimpleName().equals(nmEnt)
      || SalesInvoiceLine.class.getSimpleName().equals(nmEnt)
        || SalesReturnLine.class.getSimpleName().equals(nmEnt)
          || PurchaseReturnLine.class.getSimpleName().equals(nmEnt)) {
      destTaxItemLnNm = DestTaxGoodsLn.class.getSimpleName();
      itemNm = InvItem.class.getSimpleName();
    } else {
      throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
        "Wrong line type " + nmEnt);
    }
    String query = lazyGetQueryRevealTaxCat();
    query = query.replace(":ITEMNM", itemNm);
    query = query.replace(":DESTTAXITEMLNNM", destTaxItemLnNm);
    query = query.replace(":ITEMID", itemId.toString());
    query = query.replace(":TAXDESTID", taxDestId.toString());
    InvItemTaxCategory taxCategory = null;
    IRecordSet<RS> recordSet = null;
    try {
      this.srvDatabase.setIsAutocommit(false);
      this.srvDatabase.setTransactionIsolation(ISrvDatabase
        .TRANSACTION_READ_UNCOMMITTED);
      this.srvDatabase.beginTransaction();
      recordSet = getSrvDatabase().retrieveRecords(query);
      if (recordSet.moveToFirst()) {
        String tcn = recordSet.getString("DTCNAME");
        String tcd;
        Long tcId;
        Double tcRate;
        if (tcn == null) {
          tcn = recordSet.getString("OTCNAME");
          tcd = recordSet.getString("OTCDESCR");
          tcId = recordSet.getLong("OTCID");
          tcRate = recordSet.getDouble("OTCRATE");
        } else {
          tcd = recordSet.getString("DTCDESCR");
          tcId = recordSet.getLong("DTCID");
          tcRate = recordSet.getDouble("DTCRATE");
        }
        taxCategory = new InvItemTaxCategory();
        taxCategory.setItsId(tcId);
        taxCategory.setItsName(tcn);
        taxCategory.setTaxesDescription(tcd);
        taxCategory.setAggrOnlyPercent(BigDecimal.valueOf(tcRate));
        if (recordSet.moveToNext()) {
          throw new ExceptionWithCode(ExceptionWithCode.SOMETHING_WRONG,
    "There are multiply tax category results for item id/tax dest.id/entity: "
      + itemId + "/" + taxDestId + "/" + nmEnt);
        }
      }
      this.srvDatabase.commitTransaction();
    } catch (Exception ex) {
      this.srvDatabase.rollBackTransaction();
      throw ex;
    } finally {
      if (recordSet != null) {
        recordSet.close();
      }
      this.srvDatabase.releaseResources();
    }
    pRequestData.setAttribute("taxCategory", taxCategory);
  }

  //Utils:
  /**
   * <p>Getter for queryRevealTaxCat.</p>
   * @throws IOException - IO exception
   * @return String
   **/
  public final String lazyGetQueryRevealTaxCat() throws IOException  {
    if (this.queryRevealTaxCat == null) {
      this.queryRevealTaxCat = loadString("/" + "accounting"
        + "/revealTaxCat.sql");
    }
    return this.queryRevealTaxCat;
  }

  /**
   * <p>Load string file (usually SQL query).</p>
   * @param pFileName file name
   * @return String usually SQL query
   * @throws IOException - IO exception
   **/
  public final String loadString(final String pFileName) throws IOException {
    URL urlFile = PrcRevealTaxCat.class
      .getResource(pFileName);
    if (urlFile != null) {
      InputStream inputStream = null;
      try {
        inputStream = PrcRevealTaxCat.class.getResourceAsStream(pFileName);
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

  //Simple getters and setters:
  /**
   * <p>Geter for logger.</p>
   * @return ILogger
   **/
  public final ILogger getLogger() {
    return this.logger;
  }

  /**
   * <p>Setter for logger.</p>
   * @param pLogger reference
   **/
  public final void setLogger(final ILogger pLogger) {
    this.logger = pLogger;
  }

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
   * <p>Getter for queryRevealTaxCat.</p>
   * @return String
   **/
  public final String getQueryRevealTaxCat() {
    return this.queryRevealTaxCat;
  }

  /**
   * <p>Setter for queryRevealTaxCat.</p>
   * @param pQueryRevealTaxCat reference
   **/
  public final void setQueryRevealTaxCat(final String pQueryRevealTaxCat) {
    this.queryRevealTaxCat = pQueryRevealTaxCat;
  }
}

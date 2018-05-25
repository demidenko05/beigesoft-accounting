package org.beigesoft.accounting.report;

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


import java.io.OutputStream;
import java.util.Map;
import java.util.Date;

import org.beigesoft.model.IRequestData;
import org.beigesoft.service.ISrvDate;
import org.beigesoft.service.ISrvDatabase;
import org.beigesoft.accounting.service.ISrvAccSettings;
import org.beigesoft.accounting.model.BalanceSheet;
import org.beigesoft.handler.IHndlFileReportReq;

/**
 * <p>Handler of balance sheet to PDF request.</p>
 *
 * @param <RS> platform dependent RDBMS recordset
 * @author Yury Demidenko
 */
public class HndlBalancePdfReq<RS> implements IHndlFileReportReq {

  /**
   * <p>Balance Sheet service.</p>
   */
  private ISrvBalanceSheet srvBalanceSheet;

  /**
   * <p>Date service.</p>
   */
  private ISrvDate srvDate;

  /**
   * <p>Database service.</p>
   */
  private ISrvDatabase<RS> srvDatabase;

  /**
   * <p>Business service for accounting settings.</p>
   **/
  private ISrvAccSettings srvAccSettings;

  /**
   * <p>Balance Sheet to PDF service.</p>
   */
  private IBalanceSheetPdf balanceSheetPdf;

  /**
   * <p>Handle file-report request.</p>
   * @param pReqVars Request scoped variables
   * @param pRequestData Request Data
   * @param pSous servlet output stream
   * @throws Exception - an exception
   */
  @Override
  public final void handle(final Map<String, Object> pReqVars,
    final IRequestData pRequestData,
      final OutputStream pSous) throws Exception {
    try {
      this.srvDatabase.setIsAutocommit(false);
      this.srvDatabase.
        setTransactionIsolation(ISrvDatabase.TRANSACTION_READ_UNCOMMITTED);
      this.srvDatabase.beginTransaction();
      Date date2 = srvDate
        .fromIso8601DateTimeNoTz(pRequestData.getParameter("date2"), null);
      BalanceSheet balanceSheet = getSrvBalanceSheet()
        .retrieveBalance(pReqVars, date2);
      this.balanceSheetPdf.makeReport(pReqVars, balanceSheet, pSous);
      this.srvDatabase.commitTransaction();
    } catch (Exception ex) {
      this.srvDatabase.rollBackTransaction();
      throw ex;
    } finally {
      this.srvDatabase.releaseResources();
    }
  }

  //Simple getters and setters:
  /**
   * <p>Getter for srvBalanceSheet.</p>
   * @return ISrvBalanceSheet
   **/
  public final ISrvBalanceSheet getSrvBalanceSheet() {
    return this.srvBalanceSheet;
  }

  /**
   * <p>Setter for srvBalanceSheet.</p>
   * @param pSrvBalanceSheet reference
   **/
  public final void setSrvBalanceSheet(
    final ISrvBalanceSheet pSrvBalanceSheet) {
    this.srvBalanceSheet = pSrvBalanceSheet;
  }

  /**
   * <p>Getter for srvDate.</p>
   * @return ISrvDate
   **/
  public final ISrvDate getSrvDate() {
    return this.srvDate;
  }

  /**
   * <p>Setter for srvDate.</p>
   * @param pSrvDate reference
   **/
  public final void setSrvDate(final ISrvDate pSrvDate) {
    this.srvDate = pSrvDate;
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
   * <p>Geter for srvAccSettings.</p>
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
   * <p>Getter for balanceSheetPdf.</p>
   * @return IBalanceSheetPdf
   **/
  public final IBalanceSheetPdf getBalanceSheetPdf() {
    return this.balanceSheetPdf;
  }

  /**
   * <p>Setter for balanceSheetPdf.</p>
   * @param pBalanceSheetPdf reference
   **/
  public final void setBalanceSheetPdf(
    final IBalanceSheetPdf pBalanceSheetPdf) {
    this.balanceSheetPdf = pBalanceSheetPdf;
  }
}

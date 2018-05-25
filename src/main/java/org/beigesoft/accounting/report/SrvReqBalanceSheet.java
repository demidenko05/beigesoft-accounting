package org.beigesoft.accounting.report;

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
import java.util.Date;

import org.beigesoft.model.IRequestData;
import org.beigesoft.handler.IHandlerRequest;
import org.beigesoft.service.ISrvDate;
import org.beigesoft.service.ISrvDatabase;
import org.beigesoft.accounting.service.ISrvAccSettings;
import org.beigesoft.accounting.model.BalanceSheet;

/**
 * <p>Transactional business service that handle request
 * for balance sheet report.</p>
 *
 * @param <RS> platform dependent RDBMS recordset
 * @author Yury Demidenko
 */
public class SrvReqBalanceSheet<RS> implements IHandlerRequest {

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
   * <p>Handle request.</p>
   * @param pReqVars Request scoped variables
   * @param pRequestData Request Data
   * @throws Exception - an exception
   */
  @Override
  public final void handle(final Map<String, Object> pReqVars,
    final IRequestData pRequestData) throws Exception {
    try {
      this.srvDatabase.setIsAutocommit(false);
      this.srvDatabase.
        setTransactionIsolation(ISrvDatabase.TRANSACTION_READ_UNCOMMITTED);
      this.srvDatabase.beginTransaction();
      Date date2 = srvDate
        .fromIso8601DateTimeNoTz(pRequestData.getParameter("date2"), null);
      BalanceSheet balanceSheet = getSrvBalanceSheet()
        .retrieveBalance(pReqVars, date2);
      pRequestData.setAttribute("balanceSheet", balanceSheet);
      pRequestData.setAttribute("accSettings", srvAccSettings
        .lazyGetAccSettings(pReqVars));
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
}

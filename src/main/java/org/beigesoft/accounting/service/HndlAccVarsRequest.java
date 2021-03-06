package org.beigesoft.accounting.service;

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
import java.util.List;

import org.beigesoft.model.IRequestData;
import org.beigesoft.handler.IHandlerRequestDch;
import org.beigesoft.log.ILog;
import org.beigesoft.service.ISrvDatabase;
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.accounting.persistable.I18nAccounting;
import org.beigesoft.accounting.persistable.I18nCurrency;
import org.beigesoft.accounting.persistable.AccSettings;

/**
 * <p>It handles accounting request for setting accounting variables
 * and additional internationalization.
 * It's invoked by the first I18N handler.</p>
 *
 * @param <RS> platform dependent RDBMS recordset
 * @author Yury Demidenko
 */
public class HndlAccVarsRequest<RS> implements IHandlerRequestDch {

  /**
   * <p>Logger.</p>
   **/
  private ILog logger;

  /**
   * <p>Database service.</p>
   */
  private ISrvDatabase<RS> srvDatabase;

  /**
   * <p>ORM service.</p>
   */
  private ISrvOrm<RS> srvOrm;

  /**
   * <p>Business service for accounting settings.</p>
   **/
  private ISrvAccSettings srvAccSettings;

  /**
   * <p>Additional I18n Request Handler - Web-Store vars.</p>
   */
  private IHandlerRequestDch additionalI18nReqHndl;

  /**
   * <p>Cached common accounting I18N parameters.</p>
   */
  private List<I18nAccounting> i18nAccountingList;

  /**
   * <p>Cached common accounting I18N parameters.</p>
   */
  private List<I18nCurrency> i18nCurrencyList;

  /**
   * <p>Handle request.</p>
   * @param pReqVars Request scoped variables
   * @param pRequestData Request Data
   * @throws Exception - an exception
   */
  @Override
  public final void handle(final Map<String, Object> pReqVars,
    final IRequestData pRequestData) throws Exception {
    AccSettings as = srvAccSettings.lazyGetAccSettings(pReqVars);
    String curSign;
    if (as.getUseCurrencySign()) {
      curSign = as.getCurrency().getItsSign();
    } else {
      curSign = " " + as.getCurrency().getItsName() + " ";
    }
    pReqVars.put("quantityDp", as.getQuantityPrecision());
    pReqVars.put("priceDp", as.getPricePrecision());
    pReqVars.put("costDp", as.getCostPrecision());
    pReqVars.put("taxDp", as.getTaxPrecision());
    pReqVars.put("reportDp", as.getBalancePrecision());
    pReqVars.put("curSign", curSign);
    pReqVars.put("accSet", as);
    String lang = (String) pReqVars.get("lang");
    String langDef = (String) pReqVars.get("langDef");
    if (lang != null && langDef != null && !lang.equals(langDef)) {
      List<I18nAccounting> i18nAccTmp;
      List<I18nCurrency> i18nCurTmp;
      synchronized (this) {
        if (this.i18nAccountingList == null) {
          try {
            this.logger.info(null, HndlAccVarsRequest.class,
              "Refreshing I18N data...");
            this.srvDatabase.setIsAutocommit(false);
            this.srvDatabase.setTransactionIsolation(ISrvDatabase
              .TRANSACTION_READ_UNCOMMITTED);
            this.srvDatabase.beginTransaction();
            List<I18nAccounting> i18nac = this.srvOrm.retrieveList(pReqVars,
              I18nAccounting.class);
            List<I18nCurrency> i18ncur = this.srvOrm.retrieveList(pReqVars,
              I18nCurrency.class);
            this.srvDatabase.commitTransaction();
            //assigning fully initialized data:
            this.i18nAccountingList = i18nac;
            this.i18nCurrencyList = i18ncur;
          } catch (Exception ex) {
            if (!this.srvDatabase.getIsAutocommit()) {
              this.srvDatabase.rollBackTransaction();
            }
            throw ex;
          } finally {
            this.srvDatabase.releaseResources();
          }
        }
        i18nAccTmp = this.i18nAccountingList;
        i18nCurTmp = this.i18nCurrencyList;
      }
      for (I18nAccounting i18nAccounting : i18nAccTmp) {
        if (i18nAccounting.getLang().getItsId().equals(lang)) {
          pReqVars.put("i18nAccounting", i18nAccounting);
          break;
        }
      }
      for (I18nCurrency i18nCurrency : i18nCurTmp) {
        if (i18nCurrency.getHasName().getItsId()
          .equals(as.getCurrency().getItsId())
            && i18nCurrency.getLang().getItsId().equals(lang)) {
          pReqVars.put("i18nCurrency", i18nCurrency);
          break;
        }
      }
    }
    if (this.additionalI18nReqHndl != null) {
      this.additionalI18nReqHndl.handle(pReqVars, pRequestData);
    }
  }

  /**
   * <p>Handle data changed event.</p>
   * @throws Exception - an exception
   **/
  @Override
  public final synchronized void handleDataChanged() throws Exception {
    this.i18nAccountingList = null;
    this.i18nCurrencyList = null;
    this.logger.info(null, HndlAccVarsRequest.class,
      "I18N changes are handled.");
    if (this.additionalI18nReqHndl != null) {
      this.additionalI18nReqHndl.handleDataChanged();
    }
  }

  //Simple getters and setters:
  /**
   * <p>Geter for logger.</p>
   * @return ILog
   **/
  public final synchronized ILog getLogger() {
    return this.logger;
  }

  /**
   * <p>Setter for logger.</p>
   * @param pLogger reference
   **/
  public final synchronized void setLogger(final ILog pLogger) {
    this.logger = pLogger;
  }

  /**
   * <p>Getter for srvDatabase.</p>
   * @return ISrvDatabase<RS>
   **/
  public final synchronized ISrvDatabase<RS> getSrvDatabase() {
    return this.srvDatabase;
  }

  /**
   * <p>Setter for srvDatabase.</p>
   * @param pSrvDatabase reference
   **/
  public final synchronized void setSrvDatabase(
    final ISrvDatabase<RS> pSrvDatabase) {
    this.srvDatabase = pSrvDatabase;
  }

  /**
   * <p>Getter for srvOrm.</p>
   * @return ISrvOrm<RS>
   **/
  public final synchronized ISrvOrm<RS> getSrvOrm() {
    return this.srvOrm;
  }

  /**
   * <p>Setter for srvOrm.</p>
   * @param pSrvOrm reference
   **/
  public final synchronized void setSrvOrm(final ISrvOrm<RS> pSrvOrm) {
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
   * <p>Getter for additionalI18nReqHndl.</p>
   * @return IHandlerRequest
   **/
  public final IHandlerRequestDch getAdditionalI18nReqHndl() {
    return this.additionalI18nReqHndl;
  }

  /**
   * <p>Setter for additionalI18nReqHndl.</p>
   * @param pAdditionalI18nReqHndl reference
   **/
  public final void setAdditionalI18nReqHndl(
    final IHandlerRequestDch pAdditionalI18nReqHndl) {
    this.additionalI18nReqHndl = pAdditionalI18nReqHndl;
  }
}

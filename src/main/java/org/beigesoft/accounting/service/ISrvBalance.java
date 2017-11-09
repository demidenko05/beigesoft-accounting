package org.beigesoft.accounting.service;

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

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.beigesoft.model.EPeriod;
import org.beigesoft.accounting.model.TrialBalanceLine;
import org.beigesoft.accounting.persistable.Account;
import org.beigesoft.accounting.persistable.BalanceAt;

/**
 * <p>Service that maintenance BalanceAt.</p>
 *
 * @author Yury Demidenko
 */
public interface ISrvBalance {

  /**
   * <p>Retrieve Trial Balance.</p>
   * @param pAddParam additional param
   * @param pDate date
   * @return Trial Balance Lines
   * @throws Exception - an exception
   **/
  List<TrialBalanceLine> retrieveTrialBalance(Map<String, Object> pAddParam,
    Date pDate) throws Exception;

  /**
   * <p>Change period of stored balances EPeriod.DAILY/WEEKLY/MONTHLY
   * and switch on "current balances are dirty".</p>
   * @param pAddParam additional param
   * @param pPeriod EPeriod e.g. MONTHLY
   * @throws Exception - an exception
   **/
  void changeBalanceStorePeriod(Map<String, Object> pAddParam,
    EPeriod pPeriod) throws Exception;

  /**
   * <p>Evaluate period of stored balances according settings,
   * if it's changed then it switch on "current balances are dirty".</p>
   * @param pAddParam additional param
   * @return pPeriod EPeriod e.g. MONTHLY
   * @throws Exception - an exception
   **/
  EPeriod evalBalanceStorePeriod(
    Map<String, Object> pAddParam) throws Exception;

  /**
   * <p>Evaluate BalanceAt for pDateFor.
   * If required periodic BalanceAt (and all BalanceAt from start of year)
   * is null or dirty it makes it (they).</p>
   * @param pAddParam additional param
   * @param pAcc account
   * @param pSubaccId subaccount ID
   * @param pDateFor date for
   * @return BalanceAt data
   * @throws Exception - an exception
   **/
  BalanceAt evalBalanceAt(Map<String, Object> pAddParam, Account pAcc,
    Long pSubaccId, Date pDateFor) throws Exception;

  /**
   * <p>Handle new accounting entry is created to check
   * dirty of stored balances.</p>
   * @param pAddParam additional param
   * @param pAcc account
   * @param pSubaccId subaccount ID
   * @param pDateAt date at
   * @throws Exception - an exception
   **/
  void handleNewAccountEntry(Map<String, Object> pAddParam, Account pAcc,
    Long pSubaccId, Date pDateAt) throws Exception;

  /**
   * <p>Recalculate if need for all balances for all dates less
   * or equals pDateFor, this method is always invoked by report ledger.</p>
   * @param pAddParam additional param
   * @param pDateFor date for
   * @throws Exception - an exception
   **/
  void recalculateAllIfNeed(Map<String, Object> pAddParam,
    Date pDateFor) throws Exception;

  /**
   * <p>Forced recalculation all stored balances for this account
   * for all dates less or equals pDateFor. This method usually invoked
   * by account subaccount line service when subaccount is added.</p>
   * @param pAddParam additional param
   * @param pAcc account
   * @param pSubaccId subaccount ID
   * @param pDateFor date for
   * @throws Exception - an exception
   **/
  void recalculateFor(Map<String, Object> pAddParam, Account pAcc,
    Long pSubaccId, Date pDateFor) throws Exception;

  /**
   * <p>Forced recalculation all balances for all dates less
   * or equals pDateFor.</p>
   * @param pAddParam additional param
   * @param pDateFor date for
   * @param pIsPrepareNeed if need evaluation store period/start of store
   * @throws Exception - an exception
   **/
  void recalculateAll(Map<String, Object> pAddParam, Date pDateFor,
    boolean pIsPrepareNeed) throws Exception;

  /**
   * <p>Evaluate start of period nearest to pDateFor.
   * Tested in beige-common org.beigesoft.test.CalendarTest.</p>
   * @param pAddParam additional param
   * @param pDateFor date for
   * @return Start of period nearest to pDateFor
   * @throws Exception - an exception
   **/
  Date evalDatePeriodStartFor(Map<String, Object> pAddParam,
    Date pDateFor) throws Exception;
}

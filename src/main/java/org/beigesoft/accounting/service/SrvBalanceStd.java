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

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Date;
import java.util.Calendar;
import java.math.BigDecimal;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;

import org.beigesoft.exception.ExceptionWithCode;
import org.beigesoft.model.EPeriod;
import org.beigesoft.accounting.persistable.Account;
import org.beigesoft.accounting.persistable.BalanceAt;
import org.beigesoft.accounting.persistable.BalanceAtAllDirtyCheck;
import org.beigesoft.accounting.model.TrialBalanceLine;
import org.beigesoft.service.ISrvDatabase;
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.model.IRecordSet;
import org.beigesoft.log.ILogger;


/**
 * <p>Service that maintenance BalanceAt
 * and implements dirty check for all account.
 * If balance for account at given date is NULL then
 * it will be no record BalanceAt, this is cheap approach.
 * All work include recalculation all balances is executed
 * in single transaction</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class SrvBalanceStd<RS> implements ISrvBalance {

  /**
   * <p>Logger.</p>
   **/
  private ILogger logger;

  /**
   * <p>ORM service.</p>
   **/
  private ISrvOrm<RS> srvOrm;

  /**
   * <p>Database service.</p>
   **/
  private ISrvDatabase<RS> srvDatabase;

  /**
   * <p>Business service for accounting settings.</p>
   **/
  private ISrvAccSettings srvAccSettings;

  /**
   * <p>Balance store period.</p>
   **/
  private BalanceAtAllDirtyCheck balanceAtAllDirtyCheck;

  /**
   * <p>Query balance for all accounts.</p>
   **/
  private String queryBalance;

  /**
   * <p>Query balance for an account.</p>
   **/
  private String queryBalanceAccount;

  /**
   * <p>minimum constructor.</p>
   **/
  public SrvBalanceStd() {
  }

  /**
   * <p>Useful constructor.</p>
   * @param pSrvOrm ORM service
   * @param pSrvDatabase Database service
   * @param pSrvAccSettings AccSettings service
   * @param pLogger reference
   **/
  public SrvBalanceStd(final ISrvOrm<RS> pSrvOrm,
      final ISrvDatabase<RS> pSrvDatabase,
        final ISrvAccSettings pSrvAccSettings, final ILogger pLogger) {
    this.logger = pLogger;
    this.srvDatabase = pSrvDatabase;
    this.srvOrm = pSrvOrm;
    this.srvAccSettings = pSrvAccSettings;
  }

  /**
   * <p>Change period of stored balances EPeriod.DAILY/WEEKLY/MONTHLY
   * and switch on "current balances are dirty".</p>
   * @param pAddParam additional param
   * @param pPeriod EPeriod e.g. MONTHLY
   * @throws Exception - an exception
   **/
  @Override
  public final synchronized void changeBalanceStorePeriod(
    final Map<String, Object> pAddParam,
      final EPeriod pPeriod) throws Exception {
    if (pPeriod == null) {
      throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
        "null_not_accepted");
    }
    if (!lazyGetBalanceAtAllDirtyCheck(pAddParam).getBalanceStorePeriod()
      .equals(pPeriod)) {
      getLogger().info(null, SrvBalanceStd.class,
        "changing period from " + lazyGetBalanceAtAllDirtyCheck(pAddParam)
          .getBalanceStorePeriod() + " to " + pPeriod);
      lazyGetBalanceAtAllDirtyCheck(pAddParam).setBalanceStorePeriod(pPeriod);
      if (!getSrvAccSettings().lazyGetAccSettings(pAddParam)
        .getBalanceStorePeriod().equals(pPeriod)) {
        getSrvAccSettings().lazyGetAccSettings(pAddParam)
          .setBalanceStorePeriod(pPeriod);
        getSrvAccSettings().saveAccSettings(pAddParam, getSrvAccSettings()
          .lazyGetAccSettings(pAddParam));
      }
      lazyGetBalanceAtAllDirtyCheck(pAddParam)
        .setCurrentBalanceDate(new Date(157766400000L));
      lazyGetBalanceAtAllDirtyCheck(pAddParam).setIsPeriodChanged(true);
      getSrvOrm()
        .updateEntity(pAddParam, lazyGetBalanceAtAllDirtyCheck(pAddParam));
    }
  }

  /**
   * <p>Evaluate period of stored balances according settings,
   * if it's changed then it switch on "current balances are dirty".</p>
   * @param pAddParam additional param
   * @return pPeriod EPeriod e.g. MONTHLY
   * @throws Exception - an exception
   **/
  @Override
  public final synchronized EPeriod evalBalanceStorePeriod(
    final Map<String, Object> pAddParam) throws Exception {
    if (!lazyGetBalanceAtAllDirtyCheck(pAddParam).getBalanceStorePeriod()
      .equals(getSrvAccSettings().lazyGetAccSettings(pAddParam)
        .getBalanceStorePeriod())) {
      getLogger().info(null, SrvBalanceStd.class,
        "changing period from " + lazyGetBalanceAtAllDirtyCheck(pAddParam)
          .getBalanceStorePeriod() + " to " + getSrvAccSettings()
            .lazyGetAccSettings(pAddParam).getBalanceStorePeriod());
      lazyGetBalanceAtAllDirtyCheck(pAddParam)
        .setBalanceStorePeriod(getSrvAccSettings().lazyGetAccSettings(pAddParam)
          .getBalanceStorePeriod());
      lazyGetBalanceAtAllDirtyCheck(pAddParam).setIsPeriodChanged(true);
      lazyGetBalanceAtAllDirtyCheck(pAddParam)
        .setCurrentBalanceDate(new Date(157766400000L));
    }
    return lazyGetBalanceAtAllDirtyCheck(pAddParam).getBalanceStorePeriod();
  }

  /**
   * <p>Evaluate BalanceAt for given pAcc which itsDate less
   * or equals pDateFor. If required BalanceAt (and all
   * BalanceAt from start of year) is null or dirty
   * it makes it (they).</p>
   * @param pAddParam additional param
   * @param pAcc account
   * @param pSubaccId subaccount ID
   * @param pDateFor date for
   * @return BalanceAt data
   * @throws Exception - an exception
   **/
  @Override
  public final synchronized BalanceAt evalBalanceAt(
    final Map<String, Object> pAddParam, final Account pAcc,
      final Long pSubaccId, final Date pDateFor) throws Exception {
    // recalculateAllIfNeed(pAddParam, pDateFor);
    //TODO
    return null;
  }

  /**
   * <p>Handle new accounting entry is created to check dirty.
   * This is implementation of dirty check for all accounts.</p>
   * @param pAddParam additional param
   * @param pAcc account
   * @param pSubaccId subaccount ID
   * @param pDateAt date at
   * @throws Exception - an exception
   **/
  @Override
  public final synchronized void handleNewAccountEntry(
    final Map<String, Object> pAddParam, final Account pAcc,
      final Long pSubaccId, final Date pDateAt) throws Exception {
    if (lazyGetBalanceAtAllDirtyCheck(pAddParam).getLeastAccountingEntryDate()
      .getTime() > pDateAt.getTime()) {
      if (getLogger().getIsShowDebugMessagesFor(getClass())) {
        getLogger().debug(null, SrvBalanceStd.class,
          "changing least last entry date from "
            + lazyGetBalanceAtAllDirtyCheck(pAddParam)
              .getLeastAccountingEntryDate() + " to " + pDateAt);
      }
      lazyGetBalanceAtAllDirtyCheck(pAddParam)
        .setLeastAccountingEntryDate(pDateAt);
    }
  }

  /**
   * <p>Recalculate if need for all balances for all dates less
   * or equals pDateFor, this method is always invoked by report ledger.</p>
   * @param pAddParam additional param
   * @param pDateFor date for
   * @throws Exception - an exception
   **/
  @Override
  public final synchronized void recalculateAllIfNeed(
    final Map<String, Object> pAddParam, final Date pDateFor) throws Exception {
    //must be before evalDateBalanceStoreStart!!!
    evalBalanceStorePeriod(pAddParam);
    evalDateBalanceStoreStart(pAddParam);
    Date datePeriodStartFor = evalDatePeriodStartFor(pAddParam, pDateFor);
    if (datePeriodStartFor.getTime() > lazyGetBalanceAtAllDirtyCheck(pAddParam)
      .getCurrentBalanceDate().getTime()
        || lazyGetBalanceAtAllDirtyCheck(pAddParam)
          .getLeastAccountingEntryDate()
            .getTime() < lazyGetBalanceAtAllDirtyCheck(pAddParam)
              .getCurrentBalanceDate().getTime()) {
      recalculateAll(pAddParam, pDateFor, false);
    }
  }

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
  @Override
  public final synchronized void recalculateFor(
    final Map<String, Object> pAddParam, final Account pAcc,
    final Long pSubaccId, final Date pDateFor) throws Exception {
    //this implementation does nothing.
  }

  /**
   * <p>Forced recalculation all balances for all dates less
   * or equals pDateFor. If balance for account at given date is NULL then
   * it will be no recorded into BalanceAt, this is cheap approach.</p>
   * @param pAddParam additional param
   * @param pDateFor date for
   * @param pIsPrepareNeed if need evaluation store period/start of store
   * @throws Exception - an exception
   **/
  @Override
  public final synchronized void recalculateAll(
    final Map<String, Object> pAddParam, final Date pDateFor,
      final boolean pIsPrepareNeed) throws Exception {
    getLogger().info(null, SrvBalanceStd.class,
      "recalculation start BalanceAtAllDirtyCheck was "
        + lazyGetBalanceAtAllDirtyCheck(pAddParam));
    if (pIsPrepareNeed) {
      //must be before evalDateBalanceStoreStart!!!
      evalBalanceStorePeriod(pAddParam);
      evalDateBalanceStoreStart(pAddParam);
    }
    if (lazyGetBalanceAtAllDirtyCheck(pAddParam).getIsPeriodChanged()) {
      getLogger().info(null, SrvBalanceStd.class,
        "deleting all stored balances cause period has changed");
      getSrvDatabase().executeDelete(BalanceAt.class.getSimpleName()
        .toUpperCase(), null);
      lazyGetBalanceAtAllDirtyCheck(pAddParam).setIsPeriodChanged(false);
    }
    Date date;
    if (lazyGetBalanceAtAllDirtyCheck(pAddParam).getLeastAccountingEntryDate()
          .getTime() < lazyGetBalanceAtAllDirtyCheck(pAddParam)
            .getCurrentBalanceDate().getTime()) {
      //recalculate from start;
      date = evalDateNextPeriodStart(pAddParam,
        lazyGetBalanceAtAllDirtyCheck(pAddParam).getDateBalanceStoreStart());
      getLogger().info(null, SrvBalanceStd.class,
        "recalculating balances from start " + date + " <- "
        + lazyGetBalanceAtAllDirtyCheck(pAddParam).getDateBalanceStoreStart());
    } else {
      //recalculate from current end;
      date = evalDateNextPeriodStart(pAddParam,
        lazyGetBalanceAtAllDirtyCheck(pAddParam).getCurrentBalanceDate());
      getLogger().info(null, SrvBalanceStd.class,
        "recalculating balances from current end " + date + " <- "
          + lazyGetBalanceAtAllDirtyCheck(pAddParam).getCurrentBalanceDate());
    }
    Date lastBalanceStoredDate = date;
    do {
      String query = evalQueryBalance(pAddParam, new Date(date.getTime() - 1));
      List<TrialBalanceLine> tbls = retrieveBalanceLinesForStore(query);
      for (TrialBalanceLine tbl : tbls) {
        lastBalanceStoredDate = date;
        String subAccWhereStr;
        if (tbl.getSubaccId() == null) {
          subAccWhereStr =
            " and SUBACCID is null and BALANCEAT.SUBACCTYPE is null";
        } else {
          subAccWhereStr = " and SUBACCID=" + tbl.getSubaccId()
            + " and BALANCEAT.SUBACCTYPE=" + tbl.getSubaccType();
        }
        BalanceAt balanceAt = getSrvOrm().retrieveEntityWithConditions(
          pAddParam, BalanceAt.class, "where ITSACCOUNT='" + tbl.getAccId()
            + "' and ITSDATE=" + date.getTime() + subAccWhereStr);
        if (balanceAt == null) {
          balanceAt = new BalanceAt();
          balanceAt.setIsNew(true);
        }
        balanceAt.setItsDate(date);
        Account acc = new Account();
        acc.setItsId(tbl.getAccId());
        balanceAt.setItsAccount(acc);
        if (tbl.getDebit().doubleValue() != 0) {
          balanceAt.setItsBalance(tbl.getDebit());
        } else {
          balanceAt.setItsBalance(tbl.getCredit());
        }
        balanceAt.setSubaccType(tbl.getSubaccType());
        balanceAt.setSubaccId(tbl.getSubaccId());
        balanceAt.setSubaccount(tbl.getSubaccName());
        if (balanceAt.getIsNew()) {
          getSrvOrm().insertEntity(pAddParam, balanceAt);
        } else {
          getSrvOrm().updateEntity(pAddParam, balanceAt);
        }
      }
      date = evalDateNextPeriodStart(pAddParam, date);
    } while (date.getTime() <= pDateFor.getTime());
    getLogger().info(null, SrvBalanceStd.class,
      "last stored balance date " + lastBalanceStoredDate + ", date for "
        + pDateFor);
    if (lastBalanceStoredDate.getTime() > pDateFor.getTime()) {
      lazyGetBalanceAtAllDirtyCheck(pAddParam)
        .setCurrentBalanceDate(lastBalanceStoredDate);
    } else {
      lazyGetBalanceAtAllDirtyCheck(pAddParam).setCurrentBalanceDate(pDateFor);
    }
      lazyGetBalanceAtAllDirtyCheck(pAddParam)
        .setLeastAccountingEntryDate(lazyGetBalanceAtAllDirtyCheck(pAddParam)
          .getCurrentBalanceDate());
    getSrvOrm()
      .updateEntity(pAddParam, lazyGetBalanceAtAllDirtyCheck(pAddParam));
    getLogger().info(null, SrvBalanceStd.class,
      "recalculation end BalanceAtAllDirtyCheck is "
        + lazyGetBalanceAtAllDirtyCheck(pAddParam));
  }

  /**
   * <p>Retrieve Trial Balance.</p>
   * @param pAddParam additional param
   * @param pDate date
   * @return balance lines
   * @throws Exception - an exception
   **/
  @Override
  public final synchronized List<TrialBalanceLine> retrieveTrialBalance(
    final Map<String, Object> pAddParam,
      final Date pDate) throws Exception {
    recalculateAllIfNeed(pAddParam, pDate);
    List<TrialBalanceLine> result = new ArrayList<TrialBalanceLine>();
    String query = evalQueryBalance(pAddParam, pDate);
    IRecordSet<RS> recordSet = null;
    try {
      recordSet = getSrvDatabase().retrieveRecords(query);
      if (recordSet.moveToFirst()) {
        do {
          String accName = recordSet
            .getString("ITSNAME");
          String accNumber = recordSet
            .getString("ITSNUMBER");
          String subaccName = recordSet
            .getString("SUBACC");
          Double debit = recordSet
            .getDouble("DEBIT");
          Double credit = recordSet
            .getDouble("CREDIT");
          if (debit != 0 || credit != 0) {
            TrialBalanceLine tbl = new TrialBalanceLine();
            tbl.setAccName(accName);
            tbl.setAccNumber(accNumber);
            tbl.setSubaccName(subaccName);
            tbl.setDebit(BigDecimal.valueOf(debit).setScale(
              getSrvAccSettings().lazyGetAccSettings(pAddParam)
                .getBalancePrecision(), getSrvAccSettings()
                  .lazyGetAccSettings(pAddParam).getRoundingMode()));
            tbl.setCredit(BigDecimal.valueOf(credit).setScale(
              getSrvAccSettings().lazyGetAccSettings(pAddParam)
                .getBalancePrecision(), getSrvAccSettings()
                  .lazyGetAccSettings(pAddParam).getRoundingMode()));
            if (tbl.getDebit().doubleValue() != 0
              || tbl.getCredit().doubleValue() != 0) {
              result.add(tbl);
            }
          }
        } while (recordSet.moveToNext());
      }
    } finally {
      if (recordSet != null) {
        recordSet.close();
      }
    }
    //account totals:
    BigDecimal debitAcc = BigDecimal.ZERO;
    BigDecimal creditAcc = BigDecimal.ZERO;
    String accCurr = null;
    int lineCurr = 0;
    int lineStartAcc = 0;
    for (TrialBalanceLine tbl : result) {
      if (!tbl.getAccNumber().equals(accCurr)) {
        //save to old
        if (accCurr != null) {
          for (int j = lineStartAcc; j < lineCurr; j++) {
            result.get(j).setDebitAcc(debitAcc);
            result.get(j).setCreditAcc(creditAcc);
          }
        }
        //init new acc:
        lineStartAcc = lineCurr;
        accCurr = tbl.getAccNumber();
      }
      debitAcc = debitAcc.add(tbl.getDebit());
      creditAcc = creditAcc.add(tbl.getCredit());
      lineCurr++;
    }
    return result;
  }

  /**
   * <p>Evaluate start of period nearest to pDateFor.
   * Tested in beige-common org.beigesoft.test.CalendarTest.</p>
   * @param pAddParam additional param
   * @param pDateFor date for
   * @return Start of period nearest to pDateFor
   * @throws Exception - an exception
   **/
  @Override
  public final synchronized Date evalDatePeriodStartFor(
    final Map<String, Object> pAddParam, final Date pDateFor) throws Exception {
    EPeriod period = evalBalanceStorePeriod(pAddParam);
    if (!(period.equals(EPeriod.MONTHLY)
      || period.equals(EPeriod.WEEKLY)
        || period.equals(EPeriod.DAILY))) {
      throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
        "stored_balance_period_must_be_dwm");
    }
    Calendar cal = Calendar.getInstance();
    cal.setTime(pDateFor);
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0); //Daily is ready
    if (period.equals(EPeriod.MONTHLY)) {
      cal.set(Calendar.DAY_OF_MONTH, 1);
    } else if (period.equals(EPeriod.WEEKLY)) {
      cal.set(Calendar.DAY_OF_WEEK, 1);
    }
    return cal.getTime();
  }

  /**
   * <p>Evaluate date start of stored balances according settings,
   * this is the first month of the first accounting entry or start of current
   * year if there are no any acc-entry.</p>
   * @param pAddParam additional param
   * @return Date
   * @throws Exception - an exception
   **/
  public final synchronized Date evalDateBalanceStoreStart(
    final Map<String, Object> pAddParam) throws Exception {
    Date dateBalanceStoreStart = lazyGetBalanceAtAllDirtyCheck(pAddParam)
      .getDateBalanceStoreStart();
    Date leastAccountingEntryDate = lazyGetBalanceAtAllDirtyCheck(pAddParam)
      .getLeastAccountingEntryDate();
    if (dateBalanceStoreStart.getTime() == 157766400000L
      && leastAccountingEntryDate.getTime() == 157766400000L) {
      //the first time with no acc-entries, it's start of current year:
      Calendar cal = Calendar.getInstance();
      cal.setTime(new Date());
      cal.set(Calendar.MONTH, 0);
      cal.set(Calendar.DAY_OF_MONTH, 1);
      cal.set(Calendar.HOUR_OF_DAY, 0);
      cal.set(Calendar.MINUTE, 0);
      cal.set(Calendar.SECOND, 0);
      cal.set(Calendar.MILLISECOND, 0);
      lazyGetBalanceAtAllDirtyCheck(pAddParam)
        .setDateBalanceStoreStart(cal.getTime());
    } else if (dateBalanceStoreStart.getTime() == 157766400000L
      && leastAccountingEntryDate.getTime() > 157766400000L) {
      //the first time with acc-entries, it's start nearest period to least:
      lazyGetBalanceAtAllDirtyCheck(pAddParam).setDateBalanceStoreStart(
        evalDatePeriodStartFor(pAddParam, leastAccountingEntryDate));
    }
    return lazyGetBalanceAtAllDirtyCheck(pAddParam).getDateBalanceStoreStart();
   }

  /**
   * <p>Evaluate Trial Balance query.</p>
   * @param pAddParam additional param
   * @param pDate date of balance
   * @return query of balance
   * @throws Exception - an exception
   **/
  public final synchronized String evalQueryBalance(
    final Map<String, Object> pAddParam, final Date pDate) throws Exception {
    if (this.queryBalance == null) {
      String flName = "/" + "accounting" + "/" + "balance"
        + "/" + "queryBalance.sql";
      this.queryBalance = loadString(flName);
    }
    String query = queryBalance.replace(":DATE1",
      String.valueOf(evalDatePeriodStartFor(pAddParam, pDate).getTime()));
    query = query.replace(":DATE2", String.valueOf(pDate.getTime()));
    return query;
  }

  /**
   * <p>Retrieve Trial Balance lines with given query and precision cost.</p>
   * @param pAddParam additional param
   * @param pQuery date
   * @return balance lines
   * @throws Exception - an exception
   **/
  public final synchronized List<TrialBalanceLine> retrieveBalanceLines(
      final Map<String, Object> pAddParam,
        final String pQuery) throws Exception {
    List<TrialBalanceLine> result = new ArrayList<TrialBalanceLine>();
    IRecordSet<RS> recordSet = null;
    try {
      recordSet = getSrvDatabase().retrieveRecords(pQuery);
      if (recordSet.moveToFirst()) {
        do {
          String accName = recordSet
            .getString("ITSNAME");
          String accId = recordSet
            .getString("ACCID");
          Long subaccId = recordSet
            .getLong("SUBACCID");
          Integer subaccType = recordSet
            .getInteger("SUBACCTYPE");
          String accNumber = recordSet
            .getString("ITSNUMBER");
          String subaccName = recordSet
            .getString("SUBACC");
          Double debit = recordSet
            .getDouble("DEBIT");
          Double credit = recordSet
            .getDouble("CREDIT");
          if (debit != 0 || credit != 0) {
            TrialBalanceLine tbl = new TrialBalanceLine();
            tbl.setAccId(accId);
            tbl.setSubaccId(subaccId);
            tbl.setSubaccType(subaccType);
            tbl.setAccName(accName);
            tbl.setAccNumber(accNumber);
            tbl.setSubaccName(subaccName);
            tbl.setDebit(BigDecimal.valueOf(debit).setScale(
              getSrvAccSettings().lazyGetAccSettings(pAddParam)
                .getCostPrecision(), getSrvAccSettings()
                  .lazyGetAccSettings(pAddParam).getRoundingMode()));
            tbl.setCredit(BigDecimal.valueOf(credit).setScale(
              getSrvAccSettings().lazyGetAccSettings(pAddParam)
                .getCostPrecision(), getSrvAccSettings()
                  .lazyGetAccSettings(pAddParam).getRoundingMode()));
            if (tbl.getDebit().doubleValue() != 0
              || tbl.getCredit().doubleValue() != 0) {
              result.add(tbl);
            }
          }
        } while (recordSet.moveToNext());
      }
    } finally {
      if (recordSet != null) {
        recordSet.close();
      }
    }
    return result;
  }

  /**
   * <p>Retrieve Trial Balance lines with given query for store.</p>
   * @param pQuery date
   * @return balance lines
   * @throws Exception - an exception
   **/
  public final synchronized List<TrialBalanceLine> retrieveBalanceLinesForStore(
      final String pQuery) throws Exception {
    List<TrialBalanceLine> result = new ArrayList<TrialBalanceLine>();
    IRecordSet<RS> recordSet = null;
    try {
      recordSet = getSrvDatabase().retrieveRecords(pQuery);
      if (recordSet.moveToFirst()) {
        do {
          String accName = recordSet.getString("ITSNAME");
          String accId = recordSet.getString("ACCID");
          Long subaccId = recordSet.getLong("SUBACCID");
          Integer subaccType = recordSet.getInteger("SUBACCTYPE");
          String accNumber = recordSet.getString("ITSNUMBER");
          String subaccName = recordSet.getString("SUBACC");
          Double debit = recordSet.getDouble("DEBIT");
          Double credit = recordSet.getDouble("CREDIT");
          TrialBalanceLine tbl = new TrialBalanceLine();
          tbl.setAccId(accId);
          tbl.setSubaccId(subaccId);
          tbl.setSubaccType(subaccType);
          tbl.setAccName(accName);
          tbl.setAccNumber(accNumber);
          tbl.setSubaccName(subaccName);
          tbl.setDebit(BigDecimal.valueOf(debit));
          tbl.setCredit(BigDecimal.valueOf(credit));
          result.add(tbl);
        } while (recordSet.moveToNext());
      }
    } finally {
      if (recordSet != null) {
        recordSet.close();
      }
    }
    return result;
  }

  /**
   * <p>Load string file (usually SQL query).</p>
   * @param pFileName file name
   * @return String usually SQL query
   * @throws IOException - IO exception
   **/
  public final synchronized String loadString(final String pFileName)
        throws IOException {
    URL urlFile = SrvBalanceStd.class
      .getResource(pFileName);
    if (urlFile != null) {
      InputStream inputStream = null;
      try {
        inputStream = SrvBalanceStd.class.getResourceAsStream(pFileName);
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
   * <p>Lazy getter for balanceAtAllDirtyCheck.</p>
   * @param pAddParam additional param
   * @return BalanceAtAllDirtyCheck
   * @throws Exception - an exception
   **/
  public final synchronized BalanceAtAllDirtyCheck
    lazyGetBalanceAtAllDirtyCheck(
      final Map<String, Object> pAddParam) throws Exception {
    if (this.balanceAtAllDirtyCheck == null) {
      BalanceAtAllDirtyCheck balLoc = new BalanceAtAllDirtyCheck();
      balLoc.setItsId(1L);
      this.balanceAtAllDirtyCheck = getSrvOrm()
        .retrieveEntity(pAddParam, balLoc);
      if (this.balanceAtAllDirtyCheck == null) {
        getSrvOrm().insertEntity(pAddParam, balLoc);
        this.balanceAtAllDirtyCheck = getSrvOrm()
          .retrieveEntity(pAddParam, balLoc);
      }
    }
    return this.balanceAtAllDirtyCheck;
  }

  /**
   * <p>Evaluate date start of next balance store period.
   * Tested in beige-common org.beigesoft.test.CalendarTest.</p>
   * @param pAddParam additional param
   * @param pDateFor date for
   * @return Start of next period nearest to pDateFor
   * @throws Exception - an exception
   **/
  public final synchronized Date evalDateNextPeriodStart(
    final Map<String, Object> pAddParam,
      final Date pDateFor) throws Exception {
    EPeriod period = evalBalanceStorePeriod(pAddParam);
    if (!(period.equals(EPeriod.MONTHLY)
      || period.equals(EPeriod.WEEKLY)
        || period.equals(EPeriod.DAILY))) {
      throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
        "stored_balance_period_must_be_dwm");
    }
    Calendar cal = Calendar.getInstance();
    cal.setTime(pDateFor);
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);
    if (period.equals(EPeriod.DAILY)) {
      cal.add(Calendar.DATE, 1);
    } else if (period.equals(EPeriod.MONTHLY)) {
      cal.add(Calendar.MONTH, 1);
      cal.set(Calendar.DAY_OF_MONTH, 1);
    } else if (period.equals(EPeriod.WEEKLY)) {
      cal.add(Calendar.DAY_OF_YEAR, 7);
      cal.set(Calendar.DAY_OF_WEEK, 1);
    }
    return cal.getTime();
  }


  //Simple getters and setters:
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
   * <p>Geter for srvDatabase.</p>
   * @return ISrvDatabase
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
   * <p>Getter for srvAccSettings.</p>
   * @return ISrvAccSettings
   **/
  public final synchronized ISrvAccSettings getSrvAccSettings() {
    return this.srvAccSettings;
  }

  /**
   * <p>Setter for srvAccSettings.</p>
   * @param pSrvAccSettings reference
   **/
  public final synchronized void setSrvAccSettings(
    final ISrvAccSettings pSrvAccSettings) {
    this.srvAccSettings = pSrvAccSettings;
  }

  /**
   * <p>Getter for queryBalance.</p>
   * @return String
   **/
  public final synchronized String getQueryBalance() {
    return this.queryBalance;
  }

  /**
   * <p>Setter for queryBalance.</p>
   * @param pQueryBalance reference
   **/
  public final synchronized void setQueryBalance(final String pQueryBalance) {
    this.queryBalance = pQueryBalance;
  }

  /**
   * <p>Getter for queryBalanceAccount.</p>
   * @return String
   **/
  public final synchronized String getQueryBalanceAccount() {
    return this.queryBalanceAccount;
  }

  /**
   * <p>Setter for queryBalanceAccount.</p>
   * @param pQueryBalanceAccount reference
   **/
  public final synchronized void
    setQueryBalanceAccount(final String pQueryBalanceAccount) {
    this.queryBalanceAccount = pQueryBalanceAccount;
  }

  /**
   * <p>Geter for logger.</p>
   * @return ILogger
   **/
  public final synchronized ILogger getLogger() {
    return this.logger;
  }

  /**
   * <p>Setter for logger.</p>
   * @param pLogger reference
   **/
  public final synchronized void setLogger(final ILogger pLogger) {
    this.logger = pLogger;
  }
}

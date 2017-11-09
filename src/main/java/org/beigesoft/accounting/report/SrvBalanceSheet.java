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
import java.math.BigDecimal;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;

import org.beigesoft.accounting.model.BalanceSheet;
import org.beigesoft.accounting.model.BalanceLine;
import org.beigesoft.accounting.service.ISrvAccSettings;
import org.beigesoft.accounting.service.ISrvBalance;
import org.beigesoft.service.ISrvDatabase;
import org.beigesoft.model.IRecordSet;


/**
 * <p>Untransactional service that retrieve data for balance sheet.</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class SrvBalanceSheet<RS> implements ISrvBalanceSheet {

  /**
   * <p>ORM service.</p>
   **/
  private ISrvDatabase<RS> srvDatabase;

  /**
   * <p>Business service for accounting settings.</p>
   **/
  private ISrvAccSettings srvAccSettings;

  /**
   * <p>Balance service.</p>
   **/
  private ISrvBalance srvBalance;

  /**
   * <p>Query balance for all accounts.</p>
   **/
  private String queryBalance;

  /**
   * <p>minimum constructor.</p>
   **/
  public SrvBalanceSheet() {
  }

  /**
   * <p>Useful constructor.</p>
   * @param pSrvDatabase Database service
   * @param pSrvAccSettings AccSettings service
   * @param pSrvBalance Balance service
   **/
  public SrvBalanceSheet(final ISrvDatabase<RS> pSrvDatabase,
    final ISrvAccSettings pSrvAccSettings, final ISrvBalance pSrvBalance) {
    this.srvBalance = pSrvBalance;
    this.srvDatabase = pSrvDatabase;
    this.srvAccSettings = pSrvAccSettings;
  }

  /**
   * <p>Retrieve Balance.</p>
   * @param pAddParam additional param
   * @param pDate date
   * @return balance sheet
   * @throws Exception - an exception
   **/
  @Override
  public final synchronized BalanceSheet retrieveBalance(
    final Map<String, Object> pAddParam,
      final Date pDate) throws Exception {
    getSrvBalance().recalculateAllIfNeed(pAddParam, pDate);
    BalanceSheet result = new BalanceSheet();
    result.setItsDate(pDate);
    String query = evalQueryBalance(pAddParam, pDate);
    IRecordSet<RS> recordSet = null;
    try {
      recordSet = getSrvDatabase().retrieveRecords(query);
      if (recordSet.moveToFirst()) {
        do {
          String accName = recordSet.getString("ACCOUNTNAME");
          Integer accType = recordSet.getInteger("ITSTYPE");
          String accNumber = recordSet.getString("ITSNUMBER");
          Double debit = recordSet.getDouble("DEBIT");
          Double credit = recordSet.getDouble("CREDIT");
          if (debit != 0 || credit != 0) {
            BalanceLine bl = new BalanceLine();
            bl.setAccName(accName);
            bl.setAccNumber(accNumber);
            bl.setAccType(accType);
            bl.setDebit(BigDecimal.valueOf(debit).setScale(
              getSrvAccSettings().lazyGetAccSettings(pAddParam)
                .getBalancePrecision(), getSrvAccSettings()
                  .lazyGetAccSettings(pAddParam).getRoundingMode()));
            bl.setCredit(BigDecimal.valueOf(credit).setScale(
              getSrvAccSettings().lazyGetAccSettings(pAddParam)
                .getBalancePrecision(), getSrvAccSettings()
                  .lazyGetAccSettings(pAddParam).getRoundingMode()));
            if (bl.getDebit().doubleValue() != 0
              || bl.getCredit().doubleValue() != 0) {
              result.getItsLines().add(bl);
              if (accType == 0) {
                result.setTotalLinesAssets(result.getTotalLinesAssets() + 1);
                result.setTotalAssets(result.getTotalAssets().add(bl.getDebit()
                  .subtract(bl.getCredit())));
              } else if (accType == 1) {
                result.setTotalLinesLiabilities(result
                  .getTotalLinesLiabilities() + 1);
                result.setTotalLiabilities(result.getTotalLiabilities()
                  .add(bl.getCredit().subtract(bl.getDebit())));
              } else if (accType == 2) {
                result.setTotalLinesOwnersEquity(result
                  .getTotalLinesOwnersEquity() + 1);
                result.setTotalOwnersEquity(result.getTotalOwnersEquity()
                  .add(bl.getCredit().subtract(bl.getDebit())));
              }
            }
          }
        } while (recordSet.moveToNext());
      }
    } finally {
      if (recordSet != null) {
        recordSet.close();
      }
    }
    result.setDetailRowsCount(Math.max(result.getTotalLinesAssets(),
      result.getTotalLinesLiabilities() + result
        .getTotalLinesOwnersEquity() + 3)); //3 -total l, title e ,total e
    return result;
  }

  /**
   * <p>Evaluate Balance query.</p>
   * @param pAddParam additional param
   * @param pDate date of balance
   * @return query of balance
   * @throws Exception - an exception
   **/
  public final synchronized String evalQueryBalance(
    final Map<String, Object> pAddParam, final Date pDate) throws Exception {
    if (this.queryBalance == null) {
      String flName = "/" + "accounting" + "/" + "balance"
        + "/" + "queryBalanceSheet.sql";
      this.queryBalance = loadString(flName);
    }
    String query = queryBalance.replace(":DATE1",
      String.valueOf(getSrvBalance()
        .evalDatePeriodStartFor(pAddParam, pDate).getTime()));
    query = query.replace(":DATE2", String.valueOf(pDate.getTime()));
    return query;
  }

  /**
   * <p>Load string file (usually SQL query).</p>
   * @param pFileName file name
   * @return String usually SQL query
   * @throws IOException - IO exception
   **/
  public final synchronized String loadString(final String pFileName)
        throws IOException {
    URL urlFile = SrvBalanceSheet.class
      .getResource(pFileName);
    if (urlFile != null) {
      InputStream inputStream = null;
      try {
        inputStream = SrvBalanceSheet.class.getResourceAsStream(pFileName);
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
   * <p>Geter for srvDatabase.</p>
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
   * <p>Getter for srvBalance.</p>
   * @return ISrvBalance
   **/
  public final ISrvBalance getSrvBalance() {
    return this.srvBalance;
  }

  /**
   * <p>Setter for srvBalance.</p>
   * @param pSrvBalance reference
   **/
  public final void setSrvBalance(final ISrvBalance pSrvBalance) {
    this.srvBalance = pSrvBalance;
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
}

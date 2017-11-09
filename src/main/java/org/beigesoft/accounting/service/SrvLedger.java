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

import java.util.Map;
import java.util.Date;
import java.math.BigDecimal;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;

import org.beigesoft.accounting.model.ENormalBalanceType;
import org.beigesoft.accounting.model.LedgerDetail;
import org.beigesoft.accounting.model.LedgerPrevious;
import org.beigesoft.accounting.model.LedgerDetailLine;
import org.beigesoft.accounting.model.LedgerPreviousLine;
import org.beigesoft.accounting.persistable.Account;
import org.beigesoft.service.ISrvDatabase;
import org.beigesoft.model.IRecordSet;

/**
 * <p>Ledger service.</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class SrvLedger<RS> implements ISrvLedger {

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
   * <p>Query previous total.</p>
   **/
  private String queryPrevious;

  /**
   * <p>Query detail.</p>
   **/
  private String queryDetail;

  /**
   * <p>Minimal constructor.</p>
   **/
  public SrvLedger() {
  }

  /**
   * <p>Useful constructor.</p>
   * @param pSrvDatabase Database service
   * @param pSrvAccSettings AccSettings service
   * @param pSrvBalance Balance service
   **/
  public SrvLedger(final ISrvDatabase<RS> pSrvDatabase,
    final ISrvAccSettings pSrvAccSettings, final ISrvBalance pSrvBalance) {
    this.srvBalance = pSrvBalance;
    this.srvDatabase = pSrvDatabase;
    this.srvAccSettings = pSrvAccSettings;
  }

  /**
   * <p>Retrieve previous totals.</p>
   * @param pAddParam additional param
   * @param pAccount account
   * @param pDate1 date start
   * @param pSubaccId Subaccount ID or null
   * @return LedgerPrevious data
   * @throws Exception - an exception
   **/
  @Override
  public final LedgerPrevious retrievePrevious(
    final Map<String, Object> pAddParam, final Account pAccount,
      final Date pDate1, final String pSubaccId) throws Exception {
    getSrvBalance().recalculateAllIfNeed(pAddParam, pDate1);
    LedgerPrevious result = new LedgerPrevious();
    if (this.queryPrevious == null) {
      String flName = "/" + "accounting" + "/" + "ledger"
        + "/" + "queryPrevious.sql";
      this.queryPrevious = loadString(flName);
    }
    String query = queryPrevious.replace(":DATEBALANCE",
      String.valueOf(getSrvBalance()
        .evalDatePeriodStartFor(pAddParam, pDate1).getTime()));
    query = query.replace(":DATE1", String.valueOf(pDate1.getTime()));
    query = query.replace(":ACCID", "'" + pAccount.getItsId() + "'");
    String whereSubaccDebit = "";
    String whereSubaccCredit = "";
    String whereSubacc = "";
    if (pSubaccId != null && pSubaccId.length() > 0) {
      whereSubaccDebit = " and SUBACCDEBITID='" + pSubaccId + "'";
      whereSubaccCredit = " and SUBACCCREDITID='" + pSubaccId + "'";
      whereSubacc = " and SUBACCOUNT='" + pSubaccId + "'";
    }
    query = query.replace(":SUBACCDEBIT", whereSubaccDebit);
    query = query.replace(":SUBACCCREDIT", whereSubaccCredit);
    query = query.replace(":SUBACC", whereSubacc);
    IRecordSet<RS> recordSet = null;
    try {
      recordSet = getSrvDatabase().retrieveRecords(query);
      if (recordSet.moveToFirst()) {
        do {
          LedgerPreviousLine lpl = new LedgerPreviousLine();
          String subaccName = recordSet
            .getString("SUBACC");
          lpl.setDebit(BigDecimal.valueOf(recordSet.getDouble("DEBIT"))
            .setScale(getSrvAccSettings().lazyGetAccSettings(pAddParam)
              .getCostPrecision(), getSrvAccSettings()
                .lazyGetAccSettings(pAddParam).getRoundingMode()));
          lpl.setCredit(BigDecimal.valueOf(recordSet.getDouble("CREDIT"))
            .setScale(getSrvAccSettings().lazyGetAccSettings(pAddParam)
              .getCostPrecision(), getSrvAccSettings()
                .lazyGetAccSettings(pAddParam).getRoundingMode()));
          if (pAccount.getNormalBalanceType() == ENormalBalanceType.DEBIT) {
            lpl.setBalance(lpl.getDebit().subtract(lpl.getCredit()));
          } else {
            lpl.setBalance(lpl.getCredit().subtract(lpl.getDebit()));
          }
          result.getLinesMap().put(subaccName, lpl);
          result.setDebitAcc(result.getDebitAcc().add(lpl.getDebit()));
          result.setCreditAcc(result.getCreditAcc().add(lpl.getCredit()));
        } while (recordSet.moveToNext());
      }
    } finally {
      if (recordSet != null) {
        recordSet.close();
      }
    }
    if (pAccount.getNormalBalanceType() == ENormalBalanceType.DEBIT) {
      result.setBalanceAcc(result.getDebitAcc()
        .subtract(result.getCreditAcc()));
    } else {
      result.setBalanceAcc(result.getCreditAcc()
        .subtract(result.getDebitAcc()));
    }
    return result;
  }

  /**
   * <p>Retrieve detail entries for period.</p>
   * @param pAddParam additional param
   * @param pAccount account
   * @param pDate1 date start
   * @param pDate2 date end
   * @return LedgerDetail data
   * @param pSubaccId Subaccount ID or null
   * @param ledgerPrevious ledger previous
   * @throws Exception - an exception
   **/
  @Override
  public final LedgerDetail retrieveDetail(
    final Map<String, Object> pAddParam, final Account pAccount,
      final Date pDate1, final Date pDate2, final String pSubaccId,
        final LedgerPrevious ledgerPrevious) throws Exception {
    LedgerDetail result = new LedgerDetail();
    if (this.queryDetail == null) {
      String flName = "/" + "accounting" + "/" + "ledger"
        + "/" + "queryDetail.sql";
      this.queryDetail = loadString(flName);
    }
    String query = queryDetail.replace(":DATE1",
      String.valueOf(pDate1.getTime()));
    query = query.replace(":DATE2", String.valueOf(pDate2.getTime()));
    query = query.replace(":ACCID", "'" + pAccount.getItsId() + "'");
    String whereSubaccDebit = "";
    String whereSubaccCredit = "";
    if (pSubaccId != null && pSubaccId.length() > 0) {
      whereSubaccDebit = " and SUBACCDEBITID='" + pSubaccId + "'";
      whereSubaccCredit = " and SUBACCCREDITID='" + pSubaccId + "'";
    }
    query = query.replace(":SUBACCDEBIT", whereSubaccDebit);
    query = query.replace(":SUBACCCREDIT", whereSubaccCredit);
    IRecordSet<RS> recordSet = null;
    try {
      recordSet = getSrvDatabase().retrieveRecords(query);
      if (recordSet.moveToFirst()) {
        do {
          LedgerDetailLine ldl = new LedgerDetailLine();
          ldl.setItsDate(new Date(recordSet.getLong("ITSDATE")));
          ldl.setSourceId(recordSet.getLong("SOURCEID"));
          ldl.setSourceType(recordSet.getInteger("SOURCETYPE"));
          ldl.setDescription(recordSet.getString("DESCRIPTION"));
          ldl.setSubaccName(recordSet.getString("SUBACC"));
          ldl.setCorrAccName(recordSet.getString("CORACC"));
          ldl.setCorrSubaccName(recordSet.getString("CORSUBACC"));
          ldl.setCorrAccNumber(recordSet.getString("CORACCNUMBER"));
          Boolean isDebit = recordSet.getInteger("ISDEBIT") == 1;
          if (isDebit) {
            ldl.setDebit(BigDecimal
              .valueOf(recordSet.getDouble("ITSTOTAL"))
                .setScale(getSrvAccSettings().lazyGetAccSettings(pAddParam)
                  .getCostPrecision(), getSrvAccSettings()
                    .lazyGetAccSettings(pAddParam).getRoundingMode()));
          } else {
            ldl.setCredit(BigDecimal
              .valueOf(recordSet.getDouble("ITSTOTAL"))
                .setScale(getSrvAccSettings().lazyGetAccSettings(pAddParam)
                  .getCostPrecision(), getSrvAccSettings()
                    .lazyGetAccSettings(pAddParam).getRoundingMode()));
          }
          result.setDebitAcc(result.getDebitAcc().add(ldl.getDebit()));
          result.setCreditAcc(result.getCreditAcc().add(ldl.getCredit()));
          if (result.getSubaccDebitTotal().get(ldl.getSubaccName()) == null) {
            result.getSubaccDebitTotal()
              .put(ldl.getSubaccName(), BigDecimal.ZERO);
            result.getSubaccCreditTotal()
              .put(ldl.getSubaccName(), BigDecimal.ZERO);
          }
          result.getSubaccDebitTotal().put(ldl.getSubaccName(),
            result.getSubaccDebitTotal().get(ldl.getSubaccName())
              .add(ldl.getDebit()));
          result.getSubaccCreditTotal().put(ldl.getSubaccName(),
            result.getSubaccCreditTotal().get(ldl.getSubaccName())
              .add(ldl.getCredit()));
          if (pAccount.getNormalBalanceType() == ENormalBalanceType.DEBIT) {
            result.getSubaccBalanceTotal().put(ldl.getSubaccName(),
              result.getSubaccDebitTotal().get(ldl.getSubaccName())
                .subtract(result.getSubaccCreditTotal()
                  .get(ldl.getSubaccName())));
            result.setBalanceAcc(result.getDebitAcc()
              .subtract(result.getCreditAcc()));
            ldl.setBalance(result.getBalanceAcc());
            ldl.setBalanceSubacc(result.getSubaccBalanceTotal().
              get(ldl.getSubaccName()));
          } else {
            result.getSubaccBalanceTotal().put(ldl.getSubaccName(),
              result.getSubaccCreditTotal().get(ldl.getSubaccName())
                .subtract(result.getSubaccDebitTotal()
                  .get(ldl.getSubaccName())));
            result.setBalanceAcc(result.getCreditAcc()
              .subtract(result.getDebitAcc()));
            ldl.setBalance(result.getBalanceAcc());
            ldl.setBalanceSubacc(result.getSubaccBalanceTotal().
              get(ldl.getSubaccName()));
          }
          result.getItsLines().add(ldl);
        } while (recordSet.moveToNext());
      }
    } finally {
      if (recordSet != null) {
        recordSet.close();
      }
    }
    for (String subaccName : result.getSubaccDebitTotal().keySet()) {
      if (ledgerPrevious.getLinesMap().get(subaccName) == null) {
        ledgerPrevious.getLinesMap().put(subaccName,
          new LedgerPreviousLine());
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
  public final String loadString(final String pFileName)
        throws IOException {
    URL urlFile = SrvLedger.class
      .getResource(pFileName);
    if (urlFile != null) {
      InputStream inputStream = null;
      try {
        inputStream = SrvLedger.class.getResourceAsStream(pFileName);
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
}

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
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Locale;

import org.beigesoft.model.IRequestData;
import org.beigesoft.exception.ExceptionWithCode;
import org.beigesoft.service.IEntityProcessor;
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.service.ISrvDatabase;
import org.beigesoft.accounting.persistable.AccountingEntry;
import org.beigesoft.accounting.persistable.AccountingEntries;
import org.beigesoft.accounting.persistable.AccSettings;
import org.beigesoft.accounting.service.ISrvAccSettings;
import org.beigesoft.accounting.service.ISrvBalance;

/**
 * <p>Service that save AccountingEntry from doc AccountingEntries into DB.</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class PrcAccEntrySave<RS>
  implements IEntityProcessor<AccountingEntry, Long> {

  /**
   * <p>Database service.</p>
   **/
  private ISrvDatabase<RS> srvDatabase;

  /**
   * <p>ORM service.</p>
   **/
  private ISrvOrm<RS> srvOrm;

  /**
   * <p>Balance service.</p>
   **/
  private ISrvBalance srvBalance;

  /**
   * <p>Business service for accounting settings.</p>
   **/
  private ISrvAccSettings srvAccSettings;

  /**
   * <p>AccountingEntries type code.</p>
   **/
  private final Integer accountingEntriesTypeCode =
    new AccountingEntries().constTypeCode();

  /**
   * <p>Process entity request.</p>
   * @param pAddParam additional param, e.g. return this line's
   * document in "nextEntity" for farther process
   * @param pRequestData Request Data
   * @param pEntity Entity to process
   * @return Entity processed for farther process or null
   * @throws Exception - an exception
   **/
  @Override
  public final AccountingEntry process(
    final Map<String, Object> pAddParam,
      final AccountingEntry pEntity,
        final IRequestData pRequestData) throws Exception {
    if (!pEntity.getIdDatabaseBirth()
      .equals(getSrvOrm().getIdDatabase())) {
      throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
        "can_not_make_entry_for_foreign_src");
    }
    if (pEntity.getIsNew()) {
      AccSettings accSettings = getSrvAccSettings()
        .lazyGetAccSettings(pAddParam);
      Calendar calCurrYear = Calendar.getInstance(new Locale("en", "US"));
      calCurrYear.setTime(accSettings.getCurrentAccYear());
      calCurrYear.set(Calendar.MONTH, 0);
      calCurrYear.set(Calendar.DAY_OF_MONTH, 1);
      calCurrYear.set(Calendar.HOUR_OF_DAY, 0);
      calCurrYear.set(Calendar.MINUTE, 0);
      calCurrYear.set(Calendar.SECOND, 0);
      calCurrYear.set(Calendar.MILLISECOND, 0);
      Calendar calDoc = Calendar.getInstance(new Locale("en", "US"));
      calDoc.setTime(pEntity.getItsDate());
      calDoc.set(Calendar.MONTH, 0);
      calDoc.set(Calendar.DAY_OF_MONTH, 1);
      calDoc.set(Calendar.HOUR_OF_DAY, 0);
      calDoc.set(Calendar.MINUTE, 0);
      calDoc.set(Calendar.SECOND, 0);
      calDoc.set(Calendar.MILLISECOND, 0);
      if (calCurrYear.getTime().getTime() != calDoc.getTime().getTime()) {
        throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
          "wrong_year");
      }
      if (pEntity.getDebit().doubleValue() == 0) {
        throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
          "amount_eq_zero");
      }
      if (pEntity.getDebit().doubleValue() != 0 && pEntity.getAccDebit() == null
        && pEntity.getAccCredit() == null) {
        throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
          "account_is_null");
      }
      if (pEntity.getAccDebit() != null
        && pEntity.getAccDebit().getItsId().equals("Inventory")) {
        throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
          "account_must_not_be_inventory");
      }
      if (pEntity.getAccCredit() != null
        && pEntity.getAccCredit().getItsId().equals("Inventory")) {
        throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
          "account_must_not_be_inventory");
      }
      pEntity.setSourceType(this.accountingEntriesTypeCode);
      if (pEntity.getAccCredit() != null) {
        pEntity.setCredit(pEntity.getDebit());
        //BeigeORM refresh:
        pEntity.setAccCredit(getSrvOrm()
          .retrieveEntity(pAddParam, pEntity.getAccCredit()));
        if (pEntity.getAccCredit().getSubaccType() != null
          && pEntity.getSubaccCreditId() == null) {
          throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
            "select_subaccount");
        }
      }
      if (pEntity.getAccDebit() == null) {
        pEntity.setDebit(BigDecimal.ZERO);
      } else {
        //BeigeORM refresh:
        pEntity.setAccDebit(getSrvOrm()
          .retrieveEntity(pAddParam, pEntity.getAccDebit()));
        if (pEntity.getAccDebit().getSubaccType() != null
          && pEntity.getSubaccDebitId() == null) {
          throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
            "select_subaccount");
        }
      }
      getSrvOrm().insertEntity(pAddParam, pEntity);
      AccountingEntries itsOwner = getSrvOrm().retrieveEntityById(pAddParam,
        AccountingEntries.class, pEntity.getSourceId());
      // optimistic locking (dirty check):
      Long ownerVersion = Long.valueOf(pRequestData.getParameter(
        AccountingEntries.class.getSimpleName() + ".ownerVersion"));
      itsOwner.setItsVersion(ownerVersion);
      String query =
        "select sum(DEBIT) as DEBIT, sum(CREDIT) as CREDIT from "
        + "ACCOUNTINGENTRY where SOURCETYPE=" + this.accountingEntriesTypeCode
        + " and SOURCEID=" + itsOwner.getItsId();
      String[] columns = new String[]{"DEBIT", "CREDIT"};
      Double[] totals = getSrvDatabase().evalDoubleResults(query, columns);
      itsOwner.setTotalDebit(BigDecimal.valueOf(totals[0]).setScale(
        accSettings.getCostPrecision(), accSettings.getRoundingMode()));
      itsOwner.setTotalCredit(BigDecimal.valueOf(totals[1]).setScale(
        accSettings.getCostPrecision(), accSettings.getRoundingMode()));
      getSrvOrm().updateEntity(pAddParam, itsOwner);
      getSrvBalance().handleNewAccountEntry(pAddParam, null, null,
        pEntity.getItsDate()); //This is for SrvBalanceStd only!!!
      pAddParam.put("nextEntity", itsOwner);
      pAddParam.put("nameOwnerEntity", AccountingEntries.class.getSimpleName());
      pRequestData.setAttribute("accSettings", accSettings);
      return null;
    } else {
      throw new ExceptionWithCode(ExceptionWithCode.FORBIDDEN,
        "edit_not_allowed::" + pAddParam.get("user"));
    }
  }

  //Simple getters and setters:
  /**
   * <p>Geter for accountingEntriesTypeCode.</p>
   * @return Integer
   **/
  public final Integer getAccountingEntriesTypeCode() {
    return this.accountingEntriesTypeCode;
  }

  /**
   * <p>Geter for srvDatabase.</p>
   * @return ISrvDatabase
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
   * <p>Getter for srvOrm.</p>
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

package org.beigesoft.replicator.service;

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

import org.beigesoft.exception.ExceptionWithCode;
import org.beigesoft.accounting.service.ISrvBalance;
import org.beigesoft.accounting.persistable.AccountingEntry;
import org.beigesoft.accounting.persistable.AccountingEntries;
import org.beigesoft.service.ISrvOrm;

/**
 * <p>Service to synchronize AccEntry to invoke
 * getSrvBalance().handleNewAccountEntry.
 * It's untransactional service. Transaction must be started.</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class SrvEntitySyncAccEntry<RS> implements ISrvEntitySync {

  /**
   * <p>ORM service.</p>
   **/
  private ISrvOrm<RS> srvOrm;

  /**
   * <p>Balance service.</p>
   **/
  private ISrvBalance srvBalance;

  /**
   * <p>Accounting Entries source code.</p>
   **/
  private final Integer accountingEntriesCode = new AccountingEntries()
    .constTypeCode();

  /**
   * <p>
   * Synchronize  AccountingEntry to invoke
   * getSrvBalance().handleNewAccountEntry.
   * </p>
   * @param pEntity object
   * @param pAddParam additional params
   * @return isNew if entity exist in database (need update)
   * @throws Exception - an exception
   **/
  @Override
  public final boolean sync(final Map<String, Object> pAddParam,
    final Object pEntity) throws Exception {
    AccountingEntry entityPb = (AccountingEntry) pEntity;
    int currDbId = getSrvOrm().getIdDatabase();
    if (currDbId == entityPb.getIdDatabaseBirth()) {
      throw new ExceptionWithCode(ExceptionWithCode.SOMETHING_WRONG,
        "Foreign entity born in this database! {ID, ID BIRTH, DB BIRTH}:"
          + " {" + entityPb.getItsId() + ", " + entityPb.getIdBirth()
            + "," + entityPb.getIdDatabaseBirth());
    }
    String tblNm = pEntity.getClass().getSimpleName().toUpperCase();
    String whereStr = " where " + tblNm + ".IDBIRTH=" + entityPb.getItsId()
      + " and " + tblNm + ".IDDATABASEBIRTH=" + entityPb.getIdDatabaseBirth();
    AccountingEntry entityPbDb = getSrvOrm()
      .retrieveEntityWithConditions(pAddParam, entityPb.getClass(), whereStr);
    if (entityPb.getSourceType().equals(this.accountingEntriesCode)) {
      tblNm = AccountingEntries.class.getSimpleName().toUpperCase();
      whereStr = " where " + tblNm + ".IDBIRTH=" + entityPb.getSourceId()
      + " and " + tblNm + ".IDDATABASEBIRTH=" + entityPb.getIdDatabaseBirth();
      AccountingEntries accountingEntries = getSrvOrm()
      .retrieveEntityWithConditions(pAddParam, AccountingEntries.class,
        whereStr);
      if (accountingEntries == null) {
        throw new ExceptionWithCode(ExceptionWithCode.SOMETHING_WRONG,
          "Can't find foreign AccountingEntries {ID BIRTH, DB BIRTH}:"
            + " {" + entityPb.getSourceId()
              + "," + entityPb.getIdDatabaseBirth());
      }
      entityPb.setSourceId(accountingEntries.getItsId());
    }
    entityPb.setIdBirth(entityPb.getItsId());
    entityPb.setItsId(null);
    boolean isNew = true;
    if (entityPbDb != null) {
      entityPb.setItsId(entityPbDb.getItsId());
      isNew = false;
    }
    getSrvBalance().handleNewAccountEntry(pAddParam, null, null,
      entityPb.getItsDate()); //This is for SrvBalanceStd only!!!
    return isNew;
  }

  //Simple getters and setters:
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

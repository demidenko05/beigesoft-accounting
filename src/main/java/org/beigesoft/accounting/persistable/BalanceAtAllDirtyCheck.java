package org.beigesoft.accounting.persistable;

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

import org.beigesoft.model.EPeriod;
import org.beigesoft.persistable.AHasIdLong;

/**
 * <pre>
 * Model for "check dirty of BalanceAt for all accounts" method.
 * There is only record in database with ID=1L.
 * </pre>
 *
 * @author Yury Demidenko
 */
public class BalanceAtAllDirtyCheck extends AHasIdLong {

  /**
   * <p>Not Null, date of current calculated and stored balances
   * for all accounts e.g. 1 Feb, it is dirty when it's less than
   * leastAccountingEntryDate. After all recalculation:
   * leastAccountingEntryDate = currentBalanceDate.
   * Initialized 01/01/1975.</p>
   **/
  private Date currentBalanceDate = new Date(157766400000L);

  /**
   * <p>Not Null, the least date of last accounting entry that is made
   * after made of currentBalanceDate e.g. 22 Jan 10:56PM.
   * For improving performance every document when it's accounted
   * its first (dirty check for all accounts) entry change
   * leastAccountingEntryDate to its date of account if it less.
   * Initialized 01/01/1975.</p>
   **/
  private Date leastAccountingEntryDate = new Date(157766400000L);

  /**
   * <p>Date of start to store balance periodically, this is
   * the first month of the first accounting entry, it maintenance
   * automatically.</p>
   **/
  private Date dateBalanceStoreStart = new Date(157766400000L);

  /**
   * <p>Balance store period, not null, EPeriod.DAILY/WEEKLY/MONTHLY.
   * If period has been changed (different with acc-settings one) then
   * all BalanceAt should be deleted.</p>
   **/
  private EPeriod balanceStorePeriod = EPeriod.MONTHLY;

  /**
   * <p>If period has been changed then all BalanceAt should be deleted.</p>
   **/
  private Boolean isPeriodChanged = false;

  @Override
  public final String toString() {
    return "currentBalanceDate=" + currentBalanceDate
      + ", leastAccountingEntryDate=" + leastAccountingEntryDate
        + ", dateBalanceStoreStart=" + dateBalanceStoreStart
          + ", balanceStorePeriod=" + balanceStorePeriod
            + ", isPeriodChanged=" + isPeriodChanged;
   }

  //Hiding references getters and setters:
  /**
   * <p>Getter for dateBalanceStoreStart.</p>
   * @return Date
   **/
  public final Date getDateBalanceStoreStart() {
    if (this.dateBalanceStoreStart == null) {
      return null;
    }
    return new Date(this.dateBalanceStoreStart.getTime());
   }

  /**
   * <p>Setter for dateBalanceStoreStart.</p>
   * @param pDateBalanceStoreStart reference
   **/
  public final void setDateBalanceStoreStart(
    final Date pDateBalanceStoreStart) {
    if (pDateBalanceStoreStart == null) {
      this.dateBalanceStoreStart = null;
    } else {
      this.dateBalanceStoreStart = new Date(pDateBalanceStoreStart.getTime());
    }
  }

  /**
   * <p>Getter for currentBalanceDate.</p>
   * @return Date
   **/
  public final Date getCurrentBalanceDate() {
    if (this.currentBalanceDate == null) {
      return null;
    }
    return new Date(this.currentBalanceDate.getTime());
  }

  /**
   * <p>Setter for currentBalanceDate.</p>
   * @param pCurrentBalanceDate reference
   **/
  public final void setCurrentBalanceDate(final Date pCurrentBalanceDate) {
    if (pCurrentBalanceDate == null) {
      this.currentBalanceDate = null;
    } else {
      this.currentBalanceDate = new Date(pCurrentBalanceDate.getTime());
    }
  }

  /**
   * <p>Getter for leastAccountingEntryDate.</p>
   * @return Date
   **/
  public final Date getLeastAccountingEntryDate() {
    if (this.leastAccountingEntryDate == null) {
      return null;
    }
    return new Date(this.leastAccountingEntryDate.getTime());
  }

  /**
   * <p>Setter for leastAccountingEntryDate.</p>
   * @param pLeastAccountingEntryDate reference
   **/
  public final void setLeastAccountingEntryDate(
    final Date pLeastAccountingEntryDate) {
    if (pLeastAccountingEntryDate == null) {
      this.leastAccountingEntryDate = null;
    } else {
      this.leastAccountingEntryDate =
        new Date(pLeastAccountingEntryDate.getTime());
    }
  }

  /**
   * <p>Getter for balanceStorePeriod.</p>
   * @return EPeriod
   **/
  public final EPeriod getBalanceStorePeriod() {
    return this.balanceStorePeriod;
  }

  /**
   * <p>Setter for balanceStorePeriod.</p>
   * @param pBalanceStorePeriod reference
   **/
  public final void setBalanceStorePeriod(final EPeriod pBalanceStorePeriod) {
    this.balanceStorePeriod = pBalanceStorePeriod;
  }

  /**
   * <p>Getter for isPeriodChanged.</p>
   * @return Boolean
   **/
  public final Boolean getIsPeriodChanged() {
    return this.isPeriodChanged;
  }

  /**
   * <p>Setter for isPeriodChanged.</p>
   * @param pIsPeriodChanged reference
   **/
  public final void setIsPeriodChanged(final Boolean pIsPeriodChanged) {
    this.isPeriodChanged = pIsPeriodChanged;
  }
}

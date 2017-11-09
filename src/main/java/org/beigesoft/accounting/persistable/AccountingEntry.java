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
import java.math.BigDecimal;

import org.beigesoft.persistable.APersistableBase;

/**
 * <pre>
 * Model of Accounting Record (journal entry).
 * </pre>
 *
 * @author Yury Demidenko
 */
public class AccountingEntry extends APersistableBase {

  /**
   * <p>Date.</p>
   **/
  private Date itsDate;

  /**
   * <p>ID of reversed/reversing AccountingEntry.</p>
   **/
  private Long reversedId;

  /**
   * <p>ID database birth of reversed/reversing ACC.entry.</p>
   **/
  private Integer reversedIdDatabaseBirth;

  /**
   * <p>Integer, Not Null Source Type e.g. 1 - PurchaseInvoice,
   * 2 - SalesInvoice, 3 - AccountingEntries.
   * This is constant [document/line].constTypeCode().</p>
   **/
  private Integer sourceType;

  /**
   * <p>Integer, Not Null, ID of database where source was born.</p>
   **/
  private Integer sourceDatabaseBirth;

  /**
   * <p>Document/line ID.</p>
   **/
  private Long sourceId;

  /**
   * <p>Account debit.</p>
   **/
  private Account accDebit;

  /**
   * <p>Subacccount type, e.g. 2002 - InvItem,
   * 2003 - Tax, 2004 - DebtorCreditor.
   * This is constant [entity].constTypeCode().</p>
   **/
  private Integer subaccDebitType;

  /**
   * <p>Foreign ID of subaccount.</p>
   **/
  private Long subaccDebitId;

  /**
   * <p>Appearance of subaccount.</p>
   **/
  private String subaccDebit;

  /**
   * <p>Debit.</p>
   **/
  private BigDecimal debit = new BigDecimal("0.00");

  /**
   * <p>Account credit.</p>
   **/
  private Account accCredit;

  /**
   * <p>Subacccount type.
   * This is constant [entity].constTypeCode().</p>
   **/
  private Integer subaccCreditType;

  /**
   * <p>Foreign ID of subaccount.</p>
   **/
  private Long subaccCreditId;

  /**
   * <p>Appearance of subaccount.</p>
   **/
  private String subaccCredit;

  /**
   * <p>Credit.</p>
   **/
  private BigDecimal credit = new BigDecimal("0.00");

  /**
   * <p>Description.</p>
   **/
  private String description;

  //Hiding references getters and setters:
  /**
   * <p>Geter for itsDate.</p>
   * @return Date
   **/
  public final Date getItsDate() {
    if (this.itsDate == null) {
      return null;
    }
    return new Date(this.itsDate.getTime());
  }

  /**
   * <p>Setter for itsDate.</p>
   * @param pItsDate reference
   **/
  public final void setItsDate(final Date pItsDate) {
    if (pItsDate == null) {
      this.itsDate = null;
    } else {
      this.itsDate = new Date(pItsDate.getTime());
    }
  }

  //Simple getters and setters:
  /**
   * <p>Getter for reversedId.</p>
   * @return Long
   **/
  public final Long getReversedId() {
    return this.reversedId;
  }

  /**
   * <p>Setter for reversedId.</p>
   * @param pReversedId reference
   **/
  public final void setReversedId(final Long pReversedId) {
    this.reversedId = pReversedId;
  }

  /**
   * <p>Geter for reversed database Id.</p>
   * @return Integer DB birth ID
   **/
  public final Integer getReversedIdDatabaseBirth() {
    return this.reversedIdDatabaseBirth;
  }

  /**
   * <p>Setter for reversed database Id.</p>
   * @param pReversedIdDatabaseBirth reference
   **/
  public final void setReversedIdDatabaseBirth(
    final Integer pReversedIdDatabaseBirth) {
    this.reversedIdDatabaseBirth = pReversedIdDatabaseBirth;
  }

  /**
   * <p>Geter for sourceType.</p>
   * @return Integer
   **/
  public final Integer getSourceType() {
    return this.sourceType;
  }

  /**
   * <p>Setter for sourceType.</p>
   * @param pSourceType reference
   **/
  public final void setSourceType(final Integer pSourceType) {
    this.sourceType = pSourceType;
  }

  /**
   * <p>Getter for sourceDatabaseBirth.</p>
   * @return Integer
   **/
  public final Integer getSourceDatabaseBirth() {
    return this.sourceDatabaseBirth;
  }

  /**
   * <p>Setter for sourceDatabaseBirth.</p>
   * @param pSourceDatabaseBirth reference
   **/
  public final void setSourceDatabaseBirth(final Integer pSourceDatabaseBirth) {
    this.sourceDatabaseBirth = pSourceDatabaseBirth;
  }

  /**
   * <p>Geter for sourceId.</p>
   * @return Long
   **/
  public final Long getSourceId() {
    return this.sourceId;
  }

  /**
   * <p>Setter for sourceId.</p>
   * @param pSourceId reference
   **/
  public final void setSourceId(final Long pSourceId) {
    this.sourceId = pSourceId;
  }

  /**
   * <p>Geter for accDebit.</p>
   * @return Account
   **/
  public final Account getAccDebit() {
    return this.accDebit;
  }

  /**
   * <p>Setter for accDebit.</p>
   * @param pAccDebit reference
   **/
  public final void setAccDebit(final Account pAccDebit) {
    this.accDebit = pAccDebit;
  }

  /**
   * <p>Geter for subaccDebitType.</p>
   * @return Integer
   **/
  public final Integer getSubaccDebitType() {
    return this.subaccDebitType;
  }

  /**
   * <p>Setter for subaccDebitType.</p>
   * @param pSubaccDebitType reference
   **/
  public final void setSubaccDebitType(final Integer pSubaccDebitType) {
    this.subaccDebitType = pSubaccDebitType;
  }

  /**
   * <p>Geter for subaccDebitId.</p>
   * @return Long
   **/
  public final Long getSubaccDebitId() {
    return this.subaccDebitId;
  }

  /**
   * <p>Setter for subaccDebitId.</p>
   * @param pSubaccDebitId reference
   **/
  public final void setSubaccDebitId(final Long pSubaccDebitId) {
    this.subaccDebitId = pSubaccDebitId;
  }

  /**
   * <p>Geter for subaccDebit.</p>
   * @return String
   **/
  public final String getSubaccDebit() {
    return this.subaccDebit;
  }

  /**
   * <p>Setter for subaccDebit.</p>
   * @param pSubaccDebit reference
   **/
  public final void setSubaccDebit(final String pSubaccDebit) {
    this.subaccDebit = pSubaccDebit;
  }

  /**
   * <p>Geter for debit.</p>
   * @return BigDecimal
   **/
  public final BigDecimal getDebit() {
    return this.debit;
  }

  /**
   * <p>Setter for debit.</p>
   * @param pDebit reference
   **/
  public final void setDebit(final BigDecimal pDebit) {
    this.debit = pDebit;
  }

  /**
   * <p>Geter for accCredit.</p>
   * @return Account
   **/
  public final Account getAccCredit() {
    return this.accCredit;
  }

  /**
   * <p>Setter for accCredit.</p>
   * @param pAccCredit reference
   **/
  public final void setAccCredit(final Account pAccCredit) {
    this.accCredit = pAccCredit;
  }

  /**
   * <p>Geter for subaccCreditType.</p>
   * @return Integer
   **/
  public final Integer getSubaccCreditType() {
    return this.subaccCreditType;
  }

  /**
   * <p>Setter for subaccCreditType.</p>
   * @param pSubaccCreditType reference
   **/
  public final void setSubaccCreditType(final Integer pSubaccCreditType) {
    this.subaccCreditType = pSubaccCreditType;
  }

  /**
   * <p>Geter for subaccCreditId.</p>
   * @return Long
   **/
  public final Long getSubaccCreditId() {
    return this.subaccCreditId;
  }

  /**
   * <p>Setter for subaccCreditId.</p>
   * @param pSubaccCreditId reference
   **/
  public final void setSubaccCreditId(final Long pSubaccCreditId) {
    this.subaccCreditId = pSubaccCreditId;
  }

  /**
   * <p>Geter for subaccCredit.</p>
   * @return String
   **/
  public final String getSubaccCredit() {
    return this.subaccCredit;
  }

  /**
   * <p>Setter for subaccCredit.</p>
   * @param pSubaccCredit reference
   **/
  public final void setSubaccCredit(final String pSubaccCredit) {
    this.subaccCredit = pSubaccCredit;
  }

  /**
   * <p>Geter for credit.</p>
   * @return BigDecimal
   **/
  public final BigDecimal getCredit() {
    return this.credit;
  }

  /**
   * <p>Setter for credit.</p>
   * @param pCredit reference
   **/
  public final void setCredit(final BigDecimal pCredit) {
    this.credit = pCredit;
  }

  /**
   * <p>Geter for description.</p>
   * @return String
   **/
  public final String getDescription() {
    return this.description;
  }

  /**
   * <p>Setter for description.</p>
   * @param pDescription reference
   **/
  public final void setDescription(final String pDescription) {
    this.description = pDescription;
  }
}

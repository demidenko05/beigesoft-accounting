package org.beigesoft.accounting.model;

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

/**
 * <pre>
 * Ledger detail line.
 * </pre>
 *
 * @author Yury Demidenko
 */
public class LedgerDetailLine {

  /**
   * <p>Date.</p>
   **/
  private Date itsDate;

  /**
   * <p>Integer, Not Null Source Type e.g. 1001 - PurchaseInvoiceLine,
   * 1002 - SalesInvoiceLine, 1003 - AccountingEntriesLine.
   * This is constant [document/line].constTypeCode().</p>
   **/
  private Integer sourceType;

  /**
   * <p>Document/line ID.</p>
   **/
  private Long sourceId;

  /**
   * <p>Correspondent account number.</p>
   **/
  private String corrAccNumber;

  /**
   * <p>Correspondent account name.</p>
   **/
  private String corrAccName;

  /**
   * <p>Correspondent subaccount name, e.g. "invItem line1".</p>
   **/
  private String corrSubaccName;

  /**
   * <p>Subaccount name, e.g. "invItem line1".</p>
   **/
  private String subaccName;

  /**
   * <p>Debit.</p>
   **/
  private BigDecimal debit = BigDecimal.ZERO;

  /**
   * <p>Credit.</p>
   **/
  private BigDecimal credit = BigDecimal.ZERO;

  /**
   * <p>Balance.</p>
   **/
  private BigDecimal balance = BigDecimal.ZERO;

  /**
   * <p>Balance subaccount.</p>
   **/
  private BigDecimal balanceSubacc = BigDecimal.ZERO;

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
   * <p>Getter for sourceType.</p>
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
   * <p>Getter for sourceId.</p>
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
   * <p>Getter for corrAccNumber.</p>
   * @return String
   **/
  public final String getCorrAccNumber() {
    return this.corrAccNumber;
  }

  /**
   * <p>Setter for corrAccNumber.</p>
   * @param pCorrAccNumber reference
   **/
  public final void setCorrAccNumber(final String pCorrAccNumber) {
    this.corrAccNumber = pCorrAccNumber;
  }

  /**
   * <p>Getter for corrAccName.</p>
   * @return String
   **/
  public final String getCorrAccName() {
    return this.corrAccName;
  }

  /**
   * <p>Setter for corrAccName.</p>
   * @param pCorrAccName reference
   **/
  public final void setCorrAccName(final String pCorrAccName) {
    this.corrAccName = pCorrAccName;
  }

  /**
   * <p>Getter for corrSubaccName.</p>
   * @return String
   **/
  public final String getCorrSubaccName() {
    return this.corrSubaccName;
  }

  /**
   * <p>Setter for corrSubaccName.</p>
   * @param pCorrSubaccName reference
   **/
  public final void setCorrSubaccName(final String pCorrSubaccName) {
    this.corrSubaccName = pCorrSubaccName;
  }

  /**
   * <p>Getter for subaccName.</p>
   * @return String
   **/
  public final String getSubaccName() {
    return this.subaccName;
  }

  /**
   * <p>Setter for subaccName.</p>
   * @param pSubaccName reference
   **/
  public final void setSubaccName(final String pSubaccName) {
    this.subaccName = pSubaccName;
  }

  /**
   * <p>Getter for debit.</p>
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
   * <p>Getter for credit.</p>
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
   * <p>Getter for balance.</p>
   * @return BigDecimal
   **/
  public final BigDecimal getBalance() {
    return this.balance;
  }

  /**
   * <p>Setter for balance.</p>
   * @param pBalance reference
   **/
  public final void setBalance(final BigDecimal pBalance) {
    this.balance = pBalance;
  }

  /**
   * <p>Getter for balanceSubacc.</p>
   * @return BigDecimal
   **/
  public final BigDecimal getBalanceSubacc() {
    return this.balanceSubacc;
  }

  /**
   * <p>Setter for balanceSubacc.</p>
   * @param pBalanceSubacc reference
   **/
  public final void setBalanceSubacc(final BigDecimal pBalanceSubacc) {
    this.balanceSubacc = pBalanceSubacc;
  }

  /**
   * <p>Getter for description.</p>
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

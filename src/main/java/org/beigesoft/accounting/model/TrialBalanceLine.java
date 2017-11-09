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

import java.math.BigDecimal;

/**
 * <pre>
 * Trial Balance Line.
 * </pre>
 *
 * @author Yury Demidenko
 */
public class TrialBalanceLine {

  /**
   * <p>Account number.</p>
   **/
  private String accNumber;

  /**
   * <p>Account name.</p>
   **/
  private String accName;

  /**
   * <p>Subaccount name, e.g. "invItem line1".</p>
   **/
  private String subaccName;

  /**
   * <p>Debit subacc.</p>
   **/
  private BigDecimal debit;

  /**
   * <p>Credit subacc.</p>
   **/
  private BigDecimal credit;

  /**
   * <p>Debit account.</p>
   **/
  private BigDecimal debitAcc;

  /**
   * <p>Credit account.</p>
   **/
  private BigDecimal creditAcc;

  /**
   * <p>Account ID.</p>
   **/
  private String accId;

  /**
   * <p>Subaccount ID.</p>
   **/
  private Long subaccId;

  /**
   * <p>Subacccount type, e.g. 2002 - InvItem,
   * 2003 - Tax, 2004 - DebtorCreditor.
   * This is constant [entity].constTypeCode().</p>
   **/
  private Integer subaccType;

  //Simple getters and setters:
  /**
   * <p>Geter for accNumber.</p>
   * @return String
   **/
  public final String getAccNumber() {
    return this.accNumber;
  }

  /**
   * <p>Setter for accNumber.</p>
   * @param pAccNumber reference
   **/
  public final void setAccNumber(final String pAccNumber) {
    this.accNumber = pAccNumber;
  }

  /**
   * <p>Geter for accName.</p>
   * @return String
   **/
  public final String getAccName() {
    return this.accName;
  }

  /**
   * <p>Setter for accName.</p>
   * @param pAccName reference
   **/
  public final void setAccName(final String pAccName) {
    this.accName = pAccName;
  }

  /**
   * <p>Geter for subaccName.</p>
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
   * <p>Geter for debitAcc.</p>
   * @return BigDecimal
   **/
  public final BigDecimal getDebitAcc() {
    return this.debitAcc;
  }

  /**
   * <p>Setter for debitAcc.</p>
   * @param pDebitAcc reference
   **/
  public final void setDebitAcc(final BigDecimal pDebitAcc) {
    this.debitAcc = pDebitAcc;
  }

  /**
   * <p>Geter for creditAcc.</p>
   * @return BigDecimal
   **/
  public final BigDecimal getCreditAcc() {
    return this.creditAcc;
  }

  /**
   * <p>Setter for creditAcc.</p>
   * @param pCreditAcc reference
   **/
  public final void setCreditAcc(final BigDecimal pCreditAcc) {
    this.creditAcc = pCreditAcc;
  }

  /**
   * <p>Getter for accId.</p>
   * @return String
   **/
  public final String getAccId() {
    return this.accId;
  }

  /**
   * <p>Setter for accId.</p>
   * @param pAccId reference
   **/
  public final void setAccId(final String pAccId) {
    this.accId = pAccId;
  }

  /**
   * <p>Getter for subaccId.</p>
   * @return String
   **/
  public final Long getSubaccId() {
    return this.subaccId;
  }

  /**
   * <p>Setter for subaccId.</p>
   * @param pSubaccId reference
   **/
  public final void setSubaccId(final Long pSubaccId) {
    this.subaccId = pSubaccId;
  }

  /**
   * <p>Getter for subaccType.</p>
   * @return Integer
   **/
  public final Integer getSubaccType() {
    return this.subaccType;
  }

  /**
   * <p>Setter for subaccType.</p>
   * @param pSubaccType reference
   **/
  public final void setSubaccType(final Integer pSubaccType) {
    this.subaccType = pSubaccType;
  }
}

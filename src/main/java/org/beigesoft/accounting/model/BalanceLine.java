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
 * Balance Line.
 * </pre>
 *
 * @author Yury Demidenko
 */
public class BalanceLine {

  /**
   * <p>Account number.</p>
   **/
  private String accNumber;

  /**
   * <p>Account name.</p>
   **/
  private String accName;

  /**
   * <p>Debit subacc.</p>
   **/
  private BigDecimal debit;

  /**
   * <p>Credit subacc.</p>
   **/
  private BigDecimal credit;

  /**
   * <p>Account ID.</p>
   **/
  private String accId;

  /**
   * <p>Account type, 0-asset, 1-liabilities, 2-owner's equity.</p>
   **/
  private Integer accType;

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
   * <p>Getter for accType.</p>
   * @return Integer
   **/
  public final Integer getAccType() {
    return this.accType;
  }

  /**
   * <p>Setter for accType.</p>
   * @param pAccType reference
   **/
  public final void setAccType(final Integer pAccType) {
    this.accType = pAccType;
  }
}

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
 * Ledger previous line.
 * </pre>
 *
 * @author Yury Demidenko
 */
public class LedgerPreviousLine {

  /**
   * <p>Debit subacc.</p>
   **/
  private BigDecimal debit = BigDecimal.ZERO;

  /**
   * <p>Credit subacc.</p>
   **/
  private BigDecimal credit = BigDecimal.ZERO;

  /**
   * <p>Balance subacc.</p>
   **/
  private BigDecimal balance = BigDecimal.ZERO;

  //Simple getters and setters:
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
}

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

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.math.BigDecimal;

/**
 * <pre>
 * Ledger detail.
 * </pre>
 *
 * @author Yury Demidenko
 */
public class LedgerDetail {

  /**
   * <p>Lines.</p>
   **/
  private List<LedgerDetailLine> itsLines = new ArrayList<LedgerDetailLine>();

  /**
   * <p>Subaccount debit total.</p>
   **/
  private Map<String, BigDecimal> subaccDebitTotal =
    new HashMap<String, BigDecimal>();

  /**
   * <p>Subaccount credit total.</p>
   **/
  private Map<String, BigDecimal> subaccCreditTotal =
    new HashMap<String, BigDecimal>();

  /**
   * <p>Subaccount debit total.</p>
   **/
  private Map<String, BigDecimal> subaccBalanceTotal =
    new HashMap<String, BigDecimal>();

  /**
   * <p>Debit account total.</p>
   **/
  private BigDecimal debitAcc = BigDecimal.ZERO;

  /**
   * <p>Credit account total.</p>
   **/
  private BigDecimal creditAcc = BigDecimal.ZERO;

  /**
   * <p>Balance account total from zero.</p>
   **/
  private BigDecimal balanceAcc = BigDecimal.ZERO;

  //Simple getters and setters:
  /**
   * <p>Getter for itsLines.</p>
   * @return List<LedgerDetailLine>
   **/
  public final List<LedgerDetailLine> getItsLines() {
    return this.itsLines;
  }

  /**
   * <p>Setter for itsLines.</p>
   * @param pItsLines reference
   **/
  public final void setItsLines(final List<LedgerDetailLine> pItsLines) {
    this.itsLines = pItsLines;
  }

  /**
   * <p>Getter for subaccDebitTotal.</p>
   * @return Map<String, BigDecimal>
   **/
  public final Map<String, BigDecimal> getSubaccDebitTotal() {
    return this.subaccDebitTotal;
  }

  /**
   * <p>Setter for subaccDebitTotal.</p>
   * @param pSubaccDebitTotal reference
   **/
  public final void setSubaccDebitTotal(final Map<String,
    BigDecimal> pSubaccDebitTotal) {
    this.subaccDebitTotal = pSubaccDebitTotal;
  }

  /**
   * <p>Getter for subaccCreditTotal.</p>
   * @return Map<String, BigDecimal>
   **/
  public final Map<String, BigDecimal> getSubaccCreditTotal() {
    return this.subaccCreditTotal;
  }

  /**
   * <p>Setter for subaccCreditTotal.</p>
   * @param pSubaccCreditTotal reference
   **/
  public final void setSubaccCreditTotal(final Map<String,
    BigDecimal> pSubaccCreditTotal) {
    this.subaccCreditTotal = pSubaccCreditTotal;
  }

  /**
   * <p>Getter for subaccBalanceTotal.</p>
   * @return Map<String, BigDecimal>
   **/
  public final Map<String, BigDecimal> getSubaccBalanceTotal() {
    return this.subaccBalanceTotal;
  }

  /**
   * <p>Setter for subaccBalanceTotal.</p>
   * @param pSubaccBalanceTotal reference
   **/
  public final void setSubaccBalanceTotal(final Map<String,
    BigDecimal> pSubaccBalanceTotal) {
    this.subaccBalanceTotal = pSubaccBalanceTotal;
  }

  /**
   * <p>Getter for debitAcc.</p>
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
   * <p>Getter for creditAcc.</p>
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
   * <p>Getter for balanceAcc.</p>
   * @return BigDecimal
   **/
  public final BigDecimal getBalanceAcc() {
    return this.balanceAcc;
  }

  /**
   * <p>Setter for balanceAcc.</p>
   * @param pBalanceAcc reference
   **/
  public final void setBalanceAcc(final BigDecimal pBalanceAcc) {
    this.balanceAcc = pBalanceAcc;
  }
}

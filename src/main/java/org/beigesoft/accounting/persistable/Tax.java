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

import java.math.BigDecimal;

import org.beigesoft.accounting.model.ETaxType;
import org.beigesoft.accounting.persistable.base.ASubaccount;
import org.beigesoft.accounting.model.EDueMethod;

/**
 * <pre>
 * Model of Tax.
 * </pre>
 *
 * @author Yury Demidenko
 */
public class Tax extends ASubaccount {

  /**
   * <p>Expense, not null, e.g. Social security.
   * For accounting expense.</p>
   **/
  private Expense expense;

  /**
   * <p>Percentage.</p>
   **/
  private BigDecimal itsPercentage = BigDecimal.ZERO;

  /**
   * <p>Plus amount, not null.</p>
   **/
  private BigDecimal plusAmount = BigDecimal.ZERO;

  /**
   * <p>Account Due Method ACCURAL/CASH.</p>
   **/
  private EDueMethod dueMethod;

  /**
   * <p>Type.</p>
   **/
  private ETaxType itsType;

  /**
   * <p>OOP friendly Constant of code type.</p>
   * @return 2003
   **/
  @Override
  public final Integer constTypeCode() {
    return 2003;
  }

  //Simple getters and setters:
  /**
   * <p>Geter for itsPercentage.</p>
   * @return BigDecimal
   **/
  public final BigDecimal getItsPercentage() {
    return this.itsPercentage;
  }

  /**
   * <p>Setter for itsPercentage.</p>
   * @param pItsPercentage reference
   **/
  public final void setItsPercentage(final BigDecimal pItsPercentage) {
    this.itsPercentage = pItsPercentage;
  }

  /**
   * <p>Geter for dueMethod.</p>
   * @return EDueMethod
   **/
  public final EDueMethod getDueMethod() {
    return this.dueMethod;
  }

  /**
   * <p>Setter for dueMethod.</p>
   * @param pDueMethod reference
   **/
  public final void setDueMethod(final EDueMethod pDueMethod) {
    this.dueMethod = pDueMethod;
  }

  /**
   * <p>Getter for itsType.</p>
   * @return ETaxType
   **/
  public final ETaxType getItsType() {
    return this.itsType;
  }

  /**
   * <p>Setter for itsType.</p>
   * @param pItsType reference
   **/
  public final void setItsType(final ETaxType pItsType) {
    this.itsType = pItsType;
  }

  /**
   * <p>Getter for expense.</p>
   * @return Expense
   **/
  public final Expense getExpense() {
    return this.expense;
  }

  /**
   * <p>Setter for expense.</p>
   * @param pExpense reference
   **/
  public final void setExpense(final Expense pExpense) {
    this.expense = pExpense;
  }

  /**
   * <p>Getter for plusAmount.</p>
   * @return BigDecimal
   **/
  public final BigDecimal getPlusAmount() {
    return this.plusAmount;
  }

  /**
   * <p>Setter for plusAmount.</p>
   * @param pPlusAmount reference
   **/
  public final void setPlusAmount(final BigDecimal pPlusAmount) {
    this.plusAmount = pPlusAmount;
  }
}

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

import org.beigesoft.model.IOwned;
import org.beigesoft.persistable.APersistableBaseVersion;

/**
 * <pre>
 * Model of Wage Line for each work type,
 * e.g. cooking or delivery.
 * Version, reliable autoincrement algorithm.
 * </pre>
 *
 * @author Yury Demidenko
 */
public class WageLine extends APersistableBaseVersion
  implements IOwned<Wage> {

  /**
   * <p>Wage.</p>
   **/
  private Wage itsOwner;

  /**
   * <p>ID of reversed/reversing tax line.</p>
   **/
  private Long reversedId;

  /**
   * <p>Work Type, Not Null.</p>
   **/
  private WageType wageType;

  /**
   * <p>Gross wage.</p>
   **/
  private BigDecimal grossWage = BigDecimal.ZERO;

  /**
   * <p>Taxes from Employee.</p>
   **/
  private BigDecimal taxesEmployee = BigDecimal.ZERO;

  /**
   * <p>Account wage expense (Expenses or InventoryCapitalizedCost
   * or InventoryDirectCostTmp), Not Null.</p>
   **/
  private Account accWageExpense;

  /**
   * <p>Description.</p>
   **/
  private String description;

  /**
   * <p>Getter for itsOwner.</p>
   * @return Wage
   **/
  @Override
  public final Wage getItsOwner() {
    return this.itsOwner;
  }

  /**
   * <p>Setter for itsOwner.</p>
   * @param pItsOwner reference
   **/
  @Override
  public final void setItsOwner(final Wage pItsOwner) {
    this.itsOwner = pItsOwner;
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
   * <p>Getter for wageType.</p>
   * @return WageType
   **/
  public final WageType getWageType() {
    return this.wageType;
  }

  /**
   * <p>Setter for wageType.</p>
   * @param pWageType reference
   **/
  public final void setWageType(final WageType pWageType) {
    this.wageType = pWageType;
  }

  /**
   * <p>Getter for accWageExpense.</p>
   * @return Account
   **/
  public final Account getAccWageExpense() {
    return this.accWageExpense;
  }

  /**
   * <p>Setter for accWageExpense.</p>
   * @param pAccWageExpense reference
   **/
  public final void setAccWageExpense(final Account pAccWageExpense) {
    this.accWageExpense = pAccWageExpense;
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

  /**
   * <p>Getter for grossWage.</p>
   * @return BigDecimal
   **/
  public final BigDecimal getGrossWage() {
    return this.grossWage;
  }

  /**
   * <p>Setter for grossWage.</p>
   * @param pGrossWage reference
   **/
  public final void setGrossWage(final BigDecimal pGrossWage) {
    this.grossWage = pGrossWage;
  }

  /**
   * <p>Getter for taxesEmployee.</p>
   * @return BigDecimal
   **/
  public final BigDecimal getTaxesEmployee() {
    return this.taxesEmployee;
  }

  /**
   * <p>Setter for taxesEmployee.</p>
   * @param pTaxesEmployee reference
   **/
  public final void setTaxesEmployee(final BigDecimal pTaxesEmployee) {
    this.taxesEmployee = pTaxesEmployee;
  }
}

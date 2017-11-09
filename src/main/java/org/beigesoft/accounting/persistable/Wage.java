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

import java.util.List;
import java.math.BigDecimal;

import org.beigesoft.accounting.persistable.base.ADoc;

/**
 * <pre>
 * Model of Employee Compensation.
 * </pre>
 *
 * @author Yury Demidenko
 */
public class Wage extends ADoc {

  /**
   * <p>Employee.</p>
   **/
  private Employee employee;

  /**
   * <p>Not Null, taxes that reduce net wage.</p>
   **/
  private BigDecimal totalTaxesEmployee = BigDecimal.ZERO;

  /**
   * <p>Net wage = gross wage - taxes from employee.</p>
   **/
  private BigDecimal netWage = BigDecimal.ZERO;

  /**
   * <p>Not Null, taxes that don't reduce net wage.</p>
   **/
  private BigDecimal totalTaxesEmployer = BigDecimal.ZERO;

  /**
   * <p>Account tax expense (Expenses or InventoryCapitalizedCost),
   * Not Null.</p>
   **/
  private Account accTaxExpense;

  /**
   * <p>Lines.</p>
   **/
  private List<WageLine> itsLines;

  /**
   * <p>Taxes lines.</p>
   **/
  private List<WageTaxLine> taxesLines;

  /**
   * <p>OOP friendly Constant of code type 6.</p>
   **/
  @Override
  public final Integer constTypeCode() {
    return 6;
  }

  //Simple getters and setters:
  /**
   * <p>Getter for employee.</p>
   * @return Employee
   **/
  public final Employee getEmployee() {
    return this.employee;
  }

  /**
   * <p>Setter for employee.</p>
   * @param pEmployee reference
   **/
  public final void setEmployee(final Employee pEmployee) {
    this.employee = pEmployee;
  }

  /**
   * <p>Getter for totalTaxesEmployee.</p>
   * @return BigDecimal
   **/
  public final BigDecimal getTotalTaxesEmployee() {
    return this.totalTaxesEmployee;
  }

  /**
   * <p>Setter for totalTaxesEmployee.</p>
   * @param pTotalTaxesEmployee reference
   **/
  public final void setTotalTaxesEmployee(
    final BigDecimal pTotalTaxesEmployee) {
    this.totalTaxesEmployee = pTotalTaxesEmployee;
  }

  /**
   * <p>Getter for netWage.</p>
   * @return BigDecimal
   **/
  public final BigDecimal getNetWage() {
    return this.netWage;
  }

  /**
   * <p>Setter for netWage.</p>
   * @param pNetWage reference
   **/
  public final void setNetWage(final BigDecimal pNetWage) {
    this.netWage = pNetWage;
  }

  /**
   * <p>Getter for totalTaxesEmployer.</p>
   * @return BigDecimal
   **/
  public final BigDecimal getTotalTaxesEmployer() {
    return this.totalTaxesEmployer;
  }

  /**
   * <p>Setter for totalTaxesEmployer.</p>
   * @param pTotalTaxesEmployer reference
   **/
  public final void setTotalTaxesEmployer(
    final BigDecimal pTotalTaxesEmployer) {
    this.totalTaxesEmployer = pTotalTaxesEmployer;
  }

  /**
   * <p>Getter for accTaxExpense.</p>
   * @return Account
   **/
  public final Account getAccTaxExpense() {
    return this.accTaxExpense;
  }

  /**
   * <p>Setter for accTaxExpense.</p>
   * @param pAccTaxExpense reference
   **/
  public final void setAccTaxExpense(final Account pAccTaxExpense) {
    this.accTaxExpense = pAccTaxExpense;
  }

  /**
   * <p>Getter for itsLines.</p>
   * @return List<WageLine>
   **/
  public final List<WageLine> getItsLines() {
    return this.itsLines;
  }

  /**
   * <p>Setter for itsLines.</p>
   * @param pItsLines reference
   **/
  public final void setItsLines(final List<WageLine> pItsLines) {
    this.itsLines = pItsLines;
  }

  /**
   * <p>Getter for taxesLines.</p>
   * @return List<WageTaxLine>
   **/
  public final List<WageTaxLine> getTaxesLines() {
    return this.taxesLines;
  }

  /**
   * <p>Setter for taxesLines.</p>
   * @param pTaxesLines reference
   **/
  public final void setTaxesLines(final List<WageTaxLine> pTaxesLines) {
    this.taxesLines = pTaxesLines;
  }
}

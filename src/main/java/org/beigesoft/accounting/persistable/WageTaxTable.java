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

import org.beigesoft.persistable.APersistableBaseNameVersion;

/**
 * <pre>
 * For automation purpose there is entity WageTaxTable that implements
 * wide used method "Tax table" to estimate tax.
 * So Wage is filled automatically according this table.
 * Version changed time algorithm.
 * </pre>
 *
 * @author Yury Demidenko
 */
public class WageTaxTable extends APersistableBaseNameVersion {

  /**
   * <p>Tax.</p>
   **/
  private Tax tax;

  /**
   * <p>Description.</p>
   **/
  private String description;

  /**
   * <p>Lines.</p>
   **/
  private List<WageTaxTableLine> itsLines;

  /**
   * <p>Employees.</p>
   **/
  private List<WageTaxTableEmployee> employees;

  /**
   * <p>Wage Types that are taxable for this tax
   * e.g. Cooking, Sick compensation.</p>
   **/
  private List<WageTaxTableType> wageTypes;

  //Simple getters and setters:
  /**
   * <p>Getter for tax.</p>
   * @return Tax
   **/
  public final Tax getTax() {
    return this.tax;
  }

  /**
   * <p>Setter for tax.</p>
   * @param pTax reference
   **/
  public final void setTax(final Tax pTax) {
    this.tax = pTax;
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
   * <p>Getter for itsLines.</p>
   * @return List<WageTaxTableLine>
   **/
  public final List<WageTaxTableLine> getItsLines() {
    return this.itsLines;
  }

  /**
   * <p>Setter for itsLines.</p>
   * @param pItsLines reference
   **/
  public final void setItsLines(final List<WageTaxTableLine> pItsLines) {
    this.itsLines = pItsLines;
  }

  /**
   * <p>Getter for employees.</p>
   * @return List<WageTaxTableEmployee>
   **/
  public final List<WageTaxTableEmployee> getEmployees() {
    return this.employees;
  }

  /**
   * <p>Setter for employees.</p>
   * @param pEmployees reference
   **/
  public final void setEmployees(final List<WageTaxTableEmployee> pEmployees) {
    this.employees = pEmployees;
  }

  /**
   * <p>Getter for wageTypes.</p>
   * @return List<WageTaxTableType>
   **/
  public final List<WageTaxTableType> getWageTypes() {
    return this.wageTypes;
  }

  /**
   * <p>Setter for wageTypes.</p>
   * @param pWageTypes reference
   **/
  public final void setWageTypes(final List<WageTaxTableType> pWageTypes) {
    this.wageTypes = pWageTypes;
  }
}

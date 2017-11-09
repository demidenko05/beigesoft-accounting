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
import java.util.Date;

import org.beigesoft.accounting.persistable.base.ASubaccount;

/**
 * <pre>
 * Model of Employee.
 * </pre>
 *
 * @author Yury Demidenko
 */
public class Employee extends ASubaccount {

  /**
   * <p>Its category. e.g. accountants.</p>
   **/
  private EmployeeCategory itsCategory;

  /**
   * <p>Not Null, tax identification number e.g. SSN for US.</p>
   **/
  private String taxIdentificationNumber;


  /**
   * <p>Not Null, date of hire.</p>
   **/
  private Date dateHire;

  /**
   * <p>Description.</p>
   **/
  private String description;

  /**
   * <p>Year Wage Lines.</p>
   **/
  private List<EmployeeYearWage> yearWageLines;

  /**
   * <p>OOP friendly Constant of code type.</p>
   * @return 2006
   **/
  @Override
  public final Integer constTypeCode() {
    return 2006;
  }

  //Hiding references getters and setters:
  /**
   * <p>Getter for dateHire.</p>
   * @return Date
   **/
  public final Date getDateHire() {
    if (this.dateHire == null) {
      return null;
    }
    return new Date(this.dateHire.getTime());
  }

  /**
   * <p>Setter for dateHire.</p>
   * @param pDateHire reference
   **/
  public final void setDateHire(final Date pDateHire) {
    if (pDateHire == null) {
      this.dateHire = null;
    } else {
      this.dateHire = new Date(pDateHire.getTime());
    }
  }


  //Simple getters and setters:
  /**
   * <p>Getter for itsCategory.</p>
   * @return EmployeeCategory
   **/
  public final EmployeeCategory getItsCategory() {
    return this.itsCategory;
  }

  /**
   * <p>Setter for itsCategory.</p>
   * @param pItsCategory reference
   **/
  public final void setItsCategory(final EmployeeCategory pItsCategory) {
    this.itsCategory = pItsCategory;
  }

  /**
   * <p>Getter for taxIdentificationNumber.</p>
   * @return String
   **/
  public final String getTaxIdentificationNumber() {
    return this.taxIdentificationNumber;
  }

  /**
   * <p>Setter for taxIdentificationNumber.</p>
   * @param pTaxIdentificationNumber reference
   **/
  public final void setTaxIdentificationNumber(
    final String pTaxIdentificationNumber) {
    this.taxIdentificationNumber = pTaxIdentificationNumber;
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

  /**
   * <p>Getter for yearWageLines.</p>
   * @return List<EmployeeYearWage>
   **/
  public final List<EmployeeYearWage> getYearWageLines() {
    return this.yearWageLines;
  }

  /**
   * <p>Setter for yearWageLines.</p>
   * @param pYearWageLines reference
   **/
  public final void setYearWageLines(
    final List<EmployeeYearWage> pYearWageLines) {
    this.yearWageLines = pYearWageLines;
  }
}

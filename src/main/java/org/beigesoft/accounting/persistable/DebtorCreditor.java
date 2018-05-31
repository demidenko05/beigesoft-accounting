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

import org.beigesoft.accounting.persistable.base.ASubaccount;

/**
 * <pre>
 * Model of Debtor/Creditor Customer/Vendor.
 * </pre>
 *
 * @author Yury Demidenko
 */
public class DebtorCreditor extends ASubaccount {

  /**
   * <p>Debtor/Creditor Category.</p>
   **/
  private DebtorCreditorCategory itsCategory;

  /**
   * <p>Registered email.</p>
   **/
  private String regEmail;

  /**
   * <p>Registered address1.</p>
   **/
  private String regAddress1;

  /**
   * <p>Registered address2.</p>
   **/
  private String regAddress2;

  /**
   * <p>Registered Zip.</p>
   **/
  private String regZip;

  /**
   * <p>Registered Country.</p>
   **/
  private String regCountry;

  /**
   * <p>Registered State.</p>
   **/
  private String regState;

  /**
   * <p>Registered City.</p>
   **/
  private String regCity;

  /**
   * <p>Registered Phone.</p>
   **/
  private String regPhone;

  /**
   * <p>Tax identification number e.g. SSN for US.</p>
   **/
  private String taxIdentificationNumber;

  /**
   * <p>Description.</p>
   **/
  private String description;

  /**
   * <p>Not null, false default.
   * If sales taxes must be omitted for this buyer/vendor.</p>
   **/
  private Boolean isForeigner;

  /**
   * <p>OOP friendly Constant of code type.</p>
   * @return 2004
   **/
  @Override
  public final Integer constTypeCode() {
    return 2004;
  }

  //Simple getters and setters:
  /**
   * <p>Getter for itsCategory.</p>
   * @return DebtorCreditorCategory
   **/
  public final DebtorCreditorCategory getItsCategory() {
    return this.itsCategory;
  }

  /**
   * <p>Setter for itsCategory.</p>
   * @param pItsCategory reference
   **/
  public final void setItsCategory(
    final DebtorCreditorCategory pItsCategory) {
    this.itsCategory = pItsCategory;
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
   * <p>Getter for regEmail.</p>
   * @return String
   **/
  public final String getRegEmail() {
    return this.regEmail;
  }

  /**
   * <p>Setter for regEmail.</p>
   * @param pRegEmail reference
   **/
  public final void setRegEmail(final String pRegEmail) {
    this.regEmail = pRegEmail;
  }

  /**
   * <p>Getter for regAddress1.</p>
   * @return String
   **/
  public final String getRegAddress1() {
    return this.regAddress1;
  }

  /**
   * <p>Setter for regAddress1.</p>
   * @param pRegAddress1 reference
   **/
  public final void setRegAddress1(final String pRegAddress1) {
    this.regAddress1 = pRegAddress1;
  }

  /**
   * <p>Getter for regAddress2.</p>
   * @return String
   **/
  public final String getRegAddress2() {
    return this.regAddress2;
  }

  /**
   * <p>Setter for regAddress2.</p>
   * @param pRegAddress2 reference
   **/
  public final void setRegAddress2(final String pRegAddress2) {
    this.regAddress2 = pRegAddress2;
  }

  /**
   * <p>Getter for regZip.</p>
   * @return String
   **/
  public final String getRegZip() {
    return this.regZip;
  }

  /**
   * <p>Setter for regZip.</p>
   * @param pRegZip reference
   **/
  public final void setRegZip(final String pRegZip) {
    this.regZip = pRegZip;
  }

  /**
   * <p>Getter for regCountry.</p>
   * @return String
   **/
  public final String getRegCountry() {
    return this.regCountry;
  }

  /**
   * <p>Setter for regCountry.</p>
   * @param pRegCountry reference
   **/
  public final void setRegCountry(final String pRegCountry) {
    this.regCountry = pRegCountry;
  }

  /**
   * <p>Getter for regState.</p>
   * @return String
   **/
  public final String getRegState() {
    return this.regState;
  }

  /**
   * <p>Setter for regState.</p>
   * @param pRegState reference
   **/
  public final void setRegState(final String pRegState) {
    this.regState = pRegState;
  }

  /**
   * <p>Getter for regCity.</p>
   * @return String
   **/
  public final String getRegCity() {
    return this.regCity;
  }

  /**
   * <p>Setter for regCity.</p>
   * @param pRegCity reference
   **/
  public final void setRegCity(final String pRegCity) {
    this.regCity = pRegCity;
  }

  /**
   * <p>Getter for regPhone.</p>
   * @return Long
   **/
  public final String getRegPhone() {
    return this.regPhone;
  }

  /**
   * <p>Setter for regPhone.</p>
   * @param pRegPhone reference
   **/
  public final void setRegPhone(final String pRegPhone) {
    this.regPhone = pRegPhone;
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
   * <p>Getter for isForeigner.</p>
   * @return Boolean
   **/
  public final Boolean getIsForeigner() {
    return this.isForeigner;
  }

  /**
   * <p>Setter for isForeigner.</p>
   * @param pIsForeigner reference
   **/
  public final void setIsForeigner(final Boolean pIsForeigner) {
    this.isForeigner = pIsForeigner;
  }
}

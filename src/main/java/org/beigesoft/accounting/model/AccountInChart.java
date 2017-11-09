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

/**
 * <pre>
 * Model of account to print in chart.
 * </pre>
 *
 * @author Yury Demidenko
 */
 public class AccountInChart {

  /**
   * <p>ID.</p>
   **/
  private String itsId;

  /**
   * <p>Number.</p>
   **/
  private String itsNumber;

  /**
   * <p>Name.</p>
   **/
  private String itsName;

  /**
   * <p>Account Normal Balance Type - DEBIT, CREDIT.</p>
   **/
  private ENormalBalanceType normalBalanceType;

  /**
   * <p>EAccountType.ASSET/LIABILITY/OWNERS_EQUITY/
   * GROSS_INCOME_REVENUE/GROSS_INCOME_EXPENSE.</p>
   **/
  private EAccountType itsType;

  /**
   * <p>Subacccount, e.g. "Seven Eleven" for Account Payable.</p>
   **/
  private String subacc;


  /**
   * <p>Description.</p>
   **/
  private String description;

  //Simple getters and setters:
  /**
   * <p>Getter for itsId.</p>
   * @return String
   **/
  public final String getItsId() {
    return this.itsId;
  }

  /**
   * <p>Setter for itsId.</p>
   * @param pItsId reference
   **/
  public final void setItsId(final String pItsId) {
    this.itsId = pItsId;
  }

  /**
   * <p>Getter for itsNumber.</p>
   * @return String
   **/
  public final String getItsNumber() {
    return this.itsNumber;
  }

  /**
   * <p>Setter for itsNumber.</p>
   * @param pItsNumber reference
   **/
  public final void setItsNumber(final String pItsNumber) {
    this.itsNumber = pItsNumber;
  }

  /**
   * <p>Getter for itsName.</p>
   * @return String
   **/
  public final String getItsName() {
    return this.itsName;
  }

  /**
   * <p>Setter for itsName.</p>
   * @param pItsName reference
   **/
  public final void setItsName(final String pItsName) {
    this.itsName = pItsName;
  }

  /**
   * <p>Getter for normalBalanceType.</p>
   * @return ENormalBalanceType
   **/
  public final ENormalBalanceType getNormalBalanceType() {
    return this.normalBalanceType;
  }

  /**
   * <p>Setter for normalBalanceType.</p>
   * @param pNormalBalanceType reference
   **/
  public final void setNormalBalanceType(
    final ENormalBalanceType pNormalBalanceType) {
    this.normalBalanceType = pNormalBalanceType;
  }

  /**
   * <p>Getter for itsType.</p>
   * @return EAccountType
   **/
  public final EAccountType getItsType() {
    return this.itsType;
  }

  /**
   * <p>Setter for itsType.</p>
   * @param pItsType reference
   **/
  public final void setItsType(final EAccountType pItsType) {
    this.itsType = pItsType;
  }

  /**
   * <p>Getter for subacc.</p>
   * @return String
   **/
  public final String getSubacc() {
    return this.subacc;
  }

  /**
   * <p>Setter for subacc.</p>
   * @param pSubacc reference
   **/
  public final void setSubacc(final String pSubacc) {
    this.subacc = pSubacc;
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
}

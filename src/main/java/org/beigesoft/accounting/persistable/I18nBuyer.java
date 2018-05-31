package org.beigesoft.accounting.persistable;

/*
 * Copyright (c) 2018 Beigesoft ™
 *
 * Licensed under the GNU General Public License (GPL), Version 2.0
 * (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html
 */


import org.beigesoft.model.IHasId;
import org.beigesoft.model.IHasName;
import org.beigesoft.model.IHasVersion;
import org.beigesoft.model.AEditable;

/**
 * <p>
 * Model of I18N of overseas buyer.
 * </p>
 *
 * @author Yury Demidenko
 */
public class I18nBuyer extends AEditable
  implements IHasId<DebtorCreditor>, IHasName, IHasVersion {

  /**
   * <p>The buyer, PK.</p>
   **/
  private DebtorCreditor buyer;

  /**
   * <p>String, not null, buyer name in native language.</p>
   **/
  private String itsName;

  /**
   * <p>Registered address1.</p>
   **/
  private String regAddress1;

  /**
   * <p>Registered address2.</p>
   **/
  private String regAddress2;

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
   * <p>Version to check dirty or replication.</p>
   **/
  private Long itsVersion;

  /**
   * <p>Geter for itsVersion.</p>
   * @return Long
   **/
  @Override
  public final Long getItsVersion() {
    return this.itsVersion;
  }

  /**
   * <p>Setter for itsVersion.</p>
   * @param pItsVersion reference
   **/
  @Override
  public final void setItsVersion(final Long pItsVersion) {
    this.itsVersion = pItsVersion;
  }

  /**
   * <p>Geter for itsName.</p>
   * @return String
   **/
  @Override
  public final String getItsName() {
    return this.itsName;
  }

  /**
   * <p>Setter for itsName.</p>
   * @param pItsName reference
   **/
  @Override
  public final void setItsName(final String pItsName) {
    this.itsName = pItsName;
  }

  /**
   * <p>Getter for itsId.</p>
   * @return DebtorCreditor
   **/
  @Override
  public final DebtorCreditor getItsId() {
    return this.buyer;
  }

  /**
   * <p>Setter for itsId.</p>
   * @param pItsId reference
   **/
  @Override
  public final void setItsId(final DebtorCreditor pItsId) {
    this.buyer = pItsId;
  }

  //Simple getters and setters:
  /**
   * <p>Getter for buyer.</p>
   * @return DebtorCreditor
   **/
  public final DebtorCreditor getBuyer() {
    return this.buyer;
  }

  /**
   * <p>Setter for buyer.</p>
   * @param pBuyer reference
   **/
  public final void setBuyer(final DebtorCreditor pBuyer) {
    this.buyer = pBuyer;
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
}

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
import org.beigesoft.model.IHasVersion;
import org.beigesoft.model.AEditable;
import org.beigesoft.persistable.Languages;

/**
 * <p>
 * Model of I18N accounting common.
 * </p>
 *
 * @author Yury Demidenko
 */
public class I18nAccounting extends AEditable
  implements IHasId<Languages>, IHasVersion {

  /**
   * <p>The language, PK.</p>
   **/
  private Languages lang;

  /**
   * <p>String, not null, organization name in the language.
   * It's to report financial statements in foreign language,
   * e.g. balance sheet for foreign investors.</p>
   **/
  private String organizationName;

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
   * <p>Getter for itsId.</p>
   * @return Languages
   **/
  @Override
  public final Languages getItsId() {
    return this.lang;
  }

  /**
   * <p>Setter for itsId.</p>
   * @param pItsId reference
   **/
  @Override
  public final void setItsId(final Languages pItsId) {
    this.lang = pItsId;
  }

  //Simple getters and setters:
  /**
   * <p>Getter for lang.</p>
   * @return Languages
   **/
  public final Languages getLang() {
    return this.lang;
  }

  /**
   * <p>Setter for lang.</p>
   * @param pLang reference
   **/
  public final void setLang(final Languages pLang) {
    this.lang = pLang;
  }

  /**
   * <p>Getter for organizationName.</p>
   * @return String
   **/
  public final String getOrganizationName() {
    return this.organizationName;
  }

  /**
   * <p>Setter for organizationName.</p>
   * @param pOrganizationName reference
   **/
  public final void setOrganizationName(final String pOrganizationName) {
    this.organizationName = pOrganizationName;
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

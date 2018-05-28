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
 * Model of I18N Web-store common.
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
}

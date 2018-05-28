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
import org.beigesoft.persistable.AI18nName;
import org.beigesoft.persistable.Languages;

/**
 * <p>
 * Model of I18N name of goods.
 * </p>
 *
 * @author Yury Demidenko
 */
public class I18nCurrency extends AI18nName
  implements IHasId<IdI18nCurrency> {
  /**
   * <p>ID.</p>
   **/
  private IdI18nCurrency itsId = new IdI18nCurrency();

  /**
   * <p>Internationalized thing.</p>
   **/
  private Currency hasName;

  /**
   * <p>The language.</p>
   **/
  private Languages lang;

  /**
   * <p>Getter for itsId.</p>
   * @return IdI18nCurrency
   **/
  @Override
  public final IdI18nCurrency getItsId() {
    return this.itsId;
  }

  /**
   * <p>Setter for itsId.</p>
   * @param pItsId reference
   **/
  @Override
  public final void setItsId(final IdI18nCurrency pItsId) {
    this.itsId = pItsId;
    if (this.itsId == null) {
      this.lang = null;
      this.hasName = null;
    } else {
      this.lang = this.itsId.getLang();
      this.hasName = this.itsId.getHasName();
    }
  }

  /**
   * <p>Setter for lang.</p>
   * @param pLang reference
   **/
  public final void setLang(final Languages pLang) {
    this.lang = pLang;
    if (this.itsId == null) {
      this.itsId = new IdI18nCurrency();
    }
    this.itsId.setLang(this.lang);
  }

  /**
   * <p>Setter for hasName.</p>
   * @param pHasName reference
   **/
  public final void setHasName(final Currency pHasName) {
    this.hasName = pHasName;
    if (this.itsId == null) {
      this.itsId = new IdI18nCurrency();
    }
    this.itsId.setHasName(this.hasName);
  }

  //SGS:
  /**
   * <p>Getter for hasName.</p>
   * @return Currency
   **/
  public final Currency getHasName() {
    return this.hasName;
  }

  /**
   * <p>Getter for lang.</p>
   * @return Languages
   **/
  public final Languages getLang() {
    return this.lang;
  }
}

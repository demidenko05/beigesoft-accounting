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

import org.beigesoft.persistable.Languages;

/**
 * <p>
 * ID of I18N name of currency.
 * </p>
 *
 * @author Yury Demidenko
 */
public class IdI18nCurrency {

  /**
   * <p>Internationalized thing.</p>
   **/
  private Currency hasName;


  /**
   * <p>The language.</p>
   **/
  private Languages lang;

  //Simple getters and setters:
  /**
   * <p>Getter for hasName.</p>
   * @return Currency
   **/
  public final Currency getHasName() {
    return this.hasName;
  }

  /**
   * <p>Setter for hasName.</p>
   * @param pHasName reference
   **/
  public final void setHasName(final Currency pHasName) {
    this.hasName = pHasName;
  }

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
}

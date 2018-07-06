package org.beigesoft.accounting.persistable;

/*
 * Copyright (c) 2018 Beigesoft™
 *
 * Licensed under the GNU General Public License (GPL), Version 2.0
 * (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html
 */

import org.beigesoft.persistable.AI18nName;
import org.beigesoft.persistable.Languages;

/**
 * <p>
 * Model of I18N name of goods.
 * </p>
 *
 * @author Yury Demidenko
 */
public class I18nCurrency extends AI18nName<Currency, IdI18nCurrency> {
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
   * <p>Not Null, if  if uses currency sign in reports (e.g. $),
   * otherwise itsName (e.g. USD).</p>
   **/
  private Boolean useCurrencySign = false;

  /**
   * <p>Not Null, if print currency on left of amount
   * e.g. "1,356.12$" or "$1,356.12".</p>
   **/
  private Boolean printCurrencyLeft = false;

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
  @Override
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
  @Override
  public final void setHasName(final Currency pHasName) {
    this.hasName = pHasName;
    if (this.itsId == null) {
      this.itsId = new IdI18nCurrency();
    }
    this.itsId.setHasName(this.hasName);
  }

  /**
   * <p>Getter for hasName.</p>
   * @return Currency
   **/
  @Override
  public final Currency getHasName() {
    return this.hasName;
  }

  /**
   * <p>Getter for lang.</p>
   * @return Languages
   **/
  @Override
  public final Languages getLang() {
    return this.lang;
  }

  //SGS:
  /**
   * <p>Getter for useCurrencySign.</p>
   * @return Boolean
   **/
  public final Boolean getUseCurrencySign() {
    return this.useCurrencySign;
  }

  /**
   * <p>Setter for useCurrencySign.</p>
   * @param pUseCurrencySign reference
   **/
  public final void setUseCurrencySign(final Boolean pUseCurrencySign) {
    this.useCurrencySign = pUseCurrencySign;
  }

  /**
   * <p>Getter for printCurrencyLeft.</p>
   * @return Boolean
   **/
  public final Boolean getPrintCurrencyLeft() {
    return this.printCurrencyLeft;
  }

  /**
   * <p>Setter for printCurrencyLeft.</p>
   * @param pPrintCurrencyLeft reference
   **/
  public final void setPrintCurrencyLeft(final Boolean pPrintCurrencyLeft) {
    this.printCurrencyLeft = pPrintCurrencyLeft;
  }
}

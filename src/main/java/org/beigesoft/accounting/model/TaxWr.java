package org.beigesoft.accounting.model;

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

import java.math.BigDecimal;

import org.beigesoft.accounting.persistable.Tax;

/**
 * <p>
 * Tax wrapper with additional information.
 * </p>
 *
 * @author Yury Demidenko
 */
public class TaxWr {

  /**
   * <p>Tax.</p>
   **/
  private Tax tax;

  /**
   * <p>Rate=tax.itsPercentage/100.</p>
   **/
  private BigDecimal rate = BigDecimal.ZERO;

  /**
   * <p>If used.</p>
   **/
  private Boolean isUsed = Boolean.FALSE;

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
   * <p>Getter for rate.</p>
   * @return BigDecimal
   **/
  public final BigDecimal getRate() {
    return this.rate;
  }

  /**
   * <p>Setter for rate.</p>
   * @param pRate reference
   **/
  public final void setRate(final BigDecimal pRate) {
    this.rate = pRate;
  }

  /**
   * <p>Getter for isUsed.</p>
   * @return Boolean
   **/
  public final Boolean getIsUsed() {
    return this.isUsed;
  }

  /**
   * <p>Setter for isUsed.</p>
   * @param pIsUsed reference
   **/
  public final void setIsUsed(final Boolean pIsUsed) {
    this.isUsed = pIsUsed;
  }
}

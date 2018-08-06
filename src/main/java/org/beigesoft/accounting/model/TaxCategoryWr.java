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

import org.beigesoft.accounting.persistable.InvItemTaxCategory;

/**
 * <p>
 * Tax category wrapper with additional information.
 * </p>
 *
 * @author Yury Demidenko
 */
public class TaxCategoryWr {

  /**
   * <p>Tax category.</p>
   **/
  private InvItemTaxCategory taxCategory;

  /**
   * <p>Aggregate percent.</p>
   **/
  private BigDecimal aggrPercent = BigDecimal.ZERO;

  /**
   * <p>aggrRate=1+aggrPercent/100.</p>
   **/
  private BigDecimal aggrRate = BigDecimal.ZERO;

  /**
   * <p>If used.</p>
   **/
  private Boolean isUsed = Boolean.FALSE;

  //Simple getters and setters:
  /**
   * <p>Getter for taxCategory.</p>
   * @return InvItemTaxCategory
   **/
  public final InvItemTaxCategory getTaxCategory() {
    return this.taxCategory;
  }

  /**
   * <p>Setter for taxCategory.</p>
   * @param pTaxCategory reference
   **/
  public final void setTaxCategory(final InvItemTaxCategory pTaxCategory) {
    this.taxCategory = pTaxCategory;
  }

  /**
   * <p>Getter for aggrPercent.</p>
   * @return BigDecimal
   **/
  public final BigDecimal getAggrPercent() {
    return this.aggrPercent;
  }

  /**
   * <p>Setter for aggrPercent.</p>
   * @param pAggrPercent reference
   **/
  public final void setAggrPercent(final BigDecimal pAggrPercent) {
    this.aggrPercent = pAggrPercent;
  }

  /**
   * <p>Getter for aggrRate.</p>
   * @return BigDecimal
   **/
  public final BigDecimal getAggrRate() {
    return this.aggrRate;
  }

  /**
   * <p>Setter for aggrRate.</p>
   * @param pAggrRate reference
   **/
  public final void setAggrRate(final BigDecimal pAggrRate) {
    this.aggrRate = pAggrRate;
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

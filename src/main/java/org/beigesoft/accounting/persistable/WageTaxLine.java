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

import java.math.BigDecimal;

import org.beigesoft.model.IOwned;
import org.beigesoft.accounting.persistable.base.ADocTaxLine;

/**
 * <pre>
 * Model of Wage Tax Line.
 * </pre>
 *
 * @author Yury Demidenko
 */
public class WageTaxLine extends ADocTaxLine
  implements IOwned<Wage> {

  /**
   * <p>Wage.</p>
   **/
  private Wage itsOwner;

  /**
   * <p>Percentage.</p>
   **/
  private BigDecimal itsPercentage = BigDecimal.ZERO;

  /**
   * <p>Plus amount, not null.</p>
   **/
  private BigDecimal plusAmount = BigDecimal.ZERO;

  /**
   * <p>Allowance, not null.</p>
   **/
  private BigDecimal allowance = BigDecimal.ZERO;

  /**
   * <p>Description.</p>
   **/
  private String description;

  /**
   * <p>Getter for itsOwner.</p>
   * @return Wage
   **/
  @Override
  public final Wage getItsOwner() {
    return this.itsOwner;
  }

  /**
   * <p>Setter for itsOwner.</p>
   * @param pItsOwner reference
   **/
  @Override
  public final void setItsOwner(final Wage pItsOwner) {
    this.itsOwner = pItsOwner;
  }

  //Simple getters and setters:
  /**
   * <p>Getter for itsPercentage.</p>
   * @return BigDecimal
   **/
  public final BigDecimal getItsPercentage() {
    return this.itsPercentage;
  }

  /**
   * <p>Setter for itsPercentage.</p>
   * @param pItsPercentage reference
   **/
  public final void setItsPercentage(final BigDecimal pItsPercentage) {
    this.itsPercentage = pItsPercentage;
  }

  /**
   * <p>Getter for plusAmount.</p>
   * @return BigDecimal
   **/
  public final BigDecimal getPlusAmount() {
    return this.plusAmount;
  }

  /**
   * <p>Setter for plusAmount.</p>
   * @param pPlusAmount reference
   **/
  public final void setPlusAmount(final BigDecimal pPlusAmount) {
    this.plusAmount = pPlusAmount;
  }

  /**
   * <p>Getter for allowance.</p>
   * @return BigDecimal
   **/
  public final BigDecimal getAllowance() {
    return this.allowance;
  }

  /**
   * <p>Setter for allowance.</p>
   * @param pAllowance reference
   **/
  public final void setAllowance(final BigDecimal pAllowance) {
    this.allowance = pAllowance;
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

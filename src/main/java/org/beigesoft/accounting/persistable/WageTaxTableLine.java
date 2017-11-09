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
import org.beigesoft.persistable.APersistableBaseVersion;

/**
 * <pre>
 * Model of Wage Tax Line of payroll tax table.
 * Version changed time algorithm.
 * </pre>
 *
 * @author Yury Demidenko
 */
public class WageTaxTableLine extends APersistableBaseVersion
  implements IOwned<WageTaxTable> {

  /**
   * <p>Wage.</p>
   **/
  private WageTaxTable itsOwner;

  /**
   * <p>Allowance, not null.</p>
   **/
  private BigDecimal allowance = BigDecimal.ZERO;

  /**
   * <p>Not Null wage equals or more than...</p>
   **/
  private BigDecimal wageFrom = BigDecimal.ZERO;

  /**
   * <p>Not Null wage less than...</p>
   **/
  private BigDecimal wageTo = BigDecimal.ZERO;

  /**
   * <p>Percentage.</p>
   **/
  private BigDecimal itsPercentage = BigDecimal.ZERO;

  /**
   * <p>Plus amount, not null.</p>
   **/
  private BigDecimal plusAmount = BigDecimal.ZERO;

  /**
   * <p>Not Null year wage equals or more than...</p>
   **/
  private BigDecimal yearWageFrom = BigDecimal.ZERO;

  /**
   * <p>Not Null year wage less than...</p>
   **/
  private BigDecimal yearWageTo = new BigDecimal("99999999.99");

  /**
   * <p>Getter for itsOwner.</p>
   * @return WageTaxTable
   **/
  @Override
  public final WageTaxTable getItsOwner() {
    return this.itsOwner;
  }

  /**
   * <p>Setter for itsOwner.</p>
   * @param pItsOwner reference
   **/
  @Override
  public final void setItsOwner(final WageTaxTable pItsOwner) {
    this.itsOwner = pItsOwner;
  }

  //Simple getters and setters:
  /**
   * <p>Getter for wageFrom.</p>
   * @return BigDecimal
   **/
  public final BigDecimal getWageFrom() {
    return this.wageFrom;
  }

  /**
   * <p>Setter for wageFrom.</p>
   * @param pWageFrom reference
   **/
  public final void setWageFrom(final BigDecimal pWageFrom) {
    this.wageFrom = pWageFrom;
  }

  /**
   * <p>Getter for wageTo.</p>
   * @return BigDecimal
   **/
  public final BigDecimal getWageTo() {
    return this.wageTo;
  }

  /**
   * <p>Setter for wageTo.</p>
   * @param pWageTo reference
   **/
  public final void setWageTo(final BigDecimal pWageTo) {
    this.wageTo = pWageTo;
  }

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
   * <p>Getter for yearWageFrom.</p>
   * @return BigDecimal
   **/
  public final BigDecimal getYearWageFrom() {
    return this.yearWageFrom;
  }

  /**
   * <p>Setter for yearWageFrom.</p>
   * @param pYearWageFrom reference
   **/
  public final void setYearWageFrom(final BigDecimal pYearWageFrom) {
    this.yearWageFrom = pYearWageFrom;
  }

  /**
   * <p>Getter for yearWageTo.</p>
   * @return BigDecimal
   **/
  public final BigDecimal getYearWageTo() {
    return this.yearWageTo;
  }

  /**
   * <p>Setter for yearWageTo.</p>
   * @param pYearWageTo reference
   **/
  public final void setYearWageTo(final BigDecimal pYearWageTo) {
    this.yearWageTo = pYearWageTo;
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
}

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

import java.math.RoundingMode;

import org.beigesoft.persistable.AHasNameIdLongVersion;

/**
 * <p>
 * Model of Tax Destination method.
 * </p>
 *
 * @author Yury Demidenko
 */
public class TaxDestination extends AHasNameIdLongVersion {

  /**
   * <p>Rounding mode for sales taxes.</p>
   **/
  private RoundingMode salTaxRoundMode = RoundingMode.HALF_UP;

  /**
   * <p>Grouping method for sales taxes - false item basis, true - invoice.
   * This is about grouping rounding error:
   * round(2.244 + 2.244) != round(2.244) + round(2.244);
   * 4.49 != 4.48
   * </p>
   **/
  private Boolean salTaxIsInvoiceBase = Boolean.FALSE;

  /**
   * <p>Use aggregate tax rate or only tax.</p>
   **/
  private Boolean salTaxUseAggregItBas = Boolean.FALSE;

  //Simple getters and setters:
  /**
   * <p>Getter for salTaxRoundMode.</p>
   * @return RoundingMode
   **/
  public final RoundingMode getSalTaxRoundMode() {
    return this.salTaxRoundMode;
  }

  /**
   * <p>Setter for salTaxRoundMode.</p>
   * @param pSalTaxRoundMode reference
   **/
  public final void setSalTaxRoundMode(final RoundingMode pSalTaxRoundMode) {
    this.salTaxRoundMode = pSalTaxRoundMode;
  }

  /**
   * <p>Getter for salTaxIsInvoiceBase.</p>
   * @return Boolean
   **/
  public final Boolean getSalTaxIsInvoiceBase() {
    return this.salTaxIsInvoiceBase;
  }

  /**
   * <p>Setter for salTaxIsInvoiceBase.</p>
   * @param pSalTaxIsInvoiceBase reference
   **/
  public final void setSalTaxIsInvoiceBase(final Boolean pSalTaxIsInvoiceBase) {
    this.salTaxIsInvoiceBase = pSalTaxIsInvoiceBase;
  }

  /**
   * <p>Getter for salTaxUseAggregItBas.</p>
   * @return Boolean
   **/
  public final Boolean getSalTaxUseAggregItBas() {
    return this.salTaxUseAggregItBas;
  }

  /**
   * <p>Setter for salTaxUseAggregItBas.</p>
   * @param pSalTaxUseAggregItBas reference
   **/
  public final void setSalTaxUseAggregItBas(
    final Boolean pSalTaxUseAggregItBas) {
    this.salTaxUseAggregItBas = pSalTaxUseAggregItBas;
  }
}

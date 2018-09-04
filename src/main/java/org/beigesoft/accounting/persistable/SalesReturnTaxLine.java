package org.beigesoft.accounting.persistable;

/*
 * Copyright (c) 2016 Beigesoft™
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
 * <p>
 * Model of Sales Return tax Line.
 * </p>
 *
 * @author Yury Demidenko
 */
public class SalesReturnTaxLine extends ADocTaxLine
  implements IOwned<SalesReturn> {

  /**
   * <p>Vendor Invoice.</p>
   **/
  private SalesReturn itsOwner;

  /**
   * <p>It's 0 if item basis otherwise taxable amount for invoice basis.</p>
   **/
  private BigDecimal taxableInvBas = BigDecimal.ZERO;

  /**
   * <p>Geter for itsOwner.</p>
   * @return SalesReturn
   **/
  @Override
  public final SalesReturn getItsOwner() {
    return this.itsOwner;
  }

  /**
   * <p>Setter for itsOwner.</p>
   * @param pItsOwner reference
   **/
  @Override
  public final void setItsOwner(final SalesReturn pItsOwner) {
    this.itsOwner = pItsOwner;
  }

  //SGS:
  /**
   * <p>Getter for taxableInvBas.</p>
   * @return BigDecimal
   **/
  public final BigDecimal getTaxableInvBas() {
    return this.taxableInvBas;
  }

  /**
   * <p>Setter for taxableInvBas.</p>
   * @param pTaxableInvBas reference
   **/
  public final void setTaxableInvBas(final BigDecimal pTaxableInvBas) {
    this.taxableInvBas = pTaxableInvBas;
  }
}

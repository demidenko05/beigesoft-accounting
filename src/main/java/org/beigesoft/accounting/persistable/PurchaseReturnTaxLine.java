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
 * Model of Purchase Return Tax Line.
 * </pre>
 *
 * @author Yury Demidenko
 */
public class PurchaseReturnTaxLine extends ADocTaxLine
  implements IOwned<PurchaseReturn> {
  /**
   * <p>Customer Invoice.</p>
   **/
  private PurchaseReturn itsOwner;

  /**
   * <p>It's 0 if item basis otherwise taxable amount for invoice basis.</p>
   **/
  private BigDecimal taxableInvBas = BigDecimal.ZERO;

  /**
   * <p>Geter for itsOwner.</p>
   * @return PurchaseReturn
   **/
  @Override
  public final PurchaseReturn getItsOwner() {
    return this.itsOwner;
  }

  /**
   * <p>Setter for itsOwner.</p>
   * @param pItsOwner reference
   **/
  @Override
  public final void setItsOwner(final PurchaseReturn pItsOwner) {
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

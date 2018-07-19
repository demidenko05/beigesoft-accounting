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
import org.beigesoft.accounting.persistable.base.ADocTaxLineFc;

/**
 * <pre>
 * Model of Vendor Invoice tax Line.
 * </pre>
 *
 * @author Yury Demidenko
 */
public class PurchaseInvoiceTaxLine extends ADocTaxLineFc
  implements IOwned<PurchaseInvoice> {

  /**
   * <p>Vendor Invoice.</p>
   **/
  private PurchaseInvoice itsOwner;

  /**
   * <p>It's 0 if item basis otherwise taxable amount for invoice basis.</p>
   **/
  private BigDecimal taxableInvBas = BigDecimal.ZERO;
  /**
   * <p>It's 0 if item basis otherwise taxable amount in foreign currency
   * for invoice basis.</p>
   **/
  private BigDecimal taxableInvBasFc = BigDecimal.ZERO;

  /**
   * <p>Geter for itsOwner.</p>
   * @return PurchaseInvoice
   **/
  @Override
  public final PurchaseInvoice getItsOwner() {
    return this.itsOwner;
  }

  /**
   * <p>Setter for itsOwner.</p>
   * @param pItsOwner reference
   **/
  @Override
  public final void setItsOwner(final PurchaseInvoice pItsOwner) {
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

  /**
   * <p>Getter for taxableInvBasFc.</p>
   * @return BigDecimal
   **/
  public final BigDecimal getTaxableInvBasFc() {
    return this.taxableInvBasFc;
  }

  /**
   * <p>Setter for taxableInvBasFc.</p>
   * @param pTaxableInvBasFc reference
   **/
  public final void setTaxableInvBasFc(final BigDecimal pTaxableInvBasFc) {
    this.taxableInvBasFc = pTaxableInvBasFc;
  }
}

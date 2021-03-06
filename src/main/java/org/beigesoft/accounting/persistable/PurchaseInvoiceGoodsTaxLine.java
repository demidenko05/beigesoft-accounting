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

import org.beigesoft.accounting.persistable.base.ALineTxLn;

/**
 * <p>
 * Model of Vendor Invoice tax Line for goods line for
 * implementing item basis method.
 * </p>
 *
 * @author Yury Demidenko
 */
public class PurchaseInvoiceGoodsTaxLine
  extends ALineTxLn<PurchaseInvoice,  PurchaseInvoiceLine> {

  /**
   * <p>Goods line.</p>
   **/
  private PurchaseInvoiceLine itsOwner;

  /**
   * <p>Geter for itsOwner.</p>
   * @return PurchaseInvoice
   **/
  @Override
  public final PurchaseInvoiceLine getItsOwner() {
    return this.itsOwner;
  }

  /**
   * <p>Setter for itsOwner.</p>
   * @param pItsOwner reference
   **/
  @Override
  public final void setItsOwner(final PurchaseInvoiceLine pItsOwner) {
    this.itsOwner = pItsOwner;
  }
}

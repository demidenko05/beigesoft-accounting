package org.beigesoft.accounting.persistable;

/*
 * Copyright (c) 2018 Beigesoft ™
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
 * Model of Vendor Invoice tax Line for service line for
 * implementing item basis method.
 * </p>
 *
 * @author Yury Demidenko
 */
public class PurchaseInvoiceServiceTaxLine
  extends ALineTxLn<PurchaseInvoice, PurchaseInvoiceServiceLine> {

  /**
   * <p>Service line.</p>
   **/
  private PurchaseInvoiceServiceLine itsOwner;

  /**
   * <p>Geter for itsOwner.</p>
   * @return PurchaseInvoice
   **/
  @Override
  public final PurchaseInvoiceServiceLine getItsOwner() {
    return this.itsOwner;
  }

  /**
   * <p>Setter for itsOwner.</p>
   * @param pItsOwner reference
   **/
  @Override
  public final void setItsOwner(final PurchaseInvoiceServiceLine pItsOwner) {
    this.itsOwner = pItsOwner;
  }
}

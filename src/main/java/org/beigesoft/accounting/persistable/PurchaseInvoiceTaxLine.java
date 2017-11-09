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

import org.beigesoft.model.IOwned;
import org.beigesoft.accounting.persistable.base.ADocTaxLine;

/**
 * <pre>
 * Model of Vendor Invoice tax Line.
 * </pre>
 *
 * @author Yury Demidenko
 */
public class PurchaseInvoiceTaxLine extends ADocTaxLine
  implements IOwned<PurchaseInvoice> {

  /**
   * <p>Vendor Invoice.</p>
   **/
  private PurchaseInvoice itsOwner;

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
}

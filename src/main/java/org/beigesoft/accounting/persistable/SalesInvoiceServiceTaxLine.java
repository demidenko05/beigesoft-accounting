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

import org.beigesoft.model.IOwned;
import org.beigesoft.accounting.persistable.base.ADocTaxLine;

/**
 * <p>
 * Model of Invoice tax Line for service line.
 * </p>
 *
 * @author Yury Demidenko
 */
public class SalesInvoiceServiceTaxLine extends ADocTaxLine
  implements IOwned<SalesInvoiceServiceLine> {

  /**
   * <p>Invoice ID (to improve performance).</p>
   **/
  private Long invoiceId;

  /**
   * <p>Service line.</p>
   **/
  private SalesInvoiceServiceLine itsOwner;

  /**
   * <p>Geter for itsOwner.</p>
   * @return SalesInvoice
   **/
  @Override
  public final SalesInvoiceServiceLine getItsOwner() {
    return this.itsOwner;
  }

  /**
   * <p>Setter for itsOwner.</p>
   * @param pItsOwner reference
   **/
  @Override
  public final void setItsOwner(final SalesInvoiceServiceLine pItsOwner) {
    this.itsOwner = pItsOwner;
  }

  //Simple getters and setters:
  /**
   * <p>Getter for invoiceId.</p>
   * @return Long
   **/
  public final Long getInvoiceId() {
    return this.invoiceId;
  }

  /**
   * <p>Setter for invoiceId.</p>
   * @param pInvoiceId reference
   **/
  public final void setInvoiceId(final Long pInvoiceId) {
    this.invoiceId = pInvoiceId;
  }
}
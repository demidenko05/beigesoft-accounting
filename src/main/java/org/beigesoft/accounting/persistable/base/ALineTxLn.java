package org.beigesoft.accounting.persistable.base;

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

import org.beigesoft.model.IOwned;
import org.beigesoft.accounting.persistable.IInvoice;
import org.beigesoft.accounting.persistable.IInvoiceLine;

/**
 * <p>Model of invoice tax Lines for service line for
 * implementing item basis method.</p>
 *
 * @param <T> invoice type
 * @param <L> invoice line type
 * @author Yury Demidenko
 */
public abstract class ALineTxLn<T extends IInvoice, L extends IInvoiceLine<T>>
  extends ADocTaxLineFc implements IOwned<L> {

  /**
   * <p>Invoice ID (to improve performance).</p>
   **/
  private Long invoiceId;

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

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
 * <pre>
 * Model of Customer Invoice Tax Line.
 * </pre>
 *
 * @author Yury Demidenko
 */
public class SalesInvoiceTaxLine extends ADocTaxLine
  implements IOwned<SalesInvoice> {
  /**
   * <p>Customer Invoice.</p>
   **/
  private SalesInvoice itsOwner;

  /**
   * <p>Total taxes in foreign currency, if used,
   * in case of domestic sales (if law allow it).</p>
   **/
  private BigDecimal foreignTotalTaxes = BigDecimal.ZERO;

  /**
   * <p>Geter for itsOwner.</p>
   * @return SalesInvoice
   **/
  @Override
  public final SalesInvoice getItsOwner() {
    return this.itsOwner;
  }

  /**
   * <p>Setter for itsOwner.</p>
   * @param pItsOwner reference
   **/
  @Override
  public final void setItsOwner(final SalesInvoice pItsOwner) {
    this.itsOwner = pItsOwner;
  }

  //SGS:
  /**
   * <p>Getter for foreignTotalTaxes.</p>
   * @return BigDecimal
   **/
  public final BigDecimal getForeignTotalTaxes() {
    return this.foreignTotalTaxes;
  }

  /**
   * <p>Setter for foreignTotalTaxes.</p>
   * @param pForeignTotalTaxes reference
   **/
  public final void setForeignTotalTaxes(final BigDecimal pForeignTotalTaxes) {
    this.foreignTotalTaxes = pForeignTotalTaxes;
  }
}

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

import java.math.BigDecimal;

import org.beigesoft.model.IOwned;
import org.beigesoft.accounting.persistable.IInvoice;

/**
 * <p>Abstract Model of Invoice Tax Line.</p>
 *
 * @param <T> invoice type
 * @author Yury Demidenko
 */
public abstract class AInvTxLn<T extends IInvoice> extends ADocTaxLineFc
  implements IOwned<T> {

  /**
   * <p>It's 0 if item basis otherwise taxable amount for invoice basis.</p>
   **/
  private BigDecimal taxableInvBas = BigDecimal.ZERO;

  /**
   * <p>It's 0 if item basis otherwise taxable amount in foreign currency
   * for invoice basis.</p>
   **/
  private BigDecimal taxableInvBasFc = BigDecimal.ZERO;

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

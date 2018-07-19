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

/**
 * <pre>
 * Abstraction of document tax Line with foreign total.
 * Version, reliable autoincrement algorithm.
 * </pre>
 *
 * @author Yury Demidenko
 */
public abstract class ADocTaxLineFc extends ADocTaxLine {

  /**
   * <p>Total tax in foreign currency, if used,
   * in case of domestic sales/purchases (if law allows it).</p>
   **/
  private BigDecimal foreignTotalTaxes = BigDecimal.ZERO;

  //Simple getters and setters:
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

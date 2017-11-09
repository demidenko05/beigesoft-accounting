package org.beigesoft.accounting.persistable.base;

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


import org.beigesoft.persistable.APersistableBaseVersion;
import org.beigesoft.accounting.persistable.Tax;

/**
 * <pre>
 * Abstraction of tax line.
 * Version, changed time algorithm cause check dirty of
 * calculated from it (derived) records.
 * </pre>
 *
 * @author Yury Demidenko
 */
public abstract class ATaxLine extends APersistableBaseVersion {

  /**
   * <p>Tax.</p>
   **/
  private Tax tax;

  /**
   * <p>Percentage.</p>
   **/
  private BigDecimal itsPercentage = BigDecimal.ZERO;

  //Simple getters and setters:
  /**
   * <p>Geter for itsPercentage.</p>
   * @return BigDecimal
   **/
  public final BigDecimal getItsPercentage() {
    return this.itsPercentage;
  }

  /**
   * <p>Setter for itsPercentage.</p>
   * @param pItsPercentage reference
   **/
  public final void setItsPercentage(final BigDecimal pItsPercentage) {
    this.itsPercentage = pItsPercentage;
  }

  /**
   * <p>Geter for tax.</p>
   * @return Tax
   **/
  public final Tax getTax() {
    return this.tax;
  }

  /**
   * <p>Setter for tax.</p>
   * @param pTax reference
   **/
  public final void setTax(final Tax pTax) {
    this.tax = pTax;
  }
}

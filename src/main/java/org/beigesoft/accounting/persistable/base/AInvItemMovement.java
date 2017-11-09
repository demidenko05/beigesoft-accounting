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
import org.beigesoft.accounting.persistable.InvItem;
import org.beigesoft.accounting.persistable.UnitOfMeasure;

/**
 * <pre>
 * Abstract model of invItem movement.
 * Version, reliable autoincrement algorithm.
 * </pre>
 *
 * @author Yury Demidenko
 */
public abstract class AInvItemMovement extends APersistableBaseVersion {

  /**
   * <p>InvItem.</p>
   **/
  private InvItem invItem;

  /**
   * <p>Unit Of Measure.</p>
   **/
  private UnitOfMeasure unitOfMeasure;

  /**
   * <p>Quantity.</p>
   **/
  private BigDecimal itsQuantity = BigDecimal.ZERO;

  //Simple getters and setters:
  /**
   * <p>Geter for invItem.</p>
   * @return InvItem
   **/
  public final InvItem getInvItem() {
    return this.invItem;
  }

  /**
   * <p>Setter for invItem.</p>
   * @param pInvItem reference
   **/
  public final void setInvItem(final InvItem pInvItem) {
    this.invItem = pInvItem;
  }

  /**
   * <p>Geter for unitOfMeasure.</p>
   * @return UnitOfMeasure
   **/
  public final UnitOfMeasure getUnitOfMeasure() {
    return this.unitOfMeasure;
  }

  /**
   * <p>Setter for unitOfMeasure.</p>
   * @param pUnitOfMeasure reference
   **/
  public final void setUnitOfMeasure(final UnitOfMeasure pUnitOfMeasure) {
    this.unitOfMeasure = pUnitOfMeasure;
  }

  /**
   * <p>Geter for itsQuantity.</p>
   * @return BigDecimal
   **/
  public final BigDecimal getItsQuantity() {
    return this.itsQuantity;
  }

  /**
   * <p>Setter for itsQuantity.</p>
   * @param pItsQuantity reference
   **/
  public final void setItsQuantity(final BigDecimal pItsQuantity) {
    this.itsQuantity = pItsQuantity;
  }
}

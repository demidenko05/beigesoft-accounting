package org.beigesoft.accounting.model;

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

/**
 * <pre>
 * Warehouse Rest Line.
 * </pre>
 *
 * @author Yury Demidenko
 */
public class WarehouseRestLine {

  /**
   * <p>Warehouse name.</p>
   **/
  private String warehouse;

  /**
   * <p>Inventory item name.</p>
   **/
  private String invItem;

  /**
   * <p>Unit of measure name.</p>
   **/
  private String unitOfMeasure;

  /**
   * <p>Inventory item ID.</p>
   **/
  private Long invItemId;

  /**
   * <p>Credit account.</p>
   **/
  private BigDecimal theRest;

  //Simple getters and setters:
  /**
   * <p>Getter for warehouse.</p>
   * @return String
   **/
  public final String getWarehouse() {
    return this.warehouse;
  }

  /**
   * <p>Setter for warehouse.</p>
   * @param pWarehouse reference
   **/
  public final void setWarehouse(final String pWarehouse) {
    this.warehouse = pWarehouse;
  }

  /**
   * <p>Getter for invItem.</p>
   * @return String
   **/
  public final String getInvItem() {
    return this.invItem;
  }

  /**
   * <p>Setter for invItem.</p>
   * @param pInvItem reference
   **/
  public final void setInvItem(final String pInvItem) {
    this.invItem = pInvItem;
  }

  /**
   * <p>Getter for invItemId.</p>
   * @return Long
   **/
  public final Long getInvItemId() {
    return this.invItemId;
  }

  /**
   * <p>Setter for invItemId.</p>
   * @param pInvItemId reference
   **/
  public final void setInvItemId(final Long pInvItemId) {
    this.invItemId = pInvItemId;
  }

  /**
   * <p>Getter for theRest.</p>
   * @return BigDecimal
   **/
  public final BigDecimal getTheRest() {
    return this.theRest;
  }

  /**
   * <p>Setter for theRest.</p>
   * @param pTheRest reference
   **/
  public final void setTheRest(final BigDecimal pTheRest) {
    this.theRest = pTheRest;
  }

  /**
   * <p>Getter for unitOfMeasure.</p>
   * @return String
   **/
  public final String getUnitOfMeasure() {
    return this.unitOfMeasure;
  }

  /**
   * <p>Setter for unitOfMeasure.</p>
   * @param pUnitOfMeasure reference
   **/
  public final void setUnitOfMeasure(final String pUnitOfMeasure) {
    this.unitOfMeasure = pUnitOfMeasure;
  }
}

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

import java.math.BigDecimal;

import org.beigesoft.accounting.persistable.base.AInvItemMovement;

/**
 * <pre>
 * Model of Warehouse Movements that derived from a document
 * line e.g. PurchaseInvoiceLine.
 * It is for reverse deriving lines cause a deriving line
 * can change several warehouse rests.
 * All deriving lines are immutable.
 * It can't be created by foreign source!
 * </pre>
 *
 * @author Yury Demidenko
 */
public class WarehouseEntry extends AInvItemMovement {

  /**
   * <p>ID of reversed/reversing WarehouseEntry.</p>
   **/
  private Long reversedId;

  /**
   * <p>Source ID.</p>
   **/
  private Long sourceId;

  /**
   * <p>Source Type e.g. 1001 - PurchaseInvoiceLine.
   * 1002 - SalesInvoiceLine.</p>
   **/
  private Integer sourceType;

  /**
   * <p>Source document ID if exists, e.g. PurchaseInvoice ID.</p>
   **/
  private Long sourceOwnerId;

  /**
   * <p>Source document Type if exists e.g. 1 - PurchaseInvoice.</p>
   **/
  private Integer sourceOwnerType;

  /**
   * <p>Warehouse Place from, may be null if from outside.</p>
   **/
  private WarehouseSite warehouseSiteFrom;

  /**
   * <p>Warehouse Place to , may be null if to outside.</p>
   **/
  private WarehouseSite warehouseSiteTo;

  /**
   * <p>Description e.g. Load 2 each egg to shelf#1
   * at 12.12.12 12:12 by PIL 4 in PI 6 of 12.11.21.</p>
   **/
  private String description;

  /**
   * <p>Default constructor.</p>
   **/
  public WarehouseEntry() {
  }

  /**
   * <p>Useful constructor.</p>
   * @param pSourceId Source Id
   * @param pSourceType Source Type
   * @param pWarehouseSiteFrom Warehouse Place From
   * @param pWarehouseSiteTo Warehouse Place To
   * @param pInvItem InvItem
   * @param pQuantity Quantity
   **/
  public WarehouseEntry(final Long pSourceId, final Integer pSourceType,
    final WarehouseSite pWarehouseSiteFrom,
      final WarehouseSite pWarehouseSiteTo,
        final InvItem pInvItem, final BigDecimal pQuantity) {
    setSourceId(pSourceId);
    setSourceType(pSourceType);
    setWarehouseSiteFrom(pWarehouseSiteFrom);
    setWarehouseSiteTo(pWarehouseSiteTo);
    setInvItem(pInvItem);
    setItsQuantity(pQuantity);
  }

  //Simple getters and setters:
  /**
   * <p>Getter for reversedId.</p>
   * @return Long
   **/
  public final Long getReversedId() {
    return this.reversedId;
  }

  /**
   * <p>Setter for reversedId.</p>
   * @param pReversedId reference
   **/
  public final void setReversedId(final Long pReversedId) {
    this.reversedId = pReversedId;
  }

  /**
   * <p>Geter for sourceId.</p>
   * @return Long
   **/
  public final Long getSourceId() {
    return this.sourceId;
  }

  /**
   * <p>Setter for sourceId.</p>
   * @param pSourceId reference
   **/
  public final void setSourceId(final Long pSourceId) {
    this.sourceId = pSourceId;
  }

  /**
   * <p>Geter for sourceType.</p>
   * @return Integer
   **/
  public final Integer getSourceType() {
    return this.sourceType;
  }

  /**
   * <p>Setter for sourceType.</p>
   * @param pSourceType reference
   **/
  public final void setSourceType(final Integer pSourceType) {
    this.sourceType = pSourceType;
  }

  /**
   * <p>Getter for sourceOwnerId.</p>
   * @return Long
   **/
  public final Long getSourceOwnerId() {
    return this.sourceOwnerId;
  }

  /**
   * <p>Setter for sourceOwnerId.</p>
   * @param pSourceOwnerId reference
   **/
  public final void setSourceOwnerId(final Long pSourceOwnerId) {
    this.sourceOwnerId = pSourceOwnerId;
  }

  /**
   * <p>Getter for sourceOwnerType.</p>
   * @return Integer
   **/
  public final Integer getSourceOwnerType() {
    return this.sourceOwnerType;
  }

  /**
   * <p>Setter for sourceOwnerType.</p>
   * @param pSourceOwnerType reference
   **/
  public final void setSourceOwnerType(final Integer pSourceOwnerType) {
    this.sourceOwnerType = pSourceOwnerType;
  }

  /**
   * <p>Geter for warehouseSiteFrom.</p>
   * @return WarehouseSite
   **/
  public final WarehouseSite getWarehouseSiteFrom() {
    return this.warehouseSiteFrom;
  }

  /**
   * <p>Setter for warehouseSiteFrom.</p>
   * @param pWarehouseSiteFrom reference
   **/
  public final void setWarehouseSiteFrom(
    final WarehouseSite pWarehouseSiteFrom) {
    this.warehouseSiteFrom = pWarehouseSiteFrom;
  }

  /**
   * <p>Geter for warehouseSiteTo.</p>
   * @return WarehouseSite
   **/
  public final WarehouseSite getWarehouseSiteTo() {
    return this.warehouseSiteTo;
  }

  /**
   * <p>Setter for warehouseSiteTo.</p>
   * @param pWarehouseSiteTo reference
   **/
  public final void setWarehouseSiteTo(
    final WarehouseSite pWarehouseSiteTo) {
    this.warehouseSiteTo = pWarehouseSiteTo;
  }

  /**
   * <p>Getter for description.</p>
   * @return String
   **/
  public final String getDescription() {
    return this.description;
  }

  /**
   * <p>Setter for description.</p>
   * @param pDescription reference
   **/
  public final void setDescription(final String pDescription) {
    this.description = pDescription;
  }
}

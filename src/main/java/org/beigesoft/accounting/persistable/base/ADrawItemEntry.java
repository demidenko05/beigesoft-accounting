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

import java.util.Date;
import java.math.BigDecimal;

import org.beigesoft.persistable.APersistableBaseVersion;
import org.beigesoft.accounting.persistable.InvItem;
import org.beigesoft.accounting.persistable.UnitOfMeasure;

/**
 * <pre>
 * Abstract model of withdrawal inventory item for use, sale, loss, stole.
 * Version, reliable autoincrement algorithm.
 * It can't be created by foreign source!
 * </pre>
 *
 * @author Yury Demidenko
 */
public abstract class ADrawItemEntry extends APersistableBaseVersion {

  /**
   * <p>Date of document of accounting entry.</p>
   **/
  private Date itsDate;

  /**
   * <p>ID of reversed/reversing ADrawItemEntry.</p>
   **/
  private Long reversedId;

  /**
   * <p>Source Type e.g. 1001 - PurchaseInvoiceLine, 1004 - Manufacture.
   * This is constant [document/line].constTypeCode().</p>
   **/
  private Integer sourceType;

  /**
   * <p>Document/line ID.</p>
   **/
  private Long sourceId;

  /**
   * <p>Source document ID if exists, e.g. PurchaseInvoice ID.</p>
   **/
  private Long sourceOwnerId;

  /**
   * <p>Source document Type if exists e.g. 1 - PurchaseInvoice.
   **/
  private Integer sourceOwnerType;

  /**
   * <p>drawing document/line source Type code.</p>
   **/
  private Integer drawingType;

  /**
   * <p>Drawing document/line ID, e.g.
   * ADrawItemEntry.InvItem made by base source SalesInvoiceLine.</p>
   **/
  private Long drawingId;

  /**
   * <p>Drawing document ID if exists, e.g. SalesInvoice ID.</p>
   **/
  private Long drawingOwnerId;

  /**
   * <p>Drawing document type if exists, e.g. SalesInvoice - 2.</p>
   **/
  private Integer drawingOwnerType;

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

  /**
   * <p>Cost.</p>
   **/
  private BigDecimal itsCost = BigDecimal.ZERO;

  /**
   * <p>Total.</p>
   **/
  private BigDecimal itsTotal = BigDecimal.ZERO;

  /**
   * <p>Description e.g. Withdrawal 2 each egg for 0.1 from
   * CIL 2 in CI 1 of 11.12.12 by DML 2 in MP 2 of 11.13.12.</p>
   **/
  private String description;

  //Hiding references getters and setters:
  /**
   * <p>Geter for itsDate.</p>
   * @return Date
   **/
  public final Date getItsDate() {
    if (this.itsDate == null) {
      return null;
    }
    return new Date(this.itsDate.getTime());
  }

  /**
   * <p>Setter for itsDate.</p>
   * @param pItsDate reference
   **/
  public final void setItsDate(final Date pItsDate) {
    if (pItsDate == null) {
      this.itsDate = null;
    } else {
      this.itsDate = new Date(pItsDate.getTime());
    }
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
   * <p>Getter for sourceType.</p>
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
   * <p>Getter for sourceId.</p>
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
   * <p>Getter for drawingType.</p>
   * @return Integer
   **/
  public final Integer getDrawingType() {
    return this.drawingType;
  }

  /**
   * <p>Setter for drawingType.</p>
   * @param pDrawingType reference
   **/
  public final void setDrawingType(final Integer pDrawingType) {
    this.drawingType = pDrawingType;
  }

  /**
   * <p>Getter for drawingId.</p>
   * @return Long
   **/
  public final Long getDrawingId() {
    return this.drawingId;
  }

  /**
   * <p>Setter for drawingId.</p>
   * @param pDrawingId reference
   **/
  public final void setDrawingId(final Long pDrawingId) {
    this.drawingId = pDrawingId;
  }

  /**
   * <p>Getter for drawingOwnerId.</p>
   * @return Long
   **/
  public final Long getDrawingOwnerId() {
    return this.drawingOwnerId;
  }

  /**
   * <p>Setter for drawingOwnerId.</p>
   * @param pDrawingOwnerId reference
   **/
  public final void setDrawingOwnerId(final Long pDrawingOwnerId) {
    this.drawingOwnerId = pDrawingOwnerId;
  }

  /**
   * <p>Getter for drawingOwnerType.</p>
   * @return Long
   **/
  public final Integer getDrawingOwnerType() {
    return this.drawingOwnerType;
  }

  /**
   * <p>Setter for drawingOwnerType.</p>
   * @param pDrawingOwnerType reference
   **/
  public final void setDrawingOwnerType(final Integer pDrawingOwnerType) {
    this.drawingOwnerType = pDrawingOwnerType;
  }

  /**
   * <p>Getter for invItem.</p>
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
   * <p>Getter for unitOfMeasure.</p>
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
   * <p>Getter for itsQuantity.</p>
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

  /**
   * <p>Getter for itsCost.</p>
   * @return BigDecimal
   **/
  public final BigDecimal getItsCost() {
    return this.itsCost;
  }

  /**
   * <p>Setter for itsCost.</p>
   * @param pItsCost reference
   **/
  public final void setItsCost(final BigDecimal pItsCost) {
    this.itsCost = pItsCost;
  }

  /**
   * <p>Getter for itsTotal.</p>
   * @return BigDecimal
   **/
  public final BigDecimal getItsTotal() {
    return this.itsTotal;
  }

  /**
   * <p>Setter for itsTotal.</p>
   * @param pItsTotal reference
   **/
  public final void setItsTotal(final BigDecimal pItsTotal) {
    this.itsTotal = pItsTotal;
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

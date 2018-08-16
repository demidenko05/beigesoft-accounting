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
import java.util.Date;

import org.beigesoft.model.IOwned;
import org.beigesoft.persistable.APersistableBaseVersion;

/**
 * <pre>
 * Model of Purchase Return Line for each work type,
 * e.g. cooking or delivery.
 * Version, reliable autoincrement algorithm.
 * </pre>
 *
 * @author Yury Demidenko
 */
public class PurchaseReturnLine extends APersistableBaseVersion
  implements IMakingWarehouseEntry, IOwned<PurchaseReturn> {

  /**
   * <p>Purchase Return.</p>
   **/
  private PurchaseReturn itsOwner;

  /**
   * <p>Purchase Invoice Line.</p>
   **/
  private PurchaseInvoiceLine purchaseInvoiceLine;

  /**
   * <p>Purchase Invoice Line Appearance.</p>
   **/
  private String purchInvLnAppearance;

  /**
   * <p>Warehouse site from (optional).
   * If it's empty (null) then withdrawal will be from the first
   * site/s that has the goods, otherwise withdrawal will be exactly
   * from this site.</p>
   **/
  private WarehouseSite warehouseSiteFo;

  /**
   * <p>ID of reversed/reversing tax line.</p>
   **/
  private Long reversedId;

  /**
   * <p>Less or equals theRest in purchaseInvoiceLine.</p>
   **/
  private BigDecimal itsQuantity = BigDecimal.ZERO;

  /**
   * <p>Total with taxes.</p>
   **/
  private BigDecimal subtotal = new BigDecimal("0.00");

  /**
   * <p>Total taxes.</p>
   **/
  private BigDecimal totalTaxes = new BigDecimal("0.00");

  /**
   * <p>Total.</p>
   **/
  private BigDecimal itsTotal = new BigDecimal("0.00");

  /**
   * <p>Taxes description, uneditable,
   * e.g. "tax1 10%=12, tax2 5%=6".</p>
   **/
  private String taxesDescription;

  /**
   * <p>Description.</p>
   **/
  private String description;

  /**
   * <p>Origin or destination tax category.</p>
   **/
  private InvItemTaxCategory taxCategory;


  /**
   * <p>Setter for unitOfMeasure.</p>
   * @param pUnitOfMeasure reference
   **/
  @Override
  public final void setUnitOfMeasure(final UnitOfMeasure pUnitOfMeasure) {
    throw new RuntimeException(
      "TODO remove setUnitOfMeasure from IMakingWarehouseEntry");
  }

  /**
   * <p>Setter for unitOfMeasure.</p>
   * @param pUnitOfMeasure reference
   **/
  @Override
  public final UnitOfMeasure getUnitOfMeasure() {
    return getPurchaseInvoiceLine().getUnitOfMeasure();
  }

  /**
   * <p>Setter for invItem.</p>
   * @param pInvItem reference
   **/
  @Override
  public final void setInvItem(final InvItem pInvItem) {
    throw new RuntimeException(
      "TODO remove setInvItem from IMakingWarehouseEntry");
  }

  /**
   * <p>Setter for invItem.</p>
   * @param pInvItem reference
   **/
  @Override
  public final InvItem getInvItem() {
    return getPurchaseInvoiceLine().getInvItem();
  }

  /**
   * <p>OOP friendly Constant of code type.</p>
   * @return 1007
   **/
  @Override
  public final Integer constTypeCode() {
    return 1007;
  }

  /**
   * <p>Getter for itsOwner.</p>
   * @return PurchaseReturn
   **/
  @Override
  public final PurchaseReturn getItsOwner() {
    return this.itsOwner;
  }

  /**
   * <p>Setter for itsOwner.</p>
   * @param pItsOwner reference
   **/
  @Override
  public final void setItsOwner(final PurchaseReturn pItsOwner) {
    this.itsOwner = pItsOwner;
  }

  /**
   * <p>Geter for reversedId.</p>
   * @return Long
   **/
  @Override
  public final Long getReversedId() {
    return this.reversedId;
  }

  /**
   * <p>Setter for reversedId.</p>
   * @param pReversedId reference
   **/
  @Override
  public final void setReversedId(final Long pReversedId) {
    this.reversedId = pReversedId;
  }

  /**
   * <p>Get for document Date.</p>
   * @return Date
   **/
  @Override
  public final Date getDocumentDate() {
    return this.getItsOwner().getItsDate();
  }

  /**
   * <p>Get Owner Type if exist  e.g. PurchaseInvoice 1.</p>
   * @return Integer
   **/
  @Override
  public final Integer getOwnerType() {
    return this.getItsOwner().constTypeCode();
  }

  /**
   * <p>Get for owner's ID.</p>
   * @return Long
   **/
  @Override
  public final Long getOwnerId() {
    return this.getItsOwner().getItsId();
  }

  /**
   * <p>Getter for description.</p>
   * @return String
   **/
  @Override
  public final String getDescription() {
    return this.description;
  }

  /**
   * <p>Setter for description.</p>
   * @param pDescription reference
   **/
  @Override
  public final void setDescription(final String pDescription) {
    this.description = pDescription;
  }

  //Simple getters and setters:
  /**
   * <p>Getter for purchaseInvoiceLine.</p>
   * @return PurchaseInvoiceLine
   **/
  public final PurchaseInvoiceLine getPurchaseInvoiceLine() {
    return this.purchaseInvoiceLine;
  }

  /**
   * <p>Setter for purchaseInvoiceLine.</p>
   * @param pPurchaseInvoiceLine reference
   **/
  public final void setPurchaseInvoiceLine(
    final PurchaseInvoiceLine pPurchaseInvoiceLine) {
    this.purchaseInvoiceLine = pPurchaseInvoiceLine;
  }

  /**
   * <p>Getter for purchInvLnAppearance.</p>
   * @return String
   **/
  public final String getPurchInvLnAppearance() {
    return this.purchInvLnAppearance;
  }

  /**
   * <p>Setter for purchInvLnAppearance.</p>
   * @param pPurchInvLnAppearance reference
   **/
  public final void setPurchInvLnAppearance(
    final String pPurchInvLnAppearance) {
    this.purchInvLnAppearance = pPurchInvLnAppearance;
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
   * <p>Getter for subtotal.</p>
   * @return BigDecimal
   **/
  public final BigDecimal getSubtotal() {
    return this.subtotal;
  }

  /**
   * <p>Setter for subtotal.</p>
   * @param pSubtotal reference
   **/
  public final void setSubtotal(final BigDecimal pSubtotal) {
    this.subtotal = pSubtotal;
  }

  /**
   * <p>Getter for totalTaxes.</p>
   * @return BigDecimal
   **/
  public final BigDecimal getTotalTaxes() {
    return this.totalTaxes;
  }

  /**
   * <p>Setter for totalTaxes.</p>
   * @param pTotalTaxes reference
   **/
  public final void setTotalTaxes(final BigDecimal pTotalTaxes) {
    this.totalTaxes = pTotalTaxes;
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
   * <p>Getter for taxesDescription.</p>
   * @return String
   **/
  public final String getTaxesDescription() {
    return this.taxesDescription;
  }

  /**
   * <p>Setter for taxesDescription.</p>
   * @param pTaxesDescription reference
   **/
  public final void setTaxesDescription(final String pTaxesDescription) {
    this.taxesDescription = pTaxesDescription;
  }

  /**
   * <p>Getter for warehouseSiteFo.</p>
   * @return WarehouseSite
   **/
  public final WarehouseSite getWarehouseSiteFo() {
    return this.warehouseSiteFo;
  }

  /**
   * <p>Setter for warehouseSiteFo.</p>
   * @param pWarehouseSiteFo reference
   **/
  public final void setWarehouseSiteFo(final WarehouseSite pWarehouseSiteFo) {
    this.warehouseSiteFo = pWarehouseSiteFo;
  }

  /**
   * <p>Getter for taxCategory.</p>
   * @return InvItemTaxCategory
   **/
  public final InvItemTaxCategory getTaxCategory() {
    return this.taxCategory;
  }

  /**
   * <p>Setter for taxCategory.</p>
   * @param pTaxCategory reference
   **/
  public final void setTaxCategory(final InvItemTaxCategory pTaxCategory) {
    this.taxCategory = pTaxCategory;
  }
}

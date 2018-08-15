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
import org.beigesoft.persistable.APersistableBaseVersion;

/**
 * <p>
 * Model of Vendor Invoice ServicePurchased Line.
 * It is immutable.
 * Version, reliable autoincrement algorithm.
 * </p>
 *
 * @author Yury Demidenko
 */
public class PurchaseInvoiceServiceLine extends APersistableBaseVersion
  implements IOwned<PurchaseInvoice> {

  /**
   * <p>ID of reversed/reversing line.</p>
   **/
  private Long reversedId;

  /**
   * <p>ServicePurchased.</p>
   **/
  private ServicePurchased service;

  /**
   * <p>Account service expense, e.g. Expenses or InventoryCapitalizedCost,
   * Not Null.</p>
   **/
  private Account accExpense;

  /**
   * <p>Vendor Invoice.</p>
   **/
  private PurchaseInvoice itsOwner;

  /**
   * <p>Cost.</p>
   **/
  private BigDecimal itsCost = BigDecimal.ZERO;

  /**
   * <p>Total taxes.</p>
   **/
  private BigDecimal totalTaxes = BigDecimal.ZERO;

  /**
   * <p>Taxes description, uneditable,
   * e.g. "tax1 10%=12, tax2 5%=6".</p>
   **/
  private String taxesDescription;

  /**
   * <p>Total.</p>
   **/
  private BigDecimal itsTotal = BigDecimal.ZERO;

  /**
   * <p>Description.</p>
   **/
  private String description;

  /**
   * <p>Unit Of Measure.</p>
   **/
  private UnitOfMeasure unitOfMeasure;

  /**
   * <p>Quantity.</p>
   **/
  private BigDecimal itsQuantity = BigDecimal.ZERO;

  /**
   * <p>Subtotal without taxes.</p>
   **/
  private BigDecimal subtotal = BigDecimal.ZERO;

  /**
   * <p>Price in foreign currency, if used.</p>
   **/
  private BigDecimal foreignPrice = BigDecimal.ZERO;

  /**
   * <p>Subtotal in foreign currency, if used.</p>
   **/
  private BigDecimal foreignSubtotal = BigDecimal.ZERO;

  /**
   * <p>Total taxes in foreign currency, if used,
   * in case of domestic sales (if law allow it).</p>
   **/
  private BigDecimal foreignTotalTaxes = BigDecimal.ZERO;

  /**
   * <p>Total in foreign currency, if used.</p>
   **/
  private BigDecimal foreignTotal = BigDecimal.ZERO;

  /**
   * <p>Origin or destination tax category.</p>
   **/
  private InvItemTaxCategory taxCategory;

  /**
   * <p>Geter for itsOwner.</p>
   * @return PurchaseInvoice
   **/
  @Override
  public final PurchaseInvoice getItsOwner() {
    return this.itsOwner;
  }

  /**
   * <p>Setter for itsOwner.</p>
   * @param pItsOwner reference
   **/
  @Override
  public final void setItsOwner(final PurchaseInvoice pItsOwner) {
    this.itsOwner = pItsOwner;
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
   * <p>Getter for service.</p>
   * @return ServicePurchased
   **/
  public final ServicePurchased getService() {
    return this.service;
  }

  /**
   * <p>Setter for service.</p>
   * @param pService reference
   **/
  public final void setService(final ServicePurchased pService) {
    this.service = pService;
  }

  /**
   * <p>Getter for accExpense.</p>
   * @return Account
   **/
  public final Account getAccExpense() {
    return this.accExpense;
  }

  /**
   * <p>Setter for accExpense.</p>
   * @param pAccExpense reference
   **/
  public final void setAccExpense(final Account pAccExpense) {
    this.accExpense = pAccExpense;
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

  /**
   * <p>Geter for subtotal.</p>
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
   * <p>Getter for foreignPrice.</p>
   * @return BigDecimal
   **/
  public final BigDecimal getForeignPrice() {
    return this.foreignPrice;
  }

  /**
   * <p>Setter for foreignPrice.</p>
   * @param pForeignPrice reference
   **/
  public final void setForeignPrice(final BigDecimal pForeignPrice) {
    this.foreignPrice = pForeignPrice;
  }

  /**
   * <p>Getter for foreignSubtotal.</p>
   * @return BigDecimal
   **/
  public final BigDecimal getForeignSubtotal() {
    return this.foreignSubtotal;
  }

  /**
   * <p>Setter for foreignSubtotal.</p>
   * @param pForeignSubtotal reference
   **/
  public final void setForeignSubtotal(final BigDecimal pForeignSubtotal) {
    this.foreignSubtotal = pForeignSubtotal;
  }

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

  /**
   * <p>Getter for foreignTotal.</p>
   * @return BigDecimal
   **/
  public final BigDecimal getForeignTotal() {
    return this.foreignTotal;
  }

  /**
   * <p>Setter for foreignTotal.</p>
   * @param pForeignTotal reference
   **/
  public final void setForeignTotal(final BigDecimal pForeignTotal) {
    this.foreignTotal = pForeignTotal;
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

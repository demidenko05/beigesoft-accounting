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

import org.beigesoft.model.IOwned;
import org.beigesoft.persistable.APersistableBaseVersion;

/**
 * <pre>
 * Model of Vendor Invoice ServiceToSale Line.
 * It is immutable.
 * Version, reliable autoincrement algorithm.
 * </pre>
 *
 * @author Yury Demidenko
 */
public class SalesInvoiceServiceLine extends APersistableBaseVersion
  implements IOwned<SalesInvoice> {

  /**
   * <p>ID of reversed/reversing line.</p>
   **/
  private Long reversedId;

  /**
   * <p>ServiceToSale.</p>
   **/
  private ServiceToSale service;

  /**
   * <p>Vendor Invoice.</p>
   **/
  private SalesInvoice itsOwner;

  /**
   * <p>Price.</p>
   **/
  private BigDecimal itsPrice = BigDecimal.ZERO;

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
   * @return ServiceToSale
   **/
  public final ServiceToSale getService() {
    return this.service;
  }

  /**
   * <p>Setter for service.</p>
   * @param pService reference
   **/
  public final void setService(final ServiceToSale pService) {
    this.service = pService;
  }

  /**
   * <p>Getter for itsPrice.</p>
   * @return BigDecimal
   **/
  public final BigDecimal getItsPrice() {
    return this.itsPrice;
  }

  /**
   * <p>Setter for itsPrice.</p>
   * @param pItsPrice reference
   **/
  public final void setItsPrice(final BigDecimal pItsPrice) {
    this.itsPrice = pItsPrice;
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
}

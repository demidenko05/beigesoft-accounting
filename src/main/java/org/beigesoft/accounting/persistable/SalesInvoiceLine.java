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

import java.util.Date;
import java.math.BigDecimal;

import org.beigesoft.model.IOwned;
import org.beigesoft.accounting.persistable.base.AInvItemMovementPriceTax;

/**
 * <pre>
 * Model of Customer Invoice Line.
 * It is immutable.
 * </pre>
 *
 * @author Yury Demidenko
 */
public class SalesInvoiceLine extends AInvItemMovementPriceTax
  implements IMakingWarehouseEntry, IOwned<SalesInvoice> {

  /**
   * <p>Customer Invoice.</p>
   **/
  private SalesInvoice itsOwner;

  /**
   * <p>Warehouse site from (optional).
   * If it's empty (null) then withdrawal will be from the first
   * site/s that has the goods, otherwise withdrawal will be exactly
   * from this site.</p>
   **/
  private WarehouseSite warehouseSiteFo;

  /**
   * <p>Reversed line ID (if this reverse it).</p>
   **/
  private Long reversedId;

  /**
   * <p>Description.</p>
   **/
  private String description;

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
   * <p>Constant of code type.</p>
   * @return 1002
   **/
  @Override
  public final Integer constTypeCode() {
    return 1002;
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
}

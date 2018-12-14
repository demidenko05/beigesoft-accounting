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

import java.util.List;
import java.math.BigDecimal;

import org.beigesoft.accounting.persistable.base.ADocWithTaxes;
import org.beigesoft.accounting.model.EWarehouseMovementType;

/**
 * <p>
 * Model of Purchase Return.
 * </p>
 *
 * @author Yury Demidenko
 */
public class PurchaseReturn extends ADocWithTaxes
  implements IInvoice {

  /**
   * <p>Purchase Invoice.</p>
   **/
  private PurchaseInvoice purchaseInvoice;

  /**
   * <p>Lines.</p>
   **/
  private List<PurchaseReturnLine> itsLines;

  /**
   * <p>Taxes lines.</p>
   **/
  private List<PurchaseReturnTaxLine> taxesLines;

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
   * <p>OOP friendly Constant of code type.</p>
   * @return 13
   **/
  @Override
  public final Integer constTypeCode() {
    return 13;
  }

  /**
   * <p>If owned lines make warehouse entries this return
   * their type.</p>
   * @return Boolean
   **/
  @Override
  public final EWarehouseMovementType getLinesWarehouseType() {
    return EWarehouseMovementType.WITHDRAWAL;
  }

  /**
   * <p>Getter for foreignCurrency.</p>
   * @return Currency
   **/
  @Override
  public final Currency getForeignCurrency() {
    return this.purchaseInvoice.getForeignCurrency();
  }

  /**
   * <p>Setter for foreignCurrency.</p>
   * @param pForeignCurrency reference
   **/
  @Override
  public final void setForeignCurrency(final Currency pForeignCurrency) {
    throw new RuntimeException("UNEDITABLE");
  }

  /**
   * <p>Getter for exchangeRate.</p>
   * @return BigDecimal
   **/
  @Override
  public final BigDecimal getExchangeRate() {
    return this.purchaseInvoice.getExchangeRate();
  }

  /**
   * <p>Setter for exchangeRate.</p>
   * @param pExchangeRate reference
   **/
  @Override
  public final void setExchangeRate(final BigDecimal pExchangeRate) {
    throw new RuntimeException("UNEDITABLE");
  }

  /**
   * <p>Getter for foreignSubtotal.</p>
   * @return BigDecimal
   **/
  @Override
  public final BigDecimal getForeignSubtotal() {
    return this.foreignSubtotal;
  }

  /**
   * <p>Setter for foreignSubtotal.</p>
   * @param pForeignSubtotal reference
   **/
  @Override
  public final void setForeignSubtotal(final BigDecimal pForeignSubtotal) {
    this.foreignSubtotal = pForeignSubtotal;
  }

  /**
   * <p>Getter for foreignTotalTaxes.</p>
   * @return BigDecimal
   **/
  @Override
  public final BigDecimal getForeignTotalTaxes() {
    return this.foreignTotalTaxes;
  }

  /**
   * <p>Setter for foreignTotalTaxes.</p>
   * @param pForeignTotalTaxes reference
   **/
  @Override
  public final void setForeignTotalTaxes(final BigDecimal pForeignTotalTaxes) {
    this.foreignTotalTaxes = pForeignTotalTaxes;
  }

  /**
   * <p>Getter for foreignTotal.</p>
   * @return BigDecimal
   **/
  @Override
  public final BigDecimal getForeignTotal() {
    return this.foreignTotal;
  }

  /**
   * <p>Setter for foreignTotal.</p>
   * @param pForeignTotal reference
   **/
  @Override
  public final void setForeignTotal(final BigDecimal pForeignTotal) {
    this.foreignTotal = pForeignTotal;
  }

  /**
   * <p>Getter for priceIncTax.</p>
   * @return Boolean
   **/
  @Override
  public final Boolean getPriceIncTax() {
    return this.purchaseInvoice.getPriceIncTax();
  }

  /**
   * <p>Setter for priceIncTax.</p>
   * @param pPriceIncTax reference
   **/
  @Override
  public final void setPriceIncTax(final Boolean pPriceIncTax) {
    throw new RuntimeException("UNEDITABLE");
  }

  /**
   * <p>Getter for omitTaxes.</p>
   * @return Boolean
   **/
  @Override
  public final Boolean getOmitTaxes() {
    return this.purchaseInvoice.getOmitTaxes();
  }

  /**
   * <p>Setter for omitTaxes.</p>
   * @param pOmitTaxes reference
   **/
  @Override
  public final void setOmitTaxes(final Boolean pOmitTaxes) {
    throw new RuntimeException("UNEDITABLE");
  }
  /**
   * <p>Geter for vendor.</p>
   * @return DebtorCreditor
   **/
  @Override
  public final DebtorCreditor getCustomer() {
    return this.purchaseInvoice.getVendor();
  }

  //Simple getters and setters:
  /**
   * <p>Getter for purchaseInvoice.</p>
   * @return PurchaseInvoice
   **/
  public final PurchaseInvoice getPurchaseInvoice() {
    return this.purchaseInvoice;
  }

  /**
   * <p>Setter for purchaseInvoice.</p>
   * @param pPurchaseInvoice reference
   **/
  public final void setPurchaseInvoice(final PurchaseInvoice pPurchaseInvoice) {
    this.purchaseInvoice = pPurchaseInvoice;
  }

  /**
   * <p>Getter for itsLines.</p>
   * @return List<PurchaseReturnLine>
   **/
  public final List<PurchaseReturnLine> getItsLines() {
    return this.itsLines;
  }

  /**
   * <p>Setter for itsLines.</p>
   * @param pItsLines reference
   **/
  public final void setItsLines(final List<PurchaseReturnLine> pItsLines) {
    this.itsLines = pItsLines;
  }

  /**
   * <p>Getter for taxesLines.</p>
   * @return List<PurchaseReturnTaxLine>
   **/
  public final List<PurchaseReturnTaxLine> getTaxesLines() {
    return this.taxesLines;
  }

  /**
   * <p>Setter for taxesLines.</p>
   * @param pTaxesLines reference
   **/
  public final void setTaxesLines(
    final List<PurchaseReturnTaxLine> pTaxesLines) {
    this.taxesLines = pTaxesLines;
  }
}

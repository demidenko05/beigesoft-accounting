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

import org.beigesoft.accounting.persistable.base.ADocWithTaxesPayments;
import org.beigesoft.accounting.model.EWarehouseMovementType;

/**
 * <p>
 * Model of Vendor Invoice.
 * </p>
 *
 * @author Yury Demidenko
 */
public class PurchaseInvoice extends ADocWithTaxesPayments implements IInvoice {

  /**
   * <p>There is no goods in stock.</p>
   **/
  public static final int THERE_IS_NO_GOODS = 1301;

  /**
   * <p>There is withdrawals from this source!
   * It arises when theRest != quantity for non-reversed item source</p>
   **/
  public static final int SOURSE_IS_IN_USE = 1303;

  /**
   * <p>Vendor.</p>
   **/
  private DebtorCreditor vendor;

  /**
   * <p>Prepayment.</p>
   **/
  private PrepaymentTo prepaymentTo;

  /**
   * <p>Lines.</p>
   **/
  private List<PurchaseInvoiceLine> itsLines;

  /**
   * <p>Services.</p>
   **/
  private List<PurchaseInvoiceServiceLine> services;

  /**
   * <p>Taxes lines.</p>
   **/
  private List<PurchaseInvoiceTaxLine> taxesLines;

  /**
   * <p>Foreign currency, if used.</p>
   **/
  private Currency foreignCurrency;

  /**
   * <p>Currency current exchange rate, if used.</p>
   **/
  private BigDecimal exchangeRate = BigDecimal.ZERO;

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
   * <p>Price inclusive of taxes, false default.</p>
   **/
  private Boolean priceIncTax = Boolean.FALSE;

  /**
   * <p>Is omitting taxes, false default.</p>
   **/
  private Boolean omitTaxes = Boolean.FALSE;

  /**
   * <p>OOP friendly Constant of code type 1.</p>
   **/
  @Override
  public final Integer constTypeCode() {
    return 1;
  }

  /**
   * <p>If owned lines make warehouse entries this return
   * their type.</p>
   * @return Boolean
   **/
  @Override
  public final EWarehouseMovementType getLinesWarehouseType() {
    return EWarehouseMovementType.LOAD;
  }

  /**
   * <p>Getter for foreignCurrency.</p>
   * @return Currency
   **/
  @Override
  public final Currency getForeignCurrency() {
    return this.foreignCurrency;
  }

  /**
   * <p>Setter for foreignCurrency.</p>
   * @param pForeignCurrency reference
   **/
  @Override
  public final void setForeignCurrency(final Currency pForeignCurrency) {
    this.foreignCurrency = pForeignCurrency;
  }

  /**
   * <p>Getter for exchangeRate.</p>
   * @return BigDecimal
   **/
  @Override
  public final BigDecimal getExchangeRate() {
    return this.exchangeRate;
  }

  /**
   * <p>Setter for exchangeRate.</p>
   * @param pExchangeRate reference
   **/
  @Override
  public final void setExchangeRate(final BigDecimal pExchangeRate) {
    this.exchangeRate = pExchangeRate;
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
    return this.priceIncTax;
  }

  /**
   * <p>Setter for priceIncTax.</p>
   * @param pPriceIncTax reference
   **/
  @Override
  public final void setPriceIncTax(final Boolean pPriceIncTax) {
    this.priceIncTax = pPriceIncTax;
  }

  /**
   * <p>Getter for omitTaxes.</p>
   * @return Boolean
   **/
  @Override
  public final Boolean getOmitTaxes() {
    return this.omitTaxes;
  }

  /**
   * <p>Setter for omitTaxes.</p>
   * @param pOmitTaxes reference
   **/
  @Override
  public final void setOmitTaxes(final Boolean pOmitTaxes) {
    this.omitTaxes = pOmitTaxes;
  }

  //Simple getters and setters:
  /**
   * <p>Geter for vendor.</p>
   * @return DebtorCreditor
   **/
  public final DebtorCreditor getVendor() {
    return this.vendor;
  }

  /**
   * <p>Setter for vendor.</p>
   * @param pVendor reference
   **/
  public final void setVendor(final DebtorCreditor pVendor) {
    this.vendor = pVendor;
  }

  /**
   * <p>Getter for prepaymentTo.</p>
   * @return PrepaymentTo
   **/
  public final PrepaymentTo getPrepaymentTo() {
    return this.prepaymentTo;
  }

  /**
   * <p>Setter for prepaymentTo.</p>
   * @param pPrepaymentTo reference
   **/
  public final void setPrepaymentTo(final PrepaymentTo pPrepaymentTo) {
    this.prepaymentTo = pPrepaymentTo;
  }

  /**
   * <p>Geter for itsLines.</p>
   * @return List<PurchaseInvoiceLine>
   **/
  public final List<PurchaseInvoiceLine> getItsLines() {
    return this.itsLines;
  }

  /**
   * <p>Setter for itsLines.</p>
   * @param pItsLines reference
   **/
  public final void setItsLines(final List<PurchaseInvoiceLine> pItsLines) {
    this.itsLines = pItsLines;
  }

  /**
   * <p>Getter for services.</p>
   * @return List<PurchaseInvoiceServiceLine>
   **/
  public final List<PurchaseInvoiceServiceLine> getServices() {
    return this.services;
  }

  /**
   * <p>Setter for services.</p>
   * @param pServices reference
   **/
  public final void setServices(
    final List<PurchaseInvoiceServiceLine> pServices) {
    this.services = pServices;
  }

  /**
   * <p>Geter for taxesLines.</p>
   * @return List<PurchaseInvoiceTaxLine>
   **/
  public final List<PurchaseInvoiceTaxLine> getTaxesLines() {
    return this.taxesLines;
  }

  /**
   * <p>Setter for taxesLines.</p>
   * @param pTaxesLines reference
   **/
  public final void setTaxesLines(
    final List<PurchaseInvoiceTaxLine> pTaxesLines) {
    this.taxesLines = pTaxesLines;
  }
}

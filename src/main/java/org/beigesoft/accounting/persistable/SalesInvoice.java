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
 * Model of Customer Invoice.
 * </p>
 *
 * @author Yury Demidenko
 */
public class SalesInvoice extends ADocWithTaxesPayments implements IInvoice {

  /**
   * <p>Customer.</p>
   **/
  private DebtorCreditor customer;

  /**
   * <p>Lines.</p>
   **/
  private List<SalesInvoiceLine> itsLines;

  /**
   * <p>Services to sell.</p>
   **/
  private List<SalesInvoiceServiceLine> services;

  /**
   * <p>Taxes lines.</p>
   **/
  private List<SalesInvoiceTaxLine> taxesLines;

  /**
   * <p>Prepayment.</p>
   **/
  private PrepaymentFrom prepaymentFrom;

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
   * <p>OOP friendly Constant of code type 2.</p>
   **/
  @Override
  public final Integer constTypeCode() {
    return 2;
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
   * <p>Geter for customer.</p>
   * @return DebtorCreditor
   **/
  public final DebtorCreditor getCustomer() {
    return this.customer;
  }

  /**
   * <p>Setter for customer.</p>
   * @param pCustomer reference
   **/
  public final void setCustomer(final DebtorCreditor pCustomer) {
    this.customer = pCustomer;
  }

  /**
   * <p>Geter for itsLines.</p>
   * @return List<SalesInvoiceLine>
   **/
  public final List<SalesInvoiceLine> getItsLines() {
    return this.itsLines;
  }

  /**
   * <p>Setter for itsLines.</p>
   * @param pItsLines reference
   **/
  public final void setItsLines(final List<SalesInvoiceLine> pItsLines) {
    this.itsLines = pItsLines;
  }

  /**
   * <p>Getter for services.</p>
   * @return List<SalesInvoiceServiceLine>
   **/
  public final List<SalesInvoiceServiceLine> getServices() {
    return this.services;
  }

  /**
   * <p>Setter for services.</p>
   * @param pServices reference
   **/
  public final void setServices(
    final List<SalesInvoiceServiceLine> pServices) {
    this.services = pServices;
  }

  /**
   * <p>Geter for taxesLines.</p>
   * @return List<SalesInvoiceTaxLine>
   **/
  public final List<SalesInvoiceTaxLine> getTaxesLines() {
    return this.taxesLines;
  }

  /**
   * <p>Setter for taxesLines.</p>
   * @param pTaxesLines reference
   **/
  public final void setTaxesLines(
    final List<SalesInvoiceTaxLine> pTaxesLines) {
    this.taxesLines = pTaxesLines;
  }

  /**
   * <p>Getter for prepaymentFrom.</p>
   * @return PrepaymentFrom
   **/
  public final PrepaymentFrom getPrepaymentFrom() {
    return this.prepaymentFrom;
  }

  /**
   * <p>Setter for prepaymentFrom.</p>
   * @param pPrepaymentFrom reference
   **/
  public final void setPrepaymentFrom(final PrepaymentFrom pPrepaymentFrom) {
    this.prepaymentFrom = pPrepaymentFrom;
  }
}

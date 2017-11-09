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

import java.util.List;

import org.beigesoft.accounting.persistable.base.ADocWithTaxesPayments;
import org.beigesoft.accounting.model.EWarehouseMovementType;

/**
 * <pre>
 * Model of Customer Invoice.
 * </pre>
 *
 * @author Yury Demidenko
 */
public class SalesInvoice extends ADocWithTaxesPayments
  implements IDocWarehouse {

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

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
 * Model of Vendor Invoice.
 * </pre>
 *
 * @author Yury Demidenko
 */
public class PurchaseInvoice extends ADocWithTaxesPayments
  implements IDocWarehouse {

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

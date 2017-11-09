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

import org.beigesoft.accounting.persistable.base.ADoc;

/**
 * <pre>
 * Model of payments after purchase.
 * This document is used to track payments for purchase and can be used
 * for a sales tax deducting logic. You are free to use simple
 * accounting entries instead.
 * </pre>
 *
 * @author Yury Demidenko
 */
public class PaymentTo extends ADoc {

  /**
   * <p>Purchase Invoice, not null.</p>
   **/
  private PurchaseInvoice purchaseInvoice;

  /**
   * <p>Account cash, not null.</p>
   **/
  private Account accCash;

  /**
   * <p>Subccount cash type if exist.</p>
   **/
  private Integer subaccCashType;

  /**
   * <p>Subccount cash ID if exist.</p>
   **/
  private Long subaccCashId;

  /**
   * <p>Subccount cash appearance if exist.</p>
   **/
  private String subaccCash;

  /**
   * <p>OOP friendly Constant of code type 8.</p>
   **/
  @Override
  public final Integer constTypeCode() {
    return 8;
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
  public final void setPurchaseInvoice(
    final PurchaseInvoice pPurchaseInvoice) {
    this.purchaseInvoice = pPurchaseInvoice;
  }

  /**
   * <p>Getter for accCash.</p>
   * @return Account
   **/
  public final Account getAccCash() {
    return this.accCash;
  }

  /**
   * <p>Setter for accCash.</p>
   * @param pAccCash reference
   **/
  public final void setAccCash(final Account pAccCash) {
    this.accCash = pAccCash;
  }

  /**
   * <p>Getter for subaccCashType.</p>
   * @return Integer
   **/
  public final Integer getSubaccCashType() {
    return this.subaccCashType;
  }

  /**
   * <p>Setter for subaccCashType.</p>
   * @param pSubaccCashType reference
   **/
  public final void setSubaccCashType(final Integer pSubaccCashType) {
    this.subaccCashType = pSubaccCashType;
  }

  /**
   * <p>Getter for subaccCashId.</p>
   * @return Long
   **/
  public final Long getSubaccCashId() {
    return this.subaccCashId;
  }

  /**
   * <p>Setter for subaccCashId.</p>
   * @param pSubaccCashId reference
   **/
  public final void setSubaccCashId(final Long pSubaccCashId) {
    this.subaccCashId = pSubaccCashId;
  }

  /**
   * <p>Getter for subaccCash.</p>
   * @return String
   **/
  public final String getSubaccCash() {
    return this.subaccCash;
  }

  /**
   * <p>Setter for subaccCash.</p>
   * @param pSubaccCash reference
   **/
  public final void setSubaccCash(final String pSubaccCash) {
    this.subaccCash = pSubaccCash;
  }
}

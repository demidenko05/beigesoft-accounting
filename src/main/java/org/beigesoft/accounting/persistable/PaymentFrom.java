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

import org.beigesoft.accounting.persistable.base.ADoc;

/**
 * <p>
 * Model of payments for sales.
 * This document is used to track payments for sales.
 * You are free to use simple accounting entries instead.
 * </p>
 *
 * @author Yury Demidenko
 */
public class PaymentFrom extends ADoc {

  /**
   * <p>Purchase Invoice, not null.</p>
   **/
  private SalesInvoice salesInvoice;

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
   * <p>Total in foreign currency, if used.</p>
   **/
  private BigDecimal foreignTotal = BigDecimal.ZERO;

  /**
   * <p>OOP friendly Constant of code type 10.</p>
   **/
  @Override
  public final Integer constTypeCode() {
    return 10;
  }

  //Simple getters and setters:
  /**
   * <p>Getter for salesInvoice.</p>
   * @return SalesInvoice
   **/
  public final SalesInvoice getSalesInvoice() {
    return this.salesInvoice;
  }

  /**
   * <p>Setter for salesInvoice.</p>
   * @param pSalesInvoice reference
   **/
  public final void setSalesInvoice(
    final SalesInvoice pSalesInvoice) {
    this.salesInvoice = pSalesInvoice;
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

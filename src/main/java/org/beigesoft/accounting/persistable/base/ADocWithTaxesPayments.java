package org.beigesoft.accounting.persistable.base;

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

import java.util.Date;
import java.math.BigDecimal;

/**
 * <pre>
 * Abstraction of a document with taxes and payments/prepayments.
 * </pre>
 *
 * @author Yury Demidenko
 */
public abstract class ADocWithTaxesPayments extends ADocWithTaxes {

  /**
   * <p>Payment total (prepayment and afterpayment).</p>
   **/
  private BigDecimal paymentTotal = BigDecimal.ZERO;

  /**
   * <p>Payment description, read only.</p>
   **/
  private String paymentDescription;

  /**
   * <p>Payment done by date, if required.</p>
   **/
  private Date payByDate;

  //Hiding references getters and setters:
  /**
   * <p>Getter for payByDate.</p>
   * @return Date
   **/
  public final Date getPayByDate() {
    if (this.payByDate == null) {
      return null;
    }
    return new Date(this.payByDate.getTime());
  }

  /**
   * <p>Setter for payByDate.</p>
   * @param pPayByDate reference
   **/
  public final void setPayByDate(final Date pPayByDate) {
    if (pPayByDate == null) {
      this.payByDate = null;
    } else {
      this.payByDate = new Date(pPayByDate.getTime());
    }
  }

  //Simple getters and setters:
  /**
   * <p>Getter for paymentTotal.</p>
   * @return BigDecimal
   **/
  public final BigDecimal getPaymentTotal() {
    return this.paymentTotal;
  }

  /**
   * <p>Setter for paymentTotal.</p>
   * @param pPaymentTotal reference
   **/
  public final void setPaymentTotal(final BigDecimal pPaymentTotal) {
    this.paymentTotal = pPaymentTotal;
  }

  /**
   * <p>Getter for paymentDescription.</p>
   * @return String
   **/
  public final String getPaymentDescription() {
    return this.paymentDescription;
  }

  /**
   * <p>Setter for paymentDescription.</p>
   * @param pPaymentDescription reference
   **/
  public final void setPaymentDescription(final String pPaymentDescription) {
    this.paymentDescription = pPaymentDescription;
  }
}

package org.beigesoft.accounting.processor;

/*
 * Copyright (c) 2018 Beigesoft™
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

import org.beigesoft.accounting.persistable.Tax;
import org.beigesoft.accounting.persistable.SalesInvoiceServiceLine;

/**
 * <p>Bundle of retrieved from database tax data.</p>
 *
 * @author Yury Demidenko
 */
public class DataTx {

  /**
   * <p>Data storage for aggregate rate
   * and non-aggregate invoice basis taxes included.</p>
   **/
  private List<SalesInvoiceServiceLine> txdLns;

  /**
   * <p>Taxes data storage for non-aggregate rate
   * except invoice basis with included taxes.
   * </p>
   **/
  private List<Tax> txs;

  /**
   * <p>Tax's totals/taxables data storage for non-aggregate rate
   * except invoice basis with included taxes.
   * </p>
   **/
  private List<Double> txTotTaxb;

  /**
   * <p>Tax's percents for invoice basis data storage for non-aggregate rate
   * except invoice basis with included taxes.
   * </p>
   **/
  private List<Double> txPerc;

  //Simple getters and setters:

  /**
   * <p>Getter for txdLns.</p>
   * @return List<SalesInvoiceServiceLine>
   **/
  public final List<SalesInvoiceServiceLine> getTxdLns() {
    return this.txdLns;
  }

  /**
   * <p>Setter for txdLns.</p>
   * @param pTxdLns reference
   **/
  public final void setTxdLns(final List<SalesInvoiceServiceLine> pTxdLns) {
    this.txdLns = pTxdLns;
  }

  /**
   * <p>Getter for txs.</p>
   * @return List<Tax>
   **/
  public final List<Tax> getTxs() {
    return this.txs;
  }

  /**
   * <p>Setter for txs.</p>
   * @param pTxs reference
   **/
  public final void setTxs(final List<Tax> pTxs) {
    this.txs = pTxs;
  }

  /**
   * <p>Getter for txTotTaxb.</p>
   * @return List<Double>
   **/
  public final List<Double> getTxTotTaxb() {
    return this.txTotTaxb;
  }

  /**
   * <p>Setter for txTotTaxb.</p>
   * @param pTxTotTaxb reference
   **/
  public final void setTxTotTaxb(final List<Double> pTxTotTaxb) {
    this.txTotTaxb = pTxTotTaxb;
  }

  /**
   * <p>Getter for txPerc.</p>
   * @return List<Double>
   **/
  public final List<Double> getTxPerc() {
    return this.txPerc;
  }

  /**
   * <p>Setter for txPerc.</p>
   * @param pTxPerc reference
   **/
  public final void setTxPerc(final List<Double> pTxPerc) {
    this.txPerc = pTxPerc;
  }
}

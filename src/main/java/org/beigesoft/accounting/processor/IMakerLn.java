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

import java.util.Map;
import java.io.IOException;

import org.beigesoft.accounting.persistable.base.ADestTaxItemLn;
import org.beigesoft.accounting.persistable.IInvoice;
import org.beigesoft.accounting.persistable.IInvoiceLine;
import org.beigesoft.accounting.persistable.AccSettings;
import org.beigesoft.accounting.persistable.TaxDestination;

/**
 * <p>Abstraction of invoice line taxes item basis non-aggregate maker, and
 * holder of item's destination tax line class,
 * and other relative to invoice type information.</p>
 *
 * @param <T> invoice type
 * @param <L> invoice line type
 * @author Yury Demidenko
 */
public interface IMakerLn<T extends IInvoice, L extends IInvoiceLine<T>> {

  /**
   * <p>Makes invoice line taxes item basis basis non-aggregate.</p>
   * @param pReqVars request scoped vars
   * @param pLine invoice line
   * @param pAs Accounting Settings
   * @param pTxRules taxable rules
   * @throws Exception - an exception.
   **/
  void mkLnTxItBasNonAggr(Map<String, Object> pReqVars,
    L pLine, AccSettings pAs, TaxDestination pTxRules) throws Exception;

  /**
   * <p>Getter for item's destination tax line class.</p>
   * @return Class<?>
   **/
  Class<? extends ADestTaxItemLn<?>> getDstTxItLnCl();

  /**
   * <p>Getter for good line class.</p>
   * @return Class<InvoiceLine<T>>
   **/
  Class<IInvoiceLine<T>> getGoodLnCl();

  /**
   * <p>Getter for service line class.</p>
   * @return Class<IInvoiceLine<T>>
   **/
  Class<IInvoiceLine<T>> getServiceLnCl();


  /**
   * <p>Getter for isTxByUser, if line tax must be set by user.</p>
   * @return Boolean
   **/
  Boolean getIsTxByUser();

  /**
   * <p>Lazy get for quTxInvBas.</p>
   * @return String
   * @throws IOException - IO exception
   **/
  String lazyGetQuTxInvBas() throws IOException;

  /**
   * <p>Lazy get for quTxInvBasAggr.</p>
   * @return String
   * @throws IOException - IO exception
   **/
  String lazyGetQuTxInvBasAggr() throws IOException;

  /**
   * <p>Lazy get for quTxItBasAggr.</p>
   * @return String
   * @throws IOException - IO exception
   **/
  String lazyGetQuTxItBasAggr() throws IOException;

  /**
   * <p>Lazy get for quTxItBas.</p>
   * @return String
   * @throws IOException - IO exception
   **/
  String lazyGetQuTxItBas() throws IOException;
}

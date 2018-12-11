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

import java.io.IOException;

import org.beigesoft.factory.IFactorySimple;
import org.beigesoft.accounting.persistable.base.AInvTxLn;
import org.beigesoft.accounting.persistable.IInvoice;
import org.beigesoft.accounting.persistable.IInvoiceLine;

/**
 * <p>Abstraction of tax method code/data for purchase/sales invoice.
 * It contains data dedicated to concrete invoice type.</p>
 *
 * @param <T> invoice type
 * @param <TL> invoice tax line type
 * @author Yury Demidenko
 */
public interface IInvTxMeth<T extends IInvoice, TL extends AInvTxLn<T>> {

  /**
   * <p>Getter for tblNmsTot.</p>
   * @return String[]
   **/
  String[] getTblNmsTot();

  /**
   * <p>Getter for good line class.</p>
   * @return Class<InvoiceLine<T>>
   **/
  Class<? extends IInvoiceLine<T>> getGoodLnCl();

  /**
   * <p>Getter for service line class.</p>
   * @return Class<? extends IInvoiceLine<T>>
   **/
  Class<? extends IInvoiceLine<T>> getServiceLnCl();

  /**
   * <p>Getter for invTxLnCl.</p>
   * @return Class<TL>
   **/
  Class<TL> getInvTxLnCl();

  /**
   * <p>Getter for fctInvTxLn.</p>
   * @return IFactorySimple<TL>
   **/
  IFactorySimple<TL> getFctInvTxLn();

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

  /**
   * <p>Lazy get for quTotals.</p>
   * @return String
   * @throws IOException - IO exception
   **/
  String lazyGetQuTotals() throws IOException;
}

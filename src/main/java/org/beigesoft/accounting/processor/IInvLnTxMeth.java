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

import org.beigesoft.factory.IFactorySimple;
import org.beigesoft.accounting.persistable.base.ALineTxLn;
import org.beigesoft.accounting.persistable.base.ADestTaxItemLn;
import org.beigesoft.accounting.persistable.IInvoice;
import org.beigesoft.accounting.persistable.IInvoiceLine;

/**
 * <p>Abstraction of tax method code/data for purchase/sales invoice line.
 * It contains code/data dedicated to concrete invoice line type.</p>
 *
 * @param <T> invoice type
 * @param <L> invoice line type
 * @param <LTL> invoice line's tax line type
 * @author Yury Demidenko
 */
public interface IInvLnTxMeth<T extends IInvoice, L extends IInvoiceLine<T>,
  LTL extends ALineTxLn<T, L>> {

  /**
   * <p>Getter for dstTxItLnCl.</p>
   * @return Class<?>
   **/
  Class<? extends ADestTaxItemLn<?>> getDstTxItLnCl();

  /**
   * <p>Getter for isMutable, if line editable, e.g. any good doesn't.</p>
   * @return Boolean
   **/
  Boolean getIsMutable();

  /**
   * <p>Getter for ltlCl.</p>
   * @return Class<LTL>
   **/
  Class<LTL> getLtlCl();

  /**
   * <p>Getter for fctLineTxLn.</p>
   * @return IFactorySimple<LTL>
   **/
  IFactorySimple<LTL> getFctLineTxLn();

  /**
   * <p>Getter for need make line tax category (purchase return not).</p>
   * @return Boolean
   **/
  Boolean getNeedMkTxCat();
}

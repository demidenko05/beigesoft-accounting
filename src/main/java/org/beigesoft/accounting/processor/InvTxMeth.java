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
import java.io.InputStream;
import java.net.URL;

import org.beigesoft.factory.IFactorySimple;
import org.beigesoft.accounting.persistable.base.AInvTxLn;
import org.beigesoft.accounting.persistable.IInvoice;
import org.beigesoft.accounting.persistable.IInvoiceLine;

/**
 * <p>Tax method code/data for purchase/sales invoice.
 * It contains code/data dedicated to concrete invoice type.</p>
 *
 * @param <T> invoice type
 * @param <TL> invoice tax line type
 * @author Yury Demidenko
 */
public class InvTxMeth<T extends IInvoice, TL extends AInvTxLn<T>>
  implements IInvTxMeth<T, TL> {

  /**
   * <p>Query invoice totals.</p>
   **/
  private String quTotals;

  /**
   * <p>Query invoice taxes item basis method
   * non-aggregate tax rate.</p>
   **/
  private String quTxItBas;

  /**
   * <p>Query invoice taxes item basis method
   * aggregate tax rate.</p>
   **/
  private String quTxItBasAggr;

  /**
   * <p>Query invoice taxes invoice basis method
   * aggregate tax rate.</p>
   **/
  private String quTxInvBasAggr;

  /**
   * <p>Query invoice taxes invoice basis method
   * non-aggregate tax rate.</p>
   **/
  private String quTxInvBas;

  /**
   * <p>Query invoice taxes invoice basis method
   * for adjusting invoice lines.</p>
   **/
  private String quTxInvAdj;

  /**
   * <p>File invoice totals.</p>
   **/
  private String flTotals;

  /**
   * <p>File invoice taxes item basis method
   * non-aggregate tax rate.</p>
   **/
  private String flTxItBas;

  /**
   * <p>File invoice taxes item basis method
   * aggregate tax rate.</p>
   **/
  private String flTxItBasAggr;

  /**
   * <p>File invoice taxes invoice basis method
   * aggregate tax rate.</p>
   **/
  private String flTxInvBasAggr;

  /**
   * <p>File invoice taxes invoice basis method
   * non-aggregate tax rate.</p>
   **/
  private String flTxInvBas;

  /**
   * <p>File invoice taxes invoice basis method
   * for adjusting invoice lines.</p>
   **/
  private String flTxInvAdj;

  /**
   * <p>Invoice SQL tables names: {[GOOD LINE], [SERVICE LINE],
   * [TAX LINE], [GOOD TAX LINE], [SERVICE TAX LINE]} or
   * {[GOOD LINE], [TAX LINE], [GOOD TAX LINE]}.
   * If SQL query no needs it, then set it NULL.</p>
   **/
  private String[] tblNmsTot;

  /**
   * <p>If tax amount set by user.</p>
   **/
  private Boolean isTxByUser;

  /**
   * <p>Good line class.</p>
   **/
  private Class<? extends IInvoiceLine<T>> goodLnCl;

  /**
   * <p>Service line class, NULL for returns.</p>
   **/
  private Class<? extends IInvoiceLine<T>> serviceLnCl;

  /**
   * <p>Invoice tax line factory.</p>
   **/
  private IFactorySimple<TL> fctInvTxLn;

  /**
   * <p>Invoice tax line class.</p>
   **/
  private Class<TL> invTxLnCl;

  /**
   * <p>Where start clause for adjusting invoice goods
   * lines for invoice basis method.</p>
   **/
  private String stWhereAdjGdLnInvBas;

  /**
   * <p>Where start clause for adjusting invoice service
   * lines for invoice basis method.</p>
   **/
  private String stWhereAdjSrLnInvBas;

  /**
   * <p>Getter for stWhereAdjGdLnInvBas.
   * Where start clause for adjusting invoice goods
   * lines for invoice basis method.</p>
   * @return String
   **/
  @Override
  public final String getStWhereAdjGdLnInvBas() {
    return this.stWhereAdjGdLnInvBas;
  }

  /**
   * <p>Getter for stWhereAdjSrLnInvBas.
   * Where start clause for adjusting invoice service
   * lines for invoice basis method.</p>
   * @return String
   **/
  @Override
  public final String getStWhereAdjSrLnInvBas() {
    return this.stWhereAdjSrLnInvBas;
  }

  /**
   * <p>Getter for fctInvTxLn.</p>
   * @return IFactorySimple<TL>
   **/
  @Override
  public final IFactorySimple<TL> getFctInvTxLn() {
    return this.fctInvTxLn;
  }

  /**
   * <p>Getter for good line class.</p>
   * @return Class<? extends IInvoiceLine<T>>
   **/
  @Override
  public final Class<? extends IInvoiceLine<T>> getGoodLnCl() {
    return this.goodLnCl;
  }

  /**
   * <p>Getter for service line class.</p>
   * @return Class<? extends IInvoiceLine<T>>
   **/
  @Override
  public final Class<? extends IInvoiceLine<T>> getServiceLnCl() {
    return this.serviceLnCl;
  }

  /**
   * <p>Getter for isTxByUser, if line tax must be set by user.</p>
   * @return Boolean
   **/
  @Override
  public final Boolean getIsTxByUser() {
    return this.isTxByUser;
  }

  /**
   * <p>Lazy get for quTxInvAdj.</p>
   * @return String
   * @throws IOException - IO exception
   **/
  @Override
  public final String lazyGetQuTxInvAdj() throws IOException {
    if (this.quTxInvAdj == null) {
      this.quTxInvAdj = loadString("/accounting/trade/" + this.flTxInvAdj);
    }
    return this.quTxInvAdj;
  }

  /**
   * <p>Lazy get for quTxInvBas.</p>
   * @return String
   * @throws IOException - IO exception
   **/
  @Override
  public final String lazyGetQuTxInvBas() throws IOException {
    if (this.quTxInvBas == null) {
      this.quTxInvBas = loadString("/accounting/trade/" + this.flTxInvBas);
    }
    return this.quTxInvBas;
  }

  /**
   * <p>Lazy get for quTxInvBasAggr.</p>
   * @return String
   * @throws IOException - IO exception
   **/
  @Override
  public final String lazyGetQuTxInvBasAggr() throws IOException {
    if (this.quTxInvBasAggr == null) {
      this.quTxInvBasAggr = loadString("/accounting/trade/"
        + this.flTxInvBasAggr);
    }
    return this.quTxInvBasAggr;
  }

  /**
   * <p>Lazy get for quTxItBasAggr.</p>
   * @return String
   * @throws IOException - IO exception
   **/
  @Override
  public final String lazyGetQuTxItBasAggr() throws IOException {
    if (this.quTxItBasAggr == null) {
      this.quTxItBasAggr =
        loadString("/accounting/trade/" + this.flTxItBasAggr);
    }
    return this.quTxItBasAggr;
  }

  /**
   * <p>Lazy get for quTxItBas.</p>
   * @return String
   * @throws IOException - IO exception
   **/
  @Override
  public final String lazyGetQuTxItBas() throws IOException {
    if (this.quTxItBas == null) {
      this.quTxItBas = loadString("/accounting/trade/" + this.flTxItBas);
    }
    return this.quTxItBas;
  }

  /**
   * <p>Lazy get for quTotals.</p>
   * @return String
   * @throws IOException - IO exception
   **/
  @Override
  public final String lazyGetQuTotals() throws IOException {
    if (this.quTotals == null) {
      this.quTotals = loadString("/accounting/trade/" + this.flTotals);
    }
    return this.quTotals;
  }

  /**
   * <p>Getter for invoice SQL tables names: {[GOOD LINE], [SERVICE LINE],
   * [TAX LINE], [GOOD TAX LINE], [SERVICE TAX LINE]} or
   * {[GOOD LINE], [TAX LINE], [GOOD TAX LINE]}.
   * If SQL query no needs it, then set it NULL.</p>
   * @return String[]
   **/
  @Override
  public final String[] getTblNmsTot() {
    return this.tblNmsTot;
  }

  /**
   * <p>Getter for invTxLnCl.</p>
   * @return Class<TL>
   **/
  @Override
  public final Class<TL> getInvTxLnCl() {
    return this.invTxLnCl;
  }

  /**
   * <p>Load string file (usually SQL query).</p>
   * @param pFileName file name
   * @return String usually SQL query
   * @throws IOException - IO exception
   **/
  public final String loadString(final String pFileName) throws IOException {
    URL urlFile = InvTxMeth.class.getResource(pFileName);
    if (urlFile != null) {
      InputStream inputStream = null;
      try {
        inputStream = InvTxMeth.class.getResourceAsStream(pFileName);
        byte[] bArray = new byte[inputStream.available()];
        inputStream.read(bArray, 0, inputStream.available());
        return new String(bArray, "UTF-8");
      } finally {
        if (inputStream != null) {
          inputStream.close();
        }
      }
    }
    return null;
  }

  //Simple getters and setters:
  /**
   * <p>Setter for invTxLnCl.</p>
   * @param pInvTxLnCl reference
   **/
  public final void setInvTxLnCl(final Class<TL> pInvTxLnCl) {
    this.invTxLnCl = pInvTxLnCl;
  }

  /**
   * <p>Setter for tblNmsTot.</p>
   * @param pTblNmsTot reference
   **/
  public final void setTblNmsTot(final String[] pTblNmsTot) {
    this.tblNmsTot = pTblNmsTot;
  }

  /**
   * <p>Setter for fctInvTxLn.</p>
   * @param pFctInvTxLn reference
   **/
  public final void setFctInvTxLn(final IFactorySimple<TL> pFctInvTxLn) {
    this.fctInvTxLn = pFctInvTxLn;
  }

  /**
   * <p>Setter for quTotals.</p>
   * @param pQuTotals reference
   **/
  public final void setQuTotals(final String pQuTotals) {
    this.quTotals = pQuTotals;
  }

  /**
   * <p>Setter for quTxItBas.</p>
   * @param pQuTxItBas reference
   **/
  public final void setQuTxItBas(final String pQuTxItBas) {
    this.quTxItBas = pQuTxItBas;
  }

  /**
   * <p>Setter for quTxItBasAggr.</p>
   * @param pQuTxItBasAggr reference
   **/
  public final void setQuTxItBasAggr(final String pQuTxItBasAggr) {
    this.quTxItBasAggr = pQuTxItBasAggr;
  }

  /**
   * <p>Setter for quTxInvBasAggr.</p>
   * @param pQuTxInvBasAggr reference
   **/
  public final void setQuTxInvBasAggr(final String pQuTxInvBasAggr) {
    this.quTxInvBasAggr = pQuTxInvBasAggr;
  }

  /**
   * <p>Setter for quTxInvAdj.</p>
   * @param pQuTxInvAdj reference
   **/
  public final void setQuTxInvAdj(final String pQuTxInvAdj) {
    this.quTxInvAdj = pQuTxInvAdj;
  }

  /**
   * <p>Setter for quTxInvBas.</p>
   * @param pQuTxInvBas reference
   **/
  public final void setQuTxInvBas(final String pQuTxInvBas) {
    this.quTxInvBas = pQuTxInvBas;
  }

  /**
   * <p>Getter for flTotals.</p>
   * @return String
   **/
  public final String getFlTotals() {
    return this.flTotals;
  }

  /**
   * <p>Setter for flTotals.</p>
   * @param pFlTotals reference
   **/
  public final void setFlTotals(final String pFlTotals) {
    this.flTotals = pFlTotals;
  }

  /**
   * <p>Getter for flTxItBas.</p>
   * @return String
   **/
  public final String getFlTxItBas() {
    return this.flTxItBas;
  }

  /**
   * <p>Setter for flTxItBas.</p>
   * @param pFlTxItBas reference
   **/
  public final void setFlTxItBas(final String pFlTxItBas) {
    this.flTxItBas = pFlTxItBas;
  }

  /**
   * <p>Getter for flTxItBasAggr.</p>
   * @return String
   **/
  public final String getFlTxItBasAggr() {
    return this.flTxItBasAggr;
  }

  /**
   * <p>Setter for flTxItBasAggr.</p>
   * @param pFlTxItBasAggr reference
   **/
  public final void setFlTxItBasAggr(final String pFlTxItBasAggr) {
    this.flTxItBasAggr = pFlTxItBasAggr;
  }

  /**
   * <p>Getter for flTxInvBasAggr.</p>
   * @return String
   **/
  public final String getFlTxInvBasAggr() {
    return this.flTxInvBasAggr;
  }

  /**
   * <p>Setter for flTxInvBasAggr.</p>
   * @param pFlTxInvBasAggr reference
   **/
  public final void setFlTxInvBasAggr(final String pFlTxInvBasAggr) {
    this.flTxInvBasAggr = pFlTxInvBasAggr;
  }

  /**
   * <p>Getter for flTxInvAdj.</p>
   * @return String
   **/
  public final String getFlTxInvAdj() {
    return this.flTxInvAdj;
  }

  /**
   * <p>Setter for flTxInvAdj.</p>
   * @param pFlTxInvAdj reference
   **/
  public final void setFlTxInvAdj(final String pFlTxInvAdj) {
    this.flTxInvAdj = pFlTxInvAdj;
  }

  /**
   * <p>Getter for flTxInvBas.</p>
   * @return String
   **/
  public final String getFlTxInvBas() {
    return this.flTxInvBas;
  }

  /**
   * <p>Setter for flTxInvBas.</p>
   * @param pFlTxInvBas reference
   **/
  public final void setFlTxInvBas(final String pFlTxInvBas) {
    this.flTxInvBas = pFlTxInvBas;
  }

  /**
   * <p>Setter for isTxByUser.</p>
   * @param pIsTxByUser reference
   **/
  public final void setIsTxByUser(final Boolean pIsTxByUser) {
    this.isTxByUser = pIsTxByUser;
  }

  /**
   * <p>Setter for goodLnCl.</p>
   * @param pGoodLnCl reference
   **/
  public final void setGoodLnCl(
    final Class<? extends IInvoiceLine<T>> pGoodLnCl) {
    this.goodLnCl = pGoodLnCl;
  }

  /**
   * <p>Setter for serviceLnCl.</p>
   * @param pServiceLnCl reference
   **/
  public final void setServiceLnCl(
    final Class<? extends IInvoiceLine<T>> pServiceLnCl) {
    this.serviceLnCl = pServiceLnCl;
  }

  /**
   * <p>Setter for stWhereAdjGdLnInvBas.</p>
   * @param pStWhereAdjGdLnInvBas reference
   **/
  public final void setStWhereAdjGdLnInvBas(
    final String pStWhereAdjGdLnInvBas) {
    this.stWhereAdjGdLnInvBas = pStWhereAdjGdLnInvBas;
  }

  /**
   * <p>Setter for stWhereAdjSrLnInvBas.</p>
   * @param pStWhereAdjSrLnInvBas reference
   **/
  public final void setStWhereAdjSrLnInvBas(
    final String pStWhereAdjSrLnInvBas) {
    this.stWhereAdjSrLnInvBas = pStWhereAdjSrLnInvBas;
  }
}

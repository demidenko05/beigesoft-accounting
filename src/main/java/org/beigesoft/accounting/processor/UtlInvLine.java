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

import org.beigesoft.factory.IFactorySimple;
import org.beigesoft.accounting.persistable.base.AInvTxLn;
import org.beigesoft.accounting.persistable.base.ALineTxLn;
import org.beigesoft.accounting.persistable.base.ADestTaxItemLn;
import org.beigesoft.accounting.persistable.IInvoice;
import org.beigesoft.accounting.persistable.IInvoiceLine;
import org.beigesoft.accounting.persistable.AccSettings;
import org.beigesoft.accounting.persistable.TaxDestination;

/**
 * <p>Utility for purchase/sales invoice line.
 * It's final assembly dedicated to concrete invoice line type.
 * Code in base utility is shared (there is only instance in memory).</p>
 *
 * @param <T> invoice type
 * @param <L> invoice line type
 * @param <TL> invoice tax line type
 * @param <LTL> invoice line's tax line type
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class UtlInvLine<RS, T extends IInvoice, L extends IInvoiceLine<T>,
  TL extends AInvTxLn<T>, LTL extends ALineTxLn<T, L>>
    implements IInvLnTxMeth<T, L, LTL> {

  /**
   * <p>Shared code-bunch.</p>
   **/
  private UtlInvBase<RS> utlInvBase;

  /**
   * <p>Invoice level shared options.</p>
   **/
  private InvTxMeth<T, TL> invTxMeth;

  /**
   * <p>Invoice line's tax line factory.</p>
   **/
  private IFactorySimple<LTL> fctLineTxLn;

  /**
   * <p>Line's tax line class.</p>
   **/
  private Class<LTL> ltlCl;

  /**
   * <p>Item's destination tax line class.</p>
   **/
  private Class<? extends ADestTaxItemLn<?>> dstTxItLnCl;

  /**
   * <p>If line editable, e.g. any good doesn't.</p>
   **/
  private Boolean isMutable;

  /**
   * <p>If need make line tax category (purchase return not).</p>
   **/
  private Boolean needMkTxCat;

  /**
   * <p>Getter for need make line tax category (purchase return not).</p>
   * @return Boolean
   **/
  @Override
  public final Boolean getNeedMkTxCat() {
    return needMkTxCat;
  }

  /**
   * <p>Getter for dstTxItLnCl.</p>
   * @return Class<?>
   **/
  @Override
  public final Class<? extends ADestTaxItemLn<?>> getDstTxItLnCl() {
    return this.dstTxItLnCl;
  }

  /**
   * <p>Getter for isMutable, if line editable, e.g. any good doesn't.</p>
   * @return Boolean
   **/
  @Override
  public final Boolean getIsMutable() {
    return this.isMutable;
  }

  /**
   * <p>Getter for ltlCl.</p>
   * @return Class<LTL>
   **/
  @Override
  public final Class<LTL> getLtlCl() {
    return this.ltlCl;
  }

  /**
   * <p>Getter for fctLineTxLn.</p>
   * @return IFactorySimple<LTL>
   **/
  @Override
  public final IFactorySimple<LTL> getFctLineTxLn() {
    return this.fctLineTxLn;
  }

  /**
   * <p>Reveal shared tax rules for invoice..</p>
   * @param pReqVars request scoped vars
   * @param pInv invoice
   * @param pAs Accounting Settings
   * @param pIsExtrTx if extract taxes
   * @return tax rules, NULL if not taxable
   * @throws Exception - an exception.
   **/
  public final TaxDestination revealTaxRules(final Map<String, Object> pReqVars,
    final IInvoice pInv, final AccSettings pAs,
      final Boolean pIsExtrTx) throws Exception {
    return this.utlInvBase.revealTaxRules(pReqVars, pInv, pAs, pIsExtrTx);
  }

  /**
   * <p>Makes invoice line's taxes, totals.</p>
   * @param pReqVars request scoped vars
   * @param pLine invoice line
   * @param pAs Accounting Settings
   * @param pTxRules NULL if not taxable
   * @throws Exception - an exception.
   **/
  public final void makeLine(final Map<String, Object> pReqVars,
    final L pLine, final AccSettings pAs,
      final TaxDestination pTxRules) throws Exception {
    this.utlInvBase
      .makeLine(pReqVars, pLine, pAs, pTxRules, this.invTxMeth, this);
  }

  /**
   * <p>Makes invoice totals include taxes lines
   * cause line inserted/changed/deleted.</p>
   * @param pReqVars request scoped vars
   * @param pLine affected line
   * @param pAs Accounting Settings
   * @param pTxRules NULL if not taxable
   * @throws Exception - an exception.
   **/
  public final void makeTotals(final Map<String, Object> pReqVars,
      final L pLine, final AccSettings pAs,
        final TaxDestination pTxRules) throws Exception {
    this.utlInvBase
      .makeTotals(pReqVars, pLine, pAs, pTxRules, this.invTxMeth);
  }

  /**
   * <p>Update invoice totals after tax line has
   * been changed (Invoice basis).</p>
   * @param pReqVars additional param
   * @param pInv Invoice
   * @param pAs accounting settings
   * @throws Exception - an exception
   **/
  public final void updInvTots(
    final Map<String, Object> pReqVars, final T pInv,
      final AccSettings pAs) throws Exception {
    this.utlInvBase.updInvTots(pReqVars, pInv, pAs, this.invTxMeth);
  }

  //Simple getters and setters:
  /**
   * <p>Getter for utlInvBase.</p>
   * @return UtlInvBase<RS>
   **/
  public final UtlInvBase<RS> getUtlInvBase() {
    return this.utlInvBase;
  }

  /**
   * <p>Setter for utlInvBase.</p>
   * @param pUtlInvBase reference
   **/
  public final void setUtlInvBase(final UtlInvBase<RS> pUtlInvBase) {
    this.utlInvBase = pUtlInvBase;
  }

  /**
   * <p>Setter for fctLineTxLn.</p>
   * @param pFctLineTxLn reference
   **/
  public final void setFctLineTxLn(final IFactorySimple<LTL> pFctLineTxLn) {
    this.fctLineTxLn = pFctLineTxLn;
  }

  /**
   * <p>Getter for invTxMeth.</p>
   * @return InvTxMeth<T, TL>
   **/
  public final InvTxMeth<T, TL> getInvTxMeth() {
    return this.invTxMeth;
  }

  /**
   * <p>Setter for invTxMeth.</p>
   * @param pInvTxMeth reference
   **/
  public final void setInvTxMeth(final InvTxMeth<T, TL> pInvTxMeth) {
    this.invTxMeth = pInvTxMeth;
  }

  /**
   * <p>Setter for ltlCl.</p>
   * @param pLtlCl reference
   **/
  public final void setLtlCl(final Class<LTL> pLtlCl) {
    this.ltlCl = pLtlCl;
  }
  /**
   * <p>Setter for dstTxItLnCl.</p>
   * @param pDstTxItLnCl reference
   **/
  public final void setDstTxItLnCl(
    final Class<? extends ADestTaxItemLn<?>> pDstTxItLnCl) {
    this.dstTxItLnCl = pDstTxItLnCl;
  }

  /**
   * <p>Setter for isMutable.</p>
   * @param pIsMutable reference
   **/
  public final void setIsMutable(final Boolean pIsMutable) {
    this.isMutable = pIsMutable;
  }
  /**
   * <p>Setter for needMkTxCat.</p>
   * @param pNeedMkTxCat reference
   **/
  public final void setNeedMkTxCat(final Boolean pNeedMkTxCat) {
    this.needMkTxCat = pNeedMkTxCat;
  }
}

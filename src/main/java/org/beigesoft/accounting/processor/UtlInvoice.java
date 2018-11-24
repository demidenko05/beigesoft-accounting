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
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.math.BigDecimal;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.beigesoft.factory.IFactorySimple;
import org.beigesoft.accounting.model.ETaxType;
import org.beigesoft.accounting.model.CmprTaxCatLnRate;
import org.beigesoft.accounting.persistable.base.AInvTxLn;
import org.beigesoft.accounting.persistable.base.ALineTxLn;
import org.beigesoft.accounting.persistable.base.ADestTaxItemLn;
import org.beigesoft.accounting.persistable.IInvoice;
import org.beigesoft.accounting.persistable.Tax;
import org.beigesoft.accounting.persistable.IInvoiceLine;
import org.beigesoft.accounting.persistable.SalesInvoiceServiceLine;
import org.beigesoft.accounting.persistable.InvItemTaxCategoryLine;
import org.beigesoft.accounting.persistable.AccSettings;
import org.beigesoft.accounting.persistable.TaxDestination;

/**
 * <p>Utility for purchase/sales invoice.
 * It contains code dedicated to concrete invoice line type.
 * This is actually resource-friendly assembly for line's service.
 * Code in base utility is shared (there is only instance in memory).</p>
 *
 * @param <T> invoice type
 * @param <L> invoice line type
 * @param <TL> invoice tax line type
 * @param <LTL> invoice line's tax line type
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class UtlInvoice<RS, T extends IInvoice, L extends IInvoiceLine<T>,
  TL extends AInvTxLn<T>, LTL extends ALineTxLn<T, L>>
    implements IMakerLn<T, L> {

  /**
   * <p>Base shared code-bunch.</p>
   **/
  private UtlInvBase<RS> utlInvBase;

  /**
   * <p>Invoice tax line factory.</p>
   **/
  private IFactorySimple<TL> fctInvTxLn;

  /**
   * <p>Invoice line's tax line factory.</p>
   **/
  private IFactorySimple<LTL> fctLineTxLn;

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
   * <p>Tables names for totals query.</p>
   **/
  private String[] tblNmsTot;

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
   * <p>If tax amount set by user.</p>
   **/
  private Boolean isTxByUser;

  /**
   * <p>Good line class.</p>
   **/
  private Class<IInvoiceLine<T>> goodLnCl;

  /**
   * <p>Service line class, NULL for returns.</p>
   **/
  private Class<IInvoiceLine<T>> serviceLnCl;

  /**
   * <p>Makes invoice totals tax cause line inserted/changed/deleted.</p>
   * @param pReqVars request scoped vars
   * @param pLine affected line
   * @param pInvTxLns invoice tax lines
   * @param pAs Accounting Settings
   * @param pTxRules NULL if not taxable
   * @throws Exception - an exception.
   **/
  public final void makeTotals(final Map<String, Object> pReqVars,
    final L pLine, final List<TL> pInvTxLns, final AccSettings pAs,
      final TaxDestination pTxRules) throws Exception {
    //all tax lines will be redone:
    for (TL tl : pInvTxLns) {
      tl.setTax(null);
      tl.setTaxableInvBas(BigDecimal.ZERO);
      tl.setTaxableInvBasFc(BigDecimal.ZERO);
      tl.setItsTotal(BigDecimal.ZERO);
      tl.setForeignTotalTaxes(BigDecimal.ZERO);
    }
    if (pTxRules != null) {
      DataTx dtTx = this.utlInvBase.retrieveDataTx(pReqVars, pLine, pAs,
        pTxRules, this);
      if (!pTxRules.getSalTaxUseAggregItBas() && !(pTxRules
        .getSalTaxIsInvoiceBase() && pLine.getItsOwner().getPriceIncTax())) {
        //non-aggregate except invoice basis with included taxes:
        for (int i = 0; i < dtTx.getTxs().size(); i++) {
          TL tl = findCreateTaxLine(pReqVars, pLine.getItsOwner(),
            pInvTxLns, dtTx.getTxs().get(i), false);
          Double txTotd;
          if (!pTxRules.getSalTaxIsInvoiceBase()) {
            //item basis, taxes excluded/included:
            txTotd = dtTx.getTxTotTaxb().get(i);
          } else {
            //invoice basis, taxes excluded:
            txTotd = dtTx.getTxTotTaxb().get(i)
              * dtTx.getTxPerc().get(i) / 100.0;
           tl.setTaxableInvBas(BigDecimal.valueOf(dtTx.getTxTotTaxb().get(i)));
          }
          tl.setItsTotal(BigDecimal.valueOf(txTotd).setScale(pAs.
            getPricePrecision(), pTxRules.getSalTaxRoundMode()));
          if (tl.getIsNew()) {
            this.utlInvBase.getSrvOrm().insertEntity(pReqVars, tl);
            tl.setIsNew(false);
          } else {
            this.utlInvBase.getSrvOrm().updateEntity(pReqVars, tl);
          }
        }
      } else { //non-aggregate invoice basis with included taxes
        //and aggregate for others:
        BigDecimal bd100 = new BigDecimal("100.00");
        Comparator<InvItemTaxCategoryLine> cmpr = Collections
          .reverseOrder(new CmprTaxCatLnRate());
        for (SalesInvoiceServiceLine txdLn : dtTx.getTxdLns()) {
          int ti = 0;
          //aggregate rate line scoped storages:
          BigDecimal taxAggegated = null;
          BigDecimal taxAggrAccum = BigDecimal.ZERO;
          Collections.sort(txdLn.getTaxCategory().getTaxes(), cmpr);
          for (InvItemTaxCategoryLine itcl
            : txdLn.getTaxCategory().getTaxes()) {
            ti++;
            if (taxAggegated == null) {
              if (pTxRules.getSalTaxIsInvoiceBase()) { //invoice basis:
                if (pLine.getItsOwner().getPriceIncTax()) {
                 taxAggegated = txdLn.getItsTotal().subtract(txdLn.getItsTotal()
          .divide(BigDecimal.ONE.add(txdLn.getTaxCategory().getAggrOnlyPercent()
      .divide(bd100)), pAs.getPricePrecision(), pTxRules.getSalTaxRoundMode()));
                } else {
                  taxAggegated = txdLn.getSubtotal().multiply(txdLn
                .getTaxCategory().getAggrOnlyPercent()).divide(bd100, pAs
              .getPricePrecision(), pTxRules.getSalTaxRoundMode());
                }
              } else {
                //item basis, taxes included/excluded
                taxAggegated = txdLn.getTotalTaxes();
              }
            }
            if (ti < txdLn.getTaxCategory().getTaxes().size()) {
              txdLn.setTotalTaxes(taxAggegated.multiply(itcl.getItsPercentage())
                .divide(txdLn.getTaxCategory().getAggrOnlyPercent(),
                  pAs.getPricePrecision(), pTxRules.getSalTaxRoundMode()));
              taxAggrAccum = taxAggrAccum.add(txdLn.getTotalTaxes());
            } else { //the rest or only tax:
              txdLn.setTotalTaxes(taxAggegated.subtract(taxAggrAccum));
            }
            TL tl = findCreateTaxLine(pReqVars, pLine.getItsOwner(),
              pInvTxLns, itcl.getTax(), true);
            tl.setItsTotal(tl.getItsTotal().add(txdLn.getTotalTaxes()));
            if (pTxRules.getSalTaxIsInvoiceBase()) {
              if (!pLine.getItsOwner().getPriceIncTax()) {
                tl.setTaxableInvBas(tl.getTaxableInvBas()
                  .add(txdLn.getSubtotal()));
              } else {
                tl.setTaxableInvBas(tl.getTaxableInvBas()
                  .add(txdLn.getItsTotal()));
              }
            }
            if (tl.getIsNew()) {
              this.utlInvBase.getSrvOrm().insertEntity(pReqVars, tl);
              tl.setIsNew(false);
            } else {
              this.utlInvBase.getSrvOrm().updateEntity(pReqVars, tl);
            }
          }
        }
      }
      if (pTxRules.getSalTaxIsInvoiceBase()) {
        this.utlInvBase.adjustInvoiceLns(pReqVars, pLine.getItsOwner(),
          dtTx.getTxdLns(), pAs, this);
      }
    }
    //delete tax lines with zero tax:
    for (TL tl : pInvTxLns) {
      if (tl.getTax() == null) {
        this.utlInvBase.getSrvOrm().deleteEntity(pReqVars, tl);
      }
    }
    calcTotals(pReqVars, pLine.getItsOwner(), pAs);
  }

  /**
   * <p>Makes invoice line taxes, totals.</p>
   * @param pReqVars request scoped vars
   * @param pLine invoice line
   * @param pAs Accounting Settings
   * @param pTxRules NULL if not taxable
   * @throws Exception - an exception.
   **/
  public final void makeLine(final Map<String, Object> pReqVars, final L pLine,
    final AccSettings pAs, final TaxDestination pTxRules) throws Exception {
    this.utlInvBase.makeLine(pReqVars, pLine, pAs, pTxRules, this);
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
   * <p>Getter for good line class.</p>
   * @return Class<IInvoiceLine<T>>
   **/
  @Override
  public final Class<IInvoiceLine<T>> getGoodLnCl() {
    return this.goodLnCl;
  }

  /**
   * <p>Getter for service line class.</p>
   * @return Class<IInvoiceLine<T>>
   **/
  @Override
  public final Class<IInvoiceLine<T>> getServiceLnCl() {
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
   * <p>Makes invoice line taxes item basis basis non-aggregate.</p>
   * @param pReqVars request scoped vars
   * @param pLine invoice line
   * @param pAs Accounting Settings
   * @param pTxRules taxable rules
   * @throws Exception - an exception.
   **/
  @Override
  public final void mkLnTxItBasNonAggr(final Map<String, Object> pReqVars,
    final L pLine, final AccSettings pAs,
      final TaxDestination pTxRules) throws Exception {
    List<LTL> itls = new ArrayList<LTL>();
    BigDecimal totTxs = BigDecimal.ZERO;
    BigDecimal totTxsFc = BigDecimal.ZERO;
    BigDecimal bd100 = new BigDecimal("100.00");
    pReqVars.put("InvItemTaxCategoryLineitsOwnerdeepLevel", 1);
    List<InvItemTaxCategoryLine> itcls = this.utlInvBase.getSrvOrm()
      .retrieveListWithConditions(pReqVars, InvItemTaxCategoryLine.class,
        "where ITSOWNER=" + pLine.getTaxCategory().getItsId()
          + " order by INVITEMTAXCATEGORYLINE.ITSPERCENTAGE");
    pReqVars.remove("InvItemTaxCategoryLineitsOwnerdeepLevel");
    BigDecimal taxTot = null;
    BigDecimal taxRest = null;
    BigDecimal taxTotFc = null;
    BigDecimal taxRestFc = null;
    if (pLine.getItsOwner().getPriceIncTax()) {
      taxTot = pLine.getItsTotal().subtract(pLine.getItsTotal()
    .divide(BigDecimal.ONE.add(pLine.getTaxCategory().getAggrOnlyPercent()
  .divide(bd100)), pAs.getPricePrecision(), pTxRules.getSalTaxRoundMode()));
      taxRest = taxTot;
      if (pLine.getItsOwner().getForeignCurrency() != null) {
        taxTotFc = pLine.getForeignTotal().subtract(pLine.getForeignTotal()
      .divide(BigDecimal.ONE.add(pLine.getTaxCategory().getAggrOnlyPercent()
    .divide(bd100)), pAs.getPricePrecision(), pTxRules.getSalTaxRoundMode()));
        taxRestFc = taxTotFc;
      }
    }
    StringBuffer sb = new StringBuffer();
    int i = 0;
    for (InvItemTaxCategoryLine itcl : itcls) {
     if (ETaxType.SALES_TAX_OUTITEM.equals(itcl.getTax().getItsType())
    || ETaxType.SALES_TAX_INITEM.equals(itcl.getTax().getItsType())) {
        if (i++ > 0) {
          sb.append(", ");
        }
        LTL itl = this.fctLineTxLn.create(pReqVars);
        itl.setIsNew(true);
        itl.setTax(itcl.getTax());
        itls.add(itl);
        BigDecimal addTx;
        BigDecimal addTxFc = null;
        if (pLine.getItsOwner().getPriceIncTax()) {
          if (i < itcls.size()) {
            addTx = taxTot.multiply(itcl.getItsPercentage()).divide(
              pLine.getTaxCategory().getAggrOnlyPercent(),
                pAs.getPricePrecision(), pTxRules.getSalTaxRoundMode());
            taxRest = taxRest.subtract(addTx);
          } else {
            addTx = taxRest;
          }
          if (pLine.getItsOwner().getForeignCurrency() != null) {
            if (i < itcls.size()) {
              addTxFc = taxTotFc.multiply(itcl.getItsPercentage()).divide(
                pLine.getTaxCategory().getAggrOnlyPercent(),
                  pAs.getPricePrecision(), pTxRules.getSalTaxRoundMode());
              taxRestFc = taxRestFc.subtract(addTxFc);
            } else {
              addTxFc = taxRestFc;
            }
          }
        } else {
          addTx = pLine.getSubtotal().multiply(itcl.getItsPercentage())
        .divide(bd100, pAs.getPricePrecision(), pTxRules.getSalTaxRoundMode());
          if (pLine.getItsOwner().getForeignCurrency() != null) {
          addTxFc = pLine.getForeignSubtotal().multiply(itcl.getItsPercentage())
        .divide(bd100, pAs.getPricePrecision(), pTxRules.getSalTaxRoundMode());
          }
        }
        totTxs = totTxs.add(addTx);
        itl.setItsTotal(addTx);
        if (pLine.getItsOwner().getForeignCurrency() != null) {
          totTxsFc = totTxsFc.add(addTxFc);
          itl.setForeignTotalTaxes(addTxFc);
        }
        sb.append(itl.getTax().getItsName() + " "
          + this.utlInvBase.prn(pReqVars, addTx));
      }
    }
    pLine.setTaxesDescription(sb.toString());
    List<LTL> itlsr = null;
    if (this.isMutable && !pLine.getIsNew()) {
      pReqVars.put(this.ltlCl.getSimpleName() + "itsOwnerdeepLevel", 1);
      itlsr = this.utlInvBase.getSrvOrm().retrieveListWithConditions(pReqVars,
        this.ltlCl, "where ITSOWNER=" + pLine.getItsId());
      pReqVars.remove(this.ltlCl.getSimpleName() + "itsOwnerdeepLevel");
    }
    if (itls != null && itls.size() > 0) {
      for (int j = 0; j < itls.size(); j++) {
        if (itlsr != null && j < itlsr.size()) {
          itlsr.get(j).setTax(itls.get(j).getTax());
          itlsr.get(j).setItsTotal(itls.get(j).getItsTotal());
          itlsr.get(j).setItsOwner(pLine);
          this.utlInvBase.getSrvOrm().updateEntity(pReqVars, itlsr.get(j));
        } else {
          itls.get(j).setItsOwner(pLine);
          itls.get(j).setInvoiceId(pLine.getItsOwner().getItsId());
          this.utlInvBase.getSrvOrm().insertEntity(pReqVars, itls.get(j));
          itls.get(j).setIsNew(false);
        }
      }
      if (itlsr != null) {
        for (int j = itls.size(); j < itlsr.size(); j++) {
          this.utlInvBase.getSrvOrm().deleteEntity(pReqVars, itlsr.get(j));
        }
      }
    } else if (itlsr != null) {
      for (LTL itl : itlsr) {
        this.utlInvBase.getSrvOrm().deleteEntity(pReqVars, itl);
      }
    }
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
      this.quTxItBas = loadString("/accounting/trade/"
        + this.flTxItBas);
    }
    return this.quTxItBas;
  }
  /**
   * <p>Calculates invoice totals after its line has been changed/deleted.</p>
   * @param pReqVars additional param
   * @param pInv Invoice
   * @param pAs accounting settings
   * @throws Exception - an exception
   **/
  public final void calcTotals(final Map<String, Object> pReqVars,
    final T pInv, final AccSettings pAs) throws Exception {
    String query = lazyGetQuTotals();
    query = query.replace(":ITSOWNER", pInv.getItsId().toString());
    if (this.tblNmsTot.length == 3) { //sales/purchase:
      query = query.replace(":TGOODLN", this.tblNmsTot[0]);
      query = query.replace(":TSERVICELN", this.tblNmsTot[1]);
      query = query.replace(":TTAXLN", this.tblNmsTot[2]);
    } else { //returns:
      query = query.replace(":TITEMLN", this.tblNmsTot[0]);
      query = query.replace(":TTAXLN", this.tblNmsTot[1]);
    }
    String[] columns = new String[] {"SUBTOTAL", "ITSTOTAL", "TOTALTAXES",
      "FOREIGNSUBTOTAL", "FOREIGNTOTAL", "FOREIGNTOTALTAXES"};
    Double[] totals = this.utlInvBase.getSrvDatabase()
      .evalDoubleResults(query, columns);
    if (totals[0] == null) {
      totals[0] = 0d;
    }
    if (totals[1] == null) {
      totals[1] = 0d;
    }
    if (totals[2] == null) {
      totals[2] = 0d;
    }
    if (totals[3] == null) {
      totals[3] = 0d;
    }
    if (totals[4] == null) {
      totals[4] = 0d;
    }
    if (totals[5] == null) {
      totals[5] = 0d;
    }
    if (pInv.getPriceIncTax()) {
      pInv.setItsTotal(BigDecimal.valueOf(totals[1]).setScale(
        pAs.getPricePrecision(), pAs.getRoundingMode()));
      pInv.setTotalTaxes(BigDecimal.valueOf(totals[2]).setScale(
        pAs.getPricePrecision(), pAs.getSalTaxRoundMode()));
      pInv.setSubtotal(pInv.getItsTotal().subtract(pInv.getTotalTaxes()));
      pInv.setForeignTotal(BigDecimal.valueOf(totals[4]).setScale(
        pAs.getPricePrecision(), pAs.getRoundingMode()));
      pInv.setForeignTotalTaxes(BigDecimal.valueOf(totals[5]).setScale(
        pAs.getPricePrecision(), pAs.getSalTaxRoundMode()));
      pInv.setForeignSubtotal(pInv.getForeignTotal().
        subtract(pInv.getForeignTotalTaxes()));
    } else {
      pInv.setSubtotal(BigDecimal.valueOf(totals[0]).setScale(
        pAs.getPricePrecision(), pAs.getRoundingMode()));
      pInv.setTotalTaxes(BigDecimal.valueOf(totals[2]).setScale(
        pAs.getPricePrecision(), pAs.getSalTaxRoundMode()));
      pInv.setItsTotal(pInv.getSubtotal().add(pInv.getTotalTaxes()));
      pInv.setForeignSubtotal(BigDecimal.valueOf(totals[3]).setScale(
        pAs.getPricePrecision(), pAs.getRoundingMode()));
      pInv.setForeignTotalTaxes(BigDecimal.valueOf(totals[5]).setScale(
        pAs.getPricePrecision(), pAs.getSalTaxRoundMode()));
      pInv.setForeignTotal(pInv.getForeignSubtotal().
        add(pInv.getForeignTotalTaxes()));
    }
    this.utlInvBase.getSrvOrm().updateEntity(pReqVars, pInv);
  }

  /**
   * <p>Finds (if need) line with same tax or creates one.</p>
   * @param pReqVars additional param
   * @param pInv invoice
   * @param pInvTxLns invoice tax lines
   * @param pTax tax
   * @param pNeedFind if need to find enabled
   * @return line
   * @throws Exception if no need to find but line is found
   **/
  public final TL findCreateTaxLine(final Map<String, Object> pReqVars,
    final T pInv, final List<TL> pInvTxLns, final Tax pTax,
      final boolean pNeedFind) throws Exception {
    TL itl = null;
    //find same line to add amount:
    for (TL tl : pInvTxLns) {
      if (tl.getTax() != null
        && tl.getTax().getItsId().equals(pTax.getItsId())) {
        if (!pNeedFind) {
          throw new Exception("Algorithm error!!!");
        }
        itl = tl;
        break;
      }
    }
    //find and enable disabled line:
    for (TL tl : pInvTxLns) {
      if (tl.getTax() == null) {
        itl = tl;
        itl.setTax(pTax);
        break;
      }
    }
    if (itl == null) {
      itl = this.fctInvTxLn.create(pReqVars);
      itl.setItsOwner(pInv);
      itl.setIsNew(true);
      itl.setTax(pTax);
      pInvTxLns.add(itl);
    }
    return itl;
  }

  /**
   * <p>Lazy get for quTotals.</p>
   * @return String
   * @throws IOException - IO exception
   **/
  public final String lazyGetQuTotals() throws IOException {
    if (this.quTotals == null) {
      this.quTotals = loadString("/accounting/trade/" + this.flTotals);
    }
    return this.quTotals;
  }


  /**
   * <p>Load string file (usually SQL query).</p>
   * @param pFileName file name
   * @return String usually SQL query
   * @throws IOException - IO exception
   **/
  public final String loadString(final String pFileName) throws IOException {
    URL urlFile = UtlInvoice.class.getResource(pFileName);
    if (urlFile != null) {
      InputStream inputStream = null;
      try {
        inputStream = UtlInvoice.class.getResourceAsStream(pFileName);
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
   * <p>Getter for isMutable, if line editable, e.g. any good doesn't.</p>
   * @return Boolean
   **/
  public final Boolean getIsMutable() {
    return this.isMutable;
  }

  /**
   * <p>Getter for ltlCl.</p>
   * @return Class<LTL>
   **/
  public final Class<LTL> getLtlCl() {
    return this.ltlCl;
  }

  /**
   * <p>Getter for fctInvTxLn.</p>
   * @return IFactorySimple<TL>
   **/
  public final IFactorySimple<TL> getFctInvTxLn() {
    return this.fctInvTxLn;
  }

  /**
   * <p>Setter for fctInvTxLn.</p>
   * @param pFctInvTxLn reference
   **/
  public final void setFctInvTxLn(final IFactorySimple<TL> pFctInvTxLn) {
    this.fctInvTxLn = pFctInvTxLn;
  }

  /**
   * <p>Getter for fctLineTxLn.</p>
   * @return IFactorySimple<LTL>
   **/
  public final IFactorySimple<LTL> getFctLineTxLn() {
    return this.fctLineTxLn;
  }

  /**
   * <p>Setter for fctLineTxLn.</p>
   * @param pFctLineTxLn reference
   **/
  public final void setFctLineTxLn(final IFactorySimple<LTL> pFctLineTxLn) {
    this.fctLineTxLn = pFctLineTxLn;
  }

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
   * <p>Getter for tblNmsTot.</p>
   * @return String[]
   **/
  public final String[] getTblNmsTot() {
    return this.tblNmsTot;
  }

  /**
   * <p>Setter for tblNmsTot.</p>
   * @param pTblNmsTot reference
   **/
  public final void setTblNmsTot(final String[] pTblNmsTot) {
    this.tblNmsTot = pTblNmsTot;
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
  public final void setDstTxItLnCl(final Class<? extends ADestTaxItemLn<?>> pDstTxItLnCl) {
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
  public final void setGoodLnCl(final Class<IInvoiceLine<T>> pGoodLnCl) {
    this.goodLnCl = pGoodLnCl;
  }

  /**
   * <p>Setter for serviceLnCl.</p>
   * @param pServiceLnCl reference
   **/
  public final void setServiceLnCl(final Class<IInvoiceLine<T>> pServiceLnCl) {
    this.serviceLnCl = pServiceLnCl;
  }
}

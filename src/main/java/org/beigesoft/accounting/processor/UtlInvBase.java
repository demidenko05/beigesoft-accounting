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
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.beigesoft.log.ILogger;
import org.beigesoft.model.IRecordSet;
import org.beigesoft.factory.IFactorySimple;
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.service.ISrvDatabase;
import org.beigesoft.service.ISrvNumberToString;
import org.beigesoft.accounting.model.ETaxType;
import org.beigesoft.accounting.model.CmprTaxCatLnRate;
import org.beigesoft.accounting.model.CmprTaxRate;
import org.beigesoft.accounting.model.CmprInvLnTotal;
import org.beigesoft.accounting.persistable.base.AInvTxLn;
import org.beigesoft.accounting.persistable.base.ALineTxLn;
import org.beigesoft.accounting.persistable.base.ADestTaxItemLn;
import org.beigesoft.accounting.persistable.AccSettings;
import org.beigesoft.accounting.persistable.IInvoice;
import org.beigesoft.accounting.persistable.IInvoiceLine;
import org.beigesoft.accounting.persistable.SalesInvoiceServiceLine;
import org.beigesoft.accounting.persistable.Tax;
import org.beigesoft.accounting.persistable.TaxDestination;
import org.beigesoft.accounting.persistable.InvItemTaxCategory;
import org.beigesoft.accounting.persistable.InvItemTaxCategoryLine;

/**
 * <p>Utility for purchase/sales invoice. Base shared code-bunch.</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class UtlInvBase<RS> {

  /**
   * <p>Logger.</p>
   **/
  private ILogger logger;

  /**
   * <p>Database service.</p>
   **/
  private ISrvDatabase<RS> srvDatabase;

  /**
   * <p>ORM service.</p>
   **/
  private ISrvOrm<RS> srvOrm;

  /**
   * <p>Service print number.</p>
   **/
  private ISrvNumberToString srvNumberToString;

  //Invoice's level code:
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
    TaxDestination txRules = null;
    if (pIsExtrTx && !pInv.getOmitTaxes()
      && !pInv.getCustomer().getIsForeigner()) {
      if (pInv.getCustomer().getTaxDestination() != null) {
        //override tax method:
        txRules = pInv.getCustomer().getTaxDestination();
      } else {
        txRules = new TaxDestination();
        txRules.setSalTaxIsInvoiceBase(pAs.getSalTaxIsInvoiceBase());
        txRules.setSalTaxUseAggregItBas(pAs.getSalTaxUseAggregItBas());
        txRules.setSalTaxRoundMode(pAs.getSalTaxRoundMode());
      }
    }
    return txRules;
  }

  /**
   * <p>Makes invoice totals include taxes lines
   * cause line inserted/changed/deleted.</p>
   * @param <T> invoice type
   * @param <L> invoice line type
   * @param <TL> invoice tax line type
   * @param pReqVars request scoped vars
   * @param pLine affected line
   * @param pAs Accounting Settings
   * @param pTxRules NULL if not taxable
   * @param pInvTxMeth tax method code/data for purchase/sales invoice
   * @throws Exception - an exception.
   **/
  public final <T extends IInvoice, L extends IInvoiceLine<T>,
    TL extends AInvTxLn<T>> void makeTotals(final Map<String, Object> pReqVars,
      final L pLine, final AccSettings pAs,
        final TaxDestination pTxRules,
          final IInvTxMeth<T, TL> pInvTxMeth) throws Exception {
    //all tax lines will be redone:
    pReqVars.put(pInvTxMeth.getInvTxLnCl().getSimpleName()
      + "itsOwnerdeepLevel", 1);
    List<TL> invTxLns = getSrvOrm().retrieveListWithConditions(
        pReqVars, pInvTxMeth.getInvTxLnCl(), "where ITSOWNER="
          + pLine.getItsOwner().getItsId());
    pReqVars.remove(pInvTxMeth.getInvTxLnCl().getSimpleName()
      + "itsOwnerdeepLevel");
    for (TL tl : invTxLns) {
      tl.setTax(null);
      tl.setTaxableInvBas(BigDecimal.ZERO);
      tl.setTaxableInvBasFc(BigDecimal.ZERO);
      tl.setItsTotal(BigDecimal.ZERO);
      tl.setForeignTotalTaxes(BigDecimal.ZERO);
    }
    if (pTxRules != null) {
      DataTx dtTx = retrieveDataTx(pReqVars, pLine, pAs,
        pTxRules, pInvTxMeth);
      if (!pTxRules.getSalTaxUseAggregItBas() && !(pTxRules
        .getSalTaxIsInvoiceBase() && pLine.getItsOwner().getPriceIncTax())) {
        //non-aggregate except invoice basis with included taxes:
        for (int i = 0; i < dtTx.getTxs().size(); i++) {
          TL tl = findCreateTaxLine(pReqVars, pLine.getItsOwner(),
            invTxLns, dtTx.getTxs().get(i), false, pInvTxMeth.getFctInvTxLn());
          Double txTotd;
          Double txTotdFc = 0.0;
          if (!pTxRules.getSalTaxIsInvoiceBase()) {
            //item basis, taxes excluded/included:
            txTotd = dtTx.getTxTotTaxb().get(i);
            txTotdFc = dtTx.getTxTotTaxbFc().get(i);
          } else {
            //invoice basis, taxes excluded:
            txTotd = dtTx.getTxTotTaxb().get(i)
              * dtTx.getTxPerc().get(i) / 100.0;
            tl.setTaxableInvBas(BigDecimal.valueOf(dtTx.getTxTotTaxb().get(i)));
            if (pLine.getItsOwner().getForeignCurrency() != null) {
              txTotdFc = dtTx.getTxTotTaxbFc().get(i)
                * dtTx.getTxPerc().get(i) / 100.0;
              tl.setTaxableInvBasFc(BigDecimal
                .valueOf(dtTx.getTxTotTaxbFc().get(i)));
            }
          }
          tl.setItsTotal(BigDecimal.valueOf(txTotd).setScale(pAs.
            getPricePrecision(), pTxRules.getSalTaxRoundMode()));
          tl.setForeignTotalTaxes(BigDecimal.valueOf(txTotdFc).setScale(pAs.
            getPricePrecision(), pTxRules.getSalTaxRoundMode()));
          if (tl.getIsNew()) {
            getSrvOrm().insertEntity(pReqVars, tl);
            tl.setIsNew(false);
          } else {
            getSrvOrm().updateEntity(pReqVars, tl);
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
          BigDecimal taxAggegatedFc = BigDecimal.ZERO;
          BigDecimal taxAggrAccumFc = BigDecimal.ZERO;
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
                  if (pLine.getItsOwner().getForeignCurrency() != null) {
                 taxAggegatedFc = txdLn.getForeignTotal().subtract(txdLn
          .getForeignTotal().divide(BigDecimal.ONE.add(txdLn.getTaxCategory()
      .getAggrOnlyPercent().divide(bd100)), pAs.getPricePrecision(),
    pTxRules.getSalTaxRoundMode()));
                  }
                } else {
                  taxAggegated = txdLn.getSubtotal().multiply(txdLn
                .getTaxCategory().getAggrOnlyPercent()).divide(bd100, pAs
              .getPricePrecision(), pTxRules.getSalTaxRoundMode());
                  if (pLine.getItsOwner().getForeignCurrency() != null) {
                    taxAggegatedFc = txdLn.getForeignSubtotal().multiply(txdLn
                  .getTaxCategory().getAggrOnlyPercent()).divide(bd100, pAs
                .getPricePrecision(), pTxRules.getSalTaxRoundMode());
                  }
                }
              } else {
                //item basis, taxes included/excluded
                taxAggegated = txdLn.getTotalTaxes();
                taxAggegatedFc = txdLn.getForeignTotalTaxes();
              }
            }
            //here total taxes mean total for current tax:
            if (ti < txdLn.getTaxCategory().getTaxes().size()) {
              txdLn.setTotalTaxes(taxAggegated.multiply(itcl.getItsPercentage())
                .divide(txdLn.getTaxCategory().getAggrOnlyPercent(),
                  pAs.getPricePrecision(), pTxRules.getSalTaxRoundMode()));
              taxAggrAccum = taxAggrAccum.add(txdLn.getTotalTaxes());
              if (pLine.getItsOwner().getForeignCurrency() != null) {
                txdLn.setForeignTotalTaxes(taxAggegatedFc.multiply(itcl
        .getItsPercentage()).divide(txdLn.getTaxCategory().getAggrOnlyPercent(),
      pAs.getPricePrecision(), pTxRules.getSalTaxRoundMode()));
              taxAggrAccumFc = taxAggrAccumFc.add(txdLn.getForeignTotalTaxes());
              }
            } else { //the rest or only tax:
              txdLn.setTotalTaxes(taxAggegated.subtract(taxAggrAccum));
              if (pLine.getItsOwner().getForeignCurrency() != null) {
            txdLn.setForeignTotalTaxes(taxAggegatedFc.subtract(taxAggrAccumFc));
              }
            }
            TL tl = findCreateTaxLine(pReqVars, pLine.getItsOwner(),
              invTxLns, itcl.getTax(), true, pInvTxMeth.getFctInvTxLn());
            tl.setItsTotal(tl.getItsTotal().add(txdLn.getTotalTaxes()));
            if (pLine.getItsOwner().getForeignCurrency() != null) {
              tl.setForeignTotalTaxes(tl.getForeignTotalTaxes()
                .add(txdLn.getForeignTotalTaxes()));
            }
            if (pTxRules.getSalTaxIsInvoiceBase()) {
              if (ti == txdLn.getTaxCategory().getTaxes().size()) {
                //total line taxes for farther invoice adjusting:
                txdLn.setTotalTaxes(taxAggegated);
                txdLn.setTotalTaxes(taxAggegatedFc);
              }
              if (!pLine.getItsOwner().getPriceIncTax()) {
                tl.setTaxableInvBas(tl.getTaxableInvBas()
                  .add(txdLn.getSubtotal()));
                if (pLine.getItsOwner().getForeignCurrency() != null) {
                  tl.setTaxableInvBasFc(tl.getTaxableInvBasFc()
                    .add(txdLn.getForeignSubtotal()));
                }
              } else {
                tl.setTaxableInvBas(tl.getTaxableInvBas()
                  .add(txdLn.getItsTotal()));
                if (pLine.getItsOwner().getForeignCurrency() != null) {
                  tl.setTaxableInvBasFc(tl.getTaxableInvBasFc()
                    .add(txdLn.getForeignTotal()));
                }
              }
            }
            if (tl.getIsNew()) {
              getSrvOrm().insertEntity(pReqVars, tl);
              tl.setIsNew(false);
            } else {
              getSrvOrm().updateEntity(pReqVars, tl);
            }
          }
        }
      }
      if (pTxRules.getSalTaxIsInvoiceBase()) {
        adjustInvoiceLns(pReqVars, pLine.getItsOwner(),
          dtTx.getTxdLns(), pAs, pInvTxMeth);
      }
    }
    //delete tax lines with zero tax:
    for (TL tl : invTxLns) {
      if (tl.getTax() == null) {
        getSrvOrm().deleteEntity(pReqVars, tl);
      }
    }
    if (pTxRules != null && !pTxRules.getSalTaxUseAggregItBas()
      && pLine.getItsOwner().getPriceIncTax()) {
      String watr = "TTR without aggregate! ";
      if (pLine.getItsOwner().getDescription() == null) {
        pLine.getItsOwner().setDescription(watr);
      } else if (!pLine.getItsOwner().getDescription().contains(watr)) {
        pLine.getItsOwner().setDescription(watr
          + pLine.getItsOwner().getDescription());
      }
    }
    updInvTots(pReqVars, pLine.getItsOwner(), pAs, pInvTxMeth);
  }

  /**
   * <p>Update invoice totals after its line has been changed/deleted
   * and taxes lines has been made
   * or after tax line has been changed (Invoice basis).</p>
   * @param <T> invoice type
   * @param pReqVars additional param
   * @param pInv Invoice
   * @param pAs accounting settings
   * @param pInvTxMeth tax method code/data for purchase/sales invoice
   * @throws Exception - an exception
   **/
  public final <T extends IInvoice> void updInvTots(
    final Map<String, Object> pReqVars, final T pInv, final AccSettings pAs,
      final IInvTxMeth<T, ?> pInvTxMeth) throws Exception {
    String query = pInvTxMeth.lazyGetQuTotals();
    query = query.replace(":ITSOWNER", pInv.getItsId().toString());
    if (pInvTxMeth.getTblNmsTot().length == 5) { //sales/purchase:
      query = query.replace(":TGOODLN", pInvTxMeth.getTblNmsTot()[0]);
      query = query.replace(":TSERVICELN", pInvTxMeth.getTblNmsTot()[1]);
      query = query.replace(":TTAXLN", pInvTxMeth.getTblNmsTot()[2]);
    } else { //returns:
      query = query.replace(":TGOODLN", pInvTxMeth.getTblNmsTot()[0]);
      query = query.replace(":TTAXLN", pInvTxMeth.getTblNmsTot()[1]);
    }
    String[] columns = new String[] {"SUBTOTAL", "ITSTOTAL", "TOTALTAXES",
      "FOREIGNSUBTOTAL", "FOREIGNTOTAL", "FOREIGNTOTALTAXES"};
    Double[] totals = getSrvDatabase()
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
    getSrvOrm().updateEntity(pReqVars, pInv);
  }

  /**
   * <p>Adjust invoice lines totals/subtotals/cost for invoice basis method.</p>
   * @param <T> invoice type
   * @param <L> invoice line type
   * @param <TL> invoice tax line type
   * @param pReqVars additional param
   * @param pInv invoice
   * @param pTxdLns Tax Data lines
   * @param pAs AS
   * @param pInvTxMeth tax method code/data for purchase/sales invoice
   * @throws Exception an Exception
   **/
  public final <T extends IInvoice, L extends IInvoiceLine<T>,
    TL extends AInvTxLn<T>> void adjustInvoiceLns(
    final Map<String, Object> pReqVars, final T pInv,
      final List<SalesInvoiceServiceLine> pTxdLns, final AccSettings pAs,
        final IInvTxMeth<T, TL> pInvTxMeth) throws Exception {
    String tbNm = pInvTxMeth.getGoodLnCl().getSimpleName();
    pReqVars.put(tbNm + "itsOwnerdeepLevel", 1);
    List<? extends IInvoiceLine<T>> gls = getSrvOrm()
      .retrieveListWithConditions(pReqVars, pInvTxMeth.getGoodLnCl(),
        pInvTxMeth.getStWhereAdjGdLnInvBas() + pInv.getItsId());
    pReqVars.remove(tbNm + "itsOwnerdeepLevel");
    List<? extends IInvoiceLine<T>> sls = null;
    if (pInvTxMeth.getServiceLnCl() != null) {
      tbNm = pInvTxMeth.getServiceLnCl().getSimpleName();
      pReqVars.put(tbNm + "itsOwnerdeepLevel", 1);
      sls = getSrvOrm().retrieveListWithConditions(pReqVars, pInvTxMeth
        .getServiceLnCl(), pInvTxMeth.getStWhereAdjSrLnInvBas()
          + pInv.getItsId());
      pReqVars.remove(tbNm + "itsOwnerdeepLevel");
    }
    //matched to current tax category (affected) invoice lines:
    List<IInvoiceLine<T>> ilsm = new ArrayList<IInvoiceLine<T>>();
    Comparator<IInvoiceLine<?>> cmpr = Collections
      .reverseOrder(new CmprInvLnTotal());
    for (SalesInvoiceServiceLine txdLn : pTxdLns) {
      for (IInvoiceLine<T> gl : gls) {
        if (gl.getTaxCategory().getItsId()
          .equals(txdLn.getTaxCategory().getItsId())) {
          ilsm.add(gl);
        }
      }
      if (sls != null) {
        for (IInvoiceLine<T> sl : sls) {
          if (sl.getTaxCategory().getItsId()
            .equals(txdLn.getTaxCategory().getItsId())) {
            ilsm.add(sl);
          }
        }
      }
      Collections.sort(ilsm, cmpr);
      BigDecimal txRest = txdLn.getTotalTaxes();
      BigDecimal txRestFc = txdLn.getForeignTotalTaxes();
      for (int i = 0; i < ilsm.size(); i++) {
        if (i + 1 == ilsm.size()) {
          if (pInv.getPriceIncTax()) {
            ilsm.get(i).setSubtotal(ilsm.get(i).getItsTotal().subtract(txRest));
          } else {
            ilsm.get(i).setItsTotal(ilsm.get(i).getSubtotal().add(txRest));
          }
          ilsm.get(i).setTotalTaxes(txRest);
          if (pInv.getForeignCurrency() != null) {
            if (pInv.getPriceIncTax()) {
              ilsm.get(i).setForeignSubtotal(ilsm.get(i).getForeignTotal()
                .subtract(txRestFc));
            } else {
              ilsm.get(i).setForeignTotal(ilsm.get(i).getForeignSubtotal()
                .add(txRestFc));
            }
            ilsm.get(i).setForeignTotalTaxes(txRestFc);
          }
        } else {
          BigDecimal taxTot;
          if (pInv.getPriceIncTax()) {
            taxTot = txdLn.getTotalTaxes().multiply(ilsm.get(i).getItsTotal())
    .divide(txdLn.getItsTotal(), pAs.getPricePrecision(), RoundingMode.HALF_UP);
            ilsm.get(i).setSubtotal(ilsm.get(i).getItsTotal().subtract(taxTot));
          } else {
            taxTot = txdLn.getTotalTaxes().multiply(ilsm.get(i).getSubtotal())
    .divide(txdLn.getSubtotal(), pAs.getPricePrecision(), RoundingMode.HALF_UP);
            ilsm.get(i).setItsTotal(ilsm.get(i).getSubtotal().add(taxTot));
          }
          ilsm.get(i).setTotalTaxes(taxTot);
          txRest = txRest.subtract(taxTot);
          if (pInv.getForeignCurrency() != null) {
            BigDecimal taxTotFc;
            if (pInv.getPriceIncTax()) {
 taxTotFc = txdLn.getForeignTotalTaxes().multiply(ilsm.get(i).getForeignTotal())
.divide(txdLn.getForeignTotal(), pAs.getPricePrecision(), RoundingMode.HALF_UP);
              ilsm.get(i).setForeignSubtotal(ilsm.get(i).getForeignTotal()
                .subtract(taxTotFc));
            } else {
              taxTotFc = txdLn.getForeignTotalTaxes().multiply(ilsm.get(i)
                .getForeignSubtotal()).divide(txdLn.getForeignSubtotal(),
                  pAs.getPricePrecision(), RoundingMode.HALF_UP);
              ilsm.get(i).setForeignTotal(ilsm.get(i).getForeignSubtotal()
                .add(taxTotFc));
            }
            ilsm.get(i).setForeignTotalTaxes(taxTotFc);
            txRestFc = txRestFc.subtract(taxTotFc);
          }
        }
        getSrvOrm().updateEntity(pReqVars, ilsm.get(i));
      }
      ilsm.clear();
    }
  }

  /**
   * <p>Retrieve from database tax data for adjusting invoice lines after
   * invoice tax line has been changed by user.</p>
   * @param <T> invoice type
   * @param <L> invoice line type
   * @param <TL> invoice tax line type
   * @param pReqVars request scoped vars
   * @param pInv affected invoice
   * @param pAs Accounting Settings
   * @param pTxRules taxable rules
   * @param pInvTxMeth tax method code/data for purchase/sales invoice
   * @return taxes data
   * @throws Exception - an exception.
   **/
  public final <T extends IInvoice, L extends IInvoiceLine<T>,
    TL extends AInvTxLn<T>> ArrayList<SalesInvoiceServiceLine> retrTxdLnsAdjInv(
      final Map<String, Object> pReqVars, final T pInv, final AccSettings pAs,
        final TaxDestination pTxRules,
          final IInvTxMeth<T, TL> pInvTxMeth) throws Exception {
    //totals by tax category for farther adjusting invoice:
    ArrayList<SalesInvoiceServiceLine> txdLns =
      new ArrayList<SalesInvoiceServiceLine>();
    //totals by "tax category, tax", map key is ID of tax category
    //itsPercentage holds total and plusAmount holds total FC
    //e.g. item A "tax18%, tax3%", item B "tax18%, tax1%"
    //there tax18% is used in two tax categories
    Map<Long, List<Tax>> tcTxs = new HashMap<Long,  List<Tax>>();
    //totals by tax for farther adjusting by "tax category, tax",
    //itsPercentage holds total and plusAmount holds total FC
    ArrayList<Tax> txs = new ArrayList<Tax>();
    String query = pInvTxMeth.lazyGetQuTxInvAdj();
    if (pInvTxMeth.getTblNmsTot().length == 5) { //sales/purchase:
      query = query.replace(":TGOODLN", pInvTxMeth.getTblNmsTot()[0]);
      query = query.replace(":TSERVICELN", pInvTxMeth.getTblNmsTot()[1]);
      query = query.replace(":TTAXLN", pInvTxMeth.getTblNmsTot()[2]);
    } else { //returns:
      query = query.replace(":TGOODLN", pInvTxMeth.getTblNmsTot()[0]);
      query = query.replace(":TTAXLN", pInvTxMeth.getTblNmsTot()[1]);
    }
    query = query.replace(":INVOICEID", pInv.getItsId().toString());
    IRecordSet<RS> recordSet = null;
    BigDecimal bd100 = new BigDecimal("100.00");
    try {
      recordSet = getSrvDatabase().retrieveRecords(query);
      if (recordSet.moveToFirst()) {
        do {
          Long txId = recordSet.getLong("TAXID");
          int li = txs.size() - 1;
          if (!(li >= 0 && txs.get(li).getItsId()
            .equals(txId))) {
            Tax tax = new Tax();
            tax.setItsId(txId);
            Double txtot = recordSet.getDouble("TXTOT");
            Double txtotfc = recordSet.getDouble("TXTOTFC");
            tax.setItsPercentage(BigDecimal.valueOf(txtot));
            tax.setPlusAmount(BigDecimal.valueOf(txtotfc));
            txs.add(tax);
          }
          Double pr = recordSet.getDouble("ITSPERCENTAGE");
          Long tcId = recordSet.getLong("TAXCATID");
          List<Tax> tctxs = null;
          for (Map.Entry<Long, List<Tax>> ent : tcTxs.entrySet()) {
            if (ent.getKey().equals(tcId)) {
              tctxs = ent.getValue();
              break;
            }
          }
          if (tctxs == null) {
            tctxs = new ArrayList<Tax>();
            tcTxs.put(tcId, tctxs);
          }
          Tax tctx = new Tax();
          tctx.setItsId(txId);
          tctxs.add(tctx);
          BigDecimal prbd = BigDecimal.valueOf(pr);
          BigDecimal txv;
          BigDecimal txvf;
          if (!pInv.getPriceIncTax()) {
            Double su = recordSet.getDouble("SUBTOTAL");
            Double suf = recordSet.getDouble("FOREIGNSUBTOTAL");
            txv = BigDecimal.valueOf(su).multiply(prbd).divide(bd100,
              pAs.getPricePrecision(), pTxRules.getSalTaxRoundMode());
            txvf = BigDecimal.valueOf(suf).multiply(prbd).divide(bd100,
              pAs.getPricePrecision(), pTxRules.getSalTaxRoundMode());
          } else {
            Double tot = recordSet.getDouble("ITSTOTAL");
            Double totf = recordSet.getDouble("FOREIGNTOTAL");
            BigDecimal totbd = BigDecimal.valueOf(tot);
            BigDecimal totbdf = BigDecimal.valueOf(totf);
            txv = totbd.subtract(totbd.divide(BigDecimal.ONE.add(prbd
              .divide(bd100)), pAs.getPricePrecision(),
                pTxRules.getSalTaxRoundMode()));
            txvf = totbdf.subtract(totbdf.divide(BigDecimal.ONE.add(prbd
              .divide(bd100)), pAs.getPricePrecision(),
                pTxRules.getSalTaxRoundMode()));
          }
          tctx.setPlusAmount(txvf);
          tctx.setItsPercentage(txv);
        } while (recordSet.moveToNext());
      }
    } finally {
      if (recordSet != null) {
        recordSet.close();
      }
    }
    //adjusting "total by [tax] -> total by [tax category, tax]":
    Comparator<Tax> cmpr = new CmprTaxRate();
    for (Tax tx : txs) {
      List<Tax> tlns = new ArrayList<Tax>();
      for (Map.Entry<Long, List<Tax>> ent : tcTxs.entrySet()) {
        for (Tax tctx : ent.getValue()) {
          if (tctx.getItsId().equals(tx.getItsId())) {
            tlns.add(tctx);
          }
        }
      }
      Collections.sort(tlns, cmpr);
      BigDecimal txRest = tx.getItsPercentage();
      BigDecimal txRestFc = tx.getPlusAmount();
      for (int i = 0; i < tlns.size(); i++) {
        if (i + 1 == tlns.size()) { //the biggest last gives the rest:
          tlns.get(i).setItsPercentage(txRest);
          tlns.get(i).setPlusAmount(txRestFc);
        } else { //the first lines are kept unchanged:
          txRest = txRest.subtract(tlns.get(i).getItsPercentage());
          txRestFc = txRestFc.subtract(tlns.get(i).getPlusAmount());
        }
      }
    }
    for (Map.Entry<Long, List<Tax>> ent : tcTxs.entrySet()) {
      SalesInvoiceServiceLine txdLn = new SalesInvoiceServiceLine();
      txdLn.setItsId(ent.getKey());
      InvItemTaxCategory tc = new InvItemTaxCategory();
      tc.setItsId(ent.getKey());
      txdLn.setTaxCategory(tc);
      txdLns.add(txdLn);
      for (Tax tx : ent.getValue()) {
        txdLn.setTotalTaxes(txdLn.getTotalTaxes().add(tx.getItsPercentage()));
        txdLn.setForeignTotalTaxes(txdLn.getForeignTotalTaxes()
          .add(tx.getPlusAmount()));
      }
    }
    return txdLns;
  }

  //Line's level code:
  /**
   * <p>Makes invoice line's taxes, totals.</p>
   * @param <T> invoice type
   * @param <L> invoice line type
   * @param <TL> invoice tax line type
   * @param <LTL> invoice line's tax line type
   * @param pReqVars request scoped vars
   * @param pLine invoice line
   * @param pAs Accounting Settings
   * @param pTxRules NULL if not taxable
   * @param pInvTxMeth tax method code/data for purchase/sales invoice
   * @param pInvLnTxMeth tax method code/data for purchase/sales invoice line
   * @throws Exception - an exception.
   **/
  public final <T extends IInvoice, L extends IInvoiceLine<T>,
    TL extends AInvTxLn<T>, LTL extends ALineTxLn<T, L>> void makeLine(
      final Map<String, Object> pReqVars, final L pLine, final AccSettings pAs,
        final TaxDestination pTxRules, final IInvTxMeth<T, TL> pInvTxMeth,
          final IInvLnTxMeth<T, L, LTL> pInvLnTxMeth) throws Exception {
    if (pInvLnTxMeth.getNeedMkTxCat()) {
      if (pTxRules != null) {
        pLine.setTaxCategory(pLine.getItem().getTaxCategory());
        if (pLine.getItsOwner().getCustomer().getTaxDestination() != null) {
          //override tax method:
          pReqVars.put(pInvLnTxMeth.getDstTxItLnCl().getSimpleName()
            + "itsOwnerdeepLevel", 1);
          List<ADestTaxItemLn<?>> dtls = (List<ADestTaxItemLn<?>>) getSrvOrm()
            .retrieveListWithConditions(pReqVars, pInvLnTxMeth.getDstTxItLnCl(),
              "where ITSOWNER=" + pLine.getItem().getItsId());
          pReqVars.remove(pInvLnTxMeth.getDstTxItLnCl().getSimpleName()
            + "itsOwnerdeepLevel");
          for (ADestTaxItemLn<?> dtl : dtls) {
            if (dtl.getTaxDestination().getItsId().equals(pLine.getItsOwner()
              .getCustomer().getTaxDestination().getItsId())) {
              pLine.setTaxCategory(dtl.getTaxCategory()); //it may be null
              break;
            }
          }
        }
      } else {
        pLine.setTaxCategory(null);
      }
    }
    List<LTL> itls = null;
    if (pLine.getTaxCategory() != null) {
      if (!pTxRules.getSalTaxIsInvoiceBase()) {
        if (!pTxRules.getSalTaxUseAggregItBas()) {
          itls = mkLnTxItBasNonAggr(pReqVars, pLine, pAs, pTxRules,
            pInvTxMeth, pInvLnTxMeth);
        } else {
          BigDecimal totTxs = BigDecimal.ZERO;
          BigDecimal totTxsFc = BigDecimal.ZERO;
          BigDecimal bd100 = new BigDecimal("100.00");
          if (pLine.getItsOwner().getPriceIncTax()) {
        totTxs = pLine.getItsTotal().subtract(pLine.getItsTotal()
    .divide(BigDecimal.ONE.add(pLine.getTaxCategory().getAggrOnlyPercent()
  .divide(bd100)), pAs.getPricePrecision(), pTxRules.getSalTaxRoundMode()));
            if (pLine.getItsOwner().getForeignCurrency() != null) {
        totTxsFc = pLine.getForeignTotal().subtract(pLine.getForeignTotal()
    .divide(BigDecimal.ONE.add(pLine.getTaxCategory().getAggrOnlyPercent()
  .divide(bd100)), pAs.getPricePrecision(), pTxRules.getSalTaxRoundMode()));
            }
          } else {
            totTxs = pLine.getSubtotal().multiply(pLine.getTaxCategory()
          .getAggrOnlyPercent()).divide(bd100, pAs.getPricePrecision(),
        pTxRules.getSalTaxRoundMode());
            if (pLine.getItsOwner().getForeignCurrency() != null) {
              totTxsFc = pLine.getForeignSubtotal().multiply(pLine
            .getTaxCategory().getAggrOnlyPercent()).divide(bd100, pAs
          .getPricePrecision(), pTxRules.getSalTaxRoundMode());
            }
          }
          pLine.setTaxesDescription(pLine.getTaxCategory().getItsName());
          mkLnFinal(pLine, totTxs, totTxsFc, pInvTxMeth.getIsTxByUser());
        }
      } else {
        pLine.setTaxesDescription(pLine.getTaxCategory().getItsName());
        pLine.setTotalTaxes(BigDecimal.ZERO);
        pLine.setForeignTotalTaxes(BigDecimal.ZERO);
      }
    } else {
      pLine.setTaxesDescription(null);
      pLine.setTotalTaxes(BigDecimal.ZERO);
      pLine.setForeignTotalTaxes(BigDecimal.ZERO);
    }
    if (pLine.getItsOwner().getForeignCurrency() == null) {
      pLine.setForeignTotalTaxes(BigDecimal.ZERO);
      pLine.setForeignTotal(BigDecimal.ZERO);
      pLine.setForeignSubtotal(BigDecimal.ZERO);
    }
    if (pTxRules != null && !pTxRules.getSalTaxIsInvoiceBase()) {
      if (!pLine.getItsOwner().getPriceIncTax()) {
        pLine.setItsTotal(pLine.getSubtotal().add(pLine.getTotalTaxes()));
        if (pLine.getItsOwner().getForeignCurrency() != null) {
          pLine.setForeignTotal(pLine.getForeignSubtotal()
            .add(pLine.getForeignTotalTaxes()));
        }
      } else {
        pLine.setSubtotal(pLine.getItsTotal().subtract(pLine.getTotalTaxes()));
        if (pLine.getItsOwner().getForeignCurrency() != null) {
          pLine.setForeignSubtotal(pLine.getForeignTotal()
            .subtract(pLine.getForeignTotalTaxes()));
        }
      }
    } //invoice basis - lines tax, subt, tot will be adjusted later!
    if (pLine.getIsNew()) {
      getSrvOrm().insertEntity(pReqVars, pLine);
    } else {
      getSrvOrm().updateEntity(pReqVars, pLine);
    }
    if (itls != null) {
      List<LTL> itlsr = null;
      if (pInvLnTxMeth.getIsMutable() && !pLine.getIsNew()) {
        pReqVars.put(pInvLnTxMeth.getLtlCl().getSimpleName()
          + "itsOwnerdeepLevel", 1);
        itlsr = getSrvOrm().retrieveListWithConditions(pReqVars,
          pInvLnTxMeth.getLtlCl(), "where ITSOWNER=" + pLine.getItsId());
        pReqVars.remove(pInvLnTxMeth.getLtlCl().getSimpleName()
          + "itsOwnerdeepLevel");
      }
      for (int j = 0; j < itls.size(); j++) {
        if (itlsr != null && j < itlsr.size()) {
          itlsr.get(j).setTax(itls.get(j).getTax());
          itlsr.get(j).setItsTotal(itls.get(j).getItsTotal());
          itlsr.get(j).setItsOwner(pLine);
          getSrvOrm().updateEntity(pReqVars, itlsr.get(j));
        } else {
          itls.get(j).setItsOwner(pLine);
          itls.get(j).setInvoiceId(pLine.getItsOwner().getItsId());
          getSrvOrm().insertEntity(pReqVars, itls.get(j));
          itls.get(j).setIsNew(false);
        }
      }
      if (itlsr != null) {
        for (int j = itls.size(); j < itlsr.size(); j++) {
          getSrvOrm().deleteEntity(pReqVars, itlsr.get(j));
        }
      }
    }
  }


  /**
   * <p>Retrieve from database bundle of tax data.</p>
   * @param <T> invoice type
   * @param <L> invoice line type
   * @param <TL> invoice tax line type
   * @param pReqVars request scoped vars
   * @param pLine affected line
   * @param pAs Accounting Settings
   * @param pTxRules taxable rules
   * @param pInvTxMeth tax method code/data for purchase/sales invoice
   * @return taxes data
   * @throws Exception - an exception.
   **/
  public final <T extends IInvoice, L extends IInvoiceLine<T>,
    TL extends AInvTxLn<T>> DataTx retrieveDataTx(
      final Map<String, Object> pReqVars, final L pLine, final AccSettings pAs,
        final TaxDestination pTxRules,
          final IInvTxMeth<T, TL> pInvTxMeth) throws Exception {
    DataTx dtTx = new DataTx();
    String query;
    if (!pTxRules.getSalTaxUseAggregItBas() && !(pTxRules
      .getSalTaxIsInvoiceBase() && pLine.getItsOwner().getPriceIncTax())) {
      //non-aggregate except invoice basis with included taxes:
      dtTx.setTxs(new ArrayList<Tax>());
      dtTx.setTxTotTaxb(new ArrayList<Double>());
      dtTx.setTxTotTaxbFc(new ArrayList<Double>());
      if (!pTxRules.getSalTaxIsInvoiceBase()) {
        //item basis:
        query = pInvTxMeth.lazyGetQuTxItBas();
      } else {
        //invoice basis, taxes excluded:
        dtTx.setTxPerc(new ArrayList<Double>());
        query = pInvTxMeth.lazyGetQuTxInvBas();
        //totals by tax category for farther adjusting invoice:
        dtTx.setTxdLns(new ArrayList<SalesInvoiceServiceLine>());
      }
    } else { //non-aggregate invoice basis with included taxes
      //and aggregate for others:
      dtTx.setTxdLns(new ArrayList<SalesInvoiceServiceLine>());
      if (!pTxRules.getSalTaxIsInvoiceBase()) { //item basis
        query = pInvTxMeth.lazyGetQuTxItBasAggr();
      } else { //invoice basis:
        query = pInvTxMeth.lazyGetQuTxInvBasAggr();
      }
    }
    if (getLogger().getIsShowDebugMessagesFor(getClass())
      && getLogger().getDetailLevel() > 40000) {
      getLogger().debug(pReqVars, UtlInvBase.class,
        "Tax rules: aggregate/invoice basis/zip/RM = " + pTxRules
          .getSalTaxUseAggregItBas() + "/" + pTxRules.getSalTaxIsInvoiceBase()
            + "/" + pTxRules.getRegZip() + "/" + pTxRules.getSalTaxRoundMode());
      String txCat;
      if (pLine.getTaxCategory() != null) {
        txCat = pLine.getTaxCategory().getItsName();
      } else {
        txCat = "-";
      }
      getLogger().debug(pReqVars, UtlInvBase.class, "Item: name/tax category = "
        + pLine.getItem().getItsName() + "/" + txCat);
      getLogger().debug(pReqVars, UtlInvBase.class, "Tax query: " + query);
    }
    if (!pTxRules.getSalTaxUseAggregItBas()
      && !pTxRules.getSalTaxIsInvoiceBase()) {
      if (pInvTxMeth.getTblNmsTot().length == 5) { //sales/purchase:
        query = query.replace(":TGOODTXLN", pInvTxMeth.getTblNmsTot()[3]);
        query = query.replace(":TSERVICETXLN", pInvTxMeth.getTblNmsTot()[4]);
      } else { //returns:
        query = query.replace(":TITEMTXLN", pInvTxMeth.getTblNmsTot()[2]);
      }
    } else {
      if (pInvTxMeth.getTblNmsTot().length == 5) { //sales/purchase:
        query = query.replace(":TGOODLN", pInvTxMeth.getTblNmsTot()[0]);
        query = query.replace(":TSERVICELN", pInvTxMeth.getTblNmsTot()[1]);
      } else { //returns:
        query = query.replace(":TGOODLN", pInvTxMeth.getTblNmsTot()[0]);
      }
    }
    query = query.replace(":INVOICEID", pLine.getItsOwner()
      .getItsId().toString());
    IRecordSet<RS> recordSet = null;
    try {
      recordSet = getSrvDatabase().retrieveRecords(query);
      if (recordSet.moveToFirst()) {
        do {
          Long txId = recordSet.getLong("TAXID");
          String txNm = recordSet.getString("TAXNAME");
          Tax tax = new Tax();
          tax.setItsId(txId);
          tax.setItsName(txNm);
          if (!pTxRules.getSalTaxUseAggregItBas() && !(pTxRules
           .getSalTaxIsInvoiceBase() && pLine.getItsOwner().getPriceIncTax())) {
            //non-aggregate except invoice basis with included taxes:
            if (!pTxRules.getSalTaxIsInvoiceBase()) {
              //item basis, taxes excluded/included:
              dtTx.getTxs().add(tax);
              dtTx.getTxTotTaxb().add(recordSet.getDouble("TOTALTAX"));
            dtTx.getTxTotTaxbFc().add(recordSet.getDouble("FOREIGNTOTALTAXES"));
            } else {
              //invoice basis, taxes excluded:
              boolean isNew = true;
              int li = dtTx.getTxTotTaxb().size() - 1;
              if (li >= 0 && dtTx.getTxs().get(li).getItsId()
                .equals(tax.getItsId())) {
                isNew = false;
              }
              Double su = recordSet.getDouble("SUBTOTAL");
              Double suf = recordSet.getDouble("FOREIGNSUBTOTAL");
              Double pr = recordSet.getDouble("ITSPERCENTAGE");
              if (isNew) {
                dtTx.getTxs().add(tax);
                dtTx.getTxTotTaxb().add(su);
                dtTx.getTxTotTaxbFc().add(suf);
                dtTx.getTxPerc().add(pr);
              } else {
                Double sut = su + dtTx.getTxTotTaxb().get(li);
                dtTx.getTxTotTaxb().set(li, sut);
                Double sutf = suf + dtTx.getTxTotTaxbFc().get(li);
                dtTx.getTxTotTaxbFc().set(li, sutf);
              }
              Long tcId = recordSet.getLong("TAXCATID");
              addInvBsTxExTxc(dtTx.getTxdLns(), tcId, su, suf, pr, pAs,
                pTxRules);
            }
          } else { //non-aggregate invoice basis with included taxes
            //and aggregate for others:
            Double percent = recordSet.getDouble("ITSPERCENTAGE");
            Long tcId = recordSet.getLong("TAXCATID");
            if (!pTxRules.getSalTaxIsInvoiceBase()) { //item basis:
              Long clId = recordSet.getLong("ILID");
              SalesInvoiceServiceLine txdLn = makeTxdLine(dtTx.getTxdLns(),
                clId, tcId, tax, percent, pAs);
              txdLn.setTotalTaxes(BigDecimal.valueOf(recordSet
                .getDouble("TOTALTAXES"))
                  .setScale(pAs.getPricePrecision(), RoundingMode.HALF_UP));
              txdLn.setForeignTotalTaxes(BigDecimal.valueOf(recordSet
                .getDouble("FOREIGNTOTALTAXES"))
                  .setScale(pAs.getPricePrecision(), RoundingMode.HALF_UP));
            } else { //invoice basis:
              SalesInvoiceServiceLine txdLn = makeTxdLine(dtTx.getTxdLns(),
                tcId, tcId, tax, percent, pAs);
              txdLn.setItsTotal(BigDecimal.valueOf(recordSet
                .getDouble("ITSTOTAL"))
                  .setScale(pAs.getPricePrecision(), RoundingMode.HALF_UP));
              txdLn.setSubtotal(BigDecimal.valueOf(recordSet
                .getDouble("SUBTOTAL"))
                  .setScale(pAs.getPricePrecision(), RoundingMode.HALF_UP));
              txdLn.setForeignTotal(BigDecimal.valueOf(recordSet
                .getDouble("FOREIGNTOTAL"))
                  .setScale(pAs.getPricePrecision(), RoundingMode.HALF_UP));
              txdLn.setForeignSubtotal(BigDecimal.valueOf(recordSet
                .getDouble("FOREIGNSUBTOTAL"))
                  .setScale(pAs.getPricePrecision(), RoundingMode.HALF_UP));
            }
          }
        } while (recordSet.moveToNext());
      }
    } finally {
      if (recordSet != null) {
        recordSet.close();
      }
    }
    return dtTx;
  }

  /**
   * <p>Adds total tax by category for farther invoice adjusting.</p>
   * @param pTxdLns Tax Data lines
   * @param pCatId tax category ID
   * @param pSubt subtotal without taxes
   * @param pSubtFc subtotal FC without taxes
   * @param pPercent tax rate
   * @param pAs AS
   * @param pTxRules tax rules
   **/
  public final void addInvBsTxExTxc(final List<SalesInvoiceServiceLine> pTxdLns,
    final Long pCatId, final Double pSubt, final Double pSubtFc,
      final Double pPercent, final AccSettings pAs,
        final TaxDestination pTxRules) {
    SalesInvoiceServiceLine txdLn = null;
    for (SalesInvoiceServiceLine tdl : pTxdLns) {
      if (tdl.getItsId().equals(pCatId)) {
        txdLn = tdl;
      }
    }
    if (txdLn == null) {
      txdLn = new SalesInvoiceServiceLine();
      txdLn.setItsId(pCatId);
      InvItemTaxCategory tc = new InvItemTaxCategory();
      tc.setItsId(pCatId);
      txdLn.setTaxCategory(tc);
      pTxdLns.add(txdLn);
    }
    BigDecimal bd100 = new BigDecimal("100.00");
    BigDecimal txv = BigDecimal.valueOf(pSubt).multiply(BigDecimal
      .valueOf(pPercent)).divide(bd100, pAs.getPricePrecision(),
        pTxRules.getSalTaxRoundMode());
    txdLn.setTotalTaxes(txdLn.getTotalTaxes().add(txv));
    BigDecimal txvf = BigDecimal.valueOf(pSubtFc).multiply(BigDecimal
      .valueOf(pPercent)).divide(bd100, pAs.getPricePrecision(),
        pTxRules.getSalTaxRoundMode());
    txdLn.setForeignTotalTaxes(txdLn.getForeignTotalTaxes().add(txvf));
  }

  /**
   * <p>Make invoice line that stores taxes data in lines set
   * for invoice basis or item basis aggregate rate.</p>
   * @param pTxdLns Tax Data lines
   * @param pTdlId line ID
   * @param pCatId tax category ID
   * @param pTax tax
   * @param pPercent tax rate
   * @param pAs AS
   * @return line
   **/
  public final SalesInvoiceServiceLine makeTxdLine(
    final List<SalesInvoiceServiceLine> pTxdLns, final Long pTdlId,
      final Long pCatId,  final Tax pTax, final Double pPercent,
        final AccSettings pAs) {
    SalesInvoiceServiceLine txdLn = null;
    for (SalesInvoiceServiceLine tdl : pTxdLns) {
      if (tdl.getItsId().equals(pTdlId)) {
        txdLn = tdl;
      }
    }
    if (txdLn == null) {
      txdLn = new SalesInvoiceServiceLine();
      txdLn.setItsId(pTdlId);
      InvItemTaxCategory tc = new InvItemTaxCategory();
      tc.setItsId(pCatId);
      tc.setTaxes(new ArrayList<InvItemTaxCategoryLine>());
      txdLn.setTaxCategory(tc);
      pTxdLns.add(txdLn);
    }
    InvItemTaxCategoryLine itcl = new InvItemTaxCategoryLine();
    itcl.setTax(pTax);
    itcl.setItsPercentage(BigDecimal.valueOf(pPercent)
      .setScale(pAs.getTaxPrecision(), RoundingMode.HALF_UP));
    txdLn.getTaxCategory().getTaxes().add(itcl);
    txdLn.getTaxCategory().setAggrOnlyPercent(txdLn.getTaxCategory()
      .getAggrOnlyPercent().add(itcl.getItsPercentage()));
    return txdLn;
  }

  /**
   * <p>Makes invoice line taxes item basis basis non-aggregate.</p>
   * @param <T> invoice type
   * @param <L> invoice line type
   * @param <LTL> invoice line's tax line type
   * @param pReqVars request scoped vars
   * @param pLine invoice line
   * @param pAs Accounting Settings
   * @param pTxRules taxable rules
   * @param pInvTxMeth tax method code/data for purchase/sales invoice
   * @param pInvLnTxMeth tax method code/data for purchase/sales invoice line
   * @return created invoice line taxes for farther proceed
   * @throws Exception - an exception.
   **/
  public final <T extends IInvoice, L extends IInvoiceLine<T>,
    LTL extends ALineTxLn<T, L>> List<LTL> mkLnTxItBasNonAggr(
      final Map<String, Object> pReqVars, final L pLine, final AccSettings pAs,
        final TaxDestination pTxRules, final IInvTxMeth<T, ?> pInvTxMeth,
          final IInvLnTxMeth<T, L, LTL> pInvLnTxMeth) throws Exception {
    List<LTL> itls = new ArrayList<LTL>();
    BigDecimal totTxs = BigDecimal.ZERO;
    BigDecimal totTxsFc = BigDecimal.ZERO;
    BigDecimal bd100 = new BigDecimal("100.00");
    pReqVars.put("InvItemTaxCategoryLineitsOwnerdeepLevel", 1);
    List<InvItemTaxCategoryLine> itcls = getSrvOrm()
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
        LTL itl = pInvLnTxMeth.getFctLineTxLn().create(pReqVars);
        itl.setIsNew(true);
        itl.setTax(itcl.getTax());
        itls.add(itl);
        BigDecimal addTx;
        BigDecimal addTxFc = null;
        if (pLine.getItsOwner().getPriceIncTax()) {
          //tax set by user:
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
          //tax always calculated:
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
          + prn(pReqVars, addTx));
      }
    }
    pLine.setTaxesDescription(sb.toString());
    mkLnFinal(pLine, totTxs, totTxsFc, pLine.getItsOwner().getPriceIncTax());
    return itls;
  }

  /**
   * <p>Makes invoice line final results.</p>
   * @param <T> invoice type
   * @param <L> invoice line type
   * @param pLine invoice line
   * @param pTotTxs total line taxes
   * @param pTotTxsFc total line taxes FC
   * @param pIsTxByUser if tax set by user
   **/
  public final <T extends IInvoice, L extends IInvoiceLine<T>> void mkLnFinal(
    final L pLine, final BigDecimal pTotTxs, final BigDecimal pTotTxsFc,
      final Boolean pIsTxByUser) {
    if (pIsTxByUser) {
      if (pLine.getItsOwner().getForeignCurrency() == null) {
        if (pLine.getTotalTaxes().compareTo(pTotTxs) != 0) {
          if (pLine.getDescription() == null) {
            pLine.setDescription(pLine.getTotalTaxes().toString() + "!="
              + pTotTxs + "!");
          } else {
            pLine.setDescription(pLine.getDescription() + " " + pLine
              .getTotalTaxes().toString() + "!=" + pTotTxs + "!");
          }
        }
      } else {
        pLine.setTotalTaxes(pTotTxs);
        if (pLine.getForeignTotalTaxes().compareTo(pTotTxsFc) != 0) {
          if (pLine.getDescription() == null) {
            pLine.setDescription(pLine.getForeignTotalTaxes().toString()
              + "!=" + pTotTxsFc + "!");
          } else {
            pLine.setDescription(pLine.getDescription() + " " + pLine
              .getForeignTotalTaxes().toString() + "!=" + pTotTxsFc + "!");
          }
        }
      }
    } else {
      pLine.setTotalTaxes(pTotTxs);
      pLine.setForeignTotalTaxes(pTotTxsFc);
    }
  }

  /**
   * <p>Finds (if need) line with same tax or creates one.</p>
   * @param <T> invoice type
   * @param <TL> invoice tax line type
   * @param pReqVars additional param
   * @param pInv invoice
   * @param pInvTxLns invoice tax lines
   * @param pTax tax
   * @param pNeedFind if need to find enabled
   * @param pFctInvTxLn invoice tax line factory
   * @return line
   * @throws Exception if no need to find but line is found
   **/
  public final <T extends IInvoice, TL extends AInvTxLn<T>>
    TL findCreateTaxLine(final Map<String, Object> pReqVars, final T pInv,
      final List<TL> pInvTxLns, final Tax pTax, final boolean pNeedFind,
        final IFactorySimple<TL> pFctInvTxLn) throws Exception {
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
      itl = pFctInvTxLn.create(pReqVars);
      itl.setItsOwner(pInv);
      itl.setIsNew(true);
      itl.setTax(pTax);
      pInvTxLns.add(itl);
    }
    return itl;
  }

  /**
   * <p>Simple delegator to print number.</p>
   * @param pReqVars additional param
   * @param pVal value
   * @return String
   **/
  public final String prn(final Map<String, Object> pReqVars,
    final BigDecimal pVal) {
    return this.srvNumberToString.print(pVal.toString(),
      (String) pReqVars.get("decSepv"), //TODO system preferences
        (String) pReqVars.get("decGrSepv"),
          (Integer) pReqVars.get("priceDp"),
            (Integer) pReqVars.get("digInGr"));
  }

  //Simple getters and setters:
  /**
   * <p>Getter for srvNumberToString.</p>
   * @return ISrvNumberToString
   **/
  public final ISrvNumberToString getSrvNumberToString() {
    return this.srvNumberToString;
  }

  /**
   * <p>Setter for srvNumberToString.</p>
   * @param pSrvNumberToString reference
   **/
  public final void setSrvNumberToString(
    final ISrvNumberToString pSrvNumberToString) {
    this.srvNumberToString = pSrvNumberToString;
  }

  /**
   * <p>Getter for srvDatabase.</p>
   * @return ISrvDatabase<RS>
   **/
  public final ISrvDatabase<RS> getSrvDatabase() {
    return this.srvDatabase;
  }

  /**
   * <p>Setter for srvDatabase.</p>
   * @param pSrvDatabase reference
   **/
  public final void setSrvDatabase(final ISrvDatabase<RS> pSrvDatabase) {
    this.srvDatabase = pSrvDatabase;
  }

  /**
   * <p>Geter for srvOrm.</p>
   * @return ISrvOrm<RS>
   **/
  public final ISrvOrm<RS> getSrvOrm() {
    return this.srvOrm;
  }

  /**
   * <p>Setter for srvOrm.</p>
   * @param pSrvOrm reference
   **/
  public final void setSrvOrm(final ISrvOrm<RS> pSrvOrm) {
    this.srvOrm = pSrvOrm;
  }

  /**
   * <p>Geter for logger.</p>
   * @return ILogger
   **/
  public final ILogger getLogger() {
    return this.logger;
  }

  /**
   * <p>Setter for logger.</p>
   * @param pLogger reference
   **/
  public final void setLogger(final ILogger pLogger) {
    this.logger = pLogger;
  }
}

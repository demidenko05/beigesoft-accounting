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
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.beigesoft.factory.IFactorySimple;
import org.beigesoft.exception.ExceptionWithCode;
import org.beigesoft.model.IRecordSet;
import org.beigesoft.accounting.model.ETaxType;
import org.beigesoft.accounting.model.CmprInvLnTotal;
import org.beigesoft.accounting.model.CmprTaxCatLnRate;
import org.beigesoft.accounting.persistable.base.AInvTxLn;
import org.beigesoft.accounting.persistable.base.ALineTxLn;
import org.beigesoft.accounting.persistable.base.ADestTaxItemLn;
import org.beigesoft.accounting.persistable.IInvoice;
import org.beigesoft.accounting.persistable.Tax;
import org.beigesoft.accounting.persistable.IInvoiceLine;
import org.beigesoft.accounting.persistable.SalesInvoiceServiceLine;
import org.beigesoft.accounting.persistable.InvItemTaxCategory;
import org.beigesoft.accounting.persistable.InvItemTaxCategoryLine;
import org.beigesoft.accounting.persistable.AccSettings;
import org.beigesoft.accounting.persistable.TaxDestination;

/**
 * <p>Utility for purchase/sales invoice.
 * Code dedicated to concrete invoice line type.</p>
 *
 * @param <T> invoice type
 * @param <L> invoice line type
 * @param <TL> invoice tax line type
 * @param <LTL> invoice line's tax line type
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class UtlInvoice<RS, T extends IInvoice, L extends IInvoiceLine<T>,
  TL extends AInvTxLn<T>, LTL extends ALineTxLn<T, L>> {

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
   * <p>Tables names for totals query.</p>
   **/
  private String[] tblNmsTot;

  /**
   * <p>Line's tax line class.</p>
   **/
  private final Class<LTL> ltlCl;

  /**
   * <p>Item's destination tax line class.</p>
   **/
  private final Class<? extends ADestTaxItemLn<?>> dstTxItLnCl;

  /**
   * <p>Only constructor.</p>
   * @param pLtlCl reference
   * @param pDstTxItLnCl reference
   **/
  public UtlInvoice(final Class<LTL> pLtlCl,
    final Class<? extends ADestTaxItemLn<?>> pDstTxItLnCl) {
    this.ltlCl = pLtlCl;
    this.dstTxItLnCl = pDstTxItLnCl;
  }

  /**
   * <p>Makes invoice totals tax cause line inserted/changed/deleted.</p>
   * @param pReqVars request scoped vars
   * @param pLine affected line
   * @param pAs Accounting Settings
   * @param pTxRules NULL if not taxable
   * @throws Exception - an exception.
   **/
  public final void makeTotals(final Map<String, Object> pReqVars,
    final L pLine, final List<TL> pInvTxLns, final AccSettings pAs,
      final TaxDestination pTxRules) throws Exception {
    BigDecimal txTot = BigDecimal.ZERO;
    if (pTxRules != null) {
      if (this.utlInvBase.getLogger().getIsShowDebugMessagesFor(getClass())
        && this.utlInvBase.getLogger().getDetailLevel() > 40000) {
        this.utlInvBase.getLogger().debug(pReqVars, UtlInvoice.class,
      "Tax rules: aggregate/invoice basis/zip/RM = " + pTxRules
    .getSalTaxUseAggregItBas() + "/" + pTxRules.getSalTaxIsInvoiceBase()
  + "/" + pTxRules.getRegZip() + "/" + pTxRules.getSalTaxRoundMode());
        String txCat;
        if (pLine.getTaxCategory() != null) {
          txCat = pLine.getTaxCategory().getItsName();
        } else {
          txCat = "-";
        }
        this.utlInvBase.getLogger().debug(pReqVars, UtlInvoice.class,
          "Item: name/tax category = " + pLine.getItem().getItsName() + "/"
            + txCat);
      }
      //data storage for aggregate rate
      //and non-aggregate invoice basis taxes included:
      List<SalesInvoiceServiceLine> txdLns = null;
      //data storages for non-aggregate rate
      //except invoice basis with included taxes:
      List<Tax> txs = null; //taxes
      List<Double> txTotTaxb = null; //tax's totals/taxables
      List<Double> txPerc = null; //tax's percents for invoice basis
      String query;
      if (!pTxRules.getSalTaxUseAggregItBas()
        && !(pTxRules.getSalTaxIsInvoiceBase() && pLine.getItsOwner().getPriceIncTax())) {
        //non-aggregate except invoice basis with included taxes:
        txs = new ArrayList<Tax>();
        txTotTaxb = new ArrayList<Double>();
        if (!pTxRules.getSalTaxIsInvoiceBase()) {
          //item basis:
          query = this.utlInvBase.lazyGetQuTxItBas();
        } else {
          //invoice basis, taxes excluded:
          txPerc = new ArrayList<Double>();
          query = this.utlInvBase.lazyGetQuTxInvBas();
        }
      } else { //non-aggregate invoice basis with included taxes
        //and aggregate for others:
        txdLns = new ArrayList<SalesInvoiceServiceLine>();
        if (!pTxRules.getSalTaxIsInvoiceBase()) { //item basis
          query = this.utlInvBase.lazyGetQuTxItBasAggr();
        } else { //invoice basis:
          query = this.utlInvBase.lazyGetQuTxInvBasAggr();
        }
      }
      query = query.replace(":INVOICEID", pLine.getItsOwner()
        .getItsId().toString());
      IRecordSet<RS> recordSet = null;
      try {
        recordSet = this.utlInvBase.getSrvDatabase().retrieveRecords(query);
        if (recordSet.moveToFirst()) {
          do {
            Long txId = recordSet.getLong("TAXID");
            String txNm = recordSet.getString("TAXNAME");
            Tax tax = new Tax();
            tax.setItsId(txId);
            tax.setItsName(txNm);
            if (!pTxRules.getSalTaxUseAggregItBas()
              && !(pTxRules.getSalTaxIsInvoiceBase() && pLine.getItsOwner().getPriceIncTax())) {
              //non-aggregate except invoice basis with included taxes:
              txs.add(tax);
              if (!pTxRules.getSalTaxIsInvoiceBase()) {
                //item basis, taxes excluded/included:
                txTotTaxb.add(recordSet.getDouble("TOTALTAX"));
              } else {
                //invoice basis, taxes excluded:
                txPerc.add(recordSet.getDouble("ITSPERCENTAGE"));
                txTotTaxb.add(recordSet.getDouble("SUBTOTAL"));
              }
            } else { //non-aggregate invoice basis with included taxes
              //and aggregate for others:
              Double percent = recordSet.getDouble("ITSPERCENTAGE");
              Long tcId = recordSet.getLong("TAXCATID");
              if (!pTxRules.getSalTaxIsInvoiceBase()) { //item basis:
                Long clId = recordSet.getLong("CLID");
                SalesInvoiceServiceLine txdLn = this.utlInvBase
                  .makeTxdLine(txdLns, clId, tcId, tax, percent, pAs);
                txdLn.setTotalTaxes(BigDecimal.valueOf(recordSet
                  .getDouble("TOTALTAXES"))
                    .setScale(pAs.getPricePrecision(), RoundingMode.HALF_UP));
              } else { //invoice basis:
                SalesInvoiceServiceLine txdLn = this.utlInvBase
                  .makeTxdLine(txdLns, tcId, tcId, tax, percent,
                  pAs);
                txdLn.setItsTotal(BigDecimal.valueOf(recordSet
                  .getDouble("ITSTOTAL"))
                    .setScale(pAs.getPricePrecision(), RoundingMode.HALF_UP));
                txdLn.setSubtotal(BigDecimal.valueOf(recordSet
                  .getDouble("SUBTOTAL"))
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
      if (!pTxRules.getSalTaxUseAggregItBas()
        && !(pTxRules.getSalTaxIsInvoiceBase() && pLine.getItsOwner().getPriceIncTax())) {
        //non-aggregate except invoice basis with included taxes:
        for (int i = 0; i < txs.size(); i++) {
          TL ctl = findCreateTaxLine(pReqVars, pLine.getItsOwner(),
            pInvTxLns, txs.get(i), false);
          Double txTotd;
          if (!pTxRules.getSalTaxIsInvoiceBase()) {
            //item basis, taxes excluded/included:
            txTotd = txTotTaxb.get(i);
          } else {
            //invoice basis, taxes excluded:
            txTotd = txTotTaxb.get(i) * txPerc.get(i) / 100.0;
            ctl.setTaxableInvBas(BigDecimal.valueOf(txTotTaxb.get(i)));
          }
          ctl.setItsTotal(BigDecimal.valueOf(txTotd).setScale(pAs.
            getPricePrecision(), pTxRules.getSalTaxRoundMode()));
          if (ctl.getIsNew()) {
            this.utlInvBase.getSrvOrm().insertEntity(pReqVars, ctl);
            ctl.setIsNew(false);
          } else {
            this.utlInvBase.getSrvOrm().updateEntity(pReqVars, ctl);
          }
        }
      } else { //non-aggregate invoice basis with included taxes
        //and aggregate for others:
        BigDecimal bd100 = new BigDecimal("100.00");
        Comparator<InvItemTaxCategoryLine> cmpr = Collections
          .reverseOrder(new CmprTaxCatLnRate());
        for (SalesInvoiceServiceLine txdLn : txdLns) {
          int ti = 0;
          //aggregate rate line scoped storages:
          BigDecimal taxAggegated = null;
          BigDecimal taxAggrAccum = BigDecimal.ZERO;
          Collections.sort(txdLn.getTaxCategory().getTaxes(), cmpr);
          for (InvItemTaxCategoryLine itcl : txdLn.getTaxCategory().getTaxes()) {
            ti++;
            if (taxAggegated == null) {
              if (pTxRules.getSalTaxIsInvoiceBase()) { //invoice basis:
                if (pLine.getItsOwner().getPriceIncTax()) {
                  taxAggegated = txdLn.getItsTotal().subtract(txdLn.getItsTotal().divide(
                BigDecimal.ONE.add(txdLn.getTaxCategory().getAggrOnlyPercent().divide(
              bd100)), pAs.getPricePrecision(), pTxRules.getSalTaxRoundMode()));
                } else {
                  taxAggegated = txdLn.getSubtotal().multiply(txdLn.getTaxCategory()
                .getAggrOnlyPercent()).divide(bd100, pAs.getPricePrecision(),
              pTxRules.getSalTaxRoundMode());
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
            TL ctl = findCreateTaxLine(pReqVars, pLine.getItsOwner(),
              pInvTxLns, itcl.getTax(), true);
            ctl.setItsTotal(ctl.getItsTotal().add(txdLn.getTotalTaxes()));
            if (pTxRules.getSalTaxIsInvoiceBase()) {
              if (!pLine.getItsOwner().getPriceIncTax()) {
                ctl.setTaxableInvBas(ctl.getTaxableInvBas().add(txdLn.getSubtotal()));
              } else {
                ctl.setTaxableInvBas(ctl.getTaxableInvBas().add(txdLn.getItsTotal()));
              }
            }
            if (ctl.getIsNew()) {
              this.utlInvBase.getSrvOrm().insertEntity(pReqVars, ctl);
              ctl.setIsNew(false);
            } else {
              this.utlInvBase.getSrvOrm().updateEntity(pReqVars, ctl);
            }
          }
        }
      }
    }
    this.utlInvBase.makeTotals(pReqVars, pLine.getItsOwner(), this.tblNmsTot);
  }

  /**
   * <p>Makes invoice line taxes, totals.</p>
   * @param pReqVars request scoped vars
   * @param pLine cart line
   * @param pAs Accounting Settings
   * @param pTs TradingSettings
   * @param pTxRules NULL if not taxable
   * @param pIsMutable if line editable, good don't.
   * @throws Exception - an exception.
   **/
  public final void makeLine(final Map<String, Object> pReqVars,
    final L pLine, final AccSettings pAs, final TaxDestination pTxRules,
      final Boolean pIsMutable) throws Exception {
    List<LTL> itls = null;
    if (pTxRules != null) {
      pLine.setTaxCategory(pLine.getItem().getTaxCategory());
      if (pLine.getItsOwner().getCustomer().getTaxDestination() != null) {
        //override tax method:
        pReqVars.put(this.dstTxItLnCl.getSimpleName() + "itsOwnerdeepLevel", 1);
        List<ADestTaxItemLn<?>> dtls = (List<ADestTaxItemLn<?>>) this.utlInvBase
          .getSrvOrm().retrieveListWithConditions(pReqVars, this.dstTxItLnCl,
            "where ITSOWNER=" + pLine.getItem().getItsId());
        pReqVars.remove(this.dstTxItLnCl.getSimpleName() + "itsOwnerdeepLevel");
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
    if (pLine.getTaxCategory() != null) {
      if (!pTxRules.getSalTaxIsInvoiceBase()) {
        BigDecimal totTxs = BigDecimal.ZERO;
        BigDecimal totTxsFc = BigDecimal.ZERO;
        BigDecimal bd100 = new BigDecimal("100.00");
        if (!pTxRules.getSalTaxUseAggregItBas()) {
          itls = new ArrayList<LTL>();
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
        } else {
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
        }
        if (pLine.getTotalTaxes().compareTo(totTxs) != 0) {
          if (pLine.getDescription() == null) {
            pLine.setDescription(pLine.getTotalTaxes().toString() + "!="
              + totTxs + "!");
          } else {
            pLine.setDescription(pLine.getDescription() + " " + pLine
              .getTotalTaxes().toString() + "!=" + totTxs + "!");
          }
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
    //invoice basis - lines tax, subt, tot will be adjusted later!
    if (!pLine.getItsOwner().getPriceIncTax()) {
      pLine.setItsTotal(pLine.getSubtotal().add(pLine.getTotalTaxes()));
      if (pLine.getItsOwner().getForeignCurrency() != null) {
        pLine.setForeignTotal(pLine.getForeignSubtotal()
          .add(pLine.getForeignTotalTaxes()));
      }
    } else {
      pLine.setSubtotal(pLine.getItsTotal().subtract(pLine.getTotalTaxes()));
      if (pLine.getItsOwner().getForeignCurrency() != null) {
        pLine.setForeignSubtotal(pLine.getForeignSubtotal()
          .subtract(pLine.getForeignTotalTaxes()));
      }
    }
    List<LTL> itlsr = null;
    if (pIsMutable && !pLine.getIsNew() && itls != null) {
      pReqVars.put(this.ltlCl.getSimpleName() + "itsOwnerdeepLevel", 1);
      itlsr = this.utlInvBase.getSrvOrm().retrieveListWithConditions(pReqVars,
        this.ltlCl, "where ITSOWNER=" + pLine.getItsId());
      pReqVars.remove(this.ltlCl.getSimpleName() + "itsOwnerdeepLevel");
    }
    if (itls != null && itls.size() > 0) {
      for (int i = 0; i < itls.size(); i++) {
        if (itlsr != null && i < itlsr.size()) {
          itlsr.get(i).setTax(itls.get(i).getTax());
          itlsr.get(i).setItsTotal(itls.get(i).getItsTotal());
          itlsr.get(i).setItsOwner(pLine);
          this.utlInvBase.getSrvOrm().updateEntity(pReqVars, itlsr.get(i));
        } else {
          itls.get(i).setItsOwner(pLine);
          itls.get(i).setInvoiceId(pLine.getItsOwner().getItsId());
          this.utlInvBase.getSrvOrm().insertEntity(pReqVars, itls.get(i));
          itls.get(i).setIsNew(false);
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
    if (pLine.getIsNew()) {
      this.utlInvBase.getSrvOrm().insertEntity(pReqVars, pLine);
    } else {
      this.utlInvBase.getSrvOrm().updateEntity(pReqVars, pLine);
    }
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
    //find enabled line to add amount
    for (TL tl : pInvTxLns) {
      if (tl.getTax().getItsId().equals(pTax.getItsId())) {
        if (!pNeedFind) {
          throw new Exception("Algorithm error!!!");
        }
        itl = tl;
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

  //Simple getters and setters:
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
   * <p>Getter for dstTxItLnCl.</p>
   * @return Class<?>
   **/
  public final Class<? extends ADestTaxItemLn<?>> getDstTxItLnCl() {
    return this.dstTxItLnCl;
  }
}

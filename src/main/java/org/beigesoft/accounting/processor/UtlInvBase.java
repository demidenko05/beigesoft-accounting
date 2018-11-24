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
import java.math.RoundingMode;

import org.beigesoft.log.ILogger;
import org.beigesoft.model.IRecordSet;
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.service.ISrvDatabase;
import org.beigesoft.service.ISrvNumberToString;
import org.beigesoft.accounting.model.CmprInvLnTotal;
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

  /**
   * <p>Makes invoice line taxes, totals.</p>
   * @param <T> invoice type
   * @param <L> invoice line type
   * @param pReqVars request scoped vars
   * @param pLine invoice line
   * @param pAs Accounting Settings
   * @param pTxRules NULL if not taxable
   * @param pMakerLn invoice line taxes item basis non-aggregate maker
   * @throws Exception - an exception.
   **/
  public final <T extends IInvoice, L extends IInvoiceLine<T>> void makeLine(
    final Map<String, Object> pReqVars, final L pLine, final AccSettings pAs,
      final TaxDestination pTxRules,
        final IMakerLn<T, L> pMakerLn) throws Exception {
    if (pTxRules != null) {
      pLine.setTaxCategory(pLine.getItem().getTaxCategory());
      if (pLine.getItsOwner().getCustomer().getTaxDestination() != null) {
        //override tax method:
        pReqVars.put(pMakerLn.getDstTxItLnCl().getSimpleName()
          + "itsOwnerdeepLevel", 1);
        List<ADestTaxItemLn<?>> dtls = (List<ADestTaxItemLn<?>>) getSrvOrm()
          .retrieveListWithConditions(pReqVars, pMakerLn.getDstTxItLnCl(),
            "where ITSOWNER=" + pLine.getItem().getItsId());
        pReqVars.remove(pMakerLn.getDstTxItLnCl().getSimpleName()
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
    if (pLine.getTaxCategory() != null) {
      if (!pTxRules.getSalTaxIsInvoiceBase()) {
        BigDecimal totTxs = BigDecimal.ZERO;
        BigDecimal totTxsFc = BigDecimal.ZERO;
        BigDecimal bd100 = new BigDecimal("100.00");
        if (!pTxRules.getSalTaxUseAggregItBas()) {
          pMakerLn.mkLnTxItBasNonAggr(pReqVars, pLine, pAs, pTxRules);
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
        if (pMakerLn.getIsTxByUser()) {
          if (pLine.getItsOwner().getForeignCurrency() == null) {
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
            pLine.setTotalTaxes(totTxs);
            if (pLine.getForeignTotalTaxes().compareTo(totTxsFc) != 0) {
              if (pLine.getDescription() == null) {
                pLine.setDescription(pLine.getForeignTotalTaxes().toString()
                  + "!=" + totTxsFc + "!");
              } else {
                pLine.setDescription(pLine.getDescription() + " " + pLine
                  .getForeignTotalTaxes().toString() + "!=" + totTxsFc + "!");
              }
            }
          }
        } else {
          pLine.setTotalTaxes(totTxs);
          pLine.setForeignTotalTaxes(totTxsFc);
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
    if (pLine.getIsNew()) {
      getSrvOrm().insertEntity(pReqVars, pLine);
    } else {
      getSrvOrm().updateEntity(pReqVars, pLine);
    }
  }

  /**
   * <p>Retrieve from database bundle of tax data.</p>
   * @param <T> invoice type
   * @param <L> invoice line type
   * @param pReqVars request scoped vars
   * @param pLine affected line
   * @param pAs Accounting Settings
   * @param pTxRules taxable rules
   * @param pMakerLn invoice line taxes item basis non-aggregate maker
   * @return taxes data
   * @throws Exception - an exception.
   **/
  public final <T extends IInvoice, L extends IInvoiceLine<T>>
    DataTx retrieveDataTx(final Map<String, Object> pReqVars,
      final L pLine, final AccSettings pAs, final TaxDestination pTxRules,
        final IMakerLn<T, L> pMakerLn) throws Exception {
    DataTx dtTx = new DataTx();
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
    }
    String query;
    if (!pTxRules.getSalTaxUseAggregItBas() && !(pTxRules
      .getSalTaxIsInvoiceBase() && pLine.getItsOwner().getPriceIncTax())) {
      //non-aggregate except invoice basis with included taxes:
      dtTx.setTxs(new ArrayList<Tax>());
      dtTx.setTxTotTaxb(new ArrayList<Double>());
      if (!pTxRules.getSalTaxIsInvoiceBase()) {
        //item basis:
        query = pMakerLn.lazyGetQuTxItBas();
      } else {
        //invoice basis, taxes excluded:
        dtTx.setTxPerc(new ArrayList<Double>());
        query = pMakerLn.lazyGetQuTxInvBas();
      }
    } else { //non-aggregate invoice basis with included taxes
      //and aggregate for others:
      dtTx.setTxdLns(new ArrayList<SalesInvoiceServiceLine>());
      if (!pTxRules.getSalTaxIsInvoiceBase()) { //item basis
        query = pMakerLn.lazyGetQuTxItBasAggr();
      } else { //invoice basis:
        query = pMakerLn.lazyGetQuTxInvBasAggr();
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
            dtTx.getTxs().add(tax);
            if (!pTxRules.getSalTaxIsInvoiceBase()) {
              //item basis, taxes excluded/included:
              dtTx.getTxTotTaxb().add(recordSet.getDouble("TOTALTAX"));
            } else {
              //invoice basis, taxes excluded:
              dtTx.getTxPerc().add(recordSet.getDouble("ITSPERCENTAGE"));
              dtTx.getTxTotTaxb().add(recordSet.getDouble("SUBTOTAL"));
            }
          } else { //non-aggregate invoice basis with included taxes
            //and aggregate for others:
            Double percent = recordSet.getDouble("ITSPERCENTAGE");
            Long tcId = recordSet.getLong("TAXCATID");
            if (!pTxRules.getSalTaxIsInvoiceBase()) { //item basis:
              Long clId = recordSet.getLong("CLID");
              SalesInvoiceServiceLine txdLn = makeTxdLine(dtTx.getTxdLns(),
                clId, tcId, tax, percent, pAs);
              txdLn.setTotalTaxes(BigDecimal.valueOf(recordSet
                .getDouble("TOTALTAXES"))
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
   * <p>Adjust invoice lines totals/subtotals/cost for invoice basis.</p>
   * @param <T> invoice type
   * @param pReqVars additional param
   * @param pInv invoice
   * @param pTxdLns Tax Data lines
   * @param pAs AS
   * @param pMakerLn invoice maker
   * @throws Exception an Exception
   **/
  public final <T extends IInvoice> void adjustInvoiceLns(
    final Map<String, Object> pReqVars, final T pInv,
      final List<SalesInvoiceServiceLine> pTxdLns, final AccSettings pAs,
        final IMakerLn<T, ?> pMakerLn) throws Exception {
    String tbNm = pMakerLn.getGoodLnCl().getSimpleName();
    pReqVars.put(tbNm + "itsOwnerdeepLevel", 1);
    List<IInvoiceLine<T>> gls = getSrvOrm().retrieveListWithConditions(pReqVars,
      pMakerLn.getGoodLnCl(), "where " + tbNm.toUpperCase()
        + ".TAXCATEGORY is not null and ITSOWNER=" + pInv.getItsId());
    pReqVars.remove(tbNm + "itsOwnerdeepLevel");
    List<IInvoiceLine<T>> sls = null;
    if (pMakerLn.getServiceLnCl() != null) {
      tbNm = pMakerLn.getServiceLnCl().getSimpleName();
      pReqVars.put(tbNm + "itsOwnerdeepLevel", 1);
      sls = getSrvOrm().retrieveListWithConditions(pReqVars,
        pMakerLn.getGoodLnCl(), "where " + tbNm.toUpperCase()
          + ".TAXCATEGORY is not null and ITSOWNER=" + pInv.getItsId());
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
      for (int i = 0; i < ilsm.size(); i++) {
        if (i + 1 == ilsm.size()) {
          if (pInv.getPriceIncTax()) {
            ilsm.get(i).setSubtotal(ilsm.get(i).getItsTotal().subtract(txRest));
          } else {
            ilsm.get(i).setItsTotal(ilsm.get(i).getSubtotal().add(txRest));
          }
          ilsm.get(i).setTotalTaxes(txRest);
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
        }
        getSrvOrm().updateEntity(pReqVars, ilsm.get(i));
      }
      ilsm.clear();
    }
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

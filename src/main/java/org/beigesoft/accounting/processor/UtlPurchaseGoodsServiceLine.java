package org.beigesoft.accounting.processor;

/*
 * Copyright (c) 2017 Beigesoft™
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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.beigesoft.log.ILogger;
import org.beigesoft.exception.ExceptionWithCode;
import org.beigesoft.model.IRecordSet;
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.service.ISrvDatabase;
import org.beigesoft.accounting.persistable.AccSettings;
import org.beigesoft.accounting.persistable.PurchaseInvoice;
import org.beigesoft.accounting.persistable.PurchaseInvoiceLine;
import org.beigesoft.accounting.persistable.PurchaseInvoiceServiceLine;
import org.beigesoft.accounting.persistable.PurchaseInvoiceTaxLine;
import org.beigesoft.accounting.persistable.Tax;
import org.beigesoft.accounting.persistable.InvItemTaxCategory;
import org.beigesoft.accounting.persistable.InvItemTaxCategoryLine;
import org.beigesoft.accounting.service.ISrvAccSettings;

/**
 * <p>Utility for Vendor Invoice Goods/Service Line.
 * It makes total for owner.</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class UtlPurchaseGoodsServiceLine<RS> {

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
   * <p>File with Query Vendor Invoice Taxes item basis method
   * aggregate tax rate.</p>
   **/
  private String fileQuPurInvSaTaxItBasAggr = "purchInvSalTaxItBasAggr.sql";

  /**
   * <p>Query Vendor Invoice Taxes item basis method
   * aggregate tax rate.</p>
   **/
  private String quPurInvSaTaxItBasAggr;

  /**
   * <p>File with Query Vendor Invoice Taxes item basis method.</p>
   **/
  private String fileQuPurInvSaTaxItBas = "purchInvSalTaxItemBasis.sql";

  /**
   * <p>Query Vendor Invoice Taxes item basis method.</p>
   **/
  private String quPurInvSaTaxItBas;

  /**
   * <p>File with Query Vendor Invoice Taxes invoice basis method.</p>
   **/
  private String fileQuPurInvSaTaxInvBas = "purchInvSalTaxInvBasis.sql";

  /**
   * <p>Query Vendor Invoice Taxes invoice basis method.</p>
   **/
  private String quPurInvSaTaxInvBas;

  /**
   * <p>File with Query Vendor Invoice Totals.</p>
   **/
  private String fileQueryPurchaseInvoiceTotals = "purchaseInvoiceTotals.sql";

  /**
   * <p>Query Vendor Invoice Totals.</p>
   **/
  private String queryPurchaseInvoiceTotals;

  /**
   * <p>Business service for accounting settings.</p>
   **/
  private ISrvAccSettings srvAccSettings;

  /**
   * <p>Updates owner taxes and totals.</p>
   * @param pReqVars additional param
   * @param pItsOwner PurchaseInvoice
   * @throws Exception - an exception
   **/
  public final void updateOwner(final Map<String, Object> pReqVars,
    final PurchaseInvoice pItsOwner) throws Exception {
    updateTaxLines(pReqVars, pItsOwner);
    updateOwnerTotals(pReqVars, pItsOwner);
  }

  /**
   * <p>Updates owners totals.</p>
   * @param pReqVars additional param
   * @param pItsOwner PurchaseInvoice
   * @throws Exception - an exception
   **/
  public final void updateOwnerTotals(final Map<String, Object> pReqVars,
    final PurchaseInvoice pItsOwner) throws Exception {
    String query = lazyGetQueryPurchaseInvoiceTotals();
    query = query.replace(":ITSOWNER", pItsOwner.getItsId().toString());
    String[] columns = new String[] {"SUBTOTAL", "ITSTOTAL", "TOTALTAXES",
      "FOREIGNSUBTOTAL", "FOREIGNTOTAL", "FOREIGNTOTALTAXES"};
    Double[] totals = getSrvDatabase().evalDoubleResults(query, columns);
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
    AccSettings as = getSrvAccSettings().lazyGetAccSettings(pReqVars);
    if (pItsOwner.getPriceIncTax()) {
      pItsOwner.setItsTotal(BigDecimal.valueOf(totals[1]).setScale(
        as.getPricePrecision(), as.getRoundingMode()));
      pItsOwner.setTotalTaxes(BigDecimal.valueOf(totals[2]).setScale(
        as.getPricePrecision(), as.getSalTaxRoundMode()));
      pItsOwner.setSubtotal(pItsOwner.getItsTotal().
        subtract(pItsOwner.getTotalTaxes()));
      pItsOwner.setForeignTotal(BigDecimal.valueOf(totals[4]).setScale(
        as.getPricePrecision(), as.getRoundingMode()));
      pItsOwner.setForeignTotalTaxes(BigDecimal.valueOf(totals[5]).setScale(
        as.getPricePrecision(), as.getSalTaxRoundMode()));
      pItsOwner.setForeignSubtotal(pItsOwner.getForeignTotal().
        subtract(pItsOwner.getForeignTotalTaxes()));
    } else {
      pItsOwner.setSubtotal(BigDecimal.valueOf(totals[0]).setScale(
        as.getPricePrecision(), as.getRoundingMode()));
      pItsOwner.setTotalTaxes(BigDecimal.valueOf(totals[2]).setScale(
        as.getPricePrecision(), as.getSalTaxRoundMode()));
      pItsOwner.setItsTotal(pItsOwner.getSubtotal().
        add(pItsOwner.getTotalTaxes()));
      pItsOwner.setForeignSubtotal(BigDecimal.valueOf(totals[3]).setScale(
        as.getPricePrecision(), as.getRoundingMode()));
      pItsOwner.setForeignTotalTaxes(BigDecimal.valueOf(totals[5]).setScale(
        as.getPricePrecision(), as.getSalTaxRoundMode()));
      pItsOwner.setForeignTotal(pItsOwner.getForeignSubtotal().
        add(pItsOwner.getForeignTotalTaxes()));
    }
    getSrvOrm().updateEntity(pReqVars, pItsOwner);
  }

  /**
   * <p>Lazy get quPurInvSaTaxItBasAggr.</p>
   * @return quPurInvSaTaxItBasAggr
   * @throws Exception - an exception
   **/
  public final String lazyGetQuPurchInvSalTaxItBasAggr() throws Exception {
    if (this.quPurInvSaTaxItBasAggr == null) {
      String flName = "/accounting/trade/" + this.fileQuPurInvSaTaxItBasAggr;
      this.quPurInvSaTaxItBasAggr = loadString(flName);
    }
    return this.quPurInvSaTaxItBasAggr;
  }

  /**
   * <p>Lazy get quPurInvSaTaxItBas.</p>
   * @return quPurInvSaTaxItBas
   * @throws Exception - an exception
   **/
  public final String lazyGetQuPurchInvSalTaxItBas() throws Exception {
    if (this.quPurInvSaTaxItBas == null) {
      String flName = "/accounting/trade/" + this.fileQuPurInvSaTaxItBas;
      this.quPurInvSaTaxItBas = loadString(flName);
    }
    return this.quPurInvSaTaxItBas;
  }

  /**
   * <p>Lazy get quPurInvSaTaxInvBas.</p>
   * @return quPurInvSaTaxInvBas
   * @throws Exception - an exception
   **/
  public final String lazyGetQuPurchInvSalTaxInvBas() throws Exception {
    if (this.quPurInvSaTaxInvBas == null) {
      String flName = "/accounting/trade/" + this.fileQuPurInvSaTaxInvBas;
      this.quPurInvSaTaxInvBas = loadString(flName);
    }
    return this.quPurInvSaTaxInvBas;
  }

  /**
   * <p>Lazy get queryPurchaseInvoiceTotals.</p>
   * @return queryPurchaseInvoiceTotals
   * @throws Exception - an exception
   **/
  public final String lazyGetQueryPurchaseInvoiceTotals() throws Exception {
    if (this.queryPurchaseInvoiceTotals == null) {
      String flName = "/accounting/trade/"
        + this.fileQueryPurchaseInvoiceTotals;
      this.queryPurchaseInvoiceTotals = loadString(flName);
    }
    return this.queryPurchaseInvoiceTotals;
  }

  /**
   * <p>Load string file (usually SQL query).</p>
   * @param pFileName file name
   * @return String usually SQL query
   * @throws IOException - IO exception
   **/
  public final String loadString(final String pFileName)
        throws IOException {
    URL urlFile = UtlPurchaseGoodsServiceLine.class
      .getResource(pFileName);
    if (urlFile != null) {
      InputStream inputStream = null;
      try {
        inputStream = UtlPurchaseGoodsServiceLine.class
          .getResourceAsStream(pFileName);
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

  /**
   * <p>Update invoice Tax Lines.</p>
   * @param pReqVars additional param
   * @param pItsOwner Owner
   * @throws Exception - an exception
   **/
  public final void updateTaxLines(final Map<String, Object> pReqVars,
    final PurchaseInvoice pItsOwner) throws Exception {
    pReqVars.put("PurchaseInvoiceTaxLineitsOwnerdeepLevel", 1);
    List<PurchaseInvoiceTaxLine> itls = getSrvOrm().retrieveListWithConditions(
        pReqVars, PurchaseInvoiceTaxLine.class, "where ITSOWNER="
          + pItsOwner.getItsId());
    pReqVars.remove("PurchaseInvoiceTaxLineitsOwnerdeepLevel");
    boolean isShowDebug = getLogger().getIsShowDebugMessagesFor(getClass());
    int dbgDetLev = getLogger().getDetailLevel();
    AccSettings as = getSrvAccSettings().lazyGetAccSettings(pReqVars);
    boolean isTaxable = as.getIsExtractSalesTaxFromPurchase() && !pItsOwner
      .getOmitTaxes() && !pItsOwner.getVendor().getIsForeigner();
    if (isTaxable) {
      boolean isItemBasis = !as.getSalTaxIsInvoiceBase();
      boolean isAggrOnlyRate = as.getSalTaxUseAggregItBas();
      RoundingMode rm = as.getSalTaxRoundMode();
      if (pItsOwner.getVendor().getTaxDestination() != null) {
        //override tax method:
        isItemBasis = !pItsOwner.getVendor()
          .getTaxDestination().getSalTaxIsInvoiceBase();
        isAggrOnlyRate = pItsOwner.getVendor()
          .getTaxDestination().getSalTaxUseAggregItBas();
        rm = pItsOwner.getVendor()
          .getTaxDestination().getSalTaxRoundMode();
      }
      if (pItsOwner.getPriceIncTax() && !isAggrOnlyRate) {
        throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
          "price_inc_tax_multi_not_imp");
      }
      if (isShowDebug && dbgDetLev > 30000) {
        getLogger().debug(null, UtlPurchaseGoodsServiceLine.class,
      "Updating tax lines for invoice #/date/isItemBasis/isAggrOnlyRate/rm: "
        + pItsOwner.getItsId() + "/" + pItsOwner.getItsDate() + "/"
          + isItemBasis + "/" + isAggrOnlyRate + "/" + rm);
      }
      String query;
      if (!isItemBasis) {
        query = lazyGetQuPurchInvSalTaxInvBas();
      } else if (isAggrOnlyRate) {
        query = lazyGetQuPurchInvSalTaxItBasAggr();
      } else {
        query = lazyGetQuPurchInvSalTaxItBas();
      }
      query = query.replace(":INVOICEID", pItsOwner.getItsId().toString());
      IRecordSet<RS> recordSet = null;
      //lines (goods and services) to store data for item basis aggregate rate
      //and invoice basis:
      List<PurchaseInvoiceServiceLine> invLns =
        new ArrayList<PurchaseInvoiceServiceLine>();
      //data storages for item basis with non-aggregate rate:
      List<Long> taxesLst = new ArrayList<Long>();
      List<Double> dbResults = new ArrayList<Double>();
      try {
        recordSet = getSrvDatabase().retrieveRecords(query);
        if (recordSet.moveToFirst()) {
          do {
            if (!isItemBasis) {
              //List<PurchaseInvoiceServiceLine> used also
              //to make subtotal/total/cost for invoice lines
              Long taxId = recordSet.getLong("TAXID");
              Double percent = recordSet.getDouble("ITSPERCENTAGE");
              Long ilId = recordSet.getLong("TAXCATID");
              if (pItsOwner.getPriceIncTax()) { //&& aggregate/only rate
                PurchaseInvoiceServiceLine invLn = makeLine(invLns, ilId,
                  ilId, taxId, percent, as);
                invLn.setItsTotal(BigDecimal.valueOf(recordSet
                  .getDouble("ITSTOTAL"))
                    .setScale(as.getPricePrecision(), RoundingMode.HALF_UP));
                invLn.setForeignTotal(BigDecimal.valueOf(recordSet
                  .getDouble("FOREIGNTOTAL"))
                    .setScale(as.getPricePrecision(), RoundingMode.HALF_UP));
              } else { //any rate
                PurchaseInvoiceServiceLine invLn = makeLine(invLns, ilId,
                  ilId, taxId, percent, as);
                invLn.setSubtotal(BigDecimal.valueOf(recordSet
                  .getDouble("SUBTOTAL"))
                    .setScale(as.getPricePrecision(), RoundingMode.HALF_UP));
                invLn.setForeignTotal(BigDecimal.valueOf(recordSet
                  .getDouble("FOREIGNSUBTOTAL"))
                    .setScale(as.getPricePrecision(), RoundingMode.HALF_UP));
              }
            } else {
              if (isAggrOnlyRate) { //any tax including
                Long ilId = recordSet.getLong("ILID");
                Long taxId = recordSet.getLong("TAXID");
                Double percent = recordSet.getDouble("ITSPERCENTAGE");
                PurchaseInvoiceServiceLine invLn = makeLine(invLns, ilId, ilId,
                  taxId, percent, as);
                invLn.setTotalTaxes(BigDecimal.valueOf(recordSet
                  .getDouble("TOTALTAXES"))
                    .setScale(as.getPricePrecision(), RoundingMode.HALF_UP));
                invLn.setForeignTotalTaxes(BigDecimal.valueOf(recordSet
                  .getDouble("FOREIGNTOTALTAXES"))
                    .setScale(as.getPricePrecision(), RoundingMode.HALF_UP));
              } else { //tax excluded
                taxesLst.add(recordSet.getLong("TAXID"));
                dbResults.add(recordSet.getDouble("TOTALTAX"));
                dbResults.add(recordSet.getDouble("FOREIGNTOTALTAXES"));
              }
            }
          } while (recordSet.moveToNext());
        }
      } finally {
        if (recordSet != null) {
          recordSet.close();
        }
      }
      if (invLns.size() > 0 && taxesLst.size() >  0) {
        throw new Exception("Algorithm error!!!");
      }
      if (!isItemBasis & invLns.size() > 0) {
        Set<Long> taxIds = new HashSet<Long>();
        for (PurchaseInvoiceServiceLine invLn : invLns) {
          for (InvItemTaxCategoryLine itcl : invLn.getTaxCategory()
            .getTaxes()) {
            if (taxIds.contains(itcl.getTax().getItsId())) {
              throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
                "invoice_basis_same_taxes_with_different_tax_cat");
            }
            taxIds.add(itcl.getTax().getItsId());
          }
        }
      }
      if (itls.size() > 0) {
        for (PurchaseInvoiceTaxLine itl : itls) {
          itl.setTax(null);
          itl.setTaxableInvBas(BigDecimal.ZERO);
          itl.setTaxableInvBasFc(BigDecimal.ZERO);
          itl.setItsTotal(BigDecimal.ZERO);
          itl.setForeignTotalTaxes(BigDecimal.ZERO);
        }
      }
      List<PurchaseInvoiceTaxLine> itlsnew = null;
      if (!(isItemBasis && !isAggrOnlyRate)) {
        itlsnew = new ArrayList<PurchaseInvoiceTaxLine>();
      }
      pReqVars.put("countUpdatedItl", Integer.valueOf(0));
      if (invLns.size() > 0) {
        BigDecimal bd100 = new BigDecimal("100.00");
        for (PurchaseInvoiceServiceLine invLn : invLns) {
          int ti = 0;
          BigDecimal invBasTaxExclTaxTot = null;
          BigDecimal invBasTaxExclTaxTotFc = null;
          BigDecimal taxAggegated = null;
          BigDecimal taxAggegatedFc = null;
          BigDecimal taxAggrAccum = BigDecimal.ZERO;
          BigDecimal taxAggrAccumFc = BigDecimal.ZERO;
          for (InvItemTaxCategoryLine itcl : invLn.getTaxCategory()
            .getTaxes()) {
            ti++;
            if (taxAggegated == null) {
             if (!isItemBasis && pItsOwner.getPriceIncTax() && isAggrOnlyRate) {
               //invoice basis, aggregate/only rate, taxes included
                taxAggegated = invLn.getItsTotal().subtract(invLn
              .getItsTotal().divide(BigDecimal.ONE.add(invLn.getTaxCategory()
            .getAggrOnlyPercent().divide(bd100)), as.getPricePrecision(), rm));
                taxAggegatedFc = invLn.getForeignTotal().subtract(invLn
             .getForeignTotal().divide(BigDecimal.ONE.add(invLn.getTaxCategory()
            .getAggrOnlyPercent().divide(bd100)), as.getPricePrecision(), rm));
              } else if (isItemBasis && isAggrOnlyRate) {
               //item basis, aggregate/only rate
                taxAggegated = invLn.getTotalTaxes();
                taxAggegatedFc = invLn.getForeignTotalTaxes();
              }
            }
            if (!isItemBasis && pItsOwner.getPriceIncTax() && isAggrOnlyRate) {
             if (invLn.getTaxCategory().getTaxes().size() == 1
                || ti < invLn.getTaxCategory().getTaxes().size()) {
                invLn.setTotalTaxes(taxAggegated.multiply(itcl
              .getItsPercentage()).divide(invLn.getTaxCategory()
            .getAggrOnlyPercent(), as.getPricePrecision(), rm));
                taxAggrAccum = taxAggrAccum.add(invLn.getTotalTaxes());
                invLn.setForeignTotalTaxes(taxAggegatedFc.multiply(itcl
              .getItsPercentage()).divide(invLn.getTaxCategory()
            .getAggrOnlyPercent(), as.getPricePrecision(), rm));
                taxAggrAccumFc = taxAggrAccumFc.add(invLn
                  .getForeignTotalTaxes());
              } else {
                invLn.setTotalTaxes(taxAggegated.subtract(taxAggrAccum));
                invLn.setForeignTotalTaxes(taxAggegatedFc
                  .subtract(taxAggrAccumFc));
              }
            } else if (isItemBasis && isAggrOnlyRate) {
              if (invLn.getTaxCategory().getTaxes().size() == 1
                || ti < invLn.getTaxCategory().getTaxes().size()) {
                invLn.setTotalTaxes(taxAggegated.multiply(itcl
              .getItsPercentage()).divide(invLn.getTaxCategory()
            .getAggrOnlyPercent(), as.getPricePrecision(), rm));
                taxAggrAccum = taxAggrAccum.add(invLn.getTotalTaxes());
                invLn.setForeignTotalTaxes(taxAggegatedFc.multiply(itcl
              .getItsPercentage()).divide(invLn.getTaxCategory()
            .getAggrOnlyPercent(), as.getPricePrecision(), rm));
                taxAggrAccumFc = taxAggrAccumFc.add(invLn
                  .getForeignTotalTaxes());
              } else {
                invLn.setTotalTaxes(taxAggegated.subtract(taxAggrAccum));
                invLn.setForeignTotalTaxes(taxAggegatedFc
                  .subtract(taxAggrAccumFc));
              }
            } else if (!isItemBasis && !pItsOwner.getPriceIncTax()) {
              invBasTaxExclTaxTot = invLn.getTotalTaxes();
              invLn.setTotalTaxes(invLn.getSubtotal().multiply(itcl
                .getItsPercentage()).divide(bd100, as
                  .getPricePrecision(), rm));
              invBasTaxExclTaxTot = invBasTaxExclTaxTot
                .add(invLn.getTotalTaxes());
              invBasTaxExclTaxTotFc = invLn.getForeignTotalTaxes();
              invLn.setForeignTotalTaxes(invLn.getForeignSubtotal()
                .multiply(itcl.getItsPercentage())
                  .divide(bd100, as.getPricePrecision(), rm));
              invBasTaxExclTaxTotFc = invBasTaxExclTaxTotFc
                .add(invLn.getForeignTotalTaxes());
            } else {
              throw new Exception("Algorithm error!!!");
            }
            PurchaseInvoiceTaxLine itl = findCreateTaxLine(pReqVars, itls,
              itlsnew, itcl.getTax().getItsId());
            itl.setItsOwner(pItsOwner);
            itl.setTax(itcl.getTax());
            makeItl(pReqVars, itl, invLn, isItemBasis, pItsOwner
              .getPriceIncTax());
            if (!isItemBasis && !pItsOwner.getPriceIncTax()) {
              invLn.setTotalTaxes(invBasTaxExclTaxTot);
              invLn.setForeignTotalTaxes(invBasTaxExclTaxTotFc);
            }
          }
        }
      }
      if (taxesLst.size() >  0) {
        List<Tax> taxes = new ArrayList<Tax>();
        for (int i = 0; i < taxesLst.size(); i++) {
          //item basis, non-aggregate rate, taxes excluded
          Double totalTax = dbResults.get(i * 2);
          Double totalTaxFc = dbResults.get(i * 2 + 1);
          Tax tax = new Tax();
          tax.setItsId(taxesLst.get(i));
          taxes.add(tax);
          for (int j = 0; j < taxes.size();  j++) {
            PurchaseInvoiceTaxLine itl;
            if (!isItemBasis) {
              itl = findCreateTaxLine(pReqVars, itls,
                itlsnew, taxes.get(j).getItsId());
            } else {
              itl = findCreateTaxLine(pReqVars, itls,
                taxes.get(j).getItsId());
            }
            itl.setItsOwner(pItsOwner);
            itl.setTax(taxes.get(j));
            makeItl(pReqVars, itl, totalTax, totalTaxFc, null, null,
              as, rm);
          }
          taxes.clear();
        }
      }
      Integer countUpdatedItl = (Integer) pReqVars.get("countUpdatedItl");
      pReqVars.remove("countUpdatedItl");
      if (countUpdatedItl < itls.size()) {
        for (int j = countUpdatedItl; j < itls.size(); j++) {
          getSrvOrm().deleteEntity(pReqVars, itls.get(j));
        }
      }
      if (!isItemBasis & invLns.size() > 0) {
        //update subtotal/total/cost invoice lines:
        pReqVars.put("PurchaseInvoiceServiceLineitsOwnerdeepLevel", 1);
        List<PurchaseInvoiceServiceLine> isls = getSrvOrm().retrieveListWithConditions(
            pReqVars, PurchaseInvoiceServiceLine.class, "where ITSOWNER="
              + pItsOwner.getItsId());
        pReqVars.remove("PurchaseInvoiceServiceLineitsOwnerdeepLevel");
        pReqVars.put("PurchaseInvoiceLineitsOwnerdeepLevel", 1);
        List<PurchaseInvoiceLine> igls = getSrvOrm().retrieveListWithConditions(
            pReqVars, PurchaseInvoiceLine.class,
              "where REVERSEDID is null and ITSOWNER=" + pItsOwner.getItsId());
        pReqVars.remove("PurchaseInvoiceLineitsOwnerdeepLevel");
        for (PurchaseInvoiceServiceLine sl : invLns) {
        }
      }
    } else if (itls.size() > 0) {
      if (isShowDebug) {
        getLogger().debug(null, UtlPurchaseGoodsServiceLine.class,
          "Deleting tax lines for non-taxable invoice #"
            + pItsOwner.getItsId() + "/" + pItsOwner.getItsDate());
      }
      for (PurchaseInvoiceTaxLine itln : itls) {
        getSrvOrm().deleteEntity(pReqVars, itln);
      }
    }
  }

  /**
   * <p>Find in old/new lines or create tax line.</p>
   * @param pReqVars additional param
   * @param pTaxLnsWas lines was
   * @param pTaxLnsNew lines new
   * @param pTaxId tax ID
   * @return line
   **/
  public final PurchaseInvoiceTaxLine findCreateTaxLine(
    final Map<String, Object> pReqVars,
      final List<PurchaseInvoiceTaxLine> pTaxLnsWas,
        final List<PurchaseInvoiceTaxLine> pTaxLnsNew, final Long pTaxId) {
    PurchaseInvoiceTaxLine itl = null;
    if (pTaxLnsWas.size() > 0) {
      for (int k = 0; k < pTaxLnsWas.size(); k++) {
        if (pTaxLnsWas.get(k).getTax() != null
          && pTaxLnsWas.get(k).getTax().getItsId()
            .equals(pTaxId)) {
          itl = pTaxLnsWas.get(k);
          break;
        }
      }
    }
    if (itl == null && pTaxLnsNew.size() > 0) {
      for (int k = 0; k < pTaxLnsNew.size(); k++) {
        if (pTaxLnsNew.get(k).getTax().getItsId()
            .equals(pTaxId)) {
          itl = pTaxLnsNew.get(k);
          break;
        }
      }
    }
    if (itl == null) {
      Integer countUpdatedItl = (Integer) pReqVars.get("countUpdatedItl");
      if (pTaxLnsWas.size() > countUpdatedItl) {
        itl = pTaxLnsWas.get(countUpdatedItl);
        countUpdatedItl++;
        pReqVars.put("countUpdatedItl", countUpdatedItl);
      } else {
        itl = new PurchaseInvoiceTaxLine();
        itl.setIsNew(true);
        itl.setIdDatabaseBirth(this.srvOrm.getIdDatabase());
        pTaxLnsNew.add(itl);
      }
    }
    return itl;
  }

  /**
   * <p>Find in old lines or create tax line.</p>
   * @param pReqVars additional param
   * @param pTaxLnsWas lines was
   * @param pTaxId tax ID
   * @return line
   **/
  public final PurchaseInvoiceTaxLine findCreateTaxLine(
    final Map<String, Object> pReqVars,
      final List<PurchaseInvoiceTaxLine> pTaxLnsWas, final Long pTaxId) {
    PurchaseInvoiceTaxLine itl = null;
    if (pTaxLnsWas.size() > 0) {
      for (int k = 0; k < pTaxLnsWas.size(); k++) {
        if (pTaxLnsWas.get(k).getTax() != null
          && pTaxLnsWas.get(k).getTax().getItsId()
            .equals(pTaxId)) {
          itl = pTaxLnsWas.get(k);
          break;
        }
      }
    }
    if (itl == null) {
      Integer countUpdatedItl = (Integer) pReqVars.get("countUpdatedItl");
      if (pTaxLnsWas.size() > countUpdatedItl) {
        itl = pTaxLnsWas.get(countUpdatedItl);
        countUpdatedItl++;
        pReqVars.put("countUpdatedItl", countUpdatedItl);
      } else {
        itl = new PurchaseInvoiceTaxLine();
        itl.setIsNew(true);
        itl.setIdDatabaseBirth(this.srvOrm.getIdDatabase());
      }
    }
    return itl;
  }

  /**
   * <p>Make invoice line that stores values.</p>
   * @param pInvLns lines
   * @param pIlId line ID
   * @param pCatId tax category ID
   * @param pTaxId tax ID
   * @param pPercent tax rate
   * @param pAs AS
   * @return line
   **/
  public final PurchaseInvoiceServiceLine makeLine(
    final List<PurchaseInvoiceServiceLine> pInvLns, final Long pIlId,
      final Long pCatId,  final Long pTaxId, final Double pPercent,
        final AccSettings pAs) {
    PurchaseInvoiceServiceLine invLn = null;
    for (PurchaseInvoiceServiceLine il : pInvLns) {
      if (il.getItsId().equals(pIlId)) {
        invLn = il;
      }
    }
    if (invLn == null) {
      invLn = new PurchaseInvoiceServiceLine();
      invLn.setItsId(pIlId);
      InvItemTaxCategory tc = new InvItemTaxCategory();
      tc.setItsId(pCatId);
      tc.setTaxes(new ArrayList<InvItemTaxCategoryLine>());
      invLn.setTaxCategory(tc);
      pInvLns.add(invLn);
    }
    InvItemTaxCategoryLine itcl = new InvItemTaxCategoryLine();
    Tax tax = new Tax();
    tax.setItsId(pTaxId);
    itcl.setTax(tax);
    itcl.setItsPercentage(BigDecimal.valueOf(pPercent)
      .setScale(pAs.getTaxPrecision(), RoundingMode.HALF_UP));
    invLn.getTaxCategory().getTaxes().add(itcl);
    invLn.getTaxCategory().setAggrOnlyPercent(invLn.getTaxCategory()
      .getAggrOnlyPercent().add(itcl.getItsPercentage()));
    return invLn;
  }

  /**
   * <p>Makes invoice tax line.</p>
   * @param pReqVars additional param
   * @param pItl PurchaseInvoiceTaxLine
   * @param pInvLn inventory line
   * @param pIsItemBasis Is Item Basis
   * @param pIsPriceInclTax Is Price Inclusive Tax
   * @throws Exception an Exception
   **/
  public final void makeItl(final Map<String, Object> pReqVars,
    final PurchaseInvoiceTaxLine pItl, final PurchaseInvoiceServiceLine pInvLn,
      final boolean pIsItemBasis,
        final boolean pIsPriceInclTax) throws Exception {
    pItl.setItsTotal(pItl.getItsTotal().add(pInvLn.getTotalTaxes()));
    pItl.setForeignTotalTaxes(pItl.getForeignTotalTaxes()
      .add(pInvLn.getForeignTotalTaxes()));
    if (!pIsItemBasis) {
      if (pIsPriceInclTax) {
        pItl.setTaxableInvBas(pItl.getTaxableInvBas()
          .add(pInvLn.getItsTotal()));
        pItl.setTaxableInvBasFc(pItl.getTaxableInvBasFc()
          .add(pInvLn.getForeignTotal()));
      } else {
        pItl.setTaxableInvBas(pItl.getTaxableInvBas()
          .add(pInvLn.getSubtotal()));
        pItl.setTaxableInvBasFc(pItl.getTaxableInvBasFc()
          .add(pInvLn.getForeignSubtotal()));
      }
    }
    if (pItl.getIsNew()) {
      getSrvOrm().insertEntity(pReqVars, pItl);
      pItl.setIsNew(false);
    } else {
      getSrvOrm().updateEntity(pReqVars, pItl);
    }
  }

  /**
   * <p>Makes invoice tax line.</p>
   * @param pReqVars additional param
   * @param pItl PurchaseInvoiceTaxLine
   * @param pTotalTax Total Tax
   * @param pTotalTaxFc Total Tax in foreign currency
   * @param pTaxable Taxable
   * @param pTaxableFc Taxable in foreign currency
   * @param pAs ACC Settings
   * @param pRm rounding mode
   * @throws Exception an Exception
   **/
  public final void makeItl(final Map<String, Object> pReqVars,
    final PurchaseInvoiceTaxLine pItl, final Double pTotalTax,
      final Double pTotalTaxFc, final Double pTaxable, final Double pTaxableFc,
        final AccSettings pAs, final RoundingMode pRm) throws Exception {
    pItl.setItsTotal(pItl.getItsTotal().add(BigDecimal.valueOf(pTotalTax)
      .setScale(pAs.getPricePrecision(), pRm)));
    pItl.setForeignTotalTaxes(pItl.getForeignTotalTaxes().add(BigDecimal
      .valueOf(pTotalTaxFc).setScale(pAs.getPricePrecision(), pRm)));
    if (pTaxable != null) {
      pItl.setTaxableInvBas(pItl.getTaxableInvBas().add(BigDecimal
  .valueOf(pTaxable).setScale(pAs.getPricePrecision(), pAs.getRoundingMode())));
      pItl.setTaxableInvBasFc(pItl.getTaxableInvBasFc().add(BigDecimal
.valueOf(pTaxableFc).setScale(pAs.getPricePrecision(), pAs.getRoundingMode())));
    }
    if (pItl.getIsNew()) {
      getSrvOrm().insertEntity(pReqVars, pItl);
      pItl.setIsNew(false);
    } else {
      getSrvOrm().updateEntity(pReqVars, pItl);
    }
  }

  //Simple getters and setters:
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
   * <p>Getter for fileQuPurInvSaTaxItBasAggr.</p>
   * @return String
   **/
  public final String getFileQuPurInvSaTaxItBasAggr() {
    return this.fileQuPurInvSaTaxItBasAggr;
  }

  /**
   * <p>Setter for fileQuPurInvSaTaxItBasAggr.</p>
   * @param pFileQuPurInvSaTaxItBasAggr reference
   **/
  public final void setFileQuPurInvSaTaxItBasAggr(
    final String pFileQuPurInvSaTaxItBasAggr) {
    this.fileQuPurInvSaTaxItBasAggr = pFileQuPurInvSaTaxItBasAggr;
  }

  /**
   * <p>Setter for quPurInvSaTaxItBasAggr.</p>
   * @param pQuPurInvSaTaxItBasAggr reference
   **/
  public final void setQuPurInvSaTaxItBasAggr(
    final String pQuPurInvSaTaxItBasAggr) {
    this.quPurInvSaTaxItBasAggr = pQuPurInvSaTaxItBasAggr;
  }

  /**
   * <p>Getter for fileQuPurInvSaTaxItBas.</p>
   * @return String
   **/
  public final String getFileQuPurInvSaTaxItBas() {
    return this.fileQuPurInvSaTaxItBas;
  }

  /**
   * <p>Setter for fileQuPurInvSaTaxItBas.</p>
   * @param pFileQuPurInvSaTaxItBas reference
   **/
  public final void setFileQuPurInvSaTaxItBas(
    final String pFileQuPurInvSaTaxItBas) {
    this.fileQuPurInvSaTaxItBas = pFileQuPurInvSaTaxItBas;
  }

  /**
   * <p>Getter for quPurInvSaTaxItBas.</p>
   * @return String
   **/
  public final String getQueryPurchInvSalTaxItemBas() {
    return this.quPurInvSaTaxItBas;
  }

  /**
   * <p>Setter for quPurInvSaTaxItBas.</p>
   * @param pQueryPurchInvSalTaxItemBas reference
   **/
  public final void setQueryPurchInvSalTaxItemBas(
    final String pQueryPurchInvSalTaxItemBas) {
    this.quPurInvSaTaxItBas = pQueryPurchInvSalTaxItemBas;
  }

  /**
   * <p>Getter for fileQueryPurchaseInvoiceTotals.</p>
   * @return String
   **/
  public final String getFileQueryPurchaseInvoiceTotals() {
    return this.fileQueryPurchaseInvoiceTotals;
  }

  /**
   * <p>Setter for fileQueryPurchaseInvoiceTotals.</p>
   * @param pFileQueryPurchaseInvoiceTotals reference
   **/
  public final void setFileQueryPurchaseInvoiceTotals(
    final String pFileQueryPurchaseInvoiceTotals) {
    this.fileQueryPurchaseInvoiceTotals = pFileQueryPurchaseInvoiceTotals;
  }

  /**
   * <p>Getter for queryPurchaseInvoiceTotals.</p>
   * @return String
   **/
  public final String getQueryPurchaseInvoiceTotals() {
    return this.queryPurchaseInvoiceTotals;
  }

  /**
   * <p>Setter for queryPurchaseInvoiceTotals.</p>
   * @param pQueryPurchaseInvoiceTotals reference
   **/
  public final void setQueryPurchaseInvoiceTotals(
    final String pQueryPurchaseInvoiceTotals) {
    this.queryPurchaseInvoiceTotals = pQueryPurchaseInvoiceTotals;
  }

  /**
   * <p>Getter for srvAccSettings.</p>
   * @return ISrvAccSettings
   **/
  public final ISrvAccSettings getSrvAccSettings() {
    return this.srvAccSettings;
  }

  /**
   * <p>Setter for srvAccSettings.</p>
   * @param pSrvAccSettings reference
   **/
  public final void setSrvAccSettings(final ISrvAccSettings pSrvAccSettings) {
    this.srvAccSettings = pSrvAccSettings;
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

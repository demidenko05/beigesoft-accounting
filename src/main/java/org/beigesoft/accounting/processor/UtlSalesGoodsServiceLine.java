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
import java.util.List;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.beigesoft.log.ILogger;
import org.beigesoft.exception.ExceptionWithCode;
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.service.ISrvDatabase;
import org.beigesoft.model.IRecordSet;
import org.beigesoft.accounting.persistable.AccSettings;
import org.beigesoft.accounting.persistable.SalesInvoice;
import org.beigesoft.accounting.persistable.SalesInvoiceServiceLine;
import org.beigesoft.accounting.persistable.SalesInvoiceTaxLine;
import org.beigesoft.accounting.persistable.Tax;
import org.beigesoft.accounting.persistable.InvItemTaxCategory;
import org.beigesoft.accounting.persistable.InvItemTaxCategoryLine;
import org.beigesoft.accounting.service.ISrvAccSettings;

/**
 * <p>Utility for Sales Invoice Goods/Service Line.
 * It makes total for owner.</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class UtlSalesGoodsServiceLine<RS> {

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
   * <p>File with Query Sales Invoice Taxes item basis method
   * aggregate tax rate.</p>
   **/
  private String fileQuSalInvSaTaxItBasAggr = "salInvSalTaxItBasAggr.sql";

  /**
   * <p>Query Sales Invoice Taxes invoice basis method
   * aggregate tax rate.</p>
   **/
  private String quSalInvSaTaxItBasAggr;

  /**
   * <p>File with Query Sales Invoice Taxes item basis method.</p>
   **/
  private String fileQuSalInvSaTaxItBas = "salesInvSalTaxItemBasis.sql";

  /**
   * <p>Query Sales Invoice Taxes invoice basis method.</p>
   **/
  private String quSalInvSaTaxItBas;

  /**
   * <p>File with Query Sales Invoice Taxes invoice basis method.</p>
   **/
  private String fileQuSalInvSaTaxInvBas = "salesInvSalTaxInvBasis.sql";

  /**
   * <p>Query Sales Invoice Taxes item basis method.</p>
   **/
  private String quSalInvSaTaxInvBas;

  /**
   * <p>File with Query Sales Invoice Totals.</p>
   **/
  private String fileQuerySalesInvoiceTotals = "salesInvoiceTotals.sql";

  /**
   * <p>Query Sales Invoice Totals.</p>
   **/
  private String querySalesInvoiceTotals;

  /**
   * <p>Business service for accounting settings.</p>
   **/
  private ISrvAccSettings srvAccSettings;

  /**
   * <p>Insert immutable line into DB.</p>
   * @param pReqVars additional param
   * @param pItsOwner SalesInvoice
   * @throws Exception - an exception
   **/
  public final void updateOwner(final Map<String, Object> pReqVars,
    final SalesInvoice pItsOwner) throws Exception {
    updateTaxLines(pReqVars, pItsOwner);
    String query = lazyGetQuerySalesInvoiceTotals();
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
   * <p>Lazy get quSalInvSaTaxItBasAggr.</p>
   * @return quSalInvSaTaxItBasAggr
   * @throws Exception - an exception
   **/
  public final String lazyGetQuSalInvSaTaxItBasAggr() throws Exception {
    if (this.quSalInvSaTaxItBasAggr == null) {
      String flName = "/accounting/trade/" + this.fileQuSalInvSaTaxItBasAggr;
      this.quSalInvSaTaxItBasAggr = loadString(flName);
    }
    return this.quSalInvSaTaxItBasAggr;
  }

  /**
   * <p>Lazy get quSalInvSaTaxItBas.</p>
   * @return quSalInvSaTaxItBas
   * @throws Exception - an exception
   **/
  public final String lazyGetQuSalInvSaTaxItBas() throws Exception {
    if (this.quSalInvSaTaxItBas == null) {
      String flName = "/accounting/trade/" + this.fileQuSalInvSaTaxItBas;
      this.quSalInvSaTaxItBas = loadString(flName);
    }
    return this.quSalInvSaTaxItBas;
  }

  /**
   * <p>Lazy get quSalInvSaTaxInvBas.</p>
   * @return quSalInvSaTaxInvBas
   * @throws Exception - an exception
   **/
  public final String lazyGetQuSalInvSaTaxInvBas() throws Exception {
    if (this.quSalInvSaTaxInvBas == null) {
      String flName = "/accounting/trade/" + this.fileQuSalInvSaTaxInvBas;
      this.quSalInvSaTaxInvBas = loadString(flName);
    }
    return this.quSalInvSaTaxInvBas;
  }

  /**
   * <p>Lazy get querySalesInvoiceTotals.</p>
   * @return querySalesInvoiceTotals
   * @throws Exception - an exception
   **/
  public final String lazyGetQuerySalesInvoiceTotals() throws Exception {
    if (this.querySalesInvoiceTotals == null) {
      String flName = "/accounting/trade/" + this.fileQuerySalesInvoiceTotals;
      this.querySalesInvoiceTotals = loadString(flName);
    }
    return this.querySalesInvoiceTotals;
  }

  /**
   * <p>Load string file (usually SQL query).</p>
   * @param pFileName file name
   * @return String usually SQL query
   * @throws IOException - IO exception
   **/
  public final String loadString(final String pFileName)
        throws IOException {
    URL urlFile = UtlSalesGoodsServiceLine.class
      .getResource(pFileName);
    if (urlFile != null) {
      InputStream inputStream = null;
      try {
        inputStream = UtlSalesGoodsServiceLine.class
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
   * @param pItsOwner SalesInvoice
   * @throws Exception - an exception
   **/
  public final void updateTaxLines(final Map<String, Object> pReqVars,
    final SalesInvoice pItsOwner) throws Exception {
    pReqVars.put("SalesInvoiceTaxLineitsOwnerdeepLevel", 1);
    List<SalesInvoiceTaxLine> itls = getSrvOrm().retrieveListWithConditions(
        pReqVars, SalesInvoiceTaxLine.class, "where ITSOWNER="
          + pItsOwner.getItsId());
    pReqVars.remove("SalesInvoiceTaxLineitsOwnerdeepLevel");
    boolean isShowDebug = getLogger().getIsShowDebugMessagesFor(getClass());
    int dbgDetLev = getLogger().getDetailLevel();
    AccSettings as = getSrvAccSettings().lazyGetAccSettings(pReqVars);
    boolean isTaxable = as.getIsExtractSalesTaxFromSales() && !pItsOwner
      .getOmitTaxes() && !pItsOwner.getCustomer().getIsForeigner();
    if (isTaxable) {
      boolean isItemBasis = !as.getSalTaxIsInvoiceBase();
      boolean isAggrOnlyRate = as.getSalTaxUseAggregItBas();
      RoundingMode rm = as.getSalTaxRoundMode();
      if (pItsOwner.getCustomer().getTaxDestination() != null) {
        //override tax method:
        isItemBasis = !pItsOwner.getCustomer()
          .getTaxDestination().getSalTaxIsInvoiceBase();
        isAggrOnlyRate = pItsOwner.getCustomer()
          .getTaxDestination().getSalTaxUseAggregItBas();
        rm = pItsOwner.getCustomer()
          .getTaxDestination().getSalTaxRoundMode();
      }
      if (pItsOwner.getPriceIncTax() && !isAggrOnlyRate) {
        throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
          "price_inc_tax_multi_not_imp");
      }
      if (isShowDebug && dbgDetLev > 30000) {
        getLogger().debug(null, UtlSalesGoodsServiceLine.class,
      "Updating tax lines for invoice #/date/isItemBasis/isAggrOnlyRate/rm: "
        + pItsOwner.getItsId() + "/" + pItsOwner.getItsDate() + "/"
          + isItemBasis + "/" + isAggrOnlyRate + "/" + rm);
      }
      String query;
      if (!isItemBasis) {
        query = lazyGetQuSalInvSaTaxInvBas();
      } else if (isAggrOnlyRate) {
        query = lazyGetQuSalInvSaTaxItBasAggr();
      } else {
        query = lazyGetQuSalInvSaTaxItBas();
      }
      query = query.replace(":INVOICEID", pItsOwner.getItsId().toString());
      IRecordSet<RS> recordSet = null;
      //lines (goods and services) to store data for item basis aggregate rate
      //and invoice basis with taxes included in price:
      List<SalesInvoiceServiceLine> invLns =
        new ArrayList<SalesInvoiceServiceLine>();
      //data storages for invoice basis price without taxes and item basis
      //with non-aggregate rate:
      List<Long> taxesLst = new ArrayList<Long>();
      List<Double> dbResults = new ArrayList<Double>();
      try {
        recordSet = getSrvDatabase().retrieveRecords(query);
        if (recordSet.moveToFirst()) {
          do {
            if (!isItemBasis) {
              Long taxId = recordSet.getLong("TAXID");
              Double percent = recordSet.getDouble("ITSPERCENTAGE");
              if (pItsOwner.getPriceIncTax()) { //&& aggregate/only rate
                Long ilId = recordSet.getLong("TAXCATID");
                SalesInvoiceServiceLine invLn = makeLine(invLns, ilId,
                  ilId, taxId, percent);
                invLn.setItsTotal(BigDecimal.valueOf(recordSet
                  .getDouble("ITSTOTAL")));
                invLn.setForeignTotal(BigDecimal.valueOf(recordSet
                  .getDouble("FOREIGNTOTAL")));
              } else { //any rate
                taxesLst.add(taxId);
                Double subtotal = recordSet.getDouble("SUBTOTAL");
                Double foreignSubtotal = recordSet.getDouble("FOREIGNSUBTOTAL");
                dbResults.add(subtotal * percent / 100.0d);
                dbResults.add(foreignSubtotal * percent / 100.0d);
                dbResults.add(subtotal);
                dbResults.add(foreignSubtotal);
              }
            } else {
              if (isAggrOnlyRate) { //any tax including
                Long ilId = recordSet.getLong("ILID");
                Long taxId = recordSet.getLong("TAXID");
                Double percent = recordSet.getDouble("ITSPERCENTAGE");
                SalesInvoiceServiceLine invLn = makeLine(invLns, ilId, ilId,
                  taxId, percent);
                invLn.setTotalTaxes(BigDecimal.valueOf(recordSet
                  .getDouble("TOTALTAXES")));
                invLn.setForeignTotalTaxes(BigDecimal.valueOf(recordSet
                  .getDouble("FOREIGNTOTALTAXES")));
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
      if (itls.size() > 0) {
        for (SalesInvoiceTaxLine itl : itls) {
          itl.setTax(null);
          itl.setTaxableInvBas(BigDecimal.ZERO);
          itl.setTaxableInvBasFc(BigDecimal.ZERO);
          itl.setItsTotal(BigDecimal.ZERO);
          itl.setForeignTotalTaxes(BigDecimal.ZERO);
        }
      }
      List<SalesInvoiceTaxLine> itlsnew = null;
      if (!(isItemBasis && !isAggrOnlyRate)) {
        itlsnew = new ArrayList<SalesInvoiceTaxLine>();
      }
      pReqVars.put("countUpdatedItl", Integer.valueOf(0));
      if (invLns.size() > 0) {
        BigDecimal bd100 = new BigDecimal("100.00");
        for (SalesInvoiceServiceLine invLn : invLns) {
          int ti = 0;
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
            } else {
              throw new Exception("Algorithm error!!!");
            }
            SalesInvoiceTaxLine itl = findCreateTaxLine(pReqVars, itls,
              itlsnew, itcl.getTax().getItsId());
            itl.setItsOwner(pItsOwner);
            itl.setTax(itcl.getTax());
            makeItl(pReqVars, itl, invLn, isItemBasis);
          }
        }
      }
      if (taxesLst.size() >  0) {
        List<Tax> taxes = new ArrayList<Tax>();
        for (int i = 0; i < taxesLst.size(); i++) {
          Double totalTax;
          Double totalTaxFc;
          Double taxable = null;
          Double taxableFc = null;
          if (!isItemBasis) {
            //invoice basis, any rate, taxes excluded
            Tax tax = new Tax();
            tax.setItsId(taxesLst.get(i));
            taxes.add(tax);
            totalTax = dbResults.get(i * 4);
            totalTaxFc = dbResults.get(i * 4 + 1);
            taxable = dbResults.get(i * 4 + 2);
            taxableFc = dbResults.get(i * 4 + 3);
          } else {
            //item basis, non-aggregate rate, taxes excluded
            totalTax = dbResults.get(i * 2);
            totalTaxFc = dbResults.get(i * 2 + 1);
            Tax tax = new Tax();
            tax.setItsId(taxesLst.get(i));
            taxes.add(tax);
          }
          for (int j = 0; j < taxes.size();  j++) {
            SalesInvoiceTaxLine itl;
            if (!isItemBasis) {
              itl = findCreateTaxLine(pReqVars, itls,
                itlsnew, taxes.get(j).getItsId());
            } else {
              itl = findCreateTaxLine(pReqVars, itls,
                taxes.get(j).getItsId());
            }
            itl.setItsOwner(pItsOwner);
            itl.setTax(taxes.get(j));
            makeItl(pReqVars, itl, totalTax, totalTaxFc, taxable, taxableFc,
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
    } else if (itls.size() > 0) {
      if (isShowDebug) {
        getLogger().debug(null, UtlSalesGoodsServiceLine.class,
          "Deleting tax lines for non-taxable invoice #"
            + pItsOwner.getItsId() + "/" + pItsOwner.getItsDate());
      }
      for (SalesInvoiceTaxLine itln : itls) {
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
  public final SalesInvoiceTaxLine findCreateTaxLine(
    final Map<String, Object> pReqVars,
      final List<SalesInvoiceTaxLine> pTaxLnsWas,
        final List<SalesInvoiceTaxLine> pTaxLnsNew, final Long pTaxId) {
    SalesInvoiceTaxLine itl = null;
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
        itl = new SalesInvoiceTaxLine();
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
  public final SalesInvoiceTaxLine findCreateTaxLine(
    final Map<String, Object> pReqVars,
      final List<SalesInvoiceTaxLine> pTaxLnsWas, final Long pTaxId) {
    SalesInvoiceTaxLine itl = null;
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
        itl = new SalesInvoiceTaxLine();
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
   * @return line
   **/
  public final SalesInvoiceServiceLine makeLine(
    final List<SalesInvoiceServiceLine> pInvLns, final Long pIlId,
      final Long pCatId,  final Long pTaxId, final Double pPercent) {
    SalesInvoiceServiceLine invLn = null;
    for (SalesInvoiceServiceLine il : pInvLns) {
      if (il.getItsId().equals(pIlId)) {
        invLn = il;
      }
    }
    if (invLn == null) {
      invLn = new SalesInvoiceServiceLine();
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
    itcl.setItsPercentage(BigDecimal.valueOf(pPercent));
    invLn.getTaxCategory().getTaxes().add(itcl);
    invLn.getTaxCategory().setAggrOnlyPercent(invLn.getTaxCategory()
      .getAggrOnlyPercent().add(itcl.getItsPercentage()));
    return invLn;
  }

  /**
   * <p>Makes invoice tax line.</p>
   * @param pReqVars additional param
   * @param pItl SalesInvoiceTaxLine
   * @param pInvLn inventory line
   * @param pIsItemBasis Is Item Basis
   * @throws Exception an Exception
   **/
  public final void makeItl(final Map<String, Object> pReqVars,
    final SalesInvoiceTaxLine pItl, final SalesInvoiceServiceLine pInvLn,
      final boolean pIsItemBasis) throws Exception {
    pItl.setItsTotal(pItl.getItsTotal().add(pInvLn.getTotalTaxes()));
    pItl.setForeignTotalTaxes(pItl.getForeignTotalTaxes()
      .add(pInvLn.getForeignTotalTaxes()));
    if (!pIsItemBasis) {
      pItl.setTaxableInvBas(pItl.getTaxableInvBas().add(pInvLn.getItsTotal()));
      pItl.setTaxableInvBasFc(pItl.getTaxableInvBasFc()
        .add(pInvLn.getForeignTotal()));
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
   * @param pItl SalesInvoiceTaxLine
   * @param pTotalTax Total Tax
   * @param pTotalTaxFc Total Tax in foreign currency
   * @param pTaxable Taxable
   * @param pTaxableFc Taxable in foreign currency
   * @param pAs ACC Settings
   * @param pRm rounding mode
   * @throws Exception an Exception
   **/
  public final void makeItl(final Map<String, Object> pReqVars,
    final SalesInvoiceTaxLine pItl, final Double pTotalTax,
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
   * <p>Getter for fileQuSalInvSaTaxItBasAggr.</p>
   * @return String
   **/
  public final String getFileQuSalInvSaTaxItBasAggr() {
    return this.fileQuSalInvSaTaxItBasAggr;
  }

  /**
   * <p>Setter for fileQuSalInvSaTaxItBasAggr.</p>
   * @param pFileQuSalInvSaTaxItBasAggr reference
   **/
  public final void setFileQuSalInvSaTaxItBasAggr(
    final String pFileQuSalInvSaTaxItBasAggr) {
    this.fileQuSalInvSaTaxItBasAggr = pFileQuSalInvSaTaxItBasAggr;
  }

  /**
   * <p>Setter for quSalInvSaTaxItBasAggr.</p>
   * @param pQuSalInvSaTaxItBasAggr reference
   **/
  public final void setQuSalInvSaTaxItBasAggr(
    final String pQuSalInvSaTaxItBasAggr) {
    this.quSalInvSaTaxItBasAggr = pQuSalInvSaTaxItBasAggr;
  }

  /**
   * <p>Getter for fileQuSalInvSaTaxItBas.</p>
   * @return String
   **/
  public final String getFileQuSalInvSaTaxItBas() {
    return this.fileQuSalInvSaTaxItBas;
  }

  /**
   * <p>Setter for fileQuSalInvSaTaxItBas.</p>
   * @param pFileQuSalInvSaTaxItBas reference
   **/
  public final void setFileQuSalInvSaTaxItBas(
    final String pFileQuSalInvSaTaxItBas) {
    this.fileQuSalInvSaTaxItBas = pFileQuSalInvSaTaxItBas;
  }

  /**
   * <p>Getter for quSalInvSaTaxItBas.</p>
   * @return String
   **/
  public final String getQuSalInvSaTaxItBas() {
    return this.quSalInvSaTaxItBas;
  }

  /**
   * <p>Setter for quSalInvSaTaxItBas.</p>
   * @param pQuSalInvSaTaxItBas reference
   **/
  public final void setQuSalInvSaTaxItBas(
    final String pQuSalInvSaTaxItBas) {
    this.quSalInvSaTaxItBas = pQuSalInvSaTaxItBas;
  }

  /**
   * <p>Getter for fileQuerySalesInvoiceTotals.</p>
   * @return String
   **/
  public final String getFileQuerySalesInvoiceTotals() {
    return this.fileQuerySalesInvoiceTotals;
  }

  /**
   * <p>Setter for fileQuerySalesInvoiceTotals.</p>
   * @param pFileQuerySalesInvoiceTotals reference
   **/
  public final void setFileQuerySalesInvoiceTotals(
    final String pFileQuerySalesInvoiceTotals) {
    this.fileQuerySalesInvoiceTotals = pFileQuerySalesInvoiceTotals;
  }

  /**
   * <p>Getter for querySalesInvoiceTotals.</p>
   * @return String
   **/
  public final String getQuerySalesInvoiceTotals() {
    return this.querySalesInvoiceTotals;
  }

  /**
   * <p>Setter for querySalesInvoiceTotals.</p>
   * @param pQuerySalesInvoiceTotals reference
   **/
  public final void setQuerySalesInvoiceTotals(
    final String pQuerySalesInvoiceTotals) {
    this.querySalesInvoiceTotals = pQuerySalesInvoiceTotals;
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

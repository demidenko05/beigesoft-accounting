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

import org.beigesoft.service.ISrvOrm;
import org.beigesoft.service.ISrvDatabase;
import org.beigesoft.model.IRecordSet;
import org.beigesoft.accounting.persistable.AccSettings;
import org.beigesoft.accounting.persistable.SalesInvoice;
import org.beigesoft.accounting.persistable.SalesInvoiceTaxLine;
import org.beigesoft.accounting.persistable.Tax;
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
    String[] columns = new String[]
      {"SUBTOTAL", "TOTALTAXES", "FOREIGNSUBTOTAL", "FOREIGNTOTALTAXES"};
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
    AccSettings as = getSrvAccSettings().lazyGetAccSettings(pReqVars);
    pItsOwner.setSubtotal(BigDecimal.valueOf(totals[0]).setScale(
      as.getPricePrecision(), as.getRoundingMode()));
    pItsOwner.setTotalTaxes(BigDecimal.valueOf(totals[1]).setScale(
      as.getPricePrecision(), as.getSalTaxRoundMode()));
    pItsOwner.setItsTotal(pItsOwner.getSubtotal().
      add(pItsOwner.getTotalTaxes()));
    pItsOwner.setForeignSubtotal(BigDecimal.valueOf(totals[2]).setScale(
      as.getPricePrecision(), as.getRoundingMode()));
    pItsOwner.setForeignTotalTaxes(BigDecimal.valueOf(totals[3]).setScale(
      as.getPricePrecision(), as.getSalTaxRoundMode()));
    pItsOwner.setForeignTotal(pItsOwner.getForeignSubtotal().
      add(pItsOwner.getForeignTotalTaxes()));
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
   * @param pOwner SalesInvoice
   * @throws Exception - an exception
   **/
  public final void updateTaxLines(final Map<String, Object> pReqVars,
    final SalesInvoice pOwner) throws Exception {
    pReqVars.put("SalesInvoiceTaxLineitsOwnerdeepLevel", 1);
    List<SalesInvoiceTaxLine> itls = getSrvOrm().retrieveListWithConditions(
        pReqVars, SalesInvoiceTaxLine.class, "where ITSOWNER="
          + pOwner.getItsId());
    pReqVars.remove("SalesInvoiceTaxLineitsOwnerdeepLevel");
    AccSettings as = getSrvAccSettings().lazyGetAccSettings(pReqVars);
    boolean isTaxable = as.getIsExtractSalesTaxFromSales() && !pOwner
      .getOmitTaxes() && !pOwner.getCustomer().getIsForeigner();
    if (isTaxable) {
      boolean isItemBasis = !as.getSalTaxIsInvoiceBase();
      boolean isAggrOnlyRate = as.getSalTaxUseAggregItBas();
      RoundingMode rm = as.getSalTaxRoundMode();
      if (pOwner.getCustomer().getTaxDestination() != null) {
        //override tax method:
        isItemBasis = !pOwner.getCustomer()
          .getTaxDestination().getSalTaxIsInvoiceBase();
        isAggrOnlyRate = pOwner.getCustomer()
          .getTaxDestination().getSalTaxUseAggregItBas();
        rm = pOwner.getCustomer()
          .getTaxDestination().getSalTaxRoundMode();
      }
      String query;
      if (!isItemBasis) {
        query = lazyGetQuSalInvSaTaxInvBas();
      } else if (isAggrOnlyRate) {
        query = lazyGetQuSalInvSaTaxItBasAggr();
      } else {
        query = lazyGetQuSalInvSaTaxItBas();
      }
      query = query.replace(":INVOICEID", pOwner.getItsId().toString());
      IRecordSet<RS> recordSet = null;
      List<Long> taxesOrCats = new ArrayList<Long>();
      List<Double> dbResults = new ArrayList<Double>();
      try {
        recordSet = getSrvDatabase().retrieveRecords(query);
        if (recordSet.moveToFirst()) {
          do {
            if (!isItemBasis) {
              taxesOrCats.add(recordSet.getLong("TAXID"));
              Double percent = recordSet.getDouble("ITSPERCENTAGE");
              Double taxable = recordSet.getDouble("TAXABLE");
              Double forTaxable = recordSet.getDouble("FOREIGNTAXABLE");
              if (pOwner.getPriceIncTax()) {
                dbResults.add(taxable - (1.0 - (taxable * percent / 100.0d)));
            dbResults.add(forTaxable - (1.0 - (forTaxable * percent / 100.0d)));
                dbResults.add(taxable);
                dbResults.add(forTaxable);
              } else {
                dbResults.add(taxable * percent / 100.0d);
                dbResults.add(forTaxable * percent / 100.0d);
                dbResults.add(taxable);
                dbResults.add(forTaxable);
              }
            } else {
              if (isAggrOnlyRate) {
                taxesOrCats.add(recordSet.getLong("TAXCATEGORY"));
                dbResults.add(recordSet.getDouble("TOTALTAXES"));
                dbResults.add(recordSet.getDouble("FOREIGNTOTALTAXES"));
              } else {
                taxesOrCats.add(recordSet.getLong("TAXID"));
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
      int countUpdatedItl = 0;
      List<Tax> taxes = new ArrayList<Tax>();
      for (int i = 0; i < taxesOrCats.size(); i++) {
        Double totalTax;
        Double totalTaxFc;
        Double taxable = null;
        Double taxableFc = null;
        BigDecimal aggrTaxRate = BigDecimal.ZERO;
        if (!isItemBasis) {
          Tax tax = new Tax();
          tax.setItsId(taxesOrCats.get(i));
          taxes.add(tax);
          totalTax = dbResults.get(i * 4);
          totalTaxFc = dbResults.get(i * 4 + 1);
          taxable = dbResults.get(i * 4 + 2);
          taxableFc = dbResults.get(i * 4 + 3);
        } else {
          totalTax = dbResults.get(i * 2);
          totalTaxFc = dbResults.get(i * 2 + 1);
          if (isAggrOnlyRate) {
            pReqVars.put("InvItemTaxCategoryLineitsOwnerdeepLevel", 1);
            List<InvItemTaxCategoryLine> itcls = getSrvOrm()
              .retrieveListWithConditions(pReqVars,
                InvItemTaxCategoryLine.class, "where ITSOWNER="
                  + taxesOrCats.get(i));
            pReqVars.remove("InvItemTaxCategoryLineitsOwnerdeepLevel");
            for (InvItemTaxCategoryLine itcl : itcls) {
              Tax tax = new Tax();
              tax.setItsId(itcl.getTax().getItsId());
              taxes.add(tax);
              aggrTaxRate = aggrTaxRate.add(itcl.getItsPercentage());
            }
          } else {
            Tax tax = new Tax();
            tax.setItsId(taxesOrCats.get(i));
            taxes.add(tax);
          }
        }
        Double aggrTax = null;
        Double aggrTaxFc = null;
        Double aggrTaxRest = null;
        Double aggrTaxRestFc = null;
        for (int j = 0; j < taxes.size();  j++) {
          SalesInvoiceTaxLine itl = null;
          if (isItemBasis && isAggrOnlyRate) {
            if (aggrTaxRest == null) {
              aggrTax = totalTax;
              aggrTaxRest = totalTax;
              aggrTaxFc = totalTaxFc;
              aggrTaxRestFc = totalTaxFc;
            }
            if (j == taxes.size() - 1) {
              totalTax = aggrTaxRest;
              totalTaxFc = aggrTaxRestFc;
            } else {
              totalTax = aggrTax * taxes.get(j).getItsPercentage()
                .doubleValue() / aggrTaxRate.doubleValue();
              totalTaxFc = aggrTaxFc * taxes.get(j).getItsPercentage()
                .doubleValue() / aggrTaxRate.doubleValue();
              aggrTaxRest -= totalTax;
              aggrTaxRestFc -= totalTaxFc;
            }
            if (i > 1) {
              for (int k = 0; k < itls.size(); k++) {
                if (itls.get(k).getTax().getItsId()
                  .equals(taxes.get(j).getItsId())) {
                  itl = itls.get(k);
                  if (k > countUpdatedItl) {
                    itl.setItsTotal(BigDecimal.ZERO);
                    itl.setForeignTotalTaxes(BigDecimal.ZERO);
                    if (k - countUpdatedItl > 1) {
                  SalesInvoiceTaxLine itlex = itls.get(countUpdatedItl + 1);
                      itls.set(countUpdatedItl + 1, itl);
                      itls.set(k, itlex);
                    }
                    countUpdatedItl++;
                  }
                  break;
                }
              }
            }
          }
          if (itl == null) {
            if (itls.size() > countUpdatedItl) {
              itl = itls.get(countUpdatedItl);
              itl.setItsTotal(BigDecimal.ZERO);
              itl.setForeignTotalTaxes(BigDecimal.ZERO);
              countUpdatedItl++;
            } else {
              itl = new SalesInvoiceTaxLine();
              itl.setIsNew(true);
              itl.setIdDatabaseBirth(this.srvOrm.getIdDatabase());
            }
          }
          itl.setItsOwner(pOwner);
          makeItl(pReqVars, itl, taxes.get(j), totalTax, totalTaxFc,
            taxable, taxableFc, as, rm, isItemBasis, isAggrOnlyRate);
        }
        taxes.clear();
      }
      if (countUpdatedItl < itls.size()) {
        for (int j = countUpdatedItl; j < itls.size(); j++) {
          getSrvOrm().deleteEntity(pReqVars, itls.get(j));
        }
      }
    } else if (itls.size() > 0) {
      for (SalesInvoiceTaxLine sitln : itls) {
        getSrvOrm().deleteEntity(pReqVars, sitln);
      }
    }
  }

  /**
   * <p>Makes invoice tax line line.</p>
   * @param pReqVars additional param
   * @param pItl PurchaseInvoiceTaxLine
   * @param pTax Tax
   * @param pTotalTax Total Tax
   * @param pTotalTaxFc Total Tax in foreign currency
   * @param pTaxable Taxable
   * @param pTaxableFc Taxable in foreign currency
   * @param pAs ACC Settings
   * @param pRm rounding mode
   * @param pIsItemBasis Is Item Basis
   * @param pIsUseAggrOnlyRate Use Aggr. or Only Rate
   * @throws Exception an Exception
   **/
  public final void makeItl(final Map<String, Object> pReqVars,
    final SalesInvoiceTaxLine pItl, final Tax pTax, final Double pTotalTax,
      final Double pTotalTaxFc, final Double pTaxable, final Double pTaxableFc,
        final AccSettings pAs, final RoundingMode pRm,
          final boolean pIsItemBasis,
            final boolean pIsUseAggrOnlyRate) throws Exception {
    pItl.setTax(pTax);
    if (pIsItemBasis && pIsUseAggrOnlyRate) {
      pItl.setItsTotal(pItl.getItsTotal().add(BigDecimal.valueOf(pTotalTax)
        .setScale(pAs.getPricePrecision(), pRm)));
      pItl.setForeignTotalTaxes(pItl.getForeignTotalTaxes().add(BigDecimal
        .valueOf(pTotalTaxFc).setScale(pAs.getPricePrecision(), pRm)));
    } else {
      pItl.setItsTotal(BigDecimal.valueOf(pTotalTax)
        .setScale(pAs.getPricePrecision(), pRm));
      pItl.setForeignTotalTaxes(BigDecimal.valueOf(pTotalTaxFc)
        .setScale(pAs.getPricePrecision(), pRm));
    }
    if (!pIsItemBasis) {
      pItl.setTaxableInvBas(BigDecimal.valueOf(pTaxable).setScale(
        pAs.getPricePrecision(), pAs.getRoundingMode()));
      pItl.setTaxableInvBasFc(BigDecimal.valueOf(pTaxableFc).setScale(
        pAs.getPricePrecision(), pAs.getRoundingMode()));
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
}

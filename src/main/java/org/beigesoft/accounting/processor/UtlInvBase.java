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
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.beigesoft.log.ILogger;
import org.beigesoft.exception.ExceptionWithCode;
import org.beigesoft.model.IRecordSet;
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.service.ISrvDatabase;
import org.beigesoft.service.ISrvNumberToString;
import org.beigesoft.accounting.model.CmprInvLnTotal;
import org.beigesoft.accounting.model.CmprTaxCatLnRate;
import org.beigesoft.accounting.persistable.base.AInvTxLn;
import org.beigesoft.accounting.persistable.AccSettings;
import org.beigesoft.accounting.persistable.IInvoice;
import org.beigesoft.accounting.persistable.IInvoiceLine;
import org.beigesoft.accounting.persistable.SalesInvoiceServiceLine;
import org.beigesoft.accounting.persistable.Tax;
import org.beigesoft.accounting.persistable.TaxDestination;
import org.beigesoft.accounting.persistable.InvItemTaxCategory;
import org.beigesoft.accounting.persistable.InvItemTaxCategoryLine;
import org.beigesoft.accounting.service.ISrvAccSettings;

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
   * <p>Business service for accounting settings.</p>
   **/
  private ISrvAccSettings srvAccSettings;

  /**
   * <p>Service print number.</p>
   **/
  private ISrvNumberToString srvNumberToString;

  /**
   * <p>Makes invoice totals after its line has been changed/deleted.</p>
   * @param pReqVars additional param
   * @param pInv Invoice
   * @param pTblNms tables names
   * @throws Exception - an exception
   **/
  public final <T extends IInvoice> void makeTotals(
    final Map<String, Object> pReqVars, final T pInv,
      final String[] pTblNms) throws Exception {
    String query = lazyGetQuTotals();
    query = query.replace(":ITSOWNER", pInv.getItsId().toString());
    if (pTblNms.length == 3) { //sales/purchase:
      query = query.replace(":TGOODLN", pTblNms[0]);
      query = query.replace(":TSERVICELN", pTblNms[1]);
      query = query.replace(":TTAXLN", pTblNms[2]);
    } else { //returns:
      query = query.replace(":TITEMLN", pTblNms[0]);
      query = query.replace(":TTAXLN", pTblNms[1]);
    }
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
    if (pInv.getPriceIncTax()) {
      pInv.setItsTotal(BigDecimal.valueOf(totals[1]).setScale(
        as.getPricePrecision(), as.getRoundingMode()));
      pInv.setTotalTaxes(BigDecimal.valueOf(totals[2]).setScale(
        as.getPricePrecision(), as.getSalTaxRoundMode()));
      pInv.setSubtotal(pInv.getItsTotal().subtract(pInv.getTotalTaxes()));
      pInv.setForeignTotal(BigDecimal.valueOf(totals[4]).setScale(
        as.getPricePrecision(), as.getRoundingMode()));
      pInv.setForeignTotalTaxes(BigDecimal.valueOf(totals[5]).setScale(
        as.getPricePrecision(), as.getSalTaxRoundMode()));
      pInv.setForeignSubtotal(pInv.getForeignTotal().
        subtract(pInv.getForeignTotalTaxes()));
    } else {
      pInv.setSubtotal(BigDecimal.valueOf(totals[0]).setScale(
        as.getPricePrecision(), as.getRoundingMode()));
      pInv.setTotalTaxes(BigDecimal.valueOf(totals[2]).setScale(
        as.getPricePrecision(), as.getSalTaxRoundMode()));
      pInv.setItsTotal(pInv.getSubtotal().add(pInv.getTotalTaxes()));
      pInv.setForeignSubtotal(BigDecimal.valueOf(totals[3]).setScale(
        as.getPricePrecision(), as.getRoundingMode()));
      pInv.setForeignTotalTaxes(BigDecimal.valueOf(totals[5]).setScale(
        as.getPricePrecision(), as.getSalTaxRoundMode()));
      pInv.setForeignTotal(pInv.getForeignSubtotal().
        add(pInv.getForeignTotalTaxes()));
    }
    getSrvOrm().updateEntity(pReqVars, pInv);
  }

  /**
   * <p>Make invoice line that stores taxes data in lines set
   * for invoice basis or item basis aggregate rate.</p>
   * @param pTxdLns TD lines
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
   * @param pCart cart
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
   * <p>Lazy get for quTxInvBas.</p>
   * @return String
   * @throws IOException - IO exception
   **/
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
  public final String lazyGetQuTxItBas() throws IOException {
    if (this.quTxItBas == null) {
      this.quTxItBas = loadString("/accounting/trade/"
        + this.flTxItBas);
    }
    return this.quTxItBas;
  }

  /**
   * <p>Load string file (usually SQL query).</p>
   * @param pFileName file name
   * @return String usually SQL query
   * @throws IOException - IO exception
   **/
  public final String loadString(final String pFileName) throws IOException {
    URL urlFile = UtlInvBase.class.getResource(pFileName);
    if (urlFile != null) {
      InputStream inputStream = null;
      try {
        inputStream = UtlInvBase.class.getResourceAsStream(pFileName);
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
}

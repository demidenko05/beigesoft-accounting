package org.beigesoft.accounting.report;

/*
 * Copyright (c) 2017 Beigesoft ™
 *
 * Licensed under the GNU General Public License (GPL), Version 2.0
 * (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html
 */

import java.io.OutputStream;
import java.text.DateFormat;
import java.util.Map;
import java.util.Locale;
import java.math.BigDecimal;

import org.beigesoft.doc.model.Document;
import org.beigesoft.doc.model.DocTable;
import org.beigesoft.doc.model.EAlignHorizontal;
import org.beigesoft.doc.model.EPageSize;
import org.beigesoft.doc.model.EPageOrientation;
import org.beigesoft.doc.service.IDocumentMaker;
import org.beigesoft.pdf.model.ERegisteredTtfFont;
import org.beigesoft.pdf.model.PdfDocument;
import org.beigesoft.pdf.service.IPdfFactory;
import org.beigesoft.pdf.service.IPdfMaker;

import org.beigesoft.service.ISrvI18n;
import org.beigesoft.service.ISrvNumberToString;
import org.beigesoft.accounting.service.ISrvAccSettings;
import org.beigesoft.accounting.model.BalanceSheet;
import org.beigesoft.accounting.persistable.AccSettings;
import org.beigesoft.accounting.persistable.I18nAccounting;
import org.beigesoft.accounting.persistable.I18nCurrency;

/**
 * <p>Balance sheet report into PDF.</p>
 *
 * @param <RS> platform dependent RDBMS recordset
 * @param <WI> writing instrument type
 * @author Yury Demidenko
 */
public class BalanceSheetPdf<RS, WI> implements IBalanceSheetPdf {

  /**
   * <p>PDF Factory.</p>
   **/
  private IPdfFactory<WI> pdfFactory;

  /**
   * <p>Date format.</p>
   **/
  private DateFormat dateFormat;

  /**
   * <p>Business service for accounting settings.</p>
   **/
  private ISrvAccSettings srvAccSettings;

  /**
   * <p>I18N service.</p>
   **/
  private ISrvI18n srvI18n;

  /**
   * <p>Service print number.</p>
   **/
  private ISrvNumberToString srvNumberToString;

  /**
   * <p>Write PDF report for given balance to output stream.</p>
   * @param pAddParam additional param
   * @param pBalance Balance
   * @param pOus servlet output stream
   * @throws Exception - an exception
   **/
  @Override
  public final void makeReport(final Map<String, Object> pAddParam,
    final BalanceSheet pBalance,
      final OutputStream pOus) throws Exception {
    Document<WI> doc = this.pdfFactory.lazyGetFctDocument()
      .createDoc(EPageSize.A4, EPageOrientation.PORTRAIT);
    PdfDocument<WI> docPdf = this.pdfFactory.createPdfDoc(doc);
    AccSettings accSet = this.srvAccSettings.lazyGetAccSettings(pAddParam);
    String lang = (String) pAddParam.get("lang");
    this.dateFormat = DateFormat
      .getDateInstance(DateFormat.MEDIUM, new Locale(lang));
    String curSign;
    I18nCurrency i18nCurrency =
      (I18nCurrency) pAddParam.get("i18nCurrency");
    boolean isPrnCurLf;
    if (i18nCurrency != null) {
      isPrnCurLf = i18nCurrency.getPrintCurrencyLeft();
      if (i18nCurrency.getUseCurrencySign()) {
        curSign = i18nCurrency.getHasName().getItsSign();
      } else {
        curSign = " " + i18nCurrency.getItsName() + " ";
      }
    } else {
      curSign = (String) pAddParam.get("curSign");
      isPrnCurLf = accSet.getPrintCurrencyLeft();
    }
    IDocumentMaker<WI> docMaker = this.pdfFactory.lazyGetDocumentMaker();
    docPdf.getPdfInfo().setAuthor("Beigesoft (TM) Accounting, "
      + accSet.getOrganization());
    IPdfMaker<WI> pdfMaker = this.pdfFactory.lazyGetPdfMaker();
    pdfMaker.addFontTtf(docPdf, ERegisteredTtfFont.DEJAVUSERIF.toString());
    pdfMaker.addFontTtf(docPdf, ERegisteredTtfFont.DEJAVUSERIF_BOLD.toString());
    double widthNdot = this.pdfFactory.lazyGetUomHelper()
      .fromPoints(2.0, 300.0, doc.getUnitOfMeasure()); //printer resolution
    doc.setBorder(widthNdot);
    doc.setContentPadding(0.0);
    doc.setContentPaddingBottom(0.5);
    doc.setAlignHoriCont(EAlignHorizontal.CENTER);
    DocTable<WI> tblTitle = docMaker.addDocTableNoBorder(doc, 1, 3);
    I18nAccounting i18nAccounting =
      (I18nAccounting) pAddParam.get("i18nAccounting");
    if (i18nAccounting != null) {
      tblTitle.getItsCells().get(0)
        .setItsContent(i18nAccounting.getOrganizationName());
    } else {
      tblTitle.getItsCells().get(0).setItsContent(accSet.getOrganization());
    }
    tblTitle.getItsCells().get(1)
      .setItsContent(this.srvI18n.getMsg("balance_sheet", lang));
    tblTitle.getItsCells().get(2)
      .setItsContent(this.dateFormat.format(pBalance.getItsDate()));
    tblTitle.setAlignHorizontal(EAlignHorizontal.CENTER);
    docMaker.makeDocTableWrapping(tblTitle);
    doc.setContentPadding(1.0);
    doc.setAlignHoriCont(EAlignHorizontal.LEFT);
    DocTable<WI> tblBal = docMaker
      .addDocTable(doc, 4, pBalance.getDetailRowsCount() + 2);
    tblBal.getItsColumns().get(0).setIsWidthFixed(true);
    tblBal.getItsColumns().get(0).setWidthInPercentage(30.0);
    tblBal.getItsColumns().get(1).setIsWidthFixed(true);
    tblBal.getItsColumns().get(1).setWidthInPercentage(20.0);
    tblBal.getItsColumns().get(2).setIsWidthFixed(true);
    tblBal.getItsColumns().get(2).setWidthInPercentage(30.0);
    tblBal.getItsColumns().get(3).setIsWidthFixed(true);
    tblBal.getItsColumns().get(3).setWidthInPercentage(20.0);
    tblBal.getItsCells().get(0).setItsContent(this.srvI18n
      .getMsg("AssetsTitle", lang));
    tblBal.getItsCells().get(0).setMergedCell(tblBal.getItsCells().get(1));
    tblBal.getItsCells().get(2).setItsContent(this.srvI18n
      .getMsg("LiabilitiesTitle", lang));
    tblBal.getItsCells().get(2).setMergedCell(tblBal.getItsCells().get(3));
    int row = 1;
    for (int i = 0; i < pBalance.getTotalLinesAssets(); i++) {
      String cnt;
      if (pBalance.getItsLines().get(i).getDebit()
        .compareTo(BigDecimal.ZERO) != 0) {
        cnt = prn(pAddParam, pBalance.getItsLines().get(i).getDebit());
      } else {
        cnt = "(" + prn(pAddParam, pBalance.getItsLines().get(i).getCredit())
          + ")";
      }
      tblBal.getItsCells().get(row * 4)
        .setItsContent(pBalance.getItsLines().get(i).getAccName());
      tblBal.getItsCells().get(row * 4).setFontNumber(1);
      tblBal.getItsCells().get(row * 4 + 1).setItsContent(cnt);
      tblBal.getItsCells().get(row * 4 + 1)
        .setAlignHorizontal(EAlignHorizontal.RIGHT);
      tblBal.getItsCells().get(row * 4 + 1).setFontNumber(1);
      row++;
    }
    int totLeabOwnEq = pBalance.getTotalLinesLiabilities()
      + pBalance.getTotalLinesOwnersEquity();
    int lastRowIdx = Math.max(pBalance.getTotalLinesAssets() + 1,
      totLeabOwnEq + 4);
    tblBal.getItsCells().get(lastRowIdx * 4)
      .setItsContent(this.srvI18n.getMsg("total_assets", lang));
    tblBal.getItsCells().get(lastRowIdx * 4 + 1)
      .setAlignHorizontal(EAlignHorizontal.RIGHT);
    String cntc;
    if (isPrnCurLf) {
      cntc = curSign + prn(pAddParam, pBalance.getTotalAssets());
    } else {
      cntc = prn(pAddParam, pBalance.getTotalAssets()) + curSign;
    }
    tblBal.getItsCells().get(lastRowIdx * 4 + 1).setItsContent(cntc);
    row = 1;
    int totAssLeab = pBalance.getTotalLinesAssets()
        + pBalance.getTotalLinesLiabilities();
    for (int i = pBalance.getTotalLinesAssets(); i < totAssLeab; i++) {
      String cnt;
      if (pBalance.getItsLines().get(i).getCredit()
        .compareTo(BigDecimal.ZERO) != 0) {
        cnt = prn(pAddParam, pBalance.getItsLines().get(i).getCredit());
      } else {
        cnt = "(" + prn(pAddParam, pBalance.getItsLines().get(i).getDebit())
          + ")";
      }
      tblBal.getItsCells().get(row * 4 + 2)
        .setItsContent(pBalance.getItsLines().get(i).getAccName());
      tblBal.getItsCells().get(row * 4 + 2).setFontNumber(1);
      tblBal.getItsCells().get(row * 4 + 3).setItsContent(cnt);
      tblBal.getItsCells().get(row * 4 + 3)
        .setAlignHorizontal(EAlignHorizontal.RIGHT);
      tblBal.getItsCells().get(row * 4 + 3).setFontNumber(1);
      row++;
    }
    tblBal.getItsCells().get(pBalance.getTotalLinesLiabilities() * 4 + 6)
      .setItsContent(this.srvI18n.getMsg("total_l", lang));
    tblBal.getItsCells().get(pBalance.getTotalLinesLiabilities() * 4 + 7)
      .setAlignHorizontal(EAlignHorizontal.RIGHT);
    tblBal.getItsCells().get(pBalance.getTotalLinesLiabilities() * 4 + 7)
      .setItsContent(pBalance.getTotalLiabilities().toString());
    int oetIdx = pBalance.getTotalLinesLiabilities() * 4 + 10;
    tblBal.getItsCells().get(oetIdx)
      .setItsContent(this.srvI18n.getMsg("OwnersEquityTitle", lang));
    tblBal.getItsCells().get(oetIdx)
      .setMergedCell(tblBal.getItsCells().get(oetIdx + 1));
    row = 1 + pBalance.getTotalLinesLiabilities() + 2;
    for (int i = totAssLeab;
      i < totAssLeab + pBalance.getTotalLinesOwnersEquity(); i++) {
      String cnt;
      if (pBalance.getItsLines().get(i).getCredit()
        .compareTo(BigDecimal.ZERO) != 0) {
        cnt = prn(pAddParam, pBalance.getItsLines().get(i).getCredit());
      } else {
        cnt = "(" + prn(pAddParam, pBalance.getItsLines().get(i).getDebit())
          + ")";
      }
      tblBal.getItsCells().get(row * 4 + 2)
        .setItsContent(pBalance.getItsLines().get(i).getAccName());
      tblBal.getItsCells().get(row * 4 + 2).setFontNumber(1);
      tblBal.getItsCells().get(row * 4 + 3).setItsContent(cnt);
      tblBal.getItsCells().get(row * 4 + 3)
        .setAlignHorizontal(EAlignHorizontal.RIGHT);
      tblBal.getItsCells().get(row * 4 + 3).setFontNumber(1);
      row++;
    }
    tblBal.getItsCells().get((totLeabOwnEq + 3) * 4 + 2)
      .setItsContent(this.srvI18n.getMsg("total_oe", lang));
    tblBal.getItsCells().get((totLeabOwnEq + 3) * 4 + 3)
      .setAlignHorizontal(EAlignHorizontal.RIGHT);
    tblBal.getItsCells().get((totLeabOwnEq + 3) * 4 + 3)
      .setItsContent(pBalance.getTotalOwnersEquity().toString());
    tblBal.getItsCells().get(lastRowIdx * 4 + 2)
      .setItsContent(this.srvI18n.getMsg("total_l_oe", lang));
    tblBal.getItsCells().get(lastRowIdx * 4 + 3)
      .setAlignHorizontal(EAlignHorizontal.RIGHT);
    if (isPrnCurLf) {
      cntc = curSign + prn(pAddParam, pBalance.getTotalOwnersEquity()
        .add(pBalance.getTotalLiabilities()));
    } else {
      cntc = prn(pAddParam, pBalance.getTotalOwnersEquity()
        .add(pBalance.getTotalLiabilities())) + curSign;
    }
    tblBal.getItsCells().get(lastRowIdx * 4 + 3).setItsContent(cntc);
    docMaker.deriveElements(doc);
    pdfMaker.prepareBeforeWrite(docPdf);
    this.pdfFactory.lazyGetPdfWriter().write(null, docPdf, pOus);
  }

  /**
   * <p>Simple delegator to print number.</p>
   * @param pAddParam additional param
   * @param pVal value
   * @return String
   **/
  public final String prn(final Map<String, Object> pAddParam,
    final BigDecimal pVal) {
    return this.srvNumberToString.print(pVal.toString(),
      (String) pAddParam.get("dseparatorv"),
        (String) pAddParam.get("dgseparatorv"),
          (Integer) pAddParam.get("balancePrecision"),
            (Integer) pAddParam.get("digitsInGroup"));
  }

  //Simple getters and setters:
  /**
   * <p>Getter for pdfFactory.</p>
   * @return IPdfFactory
   **/
  public final IPdfFactory<WI> getPdfFactory() {
    return this.pdfFactory;
  }

  /**
   * <p>Setter for pdfFactory.</p>
   * @param pFactory reference
   **/
  public final void setPdfFactory(final IPdfFactory<WI> pFactory) {
    this.pdfFactory = pFactory;
  }

  /**
   * <p>Getter for dateFormat.</p>
   * @return DateFormat
   **/
  public final DateFormat getDateFormat() {
    return this.dateFormat;
  }

  /**
   * <p>Setter for dateFormat.</p>
   * @param pDateFormat reference
   **/
  public final void setDateFormat(final DateFormat pDateFormat) {
    this.dateFormat = pDateFormat;
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
   * <p>Getter for srvI18n.</p>
   * @return ISrvI18n
   **/
  public final ISrvI18n getSrvI18n() {
    return this.srvI18n;
  }

  /**
   * <p>Setter for srvI18n.</p>
   * @param pSrvI18n reference
   **/
  public final void setSrvI18n(final ISrvI18n pSrvI18n) {
    this.srvI18n = pSrvI18n;
  }

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
}

package org.beigesoft.accounting.report;

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

import java.io.OutputStream;
import java.text.DateFormat;
import java.util.Map;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Locale;
import java.math.BigDecimal;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;

import org.beigesoft.doc.model.Document;
import org.beigesoft.doc.model.DocTable;
import org.beigesoft.doc.model.EWraping;
import org.beigesoft.doc.model.EAlignHorizontal;
import org.beigesoft.doc.model.EUnitOfMeasure;
import org.beigesoft.doc.service.IDocumentMaker;
import org.beigesoft.pdf.model.PdfDocument;
import org.beigesoft.pdf.service.IPdfFactory;
import org.beigesoft.pdf.service.IPdfMaker;

import org.beigesoft.model.IRequestData;
import org.beigesoft.service.ISrvI18n;
import org.beigesoft.service.ISrvNumberToString;
import org.beigesoft.service.IEntityFileReporter;
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.accounting.service.ISrvAccSettings;
import org.beigesoft.accounting.persistable.SalesInvoice;
import org.beigesoft.accounting.persistable.SalesInvoiceLine;
import org.beigesoft.accounting.persistable.SalesInvoiceTaxLine;
import org.beigesoft.accounting.persistable.SalesInvoiceServiceLine;
import org.beigesoft.accounting.persistable.AccSettings;
import org.beigesoft.accounting.persistable.I18nAccounting;
import org.beigesoft.accounting.persistable.Currency;
import org.beigesoft.accounting.persistable.I18nCurrency;
import org.beigesoft.accounting.persistable.I18nBuyer;

/**
 * <p>Invoice report into PDF.</p>
 *
 * @param <RS> platform dependent RDBMS recordset
 * @param <WI> writing instrument type
 * @author Yury Demidenko
 */
public class InvoiceReportPdf<RS, WI>
  implements IEntityFileReporter<SalesInvoice, Long> {

  /**
   * <p>PDF Factory.</p>
   **/
  private IPdfFactory<WI> pdfFactory;

  /**
   * <p>Business service for accounting settings.</p>
   **/
  private ISrvAccSettings srvAccSettings;

  /**
   * <p>I18N service.</p>
   **/
  private ISrvI18n srvI18n;

  /**
   * <p>ORM service.</p>
   **/
  private ISrvOrm<RS> srvOrm;

  /**
   * <p>Service print number.</p>
   **/
  private ISrvNumberToString srvNumberToString;

  /**
   * <p>salesInvOverseaseLines SQL.</p>
   **/
  private String salesInvOverseaseLinesSql;

  /**
   * <p>salesInvOverseaseServiceLines SQL.</p>
   **/
  private String salesInvOverseaseServiceLinesSql;

  /**
   * <p>Write PDF report for given invoice to output stream.</p>
   * @param pReqVars additional param
   * @param pInvoice Invoice
   * @param pRequestData Request Data
   * @param pOus servlet output stream
   * @throws Exception - an exception
   **/
  @Override
  public final void makeReport(final Map<String, Object> pReqVars,
    final SalesInvoice pInvoice, final IRequestData pRequestData,
      final OutputStream pOus) throws Exception {
    AccSettings accSet = this.srvAccSettings.lazyGetAccSettings(pReqVars);
    String lang = (String) pReqVars.get("lang");
    String langDef = (String) pReqVars.get("langDef");
    DateFormat dateFormat = DateFormat
      .getDateInstance(DateFormat.MEDIUM, new Locale(lang));
    SalesInvoice inv = retrieveEntity(pReqVars, pInvoice, lang,
      !lang.equals(langDef));
    Currency currency;
    if (inv.getForeignCurrency() != null) {
      currency = inv.getForeignCurrency();
    } else {
      currency = accSet.getCurrency();
    }
    I18nCurrency i18nCurrency = null;
    String curSign;
    boolean isPrnCurLf;
    if (!lang.equals(langDef)) {
      List<I18nCurrency> i18nCurrencyLst = this.srvOrm.retrieveList(
        pReqVars, I18nCurrency.class);
      for (I18nCurrency i18nCur : i18nCurrencyLst) {
        if (i18nCur.getHasName().getItsId().equals(currency.getItsId())
          && i18nCur.getLang().getItsId().equals(lang)) {
          i18nCurrency = i18nCur;
          break;
        }
      }
    }
    if (i18nCurrency != null) {
      isPrnCurLf = i18nCurrency.getPrintCurrencyLeft();
      if (i18nCurrency.getUseCurrencySign()) {
        curSign = i18nCurrency.getHasName().getItsSign();
      } else {
        curSign = " " + i18nCurrency.getItsName() + " ";
      }
    } else {
      isPrnCurLf = accSet.getPrintCurrencyLeft();
      if (accSet.getUseCurrencySign()) {
        curSign = currency.getItsSign();
      } else {
        curSign = " " + currency.getItsName() + " ";
      }
    }
    Document<WI> doc = this.pdfFactory.lazyGetFctDocument()
      .createDoc(accSet.getPageSize(), accSet.getPageOrientation());
    doc.setFontSize(accSet.getFontSize());
    doc.getPages().get(0).setMarginBottom(accSet.getMarginBottom());
    doc.getPages().get(0).setMarginTop(accSet.getMarginTop());
    doc.getPages().get(0).setMarginLeft(accSet.getMarginLeft());
    doc.getPages().get(0).setMarginRight(accSet.getMarginRight());
    PdfDocument<WI> docPdf = this.pdfFactory.createPdfDoc(doc);
    IDocumentMaker<WI> docMaker = this.pdfFactory.lazyGetDocumentMaker();
    docPdf.getPdfInfo().setAuthor("Beigesoft (TM) Accounting, "
      + accSet.getOrganization());
    IPdfMaker<WI> pdfMaker = this.pdfFactory.lazyGetPdfMaker();
    if (accSet.getTtfBoldFileName() != null) {
      pdfMaker.addFontTtf(docPdf, accSet.getTtfBoldFileName());
    }
    pdfMaker.addFontTtf(docPdf, accSet.getTtfFileName());
    double widthNdot = this.pdfFactory.lazyGetUomHelper()
      .fromPoints(2.0, 300.0, doc.getUnitOfMeasure()); //printer resolution
    doc.setBorder(widthNdot);
    doc.setContentPadding(0.0);
    doc.setContentPaddingBottom(mmToDocUom(1.0, doc.getUnitOfMeasure()));
    DocTable<WI> tblOwner = docMaker.addDocTableNoBorder(doc, 1, 1);
    I18nAccounting i18nAccounting =
      (I18nAccounting) pReqVars.get("i18nAccounting");
    if (i18nAccounting != null) {
      tblOwner.getItsCells().get(0)
        .setItsContent(i18nAccounting.getOrganizationName());
    } else {
      tblOwner.getItsCells().get(0).setItsContent(accSet.getOrganization());
    }
    int n = 0;
    if (accSet.getTaxIdentificationNumber() != null) {
      docMaker.addRowToDocTable(tblOwner);
      tblOwner.getItsCells().get(++n).setItsContent(this.srvI18n
        .getMsg("taxIdentificationNumber", lang) + ": "
          + accSet.getTaxIdentificationNumber());
    }
    if (accSet.getRegZip() != null) {
      docMaker.addRowToDocTable(tblOwner);
      tblOwner.getItsCells().get(++n).setItsContent(this.srvI18n
        .getMsg("regZip", lang) + ": " + accSet.getRegZip());
    }
    String addr = null;
    if (i18nAccounting != null) {
      addr = i18nAccounting.getRegAddress1();
    } else {
      addr = accSet.getRegAddress1();
    }
    if (addr != null) {
      docMaker.addRowToDocTable(tblOwner);
      tblOwner.getItsCells().get(++n).setItsContent(this.srvI18n
        .getMsg("regAddress1", lang) + ": " + addr);
    }
    if (i18nAccounting != null) {
      addr = i18nAccounting.getRegAddress2();
    } else {
      addr = accSet.getRegAddress2();
    }
    if (addr != null) {
      docMaker.addRowToDocTable(tblOwner);
      tblOwner.getItsCells().get(++n).setItsContent(this.srvI18n
        .getMsg("regAddress2", lang) + ": " + addr);
    }
    if (i18nAccounting != null) {
      addr = i18nAccounting.getRegCity();
    } else {
      addr = accSet.getRegCity();
    }
    if (addr != null) {
      docMaker.addRowToDocTable(tblOwner);
      tblOwner.getItsCells().get(++n).setItsContent(this.srvI18n
        .getMsg("regCity", lang) + ": " + addr);
    }
    if (i18nAccounting != null) {
      addr = i18nAccounting.getRegState();
    } else {
      addr = accSet.getRegState();
    }
    if (addr != null) {
      docMaker.addRowToDocTable(tblOwner);
      tblOwner.getItsCells().get(++n).setItsContent(this.srvI18n
        .getMsg("regState", lang) + ": " + addr);
    }
    if (i18nAccounting != null) {
      addr = i18nAccounting.getRegCountry();
    } else {
      addr = accSet.getRegCountry();
    }
    if (addr != null) {
      docMaker.addRowToDocTable(tblOwner);
      tblOwner.getItsCells().get(++n).setItsContent(this.srvI18n
        .getMsg("regCountry", lang) + ": " + addr);
    }
    tblOwner.getItsCells().get(0).setFontNumber(1);
    docMaker.makeDocTableWrapping(tblOwner);
    tblOwner.setAlignHorizontal(EAlignHorizontal.RIGHT);
    DocTable<WI> tblTitle = docMaker.addDocTableNoBorder(doc, 1, 1);
    String invNum;
    if (inv.getIdBirth() != null) {
      invNum = inv.getIdDatabaseBirth().toString() + "-"
        + inv.getIdBirth();
    } else {
      invNum = inv.getIdDatabaseBirth().toString() + "-"
        + inv.getItsId();
    }
    String title = this.srvI18n.getMsg("Invoice", lang) + " #" + invNum + ", "
      + this.srvI18n.getMsg("date", lang) + ": "
        + dateFormat.format(inv.getItsDate());
    tblTitle.getItsCells().get(0).setItsContent(title);
    tblTitle.getItsCells().get(0).setFontNumber(1);
    tblTitle.setAlignHorizontal(EAlignHorizontal.CENTER);
    doc.setContainerMarginBottom(mmToDocUom(1.0, doc.getUnitOfMeasure()));
    docMaker.makeDocTableWrapping(tblTitle);
    DocTable<WI> tblCustomer = docMaker.addDocTableNoBorder(doc, 1, 1);
    I18nBuyer i18nBuyer = null;
    if (!lang.equals(langDef)) {
      i18nBuyer = getSrvOrm().retrieveEntityById(pReqVars,
        I18nBuyer.class, inv.getCustomer());
    }
    if (i18nBuyer != null) {
      tblCustomer.getItsCells().get(0)
        .setItsContent(i18nBuyer.getItsName());
    } else {
      tblCustomer.getItsCells().get(0)
        .setItsContent(inv.getCustomer().getItsName());
    }
    n = 0;
    if (inv.getCustomer().getTaxIdentificationNumber() != null) {
      docMaker.addRowToDocTable(tblCustomer);
      tblCustomer.getItsCells().get(++n).setItsContent(this.srvI18n
        .getMsg("taxIdentificationNumber", lang) + ": "
          + inv.getCustomer().getTaxIdentificationNumber());
    }
    if (inv.getCustomer().getRegZip() != null) {
      docMaker.addRowToDocTable(tblCustomer);
      tblCustomer.getItsCells().get(++n).setItsContent(this.srvI18n
        .getMsg("regZip", lang) + ": " + inv.getCustomer().getRegZip());
    }
    if (i18nBuyer != null) {
      addr = i18nBuyer.getRegAddress1();
    } else {
      addr = inv.getCustomer().getRegAddress1();
    }
    if (addr != null) {
      docMaker.addRowToDocTable(tblCustomer);
      tblCustomer.getItsCells().get(++n).setItsContent(this.srvI18n
        .getMsg("regAddress1", lang) + ": " + addr);
    }
    if (i18nBuyer != null) {
      addr = i18nBuyer.getRegAddress2();
    } else {
      addr = inv.getCustomer().getRegAddress2();
    }
    if (addr != null) {
      docMaker.addRowToDocTable(tblCustomer);
      tblCustomer.getItsCells().get(++n).setItsContent(this.srvI18n
        .getMsg("regAddress2", lang) + ": " + addr);
    }
    if (i18nBuyer != null) {
      addr = i18nBuyer.getRegCity();
    } else {
      addr = inv.getCustomer().getRegCity();
    }
    if (addr != null) {
      docMaker.addRowToDocTable(tblCustomer);
      tblCustomer.getItsCells().get(++n).setItsContent(this.srvI18n
        .getMsg("regCity", lang) + ": " + addr);
    }
    if (i18nBuyer != null) {
      addr = i18nBuyer.getRegState();
    } else {
      addr = inv.getCustomer().getRegState();
    }
    if (addr != null) {
      docMaker.addRowToDocTable(tblCustomer);
      tblCustomer.getItsCells().get(++n).setItsContent(this.srvI18n
        .getMsg("regState", lang) + ": " + addr);
    }
    if (i18nBuyer != null) {
      addr = i18nBuyer.getRegCountry();
    } else {
      addr = inv.getCustomer().getRegCountry();
    }
    if (addr != null) {
      docMaker.addRowToDocTable(tblCustomer);
      tblCustomer.getItsCells().get(++n).setItsContent(this.srvI18n
        .getMsg("regCountry", lang) + ": " + addr);
    }
    tblCustomer.getItsCells().get(0).setFontNumber(1);
    BigDecimal price;
    BigDecimal subtotal;
    BigDecimal totalTaxes;
    BigDecimal total;
    if (inv.getItsLines() != null && inv.getItsLines().size() > 0) {
      doc.setContainerMarginBottom(mmToDocUom(2.0, doc.getUnitOfMeasure()));
      DocTable<WI> tblTiGoods = docMaker.addDocTableNoBorder(doc, 1, 1);
      tblTiGoods.getItsCells().get(0).setItsContent(this.srvI18n
        .getMsg(SalesInvoiceLine.class.getSimpleName() + "s", lang));
      tblTiGoods.getItsCells().get(0).setFontNumber(1);
      tblTiGoods.setAlignHorizontal(EAlignHorizontal.CENTER);
      docMaker.makeDocTableWrapping(tblTiGoods);
      doc.setContentPadding(mmToDocUom(1.0, doc.getUnitOfMeasure()));
      doc.setContentPaddingBottom(mmToDocUom(1.5, doc.getUnitOfMeasure()));
      int rowc;
      if (accSet.getSalTaxIsInvoiceBase()) {
        rowc = 6;
      } else {
        rowc = 8;
      }
      DocTable<WI> tblGoods = docMaker
        .addDocTable(doc, rowc, inv.getItsLines().size() + 1);
      tblGoods.setIsRepeatHead(true);
      tblGoods.getItsRows().get(0).setIsHead(true);
      tblGoods.getItsCells().get(0).setItsContent(this.srvI18n
        .getMsg("invItem", lang).replace(" ", "\n"));
      tblGoods.getItsColumns().get(0).setIsWidthFixed(true);
      tblGoods.getItsColumns().get(0).setWidthInPercentage(60.0);
      tblGoods.getItsCells().get(1).setItsContent(this.srvI18n
        .getMsg("unitOfMeasure", lang).replace(" ", "\n"));
      tblGoods.getItsColumns().get(1).setWraping(EWraping.WRAP_CONTENT);
      tblGoods.getItsCells().get(2).setItsContent(this.srvI18n
        .getMsg("itsPrice", lang).replace(" ", "\n"));
      tblGoods.getItsColumns().get(2).setWraping(EWraping.WRAP_CONTENT);
      tblGoods.getItsCells().get(3).setItsContent(this.srvI18n
        .getMsg("itsQuantity", lang).replace(" ", "\n"));
      tblGoods.getItsColumns().get(3).setWraping(EWraping.WRAP_CONTENT);
      tblGoods.getItsCells().get(4).setItsContent(this.srvI18n
        .getMsg("subtotal", lang).replace(" ", "\n"));
      tblGoods.getItsColumns().get(4).setWraping(EWraping.WRAP_CONTENT);
      tblGoods.getItsCells().get(5)
        .setItsContent(this.srvI18n.getMsg("taxesDescription", lang));
      if (!accSet.getSalTaxIsInvoiceBase()) {
        tblGoods.getItsCells().get(6).setItsContent(this.srvI18n
          .getMsg("totalTaxes", lang).replace(" ", "\n"));
        tblGoods.getItsColumns().get(6).setWraping(EWraping.WRAP_CONTENT);
        tblGoods.getItsCells().get(7).setItsContent(this.srvI18n
          .getMsg("itsTotal", lang).replace(" ", "\n"));
        tblGoods.getItsColumns().get(7).setWraping(EWraping.WRAP_CONTENT);
      }
      for (int i = 0; i < rowc; i++) {
        tblGoods.getItsCells().get(i).setFontNumber(1);
        tblGoods.getItsCells().get(i)
          .setAlignHorizontal(EAlignHorizontal.CENTER);
      }
      int j = 1;
      for (SalesInvoiceLine ln : inv.getItsLines()) {
        if (inv.getForeignCurrency() != null) {
          price = ln.getForeignPrice();
          subtotal = ln.getForeignSubtotal();
          totalTaxes = ln.getForeignTotalTaxes();
          total = ln.getForeignTotal();
        } else {
          price = ln.getItsPrice();
          subtotal = ln.getSubtotal();
          totalTaxes = ln.getTotalTaxes();
          total = ln.getItsTotal();
        }
        int i = 0;
        int k = j * rowc + i++;
        tblGoods.getItsCells().get(k)
          .setItsContent(ln.getInvItem().getItsName());
        k = j * rowc + i++;
        tblGoods.getItsCells().get(k)
          .setAlignHorizontal(EAlignHorizontal.CENTER);
        tblGoods.getItsCells().get(k)
          .setItsContent(ln.getUnitOfMeasure().getItsName());
        k = j * rowc + i++;
        tblGoods.getItsCells().get(k)
          .setAlignHorizontal(EAlignHorizontal.RIGHT);
        tblGoods.getItsCells().get(k)
          .setItsContent(prn(pReqVars, price));
        k = j * rowc + i++;
        tblGoods.getItsCells().get(k)
          .setAlignHorizontal(EAlignHorizontal.RIGHT);
        tblGoods.getItsCells().get(k)
          .setItsContent(prn(pReqVars, ln.getItsQuantity()));
        k = j * rowc + i++;
        tblGoods.getItsCells().get(k)
          .setAlignHorizontal(EAlignHorizontal.RIGHT);
        tblGoods.getItsCells().get(k)
          .setItsContent(prn(pReqVars, subtotal));
        k = j * rowc + i++;
        tblGoods.getItsCells().get(k)
          .setAlignHorizontal(EAlignHorizontal.CENTER);
        tblGoods.getItsCells().get(k)
          .setItsContent(ln.getTaxesDescription());
        if (!accSet.getSalTaxIsInvoiceBase()) {
          k = j * rowc + i++;
          tblGoods.getItsCells().get(k)
            .setAlignHorizontal(EAlignHorizontal.RIGHT);
          tblGoods.getItsCells().get(k)
            .setItsContent(prn(pReqVars, totalTaxes));
          k = j * rowc + i++;
          tblGoods.getItsCells().get(k)
            .setAlignHorizontal(EAlignHorizontal.RIGHT);
          tblGoods.getItsCells().get(k)
            .setItsContent(prn(pReqVars, total));
        }
        j++;
      }
    }
    if (inv.getServices() != null && inv.getServices().size() > 0) {
      doc.setContainerMarginBottom(mmToDocUom(2.0, doc.getUnitOfMeasure()));
      DocTable<WI> tblTiServices = docMaker.addDocTableNoBorder(doc, 1, 1);
      tblTiServices.getItsCells().get(0).setItsContent(this.srvI18n
        .getMsg(SalesInvoiceServiceLine.class.getSimpleName() + "s", lang));
      tblTiServices.getItsCells().get(0).setFontNumber(1);
      tblTiServices.setAlignHorizontal(EAlignHorizontal.CENTER);
      docMaker.makeDocTableWrapping(tblTiServices);
      doc.setContentPadding(mmToDocUom(1.0, doc.getUnitOfMeasure()));
      doc.setContentPaddingBottom(mmToDocUom(1.5, doc.getUnitOfMeasure()));
      int rowc;
      double wd15;
      if (accSet.getSalTaxIsInvoiceBase()) {
        rowc = 6;
        wd15 = 30.0d;
      } else {
        rowc = 8;
        wd15 = 15.0d;
      }
      DocTable<WI> tblServices = docMaker
        .addDocTable(doc, rowc, inv.getServices().size() + 1);
      tblServices.setIsRepeatHead(true);
      tblServices.getItsRows().get(0).setIsHead(true);
      tblServices.getItsCells().get(0)
        .setItsContent(this.srvI18n.getMsg("service", lang));
      tblServices.getItsColumns().get(0).setIsWidthFixed(true);
      tblServices.getItsColumns().get(0).setWidthInPercentage(35.0);
      tblServices.getItsCells().get(1).setItsContent(this.srvI18n
        .getMsg("unitOfMeasure", lang).replace(" ", "\n"));
      tblServices.getItsColumns().get(1).setWraping(EWraping.WRAP_CONTENT);
      tblServices.getItsCells().get(2)
        .setItsContent(this.srvI18n.getMsg("itsPrice", lang));
      tblServices.getItsColumns().get(2).setIsWidthFixed(true);
      tblServices.getItsColumns().get(2).setWidthInPercentage(wd15);
      tblServices.getItsCells().get(3).setItsContent(this.srvI18n
        .getMsg("itsQuantity", lang).replace(" ", "\n"));
      tblServices.getItsColumns().get(3).setWraping(EWraping.WRAP_CONTENT);
      tblServices.getItsCells().get(4).setItsContent(this.srvI18n
        .getMsg("subtotal", lang).replace(" ", "\n"));
      tblServices.getItsColumns().get(4).setWraping(EWraping.WRAP_CONTENT);
      tblServices.getItsCells().get(5)
        .setItsContent(this.srvI18n.getMsg("taxesDescription", lang));
      if (!accSet.getSalTaxIsInvoiceBase()) {
        tblServices.getItsColumns().get(5).setIsWidthFixed(true);
        tblServices.getItsColumns().get(5).setWidthInPercentage(20.0);
        tblServices.getItsCells().get(6)
          .setItsContent(this.srvI18n.getMsg("totalTaxes", lang));
        tblServices.getItsColumns().get(6).setIsWidthFixed(true);
        tblServices.getItsColumns().get(6).setWidthInPercentage(wd15);
        tblServices.getItsCells().get(7)
          .setItsContent(this.srvI18n.getMsg("itsTotal", lang));
        tblServices.getItsColumns().get(7).setIsWidthFixed(true);
        tblServices.getItsColumns().get(7).setWidthInPercentage(wd15);
      }
      for (int i = 0; i < rowc; i++) {
        tblServices.getItsCells().get(i).setFontNumber(1);
        tblServices.getItsCells().get(i)
          .setAlignHorizontal(EAlignHorizontal.CENTER);
      }
      int j = 1;
      for (SalesInvoiceServiceLine ln : inv.getServices()) {
        if (inv.getForeignCurrency() != null) {
          price = ln.getForeignPrice();
          subtotal = ln.getForeignSubtotal();
          totalTaxes = ln.getForeignTotalTaxes();
          total = ln.getForeignTotal();
        } else {
          price = ln.getItsPrice();
          subtotal = ln.getSubtotal();
          totalTaxes = ln.getTotalTaxes();
          total = ln.getItsTotal();
        }
        int i = 0;
        int k = j * rowc + i++;
        tblServices.getItsCells().get(k)
          .setItsContent(ln.getService().getItsName());
        k = j * rowc + i++;
        tblServices.getItsCells().get(k)
          .setAlignHorizontal(EAlignHorizontal.CENTER);
        tblServices.getItsCells().get(k)
          .setItsContent(ln.getUnitOfMeasure().getItsName());
        k = j * rowc + i++;
        tblServices.getItsCells().get(k)
          .setAlignHorizontal(EAlignHorizontal.RIGHT);
        tblServices.getItsCells().get(k)
          .setItsContent(prn(pReqVars, price));
        k = j * rowc + i++;
        tblServices.getItsCells().get(k)
          .setAlignHorizontal(EAlignHorizontal.RIGHT);
        tblServices.getItsCells().get(k)
          .setItsContent(prn(pReqVars, ln.getItsQuantity()));
        k = j * rowc + i++;
        tblServices.getItsCells().get(k)
          .setAlignHorizontal(EAlignHorizontal.RIGHT);
        tblServices.getItsCells().get(k)
          .setItsContent(prn(pReqVars, subtotal));
        k = j * rowc + i++;
        tblServices.getItsCells().get(k)
          .setAlignHorizontal(EAlignHorizontal.CENTER);
        tblServices.getItsCells().get(k)
          .setItsContent(ln.getTaxesDescription());
        if (!accSet.getSalTaxIsInvoiceBase()) {
          k = j * rowc + i++;
          tblServices.getItsCells().get(k)
            .setAlignHorizontal(EAlignHorizontal.RIGHT);
          tblServices.getItsCells().get(k)
            .setItsContent(prn(pReqVars, totalTaxes));
          k = j * rowc + i++;
          tblServices.getItsCells().get(k)
            .setAlignHorizontal(EAlignHorizontal.RIGHT);
          tblServices.getItsCells().get(k)
            .setItsContent(prn(pReqVars, total));
        }
        j++;
      }
    }
    if (inv.getTaxesLines() != null && inv.getTaxesLines().size() > 0) {
      doc.setContainerMarginBottom(mmToDocUom(2.0, doc.getUnitOfMeasure()));
      DocTable<WI> tblTiTaxes = docMaker.addDocTableNoBorder(doc, 1, 1);
      tblTiTaxes.setIsRepeatHead(true);
      tblTiTaxes.getItsRows().get(0).setIsHead(true);
      tblTiTaxes.getItsCells().get(0).setItsContent(this.srvI18n
        .getMsg(SalesInvoiceTaxLine.class.getSimpleName() + "s", lang));
      tblTiTaxes.getItsCells().get(0).setFontNumber(1);
      tblTiTaxes.setAlignHorizontal(EAlignHorizontal.CENTER);
      docMaker.makeDocTableWrapping(tblTiTaxes);
      doc.setContentPadding(mmToDocUom(1.0, doc.getUnitOfMeasure()));
      doc.setContentPaddingBottom(mmToDocUom(1.5, doc.getUnitOfMeasure()));
      int rowc;
      double rowtw;
      if (accSet.getSalTaxIsInvoiceBase()) {
        rowc = 3;
        rowtw = 40.0d;
      } else {
        rowc = 2;
        rowtw = 70.0d;
      }
      DocTable<WI> tblTaxes = docMaker
        .addDocTable(doc, rowc, inv.getTaxesLines().size() + 1);
      tblTaxes.getItsCells().get(0)
        .setItsContent(this.srvI18n.getMsg("tax", lang));
      tblTaxes.getItsColumns().get(0).setIsWidthFixed(true);
      tblTaxes.getItsColumns().get(0).setWidthInPercentage(rowtw);
      if (accSet.getSalTaxIsInvoiceBase()) {
        tblTaxes.getItsCells().get(1)
          .setItsContent(this.srvI18n.getMsg("taxable", lang));
      }
      tblTaxes.getItsCells().get(rowc - 1)
        .setItsContent(this.srvI18n.getMsg("itsTotal", lang));
      for (int i = 0; i < rowc; i++) {
        tblTaxes.getItsCells().get(i).setFontNumber(1);
        tblTaxes.getItsCells().get(i)
          .setAlignHorizontal(EAlignHorizontal.CENTER);
      }
      BigDecimal taxable;
      int j = 1;
      for (SalesInvoiceTaxLine ln : inv.getTaxesLines()) {
        if (inv.getForeignCurrency() != null) {
          taxable = ln.getTaxableInvBasFc();
          total = ln.getForeignTotalTaxes();
        } else {
          taxable = ln.getTaxableInvBas();
          total = ln.getItsTotal();
        }
        int i = 0;
        tblTaxes.getItsCells().get(j * rowc + i++)
          .setItsContent(ln.getTax().getItsName());
        int k = j * rowc + i++;
        if (accSet.getSalTaxIsInvoiceBase()) {
          tblTaxes.getItsCells().get(k)
            .setAlignHorizontal(EAlignHorizontal.RIGHT);
          tblTaxes.getItsCells().get(k)
            .setItsContent(prn(pReqVars, taxable));
          k = j * rowc + i++;
        }
        tblTaxes.getItsCells().get(k)
          .setAlignHorizontal(EAlignHorizontal.RIGHT);
        tblTaxes.getItsCells().get(k)
          .setItsContent(prn(pReqVars, total));
        j++;
      }
    }
    doc.setAlignHoriCont(EAlignHorizontal.RIGHT);
    DocTable<WI> tblRez = docMaker.addDocTableNoBorder(doc, 2, 3);
    tblRez.getItsCells().get(0).setFontNumber(1);
    tblRez.getItsCells().get(0).setItsContent(this.srvI18n.getMsg("subtotal",
      lang) + ": ");
    tblRez.getItsCells().get(1).setFontNumber(1);
    String cnt;
    if (inv.getForeignCurrency() != null) {
      subtotal = inv.getForeignSubtotal();
      totalTaxes = inv.getForeignTotalTaxes();
      total = inv.getForeignTotal();
    } else {
      subtotal = inv.getSubtotal();
      totalTaxes = inv.getTotalTaxes();
      total = inv.getItsTotal();
    }
    if (isPrnCurLf) {
      cnt = curSign + prn(pReqVars, subtotal);
    } else {
      cnt = prn(pReqVars, subtotal) + curSign;
    }
    tblRez.getItsCells().get(1).setItsContent(cnt);
    tblRez.getItsCells().get(2).setFontNumber(1);
    tblRez.getItsCells().get(2).setItsContent(this.srvI18n.getMsg("totalTaxes",
      lang) + ": ");
    tblRez.getItsCells().get(3).setFontNumber(1);
    if (isPrnCurLf) {
      cnt = curSign + prn(pReqVars, totalTaxes);
    } else {
      cnt = prn(pReqVars, totalTaxes) + curSign;
    }
    tblRez.getItsCells().get(3).setItsContent(cnt);
    tblRez.getItsCells().get(4).setFontNumber(1);
    tblRez.getItsCells().get(4).setItsContent(this.srvI18n.getMsg("itsTotal",
      lang) + ": ");
    tblRez.getItsCells().get(5).setFontNumber(1);
    if (isPrnCurLf) {
      cnt = curSign + prn(pReqVars, total);
    } else {
      cnt = prn(pReqVars, total) + curSign;
    }
    tblRez.getItsCells().get(5).setItsContent(cnt);
    tblRez.setAlignHorizontal(EAlignHorizontal.RIGHT);
    docMaker.makeDocTableWrapping(tblRez);
    docMaker.addPagination(doc);
    docMaker.deriveElements(doc);
    pdfMaker.prepareBeforeWrite(docPdf);
    this.pdfFactory.lazyGetPdfWriter().write(null, docPdf, pOus);
  }

  /**
   * <p>Retrieves sales invoice from DB.</p>
   * @param pReqVars additional param
   * @param pInvoice Invoice
   * @param pLang Lang
   * @param pIsOverseas Is Overseas
   * @return SalesInvoice
   * @throws Exception an Exception
   **/
  public final SalesInvoice retrieveEntity(final Map<String, Object> pReqVars,
    final SalesInvoice pInvoice, final String pLang,
      final boolean pIsOverseas) throws Exception {
    SalesInvoice inv = this.srvOrm.retrieveEntity(pReqVars, pInvoice);
    if (pIsOverseas) {
      Set<String> ndFlSil = new HashSet<String>();
      ndFlSil.add("itsId");
      ndFlSil.add("subtotal");
      ndFlSil.add("totalTaxes");
      ndFlSil.add("taxesDescription");
      ndFlSil.add("invItem");
      ndFlSil.add("unitOfMeasure");
      ndFlSil.add("itsQuantity");
      ndFlSil.add("itsPrice");
      ndFlSil.add("itsTotal");
      ndFlSil.add("foreignPrice");
      ndFlSil.add("foreignSubtotal");
      ndFlSil.add("foreignTotalTaxes");
      ndFlSil.add("foreignTotal");
      pReqVars.put("SalesInvoiceLineneededFields", ndFlSil);
      Set<String> ndFlItUm = new HashSet<String>();
      ndFlItUm.add("itsId");
      ndFlItUm.add("itsName");
      pReqVars.put("InvItemneededFields", ndFlItUm);
      pReqVars.put("UnitOfMeasureneededFields", ndFlItUm);
      inv.setItsLines(getSrvOrm().retrieveListByQuery(pReqVars,
        SalesInvoiceLine.class,
          evalSalesInvOverseaseLinesSql(inv.getItsId().toString(), pLang)));
      pReqVars.remove("SalesInvoiceLineneededFields");
      pReqVars.remove("InvItemneededFields");
      pReqVars.remove("UnitOfMeasureneededFields");
    } else {
      SalesInvoiceLine sil = new SalesInvoiceLine();
      sil.setItsOwner(inv);
      pReqVars.put("SalesInvoiceLineitsOwnerdeepLevel", 1); //only ID
      inv.setItsLines(getSrvOrm().
        retrieveListForField(pReqVars, sil, "itsOwner"));
      pReqVars.remove("SalesInvoiceLineitsOwnerdeepLevel");
    }
    //overseas sales usually free from sales taxes
    SalesInvoiceTaxLine sitl = new SalesInvoiceTaxLine();
    sitl.setItsOwner(inv);
    pReqVars.put("SalesInvoiceTaxLineitsOwnerdeepLevel", 1); //only ID
    inv.setTaxesLines(getSrvOrm().
      retrieveListForField(pReqVars, sitl, "itsOwner"));
    pReqVars.remove("SalesInvoiceTaxLineitsOwnerdeepLevel");
    if (pIsOverseas) {
      Set<String> ndFlSil = new HashSet<String>();
      ndFlSil.add("itsId");
      ndFlSil.add("subtotal");
      ndFlSil.add("totalTaxes");
      ndFlSil.add("taxesDescription");
      ndFlSil.add("unitOfMeasure");
      ndFlSil.add("itsQuantity");
      ndFlSil.add("service");
      ndFlSil.add("itsPrice");
      ndFlSil.add("itsTotal");
      ndFlSil.add("foreignPrice");
      ndFlSil.add("foreignSubtotal");
      ndFlSil.add("foreignTotalTaxes");
      ndFlSil.add("foreignTotal");
      pReqVars.put("SalesInvoiceServiceLineneededFields", ndFlSil);
      Set<String> ndFlItUm = new HashSet<String>();
      ndFlItUm.add("itsId");
      ndFlItUm.add("itsName");
      pReqVars.put("ServiceToSaleneededFields", ndFlItUm);
      pReqVars.put("UnitOfMeasureneededFields", ndFlItUm);
      inv.setServices(getSrvOrm().retrieveListByQuery(pReqVars,
    SalesInvoiceServiceLine.class, evalSalesInvOverseaseServiceLinesSql(
  inv.getItsId().toString(), pLang)));
      pReqVars.remove("SalesInvoiceServiceLineneededFields");
      pReqVars.remove("ServiceToSaleneededFields");
      pReqVars.remove("UnitOfMeasureneededFields");
    } else {
      SalesInvoiceServiceLine sisl = new SalesInvoiceServiceLine();
      sisl.setItsOwner(inv);
      pReqVars.put("SalesInvoiceServiceLineitsOwnerdeepLevel", 1); //only ID
      inv.setServices(getSrvOrm().
        retrieveListForField(pReqVars, sisl, "itsOwner"));
    }
    pReqVars.remove("SalesInvoiceServiceLineitsOwnerdeepLevel");
    return inv;
  }

  /**
   * <p>Evaluate I18N overseas sales invoice service lines query.</p>
   * @param pItsOwnerId ID of sales invoice
   * @param pLang lang
   * @return query
   * @throws Exception - an exception
   **/
  public final String evalSalesInvOverseaseServiceLinesSql(
    final String pItsOwnerId, final String pLang) throws Exception {
    if (this.salesInvOverseaseServiceLinesSql == null) {
      synchronized (this) {
        if (this.salesInvOverseaseServiceLinesSql == null) {
          String flName = "/accounting/trade/salesInvOverseaseServiceLines.sql";
          this.salesInvOverseaseServiceLinesSql = loadString(flName);
        }
      }
    }
    String query = this.salesInvOverseaseServiceLinesSql.replace(":ITSOWNER",
      pItsOwnerId).replace(":LANG", pLang);
    return query;
  }

  /**
   * <p>Evaluate I18N overseas sales invoice lines query.</p>
   * @param pItsOwnerId ID of sales invoice
   * @param pLang lang
   * @return query
   * @throws Exception - an exception
   **/
  public final String evalSalesInvOverseaseLinesSql(
    final String pItsOwnerId, final String pLang) throws Exception {
    if (this.salesInvOverseaseLinesSql == null) {
      synchronized (this) {
        if (this.salesInvOverseaseLinesSql == null) {
          String flName = "/accounting/trade/salesInvOverseaseLines.sql";
          this.salesInvOverseaseLinesSql = loadString(flName);
        }
      }
    }
    String query = this.salesInvOverseaseLinesSql.replace(":ITSOWNER",
      pItsOwnerId).replace(":LANG", pLang);
    return query;
  }

  /**
   * <p>Load string file (usually SQL query).</p>
   * @param pFileName file name
   * @return String usually SQL query
   * @throws IOException - IO exception
   **/
  public final String loadString(final String pFileName)
        throws IOException {
    URL urlFile = InvoiceReportPdf.class
      .getResource(pFileName);
    if (urlFile != null) {
      InputStream inputStream = null;
      try {
        inputStream = InvoiceReportPdf.class.getResourceAsStream(pFileName);
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
      (String) pReqVars.get("dseparatorv"),
        (String) pReqVars.get("dgseparatorv"),
          (Integer) pReqVars.get("pricePrecision"),
            (Integer) pReqVars.get("digitsInGroup"));
  }

  /**
   * <p>Convert value from millimeters to document UOM.</p>
   * @param pValue in millimeters
   * @param pUom doc UOM
   * @return in document UOM value
   **/
  public final double mmToDocUom(final double pValue,
    final EUnitOfMeasure pUom) {
    if (pUom.equals(EUnitOfMeasure.INCH)) {
      return pValue / 25.4;
    }
    return pValue;
  }

  //Synchronized getters and setters:
  /**
   * <p>Getter for salesInvOverseaseLinesSql.</p>
   * @return String
   **/
  public final synchronized String getSalesInvOverseaseLinesSql() {
    return this.salesInvOverseaseLinesSql;
  }

  /**
   * <p>Setter for salesInvOverseaseLinesSql.</p>
   * @param pSalesInvOverseaseLinesSql reference
   **/
  public final synchronized void setSalesInvOverseaseLinesSql(
    final String pSalesInvOverseaseLinesSql) {
    this.salesInvOverseaseLinesSql = pSalesInvOverseaseLinesSql;
  }

  /**
   * <p>Getter for salesInvOverseaseServiceLinesSql.</p>
   * @return String
   **/
  public final synchronized String getSalesInvOverseaseServiceLinesSql() {
    return this.salesInvOverseaseServiceLinesSql;
  }

  /**
   * <p>Setter for salesInvOverseaseServiceLinesSql.</p>
   * @param pSalesInvOverseaseServiceLinesSql reference
   **/
  public final synchronized void setSalesInvOverseaseServiceLinesSql(
    final String pSalesInvOverseaseServiceLinesSql) {
    this.salesInvOverseaseServiceLinesSql = pSalesInvOverseaseServiceLinesSql;
  }

  //Simple getters and setters:
  /**
   * <p>Getter for srvOrm.</p>
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

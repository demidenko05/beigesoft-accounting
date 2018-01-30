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

import org.beigesoft.doc.model.Document;
import org.beigesoft.doc.model.DocTable;
import org.beigesoft.doc.model.EWraping;
import org.beigesoft.doc.model.EAlignHorizontal;
import org.beigesoft.doc.model.EPageSize;
import org.beigesoft.doc.model.EPageOrientation;
import org.beigesoft.doc.service.IDocumentMaker;
import org.beigesoft.pdf.model.ERegisteredTtfFont;
import org.beigesoft.pdf.model.PdfDocument;
import org.beigesoft.pdf.service.IPdfFactory;
import org.beigesoft.pdf.service.IPdfMaker;

import org.beigesoft.model.IRequestData;
import org.beigesoft.service.ISrvI18n;
import org.beigesoft.service.IEntityFileReporter;
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.accounting.service.ISrvAccSettings;
import org.beigesoft.accounting.persistable.SalesInvoice;
import org.beigesoft.accounting.persistable.SalesInvoiceLine;
import org.beigesoft.accounting.persistable.SalesInvoiceTaxLine;
import org.beigesoft.accounting.persistable.SalesInvoiceServiceLine;
import org.beigesoft.accounting.persistable.AccSettings;

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
   * <p>Date format.</p>
   **/
  private DateFormat dateFormat =
    DateFormat.getDateInstance(DateFormat.MEDIUM);

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
   * <p>Write PDF report for given invoice to output stream.</p>
   * @param pAddParam additional param
   * @param pInvoice Invoice
   * @param pRequestData Request Data
   * @param pOus servlet output stream
   * @throws Exception - an exception
   **/
  @Override
  public final void makeReport(final Map<String, Object> pAddParam,
    final SalesInvoice pInvoice, final IRequestData pRequestData,
      final OutputStream pOus) throws Exception {
    SalesInvoice inv = this.srvOrm.retrieveEntity(pAddParam, pInvoice);
    SalesInvoiceLine sil = new SalesInvoiceLine();
    sil.setItsOwner(inv);
    inv.setItsLines(getSrvOrm().
      retrieveListForField(pAddParam, sil, "itsOwner"));
    SalesInvoiceTaxLine sitl = new SalesInvoiceTaxLine();
    sitl.setItsOwner(inv);
    inv.setTaxesLines(getSrvOrm().
      retrieveListForField(pAddParam, sitl, "itsOwner"));
    SalesInvoiceServiceLine sisl = new SalesInvoiceServiceLine();
    sisl.setItsOwner(inv);
    inv.setServices(getSrvOrm().
      retrieveListForField(pAddParam, sisl, "itsOwner"));
    Document<WI> doc = this.pdfFactory.lazyGetFctDocument()
      .createDoc(EPageSize.A4, EPageOrientation.PORTRAIT);
    PdfDocument<WI> docPdf = this.pdfFactory.createPdfDoc(doc);
    AccSettings accSet = this.srvAccSettings.lazyGetAccSettings(pAddParam);
    IDocumentMaker<WI> docMaker = this.pdfFactory.lazyGetDocumentMaker();
    docPdf.getPdfInfo().setAuthor("Beigesoft (TM) Accounting, "
      + accSet.getOrganization());
    IPdfMaker<WI> pdfMaker = this.pdfFactory.lazyGetPdfMaker();
    pdfMaker.addFontTtf(docPdf, ERegisteredTtfFont.DEJAVUSERIF_BOLD.toString());
    pdfMaker.addFontTtf(docPdf, ERegisteredTtfFont.DEJAVUSERIF.toString());
    double widthNdot = this.pdfFactory.lazyGetUomHelper()
      .fromPoints(2.0, 300.0, doc.getUnitOfMeasure()); //printer resolution
    doc.setBorder(widthNdot);
    doc.setContentPadding(0.0);
    doc.setContentPaddingBottom(0.5);
    DocTable<WI> tblOwner = docMaker.addDocTableNoBorder(doc, 1, 1);
    tblOwner.getItsCells().get(0).setItsContent(accSet.getOrganization());
    if (accSet.getTaxIdentificationNumber() != null) {
      docMaker.addRowToDocTable(tblOwner);
      tblOwner.getItsCells().get(1).setItsContent(this.srvI18n
        .getMsg("taxIdentificationNumber") + ": "
          + accSet.getTaxIdentificationNumber());
    }
    int n = 0;
    if (accSet.getRegZip() != null) {
      docMaker.addRowToDocTable(tblOwner);
      tblOwner.getItsCells().get(++n).setItsContent(this.srvI18n
        .getMsg("regZip") + ": " + accSet.getRegZip());
    }
    if (accSet.getRegAddress1() != null) {
      docMaker.addRowToDocTable(tblOwner);
      tblOwner.getItsCells().get(++n).setItsContent(this.srvI18n
        .getMsg("regAddress1") + ": " + accSet.getRegAddress1());
    }
    if (accSet.getRegAddress2() != null) {
      docMaker.addRowToDocTable(tblOwner);
      tblOwner.getItsCells().get(++n).setItsContent(this.srvI18n
        .getMsg("regAddress2") + ": " + accSet.getRegAddress2());
    }
    if (accSet.getRegCity() != null) {
      docMaker.addRowToDocTable(tblOwner);
      tblOwner.getItsCells().get(++n).setItsContent(this.srvI18n
        .getMsg("regCity") + ": " + accSet.getRegCity());
    }
    if (accSet.getRegState() != null) {
      docMaker.addRowToDocTable(tblOwner);
      tblOwner.getItsCells().get(++n).setItsContent(this.srvI18n
        .getMsg("regState") + ": " + accSet.getRegState());
    }
    if (accSet.getRegCountry() != null) {
      docMaker.addRowToDocTable(tblOwner);
      tblOwner.getItsCells().get(++n).setItsContent(this.srvI18n
        .getMsg("regCountry") + ": " + accSet.getRegCountry());
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
    String title = this.srvI18n.getMsg("Invoice") + " #" + invNum + " "
      + this.dateFormat.format(inv.getItsDate());
    tblTitle.getItsCells().get(0).setItsContent(title);
    tblTitle.getItsCells().get(0).setFontNumber(1);
    tblTitle.setAlignHorizontal(EAlignHorizontal.CENTER);
    doc.setContainerMarginBottom(1.0);
    docMaker.makeDocTableWrapping(tblTitle);
    DocTable<WI> tblCustomer = docMaker.addDocTableNoBorder(doc, 1, 1);
    tblCustomer.getItsCells().get(0)
      .setItsContent(inv.getCustomer().getItsName());
    n = 0;
    if (inv.getCustomer().getTaxIdentificationNumber() != null) {
      docMaker.addRowToDocTable(tblCustomer);
      tblCustomer.getItsCells().get(++n).setItsContent(this.srvI18n
        .getMsg("taxIdentificationNumber") + ": "
          + inv.getCustomer().getTaxIdentificationNumber());
    }
    if (inv.getCustomer().getRegZip() != null) {
      docMaker.addRowToDocTable(tblCustomer);
      tblCustomer.getItsCells().get(++n).setItsContent(this.srvI18n
        .getMsg("regZip") + ": " + inv.getCustomer().getRegZip());
    }
    if (inv.getCustomer().getRegAddress1() != null) {
      docMaker.addRowToDocTable(tblCustomer);
      tblCustomer.getItsCells().get(++n).setItsContent(this.srvI18n
        .getMsg("regAddress1") + ": "
          + inv.getCustomer().getRegAddress1());
    }
    if (inv.getCustomer().getRegAddress2() != null) {
      docMaker.addRowToDocTable(tblCustomer);
      tblCustomer.getItsCells().get(++n).setItsContent(this.srvI18n
        .getMsg("regAddress2") + ": "
          + inv.getCustomer().getRegAddress2());
    }
    if (inv.getCustomer().getRegCity() != null) {
      docMaker.addRowToDocTable(tblCustomer);
      tblCustomer.getItsCells().get(++n).setItsContent(this.srvI18n
        .getMsg("regCity") + ": " + inv.getCustomer().getRegCity());
    }
    if (inv.getCustomer().getRegState() != null) {
      docMaker.addRowToDocTable(tblCustomer);
      tblCustomer.getItsCells().get(++n).setItsContent(this.srvI18n
        .getMsg("regState") + ": " + inv.getCustomer().getRegState());
    }
    if (inv.getCustomer().getRegCountry() != null) {
      docMaker.addRowToDocTable(tblCustomer);
      tblCustomer.getItsCells().get(++n).setItsContent(this.srvI18n
        .getMsg("regCountry") + ": " + inv.getCustomer().getRegCountry());
    }
    tblCustomer.getItsCells().get(0).setFontNumber(1);
    if (inv.getItsLines() != null && inv.getItsLines().size() > 0) {
      doc.setContainerMarginBottom(2.0);
      DocTable<WI> tblTiGoods = docMaker.addDocTableNoBorder(doc, 1, 1);
      tblTiGoods.getItsCells().get(0).setItsContent(this.srvI18n
        .getMsg(SalesInvoiceLine.class.getSimpleName() + "s"));
      tblTiGoods.getItsCells().get(0).setFontNumber(1);
      tblTiGoods.setAlignHorizontal(EAlignHorizontal.CENTER);
      docMaker.makeDocTableWrapping(tblTiGoods);
      doc.setContentPadding(1.0);
      DocTable<WI> tblGoods = docMaker
        .addDocTable(doc, 8, inv.getItsLines().size() + 1);
      tblGoods.setIsRepeatHead(true);
      tblGoods.getItsRows().get(0).setIsHead(true);
      tblGoods.getItsCells().get(0)
        .setItsContent(this.srvI18n.getMsg("invItem"));
      tblGoods.getItsColumns().get(0).setIsWidthFixed(true);
      tblGoods.getItsColumns().get(0).setWidthInPercentage(60.0);
      tblGoods.getItsCells().get(1)
        .setItsContent(this.srvI18n.getMsg("unitOfMeasure"));
      tblGoods.getItsColumns().get(1).setWraping(EWraping.WRAP_CONTENT);
      tblGoods.getItsCells().get(2)
        .setItsContent(this.srvI18n.getMsg("itsPrice"));
      tblGoods.getItsColumns().get(2).setWraping(EWraping.WRAP_CONTENT);
      tblGoods.getItsCells().get(3)
        .setItsContent(this.srvI18n.getMsg("itsQuantity"));
      tblGoods.getItsColumns().get(3).setWraping(EWraping.WRAP_CONTENT);
      tblGoods.getItsCells().get(4)
        .setItsContent(this.srvI18n.getMsg("subtotal"));
      tblGoods.getItsColumns().get(4).setWraping(EWraping.WRAP_CONTENT);
      tblGoods.getItsCells().get(5)
        .setItsContent(this.srvI18n.getMsg("taxesDescription"));
      tblGoods.getItsCells().get(6)
        .setItsContent(this.srvI18n.getMsg("totalTaxes").replace(" ", "\n"));
      tblGoods.getItsColumns().get(6).setWraping(EWraping.WRAP_CONTENT);
      tblGoods.getItsCells().get(7)
        .setItsContent(this.srvI18n.getMsg("itsTotal"));
      tblGoods.getItsColumns().get(7).setWraping(EWraping.WRAP_CONTENT);
      for (int i = 0; i < 8; i++) {
        tblGoods.getItsCells().get(i).setFontNumber(1);
        tblGoods.getItsCells().get(i)
          .setAlignHorizontal(EAlignHorizontal.CENTER);
      }
      int j = 1;
      for (SalesInvoiceLine ln : inv.getItsLines()) {
        int i = 0;
        tblGoods.getItsCells().get(j * 8 + i++)
          .setItsContent(ln.getInvItem().getItsName());
        tblGoods.getItsCells().get(j * 8 + i++)
          .setItsContent(ln.getUnitOfMeasure().getItsName());
        tblGoods.getItsCells().get(j * 8 + i++)
          .setItsContent(ln.getItsPrice().toString());
        tblGoods.getItsCells().get(j * 8 + i++)
          .setItsContent(ln.getItsQuantity().toString());
        tblGoods.getItsCells().get(j * 8 + i++)
          .setItsContent(ln.getSubtotal().toString());
        tblGoods.getItsCells().get(j * 8 + i++)
          .setItsContent(ln.getTaxesDescription());
        tblGoods.getItsCells().get(j * 8 + i++)
          .setItsContent(ln.getTotalTaxes().toString());
        tblGoods.getItsCells().get(j * 8 + i++)
          .setItsContent(ln.getItsTotal().toString());
        j++;
      }
    }
    if (inv.getServices() != null && inv.getServices().size() > 0) {
      doc.setContainerMarginBottom(2.0);
      DocTable<WI> tblTiServices = docMaker.addDocTableNoBorder(doc, 1, 1);
      tblTiServices.getItsCells().get(0).setItsContent(this.srvI18n
        .getMsg(SalesInvoiceServiceLine.class.getSimpleName() + "s"));
      tblTiServices.getItsCells().get(0).setFontNumber(1);
      tblTiServices.setAlignHorizontal(EAlignHorizontal.CENTER);
      docMaker.makeDocTableWrapping(tblTiServices);
      doc.setContentPadding(1.0);
      DocTable<WI> tblServices = docMaker
        .addDocTable(doc, 5, inv.getServices().size() + 1);
      tblServices.setIsRepeatHead(true);
      tblServices.getItsRows().get(0).setIsHead(true);
      tblServices.getItsCells().get(0)
        .setItsContent(this.srvI18n.getMsg("service"));
      tblServices.getItsColumns().get(0).setIsWidthFixed(true);
      tblServices.getItsColumns().get(0).setWidthInPercentage(35.0);
      tblServices.getItsCells().get(1)
        .setItsContent(this.srvI18n.getMsg("itsPrice"));
      tblServices.getItsColumns().get(1).setIsWidthFixed(true);
      tblServices.getItsColumns().get(1).setWidthInPercentage(15.0);
      tblServices.getItsCells().get(2)
        .setItsContent(this.srvI18n.getMsg("taxesDescription"));
      tblServices.getItsColumns().get(2).setIsWidthFixed(true);
      tblServices.getItsColumns().get(2).setWidthInPercentage(20.0);
      tblServices.getItsCells().get(3)
        .setItsContent(this.srvI18n.getMsg("totalTaxes"));
      tblServices.getItsColumns().get(3).setIsWidthFixed(true);
      tblServices.getItsColumns().get(3).setWidthInPercentage(15.0);
      tblServices.getItsCells().get(4)
        .setItsContent(this.srvI18n.getMsg("itsTotal"));
      tblServices.getItsColumns().get(4).setIsWidthFixed(true);
      tblServices.getItsColumns().get(4).setWidthInPercentage(15.0);
      for (int i = 0; i < 5; i++) {
        tblServices.getItsCells().get(i).setFontNumber(1);
        tblServices.getItsCells().get(i)
          .setAlignHorizontal(EAlignHorizontal.CENTER);
      }
      int j = 1;
      for (SalesInvoiceServiceLine ln : inv.getServices()) {
        int i = 0;
        tblServices.getItsCells().get(j * 5 + i++)
          .setItsContent(ln.getService().getItsName());
        tblServices.getItsCells().get(j * 5 + i++)
          .setItsContent(ln.getItsPrice().toString());
        tblServices.getItsCells().get(j * 5 + i++)
          .setItsContent(ln.getTaxesDescription());
        tblServices.getItsCells().get(j * 5 + i++)
          .setItsContent(ln.getTotalTaxes().toString());
        tblServices.getItsCells().get(j * 5 + i++)
          .setItsContent(ln.getItsTotal().toString());
        j++;
      }
    }
    if (inv.getTaxesLines() != null && inv.getTaxesLines().size() > 0) {
      doc.setContainerMarginBottom(2.0);
      DocTable<WI> tblTiTaxes = docMaker.addDocTableNoBorder(doc, 1, 1);
      tblTiTaxes.setIsRepeatHead(true);
      tblTiTaxes.getItsRows().get(0).setIsHead(true);
      tblTiTaxes.getItsCells().get(0).setItsContent(this.srvI18n
        .getMsg(SalesInvoiceTaxLine.class.getSimpleName() + "s"));
      tblTiTaxes.getItsCells().get(0).setFontNumber(1);
      tblTiTaxes.setAlignHorizontal(EAlignHorizontal.CENTER);
      docMaker.makeDocTableWrapping(tblTiTaxes);
      doc.setContentPadding(1.0);
      DocTable<WI> tblTaxes = docMaker
        .addDocTable(doc, 2, inv.getTaxesLines().size() + 1);
      tblTaxes.getItsCells().get(0)
        .setItsContent(this.srvI18n.getMsg("tax"));
      tblTaxes.getItsColumns().get(0).setIsWidthFixed(true);
      tblTaxes.getItsColumns().get(0).setWidthInPercentage(70.0);
      tblTaxes.getItsCells().get(1)
        .setItsContent(this.srvI18n.getMsg("itsTotal"));
      for (int i = 0; i < 2; i++) {
        tblTaxes.getItsCells().get(i).setFontNumber(1);
        tblTaxes.getItsCells().get(i)
          .setAlignHorizontal(EAlignHorizontal.CENTER);
      }
      int j = 1;
      for (SalesInvoiceTaxLine ln : inv.getTaxesLines()) {
        int i = 0;
        tblTaxes.getItsCells().get(j * 2 + i++)
          .setItsContent(ln.getTax().getItsName());
        tblTaxes.getItsCells().get(j * 2 + i++)
          .setItsContent(ln.getItsTotal().toString());
        j++;
      }
    }
    doc.setAlignHoriCont(EAlignHorizontal.RIGHT);
    DocTable<WI> tblRez = docMaker
      .addDocTableNoBorder(doc, 1, 3);
    tblRez.getItsCells().get(0).setFontNumber(1);
    tblRez.getItsCells().get(0).setItsContent(this.srvI18n.getMsg("subtotal")
      + ": " + inv.getSubtotal() + " " + accSet.getCurrency().getItsName());
    tblRez.getItsCells().get(1).setFontNumber(1);
    tblRez.getItsCells().get(1).setItsContent(this.srvI18n.getMsg("totalTaxes")
      + ": " + inv.getTotalTaxes() + " " + accSet.getCurrency().getItsName());
    tblRez.getItsCells().get(2).setFontNumber(1);
    tblRez.getItsCells().get(2).setItsContent(this.srvI18n.getMsg("itsTotal")
      + ": " + inv.getItsTotal() + " " + accSet.getCurrency().getItsName());
    tblRez.setAlignHorizontal(EAlignHorizontal.RIGHT);
    docMaker.makeDocTableWrapping(tblRez);
    docMaker.addPagination(doc);
    docMaker.deriveElements(doc);
    pdfMaker.prepareBeforeWrite(docPdf);
    this.pdfFactory.lazyGetPdfWriter().write(null, docPdf, pOus);
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
}

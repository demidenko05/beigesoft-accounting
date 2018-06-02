package org.beigesoft.accounting.factory;

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

import java.util.Map;
import java.util.HashMap;

import org.beigesoft.factory.IFactoryAppBeansByName;
import org.beigesoft.exception.ExceptionWithCode;
import org.beigesoft.log.ILogger;
import org.beigesoft.service.IEntityFileReporter;
import org.beigesoft.service.ISrvNumberToString;
import org.beigesoft.service.ISrvI18n;
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.pdf.service.IPdfFactory;
import org.beigesoft.accounting.service.ISrvAccSettings;
import org.beigesoft.accounting.report.InvoiceReportPdf;

/**
 * <p>Entities file reporters to PDF factory.</p>
 *
 * @param <RS> platform dependent RDBMS recordset
 * @param <WI> writing instrument type
 * @author Yury Demidenko
 */
public class FctEntitiesFileReportersPdf<RS, WI>
  implements IFactoryAppBeansByName<IEntityFileReporter> {

  //outer initialized:
  /**
   * <p>Business service for accounting settings.</p>
   **/
  private ISrvAccSettings srvAccSettings;

  /**
   * <p>I18N service.</p>
   **/
  private ISrvI18n srvI18n;

  /**
   * <p>Logger.</p>
   **/
  private ILogger logger;

  /**
   * <p>ORM service.</p>
   **/
  private ISrvOrm<RS> srvOrm;

  /**
   * <p>PDF Factory.</p>
   **/
  private IPdfFactory<WI> pdfFactory;

  /**
   * <p>Service print number.</p>
   **/
  private ISrvNumberToString srvNumberToString;

  //inner initialized:
  /**
   * <p>Reporters map.</p>
   **/
  private final Map<String, IEntityFileReporter> reportersMap =
      new HashMap<String, IEntityFileReporter>();

  /**
   * <p>Get bean in lazy mode (if bean is null then initialize it).</p>
   * @param pAddParam additional param
   * @param pBeanName - bean name
   * @return requested bean
   * @throws Exception - an exception
   */
  @Override
  public final IEntityFileReporter lazyGet(
    final Map<String, Object> pAddParam,
      final String pBeanName) throws Exception {
    IEntityFileReporter proc =
      this.reportersMap.get(pBeanName);
    if (proc == null) {
      // locking:
      synchronized (this) {
        // make sure again whether it's null after locking:
        proc = this.reportersMap.get(pBeanName);
        if (proc == null
          && pBeanName.equals(InvoiceReportPdf.class.getSimpleName())) {
          proc = lazyGetInvoiceReportPdf(pAddParam);
        }
      }
    }
    if (proc == null) {
      throw new ExceptionWithCode(ExceptionWithCode.CONFIGURATION_MISTAKE,
        "There is no entity processor with name " + pBeanName);
    }
    return proc;
  }

  /**
   * <p>Set bean.</p>
   * @param pBeanName - bean name
   * @param pBean bean
   * @throws Exception - an exception
   */
  @Override
  public final synchronized void set(final String pBeanName,
    final IEntityFileReporter pBean) throws Exception {
    this.reportersMap.put(pBeanName, pBean);
  }


  /**
   * <p>Get InvoiceReportPdf (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested InvoiceReportPdf
   * @throws Exception - an exception
   */
  protected final InvoiceReportPdf<RS, WI>
    lazyGetInvoiceReportPdf(
      final Map<String, Object> pAddParam) throws Exception {
    @SuppressWarnings("unchecked")
    InvoiceReportPdf<RS, WI> rep =
      (InvoiceReportPdf<RS, WI>)
        this.reportersMap
          .get(InvoiceReportPdf.class.getSimpleName());
    if (rep == null) {
      rep = new InvoiceReportPdf<RS, WI>();
      rep.setSrvAccSettings(getSrvAccSettings());
      rep.setSrvNumberToString(getSrvNumberToString());
      rep.setSrvI18n(getSrvI18n());
      rep.setSrvOrm(getSrvOrm());
      rep.setPdfFactory(this.pdfFactory);
      //assigning fully initialized object:
      this.reportersMap.put(InvoiceReportPdf.class.getSimpleName(), rep);
    }
    return rep;
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
   * <p>Getter for logger.</p>
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
   * <p>Getter for pdfFactory.</p>
   * @return IPdfFactory<WI>
   **/
  public final IPdfFactory<WI> getPdfFactory() {
    return this.pdfFactory;
  }

  /**
   * <p>Setter for pdfFactory.</p>
   * @param pPdfFactory reference
   **/
  public final void setPdfFactory(final IPdfFactory<WI> pPdfFactory) {
    this.pdfFactory = pPdfFactory;
  }

  /**
   * <p>Getter for reportersMap.</p>
   * @return final Map<String, IEntityFileReporter>
   **/
  public final Map<String, IEntityFileReporter> getReportersMap() {
    return this.reportersMap;
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

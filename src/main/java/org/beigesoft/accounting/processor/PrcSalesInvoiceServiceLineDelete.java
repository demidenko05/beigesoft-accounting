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

import org.beigesoft.model.IRequestData;
import org.beigesoft.service.IEntityProcessor;
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.accounting.persistable.AccSettings;
import org.beigesoft.accounting.persistable.TaxDestination;
import org.beigesoft.accounting.persistable.SalesInvoiceServiceLine;
import org.beigesoft.accounting.persistable.SalesInvoice;
import org.beigesoft.accounting.persistable.SalesInvoiceTaxLine;
import org.beigesoft.accounting.persistable.SalesInvoiceServiceTaxLine;
import org.beigesoft.accounting.service.ISrvAccSettings;

/**
 * <p>Service that delete SalesInvoiceServiceLine from DB.</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class PrcSalesInvoiceServiceLineDelete<RS>
  implements IEntityProcessor<SalesInvoiceServiceLine, Long> {

  /**
   * <p>ORM service.</p>
   **/
  private ISrvOrm<RS> srvOrm;

  /**
   * <p>Entity Delete delegator.</p>
   **/
  private IEntityProcessor<SalesInvoiceServiceLine, Long>
    prcAccEntityPbDelete;

  /**
   * <p>Business service for accounting settings.</p>
   **/
  private ISrvAccSettings srvAccSettings;

  /**
   * <p>It makes line and total for owner.</p>
   **/
  private UtlInvLine<RS, SalesInvoice, SalesInvoiceServiceLine,
    SalesInvoiceTaxLine, SalesInvoiceServiceTaxLine> utlInvLine;


  /**
   * <p>Process entity request.</p>
   * @param pReqVars additional param, e.g. return this line's
   * document in "nextEntity" for farther process
   * @param pRequestData Request Data
   * @param pEntity Entity to process
   * @return Entity processed for farther process or null
   * @throws Exception - an exception
   **/
  @Override
  public final SalesInvoiceServiceLine process(
    final Map<String, Object> pReqVars,
      final SalesInvoiceServiceLine pEntity,
        final IRequestData pRequestData) throws Exception {
    getSrvOrm().deleteEntityWhere(pReqVars,
      SalesInvoiceServiceTaxLine.class, "ITSOWNER=" + pEntity.getItsId());
    this.prcAccEntityPbDelete.process(pReqVars, pEntity, pRequestData);
    // Beige-Orm refresh:
    pReqVars.put("DebtorCreditortaxDestinationdeepLevel", 2);
    pEntity.setItsOwner(getSrvOrm()
      .retrieveEntity(pReqVars, pEntity.getItsOwner()));
    pReqVars.remove("DebtorCreditortaxDestinationdeepLevel");
    // optimistic locking (dirty check):
    Long ownerVersion = Long.valueOf(pRequestData
      .getParameter(SalesInvoice.class.getSimpleName() + ".ownerVersion"));
    pEntity.getItsOwner().setItsVersion(ownerVersion);
    AccSettings as = getSrvAccSettings().lazyGetAccSettings(pReqVars);
    TaxDestination txRules = this.utlInvLine.revealTaxRules(pReqVars,
      pEntity.getItsOwner(), as, as.getIsExtractSalesTaxFromSales());
    this.utlInvLine.makeTotals(pReqVars, pEntity, as, txRules);
    pReqVars.put("nextEntity", pEntity.getItsOwner());
    pReqVars.put("nameOwnerEntity", SalesInvoice.class.getSimpleName());
    return null;
  }

  //Simple getters and setters:
  /**
   * <p>Getter for utlInvLine.</p>
   * @return UtlInvLine<RS, SalesInvoice, SalesInvoiceServiceLine,
   *  SalesInvoiceTaxLine, SalesInvoiceServiceTaxLine>
   **/
  public final UtlInvLine<RS, SalesInvoice, SalesInvoiceServiceLine,
    SalesInvoiceTaxLine, SalesInvoiceServiceTaxLine> getUtlInvLine() {
    return this.utlInvLine;
  }

  /**
   * <p>Setter for utlInvLine.</p>
   * @param pUtlInvLine reference
   **/
  public final void setUtlInvLine(final UtlInvLine<RS, SalesInvoice,
    SalesInvoiceServiceLine, SalesInvoiceTaxLine,
      SalesInvoiceServiceTaxLine> pUtlInvLine) {
    this.utlInvLine = pUtlInvLine;
  }

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
   * <p>Getter for prcAccEntityPbDelete.</p>
   * @return PrcAccEntityPbDelete<RS, SalesInvoiceServiceLine, Long>
   **/
  public final IEntityProcessor<SalesInvoiceServiceLine, Long>
    getPrcAccEntityPbDelete() {
    return this.prcAccEntityPbDelete;
  }

  /**
   * <p>Setter for prcAccEntityPbDelete.</p>
   * @param pPrcAccEntityPbDelete reference
   **/
  public final void setPrcAccEntityPbDelete(
    final IEntityProcessor<SalesInvoiceServiceLine, Long>
      pPrcAccEntityPbDelete) {
    this.prcAccEntityPbDelete = pPrcAccEntityPbDelete;
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

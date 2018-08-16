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

import org.beigesoft.model.IRequestData;
import org.beigesoft.orm.processor.PrcEntityCreate;
import org.beigesoft.accounting.persistable.SalesInvoiceServiceLine;
import org.beigesoft.service.IEntityProcessor;

/**
 * <p>Service that create SalesInvoiceServiceLine.</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class PrcSalesInvoiceServiceLineCreate<RS>
  implements IEntityProcessor<SalesInvoiceServiceLine, Long> {

  /**
   * <p>Entity create delegator.</p>
   **/
  private PrcEntityCreate<RS, SalesInvoiceServiceLine, Long> prcEntityCreate;

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
      final SalesInvoiceServiceLine pEntityPb,
        final IRequestData pRequestData) throws Exception {
    SalesInvoiceServiceLine entity = this.prcEntityCreate
      .process(pReqVars, pEntityPb, pRequestData);
    pReqVars.put("DebtorCreditortaxDestinationdeepLevel", 2);
    entity.setItsOwner(this.prcEntityCreate.getSrvOrm()
      .retrieveEntity(pReqVars, entity.getItsOwner()));
    pReqVars.remove("DebtorCreditortaxDestinationdeepLevel");
    return entity;
  }

  //Simple getters and setters:
  /**
   * <p>Getter for prcEntityCreate.</p>
   * @return PrcEntityCreate<RS, SalesInvoiceServiceLine, Long>
   **/
  public final
    PrcEntityCreate<RS, SalesInvoiceServiceLine, Long> getPrcEntityCreate() {
    return this.prcEntityCreate;
  }

  /**
   * <p>Setter for prcEntityCreate.</p>
   * @param pPrcEntityCreate reference
   **/
  public final void setPrcEntityCreate(
    final PrcEntityCreate<RS, SalesInvoiceServiceLine, Long> pPrcEntityCreate) {
    this.prcEntityCreate = pPrcEntityCreate;
  }
}

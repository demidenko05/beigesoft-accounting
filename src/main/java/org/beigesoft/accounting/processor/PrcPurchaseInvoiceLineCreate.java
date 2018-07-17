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
import org.beigesoft.accounting.persistable.PurchaseInvoiceLine;
import org.beigesoft.service.IEntityProcessor;

/**
 * <p>Service that create PurchaseInvoiceLine.</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class PrcPurchaseInvoiceLineCreate<RS>
  implements IEntityProcessor<PurchaseInvoiceLine, Long> {

  /**
   * <p>Entity create delegator.</p>
   **/
  private PrcEntityCreate<RS, PurchaseInvoiceLine, Long> prcEntityCreate;

  /**
   * <p>Process entity request.</p>
   * @param pAddParam additional param, e.g. return this line's
   * document in "nextEntity" for farther process
   * @param pRequestData Request Data
   * @param pEntity Entity to process
   * @return Entity processed for farther process or null
   * @throws Exception - an exception
   **/
  @Override
  public final PurchaseInvoiceLine process(
    final Map<String, Object> pAddParam,
      final PurchaseInvoiceLine pEntityPb,
        final IRequestData pRequestData) throws Exception {
    PurchaseInvoiceLine entity = this.prcEntityCreate
      .process(pAddParam, pEntityPb, pRequestData);
    entity.setItsOwner(this.prcEntityCreate.getSrvOrm()
      .retrieveEntity(pAddParam, entity.getItsOwner()));
    return entity;
  }

  //Simple getters and setters:
  /**
   * <p>Getter for prcEntityCreate.</p>
   * @return PrcEntityCreate<RS, PurchaseInvoiceLine, Long>
   **/
  public final
    PrcEntityCreate<RS, PurchaseInvoiceLine, Long> getPrcEntityCreate() {
    return this.prcEntityCreate;
  }

  /**
   * <p>Setter for prcEntityCreate.</p>
   * @param pPrcEntityCreate reference
   **/
  public final void setPrcEntityCreate(
    final PrcEntityCreate<RS, PurchaseInvoiceLine, Long> pPrcEntityCreate) {
    this.prcEntityCreate = pPrcEntityCreate;
  }
}

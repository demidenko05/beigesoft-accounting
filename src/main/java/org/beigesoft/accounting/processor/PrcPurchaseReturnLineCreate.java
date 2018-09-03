package org.beigesoft.accounting.processor;

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
import java.util.Set;
import java.util.HashSet;

import org.beigesoft.model.IRequestData;
import org.beigesoft.orm.processor.PrcEntityCreate;
import org.beigesoft.accounting.persistable.PurchaseReturnLine;
import org.beigesoft.service.IEntityProcessor;

/**
 * <p>Service that create PurchaseReturnLine.</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class PrcPurchaseReturnLineCreate<RS>
  implements IEntityProcessor<PurchaseReturnLine, Long> {

  /**
   * <p>Entity create delegator.</p>
   **/
  private PrcEntityCreate<RS, PurchaseReturnLine, Long> prcEntityCreate;

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
  public final PurchaseReturnLine process(final Map<String, Object> pReqVars,
    final PurchaseReturnLine pEntityPb,
      final IRequestData pRequestData) throws Exception {
    PurchaseReturnLine entity = this.prcEntityCreate
      .process(pReqVars, pEntityPb, pRequestData);
    pReqVars.put("PurchaseInvoicevendordeepLevel", 3);
    Set<String> ndFlDc = new HashSet<String>();
    ndFlDc.add("itsId");
    ndFlDc.add("isForeigner");
    ndFlDc.add("taxDestination");
    pReqVars.put("DebtorCreditorneededFields", ndFlDc);
    Set<String> ndFlInv = new HashSet<String>();
    ndFlInv.add("itsId");
    ndFlInv.add("vendor");
    ndFlInv.add("omitTaxes");
    ndFlInv.add("hasMadeAccEntries");
    pReqVars.put("PurchaseInvoiceneededFields", ndFlInv);
    entity.setItsOwner(this.prcEntityCreate.getSrvOrm()
      .retrieveEntity(pReqVars, entity.getItsOwner()));
    pReqVars.remove("DebtorCreditorneededFields");
    pReqVars.remove("PurchaseInvoiceneededFields");
    pReqVars.remove("PurchaseInvoicevendordeepLevel");
    return entity;
  }

  //Simple getters and setters:
  /**
   * <p>Getter for prcEntityCreate.</p>
   * @return PrcEntityCreate<RS, PurchaseReturnLine, Long>
   **/
  public final
    PrcEntityCreate<RS, PurchaseReturnLine, Long> getPrcEntityCreate() {
    return this.prcEntityCreate;
  }

  /**
   * <p>Setter for prcEntityCreate.</p>
   * @param pPrcEntityCreate reference
   **/
  public final void setPrcEntityCreate(
    final PrcEntityCreate<RS, PurchaseReturnLine, Long> pPrcEntityCreate) {
    this.prcEntityCreate = pPrcEntityCreate;
  }
}

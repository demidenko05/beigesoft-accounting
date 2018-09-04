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

import java.util.Set;
import java.util.HashSet;
import java.util.Map;

import org.beigesoft.model.IRequestData;
import org.beigesoft.service.IEntityProcessor;
import org.beigesoft.accounting.persistable.PurchaseReturn;

/**
 * <p>Process that retrieves purchase return for editing.</p>
 *
 * @author Yury Demidenko
 */
public class PrcPurchRetGfe implements IEntityProcessor<PurchaseReturn, Long> {

  /**
   * <p>Acc-EntityPb Edit/Confirm delete delegator.</p>
   **/
  private IEntityProcessor<PurchaseReturn, Long> prcEntityPbEditDelete;


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
  public final PurchaseReturn process(final Map<String, Object> pReqVars,
    final PurchaseReturn pEntity,
      final IRequestData pRequestData) throws Exception {
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
    PurchaseReturn invoice = this.prcEntityPbEditDelete
      .process(pReqVars, pEntity, pRequestData);
    pReqVars.remove("DebtorCreditorneededFields");
    pReqVars.remove("PurchaseInvoiceneededFields");
    pReqVars.remove("PurchaseInvoicevendordeepLevel");
    return invoice;
  }
  //Simple getters and setters:
  /**
   * <p>Getter for prcEntityPbEditDelete.</p>
   * @return IEntityProcessor<IInvoice, Long>
   **/
  public final IEntityProcessor<PurchaseReturn, Long>
    getPrcEntityPbEditDelete() {
    return this.prcEntityPbEditDelete;
  }

  /**
   * <p>Setter for prcEntityPbEditDelete.</p>
   * @param pPrcEntityPbEditDelete reference
   **/
  public final void setPrcEntityPbEditDelete(
    final IEntityProcessor<PurchaseReturn, Long> pPrcEntityPbEditDelete) {
    this.prcEntityPbEditDelete = pPrcEntityPbEditDelete;
  }
}

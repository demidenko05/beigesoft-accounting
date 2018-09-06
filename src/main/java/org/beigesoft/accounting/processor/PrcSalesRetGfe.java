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
import org.beigesoft.accounting.persistable.SalesReturn;

/**
 * <p>Process that retrieves sales return for editing.</p>
 *
 * @author Yury Demidenko
 */
public class PrcSalesRetGfe implements IEntityProcessor<SalesReturn, Long> {

  /**
   * <p>Acc-EntityPb Edit/Confirm delete delegator.</p>
   **/
  private IEntityProcessor<SalesReturn, Long> prcEntityPbEditDelete;


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
  public final SalesReturn process(final Map<String, Object> pReqVars,
    final SalesReturn pEntity,
      final IRequestData pRequestData) throws Exception {
    pReqVars.put("DebtorCreditortaxDestinationdeepLevel", 2);
    Set<String> ndFlDc = new HashSet<String>();
    ndFlDc.add("itsId");
    ndFlDc.add("itsName");
    ndFlDc.add("isForeigner");
    ndFlDc.add("taxDestination");
    pReqVars.put("DebtorCreditorneededFields", ndFlDc);
    SalesReturn invoice = this.prcEntityPbEditDelete
      .process(pReqVars, pEntity, pRequestData);
    pReqVars.remove("DebtorCreditorneededFields");
    pReqVars.remove("DebtorCreditortaxDestinationdeepLevel");
    return invoice;
  }
  //Simple getters and setters:
  /**
   * <p>Getter for prcEntityPbEditDelete.</p>
   * @return IEntityProcessor<IInvoice, Long>
   **/
  public final IEntityProcessor<SalesReturn, Long>
    getPrcEntityPbEditDelete() {
    return this.prcEntityPbEditDelete;
  }

  /**
   * <p>Setter for prcEntityPbEditDelete.</p>
   * @param pPrcEntityPbEditDelete reference
   **/
  public final void setPrcEntityPbEditDelete(
    final IEntityProcessor<SalesReturn, Long> pPrcEntityPbEditDelete) {
    this.prcEntityPbEditDelete = pPrcEntityPbEditDelete;
  }
}

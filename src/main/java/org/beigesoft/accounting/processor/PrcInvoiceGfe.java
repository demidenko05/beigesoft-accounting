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
import org.beigesoft.service.IEntityProcessor;
import org.beigesoft.accounting.persistable.IInvoice;

/**
 * <p>Process that retrieves purchase/sales invoice for editing.</p>
 *
 * @param <RS> platform dependent record set type
 * @param <T> invoice type
 * @author Yury Demidenko
 */
public class PrcInvoiceGfe<RS, T extends IInvoice>
  implements IEntityProcessor<T, Long> {

  /**
   * <p>Acc-EntityPb Edit/Confirm delete delegator.</p>
   **/
  private IEntityProcessor<T, Long> prcEntityPbEditDelete;


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
  public final T process(final Map<String, Object> pReqVars,
    final T pEntity,
      final IRequestData pRequestData) throws Exception {
    pReqVars.put("DebtorCreditortaxDestinationdeepLevel", 2);
    T invoice = this.prcEntityPbEditDelete
      .process(pReqVars, pEntity, pRequestData);
    pReqVars.remove("DebtorCreditortaxDestinationdeepLevel");
    return invoice;
  }
  //Simple getters and setters:
  /**
   * <p>Getter for prcEntityPbEditDelete.</p>
   * @return IEntityProcessor<IInvoice, Long>
   **/
  public final IEntityProcessor<T, Long>
    getPrcEntityPbEditDelete() {
    return this.prcEntityPbEditDelete;
  }

  /**
   * <p>Setter for prcEntityPbEditDelete.</p>
   * @param pPrcEntityPbEditDelete reference
   **/
  public final void setPrcEntityPbEditDelete(
    final IEntityProcessor<T, Long> pPrcEntityPbEditDelete) {
    this.prcEntityPbEditDelete = pPrcEntityPbEditDelete;
  }
}

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

import java.util.Date;
import java.util.Map;
import java.math.BigDecimal;

import org.beigesoft.model.IRequestData;
import org.beigesoft.service.IEntityProcessor;
import org.beigesoft.accounting.persistable.PaymentTo;

/**
 * <p>Service that make PaymentTo copy from DB.</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class PrcPaymentToCopy<RS>
  implements IEntityProcessor<PaymentTo, Long> {

  /**
   * <p>Acc-entity copy delegator.</p>
   **/
  private IEntityProcessor<PaymentTo, Long> prcAccEntityPbWithSubaccCopy;

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
  public final PaymentTo process(
    final Map<String, Object> pAddParam,
      final PaymentTo pEntity,
        final IRequestData pRequestData) throws Exception {
    PaymentTo entity = this.prcAccEntityPbWithSubaccCopy
      .process(pAddParam, pEntity, pRequestData);
    entity.setReversedId(null);
    entity.setItsTotal(BigDecimal.ZERO);
    entity.setItsDate(new Date());
    entity.setHasMadeAccEntries(false);
    entity.setPurchaseInvoice(null);
    return entity;
  }

  //Simple getters and setters:
  /**
   * <p>Getter for prcAccEntityPbWithSubaccCopy.</p>
   * @return PrcAccEntityPbWithSubaccCopy<RS, PaymentTo, Long>
   **/
  public final IEntityProcessor<PaymentTo, Long>
    getPrcAccEntityPbWithSubaccCopy() {
    return this.prcAccEntityPbWithSubaccCopy;
  }

  /**
   * <p>Setter for prcAccEntityPbWithSubaccCopy.</p>
   * @param pPrcAccEntityPbWithSubaccCopy reference
   **/
  public final void setPrcAccEntityPbWithSubaccCopy(
    final IEntityProcessor<PaymentTo, Long> pPrcAccEntityPbWithSubaccCopy) {
    this.prcAccEntityPbWithSubaccCopy = pPrcAccEntityPbWithSubaccCopy;
  }
}

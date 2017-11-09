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
import org.beigesoft.accounting.persistable.base.ADocWithTaxesPayments;

/**
 * <p>Service that make document with taxes and payments/prepayments copy
 * from DB.
 * Those documents are: PurchaseInvoice, SalesInvoice.</p>
 *
 * @param <RS> platform dependent record set type
 * @param <T> entity type
 * @author Yury Demidenko
 */
public class PrcAccDocWithTaxesPaymentsCopy<RS, T extends ADocWithTaxesPayments>
  implements IEntityProcessor<T, Long> {

  /**
   * <p>Acc-entity copy delegator.</p>
   **/
  private IEntityProcessor<T, Long> prcAccEntityPbCopy;

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
  public final T process(
    final Map<String, Object> pAddParam,
      final T pEntity, final IRequestData pRequestData) throws Exception {
    T entity = this.prcAccEntityPbCopy
      .process(pAddParam, pEntity, pRequestData);
    entity.setReversedId(null);
    entity.setItsTotal(BigDecimal.ZERO);
    entity.setItsDate(new Date());
    entity.setHasMadeAccEntries(false);
    entity.setTotalTaxes(BigDecimal.ZERO);
    entity.setSubtotal(BigDecimal.ZERO);
    entity.setPaymentTotal(BigDecimal.ZERO);
    entity.setPaymentDescription("");
    return entity;
  }

  //Simple getters and setters:
  /**
   * <p>Getter for prcAccEntityPbCopy.</p>
   * @return PrcAccEntityPbCopy<RS, T, Long>
   **/
  public final IEntityProcessor<T, Long> getPrcAccEntityPbCopy() {
    return this.prcAccEntityPbCopy;
  }

  /**
   * <p>Setter for prcAccEntityPbCopy.</p>
   * @param pPrcAccEntityPbCopy reference
   **/
  public final void setPrcAccEntityPbCopy(
    final IEntityProcessor<T, Long> pPrcAccEntityPbCopy) {
    this.prcAccEntityPbCopy = pPrcAccEntityPbCopy;
  }
}

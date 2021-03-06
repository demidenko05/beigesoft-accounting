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

import org.beigesoft.exception.ExceptionWithCode;
import org.beigesoft.model.IRequestData;
import org.beigesoft.accounting.persistable.SalesInvoiceLine;
import org.beigesoft.service.IEntityProcessor;

/**
 * <p>Service that make SalesInvoiceLine copy from DB for reverse.</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class PrcSalesInvoiceLineGfr<RS>
  implements IEntityProcessor<SalesInvoiceLine, Long> {

  /**
   * <p>Acc-EntityPb Copy delegator.</p>
   **/
  private IEntityProcessor<SalesInvoiceLine, Long> prcAccEntityPbCopy;

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
  public final SalesInvoiceLine process(
    final Map<String, Object> pAddParam,
      final SalesInvoiceLine pEntityPb,
        final IRequestData pRequestData) throws Exception {
    SalesInvoiceLine entity = this.prcAccEntityPbCopy
      .process(pAddParam, pEntityPb, pRequestData);
    if (entity.getReversedId() != null) {
      throw new ExceptionWithCode(ExceptionWithCode.FORBIDDEN,
        "attempt_to_reverse_reversed");
    }
    entity.setReversedId(pEntityPb.getItsId());
    entity.setItsQuantity(entity.getItsQuantity().negate());
    entity.setSubtotal(entity.getSubtotal().negate());
    entity.setTotalTaxes(entity.getTotalTaxes().negate());
    entity.setItsTotal(entity.getItsTotal().negate());
    entity.setForeignTotal(entity.getForeignTotal().negate());
    entity.setForeignSubtotal(entity.getForeignSubtotal().negate());
    entity.setForeignTotalTaxes(entity.getForeignTotalTaxes().negate());
    return entity;
  }

  //Simple getters and setters:
  /**
   * <p>Getter for prcAccEntityPbCopy.</p>
   * @return PrcAccEntityPbCopy<RS, SalesInvoiceLine, Long>
   **/
  public final IEntityProcessor<SalesInvoiceLine, Long>
    getPrcAccEntityPbCopy() {
    return this.prcAccEntityPbCopy;
  }

  /**
   * <p>Setter for prcAccEntityPbCopy.</p>
   * @param pPrcAccEntityPbCopy reference
   **/
  public final void setPrcAccEntityPbCopy(
    final IEntityProcessor<SalesInvoiceLine, Long> pPrcAccEntityPbCopy) {
    this.prcAccEntityPbCopy = pPrcAccEntityPbCopy;
  }
}

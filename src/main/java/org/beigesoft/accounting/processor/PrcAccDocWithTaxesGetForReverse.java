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
import java.math.BigDecimal;

import org.beigesoft.model.IRequestData;
import org.beigesoft.service.IEntityProcessor;
import org.beigesoft.accounting.persistable.base.ADocWithTaxes;

/**
 * <p>Service that make document copy from DB and prepare for reversing.
 * Those documents are: PurchaseInvoice, SalesInvoice, SalesReturn,
 * PurchaseReturn.</p>
 *
 * @param <RS> platform dependent record set type
 * @param <T> entity type
 * @author Yury Demidenko
 */
public class PrcAccDocWithTaxesGetForReverse<RS, T extends ADocWithTaxes>
  implements IEntityProcessor<T, Long> {

  /**
   * <p>Doc reverse delegator.</p>
   **/
  private IEntityProcessor<T, Long> prcDocReverse;

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
    T entity = this.prcDocReverse.process(pAddParam, pEntity, pRequestData);
    entity.setTotalTaxes(BigDecimal.ZERO);
    entity.setSubtotal(BigDecimal.ZERO);
    return entity;
  }

  //Simple getters and setters:
  /**
   * <p>Getter for prcDocReverse.</p>
   * @return PrcDocReverse<RS, T, Long>
   **/
  public final IEntityProcessor<T, Long> getPrcDocReverse() {
    return this.prcDocReverse;
  }

  /**
   * <p>Setter for prcDocReverse.</p>
   * @param pPrcDocReverse reference
   **/
  public final void setPrcDocReverse(
    final IEntityProcessor<T, Long> pPrcDocReverse) {
    this.prcDocReverse = pPrcDocReverse;
  }
}

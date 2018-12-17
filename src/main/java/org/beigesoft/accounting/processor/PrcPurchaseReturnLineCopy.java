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
import org.beigesoft.accounting.persistable.PurchaseReturnLine;
import org.beigesoft.service.IEntityProcessor;
import org.beigesoft.orm.processor.PrcEntityPbCopy;

/**
 * <p>Service that make PurchaseReturnLine copy from DB.</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class PrcPurchaseReturnLineCopy<RS>
  implements IEntityProcessor<PurchaseReturnLine, Long> {

  /**
   * <p>Acc-EntityPb Copy delegator.</p>
   **/
  private PrcEntityPbCopy<RS, PurchaseReturnLine> prcAccEntityPbCopy;

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
  public final PurchaseReturnLine process(
    final Map<String, Object> pAddParam,
      final PurchaseReturnLine pEntityPb,
        final IRequestData pRequestData) throws Exception {
    PurchaseReturnLine entity = this.prcAccEntityPbCopy
      .process(pAddParam, pEntityPb, pRequestData);
    entity.setItsOwner(this.prcAccEntityPbCopy
      .getSrvOrm().retrieveEntity(pAddParam, entity.getItsOwner()));
    entity.setPurchaseInvoiceLine(null);
    entity.setPurchInvLnAppearance(null);
    entity.setItsQuantity(BigDecimal.ZERO);
    entity.setItsTotal(BigDecimal.ZERO);
    entity.setTotalTaxes(BigDecimal.ZERO);
    entity.setSubtotal(BigDecimal.ZERO);
    entity.setForeignSubtotal(BigDecimal.ZERO);
    entity.setForeignTotal(BigDecimal.ZERO);
    entity.setForeignTotalTaxes(BigDecimal.ZERO);
    entity.setTaxesDescription(null);
    return entity;
  }

  //Simple getters and setters:
  /**
   * <p>Getter for prcAccEntityPbCopy.</p>
   * @return PrcEntityPbCopy<RS, PurchaseReturnLine>
   **/
  public final PrcEntityPbCopy<RS, PurchaseReturnLine>
    getPrcAccEntityPbCopy() {
    return this.prcAccEntityPbCopy;
  }

  /**
   * <p>Setter for prcAccEntityPbCopy.</p>
   * @param pPrcAccEntityPbCopy reference
   **/
  public final void setPrcAccEntityPbCopy(
    final PrcEntityPbCopy<RS, PurchaseReturnLine> pPrcAccEntityPbCopy) {
    this.prcAccEntityPbCopy = pPrcAccEntityPbCopy;
  }
}

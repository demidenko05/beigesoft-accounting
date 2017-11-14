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

import org.beigesoft.model.IRequestData;
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
   * <p>Acc-Entity Create delegator.</p>
   **/
  private PrcAccEntityCreate<RS, PurchaseReturnLine, Long> prcAccEntityCreate;

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
    PurchaseReturnLine entity = this.prcAccEntityCreate
      .process(pAddParam, pEntityPb, pRequestData);
    entity.setItsOwner(this.prcAccEntityCreate.getPrcEntityCreate()
      .getSrvOrm().retrieveEntity(pAddParam, entity.getItsOwner()));
    return entity;
  }

  //Simple getters and setters:
  /**
   * <p>Getter for prcAccEntityCreate.</p>
   * @return PrcAccEntityCreate<RS, PurchaseReturnLine, Long>
   **/
  public final PrcAccEntityCreate<RS, PurchaseReturnLine, Long>
    getPrcAccEntityCreate() {
    return this.prcAccEntityCreate;
  }

  /**
   * <p>Setter for prcAccEntityCreate.</p>
   * @param pPrcAccEntityCreate reference
   **/
  public final void setPrcAccEntityCreate(
  final PrcAccEntityCreate<RS, PurchaseReturnLine, Long> pPrcAccEntityCreate) {
    this.prcAccEntityCreate = pPrcAccEntityCreate;
  }
}

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
import org.beigesoft.service.IEntityProcessor;
import org.beigesoft.accounting.persistable.PaymentTo;
import org.beigesoft.accounting.service.ISrvTypeCode;

/**
 * <p>Service that make PaymentTo copy from DB for reverse.</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class PrcPaymentToGfr<RS>
  implements IEntityProcessor<PaymentTo, Long> {

  /**
   * <p>Acc-entity copy delegator.</p>
   **/
  private IEntityProcessor<PaymentTo, Long> prcAccDocGetForReverse;

  /**
   * <p>Type Codes of sub-accounts service.</p>
   **/
  private ISrvTypeCode srvTypeCode;

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
    PaymentTo entity = this.prcAccDocGetForReverse
      .process(pAddParam, pEntity, pRequestData);
    entity.setForeignTotal(entity.getForeignTotal().negate());
    pRequestData.setAttribute("typeCodeSubaccMap",
      this.srvTypeCode.getTypeCodeMap());
    return entity;
  }

  //Simple getters and setters:
  /**
   * <p>Getter for prcAccDocGetForReverse.</p>
   * @return PrcAccDocGetForReverse<RS, PaymentTo, Long>
   **/
  public final IEntityProcessor<PaymentTo, Long>
    getPrcAccDocGetForReverse() {
    return this.prcAccDocGetForReverse;
  }

  /**
   * <p>Setter for prcAccDocGetForReverse.</p>
   * @param pPrcAccDocGetForReverse reference
   **/
  public final void setPrcAccDocGetForReverse(
    final IEntityProcessor<PaymentTo, Long> pPrcAccDocGetForReverse) {
    this.prcAccDocGetForReverse = pPrcAccDocGetForReverse;
  }

  /**
   * <p>Geter for srvTypeCode.</p>
   * @return ISrvTypeCode
   **/
  public final ISrvTypeCode getSrvTypeCode() {
    return this.srvTypeCode;
  }

  /**
   * <p>Setter for srvTypeCode.</p>
   * @param pSrvTypeCode reference
   **/
  public final void setSrvTypeCode(final ISrvTypeCode pSrvTypeCode) {
    this.srvTypeCode = pSrvTypeCode;
  }
}

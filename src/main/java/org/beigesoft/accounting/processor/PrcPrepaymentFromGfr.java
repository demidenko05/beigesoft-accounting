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
import org.beigesoft.accounting.persistable.PrepaymentFrom;
import org.beigesoft.accounting.service.ISrvTypeCode;

/**
 * <p>Service that make PrepaymentFrom copy from DB for reverse.</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class PrcPrepaymentFromGfr<RS>
  implements IEntityProcessor<PrepaymentFrom, Long> {

  /**
   * <p>Acc-entity copy delegator.</p>
   **/
  private IEntityProcessor<PrepaymentFrom, Long> prcAccDocGetForReverse;

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
  public final PrepaymentFrom process(
    final Map<String, Object> pAddParam,
      final PrepaymentFrom pEntity,
        final IRequestData pRequestData) throws Exception {
    PrepaymentFrom entity = this.prcAccDocGetForReverse
      .process(pAddParam, pEntity, pRequestData);
    entity.setSalesInvoiceId(null);
    pRequestData.setAttribute("typeCodeSubaccMap",
      this.srvTypeCode.getTypeCodeMap());
    return entity;
  }

  //Simple getters and setters:
  /**
   * <p>Getter for prcAccDocGetForReverse.</p>
   * @return PrcAccDocGetForReverse<RS, PrepaymentFrom, Long>
   **/
  public final IEntityProcessor<PrepaymentFrom, Long>
    getPrcAccDocGetForReverse() {
    return this.prcAccDocGetForReverse;
  }

  /**
   * <p>Setter for prcAccDocGetForReverse.</p>
   * @param pPrcAccDocGetForReverse reference
   **/
  public final void setPrcAccDocGetForReverse(
    final IEntityProcessor<PrepaymentFrom, Long> pPrcAccDocGetForReverse) {
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

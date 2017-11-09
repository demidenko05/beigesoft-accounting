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
import org.beigesoft.persistable.IPersistableBase;
import org.beigesoft.service.IEntityProcessor;
import org.beigesoft.accounting.service.ISrvTypeCode;

/**
 * <p>Service that retrieve entity, check if it foreign
 * and put into request data for farther editing or confirm delete.
 * Those entities are AdditionCostLine, PrepaymentTo, PrepaymentFrom,
 * PaymentTo, PaymentFrom.</p>
 *
 * @param <RS> platform dependent record set type
 * @param <T> entity type
 * @author Yury Demidenko
 */
public class PrcAccEntityPbWithSubaccEditDelete<RS, T extends IPersistableBase>
  implements IEntityProcessor<T, Long> {

  /**
   * <p>Acc-EntityPb Edit/Confirm delete delegator.</p>
   **/
  private IEntityProcessor<T, Long> prcAccEntityPbEditDelete;

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
  public final T process(
    final Map<String, Object> pAddParam,
      final T pEntity, final IRequestData pRequestData) throws Exception {
    pRequestData.setAttribute("typeCodeSubaccMap",
      this.srvTypeCode.getTypeCodeMap());
    return this.prcAccEntityPbEditDelete
      .process(pAddParam, pEntity, pRequestData);
  }

  //Simple getters and setters:
  /**
   * <p>Getter for prcAccEntityPbEditDelete.</p>
   * @return IEntityProcessor<T, Long>
   **/
  public final IEntityProcessor<T, Long> getPrcAccEntityPbEditDelete() {
    return this.prcAccEntityPbEditDelete;
  }

  /**
   * <p>Setter for prcAccEntityPbEditDelete.</p>
   * @param pPrcAccEntityPbEditDelete reference
   **/
  public final void setPrcAccEntityPbEditDelete(
    final IEntityProcessor<T, Long> pPrcAccEntityPbEditDelete) {
    this.prcAccEntityPbEditDelete = pPrcAccEntityPbEditDelete;
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

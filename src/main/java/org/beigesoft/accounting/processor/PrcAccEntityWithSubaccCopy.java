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
import org.beigesoft.model.IHasId;
import org.beigesoft.service.IEntityProcessor;
import org.beigesoft.accounting.service.ISrvTypeCode;

/**
 * <p>Service that make entity with subaccount copy from DB.
 * These are non-persistable-base entities - Account, SubaccountLine.</p>
 *
 * @param <RS> platform dependent record set type
 * @param <T> entity type
 * @param <ID> entity ID type
 * @author Yury Demidenko
 */
public class PrcAccEntityWithSubaccCopy<RS, T extends IHasId<ID>, ID>
  implements IEntityProcessor<T, ID> {

  /**
   * <p>Acc-Entity Copy delegator.</p>
   **/
  private IEntityProcessor<T, ID> prcAccEntityCopy;

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
    return this.prcAccEntityCopy.process(pAddParam, pEntity, pRequestData);
  }

  //Simple getters and setters:
  /**
   * <p>Getter for prcAccEntityCopy.</p>
   * @return PrcAccEntityCopy<RS, T, ID>
   **/
  public final IEntityProcessor<T, ID> getPrcAccEntityCopy() {
    return this.prcAccEntityCopy;
  }

  /**
   * <p>Setter for prcAccEntityCopy.</p>
   * @param pPrcAccEntityCopy reference
   **/
  public final void setPrcAccEntityCopy(
    final IEntityProcessor<T, ID> pPrcAccEntityCopy) {
    this.prcAccEntityCopy = pPrcAccEntityCopy;
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

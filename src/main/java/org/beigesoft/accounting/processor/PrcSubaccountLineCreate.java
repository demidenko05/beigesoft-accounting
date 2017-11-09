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
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.accounting.persistable.SubaccountLine;
import org.beigesoft.accounting.service.ISrvTypeCode;

/**
 * <p>Service that create SubaccountLine.</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class PrcSubaccountLineCreate<RS>
  implements IEntityProcessor<SubaccountLine, Long> {

  /**
   * <p>Acc-Entity create delegator.</p>
   **/
  private IEntityProcessor<SubaccountLine, Long> prcAccEntityCreate;

  /**
   * <p>Type Codes of sub-accounts service.</p>
   **/
  private ISrvTypeCode srvTypeCode;

  /**
   * <p>ORM service.</p>
   **/
  private ISrvOrm<RS> srvOrm;

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
  public final SubaccountLine process(
    final Map<String, Object> pAddParam,
      final SubaccountLine pEntity,
        final IRequestData pRequestData) throws Exception {
    SubaccountLine entity = this.prcAccEntityCreate
      .process(pAddParam, pEntity, pRequestData);
    entity.setItsOwner(getSrvOrm()
      .retrieveEntity(pAddParam, entity.getItsOwner()));
    pEntity.setSubaccType(entity.getItsOwner().getSubaccType());
    pRequestData.setAttribute("typeCodeSubaccMap",
      this.srvTypeCode.getTypeCodeMap());
    return entity;
  }

  //Simple getters and setters:
  /**
   * <p>Getter for prcAccEntityCreate.</p>
   * @return IEntityProcessor<SubaccountLine, Long>
   **/
  public final IEntityProcessor<SubaccountLine, Long> getPrcAccEntityCreate() {
    return this.prcAccEntityCreate;
  }

  /**
   * <p>Setter for prcAccEntityCreate.</p>
   * @param pPrcAccEntityCreate reference
   **/
  public final void setPrcAccEntityCreate(
    final IEntityProcessor<SubaccountLine, Long> pPrcAccEntityCreate) {
    this.prcAccEntityCreate = pPrcAccEntityCreate;
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

  /**
   * <p>Getter for srvOrm.</p>
   * @return ISrvOrm<RS>
   **/
  public final ISrvOrm<RS> getSrvOrm() {
    return this.srvOrm;
  }

  /**
   * <p>Setter for srvOrm.</p>
   * @param pSrvOrm reference
   **/
  public final void setSrvOrm(final ISrvOrm<RS> pSrvOrm) {
    this.srvOrm = pSrvOrm;
  }
}

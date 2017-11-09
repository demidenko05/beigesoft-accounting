package org.beigesoft.replicator.processor;

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

import java.util.Set;
import java.util.HashSet;
import java.util.Map;

import org.beigesoft.exception.ExceptionWithCode;
import org.beigesoft.model.IRequestData;
import org.beigesoft.handler.IHandlerModelChanged;
import org.beigesoft.replicator.persistable.ReplicationAccMethod;
import org.beigesoft.service.IEntityProcessor;
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.accounting.service.ISrvAccSettings;

/**
 * <p>Service that update only acc-entry description into DB.</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class PrcReplicationAccMethodSave<RS>
  implements IEntityProcessor<ReplicationAccMethod, Long> {

  /**
   * <p>ReplicationAccMethod Changed Handlers.</p>
   **/
  private Set<IHandlerModelChanged<ReplicationAccMethod>>
    replAccMethChangedHandlers =
      new HashSet<IHandlerModelChanged<ReplicationAccMethod>>();

  /**
   * <p>ORM service.</p>
   **/
  private ISrvOrm<RS> srvOrm;

  /**
   * <p>Business service for accounting settings.</p>
   **/
  private ISrvAccSettings srvAccSettings;

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
  public final ReplicationAccMethod process(
    final Map<String, Object> pAddParam,
      final ReplicationAccMethod pEntity,
        final IRequestData pRequestData) throws Exception {
    if (pEntity.getRequestedDatabaseId() == getSrvOrm().getIdDatabase()) {
      throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
        "requested_database_must_be_different");
    }
    if (pEntity.getIsNew()) {
      this.srvOrm.insertEntity(pAddParam, pEntity);
    } else {
      this.srvOrm.updateEntity(pAddParam, pEntity);
    }
    for (IHandlerModelChanged<ReplicationAccMethod> replAccMethChangedHandler
      : this.replAccMethChangedHandlers) {
      replAccMethChangedHandler.handleModelChanged(pEntity);
    }
    pRequestData.setAttribute("accSettings",
      this.srvAccSettings.lazyGetAccSettings(pAddParam));
    return pEntity;
  }

  /**
   * <p>Added ReplicationAccMethod Changed Handler.</p>
   * @param pReplAccMethChngHandler IHandlerModelChanged<ReplicationAccMethod>
   */
  public final void addReplAccMethChangedHandler(
    final IHandlerModelChanged<ReplicationAccMethod> pReplAccMethChngHandler) {
    this.replAccMethChangedHandlers.add(pReplAccMethChngHandler);
  }

  //Simple getters and setters:
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

  /**
   * <p>Getter for srvAccSettings.</p>
   * @return ISrvAccSettings
   **/
  public final ISrvAccSettings getSrvAccSettings() {
    return this.srvAccSettings;
  }

  /**
   * <p>Setter for srvAccSettings.</p>
   * @param pSrvAccSettings reference
   **/
  public final void setSrvAccSettings(final ISrvAccSettings pSrvAccSettings) {
    this.srvAccSettings = pSrvAccSettings;
  }
}

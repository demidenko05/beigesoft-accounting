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

import org.beigesoft.model.IRequestData;
import org.beigesoft.handler.IHandlerModelChanged;
import org.beigesoft.replicator.persistable.ReplicationAccMethod;
import org.beigesoft.replicator.persistable.
  base.AReplExcludeAccountsDebitCredit;
import org.beigesoft.service.IEntityProcessor;

/**
 * <p>Service that update only acc-entry description into DB.</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class PrcReplExcludeAccountsDebitCreditSave<RS>
  implements IEntityProcessor<AReplExcludeAccountsDebitCredit, Long> {

  /**
   * <p>Entity FOL Save delegator.</p>
   **/
  private IEntityProcessor<AReplExcludeAccountsDebitCredit, Long>
    prcEntityFolSave;

  /**
   * <p>ReplicationAccMethod Changed Handlers.</p>
   **/
  private Set<IHandlerModelChanged<ReplicationAccMethod>>
    replAccMethChangedHandlers =
      new HashSet<IHandlerModelChanged<ReplicationAccMethod>>();

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
  public final AReplExcludeAccountsDebitCredit process(
    final Map<String, Object> pAddParam,
      final AReplExcludeAccountsDebitCredit pEntity,
        final IRequestData pRequestData) throws Exception {
    AReplExcludeAccountsDebitCredit entity = this.prcEntityFolSave
      .process(pAddParam, pEntity, pRequestData);
    for (IHandlerModelChanged<ReplicationAccMethod> replAccMethChangedHandler
      : this.replAccMethChangedHandlers) {
      replAccMethChangedHandler.handleModelChanged(entity.getItsOwner());
    }
    return entity;
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
   * <p>Getter for prcEntityFolSave.</p>
   * @return IEntityProcessor<AReplExcludeAccountsDebitCredit, Long>
   **/
  public final IEntityProcessor<AReplExcludeAccountsDebitCredit, Long>
    getPrcEntityFolSave() {
    return this.prcEntityFolSave;
  }

  /**
   * <p>Setter for prcEntityFolSave.</p>
   * @param pPrcEntityFolSave reference
   **/
  public final void setPrcEntityFolSave(
    final IEntityProcessor<AReplExcludeAccountsDebitCredit, Long>
      pPrcEntityFolSave) {
    this.prcEntityFolSave = pPrcEntityFolSave;
  }
}

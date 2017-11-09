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
import org.beigesoft.accounting.persistable.WarehouseEntry;
import org.beigesoft.accounting.persistable.MoveItems;
import org.beigesoft.accounting.service.ISrvWarehouseEntry;

/**
 * <p>Service that retrieve MoveItems and (if requested)
 * its warehouse entries and put them
 * into request data for farther printing.</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class PrcMoveItemsRetrieve<RS>
  implements IEntityProcessor<MoveItems, Long> {

  /**
   * <p>Acc-entity retrieve delegator.</p>
   **/
  private IEntityProcessor<MoveItems, Long> prcAccEntityRetrieve;

  /**
   * <p>Business service for warehouse.</p>
   **/
  private ISrvWarehouseEntry srvWarehouseEntry;

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
  public final MoveItems process(
    final Map<String, Object> pAddParam,
      final MoveItems pEntity,
        final IRequestData pRequestData) throws Exception {
    MoveItems entity = this.prcAccEntityRetrieve
      .process(pAddParam, pEntity, pRequestData);
    String actionAdd = pRequestData.getParameter("actionAdd");
    if ("full".equals(actionAdd)) {
      pRequestData.setAttribute("warehouseEntries", srvWarehouseEntry
        .retrieveEntriesForOwner(pAddParam, entity.constTypeCode(),
          entity.getItsId()));
      pRequestData
        .setAttribute("classWarehouseEntry", WarehouseEntry.class);
    }
    return entity;
  }

  //Simple getters and setters:
  /**
   * <p>Getter for prcAccEntityRetrieve.</p>
   * @return PrcAccEntityRetrieve<MoveItems, Long>
   **/
  public final IEntityProcessor<MoveItems, Long> getPrcAccEntityRetrieve() {
    return this.prcAccEntityRetrieve;
  }

  /**
   * <p>Setter for prcAccEntityRetrieve.</p>
   * @param pPrcAccEntityRetrieve reference
   **/
  public final void setPrcAccEntityRetrieve(
    final IEntityProcessor<MoveItems, Long> pPrcAccEntityRetrieve) {
    this.prcAccEntityRetrieve = pPrcAccEntityRetrieve;
  }

  /**
   * <p>Geter for srvWarehouseEntry.</p>
   * @return ISrvWarehouseEntry
   **/
  public final ISrvWarehouseEntry getSrvWarehouseEntry() {
    return this.srvWarehouseEntry;
  }

  /**
   * <p>Setter for srvWarehouseEntry.</p>
   * @param pSrvWarehouseEntry reference
   **/
  public final void setSrvWarehouseEntry(
    final ISrvWarehouseEntry pSrvWarehouseEntry) {
    this.srvWarehouseEntry = pSrvWarehouseEntry;
  }
}

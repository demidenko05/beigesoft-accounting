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
import org.beigesoft.accounting.persistable.IDocWarehouse;
import org.beigesoft.accounting.persistable.WarehouseEntry;
import org.beigesoft.accounting.persistable.UseMaterialEntry;
import org.beigesoft.accounting.service.ISrvWarehouseEntry;
import org.beigesoft.accounting.service.ISrvDrawItemEntry;

/**
 * <p>Service that retrieve Acc-Warehouse-Document and (if requested)
 * their accounting, warehouse and draw material entries and put them
 * into request data for farther printing.
 * Those documents are: PurchaseReturn, Manufacture, ManufacturingProcess.</p>
 *
 * @param <RS> platform dependent record set type
 * @param <T> entity type
 * @author Yury Demidenko
 */
public class PrcAccDocUseMaterialRetrieve<RS, T extends IDocWarehouse>
  implements IEntityProcessor<T, Long> {

  /**
   * <p>Acc-doc retrieve delegator.</p>
   **/
  private IEntityProcessor<T, Long> prcAccDocRetrieve;

  /**
   * <p>Business service for warehouse.</p>
   **/
  private ISrvWarehouseEntry srvWarehouseEntry;

  /**
   * <p>Business service for draw any item to sale/loss/stole.</p>
   **/
  private ISrvDrawItemEntry<UseMaterialEntry> srvUseMaterialEntry;

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
    T entity = this.prcAccDocRetrieve.process(pAddParam, pEntity, pRequestData);
    String actionAdd = pRequestData.getParameter("actionAdd");
    if ("full".equals(actionAdd)) {
      pRequestData.setAttribute("warehouseEntries", srvWarehouseEntry
        .retrieveEntriesFor(pAddParam, entity));
      pRequestData.setAttribute("useMaterialEntries", srvUseMaterialEntry
        .retrieveEntriesFor(pAddParam, entity));
      pRequestData
        .setAttribute("classUseMaterialEntry", UseMaterialEntry.class);
      pRequestData
        .setAttribute("classWarehouseEntry", WarehouseEntry.class);
    }
    return entity;
  }

  //Simple getters and setters:
  /**
   * <p>Getter for prcAccDocRetrieve.</p>
   * @return PrcAccDocRetrieve<T, Long>
   **/
  public final IEntityProcessor<T, Long> getPrcAccDocRetrieve() {
    return this.prcAccDocRetrieve;
  }

  /**
   * <p>Setter for prcAccDocRetrieve.</p>
   * @param pPrcAccDocRetrieve reference
   **/
  public final void setPrcAccDocRetrieve(
    final IEntityProcessor<T, Long> pPrcAccDocRetrieve) {
    this.prcAccDocRetrieve = pPrcAccDocRetrieve;
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

  /**
   * <p>Getter for srvUseMaterialEntry.</p>
   * @return ISrvDrawItemEntry<UseMaterialEntry>
   **/
  public final ISrvDrawItemEntry<UseMaterialEntry> getSrvUseMaterialEntry() {
    return this.srvUseMaterialEntry;
  }

  /**
   * <p>Setter for srvUseMaterialEntry.</p>
   * @param pSrvUseMaterialEntry reference
   **/
  public final void setSrvUseMaterialEntry(
    final ISrvDrawItemEntry<UseMaterialEntry> pSrvUseMaterialEntry) {
    this.srvUseMaterialEntry = pSrvUseMaterialEntry;
  }
}

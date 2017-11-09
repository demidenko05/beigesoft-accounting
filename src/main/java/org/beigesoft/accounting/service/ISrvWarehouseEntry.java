package org.beigesoft.accounting.service;

/*
 * Copyright (c) 2016 Beigesoft ™
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
import java.util.List;
import java.math.BigDecimal;

import org.beigesoft.accounting.persistable.IDocWarehouse;
import org.beigesoft.accounting.persistable.WarehouseEntry;
import org.beigesoft.accounting.persistable.IMakingWarehouseEntry;
import org.beigesoft.accounting.persistable.WarehouseSite;

/**
 * <p>Business service for warehouse entries.</p>
 *
 * @author Yury Demidenko
 */
public interface ISrvWarehouseEntry {

  /**
   * <p>Load warehouse with item from outside or reverse a load.</p>
   * @param pAddParam additional param
   * @param pEntity movement
   * @param pWhSiteTo Site To
   * @throws Exception - an exception
   **/
  void load(Map<String, Object> pAddParam,
    IMakingWarehouseEntry pEntity,
      WarehouseSite pWhSiteTo) throws Exception;

  /**
   * <p>Move item between warehouses/sites or reverse a move.</p>
   * @param pAddParam additional param
   * @param pEntity movement
   * @param pWhSiteFrom Site From
   * @param pWhSiteTo Site To
   * @throws Exception - an exception
   **/
  void move(Map<String, Object> pAddParam,
    IMakingWarehouseEntry pEntity, WarehouseSite pWhSiteFrom,
      WarehouseSite pWhSiteTo) throws Exception;

  /**
   * <p>Make warehouse rest (load/draw/reverse).</p>
   * @param pAddParam additional param
   * @param pEntity movement
   * @param pWhSite Site
   * @param pQuantity Quantity
   * @throws Exception - an exception
   **/
  void makeWarehouseRest(final Map<String, Object> pAddParam,
    IMakingWarehouseEntry pEntity, WarehouseSite pWhSite,
        BigDecimal pQuantity) throws Exception;

  /**
   * <p>Withdrawal warehouse item to outside (or use/loss).</p>
   * @param pAddParam additional param
   * @param pEntity movement
   * @param pWhSiteFrom Site From, if null - automatically find sites
   * @throws Exception - an exception
   **/
  void withdrawal(Map<String, Object> pAddParam,
    IMakingWarehouseEntry pEntity, WarehouseSite pWhSiteFrom) throws Exception;

  /**
   * <p>Reverse a withdrawal warehouse.</p>
   * @param pAddParam additional param
   * @param pEntity movement
   * @throws Exception - an exception
   **/
  void reverseDraw(Map<String, Object> pAddParam,
    IMakingWarehouseEntry pEntity) throws Exception;

  /**
   * <p>Retrieve entries for document.</p>
   * @param pAddParam additional param
   * @param pEntity a document
   * @return warehouse entries made by this document
   * @throws Exception - an exception
   **/
  List<WarehouseEntry> retrieveEntriesFor(Map<String, Object> pAddParam,
    IDocWarehouse pEntity) throws Exception;


  /**
   * <p>Retrieve entries for lines owner id/type.</p>
   * @param pAddParam additional param
   * @param pOwnerTypeCode Owner Type code
   * @param pOwnerId Owner ID
   * @return warehouse entries made by this document
   * @throws Exception - an exception
   **/
  List<WarehouseEntry> retrieveEntriesForOwner(
    Map<String, Object> pAddParam,
      Integer pOwnerTypeCode, Long pOwnerId) throws Exception;
}

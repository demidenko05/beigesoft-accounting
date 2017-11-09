package org.beigesoft.webstore.service;

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

import org.beigesoft.accounting.persistable.InvItem;
import org.beigesoft.accounting.persistable.WarehouseSite;

/**
 * <p>Business service for increase available goods in WEB-Store.</p>
 *
 * @author Yury Demidenko
 */
public interface ISrvIncreaseGoods {

  /**
   * <p>Increase available goods in WEB-Store.</p>
   * @param pAddParam additional param
   * @param goods a goods
   * @param warehouseSite Warehouse Site
   * @param itsQuantity quantity
   * @throws Exception - an exception
   **/
  void registerIncrease(Map<String, Object> pAddParam, InvItem goods,
    WarehouseSite warehouseSite, Integer itsQuantity) throws Exception;
}

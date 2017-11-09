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

import java.util.List;
import java.util.Map;

import org.beigesoft.accounting.model.WarehouseSiteRestLine;

/**
 * <p>WarehouseSite Rests service.</p>
 *
 * @author Yury Demidenko
 */
public interface ISrvWarehouseSiteRests {

  /**
   * <p>Retrieve WarehouseSite Rests.</p>
   * @param pAddParam additional param
   * @return WarehouseSite Rests Lines
   * @throws Exception - an exception
   **/
  List<WarehouseSiteRestLine> retrieveWarehouseSiteRests(
    Map<String, Object> pAddParam) throws Exception;
}

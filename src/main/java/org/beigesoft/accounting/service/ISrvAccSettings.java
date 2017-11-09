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

import org.beigesoft.accounting.persistable.AccSettings;

/**
 * <p>Accounting settings service.</p>
 *
 * @author Yury Demidenko
 */
public interface ISrvAccSettings {

  /**
   * <p>Retrieve/get Accounting settings.</p>
   * @param pAddParam additional param
   * @return Accounting settings
   * @throws Exception - an exception
   **/
  AccSettings lazyGetAccSettings(
    Map<String, Object> pAddParam) throws Exception;

  /**
   * <p>Clear Accounting settings to retrieve from
   * database new version.</p>
   * @param pAddParam additional param
   **/
  void clearAccSettings(Map<String, Object> pAddParam);

  /**
   * <p>Save acc-settings into DB.</p>
   * @param pAddParam additional param
   * @param pEntity entity
   * @throws Exception - an exception
   **/
  void saveAccSettings(Map<String, Object> pAddParam,
      AccSettings pEntity) throws Exception;
}

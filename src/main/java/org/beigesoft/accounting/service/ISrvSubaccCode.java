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

/**
 * <p>Business service for subaccounts that hold used type map.</p>
 *
 * @author Yury Demidenko
 */
public interface ISrvSubaccCode extends ISrvTypeCode {

  /**
   * <p>Getter for code - used type map.</p>
   * @return Map<Integer, Class<?>> map og used subacc classes
   **/
  Map<Integer, Class<?>> getSubaccUsedCodeMap();

  /**
   * <p>Setter for code - used type map.</p>
   * @param pSubaccUsedCodeMap reference
   **/
  void setSubaccUsedCodeMap(Map<Integer, Class<?>> pSubaccUsedCodeMap);
}

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
 * <p>Business service for code - java type map.</p>
 *
 * @author Yury Demidenko
 */
public interface ISrvTypeCode {

  /**
   * <p>Getter for code - java type map.</p>
   * @return Map<Integer, Class<?>>
   **/
  Map<Integer, Class<?>> getTypeCodeMap();

  /**
   * <p>Setter for code - java type map.</p>
   * @param pTypeCodeMap reference
   **/
  void setTypeCodeMap(Map<Integer, Class<?>> pTypeCodeMap);
}

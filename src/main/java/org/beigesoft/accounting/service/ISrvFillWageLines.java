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

import org.beigesoft.accounting.persistable.Wage;

/**
 * <pre>
 * Abstraction that fill wage lines according a method,
 * e.g. Wage Table Percentage.
 * </pre>
 *
 * @author Yury Demidenko
 */
public interface ISrvFillWageLines {

  /**
   * <p>Fill wage lines according a method.</p>
   * @param pAddParam additional param
   * @param pWage Wage document
   * @throws Exception - an exception
   **/
  void fillWageLines(Map<String, Object> pAddParam,
    Wage pWage) throws Exception;
}

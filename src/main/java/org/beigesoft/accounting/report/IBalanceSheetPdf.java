package org.beigesoft.accounting.report;

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

import java.io.OutputStream;
import java.util.Map;

import org.beigesoft.accounting.model.BalanceSheet;

/**
 * <p>Abstraction of balance sheet report into PDF.</p>
 *
 * @author Yury Demidenko
 */
public interface IBalanceSheetPdf {

  /**
   * <p>Write PDF report for given balance to output stream.</p>
   * @param pAddParam additional param
   * @param pBalance Balance
   * @param pOus servlet output stream
   * @throws Exception - an exception
   **/
  void makeReport(final Map<String, Object> pAddParam,
    BalanceSheet pBalance, OutputStream pOus) throws Exception;
}

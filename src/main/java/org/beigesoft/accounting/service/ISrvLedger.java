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
import java.util.Date;

import org.beigesoft.accounting.model.LedgerPrevious;
import org.beigesoft.accounting.model.LedgerDetail;
import org.beigesoft.accounting.persistable.Account;

/**
 * <p>Ledger service.</p>
 *
 * @author Yury Demidenko
 */
public interface ISrvLedger {

  /**
   * <p>Retrieve previous totals.</p>
   * @param pAddParam additional param
   * @param pAccount account
   * @param pDate1 date start
   * @param pSubaccId Subaccount ID or null
   * @return LedgerPrevious data
   * @throws Exception - an exception
   **/
  LedgerPrevious retrievePrevious(Map<String, Object> pAddParam,
    Account pAccount, Date pDate1, String pSubaccId) throws Exception;

  /**
   * <p>Retrieve detail entries for period.</p>
   * @param pAddParam additional param
   * @param pAccount account
   * @param pDate1 date start
   * @param pDate2 date end
   * @return LedgerDetail data
   * @param pSubaccId Subaccount ID or null
   * @param ledgerPrevious ledger previous
   * @throws Exception - an exception
   **/
  LedgerDetail retrieveDetail(Map<String, Object> pAddParam,
    Account pAccount, Date pDate1, Date pDate2,
      String pSubaccId, LedgerPrevious ledgerPrevious) throws Exception;
}

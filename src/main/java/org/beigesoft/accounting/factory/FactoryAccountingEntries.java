package org.beigesoft.accounting.factory;

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

import org.beigesoft.factory.IFactorySimple;
import org.beigesoft.accounting.persistable.AccountingEntries;

/**
 * <pre>
 * Simple factory that create a request(or) scoped AccountingEntries.
 * </pre>
 *
 * @author Yury Demidenko
 **/
public class FactoryAccountingEntries
  implements IFactorySimple<AccountingEntries> {

  /**
   * <p>ID Database.</p>
   **/
  private Integer databaseId;

  /**
   * <p>Create AccountingEntries.</p>
   * @param pAddParam additional param
   * @return AccountingEntries request(or) scoped bean
   * @throws Exception - an exception
   */
  @Override
  public final AccountingEntries create(
    final Map<String, Object> pAddParam) throws Exception {
    AccountingEntries object = new AccountingEntries();
    object.setIsNew(false);
    object.setIdDatabaseBirth(this.databaseId);
    object.setItsDate(new Date());
    return object;
  }

  /**
   * <p>Getter for databaseId.</p>
   * @return Integer
   **/
  public final Integer getDatabaseId() {
    return this.databaseId;
  }

  /**
   * <p>Setter for databaseId.</p>
   * @param pDatabaseId reference
   **/
  public final void setDatabaseId(final Integer pDatabaseId) {
    this.databaseId = pDatabaseId;
  }
}

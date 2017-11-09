package org.beigesoft.accounting.persistable.base;

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

import org.beigesoft.persistable.APersistableBaseVersion;

/**
 * <pre>
 * Model of used subaccount in AccountingEntries.
 * </pre>
 *
 * @param <T> extends ASubaccount type
 * @author Yury Demidenko
 */
public abstract class ASubaccountUsed<T extends ASubaccount>
  extends APersistableBaseVersion {

  /**
   * <p>Geter for subaccount.</p>
   * @return subaccount
   **/
  public abstract T getSubaccount();

  /**
   * <p>Setter for subaccount.</p>
   * @param pSubaccount reference
   **/
  public abstract void setSubaccount(T pSubaccount);
}

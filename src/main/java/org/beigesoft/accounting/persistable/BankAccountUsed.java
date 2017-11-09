package org.beigesoft.accounting.persistable;

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

import org.beigesoft.accounting.persistable.base.ASubaccountUsed;

/**
 * <pre>
 * Model of used Bank Account in AccountingEntries.
 * </pre>
 *
 * @author Yury Demidenko
 */
public class BankAccountUsed extends ASubaccountUsed<BankAccount> {

  /**
   * <p>BankAccount.</p>
   **/
  private BankAccount subaccount;

  /**
   * <p>Getter for subaccount.</p>
   * @return BankAccount
   **/
  @Override
  public final BankAccount getSubaccount() {
    return this.subaccount;
  }

  /**
   * <p>Setter for subaccount.</p>
   * @param pSubaccount reference
   **/
  @Override
  public final void setSubaccount(final BankAccount pSubaccount) {
    this.subaccount = pSubaccount;
  }
}

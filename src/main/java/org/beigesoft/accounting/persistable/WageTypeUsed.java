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
 * Model of used Wage Type in AccountingEntries.
 * </pre>
 *
 * @author Yury Demidenko
 */
public class WageTypeUsed extends ASubaccountUsed<WageType> {

  /**
   * <p>WageType.</p>
   **/
  private WageType subaccount;

  /**
   * <p>Getter for subaccount.</p>
   * @return WageType
   **/
  @Override
  public final WageType getSubaccount() {
    return this.subaccount;
  }

  /**
   * <p>Setter for subaccount.</p>
   * @param pSubaccount reference
   **/
  @Override
  public final void setSubaccount(final WageType pSubaccount) {
    this.subaccount = pSubaccount;
  }
}

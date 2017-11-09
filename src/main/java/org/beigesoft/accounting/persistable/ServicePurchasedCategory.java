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

import org.beigesoft.persistable.AHasNameIdLong;

/**
 * <pre>
 * Model of category of service purchased, e.g. "Car engine repairs".
 * It used for filter list of services.
 * </pre>
 *
 * @author Yury Demidenko
 */
public class ServicePurchasedCategory extends AHasNameIdLong {

  /**
   * <p>Expense, not null, subaccount for account expense.</p>
   **/
  private Expense expense;

  //Simple getters and setters:
  /**
   * <p>Getter for expense.</p>
   * @return Expense
   **/
  public final Expense getExpense() {
    return this.expense;
  }

  /**
   * <p>Setter for expense.</p>
   * @param pExpense reference
   **/
  public final void setExpense(final Expense pExpense) {
    this.expense = pExpense;
  }
}

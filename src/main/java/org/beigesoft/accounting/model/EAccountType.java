package org.beigesoft.accounting.model;

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

/**
 * <pre>
 * Account Type - ASSET/LIABILITY/
 * OWNERS_EQUITY/GROSS_INCOME_REVENUE/GROSS_INCOME_EXPENSE.
 * </pre>
 *
 * @author Yury Demidenko
 */
 public enum EAccountType {

  /**
   * <p>If asset.</p>
   **/
  ASSET,

  /**
   * <p>If liability.</p>
   **/
  LIABILITY,

  /**
   * <p>If Owner's equity.</p>
   **/
  OWNERS_EQUITY,

  /**
   * <p>If revenue of gross income.</p>
   **/
  GROSS_INCOME_REVENUE,

  /**
   * <p>If expense of gross income.</p>
   **/
  GROSS_INCOME_EXPENSE;
}

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
 * Account Due Method ACCRUAL/CASH.
 * </pre>
 *
 * @author Yury Demidenko
 */
 public enum EDueMethod {

  /**
   * <p>Due when recieve/send document.</p>
   **/
  ACCRUAL,

  /**
   * <p>Due when recieve/send money.</p>
   **/
  CASH;
}

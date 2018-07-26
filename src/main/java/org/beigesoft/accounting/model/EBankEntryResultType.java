package org.beigesoft.accounting.model;

/*
 * Copyright (c) 2018 Beigesoft™
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
 * <p>
 * Bank Entry Result Type.
 * </p>
 *
 * @author Yury Demidenko
 */
public enum EBankEntryResultType {

  /**
   * <p>0 ACC_ENTRY.</p>
   **/
  ACC_ENTRY,

  /**
   * <p>1 PAYMENTFROM.</p>
   **/
  PAYMENTFROM,

  /**
   * <p>2 PREPAYMENTFROM.</p>
   **/
  PREPAYMENTFROM,

  /**
   * <p>3 PAYMENTTO.</p>
   **/
  PAYMENTTO,

  /**
   * <p>4 PREPAYMENTTO.</p>
   **/
  PREPAYMENTTO;
}

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
 * Bank Entry Status.
 * </p>
 *
 * @author Yury Demidenko
 */
public enum EBankEntryStatus {

  /**
   * <p>0 ACCEPTED.</p>
   **/
  ACCEPTED,

  /**
   * <p>1 VOIDED.</p>
   **/
  VOIDED,

  /**
   * <p>2 PENDING.</p>
   **/
  PENDING,

  /**
   * <p>3 OTHER.</p>
   **/
  OTHER;
}

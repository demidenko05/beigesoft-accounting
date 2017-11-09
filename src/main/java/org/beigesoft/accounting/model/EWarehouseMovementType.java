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
 * Type of warehouse movement source LOAD/WITHDRAWAL/MOVE.
 * E.g. PurchaseInvoiceLine - LOAD, SalesInvoiceLine - WITHDRAWAL.
 * </pre>
 *
 * @author Yury Demidenko
 */
 public enum EWarehouseMovementType {

  /**
   * <p>If loads (put).</p>
   **/
  LOAD,

  /**
   * <p>If withdrawals.</p>
   **/
  WITHDRAWAL,

  /**
   * <p>If move within/between warehouse/s.</p>
   **/
  MOVE,
}

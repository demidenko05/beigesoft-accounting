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

import org.beigesoft.accounting.model.EWarehouseMovementType;

/**
 * <pre>
 * Abstract model of document that makes warehouse entries,
 * e.g. PurchaseInvoice, Manufacture
 * </pre>
 *
 * @author Yury Demidenko
 */
public interface IDocWarehouse extends IDoc {

  /**
   * <p>If owned lines make warehouse entries this return
   * their type.</p>
   * @return Boolean
   **/
  EWarehouseMovementType getLinesWarehouseType();
}

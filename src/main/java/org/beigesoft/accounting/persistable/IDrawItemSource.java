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

import java.math.BigDecimal;

/**
 * <pre>
 * Model of entity that hold inventory item to draw.
 * It loads(put) an item into warehouse.
 * </pre>
 *
 * @author Yury Demidenko
 */
public interface IDrawItemSource extends IMakingWarehouseEntry {

  /**
   * <p>Getter for theRest.</p>
   * @return BigDecimal
   **/
  BigDecimal getTheRest();

  /**
   * <p>Setter for theRest.</p>
   * @param pTheRest reference
   **/
  void setTheRest(BigDecimal pTheRest);

  /**
   * <p>Getter for ItsCost.</p>
   * @return Long
   **/
  BigDecimal getItsCost();

  /**
   * <p>Setter for ItsCost.</p>
   * @param pItsCost reference
   **/
  void setItsCost(BigDecimal pItsCost);
}

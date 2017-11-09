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
 * Warehouse Site Rest Line.
 * </pre>
 *
 * @author Yury Demidenko
 */
public class WarehouseSiteRestLine extends WarehouseRestLine {

  /**
   * <p>Warehouse site name.</p>
   **/
  private String warehouseSite;
  //Simple getters and setters:

  /**
   * <p>Getter for warehouseSite.</p>
   * @return String
   **/
  public final String getWarehouseSite() {
    return this.warehouseSite;
  }

  /**
   * <p>Setter for warehouseSite.</p>
   * @param pWarehouseSite reference
   **/
  public final void setWarehouseSite(final String pWarehouseSite) {
    this.warehouseSite = pWarehouseSite;
  }
}

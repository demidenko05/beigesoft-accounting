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

import java.math.BigDecimal;

/**
 * <p>
 * Warehouse Rest Line simple.
 * </p>
 *
 * @author Yury Demidenko
 */
public class WarehouseRestLineSm {

  /**
   * <p>Inventory item ID.</p>
   **/
  private Long invItemId;

  /**
   * <p>Warehouse site ID.</p>
   **/
  private Long siteId;

  /**
   * <p>Credit account.</p>
   **/
  private BigDecimal theRest;

  //Simple getters and setters:
  /**
   * <p>Getter for invItemId.</p>
   * @return Long
   **/
  public final Long getInvItemId() {
    return this.invItemId;
  }

  /**
   * <p>Setter for invItemId.</p>
   * @param pInvItemId reference
   **/
  public final void setInvItemId(final Long pInvItemId) {
    this.invItemId = pInvItemId;
  }

  /**
   * <p>Getter for siteId.</p>
   * @return Long
   **/
  public final Long getSiteId() {
    return this.siteId;
  }

  /**
   * <p>Setter for siteId.</p>
   * @param pSiteId reference
   **/
  public final void setSiteId(final Long pSiteId) {
    this.siteId = pSiteId;
  }

  /**
   * <p>Getter for theRest.</p>
   * @return BigDecimal
   **/
  public final BigDecimal getTheRest() {
    return this.theRest;
  }

  /**
   * <p>Setter for theRest.</p>
   * @param pTheRest reference
   **/
  public final void setTheRest(final BigDecimal pTheRest) {
    this.theRest = pTheRest;
  }
}

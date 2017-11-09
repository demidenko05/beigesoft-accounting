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

import java.util.Date;

import org.beigesoft.model.IOwned;
import org.beigesoft.accounting.persistable.base.AInvItemMovement;

/**
 * <pre>
 * Model of Customer Invoice Line.
 * It is immutable.
 * </pre>
 *
 * @author Yury Demidenko
 */
public class GoodsLossLine extends AInvItemMovement
  implements IMakingWarehouseEntry, IOwned<GoodsLoss> {

  /**
   * <p>Customer Invoice.</p>
   **/
  private GoodsLoss itsOwner;

  /**
   * <p>Warehouse site from (optional).
   * If it's empty (null) then withdrawal will be from the first
   * site/s that has the goods, otherwise withdrawal will be exactly
   * from this site.</p>
   **/
  private WarehouseSite warehouseSiteFo;

  /**
   * <p>Reversed line ID (if this reverse it).</p>
   **/
  private Long reversedId;

  /**
   * <p>Description.</p>
   **/
  private String description;

  /**
   * <p>Geter for reversedId.</p>
   * @return Long
   **/
  @Override
  public final Long getReversedId() {
    return this.reversedId;
  }

  /**
   * <p>Setter for reversedId.</p>
   * @param pReversedId reference
   **/
  @Override
  public final void setReversedId(final Long pReversedId) {
    this.reversedId = pReversedId;
  }

  /**
   * <p>Constant of code type.</p>
   * @return 1005
   **/
  @Override
  public final Integer constTypeCode() {
    return 1005;
  }

  /**
   * <p>Get for document Date.</p>
   * @return Date
   **/
  @Override
  public final Date getDocumentDate() {
    return this.getItsOwner().getItsDate();
  }

  /**
   * <p>Get Owner Type if exist  e.g. PurchaseInvoice 1.</p>
   * @return Integer
   **/
  @Override
  public final Integer getOwnerType() {
    return new GoodsLoss().constTypeCode();
  }

  /**
   * <p>Get for owner's ID.</p>
   * @return Long
   **/
  @Override
  public final Long getOwnerId() {
    return this.getItsOwner().getItsId();
  }

  /**
   * <p>Getter for description.</p>
   * @return String
   **/
  @Override
  public final String getDescription() {
    return this.description;
  }

  /**
   * <p>Setter for description.</p>
   * @param pDescription reference
   **/
  @Override
  public final void setDescription(final String pDescription) {
    this.description = pDescription;
  }

  /**
   * <p>Geter for itsOwner.</p>
   * @return GoodsLoss
   **/
  @Override
  public final GoodsLoss getItsOwner() {
    return this.itsOwner;
  }

  /**
   * <p>Setter for itsOwner.</p>
   * @param pItsOwner reference
   **/
  @Override
  public final void setItsOwner(final GoodsLoss pItsOwner) {
    this.itsOwner = pItsOwner;
  }

  //Simple getters and setters:
  /**
   * <p>Getter for warehouseSiteFo.</p>
   * @return WarehouseSite
   **/
  public final WarehouseSite getWarehouseSiteFo() {
    return this.warehouseSiteFo;
  }

  /**
   * <p>Setter for warehouseSiteFo.</p>
   * @param pWarehouseSiteFo reference
   **/
  public final void setWarehouseSiteFo(final WarehouseSite pWarehouseSiteFo) {
    this.warehouseSiteFo = pWarehouseSiteFo;
  }
}

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
 * Model of Move Items within/between warehouse/s Line.
 * It is immutable.
 * </pre>
 *
 * @author Yury Demidenko
 */
public class MoveItemsLine extends AInvItemMovement
  implements IMakingWarehouseEntry, IOwned<MoveItems> {

  /**
   * <p>Customer Invoice.</p>
   **/
  private MoveItems itsOwner;

  /**
   * <p>Warehouse site from.</p>
   **/
  private WarehouseSite warehouseSiteFrom;

  /**
   * <p>Warehouse site to.</p>
   **/
  private WarehouseSite warehouseSiteTo;

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
   * @return 1008
   **/
  @Override
  public final Integer constTypeCode() {
    return 1008;
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
   * <p>Get Owner Type.</p>
   * @return Integer
   **/
  @Override
  public final Integer getOwnerType() {
    return this.getItsOwner().constTypeCode();
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
   * @return MoveItems
   **/
  @Override
  public final MoveItems getItsOwner() {
    return this.itsOwner;
  }

  /**
   * <p>Setter for itsOwner.</p>
   * @param pItsOwner reference
   **/
  @Override
  public final void setItsOwner(final MoveItems pItsOwner) {
    this.itsOwner = pItsOwner;
  }

  //Simple getters and setters:
  /**
   * <p>Getter for warehouseSiteFrom.</p>
   * @return WarehouseSite
   **/
  public final WarehouseSite getWarehouseSiteFrom() {
    return this.warehouseSiteFrom;
  }

  /**
   * <p>Setter for warehouseSiteFrom.</p>
   * @param pWarehouseSiteFrom reference
   **/
  public final void setWarehouseSiteFrom(
    final WarehouseSite pWarehouseSiteFrom) {
    this.warehouseSiteFrom = pWarehouseSiteFrom;
  }

  /**
   * <p>Getter for warehouseSiteTo.</p>
   * @return WarehouseSite
   **/
  public final WarehouseSite getWarehouseSiteTo() {
    return this.warehouseSiteTo;
  }

  /**
   * <p>Setter for warehouseSiteTo.</p>
   * @param pWarehouseSiteTo reference
   **/
  public final void setWarehouseSiteTo(
    final WarehouseSite pWarehouseSiteTo) {
    this.warehouseSiteTo = pWarehouseSiteTo;
  }
}

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
import java.math.BigDecimal;

import org.beigesoft.model.IOwned;
import org.beigesoft.accounting.persistable.base.AInvItemMovementCost;

/**
 * <pre>
 * Model of Beginning Inventory Line.
 * It is immutable.
 * </pre>
 *
 * @author Yury Demidenko
 */
public class BeginningInventoryLine extends AInvItemMovementCost
  implements IDrawItemSource, IOwned<BeginningInventory> {

  /**
   * <p>Vendor Invoice.</p>
   **/
  private BeginningInventory itsOwner;

  /**
   * <p>Warehouse site to, not null.</p>
   **/
  private WarehouseSite warehouseSite;

  /**
   * <p>Reversed line ID (if this reverse it).</p>
   **/
  private Long reversedId;

  /**
   * <p>The rest, charged by the quantity,
   * draws by sales, loss etc.</p>
   **/
  private BigDecimal theRest = BigDecimal.ZERO;

  /**
   * <p>Description.</p>
   **/
  private String description;

  /**
   * <p>Geter for theRest.</p>
   * @return BigDecimal
   **/
  @Override
  public final BigDecimal getTheRest() {
    return this.theRest;
  }

  /**
   * <p>Setter for theRest.</p>
   * @param pTheRest reference
   **/
  @Override
  public final void setTheRest(final BigDecimal pTheRest) {
    this.theRest = pTheRest;
  }

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
   * @return 1009
   **/
  @Override
  public final Integer constTypeCode() {
    return 1009;
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
   * <p>Get Owner Type if exist  e.g. BeginningInventory 1.</p>
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
   * @return BeginningInventory
   **/
  @Override
  public final BeginningInventory getItsOwner() {
    return this.itsOwner;
  }

  /**
   * <p>Setter for itsOwner.</p>
   * @param pItsOwner reference
   **/
  @Override
  public final void setItsOwner(final BeginningInventory pItsOwner) {
    this.itsOwner = pItsOwner;
  }

  //Simple getters and setters:
  /**
   * <p>Geter for warehouseSite.</p>
   * @return WarehouseSite
   **/
  public final WarehouseSite getWarehouseSite() {
    return this.warehouseSite;
  }

  /**
   * <p>Setter for warehouseSite.</p>
   * @param pWarehouseSite reference
   **/
  public final void setWarehouseSite(final WarehouseSite pWarehouseSite) {
    this.warehouseSite = pWarehouseSite;
  }
}

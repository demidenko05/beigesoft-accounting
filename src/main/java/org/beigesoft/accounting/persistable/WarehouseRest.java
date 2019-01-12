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

import org.beigesoft.model.AEditableHasVersion;
import org.beigesoft.model.IHasId;

/**
 * <pre>
 * Model of Warehouse Rests.
 * Several threads can draw and load it at the same time.
 * There is neither explicit lock nor optimistic one by version check.
 * But there is constraint theRest > 0. So any connection see the current
 * rest (READ_UNCOMMITED mode) and there is no way to make data inconsistent.
 * </pre>
 *
 * @author Yury Demidenko
 */
public class WarehouseRest extends AEditableHasVersion
  implements IHasId<WarehouseRestId> {

  /**
   * <p>Complex ID. Must be initialized cause reflection use.</p>
   **/
  private WarehouseRestId itsId = new WarehouseRestId();

  /**
   * <p>Warehouse Place part of complex ID.</p>
   **/
  private WarehouseSite warehouseSite;

  /**
   * <p>InvItem part of complex ID.</p>
   **/
  private InvItem invItem;

  /**
   * <p>UnitOfMeasure part of complex ID.</p>
   **/
  private UnitOfMeasure unitOfMeasure;

  /**
   * <p>The rest.</p>
   **/
  private BigDecimal theRest = BigDecimal.ZERO;

  /**
   * <p>Default constructor.</p>
   **/
  public WarehouseRest() {
  }

  /**
   * <p>Useful constructor.</p>
   * @param pWarehouseSite reference
   * @param pInvItem reference
   * @param pUnitOfMeasure reference
   * @param pTheRest reference
   **/
  public WarehouseRest(final WarehouseSite pWarehouseSite,
    final InvItem pInvItem, final UnitOfMeasure pUnitOfMeasure,
      final BigDecimal pTheRest) {
    setTheRest(pTheRest);
    setWarehouseSite(pWarehouseSite);
    setUnitOfMeasure(pUnitOfMeasure);
    setInvItem(pInvItem);
  }

  /**
   * <p>Geter for itsId.</p>
   * @return WarehouseRestId
   **/
  @Override
  public final WarehouseRestId getItsId() {
    return this.itsId;
  }

  /**
   * <p>Setter for itsId.</p>
   * @param pItsId reference
   **/
  @Override
  public final void setItsId(final WarehouseRestId pItsId) {
    this.itsId = pItsId;
    if (this.itsId != null) {
      this.invItem = this.itsId.getInvItem();
      this.warehouseSite = this.itsId.getWarehouseSite();
      this.unitOfMeasure = this.itsId.getUnitOfMeasure();
    } else {
      this.invItem = null;
      this.warehouseSite = null;
      this.unitOfMeasure = null;
    }
  }

  /**
   * <p>Setter for warehouseSite.</p>
   * @param pWarehouseSite reference
   **/
  public final void setWarehouseSite(final WarehouseSite pWarehouseSite) {
    this.warehouseSite = pWarehouseSite;
    if (this.itsId == null) {
      this.itsId = new WarehouseRestId();
    }
    this.itsId.setWarehouseSite(this.warehouseSite);
  }

  /**
   * <p>Setter for unitOfMeasure.</p>
   * @param pUnitOfMeasure reference
   **/
  public final void setUnitOfMeasure(final UnitOfMeasure pUnitOfMeasure) {
    this.unitOfMeasure = pUnitOfMeasure;
    if (this.itsId == null) {
      this.itsId = new WarehouseRestId();
    }
    this.itsId.setUnitOfMeasure(this.unitOfMeasure);
  }

  /**
   * <p>Setter for invItem.</p>
   * @param pInvItem reference
   **/
  public final void setInvItem(final InvItem pInvItem) {
    this.invItem = pInvItem;
    if (this.itsId == null) {
      this.itsId = new WarehouseRestId();
    }
    this.itsId.setInvItem(this.invItem);
  }

  //Simple getters and setters:
  /**
   * <p>Geter for unitOfMeasure.</p>
   * @return UnitOfMeasure
   **/
  public final UnitOfMeasure getUnitOfMeasure() {
    return this.unitOfMeasure;
  }

  /**
   * <p>Geter for warehouseSite.</p>
   * @return WarehouseSite
   **/
  public final WarehouseSite getWarehouseSite() {
    return this.warehouseSite;
  }

  /**
   * <p>Geter for invItem.</p>
   * @return InvItem
   **/
  public final InvItem getInvItem() {
    return this.invItem;
  }

  /**
   * <p>Geter for theRest.</p>
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

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

import java.util.Date;
import java.math.BigDecimal;

import org.beigesoft.accounting.persistable.Manufacture;
import org.beigesoft.accounting.persistable.InvItem;
import org.beigesoft.accounting.persistable.UnitOfMeasure;
import org.beigesoft.accounting.persistable.IMakingWarehouseEntry;

/**
 * <pre>
 * Model of wrapper of manufacture to draw product in process
 * (from manuf.process) from warehouse.
 * </pre>
 *
 * @author Yury Demidenko
 */
public class ManufactureForDraw
  implements IMakingWarehouseEntry {

  /**
   * <p>Basis manufacturing process.</p>
   **/
  private Manufacture manufacture;

  /**
   * <p>Only constructor.</p>
   * @param pManufacture reference
   **/
  public ManufactureForDraw(final Manufacture pManufacture) {
    this.manufacture = pManufacture;
  }

  /**
   * <p>Usually it's simple getter that return model ID.</p>
   * @return ID model ID
   **/
  @Override
  public final Long getItsId() {
    return this.manufacture.getItsId();
  }

  /**
   * <p>Usually it's simple setter for model ID.</p>
   * @param pId model ID
   **/
  @Override
  public final void setItsId(final Long pId) {
    //stub
  }

  /**
   * <p>Geter for idDatabaseBirth.</p>
   * @return Integer
   **/
  @Override
  public final Integer getIdDatabaseBirth() {
    return this.manufacture.getIdDatabaseBirth();
  }

  /**
   * <p>Setter for idDatabaseBirth.</p>
   * @param pIdDatabaseBirth reference
   **/
  @Override
  public final void setIdDatabaseBirth(final Integer pIdDatabaseBirth) {
    //stub
  }

  /**
   * <p>Geter for idBirth.</p>
   * @return Long
   **/
  @Override
  public final Long getIdBirth() {
    return this.manufacture.getIdBirth();
  }

  /**
   * <p>Setter for idBirth.</p>
   * @param pIdBirth reference
   **/
  public final void setIdBirth(final Long pIdBirth) {
    //stub
  }

  /**
   * <p>Evaluate "is new" status.
   * Usually it's simple getter.
   * </p>
   * @return boolean "is new?"
   **/
  @Override
  public final Boolean getIsNew() {
    return this.manufacture.getIsNew();
  }
  /**
   * <p>Set "is new" status.
   * Usually it's simple setter.
   * </p>
   * @param isNew "is new?"
   **/
  @Override
  public final void setIsNew(final Boolean isNew) {
    //stub
  }

  /**
   * <p>Geter for invItem.</p>
   * @return InvItem
   **/
  @Override
  public final InvItem getInvItem() {
    return this.manufacture.getManufacturingProcess().getInvItem();
  }

  /**
   * <p>Setter for invItem.</p>
   * @param pInvItem reference
   **/
  @Override
  public final void setInvItem(final InvItem pInvItem) {
    //stub
  }

  /**
   * <p>Geter for unitOfMeasure.</p>
   * @return UnitOfMeasure
   **/
  @Override
  public final UnitOfMeasure getUnitOfMeasure() {
    return this.manufacture.getUnitOfMeasure();
  }

  /**
   * <p>Setter for unitOfMeasure.</p>
   * @param pUnitOfMeasure reference
   **/
  @Override
  public final void setUnitOfMeasure(final UnitOfMeasure pUnitOfMeasure) {
    //stub
  }

  /**
   * <p>Geter for itsQuantity.</p>
   * @return BigDecimal
   **/
  @Override
  public final BigDecimal getItsQuantity() {
    return this.manufacture.getItsQuantity();
  }

  /**
   * <p>Setter for itsQuantity.</p>
   * @param pItsQuantity reference
   **/
  @Override
  public final void setItsQuantity(final BigDecimal pItsQuantity) {
    //stub
  }

  /**
   * <p>Geter for reversedId.</p>
   * @return Long
   **/
  @Override
  public final Long getReversedId() {
    return this.manufacture.getReversedId();
  }

  /**
   * <p>Setter for reversedId.</p>
   * @param pReversedId reference
   **/
  @Override
  public final void setReversedId(final Long pReversedId) {
    this.manufacture.setReversedId(pReversedId);
  }

  /**
   * <p>Constant of code type.</p>
   * @return 5
   **/
  @Override
  public final Integer constTypeCode() {
    return this.manufacture.constTypeCode();
  }

  /**
   * <p>Get for document Date.</p>
   * @return Date
   **/
  @Override
  public final Date getDocumentDate() {
    return this.manufacture.getItsDate();
  }

  /**
   * <p>Get Owner Type if exist  e.g. PurchaseInvoice 1.</p>
   * @return Integer
   **/
  @Override
  public final Integer getOwnerType() {
    return null;
  }

  /**
   * <p>Get for owner's ID.</p>
   * @return Long
   **/
  @Override
  public final Long getOwnerId() {
    return null;
  }

  /**
   * <p>Getter for description.</p>
   * @return String
   **/
  @Override
  public final String getDescription() {
    return this.manufacture.getDescription();
  }

  /**
   * <p>Setter for description.</p>
   * @param pDescription reference
   **/
  @Override
  public final void setDescription(final String pDescription) {
    this.manufacture.setDescription(pDescription);
  }

  //Simple getters and setters:
  /**
   * <p>Getter for manufacture.</p>
   * @return Manufacture
   **/
  public final Manufacture getManufacture() {
    return this.manufacture;
  }
}

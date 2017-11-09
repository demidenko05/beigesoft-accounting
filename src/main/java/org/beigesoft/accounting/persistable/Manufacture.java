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

import org.beigesoft.accounting.persistable.base.AInvItemMovementCost;
import org.beigesoft.accounting.model.EWarehouseMovementType;

/**
 * <pre>
 * Model of manufacturing finished product or material
 * based on completed ManufacturingProcess.
 * It just withdrawal product in process from ManufacturingProcess
 * then put it in warehouse as product or material.
 * </pre>
 *
 * @author Yury Demidenko
 */
public class Manufacture extends AInvItemMovementCost
  implements IDrawItemSource, IDocWarehouse {

  /**
   * <p>Date.</p>
   **/
  private Date itsDate;

  /**
   * <p>If document has made accounting entries
   * then he can only reverse whole document.</p>
   **/
  private Boolean hasMadeAccEntries = false;

  /**
   * <p>Basis manufacturing process.</p>
   **/
  private ManufacturingProcess manufacturingProcess;

  /**
   * <p>Warehouse site from (optional).
   * If it's empty (null) then withdrawal will be from the first
   * site/s that has the goods, otherwise withdrawal will be exactly
   * from this site.</p>
   **/
  private WarehouseSite warehouseSiteFo;

  /**
   * <p>Warehouse Place e.g. refrigerator.</p>
   **/
  private WarehouseSite warehouseSite;

  /**
   * <p>The rest, charged by the ManufacturingProcess quantity,
   * draws by sales, loss etc.</p>
   **/
  private BigDecimal theRest = BigDecimal.ZERO;

  /**
   * <p>ID of reversed/reversing manufacture.</p>
   **/
  private Long reversedId;

  /**
   * <p>ID database birth of reversed/reversing document.</p>
   **/
  private Integer reversedIdDatabaseBirth;

  /**
   * <p>Description.</p>
   **/
  private String description;

  /**
   * <p>Getter for theRest.</p>
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
   * <p>Geter for hasMadeAccEntries.</p>
   * @return Boolean
   **/
  @Override
  public final Boolean getHasMadeAccEntries() {
    return this.hasMadeAccEntries;
  }

  /**
   * <p>Setter for hasMadeAccEntries.</p>
   * @param pHasMadeAccEntries reference
   **/
  @Override
  public final void setHasMadeAccEntries(final Boolean pHasMadeAccEntries) {
    this.hasMadeAccEntries = pHasMadeAccEntries;
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
   * <p>Geter for reversed database Id.</p>
   * @return Integer DB birth ID
   **/
  @Override
  public final Integer getReversedIdDatabaseBirth() {
    return this.reversedIdDatabaseBirth;
  }

  /**
   * <p>Setter for reversed database Id.</p>
   * @param pReversedIdDatabaseBirth reference
   **/
  @Override
  public final void setReversedIdDatabaseBirth(
    final Integer pReversedIdDatabaseBirth) {
    this.reversedIdDatabaseBirth = pReversedIdDatabaseBirth;
  }

  /**
   * <p>Constant of code type.</p>
   * @return 5
   **/
  @Override
  public final Integer constTypeCode() {
    return 5;
  }

  /**
   * <p>If owned lines make warehouse entries this return
   * their type.</p>
   * @return Boolean
   **/
  @Override
  public final EWarehouseMovementType getLinesWarehouseType() {
    return null;
  }

  /**
   * <p>Get for document Date.</p>
   * @return Date
   **/
  @Override
  public final Date getDocumentDate() {
    return getItsDate();
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

  //Hiding references getters and setters:
  /**
   * <p>Geter for itsDate.</p>
   * @return Date
   **/
  @Override
  public final Date getItsDate() {
    if (this.itsDate == null) {
      return null;
    }
    return new Date(this.itsDate.getTime());
  }

  /**
   * <p>Setter for itsDate.</p>
   * @param pItsDate reference
   **/
  @Override
  public final void setItsDate(final Date pItsDate) {
    if (pItsDate == null) {
      this.itsDate = null;
    } else {
      this.itsDate = new Date(pItsDate.getTime());
    }
  }

  //Simple getters and setters:
  /**
   * <p>Geter for manufacturingProcess.</p>
   * @return ManufacturingProcess
   **/
  public final ManufacturingProcess getManufacturingProcess() {
    return this.manufacturingProcess;
  }

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

  /**
   * <p>Setter for manufacturingProcess.</p>
   * @param pManufacturingProcess reference
   **/
  public final void setManufacturingProcess(
    final ManufacturingProcess pManufacturingProcess) {
    this.manufacturingProcess = pManufacturingProcess;
  }

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

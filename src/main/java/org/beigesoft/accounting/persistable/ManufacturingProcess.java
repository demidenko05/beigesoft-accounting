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

import java.util.List;
import java.util.Date;
import java.math.BigDecimal;

import org.beigesoft.accounting.persistable.base.AInvItemMovementCost;
import org.beigesoft.accounting.model.EWarehouseMovementType;

/**
 * <pre>
 * Model of making product in progress from material
 * and collecting all costs. After complete it also make
 * accounting entries inventory material and costs
 * into inventory in progress.
 * </pre>
 *
 * @author Yury Demidenko
 */
public class ManufacturingProcess extends AInvItemMovementCost
  implements IDrawItemSource, IDocWarehouse {

  /**
   * <p>Date start of manufacturing.</p>
   **/
  private Date itsDate;

  /**
   * <p>If document has made accounting entries
   * then user can only reverse whole document.</p>
   **/
  private Boolean hasMadeAccEntries = false;

  /**
   * <p>Total materials costs.</p>
   **/
  private BigDecimal totalMaterialsCost = BigDecimal.ZERO;

  /**
   * <p>Total additional direct/indirect uncapitalized costs.</p>
   **/
  private BigDecimal totalAdditionCost = BigDecimal.ZERO;

  /**
   * <p>Warehouse site, Not Null, e.g. kitchen.</p>
   **/
  private WarehouseSite warehouseSite;

  /**
   * <p>Sign what manufacturing is complete.
   * After that this ManufacturingProcess and its lines become none-editable.
   * User can only make accounting records (if hi didn't it)
   * and reverse whole manufacture.
   * </p>
   **/
  private Boolean isComplete = false;

  /**
   * <p>ID of reversed/reversing manufacture.</p>
   **/
  private Long reversedId;

  /**
   * <p>ID database birth of reversed/reversing document.</p>
   **/
  private Integer reversedIdDatabaseBirth;

  /**
   * <p>The rest, charged by the ManufacturingProcess quantity,
   * draws by sales, loss etc.</p>
   **/
  private BigDecimal theRest = BigDecimal.ZERO;

  /**
   * <p>Used materials.</p>
   **/
  private List<UsedMaterialLine> usedMaterials;

  /**
   * <p>Additional direct/indirect uncapitalized costs.</p>
   **/
  private List<AdditionCostLine> additionCosts;

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
   * <p>Constant of code type.</p>
   * @return 4
   **/
  @Override
  public final Integer constTypeCode() {
    return 4;
  }

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
   * <p>If owned lines make warehouse entries this return
   * their type.</p>
   * @return Boolean
   **/
  @Override
  public final EWarehouseMovementType getLinesWarehouseType() {
    return EWarehouseMovementType.WITHDRAWAL;
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
   * <p>Get for owner's ID if exist.</p>
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

  /**
   * <p>Geter for totalMaterialsCost.</p>
   * @return BigDecimal
   **/
  public final BigDecimal getTotalMaterialsCost() {
    return this.totalMaterialsCost;
  }

  /**
   * <p>Setter for totalMaterialsCost.</p>
   * @param pTotalMaterialsCost reference
   **/
  public final void setTotalMaterialsCost(
    final BigDecimal pTotalMaterialsCost) {
    this.totalMaterialsCost = pTotalMaterialsCost;
  }

  /**
   * <p>Geter for totalAdditionCost.</p>
   * @return BigDecimal
   **/
  public final BigDecimal getTotalAdditionCost() {
    return this.totalAdditionCost;
  }

  /**
   * <p>Setter for totalAdditionCost.</p>
   * @param pTotalAdditionCost reference
   **/
  public final void setTotalAdditionCost(final BigDecimal pTotalAdditionCost) {
    this.totalAdditionCost = pTotalAdditionCost;
  }

  /**
   * <p>Geter for isComplete.</p>
   * @return Boolean
   **/
  public final Boolean getIsComplete() {
    return this.isComplete;
  }

  /**
   * <p>Setter for isComplete.</p>
   * @param pIsComplete reference
   **/
  public final void setIsComplete(final Boolean pIsComplete) {
    this.isComplete = pIsComplete;
  }

  /**
   * <p>Geter for usedMaterials.</p>
   * @return List<UsedMaterialLine>
   **/
  public final List<UsedMaterialLine> getUsedMaterials() {
    return this.usedMaterials;
  }

  /**
   * <p>Setter for usedMaterials.</p>
   * @param pUsedMaterials reference
   **/
  public final void setUsedMaterials(
    final List<UsedMaterialLine> pUsedMaterials) {
    this.usedMaterials = pUsedMaterials;
  }

  /**
   * <p>Geter for additionCosts.</p>
   * @return List<AdditionCostLine>
   **/
  public final List<AdditionCostLine> getAdditionCosts() {
    return this.additionCosts;
  }

  /**
   * <p>Setter for additionCosts.</p>
   * @param pAdditionCosts reference
   **/
  public final void setAdditionCosts(
    final List<AdditionCostLine> pAdditionCosts) {
    this.additionCosts = pAdditionCosts;
  }
}

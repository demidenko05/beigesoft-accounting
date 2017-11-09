package org.beigesoft.accounting.processor;

/*
 * Copyright (c) 2017 Beigesoft ™
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
import java.util.Map;
import java.math.BigDecimal;

import org.beigesoft.exception.ExceptionWithCode;
import org.beigesoft.model.IRequestData;
import org.beigesoft.accounting.model.ManufactureForDraw;
import org.beigesoft.accounting.persistable.Manufacture;
import org.beigesoft.accounting.persistable.ManufacturingProcess;
import org.beigesoft.accounting.persistable.InvItem;
import org.beigesoft.accounting.persistable.UseMaterialEntry;

/**
 * <p>Process that save manufacturing process.</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class PrcManufactureSave<RS>
  extends APrcAccDocUseMaterialSave<RS, Manufacture> {

  /**
   * <p>Make save preparations before insert/update block if it's need.</p>
   * @param pAddParam additional param
   * @param pEntity entity
   * @param pRequestData Request Data
   * @throws Exception - an exception
   **/
  @Override
  public final void makeFirstPrepareForSave(final Map<String, Object> pAddParam,
    final Manufacture pEntity,
      final IRequestData pRequestData) throws Exception {
    // Beige-ORM refresh:
    pEntity.setManufacturingProcess(getSrvOrm()
      .retrieveEntity(pAddParam, pEntity.getManufacturingProcess()));
    pEntity.setInvItem(getSrvOrm()
      .retrieveEntity(pAddParam, pEntity.getInvItem()));
    if (!(InvItem.FINISHED_PRODUCT_ID.equals(pEntity.getInvItem()
      .getItsType().getItsId()) || InvItem.MATERIAL_ID
        .equals(pEntity.getInvItem().getItsType().getItsId()))) {
      throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
        "type_must_be_product_material");
    }
    if (pEntity.getItsQuantity().compareTo(pEntity.getManufacturingProcess()
      .getItsQuantity()) > 0) {
      throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
        "source_has_no_enough_item");
    }
    if (!pEntity.getUnitOfMeasure().getItsId().equals(pEntity
      .getManufacturingProcess().getUnitOfMeasure().getItsId())) {
      throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
        "UnitOfMeasure_fiffer_with_source");
    }
    if (pEntity.getItsQuantity().doubleValue() < 0
      && pEntity.getReversedId() == null) {
      throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
        "reversed_manufacture_is_null");
    }
    pEntity.setItsQuantity(pEntity.getItsQuantity().setScale(
      getSrvAccSettings().lazyGetAccSettings(pAddParam).getQuantityPrecision(),
        getSrvAccSettings().lazyGetAccSettings(pAddParam).getRoundingMode()));
    if (pEntity.getReversedId() != null) {
      Manufacture reversed = getSrvOrm().retrieveEntityById(pAddParam,
        Manufacture.class, pEntity.getReversedId());
      if (reversed.getReversedId() != null) {
        throw new ExceptionWithCode(ExceptionWithCode.FORBIDDEN,
          "Attempt to double reverse" + pAddParam.get("user"));
      }
      if (!reversed.getItsQuantity().equals(reversed.getTheRest())) {
        throw new ExceptionWithCode(ExceptionWithCode
          .WRONG_PARAMETER, "where_is_withdrawals_from_this_source");
      }
      pEntity.setTheRest(BigDecimal.ZERO);
    } else {
      pEntity.setTheRest(pEntity.getItsQuantity());
    }
    pEntity.setItsCost(pEntity.getManufacturingProcess().getItsCost());
    pEntity.setItsTotal(pEntity.getItsCost()
      .multiply(pEntity.getItsQuantity()).setScale(
        getSrvAccSettings().lazyGetAccSettings(pAddParam).getCostPrecision(),
          getSrvAccSettings().lazyGetAccSettings(pAddParam).getRoundingMode()));
  }

  /**
   * <p>Make other entries include reversing if it's need when save.</p>
   * @param pAddParam additional param
   * @param pEntity entity
   * @param pRequestData Request Data
   * @param pIsNew if entity was new
   * @throws Exception - an exception
   **/
  @Override
  public final void makeOtherEntries(final Map<String, Object> pAddParam,
    final Manufacture pEntity, final IRequestData pRequestData,
      final boolean pIsNew) throws Exception {
    //always new
    ManufactureForDraw manufactureForDraw = new ManufactureForDraw(pEntity);
    if (pEntity.getReversedId() != null) {
      //reverse draw product in process from warehouse
      getSrvWarehouseEntry().reverseDraw(pAddParam, manufactureForDraw);
      //reverse draw product in process from manufacturing process
      useMaterialReverse(pAddParam, pEntity);
      //reverse acc.entries already done
    } else {
      //draw product in process from warehouse
      getSrvWarehouseEntry().withdrawal(pAddParam, manufactureForDraw,
        pEntity.getWarehouseSiteFo());
      //draw product in process from manufacturing process
      useMaterial(pAddParam,  pEntity);
      //it will update this doc:
      getSrvAccEntry().makeEntries(pAddParam, pEntity);
    }
    //load(put) or reverse product or created material on warehouse
    getSrvWarehouseEntry().load(pAddParam, pEntity, pEntity.getWarehouseSite());
  }

  /**
   * <p>Check other fraud update e.g. prevent change completed unaccounted
   * manufacturing process.</p>
   * @param pAddParam additional param
   * @param pEntity entity
   * @param pRequestData Request Data
   * @param pOldEntity old saved entity
   * @throws Exception - an exception
   **/
  @Override
  public final void checkOtherFraudUpdate(final Map<String, Object> pAddParam,
    final Manufacture pEntity, final IRequestData pRequestData,
      final Manufacture pOldEntity) throws Exception {
    // nothing
  }

  /**
   * <p>Additional check document for ready to account (make acc.entries).</p>
   * @param pAddParam additional param
   * @param pEntity entity
   * @param pRequestData Request Data
   * @throws Exception - an exception if don't
   **/
  @Override
  public final void addCheckIsReadyToAccount(
    final Map<String, Object> pAddParam,
      final Manufacture pEntity,
        final IRequestData pRequestData) throws Exception {
    // nothing
  }

  //Utils:
  /**
   * <p>Make description for warehouse entry.</p>
   * @param pEntity movement
   * @return description
   **/
  public final String makeDescription(final Manufacture pEntity) {
    String strWho = getSrvI18n().getMsg(pEntity.getClass().getSimpleName()
      + "short") + " #" + pEntity.getIdDatabaseBirth() + "-" + pEntity
        .getItsId() + ", " + getDateFormatter().format(pEntity.getItsDate());
    String strFrom = " " + getSrvI18n().getMsg("from") + " " + getSrvI18n()
      .getMsg(ManufacturingProcess.class.getSimpleName() + "short") + " #"
        + pEntity.getManufacturingProcess().getIdDatabaseBirth() + "-"
          + pEntity.getManufacturingProcess().getItsId(); //local
    return getSrvI18n().getMsg("made_at") + " " + getDateFormatter()
      .format(new Date()) + " " + getSrvI18n().getMsg("by") + " "
        + strWho + strFrom;
  }

  /**
   * <p>Make use material.</p>
   * @param pAddParam additional param
   * @param pEntity Manufacture
   * @throws Exception - an exception
   **/
  public final void useMaterial(final Map<String, Object> pAddParam,
    final Manufacture pEntity)
    throws Exception {
    //draw product in process from manufacturing process
    pEntity.getManufacturingProcess().setTheRest(pEntity
      .getManufacturingProcess().getTheRest()
        .subtract(pEntity.getItsQuantity()));
    getSrvOrm().updateEntity(pAddParam, pEntity.getManufacturingProcess());
    UseMaterialEntry die = new UseMaterialEntry();
    die.setItsDate(pEntity.getItsDate());
    die.setIdDatabaseBirth(getSrvOrm().getIdDatabase());
    die.setSourceType(pEntity.getManufacturingProcess().constTypeCode());
    die.setSourceId(pEntity.getManufacturingProcess().getItsId());
    die.setDrawingType(pEntity.constTypeCode());
    die.setDrawingId(pEntity.getItsId());
    die.setDrawingOwnerId(null);
    die.setDrawingOwnerType(null);
    die.setSourceOwnerId(null);
    die.setSourceOwnerType(null);
    die.setItsQuantity(pEntity.getItsQuantity());
    die.setItsCost(pEntity.getManufacturingProcess().getItsCost());
    die.setInvItem(pEntity.getManufacturingProcess().getInvItem());
    die.setUnitOfMeasure(pEntity.getManufacturingProcess().getUnitOfMeasure());
    die.setItsTotal(die.getItsCost().
      multiply(die.getItsQuantity()));
    die.setDescription(makeDescription(pEntity));
    getSrvOrm().insertEntity(pAddParam, die);
  }

  /**
   * <p>Make use material reverse.</p>
   * @param pAddParam additional param
   * @param pEntity Manufacture
   * @throws Exception - an exception
   **/
  public final void useMaterialReverse(final Map<String, Object> pAddParam,
    final Manufacture pEntity)
    throws Exception {
    //reverse draw product in process from manufacturing process
    UseMaterialEntry dies = getSrvOrm()
      .retrieveEntityWithConditions(pAddParam, UseMaterialEntry.class,
        " where DRAWINGTYPE=" + pEntity.constTypeCode()
          + " and DRAWINGID=" + pEntity.getReversedId());
    UseMaterialEntry die = new UseMaterialEntry();
    die.setItsDate(pEntity.getItsDate());
    die.setIdDatabaseBirth(getSrvOrm().getIdDatabase());
    die.setSourceType(dies.getSourceType());
    die.setSourceId(dies.getSourceId());
    die.setDrawingType(pEntity.constTypeCode());
    die.setDrawingId(pEntity.getItsId());
    die.setDrawingOwnerId(null);
    die.setDrawingOwnerType(null);
    die.setSourceOwnerId(dies.getSourceOwnerId());
    die.setSourceOwnerType(dies.getSourceOwnerType());
    die.setItsCost(dies.getItsCost());
    die.setItsTotal(dies.getItsTotal().negate());
    die.setUnitOfMeasure(dies.getUnitOfMeasure());
    die.setInvItem(dies.getInvItem());
    die.setItsQuantity(dies.getItsQuantity().negate());
    die.setReversedId(die.getItsId());
    die.setDescription(makeDescription(pEntity) + " " + getSrvI18n()
      .getMsg("reversed_entry_n") + dies.getIdDatabaseBirth() + "-"
        + dies.getItsId());
    getSrvOrm().insertEntity(pAddParam, die);
    pEntity.getManufacturingProcess().setTheRest(pEntity
      .getManufacturingProcess().getTheRest().add(dies.getItsQuantity()));
    getSrvOrm().updateEntity(pAddParam, pEntity.getManufacturingProcess());
    dies.setReversedId(die.getItsId());
    dies.setDescription(dies.getDescription() + " " + getSrvI18n()
      .getMsg("reversing_entry_n") + die.getIdDatabaseBirth() + "-"
        + die.getItsId()); //local
    getSrvOrm().updateEntity(pAddParam, dies);
  }
}

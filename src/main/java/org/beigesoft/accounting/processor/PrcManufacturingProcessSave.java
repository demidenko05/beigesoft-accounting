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

import java.util.Map;
import java.util.List;
import java.math.BigDecimal;

import org.beigesoft.exception.ExceptionWithCode;
import org.beigesoft.model.IRequestData;
import org.beigesoft.accounting.persistable.ManufacturingProcess;
import org.beigesoft.accounting.persistable.UsedMaterialLine;
import org.beigesoft.accounting.persistable.AdditionCostLine;
import org.beigesoft.accounting.persistable.InvItem;

/**
 * <p>Process that save manufacturing process.</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class PrcManufacturingProcessSave<RS>
  extends APrcAccDocUseMaterialSave<RS, ManufacturingProcess> {

  /**
   * <p>Make save preparations before insert/update block if it's need.</p>
   * @param pAddParam additional param
   * @param pEntity entity
   * @param pRequestData Request Data
   * @throws Exception - an exception
   **/
  @Override
  public final void makeFirstPrepareForSave(final Map<String, Object> pAddParam,
    final ManufacturingProcess pEntity,
      final IRequestData pRequestData) throws Exception {
    if (pEntity.getItsQuantity().doubleValue() == 0) {
      throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
        "Quantity is 0! " + pAddParam.get("user"));
    }
    pEntity.setInvItem(getSrvOrm()
      .retrieveEntity(pAddParam, pEntity.getInvItem()));
    if (!InvItem.WORK_IN_PROGRESS_ID.equals(pEntity.getInvItem().getItsType()
      .getItsId())) {
      throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
        "type_must_be_work_in_progress");
    }
    if (pEntity.getReversedId() != null) {
      pEntity.setIsComplete(true);
      pEntity.setTheRest(BigDecimal.ZERO);
    } else {
      pEntity.setTheRest(pEntity.getItsQuantity());
    }
    pEntity.setItsCost(pEntity.getItsTotal().divide(
      pEntity.getItsQuantity(), getSrvAccSettings()
        .lazyGetAccSettings(pAddParam).getCostPrecision(),
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
    final ManufacturingProcess pEntity, final IRequestData pRequestData,
      final boolean pIsNew) throws Exception {
    String actionAdd = pRequestData.getParameter("actionAdd");
    if ("makeAccEntries".equals(actionAdd)
      && pEntity.getReversedId() != null) {
      pEntity.setTheRest(BigDecimal.ZERO);
      //reverse none-reversed lines:
      ManufacturingProcess reversed = getSrvOrm()
        .retrieveEntityById(pAddParam, ManufacturingProcess.class,
          pEntity.getReversedId());
      reversed.setTheRest(BigDecimal.ZERO);
      getSrvOrm().updateEntity(pAddParam, reversed);
      UsedMaterialLine umlrd = new UsedMaterialLine();
      umlrd.setItsOwner(reversed);
      List<UsedMaterialLine> reversedMaterials = getSrvOrm().
        retrieveListForField(pAddParam, umlrd, "itsOwner");
      for (UsedMaterialLine reversedLine : reversedMaterials) {
        if (reversedLine.getReversedId() == null) {
          UsedMaterialLine reversingLine = new UsedMaterialLine();
          reversingLine.setIdDatabaseBirth(getSrvOrm().getIdDatabase());
          reversingLine.setReversedId(reversedLine.getItsId());
          reversingLine.setItsCost(reversedLine.getItsCost());
          reversingLine.setInvItem(reversedLine.getInvItem());
          reversingLine.setUnitOfMeasure(reversedLine.getUnitOfMeasure());
          reversingLine.setItsQuantity(reversedLine.getItsQuantity().negate());
          reversingLine.setItsTotal(reversedLine.getItsTotal().negate());
          reversingLine.setIsNew(true);
          reversingLine.setItsOwner(pEntity);
          reversingLine.setDescription(getSrvI18n().getMsg("reversed_n")
            + reversedLine.getIdDatabaseBirth() + "-"
              + reversedLine.getItsId()); //local
          getSrvOrm().insertEntity(pAddParam, reversingLine);
          getSrvWarehouseEntry().reverseDraw(pAddParam, reversingLine);
          getSrvUseMaterialEntry().reverseDraw(pAddParam, reversingLine,
            pEntity.getItsDate(), pEntity.getItsId());
          String descr;
          if (reversedLine.getDescription() == null) {
            descr = "";
          } else {
            descr = reversedLine.getDescription();
          }
          reversedLine.setDescription(descr
            + " " + getSrvI18n().getMsg("reversing_n") + reversingLine
              .getIdDatabaseBirth() + "-" + reversingLine.getItsId());
          reversedLine.setReversedId(reversingLine.getItsId());
          getSrvOrm().updateEntity(pAddParam, reversedLine);
        }
      }
      AdditionCostLine acl = new AdditionCostLine();
      acl.setItsOwner(reversed);
      List<AdditionCostLine> reversedAcl = getSrvOrm().
        retrieveListForField(pAddParam, acl, "itsOwner");
      for (AdditionCostLine reversedLine : reversedAcl) {
        if (reversedLine.getReversedId() == null) {
          AdditionCostLine reversingLine = new AdditionCostLine();
          reversingLine.setIdDatabaseBirth(getSrvOrm().getIdDatabase());
          reversingLine.setReversedId(reversedLine.getItsId());
          reversingLine.setSubaccExpense(reversedLine.getSubaccExpense());
          reversingLine.setSubaccExpenseId(reversedLine.getSubaccExpenseId());
          reversingLine.setSubaccExpenseType(reversedLine
            .getSubaccExpenseType());
          reversingLine.setAccExpense(reversedLine.getAccExpense());
          reversingLine.setItsTotal(reversedLine.getItsTotal().negate());
          reversingLine.setIsNew(true);
          reversingLine.setItsOwner(pEntity);
          reversingLine.setDescription(getSrvI18n().getMsg("reversed_n")
            + reversedLine.getIdDatabaseBirth() + "-"
              + reversedLine.getItsId()); //local
          getSrvOrm().insertEntity(pAddParam, reversingLine);
          String descr;
          if (reversedLine.getDescription() == null) {
            descr = "";
          } else {
            descr = reversedLine.getDescription();
          }
          reversedLine.setDescription(descr
            + " " + getSrvI18n().getMsg("reversing_n") + reversingLine
              .getIdDatabaseBirth() + "-" + reversingLine.getItsId());
          reversedLine.setReversedId(reversingLine.getItsId());
          getSrvOrm().updateEntity(pAddParam, reversedLine);
        }
      }
    }
    if (pEntity.getIsComplete()) { //need to make warehouse entries
      if (pEntity.getItsCost().doubleValue() <= 0) {
        throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
          "cost_less_or_eq_zero");
      }
      //load or reverse load:
      getSrvWarehouseEntry().load(pAddParam, pEntity,
        pEntity.getWarehouseSite());
    }
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
    final ManufacturingProcess pEntity, final IRequestData pRequestData,
      final ManufacturingProcess pOldEntity) throws Exception {
    if (pOldEntity.getIsComplete()) {
      throw new ExceptionWithCode(ExceptionWithCode.FORBIDDEN,
        "attempt_to_change_completed_manufacturing_process");
    }
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
      final ManufacturingProcess pEntity,
        final IRequestData pRequestData) throws Exception {
    if (!pEntity.getIsComplete()) {
      throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
        "manufacturing_must_be_completed");
    }
  }
}

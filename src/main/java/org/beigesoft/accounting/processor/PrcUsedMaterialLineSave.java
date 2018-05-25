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
import java.math.BigDecimal;

import org.beigesoft.model.IRequestData;
import org.beigesoft.exception.ExceptionWithCode;
import org.beigesoft.service.IEntityProcessor;
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.service.ISrvDatabase;
import org.beigesoft.accounting.persistable.InvItem;
import org.beigesoft.accounting.persistable.UsedMaterialLine;
import org.beigesoft.accounting.persistable.ManufacturingProcess;
import org.beigesoft.accounting.persistable.UseMaterialEntry;
import org.beigesoft.accounting.service.ISrvWarehouseEntry;
import org.beigesoft.accounting.service.ISrvDrawItemEntry;
import org.beigesoft.accounting.service.ISrvAccSettings;

/**
 * <p>Service that save UsedMaterialLine into DB.</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class PrcUsedMaterialLineSave<RS>
  implements IEntityProcessor<UsedMaterialLine, Long> {

  /**
   * <p>Database service.</p>
   **/
  private ISrvDatabase<RS> srvDatabase;

  /**
   * <p>ORM service.</p>
   **/
  private ISrvOrm<RS> srvOrm;

  /**
   * <p>Business service for warehouse.</p>
   **/
  private ISrvWarehouseEntry srvWarehouseEntry;

  /**
   * <p>Business service for draw any item to sale/loss/stole.</p>
   **/
  private ISrvDrawItemEntry<UseMaterialEntry> srvUseMaterialEntry;

  /**
   * <p>Business service for accounting settings.</p>
   **/
  private ISrvAccSettings srvAccSettings;

  /**
   * <p>Process entity request.</p>
   * @param pAddParam additional param, e.g. return this line's
   * document in "nextEntity" for farther process
   * @param pRequestData Request Data
   * @param pEntity Entity to process
   * @return Entity processed for farther process or null
   * @throws Exception - an exception
   **/
  @Override
  public final UsedMaterialLine process(
    final Map<String, Object> pAddParam,
      final UsedMaterialLine pEntity,
        final IRequestData pRequestData) throws Exception {

    if (pEntity.getIsNew()) {
      if (pEntity.getItsQuantity().doubleValue() <= 0
        && pEntity.getReversedId() == null) {
        throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
          "quantity_less_or_equal_zero::" + pAddParam.get("user"));
      }
      // Beige-Orm refresh:
      pEntity.setInvItem(getSrvOrm()
        .retrieveEntity(pAddParam, pEntity.getInvItem()));
      pEntity.setItsOwner(getSrvOrm()
        .retrieveEntity(pAddParam, pEntity.getItsOwner()));
      // optimistic locking (dirty check):
      Long ownerVersion = Long.valueOf(pRequestData
        .getParameter(ManufacturingProcess.class.getSimpleName()
          + ".ownerVersion"));
      pEntity.getItsOwner().setItsVersion(ownerVersion);
      if (pEntity.getItsOwner().getIsComplete()) {
        throw new ExceptionWithCode(ExceptionWithCode.FORBIDDEN,
          "attempt_to_change_completed_manufacturing_process::"
            + pAddParam.get("user"));
      }
      pEntity.setInvItem(getSrvOrm()
        .retrieveEntity(pAddParam, pEntity.getInvItem()));
      if (!InvItem.MATERIAL_ID.equals(pEntity.getInvItem().getItsType()
        .getItsId())) {
        throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
          "type_must_be_material::" + pAddParam.get("user"));
      }
      pEntity.setItsQuantity(pEntity.getItsQuantity().setScale(
        getSrvAccSettings().lazyGetAccSettings(pAddParam)
          .getQuantityPrecision(), getSrvAccSettings()
            .lazyGetAccSettings(pAddParam).getRoundingMode()));
      getSrvOrm().insertEntity(pAddParam, pEntity);
      pEntity.setItsOwner(pEntity.getItsOwner());
      if (pEntity.getReversedId() != null) {
        UsedMaterialLine reversed = getSrvOrm().retrieveEntityById(
          pAddParam, UsedMaterialLine.class, pEntity.getReversedId());
        if (reversed.getReversedId() != null) {
          throw new ExceptionWithCode(ExceptionWithCode.FORBIDDEN,
            "attempt_to_reverse_reversed::" + pAddParam.get("user"));
        }
        reversed.setReversedId(pEntity.getItsId());
        getSrvOrm().updateEntity(pAddParam, reversed);
        srvWarehouseEntry.reverseDraw(pAddParam, pEntity);
        srvUseMaterialEntry.reverseDraw(pAddParam, pEntity,
          pEntity.getItsOwner().getItsDate(),
            pEntity.getItsOwner().getItsId());
      } else {
        srvWarehouseEntry.withdrawal(pAddParam, pEntity,
          pEntity.getWarehouseSiteFo());
        srvUseMaterialEntry.withdrawal(pAddParam, pEntity,
          pEntity.getItsOwner().getItsDate(),
            pEntity.getItsOwner().getItsId());
      }
      //Total line:
      String query =
      "select sum(ITSTOTAL) as ITSTOTAL from"
        + " USEMATERIALENTRY where DRAWINGID=" + pEntity.getItsId()
          + " and DRAWINGTYPE=" + pEntity.constTypeCode();
      Double total = getSrvDatabase().evalDoubleResult(query, "ITSTOTAL");
      pEntity.setItsTotal(BigDecimal.valueOf(total)
        .setScale(getSrvAccSettings().lazyGetAccSettings(pAddParam)
          .getCostPrecision(), getSrvAccSettings()
            .lazyGetAccSettings(pAddParam).getRoundingMode()));
      pEntity.setItsCost(pEntity.getItsTotal().divide(pEntity.getItsQuantity(),
        getSrvAccSettings().lazyGetAccSettings(pAddParam).getCostPrecision(),
          getSrvAccSettings().lazyGetAccSettings(pAddParam).getRoundingMode()));
      getSrvOrm().updateEntity(pAddParam, pEntity);
      //Total document:
      query =
      "select sum(ITSTOTAL) as ITSTOTAL from"
        + " USEDMATERIALLINE where ITSOWNER="
          + pEntity.getItsOwner().getItsId();
      total = getSrvDatabase().evalDoubleResult(query, "ITSTOTAL");
      pEntity.getItsOwner().setTotalMaterialsCost(BigDecimal.valueOf(total)
        .setScale(getSrvAccSettings().lazyGetAccSettings(pAddParam)
          .getCostPrecision(), getSrvAccSettings()
            .lazyGetAccSettings(pAddParam).getRoundingMode()));
      pEntity.getItsOwner().setItsTotal(pEntity.getItsOwner()
        .getTotalMaterialsCost().add(pEntity.getItsOwner()
          .getTotalAdditionCost()));
      pEntity.getItsOwner().setItsCost(pEntity.getItsOwner().getItsTotal()
        .divide(pEntity.getItsOwner().getItsQuantity(), getSrvAccSettings()
          .lazyGetAccSettings(pAddParam).getCostPrecision(),
            getSrvAccSettings().lazyGetAccSettings(pAddParam)
              .getRoundingMode()));
      getSrvOrm().updateEntity(pAddParam, pEntity.getItsOwner());
      pAddParam.put("nextEntity", pEntity.getItsOwner());
      pAddParam.put("nameOwnerEntity", ManufacturingProcess
        .class.getSimpleName());
      return null;
    } else {
      throw new ExceptionWithCode(ExceptionWithCode.FORBIDDEN,
        "edit_not_allowed::" + pAddParam.get("user"));
    }
  }

  //Simple getters and setters:
  /**
   * <p>Geter for srvDatabase.</p>
   * @return ISrvDatabase
   **/
  public final ISrvDatabase<RS> getSrvDatabase() {
    return this.srvDatabase;
  }

  /**
   * <p>Setter for srvDatabase.</p>
   * @param pSrvDatabase reference
   **/
  public final void setSrvDatabase(final ISrvDatabase<RS> pSrvDatabase) {
    this.srvDatabase = pSrvDatabase;
  }

  /**
   * <p>Getter for srvOrm.</p>
   * @return ISrvOrm<RS>
   **/
  public final ISrvOrm<RS> getSrvOrm() {
    return this.srvOrm;
  }

  /**
   * <p>Setter for srvOrm.</p>
   * @param pSrvOrm reference
   **/
  public final void setSrvOrm(final ISrvOrm<RS> pSrvOrm) {
    this.srvOrm = pSrvOrm;
  }

  /**
   * <p>Getter for srvAccSettings.</p>
   * @return ISrvAccSettings
   **/
  public final ISrvAccSettings getSrvAccSettings() {
    return this.srvAccSettings;
  }

  /**
   * <p>Setter for srvAccSettings.</p>
   * @param pSrvAccSettings reference
   **/
  public final void setSrvAccSettings(final ISrvAccSettings pSrvAccSettings) {
    this.srvAccSettings = pSrvAccSettings;
  }

  /**
   * <p>Geter for srvWarehouseEntry.</p>
   * @return ISrvWarehouseEntry
   **/
  public final ISrvWarehouseEntry getSrvWarehouseEntry() {
    return this.srvWarehouseEntry;
  }

  /**
   * <p>Setter for srvWarehouseEntry.</p>
   * @param pSrvWarehouseEntry reference
   **/
  public final void setSrvWarehouseEntry(
    final ISrvWarehouseEntry pSrvWarehouseEntry) {
    this.srvWarehouseEntry = pSrvWarehouseEntry;
  }

  /**
   * <p>Getter for srvUseMaterialEntry.</p>
   * @return ISrvDrawItemEntry<UseMaterialEntry>
   **/
  public final ISrvDrawItemEntry<UseMaterialEntry> getSrvUseMaterialEntry() {
    return this.srvUseMaterialEntry;
  }

  /**
   * <p>Setter for srvUseMaterialEntry.</p>
   * @param pSrvUseMaterialEntry reference
   **/
  public final void setSrvUseMaterialEntry(
    final ISrvDrawItemEntry<UseMaterialEntry> pSrvUseMaterialEntry) {
    this.srvUseMaterialEntry = pSrvUseMaterialEntry;
  }
}

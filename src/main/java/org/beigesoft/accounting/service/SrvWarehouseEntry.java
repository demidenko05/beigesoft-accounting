package org.beigesoft.accounting.service;

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
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.text.DateFormat;
import java.math.BigDecimal;

import org.beigesoft.model.ColumnsValues;
import org.beigesoft.exception.ExceptionWithCode;
import org.beigesoft.accounting.persistable.IDocWarehouse;
import org.beigesoft.accounting.persistable.IMakingWarehouseEntry;
import org.beigesoft.accounting.persistable.PurchaseInvoice;
import org.beigesoft.accounting.persistable.WarehouseSite;
import org.beigesoft.accounting.persistable.WarehouseRest;
import org.beigesoft.accounting.persistable.WarehouseEntry;
import org.beigesoft.service.ISrvI18n;
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.service.ISrvDatabase;

/**
 * <p>Business service for warehouse.</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class SrvWarehouseEntry<RS> implements ISrvWarehouseEntry {

  /**
   * <p>Business service for code - java type document map.</p>
   **/
  private ISrvTypeCode srvTypeCodeDocuments;

  /**
   * <p>I18N service.</p>
   **/
  private ISrvI18n srvI18n;

  /**
   * <p>ORM service.</p>
   **/
  private ISrvOrm<RS> srvOrm;

  /**
   * <p>DB service.</p>
   **/
  private ISrvDatabase<RS> srvDb;

  /**
   * <p>Fastest locking (only record locking), i.e.:
   * update WAREHOUSEREST set THEREST=THEREST-1.5
   * where UNITOFMEASURE=5 and INVITEM=1 and WAREHOUSESITE=1.
   * It will fail when THEREST become less than 0.
   * Update is atomic RDBMS operation, i.e. it
   * locks affected record during invocation.</p>
   **/
  private boolean fastLoc = true;

  /**
   * <p>minimum constructor.</p>
   **/
  public SrvWarehouseEntry() {
  }

  /**
   * <p>Useful constructor.</p>
   * @param pSrvOrm ORM service
   * @param pSrvTypeCodeDocuments Type Code Documents service
   * @param pSrvI18n I18N service
   **/
  public SrvWarehouseEntry(final ISrvOrm<RS> pSrvOrm,
    final ISrvTypeCode pSrvTypeCodeDocuments,
      final ISrvI18n pSrvI18n) {
    this.srvOrm = pSrvOrm;
    this.srvTypeCodeDocuments = pSrvTypeCodeDocuments;
    this.srvI18n = pSrvI18n;
  }

  /**
   * <p>Load warehouse from outside with item or reverse a load.</p>
   * @param pAddParam additional param
   * @param pEntity movement
   * @param pWhSiteTo Site To
   * @throws Exception - an exception
   **/
  @Override
  public final void load(final Map<String, Object> pAddParam,
    final IMakingWarehouseEntry pEntity,
      final WarehouseSite pWhSiteTo) throws Exception {
    if (!pEntity.getIdDatabaseBirth()
      .equals(getSrvOrm().getIdDatabase())) {
      throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
        "can_not_make_ws_entry_for_foreign_src");
    }
    WarehouseEntry wms = null;
    if (pEntity.getReversedId() != null) {
      String tblNm = WarehouseEntry.class.getSimpleName().toUpperCase();
      wms = getSrvOrm().retrieveEntityWithConditions(pAddParam,
        WarehouseEntry.class, " where SOURCETYPE=" + pEntity.constTypeCode()
          + " and SOURCEID=" + pEntity.getReversedId()
            + " and " + tblNm + ".IDDATABASEBIRTH=" + getSrvOrm()
              .getIdDatabase() + " and WAREHOUSESITETO=" + pWhSiteTo.getItsId()
                + " and INVITEM=" + pEntity.getInvItem()
        .getItsId());
      if (wms == null) {
        throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
          "cant_find_reverced_source");
      }
    }
    String langDef = (String) pAddParam.get("langDef");
    DateFormat dateFormat = DateFormat.getDateTimeInstance(
      DateFormat.MEDIUM, DateFormat.SHORT, new Locale(langDef));
    WarehouseEntry wm = new WarehouseEntry();
    wm.setIdDatabaseBirth(getSrvOrm().getIdDatabase());
    wm.setSourceId(pEntity.getItsId());
    wm.setSourceType(pEntity.constTypeCode());
    wm.setWarehouseSiteTo(pWhSiteTo);
    wm.setInvItem(pEntity.getInvItem());
    wm.setUnitOfMeasure(pEntity.getUnitOfMeasure());
    wm.setItsQuantity(pEntity.getItsQuantity());
    wm.setSourceOwnerId(pEntity.getOwnerId());
    wm.setSourceOwnerType(pEntity.getOwnerType());
    if (wms != null) {
      wm.setReversedId(wms.getItsId());
      wm.setDescription(makeDescription(pEntity, langDef, dateFormat) + " "
        + getSrvI18n().getMsg("reversed_entry_n", langDef) + getSrvOrm()
          .getIdDatabase() + "-" + wms.getItsId()); //only local
    } else {
      wm.setDescription(makeDescription(pEntity, langDef, dateFormat));
    }
    getSrvOrm().insertEntity(pAddParam, wm);
    wm.setIsNew(false);
    makeWarehouseRest(pAddParam, pEntity, pWhSiteTo, pEntity.getItsQuantity());
    if (wms != null) {
      wms.setReversedId(wm.getItsId());
      wms.setDescription(wms.getDescription() + " " + getSrvI18n()
        .getMsg("reversing_entry_n", langDef) + getSrvOrm().getIdDatabase()
          + "-" + wm.getItsId());
      getSrvOrm().updateEntity(pAddParam, wms);
    }
  }

  /**
   * <p>Move item between warehouses/sites or reverse a move.</p>
   * @param pAddParam additional param
   * @param pEntity movement
   * @param pWhSiteFrom Site From
   * @param pWhSiteTo Site To
   * @throws Exception - an exception
   **/
  @Override
  public final void move(final Map<String, Object> pAddParam,
    final IMakingWarehouseEntry pEntity, final WarehouseSite pWhSiteFrom,
      final WarehouseSite pWhSiteTo) throws Exception {
    WarehouseEntry wms = null;
    if (!pEntity.getIdDatabaseBirth()
      .equals(getSrvOrm().getIdDatabase())) {
      throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
        "can_not_make_ws_entry_for_foreign_src");
    }
    if (pEntity.getReversedId() != null) {
      String tblNm = WarehouseEntry.class.getSimpleName().toUpperCase();
      wms = getSrvOrm().retrieveEntityWithConditions(pAddParam,
        WarehouseEntry.class, " where SOURCETYPE=" + pEntity.constTypeCode()
          + " and " + tblNm + ".IDDATABASEBIRTH=" + getSrvOrm().getIdDatabase()
            + " and SOURCEID=" + pEntity.getReversedId());
      if (wms == null) {
        throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
          "cant_find_reverced_source");
      }
    }
    String langDef = (String) pAddParam.get("langDef");
    DateFormat dateFormat = DateFormat.getDateTimeInstance(
      DateFormat.MEDIUM, DateFormat.SHORT, new Locale(langDef));
    WarehouseEntry wm = new WarehouseEntry();
    wm.setIdDatabaseBirth(getSrvOrm().getIdDatabase());
    wm.setSourceId(pEntity.getItsId());
    wm.setSourceType(pEntity.constTypeCode());
    wm.setWarehouseSiteFrom(pWhSiteFrom);
    wm.setWarehouseSiteTo(pWhSiteTo);
    wm.setInvItem(pEntity.getInvItem());
    wm.setUnitOfMeasure(pEntity.getUnitOfMeasure());
    wm.setItsQuantity(pEntity.getItsQuantity());
    wm.setSourceOwnerId(pEntity.getOwnerId());
    wm.setSourceOwnerType(pEntity.getOwnerType());
    if (wms != null) {
      wm.setReversedId(wms.getItsId());
      wm.setDescription(makeDescription(pEntity, langDef, dateFormat) + " "
        + getSrvI18n().getMsg("reversed_entry_n", langDef) + getSrvOrm()
          .getIdDatabase() + "-" + wms.getItsId()); //only local
    } else {
      wm.setDescription(makeDescription(pEntity, langDef, dateFormat));
    }
    getSrvOrm().insertEntity(pAddParam, wm);
    wm.setIsNew(false);
    makeWarehouseRest(pAddParam, pEntity, pWhSiteFrom,
      pEntity.getItsQuantity().negate());
    makeWarehouseRest(pAddParam, pEntity, pWhSiteTo, pEntity.getItsQuantity());
    if (wms != null) {
      wms.setReversedId(wm.getItsId());
      wms.setDescription(wms.getDescription() + " " + getSrvI18n()
        .getMsg("reversing_entry_n", langDef) + getSrvOrm().getIdDatabase()
          + "-" + wm.getItsId());
      getSrvOrm().updateEntity(pAddParam, wms);
    }
  }

  /**
   * <p>Make warehouse rest (load/draw/reverse).</p>
   * @param pAddParam additional param
   * @param pEntity movement
   * @param pWhSite Site
   * @param pQuantity Quantity positive or negative
   * @throws Exception - an exception
   **/
  @Override
  public final void makeWarehouseRest(final Map<String, Object> pAddParam,
    final IMakingWarehouseEntry pEntity,
      final WarehouseSite pWhSite,
        final BigDecimal pQuantity) throws Exception {
    if (!pEntity.getIdDatabaseBirth()
      .equals(getSrvOrm().getIdDatabase())) {
      throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
        "can_not_make_ws_entry_for_foreign_src");
    }
    WarehouseRest wr = getSrvOrm().retrieveEntityWithConditions(pAddParam,
      WarehouseRest.class, "where WAREHOUSESITE="
        + pWhSite.getItsId() + " and INVITEM="
          + pEntity.getInvItem().getItsId() + " and UNITOFMEASURE="
            + pEntity.getUnitOfMeasure().getItsId());
    if (wr == null) {
      if (pQuantity.doubleValue() < 0) {
        throw new ExceptionWithCode(ExceptionWithCode.FORBIDDEN,
          "attempt_reverse_non_existent_good_in_warehouse");
      }
      wr = new WarehouseRest();
      wr.setIsNew(true);
      wr.setWarehouseSite(pWhSite);
      wr.setUnitOfMeasure(pEntity.getUnitOfMeasure());
      wr.setInvItem(pEntity.getInvItem());
    }
    wr.setTheRest(wr.getTheRest().add(pQuantity));
    if (wr.getTheRest().doubleValue() < 0) {
      throw new ExceptionWithCode(PurchaseInvoice.THERE_IS_NO_GOODS,
        "there_is_no_goods_in_stock");
    }
    if (wr.getIsNew()) {
      getSrvOrm().insertEntity(pAddParam, wr);
      wr.setIsNew(false);
    } else {
      if (this.fastLoc) {
        ColumnsValues cvs = new ColumnsValues();
        cvs.getFormula().add("itsVersion");
        cvs.getFormula().add("theRest");
        cvs.setIdColumnsNames(new String[] {"warehouseSite", "invItem",
            "unitOfMeasure"});
        cvs.put("itsVersion", "ITSVERSION+1");
        if (pQuantity.compareTo(BigDecimal.ZERO) == -1) {
          cvs.put("theRest", "THEREST" + pQuantity);
        } else {
          cvs.put("theRest", "THEREST+" + pQuantity);
        }
        this.srvDb.executeUpdate("WAREHOUSEREST", cvs, "WAREHOUSESITE="
      + wr.getWarehouseSite().getItsId() + " and INVITEM=" + wr.getInvItem()
    .getItsId() + " and UNITOFMEASURE=" + wr.getUnitOfMeasure().getItsId());
      } else {
        getSrvOrm().updateEntity(pAddParam, wr);
      }
    }
  }

  /**
   * <p>Withdrawal warehouse item to outside (or use/loss).</p>
   * @param pAddParam additional param
   * @param pEntity movement
   * @param pWhSiteFrom Site From, if null - automatically find sites
   * @throws Exception - an exception
   **/
  @Override
  public final void withdrawal(final Map<String, Object> pAddParam,
    final IMakingWarehouseEntry pEntity,
      final WarehouseSite pWhSiteFrom) throws Exception {
    if (!pEntity.getIdDatabaseBirth()
      .equals(getSrvOrm().getIdDatabase())) {
      throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
        "can_not_make_ws_entry_for_foreign_src");
    }
    String langDef = (String) pAddParam.get("langDef");
    DateFormat dateFormat = DateFormat.getDateTimeInstance(
      DateFormat.MEDIUM, DateFormat.SHORT, new Locale(langDef));
    ColumnsValues cvs = null;
    if (this.fastLoc) {
      cvs = new ColumnsValues();
      cvs.getFormula().add("itsVersion");
      cvs.getFormula().add("theRest");
      cvs.setIdColumnsNames(new String[] {"warehouseSite", "invItem",
          "unitOfMeasure"});
      cvs.put("itsVersion", "ITSVERSION+1");
    }
    if (pWhSiteFrom != null) {
      WarehouseRest wr = getSrvOrm().retrieveEntityWithConditions(pAddParam,
        WarehouseRest.class, "where THEREST>0 and INVITEM="
          + pEntity.getInvItem().getItsId() + " and UNITOFMEASURE="
            + pEntity.getUnitOfMeasure().getItsId() + " and WAREHOUSESITE="
              + pWhSiteFrom.getItsId());
      if (wr == null
        || wr.getTheRest().compareTo(pEntity.getItsQuantity()) < 0) {
        throw new ExceptionWithCode(PurchaseInvoice.THERE_IS_NO_GOODS,
          "There is no goods in stock, Item ID/UOM ID/ WS ID/ quantity"
            + pEntity.getInvItem().getItsId() + "/" + pEntity.getUnitOfMeasure()
              .getItsId() + "/" + pEntity.getItsQuantity());
      }
      if (this.fastLoc) {
        cvs.put("theRest", "THEREST-" + pEntity.getItsQuantity());
        this.srvDb.executeUpdate("WAREHOUSEREST", cvs, "WAREHOUSESITE="
      + wr.getWarehouseSite().getItsId() + " and INVITEM=" + wr.getInvItem()
    .getItsId() + " and UNITOFMEASURE=" + wr.getUnitOfMeasure().getItsId());
      } else {
        wr.setTheRest(wr.getTheRest().subtract(pEntity.getItsQuantity()));
        getSrvOrm().updateEntity(pAddParam, wr);
      }
      WarehouseEntry wm = new WarehouseEntry();
      wm.setIdDatabaseBirth(getSrvOrm().getIdDatabase());
      wm.setSourceId(pEntity.getItsId());
      wm.setSourceType(pEntity.constTypeCode());
      wm.setWarehouseSiteFrom(wr.getWarehouseSite());
      wm.setUnitOfMeasure(wr.getUnitOfMeasure());
      wm.setInvItem(wr.getInvItem());
      wm.setItsQuantity(pEntity.getItsQuantity());
      wm.setSourceOwnerId(pEntity.getOwnerId());
      wm.setSourceOwnerType(pEntity.getOwnerType());
      wm.setDescription(makeDescription(pEntity, langDef, dateFormat));
      getSrvOrm().insertEntity(pAddParam, wm);
      wm.setIsNew(false);
    } else {
      List<WarehouseRest> wrl = getSrvOrm().retrieveListWithConditions(
        pAddParam, WarehouseRest.class, "where THEREST>0 and INVITEM="
          + pEntity.getInvItem().getItsId() + " and UNITOFMEASURE="
            + pEntity.getUnitOfMeasure().getItsId());
      BigDecimal theRest = BigDecimal.ZERO;
      for (WarehouseRest wr : wrl) {
        theRest = theRest.add(wr.getTheRest());
        if (theRest.compareTo(pEntity.getItsQuantity()) >= 0) {
          break;
        }
      }
      if (theRest.compareTo(pEntity.getItsQuantity()) < 0) {
        throw new ExceptionWithCode(PurchaseInvoice.THERE_IS_NO_GOODS,
          "there_is_no_goods_in_stock");
      }
      BigDecimal quantityToLeaveRest = pEntity.getItsQuantity();
      for (WarehouseRest wr : wrl) {
        if (quantityToLeaveRest.doubleValue() == 0) {
          break;
        }
        BigDecimal quantityToLeave;
        if (wr.getTheRest().compareTo(quantityToLeaveRest) <= 0) {
          quantityToLeave = wr.getTheRest();
        } else {
          quantityToLeave = quantityToLeaveRest;
        }
        if (this.fastLoc) {
          cvs.put("theRest", "THEREST-" + quantityToLeave);
          this.srvDb.executeUpdate("WAREHOUSEREST", cvs, "WAREHOUSESITE="
        + wr.getWarehouseSite().getItsId() + " and INVITEM=" + wr.getInvItem()
      .getItsId() + " and UNITOFMEASURE=" + wr.getUnitOfMeasure().getItsId());
        } else {
          wr.setTheRest(wr.getTheRest().subtract(quantityToLeave));
          getSrvOrm().updateEntity(pAddParam, wr);
        }
        WarehouseEntry wm = new WarehouseEntry();
        wm.setIdDatabaseBirth(getSrvOrm().getIdDatabase());
        wm.setSourceId(pEntity.getItsId());
        wm.setSourceType(pEntity.constTypeCode());
        wm.setWarehouseSiteFrom(wr.getWarehouseSite());
        wm.setUnitOfMeasure(wr.getUnitOfMeasure());
        wm.setInvItem(wr.getInvItem());
        wm.setItsQuantity(quantityToLeave);
        wm.setSourceOwnerId(pEntity.getOwnerId());
        wm.setSourceOwnerType(pEntity.getOwnerType());
        wm.setDescription(makeDescription(pEntity, langDef, dateFormat));
        getSrvOrm().insertEntity(pAddParam, wm);
        wm.setIsNew(false);
        quantityToLeaveRest = quantityToLeaveRest.subtract(quantityToLeave);
      }
    }
  }

  /**
   * <p>Reverse a withdrawal warehouse.</p>
   * @param pAddParam additional param
   * @param pEntity movement
   * @throws Exception - an exception
   **/
  @Override
  public final void reverseDraw(final Map<String, Object> pAddParam,
    final IMakingWarehouseEntry pEntity) throws Exception {
    if (!pEntity.getIdDatabaseBirth()
      .equals(getSrvOrm().getIdDatabase())) {
      throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
        "can_not_make_ws_entry_for_foreign_src");
    }
    if (pEntity.getItsQuantity().doubleValue() > 0) {
      throw new ExceptionWithCode(ExceptionWithCode.SOMETHING_WRONG,
        "reversing_source_must_has_negative_quantity");
    }
    String tblNm = WarehouseEntry.class.getSimpleName().toUpperCase();
    List<WarehouseEntry> wml = getSrvOrm().retrieveListWithConditions(
      pAddParam, WarehouseEntry.class, "where SOURCETYPE=" + pEntity
        .constTypeCode() + " and " + tblNm + ".IDDATABASEBIRTH=" + getSrvOrm()
          .getIdDatabase() + " and SOURCEID=" + pEntity.getReversedId()
            + " and INVITEM=" + pEntity.getInvItem().getItsId()
              +  " and WAREHOUSESITEFROM is not null");
    BigDecimal quantityToLeaveRst = pEntity.getItsQuantity();
    String langDef = (String) pAddParam.get("langDef");
    DateFormat dateFormat = DateFormat.getDateTimeInstance(
      DateFormat.MEDIUM, DateFormat.SHORT, new Locale(langDef));
    for (WarehouseEntry wms : wml) {
      if (wms.getItsQuantity().doubleValue() < 0) {
        throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
          "attempt_to_reverse_reversed");
      }
      WarehouseEntry wm = new WarehouseEntry();
      wm.setIdDatabaseBirth(getSrvOrm().getIdDatabase());
      wm.setSourceId(pEntity.getItsId());
      wm.setSourceType(pEntity.constTypeCode());
      wm.setWarehouseSiteFrom(wms.getWarehouseSiteFrom());
      wm.setUnitOfMeasure(wms.getUnitOfMeasure());
      wm.setInvItem(wms.getInvItem());
      wm.setItsQuantity(wms.getItsQuantity().negate());
      quantityToLeaveRst = quantityToLeaveRst.add(wms.getItsQuantity());
      if (quantityToLeaveRst.doubleValue() > 0) {
        throw new ExceptionWithCode(ExceptionWithCode.SOMETHING_WRONG,
          "reversing_source_has_different_quantity_against_movement_entries");
      }
      wm.setSourceOwnerId(pEntity.getOwnerId());
      wm.setSourceOwnerType(pEntity.getOwnerType());
      wm.setReversedId(wms.getItsId());
      wm.setDescription(makeDescription(pEntity, langDef, dateFormat) + " "
        + getSrvI18n().getMsg("reversed_entry_n", langDef) + getSrvOrm()
          .getIdDatabase() + "-" + wms.getItsId());
      getSrvOrm().insertEntity(pAddParam, wm);
      wm.setIsNew(false);
      makeWarehouseRest(pAddParam, pEntity, wm.getWarehouseSiteFrom(),
        wms.getItsQuantity());
      wms.setReversedId(wm.getItsId());
      wms.setDescription(wms.getDescription() + " " + getSrvI18n()
        .getMsg("reversing_entry_n", langDef) + getSrvOrm().getIdDatabase()
          + "-" + wm.getItsId());
      getSrvOrm().updateEntity(pAddParam, wms);
    }
    if (quantityToLeaveRst.doubleValue() != 0) {
      throw new ExceptionWithCode(ExceptionWithCode.SOMETHING_WRONG,
        "reversing_source_has_different_quantity_against_movement_entries");
    }
  }

  /**
   * <p>Retrieve entries for document.</p>
   * @param pAddParam additional param
   * @param pEntity a document
   * @return warehouse entries made by this document
   * @throws Exception - an exception
   **/
  @Override
  public final List<WarehouseEntry> retrieveEntriesFor(
    final Map<String, Object> pAddParam,
      final IDocWarehouse pEntity) throws Exception {
    String tblNm = WarehouseEntry.class.getSimpleName().toUpperCase();
    List<WarehouseEntry> result = null;
    if (pEntity instanceof IMakingWarehouseEntry) {
      //e.g. Manufacture
      String where = " where SOURCETYPE=" + pEntity.constTypeCode()
        + " and " + tblNm + ".IDDATABASEBIRTH=" + getSrvOrm().getIdDatabase()
          + " and SOURCEID=" + pEntity.getItsId();
      result = getSrvOrm().retrieveListWithConditions(pAddParam,
        WarehouseEntry.class, where);
    }
    if (pEntity.getLinesWarehouseType() != null) {
      //e.g. PurchaseInvoice
      if (result == null) {
        result = retrieveEntriesForOwner(pAddParam, pEntity.constTypeCode(),
          pEntity.getItsId());
      } else {
        result.addAll(retrieveEntriesForOwner(pAddParam,
          pEntity.constTypeCode(), pEntity.getItsId()));
      }
    }
    return result;
  }

  /**
   * <p>Retrieve entries for lines owner id/type.</p>
   * @param pAddParam additional param
   * @param pOwnerTypeCode Owner Type code
   * @param pOwnerId Owner ID
   * @return warehouse entries made by this document
   * @throws Exception - an exception
   **/
  @Override
  public final List<WarehouseEntry> retrieveEntriesForOwner(
    final Map<String, Object> pAddParam,
      final Integer pOwnerTypeCode, final Long pOwnerId) throws Exception {
    String tblNm = WarehouseEntry.class.getSimpleName().toUpperCase();
    String where = " where SOURCEOWNERTYPE=" + pOwnerTypeCode
        + " and " + tblNm + ".IDDATABASEBIRTH=" + getSrvOrm().getIdDatabase()
          + " and SOURCEOWNERID=" + pOwnerId;
    return getSrvOrm()
      .retrieveListWithConditions(pAddParam, WarehouseEntry.class, where);
  }

  //Utils:
  /**
   * <p>Make description for warehouse entry.</p>
   * @param pEntity movement
   * @param pLangDef Lang Default
   * @param pDateFormat DateFormat
   * @return description
   **/
  public final String makeDescription(final IMakingWarehouseEntry pEntity,
    final String pLangDef, final DateFormat pDateFormat) {
    String strWho = getSrvI18n().getMsg(pEntity.getClass().getSimpleName()
      + "short", pLangDef) + " #" + getSrvOrm().getIdDatabase() + "-"
        + pEntity.getItsId(); //only local
    if (pEntity.getOwnerId() == null) {
      strWho += ", " + pDateFormat.format(pEntity
        .getDocumentDate());
    } else {
      strWho += " " + getSrvI18n().getMsg("in", pLangDef) + " " + getSrvI18n()
    .getMsg(getSrvTypeCodeDocuments().getTypeCodeMap().get(pEntity
  .getOwnerType()).getSimpleName() + "short", pLangDef) + " #" + getSrvOrm()
    .getIdDatabase() + "-" + pEntity.getOwnerId() + ", "
      + pDateFormat.format(pEntity.getDocumentDate());
    }
    return getSrvI18n().getMsg("made_at", pLangDef) + " " + pDateFormat.format(
      new Date()) + " " + getSrvI18n().getMsg("by", pLangDef) + " " + strWho;
  }

  //Simple getters and setters:
  /**
   * <p>Getter for srvTypeCodeDocuments.</p>
   * @return ISrvTypeCode
   **/
  public final ISrvTypeCode getSrvTypeCodeDocuments() {
    return this.srvTypeCodeDocuments;
  }

  /**
   * <p>Setter for srvTypeCodeDocuments.</p>
   * @param pSrvTypeCodeDocuments reference
   **/
  public final void setSrvTypeCodeDocuments(
    final ISrvTypeCode pSrvTypeCodeDocuments) {
    this.srvTypeCodeDocuments = pSrvTypeCodeDocuments;
  }

  /**
   * <p>Geter for srvOrm.</p>
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
   * <p>Getter for srvI18n.</p>
   * @return ISrvI18n
   **/
  public final ISrvI18n getSrvI18n() {
    return this.srvI18n;
  }

  /**
   * <p>Setter for srvI18n.</p>
   * @param pSrvI18n reference
   **/
  public final void setSrvI18n(final ISrvI18n pSrvI18n) {
    this.srvI18n = pSrvI18n;
  }

  /**
   * <p>Getter for srvDb.</p>
   * @return ISrvDatabase<RS>
   **/
  public final ISrvDatabase<RS> getSrvDb() {
    return this.srvDb;
  }

  /**
   * <p>Setter for srvDb.</p>
   * @param pSrvDb reference
   **/
  public final void setSrvDb(final ISrvDatabase<RS> pSrvDb) {
    this.srvDb = pSrvDb;
  }

  /**
   * <p>Getter for fastLoc.</p>
   * @return boolean
   **/
  public final boolean getFastLoc() {
    return this.fastLoc;
  }

  /**
   * <p>Setter for fastLoc.</p>
   * @param pFastLoc reference
   **/
  public final void setFastLoc(final boolean pFastLoc) {
    this.fastLoc = pFastLoc;
  }
}

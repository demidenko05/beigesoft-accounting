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
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.lang.reflect.Method;

import org.beigesoft.exception.ExceptionWithCode;
import org.beigesoft.factory.IFactoryAppBeansByClass;
import org.beigesoft.factory.IFactorySimple;
import org.beigesoft.holder.IHolderForClassByName;
import org.beigesoft.service.ISrvI18n;
import org.beigesoft.accounting.persistable.base.ADrawItemSourcesLine;
import org.beigesoft.accounting.persistable.base.ADrawItemEntry;
import org.beigesoft.accounting.model.EWarehouseMovementType;
import org.beigesoft.accounting.persistable.IMakingWarehouseEntry;
import org.beigesoft.accounting.persistable.PurchaseInvoice;
import org.beigesoft.accounting.persistable.IDrawItemSource;
import org.beigesoft.accounting.persistable.IDocWarehouse;
import org.beigesoft.orm.service.ASrvOrm;
import org.beigesoft.service.ISrvDatabase;
import org.beigesoft.model.IRecordSet;

/**
 * <p>Business service for draw inventory item from a holder
 * (e.g. purchase invoice line) to sale, manufacture, use, loss.</p>
 *
 * @param <T> draw item entry type e.g. CogsEntry
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public abstract class ASrvDrawItemEntry<T extends ADrawItemEntry, RS>
  implements ISrvDrawItemEntry<T> {

  /**
   * <p>I18N service.</p>
   **/
  private ISrvI18n srvI18n;

  /**
   * <p>ORM service.</p>
   **/
  private ASrvOrm<RS> srvOrm;

  /**
   * <p>Database service.</p>
   **/
  private ISrvDatabase<RS> srvDatabase;

  /**
   * <p>Business service for accounting settings.</p>
   **/
  private ISrvAccSettings srvAccSettings;

  /**
   * <p>Business service for code - java type map of
   * material holders and documents.</p>
   **/
  private ISrvTypeCode srvTypeCode;

  /**
   * <p>Lazy initialized SQL queries map.</p>
   **/
  private final Map<String, String> queries = new HashMap<String, String>();

  /**
   * <p>Fields getters RAPI holder.</p>
   **/
  private IHolderForClassByName<Method> settersRapiHolder;

  /**
   * <p>Entitie's factories factory.</p>
   **/
  private IFactoryAppBeansByClass<IFactorySimple<?>> entitiesFactoriesFatory;

  /**
   * <p>Withdrawal warehouse item for use/sale/loss.</p>
   * @param pAddParam additional param
   * @param pEntity movement
   * @param pDateAccount date of account
   * @param pDrawingOwnerId drawing Owner Id if exists
   * @throws Exception - an exception
   **/
  @Override
  public final void withdrawal(final Map<String, Object> pAddParam,
    final IMakingWarehouseEntry pEntity, final Date pDateAccount,
      final Long pDrawingOwnerId) throws Exception {
    if (!pEntity.getIdDatabaseBirth()
      .equals(getSrvOrm().getIdDatabase())) {
      throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
        "can_not_make_di_entry_for_foreign_src");
    }
    String queryMain = lazyGetQuery(srvAccSettings
      .lazyGetAccSettings(pAddParam).getCogsMethod().getFileName());
    StringBuffer sb = new StringBuffer();
    int i = 0;
    for (ADrawItemSourcesLine drawItemSourceLine
      : getDrawItemSources(pAddParam)) {
      if (drawItemSourceLine.getIsUsed()) {
        String query = lazyGetQuery(drawItemSourceLine.getFileName());
        query = query.replace(":IDDATABASEBIRTH", String.valueOf(getSrvOrm()
          .getIdDatabase()));
        query = query.replace(":INVITEM", pEntity.getInvItem()
          .getItsId().toString());
        query = query.replace(":UNITOFMEASURE", pEntity.getUnitOfMeasure()
          .getItsId().toString());
        if (i++ > 0) {
          sb.append("\nunion all\n\n");
        }
        sb.append(query);
      }
    }
    String sourceAll = sb.toString();
    if (sourceAll.trim().length() == 0) {
      throw new ExceptionWithCode(ExceptionWithCode.CONFIGURATION_MISTAKE,
        "there_is_no_draw_material_rules");
    }
    //ADrawItemEntry just holder source id and type
    List<T> sources =
      new ArrayList<T>();
    queryMain = queryMain.replace(":SOURCEALL",
      sourceAll);
    BigDecimal quantityInSources = BigDecimal.ZERO;
    IRecordSet<RS> recordSet = null;
    try {
      recordSet = getSrvDatabase().retrieveRecords(queryMain);
      if (recordSet.moveToFirst()) {
        do {
          Long sourceId = recordSet.getLong("SOURCEID");
          Integer sourceType = recordSet.getInteger("SOURCETYPE");
          Long sourceOwnerId = recordSet.getLong("SOURCEOWNERID");
          Integer sourceOwnerType = recordSet.getInteger("SOURCEOWNERTYPE");
          BigDecimal theRest = BigDecimal
              .valueOf(recordSet.getDouble("THEREST"));
          T source = createDrawItemEntry(pAddParam);
          source.setSourceId(sourceId);
          source.setSourceType(sourceType);
          source.setSourceOwnerId(sourceOwnerId);
          source.setSourceOwnerType(sourceOwnerType);
          sources.add(source);
          quantityInSources = quantityInSources.add(theRest);
          if (quantityInSources.compareTo(pEntity.getItsQuantity()) >= 0) {
            break;
          }
        } while (recordSet.moveToNext());
      }
    } finally {
      if (recordSet != null) {
        recordSet.close();
      }
    }
    if (quantityInSources.compareTo(pEntity.getItsQuantity()) < 0) {
      throw new ExceptionWithCode(PurchaseInvoice.THERE_IS_NO_GOODS,
        "there_is_no_goods_in_stock");
    }
    BigDecimal quantityToDrawRest = pEntity.getItsQuantity();
    for (T source : sources) {
      @SuppressWarnings("unchecked")
      IFactorySimple<IDrawItemSource> fctDis =
        (IFactorySimple<IDrawItemSource>) this.entitiesFactoriesFatory
         .lazyGet(pAddParam, srvTypeCode.getTypeCodeMap()
          .get(source.getSourceType()));
      IDrawItemSource drawed = fctDis.create(pAddParam);
      String fldIdName = this.srvOrm.getTablesMap()
        .get(drawed.getClass().getSimpleName()).getIdFieldName();
      Method setterId = this.settersRapiHolder
        .getFor(drawed.getClass(), fldIdName);
      setterId.invoke(drawed, source.getSourceId());
      drawed = srvOrm.retrieveEntity(pAddParam, drawed);
      BigDecimal quantityToDraw;
      if (quantityToDrawRest.compareTo(drawed.getTheRest()) < 0) {
        quantityToDraw = quantityToDrawRest;
      } else {
        quantityToDraw = drawed.getTheRest();
      }
      withdrawalFrom(pAddParam, pEntity, drawed, quantityToDraw);
      quantityToDrawRest = quantityToDrawRest.subtract(quantityToDraw);
      if (quantityToDrawRest.doubleValue() == 0) {
        break;
      }
    }
  }

  /**
   * <p>Withdrawal warehouse item for use/sale/loss from given source.</p>
   * @param pAddParam additional param
   * @param pEntity drawing entity
   * @param pSource drawn entity
   * @param pQuantityToDraw quantity to draw
   * @throws Exception - an exception
   **/
  @Override
  public final void withdrawalFrom(final Map<String, Object> pAddParam,
    final IMakingWarehouseEntry pEntity, final IDrawItemSource pSource,
        final BigDecimal pQuantityToDraw) throws Exception {
    if (!pEntity.getIdDatabaseBirth()
      .equals(getSrvOrm().getIdDatabase())) {
      throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
        "can_not_make_di_entry_for_foreign_src");
    }
    T die = createDrawItemEntry(pAddParam);
    die.setItsDate(pEntity.getDocumentDate());
    die.setIdDatabaseBirth(getSrvOrm().getIdDatabase());
    die.setSourceType(pSource.constTypeCode());
    die.setSourceId(pSource.getItsId());
    die.setDrawingType(pEntity.constTypeCode());
    die.setDrawingId(pEntity.getItsId());
    die.setDrawingOwnerId(pEntity.getOwnerId());
    die.setDrawingOwnerType(pEntity.getOwnerType());
    die.setSourceOwnerId(pSource.getOwnerId());
    die.setSourceOwnerType(pSource.getOwnerType());
    die.setItsQuantity(pQuantityToDraw);
    die.setItsCost(pSource.getItsCost());
    die.setInvItem(pEntity.getInvItem());
    die.setUnitOfMeasure(pEntity.getUnitOfMeasure());
    die.setItsTotal(pSource.getItsCost().
      multiply(die.getItsQuantity()).setScale(getSrvAccSettings()
        .lazyGetAccSettings(pAddParam).getCostPrecision(), getSrvAccSettings()
          .lazyGetAccSettings(pAddParam).getRoundingMode()));
    die.setDescription(makeDescription(pAddParam, pEntity, die));
    this.srvOrm.insertEntity(pAddParam, die);
    pSource.setTheRest(pSource.getTheRest().subtract(pQuantityToDraw));
    this.srvOrm.updateEntity(pAddParam, pSource);
  }

  /**
   * <p>Reverse a withdrawal item.</p>
   * @param pAddParam additional param
   * @param pEntity movement
   * @param pDateAccount date of account
   * @param pDrawingOwnerId drawing Owner Id if exists
   * @throws Exception - an exception
   **/
  @Override
  public final void reverseDraw(final Map<String, Object> pAddParam,
    final IMakingWarehouseEntry pEntity, final Date pDateAccount,
      final Long pDrawingOwnerId) throws Exception {
    if (!pEntity.getIdDatabaseBirth()
      .equals(getSrvOrm().getIdDatabase())) {
      throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
        "can_not_make_di_entry_for_foreign_src");
    }
    String tblNm = getDrawItemEntryClass().getSimpleName().toUpperCase();
    List<T> diel = getSrvOrm().retrieveListWithConditions(pAddParam,
      getDrawItemEntryClass(), " where DRAWINGTYPE=" + pEntity.constTypeCode()
        + " and " + tblNm + ".IDDATABASEBIRTH=" + getSrvOrm().getIdDatabase()
          + " and DRAWINGID=" + pEntity.getReversedId());
    BigDecimal quantityToLeaveRst = pEntity.getItsQuantity();
    String langDef = (String) pAddParam.get("langDef");
    for (T dies : diel) {
      if (dies.getItsQuantity().doubleValue() < 0) {
        throw new ExceptionWithCode(ExceptionWithCode.FORBIDDEN,
          "Attempt to reverse reversed " + pAddParam.get("user"));
      }
      T die = createDrawItemEntry(pAddParam);
      die.setItsDate(pDateAccount);
      die.setIdDatabaseBirth(getSrvOrm().getIdDatabase());
      die.setSourceType(dies.getSourceType());
      die.setSourceId(dies.getSourceId());
      die.setDrawingType(pEntity.constTypeCode());
      die.setDrawingId(pEntity.getItsId());
      die.setDrawingOwnerId(pDrawingOwnerId);
      die.setDrawingOwnerType(pEntity.getOwnerType());
      die.setSourceOwnerId(dies.getSourceOwnerId());
      die.setSourceOwnerType(dies.getSourceOwnerType());
      die.setItsCost(dies.getItsCost());
      die.setItsTotal(dies.getItsTotal().negate());
      die.setUnitOfMeasure(dies.getUnitOfMeasure());
      die.setInvItem(dies.getInvItem());
      die.setItsQuantity(dies.getItsQuantity().negate());
      quantityToLeaveRst = quantityToLeaveRst.add(dies.getItsQuantity());
      if (quantityToLeaveRst.doubleValue() > 0) {
        throw new ExceptionWithCode(ExceptionWithCode.SOMETHING_WRONG,
          "Reversing source has different quantity against movement entries! "
            + pAddParam.get("user"));
      }
      die.setReversedId(dies.getItsId());
      die.setDescription(makeDescription(pAddParam, pEntity, dies) + " "
        + getSrvI18n().getMsg("reversed_entry_n", langDef)
          + getSrvOrm().getIdDatabase() + "-" + dies.getItsId());
      getSrvOrm().insertEntity(pAddParam, die);
      @SuppressWarnings("unchecked")
      IFactorySimple<IDrawItemSource> fctDis =
        (IFactorySimple<IDrawItemSource>) this.entitiesFactoriesFatory
         .lazyGet(pAddParam, srvTypeCode.getTypeCodeMap()
          .get(dies.getSourceType()));
      IDrawItemSource drawed = fctDis.create(pAddParam);
      String fldIdName = this.srvOrm.getTablesMap()
        .get(drawed.getClass().getSimpleName()).getIdFieldName();
      Method setterId = this.settersRapiHolder
        .getFor(drawed.getClass(), fldIdName);
      setterId.invoke(drawed, dies.getSourceId());
      drawed = srvOrm.retrieveEntity(pAddParam, drawed);
      drawed.setTheRest(drawed.getTheRest().add(dies.getItsQuantity()));
      srvOrm.updateEntity(pAddParam, drawed);
      dies.setReversedId(die.getItsId());
      dies.setDescription(dies.getDescription() + " "
        + getSrvI18n().getMsg("reversing_entry_n", langDef)
          + getSrvOrm().getIdDatabase() + "-" + die.getItsId()); //only local
      getSrvOrm().updateEntity(pAddParam, dies);
    }
    if (quantityToLeaveRst.doubleValue() != 0) {
      throw new ExceptionWithCode(ExceptionWithCode.SOMETHING_WRONG,
        "Reversing source has different quantity against movement entries! "
          + pAddParam.get("user"));
    }
  }

  /**
   * <p>Retrieve entries for whole document to print.</p>
   * @param pAddParam additional param
   * @param pEntity a document
   * @return warehouse entries made by this document
   * @throws Exception - an exception
   **/
  @Override
  public final List<T> retrieveEntriesFor(
    final Map<String, Object> pAddParam,
      final IDocWarehouse pEntity) throws Exception {
    String where = null;
    Long docId = pEntity.getItsId();
    if (pEntity.getIdBirth() != null) {
      docId = pEntity.getIdBirth();
    }
    String tblNm = getDrawItemEntryClass().getSimpleName().toUpperCase();
    if (pEntity instanceof IDrawItemSource) {
      where = " where SOURCETYPE=" + pEntity.constTypeCode()
        + " and SOURCEID="  + docId + " and " + tblNm + ".IDDATABASEBIRTH="
          + pEntity.getIdDatabaseBirth();
    } else if (pEntity instanceof IMakingWarehouseEntry) {
      where = " where DRAWINGTYPE=" + pEntity.constTypeCode()
        + " and DRAWINGID=" + docId + " and " + tblNm + ".IDDATABASEBIRTH="
          + pEntity.getIdDatabaseBirth();
    }
    List<T> result = null;
    if (where != null) {
      result = getSrvOrm().retrieveListWithConditions(pAddParam,
        getDrawItemEntryClass(), where);
      where = null;
    }
    if (pEntity instanceof IDrawItemSource) { //also may draw, e.g. Manufacture
      where = " where DRAWINGTYPE=" + pEntity.constTypeCode()
        + " and DRAWINGID="  + docId + " and " + tblNm + ".IDDATABASEBIRTH="
          + pEntity.getIdDatabaseBirth();
    }
    if (where != null) {
      if (result == null) {
        result = getSrvOrm().retrieveListWithConditions(pAddParam,
          getDrawItemEntryClass(), where);
      } else {
        result.addAll(getSrvOrm().retrieveListWithConditions(pAddParam,
          getDrawItemEntryClass(), where));
      }
      where = null;
    }
    if (pEntity.getLinesWarehouseType() == EWarehouseMovementType.LOAD) {
      //e.g. PurchaseInvoice
      where = " where SOURCEOWNERTYPE=" + pEntity.constTypeCode()
        + " and SOURCEOWNERID=" + docId + " and " + tblNm + ".IDDATABASEBIRTH="
          + pEntity.getIdDatabaseBirth();
    } else if (pEntity.getLinesWarehouseType()
      == EWarehouseMovementType.WITHDRAWAL) {
      //e.g. SalesInvoice
      where = " where DRAWINGOWNERTYPE=" + pEntity.constTypeCode()
        + " and DRAWINGOWNERID=" + docId + " and " + tblNm + ".IDDATABASEBIRTH="
          + pEntity.getIdDatabaseBirth();
    }
    if (where != null) {
      if (result == null) {
        result = getSrvOrm().retrieveListWithConditions(pAddParam,
          getDrawItemEntryClass(), where);
      } else {
        result.addAll(getSrvOrm().retrieveListWithConditions(pAddParam,
          getDrawItemEntryClass(), where));
      }
    }
    return result;
  }

  //To override:
  /**
   * <p>Get draw item entry class.</p>
   * @return draw item entry class
   **/
  public abstract Class<T> getDrawItemEntryClass();

  /**
   * <p>Create draw item entry.</p>
   * @param pAddParam additional param
   * @return draw item entry
   **/
  public abstract T createDrawItemEntry(Map<String, Object> pAddParam);

  /**
   * <p>Get draw item sources.</p>
   * @param pAddParam additional param
   * @return draw item sources
   * @throws Exception - an exception
   **/
  public abstract List<? extends ADrawItemSourcesLine>
    getDrawItemSources(Map<String, Object> pAddParam) throws Exception;

  //Utils:
  /**
   * <p>Query loader.</p>
   * @param pFileName File Name
   * @return String
   * @throws Exception - an exception
   **/
  public final String lazyGetQuery(final String pFileName) throws Exception {
    if (this.queries.get(pFileName) == null) {
      String flName = "/" + "accounting" + "/" + "trade"
          + "/" + pFileName + ".sql";
      this.queries.put(pFileName, loadString(flName));
    }
    return this.queries.get(pFileName);
  }

  /**
   * <p>Load string file (usually SQL query).</p>
   * @param pFileName file name
   * @return String usually SQL query
   * @throws IOException - IO exception
   **/
  public final String loadString(final String pFileName)
        throws IOException {
    URL urlFile = ASrvDrawItemEntry.class
      .getResource(pFileName);
    if (urlFile != null) {
      InputStream inputStream = null;
      try {
        inputStream = ASrvDrawItemEntry.class.getResourceAsStream(pFileName);
        byte[] bArray = new byte[inputStream.available()];
        inputStream.read(bArray, 0, inputStream.available());
        return new String(bArray, "UTF-8");
      } finally {
        if (inputStream != null) {
          inputStream.close();
        }
      }
    }
    return null;
  }

  /**
   * <p>Make description for warehouse entry.</p>
   * @param pAddParam additional param
   * @param pEntity movement
   * @param pSource source of item
   * @return description
   **/
  public final String makeDescription(final Map<String, Object> pAddParam,
    final IMakingWarehouseEntry pEntity,
      final ADrawItemEntry pSource) {
    String langDef = (String) pAddParam.get("langDef");
    DateFormat dateFormat = DateFormat.getDateTimeInstance(
      DateFormat.MEDIUM, DateFormat.SHORT, new Locale(langDef));
    String strWho = getSrvI18n().getMsg(pEntity.getClass().getSimpleName()
      + "short", langDef) + " #" + getSrvOrm().getIdDatabase() + "-"//only local
        + pEntity.getItsId();
    if (pEntity.getOwnerId() == null) {
      strWho += ", " + dateFormat.format(pEntity.getDocumentDate());
    } else {
      strWho += " " + getSrvI18n().getMsg("in", langDef) + " " + getSrvI18n()
    .getMsg(getSrvTypeCode().getTypeCodeMap().get(pEntity.getOwnerType())
      .getSimpleName() + "short", langDef) + " #" + getSrvOrm().getIdDatabase()
        + "-" + pEntity.getOwnerId() + ", "
          + dateFormat.format(pEntity.getDocumentDate());
    }
    String strFrom = " " + getSrvI18n().getMsg("from", langDef) + " "
  + getSrvI18n().getMsg(getSrvTypeCode().getTypeCodeMap()
    .get(pSource.getSourceType()).getSimpleName() + "short", langDef)
      + " #" + getSrvOrm().getIdDatabase() + "-" + pSource.getSourceId();
    return getSrvI18n().getMsg("made_at", langDef) + " " + dateFormat.format(
      new Date()) + " " + getSrvI18n().getMsg("by", langDef)
        + " " + strWho + strFrom;
  }

  //Simple getters and setters:
  /**
   * <p>Getter for srvOrm.</p>
   * @return ASrvOrm<RS>
   **/
  public final ASrvOrm<RS> getSrvOrm() {
    return this.srvOrm;
  }

  /**
   * <p>Setter for srvOrm.</p>
   * @param pSrvOrm reference
   **/
  public final void setSrvOrm(final ASrvOrm<RS> pSrvOrm) {
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
   * <p>Getter for srvTypeCode.</p>
   * @return ISrvTypeCode
   **/
  public final ISrvTypeCode getSrvTypeCode() {
    return this.srvTypeCode;
  }

  /**
   * <p>Setter for srvTypeCode.</p>
   * @param pSrvTypeCode reference
   **/
  public final void setSrvTypeCode(final ISrvTypeCode pSrvTypeCode) {
    this.srvTypeCode = pSrvTypeCode;
  }

  /**
   * <p>Getter for queries.</p>
   * @return Map<String, String>
   **/
  public final Map<String, String> getQueries() {
    return this.queries;
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
   * <p>Getter for settersRapiHolder.</p>
   * @return IHolderForClassByName<Method>
   **/
  public final IHolderForClassByName<Method> getSettersRapiHolder() {
    return this.settersRapiHolder;
  }

  /**
   * <p>Setter for settersRapiHolder.</p>
   * @param pSettersRapiHolder reference
   **/
  public final void setSettersRapiHolder(
    final IHolderForClassByName<Method> pSettersRapiHolder) {
    this.settersRapiHolder = pSettersRapiHolder;
  }

  /**
   * <p>Getter for entitiesFactoriesFatory.</p>
   * @return IFactoryAppBeansByClass<IFactorySimple<?>>
   **/
  public final IFactoryAppBeansByClass<IFactorySimple<?>>
    getEntitiesFactoriesFatory() {
    return this.entitiesFactoriesFatory;
  }

  /**
   * <p>Setter for entitiesFactoriesFatory.</p>
   * @param pEntitiesFactoriesFatory reference
   **/
  public final void setEntitiesFactoriesFatory(
    final IFactoryAppBeansByClass<IFactorySimple<?>>
      pEntitiesFactoriesFatory) {
    this.entitiesFactoriesFatory = pEntitiesFactoriesFatory;
  }
}

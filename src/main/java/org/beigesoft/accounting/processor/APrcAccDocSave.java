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

import org.beigesoft.exception.ExceptionWithCode;
import org.beigesoft.model.IRequestData;
import org.beigesoft.service.ISrvI18n;
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.accounting.persistable.IDoc;
import org.beigesoft.accounting.service.ISrvAccEntry;
import org.beigesoft.accounting.service.ISrvAccSettings;
import org.beigesoft.service.IEntityProcessor;

/**
 * <p>Service that save document into DB.</p>
 *
 * @param <RS> platform dependent record set type
 * @param <T> entity type
 * @author Yury Demidenko
 */
public abstract class APrcAccDocSave<RS, T extends IDoc>
  implements IEntityProcessor<T, Long> {

  /**
   * <p>ORM service.</p>
   **/
  private ISrvOrm<RS> srvOrm;

  /**
   * <p>I18N service.</p>
   **/
  private ISrvI18n srvI18n;

  /**
   * <p>Business service for accounting entries.</p>
   **/
  private ISrvAccEntry srvAccEntry;

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
  public final T process(
    final Map<String, Object> pAddParam,
      final T pEntity, final IRequestData pRequestData) throws Exception {
    @SuppressWarnings("unchecked")
    Class<T> entityClass = (Class<T>) pEntity.getClass();
    boolean isNew = pEntity.getIsNew();
    makeFirstPrepareForSave(pAddParam, pEntity, pRequestData);
    String actionAdd = pRequestData.getParameter("actionAdd");
    if (pEntity.getIsNew()) {
      if (pEntity.getReversedId() != null
        && pEntity.getItsTotal().doubleValue() >= 0) {
          throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
            "Reversed Total must be less than 0! " + pAddParam.get("user"));
      }
      if (pEntity.getReversedId() == null
        && pEntity.getItsTotal().doubleValue() < 0) {
          throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
            "Total must be less than 0 only in reversal! "
              + pAddParam.get("user"));
      }
      String langDef = (String) pAddParam.get("langDef");
      if (pEntity.getReversedId() != null) {
        String descr;
        if (pEntity.getDescription() == null) {
          descr = "";
        } else {
          descr = pEntity.getDescription();
        }
        pEntity.setDescription(descr
          + " " + getSrvI18n().getMsg("reversed_n", langDef) + pEntity
            .getReversedIdDatabaseBirth() + "-"
              + pEntity.getReversedId());
      }
      getSrvOrm().insertEntity(pAddParam, pEntity);
      pEntity.setIsNew(false);
      if (pEntity.getReversedId() != null) {
        T reversed;
        if (pEntity.getIdDatabaseBirth().equals(pEntity
          .getReversedIdDatabaseBirth())) { //both from current database
          reversed = getSrvOrm().retrieveEntityById(pAddParam,
            entityClass, pEntity.getReversedId());
        } else { //reversing foreign doc
          String tblNm = entityClass.getSimpleName().toUpperCase();
          String whereStr = " where " + tblNm + ".IDBIRTH=" + pEntity
            .getReversedId() + " and " + tblNm + ".IDDATABASEBIRTH=" + pEntity
              .getReversedIdDatabaseBirth();
          reversed = getSrvOrm().retrieveEntityWithConditions(pAddParam,
            entityClass, whereStr);
        }
        if (reversed.getReversedId() != null) {
          throw new ExceptionWithCode(ExceptionWithCode.FORBIDDEN,
            "Attempt to double reverse! " + pAddParam.get("user"));
        }
        String oldDesr = "";
        if (reversed.getDescription() != null) {
          oldDesr = reversed.getDescription();
        }
        reversed.setDescription(oldDesr
          + " " + getSrvI18n().getMsg("reversing_n", langDef) + pEntity
            .getIdDatabaseBirth() + "-"
              + pEntity.getItsId()); //reversing always new from current DB
        reversed.setReversedId(pEntity.getItsId());
        reversed.setReversedIdDatabaseBirth(pEntity.getIdDatabaseBirth());
        getSrvOrm().updateEntity(pAddParam, reversed);
        srvAccEntry.reverseEntries(pAddParam, pEntity, reversed);
      }
    } else {
      if (!pEntity.getIdDatabaseBirth().equals(getSrvOrm().getIdDatabase())) {
        throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
          "can_not_change_foreign_src");
      }
      //Prevent any changes when document has accounting entries:
      T oldEntity = getSrvOrm().retrieveEntityById(pAddParam,
        entityClass, pEntity.getItsId());
      if (oldEntity.getHasMadeAccEntries()) {
        throw new ExceptionWithCode(ExceptionWithCode.FORBIDDEN,
          "Attempt to update accounted document by " + pAddParam.get("user"));
      }
      checkOtherFraudUpdate(pAddParam, pEntity, pRequestData, oldEntity);
      //update also before making acc-entries, cause using SQL queries!!!
      getSrvOrm().updateEntity(pAddParam, pEntity);
    }
    if (!pEntity.getHasMadeAccEntries()
      && "makeAccEntries".equals(actionAdd)) {
      if (pEntity.getItsTotal().doubleValue() <= 0) {
        throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
          "total_less_or_eq_zero");
      }
      addCheckIsReadyToAccount(pAddParam, pEntity, pRequestData);
      //it will set hasMadeAccEntries=true and update this doc:
      this.srvAccEntry.makeEntries(pAddParam, pEntity);
    }
    makeOtherEntries(pAddParam, pEntity, pRequestData, isNew);
    return pEntity;
  }
  //To override:
  /**
   * <p>Make save preparations before insert/update block if it's need.</p>
   * @param pAddParam additional param
   * @param pEntity entity
   * @param pRequestData Request Data
   * @throws Exception - an exception
   **/
  public abstract void makeFirstPrepareForSave(Map<String, Object> pAddParam,
    T pEntity, IRequestData pRequestData) throws Exception;

  /**
   * <p>Make other entries include reversing if it's need when save.</p>
   * @param pAddParam additional param
   * @param pEntity entity
   * @param pRequestData Request Data
   * @param pIsNew if entity was new
   * @throws Exception - an exception
   **/
  public abstract void makeOtherEntries(Map<String, Object> pAddParam,
    T pEntity, IRequestData pRequestData, boolean pIsNew) throws Exception;

  /**
   * <p>Check other fraud update e.g. prevent change completed unaccounted
   * manufacturing process.</p>
   * @param pAddParam additional param
   * @param pEntity entity
   * @param pRequestData Request Data
   * @param pOldEntity old saved entity
   * @throws Exception - an exception
   **/
  public abstract void checkOtherFraudUpdate(Map<String, Object> pAddParam,
    T pEntity, IRequestData pRequestData, T pOldEntity) throws Exception;

  /**
   * <p>Additional check document for ready to account (make acc.entries).</p>
   * @param pAddParam additional param
   * @param pRequestData Request Data
   * @param pEntity entity
   * @throws Exception - an exception if don't
   **/
  public abstract void addCheckIsReadyToAccount(Map<String, Object> pAddParam,
    T pEntity, IRequestData pRequestData) throws Exception;

  //Simple getters and setters:
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
   * <p>Geter for srvI18n.</p>
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
   * <p>Getter for srvAccEntry.</p>
   * @return ISrvAccEntry
   **/
  public final ISrvAccEntry getSrvAccEntry() {
    return this.srvAccEntry;
  }

  /**
   * <p>Setter for srvAccEntry.</p>
   * @param pSrvAccEntry reference
   **/
  public final void setSrvAccEntry(final ISrvAccEntry pSrvAccEntry) {
    this.srvAccEntry = pSrvAccEntry;
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
}

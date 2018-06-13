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
import java.util.Date;
import java.util.Locale;
import java.text.DateFormat;

import org.beigesoft.model.IRequestData;
import org.beigesoft.exception.ExceptionWithCode;
import org.beigesoft.factory.IFactoryAppBeansByName;
import org.beigesoft.holder.IHolderForClassByName;
import org.beigesoft.converter.IConverterToFromString;
import org.beigesoft.service.IEntityProcessor;
import org.beigesoft.service.ISrvI18n;
import org.beigesoft.service.ISrvDate;
import org.beigesoft.settings.IMngSettings;
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.accounting.persistable.AccountingEntry;
import org.beigesoft.accounting.persistable.AccountingEntries;

/**
 * <p>Service that create copy AccountingEntry for doc AccountingEntries.</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class PrcAccEntryCopy<RS>
  implements IEntityProcessor<AccountingEntry, Long> {

  /**
   * <p>ORM service.</p>
   **/
  private ISrvOrm<RS> srvOrm;

  /**
   * <p>Manager UVD settings.</p>
   **/
  private IMngSettings mngUvdSettings;

  /**
   * <p>Date service.</p>
   **/
  private ISrvDate srvDate;

  /**
   * <p>Fields converters factory.</p>
   **/
  private IFactoryAppBeansByName<IConverterToFromString<?>>
    convertersFieldsFatory;

  /**
   * <p>Field converter names holder.</p>
   **/
  private IHolderForClassByName<String> fieldConverterNamesHolder;

  /**
   * <p>I18N service.</p>
   **/
  private ISrvI18n srvI18n;

  /**
   * <p>AccountingEntries type code.</p>
   **/
  private final Integer accountingEntriesTypeCode =
    new AccountingEntries().constTypeCode();

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
  public final AccountingEntry process(
    final Map<String, Object> pAddParam,
      final AccountingEntry pEntity,
        final IRequestData pRequestData) throws Exception {
    AccountingEntry entity = this.srvOrm.retrieveEntity(pAddParam, pEntity);
    AccountingEntries doc = getSrvOrm().retrieveEntityById(pAddParam,
      AccountingEntries.class, entity.getSourceId());
    if (!doc.getIdDatabaseBirth()
      .equals(getSrvOrm().getIdDatabase())) {
      throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
        "can_not_make_entry_for_foreign_src");
    }
    entity.setIsNew(true);
    entity.setItsId(null);
    entity.setIdBirth(null);
    entity.setIdDatabaseBirth(this.srvOrm.getIdDatabase());
    entity.setItsDate(new Date());
    entity.setSourceType(this.accountingEntriesTypeCode);
    String docDescription;
    if (doc.getDescription() != null) {
      docDescription = doc.getDescription();
    } else {
      docDescription = "";
    }
    String langDef = (String) pAddParam.get("langDef");
    DateFormat dateFormat = DateFormat.getDateTimeInstance(
      DateFormat.MEDIUM, DateFormat.SHORT, new Locale(langDef));
    entity.setDescription(getSrvI18n().getMsg(AccountingEntries.class
  .getSimpleName() + "short", langDef) + " #" + doc.getIdDatabaseBirth() + "-"
    + doc.getItsId() + ", " + dateFormat.format(doc.getItsDate())
      + ". " + docDescription); //only local allowed
    entity.setIsNew(true);
    pRequestData.setAttribute("entity", entity);
    pRequestData.setAttribute("AccountingEntriesVersion", doc.getItsVersion());
    pRequestData.setAttribute("mngUvds", this.mngUvdSettings);
    pRequestData.setAttribute("srvOrm", this.srvOrm);
    pRequestData.setAttribute("srvDate", this.srvDate);
    pRequestData.setAttribute("hldCnvFtfsNames",
      this.fieldConverterNamesHolder);
    pRequestData.setAttribute("fctCnvFtfs", this.convertersFieldsFatory);
    return entity;
  }

  //Simple getters and setters:
  /**
   * <p>Geter for accountingEntriesTypeCode.</p>
   * @return Integer
   **/
  public final Integer getAccountingEntriesTypeCode() {
    return this.accountingEntriesTypeCode;
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
   * <p>Getter for mngUvdSettings.</p>
   * @return IMngSettings
   **/
  public final IMngSettings getMngUvdSettings() {
    return this.mngUvdSettings;
  }

  /**
   * <p>Setter for mngUvdSettings.</p>
   * @param pMngUvdSettings reference
   **/
  public final void setMngUvdSettings(final IMngSettings pMngUvdSettings) {
    this.mngUvdSettings = pMngUvdSettings;
  }

  /**
   * <p>Getter for srvDate.</p>
   * @return ISrvDate
   **/
  public final ISrvDate getSrvDate() {
    return this.srvDate;
  }

  /**
   * <p>Setter for srvDate.</p>
   * @param pSrvDate reference
   **/
  public final void setSrvDate(final ISrvDate pSrvDate) {
    this.srvDate = pSrvDate;
  }

  /**
   * <p>Getter for convertersFieldsFatory.</p>
   * @return IFactoryAppBeansByName<IConverterToFromString<?>>
   **/
  public final IFactoryAppBeansByName<IConverterToFromString<?>>
    getConvertersFieldsFatory() {
    return this.convertersFieldsFatory;
  }

  /**
   * <p>Setter for convertersFieldsFatory.</p>
   * @param pConvertersFieldsFatory reference
   **/
  public final void setConvertersFieldsFatory(
    final IFactoryAppBeansByName<IConverterToFromString<?>>
      pConvertersFieldsFatory) {
    this.convertersFieldsFatory = pConvertersFieldsFatory;
  }

  /**
   * <p>Getter for fieldConverterNamesHolder.</p>
   * @return IHolderForClassByName<String>
   **/
  public final IHolderForClassByName<String> getFieldConverterNamesHolder() {
    return this.fieldConverterNamesHolder;
  }

  /**
   * <p>Setter for fieldConverterNamesHolder.</p>
   * @param pFieldConverterNamesHolder reference
   **/
  public final void setFieldConverterNamesHolder(
    final IHolderForClassByName<String> pFieldConverterNamesHolder) {
    this.fieldConverterNamesHolder = pFieldConverterNamesHolder;
  }
}

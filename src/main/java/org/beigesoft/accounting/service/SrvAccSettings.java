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

import java.util.Map;
import java.util.List;

import org.beigesoft.exception.ExceptionWithCode;
import org.beigesoft.accounting.persistable.AccSettings;
import org.beigesoft.accounting.persistable.AccEntriesSourcesLine;
import org.beigesoft.accounting.persistable.CogsItemSourcesLine;
import org.beigesoft.accounting.persistable.DrawMaterialSourcesLine;
import org.beigesoft.service.ISrvOrm;

/**
 * <p>Business service for accounting settings.</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class SrvAccSettings<RS>
  implements ISrvAccSettings {

  /**
   * <p>Current AccSettings.</p>
   **/
  private AccSettings accSettings;

  /**
   * <p>Entity class.</p>
   **/
  private final Class<AccSettings> entityClass =
    AccSettings.class;

  /**
   * <p>ORM service.</p>
   **/
  private ISrvOrm<RS> srvOrm;

  /**
   * <p>minimum constructor.</p>
   **/
  public SrvAccSettings() {
  }

  /**
   * <p>Useful constructor.</p>
   * @param pSrvOrm ORM service
   **/
  public SrvAccSettings(final ISrvOrm<RS> pSrvOrm) {
    this.srvOrm = pSrvOrm;
  }

  /**
   * <p>Retrieve/get Accounting settings.</p>
   * @param pAddParam additional param
   * @return Accounting settings
   * @throws Exception - an exception
   **/
  @Override
  public final synchronized AccSettings lazyGetAccSettings(
    final Map<String, Object> pAddParam) throws Exception {
    if (this.accSettings == null) {
      retrieveAccSettings(pAddParam);
    }
    return this.accSettings;
  }

  /**
   * <p>Clear Accounting settings to retrieve from
   * database new version.</p>
   * @param pAddParam additional param
   **/
  @Override
  public final synchronized void clearAccSettings(
    final Map<String, Object> pAddParam) {
    this.accSettings = null;
  }

  /**
   * <p>Save acc-settings into DB.</p>
   * @param pAddParam additional param
   * @param pEntity entity
   * @throws Exception - an exception
   **/
  @Override
  public final synchronized void saveAccSettings(
    final Map<String, Object> pAddParam,
      final AccSettings pEntity) throws Exception {
    if (pEntity.getIsNew()) {
      throw new ExceptionWithCode(ExceptionWithCode.FORBIDDEN,
        "Attempt to insert accounting settings by " + pAddParam.get("user"));
    } else {
      if (pEntity.getCostPrecision() < 0 || pEntity.getCostPrecision() > 4) {
        throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
          "precision_must_be_from_0_to_4");
      }
      if (pEntity.getPricePrecision() < 0 || pEntity.getPricePrecision() > 4) {
        throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
          "precision_must_be_from_0_to_4");
      }
      if (pEntity.getQuantityPrecision() < 0
        || pEntity.getQuantityPrecision() > 4) {
        throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
          "precision_must_be_from_0_to_4");
      }
      if (pEntity.getBalancePrecision() < 0
        || pEntity.getBalancePrecision() > 4) {
        throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
          "precision_must_be_from_0_to_4");
      }
      getSrvOrm().updateEntity(pAddParam, pEntity);
      retrieveAccSettings(pAddParam);
    }
  }

  //Utils:
  /**
   * <p>Retrieve Accounting settings from database.</p>
   * @param pAddParam additional param
   * @throws Exception - an exception
   **/
  public final synchronized void retrieveAccSettings(
    final Map<String, Object> pAddParam) throws Exception {
    this.accSettings = new AccSettings();
    this.accSettings.setItsId(1L);
    this.accSettings = getSrvOrm().retrieveEntity(pAddParam, this.accSettings);
    if (this.accSettings == null) {
      throw new ExceptionWithCode(ExceptionWithCode.CONFIGURATION_MISTAKE,
        "There is no accounting settings!!!");
    }
    DrawMaterialSourcesLine dmsl = new DrawMaterialSourcesLine();
    dmsl.setItsOwner(this.accSettings);
    List<DrawMaterialSourcesLine> drawMaterialSources = getSrvOrm()
      .retrieveListForField(pAddParam, dmsl, "itsOwner");
    this.accSettings.setDrawMaterialSources(drawMaterialSources);
    CogsItemSourcesLine cisl = new CogsItemSourcesLine();
    cisl.setItsOwner(this.accSettings);
    List<CogsItemSourcesLine> cogsItemSources = getSrvOrm()
      .retrieveListForField(pAddParam, cisl, "itsOwner");
    this.accSettings.setCogsItemSources(cogsItemSources);
    AccEntriesSourcesLine aesl = new AccEntriesSourcesLine();
    aesl.setItsOwner(this.accSettings);
    List<AccEntriesSourcesLine> accEntriesSources = getSrvOrm()
      .retrieveListForField(pAddParam, aesl, "itsOwner");
    this.accSettings.setAccEntriesSources(accEntriesSources);
  }

  //Simple getters and setters:
  /**
   * <p>Geter for entityClass.</p>
   * @return Class<AccSettings>
   **/
  public final Class<AccSettings> getEntityClass() {
    return this.entityClass;
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
}

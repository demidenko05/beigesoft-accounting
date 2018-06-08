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

import org.beigesoft.model.IRequestData;
import org.beigesoft.model.IHasId;
import org.beigesoft.service.IEntityProcessor;
import org.beigesoft.accounting.service.ISrvAccSettings;

/**
 * <p>Service that save accounting settings line
 *  and refresh settings in the cache.</p>
 *
 * @param <RS> platform dependent record set type
 * @param <T> entity type
 * @author Yury Demidenko
 */
public class PrcAccSettingsLineSave<RS, T extends IHasId<Long>>
  implements IEntityProcessor<T, Long> {

  /**
   * <p>Entity FOL Save delegator.</p>
   **/
  private IEntityProcessor<T, Long> prcEntityFolSave;

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
    T result = this.prcEntityFolSave.process(pAddParam, pEntity, pRequestData);
    this.srvAccSettings.clearAccSettings(pAddParam);
    return result;
  }

  //Simple getters and setters:
  /**
   * <p>Getter for prcEntityFolSave.</p>
   * @return PrcEntityFolSave<RS, T, Long>
   **/
  public final IEntityProcessor<T, Long> getPrcEntityFolSave() {
    return this.prcEntityFolSave;
  }

  /**
   * <p>Setter for prcEntityFolSave.</p>
   * @param pPrcEntityFolSave reference
   **/
  public final void setPrcEntityFolSave(
    final IEntityProcessor<T, Long> pPrcEntityFolSave) {
    this.prcEntityFolSave = pPrcEntityFolSave;
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

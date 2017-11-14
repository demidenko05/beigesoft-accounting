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
import org.beigesoft.persistable.IPersistableBase;
import org.beigesoft.service.IEntityProcessor;
import org.beigesoft.orm.processor.PrcEntityPbCopy;
import org.beigesoft.accounting.service.ISrvAccSettings;

/**
 * <p>Service that make entity copy from DB.
 * Those entities are: GoodsLoss, InvItem, BeginningInventory,
 * TODO.</p>
 *
 * @param <RS> platform dependent record set type
 * @param <T> entity type
 * @author Yury Demidenko
 */
public class PrcAccEntityPbCopy<RS, T extends IPersistableBase>
  implements IEntityProcessor<T, Long> {

  /**
   * <p>EntityPb Copy delegator.</p>
   **/
  private PrcEntityPbCopy<RS, T> prcEntityPbCopy;

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
      final T pEntityPb, final IRequestData pRequestData) throws Exception {
    pRequestData.setAttribute("accSettings",
      this.srvAccSettings.lazyGetAccSettings(pAddParam));
    return this.prcEntityPbCopy.process(pAddParam, pEntityPb, pRequestData);
  }

  //Simple getters and setters:
  /**
   * <p>Getter for prcEntityPbCopy.</p>
   * @return PrcEntityPbCopy<RS, T>
   **/
  public final PrcEntityPbCopy<RS, T> getPrcEntityPbCopy() {
    return this.prcEntityPbCopy;
  }

  /**
   * <p>Setter for prcEntityPbCopy.</p>
   * @param pPrcEntityPbCopy reference
   **/
  public final void setPrcEntityPbCopy(
    final PrcEntityPbCopy<RS, T> pPrcEntityPbCopy) {
    this.prcEntityPbCopy = pPrcEntityPbCopy;
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

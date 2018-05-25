package org.beigesoft.accounting.service;

/*
 * Copyright (c) 2018 Beigesoft ™
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
import org.beigesoft.handler.IHandlerRequest;
import org.beigesoft.accounting.persistable.AccSettings;

/**
 * <p>It handles accounting request for setting accounting variables
 * and additional internationalization.
 * It's invoked by the first I18N handler.</p>
 *
 * @author Yury Demidenko
 */
public class HndlAccVarsRequest implements IHandlerRequest {

  /**
   * <p>Business service for accounting settings.</p>
   **/
  private ISrvAccSettings srvAccSettings;

  /**
   * <p>Handle request.</p>
   * @param pReqVars Request scoped variables
   * @param pRequestData Request Data
   * @throws Exception - an exception
   */
  @Override
  public final void handle(final Map<String, Object> pReqVars,
    final IRequestData pRequestData) throws Exception {
    AccSettings as = srvAccSettings.lazyGetAccSettings(pReqVars);
    Boolean rSisUsePrecision0 = as.getPricePrecision() == 0
      || as.getCostPrecision() == 0 || as.getBalancePrecision() == 0
        || as.getQuantityPrecision() == 0;
    Boolean rSisUsePrecision1 = as.getPricePrecision() == 1
      || as.getCostPrecision() == 1 || as.getBalancePrecision() == 1
        || as.getQuantityPrecision() == 1;
    Boolean rSisUsePrecision2 = as.getPricePrecision() == 2
      || as.getCostPrecision() == 2 || as.getBalancePrecision() == 2
        || as.getQuantityPrecision() == 2;
    Boolean rSisUsePrecision3 = as.getPricePrecision() == 3
      || as.getCostPrecision() == 3 || as.getBalancePrecision() == 3
        || as.getQuantityPrecision() == 3;
    Boolean rSisUsePrecision4 = as.getPricePrecision() == 4
      || as.getCostPrecision() == 4 || as.getBalancePrecision() == 4
        || as.getQuantityPrecision() == 4;
    pRequestData.setAttribute("RSisUsePrecision0", rSisUsePrecision0);
    pRequestData.setAttribute("RSisUsePrecision1", rSisUsePrecision1);
    pRequestData.setAttribute("RSisUsePrecision2", rSisUsePrecision2);
    pRequestData.setAttribute("RSisUsePrecision3", rSisUsePrecision3);
    pRequestData.setAttribute("RSisUsePrecision4", rSisUsePrecision4);
    pRequestData.setAttribute("accSettings", as);
  }

  //Simple getters and setters (their synchronization never hit performance):
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

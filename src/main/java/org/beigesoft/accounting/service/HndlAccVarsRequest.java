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
import java.math.RoundingMode;

import org.beigesoft.model.IRequestData;
import org.beigesoft.handler.IHandlerRequestDch;
import org.beigesoft.accounting.persistable.AccSettings;

/**
 * <p>It handles accounting request for setting accounting variables
 * and additional internationalization.
 * It's invoked by the first I18N handler.</p>
 *
 * @author Yury Demidenko
 */
public class HndlAccVarsRequest implements IHandlerRequestDch {

  /**
   * <p>Business service for accounting settings.</p>
   **/
  private ISrvAccSettings srvAccSettings;

  /**
   * <p>Additional I18n Request Handler - Web-Store vars.</p>
   */
  private IHandlerRequestDch additionalI18nReqHndl;

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
    String rSmRound;
    if (as.getRoundingMode().equals(RoundingMode.HALF_UP)) {
      rSmRound = "S";
    } else if (as.getRoundingMode().equals(RoundingMode.HALF_DOWN)) {
      rSmRound = "s";
    } else if (as.getRoundingMode().equals(RoundingMode.UP)) {
      rSmRound = "U";
    } else if (as.getRoundingMode().equals(RoundingMode.DOWN)) {
      rSmRound = "D";
    } else if (as.getRoundingMode().equals(RoundingMode.HALF_EVEN)) {
      rSmRound = "B";
    } else if (as.getRoundingMode().equals(RoundingMode.CEILING)) {
      rSmRound = "C";
    } else if (as.getRoundingMode().equals(RoundingMode.FLOOR)) {
      rSmRound = "F";
    } else {
      rSmRound = "S";
    }
    pRequestData.setAttribute("RSisUsePrecision0", rSisUsePrecision0);
    pRequestData.setAttribute("RSisUsePrecision1", rSisUsePrecision1);
    pRequestData.setAttribute("RSisUsePrecision2", rSisUsePrecision2);
    pRequestData.setAttribute("RSisUsePrecision3", rSisUsePrecision3);
    pRequestData.setAttribute("RSisUsePrecision4", rSisUsePrecision4);
    pRequestData.setAttribute("quantityPrecision", as.getQuantityPrecision());
    pRequestData.setAttribute("pricePrecision", as.getPricePrecision());
    pRequestData.setAttribute("costPrecision", as.getCostPrecision());
    pRequestData.setAttribute("balancePrecision", as.getBalancePrecision());
    pRequestData.setAttribute("RSmRound", rSmRound);
    pRequestData.setAttribute("accSettings", as);
    if (this.additionalI18nReqHndl != null) {
      this.additionalI18nReqHndl.handle(pReqVars, pRequestData);
    }
  }

  /**
   * <p>Handle data changed event.</p>
   * @throws Exception - an exception
   **/
  @Override
  public final synchronized void handleDataChanged() throws Exception {
    if (this.additionalI18nReqHndl != null) {
      this.additionalI18nReqHndl.handleDataChanged();
    }
  }

  //Simple getters and setters:
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
   * <p>Getter for additionalI18nReqHndl.</p>
   * @return IHandlerRequest
   **/
  public final IHandlerRequestDch getAdditionalI18nReqHndl() {
    return this.additionalI18nReqHndl;
  }

  /**
   * <p>Setter for additionalI18nReqHndl.</p>
   * @param pAdditionalI18nReqHndl reference
   **/
  public final void setAdditionalI18nReqHndl(
    final IHandlerRequestDch pAdditionalI18nReqHndl) {
    this.additionalI18nReqHndl = pAdditionalI18nReqHndl;
  }
}

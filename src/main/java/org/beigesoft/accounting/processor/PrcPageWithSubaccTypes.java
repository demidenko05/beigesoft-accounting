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
import org.beigesoft.service.IProcessor;
import org.beigesoft.accounting.service.ISrvTypeCode;

/**
 * <p>Service that retrieve accounting entities page that requires
 * subaccount types map - Account, AdditionCostLine, SubaccountLine,
 * PrepaymentTo, PrepaymentFrom, PaymentTo, PaymentFrom.</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class PrcPageWithSubaccTypes<RS> implements IProcessor {

  /**
   * <p>Process Acc-Entities Page delegate.</p>
   **/
  private IProcessor prcAccEntitiesPage;

  /**
   * <p>Type Codes of sub-accounts service.</p>
   **/
  private ISrvTypeCode srvTypeCode;

  /**
   * <p>Process entity request.</p>
   * @param pAddParam additional param
   * @param pRequestData Request Data
   * @throws Exception - an exception
   **/
  @Override
  public final void process(final Map<String, Object> pAddParam,
    final IRequestData pRequestData) throws Exception {
    this.prcAccEntitiesPage.process(pAddParam, pRequestData);
    pRequestData.setAttribute("typeCodeSubaccMap",
      this.srvTypeCode.getTypeCodeMap());
  }

  //Simple getters and setters:
  /**
   * <p>Getter for prcAccEntitiesPage.</p>
   * @return IProcessor
   **/
  public final IProcessor getPrcAccEntitiesPage() {
    return this.prcAccEntitiesPage;
  }

  /**
   * <p>Setter for prcAccEntitiesPage.</p>
   * @param pPrcAccEntitiesPage reference
   **/
  public final void setPrcAccEntitiesPage(
    final IProcessor pPrcAccEntitiesPage) {
    this.prcAccEntitiesPage = pPrcAccEntitiesPage;
  }

  /**
   * <p>Geter for srvTypeCode.</p>
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
}

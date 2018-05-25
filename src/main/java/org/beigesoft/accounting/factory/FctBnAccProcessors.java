package org.beigesoft.accounting.factory;

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
import java.util.HashMap;

import org.beigesoft.factory.IFactoryAppBeansByName;
import org.beigesoft.exception.ExceptionWithCode;
import org.beigesoft.service.IProcessor;
import org.beigesoft.orm.processor.PrcEntitiesPage;
import org.beigesoft.orm.factory.FctBnProcessors;
import org.beigesoft.accounting.processor.PrcPageWithSubaccTypes;
import org.beigesoft.accounting.service.ISrvAccSettings;
import org.beigesoft.accounting.service.ISrvTypeCode;

/**
 * <p>ACC processors factory.</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class FctBnAccProcessors<RS>
  implements IFactoryAppBeansByName<IProcessor> {

  /**
   * <p>Factory non-ass processors.
   * Concrete factory for concrete bean name that is bean class
   * simple name. Any way any such factory must be no abstract.</p>
   **/
  private FctBnProcessors<RS> fctBnProcessors;

  /**
   * <p>Business service for accounting settings.</p>
   **/
  private ISrvAccSettings srvAccSettings;

  /**
   * <p>Type Codes of sub-accounts service.</p>
   **/
  private ISrvTypeCode srvTypeCode;

  /**
   * <p>Additional processors factory, e.g. webstore.</p>
   **/
  private IFactoryAppBeansByName<IProcessor> additionalPf;

  /**
   * <p>Converters map "converter name"-"object' s converter".</p>
   **/
  private final Map<String, IProcessor>
    processorsMap =
      new HashMap<String, IProcessor>();

  /**
   * <p>Get bean in lazy mode (if bean is null then initialize it).</p>
   * @param pAddParam additional param
   * @param pBeanName - bean name
   * @return requested bean
   * @throws Exception - an exception
   */
  @Override
  public final IProcessor lazyGet(
    final Map<String, Object> pAddParam,
      final String pBeanName) throws Exception {
    IProcessor proc =
      this.processorsMap.get(pBeanName);
    if (proc == null) {
      // locking:
      synchronized (this.processorsMap) {
        // make sure again whether it's null after locking:
        proc = this.processorsMap.get(pBeanName);
        if (proc == null) {
          if (pBeanName.equals(PrcEntitiesPage.class.getSimpleName())) {
            proc = this.fctBnProcessors
              .lazyGet(pAddParam, PrcEntitiesPage.class.getSimpleName());
          } else if (pBeanName
            .equals(PrcPageWithSubaccTypes.class.getSimpleName())) {
            proc = createPutPrcPageWithSubaccTypes(pAddParam);
          } else if (this.additionalPf != null) {
            proc = this.additionalPf.lazyGet(pAddParam, pBeanName);
          }
        }
      }
    }
    if (proc == null) {
      throw new ExceptionWithCode(ExceptionWithCode.CONFIGURATION_MISTAKE,
        "There is no processor with name " + pBeanName);
    }
    return proc;
  }

  /**
   * <p>Set bean.</p>
   * @param pBeanName - bean name
   * @param pBean bean
   * @throws Exception - an exception
   */
  @Override
  public final synchronized void set(final String pBeanName,
    final IProcessor pBean) throws Exception {
    this.processorsMap.put(pBeanName, pBean);
  }

  /**
   * <p>Get PrcPageWithSubaccTypes (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcPageWithSubaccTypes
   * @throws Exception - an exception
   */
  protected final PrcPageWithSubaccTypes<RS>
    createPutPrcPageWithSubaccTypes(
      final Map<String, Object> pAddParam) throws Exception {
    PrcPageWithSubaccTypes<RS> proc = new PrcPageWithSubaccTypes<RS>();
    PrcEntitiesPage procDlg = (PrcEntitiesPage) this.fctBnProcessors
      .lazyGet(pAddParam, PrcEntitiesPage.class.getSimpleName());
    proc.setPrcAccEntitiesPage(procDlg);
    proc.setSrvTypeCode(getSrvTypeCode());
    //assigning fully initialized object:
    this.processorsMap
      .put(PrcPageWithSubaccTypes.class.getSimpleName(), proc);
    return proc;
  }

  //Simple getters and setters:

  /**
   * <p>Getter for fctBnProcessors.</p>
   * @return IFactoryAppBeansByName<IProcessor>
   **/
  public final FctBnProcessors<RS> getFctBnProcessors() {
    return this.fctBnProcessors;
  }

  /**
   * <p>Setter for fctBnProcessors.</p>
   * @param pFctBnProcessors reference
   **/
  public final void setFctBnProcessors(
    final FctBnProcessors<RS> pFctBnProcessors) {
    this.fctBnProcessors = pFctBnProcessors;
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

  /**
   * <p>Getter for additionalPf.</p>
   * @return IFactoryAppBeansByName<IProcessor>
   **/
  public final IFactoryAppBeansByName<IProcessor> getAdditionalPf() {
    return this.additionalPf;
  }

  /**
   * <p>Setter for additionalPf.</p>
   * @param pAdditionalPf reference
   **/
  public final void setAdditionalPf(
    final IFactoryAppBeansByName<IProcessor> pAdditionalPf) {
    this.additionalPf = pAdditionalPf;
  }
}

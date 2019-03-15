package org.beigesoft.accounting.factory;

/*
 * Copyright (c) 2018 Beigesoft™
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

import org.beigesoft.log.ILog;
import org.beigesoft.factory.IFactoryAppBeansByName;
import org.beigesoft.exception.ExceptionWithCode;
import org.beigesoft.service.IProcessor;
import org.beigesoft.service.ISrvDatabase;
import org.beigesoft.accounting.processor.PrcRevealTaxCat;

/**
 * <p>Factory of transactional/non-transactional processors for
 * SimpleRequestHandler - srhAccSecure.</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class FctForSrhAccSecure<RS>
  implements IFactoryAppBeansByName<IProcessor> {

  /**
   * <p>Logger.</p>
   **/
  private ILog logger;

  /**
   * <p>Database service.</p>
   */
  private ISrvDatabase<RS> srvDatabase;

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
    IProcessor proc = this.processorsMap.get(pBeanName);
    if (proc == null) {
      // locking:
      synchronized (this.processorsMap) {
        // make sure again whether it's null after locking:
        proc = this.processorsMap.get(pBeanName);
        if (proc == null
          && pBeanName.equals(PrcRevealTaxCat.class.getSimpleName())) {
          proc = createPutPrcRevealTaxCat(pAddParam);
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
   * <p>Get PrcRevealTaxCat (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcRevealTaxCat
   * @throws Exception - an exception
   */
  protected final PrcRevealTaxCat<RS>
    createPutPrcRevealTaxCat(
      final Map<String, Object> pAddParam) throws Exception {
    PrcRevealTaxCat<RS> proc = new PrcRevealTaxCat<RS>();
    proc.setLogger(getLogger());
    proc.setSrvDatabase(getSrvDatabase());
    //assigning fully initialized object:
    this.processorsMap
      .put(PrcRevealTaxCat.class.getSimpleName(), proc);
    return proc;
  }

  //Simple getters and setters:
  /**
   * <p>Geter for logger.</p>
   * @return ILog
   **/
  public final ILog getLogger() {
    return this.logger;
  }

  /**
   * <p>Setter for logger.</p>
   * @param pLogger reference
   **/
  public final void setLogger(final ILog pLogger) {
    this.logger = pLogger;
  }

  /**
   * <p>Getter for srvDatabase.</p>
   * @return ISrvDatabase<RS>
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
}

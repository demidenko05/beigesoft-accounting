package org.beigesoft.accounting.factory;

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
import java.util.HashMap;

import org.beigesoft.persistable.IPersistableBase;
import org.beigesoft.factory.IFactorySimple;
import org.beigesoft.factory.IFactoryAppBeansByClass;
import org.beigesoft.factory.FactorySimple;
import org.beigesoft.factory.FactoryPersistableBase;
import org.beigesoft.service.ISrvDatabase;
import org.beigesoft.accounting.persistable.IDoc;
import org.beigesoft.accounting.persistable.AccountingEntries;

/**
 * <p>Entities factories factory.</p>
 *
 * @author Yury Demidenko
 */
public class FctBcFctSimpleAccEntities
  implements IFactoryAppBeansByClass<IFactorySimple<?>> {

  /**
   * <p>Converters map "object's class"-"object' s converter".</p>
   **/
  private final Map<Class<?>, IFactorySimple<?>>
    factoriesMap =
      new HashMap<Class<?>, IFactorySimple<?>>();

  /**
   * <p>Database service to get ID database.</p>
   **/
  private ISrvDatabase srvDatabase;

  /**
   * <p>Get bean in lazy mode (if bean is null then initialize it).</p>
   * @param pAddParam additional param
   * @param pBeanClass - bean name
   * @return requested bean
   * @throws Exception - an exception
   */
  @Override
  public final IFactorySimple<?> lazyGet(//NOPMD
    // Rule:DoubleCheckedLocking isn't true see in beige-common:
    // org.beigesoft.test.DoubleCkeckLockingWrApTest
    final Map<String, Object> pAddParam,
      final Class<?> pBeanClass) throws Exception {
    IFactorySimple<?> factory =
      this.factoriesMap.get(pBeanClass);
    if (factory == null) {
      // locking:
      synchronized (this.factoriesMap) {
        // make sure again whether it's null after locking:
        factory = this.factoriesMap.get(pBeanClass);
        if (AccountingEntries.class == pBeanClass) {
          factory = createPutFactoryAccountingEntries();
        } else if (IDoc.class.isAssignableFrom(pBeanClass)) {
          factory = createPutFactoryAccDoc(pBeanClass);
        } else if (IPersistableBase.class.isAssignableFrom(pBeanClass)) {
          factory = createPutFactoryPersistableBase(pBeanClass);
        } else {
          factory = createPutFactorySimple(pBeanClass);
        }
      }
    }
    return factory;
  }

  /**
   * <p>Set bean.</p>
   * @param pBeanClass - bean class
   * @param pBean bean
   * @throws Exception - an exception
   */
  @Override
  public final synchronized void set(final Class<?> pBeanClass,
    final IFactorySimple<?> pBean) throws Exception {
    this.factoriesMap.put(pBeanClass, pBean);
  }

  /**
   * <p>Get FactoryAccountingEntriesSimple (create and put into map).</p>
   * @return requested IFactoryAccountingEntriesSimple
   * @throws Exception - an exception
   */
  protected final FactoryAccountingEntries
    createPutFactoryAccountingEntries() throws Exception {
    FactoryAccountingEntries factory = new FactoryAccountingEntries();
    factory.setDatabaseId(this.srvDatabase.getIdDatabase());
    //assigning fully initialized object:
    this.factoriesMap
      .put(AccountingEntries.class, factory);
    return factory;
  }

  /**
   * <p>Get FactoryAccDocSimple (create and put into map).</p>
   * @param pBeanClass - bean class
   * @return requested IFactoryAccDocSimple
   * @throws Exception - an exception
   */
  protected final FactoryAccDoc
    createPutFactoryAccDoc(
      final Class<?> pBeanClass) throws Exception {
    FactoryAccDoc factory = new FactoryAccDoc();
    factory.setObjectClass(pBeanClass);
    factory.setDatabaseId(this.srvDatabase.getIdDatabase());
    //assigning fully initialized object:
    this.factoriesMap
      .put(pBeanClass, factory);
    return factory;
  }

  /**
   * <p>Get FactoryPersistableBaseSimple (create and put into map).</p>
   * @param pBeanClass - bean class
   * @return requested IFactoryPersistableBaseSimple
   * @throws Exception - an exception
   */
  protected final FactoryPersistableBase
    createPutFactoryPersistableBase(
      final Class<?> pBeanClass) throws Exception {
    FactoryPersistableBase factory = new FactoryPersistableBase();
    factory.setObjectClass(pBeanClass);
    factory.setDatabaseId(this.srvDatabase.getIdDatabase());
    //assigning fully initialized object:
    this.factoriesMap
      .put(pBeanClass, factory);
    return factory;
  }

  /**
   * <p>Get FactorySimple (create and put into map).</p>
   * @param pBeanClass - bean class
   * @return requested IFactorySimple
   * @throws Exception - an exception
   */
  protected final FactorySimple
    createPutFactorySimple(
      final Class<?> pBeanClass) throws Exception {
    FactorySimple factory = new FactorySimple();
    factory.setObjectClass(pBeanClass);
    //assigning fully initialized object:
    this.factoriesMap
      .put(pBeanClass, factory);
    return factory;
  }

  //Simple getters and setters:
  /**
   * <p>Getter for srvDatabase.</p>
   * @return ISrvDatabase
   **/
  public final ISrvDatabase getSrvDatabase() {
    return this.srvDatabase;
  }

  /**
   * <p>Setter for srvDatabase.</p>
   * @param pSrvDatabase reference
   **/
  public final void setSrvDatabase(final ISrvDatabase pSrvDatabase) {
    this.srvDatabase = pSrvDatabase;
  }
}

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
import java.util.Date;

import org.beigesoft.factory.IFactorySimple;
import org.beigesoft.accounting.persistable.IDoc;

/**
 * <pre>
 * Simple factory that create a request(or) scoped IDoc
 * by using reflection.
 * </pre>
 *
 * @author Yury Demidenko
 * @param <M> type of created bean
 **/
public class FactoryAccDoc<M extends IDoc>
  implements IFactorySimple<M> {

  /**
   * <p>Object class.</p>
   **/
  private Class<M> objectClass;

  /**
   * <p>ID Database.</p>
   **/
  private Integer databaseId;

  /**
   * <p>Create a bean.</p>
   * @param pAddParam additional param
   * @return M request(or) scoped bean
   * @throws Exception - an exception
   */
  @Override
  public final M create(final Map<String, Object> pAddParam) throws Exception {
    M object = this.objectClass.newInstance();
    object.setIsNew(false);
    object.setIdDatabaseBirth(this.databaseId);
    object.setItsDate(new Date());
    return object;
  }

  //Simple getters and setters:
  /**
   * <p>Getter for objectClass.</p>
   * @return Class<M>
   **/
  public final Class<M> getObjectClass() {
    return this.objectClass;
  }

  /**
   * <p>Setter for objectClass.</p>
   * @param pObjectClass reference
   **/
  public final void setObjectClass(final Class<M> pObjectClass) {
    this.objectClass = pObjectClass;
  }

  /**
   * <p>Getter for databaseId.</p>
   * @return Integer
   **/
  public final Integer getDatabaseId() {
    return this.databaseId;
  }

  /**
   * <p>Setter for databaseId.</p>
   * @param pDatabaseId reference
   **/
  public final void setDatabaseId(final Integer pDatabaseId) {
    this.databaseId = pDatabaseId;
  }
}

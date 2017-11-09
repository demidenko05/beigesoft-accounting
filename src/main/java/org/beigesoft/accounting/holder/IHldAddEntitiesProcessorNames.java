package org.beigesoft.accounting.holder;

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

/**
 * <p>Additional service that assign entities processor name for class
 * and action name e.g. for webstore.</p>
 *
 * @author Yury Demidenko
 */
public interface IHldAddEntitiesProcessorNames {

  /**
   * <p>Get processor name for copy.</p>
   * @param pClass a Class
   * @return a thing
   **/
  String getForCopy(Class<?> pClass);

  /**
   * <p>Get processor name for print.</p>
   * @param pClass a Class
   * @return a thing
   **/
  String getForPrint(Class<?> pClass);

  /**
   * <p>Get processor name for save.</p>
   * @param pClass a Class
   * @return a thing
   **/
  String getForSave(Class<?> pClass);

  /**
   * <p>Get processor name for FFOL delete.</p>
   * @param pClass a Class
   * @return a thing
   **/
  String getForFfolDelete(Class<?> pClass);

  /**
   * <p>Get processor name for FFOL save.</p>
   * @param pClass a Class
   * @return a thing
   **/
  String getForFfolSave(Class<?> pClass);

  /**
   * <p>Get processor name for FOL delete.</p>
   * @param pClass a Class
   * @return a thing
   **/
  String getForFolDelete(Class<?> pClass);

  /**
   * <p>Get processor name for FOL save.</p>
   * @param pClass a Class
   * @return a thing
   **/
  String getForFolSave(Class<?> pClass);

  /**
   * <p>Get processor name for delete.</p>
   * @param pClass a Class
   * @return a thing
   **/
  String getForDelete(Class<?> pClass);

  /**
   * <p>Get processor name for create.</p>
   * @param pClass a Class
   * @return a thing
   **/
  String getForCreate(Class<?> pClass);

  /**
   * <p>Get processor name for retrieve to edit/delete.</p>
   * @param pClass a Class
   * @return a thing
   **/
  String getForRetrieveForEditDelete(Class<?> pClass);
}

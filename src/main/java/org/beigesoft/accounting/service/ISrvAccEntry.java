package org.beigesoft.accounting.service;

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
import java.util.List;

import org.beigesoft.accounting.persistable.IDoc;
import org.beigesoft.accounting.persistable.AccountingEntry;

/**
 * <p>Business service for accounting entries.</p>
 *
 * @author Yury Demidenko
 */
public interface ISrvAccEntry {

  /**
   * <p>Make accounting entries for document.</p>
   * @param pAddParam additional param
   * @param pEntity a document
   * @throws Exception - an exception
   **/
  void makeEntries(Map<String, Object> pAddParam,
    IDoc pEntity) throws Exception;

  /**
   * <p>Make accounting entries for reversing document.</p>
   * @param pAddParam additional param
   * @param pReversing a reversing document
   * @param pReversed a reversed document
   * @throws Exception - an exception
   **/
  void reverseEntries(Map<String, Object> pAddParam,
    IDoc pReversing, IDoc pReversed) throws Exception;

  /**
   * <p>Retrieve accounting entries for document.</p>
   * @param pAddParam additional param
   * @param pEntity a document
   * @return accounting entries made by this document
   * @throws Exception - an exception
   **/
  List<AccountingEntry> retrieveAccEntriesFor(Map<String, Object> pAddParam,
    IDoc pEntity) throws Exception;


  /**
   * <p>Make accounting entries for all documents.
   * It find out date of first document that has no entries, then
   * make request for all documents since that date.
   * </p>
   * @param pAddParam additional param
   * @throws Exception - an exception
   **/
  void makeEntriesAll(Map<String, Object> pAddParam) throws Exception;
}

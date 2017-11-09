package org.beigesoft.accounting.model;

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

/**
 * <pre>
 * Entries SQL Source Type - DOCUMENT/DRAW_ITEM_ENTRY_BY_DOCUMENT/
 * DRAW_ITEM_ENTRY_BY_DOCUMENT_LINE.
 * This is to resolve dynamic filter for source ID,
 * e.g. where either PURCHASEINVOICE.ITSID=1 or DRAWINGOWNERID=1
 * </pre>
 *
 * @author Yury Demidenko
 */
 public enum EEntriesSourceType {

  /**
   * <p>SQL query for ordinal document.</p>
   **/
  DOCUMENT,

  /**
   * <p>SQL query for draw item entry that made by a document.</p>
   **/
  DRAW_ITEM_ENTRY_BY_DOCUMENT,

  /**
   * <p>SQL query for draw item entry that made by a document line.</p>
   **/
  DRAW_ITEM_ENTRY_BY_DOCUMENT_LINE;
}

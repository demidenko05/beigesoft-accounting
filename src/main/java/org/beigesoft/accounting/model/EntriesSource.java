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
 * Accounting Entries Source.
 * </pre>
 *
 * @author Yury Demidenko
 */
public class EntriesSource {

  /**
   * <p>SQL file name without extention "sql".</p>
   **/
  private String fileName;

  /**
   * <p>Entries Source Type.</p>
   **/
  private EEntriesSourceType entriesSourceType;

  /**
   * <p>Is it used in current method?</p>
   **/
  private EEntriesSourceType isUsed;

  //Simple getters and setters:
  /**
   * <p>Geter for fileName.</p>
   * @return String
   **/
  public final String getFileName() {
    return this.fileName;
  }

  /**
   * <p>Setter for fileName.</p>
   * @param pFileName reference/value
   **/
  public final void setFileName(final String pFileName) {
    this.fileName = pFileName;
  }

  /**
   * <p>Geter for entriesSourceType.</p>
   * @return EEntriesSourceType
   **/
  public final EEntriesSourceType getEntriesSourceType() {
    return this.entriesSourceType;
  }

  /**
   * <p>Setter for entriesSourceType.</p>
   * @param pEntriesSourceType reference/value
   **/
  public final void setEntriesSourceType(
    final EEntriesSourceType pEntriesSourceType) {
    this.entriesSourceType = pEntriesSourceType;
  }

  /**
   * <p>Geter for isUsed.</p>
   * @return EEntriesSourceType
   **/
  public final EEntriesSourceType getIsUsed() {
    return this.isUsed;
  }

  /**
   * <p>Setter for isUsed.</p>
   * @param pIsUsed reference/value
   **/
  public final void setIsUsed(final EEntriesSourceType pIsUsed) {
    this.isUsed = pIsUsed;
  }
}

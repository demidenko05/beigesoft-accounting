package org.beigesoft.accounting.persistable;

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

import org.beigesoft.persistable.AHasIdLongVersion;
import org.beigesoft.accounting.model.EEntriesSourceType;
import org.beigesoft.accounting.model.EEntriesAccountingType;

/**
 * <pre>
 * Accounting Entries Source Line.
 * Version changed time algorithm.
 * </pre>
 *
 * @author Yury Demidenko
 */
public class AccEntriesSourcesLine extends AHasIdLongVersion {

  /**
   * <p>Owner AccSettings, not Null.</p>
   **/
  private AccSettings itsOwner;

  /**
   * <p>Not Null, source type code e.g. 1 - PurchaseInvoice.</p>
   **/
  private Integer sourceType;

  /**
   * <p>SQL file name without extension "sql", unchangeable, not Null.</p>
   **/
  private String fileName;

  /**
   * <p>Source ID name, e.g. DRAWINGOWNERID or SALESINVOICE.ITSID,
   * unchangeable, not Null.</p>
   **/
  private String sourceIdName;

  /**
   * <p>Description.</p>
   **/
  private String description;

  /**
   * <p>Not Null, source set code for this sourceType,
   * e.g. "NoSalesTaxsubAccInvItemCategoryDebtorCreditor"
   * user can't choose sources with different setCode, unchangeable.</p>
   **/
  private String setCode;

  /**
   * <p>Is used in current method, not Null.</p>
   **/
  private Boolean isUsed;

  /**
   * <p>DOCUMENT/DRAW_ITEM_ENTRY_BY_DOCUMENT/
   * DRAW_ITEM_ENTRY_BY_DOCUMENT_LINE, not Null, unchangeable
   * This is to resolve dynamic filter for source ID,
   * e.g. where either PURCHASEINVOICE.ITSID=1 or DRAWINGOWNERID=1.</p>
   **/
  private EEntriesSourceType entriesSourceType;

  /**
   * <p>DEBIT/DEBITCREDIT/CREDIT, not Null, unchangeable.</p>
   **/
  private EEntriesAccountingType entriesAccountingType;

  //Simple getters and setters:
  /**
   * <p>Geter for itsOwner.</p>
   * @return AccSettings
   **/
  public final AccSettings getItsOwner() {
    return this.itsOwner;
  }

  /**
   * <p>Setter for itsOwner.</p>
   * @param pItsOwner reference
   **/
  public final void setItsOwner(final AccSettings pItsOwner) {
    this.itsOwner = pItsOwner;
  }

  /**
   * <p>Geter for sourceType.</p>
   * @return Integer
   **/
  public final Integer getSourceType() {
    return this.sourceType;
  }

  /**
   * <p>Setter for sourceType.</p>
   * @param pSourceType reference
   **/
  public final void setSourceType(final Integer pSourceType) {
    this.sourceType = pSourceType;
  }

  /**
   * <p>Geter for fileName.</p>
   * @return String
   **/
  public final String getFileName() {
    return this.fileName;
  }

  /**
   * <p>Setter for fileName.</p>
   * @param pFileName reference
   **/
  public final void setFileName(final String pFileName) {
    this.fileName = pFileName;
  }

  /**
   * <p>Getter for sourceIdName.</p>
   * @return String
   **/
  public final String getSourceIdName() {
    return this.sourceIdName;
  }

  /**
   * <p>Setter for sourceIdName.</p>
   * @param pSourceIdName reference
   **/
  public final void setSourceIdName(final String pSourceIdName) {
    this.sourceIdName = pSourceIdName;
  }

  /**
   * <p>Geter for description.</p>
   * @return String
   **/
  public final String getDescription() {
    return this.description;
  }

  /**
   * <p>Setter for description.</p>
   * @param pDescription reference
   **/
  public final void setDescription(final String pDescription) {
    this.description = pDescription;
  }

  /**
   * <p>Geter for setCode.</p>
   * @return String
   **/
  public final String getSetCode() {
    return this.setCode;
  }

  /**
   * <p>Setter for setCode.</p>
   * @param pSetCode reference
   **/
  public final void setSetCode(final String pSetCode) {
    this.setCode = pSetCode;
  }

  /**
   * <p>Geter for isUsed.</p>
   * @return Boolean
   **/
  public final Boolean getIsUsed() {
    return this.isUsed;
  }

  /**
   * <p>Setter for isUsed.</p>
   * @param pIsUsed reference
   **/
  public final void setIsUsed(final Boolean pIsUsed) {
    this.isUsed = pIsUsed;
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
   * @param pEntriesSourceType reference
   **/
  public final void setEntriesSourceType(
    final EEntriesSourceType pEntriesSourceType) {
    this.entriesSourceType = pEntriesSourceType;
  }

  /**
   * <p>Getter for entriesAccountingType.</p>
   * @return EEntriesAccountingType
   **/
  public final EEntriesAccountingType getEntriesAccountingType() {
    return this.entriesAccountingType;
  }

  /**
   * <p>Setter for entriesAccountingType.</p>
   * @param pEntriesAccountingType reference
   **/
  public final void setEntriesAccountingType(
    final EEntriesAccountingType pEntriesAccountingType) {
    this.entriesAccountingType = pEntriesAccountingType;
  }
}

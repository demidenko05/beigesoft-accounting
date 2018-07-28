package org.beigesoft.accounting.persistable;

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

import org.beigesoft.persistable.AHasNameIdLongVersion;
import org.beigesoft.persistable.CsvMethod;
import org.beigesoft.persistable.CsvColumn;

/**
 * <p>
 * Model of Bank CSV import method.
 * </p>
 *
 * @author Yury Demidenko
 */
public class BankCsvMethod extends AHasNameIdLongVersion {

  /**
   * <p>CSV Method, not null.</p>
   **/
  private CsvMethod csvMethod;

  /**
   * <p>Date CSV Column, not null. Example formats:
   * "dd/MM/yyyy", "MM/dd/yyyy", "MM-dd-yyyy"</p>
   **/
  private CsvColumn dateCol;

  /**
   * <p>Amount CSV Column, not null. Standard value is dot separated
   * number without group separators e.g. "11245.23", otherwise accepted
   * formats: "COMMA,SPACE" European standard - "11 245,45",
   * but in that case column value must be braced with text delimiter,
   * e.g. quotes; "COMMA,NONE" - "11245,45". All other separators should be
   * original, i.e. dot is ".", e.g. ".,NONE" is default format</p>
   **/
  private CsvColumn amountCol;

  /**
   * <p>If used, description CSV Column.</p>
   **/
  private CsvColumn descriptionCol;

  /**
   * <p>If used, status CSV Column,
   * column that contains of CANCELED or NOT information.</p>
   **/
  private CsvColumn statusCol;

  /**
   * <p>if used, comma separated words that mean that entry was ACCEPTED,
   * e.g. "OK,ACCEPTED" or single value "true".</p>
   **/
  private String acceptedWords;

  /**
   * <p>if used, comma separated words that mean that entry was CANCELED,
   * e.g. "VOIDED,CANCELED" or single value "false".</p>
   **/
  private String voidedWords;

  //Simple getters and setters:
  /**
   * <p>Getter for csvMethod.</p>
   * @return CsvMethod
   **/
  public final CsvMethod getCsvMethod() {
    return this.csvMethod;
  }

  /**
   * <p>Setter for csvMethod.</p>
   * @param pCsvMethod reference
   **/
  public final void setCsvMethod(final CsvMethod pCsvMethod) {
    this.csvMethod = pCsvMethod;
  }

  /**
   * <p>Getter for dateCol.</p>
   * @return CsvColumn
   **/
  public final CsvColumn getDateCol() {
    return this.dateCol;
  }

  /**
   * <p>Setter for dateCol.</p>
   * @param pDateCol reference
   **/
  public final void setDateCol(final CsvColumn pDateCol) {
    this.dateCol = pDateCol;
  }

  /**
   * <p>Getter for amountCol.</p>
   * @return CsvColumn
   **/
  public final CsvColumn getAmountCol() {
    return this.amountCol;
  }

  /**
   * <p>Setter for amountCol.</p>
   * @param pAmountCol reference
   **/
  public final void setAmountCol(final CsvColumn pAmountCol) {
    this.amountCol = pAmountCol;
  }

  /**
   * <p>Getter for descriptionCol.</p>
   * @return CsvColumn
   **/
  public final CsvColumn getDescriptionCol() {
    return this.descriptionCol;
  }

  /**
   * <p>Setter for descriptionCol.</p>
   * @param pDescriptionCol reference
   **/
  public final void setDescriptionCol(final CsvColumn pDescriptionCol) {
    this.descriptionCol = pDescriptionCol;
  }

  /**
   * <p>Getter for statusCol.</p>
   * @return CsvColumn
   **/
  public final CsvColumn getStatusCol() {
    return this.statusCol;
  }

  /**
   * <p>Setter for statusCol.</p>
   * @param pStatusCol reference
   **/
  public final void setStatusCol(final CsvColumn pStatusCol) {
    this.statusCol = pStatusCol;
  }

  /**
   * <p>Getter for acceptedWords.</p>
   * @return String
   **/
  public final String getAcceptedWords() {
    return this.acceptedWords;
  }

  /**
   * <p>Setter for acceptedWords.</p>
   * @param pAcceptedWords reference
   **/
  public final void setAcceptedWords(final String pAcceptedWords) {
    this.acceptedWords = pAcceptedWords;
  }

  /**
   * <p>Getter for voidedWords.</p>
   * @return String
   **/
  public final String getVoidedWords() {
    return this.voidedWords;
  }

  /**
   * <p>Setter for voidedWords.</p>
   * @param pVoidedWords reference
   **/
  public final void setVoidedWords(final String pVoidedWords) {
    this.voidedWords = pVoidedWords;
  }
}

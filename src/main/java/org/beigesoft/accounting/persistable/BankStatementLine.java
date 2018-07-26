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

import java.util.Date;
import java.math.BigDecimal;

import org.beigesoft.model.IOwned;
import org.beigesoft.persistable.APersistableBase;
import org.beigesoft.accounting.model.EBankEntryStatus;
import org.beigesoft.accounting.model.EBankEntryResultType;

/**
 * <p>
 * Model of Bank Statement Line imported from CSV.
 * </p>
 *
 * @author Yury Demidenko
 */
public class BankStatementLine extends APersistableBase
  implements IOwned<BankStatement> {

  /**
   * <p>Owner.</p>
   **/
  private BankStatement itsOwner;
  /**
   * <p>Date.</p>
   **/
  private Date itsDate;

  /**
   * <p>Amount.</p>
   **/
  private BigDecimal itsAmount;

  /**
   * <p>from CSV if present, read only field, if statusCol present,
   * then string value present here, read only field.</p>
   **/
  private String descriptionStatus;

  /**
   * <p>ACCEPTED default or from CSV according settings, read only field.</p>
   **/
  private EBankEntryStatus itsStatus = EBankEntryStatus.ACCEPTED;

  /**
   * <p>if found, read only field.</p>
   **/
  private String matchingRecords;

  /**
   * <p>if action was made, read only field,
   * e.g. "was made PaymentTo#12665".</p>
   **/
  private String resultAction;

  /**
   * <p>if made, read only field.</p>
   **/
  private Long resultRecordId;

  /**
   * <p>if made, read only field.</p>
   **/
  private EBankEntryResultType resultRecordType;

  /**
   * <p>addition description created by user,
   * e.g. "made mistake, see adjusting entry #123".</p>
   **/
  private String descriptionAdd;

  /**
   * <p>Getter for itsOwner.</p>
   * @return BankStatement
   **/
  @Override
  public final BankStatement getItsOwner() {
    return this.itsOwner;
  }

  /**
   * <p>Setter for itsOwner.</p>
   * @param pItsOwner reference
   **/
  @Override
  public final void setItsOwner(final BankStatement pItsOwner) {
    this.itsOwner = pItsOwner;
  }

  //Simple getters and setters:
  /**
   * <p>Getter for itsDate.</p>
   * @return Date
   **/
  public final Date getItsDate() {
    return this.itsDate;
  }

  /**
   * <p>Setter for itsDate.</p>
   * @param pItsDate reference
   **/
  public final void setItsDate(final Date pItsDate) {
    this.itsDate = pItsDate;
  }

  /**
   * <p>Getter for itsAmount.</p>
   * @return BigDecimal
   **/
  public final BigDecimal getItsAmount() {
    return this.itsAmount;
  }

  /**
   * <p>Setter for itsAmount.</p>
   * @param pItsAmount reference
   **/
  public final void setItsAmount(final BigDecimal pItsAmount) {
    this.itsAmount = pItsAmount;
  }

  /**
   * <p>Getter for descriptionStatus.</p>
   * @return String
   **/
  public final String getDescriptionStatus() {
    return this.descriptionStatus;
  }

  /**
   * <p>Setter for descriptionStatus.</p>
   * @param pDescriptionStatus reference
   **/
  public final void setDescriptionStatus(final String pDescriptionStatus) {
    this.descriptionStatus = pDescriptionStatus;
  }

  /**
   * <p>Getter for itsStatus.</p>
   * @return EBankEntryStatus
   **/
  public final EBankEntryStatus getItsStatus() {
    return this.itsStatus;
  }

  /**
   * <p>Setter for itsStatus.</p>
   * @param pItsStatus reference
   **/
  public final void setItsStatus(final EBankEntryStatus pItsStatus) {
    this.itsStatus = pItsStatus;
  }

  /**
   * <p>Getter for matchingRecords.</p>
   * @return String
   **/
  public final String getMatchingRecords() {
    return this.matchingRecords;
  }

  /**
   * <p>Setter for matchingRecords.</p>
   * @param pMatchingRecords reference
   **/
  public final void setMatchingRecords(final String pMatchingRecords) {
    this.matchingRecords = pMatchingRecords;
  }

  /**
   * <p>Getter for resultAction.</p>
   * @return String
   **/
  public final String getResultAction() {
    return this.resultAction;
  }

  /**
   * <p>Setter for resultAction.</p>
   * @param pResultAction reference
   **/
  public final void setResultAction(final String pResultAction) {
    this.resultAction = pResultAction;
  }

  /**
   * <p>Getter for resultRecordId.</p>
   * @return Long
   **/
  public final Long getResultRecordId() {
    return this.resultRecordId;
  }

  /**
   * <p>Setter for resultRecordId.</p>
   * @param pResultRecordId reference
   **/
  public final void setResultRecordId(final Long pResultRecordId) {
    this.resultRecordId = pResultRecordId;
  }

  /**
   * <p>Getter for resultRecordType.</p>
   * @return EBankEntryResultType
   **/
  public final EBankEntryResultType getResultRecordType() {
    return this.resultRecordType;
  }

  /**
   * <p>Setter for resultRecordType.</p>
   * @param pResultRecordType reference
   **/
  public final void setResultRecordType(
    final EBankEntryResultType pResultRecordType) {
    this.resultRecordType = pResultRecordType;
  }

  /**
   * <p>Getter for descriptionAdd.</p>
   * @return String
   **/
  public final String getDescriptionAdd() {
    return this.descriptionAdd;
  }

  /**
   * <p>Setter for descriptionAdd.</p>
   * @param pDescriptionAdd reference
   **/
  public final void setDescriptionAdd(final String pDescriptionAdd) {
    this.descriptionAdd = pDescriptionAdd;
  }
}

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

import java.util.List;
import java.util.Date;
import java.math.BigDecimal;

import org.beigesoft.model.IHasTypeCode;
import org.beigesoft.persistable.APersistableBaseVersion;

/**
 * <pre>
 * Model of Input Accounting entries.
 * Version changed time algorithm.
 * </pre>
 *
 * @author Yury Demidenko
 */
public class AccountingEntries extends APersistableBaseVersion
 implements IHasTypeCode {

  /**
   * <p>Date.</p>
   **/
  private Date itsDate;

  /**
   * <p>Total debit.</p>
   **/
  private BigDecimal totalDebit = new BigDecimal("0.00");

  /**
   * <p>Total credit.</p>
   **/
  private BigDecimal totalCredit = new BigDecimal("0.00");

  /**
   * <p>Lines.</p>
   **/
  private List<AccountingEntry> itsLines;

  /**
   * <p>Description.</p>
   **/
  private String description;

  /**
   * <p>Constant of code type.</p>
   * @return 3
   **/
  public final Integer constTypeCode() {
    return 3;
  }

  //Hiding references getters and setters:
  /**
   * <p>Geter for itsDate.</p>
   * @return Date
   **/
  public final Date getItsDate() {
    if (this.itsDate == null) {
      return null;
    }
    return new Date(this.itsDate.getTime());
  }

  /**
   * <p>Setter for itsDate.</p>
   * @param pItsDate reference
   **/
  public final void setItsDate(final Date pItsDate) {
    if (pItsDate == null) {
      this.itsDate = null;
    } else {
      this.itsDate = new Date(pItsDate.getTime());
    }
  }

  //Simple getters and setters:
  /**
   * <p>Getter for totalDebit.</p>
   * @return BigDecimal
   **/
  public final BigDecimal getTotalDebit() {
    return this.totalDebit;
  }

  /**
   * <p>Setter for totalDebit.</p>
   * @param pTotalDebit reference
   **/
  public final void setTotalDebit(final BigDecimal pTotalDebit) {
    this.totalDebit = pTotalDebit;
  }

  /**
   * <p>Geter for totalCredit.</p>
   * @return BigDecimal
   **/
  public final BigDecimal getTotalCredit() {
    return this.totalCredit;
  }

  /**
   * <p>Setter for totalCredit.</p>
   * @param pTotalCredit reference
   **/
  public final void setTotalCredit(final BigDecimal pTotalCredit) {
    this.totalCredit = pTotalCredit;
  }

  /**
   * <p>Geter for itsLines.</p>
   * @return List<AccountingEntry>
   **/
  public final List<AccountingEntry> getItsLines() {
    return this.itsLines;
  }

  /**
   * <p>Setter for itsLines.</p>
   * @param pItsLines reference
   **/
  public final void setItsLines(final List<AccountingEntry> pItsLines) {
    this.itsLines = pItsLines;
  }

  /**
   * <p>Getter for description.</p>
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
}

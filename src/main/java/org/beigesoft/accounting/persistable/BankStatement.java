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

import java.util.List;
import java.util.Date;

import org.beigesoft.persistable.APersistableBase;

/**
 * <p>
 * Model of Bank Statement imported from CSV.
 * </p>
 *
 * @author Yury Demidenko
 */
public class BankStatement extends APersistableBase {

  /**
   * <p>Date.</p>
   **/
  private Date itsDate;

  /**
   * <p>Bank account.</p>
   **/
  private BankAccount bankAccount;

  /**
   * <p>CSV file name + BankCsvMethod name.</p>
   **/
  private String sourceName;

  /**
   * <p>Lines.</p>
   **/
  private List<BankStatementLine> itsLines;

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
   * <p>Getter for bankAccount.</p>
   * @return BankAccount
   **/
  public final BankAccount getBankAccount() {
    return this.bankAccount;
  }

  /**
   * <p>Setter for bankAccount.</p>
   * @param pBankAccount reference
   **/
  public final void setBankAccount(final BankAccount pBankAccount) {
    this.bankAccount = pBankAccount;
  }

  /**
   * <p>Getter for sourceName.</p>
   * @return String
   **/
  public final String getSourceName() {
    return this.sourceName;
  }

  /**
   * <p>Setter for sourceName.</p>
   * @param pSourceName reference
   **/
  public final void setSourceName(final String pSourceName) {
    this.sourceName = pSourceName;
  }

  /**
   * <p>Getter for itsLines.</p>
   * @return List<BankStatementLine>
   **/
  public final List<BankStatementLine> getItsLines() {
    return this.itsLines;
  }

  /**
   * <p>Setter for itsLines.</p>
   * @param pItsLines reference
   **/
  public final void setItsLines(final List<BankStatementLine> pItsLines) {
    this.itsLines = pItsLines;
  }
}

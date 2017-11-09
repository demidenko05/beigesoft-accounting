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

import org.beigesoft.accounting.model.EAccountType;
import org.beigesoft.accounting.model.ENormalBalanceType;
import org.beigesoft.persistable.AHasNameIdStringVersion;

/**
 * <pre>
 * Model of account.
 * </pre>
 *
 * @author Yury Demidenko
 */
 public class Account extends AHasNameIdStringVersion {

  /**
   * <p>Number.</p>
   **/
  private String itsNumber;

  /**
   * <p>If used in current method,
   * e.g. Sales Tax Payable not used if you are not
   * Sales Tax Vendor (no selling taxable goods and services).</p>
   **/
  private Boolean isUsed = false;

  /**
   * <p>Account Normal Balance Type - DEBIT, CREDIT.</p>
   **/
  private ENormalBalanceType normalBalanceType;

  /**
   * <p>EAccountType.ASSET/LIABILITY/OWNERS_EQUITY/
   * GROSS_INCOME_REVENUE/GROSS_INCOME_EXPENSE.</p>
   **/
  private EAccountType itsType;

  /**
   * <p>Subacccount type, e.g. 2001 - InvItemCategory,
   * 2002 - InvItem, 2003 - Tax, 2004 - DebtorCreditor.
   * This is constant [entity].ITS_TYPE_CODE.</p>
   **/
  private Integer subaccType;

  /**
   * <p>If account created programmatically then  user can't delete
   * or change its main fields (ID, type, normal balance type).</p>
   **/
  private Boolean isCreatedByUser = true;

  /**
   * <p>Description.</p>
   **/
  private String description;

  /**
   * <p>List of existed subaccounts.</p>
   **/
  private List<SubaccountLine> subaccounts;

  //Simple getters and setters:

  /**
   * <p>Geter for itsNumber.</p>
   * @return String
   **/
  public final String getItsNumber() {
    return this.itsNumber;
  }

  /**
   * <p>Setter for itsNumber.</p>
   * @param pItsNumber reference
   **/
  public final void setItsNumber(final String pItsNumber) {
    this.itsNumber = pItsNumber;
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
   * <p>Geter for normalBalanceType.</p>
   * @return ENormalBalanceType
   **/
  public final ENormalBalanceType getNormalBalanceType() {
    return this.normalBalanceType;
  }

  /**
   * <p>Setter for normalBalanceType.</p>
   * @param pNormalBalanceType reference
   **/
  public final void setNormalBalanceType(
    final ENormalBalanceType pNormalBalanceType) {
    this.normalBalanceType = pNormalBalanceType;
  }

  /**
   * <p>Geter for itsType.</p>
   * @return EAccountType
   **/
  public final EAccountType getItsType() {
    return this.itsType;
  }

  /**
   * <p>Setter for itsType.</p>
   * @param pItsType reference
   **/
  public final void setItsType(final EAccountType pItsType) {
    this.itsType = pItsType;
  }

  /**
   * <p>Geter for subaccType.</p>
   * @return Integer
   **/
  public final Integer getSubaccType() {
    return this.subaccType;
  }

  /**
   * <p>Setter for subaccType.</p>
   * @param pSubaccType reference
   **/
  public final void setSubaccType(final Integer pSubaccType) {
    this.subaccType = pSubaccType;
  }

  /**
   * <p>Getter for isCreatedByUser.</p>
   * @return Boolean
   **/
  public final Boolean getIsCreatedByUser() {
    return this.isCreatedByUser;
  }

  /**
   * <p>Setter for isCreatedByUser.</p>
   * @param pIsCreatedByUser reference
   **/
  public final void setIsCreatedByUser(final Boolean pIsCreatedByUser) {
    this.isCreatedByUser = pIsCreatedByUser;
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
   * <p>Getter for subaccounts.</p>
   * @return List<SubaccountLine>
   **/
  public final List<SubaccountLine> getSubaccounts() {
    return this.subaccounts;
  }

  /**
   * <p>Setter for subaccounts.</p>
   * @param pSubaccounts reference
   **/
  public final void setSubaccounts(final List<SubaccountLine> pSubaccounts) {
    this.subaccounts = pSubaccounts;
  }
}

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

import org.beigesoft.model.IOwned;
import org.beigesoft.persistable.AHasIdLongVersion;

/**
 * <pre>
 * Model of subaccount line in account.
 * Version changed time algorithm.
 * </pre>
 *
 * @author Yury Demidenko
 */
public class SubaccountLine extends AHasIdLongVersion
  implements IOwned<Account> {

  /**
   * <p>Account.</p>
   **/
  private Account itsOwner;

  /**
   * <p>Subaccount type, not null, must be same as owner's one.</p>
   **/
  private Integer subaccType;

  /**
   * <p>Subaccount ID, not null.</p>
   **/
  private Long subaccId;

  /**
   * <p>Subaccount name, not null.</p>
   **/
  private String subaccName;

  /**
   * <p>Getter for itsOwner.</p>
   * @return Account
   **/
  @Override
  public final Account getItsOwner() {
    return this.itsOwner;
  }

  /**
   * <p>Setter for itsOwner.</p>
   * @param pItsOwner reference
   **/
  @Override
  public final void setItsOwner(final Account pItsOwner) {
    this.itsOwner = pItsOwner;
  }

  //Simple getters and setters:
  /**
   * <p>Getter for subaccType.</p>
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
   * <p>Getter for subaccId.</p>
   * @return Integer
   **/
  public final Long getSubaccId() {
    return this.subaccId;
  }

  /**
   * <p>Setter for subaccId.</p>
   * @param pSubaccId reference
   **/
  public final void setSubaccId(final Long pSubaccId) {
    this.subaccId = pSubaccId;
  }

  /**
   * <p>Getter for subaccName.</p>
   * @return String
   **/
  public final String getSubaccName() {
    return this.subaccName;
  }

  /**
   * <p>Setter for subaccName.</p>
   * @param pSubaccName reference
   **/
  public final void setSubaccName(final String pSubaccName) {
    this.subaccName = pSubaccName;
  }
}

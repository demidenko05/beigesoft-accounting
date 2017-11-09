package org.beigesoft.replicator.persistable.base;

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
import org.beigesoft.replicator.persistable.ReplicationAccMethod;
import org.beigesoft.persistable.APersistableBase;
import org.beigesoft.accounting.persistable.Account;

/**
 * <pre>
 * Model of Replication Account Debit/Credit avoid filter Method.
 * Database replication from tax to market accounting specification #1.
 * </pre>
 *
 * @author Yury Demidenko
 */
public abstract class AReplExcludeAccountsDebitCredit extends APersistableBase
  implements IOwned<ReplicationAccMethod> {

  /**
   * <p>Replication Method.</p>
   **/
  private ReplicationAccMethod itsOwner;

  /**
   * <p>Account, Not Null.</p>
   **/
  private Account account;

  /**
   * <p>Description.</p>
   **/
  private String description;

  /**
   * <p>Getter for itsOwner.</p>
   * @return ReplicationAccMethod
   **/
  @Override
  public final ReplicationAccMethod getItsOwner() {
    return this.itsOwner;
  }

  /**
   * <p>Setter for itsOwner.</p>
   * @param pItsOwner reference
   **/
  @Override
  public final void setItsOwner(final ReplicationAccMethod pItsOwner) {
    this.itsOwner = pItsOwner;
  }

  //Simple getters and setters:
  /**
   * <p>Getter for account.</p>
   * @return Account
   **/
  public final Account getAccount() {
    return this.account;
  }

  /**
   * <p>Setter for account.</p>
   * @param pAccount reference
   **/
  public final void setAccount(final Account pAccount) {
    this.account = pAccount;
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
}

package org.beigesoft.replicator.persistable;

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

/**
 * <pre>
 * Model of Replication Method with exclude accounts filter.
 * Database replication from tax to market accounting specification #1.
 * </pre>
 *
 * @author Yury Demidenko
 */
public class ReplicationAccMethod extends AReplicationMethod {

  /**
   * <p>Exclude accounting entries with debits.</p>
   **/
  private List<ReplExcludeAccountsDebit> excludeDebitAccounts;

  /**
   * <p>Exclude accounting entries with credits.</p>
   **/
  private List<ReplExcludeAccountsCredit> excludeCreditAccounts;

  //Simple getters and setters:
  /**
   * <p>Getter for excludeDebitAccounts.</p>
   * @return List<ReplExcludeAccountsDebit>
   **/
  public final List<ReplExcludeAccountsDebit> getExcludeDebitAccounts() {
    return this.excludeDebitAccounts;
  }

  /**
   * <p>Setter for excludeDebitAccounts.</p>
   * @param pExcludeDebitAccounts reference
   **/
  public final void setExcludeDebitAccounts(
    final List<ReplExcludeAccountsDebit> pExcludeDebitAccounts) {
    this.excludeDebitAccounts = pExcludeDebitAccounts;
  }

  /**
   * <p>Getter for excludeCreditAccounts.</p>
   * @return List<ReplExcludeAccountsCredit>
   **/
  public final List<ReplExcludeAccountsCredit> getExcludeCreditAccounts() {
    return this.excludeCreditAccounts;
  }

  /**
   * <p>Setter for excludeCreditAccounts.</p>
   * @param pExcludeCreditAccounts reference
   **/
  public final void setExcludeCreditAccounts(
    final List<ReplExcludeAccountsCredit> pExcludeCreditAccounts) {
    this.excludeCreditAccounts = pExcludeCreditAccounts;
  }
}

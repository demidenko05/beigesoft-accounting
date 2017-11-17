package org.beigesoft.replicator.filter;

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

import java.util.Map;
import java.util.Date;

import org.beigesoft.delegate.IDelegator;
import org.beigesoft.delegate.IDelegateEvalExt;
import org.beigesoft.handler.IHandlerModelChanged;
import org.beigesoft.accounting.persistable.AccountingEntry;
import org.beigesoft.replicator.persistable.ReplicationAccMethod;
import org.beigesoft.replicator.persistable.ReplExcludeAccountsCredit;
import org.beigesoft.replicator.persistable.ReplExcludeAccountsDebit;
import org.beigesoft.exception.ExceptionWithCode;
import org.beigesoft.service.ISrvOrm;

/**
 * <p>Interactive filter of accounting entries.
 * User can elect accounts to avoid they replication.
 * It also prepares database after import.
 * Database replication from tax to market accounting specification #1.
 * It's untransactional service. Transaction must be started.</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class FilterAvoidAccDebtCredit<RS> implements IFilterEntities,
  IHandlerModelChanged<ReplicationAccMethod>, IDelegateEvalExt<Date>,
    IDelegator {

  /**
   * <p>ORM service.</p>
   **/
  private ISrvOrm<RS> srvOrm;

  /**
   * <p>Usually FilterPersistableBaseImmutable.</p>
   **/
  private IFilterEntities filterId;

  /**
   * <p>Replication Method.</p>
   **/
  private ReplicationAccMethod replicationMethod;

  /**
   * <p>
   * Interactive filter of accounting entries.
   * </p>
   * @param pEntityClass Entity Class
   * @param pAddParam additional params (must present requestedDatabaseId
   * and replicationMethodId of String type (WEB parameters))
   * @return filter e.g. "((ITSID>0 and IDDATABASEBIRTH=2135)
   * and ((ACCDEBIT is null or ACCDEBIT not in ('BadDebts'))
   * and (ACCCREDIT is null or ACCCREDIT not in ('BadDebts','Property'))))"
   * @throws Exception - an exception
   **/
  @Override
  public final String makeFilter(final Class<?> pEntityClass,
    final Map<String, Object> pAddParam) throws Exception {
    if (!AccountingEntry.class.isAssignableFrom(pEntityClass)) {
      throw new ExceptionWithCode(ExceptionWithCode.CONFIGURATION_MISTAKE,
        "This class not descendant of AccountingEntry: "
          + pEntityClass);
    }
    lazyEvalReplicationMethod(pAddParam);
    StringBuffer filterAvoidAccDbCr = new StringBuffer("");
    if (this.replicationMethod.getExcludeDebitAccounts().size() > 0) {
      filterAvoidAccDbCr.append(" and ((ACCDEBIT is null or ACCDEBIT not in (");
      boolean isFirst = true;
      for (ReplExcludeAccountsDebit repExclAccDb
        : this.replicationMethod.getExcludeDebitAccounts()) {
        if (isFirst) {
          isFirst = false;
        } else {
          filterAvoidAccDbCr.append(",");
        }
        filterAvoidAccDbCr.append("'" + repExclAccDb.getAccount()
          .getItsId() + "'");
      }
    }
    if (this.replicationMethod.getExcludeCreditAccounts().size() > 0) {
      if (this.replicationMethod.getExcludeDebitAccounts().size() > 0) {
        filterAvoidAccDbCr
          .append(")) and (ACCCREDIT is null or ACCCREDIT not in (");
      } else {
        filterAvoidAccDbCr
          .append(" and (ACCCREDIT is null or ACCCREDIT not in (");
      }
      boolean isFirst = true;
      for (ReplExcludeAccountsCredit repExclAccCr
        : this.replicationMethod.getExcludeCreditAccounts()) {
        if (isFirst) {
          isFirst = false;
        } else {
          filterAvoidAccDbCr.append(",");
        }
        filterAvoidAccDbCr.append("'" + repExclAccCr.getAccount()
          .getItsId() + "'");
      }
    }
    if (this.replicationMethod.getExcludeDebitAccounts().size() > 0
      && this.replicationMethod.getExcludeCreditAccounts().size() > 0) {
      filterAvoidAccDbCr.append(")))");
    } else if (this.replicationMethod.getExcludeDebitAccounts().size() > 0
      || this.replicationMethod.getExcludeCreditAccounts().size() > 0) {
      filterAvoidAccDbCr.append("))");
    }
    if (this.replicationMethod.getExcludeDebitAccounts().size() > 0
      || this.replicationMethod.getExcludeCreditAccounts().size() > 0) {
      return "(" + this.filterId.makeFilter(pEntityClass, pAddParam)
        + filterAvoidAccDbCr.toString() + ")";
    }
    return this.filterId.makeFilter(pEntityClass, pAddParam);
  }


  /**
   * <p>Handle model changed event.</p>
   * @param pModel which changed
   **/
  @Override
  public final void handleModelChanged(final ReplicationAccMethod pModel) {
    if (this.replicationMethod != null
      && this.replicationMethod.getItsId().equals(pModel.getItsId())) {
      this.replicationMethod = null;
    }
  }

  /**
   * <p>Evaluate (retrieve) model.</p>
   * @param pAddParam additional params, (must present
   * replicationMethodId of String type (WEB parameters)).
   * @throws Exception - an exception
   * @return evaluated data
   **/
  @Override
  public final Date evalData(
    final Map<String, Object> pAddParam) throws Exception {
    lazyEvalReplicationMethod(pAddParam);
    if (this.replicationMethod.getLastDateReplication() == null) {
      return new Date(1L);
    }
    return this.replicationMethod.getLastDateReplication();
  }

  /**
   * <p>It prepares database after import.</p>
   * @param pAddParam additional params
   * @throws Exception - an exception
   **/
  @Override
  public final void make(
    final Map<String, Object> pAddParam) throws Exception {
    this.replicationMethod.setLastDateReplication(new Date());
    getSrvOrm().updateEntity(pAddParam, this.replicationMethod);
  }

  //Utils:
  /**
   * <p>Lazy Evaluate Replication Method.</p>
   * @param pAddParam additional params, (must present
   * replicationMethodId of String type (WEB parameters)).
   * @throws Exception - an exception
   **/
  public final void lazyEvalReplicationMethod(
    final Map<String, Object> pAddParam) throws Exception {
    Long replicationMethodId;
    try {
      replicationMethodId = Long.parseLong(pAddParam
        .get("replicationMethodId").toString());
    } catch (Exception e) {
      throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
        "Wrong or missing parameter replicationMethodId (in pAddParam): "
          + pAddParam.get("replicationMethodId"));
    }
    if (this.replicationMethod == null || !this.replicationMethod
      .getItsId().equals(replicationMethodId)) {
      this.replicationMethod = new ReplicationAccMethod();
      this.replicationMethod.setItsId(replicationMethodId);
      this.replicationMethod = getSrvOrm()
        .retrieveEntity(pAddParam, this.replicationMethod);
      ReplExcludeAccountsDebit ead = new ReplExcludeAccountsDebit();
      ead.setItsOwner(this.replicationMethod);
      this.replicationMethod.setExcludeDebitAccounts(getSrvOrm()
        .retrieveListForField(pAddParam, ead, "itsOwner"));
      ReplExcludeAccountsCredit eac = new ReplExcludeAccountsCredit();
      eac.setItsOwner(this.replicationMethod);
      this.replicationMethod.setExcludeCreditAccounts(getSrvOrm()
        .retrieveListForField(pAddParam, eac, "itsOwner"));
    }
  }

  //Simple getters and setters:
  /**
   * <p>Getter for srvOrm.</p>
   * @return ISrvOrm<RS>
   **/
  public final ISrvOrm<RS> getSrvOrm() {
    return this.srvOrm;
  }

  /**
   * <p>Setter for srvOrm.</p>
   * @param pSrvOrm reference
   **/
  public final void setSrvOrm(final ISrvOrm<RS> pSrvOrm) {
    this.srvOrm = pSrvOrm;
  }

  /**
   * <p>Getter for filterId.</p>
   * @return IFilterEntities
   **/
  public final IFilterEntities getFilterId() {
    return this.filterId;
  }

  /**
   * <p>Setter for filterId.</p>
   * @param pFilterId reference
   **/
  public final void setFilterId(
    final IFilterEntities pFilterId) {
    this.filterId = pFilterId;
  }

  /**
   * <p>Getter for replicationMethod.</p>
   * @return ReplicationAccMethod
   **/
  public final ReplicationAccMethod getReplicationAccMethod() {
    return this.replicationMethod;
  }

  /**
   * <p>Setter for replicationMethod.</p>
   * @param pReplicationAccMethod reference
   **/
  public final void setReplicationAccMethod(
    final ReplicationAccMethod pReplicationAccMethod) {
    this.replicationMethod = pReplicationAccMethod;
  }
}

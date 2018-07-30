package org.beigesoft.accounting.processor;

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

import java.util.Map;
import java.util.List;
import java.util.Date;
import java.util.Calendar;
import java.util.Locale;
import java.math.BigDecimal;

import org.beigesoft.exception.ExceptionWithCode;
import org.beigesoft.model.IRequestData;
import org.beigesoft.service.IEntityProcessor;
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.accounting.model.EBankEntryStatus;
import org.beigesoft.accounting.persistable.BankStatementLine;
import org.beigesoft.accounting.persistable.PaymentFrom;
import org.beigesoft.accounting.persistable.PrepaymentFrom;
import org.beigesoft.accounting.persistable.PaymentTo;
import org.beigesoft.accounting.persistable.PrepaymentTo;
import org.beigesoft.accounting.persistable.AccountingEntry;

/**
 * <p>Service that retrieve BSL, check if it foreign
 * and put into request data for farther editing.</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class PrcBankStatementLineGfe<RS>
  implements IEntityProcessor<BankStatementLine, Long> {

  /**
   * <p>Acc-EntityPb Edit/Confirm delete delegator.</p>
   **/
  private IEntityProcessor<BankStatementLine, Long> prcEntityPbEditDelete;

  /**
   * <p>ORM service.</p>
   **/
  private ISrvOrm<RS> srvOrm;

  /**
   * <p>Process entity request.</p>
   * @param pAddParam additional param, e.g. return this line's
   * document in "nextEntity" for farther process
   * @param pRequestData Request Data
   * @param pEntity Entity to process
   * @return Entity processed for farther process or null
   * @throws Exception - an exception
   **/
  @Override
  public final BankStatementLine process(final Map<String, Object> pAddParam,
    final BankStatementLine pEntity,
      final IRequestData pRequestData) throws Exception {
    BankStatementLine bsl = this.prcEntityPbEditDelete
      .process(pAddParam, pEntity, pRequestData);
    if (bsl.getResultAction() != null) {
      throw new ExceptionWithCode(ExceptionWithCode.FORBIDDEN,
        "attempt_to_edit_completed_bank_statement_line");
    }
    String amountStr;
    if (bsl.getItsAmount().compareTo(BigDecimal.ZERO) > 0) {
      amountStr = bsl.getItsAmount().toString();
    } else if (bsl.getItsAmount().compareTo(BigDecimal.ZERO) < 0) {
      amountStr = bsl.getItsAmount().negate().toString();
    } else {
      throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
        "amount_is_zero");
    }
    long[] startEnd = evalDayStartEndFor(pEntity.getItsDate());
    String whereReversed;
    if (EBankEntryStatus.VOIDED.equals(bsl.getItsStatus())) {
      whereReversed = "";
    } else {
      whereReversed = " and REVERSEDID is null";
    }
    String dWhere =
      "where HASMADEACCENTRIES=1 and ITSTOTAL="
        + amountStr + whereReversed + " and ITSDATE >= " + startEnd[0]
          + " and ITSDATE <= " + startEnd[1];
    if (bsl.getItsAmount().compareTo(BigDecimal.ZERO) > 0) {
      //bank account debit
      List<PrepaymentFrom> prepaymentsFrom = getSrvOrm()
        .retrieveListWithConditions(pAddParam, PrepaymentFrom.class, dWhere);
      if (prepaymentsFrom.size() > 0) {
        pRequestData.setAttribute("prepayments", prepaymentsFrom);
      }
      List<PaymentFrom> paymentsFrom = getSrvOrm()
        .retrieveListWithConditions(pAddParam, PaymentFrom.class, dWhere);
      if (paymentsFrom.size() > 0) {
        pRequestData.setAttribute("payments", paymentsFrom);
      }
      String eWhereD =
        "where SOURCETYPE=3 and SUBACCDEBITTYPE=2010 and SUBACCDEBITID="
          + bsl.getItsOwner().getBankAccount().getItsId() + whereReversed
            + " and DEBIT=" + amountStr + " and ITSDATE >= " + startEnd[0]
              + " and ITSDATE <= " + startEnd[1];
      List<AccountingEntry> entriesFrom = getSrvOrm()
        .retrieveListWithConditions(pAddParam, AccountingEntry.class, eWhereD);
      if (entriesFrom.size() > 0) {
        pRequestData.setAttribute("entries", entriesFrom);
      }
    } else {
      //bank account credit
      List<PrepaymentTo> prepaymentsTo = getSrvOrm()
        .retrieveListWithConditions(pAddParam, PrepaymentTo.class, dWhere);
      if (prepaymentsTo.size() > 0) {
        pRequestData.setAttribute("prepayments", prepaymentsTo);
      }
      List<PaymentTo> paymentsTo = getSrvOrm()
        .retrieveListWithConditions(pAddParam, PaymentTo.class, dWhere);
      if (paymentsTo.size() > 0) {
        pRequestData.setAttribute("payments", paymentsTo);
      }
      String eWhereC =
        "where SOURCETYPE=3 and SUBACCCREDITTYPE=2010 and SUBACCCREDITID="
          + bsl.getItsOwner().getBankAccount().getItsId() + whereReversed
            + " and CREDIT=" + amountStr + " and ITSDATE >= " + startEnd[0]
              + " and ITSDATE <= " + startEnd[1];
      List<AccountingEntry> entriesTo = getSrvOrm()
        .retrieveListWithConditions(pAddParam, AccountingEntry.class, eWhereC);
      if (entriesTo.size() > 0) {
        pRequestData.setAttribute("entries", entriesTo);
      }
    }
    return bsl;
  }

  /**
   * <p>Evaluate start and end of day for given pDateFor.</p>
   * @param pDateFor date for
   * @return Start and end of day for pDateFor
   **/
  public final long[] evalDayStartEndFor(final Date pDateFor) {
    Calendar cal = Calendar.getInstance(new Locale("en", "US"));
    cal.setTime(pDateFor);
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);
    long[] result = new long[2];
    result[0] = cal.getTimeInMillis();
    cal.set(Calendar.HOUR_OF_DAY, 23);
    cal.set(Calendar.MINUTE, 59);
    cal.set(Calendar.SECOND, 59);
    cal.set(Calendar.MILLISECOND, 999);
    result[1] = cal.getTimeInMillis();
    return result;
  }

  //Simple getters and setters:
  /**
   * <p>Getter for prcEntityPbEditDelete.</p>
   * @return IEntityProcessor<BankStatementLine, Long>
   **/
  public final IEntityProcessor<BankStatementLine, Long>
    getPrcEntityPbEditDelete() {
    return this.prcEntityPbEditDelete;
  }

  /**
   * <p>Setter for prcEntityPbEditDelete.</p>
   * @param pPrcEntityPbEditDelete reference
   **/
  public final void setPrcEntityPbEditDelete(
    final IEntityProcessor<BankStatementLine, Long> pPrcEntityPbEditDelete) {
    this.prcEntityPbEditDelete = pPrcEntityPbEditDelete;
  }

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
}

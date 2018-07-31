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
import java.util.Date;
import java.util.Locale;
import java.math.BigDecimal;
import java.text.DateFormat;

import org.beigesoft.exception.ExceptionWithCode;
import org.beigesoft.model.IRequestData;
import org.beigesoft.service.IEntityProcessor;
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.service.ISrvI18n;
import org.beigesoft.persistable.APersistableBase;
import org.beigesoft.accounting.model.EBankEntryResultType;
import org.beigesoft.accounting.model.EBankEntryResultAction;
import org.beigesoft.accounting.model.EBankEntryStatus;
import org.beigesoft.accounting.persistable.base.ADoc;
import org.beigesoft.accounting.persistable.DebtorCreditor;
import org.beigesoft.accounting.persistable.Account;
import org.beigesoft.accounting.persistable.BankStatement;
import org.beigesoft.accounting.persistable.BankStatementLine;
import org.beigesoft.accounting.persistable.PaymentFrom;
import org.beigesoft.accounting.persistable.PrepaymentFrom;
import org.beigesoft.accounting.persistable.PaymentTo;
import org.beigesoft.accounting.persistable.PrepaymentTo;
import org.beigesoft.accounting.persistable.AccountingEntry;
import org.beigesoft.accounting.service.ISrvAccEntry;

/**
 * <p>Service that save BSL.</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class PrcBankStatementLineSave<RS>
  implements IEntityProcessor<BankStatementLine, Long> {

  /**
   * <p>ORM service.</p>
   **/
  private ISrvOrm<RS> srvOrm;

  /**
   * <p>I18N service.</p>
   **/
  private ISrvI18n srvI18n;

  /**
   * <p>Business service for accounting entries.</p>
   **/
  private ISrvAccEntry srvAccEntry;

  /**
   * <p>Process entity request.</p>
   * @param pReqVars additional param, e.g. return this line's
   * document in "nextEntity" for farther process
   * @param pRequestData Request Data
   * @param pEntity Entity to process
   * @return Entity processed for farther process or null
   * @throws Exception - an exception
   **/
  @Override
  public final BankStatementLine process(final Map<String, Object> pReqVars,
    final BankStatementLine pEntity,
      final IRequestData pRequestData) throws Exception {
    BankStatementLine bsl = getSrvOrm().retrieveEntity(pReqVars, pEntity);
    if (bsl.getIdDatabaseBirth() != getSrvOrm().getIdDatabase()) {
      throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
        "can_not_change_foreign_src");
    }
    if (bsl.getResultAction() != null) {
      throw new ExceptionWithCode(ExceptionWithCode.FORBIDDEN,
        "attempt_to_edit_completed_bank_statement_line");
    }
    if (bsl.getItsAmount().compareTo(BigDecimal.ZERO) == 0) {
      throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
        "amount_is_zero");
    }
    String langDef = (String) pReqVars.get("langDef");
    DateFormat dateFormat = DateFormat.getDateTimeInstance(
      DateFormat.MEDIUM, DateFormat.SHORT, new Locale(langDef));
    String prepaymentId = pRequestData.getParameter("prepaymentId");
    if (prepaymentId != null && !"".equals(prepaymentId)) {
      makePrepaymentMatchingReversed(pReqVars, prepaymentId, bsl, dateFormat,
        langDef);
    } else {
      String paymentId = pRequestData.getParameter("paymentId");
      if (paymentId != null && !"".equals(paymentId)) {
        makePaymentMatchingReversed(pReqVars, paymentId, bsl, dateFormat,
          langDef);
      } else {
        String accentryId = pRequestData.getParameter("accentryId");
        if (accentryId != null && !"".equals(accentryId)) {
          makeAccentryMatchingReversed(pReqVars, accentryId, bsl, dateFormat,
            langDef);
        } else {
          String adjDocType = pRequestData.getParameter("adjDocType");
          if (adjDocType != null && !"".equals(adjDocType)
            && !"-".equals(adjDocType)) {
            if ("1".equals(adjDocType)) {
              createPrepayment(pReqVars, bsl, dateFormat, langDef,
                pRequestData);
            } else {
              throw new Exception("NEI");
            }
          } else {
            throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
              "you_have_to_complete_data");
          }
        }
      }
    }
    this.srvOrm.updateEntity(pReqVars, bsl);
    pReqVars.put("nextEntity", pEntity.getItsOwner());
    pReqVars.put("nameOwnerEntity", BankStatement.class.getSimpleName());
    return bsl;
  }

  /**
   * <p>Creates prepayment.</p>
   * @param pReqVars additional param
   * @param pBsl BSL
   * @param pDateFormat Date Formatter
   * @param pLangDef language
   * @param pRequestData Request Data
   * @throws Exception - an exception
   **/
  public final void createPrepayment(final Map<String, Object> pReqVars,
    final BankStatementLine pBsl, final DateFormat pDateFormat,
      final String pLangDef, final IRequestData pRequestData) throws Exception {
    //String foreignTotalStr = pRequestData.getParameter("foreignTotal");
    String dcIdStr = pRequestData.getParameter("debtorCreditor");
    DebtorCreditor dc = new DebtorCreditor();
    dc.setItsId(Long.parseLong(dcIdStr));
    dc = getSrvOrm().retrieveEntity(pReqVars, dc);
    String accCashStr = pRequestData.getParameter("accCash");
    Account accCash = new Account();
    accCash.setItsId(accCashStr);
    accCash = getSrvOrm().retrieveEntity(pReqVars, accCash);
    EBankEntryResultType resultRecordType = null;
    EBankEntryResultAction resultAction = EBankEntryResultAction.CREATE;
    String resultDescription = null;
    Long resultRecordId = null;
    if (pBsl.getItsAmount().compareTo(BigDecimal.ZERO) > 0) {
      resultRecordType = EBankEntryResultType.PREPAYMENTFROM;
      PrepaymentFrom prep = new PrepaymentFrom();
      prep.setIdDatabaseBirth(getSrvOrm().getIdDatabase());
      prep.setAccCash(accCash);
      prep.setSubaccCash(pBsl.getItsOwner().getBankAccount().getItsName());
      prep.setSubaccCashId(pBsl.getItsOwner().getBankAccount().getItsId());
      prep.setSubaccCashType(2002);
      prep.setCustomer(dc);
      prep.setItsTotal(pBsl.getItsAmount().abs());
      //prep.setForeignTotal(foreignTotal);
      getSrvOrm().insertEntity(pReqVars, prep);
      resultRecordId = prep.getItsId();
      resultDescription = makeDescription(resultAction, pDateFormat, prep,
        prep.getItsDate(), pLangDef);
    }
    pBsl.setResultAction(resultAction);
    pBsl.setResultRecordType(resultRecordType);
    pBsl.setResultRecordId(resultRecordId);
    pBsl.setResultDescription(resultDescription);
    getSrvOrm().updateEntity(pReqVars, pBsl);
  }

  /**
   * <p>Makes accentry matching or reversed.</p>
   * @param pReqVars additional param
   * @param pPayId Accentry Id
   * @param pBsl BSL
   * @param pDateFormat Date Formatter
   * @param pLangDef language
   * @throws Exception - an exception
   **/
  public final void makeAccentryMatchingReversed(
    final Map<String, Object> pReqVars, final String pPayId,
      final BankStatementLine pBsl, final DateFormat pDateFormat,
        final String pLangDef) throws Exception {
    EBankEntryResultType resultRecordType = EBankEntryResultType.ACC_ENTRY;
    EBankEntryResultAction resultAction = null;
    String resultDescription = null;
    Long resultRecordId = null;
    AccountingEntry accent = getSrvOrm().retrieveEntityById(pReqVars,
      AccountingEntry.class, Long.parseLong(pPayId));
    if (pBsl.getItsAmount().compareTo(BigDecimal.ZERO) > 0 && pBsl
      .getItsAmount().abs().compareTo(accent.getDebit()) != 0
        || accent.getSubaccDebitType() != 2002 || !pBsl.getItsOwner()
          .getBankAccount().getItsId().equals(accent.getSubaccDebitId())) {
      throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
        "record_is_not_matching");
    } else if (pBsl.getItsAmount().compareTo(BigDecimal.ZERO) < 0 && pBsl
      .getItsAmount().abs().compareTo(accent.getCredit()) != 0
        || accent.getSubaccCreditType() != 2002 || !pBsl.getItsOwner()
          .getBankAccount().getItsId().equals(accent.getSubaccCreditId())) {
      throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
        "record_is_not_matching");
    }
    if (EBankEntryStatus.VOIDED.equals(pBsl.getItsStatus())) {
      if (accent.getIdDatabaseBirth() != getSrvOrm().getIdDatabase()) {
        throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
          "can_not_change_foreign_src");
      }
      resultAction = EBankEntryResultAction.CREATE;
      AccountingEntry reversed = accent;
      accent = new AccountingEntry();
      accent.setSourceType(pBsl.constTypeCode());
      accent.setSourceId(pBsl.getItsId());
      accent.setSourceDatabaseBirth(pBsl.getIdDatabaseBirth());
      accent.setIdDatabaseBirth(reversed.getIdDatabaseBirth());
      accent.setReversedId(reversed.getItsId());
      accent.setReversedIdDatabaseBirth(reversed.getIdDatabaseBirth());
      accent.setItsDate(new Date(reversed.getItsDate().getTime() + 1));
      accent.setAccDebit(reversed.getAccDebit());
      accent.setSubaccDebit(reversed.getSubaccDebit());
      accent.setSubaccDebitId(reversed.getSubaccDebitId());
      accent.setSubaccDebitType(reversed.getSubaccDebitType());
      accent.setDebit(reversed.getDebit().negate());
      accent.setAccCredit(reversed.getAccCredit());
      accent.setSubaccCredit(reversed.getSubaccCredit());
      accent.setSubaccCreditId(reversed.getSubaccCreditId());
      accent.setSubaccCreditType(reversed.getSubaccCreditType());
      accent.setCredit(reversed.getCredit().negate());
      accent.setDescription(getSrvI18n().getMsg("reversed_n", pLangDef)
        + accent.getReversedIdDatabaseBirth() + "-" + accent.getReversedId());
      getSrvOrm().insertEntity(pReqVars, accent);
      accent.setIsNew(false);
      String oldDesr = "";
      if (reversed.getDescription() != null) {
        oldDesr = reversed.getDescription();
      }
      reversed.setDescription(oldDesr + " " + getSrvI18n()
        .getMsg("reversing_n", pLangDef) + accent.getIdDatabaseBirth() + "-"
          + accent.getItsId());
      reversed.setReversedId(accent.getItsId());
      reversed.setReversedIdDatabaseBirth(accent.getIdDatabaseBirth());
      getSrvOrm().updateEntity(pReqVars, reversed);
    } else {
      resultAction = EBankEntryResultAction.MATCH;
    }
    resultRecordId = accent.getItsId();
    resultDescription = makeDescription(resultAction, pDateFormat, accent,
      accent.getItsDate(), pLangDef);
    pBsl.setResultAction(resultAction);
    pBsl.setResultRecordType(resultRecordType);
    pBsl.setResultRecordId(resultRecordId);
    pBsl.setResultDescription(resultDescription);
    getSrvOrm().updateEntity(pReqVars, pBsl);
  }

  /**
   * <p>Makes payment matching or reversed.</p>
   * @param pReqVars additional param
   * @param pPayId Payment Id
   * @param pBsl BSL
   * @param pDateFormat Date Formatter
   * @param pLangDef language
   * @throws Exception - an exception
   **/
  public final void makePaymentMatchingReversed(
    final Map<String, Object> pReqVars, final String pPayId,
      final BankStatementLine pBsl, final DateFormat pDateFormat,
        final String pLangDef) throws Exception {
    EBankEntryResultType resultRecordType = null;
    EBankEntryResultAction resultAction = null;
    String resultDescription = null;
    Long resultRecordId = null;
    if (pBsl.getItsAmount().compareTo(BigDecimal.ZERO) > 0) {
      PaymentFrom pay = getSrvOrm().retrieveEntityById(pReqVars,
        PaymentFrom.class, Long.parseLong(pPayId));
      if (!pay.getHasMadeAccEntries() || pBsl.getItsAmount().abs()
        .compareTo(pay.getItsTotal()) != 0
          || pay.getSubaccCashType() != 2002 || !pBsl.getItsOwner()
            .getBankAccount().getItsId().equals(pay.getSubaccCashId())) {
        throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
          "record_is_not_matching");
      }
      resultRecordType = EBankEntryResultType.PAYMENTFROM;
      if (EBankEntryStatus.VOIDED.equals(pBsl.getItsStatus())
        && pay.getReversedId() == null) {
        resultAction = EBankEntryResultAction.CREATE;
        PaymentFrom reversed = pay;
        pay = new PaymentFrom();
        pay.setAccCash(reversed.getAccCash());
        pay.setSubaccCash(reversed.getSubaccCash());
        pay.setSubaccCashId(reversed.getSubaccCashId());
        pay.setSubaccCashType(2002);
        pay.setSalesInvoice(reversed.getSalesInvoice());
        pay.setForeignTotal(reversed.getForeignTotal().negate());
        makeDocReversed(pReqVars, pay, reversed, pLangDef);
      } else {
        resultAction = EBankEntryResultAction.MATCH;
      }
      resultRecordId = pay.getItsId();
      resultDescription = makeDescription(resultAction, pDateFormat, pay,
        pay.getItsDate(), pLangDef);
    } else {
      PaymentTo pay = getSrvOrm().retrieveEntityById(pReqVars,
        PaymentTo.class, Long.parseLong(pPayId));
      if (!pay.getHasMadeAccEntries() || pBsl.getItsAmount().abs()
        .compareTo(pay.getItsTotal()) != 0
          || pay.getSubaccCashType() != 2002 || !pBsl.getItsOwner()
            .getBankAccount().getItsId().equals(pay.getSubaccCashId())) {
        throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
          "record_is_not_matching");
      }
      resultRecordType = EBankEntryResultType.PAYMENTTO;
      if (EBankEntryStatus.VOIDED.equals(pBsl.getItsStatus())
        && pay.getReversedId() == null) {
        resultAction = EBankEntryResultAction.CREATE;
        PaymentTo reversed = pay;
        pay = new PaymentTo();
        pay.setAccCash(reversed.getAccCash());
        pay.setSubaccCash(reversed.getSubaccCash());
        pay.setSubaccCashId(reversed.getSubaccCashId());
        pay.setSubaccCashType(2002);
        pay.setPurchaseInvoice(reversed.getPurchaseInvoice());
        pay.setForeignTotal(reversed.getForeignTotal().negate());
        makeDocReversed(pReqVars, pay, reversed, pLangDef);
      } else {
        resultAction = EBankEntryResultAction.MATCH;
      }
      resultRecordId = pay.getItsId();
      resultDescription = makeDescription(resultAction, pDateFormat, pay,
        pay.getItsDate(), pLangDef);
    }
    pBsl.setResultAction(resultAction);
    pBsl.setResultRecordType(resultRecordType);
    pBsl.setResultRecordId(resultRecordId);
    pBsl.setResultDescription(resultDescription);
    getSrvOrm().updateEntity(pReqVars, pBsl);
  }

  /**
   * <p>Makes prepayment matching or reversed.</p>
   * @param pReqVars additional param
   * @param pPrepayId Prepayment Id
   * @param pBsl BSL
   * @param pDateFormat Date Formatter
   * @param pLangDef language
   * @throws Exception - an exception
   **/
  public final void makePrepaymentMatchingReversed(
    final Map<String, Object> pReqVars, final String pPrepayId,
      final BankStatementLine pBsl, final DateFormat pDateFormat,
        final String pLangDef) throws Exception {
    EBankEntryResultType resultRecordType = null;
    EBankEntryResultAction resultAction = null;
    String resultDescription = null;
    Long resultRecordId = null;
    if (pBsl.getItsAmount().compareTo(BigDecimal.ZERO) > 0) {
      PrepaymentFrom prep = getSrvOrm().retrieveEntityById(pReqVars,
        PrepaymentFrom.class, Long.parseLong(pPrepayId));
      if (!prep.getHasMadeAccEntries() || pBsl.getItsAmount().abs()
        .compareTo(prep.getItsTotal().abs()) != 0
          || prep.getSubaccCashType() != 2002 || !pBsl.getItsOwner()
            .getBankAccount().getItsId().equals(prep.getSubaccCashId())) {
        throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
          "record_is_not_matching");
      }
      resultRecordType = EBankEntryResultType.PREPAYMENTFROM;
      if (EBankEntryStatus.VOIDED.equals(pBsl.getItsStatus())
        && prep.getReversedId() == null) {
        resultAction = EBankEntryResultAction.CREATE;
        PrepaymentFrom reversed = prep;
        prep = new PrepaymentFrom();
        prep.setAccCash(reversed.getAccCash());
        prep.setSubaccCash(reversed.getSubaccCash());
        prep.setSubaccCashId(reversed.getSubaccCashId());
        prep.setSubaccCashType(2002);
        prep.setCustomer(reversed.getCustomer());
        prep.setForeignTotal(reversed.getForeignTotal().negate());
        makeDocReversed(pReqVars, prep, reversed, pLangDef);
      } else {
        resultAction = EBankEntryResultAction.MATCH;
      }
      resultRecordId = prep.getItsId();
      resultDescription = makeDescription(resultAction, pDateFormat, prep,
        prep.getItsDate(), pLangDef);
    } else {
      PrepaymentTo prep = getSrvOrm().retrieveEntityById(pReqVars,
        PrepaymentTo.class, Long.parseLong(pPrepayId));
      if (!prep.getHasMadeAccEntries() || pBsl.getItsAmount().abs()
        .compareTo(prep.getItsTotal().abs()) != 0
          || prep.getSubaccCashType() != 2002 || !pBsl.getItsOwner()
            .getBankAccount().getItsId().equals(prep.getSubaccCashId())) {
        throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
          "record_is_not_matching");
      }
      resultRecordType = EBankEntryResultType.PREPAYMENTTO;
      if (EBankEntryStatus.VOIDED.equals(pBsl.getItsStatus())
        && prep.getReversedId() == null) {
        resultAction = EBankEntryResultAction.CREATE;
        PrepaymentTo reversed = prep;
        prep = new PrepaymentTo();
        prep.setAccCash(reversed.getAccCash());
        prep.setSubaccCash(reversed.getSubaccCash());
        prep.setSubaccCashId(reversed.getSubaccCashId());
        prep.setSubaccCashType(2002);
        prep.setVendor(reversed.getVendor());
        prep.setForeignTotal(reversed.getForeignTotal().negate());
        makeDocReversed(pReqVars, prep, reversed, pLangDef);
      } else {
        resultAction = EBankEntryResultAction.MATCH;
      }
      resultRecordId = prep.getItsId();
      resultDescription = makeDescription(resultAction, pDateFormat, prep,
        prep.getItsDate(), pLangDef);
    }
    pBsl.setResultAction(resultAction);
    pBsl.setResultRecordType(resultRecordType);
    pBsl.setResultRecordId(resultRecordId);
    pBsl.setResultDescription(resultDescription);
    getSrvOrm().updateEntity(pReqVars, pBsl);
  }

  /**
   * <p>Makes document reversed.</p>
   * @param pReqVars additional param
   * @param pReversing Reversing
   * @param pReversed Reversed
   * @param pLangDef language
   * @throws Exception - an exception
   **/
  public final void makeDocReversed(final Map<String, Object> pReqVars,
    final ADoc pReversing, final ADoc pReversed,
      final String pLangDef) throws Exception {
    pReversing.setIdDatabaseBirth(pReversed.getIdDatabaseBirth());
    pReversing.setReversedId(pReversed.getItsId());
    pReversing.setReversedIdDatabaseBirth(pReversed.getIdDatabaseBirth());
    pReversing.setItsDate(new Date(pReversed.getItsDate().getTime() + 1));
    pReversing.setItsTotal(pReversed.getItsTotal().negate());
    pReversing.setHasMadeAccEntries(false);
    pReversing.setDescription(getSrvI18n().getMsg("reversed_n", pLangDef)
+ pReversing.getReversedIdDatabaseBirth() + "-" + pReversing.getReversedId());
    getSrvOrm().insertEntity(pReqVars, pReversing);
    pReversing.setIsNew(false);
    String oldDesr = "";
    if (pReversed.getDescription() != null) {
      oldDesr = pReversed.getDescription();
    }
    pReversed.setDescription(oldDesr + " " + getSrvI18n()
      .getMsg("reversing_n", pLangDef) + pReversing.getIdDatabaseBirth() + "-"
        + pReversing.getItsId());
    pReversed.setReversedId(pReversing.getItsId());
    pReversed.setReversedIdDatabaseBirth(pReversing.getIdDatabaseBirth());
    getSrvOrm().updateEntity(pReqVars, pReversed);
    this.srvAccEntry.reverseEntries(pReqVars, pReversing, pReversed);
  }

  /**
   * <p>Makes description.</p>
   * @param pResAct action
   * @param pDateFormat Date Formatter
   * @param pRecord Record
   * @param pDate Date
   * @param pLangDef language
   * @return description
   **/
  public final String makeDescription(final EBankEntryResultAction pResAct,
    final DateFormat pDateFormat, final APersistableBase pRecord,
      final Date pDate, final String pLangDef) {
    StringBuffer sb = new StringBuffer();
    if (EBankEntryResultAction.MATCH.equals(pResAct)) {
      sb.append(getSrvI18n().getMsg("Found", pLangDef));
    } else {
      sb.append(getSrvI18n().getMsg("Created", pLangDef));
    }
    sb.append(" " + getSrvI18n()
      .getMsg(pRecord.getClass().getSimpleName(), pLangDef));
    sb.append("#" + pRecord.getIdDatabaseBirth() + "-" + pRecord.getItsId()
      + ", " + pDateFormat.format(pDate));
    return sb.toString();
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
   * <p>Getter for srvI18n.</p>
   * @return ISrvI18n
   **/
  public final ISrvI18n getSrvI18n() {
    return this.srvI18n;
  }

  /**
   * <p>Setter for srvI18n.</p>
   * @param pSrvI18n reference
   **/
  public final void setSrvI18n(final ISrvI18n pSrvI18n) {
    this.srvI18n = pSrvI18n;
  }

  /**
   * <p>Getter for srvAccEntry.</p>
   * @return ISrvAccEntry
   **/
  public final ISrvAccEntry getSrvAccEntry() {
    return this.srvAccEntry;
  }

  /**
   * <p>Setter for srvAccEntry.</p>
   * @param pSrvAccEntry reference
   **/
  public final void setSrvAccEntry(final ISrvAccEntry pSrvAccEntry) {
    this.srvAccEntry = pSrvAccEntry;
  }
}

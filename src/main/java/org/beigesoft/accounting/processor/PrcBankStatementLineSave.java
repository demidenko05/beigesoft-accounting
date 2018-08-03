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
import java.text.SimpleDateFormat;

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
import org.beigesoft.accounting.persistable.PurchaseInvoice;
import org.beigesoft.accounting.persistable.SalesInvoice;
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
   * <p>Processor SalesInvoice Save.</p>
   **/
  private PrcSalesInvoiceSave<RS> prcSalesInvoiceSave;

  /**
   * <p>Processor PurchaseInvoice Save.</p>
   **/
  private PrcPurchaseInvoiceSave<RS> prcPurchaseInvoiceSave;

  /**
   * <p>Format date-time ISO8601 no time zone,
   * e.g. 2001-07-04T21:55.</p>
   **/
  private DateFormat dateTimeNoTzFormatIso8601 =
    new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");

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
    pReqVars.put("BankStatementLineitsOwnerdeepLevel", 3);
    BankStatementLine bsl = getSrvOrm().retrieveEntity(pReqVars, pEntity);
    pReqVars.remove("BankStatementLineitsOwnerdeepLevel");
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
            if (EBankEntryStatus.VOIDED.equals(bsl.getItsStatus())) {
              throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
                "can_not_create_for_voided");
            }
            if ("1".equals(adjDocType)) {
              createPrepayment(pReqVars, bsl, dateFormat, langDef,
                pRequestData);
            } else if ("2".equals(adjDocType)) {
              createPayment(pReqVars, bsl, dateFormat, langDef,
                pRequestData);
            } else {
              createAccentry(pReqVars, bsl, dateFormat, langDef,
                pRequestData);
            }
          } else {
            throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
              "wrong_paramaters");
          }
        }
      }
    }
    this.srvOrm.updateEntity(pReqVars, bsl);
    pReqVars.put("nextEntity", pEntity.getItsOwner());
    pReqVars.put("nameOwnerEntity", BankStatement.class.getSimpleName());
    return null;
  }

  /**
   * <p>Creates payment.</p>
   * @param pReqVars additional param
   * @param pBsl BSL
   * @param pDateFormat Date Formatter
   * @param pLangDef language
   * @param pRequestData Request Data
   * @throws Exception - an exception
   **/
  public final void createPayment(final Map<String, Object> pReqVars,
    final BankStatementLine pBsl, final DateFormat pDateFormat,
      final String pLangDef, final IRequestData pRequestData) throws Exception {
    BigDecimal foreignTotal;
    Account accCash;
    Date itsDate;
    try {
      String forTotStr = pRequestData.getParameter("foreignTotal");
      if (forTotStr == null || "".equals(forTotStr)) {
        foreignTotal = BigDecimal.ZERO;
      } else {
        String dsep = (String) pReqVars.get("dseparatorv");
        if (dsep != null) {
          String dgsep = (String) pReqVars.get("dgseparatorv");
          forTotStr = forTotStr.replace(dgsep, "").replace(dsep, ".");
        }
        foreignTotal = new BigDecimal(forTotStr);
      }
      String accCashStr = pRequestData.getParameter("accCash");
      accCash = new Account();
      accCash.setItsId(accCashStr);
      accCash = getSrvOrm().retrieveEntity(pReqVars, accCash);
      if (accCash == null) {
        throw new Exception("cant_find_account");
      }
      String itsDateStr = pRequestData.getParameter("itsDate");
      itsDate = this.dateTimeNoTzFormatIso8601.parse(itsDateStr);
    } catch (Exception e) {
      throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
        "wrong_paramaters", e);
    }
    EBankEntryResultType resultRecordType = null;
    EBankEntryResultAction resultAction = EBankEntryResultAction.CREATE;
    String resultDescription = null;
    Long resultRecordId = null;
    if (pBsl.getItsAmount().compareTo(BigDecimal.ZERO) > 0) {
      SalesInvoice inv;
      try {
        String invStr = pRequestData.getParameter("invoice");
        inv = new SalesInvoice();
        inv.setItsId(Long.parseLong(invStr));
        inv = getSrvOrm().retrieveEntity(pReqVars, inv);
        if (inv == null) {
          throw new Exception("cant_find_debtor_invoice");
        }
      } catch (Exception e) {
        throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
          "wrong_paramaters", e);
      }
      resultRecordType = EBankEntryResultType.PREPAYMENTFROM;
      PaymentFrom pay = new PaymentFrom();
      pay.setIdDatabaseBirth(getSrvOrm().getIdDatabase());
      pay.setItsDate(itsDate);
      pay.setSalesInvoice(inv);
      pay.setAccCash(accCash);
      pay.setSubaccCash(pBsl.getItsOwner().getBankAccount().getItsName());
      pay.setSubaccCashId(pBsl.getItsOwner().getBankAccount().getItsId());
      pay.setSubaccCashType(2002);
      pay.setItsTotal(pBsl.getItsAmount().abs());
      pay.setForeignTotal(foreignTotal);
      pay.setDescription(makeDescrForCreated(pBsl, pDateFormat, pLangDef));
      getSrvOrm().insertEntity(pReqVars, pay);
      this.srvAccEntry.makeEntries(pReqVars, pay);
      this.prcSalesInvoiceSave.calculateTotalPayment(pReqVars, pay
        .getSalesInvoice());
      getSrvOrm().updateEntity(pReqVars, pay.getSalesInvoice());
      resultRecordId = pay.getItsId();
      resultDescription = makeBslResDescr(resultAction, pDateFormat, pay,
        pay.getItsDate(), pLangDef);
    } else {
      PurchaseInvoice inv;
      try {
        String invStr = pRequestData.getParameter("invoice");
        inv = new PurchaseInvoice();
        inv.setItsId(Long.parseLong(invStr));
        inv = getSrvOrm().retrieveEntity(pReqVars, inv);
        if (inv == null) {
          throw new Exception("cant_find_debtor_invoice");
        }
      } catch (Exception e) {
        throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
          "wrong_paramaters", e);
      }
      resultRecordType = EBankEntryResultType.PREPAYMENTTO;
      PaymentTo pay = new PaymentTo();
      pay.setIdDatabaseBirth(getSrvOrm().getIdDatabase());
      pay.setItsDate(itsDate);
      pay.setPurchaseInvoice(inv);
      pay.setAccCash(accCash);
      pay.setSubaccCash(pBsl.getItsOwner().getBankAccount().getItsName());
      pay.setSubaccCashId(pBsl.getItsOwner().getBankAccount().getItsId());
      pay.setSubaccCashType(2002);
      pay.setItsTotal(pBsl.getItsAmount().abs());
      pay.setForeignTotal(foreignTotal);
      pay.setDescription(makeDescrForCreated(pBsl, pDateFormat, pLangDef));
      getSrvOrm().insertEntity(pReqVars, pay);
      this.srvAccEntry.makeEntries(pReqVars, pay);
      this.prcPurchaseInvoiceSave.calculateTotalPayment(pReqVars,
        pay.getPurchaseInvoice());
      getSrvOrm().updateEntity(pReqVars, pay.getPurchaseInvoice());
      resultRecordId = pay.getItsId();
      resultDescription = makeBslResDescr(resultAction, pDateFormat, pay,
        pay.getItsDate(), pLangDef);
    }
    pBsl.setResultAction(resultAction);
    pBsl.setResultRecordType(resultRecordType);
    pBsl.setResultRecordId(resultRecordId);
    pBsl.setResultDescription(resultDescription);
    getSrvOrm().updateEntity(pReqVars, pBsl);
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
    BigDecimal foreignTotal;
    DebtorCreditor dc;
    Account accCash;
    Date itsDate;
    try {
      String forTotStr = pRequestData.getParameter("foreignTotal");
      if (forTotStr == null || "".equals(forTotStr)) {
        foreignTotal = BigDecimal.ZERO;
      } else {
        String dsep = (String) pReqVars.get("dseparatorv");
        if (dsep != null) {
          String dgsep = (String) pReqVars.get("dgseparatorv");
          forTotStr = forTotStr.replace(dgsep, "").replace(dsep, ".");
        }
        foreignTotal = new BigDecimal(forTotStr);
      }
      String dcIdStr = pRequestData.getParameter("debtorCreditor");
      dc = new DebtorCreditor();
      dc.setItsId(Long.parseLong(dcIdStr));
      dc = getSrvOrm().retrieveEntity(pReqVars, dc);
      if (dc == null) {
        throw new Exception("cant_find_debtor_creditor");
      }
      String accCashStr = pRequestData.getParameter("accCash");
      accCash = new Account();
      accCash.setItsId(accCashStr);
      accCash = getSrvOrm().retrieveEntity(pReqVars, accCash);
      if (accCash == null) {
        throw new Exception("cant_find_account");
      }
      String itsDateStr = pRequestData.getParameter("itsDate");
      itsDate = this.dateTimeNoTzFormatIso8601.parse(itsDateStr);
    } catch (Exception e) {
      throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
        "wrong_paramaters", e);
    }
    EBankEntryResultType resultRecordType = null;
    EBankEntryResultAction resultAction = EBankEntryResultAction.CREATE;
    String resultDescription = null;
    Long resultRecordId = null;
    if (pBsl.getItsAmount().compareTo(BigDecimal.ZERO) > 0) {
      resultRecordType = EBankEntryResultType.PREPAYMENTFROM;
      PrepaymentFrom prep = new PrepaymentFrom();
      prep.setIdDatabaseBirth(getSrvOrm().getIdDatabase());
      prep.setItsDate(itsDate);
      prep.setCustomer(dc);
      prep.setAccCash(accCash);
      prep.setSubaccCash(pBsl.getItsOwner().getBankAccount().getItsName());
      prep.setSubaccCashId(pBsl.getItsOwner().getBankAccount().getItsId());
      prep.setSubaccCashType(2002);
      prep.setItsTotal(pBsl.getItsAmount().abs());
      prep.setForeignTotal(foreignTotal);
      prep.setDescription(makeDescrForCreated(pBsl, pDateFormat, pLangDef));
      getSrvOrm().insertEntity(pReqVars, prep);
      this.srvAccEntry.makeEntries(pReqVars, prep);
      resultRecordId = prep.getItsId();
      resultDescription = makeBslResDescr(resultAction, pDateFormat, prep,
        prep.getItsDate(), pLangDef);
    } else {
      resultRecordType = EBankEntryResultType.PREPAYMENTTO;
      PrepaymentTo prep = new PrepaymentTo();
      prep.setIdDatabaseBirth(getSrvOrm().getIdDatabase());
      prep.setVendor(dc);
      prep.setItsDate(itsDate);
      prep.setAccCash(accCash);
      prep.setSubaccCash(pBsl.getItsOwner().getBankAccount().getItsName());
      prep.setSubaccCashId(pBsl.getItsOwner().getBankAccount().getItsId());
      prep.setSubaccCashType(2002);
      prep.setItsTotal(pBsl.getItsAmount().abs());
      prep.setForeignTotal(foreignTotal);
      prep.setDescription(makeDescrForCreated(pBsl, pDateFormat, pLangDef));
      getSrvOrm().insertEntity(pReqVars, prep);
      this.srvAccEntry.makeEntries(pReqVars, prep);
      resultRecordId = prep.getItsId();
      resultDescription = makeBslResDescr(resultAction, pDateFormat, prep,
        prep.getItsDate(), pLangDef);
    }
    pBsl.setResultAction(resultAction);
    pBsl.setResultRecordType(resultRecordType);
    pBsl.setResultRecordId(resultRecordId);
    pBsl.setResultDescription(resultDescription);
    getSrvOrm().updateEntity(pReqVars, pBsl);
  }

  /**
   * <p>Create accentry.</p>
   * @param pReqVars additional param
   * @param pBsl BSL
   * @param pDateFormat Date Formatter
   * @param pLangDef language
   * @param pRequestData Request Data
   * @throws Exception - an exception
   **/
  public final void createAccentry(final Map<String, Object> pReqVars,
    final BankStatementLine pBsl, final DateFormat pDateFormat,
      final String pLangDef, final IRequestData pRequestData) throws Exception {
    Account accCash;
    Account corAcc;
    String subcorAcc = null;
    Long subcorAccId = null;
    Integer subcorAccType = null;
    Date itsDate;
    try {
      String accCashStr = pRequestData.getParameter("accCash");
      accCash = new Account();
      accCash.setItsId(accCashStr);
      accCash = getSrvOrm().retrieveEntity(pReqVars, accCash);
      if (accCash == null) {
        throw new Exception("cant_find_account");
      }
      String corAccStr = pRequestData.getParameter("corAcc");
      corAcc = new Account();
      corAcc.setItsId(corAccStr);
      corAcc = getSrvOrm().retrieveEntity(pReqVars, corAcc);
      if (corAcc == null) {
        throw new Exception("cant_find_account");
      }
      if (corAcc.getSubaccType() != null) {
        subcorAcc = pRequestData.getParameter("subcorAcc");
        String subcorAccTypeStr = pRequestData.getParameter("subcorAccType");
        subcorAccType = Integer.parseInt(subcorAccTypeStr);
        String subcorAccIdStr = pRequestData.getParameter("subcorAccId");
        subcorAccId = Long.parseLong(subcorAccIdStr);
      }
      String itsDateStr = pRequestData.getParameter("itsDate");
      itsDate = this.dateTimeNoTzFormatIso8601.parse(itsDateStr);
    } catch (Exception e) {
      throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
        "wrong_paramaters", e);
    }
    EBankEntryResultType resultRecordType = EBankEntryResultType.ACC_ENTRY;
    EBankEntryResultAction resultAction = EBankEntryResultAction.CREATE;
    String resultDescription = null;
    Long resultRecordId = null;
    AccountingEntry accent = new AccountingEntry();
    if (pBsl.getItsAmount().compareTo(BigDecimal.ZERO) > 0) {
      accent.setAccDebit(accCash);
      accent.setSubaccDebit(pBsl.getItsOwner().getBankAccount().getItsName());
      accent.setSubaccDebitId(pBsl.getItsOwner().getBankAccount().getItsId());
      accent.setSubaccDebitType(2002);
      accent.setAccCredit(corAcc);
      accent.setSubaccCredit(subcorAcc);
      accent.setSubaccCreditId(subcorAccId);
      accent.setSubaccCreditType(subcorAccType);
    } else {
      accent.setAccDebit(corAcc);
      accent.setSubaccDebit(subcorAcc);
      accent.setSubaccDebitId(subcorAccId);
      accent.setSubaccDebitType(subcorAccType);
      accent.setAccCredit(accCash);
      accent.setSubaccCredit(pBsl.getItsOwner().getBankAccount().getItsName());
      accent.setSubaccCreditId(pBsl.getItsOwner().getBankAccount().getItsId());
      accent.setSubaccCreditType(2002);
    }
    accent.setSourceType(pBsl.constTypeCode());
    accent.setSourceId(pBsl.getItsId());
    accent.setSourceDatabaseBirth(pBsl.getIdDatabaseBirth());
    accent.setIdDatabaseBirth(pBsl.getIdDatabaseBirth());
    accent.setItsDate(itsDate);
    accent.setDebit(pBsl.getItsAmount().abs());
    accent.setCredit(accent.getDebit());
    accent.setDescription(makeDescrForCreated(pBsl, pDateFormat, pLangDef));
    getSrvOrm().insertEntity(pReqVars, accent);
    accent.setIsNew(false);
    resultRecordId = accent.getItsId();
    resultDescription = makeBslResDescr(resultAction, pDateFormat, accent,
      accent.getItsDate(), pLangDef);
    pBsl.setResultAction(resultAction);
    pBsl.setResultRecordType(resultRecordType);
    pBsl.setResultRecordId(resultRecordId);
    pBsl.setResultDescription(resultDescription);
    getSrvOrm().updateEntity(pReqVars, pBsl);
  }

  /**
   * <p>Makes accentry matching or reversed.</p>
   * @param pReqVars additional param
   * @param pEntryId Accentry Id
   * @param pBsl BSL
   * @param pDateFormat Date Formatter
   * @param pLangDef language
   * @throws Exception - an exception
   **/
  public final void makeAccentryMatchingReversed(
    final Map<String, Object> pReqVars, final String pEntryId,
      final BankStatementLine pBsl, final DateFormat pDateFormat,
        final String pLangDef) throws Exception {
    EBankEntryResultType resultRecordType = EBankEntryResultType.ACC_ENTRY;
    EBankEntryResultAction resultAction = null;
    String resultDescription = null;
    Long resultRecordId = null;
    AccountingEntry accent = getSrvOrm().retrieveEntityById(pReqVars,
      AccountingEntry.class, Long.parseLong(pEntryId));
    if (accent == null) {
      throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
        "cant_found_accentry");
    }
    if (accent.getSourceType() == 1010) {
      throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
        "AlreadyDone");
    }
    if (pBsl.getItsAmount().compareTo(BigDecimal.ZERO) > 0 && pBsl
      .getItsAmount().compareTo(accent.getDebit()) != 0
        && accent.getSubaccDebitType() != 2002 && !pBsl.getItsOwner()
          .getBankAccount().getItsId().equals(accent.getSubaccDebitId())) {
      throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
        "record_is_not_matching");
    } else if (pBsl.getItsAmount().compareTo(BigDecimal.ZERO) < 0 && pBsl
      .getItsAmount().abs().compareTo(accent.getCredit()) != 0
        && accent.getSubaccCreditType() != 2002 && !pBsl.getItsOwner()
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
      accent.setDescription(makeDescrForCreated(pBsl, pDateFormat, pLangDef)
        + " " + getSrvI18n().getMsg("reversed_n", pLangDef)
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
    resultDescription = makeBslResDescr(resultAction, pDateFormat, accent,
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
        pay.setDescription(makeDescrForCreated(pBsl, pDateFormat, pLangDef)
          + " " + getSrvI18n().getMsg("reversed_n", pLangDef) + reversed
            .getIdDatabaseBirth() + "-" + reversed.getItsId());
        makeDocReversed(pReqVars, pay, reversed, pLangDef);
      } else {
        resultAction = EBankEntryResultAction.MATCH;
      }
      resultRecordId = pay.getItsId();
      resultDescription = makeBslResDescr(resultAction, pDateFormat, pay,
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
        pay.setDescription(makeDescrForCreated(pBsl, pDateFormat, pLangDef)
          + " " + getSrvI18n().getMsg("reversed_n", pLangDef) + reversed
            .getIdDatabaseBirth() + "-" + reversed.getItsId());
        makeDocReversed(pReqVars, pay, reversed, pLangDef);
      } else {
        resultAction = EBankEntryResultAction.MATCH;
      }
      resultRecordId = pay.getItsId();
      resultDescription = makeBslResDescr(resultAction, pDateFormat, pay,
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
        prep.setDescription(makeDescrForCreated(pBsl, pDateFormat, pLangDef)
          + " " + getSrvI18n().getMsg("reversed_n", pLangDef) + reversed
            .getIdDatabaseBirth() + "-" + reversed.getItsId());
        makeDocReversed(pReqVars, prep, reversed, pLangDef);
      } else {
        resultAction = EBankEntryResultAction.MATCH;
      }
      resultRecordId = prep.getItsId();
      resultDescription = makeBslResDescr(resultAction, pDateFormat, prep,
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
        prep.setDescription(makeDescrForCreated(pBsl, pDateFormat, pLangDef)
          + " " + getSrvI18n().getMsg("reversed_n", pLangDef) + reversed
            .getIdDatabaseBirth() + "-" + reversed.getItsId());
        makeDocReversed(pReqVars, prep, reversed, pLangDef);
      } else {
        resultAction = EBankEntryResultAction.MATCH;
      }
      resultRecordId = prep.getItsId();
      resultDescription = makeBslResDescr(resultAction, pDateFormat, prep,
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
   * <p>Makes BSL result description.</p>
   * @param pResAct action
   * @param pDateFormat Date Formatter
   * @param pRecord Record
   * @param pDate Date
   * @param pLangDef language
   * @return description
   **/
  public final String makeBslResDescr(final EBankEntryResultAction pResAct,
    final DateFormat pDateFormat, final APersistableBase pRecord,
      final Date pDate, final String pLangDef) {
    StringBuffer sb = new StringBuffer();
    if (EBankEntryResultAction.MATCH.equals(pResAct)) {
      sb.append(getSrvI18n().getMsg("Found", pLangDef));
    } else {
      sb.append(getSrvI18n().getMsg("Created", pLangDef));
    }
    sb.append(" " + getSrvI18n()
      .getMsg(pRecord.getClass().getSimpleName() + "short", pLangDef));
    sb.append("#" + pRecord.getIdDatabaseBirth() + "-" + pRecord.getItsId()
      + ", " + pDateFormat.format(pDate));
    return sb.toString();
  }

  /**
   * <p>Makes description for created record.</p>
   * @param pBsl BSL
   * @param pDateFormat Date Formatter
   * @param pLangDef language
   * @return description
   **/
  public final String makeDescrForCreated(final BankStatementLine pBsl,
    final DateFormat pDateFormat, final String pLangDef) {
    StringBuffer sb = new StringBuffer();
    sb.append(getSrvI18n().getMsg("Created", pLangDef)
      + " " + getSrvI18n().getMsg("by", pLangDef));
    sb.append(" " + getSrvI18n()
      .getMsg(pBsl.getClass().getSimpleName() + "short", pLangDef));
    sb.append("#" + pBsl.getIdDatabaseBirth() + "-" + pBsl.getItsId()
      + ", " + pDateFormat.format(pBsl.getItsDate()));
    sb.append(" (" + pBsl.getDescriptionStatus() + ")");
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

  /**
   * <p>Getter for prcPurchaseInvoiceSave.</p>
   * @return PrcPurchaseInvoiceSave<RS>
   **/
  public final PrcPurchaseInvoiceSave<RS> getPrcPurchaseInvoiceSave() {
    return this.prcPurchaseInvoiceSave;
  }

  /**
   * <p>Setter for prcPurchaseInvoiceSave.</p>
   * @param pPrcPurchaseInvoiceSave reference
   **/
  public final void setPrcPurchaseInvoiceSave(
    final PrcPurchaseInvoiceSave<RS> pPrcPurchaseInvoiceSave) {
    this.prcPurchaseInvoiceSave = pPrcPurchaseInvoiceSave;
  }

  /**
   * <p>Getter for prcSalesInvoiceSave.</p>
   * @return PrcSalesInvoiceSave<RS>
   **/
  public final PrcSalesInvoiceSave<RS> getPrcSalesInvoiceSave() {
    return this.prcSalesInvoiceSave;
  }

  /**
   * <p>Setter for prcSalesInvoiceSave.</p>
   * @param pPrcSalesInvoiceSave reference
   **/
  public final void setPrcSalesInvoiceSave(
    final PrcSalesInvoiceSave<RS> pPrcSalesInvoiceSave) {
    this.prcSalesInvoiceSave = pPrcSalesInvoiceSave;
  }
}

package org.beigesoft.accounting.processor;

/*
 * Copyright (c) 2017 Beigesoft™
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
import java.util.Locale;
import java.math.BigDecimal;
import java.text.DateFormat;

import org.beigesoft.model.IRequestData;
import org.beigesoft.exception.ExceptionWithCode;
import org.beigesoft.accounting.persistable.PaymentFrom;
import org.beigesoft.accounting.persistable.PrepaymentFrom;
import org.beigesoft.accounting.persistable.SalesInvoice;
import org.beigesoft.accounting.persistable.SalesInvoiceLine;
import org.beigesoft.accounting.persistable.SalesInvoiceServiceLine;
import org.beigesoft.accounting.persistable.SalesInvoiceTaxLine;
import org.beigesoft.accounting.persistable.SalesInvoiceGoodsTaxLine;
import org.beigesoft.accounting.persistable.SalesInvoiceServiceTaxLine;

/**
 * <p>Process that saves sales invoice.</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class PrcSalesInvoiceSave<RS>
  extends APrcAccDocCogsSave<RS, SalesInvoice> {

  /**
   * <p>Make save preparations before insert/update block if it's need.</p>
   * @param pReqVars additional param
   * @param pEntity entity
   * @param pRequestData Request Data
   * @throws Exception - an exception
   **/
  @Override
  public final void makeFirstPrepareForSave(final Map<String, Object> pReqVars,
    final SalesInvoice pEntity,
      final IRequestData pRequestData) throws Exception {
    if (pEntity.getPrepaymentFrom() != null) {
      pEntity.setPrepaymentFrom(getSrvOrm()
        .retrieveEntity(pReqVars, pEntity.getPrepaymentFrom()));
      if (pEntity.getReversedId() == null && pEntity.getPrepaymentFrom()
        .getSalesInvoiceId() != null && !pEntity.getHasMadeAccEntries()) {
        throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
          "prepayment_already_in_use");
      }
      if (pEntity.getReversedId() == null && !pEntity.getPrepaymentFrom()
        .getCustomer().getItsId().equals(pEntity.getCustomer().getItsId())) {
        throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
          "prepayment_for_different_vendor");
      }
    }
    if (pEntity.getReversedId() != null && pEntity.getPrepaymentFrom() != null
      && pEntity.getPaymentTotal().compareTo(pEntity.getPrepaymentFrom()
        .getItsTotal()) != 0) {
      throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
        "reverse_payments_first");
    }
    if (pEntity.getReversedId() != null && pEntity.getPrepaymentFrom() == null
      && pEntity.getPaymentTotal().compareTo(BigDecimal.ZERO) != 0) {
      throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
        "reverse_payments_first");
    }
    if (pEntity.getReversedId() == null) {
      calculateTotalPayment(pReqVars, pEntity);
    }
    if (!pEntity.getIsNew()) {
      pReqVars.put("DebtorCreditortaxDestinationdeepLevel", 2);
    }
  }

  /**
   * <p>Make other entries include reversing if it's need when save.</p>
   * @param pReqVars additional param
   * @param pEntity entity
   * @param pRequestData Request Data
   * @param pIsNew if entity was new
   * @throws Exception - an exception
   **/
  @Override
  public final void makeOtherEntries(final Map<String, Object> pReqVars,
    final SalesInvoice pEntity, final IRequestData pRequestData,
      final boolean pIsNew) throws Exception {
    String actionAdd = pRequestData.getParameter("actionAdd");
    if ("makeAccEntries".equals(actionAdd)) {
      if (pEntity.getReversedId() != null) {
        //reverse none-reversed lines:
        SalesInvoiceLine sil = new SalesInvoiceLine();
        SalesInvoice reversed = new SalesInvoice();
        pEntity.setForeignCurrency(reversed.getForeignCurrency());
        pEntity.setForeignSubtotal(reversed.getForeignSubtotal().negate());
        pEntity.setForeignTotalTaxes(reversed.getForeignTotalTaxes().negate());
        pEntity.setForeignTotal(reversed.getForeignTotal().negate());
        reversed.setItsId(pEntity.getReversedId());
        sil.setItsOwner(reversed);
        List<SalesInvoiceLine> reversedLines = getSrvOrm().
          retrieveListForField(pReqVars, sil, "itsOwner");
        String langDef = (String) pReqVars.get("langDef");
        for (SalesInvoiceLine reversedLine : reversedLines) {
          if (reversedLine.getReversedId() == null) {
            SalesInvoiceLine reversingLine = new SalesInvoiceLine();
            reversingLine.setIdDatabaseBirth(getSrvOrm().getIdDatabase());
            reversingLine.setReversedId(reversedLine.getItsId());
            reversingLine.setInvItem(reversedLine.getInvItem());
            reversingLine.setUnitOfMeasure(reversedLine.getUnitOfMeasure());
            reversingLine.setItsPrice(reversedLine.getItsPrice());
            reversingLine.setItsQuantity(reversedLine.getItsQuantity()
              .negate());
            reversingLine.setItsTotal(reversedLine.getItsTotal().negate());
            reversingLine.setSubtotal(reversedLine.getSubtotal().negate());
            reversingLine.setTotalTaxes(reversedLine.getTotalTaxes().negate());
            reversingLine.setTaxesDescription(reversedLine
              .getTaxesDescription());
            reversingLine.setForeignPrice(reversedLine.getForeignPrice());
            reversingLine.setForeignSubtotal(reversedLine.getForeignSubtotal()
              .negate());
            reversingLine.setForeignTotalTaxes(reversedLine
              .getForeignTotalTaxes().negate());
            reversingLine.setForeignTotal(reversedLine.getForeignTotal()
              .negate());
            reversingLine.setIsNew(true);
            reversingLine.setItsOwner(pEntity);
            reversingLine.setDescription(getSrvI18n()
              .getMsg("reversed_n", langDef) + reversedLine.getIdDatabaseBirth()
                + "-" + reversedLine.getItsId()); //local
            getSrvOrm().insertEntity(pReqVars, reversingLine);
            reversingLine.setIsNew(false);
            getSrvWarehouseEntry().reverseDraw(pReqVars, reversingLine);
            getSrvCogsEntry().reverseDraw(pReqVars, reversingLine,
              pEntity.getItsDate(), pEntity.getItsId());
            String descr;
            if (reversedLine.getDescription() == null) {
              descr = "";
            } else {
              descr = reversedLine.getDescription();
            }
            reversedLine.setDescription(descr
              + " " + getSrvI18n().getMsg("reversing_n", langDef)
                + reversingLine.getIdDatabaseBirth() + "-"
                  + reversingLine.getItsId());
            reversedLine.setReversedId(reversingLine.getItsId());
            getSrvOrm().updateEntity(pReqVars, reversedLine);
            SalesInvoiceGoodsTaxLine pigtlt = new SalesInvoiceGoodsTaxLine();
            pigtlt.setItsOwner(reversedLine);
            List<SalesInvoiceGoodsTaxLine> tls = getSrvOrm()
              .retrieveListForField(pReqVars, pigtlt, "itsOwner");
            for (SalesInvoiceGoodsTaxLine pigtl : tls) {
              getSrvOrm().deleteEntity(pReqVars, pigtl);
            }
          }
        }
        SalesInvoiceServiceLine sisl = new SalesInvoiceServiceLine();
        sisl.setItsOwner(reversed);
        List<SalesInvoiceServiceLine> revServLines = getSrvOrm().
          retrieveListForField(pReqVars, sisl, "itsOwner");
        for (SalesInvoiceServiceLine reversedLine : revServLines) {
          if (reversedLine.getReversedId() == null) {
            SalesInvoiceServiceLine reversingLine =
              new SalesInvoiceServiceLine();
            reversingLine.setIdDatabaseBirth(getSrvOrm().getIdDatabase());
            reversingLine.setReversedId(reversedLine.getItsId());
            reversingLine.setService(reversedLine.getService());
            reversingLine.setItsPrice(reversedLine.getItsPrice().negate());
            reversingLine.setUnitOfMeasure(reversedLine.getUnitOfMeasure());
            reversingLine.setItsQuantity(reversedLine.getItsQuantity()
              .negate());
            reversingLine.setSubtotal(reversedLine.getSubtotal().negate());
            reversingLine.setItsTotal(reversedLine.getItsTotal().negate());
            reversingLine.setTotalTaxes(reversedLine.getTotalTaxes().negate());
            reversingLine.setTaxesDescription(reversedLine
              .getTaxesDescription());
            reversingLine.setForeignPrice(reversedLine.getForeignPrice());
            reversingLine.setForeignSubtotal(reversedLine.getForeignSubtotal()
              .negate());
            reversingLine.setForeignTotalTaxes(reversedLine
              .getForeignTotalTaxes().negate());
            reversingLine.setForeignTotal(reversedLine.getForeignTotal()
              .negate());
            reversingLine.setIsNew(true);
            reversingLine.setItsOwner(pEntity);
            getSrvOrm().insertEntity(pReqVars, reversingLine);
            reversingLine.setIsNew(false);
            reversedLine.setReversedId(reversingLine.getItsId());
            getSrvOrm().updateEntity(pReqVars, reversedLine);
            SalesInvoiceServiceTaxLine pigtlt =
              new SalesInvoiceServiceTaxLine();
            pigtlt.setItsOwner(reversedLine);
            List<SalesInvoiceServiceTaxLine> tls = getSrvOrm()
              .retrieveListForField(pReqVars, pigtlt, "itsOwner");
            for (SalesInvoiceServiceTaxLine pigtl : tls) {
              getSrvOrm().deleteEntity(pReqVars, pigtl);
            }
          }
        }
        SalesInvoiceTaxLine sitl = new SalesInvoiceTaxLine();
        sitl.setItsOwner(reversed);
        List<SalesInvoiceTaxLine> reversedTaxLines = getSrvOrm().
          retrieveListForField(pReqVars, sitl, "itsOwner");
        for (SalesInvoiceTaxLine reversedLine : reversedTaxLines) {
          if (reversedLine.getReversedId() == null) {
            SalesInvoiceTaxLine reversingLine = new SalesInvoiceTaxLine();
            reversingLine.setIdDatabaseBirth(getSrvOrm().getIdDatabase());
            reversingLine.setReversedId(reversedLine.getItsId());
            reversingLine.setItsTotal(reversedLine.getItsTotal().negate());
            reversingLine.setForeignTotalTaxes(reversedLine
              .getForeignTotalTaxes().negate());
            reversingLine.setTax(reversedLine.getTax());
            reversingLine.setIsNew(true);
            reversingLine.setItsOwner(pEntity);
            getSrvOrm().insertEntity(pReqVars, reversingLine);
            reversingLine.setIsNew(false);
            reversedLine.setReversedId(reversingLine.getItsId());
            getSrvOrm().updateEntity(pReqVars, reversedLine);
          }
        }
      }
      if (pEntity.getPrepaymentFrom() != null) {
        if (pEntity.getReversedId() != null) {
          pEntity.getPrepaymentFrom().setSalesInvoiceId(null);
        } else {
          pEntity.getPrepaymentFrom().setSalesInvoiceId(pEntity.getItsId());
        }
        getSrvOrm().updateEntity(pReqVars, pEntity.getPrepaymentFrom());
      }
    }
  }

  /**
   * <p>Check other fraud update e.g. prevent change completed unaccounted
   * manufacturing process.</p>
   * @param pReqVars additional param
   * @param pEntity entity
   * @param pRequestData Request Data
   * @param pOldEntity old saved entity
   * @throws Exception - an exception
   **/
  @Override
  public final void checkOtherFraudUpdate(final Map<String, Object> pReqVars,
    final SalesInvoice pEntity, final IRequestData pRequestData,
      final SalesInvoice pOldEntity) throws Exception {
    if (pEntity.getItsTotal().compareTo(BigDecimal.ZERO) == 1) {
      if (pOldEntity.getCustomer().getTaxDestination() != null && !pOldEntity
        .getCustomer().getItsId().equals(pEntity.getCustomer().getItsId())) {
        throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
          "can_not_cange_customer_with_another_tax_destination");
      }
      if (!pOldEntity.getOmitTaxes().equals(pEntity.getOmitTaxes())
        || !pOldEntity.getPriceIncTax().equals(pEntity.getPriceIncTax())) {
        throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
          "can_not_change_tax_method");
      }
    }
    pReqVars.remove("DebtorCreditortaxDestinationdeepLevel");
  }

  /**
   * <p>Additional check document for ready to account (make acc.entries).</p>
   * @param pReqVars additional param
   * @param pEntity entity
   * @param pRequestData Request Data
   * @throws Exception - an exception if don't
   **/
  @Override
  public final void addCheckIsReadyToAccount(
    final Map<String, Object> pReqVars,
      final SalesInvoice pEntity,
        final IRequestData pRequestData) throws Exception {
    // nothing
  }

  //Utils:
  /**
   * <p>Calculate Total Payment.</p>
   * @param pReqVars additional param
   * @param pEntity SalesInvoice
   * @throws Exception - an exception
   **/
  public final void calculateTotalPayment(
    final Map<String, Object> pReqVars,
      final SalesInvoice pEntity) throws Exception {
    String langDef = (String) pReqVars.get("langDef");
    DateFormat dateFormat = DateFormat.getDateTimeInstance(
      DateFormat.MEDIUM, DateFormat.SHORT, new Locale(langDef));
    if (pEntity.getPrepaymentFrom() != null) {
      if (pEntity.getForeignCurrency() != null) {
        pEntity.setPaymentTotal(pEntity.getPrepaymentFrom().getForeignTotal());
      } else {
        pEntity.setPaymentTotal(pEntity.getPrepaymentFrom().getItsTotal());
      }
      pEntity.setPaymentDescription(getSrvI18n().getMsg(PrepaymentFrom
    .class.getSimpleName() + "short", langDef) + " #" + pEntity
  .getPrepaymentFrom().getIdDatabaseBirth() + "-" + pEntity.getPrepaymentFrom()
    .getItsId() + ", " + dateFormat.format(pEntity.getPrepaymentFrom()
      .getItsDate()) + ", " + pEntity.getPaymentTotal());
    } else {
      pEntity.setPaymentTotal(BigDecimal.ZERO);
      pEntity.setPaymentDescription("");
    }
    if (!pEntity.getIsNew()) {
      List<PaymentFrom> payments = getSrvOrm()
        .retrieveListWithConditions(pReqVars, PaymentFrom.class,
          "where PAYMENTFROM.HASMADEACCENTRIES=1 and PAYMENTFROM.REVERSEDID"
            + " is null and SALESINVOICE=" + pEntity.getItsId());
      for (PaymentFrom payment : payments) {
        if (pEntity.getForeignCurrency() != null) {
          pEntity.setPaymentTotal(pEntity.getPaymentTotal()
            .add(payment.getForeignTotal()));
        } else {
          pEntity.setPaymentTotal(pEntity.getPaymentTotal()
            .add(payment.getItsTotal()));
        }
        pEntity.setPaymentDescription(pEntity.getPaymentDescription() + " "
    + getSrvI18n().getMsg(PaymentFrom.class.getSimpleName() + "short", langDef)
      + " #" + payment.getIdDatabaseBirth() + "-" + payment.getItsId()
        + ", " + dateFormat.format(payment.getItsDate())
          + ", " + payment.getItsTotal());
      }
    }
  }
}

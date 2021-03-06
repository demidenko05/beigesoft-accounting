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

import java.util.Set;
import java.util.HashSet;
import java.math.BigDecimal;
import java.util.Map;
import java.util.List;
import java.util.Locale;
import java.text.DateFormat;

import org.beigesoft.exception.ExceptionWithCode;
import org.beigesoft.model.IRequestData;
import org.beigesoft.accounting.persistable.PaymentTo;
import org.beigesoft.accounting.persistable.PrepaymentTo;
import org.beigesoft.accounting.persistable.PurchaseInvoice;
import org.beigesoft.accounting.persistable.PurchaseInvoiceLine;
import org.beigesoft.accounting.persistable.PurchaseInvoiceGoodsTaxLine;
import org.beigesoft.accounting.persistable.PurchaseInvoiceServiceTaxLine;
import org.beigesoft.accounting.persistable.PurchaseInvoiceServiceLine;
import org.beigesoft.accounting.persistable.PurchaseInvoiceTaxLine;

/**
 * <p>Process that saves vendor invoice.</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class PrcPurchaseInvoiceSave<RS>
  extends APrcAccDocFullSave<RS, PurchaseInvoice> {

  /**
   * <p>Make save preparations before insert/update block if it's need.</p>
   * @param pReqVars additional param
   * @param pEntity entity
   * @param pRequestData Request Data
   * @throws Exception - an exception
   **/
  @Override
  public final void makeFirstPrepareForSave(final Map<String, Object> pReqVars,
    final PurchaseInvoice pEntity,
      final IRequestData pRequestData) throws Exception {
    if (pEntity.getPrepaymentTo() != null) {
      pEntity.setPrepaymentTo(getSrvOrm()
        .retrieveEntity(pReqVars, pEntity.getPrepaymentTo()));
      if (pEntity.getReversedId() == null && pEntity.getPrepaymentTo()
        .getPurchaseInvoiceId() != null && !pEntity.getHasMadeAccEntries()) {
        throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
          "prepayment_already_in_use");
      }
      if (pEntity.getReversedId() == null && !pEntity.getPrepaymentTo()
        .getVendor().getItsId().equals(pEntity.getVendor().getItsId())) {
        throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
          "prepayment_for_different_vendor");
      }
    }
    if (pEntity.getReversedId() != null && pEntity.getPrepaymentTo() != null
      && pEntity.getPaymentTotal().compareTo(pEntity.getPrepaymentTo()
        .getItsTotal()) != 0) {
      throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
        "reverse_payments_first");
    }
    if (pEntity.getReversedId() != null && pEntity.getPrepaymentTo() == null
      && pEntity.getPaymentTotal().compareTo(BigDecimal.ZERO) != 0) {
      throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
        "reverse_payments_first");
    }
    if (pEntity.getReversedId() == null) {
      calculateTotalPayment(pReqVars, pEntity);
    }
    if (!pEntity.getIsNew()) {
      pReqVars.put("DebtorCreditortaxDestinationdeepLevel", 2);
      Set<String> ndFlDc = new HashSet<String>();
      ndFlDc.add("itsId");
      ndFlDc.add("isForeigner");
      ndFlDc.add("taxDestination");
      pReqVars.put("DebtorCreditorneededFields", ndFlDc);
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
    final PurchaseInvoice pEntity, final IRequestData pRequestData,
      final boolean pIsNew) throws Exception {
    String actionAdd = pRequestData.getParameter("actionAdd");
    if ("makeAccEntries".equals(actionAdd)) {
      if (pEntity.getReversedId() != null) {
        //reverse none-reversed lines:
        PurchaseInvoiceLine pil = new PurchaseInvoiceLine();
        PurchaseInvoice reversed = new PurchaseInvoice();
        reversed.setItsId(pEntity.getReversedId());
        pil.setItsOwner(reversed);
        List<PurchaseInvoiceLine> reversedLines = getSrvOrm().
          retrieveListForField(pReqVars, pil, "itsOwner");
        String langDef = (String) pReqVars.get("langDef");
        for (PurchaseInvoiceLine reversedLine : reversedLines) {
          if (reversedLine.getReversedId() == null) {
            if (!reversedLine.getItsQuantity()
              .equals(reversedLine.getTheRest())) {
              throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
                "where_is_withdrawals_from_this_source");
            }
            PurchaseInvoiceLine reversingLine = new PurchaseInvoiceLine();
            reversingLine.setIdDatabaseBirth(getSrvOrm().getIdDatabase());
            reversingLine.setReversedId(reversedLine.getItsId());
            reversingLine.setWarehouseSite(reversedLine.getWarehouseSite());
            reversingLine.setInvItem(reversedLine.getInvItem());
            reversingLine.setUnitOfMeasure(reversedLine.getUnitOfMeasure());
            reversingLine.setItsCost(reversedLine.getItsCost());
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
              .getMsg("reversed_entry_n", langDef) + reversedLine
                .getIdDatabaseBirth() + "-" + reversedLine.getItsId()); //local
            getSrvOrm().insertEntity(pReqVars, reversingLine);
            reversingLine.setIsNew(false);
            getSrvWarehouseEntry().load(pReqVars, reversingLine,
              reversingLine.getWarehouseSite());
            String descr;
            if (reversedLine.getDescription() == null) {
              descr = "";
            } else {
              descr = reversedLine.getDescription();
            }
            reversedLine.setDescription(descr + " " + getSrvI18n().getMsg(
              "reversing_entry_n", langDef) + reversingLine.getIdDatabaseBirth()
                + "-" + reversingLine.getItsId()); //only local
            reversedLine.setReversedId(reversingLine.getItsId());
            reversedLine.setTheRest(BigDecimal.ZERO);
            getSrvOrm().updateEntity(pReqVars, reversedLine);
            PurchaseInvoiceGoodsTaxLine pigtlt =
              new PurchaseInvoiceGoodsTaxLine();
            pigtlt.setItsOwner(reversedLine);
            List<PurchaseInvoiceGoodsTaxLine> tls = getSrvOrm()
              .retrieveListForField(pReqVars, pigtlt, "itsOwner");
            for (PurchaseInvoiceGoodsTaxLine pigtl : tls) {
              getSrvOrm().deleteEntity(pReqVars, pigtl);
            }
          }
        }
        PurchaseInvoiceServiceLine pisl = new PurchaseInvoiceServiceLine();
        pisl.setItsOwner(reversed);
        List<PurchaseInvoiceServiceLine> revServLines = getSrvOrm().
          retrieveListForField(pReqVars, pisl, "itsOwner");
        for (PurchaseInvoiceServiceLine reversedLine : revServLines) {
          if (reversedLine.getReversedId() == null) {
            PurchaseInvoiceServiceLine reversingLine =
              new PurchaseInvoiceServiceLine();
            reversingLine.setIdDatabaseBirth(getSrvOrm().getIdDatabase());
            reversingLine.setReversedId(reversedLine.getItsId());
            reversingLine.setService(reversedLine.getService());
            reversingLine.setAccExpense(reversedLine.getAccExpense());
            reversingLine.setItsCost(reversedLine.getItsCost().negate());
            reversingLine.setUnitOfMeasure(reversedLine.getUnitOfMeasure());
            reversingLine.setItsCost(reversedLine.getItsCost());
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
            getSrvOrm().insertEntity(pReqVars, reversingLine);
            reversingLine.setIsNew(false);
            reversedLine.setReversedId(reversingLine.getItsId());
            getSrvOrm().updateEntity(pReqVars, reversedLine);
            PurchaseInvoiceServiceTaxLine pigtlt =
              new PurchaseInvoiceServiceTaxLine();
            pigtlt.setItsOwner(reversedLine);
            List<PurchaseInvoiceServiceTaxLine> tls = getSrvOrm()
              .retrieveListForField(pReqVars, pigtlt, "itsOwner");
            for (PurchaseInvoiceServiceTaxLine pigtl : tls) {
              getSrvOrm().deleteEntity(pReqVars, pigtl);
            }
          }
        }
        PurchaseInvoiceTaxLine pitl = new PurchaseInvoiceTaxLine();
        pitl.setItsOwner(reversed);
        List<PurchaseInvoiceTaxLine> reversedTaxLines = getSrvOrm().
          retrieveListForField(pReqVars, pitl, "itsOwner");
        for (PurchaseInvoiceTaxLine reversedLine : reversedTaxLines) {
          if (reversedLine.getReversedId() == null) {
            PurchaseInvoiceTaxLine reversingLine = new PurchaseInvoiceTaxLine();
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
      if (pEntity.getPrepaymentTo() != null) {
        if (pEntity.getReversedId() != null) {
          pEntity.getPrepaymentTo().setPurchaseInvoiceId(null);
        } else {
          pEntity.getPrepaymentTo().setPurchaseInvoiceId(pEntity.getItsId());
        }
        getSrvOrm().updateEntity(pReqVars, pEntity.getPrepaymentTo());
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
    final PurchaseInvoice pEntity, final IRequestData pRequestData,
      final PurchaseInvoice pOldEntity) throws Exception {
    pReqVars.remove("DebtorCreditortaxDestinationdeepLevel");
    pReqVars.remove("DebtorCreditorneededFields");
    if (pEntity.getItsTotal().compareTo(BigDecimal.ZERO) == 1) {
      if (!pOldEntity.getVendor().getItsId()
        .equals(pEntity.getVendor().getItsId())) {
        pEntity.setVendor(getSrvOrm()
          .retrieveEntity(pReqVars, pEntity.getVendor()));
        if (pOldEntity.getVendor().getTaxDestination() != null
            || pEntity.getVendor().getTaxDestination() != null) {
          throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
            "can_not_cange_customer_with_another_tax_destination");
        }
      }
      if (!pOldEntity.getOmitTaxes().equals(pEntity.getOmitTaxes())
        || !pOldEntity.getPriceIncTax().equals(pEntity.getPriceIncTax())) {
        throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
          "can_not_change_tax_method");
      }
    }
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
      final PurchaseInvoice pEntity,
        final IRequestData pRequestData) throws Exception {
    // nothing
  }

  //Utils:
  /**
   * <p>Calculate Total Payment.</p>
   * @param pReqVars additional param
   * @param pEntity PurchaseInvoice
   * @throws Exception - an exception
   **/
  public final void calculateTotalPayment(final Map<String, Object> pReqVars,
    final PurchaseInvoice pEntity) throws Exception {
    String langDef = (String) pReqVars.get("langDef");
    DateFormat dateFormat = DateFormat.getDateTimeInstance(
    DateFormat.MEDIUM, DateFormat.SHORT, new Locale(langDef));
    if (pEntity.getPrepaymentTo() != null) {
      if (pEntity.getForeignCurrency() != null) {
        pEntity.setPaymentTotal(pEntity.getPrepaymentTo().getForeignTotal());
      } else {
        pEntity.setPaymentTotal(pEntity.getPrepaymentTo().getItsTotal());
      }
      pEntity.setPaymentDescription(getSrvI18n().getMsg(PrepaymentTo
        .class.getSimpleName() + "short", langDef) + " #" + pEntity
          .getPrepaymentTo().getIdDatabaseBirth() + "-"
            + pEntity.getPrepaymentTo().getItsId() + ", " //local
          + dateFormat.format(pEntity.getPrepaymentTo().getItsDate())
        + ", " + pEntity.getPaymentTotal());
    } else {
      pEntity.setPaymentTotal(BigDecimal.ZERO);
      pEntity.setPaymentDescription("");
    }
    List<PaymentTo> payments = getSrvOrm()
      .retrieveListWithConditions(pReqVars, PaymentTo.class,
        "where PAYMENTTO.HASMADEACCENTRIES=1 and PAYMENTTO.REVERSEDID"
          + " is null and PURCHASEINVOICE=" + pEntity.getItsId());
    for (PaymentTo payment : payments) {
      if (pEntity.getForeignCurrency() != null) {
        pEntity.setPaymentTotal(pEntity.getPaymentTotal()
          .add(payment.getForeignTotal()));
      } else {
        pEntity.setPaymentTotal(pEntity.getPaymentTotal()
          .add(payment.getItsTotal()));
      }
      pEntity.setPaymentDescription(pEntity.getPaymentDescription() + " "
    + getSrvI18n().getMsg(PaymentTo.class.getSimpleName() + "short", langDef)
      + " #" + payment.getIdDatabaseBirth() + "-" + payment.getItsId()
        + ", " + dateFormat.format(payment.getItsDate()) + ", "
          + payment.getItsTotal());
    }
  }
}

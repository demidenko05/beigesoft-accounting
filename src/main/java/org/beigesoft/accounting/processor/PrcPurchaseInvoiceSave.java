package org.beigesoft.accounting.processor;

/*
 * Copyright (c) 2017 Beigesoft ™
 *
 * Licensed under the GNU General Public License (GPL), Version 2.0
 * (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html
 */

import java.math.BigDecimal;
import java.util.Map;
import java.util.List;

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
 * <p>Process that save vendor invoice.</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class PrcPurchaseInvoiceSave<RS>
  extends APrcAccDocFullSave<RS, PurchaseInvoice> {

  /**
   * <p>Make save preparations before insert/update block if it's need.</p>
   * @param pAddParam additional param
   * @param pEntity entity
   * @param pRequestData Request Data
   * @throws Exception - an exception
   **/
  @Override
  public final void makeFirstPrepareForSave(final Map<String, Object> pAddParam,
    final PurchaseInvoice pEntity,
      final IRequestData pRequestData) throws Exception {
    if (pEntity.getPrepaymentTo() != null) {
      pEntity.setPrepaymentTo(getSrvOrm()
        .retrieveEntity(pAddParam, pEntity.getPrepaymentTo()));
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
    calculateTotalPayment(pAddParam, pEntity);
  }

  /**
   * <p>Make other entries include reversing if it's need when save.</p>
   * @param pAddParam additional param
   * @param pEntity entity
   * @param pRequestData Request Data
   * @param pIsNew if entity was new
   * @throws Exception - an exception
   **/
  @Override
  public final void makeOtherEntries(final Map<String, Object> pAddParam,
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
          retrieveListForField(pAddParam, pil, "itsOwner");
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
            reversingLine.setIsNew(true);
            reversingLine.setItsOwner(pEntity);
            reversingLine.setDescription(getSrvI18n().getMsg("reversed_entry_n")
              + reversedLine.getIdDatabaseBirth() + "-"
                + reversedLine.getItsId()); //local
            getSrvOrm().insertEntity(pAddParam, reversingLine);
            getSrvWarehouseEntry().load(pAddParam, reversingLine,
              reversingLine.getWarehouseSite());
            String descr;
            if (reversedLine.getDescription() == null) {
              descr = "";
            } else {
              descr = reversedLine.getDescription();
            }
            reversedLine.setDescription(descr + " " + getSrvI18n()
              .getMsg("reversing_entry_n") + reversingLine.getIdDatabaseBirth()
                + "-" + reversingLine.getItsId()); //only local
            reversedLine.setReversedId(reversingLine.getItsId());
            reversedLine.setTheRest(BigDecimal.ZERO);
            getSrvOrm().updateEntity(pAddParam, reversedLine);
            PurchaseInvoiceGoodsTaxLine pigtlt =
              new PurchaseInvoiceGoodsTaxLine();
            pigtlt.setItsOwner(reversedLine);
            List<PurchaseInvoiceGoodsTaxLine> tls = getSrvOrm()
              .retrieveListForField(pAddParam, pigtlt, "itsOwner");
            for (PurchaseInvoiceGoodsTaxLine pigtl : tls) {
              getSrvOrm().deleteEntity(pAddParam, pigtl);
            }
          }
        }
        PurchaseInvoiceServiceLine pisl = new PurchaseInvoiceServiceLine();
        pisl.setItsOwner(reversed);
        List<PurchaseInvoiceServiceLine> revServLines = getSrvOrm().
          retrieveListForField(pAddParam, pisl, "itsOwner");
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
            reversingLine.setIsNew(true);
            reversingLine.setItsOwner(pEntity);
            getSrvOrm().insertEntity(pAddParam, reversingLine);
            reversedLine.setReversedId(reversingLine.getItsId());
            getSrvOrm().updateEntity(pAddParam, reversedLine);
            PurchaseInvoiceServiceTaxLine pigtlt =
              new PurchaseInvoiceServiceTaxLine();
            pigtlt.setItsOwner(reversedLine);
            List<PurchaseInvoiceServiceTaxLine> tls = getSrvOrm()
              .retrieveListForField(pAddParam, pigtlt, "itsOwner");
            for (PurchaseInvoiceServiceTaxLine pigtl : tls) {
              getSrvOrm().deleteEntity(pAddParam, pigtl);
            }
          }
        }
        PurchaseInvoiceTaxLine pitl = new PurchaseInvoiceTaxLine();
        pitl.setItsOwner(reversed);
        List<PurchaseInvoiceTaxLine> reversedTaxLines = getSrvOrm().
          retrieveListForField(pAddParam, pitl, "itsOwner");
        for (PurchaseInvoiceTaxLine reversedLine : reversedTaxLines) {
          if (reversedLine.getReversedId() == null) {
            PurchaseInvoiceTaxLine reversingLine = new PurchaseInvoiceTaxLine();
            reversingLine.setIdDatabaseBirth(getSrvOrm().getIdDatabase());
            reversingLine.setReversedId(reversedLine.getItsId());
            reversingLine.setItsTotal(reversedLine.getItsTotal().negate());
            reversingLine.setTax(reversedLine.getTax());
            reversingLine.setIsNew(true);
            reversingLine.setItsOwner(pEntity);
            getSrvOrm().insertEntity(pAddParam, reversingLine);
            reversedLine.setReversedId(reversingLine.getItsId());
            getSrvOrm().updateEntity(pAddParam, reversedLine);
          }
        }
      }
      if (pEntity.getPrepaymentTo() != null) {
        if (pEntity.getReversedId() != null) {
          pEntity.getPrepaymentTo().setPurchaseInvoiceId(null);
        } else {
          pEntity.getPrepaymentTo().setPurchaseInvoiceId(pEntity.getItsId());
        }
        getSrvOrm().updateEntity(pAddParam, pEntity.getPrepaymentTo());
      }
    }
  }

  /**
   * <p>Check other fraud update e.g. prevent change completed unaccounted
   * manufacturing process.</p>
   * @param pAddParam additional param
   * @param pEntity entity
   * @param pRequestData Request Data
   * @param pOldEntity old saved entity
   * @throws Exception - an exception
   **/
  @Override
  public final void checkOtherFraudUpdate(final Map<String, Object> pAddParam,
    final PurchaseInvoice pEntity, final IRequestData pRequestData,
      final PurchaseInvoice pOldEntity) throws Exception {
    // nothing
  }

  /**
   * <p>Additional check document for ready to account (make acc.entries).</p>
   * @param pAddParam additional param
   * @param pEntity entity
   * @param pRequestData Request Data
   * @throws Exception - an exception if don't
   **/
  @Override
  public final void addCheckIsReadyToAccount(
    final Map<String, Object> pAddParam,
      final PurchaseInvoice pEntity,
        final IRequestData pRequestData) throws Exception {
    // nothing
  }

  //Utils:
  /**
   * <p>Calculate Total Payment.</p>
   * @param pAddParam additional param
   * @param pEntity PurchaseInvoice
   * @throws Exception - an exception
   **/
  public final void calculateTotalPayment(final Map<String, Object> pAddParam,
    final PurchaseInvoice pEntity) throws Exception {
    if (pEntity.getPrepaymentTo() != null) {
      pEntity.setPaymentTotal(pEntity.getPrepaymentTo().getItsTotal());
      pEntity.setPaymentDescription(getSrvI18n().getMsg(PrepaymentTo
        .class.getSimpleName() + "short") + " #" + pEntity.getPrepaymentTo()
          .getIdDatabaseBirth() + "-"
            + pEntity.getPrepaymentTo().getItsId() + ", " //local
          + getDateFormatter().format(pEntity.getPrepaymentTo().getItsDate())
        + ", " + pEntity.getPaymentTotal());
    } else {
      pEntity.setPaymentTotal(BigDecimal.ZERO);
      pEntity.setPaymentDescription("");
    }
    List<PaymentTo> payments = getSrvOrm()
      .retrieveListWithConditions(pAddParam, PaymentTo.class,
        "where PAYMENTTO.HASMADEACCENTRIES=1 and PAYMENTTO.REVERSEDID"
          + " is null and PURCHASEINVOICE=" + pEntity.getItsId());
    for (PaymentTo payment : payments) {
      pEntity.setPaymentTotal(pEntity.getPaymentTotal()
        .add(payment.getItsTotal()));
      pEntity.setPaymentDescription(pEntity.getPaymentDescription() + " "
        + getSrvI18n().getMsg(PaymentTo.class.getSimpleName() + "short")
          + " #" + payment.getIdDatabaseBirth() + "-" + payment.getItsId()
        + ", " + getDateFormatter().format(payment.getItsDate()) + ", "
          + payment.getItsTotal());
    }
  }
}

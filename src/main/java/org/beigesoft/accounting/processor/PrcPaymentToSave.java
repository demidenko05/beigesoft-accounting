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

import java.util.Map;

import org.beigesoft.exception.ExceptionWithCode;
import org.beigesoft.model.IRequestData;
import org.beigesoft.accounting.persistable.PaymentTo;
import org.beigesoft.accounting.persistable.PurchaseInvoice;

/**
 * <p>Service that save PaymentTo into DB.</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class PrcPaymentToSave<RS> extends APrcAccDocSave<RS, PaymentTo> {

  /**
   * <p>Processor PurchaseInvoice Save.</p>
   **/
  private PrcPurchaseInvoiceSave<RS> prcPurchaseInvoiceSave;

  //To override:
  /**
   * <p>Make save preparations before insert/update block if it's need.</p>
   * @param pAddParam additional param
   * @param pEntity entity
   * @param pRequestData Request Data
   * @throws Exception - an exception
   **/
  @Override
  public final void makeFirstPrepareForSave(final Map<String, Object> pAddParam,
    final PaymentTo pEntity, final IRequestData pRequestData) throws Exception {
    //BeigeORM refresh:
    pEntity.setAccCash(getSrvOrm()
      .retrieveEntity(pAddParam, pEntity.getAccCash()));
    if (pEntity.getAccCash().getSubaccType() != null
      && pEntity.getSubaccCashId() == null) {
      throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
        "select_subaccount");
    }
    if (pEntity.getPurchaseInvoice() != null) {
      PurchaseInvoice purchaseInvoice = getSrvOrm()
        .retrieveEntity(pAddParam, pEntity.getPurchaseInvoice());
      if (!purchaseInvoice.getHasMadeAccEntries()
        || purchaseInvoice.getReversedId() != null) {
        throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
          "purchase_invoice_must_be_accounted");
      }
      pEntity.setPurchaseInvoice(purchaseInvoice);
    }
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
    final PaymentTo pEntity, final IRequestData pRequestData,
      final boolean pIsNew) throws Exception {
    String actionAdd = pRequestData.getParameter("actionAdd");
    if ("makeAccEntries".equals(actionAdd)) {
      this.prcPurchaseInvoiceSave.calculateTotalPayment(pAddParam,
        pEntity.getPurchaseInvoice());
      getSrvOrm().updateEntity(pAddParam, pEntity.getPurchaseInvoice());
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
    final PaymentTo pEntity, final IRequestData pRequestData,
      final PaymentTo pOldEntity) throws Exception {
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
      final PaymentTo pEntity,
        final IRequestData pRequestData) throws Exception {
    // nothing
  }

  //Simple getters and setters:
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
}

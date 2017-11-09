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
import org.beigesoft.accounting.persistable.PaymentFrom;
import org.beigesoft.accounting.persistable.SalesInvoice;

/**
 * <p>Service that save PaymentFrom into DB.</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class PrcPaymentFromSave<RS> extends APrcAccDocSave<RS, PaymentFrom> {

  /**
   * <p>Processor SalesInvoice Save.</p>
   **/
  private PrcSalesInvoiceSave<RS> prcSalesInvoiceSave;

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
    final PaymentFrom pEntity,
      final IRequestData pRequestData) throws Exception {
    //BeigeORM refresh:
    pEntity.setAccCash(getSrvOrm()
      .retrieveEntity(pAddParam, pEntity.getAccCash()));
    if (pEntity.getAccCash().getSubaccType() != null
      && pEntity.getSubaccCashId() == null) {
      throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
        "select_subaccount");
    }
    if (pEntity.getSalesInvoice() != null) {
      SalesInvoice salesInvoice = getSrvOrm()
        .retrieveEntity(pAddParam, pEntity.getSalesInvoice());
      if (!salesInvoice.getHasMadeAccEntries()
        || salesInvoice.getReversedId() != null) {
        throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
          "sales_invoice_must_be_accounted");
      }
      pEntity.setSalesInvoice(salesInvoice);
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
    final PaymentFrom pEntity, final IRequestData pRequestData,
      final boolean pIsNew) throws Exception {
    String actionAdd = pRequestData.getParameter("actionAdd");
    if ("makeAccEntries".equals(actionAdd)) {
      this.prcSalesInvoiceSave.calculateTotalPayment(pAddParam, pEntity
        .getSalesInvoice());
      getSrvOrm().updateEntity(pAddParam, pEntity.getSalesInvoice());
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
    final PaymentFrom pEntity, final IRequestData pRequestData,
      final PaymentFrom pOldEntity) throws Exception {
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
      final PaymentFrom pEntity,
        final IRequestData pRequestData) throws Exception {
    // nothing
  }

  //Simple getters and setters:
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

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
import org.beigesoft.accounting.persistable.PurchaseInvoice;
import org.beigesoft.accounting.persistable.SalesReturn;
import org.beigesoft.accounting.persistable.SalesReturnLine;
import org.beigesoft.accounting.persistable.SalesReturnTaxLine;

/**
 * <p>Process that save sales return.</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class PrcSalesReturnSave<RS>
  extends APrcAccDocFullSave<RS, SalesReturn> {

  /**
   * <p>Make save preparations before insert/update block if it's need.</p>
   * @param pAddParam additional param
   * @param pEntity entity
   * @param pRequestData Request Data
   * @throws Exception - an exception
   **/
  @Override
  public final void makeFirstPrepareForSave(final Map<String, Object> pAddParam,
    final SalesReturn pEntity,
      final IRequestData pRequestData) throws Exception {
    //nothing
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
    final SalesReturn pEntity, final IRequestData pRequestData,
      final boolean pIsNew) throws Exception {
    String actionAdd = pRequestData.getParameter("actionAdd");
    if ("makeAccEntries".equals(actionAdd)
      && pEntity.getReversedId() != null) {
      //reverse none-reversed lines:
      SalesReturnLine srl = new SalesReturnLine();
      SalesReturn reversed = new SalesReturn();
      reversed.setItsId(pEntity.getReversedId());
      srl.setItsOwner(reversed);
      List<SalesReturnLine> reversedLines = getSrvOrm().
        retrieveListForField(pAddParam, srl, "itsOwner");
      for (SalesReturnLine reversedLine : reversedLines) {
        if (reversedLine.getReversedId() == null) {
          if (!reversedLine.getItsQuantity()
            .equals(reversedLine.getTheRest())) {
            throw new ExceptionWithCode(PurchaseInvoice.SOURSE_IS_IN_USE,
              "There is withdrawals from this source!");
          }
          SalesReturnLine reversingLine = new SalesReturnLine();
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
          reversingLine.setDescription(getSrvI18n().getMsg("reversed_n")
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
          reversedLine.setDescription(descr
            + " " + getSrvI18n().getMsg("reversing_n") + reversingLine
              .getIdDatabaseBirth() + "-" + reversingLine.getItsId());
          reversedLine.setReversedId(reversingLine.getItsId());
          reversedLine.setTheRest(BigDecimal.ZERO);
          getSrvOrm().updateEntity(pAddParam, reversedLine);
        }
      }
      SalesReturnTaxLine srtl = new SalesReturnTaxLine();
      srtl.setItsOwner(reversed);
      List<SalesReturnTaxLine> reversedTaxLines = getSrvOrm().
        retrieveListForField(pAddParam, srtl, "itsOwner");
      for (SalesReturnTaxLine reversedLine : reversedTaxLines) {
        if (reversedLine.getReversedId() == null) {
          SalesReturnTaxLine reversingLine = new SalesReturnTaxLine();
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
    final SalesReturn pEntity, final IRequestData pRequestData,
      final SalesReturn pOldEntity) throws Exception {
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
      final SalesReturn pEntity,
        final IRequestData pRequestData) throws Exception {
    // nothing
  }
}

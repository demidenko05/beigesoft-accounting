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
import java.util.List;

import org.beigesoft.model.IRequestData;
import org.beigesoft.accounting.persistable.PurchaseReturn;
import org.beigesoft.accounting.persistable.PurchaseReturnLine;
import org.beigesoft.accounting.persistable.PurchaseReturnTaxLine;
import org.beigesoft.accounting.persistable.PurchaseReturnGoodsTaxLine;

/**
 * <p>Process that save purchase return.</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class PrcPurchaseReturnSave<RS>
  extends APrcAccDocUseMaterialSave<RS, PurchaseReturn> {

  /**
   * <p>Make save preparations before insert/update block if it's need.</p>
   * @param pAddParam additional param
   * @param pEntity entity
   * @param pRequestData Request Data
   * @throws Exception - an exception
   **/
  @Override
  public final void makeFirstPrepareForSave(final Map<String, Object> pAddParam,
    final PurchaseReturn pEntity,
      final IRequestData pRequestData) throws Exception {
    // nothing
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
    final PurchaseReturn pEntity, final IRequestData pRequestData,
      final boolean pIsNew) throws Exception {
    String actionAdd = pRequestData.getParameter("actionAdd");
    if ("makeAccEntries".equals(actionAdd)
      && pEntity.getReversedId() != null) {
      //reverse none-reversed lines:
      PurchaseReturnLine prl = new PurchaseReturnLine();
      PurchaseReturn reversed = new PurchaseReturn();
      reversed.setItsId(pEntity.getReversedId());
      prl.setItsOwner(reversed);
      List<PurchaseReturnLine> reversedLines = getSrvOrm().
        retrieveListForField(pAddParam, prl, "itsOwner");
      for (PurchaseReturnLine reversedLine : reversedLines) {
        if (reversedLine.getReversedId() == null) {
          PurchaseReturnLine reversingLine = new PurchaseReturnLine();
          reversingLine.setIdDatabaseBirth(getSrvOrm().getIdDatabase());
          reversingLine.setReversedId(reversedLine.getItsId());
          reversingLine.setPurchaseInvoiceLine(reversedLine
            .getPurchaseInvoiceLine());
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
              + reversedLine.getItsId());
          getSrvOrm().insertEntity(pAddParam, reversingLine);
          getSrvWarehouseEntry().reverseDraw(pAddParam, reversingLine);
          getSrvUseMaterialEntry().reverseDraw(pAddParam, reversingLine,
            pEntity.getItsDate(), pEntity.getItsId());
          String descr;
          if (reversedLine.getDescription() == null) {
            descr = "";
          } else {
            descr = reversedLine.getDescription();
          }
          reversedLine.setDescription(descr + " " + getSrvI18n()
            .getMsg("reversing_n") + reversingLine.getIdDatabaseBirth()
              + "-" + reversingLine.getItsId()); //local
          reversedLine.setReversedId(reversingLine.getItsId());
          getSrvOrm().updateEntity(pAddParam, reversedLine);
          PurchaseReturnGoodsTaxLine pigtlt =
            new PurchaseReturnGoodsTaxLine();
          pigtlt.setItsOwner(reversedLine);
          List<PurchaseReturnGoodsTaxLine> tls = getSrvOrm()
            .retrieveListForField(pAddParam, pigtlt, "itsOwner");
          for (PurchaseReturnGoodsTaxLine pigtl : tls) {
            getSrvOrm().deleteEntity(pAddParam, pigtl);
          }
        }
      }
      PurchaseReturnTaxLine prtl = new PurchaseReturnTaxLine();
      prtl.setItsOwner(reversed);
      List<PurchaseReturnTaxLine> reversedTaxLines = getSrvOrm().
        retrieveListForField(pAddParam, prtl, "itsOwner");
      for (PurchaseReturnTaxLine reversedLine : reversedTaxLines) {
        if (reversedLine.getReversedId() == null) {
          PurchaseReturnTaxLine reversingLine = new PurchaseReturnTaxLine();
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
    final PurchaseReturn pEntity, final IRequestData pRequestData,
      final PurchaseReturn pOldEntity) throws Exception {
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
      final PurchaseReturn pEntity,
        final IRequestData pRequestData) throws Exception {
    // nothing
  }
}

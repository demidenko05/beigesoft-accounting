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

import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.List;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.beigesoft.model.IRequestData;
import org.beigesoft.exception.ExceptionWithCode;
import org.beigesoft.service.ISrvI18n;
import org.beigesoft.service.IEntityProcessor;
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.service.ISrvNumberToString;
import org.beigesoft.accounting.persistable.AccSettings;
import org.beigesoft.accounting.persistable.PurchaseReturnGoodsTaxLine;
import org.beigesoft.accounting.persistable.PurchaseInvoiceLine;
import org.beigesoft.accounting.persistable.PurchaseReturn;
import org.beigesoft.accounting.persistable.PurchaseReturnLine;
import org.beigesoft.accounting.persistable.PurchaseReturnTaxLine;
import org.beigesoft.accounting.persistable.TaxDestination;
import org.beigesoft.accounting.persistable.UseMaterialEntry;
import org.beigesoft.accounting.service.ISrvDrawItemEntry;
import org.beigesoft.accounting.service.ISrvWarehouseEntry;
import org.beigesoft.accounting.service.ISrvAccSettings;

/**
 * <p>Service that save PurchaseReturnLine into DB.</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class PrcPurchaseReturnLineSave<RS>
  implements IEntityProcessor<PurchaseReturnLine, Long> {

  /**
   * <p>I18N service.</p>
   **/
  private ISrvI18n srvI18n;

  /**
   * <p>ORM service.</p>
   **/
  private ISrvOrm<RS> srvOrm;

  /**
   * <p>Business service for warehouse.</p>
   **/
  private ISrvWarehouseEntry srvWarehouseEntry;

  /**
   * <p>Business service for draw any item to sale/loss/stole.</p>
   **/
  private ISrvDrawItemEntry<UseMaterialEntry> srvUseMaterialEntry;

  /**
   * <p>Business service for accounting settings.</p>
   **/
  private ISrvAccSettings srvAccSettings;

  /**
   * <p>Service print number.</p>
   **/
  private ISrvNumberToString srvNumberToString;

  /**
   * <p>It makes line and total for owner.</p>
   **/
  private UtlInvLine<RS, PurchaseReturn, PurchaseReturnLine,
    PurchaseReturnTaxLine, PurchaseReturnGoodsTaxLine> utlInvLine;

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
  public final PurchaseReturnLine process(
    final Map<String, Object> pReqVars,
      final PurchaseReturnLine pEntity,
        final IRequestData pRequestData) throws Exception {
    if (pEntity.getIsNew()) {
      //BeigeORM refresh:
      pReqVars.put("PurchaseInvoicevendordeepLevel", 3);
      Set<String> ndFlDc = new HashSet<String>();
      ndFlDc.add("itsId");
      ndFlDc.add("isForeigner");
      ndFlDc.add("taxDestination");
      pReqVars.put("DebtorCreditorneededFields", ndFlDc);
      Set<String> ndFlInv = new HashSet<String>();
      ndFlInv.add("itsId");
      ndFlInv.add("vendor");
      ndFlInv.add("omitTaxes");
      ndFlInv.add("priceIncTax");
      ndFlInv.add("hasMadeAccEntries");
      pReqVars.put("PurchaseInvoiceneededFields", ndFlInv);
      pEntity.setItsOwner(getSrvOrm()
        .retrieveEntity(pReqVars, pEntity.getItsOwner()));
      pReqVars.remove("DebtorCreditorneededFields");
      pReqVars.remove("PurchaseInvoiceneededFields");
      pReqVars.remove("PurchaseInvoicevendordeepLevel");
      pReqVars.put("PurchaseInvoiceLineitsOwnerdeepLevel", 1);
      pEntity.setPurchaseInvoiceLine(getSrvOrm()
        .retrieveEntity(pReqVars, pEntity.getPurchaseInvoiceLine()));
      pReqVars.remove("PurchaseInvoiceLineitsOwnerdeepLevel");
      AccSettings as = getSrvAccSettings().lazyGetAccSettings(pReqVars);
      TaxDestination txRules = this.utlInvLine.revealTaxRules(pReqVars,
        pEntity.getItsOwner().getPurchaseInvoice(), as,
          as.getIsExtractSalesTaxFromPurchase());
      if (pEntity.getReversedId() != null) {
        PurchaseReturnLine reversed = getSrvOrm().retrieveEntityById(
          pReqVars, PurchaseReturnLine.class, pEntity.getReversedId());
        if (reversed.getReversedId() != null) {
          throw new ExceptionWithCode(ExceptionWithCode.FORBIDDEN,
            "Attempt to double reverse" + pReqVars.get("user"));
        }
        pEntity.setWarehouseSiteFo(reversed.getWarehouseSiteFo());
        pEntity.setPurchaseInvoiceLine(reversed.getPurchaseInvoiceLine());
        pEntity.setPurchInvLnAppearance(reversed.getPurchInvLnAppearance());
        pEntity.setTaxesDescription(reversed.getTaxesDescription());
        pEntity.setForeignTotalTaxes(reversed.getForeignTotalTaxes().negate());
        pEntity.setTotalTaxes(reversed.getTotalTaxes().negate());
        pEntity.setItsQuantity(reversed.getItsQuantity().negate());
        pEntity.setForeignSubtotal(reversed.getForeignSubtotal().negate());
        pEntity.setSubtotal(reversed.getSubtotal().negate());
        pEntity.setItsTotal(reversed.getItsTotal().negate());
        pEntity.setForeignTotal(reversed.getForeignTotal().negate());
        getSrvOrm().insertEntity(pReqVars, pEntity);
        pEntity.setIsNew(false);
        reversed.setReversedId(pEntity.getItsId());
        getSrvOrm().updateEntity(pReqVars, reversed);
        PurchaseReturnGoodsTaxLine pigtlt = new PurchaseReturnGoodsTaxLine();
        pigtlt.setItsOwner(reversed);
        List<PurchaseReturnGoodsTaxLine> tls = getSrvOrm()
          .retrieveListForField(pReqVars, pigtlt, "itsOwner");
        for (PurchaseReturnGoodsTaxLine pigtl : tls) {
          getSrvOrm().deleteEntity(pReqVars, pigtl);
        }
        srvWarehouseEntry.reverseDraw(pReqVars, pEntity);
        srvUseMaterialEntry.reverseDraw(pReqVars, pEntity,
          pEntity.getItsOwner().getItsDate(),
            pEntity.getItsOwner().getItsId());
      } else {
        if (pEntity.getItsQuantity().doubleValue() <= 0) {
          throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
            "quantity_less_or_equal_zero");
        }
        if (pEntity.getPurchaseInvoiceLine() == null) {
          throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
            "wrong_purchaseInvoiceLine");
        }
        String langDef = (String) pReqVars.get("langDef");
        pEntity.setPurchInvLnAppearance(getSrvI18n().getMsg(PurchaseInvoiceLine
      .class.getSimpleName() + "short", langDef) + " #" + pEntity
    .getPurchaseInvoiceLine().getIdDatabaseBirth() + "-" //local
      + pEntity.getPurchaseInvoiceLine().getItsId() + ", " + pEntity
        .getPurchaseInvoiceLine().getInvItem().getItsName() + ", " + pEntity
          .getPurchaseInvoiceLine().getUnitOfMeasure().getItsName() + ", "
    + getSrvI18n().getMsg("itsCost", langDef) + "=" + prnc(pReqVars, pEntity
      .getPurchaseInvoiceLine().getItsCost()) + ", " + getSrvI18n()
        .getMsg("rest_was", langDef) + "=" + prnq(pReqVars, pEntity
          .getPurchaseInvoiceLine().getTheRest()));
        BigDecimal exchRate = pEntity.getItsOwner().getExchangeRate();
        if (exchRate != null && exchRate.compareTo(BigDecimal.ZERO) == -1) {
          exchRate = BigDecimal.ONE.divide(exchRate.negate(), 15,
            RoundingMode.HALF_UP);
        }
        BigDecimal sourceCost;
        BigDecimal curCost;
        //using user passed total cause rounding error cost*quantity!=total:
        if (pEntity.getItsOwner().getForeignCurrency() != null) {
          if (txRules == null || pEntity.getItsOwner().getPriceIncTax()) {
            pEntity.setItsTotal(pEntity.getForeignTotal().multiply(exchRate)
              .setScale(as.getPricePrecision(), as.getRoundingMode()));
            curCost = pEntity.getForeignTotal().divide(pEntity
              .getItsQuantity(), as.getCostPrecision(), as.getRoundingMode());
          } else {
            pEntity.setSubtotal(pEntity.getForeignSubtotal().multiply(
          exchRate).setScale(as.getPricePrecision(), as.getRoundingMode()));
            curCost = pEntity.getForeignSubtotal().divide(pEntity
              .getItsQuantity(), as.getCostPrecision(), as.getRoundingMode());
          }
          sourceCost = pEntity.getForeignPrice();
        } else {
          if (txRules == null || pEntity.getItsOwner().getPriceIncTax()) {
            curCost = pEntity.getItsTotal().divide(pEntity
              .getItsQuantity(), as.getCostPrecision(), as.getRoundingMode());
          } else {
            curCost = pEntity.getSubtotal().divide(pEntity
              .getItsQuantity(), as.getCostPrecision(), as.getRoundingMode());
          }
          sourceCost = pEntity.getItsPrice();
        }
        if (sourceCost.compareTo(curCost) != 0) {
          if (pEntity.getDescription() == null) {
            pEntity.setDescription(curCost.toString() + "!="
              + sourceCost + "!");
          } else {
            pEntity.setDescription(pEntity.getDescription() + " " + curCost
              + "!=" + sourceCost + "!");
          }
        }
        this.utlInvLine.makeLine(pReqVars, pEntity, as, txRules);
        srvWarehouseEntry.withdrawal(pReqVars, pEntity,
          pEntity.getWarehouseSiteFo());
        srvUseMaterialEntry.withdrawalFrom(pReqVars, pEntity,
          pEntity.getPurchaseInvoiceLine(), pEntity.getItsQuantity());
      }
      //owner update:
      // optimistic locking (dirty check):
      Long ownerVersion = Long.valueOf(pRequestData
        .getParameter(PurchaseReturn.class.getSimpleName() + ".ownerVersion"));
      pEntity.getItsOwner().setItsVersion(ownerVersion);
      this.utlInvLine.makeTotals(pReqVars, pEntity, as, txRules);
      pReqVars.put("nextEntity", pEntity.getItsOwner());
      pReqVars.put("nameOwnerEntity", PurchaseReturn.class.getSimpleName());
      return null;
    } else {
      throw new ExceptionWithCode(ExceptionWithCode.FORBIDDEN,
        "Attempt to update purchase return line by " + pReqVars.get("user"));
    }
  }

  /**
   * <p>Simple delegator to print cost.</p>
   * @param pReqVars additional param
   * @param pVal value
   * @return String
   **/
  public final String prnc(final Map<String, Object> pReqVars,
    final BigDecimal pVal) {
    return this.srvNumberToString.print(pVal.toString(),
      (String) pReqVars.get("decSepv"),
        (String) pReqVars.get("decGrSepv"),
          (Integer) pReqVars.get("costDp"),
            (Integer) pReqVars.get("digInGr"));
  }

  /**
   * <p>Simple delegator to print quantity.</p>
   * @param pReqVars additional param
   * @param pVal value
   * @return String
   **/
  public final String prnq(final Map<String, Object> pReqVars,
    final BigDecimal pVal) {
    return this.srvNumberToString.print(pVal.toString(),
      (String) pReqVars.get("decSepv"),
        (String) pReqVars.get("decGrSepv"),
          (Integer) pReqVars.get("quantityDp"),
            (Integer) pReqVars.get("digInGr"));
  }

  //Simple getters and setters:
  /**
   * <p>Getter for utlInvLine.</p>
   * @return UtlInvLine<RS, PurchaseReturn, PurchaseReturnLine,
   *  PurchaseReturnTaxLine, PurchaseReturnGoodsTaxLine>
   **/
  public final UtlInvLine<RS, PurchaseReturn, PurchaseReturnLine,
    PurchaseReturnTaxLine, PurchaseReturnGoodsTaxLine> getUtlInvLine() {
    return this.utlInvLine;
  }

  /**
   * <p>Setter for utlInvLine.</p>
   * @param pUtlInvLine reference
   **/
  public final void setUtlInvLine(final UtlInvLine<RS, PurchaseReturn,
    PurchaseReturnLine, PurchaseReturnTaxLine,
      PurchaseReturnGoodsTaxLine> pUtlInvLine) {
    this.utlInvLine = pUtlInvLine;
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
   * <p>Geter for srvWarehouseEntry.</p>
   * @return ISrvWarehouseEntry
   **/
  public final ISrvWarehouseEntry getSrvWarehouseEntry() {
    return this.srvWarehouseEntry;
  }

  /**
   * <p>Setter for srvWarehouseEntry.</p>
   * @param pSrvWarehouseEntry reference
   **/
  public final void setSrvWarehouseEntry(
    final ISrvWarehouseEntry pSrvWarehouseEntry) {
    this.srvWarehouseEntry = pSrvWarehouseEntry;
  }

  /**
   * <p>Getter for srvUseMaterialEntry.</p>
   * @return ISrvDrawItemEntry<UseMaterialEntry>
   **/
  public final ISrvDrawItemEntry<UseMaterialEntry> getSrvUseMaterialEntry() {
    return this.srvUseMaterialEntry;
  }

  /**
   * <p>Setter for srvUseMaterialEntry.</p>
   * @param pSrvUseMaterialEntry reference
   **/
  public final void setSrvUseMaterialEntry(
    final ISrvDrawItemEntry<UseMaterialEntry> pSrvUseMaterialEntry) {
    this.srvUseMaterialEntry = pSrvUseMaterialEntry;
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

  /**
   * <p>Getter for srvAccSettings.</p>
   * @return ISrvAccSettings
   **/
  public final ISrvAccSettings getSrvAccSettings() {
    return this.srvAccSettings;
  }

  /**
   * <p>Setter for srvAccSettings.</p>
   * @param pSrvAccSettings reference
   **/
  public final void setSrvAccSettings(final ISrvAccSettings pSrvAccSettings) {
    this.srvAccSettings = pSrvAccSettings;
  }

  /**
   * <p>Getter for srvNumberToString.</p>
   * @return ISrvNumberToString
   **/
  public final ISrvNumberToString getSrvNumberToString() {
    return this.srvNumberToString;
  }

  /**
   * <p>Setter for srvNumberToString.</p>
   * @param pSrvNumberToString reference
   **/
  public final void setSrvNumberToString(
    final ISrvNumberToString pSrvNumberToString) {
    this.srvNumberToString = pSrvNumberToString;
  }
}

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
import java.util.Map;
import java.math.BigDecimal;

import org.beigesoft.model.IRequestData;
import org.beigesoft.exception.ExceptionWithCode;
import org.beigesoft.service.IEntityProcessor;
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.accounting.persistable.InvItem;
import org.beigesoft.accounting.persistable.PurchaseInvoiceGoodsTaxLine;
import org.beigesoft.accounting.persistable.PurchaseInvoiceTaxLine;
import org.beigesoft.accounting.persistable.PurchaseInvoiceLine;
import org.beigesoft.accounting.persistable.PurchaseInvoice;
import org.beigesoft.accounting.persistable.AccSettings;
import org.beigesoft.accounting.persistable.TaxDestination;
import org.beigesoft.accounting.service.ISrvWarehouseEntry;
import org.beigesoft.accounting.service.ISrvAccSettings;

/**
 * <p>Service that saves Purchase Invoice Line into DB.</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class PrcPurchaseInvoiceLineSave<RS>
  implements IEntityProcessor<PurchaseInvoiceLine, Long> {

  /**
   * <p>ORM service.</p>
   **/
  private ISrvOrm<RS> srvOrm;

  /**
   * <p>Business service for warehouse.</p>
   **/
  private ISrvWarehouseEntry srvWarehouseEntry;

  /**
   * <p>It makes line and total for owner.</p>
   **/
  private UtlInvLine<RS, PurchaseInvoice, PurchaseInvoiceLine,
    PurchaseInvoiceTaxLine, PurchaseInvoiceGoodsTaxLine> utlInvLine;

  /**
   * <p>Business service for accounting settings.</p>
   **/
  private ISrvAccSettings srvAccSettings;

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
  public final PurchaseInvoiceLine process(
    final Map<String, Object> pReqVars,
      final PurchaseInvoiceLine pEntity,
        final IRequestData pRequestData) throws Exception {
    if (pEntity.getIsNew()) {
      // Beige-Orm refresh:
      pReqVars.put("DebtorCreditortaxDestinationdeepLevel", 2);
      Set<String> ndFlDc = new HashSet<String>();
      ndFlDc.add("itsId");
      ndFlDc.add("isForeigner");
      ndFlDc.add("taxDestination");
      pReqVars.put("DebtorCreditorneededFields", ndFlDc);
      pEntity.setItsOwner(getSrvOrm()
        .retrieveEntity(pReqVars, pEntity.getItsOwner()));
      pReqVars.remove("DebtorCreditorneededFields");
      pReqVars.remove("DebtorCreditortaxDestinationdeepLevel");
      AccSettings as = getSrvAccSettings().lazyGetAccSettings(pReqVars);
      TaxDestination txRules = this.utlInvLine.revealTaxRules(pReqVars,
        pEntity.getItsOwner(), as, as.getIsExtractSalesTaxFromPurchase());
      if (pEntity.getReversedId() != null) {
        PurchaseInvoiceLine reversed = getSrvOrm().retrieveEntityById(
          pReqVars, PurchaseInvoiceLine.class, pEntity.getReversedId());
        if (reversed.getReversedId() != null) {
          throw new ExceptionWithCode(ExceptionWithCode.FORBIDDEN,
            "attempt_to_reverse_reversed::" + pReqVars.get("user"));
        }
        if (!reversed.getItsQuantity().equals(reversed.getTheRest())) {
          throw new ExceptionWithCode(ExceptionWithCode
            .WRONG_PARAMETER, "where_is_withdrawals_from_this_source::"
              + pReqVars.get("user"));
        }
        pEntity.setTheRest(BigDecimal.ZERO);
        pEntity.setInvItem(reversed.getInvItem());
        pEntity.setUnitOfMeasure(reversed.getUnitOfMeasure());
        pEntity.setWarehouseSite(reversed.getWarehouseSite());
        pEntity.setTaxCategory(reversed.getTaxCategory());
        pEntity.setTaxesDescription(reversed.getTaxesDescription());
        pEntity.setTotalTaxes(reversed.getTotalTaxes().negate());
        pEntity.setItsQuantity(reversed.getItsQuantity().negate());
        pEntity.setItsCost(reversed.getItsCost());
        pEntity.setSubtotal(reversed.getSubtotal().negate());
        pEntity.setItsTotal(reversed.getItsTotal().negate());
        pEntity.setForeignPrice(reversed.getForeignPrice());
        pEntity.setForeignSubtotal(reversed.getForeignSubtotal().negate());
        pEntity.setForeignTotalTaxes(reversed.getForeignTotalTaxes().negate());
        pEntity.setForeignTotal(reversed.getForeignTotal().negate());
        getSrvOrm().insertEntity(pReqVars, pEntity);
        pEntity.setIsNew(false);
        reversed.setTheRest(BigDecimal.ZERO);
        reversed.setReversedId(pEntity.getItsId());
        getSrvOrm().updateEntity(pReqVars, reversed);
        getSrvOrm().deleteEntityWhere(pReqVars,
          PurchaseInvoiceGoodsTaxLine.class, "ITSOWNER=" + reversed.getItsId());
      } else {
        if (pEntity.getItsQuantity().doubleValue() <= 0) {
          throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
            "quantity_less_or_equal_zero::" + pReqVars.get("user"));
        }
        if (!(pEntity.getItsCost().compareTo(BigDecimal.ZERO) > 0
          || pEntity.getForeignPrice().compareTo(BigDecimal.ZERO) > 0)) {
          throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
            "cost_less_or_eq_zero::" + pReqVars.get("user"));
        }
        // Beige-Orm refresh:
        pEntity.setInvItem(getSrvOrm()
          .retrieveEntity(pReqVars, pEntity.getInvItem()));
        if (!(InvItem.MATERIAL_ID.equals(pEntity.getInvItem().getItsType()
          .getItsId()) || InvItem.MERCHANDISE_ID.equals(pEntity.getInvItem()
            .getItsType().getItsId()))) {
          throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
            "type_must_be_material_or_merchandise::" + pReqVars.get("user"));
        }
        pEntity.setTheRest(pEntity.getItsQuantity());
        if (pEntity.getInvItem().getKnownCost() != null) {
          if (pEntity.getItsOwner().getForeignCurrency() != null) {
            pEntity.setForeignPrice(pEntity.getInvItem().getKnownCost());
            if (txRules == null || pEntity.getItsOwner().getPriceIncTax()) {
            pEntity.setForeignTotal(pEntity.getItsQuantity().multiply(pEntity
    .getForeignPrice()).setScale(as.getPricePrecision(), as.getRoundingMode()));
            } else {
            pEntity.setForeignSubtotal(pEntity.getItsQuantity().multiply(pEntity
    .getForeignPrice()).setScale(as.getPricePrecision(), as.getRoundingMode()));
            }
            pEntity.setItsCost(pEntity.getForeignPrice().multiply(pEntity
              .getItsOwner().getExchangeRate()).setScale(as
                .getPricePrecision(), as.getRoundingMode()));
          } else {
            pEntity.setItsCost(pEntity.getInvItem().getKnownCost());
          }
          if (txRules == null || pEntity.getItsOwner().getPriceIncTax()) {
          pEntity.setItsTotal(pEntity.getItsQuantity().multiply(pEntity
        .getItsCost()).setScale(as.getPricePrecision(), as.getRoundingMode()));
          } else {
            pEntity.setSubtotal(pEntity.getItsQuantity().multiply(pEntity
        .getItsCost()).setScale(as.getPricePrecision(), as.getRoundingMode()));
          }
        } else {
          //using user passed values:
          if (pEntity.getItsOwner().getForeignCurrency() != null) {
            pEntity.setItsCost(pEntity.getForeignPrice().multiply(pEntity
              .getItsOwner().getExchangeRate()).setScale(as
                .getPricePrecision(), as.getRoundingMode()));
            if (txRules == null || pEntity.getItsOwner().getPriceIncTax()) {
              pEntity.setItsTotal(pEntity.getForeignTotal().multiply(pEntity
              .getItsOwner().getExchangeRate()).setScale(as
                .getPricePrecision(), as.getRoundingMode()));
            } else {
              pEntity.setSubtotal(pEntity.getForeignSubtotal().multiply(pEntity
              .getItsOwner().getExchangeRate()).setScale(as
                .getPricePrecision(), as.getRoundingMode()));
            }
          }
        }
        this.utlInvLine.makeLine(pReqVars, pEntity, as, txRules);
      }
      //draw or reverse warehouse entries:
      srvWarehouseEntry.load(pReqVars, pEntity, pEntity.getWarehouseSite());
      // optimistic locking (dirty check):
      Long ownerVersion = Long.valueOf(pRequestData
        .getParameter(PurchaseInvoice.class.getSimpleName() + ".ownerVersion"));
      pEntity.getItsOwner().setItsVersion(ownerVersion);
      this.utlInvLine.makeTotals(pReqVars, pEntity, as, txRules);
      pReqVars.put("nextEntity", pEntity.getItsOwner());
      pReqVars.put("nameOwnerEntity", PurchaseInvoice.class.getSimpleName());
      return null;
    } else {
      throw new ExceptionWithCode(ExceptionWithCode.FORBIDDEN,
        "edit_not_allowed::" + pReqVars.get("user"));
    }
  }

  //Simple getters and setters:
  /**
   * <p>Getter for utlInvLine.</p>
   * @return UtlInvLine<RS, PurchaseInvoice, PurchaseInvoiceLine,
   *  PurchaseInvoiceTaxLine, PurchaseInvoiceGoodsTaxLine>
   **/
  public final UtlInvLine<RS, PurchaseInvoice, PurchaseInvoiceLine,
    PurchaseInvoiceTaxLine, PurchaseInvoiceGoodsTaxLine> getUtlInvLine() {
    return this.utlInvLine;
  }

  /**
   * <p>Setter for utlInvLine.</p>
   * @param pUtlInvLine reference
   **/
  public final void setUtlInvLine(final UtlInvLine<RS, PurchaseInvoice,
    PurchaseInvoiceLine, PurchaseInvoiceTaxLine,
      PurchaseInvoiceGoodsTaxLine> pUtlInvLine) {
    this.utlInvLine = pUtlInvLine;
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
}

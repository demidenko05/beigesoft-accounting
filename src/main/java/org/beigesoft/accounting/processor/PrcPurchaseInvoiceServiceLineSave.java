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
import java.math.RoundingMode;

import org.beigesoft.model.IRequestData;
import org.beigesoft.exception.ExceptionWithCode;
import org.beigesoft.service.IEntityProcessor;
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.accounting.persistable.AccSettings;
import org.beigesoft.accounting.persistable.PurchaseInvoiceServiceLine;
import org.beigesoft.accounting.persistable.PurchaseInvoiceTaxLine;
import org.beigesoft.accounting.persistable.PurchaseInvoiceServiceTaxLine;
import org.beigesoft.accounting.persistable.PurchaseInvoice;
import org.beigesoft.accounting.persistable.TaxDestination;
import org.beigesoft.accounting.service.ISrvAccSettings;

/**
 * <p>Service that saves Purchase Invoice Service Line into DB.</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class PrcPurchaseInvoiceServiceLineSave<RS>
  implements IEntityProcessor<PurchaseInvoiceServiceLine, Long> {

  /**
   * <p>ORM service.</p>
   **/
  private ISrvOrm<RS> srvOrm;

  /**
   * <p>It makes line and total for owner.</p>
   **/
  private UtlInvLine<RS, PurchaseInvoice, PurchaseInvoiceServiceLine,
    PurchaseInvoiceTaxLine, PurchaseInvoiceServiceTaxLine> utlInvLine;

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
  public final PurchaseInvoiceServiceLine process(
    final Map<String, Object> pReqVars,
      final PurchaseInvoiceServiceLine pEntity,
        final IRequestData pRequestData) throws Exception {
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
    pEntity.setService(getSrvOrm()
      .retrieveEntity(pReqVars, pEntity.getService()));
    AccSettings as = getSrvAccSettings().lazyGetAccSettings(pReqVars);
    TaxDestination txRules = this.utlInvLine.revealTaxRules(pReqVars,
      pEntity.getItsOwner(), as, as.getIsExtractSalesTaxFromPurchase());
    //using user passed values:
    if (pEntity.getItsOwner().getForeignCurrency() != null) {
      BigDecimal exchRate = pEntity.getItsOwner().getExchangeRate();
      if (exchRate.compareTo(BigDecimal.ZERO) == -1) {
        exchRate = BigDecimal.ONE.divide(exchRate.negate(), 15,
          RoundingMode.HALF_UP);
      }
      pEntity.setItsCost(pEntity.getForeignPrice().multiply(exchRate)
        .setScale(as.getPricePrecision(), as.getRoundingMode()));
      if (txRules == null || pEntity.getItsOwner().getPriceIncTax()) {
        pEntity.setItsTotal(pEntity.getForeignTotal().multiply(exchRate)
          .setScale(as.getPricePrecision(), as.getRoundingMode()));
      } else {
        pEntity.setSubtotal(pEntity.getForeignSubtotal().multiply(exchRate)
          .setScale(as.getPricePrecision(), as.getRoundingMode()));
      }
    }
    this.utlInvLine.makeLine(pReqVars, pEntity, as, txRules);
    // optimistic locking (dirty check):
    Long ownerVersion = Long.valueOf(pRequestData
      .getParameter(PurchaseInvoice.class.getSimpleName() + ".ownerVersion"));
    pEntity.getItsOwner().setItsVersion(ownerVersion);
      this.utlInvLine.makeTotals(pReqVars, pEntity, as, txRules);
    pReqVars.put("nextEntity", pEntity.getItsOwner());
    pReqVars.put("nameOwnerEntity", PurchaseInvoice.class.getSimpleName());
    return null;
  }

  //Simple getters and setters:
  /**
   * <p>Getter for utlInvLine.</p>
   * @return UtlInvLine<RS, PurchaseInvoice, PurchaseInvoiceServiceLine,
   *  PurchaseInvoiceTaxLine, PurchaseInvoiceServiceTaxLine>
   **/
  public final UtlInvLine<RS, PurchaseInvoice, PurchaseInvoiceServiceLine,
    PurchaseInvoiceTaxLine, PurchaseInvoiceServiceTaxLine> getUtlInvLine() {
    return this.utlInvLine;
  }

  /**
   * <p>Setter for utlInvLine.</p>
   * @param pUtlInvLine reference
   **/
  public final void setUtlInvLine(final UtlInvLine<RS, PurchaseInvoice,
    PurchaseInvoiceServiceLine, PurchaseInvoiceTaxLine,
      PurchaseInvoiceServiceTaxLine> pUtlInvLine) {
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
}

package org.beigesoft.accounting.processor;

/*
 * Copyright (c) 2018 Beigesoft™
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
import org.beigesoft.accounting.persistable.AccSettings;
import org.beigesoft.accounting.persistable.TaxDestination;
import org.beigesoft.accounting.persistable.PurchaseInvoiceTaxLine;
import org.beigesoft.accounting.persistable.PurchaseInvoice;
import org.beigesoft.accounting.service.ISrvAccSettings;

/**
 * <p>Service that saves Purchase Invoice Tax Line into DB
 * (only invoice basis!).</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class PrcPurchInvTaxLnSave<RS>
  implements IEntityProcessor<PurchaseInvoiceTaxLine, Long> {

  /**
   * <p>ORM service.</p>
   **/
  private ISrvOrm<RS> srvOrm;

  /**
   * <p>Business service for accounting settings.</p>
   **/
  private ISrvAccSettings srvAccSettings;

  /**
   * <p>It makes line and total for owner.</p>
   **/
  private UtlInvLine<RS, PurchaseInvoice, ?, ?, ?> utlInvLine;


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
  public final PurchaseInvoiceTaxLine process(
    final Map<String, Object> pReqVars,
      final PurchaseInvoiceTaxLine pEntity,
        final IRequestData pRequestData) throws Exception {
    if (pEntity.getItsTotal().compareTo(BigDecimal.ZERO) <= 0) {
      throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
        "total_less_or_eq_zero");
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
    AccSettings as = getSrvAccSettings().lazyGetAccSettings(pReqVars);
    TaxDestination txRules = this.utlInvLine.revealTaxRules(pReqVars,
      pEntity.getItsOwner(), as, as.getIsExtractSalesTaxFromPurchase());
    if (txRules == null) {
      throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
        "non_taxable");
    }
    if (!txRules.getSalTaxIsInvoiceBase()) {
      throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
        "cant_edit_item_basis_tax");
    }
    PurchaseInvoiceTaxLine oldEntity = getSrvOrm()
      .retrieveEntity(pReqVars, pEntity);
    pEntity.setTax(oldEntity.getTax());
    pEntity.setTaxableInvBas(oldEntity.getTaxableInvBas());
    pEntity.setTaxableInvBasFc(oldEntity.getTaxableInvBasFc());
    //rounding:
    pEntity.setItsTotal(pEntity.getItsTotal().setScale(as
      .getPricePrecision(), txRules.getSalTaxRoundMode()));
    if (pEntity.getItsTotal().compareTo(oldEntity.getItsTotal()) != 0) {
      if (pEntity.getItsOwner().getDescription() == null) {
        pEntity.getItsOwner().setDescription(pEntity.getTax().getItsName()
         + ": " + oldEntity.getItsTotal() + "->" + pEntity.getItsTotal() + "!");
      } else {
        pEntity.getItsOwner().setDescription(pEntity.getItsOwner()
          .getDescription() + " " + pEntity.getTax().getItsName()
            + ": " + oldEntity.getItsTotal() + "->"
              + pEntity.getItsTotal() + "!");
      }
    }
    getSrvOrm().updateEntity(pReqVars, pEntity);
    // optimistic locking (dirty check):
    Long ownerVersion = Long.valueOf(pRequestData
      .getParameter(PurchaseInvoice.class.getSimpleName() + ".ownerVersion"));
    pEntity.getItsOwner().setItsVersion(ownerVersion);
    this.utlInvLine.updInvTots(pReqVars, pEntity.getItsOwner(), as);
    pReqVars.put("nextEntity", pEntity.getItsOwner());
    pReqVars.put("nameOwnerEntity", PurchaseInvoice.class.getSimpleName());
    return null;
  }

  //Simple getters and setters:
  /**
   * <p>Getter for utlInvLine.</p>
   * @return UtlInvLine<RS, PurchaseInvoice, ?,
   *  PurchaseInvoiceTaxLine, ?>
   **/
  public final UtlInvLine<RS, PurchaseInvoice, ?, ?, ?> getUtlInvLine() {
    return this.utlInvLine;
  }

  /**
   * <p>Setter for utlInvLine.</p>
   * @param pUtlInvLine reference
   **/
  public final void setUtlInvLine(
    final UtlInvLine<RS, PurchaseInvoice, ?, ?, ?> pUtlInvLine) {
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

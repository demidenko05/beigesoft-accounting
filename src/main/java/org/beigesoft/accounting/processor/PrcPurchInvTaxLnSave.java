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

import java.util.Map;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.beigesoft.model.IRequestData;
import org.beigesoft.exception.ExceptionWithCode;
import org.beigesoft.service.IEntityProcessor;
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.accounting.persistable.AccSettings;
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
   * <p>It makes total for owner.</p>
   **/
  private UtlPurchaseGoodsServiceLine<RS> utlPurchaseGoodsServiceLine;

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
  public final PurchaseInvoiceTaxLine process(
    final Map<String, Object> pReqVars,
      final PurchaseInvoiceTaxLine pEntity,
        final IRequestData pRequestData) throws Exception {
    if (pEntity.getItsTotal().compareTo(BigDecimal.ZERO) <= 0) {
      throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
        "total_less_or_eq_zero");
    }
    AccSettings as = getSrvAccSettings().lazyGetAccSettings(pReqVars);
    // Beige-Orm refresh:
    pReqVars.put("DebtorCreditortaxDestinationdeepLevel", 2);
    pEntity.setItsOwner(getSrvOrm()
      .retrieveEntity(pReqVars, pEntity.getItsOwner()));
    pReqVars.remove("DebtorCreditortaxDestinationdeepLevel");
    boolean isTaxable = as.getIsExtractSalesTaxFromPurchase() && !pEntity
      .getItsOwner().getOmitTaxes() && !pEntity.getItsOwner().getVendor()
        .getIsForeigner();
    if (!isTaxable) {
      throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
        "non_taxable");
    }
    PurchaseInvoiceTaxLine oldEntity = getSrvOrm()
      .retrieveEntity(pReqVars, pEntity);
    pEntity.setTax(oldEntity.getTax());
    pEntity.setTaxableInvBas(oldEntity.getTaxableInvBas());
    pEntity.setTaxableInvBasFc(oldEntity.getTaxableInvBasFc());
    boolean isItemBasis = !as.getSalTaxIsInvoiceBase();
    RoundingMode rm = as.getSalTaxRoundMode();
    if (pEntity.getItsOwner().getVendor().getTaxDestination() != null) {
      //override tax method:
      isItemBasis = !pEntity.getItsOwner().getVendor()
        .getTaxDestination().getSalTaxIsInvoiceBase();
      rm = pEntity.getItsOwner().getVendor()
        .getTaxDestination().getSalTaxRoundMode();
    }
    if (isItemBasis) {
      throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
        "cant_edit_item_basis_tax");
    }
    //rounding:
    pEntity.setItsTotal(pEntity.getItsTotal().setScale(as
      .getPricePrecision(), rm));
    BigDecimal totalTaxes;
    BigDecimal bd100 = new BigDecimal("100.00");
    if (pEntity.getItsOwner().getPriceIncTax()) {
      totalTaxes = pEntity.getTaxableInvBas().subtract(pEntity
    .getTaxableInvBas().divide(BigDecimal.ONE.add(pEntity.getTax()
  .getItsPercentage().divide(bd100)), as.getPricePrecision(), rm));
    } else {
      totalTaxes = pEntity.getTaxableInvBas().multiply(pEntity.getTax()
        .getItsPercentage()).divide(bd100, as.getPricePrecision(), rm);
    }
    if (pEntity.getItsTotal().compareTo(totalTaxes) != 0) {
      if (pEntity.getItsOwner().getDescription() == null) {
        pEntity.getItsOwner().setDescription(pEntity.getItsTotal().toString()
          + "!=" + totalTaxes + "!");
      } else {
        pEntity.getItsOwner().setDescription(pEntity.getItsOwner()
          .getDescription() + " " + pEntity.getItsTotal().toString()
            + "!=" + totalTaxes + "!");
      }
    }
    getSrvOrm().updateEntity(pReqVars, pEntity);
    // optimistic locking (dirty check):
    Long ownerVersion = Long.valueOf(pRequestData
      .getParameter(PurchaseInvoice.class.getSimpleName() + ".ownerVersion"));
    pEntity.getItsOwner().setItsVersion(ownerVersion);
    this.utlPurchaseGoodsServiceLine
      .updateOwnerTotals(pReqVars, pEntity.getItsOwner());
    pReqVars.put("nextEntity", pEntity.getItsOwner());
    pReqVars.put("nameOwnerEntity", PurchaseInvoice.class.getSimpleName());
    return null;
  }

  //Simple getters and setters:
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
   * <p>Getter for utlPurchaseGoodsServiceLine.</p>
   * @return UtlPurchaseGoodsServiceLine<RS>
   **/
  public final UtlPurchaseGoodsServiceLine<RS>
    getUtlPurchaseGoodsServiceLine() {
    return this.utlPurchaseGoodsServiceLine;
  }

  /**
   * <p>Setter for utlPurchaseGoodsServiceLine.</p>
   * @param pUtlPurchaseGoodsServiceLine reference
   **/
  public final void setUtlPurchaseGoodsServiceLine(
    final UtlPurchaseGoodsServiceLine<RS> pUtlPurchaseGoodsServiceLine) {
    this.utlPurchaseGoodsServiceLine = pUtlPurchaseGoodsServiceLine;
  }
}

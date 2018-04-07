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
import java.math.BigDecimal;

import org.beigesoft.model.IRequestData;
import org.beigesoft.exception.ExceptionWithCode;
import org.beigesoft.service.IEntityProcessor;
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.accounting.model.ETaxType;
import org.beigesoft.accounting.persistable.InvItemTaxCategoryLine;
import org.beigesoft.accounting.persistable.PurchaseInvoiceServiceLine;
import org.beigesoft.accounting.persistable.PurchaseInvoice;
import org.beigesoft.accounting.service.ISrvAccSettings;

/**
 * <p>Service that save PurchaseInvoiceServiceLine into DB.</p>
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
   * <p>It makes total for owner.</p>
   **/
  private UtlPurchaseGoodsServiceLine<RS> utlPurchaseGoodsServiceLine;

  /**
   * <p>Business service for accounting settings.</p>
   **/
  private ISrvAccSettings srvAccSettings;

  /**
   * <p>Process entity request.</p>
   * @param pAddParam additional param, e.g. return this line's
   * document in "nextEntity" for farther process
   * @param pRequestData Request Data
   * @param pEntity Entity to process
   * @return Entity processed for farther process or null
   * @throws Exception - an exception
   **/
  @Override
  public final PurchaseInvoiceServiceLine process(
    final Map<String, Object> pAddParam,
      final PurchaseInvoiceServiceLine pEntity,
        final IRequestData pRequestData) throws Exception {
    if (pEntity.getItsCost().doubleValue() <= 0) {
      throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
        "cost_less_or_eq_zero::" + pAddParam.get("user"));
    }
    // Beige-Orm refresh:
    pEntity.setItsOwner(getSrvOrm()
      .retrieveEntity(pAddParam, pEntity.getItsOwner()));
    pEntity.setService(getSrvOrm()
      .retrieveEntity(pAddParam, pEntity.getService()));
    //rounding:
    pEntity.setItsCost(pEntity.getItsCost()
      .setScale(getSrvAccSettings().lazyGetAccSettings(pAddParam)
        .getCostPrecision(), getSrvAccSettings()
          .lazyGetAccSettings(pAddParam).getRoundingMode()));
    BigDecimal totalTaxes = BigDecimal.ZERO;
    String taxesDescription = "";
    if (!pEntity.getItsOwner().getVendor().getIsForeigner()
      && getSrvAccSettings().lazyGetAccSettings(pAddParam)
        .getIsExtractSalesTaxFromPurchase()
          && pEntity.getService().getTaxCategory() != null) {
      List<InvItemTaxCategoryLine> pstl = getSrvOrm()
        .retrieveListWithConditions(pAddParam,
          InvItemTaxCategoryLine.class, "where ITSOWNER="
            + pEntity.getService().getTaxCategory().getItsId());
      BigDecimal bigDecimal100 = new BigDecimal("100.00");
      StringBuffer sb = new StringBuffer();
      int i = 0;
      for (InvItemTaxCategoryLine pst : pstl) {
        if (ETaxType.SALES_TAX_OUTITEM.equals(pst.getTax().getItsType())
          || ETaxType.SALES_TAX_INITEM.equals(pst.getTax().getItsType())) {
          BigDecimal addTx = pEntity.getItsCost().multiply(pst
            .getItsPercentage()).divide(bigDecimal100,
              getSrvAccSettings().lazyGetAccSettings(pAddParam)
                .getPricePrecision(), getSrvAccSettings()
                  .lazyGetAccSettings(pAddParam).getRoundingMode());
          totalTaxes = totalTaxes.add(addTx);
          if (i++ > 0) {
            sb.append(", ");
          }
          sb.append(pst.getTax().getItsName() + " " + pst.getItsPercentage()
            + "%=" + addTx);
        }
      }
      taxesDescription = sb.toString();
    }
    pEntity.setTaxesDescription(taxesDescription);
    pEntity.setTotalTaxes(totalTaxes);
    pEntity.setItsTotal(pEntity.getItsCost().add(totalTaxes));
    if (pEntity.getIsNew()) {
      getSrvOrm().insertEntity(pAddParam, pEntity);
    } else {
      getSrvOrm().updateEntity(pAddParam, pEntity);
    }
    // optimistic locking (dirty check):
    Long ownerVersion = Long.valueOf(pRequestData
      .getParameter(PurchaseInvoice.class.getSimpleName() + ".ownerVersion"));
    pEntity.getItsOwner().setItsVersion(ownerVersion);
    this.utlPurchaseGoodsServiceLine
      .updateOwner(pAddParam, pEntity.getItsOwner());
    pAddParam.put("nextEntity", pEntity.getItsOwner());
    pAddParam.put("nameOwnerEntity", PurchaseInvoice.class.getSimpleName());
    pRequestData.setAttribute("accSettings",
      this.srvAccSettings.lazyGetAccSettings(pAddParam));
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

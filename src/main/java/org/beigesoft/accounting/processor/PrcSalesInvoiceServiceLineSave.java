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
import org.beigesoft.service.ISrvNumberToString;
import org.beigesoft.accounting.model.ETaxType;
import org.beigesoft.accounting.persistable.InvItemTaxCategoryLine;
import org.beigesoft.accounting.persistable.SalesInvoiceServiceLine;
import org.beigesoft.accounting.persistable.SalesInvoice;
import org.beigesoft.accounting.service.ISrvAccSettings;

/**
 * <p>Service that save SalesInvoiceServiceLine into DB.</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class PrcSalesInvoiceServiceLineSave<RS>
  implements IEntityProcessor<SalesInvoiceServiceLine, Long> {

  /**
   * <p>ORM service.</p>
   **/
  private ISrvOrm<RS> srvOrm;

  /**
   * <p>It makes total for owner.</p>
   **/
  private UtlSalesGoodsServiceLine<RS> utlSalesGoodsServiceLine;

  /**
   * <p>Business service for accounting settings.</p>
   **/
  private ISrvAccSettings srvAccSettings;

  /**
   * <p>Service print number.</p>
   **/
  private ISrvNumberToString srvNumberToString;

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
  public final SalesInvoiceServiceLine process(
    final Map<String, Object> pAddParam,
      final SalesInvoiceServiceLine pEntity,
        final IRequestData pRequestData) throws Exception {
    if (pEntity.getItsQuantity().doubleValue() <= 0) {
      throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
        "quantity_less_or_equal_zero::" + pAddParam.get("user"));
    }
    if (pEntity.getItsPrice().compareTo(BigDecimal.ZERO) <= 0) {
      throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
        "price_less_or_eq_zero");
    }
    // Beige-Orm refresh:
    pEntity.setItsOwner(getSrvOrm()
      .retrieveEntity(pAddParam, pEntity.getItsOwner()));
    pEntity.setService(getSrvOrm()
      .retrieveEntity(pAddParam, pEntity.getService()));
    //rounding:
    pEntity.setItsQuantity(pEntity.getItsQuantity().setScale(
      getSrvAccSettings().lazyGetAccSettings(pAddParam)
        .getQuantityPrecision(), getSrvAccSettings()
          .lazyGetAccSettings(pAddParam).getRoundingMode()));
    pEntity.setItsPrice(pEntity.getItsPrice()
      .setScale(getSrvAccSettings().lazyGetAccSettings(pAddParam)
        .getPricePrecision(), getSrvAccSettings()
          .lazyGetAccSettings(pAddParam).getRoundingMode()));
    //without taxes:
    pEntity.setSubtotal(pEntity.getItsQuantity().multiply(pEntity
      .getItsPrice()).setScale(getSrvAccSettings()
        .lazyGetAccSettings(pAddParam).getPricePrecision(),
          getSrvAccSettings().lazyGetAccSettings(pAddParam)
            .getRoundingMode()));
    BigDecimal totalTaxes = BigDecimal.ZERO;
    String taxesDescription = "";
    if (!pEntity.getItsOwner().getCustomer().getIsForeigner()
        && getSrvAccSettings().lazyGetAccSettings(pAddParam)
          .getIsExtractSalesTaxFromSales()
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
          BigDecimal addTx = pEntity.getItsPrice().multiply(pEntity
        .getItsQuantity()).multiply(pst.getItsPercentage())
      .divide(bigDecimal100, getSrvAccSettings().lazyGetAccSettings(pAddParam)
    .getPricePrecision(), getSrvAccSettings().lazyGetAccSettings(pAddParam)
  .getRoundingMode());
          totalTaxes = totalTaxes.add(addTx);
          if (i++ > 0) {
            sb.append(", ");
          }
          sb.append(pst.getTax().getItsName() + " " + prn(pAddParam,
            pst.getItsPercentage()) + "%=" + prn(pAddParam, addTx));
        }
      }
      taxesDescription = sb.toString();
    }
    pEntity.setTaxesDescription(taxesDescription);
    pEntity.setTotalTaxes(totalTaxes);
    pEntity.setItsTotal(pEntity.getSubtotal().add(totalTaxes));
    if (pEntity.getIsNew()) {
      getSrvOrm().insertEntity(pAddParam, pEntity);
    } else {
      getSrvOrm().updateEntity(pAddParam, pEntity);
    }
    // optimistic locking (dirty check):
    Long ownerVersion = Long.valueOf(pRequestData
      .getParameter(SalesInvoice.class.getSimpleName() + ".ownerVersion"));
    pEntity.getItsOwner().setItsVersion(ownerVersion);
    this.utlSalesGoodsServiceLine
      .updateOwner(pAddParam, pEntity.getItsOwner());
    pAddParam.put("nextEntity", pEntity.getItsOwner());
    pAddParam.put("nameOwnerEntity", SalesInvoice.class.getSimpleName());
    return null;
  }

  /**
   * <p>Simple delegator to print number.</p>
   * @param pAddParam additional param
   * @param pVal value
   * @return String
   **/
  public final String prn(final Map<String, Object> pAddParam,
    final BigDecimal pVal) {
    return this.srvNumberToString.print(pVal.toString(),
      (String) pAddParam.get("dseparatorv"),
        (String) pAddParam.get("dgseparatorv"),
          (Integer) pAddParam.get("balancePrecision"),
            (Integer) pAddParam.get("digitsInGroup"));
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
   * <p>Getter for utlSalesGoodsServiceLine.</p>
   * @return UtlSalesGoodsServiceLine<RS>
   **/
  public final UtlSalesGoodsServiceLine<RS>
    getUtlSalesGoodsServiceLine() {
    return this.utlSalesGoodsServiceLine;
  }

  /**
   * <p>Setter for utlSalesGoodsServiceLine.</p>
   * @param pUtlSalesGoodsServiceLine reference
   **/
  public final void setUtlSalesGoodsServiceLine(
    final UtlSalesGoodsServiceLine<RS> pUtlSalesGoodsServiceLine) {
    this.utlSalesGoodsServiceLine = pUtlSalesGoodsServiceLine;
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

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

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.math.BigDecimal;

import org.beigesoft.model.IRequestData;
import org.beigesoft.exception.ExceptionWithCode;
import org.beigesoft.service.IEntityProcessor;
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.service.ISrvNumberToString;
import org.beigesoft.accounting.model.ETaxType;
import org.beigesoft.accounting.persistable.InvItemTaxCategoryLine;
import org.beigesoft.accounting.persistable.PurchaseInvoiceServiceLine;
import org.beigesoft.accounting.persistable.PurchaseInvoiceServiceTaxLine;
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
  public final PurchaseInvoiceServiceLine process(
    final Map<String, Object> pAddParam,
      final PurchaseInvoiceServiceLine pEntity,
        final IRequestData pRequestData) throws Exception {
    if (pEntity.getItsQuantity().doubleValue() <= 0) {
      throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
        "quantity_less_or_equal_zero::" + pAddParam.get("user"));
    }
    if (!(pEntity.getItsCost().compareTo(BigDecimal.ZERO) > 0
      || pEntity.getForeignPrice().compareTo(BigDecimal.ZERO) > 0)) {
      throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
        "cost_less_or_eq_zero::" + pAddParam.get("user"));
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
    if (pEntity.getItsOwner().getForeignCurrency() != null) {
      pEntity.setForeignPrice(pEntity.getForeignPrice().setScale(
    getSrvAccSettings().lazyGetAccSettings(pAddParam).getCostPrecision(),
  getSrvAccSettings().lazyGetAccSettings(pAddParam).getRoundingMode()));
      pEntity.setItsCost(pEntity.getForeignPrice().multiply(pEntity
    .getItsOwner().getExchangeRate()).setScale(getSrvAccSettings()
  .lazyGetAccSettings(pAddParam).getCostPrecision(), getSrvAccSettings()
.lazyGetAccSettings(pAddParam).getRoundingMode()));
      //without taxes:
      pEntity.setForeignSubtotal(pEntity.getItsQuantity().multiply(pEntity
    .getForeignPrice()).setScale(getSrvAccSettings().lazyGetAccSettings(
  pAddParam).getCostPrecision(), getSrvAccSettings()
.lazyGetAccSettings(pAddParam).getRoundingMode()));
    } else {
      pEntity.setItsCost(pEntity.getItsCost().setScale(getSrvAccSettings()
    .lazyGetAccSettings(pAddParam).getCostPrecision(), getSrvAccSettings()
  .lazyGetAccSettings(pAddParam).getRoundingMode()));
    }
    //without taxes:
    pEntity.setSubtotal(pEntity.getItsQuantity().multiply(pEntity
      .getItsCost()).setScale(getSrvAccSettings()
        .lazyGetAccSettings(pAddParam).getPricePrecision(),
          getSrvAccSettings().lazyGetAccSettings(pAddParam)
            .getRoundingMode()));
    BigDecimal totalTaxes = BigDecimal.ZERO;
    BigDecimal totalTaxesFc = BigDecimal.ZERO;
    String taxesDescription = "";
    List<PurchaseInvoiceServiceTaxLine> tls = null;
    boolean isItemBasis = !getSrvAccSettings()
      .lazyGetAccSettings(pAddParam).getSalTaxIsInvoiceBase();
    if (!pEntity.getItsOwner().getVendor().getIsForeigner()
      && getSrvAccSettings().lazyGetAccSettings(pAddParam)
        .getIsExtractSalesTaxFromPurchase()
          && pEntity.getService().getTaxCategory() != null) {
      if (isItemBasis) {
        tls = new ArrayList<PurchaseInvoiceServiceTaxLine>();
      }
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
          BigDecimal addTx = pEntity.getSubtotal().multiply(pst
        .getItsPercentage()).divide(bigDecimal100,
      getSrvAccSettings().lazyGetAccSettings(pAddParam).getPricePrecision(),
    getSrvAccSettings().lazyGetAccSettings(pAddParam).getRoundingMode());
          totalTaxes = totalTaxes.add(addTx);
          if (i++ > 0) {
            sb.append(", ");
          }
          if (isItemBasis) {
            PurchaseInvoiceServiceTaxLine pistl =
              new PurchaseInvoiceServiceTaxLine();
            pistl.setIsNew(true);
            pistl.setIdDatabaseBirth(this.srvOrm.getIdDatabase());
            pistl.setItsTotal(addTx);
            pistl.setTax(pst.getTax());
            if (pEntity.getItsOwner().getForeignCurrency() != null) {
              BigDecimal addTxFc = pEntity.getForeignSubtotal().multiply(pst
            .getItsPercentage()).divide(bigDecimal100, getSrvAccSettings()
          .lazyGetAccSettings(pAddParam).getPricePrecision(),
        getSrvAccSettings().lazyGetAccSettings(pAddParam).getRoundingMode());
              totalTaxesFc = totalTaxesFc.add(addTxFc);
              pistl.setForeignTotalTaxes(addTxFc);
            }
            tls.add(pistl);
            sb.append(pst.getTax().getItsName() + " "
              + prn(pAddParam, addTx));
          } else {
            sb.append(pst.getTax().getItsName());
          }
        }
      }
      taxesDescription = sb.toString();
    }
    pEntity.setTaxesDescription(taxesDescription);
    pEntity.setTotalTaxes(totalTaxes);
    pEntity.setItsTotal(pEntity.getSubtotal().add(totalTaxes));
    pEntity.setForeignTotalTaxes(totalTaxesFc);
    pEntity.setForeignTotal(pEntity.getForeignSubtotal().add(totalTaxesFc));
    if (pEntity.getIsNew()) {
      getSrvOrm().insertEntity(pAddParam, pEntity);
      pEntity.setIsNew(false);
    } else {
      getSrvOrm().updateEntity(pAddParam, pEntity);
    }
    PurchaseInvoiceServiceTaxLine pistlt = new PurchaseInvoiceServiceTaxLine();
    pistlt.setItsOwner(pEntity);
    List<PurchaseInvoiceServiceTaxLine> tlsw = getSrvOrm()
      .retrieveListForField(pAddParam, pistlt, "itsOwner");
    if (tls != null) {
      for (int i = 0; i < tls.size(); i++) {
        if (i < tlsw.size()) {
          tlsw.get(i).setTax(tls.get(i).getTax());
          tlsw.get(i).setItsTotal(tls.get(i).getItsTotal());
          getSrvOrm().updateEntity(pAddParam, tlsw.get(i));
        } else {
          tls.get(i).setItsOwner(pEntity);
          tls.get(i).setInvoiceId(pEntity.getItsOwner().getItsId());
          getSrvOrm().insertEntity(pAddParam, tls.get(i));
          tls.get(i).setIsNew(false);
        }
      }
      for (int j = tls.size(); j < tlsw.size(); j++) {
        getSrvOrm().deleteEntity(pAddParam, tlsw.get(j));
      }
    } else {
      for (PurchaseInvoiceServiceTaxLine pistlw : tlsw) {
        getSrvOrm().deleteEntity(pAddParam, pistlw);
      }
    }
    // optimistic locking (dirty check):
    Long ownerVersion = Long.valueOf(pRequestData
      .getParameter(PurchaseInvoice.class.getSimpleName() + ".ownerVersion"));
    pEntity.getItsOwner().setItsVersion(ownerVersion);
    this.utlPurchaseGoodsServiceLine
      .updateOwner(pAddParam, pEntity.getItsOwner());
    pAddParam.put("nextEntity", pEntity.getItsOwner());
    pAddParam.put("nameOwnerEntity", PurchaseInvoice.class.getSimpleName());
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
          (Integer) pAddParam.get("pricePrecision"),
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

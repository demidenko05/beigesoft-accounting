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
import org.beigesoft.accounting.persistable.AccSettings;
import org.beigesoft.accounting.persistable.InvItemTaxCategoryLine;
import org.beigesoft.accounting.persistable.SalesInvoiceServiceLine;
import org.beigesoft.accounting.persistable.SalesInvoice;
import org.beigesoft.accounting.persistable.SalesInvoiceServiceTaxLine;
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
   * @param pReqVars additional param, e.g. return this line's
   * document in "nextEntity" for farther process
   * @param pRequestData Request Data
   * @param pEntity Entity to process
   * @return Entity processed for farther process or null
   * @throws Exception - an exception
   **/
  @Override
  public final SalesInvoiceServiceLine process(
    final Map<String, Object> pReqVars,
      final SalesInvoiceServiceLine pEntity,
        final IRequestData pRequestData) throws Exception {
    if (pEntity.getItsQuantity().compareTo(BigDecimal.ZERO) <= 0) {
      throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
        "quantity_less_or_equal_zero::" + pReqVars.get("user"));
    }
    if (!(pEntity.getItsPrice().compareTo(BigDecimal.ZERO) > 0
      || pEntity.getForeignPrice().compareTo(BigDecimal.ZERO) > 0)) {
      throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
        "price_less_or_eq_zero");
    }
    AccSettings as = getSrvAccSettings().lazyGetAccSettings(pReqVars);
    // Beige-Orm refresh:
    pEntity.setItsOwner(getSrvOrm()
      .retrieveEntity(pReqVars, pEntity.getItsOwner()));
    pEntity.setService(getSrvOrm()
      .retrieveEntity(pReqVars, pEntity.getService()));
    //rounding:
    pEntity.setItsQuantity(pEntity.getItsQuantity().setScale(
      as.getQuantityPrecision(), as.getRoundingMode()));
    if (pEntity.getItsOwner().getForeignCurrency() != null) {
      pEntity.setForeignPrice(pEntity.getForeignPrice().setScale(as
        .getPricePrecision(), as.getRoundingMode()));
      pEntity.setItsPrice(pEntity.getForeignPrice().multiply(pEntity
        .getItsOwner().getExchangeRate()).setScale(as
          .getPricePrecision(), as.getRoundingMode()));
      //without taxes:
      pEntity.setForeignSubtotal(pEntity.getItsQuantity().multiply(pEntity
        .getForeignPrice()).setScale(as
          .getPricePrecision(), as.getRoundingMode()));
    } else {
      pEntity.setItsPrice(pEntity.getItsPrice().setScale(as
        .getPricePrecision(), as.getRoundingMode()));
    }
    //without taxes:
    pEntity.setSubtotal(pEntity.getItsQuantity().multiply(pEntity.getItsPrice())
      .setScale(as.getPricePrecision(), as.getRoundingMode()));
    BigDecimal aggrTaxRate = BigDecimal.ZERO;
    BigDecimal totalTaxes = BigDecimal.ZERO;
    BigDecimal totalTaxesFc = BigDecimal.ZERO;
    BigDecimal bd100 = new BigDecimal("100.00");
    String taxesDescription = "";
    List<SalesInvoiceServiceTaxLine> tls = null;
    if (!pEntity.getItsOwner().getCustomer().getIsForeigner()
      && as.getIsExtractSalesTaxFromSales()
        && pEntity.getService().getTaxCategory() != null) {
      if (!as.getSalTaxIsInvoiceBase()) {
        tls = new ArrayList<SalesInvoiceServiceTaxLine>();
      }
      pReqVars.put("InvItemTaxCategoryLineitsOwnerdeepLevel", 1);
      List<InvItemTaxCategoryLine> pstl = getSrvOrm()
        .retrieveListWithConditions(pReqVars,
          InvItemTaxCategoryLine.class, "where ITSOWNER="
            + pEntity.getService().getTaxCategory().getItsId());
      pReqVars.remove("InvItemTaxCategoryLineitsOwnerdeepLevel");
      StringBuffer sb = new StringBuffer();
      int i = 0;
      for (InvItemTaxCategoryLine pst : pstl) {
        if (ETaxType.SALES_TAX_OUTITEM.equals(pst.getTax().getItsType())
          || ETaxType.SALES_TAX_INITEM.equals(pst.getTax().getItsType())) {
          if (i++ > 0) {
            sb.append(", ");
          }
          if (!as.getSalTaxIsInvoiceBase()) {
            if (as.getSalTaxUseAggregItBas()) {
              aggrTaxRate = aggrTaxRate.add(pst.getItsPercentage());
            } else {
              BigDecimal addTx = pEntity.getSubtotal().multiply(pst
                .getItsPercentage()).divide(bd100, as
                  .getPricePrecision(), as.getSalTaxRoundMode());
              totalTaxes = totalTaxes.add(addTx);
              SalesInvoiceServiceTaxLine pistl =
                new SalesInvoiceServiceTaxLine();
              pistl.setIsNew(true);
              pistl.setIdDatabaseBirth(this.srvOrm.getIdDatabase());
              pistl.setItsTotal(addTx);
              pistl.setTax(pst.getTax());
              if (pEntity.getItsOwner().getForeignCurrency() != null) {
                BigDecimal addTxFc = pEntity.getForeignSubtotal().multiply(pst
                  .getItsPercentage()).divide(bd100, as
                    .getPricePrecision(), as.getSalTaxRoundMode());
                totalTaxesFc = totalTaxesFc.add(addTxFc);
                pistl.setForeignTotalTaxes(addTxFc);
              }
              tls.add(pistl);
              sb.append(pst.getTax().getItsName() + " "
                + prn(pReqVars, addTx));
            }
          }
          if (as.getSalTaxIsInvoiceBase() || as.getSalTaxUseAggregItBas()) {
            sb.append(pst.getTax().getItsName());
          }
        }
      }
      taxesDescription = sb.toString();
    }
    if (!as.getSalTaxIsInvoiceBase() && as.getSalTaxUseAggregItBas()) {
      totalTaxes = pEntity.getSubtotal().multiply(aggrTaxRate).divide(
        bd100, as.getPricePrecision(), as.getSalTaxRoundMode());
      if (pEntity.getItsOwner().getForeignCurrency() != null) {
        totalTaxesFc = pEntity.getForeignSubtotal().multiply(aggrTaxRate)
          .divide(bd100, as.getPricePrecision(), as.getSalTaxRoundMode());
      }
    }
    pEntity.setTaxesDescription(taxesDescription);
    pEntity.setTotalTaxes(totalTaxes);
    pEntity.setItsTotal(pEntity.getSubtotal().add(totalTaxes));
    pEntity.setForeignTotalTaxes(totalTaxesFc);
    pEntity.setForeignTotal(pEntity.getForeignSubtotal().add(totalTaxesFc));
    if (pEntity.getIsNew()) {
      getSrvOrm().insertEntity(pReqVars, pEntity);
      pEntity.setIsNew(false);
    } else {
      getSrvOrm().updateEntity(pReqVars, pEntity);
    }
    SalesInvoiceServiceTaxLine pistlt = new SalesInvoiceServiceTaxLine();
    pistlt.setItsOwner(pEntity);
    pReqVars.put("SalesInvoiceServiceTaxLineitsOwnerdeepLevel", 1);
    List<SalesInvoiceServiceTaxLine> tlsw = getSrvOrm()
      .retrieveListForField(pReqVars, pistlt, "itsOwner");
    pReqVars.remove("SalesInvoiceServiceTaxLineitsOwnerdeepLevel");
    if (tls != null) {
      for (int i = 0; i < tls.size(); i++) {
        if (i < tlsw.size()) {
          tlsw.get(i).setTax(tls.get(i).getTax());
          tlsw.get(i).setItsTotal(tls.get(i).getItsTotal());
          tlsw.get(i).setItsOwner(pEntity);
          getSrvOrm().updateEntity(pReqVars, tlsw.get(i));
        } else {
          tls.get(i).setItsOwner(pEntity);
          tls.get(i).setInvoiceId(pEntity.getItsOwner().getItsId());
          getSrvOrm().insertEntity(pReqVars, tls.get(i));
          tls.get(i).setIsNew(false);
        }
      }
      for (int j = tls.size(); j < tlsw.size(); j++) {
        getSrvOrm().deleteEntity(pReqVars, tlsw.get(j));
      }
    } else {
      for (SalesInvoiceServiceTaxLine pistlw : tlsw) {
        getSrvOrm().deleteEntity(pReqVars, pistlw);
      }
    }
    // optimistic locking (dirty check):
    Long ownerVersion = Long.valueOf(pRequestData
      .getParameter(SalesInvoice.class.getSimpleName() + ".ownerVersion"));
    pEntity.getItsOwner().setItsVersion(ownerVersion);
    this.utlSalesGoodsServiceLine
      .updateOwner(pReqVars, pEntity.getItsOwner());
    pReqVars.put("nextEntity", pEntity.getItsOwner());
    pReqVars.put("nameOwnerEntity", SalesInvoice.class.getSimpleName());
    return null;
  }

  /**
   * <p>Simple delegator to print number.</p>
   * @param pReqVars additional param
   * @param pVal value
   * @return String
   **/
  public final String prn(final Map<String, Object> pReqVars,
    final BigDecimal pVal) {
    return this.srvNumberToString.print(pVal.toString(),
      (String) pReqVars.get("dseparatorv"), //TODO default I18N
        (String) pReqVars.get("dgseparatorv"),
          (Integer) pReqVars.get("pricePrecision"),
            (Integer) pReqVars.get("digitsInGroup"));
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

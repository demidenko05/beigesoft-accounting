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
import java.util.List;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.math.RoundingMode;

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
import org.beigesoft.accounting.persistable.DestTaxServSelLn;
import org.beigesoft.accounting.service.ISrvAccSettings;

/**
 * <p>Service that saves Sales Invoice Service Line into DB.</p>
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
   * <p>Server side calculation policy (for invoice line):
   * <ul>
   *   <li>getting from user's form price and quantity</li>
   *   <li>getting from DB if price inclusive of taxes</li>
   *   <li>getting from DB tax method</li>
   *   <li>calculates line/invoice totals, subtotals, taxes</li>
   * </ul>
   * Service that saves invoice line, will calculates tax according
   * given tax category (independent on form amount).</p>
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
    boolean isTaxable = as.getIsExtractSalesTaxFromSales() && !pEntity
      .getItsOwner().getOmitTaxes() && !pEntity.getItsOwner().getCustomer()
        .getIsForeigner();
    //using user passed values:
    if (pEntity.getItsOwner().getForeignCurrency() != null) {
      pEntity.setItsPrice(pEntity.getForeignPrice().multiply(pEntity
        .getItsOwner().getExchangeRate()).setScale(as
          .getPricePrecision(), as.getRoundingMode()));
      if (!isTaxable || pEntity.getItsOwner().getPriceIncTax()) {
        pEntity.setItsTotal(pEntity.getForeignTotal().multiply(pEntity
        .getItsOwner().getExchangeRate()).setScale(as
          .getPricePrecision(), as.getRoundingMode()));
      } else {
        pEntity.setSubtotal(pEntity.getForeignSubtotal().multiply(pEntity
        .getItsOwner().getExchangeRate()).setScale(as
          .getPricePrecision(), as.getRoundingMode()));
      }
    }
    BigDecimal totalTaxes = BigDecimal.ZERO;
    BigDecimal totalTaxesFc = BigDecimal.ZERO;
    BigDecimal bd100 = new BigDecimal("100.00");
    List<SalesInvoiceServiceTaxLine> tls = null;
    boolean isItemBasis = !as.getSalTaxIsInvoiceBase();
    boolean isAggrOnlyRate = as.getSalTaxUseAggregItBas();
    if (isTaxable) {
      pEntity.setTaxCategory(pEntity.getService().getTaxCategory());
      RoundingMode rm = as.getSalTaxRoundMode();
      if (pEntity.getItsOwner().getCustomer().getTaxDestination() != null) {
        //override tax method:
        isItemBasis = !pEntity.getItsOwner().getCustomer()
          .getTaxDestination().getSalTaxIsInvoiceBase();
        isAggrOnlyRate = pEntity.getItsOwner().getCustomer()
          .getTaxDestination().getSalTaxUseAggregItBas();
        rm = pEntity.getItsOwner().getCustomer()
          .getTaxDestination().getSalTaxRoundMode();
        pReqVars.put("DestTaxServSelLnitsOwnerdeepLevel", 1);
        List<DestTaxServSelLn> dtls = getSrvOrm()
          .retrieveListWithConditions(pReqVars, DestTaxServSelLn.class,
            "where ITSOWNER=" + pEntity.getService().getItsId());
        pReqVars.remove("DestTaxServSelLnitsOwnerdeepLevel");
        for (DestTaxServSelLn dtl : dtls) {
          if (dtl.getTaxDestination().getItsId().equals(pEntity.getItsOwner()
            .getCustomer().getTaxDestination().getItsId())) {
            pEntity.setTaxCategory(dtl.getTaxCategory()); //it may be null
            break;
          }
        }
      }
      if (pEntity.getTaxCategory() != null && isItemBasis) {
        if (!isAggrOnlyRate) {
          if (pEntity.getItsOwner().getPriceIncTax()) {
            throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
              "price_inc_tax_multi_not_imp");
          }
          tls = new ArrayList<SalesInvoiceServiceTaxLine>();
          pReqVars.put("InvItemTaxCategoryLineitsOwnerdeepLevel", 1);
          List<InvItemTaxCategoryLine> itcls = getSrvOrm()
            .retrieveListWithConditions(pReqVars,
              InvItemTaxCategoryLine.class, "where ITSOWNER="
                + pEntity.getTaxCategory().getItsId());
          pReqVars.remove("InvItemTaxCategoryLineitsOwnerdeepLevel");
          StringBuffer sb = new StringBuffer();
          int i = 0;
          for (InvItemTaxCategoryLine itcl : itcls) {
            if (ETaxType.SALES_TAX_OUTITEM.equals(itcl.getTax().getItsType())
            || ETaxType.SALES_TAX_INITEM.equals(itcl.getTax().getItsType())) {
              if (i++ > 0) {
                sb.append(", ");
              }
              BigDecimal addTx = pEntity.getSubtotal().multiply(itcl
              .getItsPercentage()).divide(bd100, as.getPricePrecision(), rm);
              totalTaxes = totalTaxes.add(addTx);
              SalesInvoiceServiceTaxLine iitl =
                new SalesInvoiceServiceTaxLine();
              iitl.setIsNew(true);
              iitl.setIdDatabaseBirth(this.srvOrm.getIdDatabase());
              iitl.setItsTotal(addTx);
              iitl.setTax(itcl.getTax());
              if (pEntity.getItsOwner().getForeignCurrency() != null) {
                BigDecimal addTxFc = pEntity.getForeignSubtotal().multiply(
          itcl.getItsPercentage()).divide(bd100, as.getPricePrecision(), rm);
                totalTaxesFc = totalTaxesFc.add(addTxFc);
                iitl.setForeignTotalTaxes(addTxFc);
              }
              tls.add(iitl);
              sb.append(itcl.getTax().getItsName() + " "
                + prn(pReqVars, addTx));
            }
          }
          pEntity.setTaxesDescription(sb.toString());
        } else {
          if (pEntity.getItsOwner().getPriceIncTax()) {
            totalTaxes = pEntity.getItsTotal().subtract(pEntity.getItsTotal()
      .divide(BigDecimal.ONE.add(pEntity.getTaxCategory().getAggrOnlyPercent()
    .divide(bd100)), as.getPricePrecision(), rm));
          } else {
          totalTaxes = pEntity.getSubtotal().multiply(pEntity.getTaxCategory()
            .getAggrOnlyPercent()).divide(bd100, as.getPricePrecision(), rm);
          }
          pEntity.setTaxesDescription(pEntity.getTaxCategory().getItsName());
          if (pEntity.getItsOwner().getForeignCurrency() != null) {
            if (pEntity.getItsOwner().getPriceIncTax()) {
  totalTaxesFc = pEntity.getForeignTotal().subtract(pEntity.getForeignTotal()
    .divide(BigDecimal.ONE.add(pEntity.getTaxCategory().getAggrOnlyPercent()
       .divide(bd100)), as.getPricePrecision(), rm));
            } else {
              totalTaxesFc = pEntity.getForeignSubtotal().multiply(pEntity
                .getTaxCategory().getAggrOnlyPercent())
                  .divide(bd100, as.getPricePrecision(), rm);
            }
          }
        }
      } else if (pEntity.getTaxCategory() != null) {
        pEntity.setTaxesDescription(pEntity.getTaxCategory().getItsName());
      }
    }
    pEntity.setTotalTaxes(totalTaxes);
    pEntity.setForeignTotalTaxes(totalTaxesFc);
    if (!isTaxable || pEntity.getItsOwner().getPriceIncTax()) {
      pEntity.setSubtotal(pEntity.getItsTotal().subtract(totalTaxes));
    } else {
      pEntity.setItsTotal(pEntity.getSubtotal().add(totalTaxes));
    }
    if (pEntity.getItsOwner().getForeignCurrency() != null) {
      pEntity.setForeignTotalTaxes(totalTaxesFc);
      if (!isTaxable || pEntity.getItsOwner().getPriceIncTax()) {
        pEntity.setForeignSubtotal(pEntity.getForeignTotal()
          .subtract(totalTaxesFc));
      } else {
        pEntity.setForeignTotal(pEntity.getForeignSubtotal().add(totalTaxesFc));
      }
    }
    if (pEntity.getIsNew()) {
      getSrvOrm().insertEntity(pReqVars, pEntity);
      pEntity.setIsNew(false);
    } else {
      getSrvOrm().updateEntity(pReqVars, pEntity);
    }
    pReqVars.put("SalesInvoiceServiceTaxLineitsOwnerdeepLevel", 1);
    List<SalesInvoiceServiceTaxLine> iitls = getSrvOrm()
      .retrieveListWithConditions(pReqVars, SalesInvoiceServiceTaxLine.class,
        "where ITSOWNER=" + pEntity.getItsId());
    pReqVars.remove("SalesInvoiceServiceTaxLineitsOwnerdeepLevel");
    if (tls != null) {
      for (int i = 0; i < tls.size(); i++) {
        if (i < iitls.size()) {
          iitls.get(i).setTax(tls.get(i).getTax());
          iitls.get(i).setItsTotal(tls.get(i).getItsTotal());
          iitls.get(i).setItsOwner(pEntity);
          getSrvOrm().updateEntity(pReqVars, iitls.get(i));
        } else {
          tls.get(i).setItsOwner(pEntity);
          tls.get(i).setInvoiceId(pEntity.getItsOwner().getItsId());
          getSrvOrm().insertEntity(pReqVars, tls.get(i));
          tls.get(i).setIsNew(false);
        }
      }
      for (int j = tls.size(); j < iitls.size(); j++) {
        getSrvOrm().deleteEntity(pReqVars, iitls.get(j));
      }
    } else {
      for (SalesInvoiceServiceTaxLine iitl : iitls) {
        getSrvOrm().deleteEntity(pReqVars, iitl);
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
      (String) pReqVars.get("decSepv"), //TODO default I18N
        (String) pReqVars.get("decGrSepv"),
          (Integer) pReqVars.get("priceDp"),
            (Integer) pReqVars.get("digInGr"));
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

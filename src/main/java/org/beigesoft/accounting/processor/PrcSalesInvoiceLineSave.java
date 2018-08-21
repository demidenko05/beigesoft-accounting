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
import java.math.RoundingMode;

import org.beigesoft.model.IRequestData;
import org.beigesoft.exception.ExceptionWithCode;
import org.beigesoft.service.IEntityProcessor;
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.service.ISrvNumberToString;
import org.beigesoft.accounting.model.ETaxType;
import org.beigesoft.accounting.persistable.AccSettings;
import org.beigesoft.accounting.persistable.InvItemTaxCategoryLine;
import org.beigesoft.accounting.persistable.SalesInvoiceLine;
import org.beigesoft.accounting.persistable.SalesInvoiceGoodsTaxLine;
import org.beigesoft.accounting.persistable.SalesInvoice;
import org.beigesoft.accounting.persistable.DestTaxGoodsLn;
import org.beigesoft.accounting.persistable.CogsEntry;
import org.beigesoft.accounting.service.ISrvWarehouseEntry;
import org.beigesoft.accounting.service.ISrvDrawItemEntry;
import org.beigesoft.accounting.service.ISrvAccSettings;

/**
 * <p>Service that saves Sales Invoice Line into DB.</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class PrcSalesInvoiceLineSave<RS>
  implements IEntityProcessor<SalesInvoiceLine, Long> {

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
  private ISrvDrawItemEntry<CogsEntry> srvCogsEntry;

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
  public final SalesInvoiceLine process(
    final Map<String, Object> pReqVars,
      final SalesInvoiceLine pEntity,
        final IRequestData pRequestData) throws Exception {
    if (pEntity.getIsNew()) {
      // Beige-Orm refresh:
      pReqVars.put("DebtorCreditortaxDestinationdeepLevel", 2);
      pEntity.setItsOwner(getSrvOrm()
        .retrieveEntity(pReqVars, pEntity.getItsOwner()));
      pReqVars.remove("DebtorCreditortaxDestinationdeepLevel");
      if (pEntity.getReversedId() != null) {
        SalesInvoiceLine reversed = getSrvOrm().retrieveEntityById(
          pReqVars, SalesInvoiceLine.class, pEntity.getReversedId());
        if (reversed.getReversedId() != null) {
          throw new ExceptionWithCode(ExceptionWithCode.FORBIDDEN,
            "attempt_to_reverse_reversed::" + pReqVars.get("user"));
        }
        pEntity.setInvItem(reversed.getInvItem());
        pEntity.setUnitOfMeasure(reversed.getUnitOfMeasure());
        pEntity.setWarehouseSiteFo(reversed.getWarehouseSiteFo());
        pEntity.setTaxesDescription(reversed.getTaxesDescription());
        pEntity.setTotalTaxes(reversed.getTotalTaxes().negate());
        pEntity.setItsQuantity(reversed.getItsQuantity().negate());
        pEntity.setItsPrice(reversed.getItsPrice());
        pEntity.setSubtotal(reversed.getSubtotal().negate());
        pEntity.setItsTotal(reversed.getItsTotal().negate());
        pEntity.setForeignPrice(reversed.getForeignPrice());
        pEntity.setForeignSubtotal(reversed.getForeignSubtotal().negate());
        pEntity.setForeignTotalTaxes(reversed.getForeignTotalTaxes().negate());
        pEntity.setForeignTotal(reversed.getForeignTotal().negate());
        getSrvOrm().insertEntity(pReqVars, pEntity);
        pEntity.setIsNew(false);
        reversed.setReversedId(pEntity.getItsId());
        getSrvOrm().updateEntity(pReqVars, reversed);
        srvWarehouseEntry.reverseDraw(pReqVars, pEntity);
        srvCogsEntry.reverseDraw(pReqVars, pEntity, pEntity.getItsOwner()
          .getItsDate(), pEntity.getItsOwner().getItsId());
        getSrvOrm().deleteEntityWhere(pReqVars,
          SalesInvoiceGoodsTaxLine.class, "ITSOWNER=" + reversed.getItsId());
      } else {
        if (pEntity.getItsQuantity().compareTo(BigDecimal.ZERO) <= 0
          && pEntity.getReversedId() == null) {
          throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
            "quantity_less_or_equal_zero::" + pReqVars.get("user"));
        }
        if (!(pEntity.getItsPrice().compareTo(BigDecimal.ZERO) > 0
          || pEntity.getForeignPrice().compareTo(BigDecimal.ZERO) > 0)) {
          throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
            "price_less_eq_0::" + pReqVars.get("user"));
        }
        AccSettings as = getSrvAccSettings().lazyGetAccSettings(pReqVars);
        // Beige-Orm refresh:
        pEntity.setInvItem(getSrvOrm()
          .retrieveEntity(pReqVars, pEntity.getInvItem()));
        //rounding:
        pEntity.setItsQuantity(pEntity.getItsQuantity().setScale(as
         .getQuantityPrecision(), as.getRoundingMode()));
        boolean isTaxable = as.getIsExtractSalesTaxFromSales() && !pEntity
          .getItsOwner().getOmitTaxes() && !pEntity.getItsOwner().getCustomer()
            .getIsForeigner();
        if (pEntity.getItsOwner().getForeignCurrency() != null) {
          pEntity.setForeignPrice(pEntity.getForeignPrice().setScale(as
            .getPricePrecision(), as.getRoundingMode()));
          if (!isTaxable || pEntity.getItsOwner().getPriceIncTax()) {
            pEntity.setForeignTotal(pEntity.getItsQuantity().multiply(pEntity
    .getForeignPrice()).setScale(as.getPricePrecision(), as.getRoundingMode()));
          } else {
            pEntity.setForeignSubtotal(pEntity.getItsQuantity().multiply(pEntity
    .getForeignPrice()).setScale(as.getPricePrecision(), as.getRoundingMode()));
          }
          pEntity.setItsPrice(pEntity.getForeignPrice().multiply(pEntity
            .getItsOwner().getExchangeRate()).setScale(as
              .getPricePrecision(), as.getRoundingMode()));
        } else {
          pEntity.setItsPrice(pEntity.getItsPrice().setScale(as
            .getPricePrecision(), as.getRoundingMode()));
        }
        if (!isTaxable || pEntity.getItsOwner().getPriceIncTax()) {
          pEntity.setItsTotal(pEntity.getItsQuantity().multiply(pEntity
        .getItsPrice()).setScale(as.getPricePrecision(), as.getRoundingMode()));
        } else {
          pEntity.setSubtotal(pEntity.getItsQuantity().multiply(pEntity
        .getItsPrice()).setScale(as.getPricePrecision(), as.getRoundingMode()));
        }
        BigDecimal totalTaxes = BigDecimal.ZERO;
        BigDecimal totalTaxesFc = BigDecimal.ZERO;
        BigDecimal bd100 = new BigDecimal("100.00");
        List<SalesInvoiceGoodsTaxLine> tls = null;
        if (isTaxable) {
          boolean isItemBasis = !as.getSalTaxIsInvoiceBase();
          boolean isAggrOnlyRate = as.getSalTaxUseAggregItBas();
          pEntity.setTaxCategory(pEntity.getInvItem().getTaxCategory());
          RoundingMode rm = as.getSalTaxRoundMode();
          if (pEntity.getItsOwner().getCustomer().getTaxDestination() != null) {
            //override tax method:
            isItemBasis = !pEntity.getItsOwner().getCustomer()
              .getTaxDestination().getSalTaxIsInvoiceBase();
            isAggrOnlyRate = pEntity.getItsOwner().getCustomer()
              .getTaxDestination().getSalTaxUseAggregItBas();
            rm = pEntity.getItsOwner().getCustomer()
              .getTaxDestination().getSalTaxRoundMode();
            pReqVars.put("DestTaxGoodsLnitsOwnerdeepLevel", 1);
            List<DestTaxGoodsLn> dtls = getSrvOrm()
              .retrieveListWithConditions(pReqVars, DestTaxGoodsLn.class,
                "where ITSOWNER=" + pEntity.getInvItem().getItsId());
            pReqVars.remove("DestTaxGoodsLnitsOwnerdeepLevel");
            for (DestTaxGoodsLn dtl : dtls) {
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
              tls = new ArrayList<SalesInvoiceGoodsTaxLine>();
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
                  SalesInvoiceGoodsTaxLine iitl =
                    new SalesInvoiceGoodsTaxLine();
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
          }
        }
        pEntity.setTotalTaxes(totalTaxes);
        if (!isTaxable || pEntity.getItsOwner().getPriceIncTax()) {
          pEntity.setSubtotal(pEntity.getItsTotal().subtract(totalTaxes));
        } else {
          pEntity.setItsTotal(pEntity.getSubtotal().add(totalTaxes));
        }
        if (pEntity.getItsOwner().getForeignCurrency() != null) {
          pEntity.setForeignTotalTaxes(totalTaxesFc);
          if (!isTaxable || pEntity.getItsOwner().getPriceIncTax()) {
  pEntity.setForeignSubtotal(pEntity.getForeignTotal().subtract(totalTaxesFc));
          } else {
        pEntity.setForeignTotal(pEntity.getForeignSubtotal().add(totalTaxesFc));
          }
        }
        getSrvOrm().insertEntity(pReqVars, pEntity);
        pEntity.setIsNew(false);
        if (tls != null) {
          for (SalesInvoiceGoodsTaxLine iitl : tls) {
            iitl.setItsOwner(pEntity);
            iitl.setInvoiceId(pEntity.getItsOwner().getItsId());
            getSrvOrm().insertEntity(pReqVars, iitl);
            iitl.setIsNew(false);
          }
        }
        srvWarehouseEntry.withdrawal(pReqVars, pEntity,
          pEntity.getWarehouseSiteFo());
        srvCogsEntry.withdrawal(pReqVars, pEntity, pEntity.getItsOwner()
          .getItsDate(), pEntity.getItsOwner().getItsId());
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
    } else {
      throw new ExceptionWithCode(ExceptionWithCode.FORBIDDEN,
        "edit_not_allowed::" + pReqVars.get("user"));
    }
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
   * <p>Getter for srvCogsEntry.</p>
   * @return ISrvDrawItemEntry<CogsEntry>
   **/
  public final ISrvDrawItemEntry<CogsEntry> getSrvCogsEntry() {
    return this.srvCogsEntry;
  }

  /**
   * <p>Setter for srvCogsEntry.</p>
   * @param pSrvCogsEntry reference
   **/
  public final void setSrvCogsEntry(
    final ISrvDrawItemEntry<CogsEntry> pSrvCogsEntry) {
    this.srvCogsEntry = pSrvCogsEntry;
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

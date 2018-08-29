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
import org.beigesoft.accounting.persistable.InvItem;
import org.beigesoft.accounting.persistable.InvItemTaxCategoryLine;
import org.beigesoft.accounting.persistable.PurchaseInvoiceGoodsTaxLine;
import org.beigesoft.accounting.persistable.PurchaseInvoiceLine;
import org.beigesoft.accounting.persistable.PurchaseInvoice;
import org.beigesoft.accounting.persistable.AccSettings;
import org.beigesoft.accounting.persistable.DestTaxGoodsLn;
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
      pEntity.setItsOwner(getSrvOrm()
        .retrieveEntity(pReqVars, pEntity.getItsOwner()));
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
        AccSettings as = getSrvAccSettings().lazyGetAccSettings(pReqVars);
        pEntity.setTheRest(pEntity.getItsQuantity());
        boolean isTaxable = as.getIsExtractSalesTaxFromPurchase() && !pEntity
          .getItsOwner().getOmitTaxes() && !pEntity.getItsOwner().getVendor()
            .getIsForeigner();
        if (pEntity.getInvItem().getKnownCost() != null) {
          if (pEntity.getItsOwner().getForeignCurrency() != null) {
            pEntity.setForeignPrice(pEntity.getInvItem().getKnownCost());
            if (!isTaxable || pEntity.getItsOwner().getPriceIncTax()) {
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
          if (!isTaxable || pEntity.getItsOwner().getPriceIncTax()) {
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
        }
        BigDecimal totalTaxes = BigDecimal.ZERO;
        BigDecimal totalTaxesFc = BigDecimal.ZERO;
        BigDecimal bd100 = new BigDecimal("100.00");
        List<PurchaseInvoiceGoodsTaxLine> tls = null;
        boolean isItemBasis = !as.getSalTaxIsInvoiceBase();
        boolean isAggrOnlyRate = as.getSalTaxUseAggregItBas();
        if (isTaxable) {
          pEntity.setTaxCategory(pEntity.getInvItem().getTaxCategory());
          RoundingMode rm = as.getSalTaxRoundMode();
          if (pEntity.getItsOwner().getVendor().getTaxDestination() != null) {
            //override tax method:
            isItemBasis = !pEntity.getItsOwner().getVendor()
              .getTaxDestination().getSalTaxIsInvoiceBase();
            isAggrOnlyRate = pEntity.getItsOwner().getVendor()
              .getTaxDestination().getSalTaxUseAggregItBas();
            rm = pEntity.getItsOwner().getVendor()
              .getTaxDestination().getSalTaxRoundMode();
            pReqVars.put("DestTaxGoodsLnitsOwnerdeepLevel", 1);
            List<DestTaxGoodsLn> dtls = getSrvOrm()
              .retrieveListWithConditions(pReqVars, DestTaxGoodsLn.class,
                "where ITSOWNER=" + pEntity.getInvItem().getItsId());
            pReqVars.remove("DestTaxGoodsLnitsOwnerdeepLevel");
            for (DestTaxGoodsLn dtl : dtls) {
              if (dtl.getTaxDestination().getItsId().equals(pEntity
                .getItsOwner().getVendor().getTaxDestination().getItsId())) {
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
              tls = new ArrayList<PurchaseInvoiceGoodsTaxLine>();
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
                  PurchaseInvoiceGoodsTaxLine iitl =
                    new PurchaseInvoiceGoodsTaxLine();
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
        if (isTaxable && pEntity.getTaxCategory() != null && isItemBasis
          && isAggrOnlyRate) {
          if (pEntity.getTotalTaxes().compareTo(totalTaxes) != 0) {
            if (pEntity.getDescription() == null) {
              pEntity.setDescription(pEntity.getTotalTaxes().toString() + "!="
                + totalTaxes + "!");
            } else {
              pEntity.setDescription(pEntity.getDescription() + " " + pEntity
                .getTotalTaxes().toString() + "!=" + totalTaxes + "!");
            }
          }
        } else { //multi-sales non-aggregate or non-taxable:
          pEntity.setTotalTaxes(totalTaxes);
        }
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
            pEntity.setForeignTotal(pEntity.getForeignSubtotal()
              .add(totalTaxesFc));
          }
        }
        getSrvOrm().insertEntity(pReqVars, pEntity);
        pEntity.setIsNew(false);
        if (tls != null) {
          for (PurchaseInvoiceGoodsTaxLine pigtl : tls) {
            pigtl.setItsOwner(pEntity);
            pigtl.setInvoiceId(pEntity.getItsOwner().getItsId());
            getSrvOrm().insertEntity(pReqVars, pigtl);
          }
        }
      }
      //draw or reverse warehouse entries:
      srvWarehouseEntry.load(pReqVars, pEntity, pEntity.getWarehouseSite());
      // optimistic locking (dirty check):
      Long ownerVersion = Long.valueOf(pRequestData
        .getParameter(PurchaseInvoice.class.getSimpleName() + ".ownerVersion"));
      pEntity.getItsOwner().setItsVersion(ownerVersion);
      this.utlPurchaseGoodsServiceLine
        .updateOwner(pReqVars, pEntity.getItsOwner());
      pReqVars.put("nextEntity", pEntity.getItsOwner());
      pReqVars.put("nameOwnerEntity", PurchaseInvoice.class.getSimpleName());
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
      (String) pReqVars.get("dseparatorv"),
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

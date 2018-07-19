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
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.math.BigDecimal;

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
import org.beigesoft.accounting.service.ISrvWarehouseEntry;
import org.beigesoft.accounting.service.ISrvAccSettings;

/**
 * <p>Service that save PurchaseInvoiceLine into DB.</p>
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
   * @param pAddParam additional param, e.g. return this line's
   * document in "nextEntity" for farther process
   * @param pRequestData Request Data
   * @param pEntity Entity to process
   * @return Entity processed for farther process or null
   * @throws Exception - an exception
   **/
  @Override
  public final PurchaseInvoiceLine process(
    final Map<String, Object> pAddParam,
      final PurchaseInvoiceLine pEntity,
        final IRequestData pRequestData) throws Exception {
    if (pEntity.getIsNew()) {
      // Beige-Orm refresh:
      pEntity.setItsOwner(getSrvOrm()
        .retrieveEntity(pAddParam, pEntity.getItsOwner()));
      // optimistic locking (dirty check):
      Long ownerVersion = Long.valueOf(pRequestData
        .getParameter(PurchaseInvoice.class.getSimpleName() + ".ownerVersion"));
      pEntity.getItsOwner().setItsVersion(ownerVersion);
      if (pEntity.getReversedId() != null) {
        PurchaseInvoiceLine reversed = getSrvOrm().retrieveEntityById(
          pAddParam, PurchaseInvoiceLine.class, pEntity.getReversedId());
        if (reversed.getReversedId() != null) {
          throw new ExceptionWithCode(ExceptionWithCode.FORBIDDEN,
            "attempt_to_reverse_reversed::" + pAddParam.get("user"));
        }
        if (!reversed.getItsQuantity().equals(reversed.getTheRest())) {
          throw new ExceptionWithCode(ExceptionWithCode
            .WRONG_PARAMETER, "where_is_withdrawals_from_this_source::"
              + pAddParam.get("user"));
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
        getSrvOrm().insertEntity(pAddParam, pEntity);
        pEntity.setIsNew(false);
        reversed.setTheRest(BigDecimal.ZERO);
        reversed.setReversedId(pEntity.getItsId());
        getSrvOrm().updateEntity(pAddParam, reversed);
        PurchaseInvoiceGoodsTaxLine pigtlt = new PurchaseInvoiceGoodsTaxLine();
        pigtlt.setItsOwner(reversed);
        List<PurchaseInvoiceGoodsTaxLine> tls = getSrvOrm()
          .retrieveListForField(pAddParam, pigtlt, "itsOwner");
        for (PurchaseInvoiceGoodsTaxLine pigtl : tls) {
          getSrvOrm().deleteEntity(pAddParam, pigtl);
        }
      } else {
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
        pEntity.setInvItem(getSrvOrm()
          .retrieveEntity(pAddParam, pEntity.getInvItem()));
        if (!(InvItem.MATERIAL_ID.equals(pEntity.getInvItem().getItsType()
          .getItsId()) || InvItem.MERCHANDISE_ID.equals(pEntity.getInvItem()
            .getItsType().getItsId()))) {
          throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
            "type_must_be_material_or_merchandise::" + pAddParam.get("user"));
        }
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
        pEntity.setTheRest(pEntity.getItsQuantity());
        BigDecimal totalTaxes = BigDecimal.ZERO;
        BigDecimal totalTaxesFc = BigDecimal.ZERO;
        String taxesDescription = "";
        Set<PurchaseInvoiceGoodsTaxLine> tls = null;
        boolean isItemBasis = !getSrvAccSettings()
          .lazyGetAccSettings(pAddParam).getSalTaxIsInvoiceBase();
        if (!pEntity.getItsOwner().getVendor().getIsForeigner()
          && getSrvAccSettings().lazyGetAccSettings(pAddParam)
            .getIsExtractSalesTaxFromPurchase()
              && pEntity.getInvItem().getTaxCategory() != null) {
          if (isItemBasis) {
            tls = new HashSet<PurchaseInvoiceGoodsTaxLine>();
          }
          InvItemTaxCategoryLine iitcLn = new InvItemTaxCategoryLine();
          iitcLn.setItsOwner(pEntity.getInvItem().getTaxCategory());
          List<InvItemTaxCategoryLine> iitcll = getSrvOrm()
            .retrieveListForField(pAddParam, iitcLn, "itsOwner");
          BigDecimal bigDecimal100 = new BigDecimal("100.00");
          StringBuffer sb = new StringBuffer();
          int i = 0;
          for (InvItemTaxCategoryLine iitcl : iitcll) {
            if (ETaxType.SALES_TAX_OUTITEM.equals(iitcl.getTax().getItsType())
            || ETaxType.SALES_TAX_INITEM.equals(iitcl.getTax().getItsType())) {
              BigDecimal addTx = pEntity.getSubtotal().multiply(iitcl
                .getItsPercentage()).divide(bigDecimal100, getSrvAccSettings()
                  .lazyGetAccSettings(pAddParam).getPricePrecision(),
                    getSrvAccSettings()
                      .lazyGetAccSettings(pAddParam).getRoundingMode());
              if (i++ > 0) {
                sb.append(", ");
              }
              if (isItemBasis) {
                totalTaxes = totalTaxes.add(addTx);
                PurchaseInvoiceGoodsTaxLine pigtl =
                  new PurchaseInvoiceGoodsTaxLine();
                pigtl.setIsNew(true);
                pigtl.setIdDatabaseBirth(this.srvOrm.getIdDatabase());
                pigtl.setItsTotal(addTx);
                pigtl.setTax(iitcl.getTax());
                if (pEntity.getItsOwner().getForeignCurrency() != null) {
                  BigDecimal addTxFc = pEntity.getForeignSubtotal().multiply(
                iitcl.getItsPercentage()).divide(bigDecimal100,
              getSrvAccSettings().lazyGetAccSettings(pAddParam)
            .getPricePrecision(), getSrvAccSettings()
          .lazyGetAccSettings(pAddParam).getRoundingMode());
                  totalTaxesFc = totalTaxesFc.add(addTxFc);
                  pigtl.setForeignTotalTaxes(addTxFc);
                }
                tls.add(pigtl);
                sb.append(iitcl.getTax().getItsName() + " "
                  + prn(pAddParam, addTx));
              } else {
                sb.append(iitcl.getTax().getItsName());
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
        getSrvOrm().insertEntity(pAddParam, pEntity);
        pEntity.setIsNew(false);
        if (tls != null) {
          for (PurchaseInvoiceGoodsTaxLine pigtl : tls) {
            pigtl.setItsOwner(pEntity);
            pigtl.setInvoiceId(pEntity.getItsOwner().getItsId());
            getSrvOrm().insertEntity(pAddParam, pigtl);
          }
        }
      }
      //draw or reverse warehouse entries:
      srvWarehouseEntry.load(pAddParam, pEntity, pEntity.getWarehouseSite());
      this.utlPurchaseGoodsServiceLine
        .updateOwner(pAddParam, pEntity.getItsOwner());
      pAddParam.put("nextEntity", pEntity.getItsOwner());
      pAddParam.put("nameOwnerEntity", PurchaseInvoice.class.getSimpleName());
      return null;
    } else {
      throw new ExceptionWithCode(ExceptionWithCode.FORBIDDEN,
        "edit_not_allowed::" + pAddParam.get("user"));
    }
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

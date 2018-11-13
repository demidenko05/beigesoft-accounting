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

import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.Comparator;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.beigesoft.model.IRequestData;
import org.beigesoft.exception.ExceptionWithCode;
import org.beigesoft.service.ISrvI18n;
import org.beigesoft.service.IEntityProcessor;
import org.beigesoft.model.IRecordSet;
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.service.ISrvDatabase;
import org.beigesoft.service.ISrvNumberToString;
import org.beigesoft.accounting.model.ETaxType;
import org.beigesoft.accounting.model.CmprTaxCatLnRate;
import org.beigesoft.accounting.model.CmprPurchRetLnTotal;
import org.beigesoft.accounting.persistable.AccSettings;
import org.beigesoft.accounting.persistable.PurchaseInvoiceServiceLine;
import org.beigesoft.accounting.persistable.InvItemTaxCategory;
import org.beigesoft.accounting.persistable.PurchaseReturnGoodsTaxLine;
import org.beigesoft.accounting.persistable.PurchaseInvoiceLine;
import org.beigesoft.accounting.persistable.PurchaseReturn;
import org.beigesoft.accounting.persistable.PurchaseReturnLine;
import org.beigesoft.accounting.persistable.PurchaseReturnTaxLine;
import org.beigesoft.accounting.persistable.Tax;
import org.beigesoft.accounting.persistable.InvItemTaxCategoryLine;
import org.beigesoft.accounting.persistable.UseMaterialEntry;
import org.beigesoft.accounting.service.ISrvDrawItemEntry;
import org.beigesoft.accounting.service.ISrvWarehouseEntry;
import org.beigesoft.accounting.service.ISrvAccSettings;

/**
 * <p>Service that save PurchaseReturnLine into DB.</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class PrcPurchaseReturnLineSave<RS>
  implements IEntityProcessor<PurchaseReturnLine, Long> {

  /**
   * <p>I18N service.</p>
   **/
  private ISrvI18n srvI18n;

  /**
   * <p>Database service.</p>
   **/
  private ISrvDatabase<RS> srvDatabase;

  /**
   * <p>Query Taxes Item basis.</p>
   **/
  private String queryPurchaseReturnLineTaxes;

  /**
   * <p>Query Taxes Item basis Aggregate rate.</p>
   **/
  private String queryPurchRetSalTaxItBasAggr;

  /**
   * <p>Query Taxes Invoice basis Aggregate/Non rate.</p>
   **/
  private String queryPurchRetSalTaxInvBas;

  /**
   * <p>Query invoice totals.</p>
   **/
  private String queryInvTot;

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
  private ISrvDrawItemEntry<UseMaterialEntry> srvUseMaterialEntry;

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
  public final PurchaseReturnLine process(
    final Map<String, Object> pReqVars,
      final PurchaseReturnLine pEntity,
        final IRequestData pRequestData) throws Exception {
    if (pEntity.getIsNew()) {
      //BeigeORM refresh:
      pReqVars.put("PurchaseInvoicevendordeepLevel", 3);
      Set<String> ndFlDc = new HashSet<String>();
      ndFlDc.add("itsId");
      ndFlDc.add("isForeigner");
      ndFlDc.add("taxDestination");
      pReqVars.put("DebtorCreditorneededFields", ndFlDc);
      Set<String> ndFlInv = new HashSet<String>();
      ndFlInv.add("itsId");
      ndFlInv.add("vendor");
      ndFlInv.add("omitTaxes");
      ndFlInv.add("priceIncTax");
      ndFlInv.add("hasMadeAccEntries");
      pReqVars.put("PurchaseInvoiceneededFields", ndFlInv);
      pEntity.setItsOwner(getSrvOrm()
        .retrieveEntity(pReqVars, pEntity.getItsOwner()));
      pReqVars.remove("DebtorCreditorneededFields");
      pReqVars.remove("PurchaseInvoiceneededFields");
      pReqVars.remove("PurchaseInvoicevendordeepLevel");
      pReqVars.put("PurchaseInvoiceLineitsOwnerdeepLevel", 1);
      pEntity.setPurchaseInvoiceLine(getSrvOrm()
        .retrieveEntity(pReqVars, pEntity.getPurchaseInvoiceLine()));
      pReqVars.remove("PurchaseInvoiceLineitsOwnerdeepLevel");
      AccSettings as = getSrvAccSettings().lazyGetAccSettings(pReqVars);
      boolean isItemBasis = !as.getSalTaxIsInvoiceBase();
      boolean isAggrOnlyRate = as.getSalTaxUseAggregItBas();
      RoundingMode rm = as.getSalTaxRoundMode();
      if (pEntity.getPurchaseInvoiceLine().getTaxCategory() != null
        && pEntity.getItsOwner().getPurchaseInvoice().getVendor()
          .getTaxDestination() != null) {
        //override tax method:
        isItemBasis = !pEntity.getItsOwner().getPurchaseInvoice().getVendor()
          .getTaxDestination().getSalTaxIsInvoiceBase();
        isAggrOnlyRate = pEntity.getItsOwner().getPurchaseInvoice()
          .getVendor().getTaxDestination().getSalTaxUseAggregItBas();
        rm = pEntity.getItsOwner().getPurchaseInvoice().getVendor()
          .getTaxDestination().getSalTaxRoundMode();
      }
      if (pEntity.getReversedId() != null) {
        PurchaseReturnLine reversed = getSrvOrm().retrieveEntityById(
          pReqVars, PurchaseReturnLine.class, pEntity.getReversedId());
        if (reversed.getReversedId() != null) {
          throw new ExceptionWithCode(ExceptionWithCode.FORBIDDEN,
            "Attempt to double reverse" + pReqVars.get("user"));
        }
        pEntity.setWarehouseSiteFo(reversed.getWarehouseSiteFo());
        pEntity.setPurchaseInvoiceLine(reversed.getPurchaseInvoiceLine());
        pEntity.setPurchInvLnAppearance(reversed.getPurchInvLnAppearance());
        pEntity.setTaxesDescription(reversed.getTaxesDescription());
        pEntity.setTotalTaxes(reversed.getTotalTaxes().negate());
        pEntity.setItsQuantity(reversed.getItsQuantity().negate());
        pEntity.setSubtotal(reversed.getSubtotal().negate());
        pEntity.setItsTotal(reversed.getItsTotal().negate());
        getSrvOrm().insertEntity(pReqVars, pEntity);
        pEntity.setIsNew(false);
        reversed.setReversedId(pEntity.getItsId());
        getSrvOrm().updateEntity(pReqVars, reversed);
        PurchaseReturnGoodsTaxLine pigtlt = new PurchaseReturnGoodsTaxLine();
        pigtlt.setItsOwner(reversed);
        List<PurchaseReturnGoodsTaxLine> tls = getSrvOrm()
          .retrieveListForField(pReqVars, pigtlt, "itsOwner");
        for (PurchaseReturnGoodsTaxLine pigtl : tls) {
          getSrvOrm().deleteEntity(pReqVars, pigtl);
        }
        srvWarehouseEntry.reverseDraw(pReqVars, pEntity);
        srvUseMaterialEntry.reverseDraw(pReqVars, pEntity,
          pEntity.getItsOwner().getItsDate(),
            pEntity.getItsOwner().getItsId());
      } else {
        if (pEntity.getItsQuantity().doubleValue() <= 0) {
          throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
            "quantity_less_or_equal_zero");
        }
        if (pEntity.getPurchaseInvoiceLine() == null) {
          throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
            "wrong_purchaseInvoiceLine");
        }
        String langDef = (String) pReqVars.get("langDef");
        pEntity.setPurchInvLnAppearance(getSrvI18n().getMsg(PurchaseInvoiceLine
      .class.getSimpleName() + "short", langDef) + " #" + pEntity
    .getPurchaseInvoiceLine().getIdDatabaseBirth() + "-" //local
      + pEntity.getPurchaseInvoiceLine().getItsId() + ", " + pEntity
        .getPurchaseInvoiceLine().getInvItem().getItsName() + ", " + pEntity
          .getPurchaseInvoiceLine().getUnitOfMeasure().getItsName() + ", "
    + getSrvI18n().getMsg("itsCost", langDef) + "=" + prnc(pReqVars, pEntity
      .getPurchaseInvoiceLine().getItsCost()) + ", " + getSrvI18n()
        .getMsg("rest_was", langDef) + "=" + prnq(pReqVars, pEntity
          .getPurchaseInvoiceLine().getTheRest()));
        //using user passed values:
        BigDecimal totalTaxes = BigDecimal.ZERO;
        List<PurchaseReturnGoodsTaxLine> tls = null;
        BigDecimal bd100 = new BigDecimal("100.00");
        if (pEntity.getPurchaseInvoiceLine().getTaxCategory() != null
          && isItemBasis) {
          if (!isAggrOnlyRate) {
            tls = new ArrayList<PurchaseReturnGoodsTaxLine>();
            pReqVars.put("InvItemTaxCategoryLineitsOwnerdeepLevel", 1);
            List<InvItemTaxCategoryLine> itcls = getSrvOrm()
              .retrieveListWithConditions(pReqVars, InvItemTaxCategoryLine
                .class, "where ITSOWNER=" + pEntity.getPurchaseInvoiceLine()
                  .getTaxCategory().getItsId());
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
                PurchaseReturnGoodsTaxLine iitl =
                  new PurchaseReturnGoodsTaxLine();
                iitl.setIsNew(true);
                iitl.setIdDatabaseBirth(this.srvOrm.getIdDatabase());
                iitl.setItsTotal(addTx);
                iitl.setTax(itcl.getTax());
                tls.add(iitl);
                sb.append(itcl.getTax().getItsName() + " "
                  + prn(pReqVars, addTx));
              }
            }
            pEntity.setTaxesDescription(sb.toString());
          } else {
            totalTaxes = pEntity.getSubtotal().multiply(pEntity
              .getPurchaseInvoiceLine().getTaxCategory().getAggrOnlyPercent())
                .divide(bd100, as.getPricePrecision(), rm);
            pEntity.setTaxesDescription(pEntity.getPurchaseInvoiceLine()
              .getTaxCategory().getItsName());
          }
        } else if (pEntity.getPurchaseInvoiceLine().getTaxCategory() != null) {
          pEntity.setTaxesDescription(pEntity.getPurchaseInvoiceLine()
            .getTaxCategory().getItsName());
        }
        if (pEntity.getPurchaseInvoiceLine().getTaxCategory() != null
          && isItemBasis && isAggrOnlyRate) {
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
        BigDecimal sourceCostNt;
        if (pEntity.getItsOwner().getPurchaseInvoice().getPriceIncTax()) {
          sourceCostNt = pEntity.getPurchaseInvoiceLine().getItsCost()
        .divide(BigDecimal.ONE.add(pEntity.getPurchaseInvoiceLine()
      .getTaxCategory().getAggrOnlyPercent().divide(bd100)), as
    .getCostPrecision(), as.getRoundingMode());
        } else {
          sourceCostNt = pEntity.getPurchaseInvoiceLine().getItsCost();
        }
        BigDecimal curCostNt = pEntity.getSubtotal().divide(pEntity
          .getItsQuantity(), as.getCostPrecision(), as.getRoundingMode());
        if (sourceCostNt.compareTo(curCostNt) != 0) {
          if (pEntity.getDescription() == null) {
            pEntity.setDescription(curCostNt.toString() + "!="
              + sourceCostNt + "!");
          } else {
            pEntity.setDescription(pEntity.getDescription() + " " + curCostNt
              + "!=" + sourceCostNt + "!");
          }
        }
        pEntity.setItsTotal(pEntity.getSubtotal().add(totalTaxes));
        getSrvOrm().insertEntity(pReqVars, pEntity);
        pEntity.setIsNew(false);
        if (tls != null) {
          for (PurchaseReturnGoodsTaxLine pigtl : tls) {
            pigtl.setItsOwner(pEntity);
            pigtl.setInvoiceId(pEntity.getItsOwner().getItsId());
            getSrvOrm().insertEntity(pReqVars, pigtl);
            pigtl.setIsNew(false);
          }
        }
        srvWarehouseEntry.withdrawal(pReqVars, pEntity,
          pEntity.getWarehouseSiteFo());
        srvUseMaterialEntry.withdrawalFrom(pReqVars, pEntity,
          pEntity.getPurchaseInvoiceLine(), pEntity.getItsQuantity());
      }
      updateTaxLines(pReqVars, pEntity.getItsOwner(), pEntity
        .getPurchaseInvoiceLine().getTaxCategory() != null, isItemBasis,
          isAggrOnlyRate, as, rm);
      //owner update:
      // optimistic locking (dirty check):
      Long ownerVersion = Long.valueOf(pRequestData
        .getParameter(PurchaseReturn.class.getSimpleName() + ".ownerVersion"));
      pEntity.getItsOwner().setItsVersion(ownerVersion);
      String query = lazyGetQueryInvTot();
      query = query.replace(":ITSOWNER", pEntity.getItsOwner().getItsId()
        .toString());
      String[] columns = new String[]{"SUBTOTAL", "TOTALTAXES"};
      Double[] totals = getSrvDatabase().evalDoubleResults(query, columns);
      if (totals[0] == null) {
        totals[0] = 0d;
      }
      if (totals[1] == null) {
        totals[1] = 0d;
      }
      pEntity.getItsOwner().setSubtotal(BigDecimal.valueOf(totals[0]).setScale(
        getSrvAccSettings().lazyGetAccSettings(pReqVars).getPricePrecision(),
          getSrvAccSettings().lazyGetAccSettings(pReqVars).getRoundingMode()));
      pEntity.getItsOwner().setTotalTaxes(BigDecimal.valueOf(totals[1])
        .setScale(getSrvAccSettings().lazyGetAccSettings(pReqVars)
          .getPricePrecision(), getSrvAccSettings()
            .lazyGetAccSettings(pReqVars).getRoundingMode()));
      pEntity.getItsOwner().setItsTotal(pEntity.getItsOwner().getSubtotal().
        add(pEntity.getItsOwner().getTotalTaxes()));
      getSrvOrm().updateEntity(pReqVars, pEntity.getItsOwner());
    } else {
      throw new ExceptionWithCode(ExceptionWithCode.FORBIDDEN,
        "Attempt to update purchase return line by " + pReqVars.get("user"));
    }
    pReqVars.put("nextEntity", pEntity.getItsOwner());
    pReqVars.put("nameOwnerEntity", PurchaseReturn.class.getSimpleName());
    return null;
  }

  //Utils:
  /**
   * <p>Update invoice Tax Lines.</p>
   * @param pReqVars additional param
   * @param pItsOwner PurchaseReturn
   * @param pIsTaxable Is Taxable
   * @param pIsItemBasis Is Item Basis
   * @param pIsAggrOnlyRate Is Aggregate/Only Rate
   * @param pAs Acc.settings
   * @param pRm tax rounding mode
   * @throws Exception - an exception
   **/
  public final void updateTaxLines(final Map<String, Object> pReqVars,
    final PurchaseReturn pItsOwner, final boolean pIsTaxable,
      final boolean pIsItemBasis, final boolean pIsAggrOnlyRate,
        final AccSettings pAs, final RoundingMode pRm) throws Exception {
    pReqVars.put("PurchaseInvoiceTaxLineitsOwnerdeepLevel", 1);
    List<PurchaseReturnTaxLine> itls = getSrvOrm().retrieveListWithConditions(
        pReqVars, PurchaseReturnTaxLine.class, "where ITSOWNER="
          + pItsOwner.getItsId());
    pReqVars.remove("PurchaseInvoiceTaxLineitsOwnerdeepLevel");
    if (pIsTaxable) {
      String query;
      if (!pIsItemBasis) {
        query = lazyGetQuPurchRetSalTaxInvBas();
      } else if (pIsAggrOnlyRate) {
        query = lazyGetQuPurchRetSalTaxItBasAggr();
      } else {
        query = lazyGetQueryPurchaseReturnLineTaxes();
      }
      query = query.replace(":INVOICEID", pItsOwner.getItsId().toString());
      IRecordSet<RS> recordSet = null;
      //data storage for item basis aggregate rate and invoice basis,
      //and for farther making total/subtotal/cost in invoice lines
      //for invoice basis:
      List<PurchaseInvoiceServiceLine> inLnsDt =
        new ArrayList<PurchaseInvoiceServiceLine>();
      //data storages for item basis with non-aggregate rate:
      List<Long> taxesLst = new ArrayList<Long>();
      List<Double> dbResults = new ArrayList<Double>();
      try {
        recordSet = getSrvDatabase().retrieveRecords(query);
        if (recordSet.moveToFirst()) {
          do {
            Long taxId = recordSet.getLong("TAXID");
            if (!pIsItemBasis) {
              Double percent = recordSet.getDouble("ITSPERCENTAGE");
              Long ilId = recordSet.getLong("TAXCATID");
              PurchaseInvoiceServiceLine invLn = makeLine(inLnsDt, ilId,
                ilId, taxId, percent, pAs);
              invLn.setSubtotal(BigDecimal.valueOf(recordSet
                .getDouble("SUBTOTAL"))
                  .setScale(pAs.getPricePrecision(), RoundingMode.HALF_UP));
            } else {
              if (pIsAggrOnlyRate) { //any tax including
                Long ilId = recordSet.getLong("ILID");
                Double percent = recordSet.getDouble("ITSPERCENTAGE");
                PurchaseInvoiceServiceLine invLn = makeLine(inLnsDt, ilId, ilId,
                  taxId, percent, pAs);
                invLn.setTotalTaxes(BigDecimal.valueOf(recordSet
                  .getDouble("TOTALTAXES"))
                    .setScale(pAs.getPricePrecision(), RoundingMode.HALF_UP));
              } else { //tax excluded
                taxesLst.add(taxId);
                dbResults.add(recordSet.getDouble("TOTALTAX"));
              }
            }
          } while (recordSet.moveToNext());
        }
      } finally {
        if (recordSet != null) {
          recordSet.close();
        }
      }
      if (inLnsDt.size() > 0 && taxesLst.size() >  0) {
        throw new Exception("Algorithm error!!!");
      }
      if (!pIsItemBasis && inLnsDt.size() > 0) {
        Set<Long> taxIds = new HashSet<Long>();
        for (PurchaseInvoiceServiceLine invLn : inLnsDt) {
          for (InvItemTaxCategoryLine itcl : invLn.getTaxCategory()
            .getTaxes()) {
            if (taxIds.contains(itcl.getTax().getItsId())) {
              throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
                "invoice_basis_same_taxes_with_different_tax_cat");
            }
            taxIds.add(itcl.getTax().getItsId());
          }
        }
      }
      if (itls.size() > 0) {
        for (PurchaseReturnTaxLine itl : itls) {
          itl.setTax(null);
          itl.setTaxableInvBas(BigDecimal.ZERO);
          itl.setItsTotal(BigDecimal.ZERO);
        }
      }
      List<PurchaseReturnTaxLine> itlsnew = null;
      if (!(pIsItemBasis && !pIsAggrOnlyRate)) {
        itlsnew = new ArrayList<PurchaseReturnTaxLine>();
      }
      pReqVars.put("countUpdatedItl", Integer.valueOf(0));
      if (inLnsDt.size() > 0) {
        BigDecimal bd100 = new BigDecimal("100.00");
        Comparator<InvItemTaxCategoryLine> cmpr = Collections
          .reverseOrder(new CmprTaxCatLnRate());
        for (PurchaseInvoiceServiceLine invLn : inLnsDt) {
          int ti = 0;
          //total taxes for tax category for updating invoice lines:
          BigDecimal invBasTaxTot = null;
          //aggregate rate line scoped storages:
          BigDecimal taxAggegated = null;
          BigDecimal taxAggrAccum = BigDecimal.ZERO;
          if (pIsAggrOnlyRate) {
            Collections.sort(invLn.getTaxCategory().getTaxes(), cmpr);
          }
          for (InvItemTaxCategoryLine itcl : invLn.getTaxCategory()
            .getTaxes()) {
            ti++;
            if (taxAggegated == null && pIsAggrOnlyRate && pIsItemBasis) {
             //item basis, aggregate/only rate
              taxAggegated = invLn.getTotalTaxes();
            }
            if (!pIsItemBasis) {
              //total taxes for tax category for updating invoice lines:
              invBasTaxTot = invLn.getTotalTaxes();
            }
            if (!pIsItemBasis) {
              invLn.setTotalTaxes(invLn.getSubtotal().multiply(itcl
                .getItsPercentage()).divide(bd100, pAs
                  .getPricePrecision(), pRm));
            } else if (pIsItemBasis && pIsAggrOnlyRate) {
              if (invLn.getTaxCategory().getTaxes().size() == 1
                || ti < invLn.getTaxCategory().getTaxes().size()) {
                invLn.setTotalTaxes(taxAggegated.multiply(itcl
              .getItsPercentage()).divide(invLn.getTaxCategory()
            .getAggrOnlyPercent(), pAs.getPricePrecision(), pRm));
                taxAggrAccum = taxAggrAccum.add(invLn.getTotalTaxes());
              } else {
                invLn.setTotalTaxes(taxAggegated.subtract(taxAggrAccum));
              }
            } else {
              throw new Exception("Algorithm error!!!");
            }
            PurchaseReturnTaxLine itl = findCreateTaxLine(pReqVars, itls,
              itlsnew, itcl.getTax().getItsId());
            itl.setItsOwner(pItsOwner);
            itl.setTax(itcl.getTax());
            makeItl(pReqVars, itl, invLn, pIsItemBasis);
            if (!pIsItemBasis) {
              //total taxes for tax category for updating invoice lines:
              invLn.setTotalTaxes(invBasTaxTot.add(invLn.getTotalTaxes()));
            }
          }
        }
      }
      if (taxesLst.size() >  0) {
        for (int i = 0; i < taxesLst.size(); i++) {
          //item basis, non-aggregate rate, taxes excluded
          Tax tax = new Tax();
          tax.setItsId(taxesLst.get(i));
          PurchaseReturnTaxLine itl;
          itl = findCreateTaxLine(pReqVars, itls, null, tax.getItsId());
          itl.setItsOwner(pItsOwner);
          itl.setTax(tax);
          itl.setItsTotal(BigDecimal.valueOf(dbResults.get(i))
            .setScale(pAs.getPricePrecision(), pRm));
          if (itl.getIsNew()) {
            getSrvOrm().insertEntity(pReqVars, itl);
            itl.setIsNew(false);
          } else {
            getSrvOrm().updateEntity(pReqVars, itl);
          }
        }
      }
      Integer countUpdatedItl = (Integer) pReqVars.get("countUpdatedItl");
      pReqVars.remove("countUpdatedItl");
      if (countUpdatedItl < itls.size()) {
        for (int j = countUpdatedItl; j < itls.size(); j++) {
          getSrvOrm().deleteEntity(pReqVars, itls.get(j));
        }
      }
      if (!pIsItemBasis && inLnsDt.size() > 0) {
        //update subtotal/total/cost invoice lines:
        adjustInvoiceLns(pReqVars, pItsOwner, inLnsDt, pAs);
      }
    } else if (itls.size() > 0) {
      for (PurchaseReturnTaxLine prtln : itls) {
        getSrvOrm().deleteEntity(pReqVars, prtln);
      }
    }
  }

  /**
   * <p>Adjust invoice lines totals/subtotals/cost for invoice basis.</p>
   * @param pReqVars additional param
   * @param pItsOwner invoice
   * @param pTacCatTotLns tax category totals lines
   * @param pAs AS
   * @throws Exception an Exception
   **/
  public final void adjustInvoiceLns(final Map<String, Object> pReqVars,
    final PurchaseReturn pItsOwner,
      final List<PurchaseInvoiceServiceLine> pTacCatTotLns,
        final AccSettings pAs) throws Exception {
    pReqVars.put("PurchaseReturnLineitsOwnerdeepLevel", 1);
    List<PurchaseReturnLine> igls = getSrvOrm().retrieveListWithConditions(
      pReqVars, PurchaseReturnLine.class,
        "where PURCHASEINVOICELINE.TAXCATEGORY is not null and"
  + " PURCHASERETURNLINE.REVERSEDID is null and PURCHASERETURNLINE.ITSOWNER="
+ pItsOwner.getItsId());
    pReqVars.remove("PurchaseReturnLineitsOwnerdeepLevel");
    List<PurchaseReturnLine> ilnt = new ArrayList<PurchaseReturnLine>();
    Comparator<PurchaseReturnLine> cmpr = Collections
      .reverseOrder(new CmprPurchRetLnTotal());
    for (PurchaseInvoiceServiceLine ttl : pTacCatTotLns) {
      for (PurchaseReturnLine igl : igls) {
        if (igl.getPurchaseInvoiceLine().getTaxCategory().getItsId()
          .equals(ttl.getTaxCategory().getItsId())) {
          ilnt.add(igl);
        }
      }
      Collections.sort(ilnt, cmpr);
      BigDecimal txRest = ttl.getTotalTaxes();
      for (int i = 0; i < ilnt.size(); i++) {
        if (i + 1 == ilnt.size()) {
          ilnt.get(i).setItsTotal(ilnt.get(i).getSubtotal().add(txRest));
          ilnt.get(i).setTotalTaxes(txRest);
        } else {
          BigDecimal taxTot;
          taxTot = ttl.getTotalTaxes().multiply(ilnt.get(i).getSubtotal())
    .divide(ttl.getSubtotal(), pAs.getPricePrecision(), RoundingMode.HALF_UP);
          ilnt.get(i).setItsTotal(ilnt.get(i).getSubtotal().add(taxTot));
          ilnt.get(i).setTotalTaxes(taxTot);
          txRest = txRest.subtract(taxTot);
        }
        getSrvOrm().updateEntity(pReqVars, ilnt.get(i));
      }
      ilnt.clear();
    }
  }

  /**
   * <p>Find in old/new lines or create tax line.</p>
   * @param pReqVars additional param
   * @param pTaxLnsWas lines was
   * @param pTaxLnsNew lines new
   * @param pTaxId tax ID
   * @return line
   **/
  public final PurchaseReturnTaxLine findCreateTaxLine(
    final Map<String, Object> pReqVars,
      final List<PurchaseReturnTaxLine> pTaxLnsWas,
        final List<PurchaseReturnTaxLine> pTaxLnsNew, final Long pTaxId) {
    PurchaseReturnTaxLine itl = null;
    if (pTaxLnsWas.size() > 0) {
      for (int k = 0; k < pTaxLnsWas.size(); k++) {
        if (pTaxLnsWas.get(k).getTax() != null
          && pTaxLnsWas.get(k).getTax().getItsId()
            .equals(pTaxId)) {
          itl = pTaxLnsWas.get(k);
          break;
        }
      }
    }
    if (itl == null && pTaxLnsNew.size() > 0) {
      for (int k = 0; k < pTaxLnsNew.size(); k++) {
        if (pTaxLnsNew.get(k).getTax().getItsId()
            .equals(pTaxId)) {
          itl = pTaxLnsNew.get(k);
          break;
        }
      }
    }
    if (itl == null) {
      Integer countUpdatedItl = (Integer) pReqVars.get("countUpdatedItl");
      if (pTaxLnsWas.size() > countUpdatedItl) {
        itl = pTaxLnsWas.get(countUpdatedItl);
        countUpdatedItl++;
        pReqVars.put("countUpdatedItl", countUpdatedItl);
      } else {
        itl = new PurchaseReturnTaxLine();
        itl.setIsNew(true);
        itl.setIdDatabaseBirth(this.srvOrm.getIdDatabase());
        if (pTaxLnsNew != null) {
          pTaxLnsNew.add(itl);
        }
      }
    }
    return itl;
  }

  /**
   * <p>Make invoice line that stores values.</p>
   * @param pInvLns lines
   * @param pIlId line ID
   * @param pCatId tax category ID
   * @param pTaxId tax ID
   * @param pPercent tax rate
   * @param pAs AS
   * @return line
   **/
  public final PurchaseInvoiceServiceLine makeLine(
    final List<PurchaseInvoiceServiceLine> pInvLns, final Long pIlId,
      final Long pCatId,  final Long pTaxId, final Double pPercent,
        final AccSettings pAs) {
    PurchaseInvoiceServiceLine invLn = null;
    for (PurchaseInvoiceServiceLine il : pInvLns) {
      if (il.getItsId().equals(pIlId)) {
        invLn = il;
      }
    }
    if (invLn == null) {
      invLn = new PurchaseInvoiceServiceLine();
      invLn.setItsId(pIlId);
      InvItemTaxCategory tc = new InvItemTaxCategory();
      tc.setItsId(pCatId);
      tc.setTaxes(new ArrayList<InvItemTaxCategoryLine>());
      invLn.setTaxCategory(tc);
      pInvLns.add(invLn);
    }
    InvItemTaxCategoryLine itcl = new InvItemTaxCategoryLine();
    Tax tax = new Tax();
    tax.setItsId(pTaxId);
    itcl.setTax(tax);
    itcl.setItsPercentage(BigDecimal.valueOf(pPercent)
      .setScale(pAs.getTaxPrecision(), RoundingMode.HALF_UP));
    invLn.getTaxCategory().getTaxes().add(itcl);
    invLn.getTaxCategory().setAggrOnlyPercent(invLn.getTaxCategory()
      .getAggrOnlyPercent().add(itcl.getItsPercentage()));
    return invLn;
  }

  /**
   * <p>Makes invoice tax line.</p>
   * @param pReqVars additional param
   * @param pItl PurchaseReturnTaxLine
   * @param pInvLn inventory line
   * @param pIsItemBasis Is Item Basis
   * @throws Exception an Exception
   **/
  public final void makeItl(final Map<String, Object> pReqVars,
    final PurchaseReturnTaxLine pItl, final PurchaseInvoiceServiceLine pInvLn,
      final boolean pIsItemBasis) throws Exception {
    pItl.setItsTotal(pItl.getItsTotal().add(pInvLn.getTotalTaxes()));
    if (!pIsItemBasis) {
      pItl.setTaxableInvBas(pItl.getTaxableInvBas().add(pInvLn.getSubtotal()));
    }
    if (pItl.getIsNew()) {
      getSrvOrm().insertEntity(pReqVars, pItl);
      pItl.setIsNew(false);
    } else {
      getSrvOrm().updateEntity(pReqVars, pItl);
    }
  }

  /**
   * <p>Lazy Get queryPurchaseReturnLineTaxes.</p>
   * @return String
   * @throws Exception - an exception
   **/
  public final String
    lazyGetQueryPurchaseReturnLineTaxes() throws Exception {
    if (this.queryPurchaseReturnLineTaxes == null) {
      String flName = "/accounting/trade/purchaseReturnLineTaxes.sql";
      this.queryPurchaseReturnLineTaxes = loadString(flName);
    }
    return this.queryPurchaseReturnLineTaxes;
  }

  /**
   * <p>Lazy Get queryPurchRetSalTaxItBasAggr.</p>
   * @return String
   * @throws Exception - an exception
   **/
  public final String lazyGetQuPurchRetSalTaxItBasAggr() throws Exception {
    if (this.queryPurchRetSalTaxItBasAggr == null) {
      String flName = "/accounting/trade/purchRetSalTaxItBasAggr.sql";
      this.queryPurchRetSalTaxItBasAggr = loadString(flName);
    }
    return this.queryPurchRetSalTaxItBasAggr;
  }

  /**
   * <p>Lazy Get queryPurchRetSalTaxInvBas.</p>
   * @return String
   * @throws Exception - an exception
   **/
  public final String lazyGetQuPurchRetSalTaxInvBas() throws Exception {
    if (this.queryPurchRetSalTaxInvBas == null) {
      String flName = "/accounting/trade/purchRetSalTaxInvBasis.sql";
      this.queryPurchRetSalTaxInvBas = loadString(flName);
    }
    return this.queryPurchRetSalTaxInvBas;
  }

  /**
   * <p>Lazy Get query invoice totals.</p>
   * @return String
   * @throws Exception - an exception
   **/
  public final String lazyGetQueryInvTot() throws Exception {
    if (this.queryInvTot == null) {
      String flName = "/accounting/trade/purchRetTot.sql";
      this.queryInvTot = loadString(flName);
    }
    return this.queryInvTot;
  }

  /**
   * <p>Load string file (usually SQL query).</p>
   * @param pFileName file name
   * @return String usually SQL query
   * @throws IOException - IO exception
   **/
  public final String loadString(final String pFileName)
        throws IOException {
    URL urlFile = PrcPurchaseReturnLineSave.class
      .getResource(pFileName);
    if (urlFile != null) {
      InputStream inputStream = null;
      try {
        inputStream = PrcPurchaseReturnLineSave.class
          .getResourceAsStream(pFileName);
        byte[] bArray = new byte[inputStream.available()];
        inputStream.read(bArray, 0, inputStream.available());
        return new String(bArray, "UTF-8");
      } finally {
        if (inputStream != null) {
          inputStream.close();
        }
      }
    }
    return null;
  }

  /**
   * <p>Simple delegator to print price.</p>
   * @param pReqVars additional param
   * @param pVal value
   * @return String
   **/
  public final String prn(final Map<String, Object> pReqVars,
    final BigDecimal pVal) {
    return this.srvNumberToString.print(pVal.toString(),
      (String) pReqVars.get("decSepv"),
        (String) pReqVars.get("decGrSepv"),
          (Integer) pReqVars.get("priceDp"),
            (Integer) pReqVars.get("digInGr"));
  }

  /**
   * <p>Simple delegator to print cost.</p>
   * @param pReqVars additional param
   * @param pVal value
   * @return String
   **/
  public final String prnc(final Map<String, Object> pReqVars,
    final BigDecimal pVal) {
    return this.srvNumberToString.print(pVal.toString(),
      (String) pReqVars.get("decSepv"),
        (String) pReqVars.get("decGrSepv"),
          (Integer) pReqVars.get("costPrecision"),
            (Integer) pReqVars.get("digInGr"));
  }

  /**
   * <p>Simple delegator to print quantity.</p>
   * @param pReqVars additional param
   * @param pVal value
   * @return String
   **/
  public final String prnq(final Map<String, Object> pReqVars,
    final BigDecimal pVal) {
    return this.srvNumberToString.print(pVal.toString(),
      (String) pReqVars.get("decSepv"),
        (String) pReqVars.get("decGrSepv"),
          (Integer) pReqVars.get("quantityPrecision"),
            (Integer) pReqVars.get("digInGr"));
  }

  //Simple getters and setters:
  /**
   * <p>Getter for srvI18n.</p>
   * @return ISrvI18n
   **/
  public final ISrvI18n getSrvI18n() {
    return this.srvI18n;
  }

  /**
   * <p>Setter for srvI18n.</p>
   * @param pSrvI18n reference
   **/
  public final void setSrvI18n(final ISrvI18n pSrvI18n) {
    this.srvI18n = pSrvI18n;
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
   * <p>Getter for srvUseMaterialEntry.</p>
   * @return ISrvDrawItemEntry<UseMaterialEntry>
   **/
  public final ISrvDrawItemEntry<UseMaterialEntry> getSrvUseMaterialEntry() {
    return this.srvUseMaterialEntry;
  }

  /**
   * <p>Setter for srvUseMaterialEntry.</p>
   * @param pSrvUseMaterialEntry reference
   **/
  public final void setSrvUseMaterialEntry(
    final ISrvDrawItemEntry<UseMaterialEntry> pSrvUseMaterialEntry) {
    this.srvUseMaterialEntry = pSrvUseMaterialEntry;
  }

  /**
   * <p>Setter for queryPurchaseReturnLineTaxes.</p>
   * @param pQueryPurchaseReturnLineTaxes reference
   **/
  public final void setQueryPurchaseReturnLineTaxes(
    final String pQueryPurchaseReturnLineTaxes) {
    this.queryPurchaseReturnLineTaxes = pQueryPurchaseReturnLineTaxes;
  }

  /**
   * <p>Geter for srvDatabase.</p>
   * @return ISrvDatabase
   **/
  public final ISrvDatabase<RS> getSrvDatabase() {
    return this.srvDatabase;
  }

  /**
   * <p>Setter for srvDatabase.</p>
   * @param pSrvDatabase reference
   **/
  public final void setSrvDatabase(final ISrvDatabase<RS> pSrvDatabase) {
    this.srvDatabase = pSrvDatabase;
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

  /**
   * <p>Setter for queryPurchRetSalTaxItBasAggr.</p>
   * @param pQueryPurchRetSalTaxItBasAggr reference
   **/
  public final void setQueryPurchRetSalTaxItBasAggr(
    final String pQueryPurchRetSalTaxItBasAggr) {
    this.queryPurchRetSalTaxItBasAggr = pQueryPurchRetSalTaxItBasAggr;
  }

  /**
   * <p>Setter for queryPurchRetSalTaxInvBas.</p>
   * @param pQueryPurchRetSalTaxInvBas reference
   **/
  public final void setQueryPurchRetSalTaxInvBas(
    final String pQueryPurchRetSalTaxInvBas) {
    this.queryPurchRetSalTaxInvBas = pQueryPurchRetSalTaxInvBas;
  }

  /**
   * <p>Setter for queryInvTot.</p>
   * @param pQueryInvTot reference
   **/
  public final void setQueryInvTot(final String pQueryInvTot) {
    this.queryInvTot = pQueryInvTot;
  }
}

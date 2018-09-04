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
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.beigesoft.model.IRecordSet;
import org.beigesoft.model.IRequestData;
import org.beigesoft.exception.ExceptionWithCode;
import org.beigesoft.service.IEntityProcessor;
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.service.ISrvDatabase;
import org.beigesoft.service.ISrvNumberToString;
import org.beigesoft.accounting.model.ETaxType;
import org.beigesoft.accounting.persistable.SalesReturn;
import org.beigesoft.accounting.persistable.SalesReturnLine;
import org.beigesoft.accounting.persistable.SalesReturnTaxLine;
import org.beigesoft.accounting.persistable.SalesReturnGoodsTaxLine;
import org.beigesoft.accounting.persistable.SalesInvoiceServiceLine;
import org.beigesoft.accounting.persistable.Tax;
import org.beigesoft.accounting.persistable.AccSettings;
import org.beigesoft.accounting.persistable.DestTaxGoodsLn;
import org.beigesoft.accounting.persistable.InvItemTaxCategory;
import org.beigesoft.accounting.persistable.InvItemTaxCategoryLine;
import org.beigesoft.accounting.service.ISrvWarehouseEntry;
import org.beigesoft.accounting.service.ISrvAccSettings;

/**
 * <p>Service that save SalesReturnLine into DB.</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class PrcSalesReturnLineSave<RS>
  implements IEntityProcessor<SalesReturnLine, Long> {

  /**
   * <p>Database service.</p>
   **/
  private ISrvDatabase<RS> srvDatabase;

  /**
   * <p>Query Taxes Invoice Basis.</p>
   **/
  private String querySalRetTaxInvBas;

  /**
   * <p>Query Taxes Item Basis Aggregate rate.</p>
   **/
  private String querySalRetTaxItBasAggr;

  /**
   * <p>Query Taxes Item Basis.</p>
   **/
  private String querySalRetTaxItemBas;

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
  public final SalesReturnLine process(
    final Map<String, Object> pReqVars,
      final SalesReturnLine pEntity,
        final IRequestData pRequestData) throws Exception {
    if (pEntity.getIsNew()) {
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
      pEntity.setInvItem(getSrvOrm()
        .retrieveEntity(pReqVars, pEntity.getInvItem()));
      AccSettings as = getSrvAccSettings().lazyGetAccSettings(pReqVars);
      boolean isTaxable = as.getIsExtractSalesTaxFromSales() && !pEntity
        .getItsOwner().getOmitTaxes() && !pEntity.getItsOwner().getCustomer()
          .getIsForeigner();
      boolean isItemBasis = !as.getSalTaxIsInvoiceBase();
      boolean isAggrOnlyRate = as.getSalTaxUseAggregItBas();
      RoundingMode rm = as.getSalTaxRoundMode();
      if (isTaxable) {
        pEntity.setTaxCategory(pEntity.getInvItem().getTaxCategory());
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
            if (dtl.getTaxDestination().getItsId().equals(pEntity
              .getItsOwner().getCustomer().getTaxDestination().getItsId())) {
              pEntity.setTaxCategory(dtl.getTaxCategory()); //it may be null
              break;
            }
          }
        }
      }
      if (pEntity.getReversedId() != null) {
        SalesReturnLine reversed = getSrvOrm().retrieveEntityById(pReqVars,
          SalesReturnLine.class, pEntity.getReversedId());
        if (reversed.getReversedId() != null) {
          throw new ExceptionWithCode(ExceptionWithCode.FORBIDDEN,
            "Attempt to double reverse" + pReqVars.get("user"));
        }
        if (!reversed.getItsQuantity().equals(reversed.getTheRest())) {
          throw new ExceptionWithCode(ExceptionWithCode
            .WRONG_PARAMETER, "where_is_withdrawals_from_this_source");
        }
        pEntity.setTheRest(BigDecimal.ZERO);
        pEntity.setInvItem(reversed.getInvItem());
        pEntity.setUnitOfMeasure(reversed.getUnitOfMeasure());
        pEntity.setWarehouseSite(reversed.getWarehouseSite());
        pEntity.setTaxesDescription(reversed.getTaxesDescription());
        pEntity.setTotalTaxes(reversed.getTotalTaxes().negate());
        pEntity.setItsQuantity(reversed.getItsQuantity().negate());
        pEntity.setItsCost(reversed.getItsCost());
        pEntity.setItsPrice(reversed.getItsPrice());
        pEntity.setSubtotal(reversed.getSubtotal().negate());
        pEntity.setItsTotal(reversed.getItsTotal().negate());
        getSrvOrm().insertEntity(pReqVars, pEntity);
        pEntity.setIsNew(false);
        reversed.setTheRest(BigDecimal.ZERO);
        reversed.setReversedId(pEntity.getItsId());
        getSrvOrm().updateEntity(pReqVars, reversed);
        SalesReturnGoodsTaxLine pigtlt = new SalesReturnGoodsTaxLine();
        pigtlt.setItsOwner(reversed);
        List<SalesReturnGoodsTaxLine> tls = getSrvOrm()
          .retrieveListForField(pReqVars, pigtlt, "itsOwner");
        for (SalesReturnGoodsTaxLine pigtl : tls) {
          getSrvOrm().deleteEntity(pReqVars, pigtl);
        }
      } else {
        if (pEntity.getItsQuantity().doubleValue() == 0) {
          throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
            "quantity_is_0");
        }
        if (pEntity.getItsCost().doubleValue() <= 0) {
          throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
            "cost_less_or_eq_zero" + pReqVars.get("user"));
        }
        if (pEntity.getItsPrice().doubleValue() <= 0) {
          throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
            "price_less_eq_0");
        }
        //using user passed values:
        BigDecimal totalTaxes = BigDecimal.ZERO;
        List<SalesReturnGoodsTaxLine> tls = null;
        if (pEntity.getTaxCategory() != null
          && isItemBasis) {
          BigDecimal bd100 = new BigDecimal("100.00");
          if (!isAggrOnlyRate) {
            tls = new ArrayList<SalesReturnGoodsTaxLine>();
            pReqVars.put("InvItemTaxCategoryLineitsOwnerdeepLevel", 1);
            List<InvItemTaxCategoryLine> itcls = getSrvOrm()
              .retrieveListWithConditions(pReqVars, InvItemTaxCategoryLine
                .class, "where ITSOWNER=" + pEntity
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
                SalesReturnGoodsTaxLine iitl =
                  new SalesReturnGoodsTaxLine();
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
              .getTaxCategory().getAggrOnlyPercent())
                .divide(bd100, as.getPricePrecision(), rm);
            pEntity.setTaxesDescription(pEntity
              .getTaxCategory().getItsName());
          }
        } else if (pEntity.getTaxCategory() != null) {
          pEntity.setTaxesDescription(pEntity
            .getTaxCategory().getItsName());
        }
        if (pEntity.getTaxCategory() != null
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
        pEntity.setItsTotal(pEntity.getSubtotal().add(totalTaxes));
        getSrvOrm().insertEntity(pReqVars, pEntity);
        pEntity.setIsNew(false);
        if (tls != null) {
          for (SalesReturnGoodsTaxLine itl : tls) {
            itl.setItsOwner(pEntity);
            itl.setInvoiceId(pEntity.getItsOwner().getItsId());
            getSrvOrm().insertEntity(pReqVars, itl);
            itl.setIsNew(false);
          }
        }
        srvWarehouseEntry.load(pReqVars, pEntity, pEntity.getWarehouseSite());
      }
      updateTaxLines(pReqVars, pEntity.getItsOwner(), isTaxable, isItemBasis,
          isAggrOnlyRate, as, rm);
      //owner update:
      // optimistic locking (dirty check):
      Long ownerVersion = Long.valueOf(pRequestData
        .getParameter(SalesReturn.class.getSimpleName() + ".ownerVersion"));
      pEntity.getItsOwner().setItsVersion(ownerVersion);
      String query = lazyGetQueryInvTot();
      query = query.replace(":ITSOWNER", pEntity.getItsOwner().getItsId()
        .toString());
      String[] columns = new String[]{"SUBTOTAL", "TOTALTAXES"};
      Double[] totals = getSrvDatabase().evalDoubleResults(query, columns);
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
        "Attempt to update Sales Return line by " + pReqVars.get("user"));
    }
    pReqVars.put("nextEntity", pEntity.getItsOwner());
    pReqVars.put("nameOwnerEntity", SalesReturn.class.getSimpleName());
    return null;
  }

  //Utils:
  /**
   * <p>Update invoice Tax Lines.</p>
   * @param pReqVars additional param
   * @param pItsOwner SalesReturn
   * @param pIsTaxable Is Taxable
   * @param pIsItemBasis Is Item Basis
   * @param pIsAggrOnlyRate Is Aggregate/Only Rate
   * @param pAs Acc.settings
   * @param pRm tax rounding mode
   * @throws Exception - an exception
   **/
  public final void updateTaxLines(final Map<String, Object> pReqVars,
    final SalesReturn pItsOwner, final boolean pIsTaxable,
      final boolean pIsItemBasis, final boolean pIsAggrOnlyRate,
        final AccSettings pAs, final RoundingMode pRm) throws Exception {
    pReqVars.put("SalesInvoiceTaxLineitsOwnerdeepLevel", 1);
    List<SalesReturnTaxLine> itls = getSrvOrm().retrieveListWithConditions(
        pReqVars, SalesReturnTaxLine.class, "where ITSOWNER="
          + pItsOwner.getItsId());
    pReqVars.remove("SalesInvoiceTaxLineitsOwnerdeepLevel");
    if (pIsTaxable) {
      String query;
      if (!pIsItemBasis) {
        query = lazyGetQuSalRetTaxInvBas();
      } else if (pIsAggrOnlyRate) {
        query = lazyGetQuSalRetTaxItBasAggr();
      } else {
        query = lazyGetQuSalRetTaxItemBas();
      }
      query = query.replace(":INVOICEID", pItsOwner.getItsId().toString());
      IRecordSet<RS> recordSet = null;
      //lines (goods and services) to store data for item basis aggregate rate
      //and invoice basis with taxes included in price:
      List<SalesInvoiceServiceLine> invLns =
        new ArrayList<SalesInvoiceServiceLine>();
      //data storages for invoice basis price without taxes and item basis
      //with non-aggregate rate:
      List<Long> taxesLst = new ArrayList<Long>();
      List<Double> dbResults = new ArrayList<Double>();
      try {
        recordSet = getSrvDatabase().retrieveRecords(query);
        if (recordSet.moveToFirst()) {
          do {
            if (!pIsItemBasis) {
              Long taxId = recordSet.getLong("TAXID");
              Double percent = recordSet.getDouble("ITSPERCENTAGE");
              taxesLst.add(taxId);
              Double subtotal = recordSet.getDouble("SUBTOTAL");
              dbResults.add(subtotal * percent / 100.0d);
              dbResults.add(subtotal);
            } else {
              if (pIsAggrOnlyRate) { //any tax including
                Long ilId = recordSet.getLong("ILID");
                Long taxId = recordSet.getLong("TAXID");
                Double percent = recordSet.getDouble("ITSPERCENTAGE");
                SalesInvoiceServiceLine invLn = makeLine(invLns, ilId, ilId,
                  taxId, percent);
                invLn.setTotalTaxes(BigDecimal.valueOf(recordSet
                  .getDouble("TOTALTAXES")));
              } else { //tax excluded
                taxesLst.add(recordSet.getLong("TAXID"));
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
      if (invLns.size() > 0 && taxesLst.size() >  0) {
        throw new Exception("Algorithm error!!!");
      }
      if (itls.size() > 0) {
        for (SalesReturnTaxLine itl : itls) {
          itl.setTax(null);
          itl.setTaxableInvBas(BigDecimal.ZERO);
          itl.setItsTotal(BigDecimal.ZERO);
        }
      }
      List<SalesReturnTaxLine> itlsnew = null;
      if (!(pIsItemBasis && !pIsAggrOnlyRate)) {
        itlsnew = new ArrayList<SalesReturnTaxLine>();
      }
      pReqVars.put("countUpdatedItl", Integer.valueOf(0));
      if (invLns.size() > 0) {
        for (SalesInvoiceServiceLine invLn : invLns) {
          int ti = 0;
          BigDecimal taxAggegated = null;
          BigDecimal taxAggrAccum = BigDecimal.ZERO;
          for (InvItemTaxCategoryLine itcl : invLn.getTaxCategory()
            .getTaxes()) {
            ti++;
            if (taxAggegated == null && pIsItemBasis && pIsAggrOnlyRate) {
             //item basis, aggregate/only rate
              taxAggegated = invLn.getTotalTaxes();
            }
            if (pIsItemBasis && pIsAggrOnlyRate) {
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
            SalesReturnTaxLine itl = findCreateTaxLine(pReqVars, itls,
              itlsnew, itcl.getTax().getItsId());
            itl.setItsOwner(pItsOwner);
            itl.setTax(itcl.getTax());
            makeItl(pReqVars, itl, invLn, pIsItemBasis);
          }
        }
      }
      if (taxesLst.size() >  0) {
        List<Tax> taxes = new ArrayList<Tax>();
        for (int i = 0; i < taxesLst.size(); i++) {
          Double totalTax;
          Double taxable = null;
          if (!pIsItemBasis) {
            //invoice basis, any rate, taxes excluded
            Tax tax = new Tax();
            tax.setItsId(taxesLst.get(i));
            taxes.add(tax);
            totalTax = dbResults.get(i * 2);
            taxable = dbResults.get(i * 2 + 1);
          } else {
            //item basis, non-aggregate rate, taxes excluded
            totalTax = dbResults.get(i);
            Tax tax = new Tax();
            tax.setItsId(taxesLst.get(i));
            taxes.add(tax);
          }
          for (int j = 0; j < taxes.size();  j++) {
            SalesReturnTaxLine itl;
            if (!pIsItemBasis) {
              itl = findCreateTaxLine(pReqVars, itls,
                itlsnew, taxes.get(j).getItsId());
            } else {
              itl = findCreateTaxLine(pReqVars, itls,
                taxes.get(j).getItsId());
            }
            itl.setItsOwner(pItsOwner);
            itl.setTax(taxes.get(j));
            makeItl(pReqVars, itl, totalTax, taxable, pAs, pRm);
          }
          taxes.clear();
        }
      }
      Integer countUpdatedItl = (Integer) pReqVars.get("countUpdatedItl");
      pReqVars.remove("countUpdatedItl");
      if (countUpdatedItl < itls.size()) {
        for (int j = countUpdatedItl; j < itls.size(); j++) {
          getSrvOrm().deleteEntity(pReqVars, itls.get(j));
        }
      }
    } else if (itls.size() > 0) {
      for (SalesReturnTaxLine prtln : itls) {
        getSrvOrm().deleteEntity(pReqVars, prtln);
      }
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
  public final SalesReturnTaxLine findCreateTaxLine(
    final Map<String, Object> pReqVars,
      final List<SalesReturnTaxLine> pTaxLnsWas,
        final List<SalesReturnTaxLine> pTaxLnsNew, final Long pTaxId) {
    SalesReturnTaxLine itl = null;
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
        itl = new SalesReturnTaxLine();
        itl.setIsNew(true);
        itl.setIdDatabaseBirth(this.srvOrm.getIdDatabase());
        pTaxLnsNew.add(itl);
      }
    }
    return itl;
  }

  /**
   * <p>Find in old lines or create tax line.</p>
   * @param pReqVars additional param
   * @param pTaxLnsWas lines was
   * @param pTaxId tax ID
   * @return line
   **/
  public final SalesReturnTaxLine findCreateTaxLine(
    final Map<String, Object> pReqVars,
      final List<SalesReturnTaxLine> pTaxLnsWas, final Long pTaxId) {
    SalesReturnTaxLine itl = null;
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
    if (itl == null) {
      Integer countUpdatedItl = (Integer) pReqVars.get("countUpdatedItl");
      if (pTaxLnsWas.size() > countUpdatedItl) {
        itl = pTaxLnsWas.get(countUpdatedItl);
        countUpdatedItl++;
        pReqVars.put("countUpdatedItl", countUpdatedItl);
      } else {
        itl = new SalesReturnTaxLine();
        itl.setIsNew(true);
        itl.setIdDatabaseBirth(this.srvOrm.getIdDatabase());
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
   * @return line
   **/
  public final SalesInvoiceServiceLine makeLine(
    final List<SalesInvoiceServiceLine> pInvLns, final Long pIlId,
      final Long pCatId,  final Long pTaxId, final Double pPercent) {
    SalesInvoiceServiceLine invLn = null;
    for (SalesInvoiceServiceLine il : pInvLns) {
      if (il.getItsId().equals(pIlId)) {
        invLn = il;
      }
    }
    if (invLn == null) {
      invLn = new SalesInvoiceServiceLine();
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
    itcl.setItsPercentage(BigDecimal.valueOf(pPercent));
    invLn.getTaxCategory().getTaxes().add(itcl);
    invLn.getTaxCategory().setAggrOnlyPercent(invLn.getTaxCategory()
      .getAggrOnlyPercent().add(itcl.getItsPercentage()));
    return invLn;
  }

  /**
   * <p>Makes invoice tax line.</p>
   * @param pReqVars additional param
   * @param pItl SalesReturnTaxLine
   * @param pInvLn inventory line
   * @param pIsItemBasis Is Item Basis
   * @throws Exception an Exception
   **/
  public final void makeItl(final Map<String, Object> pReqVars,
    final SalesReturnTaxLine pItl, final SalesInvoiceServiceLine pInvLn,
      final boolean pIsItemBasis) throws Exception {
    pItl.setItsTotal(pItl.getItsTotal().add(pInvLn.getTotalTaxes()));
    if (!pIsItemBasis) {
      pItl.setTaxableInvBas(pItl.getTaxableInvBas().add(pInvLn.getItsTotal()));
    }
    if (pItl.getIsNew()) {
      getSrvOrm().insertEntity(pReqVars, pItl);
      pItl.setIsNew(false);
    } else {
      getSrvOrm().updateEntity(pReqVars, pItl);
    }
  }

  /**
   * <p>Makes invoice tax line.</p>
   * @param pReqVars additional param
   * @param pItl SalesReturnTaxLine
   * @param pTotalTax Total Tax
   * @param pTaxable Taxable
   * @param pAs ACC Settings
   * @param pRm rounding mode
   * @throws Exception an Exception
   **/
  public final void makeItl(final Map<String, Object> pReqVars,
    final SalesReturnTaxLine pItl, final Double pTotalTax,
      final Double pTaxable, final AccSettings pAs,
        final RoundingMode pRm) throws Exception {
    pItl.setItsTotal(pItl.getItsTotal().add(BigDecimal.valueOf(pTotalTax)
      .setScale(pAs.getPricePrecision(), pRm)));
    if (pTaxable != null) {
      pItl.setTaxableInvBas(pItl.getTaxableInvBas().add(BigDecimal
  .valueOf(pTaxable).setScale(pAs.getPricePrecision(), pAs.getRoundingMode())));
    }
    if (pItl.getIsNew()) {
      getSrvOrm().insertEntity(pReqVars, pItl);
      pItl.setIsNew(false);
    } else {
      getSrvOrm().updateEntity(pReqVars, pItl);
    }
  }

  /**
   * <p>Lazy Get Query SalRetTaxItBasAggr.</p>
   * @return String
   * @throws Exception - an exception
   **/
  public final String lazyGetQuSalRetTaxItBasAggr() throws Exception {
    if (this.querySalRetTaxItBasAggr == null) {
      String flName = "/accounting/trade/salRetTaxItBasAggr.sql";
      this.querySalRetTaxItBasAggr = loadString(flName);
    }
    return this.querySalRetTaxItBasAggr;
  }

  /**
   * <p>Lazy Get Query SalRetTaxInvBas.</p>
   * @return String
   * @throws Exception - an exception
   **/
  public final String lazyGetQuSalRetTaxInvBas() throws Exception {
    if (this.querySalRetTaxInvBas == null) {
      String flName = "/accounting/trade/salRetTaxInvBas.sql";
      this.querySalRetTaxInvBas = loadString(flName);
    }
    return this.querySalRetTaxInvBas;
  }

  /**
   * <p>Lazy Get Query SalRetTaxItemBas.</p>
   * @return String
   * @throws Exception - an exception
   **/
  public final String lazyGetQuSalRetTaxItemBas() throws Exception {
    if (this.querySalRetTaxItemBas == null) {
      String flName = "/accounting/trade/salRetTaxItemBas.sql";
      this.querySalRetTaxItemBas = loadString(flName);
    }
    return this.querySalRetTaxItemBas;
  }

  /**
   * <p>Lazy Get query invoice totals.</p>
   * @return String
   * @throws Exception - an exception
   **/
  public final String lazyGetQueryInvTot() throws Exception {
    if (this.queryInvTot == null) {
      String flName = "/accounting/trade/salRetTot.sql";
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
    URL urlFile = PrcSalesReturnLineSave.class
      .getResource(pFileName);
    if (urlFile != null) {
      InputStream inputStream = null;
      try {
        inputStream = PrcSalesReturnLineSave.class
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
   * <p>Setter for queryInvTot.</p>
   * @param pQueryInvTot reference
   **/
  public final void setQueryInvTot(final String pQueryInvTot) {
    this.queryInvTot = pQueryInvTot;
  }

  /**
   * <p>Setter for querySalRetTaxInvBas.</p>
   * @param pQuerySalRetTaxInvBas reference
   **/
  public final void setQuerySalRetTaxInvBas(
    final String pQuerySalRetTaxInvBas) {
    this.querySalRetTaxInvBas = pQuerySalRetTaxInvBas;
  }

  /**
   * <p>Setter for querySalRetTaxItBasAggr.</p>
   * @param pQuerySalRetTaxItBasAggr reference
   **/
  public final void setQuerySalRetTaxItBasAggr(
    final String pQuerySalRetTaxItBasAggr) {
    this.querySalRetTaxItBasAggr = pQuerySalRetTaxItBasAggr;
  }

  /**
   * <p>Setter for querySalRetTaxItemBas.</p>
   * @param pQuerySalRetTaxItemBas reference
   **/
  public final void setQuerySalRetTaxItemBas(
    final String pQuerySalRetTaxItemBas) {
    this.querySalRetTaxItemBas = pQuerySalRetTaxItemBas;
  }
}

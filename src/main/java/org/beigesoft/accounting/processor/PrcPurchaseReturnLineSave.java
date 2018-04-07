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
import org.beigesoft.accounting.model.ETaxType;
import org.beigesoft.accounting.persistable.PurchaseInvoice;
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
   * <p>Query Vendor Invoice Line Taxes.</p>
   **/
  private String queryPurchaseReturnLineTaxes;

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
   * <p>Process entity request.</p>
   * @param pAddParam additional param, e.g. return this line's
   * document in "nextEntity" for farther process
   * @param pRequestData Request Data
   * @param pEntity Entity to process
   * @return Entity processed for farther process or null
   * @throws Exception - an exception
   **/
  @Override
  public final PurchaseReturnLine process(
    final Map<String, Object> pAddParam,
      final PurchaseReturnLine pEntity,
        final IRequestData pRequestData) throws Exception {
    if (pEntity.getIsNew()) {
      //BeigeORM refresh:
      pEntity.setItsOwner(getSrvOrm()
        .retrieveEntity(pAddParam, pEntity.getItsOwner()));
      PurchaseInvoice purchaseInvoice = getSrvOrm().retrieveEntity(pAddParam,
        pEntity.getItsOwner().getPurchaseInvoice());
      // optimistic locking (dirty check):
      Long ownerVersion = Long.valueOf(pRequestData
        .getParameter(PurchaseReturn.class.getSimpleName() + ".ownerVersion"));
      pEntity.getItsOwner().setItsVersion(ownerVersion);
      if (pEntity.getReversedId() != null) {
        PurchaseReturnLine reversed = getSrvOrm().retrieveEntityById(
          pAddParam, PurchaseReturnLine.class, pEntity.getReversedId());
        if (reversed.getReversedId() != null) {
          throw new ExceptionWithCode(ExceptionWithCode.FORBIDDEN,
            "Attempt to double reverse" + pAddParam.get("user"));
        }
        pEntity.setWarehouseSiteFo(reversed.getWarehouseSiteFo());
        pEntity.setPurchaseInvoiceLine(reversed.getPurchaseInvoiceLine());
        pEntity.setPurchInvLnAppearance(reversed.getPurchInvLnAppearance());
        pEntity.setTaxesDescription(reversed.getTaxesDescription());
        pEntity.setTotalTaxes(reversed.getTotalTaxes().negate());
        pEntity.setItsQuantity(reversed.getItsQuantity().negate());
        pEntity.setSubtotal(reversed.getSubtotal().negate());
        pEntity.setItsTotal(reversed.getItsTotal().negate());
        getSrvOrm().insertEntity(pAddParam, pEntity);
        reversed.setReversedId(pEntity.getItsId());
        getSrvOrm().updateEntity(pAddParam, reversed);
        srvWarehouseEntry.reverseDraw(pAddParam, pEntity);
        srvUseMaterialEntry.reverseDraw(pAddParam, pEntity,
          pEntity.getItsOwner().getItsDate(),
            pEntity.getItsOwner().getItsId());
      } else {
        if (pEntity.getItsQuantity().doubleValue() == 0) {
          throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
            "Quantity is 0!" + pAddParam.get("user"));
        }
        if (pEntity.getPurchaseInvoiceLine() == null) {
          throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
            "wrong_purchaseInvoiceLine");
        }
        //BeigeORM refresh:
        pEntity.setPurchaseInvoiceLine(getSrvOrm()
          .retrieveEntity(pAddParam, pEntity.getPurchaseInvoiceLine()));
        pEntity.setPurchInvLnAppearance(getSrvI18n().getMsg(PurchaseInvoiceLine
          .class.getSimpleName() + "short") + " #" + pEntity
            .getPurchaseInvoiceLine().getIdDatabaseBirth() + "-" //local
              + pEntity.getPurchaseInvoiceLine().getItsId() + ", " + pEntity
            .getPurchaseInvoiceLine().getInvItem().getItsName() + ", " + pEntity
              .getPurchaseInvoiceLine().getUnitOfMeasure().getItsName() + ", "
            + getSrvI18n().getMsg("itsCost") + "=" + pEntity
              .getPurchaseInvoiceLine().getItsCost() + ", " + getSrvI18n()
            .getMsg("rest_was") + "=" + pEntity.getPurchaseInvoiceLine()
              .getTheRest());
        //rounding:
        pEntity.setItsQuantity(pEntity.getItsQuantity().setScale(
          getSrvAccSettings().lazyGetAccSettings(pAddParam)
            .getQuantityPrecision(), getSrvAccSettings()
              .lazyGetAccSettings(pAddParam).getRoundingMode()));
        pEntity.setSubtotal(pEntity.getItsQuantity().multiply(pEntity
          .getPurchaseInvoiceLine().getItsCost()).setScale(getSrvAccSettings()
            .lazyGetAccSettings(pAddParam).getPricePrecision(),
              getSrvAccSettings().lazyGetAccSettings(pAddParam)
                .getRoundingMode()));
        BigDecimal totalTaxes = BigDecimal.ZERO;
        String taxesDescription = "";
        if (!purchaseInvoice.getVendor().getIsForeigner()
          && getSrvAccSettings().lazyGetAccSettings(pAddParam)
            .getIsExtractSalesTaxFromPurchase()
            && pEntity.getPurchaseInvoiceLine().getInvItem()
              .getTaxCategory() != null) {
          List<InvItemTaxCategoryLine> pstl = getSrvOrm()
            .retrieveListWithConditions(pAddParam,
              InvItemTaxCategoryLine.class, "where ITSOWNER="
                + pEntity.getPurchaseInvoiceLine().getInvItem()
                  .getTaxCategory().getItsId());
          BigDecimal bigDecimal100 = new BigDecimal("100.00");
          StringBuffer sb = new StringBuffer();
          int i = 0;
          for (InvItemTaxCategoryLine pst : pstl) {
            if (ETaxType.SALES_TAX_OUTITEM.equals(pst.getTax().getItsType())
            || ETaxType.SALES_TAX_INITEM.equals(pst.getTax().getItsType())) {
              BigDecimal addTx = pEntity.getSubtotal().multiply(pst
                .getItsPercentage()).divide(bigDecimal100, getSrvAccSettings()
                  .lazyGetAccSettings(pAddParam).getPricePrecision(),
                    getSrvAccSettings().lazyGetAccSettings(pAddParam)
                      .getRoundingMode());
              totalTaxes = totalTaxes.add(addTx);
              if (i++ > 0) {
                sb.append(", ");
              }
              sb.append(pst.getTax().getItsName() + " "
                + pst.getItsPercentage() + "%=" + addTx);
            }
          }
          taxesDescription = sb.toString();
        }
        pEntity.setTaxesDescription(taxesDescription);
        pEntity.setTotalTaxes(totalTaxes);
        pEntity.setItsTotal(pEntity.getSubtotal().add(totalTaxes));
        getSrvOrm().insertEntity(pAddParam, pEntity);
        srvWarehouseEntry.withdrawal(pAddParam, pEntity,
          pEntity.getWarehouseSiteFo());
        srvUseMaterialEntry.withdrawalFrom(pAddParam, pEntity,
          pEntity.getPurchaseInvoiceLine(), pEntity.getItsQuantity());
      }
      //owner update:
      String query =
      "select sum(SUBTOTAL) as SUBTOTAL, sum(TOTALTAXES) as TOTALTAXES from"
        + " PURCHASERETURNLINE where ITSOWNER="
          + pEntity.getItsOwner().getItsId();
      String[] columns = new String[]{"SUBTOTAL", "TOTALTAXES"};
      Double[] totals = getSrvDatabase().evalDoubleResults(query, columns);
      pEntity.getItsOwner().setSubtotal(BigDecimal.valueOf(totals[0]).setScale(
        getSrvAccSettings().lazyGetAccSettings(pAddParam).getPricePrecision(),
          getSrvAccSettings().lazyGetAccSettings(pAddParam).getRoundingMode()));
      pEntity.getItsOwner().setTotalTaxes(BigDecimal.valueOf(totals[1])
        .setScale(getSrvAccSettings().lazyGetAccSettings(pAddParam)
          .getPricePrecision(), getSrvAccSettings()
            .lazyGetAccSettings(pAddParam).getRoundingMode()));
      pEntity.getItsOwner().setItsTotal(pEntity.getItsOwner().getSubtotal().
        add(pEntity.getItsOwner().getTotalTaxes()));
      getSrvOrm().updateEntity(pAddParam, pEntity.getItsOwner());
      updateTaxLines(pAddParam, pEntity.getItsOwner(), purchaseInvoice);
    } else {
      throw new ExceptionWithCode(ExceptionWithCode.FORBIDDEN,
        "Attempt to update sales invoice line by " + pAddParam.get("user"));
    }
    pAddParam.put("nextEntity", pEntity.getItsOwner());
    pAddParam.put("nameOwnerEntity", PurchaseReturn.class.getSimpleName());
    pRequestData.setAttribute("accSettings",
      this.srvAccSettings.lazyGetAccSettings(pAddParam));
    return null;
  }

  //Utils:
  /**
   * <p>Update invoice Tax Lines.</p>
   * @param pAddParam additional param
   * @param pItsOwner PurchaseReturn
   * @param pPurchaseInvoice PurchaseInvoice
   * @throws Exception - an exception
   **/
  public final void updateTaxLines(final Map<String, Object> pAddParam,
    final PurchaseReturn pItsOwner,
      final PurchaseInvoice pPurchaseInvoice) throws Exception {
    List<PurchaseReturnTaxLine> citl = getSrvOrm().retrieveListWithConditions(
        pAddParam, PurchaseReturnTaxLine.class, "where ITSOWNER="
          + pItsOwner.getItsId());
    if (!pPurchaseInvoice.getVendor().getIsForeigner()
      && getSrvAccSettings().lazyGetAccSettings(pAddParam)
        .getIsExtractSalesTaxFromPurchase()) {
      String query = lazyGetQueryPurchaseReturnLineTaxes().replace(":ITSOWNER",
        pItsOwner.getItsId().toString());
      int countUpdatedSitl = 0;
      IRecordSet<RS> recordSet = null;
      try {
        recordSet = getSrvDatabase().retrieveRecords(query);
        if (recordSet.moveToFirst()) {
          do {
            Long taxId = recordSet.getLong("TAXID");
            Double totalTax = recordSet.getDouble("TOTALTAX");
            PurchaseReturnTaxLine cit;
            if (citl.size() > countUpdatedSitl) {
              cit = citl.get(countUpdatedSitl);
              countUpdatedSitl++;
            } else {
              cit = new PurchaseReturnTaxLine();
              cit.setIdDatabaseBirth(this.srvOrm.getIdDatabase());
              cit.setItsOwner(pItsOwner);
              cit.setIsNew(true);
            }
            Tax tax = new Tax();
            tax.setItsId(taxId);
            cit.setTax(tax);
            cit.setItsTotal(BigDecimal.valueOf(totalTax).setScale(
              getSrvAccSettings().lazyGetAccSettings(pAddParam)
                .getPricePrecision(), getSrvAccSettings()
                  .lazyGetAccSettings(pAddParam).getRoundingMode()));
            if (cit.getIsNew()) {
              getSrvOrm().insertEntity(pAddParam, cit);
            } else {
              getSrvOrm().updateEntity(pAddParam, cit);
            }
          } while (recordSet.moveToNext());
        }
      } finally {
        if (recordSet != null) {
          recordSet.close();
        }
      }
      if (countUpdatedSitl < citl.size()) {
        for (int j = countUpdatedSitl; j < citl.size(); j++) {
          getSrvOrm().deleteEntity(pAddParam, citl.get(j));
        }
      }
    } else if (citl.size() > 0) {
      for (PurchaseReturnTaxLine prtln : citl) {
        getSrvOrm().deleteEntity(pAddParam, prtln);
      }
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
}

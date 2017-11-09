package org.beigesoft.accounting.holder;

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
import java.util.HashMap;

import org.beigesoft.persistable.IPersistableBase;
import org.beigesoft.persistable.EmailMsg;
import org.beigesoft.holder.IHolderForClassByName;
import org.beigesoft.orm.processor.PrcEntityFfolDelete;
import org.beigesoft.orm.processor.PrcEntityFfolSave;
import org.beigesoft.orm.processor.PrcEmailMsgSave;
import org.beigesoft.accounting.processor.PrcAccEntityPbDelete;
import org.beigesoft.accounting.processor.PrcAccEntityDelete;
import org.beigesoft.accounting.processor.PrcAccEntityFolDelete;
import org.beigesoft.accounting.processor.PrcAccEntityFolSave;
import org.beigesoft.accounting.processor.PrcAccEntityPbCopy;
import org.beigesoft.accounting.processor.PrcAccEntityCopy;
import org.beigesoft.accounting.processor.PrcAccEntityPbWithSubaccCopy;
import org.beigesoft.accounting.processor.PrcAccEntityWithSubaccCopy;
import org.beigesoft.accounting.processor.PrcAccEntityPbSave;
import org.beigesoft.accounting.processor.PrcAccEntitySave;
import org.beigesoft.accounting.processor.PrcAccEntityRetrieve;
import org.beigesoft.accounting.processor.PrcAccEntityPbEditDelete;
import org.beigesoft.accounting.processor.PrcSubaccountLineCreate;
import org.beigesoft.accounting.processor.PrcAccEntityWithSubaccCreate;
import org.beigesoft.accounting.processor.PrcAccEntityCreate;
import org.beigesoft.accounting.processor.PrcAccEntityWithSubaccRetrieve;
import org.beigesoft.accounting.processor.PrcAccEntityPbWithSubaccEditDelete;
import org.beigesoft.accounting.processor.PrcAccDocRetrieve;
import org.beigesoft.accounting.processor.PrcAccDocWithSubaccRetrieve;
import org.beigesoft.accounting.processor.PrcAccDocCogsRetrieve;
import org.beigesoft.accounting.processor.PrcAccDocUseMaterialRetrieve;
import org.beigesoft.accounting.processor.PrcAccDocFullRetrieve;
import org.beigesoft.accounting.processor.PrcAccDocWithTaxesGetForReverse;
import org.beigesoft.accounting.processor.PrcPrepaymentFromSave;
import org.beigesoft.accounting.processor.PrcPrepaymentFromCopy;
import org.beigesoft.accounting.processor.PrcPrepaymentFromGfr;
import org.beigesoft.accounting.processor.PrcPrepaymentToSave;
import org.beigesoft.accounting.processor.PrcPrepaymentToCopy;
import org.beigesoft.accounting.processor.PrcPrepaymentToGfr;
import org.beigesoft.accounting.processor.PrcPaymentFromSave;
import org.beigesoft.accounting.processor.PrcPaymentFromCopy;
import org.beigesoft.accounting.processor.PrcPaymentFromGfr;
import org.beigesoft.accounting.processor.PrcWageSave;
import org.beigesoft.accounting.processor.PrcWageCopy;
import org.beigesoft.accounting.processor.PrcWageGfr;
import org.beigesoft.accounting.processor.PrcWageTaxLineDelete;
import org.beigesoft.accounting.processor.PrcWageTaxLineSave;
import org.beigesoft.accounting.processor.PrcWageLineDelete;
import org.beigesoft.accounting.processor.PrcWageLineSave;
import org.beigesoft.accounting.processor.PrcPaymentToSave;
import org.beigesoft.accounting.processor.PrcPaymentToCopy;
import org.beigesoft.accounting.processor.PrcPaymentToGfr;
import org.beigesoft.accounting.processor.PrcSalesReturnSave;
import org.beigesoft.accounting.processor.PrcSalesReturnLineSave;
import org.beigesoft.accounting.processor.PrcSalesReturnLineCopy;
import org.beigesoft.accounting.processor.PrcSalesReturnLineGfr;
import org.beigesoft.accounting.processor.PrcBeginningInventorySave;
import org.beigesoft.accounting.processor.PrcBeginningInventoryLineSave;
import org.beigesoft.accounting.processor.PrcBeginningInventoryLineCopy;
import org.beigesoft.accounting.processor.PrcBeginningInventoryLineGfr;
import org.beigesoft.accounting.processor.PrcPurchaseInvoiceSave;
import org.beigesoft.accounting.processor.PrcManufacturingProcessSave;
import org.beigesoft.accounting.processor.PrcManufacturingProcessGfr;
import org.beigesoft.accounting.processor.PrcAdditionCostLineDelete;
import org.beigesoft.accounting.processor.PrcAdditionCostLineSave;
import org.beigesoft.accounting.processor.PrcUsedMaterialLineCopy;
import org.beigesoft.accounting.processor.PrcUsedMaterialLineGfr;
import org.beigesoft.accounting.processor.PrcUsedMaterialLineSave;
import org.beigesoft.accounting.processor.PrcManufactureSave;
import org.beigesoft.accounting.processor.PrcManufactureGfr;
import org.beigesoft.accounting.processor.PrcManufactureCopy;
import org.beigesoft.accounting.processor.PrcPurchaseReturnSave;
import org.beigesoft.accounting.processor.PrcPurchaseReturnLineSave;
import org.beigesoft.accounting.processor.PrcPurchaseReturnLineCopy;
import org.beigesoft.accounting.processor.PrcPurchaseReturnLineGfr;
import org.beigesoft.accounting.processor.PrcGoodsLossSave;
import org.beigesoft.accounting.processor.PrcGoodsLossLineCopy;
import org.beigesoft.accounting.processor.PrcGoodsLossLineGfr;
import org.beigesoft.accounting.processor.PrcGoodsLossLineSave;
import org.beigesoft.accounting.processor.PrcSalesInvoiceSave;
import org.beigesoft.accounting.processor.PrcSalesInvoiceLineCopy;
import org.beigesoft.accounting.processor.PrcSalesInvoiceLineGfr;
import org.beigesoft.accounting.processor.PrcSalesInvoiceLineSave;
import org.beigesoft.accounting.processor.PrcPurchaseInvoiceServiceLineSave;
import org.beigesoft.accounting.processor.PrcPurchaseInvoiceServiceLineDelete;
import org.beigesoft.accounting.processor.PrcPurchaseInvoiceLineSave;
import org.beigesoft.accounting.processor.PrcPurchaseInvoiceLineCopy;
import org.beigesoft.accounting.processor.PrcPurchaseInvoiceLineGfr;
import org.beigesoft.accounting.processor.PrcAccDocGetForReverse;
import org.beigesoft.accounting.processor.PrcAccDocWithTaxesCopy;
import org.beigesoft.accounting.processor.PrcAccDocWithTaxesPaymentsCopy;
import org.beigesoft.accounting.processor.PrcInvItemTaxCategoryLineDelete;
import org.beigesoft.accounting.processor.PrcInvItemTaxCategoryLineSave;
import org.beigesoft.accounting.processor.PrcInpAccEntriesRetrieve;
import org.beigesoft.accounting.processor.PrcAccEntrySave;
import org.beigesoft.accounting.processor.PrcAccEntryCreate;
import org.beigesoft.accounting.processor.PrcAccEntryCopy;
import org.beigesoft.accounting.processor.PrcAccEntrySaveDescr;
import org.beigesoft.accounting.processor.PrcMoveItemsLineSave;
import org.beigesoft.accounting.processor.PrcMoveItemsRetrieve;
import org.beigesoft.accounting.processor.PrcAccSettingsSave;
import org.beigesoft.accounting.persistable.AccSettings;
import org.beigesoft.accounting.persistable.AccountingEntries;
import org.beigesoft.accounting.persistable.AccountingEntry;
import org.beigesoft.accounting.persistable.Account;
import org.beigesoft.accounting.persistable.SubaccountLine;
import org.beigesoft.accounting.persistable.PrepaymentTo;
import org.beigesoft.accounting.persistable.PrepaymentFrom;
import org.beigesoft.accounting.persistable.PaymentTo;
import org.beigesoft.accounting.persistable.MoveItems;
import org.beigesoft.accounting.persistable.MoveItemsLine;
import org.beigesoft.accounting.persistable.PaymentFrom;
import org.beigesoft.accounting.persistable.Wage;
import org.beigesoft.accounting.persistable.PurchaseInvoiceServiceLine;
import org.beigesoft.accounting.persistable.SalesInvoiceLine;
import org.beigesoft.accounting.persistable.PurchaseInvoiceLine;
import org.beigesoft.accounting.persistable.BeginningInventory;
import org.beigesoft.accounting.persistable.BeginningInventoryLine;
import org.beigesoft.accounting.persistable.PurchaseInvoice;
import org.beigesoft.accounting.persistable.Manufacture;
import org.beigesoft.accounting.persistable.ManufacturingProcess;
import org.beigesoft.accounting.persistable.UsedMaterialLine;
import org.beigesoft.accounting.persistable.InvItemTaxCategoryLine;
import org.beigesoft.accounting.persistable.AdditionCostLine;
import org.beigesoft.accounting.persistable.PurchaseReturn;
import org.beigesoft.accounting.persistable.PurchaseReturnLine;
import org.beigesoft.accounting.persistable.GoodsLoss;
import org.beigesoft.accounting.persistable.GoodsLossLine;
import org.beigesoft.accounting.persistable.SalesInvoice;
import org.beigesoft.accounting.persistable.SalesReturn;
import org.beigesoft.accounting.persistable.WageTaxLine;
import org.beigesoft.accounting.persistable.WageLine;
import org.beigesoft.accounting.persistable.SalesReturnLine;
import org.beigesoft.replicator.processor.PrcReplExcludeAccountsDebitCreditSave;
import org.beigesoft.replicator.processor.PrcReplicationAccMethodSave;
import org.beigesoft.replicator.persistable.ReplicationAccMethod;
import org.beigesoft.replicator.persistable.
  base.AReplExcludeAccountsDebitCredit;

/**
 * <p>Generic service that assign entities processor name for class
 * and action name.</p>
 *
 * @author Yury Demidenko
 */
public class HldAccEntitiesProcessorNames
  implements IHolderForClassByName<String> {

  /**
   * <p>Additional holder EP names.</p>
   **/
  private IHldAddEntitiesProcessorNames hldAddEntitiesProcessorNames;

  /**
   * <p>EntitiesProcessors names map:
   * "key = class simple name + action"-"processor name".</p>
   **/
  private final Map<String, String> processorsNamesMap =
      new HashMap<String, String>();

  /**
   * <p>Get thing for given class and thing name.
   * findbugs: UG_SYNC_SET_UNSYNC_GET - this code is designed
   * for high performance. Getting name is happened very frequency
   * (e.g. 10 per second by multi-threads).
   * Setting is seldom (e.g. hot change configuration to fix program bug)
   * or may not be happen.</p>
   * @param pClass a Class
   * @param pThingName Thing Name
   * @return a thing
   **/
  @Override
  public final String getFor(final Class<?> pClass, final String pThingName) {
    if ("entityEdit".equals(pThingName)
      || "entityConfirmDelete".equals(pThingName)) {
      return getForRetrieveForEditDelete(pClass);
    } else if ("entityCopy".equals(pThingName)) {
      return getForCopy(pClass);
    } else if ("entityPrint".equals(pThingName)) {
      return getForPrint(pClass);
    } else if ("entitySave".equals(pThingName)) {
      return getForSave(pClass);
    } else if ("entityReverse".equals(pThingName)) {
      return getForRetrieveForReverse(pClass);
    } else if ("entityFfolDelete".equals(pThingName)) {
      return getForFfolDelete(pClass);
    } else if ("entityFfolSave".equals(pThingName)) {
      return getForFfolSave(pClass);
    } else if ("entityFolDelete".equals(pThingName)) {
      return getForFolDelete(pClass);
    } else if ("entityFolSave".equals(pThingName)) {
      return getForFolSave(pClass);
    } else if ("entityDelete".equals(pThingName)) {
      return getForDelete(pClass);
    } else if ("entityCreate".equals(pThingName)) {
      return getForCreate(pClass);
    }
    return this.processorsNamesMap
      .get(pClass.getSimpleName() + pThingName);
  }

  /**
   * <p>Set thing for given class and thing name.</p>
   * @param pThing Thing
   * @param pClass Class
   * @param pThingName Thing Name
   **/
  @Override
  public final synchronized void setFor(final String pThing,
    final Class<?> pClass, final String pThingName) {
    if ("entityEdit".equals(pThingName)) {
      throw new RuntimeException("Forbidden code!");
    } else if ("entityPrint".equals(pThingName)) {
      throw new RuntimeException("Forbidden code!");
    } else if ("entityFfolDelete".equals(pThingName)) {
      throw new RuntimeException("Forbidden code!");
    } else if ("entityFfolSave".equals(pThingName)) {
      throw new RuntimeException("Forbidden code!");
    } else if ("entityFolDelete".equals(pThingName)) {
      throw new RuntimeException("Forbidden code!");
    } else if ("entityFolSave".equals(pThingName)) {
      throw new RuntimeException("Forbidden code!");
    } else if ("entityCopy".equals(pThingName)) {
      throw new RuntimeException("Forbidden code!");
    } else if ("entitySave".equals(pThingName)) {
      throw new RuntimeException("Forbidden code!");
    } else if ("entityDelete".equals(pThingName)) {
      throw new RuntimeException("Forbidden code!");
    } else if ("entityCreate".equals(pThingName)) {
      throw new RuntimeException("Forbidden code!");
    } else if ("entityReverse".equals(pThingName)) {
      throw new RuntimeException("Forbidden code!");
    } else if ("entityConfirmDelete".equals(pThingName)) {
      throw new RuntimeException("Forbidden code!");
    }
    this.processorsNamesMap
      .put(pClass.getSimpleName() + pThingName, pThing);
  }

  /**
   * <p>Get processor name for copy.</p>
   * @param pClass a Class
   * @return a thing
   **/
  protected final String getForCopy(final Class<?> pClass) {
    if (PurchaseReturnLine.class == pClass) {
      return PrcPurchaseReturnLineCopy.class.getSimpleName();
    } else if (SalesReturnLine.class == pClass) {
      return PrcSalesReturnLineCopy.class.getSimpleName();
    } else if (AccountingEntry.class == pClass) {
      return PrcAccEntryCopy.class.getSimpleName();
    } else if (UsedMaterialLine.class == pClass) {
      return PrcUsedMaterialLineCopy.class.getSimpleName();
    } else if (GoodsLossLine.class == pClass) {
      return PrcGoodsLossLineCopy.class.getSimpleName();
    } else if (SalesInvoiceLine.class == pClass) {
      return PrcSalesInvoiceLineCopy.class.getSimpleName();
    } else if (BeginningInventoryLine.class == pClass) {
      return PrcBeginningInventoryLineCopy.class.getSimpleName();
    } else if (PurchaseInvoiceLine.class == pClass) {
      return PrcPurchaseInvoiceLineCopy.class.getSimpleName();
    } else if (pClass == SubaccountLine.class || pClass == Account.class) {
      return PrcAccEntityWithSubaccCopy.class.getSimpleName();
    } else if (SalesReturn.class == pClass
      || PurchaseReturn.class == pClass) {
      return PrcAccDocWithTaxesCopy.class.getSimpleName();
    } else if (PurchaseInvoice.class == pClass
      || SalesInvoice.class == pClass) {
      return PrcAccDocWithTaxesPaymentsCopy.class.getSimpleName();
    } else if (PrepaymentFrom.class == pClass) {
      return PrcPrepaymentFromCopy.class.getSimpleName();
    } else if (PrepaymentTo.class == pClass) {
      return PrcPrepaymentToCopy.class.getSimpleName();
    } else if (PaymentFrom.class == pClass) {
      return PrcPaymentFromCopy.class.getSimpleName();
    } else if (Manufacture.class == pClass) {
      return PrcManufactureCopy.class.getSimpleName();
    } else if (Wage.class == pClass) {
      return PrcWageCopy.class.getSimpleName();
    } else if (PaymentTo.class == pClass) {
      return PrcPaymentToCopy.class.getSimpleName();
    } else if (pClass == AdditionCostLine.class) {
      return PrcAccEntityPbWithSubaccCopy.class.getSimpleName();
    } else if (IPersistableBase.class.isAssignableFrom(pClass)) {
      return PrcAccEntityPbCopy.class.getSimpleName();
    } else {
      if (this.hldAddEntitiesProcessorNames != null) {
        String name = this.hldAddEntitiesProcessorNames
          .getForCopy(pClass);
        if (name != null) {
          return name;
        }
      }
      return PrcAccEntityCopy.class.getSimpleName();
    }
  }

  /**
   * <p>Get processor name for print.</p>
   * @param pClass a Class
   * @return a thing
   **/
  protected final String getForPrint(final Class<?> pClass) {
    if (pClass == SubaccountLine.class || pClass == Account.class
          || pClass == AdditionCostLine.class) {
      return PrcAccEntityWithSubaccRetrieve.class.getSimpleName();
    } else if (pClass == MoveItems.class) {
      return PrcMoveItemsRetrieve.class.getSimpleName();
    } else if (pClass == AccountingEntries.class) {
      return PrcInpAccEntriesRetrieve.class.getSimpleName();
    } else if (pClass == PaymentFrom.class || pClass == PaymentTo.class
        || pClass == PrepaymentFrom.class || pClass == PrepaymentTo.class) {
      return PrcAccDocWithSubaccRetrieve.class.getSimpleName();
    } else if (pClass == Wage.class) {
      return PrcAccDocRetrieve.class.getSimpleName();
    } else if (pClass == PurchaseReturn.class
      || pClass == ManufacturingProcess.class || pClass == Manufacture.class) {
      return PrcAccDocUseMaterialRetrieve.class.getSimpleName();
    } else if (pClass == GoodsLoss.class || pClass == SalesInvoice.class) {
      return PrcAccDocCogsRetrieve.class.getSimpleName();
    } else if (pClass == PurchaseInvoice.class || pClass == SalesReturn.class
      || pClass == BeginningInventory.class) {
      return PrcAccDocFullRetrieve.class.getSimpleName();
    } else {
      if (this.hldAddEntitiesProcessorNames != null) {
        String name = this.hldAddEntitiesProcessorNames
          .getForPrint(pClass);
        if (name != null) {
          return name;
        }
      }
      return PrcAccEntityRetrieve.class.getSimpleName();
    }
  }

  /**
   * <p>Get processor name for save.</p>
   * @param pClass a Class
   * @return a thing
   **/
  protected final String getForSave(final Class<?> pClass) {
    if (EmailMsg.class == pClass) {
      return PrcEmailMsgSave.class.getSimpleName();
    } else if (AccSettings.class == pClass) {
      return PrcAccSettingsSave.class.getSimpleName();
    } else if (SalesReturn.class == pClass) {
      return PrcSalesReturnSave.class.getSimpleName();
    } else if (Manufacture.class == pClass) {
      return PrcManufactureSave.class.getSimpleName();
    } else if (ManufacturingProcess.class == pClass) {
      return PrcManufacturingProcessSave.class.getSimpleName();
    } else if (PurchaseReturn.class == pClass) {
      return PrcPurchaseReturnSave.class.getSimpleName();
    } else if (GoodsLoss.class == pClass) {
      return PrcGoodsLossSave.class.getSimpleName();
    } else if (SalesInvoice.class == pClass) {
      return PrcSalesInvoiceSave.class.getSimpleName();
    } else if (BeginningInventory.class == pClass) {
      return PrcBeginningInventorySave.class.getSimpleName();
    } else if (PurchaseInvoice.class == pClass) {
      return PrcPurchaseInvoiceSave.class.getSimpleName();
    } else if (PrepaymentFrom.class == pClass) {
      return PrcPrepaymentFromSave.class.getSimpleName();
    } else if (PrepaymentTo.class == pClass) {
      return PrcPrepaymentToSave.class.getSimpleName();
    } else if (PaymentFrom.class == pClass) {
      return PrcPaymentFromSave.class.getSimpleName();
    } else if (Wage.class == pClass) {
      return PrcWageSave.class.getSimpleName();
    } else if (PaymentTo.class == pClass) {
      return PrcPaymentToSave.class.getSimpleName();
    } else if (ReplicationAccMethod.class == pClass) {
      return PrcReplicationAccMethodSave.class.getSimpleName();
    } else if (AccountingEntry.class == pClass) {
      return PrcAccEntrySaveDescr.class.getSimpleName();
    } else if (IPersistableBase.class.isAssignableFrom(pClass)) {
      return PrcAccEntityPbSave.class.getSimpleName();
    } else {
      if (this.hldAddEntitiesProcessorNames != null) {
        String name = this.hldAddEntitiesProcessorNames
          .getForSave(pClass);
        if (name != null) {
          return name;
        }
      }
      return PrcAccEntitySave.class.getSimpleName();
    }
  }

  /**
   * <p>Get processor name for retrieve for reverse.</p>
   * @param pClass a Class
   * @return a thing
   **/
  protected final String getForRetrieveForReverse(final Class<?> pClass) {
    if (PurchaseReturnLine.class == pClass) {
      return PrcPurchaseReturnLineGfr.class.getSimpleName();
    } else if (SalesReturnLine.class == pClass) {
      return PrcSalesReturnLineGfr.class.getSimpleName();
    } else if (UsedMaterialLine.class == pClass) {
      return PrcUsedMaterialLineGfr.class.getSimpleName();
    } else if (GoodsLossLine.class == pClass) {
      return PrcGoodsLossLineGfr.class.getSimpleName();
    } else if (SalesInvoiceLine.class == pClass) {
      return PrcSalesInvoiceLineGfr.class.getSimpleName();
    } else if (BeginningInventoryLine.class == pClass) {
      return PrcBeginningInventoryLineGfr.class.getSimpleName();
    } else if (PurchaseInvoiceLine.class == pClass) {
      return PrcPurchaseInvoiceLineGfr.class.getSimpleName();
    } else if (PrepaymentFrom.class == pClass) {
      return PrcPrepaymentFromGfr.class.getSimpleName();
    } else if (Manufacture.class == pClass) {
      return PrcManufactureGfr.class.getSimpleName();
    } else if (ManufacturingProcess.class == pClass) {
      return PrcManufacturingProcessGfr.class.getSimpleName();
    } else if (PrepaymentTo.class == pClass) {
      return PrcPrepaymentToGfr.class.getSimpleName();
    } else if (PaymentFrom.class == pClass) {
      return PrcPaymentFromGfr.class.getSimpleName();
    } else if (Wage.class == pClass) {
      return PrcWageGfr.class.getSimpleName();
    } else if (PaymentTo.class == pClass) {
      return PrcPaymentToGfr.class.getSimpleName();
    } else if (GoodsLoss.class == pClass
      || BeginningInventory.class == pClass) {
      return PrcAccDocGetForReverse.class.getSimpleName();
    } else if (PurchaseInvoice.class == pClass || SalesInvoice.class == pClass
      || SalesReturn.class == pClass || PurchaseReturn.class == pClass) {
      return PrcAccDocWithTaxesGetForReverse.class.getSimpleName();
    }
    throw new RuntimeException(
      "there_is_no_retriever_for_reverse_name_for_class::"
        + pClass.getCanonicalName());
  }

  /**
   * <p>Get processor name for FFOL delete.</p>
   * @param pClass a Class
   * @return a thing
   **/
  protected final String getForFfolDelete(final Class<?> pClass) {
    return PrcEntityFfolDelete.class.getSimpleName();
  }

  /**
   * <p>Get processor name for FFOL save.</p>
   * @param pClass a Class
   * @return a thing
   **/
  protected final String getForFfolSave(final Class<?> pClass) {
    if (this.hldAddEntitiesProcessorNames != null) {
      String name = this.hldAddEntitiesProcessorNames
        .getForFfolSave(pClass);
      if (name != null) {
        return name;
      }
    }
    return PrcEntityFfolSave.class.getSimpleName();
  }

  /**
   * <p>Get processor name for FOL delete.</p>
   * @param pClass a Class
   * @return a thing
   **/
  protected final String getForFolDelete(final Class<?> pClass) {
    if (PurchaseInvoiceServiceLine.class == pClass) {
      return PrcPurchaseInvoiceServiceLineDelete.class.getSimpleName();
    } else if (WageTaxLine.class == pClass) {
      return PrcWageTaxLineDelete.class.getSimpleName();
    } else if (InvItemTaxCategoryLine.class == pClass) {
      return PrcInvItemTaxCategoryLineDelete.class.getSimpleName();
    } else if (AdditionCostLine.class == pClass) {
      return PrcAdditionCostLineDelete.class.getSimpleName();
    } else if (WageLine.class == pClass) {
      return PrcWageLineDelete.class.getSimpleName();
    } else {
      if (this.hldAddEntitiesProcessorNames != null) {
        String name = this.hldAddEntitiesProcessorNames
          .getForFolDelete(pClass);
        if (name != null) {
          return name;
        }
      }
      return PrcAccEntityFolDelete.class.getSimpleName();
    }
  }

  /**
   * <p>Get processor name for FOL save.</p>
   * @param pClass a Class
   * @return a thing
   **/
  protected final String getForFolSave(final Class<?> pClass) {
    if (PurchaseInvoiceServiceLine.class == pClass) {
      return PrcPurchaseInvoiceServiceLineSave.class.getSimpleName();
    } else if (PurchaseReturnLine.class == pClass) {
      return PrcPurchaseReturnLineSave.class.getSimpleName();
    } else if (WageTaxLine.class == pClass) {
      return PrcWageTaxLineSave.class.getSimpleName();
    } else if (InvItemTaxCategoryLine.class == pClass) {
      return PrcInvItemTaxCategoryLineSave.class.getSimpleName();
    } else if (AdditionCostLine.class == pClass) {
      return PrcAdditionCostLineSave.class.getSimpleName();
    } else if (WageLine.class == pClass) {
      return PrcWageLineSave.class.getSimpleName();
    } else if (SalesReturnLine.class == pClass) {
      return PrcSalesReturnLineSave.class.getSimpleName();
    } else if (MoveItemsLine.class == pClass) {
      return PrcMoveItemsLineSave.class.getSimpleName();
    } else if (UsedMaterialLine.class == pClass) {
      return PrcUsedMaterialLineSave.class.getSimpleName();
    } else if (AccountingEntry.class == pClass) {
      return PrcAccEntrySave.class.getSimpleName();
    } else if (GoodsLossLine.class == pClass) {
      return PrcGoodsLossLineSave.class.getSimpleName();
    } else if (SalesInvoiceLine.class == pClass) {
      return PrcSalesInvoiceLineSave.class.getSimpleName();
    } else if (BeginningInventoryLine.class == pClass) {
      return PrcBeginningInventoryLineSave.class.getSimpleName();
    } else if (PurchaseInvoiceLine.class == pClass) {
      return PrcPurchaseInvoiceLineSave.class.getSimpleName();
    } else if (AReplExcludeAccountsDebitCredit.class
      .isAssignableFrom(pClass)) {
      return PrcReplExcludeAccountsDebitCreditSave.class.getSimpleName();
    } else {
      if (this.hldAddEntitiesProcessorNames != null) {
        String name = this.hldAddEntitiesProcessorNames
          .getForFolSave(pClass);
        if (name != null) {
          return name;
        }
      }
      return PrcAccEntityFolSave.class.getSimpleName();
    }
  }

  /**
   * <p>Get processor name for delete.</p>
   * @param pClass a Class
   * @return a thing
   **/
  protected final String getForDelete(final Class<?> pClass) {
    if (this.hldAddEntitiesProcessorNames != null) {
      String name = this.hldAddEntitiesProcessorNames
        .getForDelete(pClass);
      if (name != null) {
        return name;
      }
    }
    if (IPersistableBase.class.isAssignableFrom(pClass)) {
      return PrcAccEntityPbDelete.class.getSimpleName();
    } else {
      return PrcAccEntityDelete.class.getSimpleName();
    }
  }

  /**
   * <p>Get processor name for create.</p>
   * @param pClass a Class
   * @return a thing
   **/
  protected final String getForCreate(final Class<?> pClass) {
    if (pClass == Account.class
      || pClass == PaymentFrom.class || pClass == PaymentTo.class
        || pClass == PrepaymentFrom.class || pClass == PrepaymentTo.class
          || pClass == AdditionCostLine.class) {
      return PrcAccEntityWithSubaccCreate.class.getSimpleName();
    } else if (pClass == SubaccountLine.class) {
      return PrcSubaccountLineCreate.class.getSimpleName();
    } else if (pClass == AccountingEntry.class) {
      return PrcAccEntryCreate.class.getSimpleName();
    } else {
      if (this.hldAddEntitiesProcessorNames != null) {
        String name = this.hldAddEntitiesProcessorNames
          .getForCreate(pClass);
        if (name != null) {
          return name;
        }
      }
      return PrcAccEntityCreate.class.getSimpleName();
    }
  }

  /**
   * <p>Get processor name for retrieve to edit/delete.</p>
   * @param pClass a Class
   * @return a thing
   **/
  protected final String getForRetrieveForEditDelete(final Class<?> pClass) {
    if (pClass == SubaccountLine.class || pClass == Account.class) {
      return PrcAccEntityWithSubaccRetrieve.class.getSimpleName();
    } else if (pClass == PaymentFrom.class || pClass == PaymentTo.class
      || pClass == PrepaymentFrom.class || pClass == PrepaymentTo.class
        || pClass == AdditionCostLine.class) {
      return PrcAccEntityPbWithSubaccEditDelete.class.getSimpleName();
    } else if (pClass == AccountingEntries.class) {
      return PrcInpAccEntriesRetrieve.class.getSimpleName();
    } else if (IPersistableBase.class.isAssignableFrom(pClass)) {
      return PrcAccEntityPbEditDelete.class.getSimpleName();
    } else {
      if (this.hldAddEntitiesProcessorNames != null) {
        String name = this.hldAddEntitiesProcessorNames
          .getForRetrieveForEditDelete(pClass);
        if (name != null) {
          return name;
        }
      }
      return PrcAccEntityRetrieve.class.getSimpleName();
    }
  }

  //Simple getters and setters:

  /**
   * <p>Getter for hldAddEntitiesProcessorNames.</p>
   * @return IHldAddEntitiesProcessorNames
   **/
  public final IHldAddEntitiesProcessorNames
    getHldAddEntitiesProcessorNames() {
    return this.hldAddEntitiesProcessorNames;
  }

  /**
   * <p>Setter for hldAddEntitiesProcessorNames.</p>
   * @param pHldAddEntitiesProcessorNames reference
   **/
  public final void setHldAddEntitiesProcessorNames(
    final IHldAddEntitiesProcessorNames pHldAddEntitiesProcessorNames) {
    this.hldAddEntitiesProcessorNames = pHldAddEntitiesProcessorNames;
  }
}

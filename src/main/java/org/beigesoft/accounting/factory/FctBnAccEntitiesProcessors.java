package org.beigesoft.accounting.factory;

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
import java.util.HashMap;

import org.beigesoft.log.ILog;
import org.beigesoft.model.IHasId;
import org.beigesoft.persistable.IPersistableBase;
import org.beigesoft.factory.FactoryPersistableBase;
import org.beigesoft.factory.IFactoryAppBeans;
import org.beigesoft.factory.IFactoryAppBeansByName;
import org.beigesoft.holder.IHolderForClassByName;
import org.beigesoft.converter.IConverterToFromString;
import org.beigesoft.exception.ExceptionWithCode;
import org.beigesoft.service.IEntityProcessor;
import org.beigesoft.service.ICsvReader;
import org.beigesoft.service.ISrvI18n;
import org.beigesoft.service.ISrvDate;
import org.beigesoft.service.ISrvNumberToString;
import org.beigesoft.settings.IMngSettings;
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.service.ISrvDatabase;
import org.beigesoft.orm.processor.PrcEntityPbDelete;
import org.beigesoft.orm.processor.PrcEntityFfolDelete;
import org.beigesoft.orm.processor.PrcEntityFfolSave;
import org.beigesoft.orm.processor.PrcEntityFDelete;
import org.beigesoft.orm.processor.PrcEntityFSave;
import org.beigesoft.orm.processor.PrcEntityFolSave;
import org.beigesoft.orm.processor.PrcEntityPbCopy;
import org.beigesoft.orm.processor.PrcEntityCopy;
import org.beigesoft.orm.processor.PrcEmailMsgSave;
import org.beigesoft.orm.processor.PrcEntityRetrieve;
import org.beigesoft.orm.processor.PrcEntityPbEditDelete;
import org.beigesoft.orm.processor.PrcEntityCreate;
import org.beigesoft.orm.factory.FctBnEntitiesProcessors;
import org.beigesoft.accounting.processor.PrcAccEntityPbWithSubaccCopy;
import org.beigesoft.accounting.processor.PrcAccEntityWithSubaccCopy;
import org.beigesoft.accounting.processor.PrcAccEntityWithSubaccRetrieve;
import org.beigesoft.accounting.processor.PrcAccEntityPbWithSubaccEditDelete;
import org.beigesoft.accounting.processor.PrcSubaccountLineCreate;
import org.beigesoft.accounting.processor.PrcAccEntityWithSubaccCreate;
import org.beigesoft.accounting.processor.PrcAccDocRetrieve;
import org.beigesoft.accounting.processor.PrcAccDocWithSubaccRetrieve;
import org.beigesoft.accounting.processor.PrcAccDocCogsRetrieve;
import org.beigesoft.accounting.processor.PrcAccDocUseMaterialRetrieve;
import org.beigesoft.accounting.processor.PrcAccDocFullRetrieve;
import org.beigesoft.accounting.processor.PrcAccDocGetForReverse;
import org.beigesoft.accounting.processor.PrcAccDocWithTaxesGetForReverse;
import org.beigesoft.accounting.processor.PrcPrepaymentFromSave;
import org.beigesoft.accounting.processor.PrcPrepaymentFromGfr;
import org.beigesoft.accounting.processor.PrcPrepaymentFromCopy;
import org.beigesoft.accounting.processor.PrcPrepaymentToSave;
import org.beigesoft.accounting.processor.PrcPrepaymentToGfr;
import org.beigesoft.accounting.processor.PrcPrepaymentToCopy;
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
import org.beigesoft.accounting.processor.PrcPurchaseInvoiceServiceLineSave;
import org.beigesoft.accounting.processor.PrcPurchaseInvoiceServiceLineDelete;
import org.beigesoft.accounting.processor.PrcPurchaseInvoiceLineSave;
import org.beigesoft.accounting.processor.PrcInvTaxLnSave;
import org.beigesoft.accounting.processor.PrcPurRetTaxLnSave;
import org.beigesoft.accounting.processor.PrcPurchaseInvoiceLineCopy;
import org.beigesoft.accounting.processor.PrcPurchaseInvoiceLineGfr;
import org.beigesoft.accounting.processor.PrcGoodsLossSave;
import org.beigesoft.accounting.processor.PrcGoodsLossLineSave;
import org.beigesoft.accounting.processor.PrcGoodsLossLineCopy;
import org.beigesoft.accounting.processor.PrcGoodsLossLineGfr;
import org.beigesoft.accounting.processor.PrcInvoiceLnCreate;
import org.beigesoft.accounting.processor.PrcInvoiceGfe;
import org.beigesoft.accounting.processor.PrcPurchRetGfe;
import org.beigesoft.accounting.processor.PrcSalesRetGfe;
import org.beigesoft.accounting.processor.PrcInvoiceLnGfe;
import org.beigesoft.accounting.processor.PrcSalesInvoiceSave;
import org.beigesoft.accounting.processor.PrcSalesInvoiceLineSave;
import org.beigesoft.accounting.processor.PrcSalesInvoiceLineCopy;
import org.beigesoft.accounting.processor.PrcSalesInvoiceLineGfr;
import org.beigesoft.accounting.processor.PrcSalesInvoiceServiceLineSave;
import org.beigesoft.accounting.processor.PrcSalesInvoiceServiceLineDelete;
import org.beigesoft.accounting.processor.PrcManufactureGfr;
import org.beigesoft.accounting.processor.PrcManufactureCopy;
import org.beigesoft.accounting.processor.PrcManufactureSave;
import org.beigesoft.accounting.processor.PrcManufacturingProcessGfr;
import org.beigesoft.accounting.processor.PrcManufacturingProcessSave;
import org.beigesoft.accounting.processor.PrcAdditionCostLineDelete;
import org.beigesoft.accounting.processor.PrcAdditionCostLineSave;
import org.beigesoft.accounting.processor.PrcUsedMaterialLineSave;
import org.beigesoft.accounting.processor.PrcUsedMaterialLineCopy;
import org.beigesoft.accounting.processor.PrcUsedMaterialLineGfr;
import org.beigesoft.accounting.processor.PrcPurchaseReturnSave;
import org.beigesoft.accounting.processor.PrcPurchaseReturnLineSave;
import org.beigesoft.accounting.processor.PrcPurchaseReturnLineCopy;
import org.beigesoft.accounting.processor.PrcPurchaseReturnLineGfr;
import org.beigesoft.accounting.processor.PrcPurchaseReturnLineCreate;
import org.beigesoft.accounting.processor.PrcSalRetLnCreate;
import org.beigesoft.accounting.processor.PrcBeginningInventorySave;
import org.beigesoft.accounting.processor.PrcBeginningInventoryLineSave;
import org.beigesoft.accounting.processor.PrcBeginningInventoryLineCopy;
import org.beigesoft.accounting.processor.PrcBeginningInventoryLineGfr;
import org.beigesoft.accounting.processor.PrcPurchaseInvoiceSave;
import org.beigesoft.accounting.processor.PrcSalesReturnSave;
import org.beigesoft.accounting.processor.PrcSalesReturnLineSave;
import org.beigesoft.accounting.processor.PrcSalesReturnLineCopy;
import org.beigesoft.accounting.processor.PrcSalesReturnLineGfr;
import org.beigesoft.accounting.processor.PrcAccDocWithTaxesCopy;
import org.beigesoft.accounting.processor.PrcAccDocWithTaxesPaymentsCopy;
import org.beigesoft.replicator.processor.PrcReplExcludeAccountsDebitCreditSave;
import org.beigesoft.replicator.processor.PrcReplicationAccMethodSave;
import org.beigesoft.accounting.processor.PrcInvItemTaxCategoryLineDelete;
import org.beigesoft.accounting.processor.PrcInvItemTaxCategoryLineSave;
import org.beigesoft.accounting.processor.PrcInpAccEntriesRetrieve;
import org.beigesoft.accounting.processor.PrcAccEntrySaveDescr;
import org.beigesoft.accounting.processor.PrcAccEntrySave;
import org.beigesoft.accounting.processor.PrcAccEntryCreate;
import org.beigesoft.accounting.processor.PrcAccEntryCopy;
import org.beigesoft.accounting.processor.PrcMoveItemsLineSave;
import org.beigesoft.accounting.processor.PrcMoveItemsRetrieve;
import org.beigesoft.accounting.processor.PrcAccSettingsSave;
import org.beigesoft.accounting.processor.PrcBankStatementSave;
import org.beigesoft.accounting.processor.PrcBankStatementLineGfe;
import org.beigesoft.accounting.processor.PrcBankStatementLineSave;
import org.beigesoft.accounting.processor.PrcAccSettingsLineSave;
import org.beigesoft.accounting.processor.UtlInvBase;
import org.beigesoft.accounting.processor.UtlInvLine;
import org.beigesoft.accounting.processor.InvTxMeth;
import org.beigesoft.replicator.persistable.
  base.AReplExcludeAccountsDebitCredit;
import org.beigesoft.accounting.persistable.base.ADocWithTaxes;
import org.beigesoft.accounting.persistable.base.ADocWithTaxesPayments;
import org.beigesoft.accounting.persistable.IInvoice;
import org.beigesoft.accounting.persistable.IDocWarehouse;
import org.beigesoft.accounting.persistable.IDoc;
import org.beigesoft.accounting.persistable.MoveItems;
import org.beigesoft.accounting.persistable.CogsEntry;
import org.beigesoft.accounting.persistable.UseMaterialEntry;
import org.beigesoft.accounting.persistable.SubaccountLine;
import org.beigesoft.accounting.persistable.PaymentFrom;
import org.beigesoft.accounting.persistable.Wage;
import org.beigesoft.accounting.persistable.PaymentTo;
import org.beigesoft.accounting.persistable.BankStatementLine;
import org.beigesoft.accounting.persistable.PrepaymentFrom;
import org.beigesoft.accounting.persistable.Manufacture;
import org.beigesoft.accounting.persistable.ManufacturingProcess;
import org.beigesoft.accounting.persistable.UsedMaterialLine;
import org.beigesoft.accounting.persistable.PrepaymentTo;
import org.beigesoft.accounting.persistable.PurchaseReturn;
import org.beigesoft.accounting.persistable.PurchaseReturnLine;
import org.beigesoft.accounting.persistable.PurchaseReturnTaxLine;
import org.beigesoft.accounting.persistable.PurchaseReturnGoodsTaxLine;
import org.beigesoft.accounting.persistable.SalesReturn;
import org.beigesoft.accounting.persistable.SalesReturnLine;
import org.beigesoft.accounting.persistable.SalesReturnTaxLine;
import org.beigesoft.accounting.persistable.SalesReturnGoodsTaxLine;
import org.beigesoft.accounting.persistable.GoodsLossLine;
import org.beigesoft.accounting.persistable.SalesInvoice;
import org.beigesoft.accounting.persistable.SalesInvoiceLine;
import org.beigesoft.accounting.persistable.SalesInvoiceGoodsTaxLine;
import org.beigesoft.accounting.persistable.SalesInvoiceServiceLine;
import org.beigesoft.accounting.persistable.SalesInvoiceTaxLine;
import org.beigesoft.accounting.persistable.SalesInvoiceServiceTaxLine;
import org.beigesoft.accounting.persistable.BeginningInventoryLine;
import org.beigesoft.accounting.persistable.PurchaseInvoiceLine;
import org.beigesoft.accounting.persistable.PurchaseInvoiceServiceLine;
import org.beigesoft.accounting.persistable.IInvoiceLine;
import org.beigesoft.accounting.persistable.PurchaseInvoice;
import org.beigesoft.accounting.persistable.PurchaseInvoiceTaxLine;
import org.beigesoft.accounting.persistable.PurchaseInvoiceServiceTaxLine;
import org.beigesoft.accounting.persistable.PurchaseInvoiceGoodsTaxLine;
import org.beigesoft.accounting.persistable.DestTaxGoodsLn;
import org.beigesoft.accounting.persistable.DestTaxServPurchLn;
import org.beigesoft.accounting.persistable.DestTaxServSelLn;
import org.beigesoft.accounting.service.ISrvTypeCode;
import org.beigesoft.accounting.service.ISrvAccEntry;
import org.beigesoft.accounting.service.ISrvWarehouseEntry;
import org.beigesoft.accounting.service.ISrvDrawItemEntry;
import org.beigesoft.accounting.service.ISrvAccSettings;
import org.beigesoft.accounting.service.ISrvBalance;

/**
 * <p>ORM entities processors factory. This is inner factory inside
 * IFactoryBldServices, that is also inner one inside main application factory.
 * It's invoked by entity processor, so it's synchronized.
 * Reference of this factory is in "application beans map" in
 * main factory, so reinitializing (e.g. cause changing database) will clear
 * that map, so this factory and all initialized by it beans will be destroyed
 * by garbage collector.</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class FctBnAccEntitiesProcessors<RS>
  implements IFactoryAppBeansByName<IEntityProcessor> {

  /**
   * <p>Logger.</p>
   **/
  private ILog logger;

  /**
   * <p>App beans factort.</p>
   **/
  private IFactoryAppBeans factoryAppBeans;

  /**
   * <p>Factory non-acc entity processors.
   * Concrete factory for concrete bean name that is bean class
   * simple name. Any way any such factory must be no abstract.</p>
   **/
  private FctBnEntitiesProcessors<RS> fctBnEntitiesProcessors;

  /**
   * <p>Business service for accounting settings.</p>
   **/
  private ISrvAccSettings srvAccSettings;

  /**
   * <p>Type Codes of sub-accounts service.</p>
   **/
  private ISrvTypeCode srvTypeCode;

  /**
   * <p>ORM service.</p>
   **/
  private ISrvOrm<RS> srvOrm;

  /**
   * <p>Database service.</p>
   **/
  private ISrvDatabase<RS> srvDatabase;

  /**
   * <p>I18N service.</p>
   **/
  private ISrvI18n srvI18n;

  /**
   * <p>Business service for accounting entries.</p>
   **/
  private ISrvAccEntry srvAccEntry;

  /**
   * <p>Business service for warehouse.</p>
   **/
  private ISrvWarehouseEntry srvWarehouseEntry;

  /**
   * <p>Business service for draw any item to sale/loss/stole.</p>
   **/
  private ISrvDrawItemEntry<CogsEntry> srvCogsEntry;

  /**
   * <p>Business service for draw material.</p>
   **/
  private ISrvDrawItemEntry<UseMaterialEntry> srvUseMaterialEntry;

  /**
   * <p>Date service.</p>
   **/
  private ISrvDate srvDate;

  /**
   * <p>Manager UVD settings.</p>
   **/
  private IMngSettings mngUvdSettings;

  /**
   * <p>Fields converters factory.</p>
   **/
  private IFactoryAppBeansByName<IConverterToFromString<?>>
    convertersFieldsFatory;

  /**
   * <p>Field converter names holder.</p>
   **/
  private IHolderForClassByName<String> fieldConverterNamesHolder;

  /**
   * <p>Balance service.</p>
   **/
  private ISrvBalance srvBalance;

  /**
   * <p>Service print number.</p>
   **/
  private ISrvNumberToString srvNumberToString;

  /**
   * <p>CSV reader.</p>
   **/
  private ICsvReader csvReader;

  /**
   * <p>Shared invoice code-bunch.</p>
   **/
  private UtlInvBase<RS> utlInvBase;

  /**
   * <p>Sales return tax method data/code.</p>
   **/
  private InvTxMeth<SalesReturn, SalesReturnTaxLine> salRetTxMeth;

  /**
   * <p>Sales return good line utility.</p>
   **/
  private UtlInvLine<RS, SalesReturn, SalesReturnLine,
    SalesReturnTaxLine, SalesReturnGoodsTaxLine> utlSalRetGdLn;

  /**
   * <p>Purchase return tax method data/code.</p>
   **/
  private InvTxMeth<PurchaseReturn, PurchaseReturnTaxLine> purRetTxMeth;

  /**
   * <p>Purchase return good line utility.</p>
   **/
  private UtlInvLine<RS, PurchaseReturn, PurchaseReturnLine,
    PurchaseReturnTaxLine, PurchaseReturnGoodsTaxLine> utlPurRetGdLn;

  /**
   * <p>Purchase invoice tax method data/code.</p>
   **/
  private InvTxMeth<PurchaseInvoice, PurchaseInvoiceTaxLine> purInvTxMeth;

  /**
   * <p>Purchase invoice good line utility.</p>
   **/
  private UtlInvLine<RS, PurchaseInvoice, PurchaseInvoiceLine,
    PurchaseInvoiceTaxLine, PurchaseInvoiceGoodsTaxLine> utlPurInvGdLn;

  /**
   * <p>Purchase invoice service line utility.</p>
   **/
  private UtlInvLine<RS, PurchaseInvoice, PurchaseInvoiceServiceLine,
    PurchaseInvoiceTaxLine, PurchaseInvoiceServiceTaxLine> utlPurInvSrLn;

  /**
   * <p>Sales invoice tax method data/code.</p>
   **/
  private InvTxMeth<SalesInvoice, SalesInvoiceTaxLine> salInvTxMeth;

  /**
   * <p>Sales invoice good line utility.</p>
   **/
  private UtlInvLine<RS, SalesInvoice, SalesInvoiceLine,
    SalesInvoiceTaxLine, SalesInvoiceGoodsTaxLine> utlSalInvGdLn;

  /**
   * <p>Sales invoice service line utility.</p>
   **/
  private UtlInvLine<RS, SalesInvoice, SalesInvoiceServiceLine,
    SalesInvoiceTaxLine, SalesInvoiceServiceTaxLine> utlSalInvSrLn;

  /**
   * <p>Converters map "converter name"-"object' s converter".</p>
   **/
  private final Map<String, IEntityProcessor>
    processorsMap = new HashMap<String, IEntityProcessor>();

  /**
   * <p>Get bean in lazy mode (if bean is null then initialize it).</p>
   * @param pAddParam additional param
   * @param pBeanName - bean name
   * @return requested bean
   * @throws Exception - an exception
   */
  @Override
  public final IEntityProcessor lazyGet(
    final Map<String, Object> pAddParam,
      final String pBeanName) throws Exception {
    IEntityProcessor proc =
      this.processorsMap.get(pBeanName);
    if (proc == null) {
      synchronized (this) {
        proc = this.processorsMap.get(pBeanName);
        if (proc == null) {
          if (pBeanName.equals(PrcAccEntryCopy.class.getSimpleName())) {
            proc = lazyGetPrcAccEntryCopy(pAddParam);
          } else if (pBeanName
            .equals(PrcAccEntryCreate.class.getSimpleName())) {
            proc = lazyGetPrcAccEntryCreate(pAddParam);
          } else if (pBeanName
            .equals(PrcAccEntrySave.class.getSimpleName())) {
            proc = lazyGetPrcAccEntrySave(pAddParam);
          } else if (pBeanName
            .equals(PrcInpAccEntriesRetrieve.class.getSimpleName())) {
            proc = lazyGetPrcInpAccEntriesRetrieve(pAddParam);
          } else if (pBeanName
            .equals(PrcAccEntityWithSubaccRetrieve.class.getSimpleName())) {
            proc = lazyGetPrcAccEntityWithSubaccRetrieve(pAddParam);
          } else if (pBeanName
            .equals(PrcAccEntrySaveDescr.class.getSimpleName())) {
            proc = createPutPrcAccEntrySaveDescr(pAddParam);
          } else if (pBeanName
            .equals(PrcReplicationAccMethodSave.class.getSimpleName())) {
            proc = createPutPrcReplicationAccMethodSave(pAddParam);
          } else if (pBeanName
            .equals(PrcReplExcludeAccountsDebitCreditSave
              .class.getSimpleName())) {
            proc = createPutPrcReplExcludeAccountsDebitCreditSave(pAddParam);
          } else if (pBeanName
            .equals(PrcSubaccountLineCreate.class.getSimpleName())) {
            proc = createPutPrcSubaccountLineCreate(pAddParam);
          } else if (pBeanName
            .equals(PrcAccEntityWithSubaccCreate.class.getSimpleName())) {
            proc = createPutPrcAccEntityWithSubaccCreate(pAddParam);
          } else if (pBeanName.equals(PrcSalesInvoiceServiceLineDelete
            .class.getSimpleName())) {
            proc = createPutPrcSalesInvoiceServiceLineDelete(pAddParam);
          } else if (pBeanName.equals(PrcPurchaseInvoiceServiceLineDelete
            .class.getSimpleName())) {
            proc = createPutPrcPurchaseInvoiceServiceLineDelete(pAddParam);
          } else if (pBeanName
            .equals(PrcEntityFDelete.class.getSimpleName())) {
            proc = createPutPrcEntityFDelete(pAddParam);
          } else if (pBeanName
            .equals(PrcEntityFSave.class.getSimpleName())) {
            proc = createPutPrcEntityFSave(pAddParam);
          } else if (pBeanName
            .equals(PrcEntityFfolDelete.class.getSimpleName())) {
            proc = createPutPrcEntityFfolDelete(pAddParam);
          } else if (pBeanName
            .equals(PrcEntityFfolSave.class.getSimpleName())) {
            proc = createPutPrcEntityFfolSave(pAddParam);
          } else if (pBeanName
            .equals(PrcAccSettingsLineSave.class.getSimpleName())) {
            proc = lazyGetPrcAccSettingsLineSave(pAddParam);
          } else if (pBeanName
            .equals(PrcInvoiceLnCreate.class.getSimpleName())) {
            proc = lazyGetPrcInvoiceLnCreate(pAddParam);
          } else if (pBeanName
            .equals(PrcSalRetLnCreate.class.getSimpleName())) {
            proc = lazyGetPrcSalRetLnCreate(pAddParam);
          } else if (pBeanName
            .equals(PrcPurchaseReturnLineCreate.class.getSimpleName())) {
            proc = lazyGetPrcPurchaseReturnLineCreate(pAddParam);
          } else if (pBeanName
            .equals(PrcPurchaseReturnLineGfr.class.getSimpleName())) {
            proc = lazyGetPrcPurchaseReturnLineGfr(pAddParam);
          } else if (pBeanName
            .equals(PrcPurchaseReturnLineCopy.class.getSimpleName())) {
            proc = lazyGetPrcPurchaseReturnLineCopy(pAddParam);
          } else if (pBeanName
            .equals(PrcSalesReturnLineGfr.class.getSimpleName())) {
            proc = lazyGetPrcSalesReturnLineGfr(pAddParam);
          } else if (pBeanName
            .equals(PrcSalesReturnLineCopy.class.getSimpleName())) {
            proc = lazyGetPrcSalesReturnLineCopy(pAddParam);
          } else if (pBeanName
            .equals(PrcUsedMaterialLineGfr.class.getSimpleName())) {
            proc = lazyGetPrcUsedMaterialLineGfr(pAddParam);
          } else if (pBeanName
            .equals(PrcUsedMaterialLineCopy.class.getSimpleName())) {
            proc = lazyGetPrcUsedMaterialLineCopy(pAddParam);
          } else if (pBeanName
            .equals(PrcMoveItemsLineSave.class.getSimpleName())) {
            proc = lazyGetPrcMoveItemsLineSave(pAddParam);
          } else if (pBeanName
            .equals(PrcUsedMaterialLineSave.class.getSimpleName())) {
            proc = lazyGetPrcUsedMaterialLineSave(pAddParam);
          } else if (pBeanName
            .equals(PrcGoodsLossSave.class.getSimpleName())) {
            proc = lazyGetPrcGoodsLossSave(pAddParam);
          } else if (pBeanName
            .equals(PrcGoodsLossLineGfr.class.getSimpleName())) {
            proc = lazyGetPrcGoodsLossLineGfr(pAddParam);
          } else if (pBeanName
            .equals(PrcGoodsLossLineCopy.class.getSimpleName())) {
            proc = lazyGetPrcGoodsLossLineCopy(pAddParam);
          } else if (pBeanName
            .equals(PrcGoodsLossLineSave.class.getSimpleName())) {
            proc = lazyGetPrcGoodsLossLineSave(pAddParam);
          } else if (pBeanName
            .equals(PrcSalesInvoiceLineGfr.class.getSimpleName())) {
            proc = lazyGetPrcSalesInvoiceLineGfr(pAddParam);
          } else if (pBeanName
            .equals(PrcSalesInvoiceLineCopy.class.getSimpleName())) {
            proc = lazyGetPrcSalesInvoiceLineCopy(pAddParam);
          } else if (pBeanName
            .equals(PrcSalesInvoiceLineSave.class.getSimpleName())) {
            proc = lazyGetPrcSalesInvoiceLineSave(pAddParam);
          } else if (pBeanName
            .equals(PrcBeginningInventoryLineGfr.class.getSimpleName())) {
            proc = lazyGetPrcBeginningInventoryLineGfr(pAddParam);
          } else if (pBeanName
            .equals(PrcBeginningInventoryLineCopy.class.getSimpleName())) {
            proc = lazyGetPrcBeginningInventoryLineCopy(pAddParam);
          } else if (pBeanName
            .equals(PrcBeginningInventoryLineSave.class.getSimpleName())) {
            proc = lazyGetPrcBeginningInventoryLineSave(pAddParam);
          } else if (pBeanName
            .equals(PrcPurchaseInvoiceLineGfr.class.getSimpleName())) {
            proc = lazyGetPrcPurchaseInvoiceLineGfr(pAddParam);
          } else if (pBeanName
            .equals(PrcPurchaseInvoiceLineCopy.class.getSimpleName())) {
            proc = lazyGetPrcPurchaseInvoiceLineCopy(pAddParam);
          } else if (pBeanName
            .equals(PrcInvTaxLnSave.class.getSimpleName() + "SRTL")) {
            proc = lazyGetPrcSalRetTaxLnSave(pAddParam);
          } else if (pBeanName
            .equals(PrcInvTaxLnSave.class.getSimpleName() + "SITL")) {
            proc = lazyGetPrcSalInvTaxLnSave(pAddParam);
          } else if (pBeanName
            .equals(PrcPurRetTaxLnSave.class.getSimpleName())) {
            proc = lazyGetPrcPurchRetTaxLnSave(pAddParam);
          } else if (pBeanName
            .equals(PrcInvTaxLnSave.class.getSimpleName() + "PITL")) {
            proc = lazyGetPrcPurchInvTaxLnSave(pAddParam);
          } else if (pBeanName
            .equals(PrcPurchaseInvoiceLineSave.class.getSimpleName())) {
            proc = lazyGetPrcPurchaseInvoiceLineSave(pAddParam);
          } else if (pBeanName
            .equals(PrcAccEntityPbWithSubaccCopy.class.getSimpleName())) {
            proc = lazyGetPrcAccEntityPbWithSubaccCopy(pAddParam);
          } else if (pBeanName
            .equals(PrcAccDocWithTaxesCopy.class.getSimpleName())) {
            proc = lazyGetPrcAccDocWithTaxesCopy(pAddParam);
          } else if (pBeanName
            .equals(PrcAccDocWithTaxesPaymentsCopy.class.getSimpleName())) {
            proc = lazyGetPrcAccDocWithTaxesPaymentsCopy(pAddParam);
          } else if (pBeanName
            .equals(PrcAccDocWithTaxesGetForReverse.class.getSimpleName())) {
            proc = lazyGetPrcAccDocWithTaxesGetForReverse(pAddParam);
          } else if (pBeanName
            .equals(PrcAccDocGetForReverse.class.getSimpleName())) {
            proc = lazyGetPrcAccDocGetForReverse(pAddParam);
          } else if (pBeanName
            .equals(PrcAccEntityWithSubaccCopy.class.getSimpleName())) {
            proc = createPutPrcAccEntityWithSubaccCopy(pAddParam);
          } else if (pBeanName
            .equals(PrcAccEntityPbWithSubaccEditDelete.class.getSimpleName())) {
            proc = createPutPrcAccEntityPbWithSubaccEditDelete(pAddParam);
          } else if (pBeanName
            .equals(PrcEmailMsgSave.class.getSimpleName())) {
            proc = createPutPrcEmailMsgSave(pAddParam);
          } else if (pBeanName
            .equals(PrcSalesInvoiceServiceLineSave.class.getSimpleName())) {
            proc = lazyGetPrcSalesInvoiceServiceLineSave(pAddParam);
          } else if (pBeanName
            .equals(PrcPurchaseInvoiceServiceLineSave.class.getSimpleName())) {
            proc = lazyGetPrcPurchaseInvoiceServiceLineSave(pAddParam);
          } else if (pBeanName
            .equals(PrcPurchaseReturnLineSave.class.getSimpleName())) {
            proc = lazyGetPrcPurchaseReturnLineSave(pAddParam);
          } else if (pBeanName
            .equals(PrcWageTaxLineDelete.class.getSimpleName())) {
            proc = lazyGetPrcWageTaxLineDelete(pAddParam);
          } else if (pBeanName
            .equals(PrcWageTaxLineSave.class.getSimpleName())) {
            proc = lazyGetPrcWageTaxLineSave(pAddParam);
          } else if (pBeanName
            .equals(PrcWageLineDelete.class.getSimpleName())) {
            proc = lazyGetPrcWageLineDelete(pAddParam);
          } else if (pBeanName
            .equals(PrcWageLineSave.class.getSimpleName())) {
            proc = lazyGetPrcWageLineSave(pAddParam);
          } else if (pBeanName
            .equals(PrcSalesReturnLineSave.class.getSimpleName())) {
            proc = lazyGetPrcSalesReturnLineSave(pAddParam);
          } else if (pBeanName
            .equals(PrcSalesReturnSave.class.getSimpleName())) {
            proc = lazyGetPrcSalesReturnSave(pAddParam);
          } else if (pBeanName
            .equals(PrcAccSettingsSave.class.getSimpleName())) {
            proc = lazyGetPrcAccSettingsSave(pAddParam);
          } else if (pBeanName
            .equals(PrcSalesInvoiceSave.class.getSimpleName())) {
            proc = lazyGetPrcSalesInvoiceSave(pAddParam);
          } else if (pBeanName
            .equals(PrcManufactureSave.class.getSimpleName())) {
            proc = lazyGetPrcManufactureSave(pAddParam);
          } else if (pBeanName
            .equals(PrcManufactureGfr.class.getSimpleName())) {
            proc = createPutPrcManufactureGfr(pAddParam);
          } else if (pBeanName
            .equals(PrcManufactureCopy.class.getSimpleName())) {
            proc = createPutPrcManufactureCopy(pAddParam);
          } else if (pBeanName
            .equals(PrcManufacturingProcessSave.class.getSimpleName())) {
            proc = lazyGetPrcManufacturingProcessSave(pAddParam);
          } else if (pBeanName
            .equals(PrcManufacturingProcessGfr.class.getSimpleName())) {
            proc = createPutPrcManufacturingProcessGfr(pAddParam);
          } else if (pBeanName
            .equals(PrcInvItemTaxCategoryLineDelete.class.getSimpleName())) {
            proc = lazyGetPrcInvItemTaxCategoryLineDelete(pAddParam);
          } else if (pBeanName
            .equals(PrcInvItemTaxCategoryLineSave.class.getSimpleName())) {
            proc = lazyGetPrcInvItemTaxCategoryLineSave(pAddParam);
          } else if (pBeanName
            .equals(PrcAdditionCostLineDelete.class.getSimpleName())) {
            proc = lazyGetPrcAdditionCostLineDelete(pAddParam);
          } else if (pBeanName
            .equals(PrcBankStatementLineSave.class.getSimpleName())) {
            proc = lazyGetPrcBankStatementLineSave(pAddParam);
          } else if (pBeanName
            .equals(PrcSalesRetGfe.class.getSimpleName())) {
            proc = lazyGetPrcSalesRetGfe(pAddParam);
          } else if (pBeanName
            .equals(PrcPurchRetGfe.class.getSimpleName())) {
            proc = lazyGetPrcPurchRetGfe(pAddParam);
          } else if (pBeanName
            .equals(PrcInvoiceGfe.class.getSimpleName())) {
            proc = lazyGetPrcInvoiceGfe(pAddParam);
          } else if (pBeanName
            .equals(PrcInvoiceLnGfe.class.getSimpleName())) {
            proc = lazyGetPrcInvoiceLnGfe(pAddParam);
          } else if (pBeanName
            .equals(PrcBankStatementLineGfe.class.getSimpleName())) {
            proc = lazyGetPrcBankStatementLineGfe(pAddParam);
          } else if (pBeanName
            .equals(PrcBankStatementSave.class.getSimpleName())) {
            proc = lazyGetPrcBankStatementSave(pAddParam);
          } else if (pBeanName
            .equals(PrcAdditionCostLineSave.class.getSimpleName())) {
            proc = lazyGetPrcAdditionCostLineSave(pAddParam);
          } else if (pBeanName
            .equals(PrcPurchaseReturnSave.class.getSimpleName())) {
            proc = lazyGetPrcPurchaseReturnSave(pAddParam);
          } else if (pBeanName
            .equals(PrcBeginningInventorySave.class.getSimpleName())) {
            proc = lazyGetPrcBeginningInventorySave(pAddParam);
          } else if (pBeanName
            .equals(PrcPurchaseInvoiceSave.class.getSimpleName())) {
            proc = lazyGetPrcPurchaseInvoiceSave(pAddParam);
          } else if (pBeanName
            .equals(PrcAccDocFullRetrieve.class.getSimpleName())) {
            proc = createPutPrcAccDocFullRetrieve(pAddParam);
          } else if (pBeanName
            .equals(PrcMoveItemsRetrieve.class.getSimpleName())) {
            proc = lazyGetPrcMoveItemsRetrieve(pAddParam);
          } else if (pBeanName
            .equals(PrcAccDocUseMaterialRetrieve.class.getSimpleName())) {
            proc = lazyGetPrcAccDocUseMaterialRetrieve(pAddParam);
          } else if (pBeanName
            .equals(PrcAccDocCogsRetrieve.class.getSimpleName())) {
            proc = lazyGetPrcAccDocCogsRetrieve(pAddParam);
          } else if (pBeanName
            .equals(PrcAccDocWithSubaccRetrieve.class.getSimpleName())) {
            proc = lazyGetPrcAccDocWithSubaccRetrieve(pAddParam);
          } else if (pBeanName
            .equals(PrcAccDocRetrieve.class.getSimpleName())) {
            proc = lazyGetPrcAccDocRetrieve(pAddParam);
          } else if (pBeanName
            .equals(PrcPaymentFromSave.class.getSimpleName())) {
            proc = createPutPrcPaymentFromSave(pAddParam);
          } else if (pBeanName
            .equals(PrcPaymentFromGfr.class.getSimpleName())) {
            proc = createPutPrcPaymentFromGfr(pAddParam);
          } else if (pBeanName
            .equals(PrcPaymentFromCopy.class.getSimpleName())) {
            proc = createPutPrcPaymentFromCopy(pAddParam);
          } else if (pBeanName
            .equals(PrcWageSave.class.getSimpleName())) {
            proc = createPutPrcWageSave(pAddParam);
          } else if (pBeanName
            .equals(PrcWageGfr.class.getSimpleName())) {
            proc = createPutPrcWageGfr(pAddParam);
          } else if (pBeanName
            .equals(PrcWageCopy.class.getSimpleName())) {
            proc = createPutPrcWageCopy(pAddParam);
          } else if (pBeanName
            .equals(PrcPaymentToSave.class.getSimpleName())) {
            proc = createPutPrcPaymentToSave(pAddParam);
          } else if (pBeanName
            .equals(PrcPaymentToGfr.class.getSimpleName())) {
            proc = createPutPrcPaymentToGfr(pAddParam);
          } else if (pBeanName
            .equals(PrcPaymentToCopy.class.getSimpleName())) {
            proc = createPutPrcPaymentToCopy(pAddParam);
          } else if (pBeanName
            .equals(PrcPrepaymentFromSave.class.getSimpleName())) {
            proc = createPutPrcPrepaymentFromSave(pAddParam);
          } else if (pBeanName
            .equals(PrcPrepaymentFromGfr.class.getSimpleName())) {
            proc = createPutPrcPrepaymentFromGfr(pAddParam);
          } else if (pBeanName
            .equals(PrcPrepaymentFromCopy.class.getSimpleName())) {
            proc = createPutPrcPrepaymentFromCopy(pAddParam);
          } else if (pBeanName
            .equals(PrcPrepaymentToSave.class.getSimpleName())) {
            proc = createPutPrcPrepaymentToSave(pAddParam);
          } else if (pBeanName
            .equals(PrcPrepaymentToGfr.class.getSimpleName())) {
            proc = createPutPrcPrepaymentToGfr(pAddParam);
          } else if (pBeanName
            .equals(PrcPrepaymentToCopy.class.getSimpleName())) {
            proc = createPutPrcPrepaymentToCopy(pAddParam);
          } else {
            proc = this.fctBnEntitiesProcessors.lazyGet(pAddParam, pBeanName);
          }
        }
      }
    }
    if (proc == null) {
      throw new ExceptionWithCode(ExceptionWithCode.CONFIGURATION_MISTAKE,
        "There is no entity processor with name " + pBeanName);
    }
    return proc;
  }

  /**
   * <p>Set bean.</p>
   * @param pBeanName - bean name
   * @param pBean bean
   * @throws Exception - an exception
   */
  @Override
  public final void set(final String pBeanName,
    final IEntityProcessor pBean) throws Exception {
    throw new Exception("Setting is not allowed!");
  }

  /**
   * <p>Get PrcReplExcludeAccountsDebitCreditSave (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcReplExcludeAccountsDebitCreditSave
   * @throws Exception - an exception
   */
  protected final PrcReplExcludeAccountsDebitCreditSave<RS>
    createPutPrcReplExcludeAccountsDebitCreditSave(
      final Map<String, Object> pAddParam) throws Exception {
    PrcReplExcludeAccountsDebitCreditSave<RS> proc =
      new PrcReplExcludeAccountsDebitCreditSave<RS>();
    @SuppressWarnings("unchecked")
    IEntityProcessor<AReplExcludeAccountsDebitCredit, Long>
      prcAccEntityFolSave =
        (IEntityProcessor<AReplExcludeAccountsDebitCredit, Long>)
          this.fctBnEntitiesProcessors
            .lazyGet(pAddParam, PrcEntityFolSave.class.getSimpleName());
    proc.setPrcEntityFolSave(prcAccEntityFolSave);
    //assigning fully initialized object:
    this.processorsMap
      .put(PrcReplExcludeAccountsDebitCreditSave.class.getSimpleName(), proc);
    return proc;
  }

  /**
   * <p>Get PrcAccEntrySaveDescr (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcAccEntrySaveDescr
   * @throws Exception - an exception
   */
  protected final PrcAccEntrySaveDescr<RS> createPutPrcAccEntrySaveDescr(
    final Map<String, Object> pAddParam) throws Exception {
    PrcAccEntrySaveDescr<RS> proc = new PrcAccEntrySaveDescr<RS>();
    proc.setSrvAccSettings(getSrvAccSettings());
    proc.setSrvOrm(getSrvOrm());
    //assigning fully initialized object:
    this.processorsMap.put(PrcAccEntrySaveDescr.class.getSimpleName(), proc);
    return proc;
  }

  /**
   * <p>Get PrcReplicationAccMethodSave (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcReplicationAccMethodSave
   * @throws Exception - an exception
   */
  protected final PrcReplicationAccMethodSave<RS>
    createPutPrcReplicationAccMethodSave(
      final Map<String, Object> pAddParam) throws Exception {
    PrcReplicationAccMethodSave<RS> proc =
      new PrcReplicationAccMethodSave<RS>();
    proc.setSrvAccSettings(getSrvAccSettings());
    proc.setSrvOrm(getSrvOrm());
    //assigning fully initialized object:
    this.processorsMap
      .put(PrcReplicationAccMethodSave.class.getSimpleName(), proc);
    return proc;
  }

  /**
   * <p>Get PrcSubaccountLineCreate (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcSubaccountLineCreate
   * @throws Exception - an exception
   */
  protected final PrcSubaccountLineCreate<RS>
    createPutPrcSubaccountLineCreate(
      final Map<String, Object> pAddParam) throws Exception {
    PrcSubaccountLineCreate<RS> proc = new PrcSubaccountLineCreate<RS>();
    @SuppressWarnings("unchecked")
    IEntityProcessor<SubaccountLine, Long> procDlg =
      (IEntityProcessor<SubaccountLine, Long>) this.fctBnEntitiesProcessors
        .lazyGet(pAddParam, PrcEntityCreate.class.getSimpleName());
    proc.setPrcAccEntityCreate(procDlg);
    proc.setSrvTypeCode(getSrvTypeCode());
    proc.setSrvOrm(getSrvOrm());
    //assigning fully initialized object:
    this.processorsMap
      .put(PrcSubaccountLineCreate.class.getSimpleName(), proc);
    return proc;
  }

  /**
   * <p>Get PrcAccEntityWithSubaccCreate (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcAccEntityWithSubaccCreate
   * @throws Exception - an exception
   */
  protected final PrcAccEntityWithSubaccCreate<RS, IHasId<Object>, Object>
    createPutPrcAccEntityWithSubaccCreate(
      final Map<String, Object> pAddParam) throws Exception {
    PrcAccEntityWithSubaccCreate<RS, IHasId<Object>, Object> proc =
      new PrcAccEntityWithSubaccCreate<RS, IHasId<Object>, Object>();
    @SuppressWarnings("unchecked")
    PrcEntityCreate<RS, IHasId<Object>, Object> procDlg =
      (PrcEntityCreate<RS, IHasId<Object>, Object>)
        this.fctBnEntitiesProcessors
          .lazyGet(pAddParam, PrcEntityCreate.class.getSimpleName());
    proc.setPrcAccEntityCreate(procDlg);
    proc.setSrvTypeCode(getSrvTypeCode());
    //assigning fully initialized object:
    this.processorsMap
      .put(PrcAccEntityWithSubaccCreate.class.getSimpleName(), proc);
    return proc;
  }

  /**
   * <p>Get PrcSalesInvoiceServiceLineDelete (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcSalesInvoiceServiceLineDelete
   * @throws Exception - an exception
   */
  protected final PrcSalesInvoiceServiceLineDelete
    createPutPrcSalesInvoiceServiceLineDelete(
      final Map<String, Object> pAddParam) throws Exception {
    PrcSalesInvoiceServiceLineDelete<RS> proc =
      new PrcSalesInvoiceServiceLineDelete<RS>();
    @SuppressWarnings("unchecked")
    IEntityProcessor<SalesInvoiceServiceLine, Long> procDlg =
      (IEntityProcessor<SalesInvoiceServiceLine, Long>)
        this.fctBnEntitiesProcessors
          .lazyGet(pAddParam, PrcEntityPbDelete.class.getSimpleName());
    proc.setSrvOrm(getSrvOrm());
    proc.setPrcAccEntityPbDelete(procDlg);
    proc.setSrvAccSettings(getSrvAccSettings());
    proc.setUtlInvLine(lazyGetUtlSalSrLn(pAddParam));
    //assigning fully initialized object:
    this.processorsMap
      .put(PrcSalesInvoiceServiceLineDelete.class.getSimpleName(), proc);
    return proc;
  }

  /**
   * <p>Get PrcPurchaseInvoiceServiceLineDelete (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcPurchaseInvoiceServiceLineDelete
   * @throws Exception - an exception
   */
  protected final PrcPurchaseInvoiceServiceLineDelete
    createPutPrcPurchaseInvoiceServiceLineDelete(
      final Map<String, Object> pAddParam) throws Exception {
    PrcPurchaseInvoiceServiceLineDelete<RS> proc =
      new PrcPurchaseInvoiceServiceLineDelete<RS>();
    @SuppressWarnings("unchecked")
    IEntityProcessor<PurchaseInvoiceServiceLine, Long> procDlg =
      (IEntityProcessor<PurchaseInvoiceServiceLine, Long>)
        this.fctBnEntitiesProcessors
          .lazyGet(pAddParam, PrcEntityPbDelete.class.getSimpleName());
    proc.setSrvOrm(getSrvOrm());
    proc.setPrcAccEntityPbDelete(procDlg);
    proc.setSrvAccSettings(getSrvAccSettings());
    proc.setUtlInvLine(lazyGetUtlPurSrLn(pAddParam));
    //assigning fully initialized object:
    this.processorsMap
      .put(PrcPurchaseInvoiceServiceLineDelete.class.getSimpleName(), proc);
    return proc;
  }

  /**
   * <p>Get PrcEntityFDelete (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcEntityFDelete
   * @throws Exception - an exception
   */
  protected final PrcEntityFDelete<RS, IHasId<Object>, Object>
    createPutPrcEntityFDelete(
      final Map<String, Object> pAddParam) throws Exception {
    @SuppressWarnings("unchecked")
    PrcEntityFDelete<RS, IHasId<Object>, Object> proc =
      (PrcEntityFDelete<RS, IHasId<Object>, Object>)
        this.fctBnEntitiesProcessors
          .lazyGet(pAddParam, PrcEntityFDelete.class.getSimpleName());
    //assigning fully initialized object:
    this.processorsMap.put(PrcEntityFDelete.class.getSimpleName(), proc);
    return proc;
  }

  /**
   * <p>Get PrcEntityFSave (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcEntityFSave
   * @throws Exception - an exception
   */
  protected final PrcEntityFSave<RS, IHasId<Object>, Object>
    createPutPrcEntityFSave(
      final Map<String, Object> pAddParam) throws Exception {
    @SuppressWarnings("unchecked")
    PrcEntityFSave<RS, IHasId<Object>, Object> proc =
      (PrcEntityFSave<RS, IHasId<Object>, Object>)
        this.fctBnEntitiesProcessors
          .lazyGet(pAddParam, PrcEntityFSave.class.getSimpleName());
    //assigning fully initialized object:
    this.processorsMap.put(PrcEntityFSave.class.getSimpleName(), proc);
    return proc;
  }

  /**
   * <p>Get PrcEntityFfolDelete (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcEntityFfolDelete
   * @throws Exception - an exception
   */
  protected final PrcEntityFfolDelete<RS, IHasId<Object>, Object>
    createPutPrcEntityFfolDelete(
      final Map<String, Object> pAddParam) throws Exception {
    @SuppressWarnings("unchecked")
    PrcEntityFfolDelete<RS, IHasId<Object>, Object> proc =
      (PrcEntityFfolDelete<RS, IHasId<Object>, Object>)
        this.fctBnEntitiesProcessors
          .lazyGet(pAddParam, PrcEntityFfolDelete.class.getSimpleName());
    //assigning fully initialized object:
    this.processorsMap.put(PrcEntityFfolDelete.class.getSimpleName(), proc);
    return proc;
  }

  /**
   * <p>Get PrcEntityFfolSave (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcEntityFfolSave
   * @throws Exception - an exception
   */
  protected final PrcEntityFfolSave<RS, IHasId<Object>, Object>
    createPutPrcEntityFfolSave(
      final Map<String, Object> pAddParam) throws Exception {
    @SuppressWarnings("unchecked")
    PrcEntityFfolSave<RS, IHasId<Object>, Object> proc =
      (PrcEntityFfolSave<RS, IHasId<Object>, Object>)
        this.fctBnEntitiesProcessors
          .lazyGet(pAddParam, PrcEntityFfolSave.class.getSimpleName());
    //assigning fully initialized object:
    this.processorsMap.put(PrcEntityFfolSave.class.getSimpleName(), proc);
    return proc;
  }

  /**
   * <p>Lazy get PrcAccSettingsLineSave.</p>
   * @param pAddParam additional param
   * @return requested PrcAccSettingsLineSave
   * @throws Exception - an exception
   */
  protected final PrcAccSettingsLineSave lazyGetPrcAccSettingsLineSave(
    final Map<String, Object> pAddParam) throws Exception {
    @SuppressWarnings("unchecked")
    PrcAccSettingsLineSave<RS, IHasId<Long>> proc =
      (PrcAccSettingsLineSave<RS, IHasId<Long>>) this.processorsMap
        .get(PrcAccSettingsLineSave.class.getSimpleName());
    if (proc == null) {
      proc = new PrcAccSettingsLineSave<RS, IHasId<Long>>();
      proc.setSrvAccSettings(getSrvAccSettings());
      @SuppressWarnings("unchecked")
      PrcEntityFolSave<RS, IHasId<Long>, Long> procDlg =
        (PrcEntityFolSave<RS, IHasId<Long>, Long>) this.fctBnEntitiesProcessors
          .lazyGet(pAddParam, PrcEntityFolSave.class.getSimpleName());
      proc.setPrcEntityFolSave(procDlg);
      //assigning fully initialized object:
      this.processorsMap
        .put(PrcAccSettingsLineSave.class.getSimpleName(), proc);
    }
    return proc;
  }

  /**
   * <p>Get PrcInvoiceLnCreate (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcInvoiceLnCreate
   * @throws Exception - an exception
   */
  protected final PrcInvoiceLnCreate<RS, IInvoiceLine<IInvoice>, IInvoice>
    lazyGetPrcInvoiceLnCreate(
      final Map<String, Object> pAddParam) throws Exception {
    @SuppressWarnings("unchecked")
    PrcInvoiceLnCreate<RS, IInvoiceLine<IInvoice>, IInvoice> proc =
      (PrcInvoiceLnCreate<RS, IInvoiceLine<IInvoice>, IInvoice>)
        this.processorsMap.get(PrcInvoiceLnCreate.class.getSimpleName());
    if (proc == null) {
      proc = new PrcInvoiceLnCreate<RS, IInvoiceLine<IInvoice>, IInvoice>();
      @SuppressWarnings("unchecked")
      PrcEntityCreate<RS, IInvoiceLine<IInvoice>, Long> procDlg =
        (PrcEntityCreate<RS, IInvoiceLine<IInvoice>, Long>)
          this.fctBnEntitiesProcessors
            .lazyGet(pAddParam, PrcEntityCreate.class.getSimpleName());
      proc.setPrcEntityCreate(procDlg);
      //assigning fully initialized object:
      this.processorsMap
        .put(PrcInvoiceLnCreate.class.getSimpleName(), proc);
    }
    return proc;
  }

  /**
   * <p>Get PrcSalRetLnCreate (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcSalRetLnCreate
   * @throws Exception - an exception
   */
  protected final PrcSalRetLnCreate<RS>
    lazyGetPrcSalRetLnCreate(
      final Map<String, Object> pAddParam) throws Exception {
    @SuppressWarnings("unchecked")
    PrcSalRetLnCreate<RS> proc = (PrcSalRetLnCreate<RS>)
      this.processorsMap.get(PrcSalRetLnCreate.class.getSimpleName());
    if (proc == null) {
      proc = new PrcSalRetLnCreate<RS>();
      @SuppressWarnings("unchecked")
      PrcEntityCreate<RS, SalesReturnLine, Long> procDlg =
        (PrcEntityCreate<RS, SalesReturnLine, Long>)
          this.fctBnEntitiesProcessors
            .lazyGet(pAddParam, PrcEntityCreate.class.getSimpleName());
      proc.setPrcEntityCreate(procDlg);
      //assigning fully initialized object:
      this.processorsMap.put(PrcSalRetLnCreate.class.getSimpleName(), proc);
    }
    return proc;
  }

  /**
   * <p>Get PrcPurchaseReturnLineCreate (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcPurchaseReturnLineCreate
   * @throws Exception - an exception
   */
  protected final PrcPurchaseReturnLineCreate<RS>
    lazyGetPrcPurchaseReturnLineCreate(
      final Map<String, Object> pAddParam) throws Exception {
    @SuppressWarnings("unchecked")
    PrcPurchaseReturnLineCreate<RS> proc = (PrcPurchaseReturnLineCreate<RS>)
      this.processorsMap.get(PrcPurchaseReturnLineCreate.class.getSimpleName());
    if (proc == null) {
      proc = new PrcPurchaseReturnLineCreate<RS>();
      @SuppressWarnings("unchecked")
      PrcEntityCreate<RS, PurchaseReturnLine, Long> procDlg =
        (PrcEntityCreate<RS, PurchaseReturnLine, Long>)
          this.fctBnEntitiesProcessors
            .lazyGet(pAddParam, PrcEntityCreate.class.getSimpleName());
      proc.setPrcEntityCreate(procDlg);
      //assigning fully initialized object:
      this.processorsMap
        .put(PrcPurchaseReturnLineCreate.class.getSimpleName(), proc);
    }
    return proc;
  }

  /**
   * <p>Get PrcPurchaseReturnLineGfr (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcPurchaseReturnLineGfr
   * @throws Exception - an exception
   */
  protected final PrcPurchaseReturnLineGfr lazyGetPrcPurchaseReturnLineGfr(
    final Map<String, Object> pAddParam) throws Exception {
    @SuppressWarnings("unchecked")
    PrcPurchaseReturnLineGfr<RS> proc = (PrcPurchaseReturnLineGfr<RS>)
      this.processorsMap.get(PrcPurchaseReturnLineGfr.class.getSimpleName());
    if (proc == null) {
      proc = new PrcPurchaseReturnLineGfr<RS>();
      @SuppressWarnings("unchecked")
      PrcEntityPbCopy<RS, PurchaseReturnLine> procDlg =
        (PrcEntityPbCopy<RS, PurchaseReturnLine>) this.fctBnEntitiesProcessors
          .lazyGet(pAddParam, PrcEntityPbCopy.class.getSimpleName());
      proc.setPrcAccEntityPbCopy(procDlg);
      //assigning fully initialized object:
      this.processorsMap
        .put(PrcPurchaseReturnLineGfr.class.getSimpleName(), proc);
    }
    return proc;
  }

  /**
   * <p>Get PrcPurchaseReturnLineCopy (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcPurchaseReturnLineCopy
   * @throws Exception - an exception
   */
  protected final PrcPurchaseReturnLineCopy
    lazyGetPrcPurchaseReturnLineCopy(
      final Map<String, Object> pAddParam) throws Exception {
    @SuppressWarnings("unchecked")
    PrcPurchaseReturnLineCopy<RS> proc = (PrcPurchaseReturnLineCopy<RS>)
      this.processorsMap.get(PrcPurchaseReturnLineCopy.class.getSimpleName());
    if (proc == null) {
      proc = new PrcPurchaseReturnLineCopy<RS>();
      @SuppressWarnings("unchecked")
      PrcEntityPbCopy<RS, PurchaseReturnLine> procDlg =
        (PrcEntityPbCopy<RS, PurchaseReturnLine>) this.fctBnEntitiesProcessors
          .lazyGet(pAddParam, PrcEntityPbCopy.class.getSimpleName());
      proc.setPrcAccEntityPbCopy(procDlg);
      //assigning fully initialized object:
      this.processorsMap
        .put(PrcPurchaseReturnLineCopy.class.getSimpleName(), proc);
    }
    return proc;
  }

  /**
   * <p>Get PrcSalesReturnLineGfr (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcSalesReturnLineGfr
   * @throws Exception - an exception
   */
  protected final PrcSalesReturnLineGfr lazyGetPrcSalesReturnLineGfr(
    final Map<String, Object> pAddParam) throws Exception {
    @SuppressWarnings("unchecked")
    PrcSalesReturnLineGfr<RS> proc = (PrcSalesReturnLineGfr<RS>)
      this.processorsMap.get(PrcSalesReturnLineGfr.class.getSimpleName());
    if (proc == null) {
      proc = new PrcSalesReturnLineGfr<RS>();
      @SuppressWarnings("unchecked")
      PrcEntityPbCopy<RS, SalesReturnLine> procDlg =
        (PrcEntityPbCopy<RS, SalesReturnLine>) this.fctBnEntitiesProcessors
          .lazyGet(pAddParam, PrcEntityPbCopy.class.getSimpleName());
      proc.setPrcAccEntityPbCopy(procDlg);
      //assigning fully initialized object:
      this.processorsMap.put(PrcSalesReturnLineGfr.class.getSimpleName(), proc);
    }
    return proc;
  }

  /**
   * <p>Get PrcSalesReturnLineCopy (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcSalesReturnLineCopy
   * @throws Exception - an exception
   */
  protected final PrcSalesReturnLineCopy lazyGetPrcSalesReturnLineCopy(
    final Map<String, Object> pAddParam) throws Exception {
    @SuppressWarnings("unchecked")
    PrcSalesReturnLineCopy<RS> proc = (PrcSalesReturnLineCopy<RS>)
      this.processorsMap.get(PrcSalesReturnLineCopy.class.getSimpleName());
    if (proc == null) {
      proc = new PrcSalesReturnLineCopy<RS>();
      @SuppressWarnings("unchecked")
      PrcEntityPbCopy<RS, SalesReturnLine> procDlg =
        (PrcEntityPbCopy<RS, SalesReturnLine>) this.fctBnEntitiesProcessors
          .lazyGet(pAddParam, PrcEntityPbCopy.class.getSimpleName());
      proc.setPrcAccEntityPbCopy(procDlg);
      //assigning fully initialized object:
      this.processorsMap
        .put(PrcSalesReturnLineCopy.class.getSimpleName(), proc);
    }
    return proc;
  }

  /**
   * <p>Get PrcUsedMaterialLineGfr (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcUsedMaterialLineGfr
   * @throws Exception - an exception
   */
  protected final PrcUsedMaterialLineGfr lazyGetPrcUsedMaterialLineGfr(
    final Map<String, Object> pAddParam) throws Exception {
    @SuppressWarnings("unchecked")
    PrcUsedMaterialLineGfr<RS> proc = (PrcUsedMaterialLineGfr<RS>)
      this.processorsMap.get(PrcUsedMaterialLineGfr.class.getSimpleName());
    if (proc == null) {
      proc = new PrcUsedMaterialLineGfr<RS>();
      @SuppressWarnings("unchecked")
      PrcEntityPbCopy<RS, UsedMaterialLine> procDlg =
        (PrcEntityPbCopy<RS, UsedMaterialLine>) this.fctBnEntitiesProcessors
          .lazyGet(pAddParam, PrcEntityPbCopy.class.getSimpleName());
      proc.setPrcAccEntityPbCopy(procDlg);
      //assigning fully initialized object:
      this.processorsMap
        .put(PrcUsedMaterialLineGfr.class.getSimpleName(), proc);
    }
    return proc;
  }

  /**
   * <p>Get PrcUsedMaterialLineCopy (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcUsedMaterialLineCopy
   * @throws Exception - an exception
   */
  protected final PrcUsedMaterialLineCopy lazyGetPrcUsedMaterialLineCopy(
    final Map<String, Object> pAddParam) throws Exception {
    @SuppressWarnings("unchecked")
    PrcUsedMaterialLineCopy<RS> proc = (PrcUsedMaterialLineCopy<RS>)
      this.processorsMap.get(PrcUsedMaterialLineCopy.class.getSimpleName());
    if (proc == null) {
      proc = new PrcUsedMaterialLineCopy<RS>();
      @SuppressWarnings("unchecked")
      PrcEntityPbCopy<RS, UsedMaterialLine> procDlg =
        (PrcEntityPbCopy<RS, UsedMaterialLine>) this.fctBnEntitiesProcessors
          .lazyGet(pAddParam, PrcEntityPbCopy.class.getSimpleName());
      proc.setPrcAccEntityPbCopy(procDlg);
      //assigning fully initialized object:
      this.processorsMap
        .put(PrcUsedMaterialLineCopy.class.getSimpleName(), proc);
    }
    return proc;
  }

  /**
   * <p>Get PrcMoveItemsLineSave (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcMoveItemsLineSave
   * @throws Exception - an exception
   */
  protected final PrcMoveItemsLineSave<RS> lazyGetPrcMoveItemsLineSave(
    final Map<String, Object> pAddParam) throws Exception {
    @SuppressWarnings("unchecked")
    PrcMoveItemsLineSave<RS> proc = (PrcMoveItemsLineSave<RS>)
      this.processorsMap.get(PrcMoveItemsLineSave.class.getSimpleName());
    if (proc == null) {
      proc = new PrcMoveItemsLineSave<RS>();
      proc.setSrvAccSettings(getSrvAccSettings());
      proc.setSrvOrm(getSrvOrm());
      proc.setSrvWarehouseEntry(getSrvWarehouseEntry());
      //assigning fully initialized object:
      this.processorsMap
        .put(PrcMoveItemsLineSave.class.getSimpleName(), proc);
    }
    return proc;
  }

  /**
   * <p>Get PrcUsedMaterialLineSave (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcUsedMaterialLineSave
   * @throws Exception - an exception
   */
  protected final PrcUsedMaterialLineSave<RS> lazyGetPrcUsedMaterialLineSave(
    final Map<String, Object> pAddParam) throws Exception {
    @SuppressWarnings("unchecked")
    PrcUsedMaterialLineSave<RS> proc = (PrcUsedMaterialLineSave<RS>)
      this.processorsMap.get(PrcUsedMaterialLineSave.class.getSimpleName());
    if (proc == null) {
      proc = new PrcUsedMaterialLineSave<RS>();
      proc.setSrvAccSettings(getSrvAccSettings());
      proc.setSrvOrm(getSrvOrm());
      proc.setSrvWarehouseEntry(getSrvWarehouseEntry());
      proc.setSrvUseMaterialEntry(getSrvUseMaterialEntry());
      proc.setSrvDatabase(getSrvDatabase());
      //assigning fully initialized object:
      this.processorsMap
        .put(PrcUsedMaterialLineSave.class.getSimpleName(), proc);
    }
    return proc;
  }

  /**
   * <p>Get PrcGoodsLossLineGfr (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcGoodsLossLineGfr
   * @throws Exception - an exception
   */
  protected final PrcGoodsLossLineGfr lazyGetPrcGoodsLossLineGfr(
    final Map<String, Object> pAddParam) throws Exception {
    @SuppressWarnings("unchecked")
    PrcGoodsLossLineGfr<RS> proc = (PrcGoodsLossLineGfr<RS>)
      this.processorsMap.get(PrcGoodsLossLineGfr.class.getSimpleName());
    if (proc == null) {
      proc = new PrcGoodsLossLineGfr<RS>();
      @SuppressWarnings("unchecked")
      PrcEntityPbCopy<RS, GoodsLossLine> procDlg =
        (PrcEntityPbCopy<RS, GoodsLossLine>) this.fctBnEntitiesProcessors
          .lazyGet(pAddParam, PrcEntityPbCopy.class.getSimpleName());
      proc.setPrcAccEntityPbCopy(procDlg);
      //assigning fully initialized object:
      this.processorsMap.put(PrcGoodsLossLineGfr.class.getSimpleName(), proc);
    }
    return proc;
  }

  /**
   * <p>Get PrcGoodsLossLineCopy (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcGoodsLossLineCopy
   * @throws Exception - an exception
   */
  protected final PrcGoodsLossLineCopy lazyGetPrcGoodsLossLineCopy(
    final Map<String, Object> pAddParam) throws Exception {
    @SuppressWarnings("unchecked")
    PrcGoodsLossLineCopy<RS> proc = (PrcGoodsLossLineCopy<RS>)
        this.processorsMap.get(PrcGoodsLossLineCopy.class.getSimpleName());
    if (proc == null) {
      proc = new PrcGoodsLossLineCopy<RS>();
      @SuppressWarnings("unchecked")
      PrcEntityPbCopy<RS, GoodsLossLine> procDlg =
        (PrcEntityPbCopy<RS, GoodsLossLine>) this.fctBnEntitiesProcessors
          .lazyGet(pAddParam, PrcEntityPbCopy.class.getSimpleName());
      proc.setPrcAccEntityPbCopy(procDlg);
      //assigning fully initialized object:
      this.processorsMap
        .put(PrcGoodsLossLineCopy.class.getSimpleName(), proc);
    }
    return proc;
  }

  /**
   * <p>Get PrcGoodsLossLineSave (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcGoodsLossLineSave
   * @throws Exception - an exception
   */
  protected final PrcGoodsLossLineSave<RS> lazyGetPrcGoodsLossLineSave(
    final Map<String, Object> pAddParam) throws Exception {
    @SuppressWarnings("unchecked")
    PrcGoodsLossLineSave<RS> proc = (PrcGoodsLossLineSave<RS>)
      this.processorsMap.get(PrcGoodsLossLineSave.class.getSimpleName());
    if (proc == null) {
      proc = new PrcGoodsLossLineSave<RS>();
      proc.setSrvAccSettings(getSrvAccSettings());
      proc.setSrvOrm(getSrvOrm());
      proc.setSrvWarehouseEntry(getSrvWarehouseEntry());
      proc.setSrvCogsEntry(getSrvCogsEntry());
      proc.setSrvDatabase(getSrvDatabase());
      //assigning fully initialized object:
      this.processorsMap
        .put(PrcGoodsLossLineSave.class.getSimpleName(), proc);
    }
    return proc;
  }

  /**
   * <p>Get PrcSalesInvoiceLineGfr (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcSalesInvoiceLineGfr
   * @throws Exception - an exception
   */
  protected final PrcSalesInvoiceLineGfr lazyGetPrcSalesInvoiceLineGfr(
    final Map<String, Object> pAddParam) throws Exception {
    @SuppressWarnings("unchecked")
    PrcSalesInvoiceLineGfr<RS> proc = (PrcSalesInvoiceLineGfr<RS>)
      this.processorsMap.get(PrcSalesInvoiceLineGfr.class.getSimpleName());
    if (proc == null) {
      proc = new PrcSalesInvoiceLineGfr<RS>();
      @SuppressWarnings("unchecked")
      PrcEntityPbCopy<RS, SalesInvoiceLine> procDlg =
        (PrcEntityPbCopy<RS, SalesInvoiceLine>)
          this.fctBnEntitiesProcessors
            .lazyGet(pAddParam, PrcEntityPbCopy.class.getSimpleName());
      proc.setPrcAccEntityPbCopy(procDlg);
      //assigning fully initialized object:
      this.processorsMap
        .put(PrcSalesInvoiceLineGfr.class.getSimpleName(), proc);
    }
    return proc;
  }

  /**
   * <p>Get PrcSalesInvoiceLineCopy (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcSalesInvoiceLineCopy
   * @throws Exception - an exception
   */
  protected final PrcSalesInvoiceLineCopy lazyGetPrcSalesInvoiceLineCopy(
    final Map<String, Object> pAddParam) throws Exception {
    @SuppressWarnings("unchecked")
    PrcSalesInvoiceLineCopy<RS> proc = (PrcSalesInvoiceLineCopy<RS>)
      this.processorsMap.get(PrcSalesInvoiceLineCopy.class.getSimpleName());
    if (proc == null) {
      proc = new PrcSalesInvoiceLineCopy<RS>();
      @SuppressWarnings("unchecked")
      PrcEntityPbCopy<RS, SalesInvoiceLine> procDlg =
        (PrcEntityPbCopy<RS, SalesInvoiceLine>)
          this.fctBnEntitiesProcessors
            .lazyGet(pAddParam, PrcEntityPbCopy.class.getSimpleName());
      proc.setPrcAccEntityPbCopy(procDlg);
      //assigning fully initialized object:
      this.processorsMap
        .put(PrcSalesInvoiceLineCopy.class.getSimpleName(), proc);
    }
    return proc;
  }

  /**
   * <p>Get PrcSalesInvoiceLineSave (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcSalesInvoiceLineSave
   * @throws Exception - an exception
   */
  protected final PrcSalesInvoiceLineSave<RS> lazyGetPrcSalesInvoiceLineSave(
    final Map<String, Object> pAddParam) throws Exception {
    @SuppressWarnings("unchecked")
    PrcSalesInvoiceLineSave<RS> proc = (PrcSalesInvoiceLineSave<RS>)
      this.processorsMap.get(PrcSalesInvoiceLineSave.class.getSimpleName());
    if (proc == null) {
      proc = new PrcSalesInvoiceLineSave<RS>();
      proc.setSrvAccSettings(getSrvAccSettings());
      proc.setSrvOrm(getSrvOrm());
      proc.setSrvWarehouseEntry(getSrvWarehouseEntry());
      proc.setSrvCogsEntry(getSrvCogsEntry());
      proc.setUtlInvLine(lazyGetUtlSalGdLn(pAddParam));
      //assigning fully initialized object:
      this.processorsMap
        .put(PrcSalesInvoiceLineSave.class.getSimpleName(), proc);
    }
    return proc;
  }

  /**
   * <p>Get PrcBeginningInventoryLineGfr (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcBeginningInventoryLineGfr
   * @throws Exception - an exception
   */
  protected final PrcBeginningInventoryLineGfr
    lazyGetPrcBeginningInventoryLineGfr(
      final Map<String, Object> pAddParam) throws Exception {
    @SuppressWarnings("unchecked")
    PrcBeginningInventoryLineGfr<RS> proc =
      (PrcBeginningInventoryLineGfr<RS>) this.processorsMap
        .get(PrcBeginningInventoryLineGfr.class.getSimpleName());
    if (proc == null) {
      proc = new PrcBeginningInventoryLineGfr<RS>();
      @SuppressWarnings("unchecked")
      PrcEntityPbCopy<RS, BeginningInventoryLine> procDlg =
        (PrcEntityPbCopy<RS, BeginningInventoryLine>)
          this.fctBnEntitiesProcessors
            .lazyGet(pAddParam, PrcEntityPbCopy.class.getSimpleName());
      proc.setPrcAccEntityPbCopy(procDlg);
      //assigning fully initialized object:
      this.processorsMap
        .put(PrcBeginningInventoryLineGfr.class.getSimpleName(), proc);
    }
    return proc;
  }

  /**
   * <p>Get PrcBeginningInventoryLineCopy (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcBeginningInventoryLineCopy
   * @throws Exception - an exception
   */
  protected final PrcBeginningInventoryLineCopy
    lazyGetPrcBeginningInventoryLineCopy(
      final Map<String, Object> pAddParam) throws Exception {
    @SuppressWarnings("unchecked")
    PrcBeginningInventoryLineCopy<RS> proc =
      (PrcBeginningInventoryLineCopy<RS>) this.processorsMap
        .get(PrcBeginningInventoryLineCopy.class.getSimpleName());
    if (proc == null) {
      proc = new PrcBeginningInventoryLineCopy<RS>();
      @SuppressWarnings("unchecked")
      PrcEntityPbCopy<RS, BeginningInventoryLine> procDlg =
        (PrcEntityPbCopy<RS, BeginningInventoryLine>)
          this.fctBnEntitiesProcessors
            .lazyGet(pAddParam, PrcEntityPbCopy.class.getSimpleName());
      proc.setPrcAccEntityPbCopy(procDlg);
      //assigning fully initialized object:
      this.processorsMap
        .put(PrcBeginningInventoryLineCopy.class.getSimpleName(), proc);
    }
    return proc;
  }

  /**
   * <p>Get PrcBeginningInventoryLineSave (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcBeginningInventoryLineSave
   * @throws Exception - an exception
   */
  protected final PrcBeginningInventoryLineSave<RS>
    lazyGetPrcBeginningInventoryLineSave(
      final Map<String, Object> pAddParam) throws Exception {
    @SuppressWarnings("unchecked")
    PrcBeginningInventoryLineSave<RS> proc = (PrcBeginningInventoryLineSave<RS>)
  this.processorsMap.get(PrcBeginningInventoryLineSave.class.getSimpleName());
    if (proc == null) {
      proc = new PrcBeginningInventoryLineSave<RS>();
      proc.setSrvAccSettings(getSrvAccSettings());
      proc.setSrvOrm(getSrvOrm());
      proc.setSrvWarehouseEntry(getSrvWarehouseEntry());
      proc.setSrvDatabase(getSrvDatabase());
      //assigning fully initialized object:
      this.processorsMap
        .put(PrcBeginningInventoryLineSave.class.getSimpleName(), proc);
    }
    return proc;
  }

  /**
   * <p>Get PrcPurchaseInvoiceLineGfr (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcPurchaseInvoiceLineGfr
   * @throws Exception - an exception
   */
  protected final PrcPurchaseInvoiceLineGfr lazyGetPrcPurchaseInvoiceLineGfr(
    final Map<String, Object> pAddParam) throws Exception {
    @SuppressWarnings("unchecked")
    PrcPurchaseInvoiceLineGfr<RS> proc = (PrcPurchaseInvoiceLineGfr<RS>)
      this.processorsMap.get(PrcPurchaseInvoiceLineGfr.class.getSimpleName());
    if (proc == null) {
      proc = new PrcPurchaseInvoiceLineGfr<RS>();
      @SuppressWarnings("unchecked")
      PrcEntityPbCopy<RS, PurchaseInvoiceLine> procDlg =
        (PrcEntityPbCopy<RS, PurchaseInvoiceLine>)
          this.fctBnEntitiesProcessors
            .lazyGet(pAddParam, PrcEntityPbCopy.class.getSimpleName());
      proc.setPrcAccEntityPbCopy(procDlg);
      //assigning fully initialized object:
      this.processorsMap
        .put(PrcPurchaseInvoiceLineGfr.class.getSimpleName(), proc);
    }
    return proc;
  }

  /**
   * <p>Get PrcPurchaseInvoiceLineCopy (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcPurchaseInvoiceLineCopy
   * @throws Exception - an exception
   */
  protected final PrcPurchaseInvoiceLineCopy
    lazyGetPrcPurchaseInvoiceLineCopy(
      final Map<String, Object> pAddParam) throws Exception {
    @SuppressWarnings("unchecked")
    PrcPurchaseInvoiceLineCopy<RS> proc = (PrcPurchaseInvoiceLineCopy<RS>)
      this.processorsMap.get(PrcPurchaseInvoiceLineCopy.class.getSimpleName());
    if (proc == null) {
      proc = new PrcPurchaseInvoiceLineCopy<RS>();
      @SuppressWarnings("unchecked")
      PrcEntityPbCopy<RS, PurchaseInvoiceLine> procDlg =
        (PrcEntityPbCopy<RS, PurchaseInvoiceLine>)
          this.fctBnEntitiesProcessors
            .lazyGet(pAddParam, PrcEntityPbCopy.class.getSimpleName());
      proc.setPrcAccEntityPbCopy(procDlg);
      //assigning fully initialized object:
      this.processorsMap
        .put(PrcPurchaseInvoiceLineCopy.class.getSimpleName(), proc);
    }
    return proc;
  }

  /**
   * <p>Get PrcInvTaxLnSave (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcInvTaxLnSave
   * @throws Exception - an exception
   */
  protected final PrcInvTaxLnSave<RS, SalesInvoice, SalesInvoiceTaxLine>
    lazyGetPrcSalInvTaxLnSave(
      final Map<String, Object> pAddParam) throws Exception {
    @SuppressWarnings("unchecked")
    PrcInvTaxLnSave<RS, SalesInvoice, SalesInvoiceTaxLine> proc =
      (PrcInvTaxLnSave<RS, SalesInvoice, SalesInvoiceTaxLine>)
        this.processorsMap.get(PrcInvTaxLnSave.class.getSimpleName() + "SITL");
    if (proc == null) {
      proc = new PrcInvTaxLnSave<RS, SalesInvoice, SalesInvoiceTaxLine>();
      proc.setSrvAccSettings(getSrvAccSettings());
      proc.setSrvOrm(getSrvOrm());
      proc.setUtlInvLine(lazyGetUtlSalGdLn(pAddParam));
      //assigning fully initialized object:
      this.processorsMap
        .put(PrcInvTaxLnSave.class.getSimpleName() + "SITL", proc);
    }
    return proc;
  }

  /**
   * <p>Get PrcInvTaxLnSave (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcInvTaxLnSave
   * @throws Exception - an exception
   */
  protected final PrcInvTaxLnSave<RS, SalesReturn, SalesReturnTaxLine>
    lazyGetPrcSalRetTaxLnSave(
      final Map<String, Object> pAddParam) throws Exception {
    @SuppressWarnings("unchecked")
    PrcInvTaxLnSave<RS, SalesReturn, SalesReturnTaxLine> proc =
      (PrcInvTaxLnSave<RS, SalesReturn, SalesReturnTaxLine>)
        this.processorsMap.get(PrcInvTaxLnSave.class.getSimpleName() + "SRTL");
    if (proc == null) {
      proc = new PrcInvTaxLnSave<RS, SalesReturn, SalesReturnTaxLine>();
      proc.setSrvAccSettings(getSrvAccSettings());
      proc.setSrvOrm(getSrvOrm());
      proc.setUtlInvLine(lazyGetUtlSalRetGdLn(pAddParam));
      //assigning fully initialized object:
      this.processorsMap
        .put(PrcInvTaxLnSave.class.getSimpleName() + "SRTL", proc);
    }
    return proc;
  }

  /**
   * <p>Get PrcPurRetTaxLnSave (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcPurRetTaxLnSave
   * @throws Exception - an exception
   */
  protected final PrcPurRetTaxLnSave<RS>
    lazyGetPrcPurchRetTaxLnSave(
      final Map<String, Object> pAddParam) throws Exception {
    @SuppressWarnings("unchecked")
    PrcPurRetTaxLnSave<RS> proc = (PrcPurRetTaxLnSave<RS>)
      this.processorsMap.get(PrcPurRetTaxLnSave.class.getSimpleName());
    if (proc == null) {
      proc = new PrcPurRetTaxLnSave<RS>();
      proc.setSrvAccSettings(getSrvAccSettings());
      proc.setSrvOrm(getSrvOrm());
      proc.setUtlInvLine(lazyGetUtlPurRetGdLn(pAddParam));
      //assigning fully initialized object:
      this.processorsMap
        .put(PrcPurRetTaxLnSave.class.getSimpleName(), proc);
    }
    return proc;
  }

  /**
   * <p>Get PrcInvTaxLnSave (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcInvTaxLnSave
   * @throws Exception - an exception
   */
  protected final PrcInvTaxLnSave<RS, PurchaseInvoice, PurchaseInvoiceTaxLine>
    lazyGetPrcPurchInvTaxLnSave(
      final Map<String, Object> pAddParam) throws Exception {
    @SuppressWarnings("unchecked")
    PrcInvTaxLnSave<RS, PurchaseInvoice, PurchaseInvoiceTaxLine> proc =
      (PrcInvTaxLnSave<RS, PurchaseInvoice, PurchaseInvoiceTaxLine>)
        this.processorsMap.get(PrcInvTaxLnSave.class.getSimpleName() + "PITL");
    if (proc == null) {
      proc = new PrcInvTaxLnSave<RS, PurchaseInvoice, PurchaseInvoiceTaxLine>();
      proc.setSrvAccSettings(getSrvAccSettings());
      proc.setSrvOrm(getSrvOrm());
      proc.setUtlInvLine(lazyGetUtlPurGdLn(pAddParam));
      //assigning fully initialized object:
      this.processorsMap
        .put(PrcInvTaxLnSave.class.getSimpleName() + "PITL", proc);
    }
    return proc;
  }

  /**
   * <p>Get PrcPurchaseInvoiceLineSave (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcPurchaseInvoiceLineSave
   * @throws Exception - an exception
   */
  protected final PrcPurchaseInvoiceLineSave<RS>
    lazyGetPrcPurchaseInvoiceLineSave(
      final Map<String, Object> pAddParam) throws Exception {
    @SuppressWarnings("unchecked")
    PrcPurchaseInvoiceLineSave<RS> proc = (PrcPurchaseInvoiceLineSave<RS>)
      this.processorsMap.get(PrcPurchaseInvoiceLineSave.class.getSimpleName());
    if (proc == null) {
      proc = new PrcPurchaseInvoiceLineSave<RS>();
      proc.setSrvAccSettings(getSrvAccSettings());
      proc.setSrvOrm(getSrvOrm());
      proc.setSrvWarehouseEntry(getSrvWarehouseEntry());
      proc.setUtlInvLine(lazyGetUtlPurGdLn(pAddParam));
      //assigning fully initialized object:
      this.processorsMap
        .put(PrcPurchaseInvoiceLineSave.class.getSimpleName(), proc);
    }
    return proc;
  }

  /**
   * <p>Get PrcAccEntityPbWithSubaccCopy (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcAccEntityPbWithSubaccCopy
   * @throws Exception - an exception
   */
  protected final PrcAccEntityPbWithSubaccCopy
    lazyGetPrcAccEntityPbWithSubaccCopy(
      final Map<String, Object> pAddParam) throws Exception {
    @SuppressWarnings("unchecked")
    PrcAccEntityPbWithSubaccCopy<RS, IPersistableBase> proc =
      (PrcAccEntityPbWithSubaccCopy<RS, IPersistableBase>)
        this.processorsMap
          .get(PrcAccEntityPbWithSubaccCopy.class.getSimpleName());
    if (proc == null) {
      proc = new PrcAccEntityPbWithSubaccCopy<RS, IPersistableBase>();
      @SuppressWarnings("unchecked")
      PrcEntityPbCopy<RS, IPersistableBase> procDlg =
        (PrcEntityPbCopy<RS, IPersistableBase>)
          this.fctBnEntitiesProcessors
            .lazyGet(pAddParam, PrcEntityPbCopy.class.getSimpleName());
      proc.setPrcAccEntityPbCopy(procDlg);
      proc.setSrvTypeCode(getSrvTypeCode());
      //assigning fully initialized object:
      this.processorsMap
        .put(PrcAccEntityPbWithSubaccCopy.class.getSimpleName(), proc);
    }
    return proc;
  }

  /**
   * <p>Lazy get PrcAccDocGetForReverse.</p>
   * @param pAddParam additional param
   * @return requested PrcAccDocGetForReverse
   * @throws Exception - an exception
   */
  protected final PrcAccDocGetForReverse lazyGetPrcAccDocGetForReverse(
    final Map<String, Object> pAddParam) throws Exception {
    @SuppressWarnings("unchecked")
    PrcAccDocGetForReverse<RS, IDoc> proc = (PrcAccDocGetForReverse<RS, IDoc>)
      this.processorsMap.get(PrcAccDocGetForReverse.class.getSimpleName());
    if (proc == null) {
      proc = new PrcAccDocGetForReverse<RS, IDoc>();
      proc.setSrvOrm(getSrvOrm());
      @SuppressWarnings("unchecked")
      IEntityProcessor<IDoc, Long> procDlg =
        (IEntityProcessor<IDoc, Long>)
          this.fctBnEntitiesProcessors
            .lazyGet(pAddParam, PrcEntityPbCopy.class.getSimpleName());
      proc.setPrcAccEntityPbCopy(procDlg);
      //assigning fully initialized object:
      this.processorsMap
        .put(PrcAccDocGetForReverse.class.getSimpleName(), proc);
    }
    return proc;
  }

  /**
   * <p>Lazy get PrcAccDocWithTaxesCopy.</p>
   * @param pAddParam additional param
   * @return requested PrcAccDocWithTaxesCopy
   * @throws Exception - an exception
   */
  protected final PrcAccDocWithTaxesCopy<RS, ADocWithTaxes>
    lazyGetPrcAccDocWithTaxesCopy(
      final Map<String, Object> pAddParam) throws Exception {
    @SuppressWarnings("unchecked")
    PrcAccDocWithTaxesCopy<RS, ADocWithTaxes> proc =
      (PrcAccDocWithTaxesCopy<RS, ADocWithTaxes>)
        this.processorsMap
          .get(PrcAccDocWithTaxesCopy.class.getSimpleName());
    if (proc == null) {
      proc = new PrcAccDocWithTaxesCopy<RS, ADocWithTaxes>();
      @SuppressWarnings("unchecked")
      IEntityProcessor<ADocWithTaxes, Long> procDlg =
        (IEntityProcessor<ADocWithTaxes, Long>)
          this.fctBnEntitiesProcessors
            .lazyGet(pAddParam, PrcEntityPbCopy.class.getSimpleName());
      proc.setPrcAccEntityPbCopy(procDlg);
      //assigning fully initialized object:
      this.processorsMap
        .put(PrcAccDocWithTaxesCopy.class.getSimpleName(), proc);
    }
    return proc;
  }

  /**
   * <p>Lazy get PrcAccDocWithTaxesPaymentsCopy.</p>
   * @param pAddParam additional param
   * @return requested PrcAccDocWithTaxesPaymentsCopy
   * @throws Exception - an exception
   */
  protected final PrcAccDocWithTaxesPaymentsCopy<RS, ADocWithTaxesPayments>
    lazyGetPrcAccDocWithTaxesPaymentsCopy(
      final Map<String, Object> pAddParam) throws Exception {
    @SuppressWarnings("unchecked")
    PrcAccDocWithTaxesPaymentsCopy<RS, ADocWithTaxesPayments> proc =
      (PrcAccDocWithTaxesPaymentsCopy<RS, ADocWithTaxesPayments>)
        this.processorsMap
          .get(PrcAccDocWithTaxesPaymentsCopy.class.getSimpleName());
    if (proc == null) {
      proc =
        new PrcAccDocWithTaxesPaymentsCopy<RS, ADocWithTaxesPayments>();
      @SuppressWarnings("unchecked")
      IEntityProcessor<ADocWithTaxesPayments, Long> procDlg =
        (IEntityProcessor<ADocWithTaxesPayments, Long>)
          this.fctBnEntitiesProcessors
            .lazyGet(pAddParam, PrcEntityPbCopy.class.getSimpleName());
      proc.setPrcAccEntityPbCopy(procDlg);
      //assigning fully initialized object:
      this.processorsMap
        .put(PrcAccDocWithTaxesPaymentsCopy.class.getSimpleName(), proc);
    }
    return proc;
  }

  /**
   * <p>Lazy get PrcAccDocWithTaxesGetForReverse.</p>
   * @param pAddParam additional param
   * @return requested PrcAccDocWithTaxesGetForReverse
   * @throws Exception - an exception
   */
  protected final PrcAccDocWithTaxesGetForReverse<RS, ADocWithTaxes>
    lazyGetPrcAccDocWithTaxesGetForReverse(
      final Map<String, Object> pAddParam) throws Exception {
    @SuppressWarnings("unchecked")
    PrcAccDocWithTaxesGetForReverse<RS, ADocWithTaxes> proc =
      (PrcAccDocWithTaxesGetForReverse<RS, ADocWithTaxes>) this.processorsMap
        .get(PrcAccDocWithTaxesGetForReverse.class.getSimpleName());
    if (proc == null) {
      proc =
        new PrcAccDocWithTaxesGetForReverse<RS, ADocWithTaxes>();
      @SuppressWarnings("unchecked")
      IEntityProcessor<ADocWithTaxes, Long> procDlg =
        (IEntityProcessor<ADocWithTaxes, Long>)
          lazyGetPrcAccDocGetForReverse(pAddParam);
      proc.setPrcDocReverse(procDlg);
      //assigning fully initialized object:
      this.processorsMap
        .put(PrcAccDocWithTaxesGetForReverse.class.getSimpleName(), proc);
    }
    return proc;
  }

  /**
   * <p>Get PrcAccEntityWithSubaccCopy (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcAccEntityWithSubaccCopy
   * @throws Exception - an exception
   */
  protected final PrcAccEntityWithSubaccCopy<RS, IHasId<Object>, Object>
    createPutPrcAccEntityWithSubaccCopy(
      final Map<String, Object> pAddParam) throws Exception {
    PrcAccEntityWithSubaccCopy<RS, IHasId<Object>, Object> proc =
      new PrcAccEntityWithSubaccCopy<RS, IHasId<Object>, Object>();
    @SuppressWarnings("unchecked")
    PrcEntityCopy<RS, IHasId<Object>, Object> procDlg =
      (PrcEntityCopy<RS, IHasId<Object>, Object>) this.fctBnEntitiesProcessors
        .lazyGet(pAddParam, PrcEntityCopy.class.getSimpleName());
    proc.setPrcAccEntityCopy(procDlg);
    proc.setSrvTypeCode(getSrvTypeCode());
    //assigning fully initialized object:
    this.processorsMap
      .put(PrcAccEntityWithSubaccCopy.class.getSimpleName(), proc);
    return proc;
  }

  /**
   * <p>Get PrcEmailMsgSave (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcEmailMsgSave
   * @throws Exception - an exception
   */
  protected final PrcEmailMsgSave<RS> createPutPrcEmailMsgSave(
    final Map<String, Object> pAddParam) throws Exception {
    @SuppressWarnings("unchecked")
    PrcEmailMsgSave<RS> proc = (PrcEmailMsgSave<RS>)
      this.fctBnEntitiesProcessors
        .lazyGet(pAddParam, PrcEmailMsgSave.class.getSimpleName());
    //assigning fully initialized object:
    this.processorsMap.put(PrcEmailMsgSave.class.getSimpleName(), proc);
    return proc;
  }

  /**
   * <p>Get PrcSalesInvoiceServiceLineSave (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcSalesInvoiceServiceLineSave
   * @throws Exception - an exception
   */
  protected final PrcSalesInvoiceServiceLineSave<RS>
    lazyGetPrcSalesInvoiceServiceLineSave(
      final Map<String, Object> pAddParam) throws Exception {
    @SuppressWarnings("unchecked")
    PrcSalesInvoiceServiceLineSave<RS> proc =
      (PrcSalesInvoiceServiceLineSave<RS>) this.processorsMap
        .get(PrcSalesInvoiceServiceLineSave.class.getSimpleName());
    if (proc == null) {
      proc = new PrcSalesInvoiceServiceLineSave<RS>();
      proc.setSrvAccSettings(getSrvAccSettings());
      proc.setSrvOrm(getSrvOrm());
      proc.setUtlInvLine(lazyGetUtlSalSrLn(pAddParam));
      //assigning fully initialized object:
      this.processorsMap
        .put(PrcSalesInvoiceServiceLineSave.class.getSimpleName(), proc);
    }
    return proc;
  }

  /**
   * <p>Get PrcPurchaseInvoiceServiceLineSave (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcPurchaseInvoiceServiceLineSave
   * @throws Exception - an exception
   */
  protected final PrcPurchaseInvoiceServiceLineSave<RS>
    lazyGetPrcPurchaseInvoiceServiceLineSave(
      final Map<String, Object> pAddParam) throws Exception {
    @SuppressWarnings("unchecked")
    PrcPurchaseInvoiceServiceLineSave<RS> proc =
      (PrcPurchaseInvoiceServiceLineSave<RS>)
      this.processorsMap
        .get(PrcPurchaseInvoiceServiceLineSave.class.getSimpleName());
    if (proc == null) {
      proc = new PrcPurchaseInvoiceServiceLineSave<RS>();
      proc.setSrvAccSettings(getSrvAccSettings());
      proc.setSrvOrm(getSrvOrm());
      proc.setUtlInvLine(lazyGetUtlPurSrLn(pAddParam));
      //assigning fully initialized object:
      this.processorsMap
        .put(PrcPurchaseInvoiceServiceLineSave.class.getSimpleName(), proc);
    }
    return proc;
  }

  /**
   * <p>Get PrcPurchaseReturnLineSave (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcPurchaseReturnLineSave
   * @throws Exception - an exception
   */
  protected final PrcPurchaseReturnLineSave<RS>
    lazyGetPrcPurchaseReturnLineSave(
      final Map<String, Object> pAddParam) throws Exception {
    @SuppressWarnings("unchecked")
    PrcPurchaseReturnLineSave<RS> proc =
      (PrcPurchaseReturnLineSave<RS>)
      this.processorsMap
        .get(PrcPurchaseReturnLineSave.class.getSimpleName());
    if (proc == null) {
      proc = new PrcPurchaseReturnLineSave<RS>();
      proc.setSrvAccSettings(getSrvAccSettings());
      proc.setSrvNumberToString(getSrvNumberToString());
      proc.setSrvOrm(getSrvOrm());
      proc.setSrvI18n(getSrvI18n());
      proc.setUtlInvLine(lazyGetUtlPurRetGdLn(pAddParam));
      proc.setSrvWarehouseEntry(getSrvWarehouseEntry());
      proc.setSrvUseMaterialEntry(getSrvUseMaterialEntry());
      //assigning fully initialized object:
      this.processorsMap
        .put(PrcPurchaseReturnLineSave.class.getSimpleName(), proc);
    }
    return proc;
  }

  /**
   * <p>Get PrcWageTaxLineDelete (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcWageTaxLineDelete
   * @throws Exception - an exception
   */
  protected final PrcWageTaxLineDelete<RS>
    lazyGetPrcWageTaxLineDelete(
      final Map<String, Object> pAddParam) throws Exception {
    @SuppressWarnings("unchecked")
    PrcWageTaxLineDelete<RS> proc =
      (PrcWageTaxLineDelete<RS>)
      this.processorsMap
        .get(PrcWageTaxLineDelete.class.getSimpleName());
    if (proc == null) {
      proc = new PrcWageTaxLineDelete<RS>();
      proc.setSrvAccSettings(getSrvAccSettings());
      proc.setSrvOrm(getSrvOrm());
      proc.setSrvDatabase(getSrvDatabase());
      //assigning fully initialized object:
      this.processorsMap
        .put(PrcWageTaxLineDelete.class.getSimpleName(), proc);
    }
    return proc;
  }

  /**
   * <p>Get PrcWageTaxLineSave (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcWageTaxLineSave
   * @throws Exception - an exception
   */
  protected final PrcWageTaxLineSave<RS> lazyGetPrcWageTaxLineSave(
    final Map<String, Object> pAddParam) throws Exception {
    @SuppressWarnings("unchecked")
    PrcWageTaxLineSave<RS> proc = (PrcWageTaxLineSave<RS>)
      this.processorsMap.get(PrcWageTaxLineSave.class.getSimpleName());
    if (proc == null) {
      proc = new PrcWageTaxLineSave<RS>();
      proc.setSrvAccSettings(getSrvAccSettings());
      proc.setSrvOrm(getSrvOrm());
      proc.setSrvDatabase(getSrvDatabase());
      //assigning fully initialized object:
      this.processorsMap.put(PrcWageTaxLineSave.class.getSimpleName(), proc);
    }
    return proc;
  }

  /**
   * <p>Get PrcWageLineDelete (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcWageLineDelete
   * @throws Exception - an exception
   */
  protected final PrcWageLineDelete<RS> lazyGetPrcWageLineDelete(
    final Map<String, Object> pAddParam) throws Exception {
    @SuppressWarnings("unchecked")
    PrcWageLineDelete<RS> proc = (PrcWageLineDelete<RS>)
      this.processorsMap.get(PrcWageLineDelete.class.getSimpleName());
    if (proc == null) {
      proc = new PrcWageLineDelete<RS>();
      proc.setSrvAccSettings(getSrvAccSettings());
      proc.setSrvOrm(getSrvOrm());
      proc.setSrvDatabase(getSrvDatabase());
      //assigning fully initialized object:
      this.processorsMap.put(PrcWageLineDelete.class.getSimpleName(), proc);
    }
    return proc;
  }

  /**
   * <p>Get PrcWageLineSave (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcWageLineSave
   * @throws Exception - an exception
   */
  protected final PrcWageLineSave<RS> lazyGetPrcWageLineSave(
    final Map<String, Object> pAddParam) throws Exception {
    @SuppressWarnings("unchecked")
    PrcWageLineSave<RS> proc = (PrcWageLineSave<RS>)
      this.processorsMap.get(PrcWageLineSave.class.getSimpleName());
    if (proc == null) {
      proc = new PrcWageLineSave<RS>();
      proc.setSrvAccSettings(getSrvAccSettings());
      proc.setSrvOrm(getSrvOrm());
      proc.setSrvDatabase(getSrvDatabase());
      //assigning fully initialized object:
      this.processorsMap.put(PrcWageLineSave.class.getSimpleName(), proc);
    }
    return proc;
  }

  /**
   * <p>Get PrcSalesReturnLineSave (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcSalesReturnLineSave
   * @throws Exception - an exception
   */
  protected final PrcSalesReturnLineSave<RS> lazyGetPrcSalesReturnLineSave(
    final Map<String, Object> pAddParam) throws Exception {
    @SuppressWarnings("unchecked")
    PrcSalesReturnLineSave<RS> proc = (PrcSalesReturnLineSave<RS>)
      this.processorsMap.get(PrcSalesReturnLineSave.class.getSimpleName());
    if (proc == null) {
      proc = new PrcSalesReturnLineSave<RS>();
      proc.setSrvAccSettings(getSrvAccSettings());
      proc.setSrvOrm(getSrvOrm());
      proc.setUtlInvLine(lazyGetUtlSalRetGdLn(pAddParam));
      proc.setSrvWarehouseEntry(getSrvWarehouseEntry());
      //assigning fully initialized object:
      this.processorsMap
        .put(PrcSalesReturnLineSave.class.getSimpleName(), proc);
    }
    return proc;
  }

  /**
   * <p>Get UtlPurchaseGoodsServiceLine (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested UtlPurchaseGoodsServiceLine
   * @throws Exception - an exception
   */
  protected final UtlInvBase<RS> lazyGetUtlInvBase(
      final Map<String, Object> pAddParam) throws Exception {
    UtlInvBase<RS> utlInvBs = this.utlInvBase;
    if (utlInvBs == null) {
      utlInvBs = new UtlInvBase<RS>();
      utlInvBs.setSrvOrm(getSrvOrm());
      utlInvBs.setLogger(getLogger());
      utlInvBs.setSrvDatabase(getSrvDatabase());
      utlInvBs.setSrvNumberToString(getSrvNumberToString());
      //assigning fully initialized object:
      this.utlInvBase = utlInvBs;
    }
    return utlInvBs;
  }

  /**
   * <p>Get InvTxMeth<SalesReturn, SalesReturnTaxLine>.</p>
   * @param pAddParam additional param
   * @return requested InvTxMeth<SalesReturn, SalesReturnTaxLine>
   * @throws Exception - an exception
   */
  protected final InvTxMeth<SalesReturn, SalesReturnTaxLine>
    lazyGetSalRetTxMeth(final Map<String, Object> pAddParam) throws Exception {
    InvTxMeth<SalesReturn, SalesReturnTaxLine> salRetTxMe =
      this.salRetTxMeth;
    if (salRetTxMe == null) {
      salRetTxMe = new InvTxMeth<SalesReturn, SalesReturnTaxLine>();
      salRetTxMe.setGoodLnCl(SalesReturnLine.class);
      salRetTxMe.setInvTxLnCl(SalesReturnTaxLine.class);
      salRetTxMe.setIsTxByUser(true);
      salRetTxMe.setStWhereAdjGdLnInvBas(
        "where SALESRETURNLINE.TAXCATEGORY is not null and REVERSEDID "
          + "is null and ITSOWNER=");
      salRetTxMe.setFlTotals("invGdTotals.sql");
      salRetTxMe.setFlTxItBas("invGdTxItBas.sql");
      salRetTxMe.setFlTxItBasAggr("invGdTxItBasAggr.sql");
      salRetTxMe.setFlTxInvBas("invGdTxInvBas.sql");
      salRetTxMe.setFlTxInvAdj("invGdTxInvAdj.sql");
      salRetTxMe.setFlTxInvBasAggr("invGdTxInvBasAggr.sql");
      salRetTxMe.setTblNmsTot(new String[] {"SALESRETURNLINE",
        "SALESRETURNTAXLINE", "SALESRETURNGOODSTAXLINE"});
      FactoryPersistableBase<SalesReturnTaxLine> fctItl =
        new FactoryPersistableBase<SalesReturnTaxLine>();
      fctItl.setObjectClass(SalesReturnTaxLine.class);
      fctItl.setDatabaseId(getSrvDatabase().getIdDatabase());
      salRetTxMe.setFctInvTxLn(fctItl);
      //assigning fully initialized object:
      this.salRetTxMeth = salRetTxMe;
    }
    return salRetTxMe;
  }

  /**
   * <p>Get sales return good line utility.</p>
   * @param pAddParam additional param
   * @return sales return good line utility
   * @throws Exception - an exception
   */
  protected final UtlInvLine<RS, SalesReturn, SalesReturnLine,
    SalesReturnTaxLine, SalesReturnGoodsTaxLine> lazyGetUtlSalRetGdLn(
      final Map<String, Object> pAddParam) throws Exception {
    UtlInvLine<RS, SalesReturn, SalesReturnLine,
      SalesReturnTaxLine, SalesReturnGoodsTaxLine> utlInvLn = this
        .utlSalRetGdLn;
    if (utlInvLn == null) {
      utlInvLn = new UtlInvLine<RS, SalesReturn, SalesReturnLine,
        SalesReturnTaxLine, SalesReturnGoodsTaxLine>();
      utlInvLn.setUtlInvBase(lazyGetUtlInvBase(pAddParam));
      utlInvLn.setInvTxMeth(lazyGetSalRetTxMeth(pAddParam));
      utlInvLn.setIsMutable(false);
      utlInvLn.setNeedMkTxCat(true);
      utlInvLn.setLtlCl(SalesReturnGoodsTaxLine.class);
      utlInvLn.setDstTxItLnCl(DestTaxGoodsLn.class);
      FactoryPersistableBase<SalesReturnGoodsTaxLine> fctLtl =
        new FactoryPersistableBase<SalesReturnGoodsTaxLine>();
      fctLtl.setObjectClass(SalesReturnGoodsTaxLine.class);
      fctLtl.setDatabaseId(getSrvDatabase().getIdDatabase());
      utlInvLn.setFctLineTxLn(fctLtl);
      //assigning fully initialized object:
      this.utlSalRetGdLn = utlInvLn;
    }
    return utlInvLn;
  }

  /**
   * <p>Get InvTxMeth<PurchaseReturn, PurchaseReturnTaxLine>.</p>
   * @param pAddParam additional param
   * @return requested InvTxMeth<PurchaseReturn, PurchaseReturnTaxLine>
   * @throws Exception - an exception
   */
  protected final InvTxMeth<PurchaseReturn, PurchaseReturnTaxLine>
    lazyGetPurRetTxMeth(final Map<String, Object> pAddParam) throws Exception {
    InvTxMeth<PurchaseReturn, PurchaseReturnTaxLine> purRetTxMe =
      this.purRetTxMeth;
    if (purRetTxMe == null) {
      purRetTxMe = new InvTxMeth<PurchaseReturn, PurchaseReturnTaxLine>();
      purRetTxMe.setGoodLnCl(PurchaseReturnLine.class);
      purRetTxMe.setInvTxLnCl(PurchaseReturnTaxLine.class);
      purRetTxMe.setIsTxByUser(true);
      purRetTxMe.setStWhereAdjGdLnInvBas(
        "where TAXCATEGORY is not null and PURCHASERETURNLINE.REVERSEDID "
          + "is null and PURCHASERETURNLINE.ITSOWNER=");
      purRetTxMe.setFlTotals("invGdTotals.sql");
      purRetTxMe.setFlTxItBas("invGdTxItBas.sql");
      purRetTxMe.setFlTxItBasAggr("purRtTxItBasAggr.sql");
      purRetTxMe.setFlTxInvBas("purRtTxInvBas.sql");
      purRetTxMe.setFlTxInvAdj("purRtTxInvAdj.sql");
      purRetTxMe.setFlTxInvBasAggr("purRtTxInvBasAggr.sql");
      purRetTxMe.setTblNmsTot(new String[] {"PURCHASERETURNLINE",
        "PURCHASERETURNTAXLINE", "PURCHASERETURNGOODSTAXLINE"});
      FactoryPersistableBase<PurchaseReturnTaxLine> fctItl =
        new FactoryPersistableBase<PurchaseReturnTaxLine>();
      fctItl.setObjectClass(PurchaseReturnTaxLine.class);
      fctItl.setDatabaseId(getSrvDatabase().getIdDatabase());
      purRetTxMe.setFctInvTxLn(fctItl);
      //assigning fully initialized object:
      this.purRetTxMeth = purRetTxMe;
    }
    return purRetTxMe;
  }

  /**
   * <p>Get purchase return good line utility.</p>
   * @param pAddParam additional param
   * @return purchase return good line utility
   * @throws Exception - an exception
   */
  protected final UtlInvLine<RS, PurchaseReturn, PurchaseReturnLine,
    PurchaseReturnTaxLine, PurchaseReturnGoodsTaxLine> lazyGetUtlPurRetGdLn(
      final Map<String, Object> pAddParam) throws Exception {
    UtlInvLine<RS, PurchaseReturn, PurchaseReturnLine,
      PurchaseReturnTaxLine, PurchaseReturnGoodsTaxLine> utlInvLn = this
        .utlPurRetGdLn;
    if (utlInvLn == null) {
      utlInvLn = new UtlInvLine<RS, PurchaseReturn, PurchaseReturnLine,
        PurchaseReturnTaxLine, PurchaseReturnGoodsTaxLine>();
      utlInvLn.setUtlInvBase(lazyGetUtlInvBase(pAddParam));
      utlInvLn.setInvTxMeth(lazyGetPurRetTxMeth(pAddParam));
      utlInvLn.setIsMutable(false);
      utlInvLn.setNeedMkTxCat(false);
      utlInvLn.setLtlCl(PurchaseReturnGoodsTaxLine.class);
      utlInvLn.setDstTxItLnCl(DestTaxGoodsLn.class);
      FactoryPersistableBase<PurchaseReturnGoodsTaxLine> fctLtl =
        new FactoryPersistableBase<PurchaseReturnGoodsTaxLine>();
      fctLtl.setObjectClass(PurchaseReturnGoodsTaxLine.class);
      fctLtl.setDatabaseId(getSrvDatabase().getIdDatabase());
      utlInvLn.setFctLineTxLn(fctLtl);
      //assigning fully initialized object:
      this.utlPurRetGdLn = utlInvLn;
    }
    return utlInvLn;
  }

  /**
   * <p>Get InvTxMeth<PurchaseInvoice, PurchaseInvoiceTaxLine>.</p>
   * @param pAddParam additional param
   * @return requested InvTxMeth<PurchaseInvoice, PurchaseInvoiceTaxLine>
   * @throws Exception - an exception
   */
  protected final InvTxMeth<PurchaseInvoice, PurchaseInvoiceTaxLine>
    lazyGetPurInvTxMeth(final Map<String, Object> pAddParam) throws Exception {
    InvTxMeth<PurchaseInvoice, PurchaseInvoiceTaxLine> purInvTxMe =
      this.purInvTxMeth;
    if (purInvTxMe == null) {
      purInvTxMe = new InvTxMeth<PurchaseInvoice, PurchaseInvoiceTaxLine>();
      purInvTxMe.setGoodLnCl(PurchaseInvoiceLine.class);
      purInvTxMe.setServiceLnCl(PurchaseInvoiceServiceLine.class);
      purInvTxMe.setInvTxLnCl(PurchaseInvoiceTaxLine.class);
      purInvTxMe.setIsTxByUser(true);
      purInvTxMe.setStWhereAdjGdLnInvBas(
        "where PURCHASEINVOICELINE.TAXCATEGORY is not null and REVERSEDID "
          + "is null and ITSOWNER=");
      purInvTxMe.setStWhereAdjSrLnInvBas(
      "where PURCHASEINVOICESERVICELINE.TAXCATEGORY is not null and REVERSEDID "
          + "is null and ITSOWNER=");
      purInvTxMe.setFlTotals("invTotals.sql");
      purInvTxMe.setFlTxItBas("invTxItBas.sql");
      purInvTxMe.setFlTxItBasAggr("invTxItBasAggr.sql");
      purInvTxMe.setFlTxInvBas("invTxInvBas.sql");
      purInvTxMe.setFlTxInvAdj("invTxInvAdj.sql");
      purInvTxMe.setFlTxInvBasAggr("invTxInvBasAggr.sql");
      purInvTxMe.setTblNmsTot(new String[] {"PURCHASEINVOICELINE",
        "PURCHASEINVOICESERVICELINE", "PURCHASEINVOICETAXLINE",
           "PURCHASEINVOICEGOODSTAXLINE", "PURCHASEINVOICESERVICETAXLINE"});
      FactoryPersistableBase<PurchaseInvoiceTaxLine> fctItl =
        new FactoryPersistableBase<PurchaseInvoiceTaxLine>();
      fctItl.setObjectClass(PurchaseInvoiceTaxLine.class);
      fctItl.setDatabaseId(getSrvDatabase().getIdDatabase());
      purInvTxMe.setFctInvTxLn(fctItl);
      //assigning fully initialized object:
      this.purInvTxMeth = purInvTxMe;
    }
    return purInvTxMe;
  }

  /**
   * <p>Get purchase invoice good line utility.</p>
   * @param pAddParam additional param
   * @return purchase invoice good line utility
   * @throws Exception - an exception
   */
  protected final UtlInvLine<RS, PurchaseInvoice, PurchaseInvoiceLine,
    PurchaseInvoiceTaxLine, PurchaseInvoiceGoodsTaxLine> lazyGetUtlPurGdLn(
      final Map<String, Object> pAddParam) throws Exception {
    UtlInvLine<RS, PurchaseInvoice, PurchaseInvoiceLine,
      PurchaseInvoiceTaxLine, PurchaseInvoiceGoodsTaxLine> utlInvLn = this
        .utlPurInvGdLn;
    if (utlInvLn == null) {
      utlInvLn = new UtlInvLine<RS, PurchaseInvoice, PurchaseInvoiceLine,
        PurchaseInvoiceTaxLine, PurchaseInvoiceGoodsTaxLine>();
      utlInvLn.setUtlInvBase(lazyGetUtlInvBase(pAddParam));
      utlInvLn.setInvTxMeth(lazyGetPurInvTxMeth(pAddParam));
      utlInvLn.setIsMutable(false);
      utlInvLn.setNeedMkTxCat(true);
      utlInvLn.setLtlCl(PurchaseInvoiceGoodsTaxLine.class);
      utlInvLn.setDstTxItLnCl(DestTaxGoodsLn.class);
      FactoryPersistableBase<PurchaseInvoiceGoodsTaxLine> fctLtl =
        new FactoryPersistableBase<PurchaseInvoiceGoodsTaxLine>();
      fctLtl.setObjectClass(PurchaseInvoiceGoodsTaxLine.class);
      fctLtl.setDatabaseId(getSrvDatabase().getIdDatabase());
      utlInvLn.setFctLineTxLn(fctLtl);
      //assigning fully initialized object:
      this.utlPurInvGdLn = utlInvLn;
    }
    return utlInvLn;
  }

  /**
   * <p>Get purchase invoice service line utility.</p>
   * @param pAddParam additional param
   * @return purchase invoice service line utility
   * @throws Exception - an exception
   */
  protected final UtlInvLine<RS, PurchaseInvoice, PurchaseInvoiceServiceLine,
    PurchaseInvoiceTaxLine, PurchaseInvoiceServiceTaxLine> lazyGetUtlPurSrLn(
      final Map<String, Object> pAddParam) throws Exception {
    UtlInvLine<RS, PurchaseInvoice, PurchaseInvoiceServiceLine,
      PurchaseInvoiceTaxLine, PurchaseInvoiceServiceTaxLine> utlInvLn = this
        .utlPurInvSrLn;
    if (utlInvLn == null) {
      utlInvLn = new UtlInvLine<RS, PurchaseInvoice, PurchaseInvoiceServiceLine,
        PurchaseInvoiceTaxLine, PurchaseInvoiceServiceTaxLine>();
      utlInvLn.setUtlInvBase(lazyGetUtlInvBase(pAddParam));
      utlInvLn.setInvTxMeth(lazyGetPurInvTxMeth(pAddParam));
      utlInvLn.setIsMutable(true);
      utlInvLn.setNeedMkTxCat(true);
      utlInvLn.setLtlCl(PurchaseInvoiceServiceTaxLine.class);
      utlInvLn.setDstTxItLnCl(DestTaxServPurchLn.class);
      FactoryPersistableBase<PurchaseInvoiceServiceTaxLine> fctLtl =
        new FactoryPersistableBase<PurchaseInvoiceServiceTaxLine>();
      fctLtl.setObjectClass(PurchaseInvoiceServiceTaxLine.class);
      fctLtl.setDatabaseId(getSrvDatabase().getIdDatabase());
      utlInvLn.setFctLineTxLn(fctLtl);
      //assigning fully initialized object:
      this.utlPurInvSrLn = utlInvLn;
    }
    return utlInvLn;
  }

  /**
   * <p>Get InvTxMeth<SalesInvoice, SalesInvoiceTaxLine>.</p>
   * @param pAddParam additional param
   * @return requested InvTxMeth<SalesInvoice, SalesInvoiceTaxLine>
   * @throws Exception - an exception
   */
  protected final InvTxMeth<SalesInvoice, SalesInvoiceTaxLine>
    lazyGetSalInvTxMeth(final Map<String, Object> pAddParam) throws Exception {
    InvTxMeth<SalesInvoice, SalesInvoiceTaxLine> salInvTxMe =
      this.salInvTxMeth;
    if (salInvTxMe == null) {
      salInvTxMe = new InvTxMeth<SalesInvoice, SalesInvoiceTaxLine>();
      salInvTxMe.setGoodLnCl(SalesInvoiceLine.class);
      salInvTxMe.setServiceLnCl(SalesInvoiceServiceLine.class);
      salInvTxMe.setInvTxLnCl(SalesInvoiceTaxLine.class);
      salInvTxMe.setIsTxByUser(true);
      salInvTxMe.setStWhereAdjGdLnInvBas(
        "where SALESINVOICELINE.TAXCATEGORY is not null and REVERSEDID "
          + "is null and ITSOWNER=");
      salInvTxMe.setStWhereAdjSrLnInvBas(
      "where SALESINVOICESERVICELINE.TAXCATEGORY is not null and REVERSEDID "
          + "is null and ITSOWNER=");
      salInvTxMe.setFlTotals("invTotals.sql");
      salInvTxMe.setFlTxItBas("invTxItBas.sql");
      salInvTxMe.setFlTxItBasAggr("invTxItBasAggr.sql");
      salInvTxMe.setFlTxInvBas("invTxInvBas.sql");
      salInvTxMe.setFlTxInvAdj("invTxInvAdj.sql");
      salInvTxMe.setFlTxInvBasAggr("invTxInvBasAggr.sql");
      salInvTxMe.setTblNmsTot(new String[] {"SALESINVOICELINE",
        "SALESINVOICESERVICELINE", "SALESINVOICETAXLINE",
           "SALESINVOICEGOODSTAXLINE", "SALESINVOICESERVICETAXLINE"});
      FactoryPersistableBase<SalesInvoiceTaxLine> fctItl =
        new FactoryPersistableBase<SalesInvoiceTaxLine>();
      fctItl.setObjectClass(SalesInvoiceTaxLine.class);
      fctItl.setDatabaseId(getSrvDatabase().getIdDatabase());
      salInvTxMe.setFctInvTxLn(fctItl);
      //assigning fully initialized object:
      this.salInvTxMeth = salInvTxMe;
    }
    return salInvTxMe;
  }

  /**
   * <p>Get sales invoice good line utility.</p>
   * @param pAddParam additional param
   * @return sales invoice good line utility
   * @throws Exception - an exception
   */
  protected final UtlInvLine<RS, SalesInvoice, SalesInvoiceLine,
    SalesInvoiceTaxLine, SalesInvoiceGoodsTaxLine> lazyGetUtlSalGdLn(
      final Map<String, Object> pAddParam) throws Exception {
    UtlInvLine<RS, SalesInvoice, SalesInvoiceLine,
      SalesInvoiceTaxLine, SalesInvoiceGoodsTaxLine> utlInvLn = this
        .utlSalInvGdLn;
    if (utlInvLn == null) {
      utlInvLn = new UtlInvLine<RS, SalesInvoice, SalesInvoiceLine,
        SalesInvoiceTaxLine, SalesInvoiceGoodsTaxLine>();
      utlInvLn.setUtlInvBase(lazyGetUtlInvBase(pAddParam));
      utlInvLn.setInvTxMeth(lazyGetSalInvTxMeth(pAddParam));
      utlInvLn.setIsMutable(false);
      utlInvLn.setNeedMkTxCat(true);
      utlInvLn.setLtlCl(SalesInvoiceGoodsTaxLine.class);
      utlInvLn.setDstTxItLnCl(DestTaxGoodsLn.class);
      FactoryPersistableBase<SalesInvoiceGoodsTaxLine> fctLtl =
        new FactoryPersistableBase<SalesInvoiceGoodsTaxLine>();
      fctLtl.setObjectClass(SalesInvoiceGoodsTaxLine.class);
      fctLtl.setDatabaseId(getSrvDatabase().getIdDatabase());
      utlInvLn.setFctLineTxLn(fctLtl);
      //assigning fully initialized object:
      this.utlSalInvGdLn = utlInvLn;
    }
    return utlInvLn;
  }

  /**
   * <p>Get sales invoice service line utility.</p>
   * @param pAddParam additional param
   * @return sales invoice service line utility
   * @throws Exception - an exception
   */
  protected final UtlInvLine<RS, SalesInvoice, SalesInvoiceServiceLine,
    SalesInvoiceTaxLine, SalesInvoiceServiceTaxLine> lazyGetUtlSalSrLn(
      final Map<String, Object> pAddParam) throws Exception {
    UtlInvLine<RS, SalesInvoice, SalesInvoiceServiceLine,
      SalesInvoiceTaxLine, SalesInvoiceServiceTaxLine> utlInvLn = this
        .utlSalInvSrLn;
    if (utlInvLn == null) {
      utlInvLn = new UtlInvLine<RS, SalesInvoice, SalesInvoiceServiceLine,
        SalesInvoiceTaxLine, SalesInvoiceServiceTaxLine>();
      utlInvLn.setUtlInvBase(lazyGetUtlInvBase(pAddParam));
      utlInvLn.setInvTxMeth(lazyGetSalInvTxMeth(pAddParam));
      utlInvLn.setIsMutable(true);
      utlInvLn.setNeedMkTxCat(true);
      utlInvLn.setLtlCl(SalesInvoiceServiceTaxLine.class);
      utlInvLn.setDstTxItLnCl(DestTaxServSelLn.class);
      FactoryPersistableBase<SalesInvoiceServiceTaxLine> fctLtl =
        new FactoryPersistableBase<SalesInvoiceServiceTaxLine>();
      fctLtl.setObjectClass(SalesInvoiceServiceTaxLine.class);
      fctLtl.setDatabaseId(getSrvDatabase().getIdDatabase());
      utlInvLn.setFctLineTxLn(fctLtl);
      //assigning fully initialized object:
      this.utlSalInvSrLn = utlInvLn;
    }
    return utlInvLn;
  }

  /**
   * <p>Get PrcSalesReturnSave (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcSalesReturnSave
   * @throws Exception - an exception
   */
  protected final PrcSalesReturnSave<RS> lazyGetPrcSalesReturnSave(
    final Map<String, Object> pAddParam) throws Exception {
    @SuppressWarnings("unchecked")
    PrcSalesReturnSave<RS> proc = (PrcSalesReturnSave<RS>)
      this.processorsMap.get(PrcSalesReturnSave.class.getSimpleName());
    if (proc == null) {
      proc = new PrcSalesReturnSave<RS>();
      proc.setSrvAccSettings(getSrvAccSettings());
      proc.setSrvAccEntry(getSrvAccEntry());
      proc.setSrvOrm(getSrvOrm());
      proc.setSrvI18n(getSrvI18n());
      proc.setSrvWarehouseEntry(getSrvWarehouseEntry());
      proc.setSrvCogsEntry(getSrvCogsEntry());
      proc.setSrvUseMaterialEntry(getSrvUseMaterialEntry());
      //assigning fully initialized object:
      this.processorsMap.put(PrcSalesReturnSave.class.getSimpleName(), proc);
    }
    return proc;
  }

  /**
   * <p>Get PrcGoodsLossSave (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcGoodsLossSave
   * @throws Exception - an exception
   */
  protected final PrcGoodsLossSave<RS> lazyGetPrcGoodsLossSave(
    final Map<String, Object> pAddParam) throws Exception {
    @SuppressWarnings("unchecked")
    PrcGoodsLossSave<RS> proc = (PrcGoodsLossSave<RS>)
      this.processorsMap.get(PrcGoodsLossSave.class.getSimpleName());
    if (proc == null) {
      proc = new PrcGoodsLossSave<RS>();
      proc.setSrvAccSettings(getSrvAccSettings());
      proc.setSrvAccEntry(getSrvAccEntry());
      proc.setSrvOrm(getSrvOrm());
      proc.setSrvI18n(getSrvI18n());
      proc.setSrvWarehouseEntry(getSrvWarehouseEntry());
      proc.setSrvCogsEntry(getSrvCogsEntry());
      //assigning fully initialized object:
      this.processorsMap.put(PrcGoodsLossSave.class.getSimpleName(), proc);
    }
    return proc;
  }

  /**
   * <p>Get PrcAccSettingsSave (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcAccSettingsSave
   * @throws Exception - an exception
   */
  protected final PrcAccSettingsSave<RS> lazyGetPrcAccSettingsSave(
    final Map<String, Object> pAddParam) throws Exception {
    @SuppressWarnings("unchecked")
    PrcAccSettingsSave<RS> proc = (PrcAccSettingsSave<RS>)
      this.processorsMap.get(PrcAccSettingsSave.class.getSimpleName());
    if (proc == null) {
      proc = new PrcAccSettingsSave<RS>();
      proc.setSrvAccSettings(getSrvAccSettings());
      //assigning fully initialized object:
      this.processorsMap.put(PrcAccSettingsSave.class.getSimpleName(), proc);
    }
    return proc;
  }

  /**
   * <p>Get PrcSalesInvoiceSave (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcSalesInvoiceSave
   * @throws Exception - an exception
   */
  protected final PrcSalesInvoiceSave<RS> lazyGetPrcSalesInvoiceSave(
    final Map<String, Object> pAddParam) throws Exception {
    @SuppressWarnings("unchecked")
    PrcSalesInvoiceSave<RS> proc = (PrcSalesInvoiceSave<RS>)
      this.processorsMap.get(PrcSalesInvoiceSave.class.getSimpleName());
    if (proc == null) {
      proc = new PrcSalesInvoiceSave<RS>();
      proc.setSrvAccSettings(getSrvAccSettings());
      proc.setSrvAccEntry(getSrvAccEntry());
      proc.setSrvOrm(getSrvOrm());
      proc.setSrvI18n(getSrvI18n());
      proc.setSrvWarehouseEntry(getSrvWarehouseEntry());
      proc.setSrvCogsEntry(getSrvCogsEntry());
      //assigning fully initialized object:
      this.processorsMap.put(PrcSalesInvoiceSave.class.getSimpleName(), proc);
    }
    return proc;
  }

  /**
   * <p>Get PrcManufactureSave (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcManufactureSave
   * @throws Exception - an exception
   */
  protected final PrcManufactureSave<RS> lazyGetPrcManufactureSave(
    final Map<String, Object> pAddParam) throws Exception {
    @SuppressWarnings("unchecked")
    PrcManufactureSave<RS> proc = (PrcManufactureSave<RS>)
      this.processorsMap.get(PrcManufactureSave.class.getSimpleName());
    if (proc == null) {
      proc = new PrcManufactureSave<RS>();
      proc.setSrvAccSettings(getSrvAccSettings());
      proc.setSrvAccEntry(getSrvAccEntry());
      proc.setSrvOrm(getSrvOrm());
      proc.setSrvI18n(getSrvI18n());
      proc.setSrvWarehouseEntry(getSrvWarehouseEntry());
      proc.setSrvUseMaterialEntry(getSrvUseMaterialEntry());
      //assigning fully initialized object:
      this.processorsMap.put(PrcManufactureSave.class.getSimpleName(), proc);
    }
    return proc;
  }

  /**
   * <p>Get PrcManufactureCopy (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcManufactureCopy
   * @throws Exception - an exception
   */
  protected final PrcManufactureCopy<RS> createPutPrcManufactureCopy(
    final Map<String, Object> pAddParam) throws Exception {
    PrcManufactureCopy<RS> proc = new PrcManufactureCopy<RS>();
    @SuppressWarnings("unchecked")
    IEntityProcessor<Manufacture, Long> procDlg =
      (IEntityProcessor<Manufacture, Long>) this.fctBnEntitiesProcessors
        .lazyGet(pAddParam, PrcEntityPbCopy.class.getSimpleName());
    proc.setPrcAccEntityPbCopy(procDlg);
    //assigning fully initialized object:
    this.processorsMap.put(PrcManufactureCopy.class.getSimpleName(), proc);
    return proc;
  }

  /**
   * <p>Get PrcManufactureGfr (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcManufactureGfr
   * @throws Exception - an exception
   */
  protected final PrcManufactureGfr<RS> createPutPrcManufactureGfr(
    final Map<String, Object> pAddParam) throws Exception {
    PrcManufactureGfr<RS> proc = new PrcManufactureGfr<RS>();
    @SuppressWarnings("unchecked")
    IEntityProcessor<Manufacture, Long> procDlg =
      (IEntityProcessor<Manufacture, Long>)
        lazyGetPrcAccDocGetForReverse(pAddParam);
    proc.setPrcAccDocGetForReverse(procDlg);
    //assigning fully initialized object:
    this.processorsMap.put(PrcManufactureGfr.class.getSimpleName(), proc);
    return proc;
  }

  /**
   * <p>Get PrcManufacturingProcessSave (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcManufacturingProcessSave
   * @throws Exception - an exception
   */
  protected final PrcManufacturingProcessSave<RS>
    lazyGetPrcManufacturingProcessSave(
      final Map<String, Object> pAddParam) throws Exception {
    @SuppressWarnings("unchecked")
    PrcManufacturingProcessSave<RS> proc = (PrcManufacturingProcessSave<RS>)
      this.processorsMap.get(PrcManufacturingProcessSave.class.getSimpleName());
    if (proc == null) {
      proc = new PrcManufacturingProcessSave<RS>();
      proc.setSrvAccEntry(getSrvAccEntry());
      proc.setSrvAccSettings(getSrvAccSettings());
      proc.setSrvOrm(getSrvOrm());
      proc.setSrvI18n(getSrvI18n());
      proc.setSrvWarehouseEntry(getSrvWarehouseEntry());
      proc.setSrvUseMaterialEntry(getSrvUseMaterialEntry());
      //assigning fully initialized object:
      this.processorsMap
        .put(PrcManufacturingProcessSave.class.getSimpleName(), proc);
    }
    return proc;
  }

  /**
   * <p>Get PrcManufacturingProcessGfr (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcManufacturingProcessGfr
   * @throws Exception - an exception
   */
  protected final PrcManufacturingProcessGfr<RS>
    createPutPrcManufacturingProcessGfr(
      final Map<String, Object> pAddParam) throws Exception {
    PrcManufacturingProcessGfr<RS> proc = new PrcManufacturingProcessGfr<RS>();
    @SuppressWarnings("unchecked")
    IEntityProcessor<ManufacturingProcess, Long> procDlg =
      (IEntityProcessor<ManufacturingProcess, Long>)
        lazyGetPrcAccDocGetForReverse(pAddParam);
    proc.setPrcAccDocGetForReverse(procDlg);
    //assigning fully initialized object:
    this.processorsMap
      .put(PrcManufacturingProcessGfr.class.getSimpleName(), proc);
    return proc;
  }

  /**
   * <p>Get PrcAdditionCostLineDelete (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcAdditionCostLineDelete
   * @throws Exception - an exception
   */
  protected final PrcAdditionCostLineDelete<RS>
    lazyGetPrcAdditionCostLineDelete(
      final Map<String, Object> pAddParam) throws Exception {
    @SuppressWarnings("unchecked")
    PrcAdditionCostLineDelete<RS> proc = (PrcAdditionCostLineDelete<RS>)
      this.processorsMap.get(PrcAdditionCostLineDelete.class.getSimpleName());
    if (proc == null) {
      proc = new PrcAdditionCostLineDelete<RS>();
      proc.setSrvAccSettings(getSrvAccSettings());
      proc.setSrvOrm(getSrvOrm());
      proc.setSrvDatabase(getSrvDatabase());
      //assigning fully initialized object:
      this.processorsMap
        .put(PrcAdditionCostLineDelete.class.getSimpleName(), proc);
    }
    return proc;
  }

  /**
   * <p>Get PrcBankStatementLineSave (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcBankStatementLineSave
   * @throws Exception - an exception
   */
  protected final PrcBankStatementLineSave<RS> lazyGetPrcBankStatementLineSave(
    final Map<String, Object> pAddParam) throws Exception {
    @SuppressWarnings("unchecked")
    PrcBankStatementLineSave<RS> proc = (PrcBankStatementLineSave<RS>)
      this.processorsMap.get(PrcBankStatementLineSave.class.getSimpleName());
    if (proc == null) {
      proc = new PrcBankStatementLineSave<RS>();
      proc.setSrvOrm(getSrvOrm());
      proc.setSrvI18n(getSrvI18n());
      proc.setSrvAccEntry(getSrvAccEntry());
      proc.setPrcPurchaseInvoiceSave(lazyGetPrcPurchaseInvoiceSave(pAddParam));
      proc.setPrcSalesInvoiceSave(lazyGetPrcSalesInvoiceSave(pAddParam));
      //assigning fully initialized object:
      this.processorsMap
        .put(PrcBankStatementLineSave.class.getSimpleName(), proc);
    }
    return proc;
  }

  /**
   * <p>Get PrcInvoiceLnGfe (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcInvoiceLnGfe
   * @throws Exception - an exception
   */
  protected final PrcInvoiceLnGfe<RS, IInvoiceLine<IInvoice>, IInvoice>
    lazyGetPrcInvoiceLnGfe(
      final Map<String, Object> pAddParam) throws Exception {
    @SuppressWarnings("unchecked")
    PrcInvoiceLnGfe<RS, IInvoiceLine<IInvoice>, IInvoice> proc =
      (PrcInvoiceLnGfe<RS, IInvoiceLine<IInvoice>, IInvoice>)
        this.processorsMap.get(PrcInvoiceLnGfe.class.getSimpleName());
    if (proc == null) {
      proc = new PrcInvoiceLnGfe<RS, IInvoiceLine<IInvoice>, IInvoice>();
      @SuppressWarnings("unchecked")
      PrcEntityPbEditDelete<RS, IInvoiceLine<IInvoice>> procDlg =
    (PrcEntityPbEditDelete<RS, IInvoiceLine<IInvoice>>) this
  .fctBnEntitiesProcessors
.lazyGet(pAddParam, PrcEntityPbEditDelete.class.getSimpleName());
      proc.setPrcEntityPbEditDelete(procDlg);
      //assigning fully initialized object:
      this.processorsMap
        .put(PrcInvoiceLnGfe.class.getSimpleName(), proc);
    }
    return proc;
  }

  /**
   * <p>Get PrcSalesRetGfe (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcSalesRetGfe
   * @throws Exception - an exception
   */
  protected final PrcSalesRetGfe lazyGetPrcSalesRetGfe(
    final Map<String, Object> pAddParam) throws Exception {
    PrcSalesRetGfe proc = (PrcSalesRetGfe)
      this.processorsMap.get(PrcSalesRetGfe.class.getSimpleName());
    if (proc == null) {
      proc = new PrcSalesRetGfe();
      @SuppressWarnings("unchecked")
      PrcEntityPbEditDelete<RS, SalesReturn> procDlg =
    (PrcEntityPbEditDelete<RS, SalesReturn>) this.fctBnEntitiesProcessors
  .lazyGet(pAddParam, PrcEntityPbEditDelete.class.getSimpleName());
      proc.setPrcEntityPbEditDelete(procDlg);
      //assigning fully initialized object:
      this.processorsMap
        .put(PrcSalesRetGfe.class.getSimpleName(), proc);
    }
    return proc;
  }

  /**
   * <p>Get PrcPurchRetGfe (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcPurchRetGfe
   * @throws Exception - an exception
   */
  protected final PrcPurchRetGfe lazyGetPrcPurchRetGfe(
    final Map<String, Object> pAddParam) throws Exception {
    PrcPurchRetGfe proc = (PrcPurchRetGfe)
      this.processorsMap.get(PrcPurchRetGfe.class.getSimpleName());
    if (proc == null) {
      proc = new PrcPurchRetGfe();
      @SuppressWarnings("unchecked")
      PrcEntityPbEditDelete<RS, PurchaseReturn> procDlg =
    (PrcEntityPbEditDelete<RS, PurchaseReturn>) this.fctBnEntitiesProcessors
  .lazyGet(pAddParam, PrcEntityPbEditDelete.class.getSimpleName());
      proc.setPrcEntityPbEditDelete(procDlg);
      //assigning fully initialized object:
      this.processorsMap
        .put(PrcPurchRetGfe.class.getSimpleName(), proc);
    }
    return proc;
  }

  /**
   * <p>Get PrcInvoiceGfe (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcInvoiceGfe
   * @throws Exception - an exception
   */
  protected final PrcInvoiceGfe<IInvoice> lazyGetPrcInvoiceGfe(
    final Map<String, Object> pAddParam) throws Exception {
    @SuppressWarnings("unchecked")
    PrcInvoiceGfe<IInvoice> proc = (PrcInvoiceGfe<IInvoice>)
      this.processorsMap.get(PrcInvoiceGfe.class.getSimpleName());
    if (proc == null) {
      proc = new PrcInvoiceGfe<IInvoice>();
      @SuppressWarnings("unchecked")
      PrcEntityPbEditDelete<RS, IInvoice> procDlg =
    (PrcEntityPbEditDelete<RS, IInvoice>) this.fctBnEntitiesProcessors
  .lazyGet(pAddParam, PrcEntityPbEditDelete.class.getSimpleName());
      proc.setPrcEntityPbEditDelete(procDlg);
      //assigning fully initialized object:
      this.processorsMap
        .put(PrcInvoiceGfe.class.getSimpleName(), proc);
    }
    return proc;
  }

  /**
   * <p>Get PrcBankStatementLineGfe (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcBankStatementLineGfe
   * @throws Exception - an exception
   */
  protected final PrcBankStatementLineGfe<RS> lazyGetPrcBankStatementLineGfe(
    final Map<String, Object> pAddParam) throws Exception {
    @SuppressWarnings("unchecked")
    PrcBankStatementLineGfe<RS> proc = (PrcBankStatementLineGfe<RS>)
      this.processorsMap.get(PrcBankStatementLineGfe.class.getSimpleName());
    if (proc == null) {
      proc = new PrcBankStatementLineGfe<RS>();
      proc.setSrvOrm(getSrvOrm());
      proc.setSrvTypeCode(getSrvTypeCode());
      @SuppressWarnings("unchecked")
      PrcEntityPbEditDelete<RS, BankStatementLine> procDlg =
    (PrcEntityPbEditDelete<RS, BankStatementLine>) this.fctBnEntitiesProcessors
  .lazyGet(pAddParam, PrcEntityPbEditDelete.class.getSimpleName());
      proc.setPrcEntityPbEditDelete(procDlg);
      //assigning fully initialized object:
      this.processorsMap
        .put(PrcBankStatementLineGfe.class.getSimpleName(), proc);
    }
    return proc;
  }

  /**
   * <p>Get PrcBankStatementSave (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcBankStatementSave
   * @throws Exception - an exception
   */
  protected final PrcBankStatementSave<RS> lazyGetPrcBankStatementSave(
    final Map<String, Object> pAddParam) throws Exception {
    @SuppressWarnings("unchecked")
    PrcBankStatementSave<RS> proc = (PrcBankStatementSave<RS>)
      this.processorsMap.get(PrcBankStatementSave.class.getSimpleName());
    if (proc == null) {
      proc = new PrcBankStatementSave<RS>();
      proc.setSrvOrm(getSrvOrm());
      proc.setCsvReader(getCsvReader());
      //assigning fully initialized object:
      this.processorsMap.put(PrcBankStatementSave.class.getSimpleName(), proc);
    }
    return proc;
  }

  /**
   * <p>Get PrcAdditionCostLineSave (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcAdditionCostLineSave
   * @throws Exception - an exception
   */
  protected final PrcAdditionCostLineSave<RS> lazyGetPrcAdditionCostLineSave(
    final Map<String, Object> pAddParam) throws Exception {
    @SuppressWarnings("unchecked")
    PrcAdditionCostLineSave<RS> proc = (PrcAdditionCostLineSave<RS>)
      this.processorsMap.get(PrcAdditionCostLineSave.class.getSimpleName());
    if (proc == null) {
      proc = new PrcAdditionCostLineSave<RS>();
      proc.setSrvAccSettings(getSrvAccSettings());
      proc.setSrvOrm(getSrvOrm());
      proc.setSrvDatabase(getSrvDatabase());
      //assigning fully initialized object:
      this.processorsMap
        .put(PrcAdditionCostLineSave.class.getSimpleName(), proc);
    }
    return proc;
  }

  /**
   * <p>Get PrcInvItemTaxCategoryLineDelete (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcInvItemTaxCategoryLineDelete
   * @throws Exception - an exception
   */
  protected final PrcInvItemTaxCategoryLineDelete<RS>
    lazyGetPrcInvItemTaxCategoryLineDelete(
      final Map<String, Object> pAddParam) throws Exception {
    @SuppressWarnings("unchecked")
    PrcInvItemTaxCategoryLineDelete<RS> proc =
      (PrcInvItemTaxCategoryLineDelete<RS>) this.processorsMap
        .get(PrcInvItemTaxCategoryLineDelete.class.getSimpleName());
    if (proc == null) {
      proc = new PrcInvItemTaxCategoryLineDelete<RS>();
      proc.setSrvOrm(getSrvOrm());
      //assigning fully initialized object:
      this.processorsMap
        .put(PrcInvItemTaxCategoryLineDelete.class.getSimpleName(), proc);
    }
    return proc;
  }

  /**
   * <p>Get PrcInvItemTaxCategoryLineSave (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcInvItemTaxCategoryLineSave
   * @throws Exception - an exception
   */
  protected final PrcInvItemTaxCategoryLineSave<RS>
    lazyGetPrcInvItemTaxCategoryLineSave(
      final Map<String, Object> pAddParam) throws Exception {
    @SuppressWarnings("unchecked")
    PrcInvItemTaxCategoryLineSave<RS> proc = (PrcInvItemTaxCategoryLineSave<RS>)
  this.processorsMap.get(PrcInvItemTaxCategoryLineSave.class.getSimpleName());
    if (proc == null) {
      proc = new PrcInvItemTaxCategoryLineSave<RS>();
      proc.setSrvOrm(getSrvOrm());
      //assigning fully initialized object:
      this.processorsMap
        .put(PrcInvItemTaxCategoryLineSave.class.getSimpleName(), proc);
    }
    return proc;
  }

  /**
   * <p>Get PrcPurchaseReturnSave (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcPurchaseReturnSave
   * @throws Exception - an exception
   */
  protected final PrcPurchaseReturnSave<RS> lazyGetPrcPurchaseReturnSave(
    final Map<String, Object> pAddParam) throws Exception {
    @SuppressWarnings("unchecked")
    PrcPurchaseReturnSave<RS> proc = (PrcPurchaseReturnSave<RS>)
      this.processorsMap.get(PrcPurchaseReturnSave.class.getSimpleName());
    if (proc == null) {
      proc = new PrcPurchaseReturnSave<RS>();
      proc.setSrvAccSettings(getSrvAccSettings());
      proc.setSrvAccEntry(getSrvAccEntry());
      proc.setSrvOrm(getSrvOrm());
      proc.setSrvI18n(getSrvI18n());
      proc.setSrvWarehouseEntry(getSrvWarehouseEntry());
      proc.setSrvUseMaterialEntry(getSrvUseMaterialEntry());
      //assigning fully initialized object:
      this.processorsMap.put(PrcPurchaseReturnSave.class.getSimpleName(), proc);
    }
    return proc;
  }

  /**
   * <p>Get PrcBeginningInventorySave (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcBeginningInventorySave
   * @throws Exception - an exception
   */
  protected final PrcBeginningInventorySave<RS>
    lazyGetPrcBeginningInventorySave(
      final Map<String, Object> pAddParam) throws Exception {
    @SuppressWarnings("unchecked")
    PrcBeginningInventorySave<RS> proc = (PrcBeginningInventorySave<RS>)
      this.processorsMap.get(PrcBeginningInventorySave.class.getSimpleName());
    if (proc == null) {
      proc = new PrcBeginningInventorySave<RS>();
      proc.setSrvAccSettings(getSrvAccSettings());
      proc.setSrvAccEntry(getSrvAccEntry());
      proc.setSrvOrm(getSrvOrm());
      proc.setSrvI18n(getSrvI18n());
      proc.setSrvWarehouseEntry(getSrvWarehouseEntry());
      proc.setSrvCogsEntry(getSrvCogsEntry());
      proc.setSrvUseMaterialEntry(getSrvUseMaterialEntry());
      //assigning fully initialized object:
      this.processorsMap
        .put(PrcBeginningInventorySave.class.getSimpleName(), proc);
    }
    return proc;
  }

  /**
   * <p>Get PrcPurchaseInvoiceSave (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcPurchaseInvoiceSave
   * @throws Exception - an exception
   */
  protected final PrcPurchaseInvoiceSave<RS> lazyGetPrcPurchaseInvoiceSave(
    final Map<String, Object> pAddParam) throws Exception {
    @SuppressWarnings("unchecked")
    PrcPurchaseInvoiceSave<RS> proc = (PrcPurchaseInvoiceSave<RS>)
      this.processorsMap.get(PrcPurchaseInvoiceSave.class.getSimpleName());
    if (proc == null) {
      proc = new PrcPurchaseInvoiceSave<RS>();
      proc.setSrvAccSettings(getSrvAccSettings());
      proc.setSrvAccEntry(getSrvAccEntry());
      proc.setSrvOrm(getSrvOrm());
      proc.setSrvI18n(getSrvI18n());
      proc.setSrvWarehouseEntry(getSrvWarehouseEntry());
      proc.setSrvCogsEntry(getSrvCogsEntry());
      proc.setSrvUseMaterialEntry(getSrvUseMaterialEntry());
      //assigning fully initialized object:
      this.processorsMap
        .put(PrcPurchaseInvoiceSave.class.getSimpleName(), proc);
    }
    return proc;
  }

  /**
   * <p>Get PrcPrepaymentFromSave (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcPrepaymentFromSave
   * @throws Exception - an exception
   */
  protected final PrcPrepaymentFromSave<RS> createPutPrcPrepaymentFromSave(
    final Map<String, Object> pAddParam) throws Exception {
    PrcPrepaymentFromSave<RS> proc = new PrcPrepaymentFromSave<RS>();
    proc.setSrvAccSettings(getSrvAccSettings());
    proc.setSrvAccEntry(getSrvAccEntry());
    proc.setSrvOrm(getSrvOrm());
    proc.setSrvI18n(getSrvI18n());
    //assigning fully initialized object:
    this.processorsMap.put(PrcPrepaymentFromSave.class.getSimpleName(), proc);
    return proc;
  }

  /**
   * <p>Get PrcPrepaymentFromGfr (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcPrepaymentFromGfr
   * @throws Exception - an exception
   */
  protected final PrcPrepaymentFromGfr<RS> createPutPrcPrepaymentFromGfr(
    final Map<String, Object> pAddParam) throws Exception {
    PrcPrepaymentFromGfr<RS> proc = new PrcPrepaymentFromGfr<RS>();
    @SuppressWarnings("unchecked")
    IEntityProcessor<PrepaymentFrom, Long> procDlg =
      (IEntityProcessor<PrepaymentFrom, Long>)
        lazyGetPrcAccDocGetForReverse(pAddParam);
    proc.setPrcAccDocGetForReverse(procDlg);
    proc.setSrvTypeCode(getSrvTypeCode());
    //assigning fully initialized object:
    this.processorsMap.put(PrcPrepaymentFromGfr.class.getSimpleName(), proc);
    return proc;
  }

  /**
   * <p>Get PrcPrepaymentFromCopy (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcPrepaymentFromCopy
   * @throws Exception - an exception
   */
  protected final PrcPrepaymentFromCopy<RS> createPutPrcPrepaymentFromCopy(
    final Map<String, Object> pAddParam) throws Exception {
    PrcPrepaymentFromCopy<RS> proc = new PrcPrepaymentFromCopy<RS>();
    @SuppressWarnings("unchecked")
    IEntityProcessor<PrepaymentFrom, Long> procDlg =
      (IEntityProcessor<PrepaymentFrom, Long>)
        lazyGetPrcAccEntityPbWithSubaccCopy(pAddParam);
    proc.setPrcAccEntityPbWithSubaccCopy(procDlg);
    proc.setSrvTypeCode(getSrvTypeCode());
    //assigning fully initialized object:
    this.processorsMap.put(PrcPrepaymentFromCopy.class.getSimpleName(), proc);
    return proc;
  }

  /**
   * <p>Get PrcPrepaymentToSave (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcPrepaymentToSave
   * @throws Exception - an exception
   */
  protected final PrcPrepaymentToSave<RS> createPutPrcPrepaymentToSave(
    final Map<String, Object> pAddParam) throws Exception {
    PrcPrepaymentToSave<RS> proc = new PrcPrepaymentToSave<RS>();
    proc.setSrvAccSettings(getSrvAccSettings());
    proc.setSrvAccEntry(getSrvAccEntry());
    proc.setSrvOrm(getSrvOrm());
    proc.setSrvI18n(getSrvI18n());
    //assigning fully initialized object:
    this.processorsMap.put(PrcPrepaymentToSave.class.getSimpleName(), proc);
    return proc;
  }

  /**
   * <p>Get PrcPrepaymentToGfr (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcPrepaymentToGfr
   * @throws Exception - an exception
   */
  protected final PrcPrepaymentToGfr<RS> createPutPrcPrepaymentToGfr(
    final Map<String, Object> pAddParam) throws Exception {
    PrcPrepaymentToGfr<RS> proc = new PrcPrepaymentToGfr<RS>();
    @SuppressWarnings("unchecked")
    IEntityProcessor<PrepaymentTo, Long> procDlg =
      (IEntityProcessor<PrepaymentTo, Long>)
        lazyGetPrcAccDocGetForReverse(pAddParam);
    proc.setPrcAccDocGetForReverse(procDlg);
    proc.setSrvTypeCode(getSrvTypeCode());
    //assigning fully initialized object:
    this.processorsMap.put(PrcPrepaymentToGfr.class.getSimpleName(), proc);
    return proc;
  }

  /**
   * <p>Get PrcPrepaymentToCopy (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcPrepaymentToCopy
   * @throws Exception - an exception
   */
  protected final PrcPrepaymentToCopy<RS> createPutPrcPrepaymentToCopy(
    final Map<String, Object> pAddParam) throws Exception {
    PrcPrepaymentToCopy<RS> proc = new PrcPrepaymentToCopy<RS>();
    @SuppressWarnings("unchecked")
    IEntityProcessor<PrepaymentTo, Long> procDlg =
      (IEntityProcessor<PrepaymentTo, Long>)
        lazyGetPrcAccEntityPbWithSubaccCopy(pAddParam);
    proc.setPrcAccEntityPbWithSubaccCopy(procDlg);
    proc.setSrvTypeCode(getSrvTypeCode());
    //assigning fully initialized object:
    this.processorsMap.put(PrcPrepaymentToCopy.class.getSimpleName(), proc);
    return proc;
  }

  /**
   * <p>Get PrcPaymentFromSave (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcPaymentFromSave
   * @throws Exception - an exception
   */
  protected final PrcPaymentFromSave<RS> createPutPrcPaymentFromSave(
      final Map<String, Object> pAddParam) throws Exception {
    PrcPaymentFromSave<RS> proc = new PrcPaymentFromSave<RS>();
    proc.setSrvAccSettings(getSrvAccSettings());
    proc.setSrvAccEntry(getSrvAccEntry());
    proc.setSrvOrm(getSrvOrm());
    proc.setSrvI18n(getSrvI18n());
    proc.setPrcSalesInvoiceSave(lazyGetPrcSalesInvoiceSave(pAddParam));
    //assigning fully initialized object:
    this.processorsMap.put(PrcPaymentFromSave.class.getSimpleName(), proc);
    return proc;
  }

  /**
   * <p>Get PrcPaymentFromGfr (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcPaymentFromGfr
   * @throws Exception - an exception
   */
  protected final PrcPaymentFromGfr<RS> createPutPrcPaymentFromGfr(
    final Map<String, Object> pAddParam) throws Exception {
    PrcPaymentFromGfr<RS> proc = new PrcPaymentFromGfr<RS>();
    @SuppressWarnings("unchecked")
    IEntityProcessor<PaymentFrom, Long> procDlg =
      (IEntityProcessor<PaymentFrom, Long>)
        lazyGetPrcAccDocGetForReverse(pAddParam);
    proc.setPrcAccDocGetForReverse(procDlg);
    proc.setSrvTypeCode(getSrvTypeCode());
    //assigning fully initialized object:
    this.processorsMap.put(PrcPaymentFromGfr.class.getSimpleName(), proc);
    return proc;
  }

  /**
   * <p>Get PrcPaymentFromCopy (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcPaymentFromCopy
   * @throws Exception - an exception
   */
  protected final PrcPaymentFromCopy<RS> createPutPrcPaymentFromCopy(
    final Map<String, Object> pAddParam) throws Exception {
    PrcPaymentFromCopy<RS> proc = new PrcPaymentFromCopy<RS>();
    @SuppressWarnings("unchecked")
    IEntityProcessor<PaymentFrom, Long> procDlg =
      (IEntityProcessor<PaymentFrom, Long>)
        lazyGetPrcAccEntityPbWithSubaccCopy(pAddParam);
    proc.setPrcAccEntityPbWithSubaccCopy(procDlg);
    //assigning fully initialized object:
    this.processorsMap.put(PrcPaymentFromCopy.class.getSimpleName(), proc);
    return proc;
  }

  /**
   * <p>Get PrcWageSave (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcWageSave
   * @throws Exception - an exception
   */
  protected final PrcWageSave<RS> createPutPrcWageSave(
    final Map<String, Object> pAddParam) throws Exception {
    PrcWageSave<RS> proc = new PrcWageSave<RS>();
    proc.setSrvAccSettings(getSrvAccSettings());
    proc.setSrvAccEntry(getSrvAccEntry());
    proc.setSrvOrm(getSrvOrm());
    proc.setSrvI18n(getSrvI18n());
    proc.setFactoryAppBeans(getFactoryAppBeans());
    //assigning fully initialized object:
    this.processorsMap.put(PrcWageSave.class.getSimpleName(), proc);
    return proc;
  }

  /**
   * <p>Get PrcWageGfr (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcWageGfr
   * @throws Exception - an exception
   */
  protected final PrcWageGfr<RS> createPutPrcWageGfr(
    final Map<String, Object> pAddParam) throws Exception {
    PrcWageGfr<RS> proc = new PrcWageGfr<RS>();
    @SuppressWarnings("unchecked")
    IEntityProcessor<Wage, Long> procDlg = (IEntityProcessor<Wage, Long>)
      lazyGetPrcAccDocGetForReverse(pAddParam);
    proc.setPrcAccDocGetForReverse(procDlg);
    //assigning fully initialized object:
    this.processorsMap.put(PrcWageGfr.class.getSimpleName(), proc);
    return proc;
  }

  /**
   * <p>Get PrcWageCopy (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcWageCopy
   * @throws Exception - an exception
   */
  protected final PrcWageCopy<RS> createPutPrcWageCopy(
    final Map<String, Object> pAddParam) throws Exception {
    PrcWageCopy<RS> proc = new PrcWageCopy<RS>();
    @SuppressWarnings("unchecked")
    IEntityProcessor<Wage, Long> procDlg = (IEntityProcessor<Wage, Long>)
      this.fctBnEntitiesProcessors
        .lazyGet(pAddParam, PrcEntityPbCopy.class.getSimpleName());
    proc.setPrcAccEntityPbCopy(procDlg);
    //assigning fully initialized object:
    this.processorsMap.put(PrcWageCopy.class.getSimpleName(), proc);
    return proc;
  }

  /**
   * <p>Get PrcPaymentToSave (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcPaymentToSave
   * @throws Exception - an exception
   */
  protected final PrcPaymentToSave<RS> createPutPrcPaymentToSave(
    final Map<String, Object> pAddParam) throws Exception {
    PrcPaymentToSave<RS> proc = new PrcPaymentToSave<RS>();
    proc.setSrvAccSettings(getSrvAccSettings());
    proc.setSrvAccEntry(getSrvAccEntry());
    proc.setSrvOrm(getSrvOrm());
    proc.setSrvI18n(getSrvI18n());
    proc.setPrcPurchaseInvoiceSave(lazyGetPrcPurchaseInvoiceSave(pAddParam));
    //assigning fully initialized object:
    this.processorsMap.put(PrcPaymentToSave.class.getSimpleName(), proc);
    return proc;
  }

  /**
   * <p>Get PrcPaymentToGfr (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcPaymentToGfr
   * @throws Exception - an exception
   */
  protected final PrcPaymentToGfr<RS> createPutPrcPaymentToGfr(
    final Map<String, Object> pAddParam) throws Exception {
    PrcPaymentToGfr<RS> proc = new PrcPaymentToGfr<RS>();
    @SuppressWarnings("unchecked")
    IEntityProcessor<PaymentTo, Long> procDlg =
      (IEntityProcessor<PaymentTo, Long>)
        lazyGetPrcAccDocGetForReverse(pAddParam);
    proc.setPrcAccDocGetForReverse(procDlg);
    proc.setSrvTypeCode(getSrvTypeCode());
    //assigning fully initialized object:
    this.processorsMap.put(PrcPaymentToGfr.class.getSimpleName(), proc);
    return proc;
  }

  /**
   * <p>Get PrcPaymentToCopy (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcPaymentToCopy
   * @throws Exception - an exception
   */
  protected final PrcPaymentToCopy<RS> createPutPrcPaymentToCopy(
    final Map<String, Object> pAddParam) throws Exception {
    PrcPaymentToCopy<RS> proc = new PrcPaymentToCopy<RS>();
    @SuppressWarnings("unchecked")
    IEntityProcessor<PaymentTo, Long> procDlg =
      (IEntityProcessor<PaymentTo, Long>)
        lazyGetPrcAccEntityPbWithSubaccCopy(pAddParam);
    proc.setPrcAccEntityPbWithSubaccCopy(procDlg);
    //assigning fully initialized object:
    this.processorsMap.put(PrcPaymentToCopy.class.getSimpleName(), proc);
    return proc;
  }

  /**
   * <p>Get PrcAccEntityPbWithSubaccEditDelete (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcAccEntityPbWithSubaccEditDelete
   * @throws Exception - an exception
   */
  protected final PrcAccEntityPbWithSubaccEditDelete<RS, IPersistableBase>
    createPutPrcAccEntityPbWithSubaccEditDelete(
      final Map<String, Object> pAddParam) throws Exception {
    PrcAccEntityPbWithSubaccEditDelete<RS, IPersistableBase> proc =
      new PrcAccEntityPbWithSubaccEditDelete<RS, IPersistableBase>();
    @SuppressWarnings("unchecked")
    PrcEntityPbEditDelete<RS, IPersistableBase> procDlg =
      (PrcEntityPbEditDelete<RS, IPersistableBase>) this.fctBnEntitiesProcessors
        .lazyGet(pAddParam, PrcEntityPbEditDelete.class.getSimpleName());
    proc.setPrcAccEntityPbEditDelete(procDlg);
    proc.setSrvTypeCode(getSrvTypeCode());
    //assigning fully initialized object:
    this.processorsMap
      .put(PrcAccEntityPbWithSubaccEditDelete.class.getSimpleName(), proc);
    return proc;
  }

  /**
   * <p>Get PrcAccEntrySave (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcAccEntrySave
   * @throws Exception - an exception
   */
  protected final PrcAccEntrySave lazyGetPrcAccEntrySave(
    final Map<String, Object> pAddParam) throws Exception {
    @SuppressWarnings("unchecked")
    PrcAccEntrySave<RS> proc = (PrcAccEntrySave<RS>)
      this.processorsMap.get(PrcAccEntrySave.class.getSimpleName());
    if (proc == null) {
      proc = new PrcAccEntrySave<RS>();
      proc.setSrvAccSettings(getSrvAccSettings());
      proc.setSrvOrm(getSrvOrm());
      proc.setSrvDatabase(getSrvDatabase());
      proc.setSrvBalance(getSrvBalance());
      //assigning fully initialized object:
      this.processorsMap.put(PrcAccEntrySave.class.getSimpleName(), proc);
    }
    return proc;
  }

  /**
   * <p>Get PrcAccEntryCopy (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcAccEntryCopy
   * @throws Exception - an exception
   */
  protected final PrcAccEntryCopy lazyGetPrcAccEntryCopy(
    final Map<String, Object> pAddParam) throws Exception {
    @SuppressWarnings("unchecked")
    PrcAccEntryCopy<RS> proc = (PrcAccEntryCopy<RS>)
      this.processorsMap.get(PrcAccEntryCopy.class.getSimpleName());
    if (proc == null) {
      proc = new PrcAccEntryCopy<RS>();
      proc.setSrvOrm(getSrvOrm());
      proc.setMngUvdSettings(getMngUvdSettings());
      proc.setSrvDate(getSrvDate());
      proc.setSrvI18n(getSrvI18n());
      proc.setConvertersFieldsFatory(getConvertersFieldsFatory());
      proc.setFieldConverterNamesHolder(getFieldConverterNamesHolder());
      //assigning fully initialized object:
      this.processorsMap.put(PrcAccEntryCopy.class.getSimpleName(), proc);
    }
    return proc;
  }

  /**
   * <p>Get PrcAccEntryCreate (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcAccEntryCreate
   * @throws Exception - an exception
   */
  protected final PrcAccEntryCreate lazyGetPrcAccEntryCreate(
    final Map<String, Object> pAddParam) throws Exception {
    @SuppressWarnings("unchecked")
    PrcAccEntryCreate<RS> proc = (PrcAccEntryCreate<RS>) this.processorsMap
      .get(PrcAccEntryCreate.class.getSimpleName());
    if (proc == null) {
      proc = new PrcAccEntryCreate<RS>();
      proc.setSrvOrm(getSrvOrm());
      proc.setMngUvdSettings(getMngUvdSettings());
      proc.setSrvDate(getSrvDate());
      proc.setSrvI18n(getSrvI18n());
      proc.setConvertersFieldsFatory(getConvertersFieldsFatory());
      proc.setFieldConverterNamesHolder(getFieldConverterNamesHolder());
      //assigning fully initialized object:
      this.processorsMap.put(PrcAccEntryCreate.class.getSimpleName(), proc);
    }
    return proc;
  }

  /**
   * <p>Get PrcInpAccEntriesRetrieve (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcInpAccEntriesRetrieve
   * @throws Exception - an exception
   */
  protected final PrcInpAccEntriesRetrieve lazyGetPrcInpAccEntriesRetrieve(
    final Map<String, Object> pAddParam) throws Exception {
    @SuppressWarnings("unchecked")
    PrcInpAccEntriesRetrieve<RS> proc =
      (PrcInpAccEntriesRetrieve<RS>) this.processorsMap
        .get(PrcInpAccEntriesRetrieve.class.getSimpleName());
    if (proc == null) {
      proc = new PrcInpAccEntriesRetrieve<RS>();
      proc.setSrvOrm(getSrvOrm());
      proc.setMngUvdSettings(getMngUvdSettings());
      proc.setSrvDate(getSrvDate());
      proc.setConvertersFieldsFatory(getConvertersFieldsFatory());
      proc.setFieldConverterNamesHolder(getFieldConverterNamesHolder());
      //assigning fully initialized object:
      this.processorsMap
        .put(PrcInpAccEntriesRetrieve.class.getSimpleName(), proc);
    }
    return proc;
  }

  /**
   * <p>Get PrcAccEntityWithSubaccRetrieve (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcAccEntityWithSubaccRetrieve
   * @throws Exception - an exception
   */
  protected final PrcAccEntityWithSubaccRetrieve
    lazyGetPrcAccEntityWithSubaccRetrieve(
      final Map<String, Object> pAddParam) throws Exception {
    @SuppressWarnings("unchecked")
    PrcAccEntityWithSubaccRetrieve<RS, IHasId<Object>, Object> proc =
      (PrcAccEntityWithSubaccRetrieve<RS, IHasId<Object>, Object>)
        this.processorsMap
          .get(PrcAccEntityWithSubaccRetrieve.class.getSimpleName());
    if (proc == null) {
      proc = new PrcAccEntityWithSubaccRetrieve<RS, IHasId<Object>, Object>();
      @SuppressWarnings("unchecked")
      IEntityProcessor<IHasId<Object>, Object> procDlg =
        (IEntityProcessor<IHasId<Object>, Object>) this.fctBnEntitiesProcessors
          .lazyGet(pAddParam, PrcEntityRetrieve.class.getSimpleName());
      proc.setPrcAccEntityRetrieve(procDlg);
      proc.setSrvTypeCode(getSrvTypeCode());
      //assigning fully initialized object:
      this.processorsMap
        .put(PrcAccEntityWithSubaccRetrieve.class.getSimpleName(), proc);
    }
    return proc;
  }

  /**
   * <p>Get PrcAccDocFullRetrieve (create and put into map).</p>
   * @param pAddParam additional param
   * @return requested PrcAccDocFullRetrieve
   * @throws Exception - an exception
   */
  protected final PrcAccDocFullRetrieve createPutPrcAccDocFullRetrieve(
    final Map<String, Object> pAddParam) throws Exception {
    PrcAccDocFullRetrieve<RS, IDocWarehouse> proc =
      new PrcAccDocFullRetrieve<RS, IDocWarehouse>();
    proc.setSrvUseMaterialEntry(getSrvUseMaterialEntry());
    @SuppressWarnings("unchecked")
    IEntityProcessor<IDocWarehouse, Long> delegate =
      (IEntityProcessor<IDocWarehouse, Long>)
        lazyGetPrcAccDocCogsRetrieve(pAddParam);
    proc.setPrcAccDocCogsRetrieve(delegate);
    //assigning fully initialized object:
    this.processorsMap.put(PrcAccDocFullRetrieve.class.getSimpleName(), proc);
    return proc;
  }

  /**
   * <p>Lazy get PrcMoveItemsRetrieve.</p>
   * @param pAddParam additional param
   * @return requested PrcMoveItemsRetrieve
   * @throws Exception - an exception
   */
  protected final PrcMoveItemsRetrieve lazyGetPrcMoveItemsRetrieve(
    final Map<String, Object> pAddParam) throws Exception {
    @SuppressWarnings("unchecked")
    PrcMoveItemsRetrieve<RS> proc = (PrcMoveItemsRetrieve<RS>)
      this.processorsMap.get(PrcMoveItemsRetrieve.class.getSimpleName());
    if (proc == null) {
      proc = new PrcMoveItemsRetrieve<RS>();
      proc.setSrvWarehouseEntry(getSrvWarehouseEntry());
      @SuppressWarnings("unchecked")
      IEntityProcessor<MoveItems, Long> delegate =
        (IEntityProcessor<MoveItems, Long>)
          this.fctBnEntitiesProcessors
            .lazyGet(pAddParam, PrcEntityRetrieve.class.getSimpleName());
      proc.setPrcAccEntityRetrieve(delegate);
      //assigning fully initialized object:
      this.processorsMap.put(PrcMoveItemsRetrieve.class.getSimpleName(), proc);
    }
    return proc;
  }

  /**
   * <p>Lazy get PrcAccDocUseMaterialRetrieve.</p>
   * @param pAddParam additional param
   * @return requested PrcAccDocUseMaterialRetrieve
   * @throws Exception - an exception
   */
  protected final PrcAccDocUseMaterialRetrieve
    lazyGetPrcAccDocUseMaterialRetrieve(
      final Map<String, Object> pAddParam) throws Exception {
    @SuppressWarnings("unchecked")
    PrcAccDocUseMaterialRetrieve<RS, IDocWarehouse> proc =
      (PrcAccDocUseMaterialRetrieve<RS, IDocWarehouse>)
        this.processorsMap
          .get(PrcAccDocUseMaterialRetrieve.class.getSimpleName());
    if (proc == null) {
      proc = new PrcAccDocUseMaterialRetrieve<RS, IDocWarehouse>();
      proc.setSrvWarehouseEntry(getSrvWarehouseEntry());
      proc.setSrvUseMaterialEntry(getSrvUseMaterialEntry());
      @SuppressWarnings("unchecked")
      IEntityProcessor<IDocWarehouse, Long> delegate =
        (IEntityProcessor<IDocWarehouse, Long>)
          lazyGetPrcAccDocRetrieve(pAddParam);
      proc.setPrcAccDocRetrieve(delegate);
      //assigning fully initialized object:
      this.processorsMap
        .put(PrcAccDocUseMaterialRetrieve.class.getSimpleName(), proc);
    }
    return proc;
  }

  /**
   * <p>Lazy get PrcAccDocCogsRetrieve.</p>
   * @param pAddParam additional param
   * @return requested PrcAccDocCogsRetrieve
   * @throws Exception - an exception
   */
  protected final PrcAccDocCogsRetrieve lazyGetPrcAccDocCogsRetrieve(
    final Map<String, Object> pAddParam) throws Exception {
    @SuppressWarnings("unchecked")
    PrcAccDocCogsRetrieve<RS, IDocWarehouse> proc =
      (PrcAccDocCogsRetrieve<RS, IDocWarehouse>)
        this.processorsMap.get(PrcAccDocCogsRetrieve.class.getSimpleName());
    if (proc == null) {
      proc = new PrcAccDocCogsRetrieve<RS, IDocWarehouse>();
      proc.setSrvWarehouseEntry(getSrvWarehouseEntry());
      proc.setSrvCogsEntry(getSrvCogsEntry());
      @SuppressWarnings("unchecked")
      IEntityProcessor<IDocWarehouse, Long> delegate =
        (IEntityProcessor<IDocWarehouse, Long>)
          lazyGetPrcAccDocRetrieve(pAddParam);
      proc.setPrcAccDocRetrieve(delegate);
      //assigning fully initialized object:
      this.processorsMap
        .put(PrcAccDocCogsRetrieve.class.getSimpleName(), proc);
    }
    return proc;
  }

  /**
   * <p>Lazy get PrcAccDocWithSubaccRetrieve.</p>
   * @param pAddParam additional param
   * @return requested PrcAccDocWithSubaccRetrieve
   * @throws Exception - an exception
   */
  protected final PrcAccDocWithSubaccRetrieve
    lazyGetPrcAccDocWithSubaccRetrieve(
      final Map<String, Object> pAddParam) throws Exception {
    @SuppressWarnings("unchecked")
    PrcAccDocWithSubaccRetrieve<RS, IDoc> proc =
      (PrcAccDocWithSubaccRetrieve<RS, IDoc>) this.processorsMap
        .get(PrcAccDocWithSubaccRetrieve.class.getSimpleName());
    if (proc == null) {
      proc = new PrcAccDocWithSubaccRetrieve<RS, IDoc>();
      proc.setSrvAccEntry(getSrvAccEntry());
      @SuppressWarnings("unchecked")
      IEntityProcessor<IDoc, Long> delegate = (IEntityProcessor<IDoc, Long>)
        lazyGetPrcAccEntityWithSubaccRetrieve(pAddParam);
      proc.setPrcAccEntityWithSubaccRetrieve(delegate);
      //assigning fully initialized object:
      this.processorsMap
        .put(PrcAccDocWithSubaccRetrieve.class.getSimpleName(), proc);
    }
    return proc;
  }

  /**
   * <p>Lazy get PrcAccDocRetrieve.</p>
   * @param pAddParam additional param
   * @return requested PrcAccDocRetrieve
   * @throws Exception - an exception
   */
  protected final PrcAccDocRetrieve lazyGetPrcAccDocRetrieve(
    final Map<String, Object> pAddParam) throws Exception {
    @SuppressWarnings("unchecked")
    PrcAccDocRetrieve<RS, IDoc> proc = (PrcAccDocRetrieve<RS, IDoc>)
      this.processorsMap.get(PrcAccDocRetrieve.class.getSimpleName());
    if (proc == null) {
      proc = new PrcAccDocRetrieve<RS, IDoc>();
      proc.setSrvAccEntry(getSrvAccEntry());
      @SuppressWarnings("unchecked")
      IEntityProcessor<IDoc, Long> delegate = (IEntityProcessor<IDoc, Long>)
        this.fctBnEntitiesProcessors
          .lazyGet(pAddParam, PrcEntityRetrieve.class.getSimpleName());
      proc.setPrcAccEntityRetrieve(delegate);
      //assigning fully initialized object:
      this.processorsMap.put(PrcAccDocRetrieve.class.getSimpleName(), proc);
    }
    return proc;
  }

  //Synchronized getters and setters:
  /**
   * <p>Getter for fctBnEntitiesProcessors.</p>
   * @return FctBnEntitiesProcessors<RS>
   **/
  public final synchronized FctBnEntitiesProcessors<RS>
    getFctBnEntitiesProcessors() {
    return this.fctBnEntitiesProcessors;
  }

  /**
   * <p>Setter for fctBnEntitiesProcessors.</p>
   * @param pFctBnEntitiesProcessors reference
   **/
  public final synchronized void setFctBnEntitiesProcessors(
    final FctBnEntitiesProcessors<RS> pFctBnEntitiesProcessors) {
    this.fctBnEntitiesProcessors = pFctBnEntitiesProcessors;
  }

  /**
   * <p>Getter for factoryAppBeans.</p>
   * @return IFactoryAppBeans
   **/
  public final synchronized IFactoryAppBeans getFactoryAppBeans() {
    return this.factoryAppBeans;
  }

  /**
   * <p>Setter for factoryAppBeans.</p>
   * @param pFactoryAppBeans reference
   **/
  public final synchronized void setFactoryAppBeans(
    final IFactoryAppBeans pFactoryAppBeans) {
    this.factoryAppBeans = pFactoryAppBeans;
  }

  /**
   * <p>Getter for srvAccSettings.</p>
   * @return ISrvAccSettings
   **/
  public final synchronized ISrvAccSettings getSrvAccSettings() {
    return this.srvAccSettings;
  }

  /**
   * <p>Setter for srvAccSettings.</p>
   * @param pSrvAccSettings reference
   **/
  public final synchronized void setSrvAccSettings(
    final ISrvAccSettings pSrvAccSettings) {
    this.srvAccSettings = pSrvAccSettings;
  }

  /**
   * <p>Getter for srvOrm.</p>
   * @return ISrvOrm<RS>
   **/
  public final synchronized ISrvOrm<RS> getSrvOrm() {
    return this.srvOrm;
  }

  /**
   * <p>Setter for srvOrm.</p>
   * @param pSrvOrm reference
   **/
  public final synchronized void setSrvOrm(final ISrvOrm<RS> pSrvOrm) {
    this.srvOrm = pSrvOrm;
  }

  /**
   * <p>Geter for srvDatabase.</p>
   * @return ISrvDatabase
   **/
  public final synchronized ISrvDatabase<RS> getSrvDatabase() {
    return this.srvDatabase;
  }

  /**
   * <p>Setter for srvDatabase.</p>
   * @param pSrvDatabase reference
   **/
  public final synchronized void setSrvDatabase(
    final ISrvDatabase<RS> pSrvDatabase) {
    this.srvDatabase = pSrvDatabase;
  }

  /**
   * <p>Geter for srvTypeCode.</p>
   * @return ISrvTypeCode
   **/
  public final synchronized ISrvTypeCode getSrvTypeCode() {
    return this.srvTypeCode;
  }

  /**
   * <p>Setter for srvTypeCode.</p>
   * @param pSrvTypeCode reference
   **/
  public final synchronized void setSrvTypeCode(
    final ISrvTypeCode pSrvTypeCode) {
    this.srvTypeCode = pSrvTypeCode;
  }

  /**
   * <p>Geter for srvI18n.</p>
   * @return ISrvI18n
   **/
  public final synchronized ISrvI18n getSrvI18n() {
    return this.srvI18n;
  }

  /**
   * <p>Setter for srvI18n.</p>
   * @param pSrvI18n reference
   **/
  public final synchronized void setSrvI18n(final ISrvI18n pSrvI18n) {
    this.srvI18n = pSrvI18n;
  }

  /**
   * <p>Getter for srvAccEntry.</p>
   * @return ISrvAccEntry
   **/
  public final synchronized ISrvAccEntry getSrvAccEntry() {
    return this.srvAccEntry;
  }

  /**
   * <p>Setter for srvAccEntry.</p>
   * @param pSrvAccEntry reference
   **/
  public final synchronized void setSrvAccEntry(
    final ISrvAccEntry pSrvAccEntry) {
    this.srvAccEntry = pSrvAccEntry;
  }

  /**
   * <p>Geter for srvWarehouseEntry.</p>
   * @return ISrvWarehouseEntry
   **/
  public final synchronized ISrvWarehouseEntry getSrvWarehouseEntry() {
    return this.srvWarehouseEntry;
  }

  /**
   * <p>Setter for srvWarehouseEntry.</p>
   * @param pSrvWarehouseEntry reference
   **/
  public final synchronized void setSrvWarehouseEntry(
    final ISrvWarehouseEntry pSrvWarehouseEntry) {
    this.srvWarehouseEntry = pSrvWarehouseEntry;
  }

  /**
   * <p>Getter for srvCogsEntry.</p>
   * @return ISrvDrawItemEntry<CogsEntry>
   **/
  public final synchronized ISrvDrawItemEntry<CogsEntry> getSrvCogsEntry() {
    return this.srvCogsEntry;
  }

  /**
   * <p>Setter for srvCogsEntry.</p>
   * @param pSrvCogsEntry reference
   **/
  public final synchronized void setSrvCogsEntry(
    final ISrvDrawItemEntry<CogsEntry> pSrvCogsEntry) {
    this.srvCogsEntry = pSrvCogsEntry;
  }

  /**
   * <p>Getter for srvUseMaterialEntry.</p>
   * @return ISrvDrawItemEntry<UseMaterialEntry>
   **/
  public final synchronized ISrvDrawItemEntry<UseMaterialEntry>
    getSrvUseMaterialEntry() {
    return this.srvUseMaterialEntry;
  }

  /**
   * <p>Setter for srvUseMaterialEntry.</p>
   * @param pSrvUseMaterialEntry reference
   **/
  public final synchronized void setSrvUseMaterialEntry(
    final ISrvDrawItemEntry<UseMaterialEntry> pSrvUseMaterialEntry) {
    this.srvUseMaterialEntry = pSrvUseMaterialEntry;
  }

  /**
   * <p>Getter for srvDate.</p>
   * @return ISrvDate
   **/
  public final synchronized ISrvDate getSrvDate() {
    return this.srvDate;
  }

  /**
   * <p>Setter for srvDate.</p>
   * @param pSrvDate reference
   **/
  public final synchronized void setSrvDate(final ISrvDate pSrvDate) {
    this.srvDate = pSrvDate;
  }

  /**
   * <p>Getter for mngUvdSettings.</p>
   * @return IMngSettings
   **/
  public final synchronized IMngSettings getMngUvdSettings() {
    return this.mngUvdSettings;
  }

  /**
   * <p>Setter for mngUvdSettings.</p>
   * @param pMngUvdSettings reference
   **/
  public final synchronized void setMngUvdSettings(
    final IMngSettings pMngUvdSettings) {
    this.mngUvdSettings = pMngUvdSettings;
  }

  /**
   * <p>Getter for convertersFieldsFatory.</p>
   * @return IFactoryAppBeansByName<IConverterToFromString<?>>
   **/
  public final synchronized IFactoryAppBeansByName<IConverterToFromString<?>>
    getConvertersFieldsFatory() {
    return this.convertersFieldsFatory;
  }

  /**
   * <p>Setter for convertersFieldsFatory.</p>
   * @param pConvertersFieldsFatory reference
   **/
  public final synchronized void setConvertersFieldsFatory(
    final IFactoryAppBeansByName<IConverterToFromString<?>>
      pConvertersFieldsFatory) {
    this.convertersFieldsFatory = pConvertersFieldsFatory;
  }

  /**
   * <p>Getter for fieldConverterNamesHolder.</p>
   * @return IHolderForClassByName<String>
   **/
  public final synchronized IHolderForClassByName<String>
    getFieldConverterNamesHolder() {
    return this.fieldConverterNamesHolder;
  }

  /**
   * <p>Setter for fieldConverterNamesHolder.</p>
   * @param pFieldConverterNamesHolder reference
   **/
  public final synchronized void setFieldConverterNamesHolder(
    final IHolderForClassByName<String> pFieldConverterNamesHolder) {
    this.fieldConverterNamesHolder = pFieldConverterNamesHolder;
  }

  /**
   * <p>Getter for srvBalance.</p>
   * @return ISrvBalance
   **/
  public final synchronized ISrvBalance getSrvBalance() {
    return this.srvBalance;
  }

  /**
   * <p>Setter for srvBalance.</p>
   * @param pSrvBalance reference
   **/
  public final synchronized void setSrvBalance(final ISrvBalance pSrvBalance) {
    this.srvBalance = pSrvBalance;
  }

  /**
   * <p>Getter for srvNumberToString.</p>
   * @return ISrvNumberToString
   **/
  public final synchronized ISrvNumberToString getSrvNumberToString() {
    return this.srvNumberToString;
  }

  /**
   * <p>Setter for srvNumberToString.</p>
   * @param pSrvNumberToString reference
   **/
  public final synchronized void setSrvNumberToString(
    final ISrvNumberToString pSrvNumberToString) {
    this.srvNumberToString = pSrvNumberToString;
  }

  /**
   * <p>Getter for csvReader.</p>
   * @return ICsvReader
   **/
  public final synchronized ICsvReader getCsvReader() {
    return this.csvReader;
  }

  /**
   * <p>Setter for csvReader.</p>
   * @param pCsvReader reference
   **/
  public final synchronized void setCsvReader(final ICsvReader pCsvReader) {
    this.csvReader = pCsvReader;
  }

  /**
   * <p>Geter for logger.</p>
   * @return ILog
   **/
  public final synchronized ILog getLogger() {
    return this.logger;
  }

  /**
   * <p>Setter for logger.</p>
   * @param pLogger reference
   **/
  public final synchronized void setLogger(final ILog pLogger) {
    this.logger = pLogger;
  }
}

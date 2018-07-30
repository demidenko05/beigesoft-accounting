package org.beigesoft.accounting.service;

/*
 * Copyright (c) 2016 Beigesoft ™
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

import org.beigesoft.accounting.persistable.PurchaseInvoiceLine;
import org.beigesoft.accounting.persistable.PurchaseInvoice;
import org.beigesoft.accounting.persistable.SalesInvoiceLine;
import org.beigesoft.accounting.persistable.SalesInvoice;
import org.beigesoft.accounting.persistable.Manufacture;
import org.beigesoft.accounting.persistable.ManufacturingProcess;
import org.beigesoft.accounting.persistable.Wage;
import org.beigesoft.accounting.persistable.PrepaymentTo;
import org.beigesoft.accounting.persistable.PaymentTo;
import org.beigesoft.accounting.persistable.PrepaymentFrom;
import org.beigesoft.accounting.persistable.MoveItems;
import org.beigesoft.accounting.persistable.MoveItemsLine;
import org.beigesoft.accounting.persistable.GoodsLoss;
import org.beigesoft.accounting.persistable.GoodsLossLine;
import org.beigesoft.accounting.persistable.SalesReturn;
import org.beigesoft.accounting.persistable.SalesReturnLine;
import org.beigesoft.accounting.persistable.PurchaseReturn;
import org.beigesoft.accounting.persistable.PurchaseReturnLine;
import org.beigesoft.accounting.persistable.BeginningInventory;
import org.beigesoft.accounting.persistable.BeginningInventoryLine;
import org.beigesoft.accounting.persistable.BankStatementLine;


/**
 * <p>Business service for code - java type map.
 * It holds all documents and its lines that makes accounting
 * entries.</p>
 *
 * @author Yury Demidenko
 */
public class SrvTypeCodeAccSources implements ISrvTypeCode {

  /**
   * <p>Source types map.</p>
   **/
  private Map<Integer, Class<?>> typeCodeMap;

  /**
   * <p>constructor.</p>
   **/
  public SrvTypeCodeAccSources() {
    typeCodeMap = new HashMap<Integer, Class<?>>();
    typeCodeMap.put(new PurchaseInvoiceLine().constTypeCode(),
      PurchaseInvoiceLine.class);
    typeCodeMap.put(new PurchaseInvoice().constTypeCode(),
      PurchaseInvoice.class);
    typeCodeMap.put(new SalesInvoiceLine().constTypeCode(),
      SalesInvoiceLine.class);
    typeCodeMap.put(new SalesInvoice().constTypeCode(),
      SalesInvoice.class);
    typeCodeMap.put(new Manufacture().constTypeCode(),
      Manufacture.class);
    typeCodeMap.put(new ManufacturingProcess().constTypeCode(),
      ManufacturingProcess.class);
    typeCodeMap.put(new Wage().constTypeCode(),
      Wage.class);
    typeCodeMap.put(new PrepaymentTo().constTypeCode(),
      PrepaymentTo.class);
    typeCodeMap.put(new PaymentTo().constTypeCode(),
      PaymentTo.class);
    typeCodeMap.put(new PrepaymentFrom().constTypeCode(),
      PrepaymentFrom.class);
    typeCodeMap.put(new GoodsLoss().constTypeCode(),
      GoodsLoss.class);
    typeCodeMap.put(new GoodsLossLine().constTypeCode(),
      GoodsLossLine.class);
    typeCodeMap.put(new MoveItems().constTypeCode(),
      MoveItems.class);
    typeCodeMap.put(new MoveItemsLine().constTypeCode(),
      MoveItemsLine.class);
    typeCodeMap.put(new SalesReturn().constTypeCode(),
      SalesReturn.class);
    typeCodeMap.put(new SalesReturnLine().constTypeCode(),
      SalesReturnLine.class);
    typeCodeMap.put(new PurchaseReturn().constTypeCode(),
      PurchaseReturn.class);
    typeCodeMap.put(new PurchaseReturnLine().constTypeCode(),
      PurchaseReturnLine.class);
    typeCodeMap.put(new BeginningInventory().constTypeCode(),
      BeginningInventory.class);
    typeCodeMap.put(new BeginningInventoryLine().constTypeCode(),
      BeginningInventoryLine.class);
    typeCodeMap.put(new BankStatementLine().constTypeCode(),
      BankStatementLine.class);
  }

  /**
   * <p>Getter for code - java type map.</p>
   * @return Map<Integer, String>
   **/
  @Override
  public final Map<Integer, Class<?>> getTypeCodeMap() {
    return this.typeCodeMap;
  }

  /**
   * <p>Setter for code - java type simple name map.</p>
   * @param pTypeCodeMap reference
   **/
  @Override
  public final void setTypeCodeMap(final Map<Integer, Class<?>> pTypeCodeMap) {
    this.typeCodeMap = pTypeCodeMap;
  }
}

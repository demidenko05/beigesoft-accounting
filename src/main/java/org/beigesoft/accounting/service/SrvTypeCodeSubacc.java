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

import org.beigesoft.accounting.persistable.DebtorCreditor;
import org.beigesoft.accounting.persistable.DebtorCreditorUsed;
import org.beigesoft.accounting.persistable.DebtorCreditorCategory;
import org.beigesoft.accounting.persistable.DebtorCreditorCategoryUsed;
import org.beigesoft.accounting.persistable.Tax;
import org.beigesoft.accounting.persistable.TaxUsed;
import org.beigesoft.accounting.persistable.InvItemCategory;
import org.beigesoft.accounting.persistable.InvItemCategoryUsed;
import org.beigesoft.accounting.persistable.Expense;
import org.beigesoft.accounting.persistable.ExpenseUsed;
import org.beigesoft.accounting.persistable.Property;
import org.beigesoft.accounting.persistable.PropertyUsed;
import org.beigesoft.accounting.persistable.BankAccount;
import org.beigesoft.accounting.persistable.BankAccountUsed;
import org.beigesoft.accounting.persistable.WageType;
import org.beigesoft.accounting.persistable.WageTypeUsed;
import org.beigesoft.accounting.persistable.EmployeeCategory;
import org.beigesoft.accounting.persistable.EmployeeCategoryUsed;
import org.beigesoft.accounting.persistable.Employee;
import org.beigesoft.accounting.persistable.EmployeeUsed;
import org.beigesoft.accounting.persistable.ServiceToSaleCategory;
import org.beigesoft.accounting.persistable.ServiceToSaleCategoryUsed;

/**
 * <p>Business service for code - java sub-account type map.</p>
 *
 * @author Yury Demidenko
 */
public class SrvTypeCodeSubacc implements ISrvSubaccCode {

  /**
   * <p>Subaccount types map.</p>
   **/
  private Map<Integer, Class<?>> typeCodeMap;

  /**
   * <p>Subaccount used types map.</p>
   **/
  private Map<Integer, Class<?>> subaccUsedCodeMap;

  /**
   * <p>constructor.</p>
   **/
  public SrvTypeCodeSubacc() {
    this.typeCodeMap = new HashMap<Integer, Class<?>>();
    this.subaccUsedCodeMap = new HashMap<Integer, Class<?>>();
    DebtorCreditor dc = new DebtorCreditor();
    this.typeCodeMap.put(dc.constTypeCode(), DebtorCreditor.class);
    this.subaccUsedCodeMap.put(dc.constTypeCode(),
      DebtorCreditorUsed.class);
    DebtorCreditorCategory dcc = new DebtorCreditorCategory();
    this.typeCodeMap.put(dcc.constTypeCode(), DebtorCreditorCategory.class);
    this.subaccUsedCodeMap.put(dcc.constTypeCode(),
      DebtorCreditorCategoryUsed.class);
    Tax tax = new Tax();
    this.typeCodeMap.put(tax.constTypeCode(), Tax.class);
    this.subaccUsedCodeMap.put(tax.constTypeCode(),
      TaxUsed.class);
    InvItemCategory iic = new InvItemCategory();
    this.typeCodeMap.put(iic.constTypeCode(), InvItemCategory.class);
    this.subaccUsedCodeMap.put(iic.constTypeCode(),
      InvItemCategoryUsed.class);
    Expense exp = new Expense();
    this.typeCodeMap.put(exp.constTypeCode(), Expense.class);
    this.subaccUsedCodeMap.put(exp.constTypeCode(),
      ExpenseUsed.class);
    Property prp = new Property();
    this.typeCodeMap.put(prp.constTypeCode(), Property.class);
    this.subaccUsedCodeMap.put(prp.constTypeCode(),
      PropertyUsed.class);
    BankAccount ba = new BankAccount();
    this.typeCodeMap.put(ba.constTypeCode(), BankAccount.class);
    this.subaccUsedCodeMap.put(ba.constTypeCode(),
      BankAccountUsed.class);
    WageType wt = new WageType();
    this.typeCodeMap.put(wt.constTypeCode(), WageType.class);
    this.subaccUsedCodeMap.put(wt.constTypeCode(),
      WageTypeUsed.class);
    EmployeeCategory emc = new EmployeeCategory();
    this.typeCodeMap.put(emc.constTypeCode(), EmployeeCategory.class);
    this.subaccUsedCodeMap.put(emc.constTypeCode(),
      EmployeeCategoryUsed.class);
    Employee em = new Employee();
    this.typeCodeMap.put(em.constTypeCode(), Employee.class);
    this.subaccUsedCodeMap.put(em.constTypeCode(),
      EmployeeUsed.class);
    ServiceToSaleCategory stsc = new ServiceToSaleCategory();
    this.typeCodeMap.put(stsc.constTypeCode(), ServiceToSaleCategory.class);
    this.subaccUsedCodeMap.put(stsc.constTypeCode(),
      ServiceToSaleCategoryUsed.class);
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

  /**
   * <p>Getter for code - used type map.</p>
   * @return Map<Integer, String>
   **/
  @Override
  public final Map<Integer, Class<?>> getSubaccUsedCodeMap() {
    return this.subaccUsedCodeMap;
  }

  /**
   * <p>Setter for code - used type simple name map.</p>
   * @param pSubaccUsedCodeMap reference
   **/
  @Override
  public final void setSubaccUsedCodeMap(
    final Map<Integer, Class<?>> pSubaccUsedCodeMap) {
    this.subaccUsedCodeMap = pSubaccUsedCodeMap;
  }
}

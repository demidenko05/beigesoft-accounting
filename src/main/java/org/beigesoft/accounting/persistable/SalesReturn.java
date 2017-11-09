package org.beigesoft.accounting.persistable;

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

import java.util.List;

import org.beigesoft.accounting.persistable.base.ADocWithTaxes;
import org.beigesoft.accounting.model.EWarehouseMovementType;

/**
 * <pre>
 * Model of Sales Return.
 * </pre>
 *
 * @author Yury Demidenko
 */
public class SalesReturn extends ADocWithTaxes
  implements IDocWarehouse {

  /**
   * <p>Customer.</p>
   **/
  private DebtorCreditor customer;

  /**
   * <p>Lines.</p>
   **/
  private List<SalesReturnLine> itsLines;

  /**
   * <p>Taxes lines.</p>
   **/
  private List<SalesReturnTaxLine> taxesLines;

  /**
   * <p>OOP friendly Constant of code type.</p>
   * @return 12
   **/
  @Override
  public final Integer constTypeCode() {
    return 12;
  }

  /**
   * <p>If owned lines make warehouse entries this return
   * their type.</p>
   * @return Boolean
   **/
  @Override
  public final EWarehouseMovementType getLinesWarehouseType() {
    return EWarehouseMovementType.LOAD;
  }

  //Simple getters and setters:
  /**
   * <p>Getter for customer.</p>
   * @return DebtorCreditor
   **/
  public final DebtorCreditor getCustomer() {
    return this.customer;
  }

  /**
   * <p>Setter for customer.</p>
   * @param pCustomer reference
   **/
  public final void setCustomer(final DebtorCreditor pCustomer) {
    this.customer = pCustomer;
  }

  /**
   * <p>Getter for itsLines.</p>
   * @return List<SalesReturnLine>
   **/
  public final List<SalesReturnLine> getItsLines() {
    return this.itsLines;
  }

  /**
   * <p>Setter for itsLines.</p>
   * @param pItsLines reference
   **/
  public final void setItsLines(final List<SalesReturnLine> pItsLines) {
    this.itsLines = pItsLines;
  }

  /**
   * <p>Getter for taxesLines.</p>
   * @return List<SalesReturnTaxLine>
   **/
  public final List<SalesReturnTaxLine> getTaxesLines() {
    return this.taxesLines;
  }

  /**
   * <p>Setter for taxesLines.</p>
   * @param pTaxesLines reference
   **/
  public final void setTaxesLines(final List<SalesReturnTaxLine> pTaxesLines) {
    this.taxesLines = pTaxesLines;
  }
}

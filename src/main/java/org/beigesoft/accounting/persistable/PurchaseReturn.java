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
 * Model of Purchase Return.
 * </pre>
 *
 * @author Yury Demidenko
 */
public class PurchaseReturn extends ADocWithTaxes
  implements IDocWarehouse {

  /**
   * <p>Purchase Invoice.</p>
   **/
  private PurchaseInvoice purchaseInvoice;

  /**
   * <p>Lines.</p>
   **/
  private List<PurchaseReturnLine> itsLines;

  /**
   * <p>Taxes lines.</p>
   **/
  private List<PurchaseReturnTaxLine> taxesLines;

  /**
   * <p>OOP friendly Constant of code type.</p>
   * @return 13
   **/
  @Override
  public final Integer constTypeCode() {
    return 13;
  }

  /**
   * <p>If owned lines make warehouse entries this return
   * their type.</p>
   * @return Boolean
   **/
  @Override
  public final EWarehouseMovementType getLinesWarehouseType() {
    return EWarehouseMovementType.WITHDRAWAL;
  }

  //Simple getters and setters:
  /**
   * <p>Getter for purchaseInvoice.</p>
   * @return PurchaseInvoice
   **/
  public final PurchaseInvoice getPurchaseInvoice() {
    return this.purchaseInvoice;
  }

  /**
   * <p>Setter for purchaseInvoice.</p>
   * @param pPurchaseInvoice reference
   **/
  public final void setPurchaseInvoice(final PurchaseInvoice pPurchaseInvoice) {
    this.purchaseInvoice = pPurchaseInvoice;
  }

  /**
   * <p>Getter for itsLines.</p>
   * @return List<PurchaseReturnLine>
   **/
  public final List<PurchaseReturnLine> getItsLines() {
    return this.itsLines;
  }

  /**
   * <p>Setter for itsLines.</p>
   * @param pItsLines reference
   **/
  public final void setItsLines(final List<PurchaseReturnLine> pItsLines) {
    this.itsLines = pItsLines;
  }

  /**
   * <p>Getter for taxesLines.</p>
   * @return List<PurchaseReturnTaxLine>
   **/
  public final List<PurchaseReturnTaxLine> getTaxesLines() {
    return this.taxesLines;
  }

  /**
   * <p>Setter for taxesLines.</p>
   * @param pTaxesLines reference
   **/
  public final void setTaxesLines(
    final List<PurchaseReturnTaxLine> pTaxesLines) {
    this.taxesLines = pTaxesLines;
  }
}

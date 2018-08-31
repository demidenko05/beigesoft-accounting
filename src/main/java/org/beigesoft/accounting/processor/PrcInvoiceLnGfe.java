package org.beigesoft.accounting.processor;

/*
 * Copyright (c) 2018 Beigesoft™
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
import java.math.RoundingMode;

import org.beigesoft.model.IRequestData;
import org.beigesoft.service.IEntityProcessor;
import org.beigesoft.accounting.persistable.PurchaseInvoice;
import org.beigesoft.accounting.persistable.PurchaseInvoiceServiceLine;
import org.beigesoft.accounting.persistable.SalesInvoice;
import org.beigesoft.accounting.persistable.SalesInvoiceServiceLine;
import org.beigesoft.accounting.persistable.IInvoice;
import org.beigesoft.accounting.persistable.IInvoiceLine;

/**
 * <p>Process that retrieves purchase/sales invoice service line
 * for editing.</p>
 *
 * @param <RS> platform dependent record set type
 * @param <T> line type
 * @param <I> invoice type
 * @author Yury Demidenko
 */
public class PrcInvoiceLnGfe<RS, T extends IInvoiceLine<I>, I extends IInvoice>
  implements IEntityProcessor<T, Long> {

  /**
   * <p>Acc-EntityPb Edit/Confirm delete delegator.</p>
   **/
  private IEntityProcessor<T, Long> prcEntityPbEditDelete;


  /**
   * <p>Process invLn request.</p>
   * @param pReqVars additional param, e.g. return this line's
   * document in "nextEntity" for farther process
   * @param pRequestData Request Data
   * @param pEntity Entity to process
   * @return Entity processed for farther process or null
   * @throws Exception - an exception
   **/
  @Override
  public final T process(final Map<String, Object> pReqVars,
    final T pEntity,
      final IRequestData pRequestData) throws Exception {
    if (pEntity.getClass() == PurchaseInvoiceServiceLine.class) {
      pReqVars.put("PurchaseInvoicevendordeepLevel", 3);
    } else if (pEntity.getClass() == SalesInvoiceServiceLine.class) {
      pReqVars.put("SalesInvoicecustomerdeepLevel", 3);
    }
    Set<String> ndFlDc = new HashSet<String>();
    ndFlDc.add("itsId");
    ndFlDc.add("isForeigner");
    ndFlDc.add("taxDestination");
    pReqVars.put("DebtorCreditorneededFields", ndFlDc);
    T invLn = this.prcEntityPbEditDelete
      .process(pReqVars, pEntity, pRequestData);
    pReqVars.remove("DebtorCreditorneededFields");
    if (invLn.getClass() == PurchaseInvoiceServiceLine.class) {
      pReqVars.remove("PurchaseInvoicevendordeepLevel");
    } else if (invLn.getClass() == SalesInvoiceServiceLine.class) {
      pReqVars.remove("SalesInvoicecustomerdeepLevel");
    }
    RoundingMode rounding = null;
    String taxRounding = null;
    if (invLn.getClass() == PurchaseInvoiceServiceLine.class) {
      PurchaseInvoice inv = (PurchaseInvoice) invLn.getItsOwner();
      if (inv.getVendor().getTaxDestination() != null) {
        rounding = inv.getVendor().getTaxDestination().getSalTaxRoundMode();
      }
    } else if (invLn.getClass() == SalesInvoiceServiceLine.class) {
      SalesInvoice inv = (SalesInvoice) invLn.getItsOwner();
      if (inv.getCustomer().getTaxDestination() != null) {
        rounding = inv.getCustomer().getTaxDestination().getSalTaxRoundMode();
      }
    }
    if (rounding != null) {
      if (rounding.equals(RoundingMode.HALF_UP)) {
        taxRounding = "S";
      } else if (rounding.equals(RoundingMode.HALF_DOWN)) {
        taxRounding = "s";
      } else if (rounding.equals(RoundingMode.UP)) {
        taxRounding = "U";
      } else if (rounding.equals(RoundingMode.DOWN)) {
        taxRounding = "D";
      } else if (rounding.equals(RoundingMode.HALF_EVEN)) {
        taxRounding = "B";
      } else if (rounding.equals(RoundingMode.CEILING)) {
        taxRounding = "C";
      } else if (rounding.equals(RoundingMode.FLOOR)) {
        taxRounding = "F";
      } else {
        taxRounding = "S";
      }
    }
    pRequestData.setAttribute("taxRounding", taxRounding);
    return invLn;
  }
  //Simple getters and setters:
  /**
   * <p>Getter for prcEntityPbEditDelete.</p>
   * @return IEntityProcessor<IInvoice, Long>
   **/
  public final IEntityProcessor<T, Long>
    getPrcEntityPbEditDelete() {
    return this.prcEntityPbEditDelete;
  }

  /**
   * <p>Setter for prcEntityPbEditDelete.</p>
   * @param pPrcEntityPbEditDelete reference
   **/
  public final void setPrcEntityPbEditDelete(
    final IEntityProcessor<T, Long> pPrcEntityPbEditDelete) {
    this.prcEntityPbEditDelete = pPrcEntityPbEditDelete;
  }
}

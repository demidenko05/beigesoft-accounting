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
import org.beigesoft.orm.processor.PrcEntityCreate;
import org.beigesoft.accounting.persistable.IInvoice;
import org.beigesoft.accounting.persistable.IInvoiceLine;
import org.beigesoft.accounting.persistable.PurchaseInvoice;
import org.beigesoft.accounting.persistable.SalesInvoice;
import org.beigesoft.service.IEntityProcessor;

/**
 * <p>Service that create sales/purchase line.</p>
 *
 * @param <L> line type
 * @param <I> invoice type
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class
  PrcInvoiceLnCreate<RS, L extends IInvoiceLine<I>, I extends IInvoice>
    implements IEntityProcessor<L, Long> {

  /**
   * <p>Entity create delegator.</p>
   **/
  private PrcEntityCreate<RS, L, Long> prcEntityCreate;

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
  public final L process(
    final Map<String, Object> pReqVars,
      final L pEntityPb,
        final IRequestData pRequestData) throws Exception {
    L entity = this.prcEntityCreate
      .process(pReqVars, pEntityPb, pRequestData);
    pReqVars.put("DebtorCreditortaxDestinationdeepLevel", 2);
    Set<String> ndFlDc = new HashSet<String>();
    ndFlDc.add("itsId");
    ndFlDc.add("isForeigner");
    ndFlDc.add("taxDestination");
    pReqVars.put("DebtorCreditorneededFields", ndFlDc);
    entity.setItsOwner(this.prcEntityCreate.getSrvOrm()
      .retrieveEntity(pReqVars, entity.getItsOwner()));
    pReqVars.remove("DebtorCreditorneededFields");
    pReqVars.remove("DebtorCreditortaxDestinationdeepLevel");
    RoundingMode rounding = null;
    String taxRounding = null;
    if (entity.getItsOwner().getClass() == PurchaseInvoice.class) {
      PurchaseInvoice inv = (PurchaseInvoice) entity.getItsOwner();
      if (inv.getVendor().getTaxDestination() != null) {
        rounding = inv.getVendor().getTaxDestination().getSalTaxRoundMode();
      }
    } else if (entity.getItsOwner().getClass() == SalesInvoice.class) {
      SalesInvoice inv = (SalesInvoice) entity.getItsOwner();
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
    return entity;
  }

  //Simple getters and setters:
  /**
   * <p>Getter for prcEntityCreate.</p>
   * @return PrcEntityCreate<RS, L, Long>
   **/
  public final
    PrcEntityCreate<RS, L, Long> getPrcEntityCreate() {
    return this.prcEntityCreate;
  }

  /**
   * <p>Setter for prcEntityCreate.</p>
   * @param pPrcEntityCreate reference
   **/
  public final void setPrcEntityCreate(
    final PrcEntityCreate<RS, L, Long> pPrcEntityCreate) {
    this.prcEntityCreate = pPrcEntityCreate;
  }
}

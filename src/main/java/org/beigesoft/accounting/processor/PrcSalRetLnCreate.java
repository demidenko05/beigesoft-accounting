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
import java.util.Set;
import java.util.HashSet;

import org.beigesoft.model.IRequestData;
import org.beigesoft.orm.processor.PrcEntityCreate;
import org.beigesoft.accounting.persistable.SalesReturnLine;
import org.beigesoft.service.IEntityProcessor;

/**
 * <p>Service that create SalesReturnLine.</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class PrcSalRetLnCreate<RS>
  implements IEntityProcessor<SalesReturnLine, Long> {

  /**
   * <p>Entity create delegator.</p>
   **/
  private PrcEntityCreate<RS, SalesReturnLine, Long> prcEntityCreate;

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
  public final SalesReturnLine process(final Map<String, Object> pReqVars,
    final SalesReturnLine pEntityPb,
      final IRequestData pRequestData) throws Exception {
    SalesReturnLine entity = this.prcEntityCreate
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
    return entity;
  }

  //Simple getters and setters:
  /**
   * <p>Getter for prcEntityCreate.</p>
   * @return PrcEntityCreate<RS, SalesReturnLine, Long>
   **/
  public final
    PrcEntityCreate<RS, SalesReturnLine, Long> getPrcEntityCreate() {
    return this.prcEntityCreate;
  }

  /**
   * <p>Setter for prcEntityCreate.</p>
   * @param pPrcEntityCreate reference
   **/
  public final void setPrcEntityCreate(
    final PrcEntityCreate<RS, SalesReturnLine, Long> pPrcEntityCreate) {
    this.prcEntityCreate = pPrcEntityCreate;
  }
}

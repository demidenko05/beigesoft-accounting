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

import java.util.Date;
import java.util.Map;
import java.math.BigDecimal;

import org.beigesoft.model.IRequestData;
import org.beigesoft.service.IEntityProcessor;
import org.beigesoft.accounting.persistable.PrepaymentTo;
import org.beigesoft.accounting.service.ISrvTypeCode;

/**
 * <p>Service that make PrepaymentTo copy from DB.</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class PrcPrepaymentToCopy<RS>
  implements IEntityProcessor<PrepaymentTo, Long> {

  /**
   * <p>Acc-entity copy delegator.</p>
   **/
  private IEntityProcessor<PrepaymentTo, Long> prcAccEntityPbWithSubaccCopy;

  /**
   * <p>Type Codes of sub-accounts service.</p>
   **/
  private ISrvTypeCode srvTypeCode;

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
  public final PrepaymentTo process(
    final Map<String, Object> pAddParam,
      final PrepaymentTo pEntity,
        final IRequestData pRequestData) throws Exception {
    PrepaymentTo entity = this.prcAccEntityPbWithSubaccCopy
      .process(pAddParam, pEntity, pRequestData);
    entity.setReversedId(null);
    entity.setItsTotal(BigDecimal.ZERO);
    entity.setItsDate(new Date());
    entity.setHasMadeAccEntries(false);
    entity.setPurchaseInvoiceId(null);
    pRequestData.setAttribute("typeCodeSubaccMap",
      this.srvTypeCode.getTypeCodeMap());
    return entity;
  }

  //Simple getters and setters:
  /**
   * <p>Getter for prcAccEntityPbWithSubaccCopy.</p>
   * @return PrcAccEntityPbWithSubaccCopy<RS, PrepaymentTo, Long>
   **/
  public final IEntityProcessor<PrepaymentTo, Long>
    getPrcAccEntityPbWithSubaccCopy() {
    return this.prcAccEntityPbWithSubaccCopy;
  }

  /**
   * <p>Setter for prcAccEntityPbWithSubaccCopy.</p>
   * @param pPrcAccEntityPbWithSubaccCopy reference
   **/
  public final void setPrcAccEntityPbWithSubaccCopy(
    final IEntityProcessor<PrepaymentTo, Long> pPrcAccEntityPbWithSubaccCopy) {
    this.prcAccEntityPbWithSubaccCopy = pPrcAccEntityPbWithSubaccCopy;
  }

  /**
   * <p>Geter for srvTypeCode.</p>
   * @return ISrvTypeCode
   **/
  public final ISrvTypeCode getSrvTypeCode() {
    return this.srvTypeCode;
  }

  /**
   * <p>Setter for srvTypeCode.</p>
   * @param pSrvTypeCode reference
   **/
  public final void setSrvTypeCode(final ISrvTypeCode pSrvTypeCode) {
    this.srvTypeCode = pSrvTypeCode;
  }
}

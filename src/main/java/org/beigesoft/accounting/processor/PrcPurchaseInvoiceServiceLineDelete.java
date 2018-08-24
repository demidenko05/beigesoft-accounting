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

import org.beigesoft.model.IRequestData;
import org.beigesoft.service.IEntityProcessor;
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.accounting.persistable.PurchaseInvoiceServiceLine;
import org.beigesoft.accounting.persistable.PurchaseInvoiceServiceTaxLine;
import org.beigesoft.accounting.persistable.PurchaseInvoice;

/**
 * <p>Service that delete PurchaseInvoiceServiceLine from DB.</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class PrcPurchaseInvoiceServiceLineDelete<RS>
  implements IEntityProcessor<PurchaseInvoiceServiceLine, Long> {

  /**
   * <p>ORM service.</p>
   **/
  private ISrvOrm<RS> srvOrm;

  /**
   * <p>Entity Delete delegator.</p>
   **/
  private IEntityProcessor<PurchaseInvoiceServiceLine, Long>
    prcAccEntityPbDelete;

  /**
   * <p>It makes total for owner.</p>
   **/
  private UtlPurchaseGoodsServiceLine<RS> utlPurchaseGoodsServiceLine;

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
  public final PurchaseInvoiceServiceLine process(
    final Map<String, Object> pReqVars,
      final PurchaseInvoiceServiceLine pEntity,
        final IRequestData pRequestData) throws Exception {
    getSrvOrm().deleteEntityWhere(pReqVars,
      PurchaseInvoiceServiceTaxLine.class, "ITSOWNER=" + pEntity.getItsId());
    this.prcAccEntityPbDelete.process(pReqVars, pEntity, pRequestData);
    // Beige-Orm refresh:
    pReqVars.put("DebtorCreditortaxDestinationdeepLevel", 2);
    pEntity.setItsOwner(getSrvOrm()
      .retrieveEntity(pReqVars, pEntity.getItsOwner()));
    pReqVars.remove("DebtorCreditortaxDestinationdeepLevel");
    // optimistic locking (dirty check):
    Long ownerVersion = Long.valueOf(pRequestData
      .getParameter(PurchaseInvoice.class.getSimpleName() + ".ownerVersion"));
    pEntity.getItsOwner().setItsVersion(ownerVersion);
    this.utlPurchaseGoodsServiceLine
      .updateOwner(pReqVars, pEntity.getItsOwner());
    pReqVars.put("nextEntity", pEntity.getItsOwner());
    pReqVars.put("nameOwnerEntity", PurchaseInvoice.class.getSimpleName());
    return null;
  }

  //Simple getters and setters:
  /**
   * <p>Getter for srvOrm.</p>
   * @return ISrvOrm<RS>
   **/
  public final ISrvOrm<RS> getSrvOrm() {
    return this.srvOrm;
  }

  /**
   * <p>Setter for srvOrm.</p>
   * @param pSrvOrm reference
   **/
  public final void setSrvOrm(final ISrvOrm<RS> pSrvOrm) {
    this.srvOrm = pSrvOrm;
  }

  /**
   * <p>Getter for prcAccEntityPbDelete.</p>
   * @return PrcAccEntityPbDelete<RS, PurchaseInvoiceServiceLine, Long>
   **/
  public final IEntityProcessor<PurchaseInvoiceServiceLine, Long>
    getPrcAccEntityPbDelete() {
    return this.prcAccEntityPbDelete;
  }

  /**
   * <p>Setter for prcAccEntityPbDelete.</p>
   * @param pPrcAccEntityPbDelete reference
   **/
  public final void setPrcAccEntityPbDelete(
    final IEntityProcessor<PurchaseInvoiceServiceLine, Long>
      pPrcAccEntityPbDelete) {
    this.prcAccEntityPbDelete = pPrcAccEntityPbDelete;
  }

  /**
   * <p>Getter for utlPurchaseGoodsServiceLine.</p>
   * @return UtlPurchaseGoodsServiceLine<RS>
   **/
  public final UtlPurchaseGoodsServiceLine<RS>
    getUtlPurchaseGoodsServiceLine() {
    return this.utlPurchaseGoodsServiceLine;
  }

  /**
   * <p>Setter for utlPurchaseGoodsServiceLine.</p>
   * @param pUtlPurchaseGoodsServiceLine reference
   **/
  public final void setUtlPurchaseGoodsServiceLine(
    final UtlPurchaseGoodsServiceLine<RS> pUtlPurchaseGoodsServiceLine) {
    this.utlPurchaseGoodsServiceLine = pUtlPurchaseGoodsServiceLine;
  }
}

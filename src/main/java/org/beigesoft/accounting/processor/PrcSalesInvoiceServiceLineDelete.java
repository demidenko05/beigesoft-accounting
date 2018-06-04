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

import java.util.List;
import java.util.Map;

import org.beigesoft.model.IRequestData;
import org.beigesoft.service.IEntityProcessor;
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.accounting.persistable.SalesInvoiceServiceLine;
import org.beigesoft.accounting.persistable.SalesInvoice;
import org.beigesoft.accounting.persistable.SalesInvoiceServiceTaxLine;

/**
 * <p>Service that delete SalesInvoiceServiceLine from DB.</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class PrcSalesInvoiceServiceLineDelete<RS>
  implements IEntityProcessor<SalesInvoiceServiceLine, Long> {

  /**
   * <p>ORM service.</p>
   **/
  private ISrvOrm<RS> srvOrm;

  /**
   * <p>Entity Delete delegator.</p>
   **/
  private IEntityProcessor<SalesInvoiceServiceLine, Long>
    prcAccEntityPbDelete;

  /**
   * <p>It makes total for owner.</p>
   **/
  private UtlSalesGoodsServiceLine<RS> utlSalesGoodsServiceLine;

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
  public final SalesInvoiceServiceLine process(
    final Map<String, Object> pAddParam,
      final SalesInvoiceServiceLine pEntity,
        final IRequestData pRequestData) throws Exception {
    SalesInvoiceServiceTaxLine pistlt = new SalesInvoiceServiceTaxLine();
    pistlt.setItsOwner(pEntity);
    List<SalesInvoiceServiceTaxLine> tls = getSrvOrm()
      .retrieveListForField(pAddParam, pistlt, "itsOwner");
    for (SalesInvoiceServiceTaxLine pistl : tls) {
      getSrvOrm().deleteEntity(pAddParam, pistl);
    }
    this.prcAccEntityPbDelete.process(pAddParam, pEntity, pRequestData);
    // Beige-Orm refresh:
    pEntity.setItsOwner(getSrvOrm()
      .retrieveEntity(pAddParam, pEntity.getItsOwner()));
    // optimistic locking (dirty check):
    Long ownerVersion = Long.valueOf(pRequestData
      .getParameter(SalesInvoice.class.getSimpleName() + ".ownerVersion"));
    pEntity.getItsOwner().setItsVersion(ownerVersion);
    this.utlSalesGoodsServiceLine
      .updateOwner(pAddParam, pEntity.getItsOwner());
    pAddParam.put("nextEntity", pEntity.getItsOwner());
    pAddParam.put("nameOwnerEntity", SalesInvoice.class.getSimpleName());
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
   * @return PrcAccEntityPbDelete<RS, SalesInvoiceServiceLine, Long>
   **/
  public final IEntityProcessor<SalesInvoiceServiceLine, Long>
    getPrcAccEntityPbDelete() {
    return this.prcAccEntityPbDelete;
  }

  /**
   * <p>Setter for prcAccEntityPbDelete.</p>
   * @param pPrcAccEntityPbDelete reference
   **/
  public final void setPrcAccEntityPbDelete(
    final IEntityProcessor<SalesInvoiceServiceLine, Long>
      pPrcAccEntityPbDelete) {
    this.prcAccEntityPbDelete = pPrcAccEntityPbDelete;
  }

  /**
   * <p>Getter for utlSalesGoodsServiceLine.</p>
   * @return UtlSalesGoodsServiceLine<RS>
   **/
  public final UtlSalesGoodsServiceLine<RS>
    getUtlSalesGoodsServiceLine() {
    return this.utlSalesGoodsServiceLine;
  }

  /**
   * <p>Setter for utlSalesGoodsServiceLine.</p>
   * @param pUtlSalesGoodsServiceLine reference
   **/
  public final void setUtlSalesGoodsServiceLine(
    final UtlSalesGoodsServiceLine<RS> pUtlSalesGoodsServiceLine) {
    this.utlSalesGoodsServiceLine = pUtlSalesGoodsServiceLine;
  }
}

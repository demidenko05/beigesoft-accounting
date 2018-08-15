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
import java.math.BigDecimal;

import org.beigesoft.model.IRequestData;
import org.beigesoft.exception.ExceptionWithCode;
import org.beigesoft.service.IEntityProcessor;
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.accounting.model.ETaxType;
import org.beigesoft.accounting.persistable.InvItemTaxCategory;
import org.beigesoft.accounting.persistable.InvItemTaxCategoryLine;

/**
 * <p>Service that save InvItemTaxCategoryLine into DB.</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class PrcInvItemTaxCategoryLineSave<RS>
  implements IEntityProcessor<InvItemTaxCategoryLine, Long> {

  /**
   * <p>ORM service.</p>
   **/
  private ISrvOrm<RS> srvOrm;

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
  public final InvItemTaxCategoryLine process(
    final Map<String, Object> pAddParam,
      final InvItemTaxCategoryLine pEntity,
        final IRequestData pRequestData) throws Exception {
    if (pEntity.getItsPercentage().doubleValue() <= 0) {
      throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
        "percentage_wrong");
    }
    if (pEntity.getTax() == null) {
      throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
        "tax_wrong");
    }
    // Beige-Orm refresh:
    pEntity.setItsOwner(getSrvOrm()
      .retrieveEntity(pAddParam, pEntity.getItsOwner()));
    // optimistic locking (dirty check):
    Long ownerVersion = Long.valueOf(pRequestData
      .getParameter(InvItemTaxCategory.class.getSimpleName()
        + ".ownerVersion"));
    pEntity.getItsOwner().setItsVersion(ownerVersion);
    pEntity.setTax(getSrvOrm().retrieveEntity(pAddParam, pEntity.getTax()));
    if (!(ETaxType.SALES_TAX_INITEM.equals(pEntity.getTax().getItsType())
      || ETaxType.SALES_TAX_OUTITEM.equals(pEntity.getTax().getItsType()))) {
      throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
        "tax_wrong");
    }
    if (pEntity.getIsNew()) {
      getSrvOrm().insertEntity(pAddParam, pEntity);
      pEntity.setIsNew(false);
    } else {
      getSrvOrm().updateEntity(pAddParam, pEntity);
    }
    updateInvItemTaxCategory(pAddParam, pEntity.getItsOwner());
    pAddParam.put("nextEntity", pEntity.getItsOwner());
    pAddParam.put("nameOwnerEntity",
      InvItemTaxCategory.class.getSimpleName());
    return null;
  }

  //Utils: TODO save-delete same
  /**
   * <p>Update InvItemTaxCategory.</p>
   * @param pAddParam additional param
   * @param pOwner InvItemTaxCategory
   * @throws Exception - an exception
   **/
  public final void updateInvItemTaxCategory(
    final Map<String, Object> pAddParam,
      final InvItemTaxCategory pOwner) throws Exception {
    InvItemTaxCategoryLine iitcl = new InvItemTaxCategoryLine();
    iitcl.setItsOwner(pOwner);
    List<InvItemTaxCategoryLine> ptl = getSrvOrm()
      .retrieveListForField(pAddParam, iitcl,  "itsOwner");
    StringBuffer sb = new StringBuffer("");
    int i = 0;
    BigDecimal aggrPercent = BigDecimal.ZERO;
    for (InvItemTaxCategoryLine pt : ptl) {
      aggrPercent = aggrPercent.add(pt.getItsPercentage());
      if (i++ > 0) {
        sb.append(", ");
      }
      sb.append(pt.getTax().getItsName());
    }
    pOwner.setAggrOnlyPercent(aggrPercent);
    pOwner.setTaxesDescription(sb.toString());
    getSrvOrm().updateEntity(pAddParam, pOwner);
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
}

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
import java.math.BigDecimal;

import org.beigesoft.model.IRequestData;
import org.beigesoft.exception.ExceptionWithCode;
import org.beigesoft.service.IEntityProcessor;
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.service.ISrvDatabase;
import org.beigesoft.accounting.persistable.Wage;
import org.beigesoft.accounting.persistable.WageTaxLine;
import org.beigesoft.accounting.service.ISrvAccSettings;

/**
 * <p>Service that save WageTaxLine into DB.</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class PrcWageTaxLineSave<RS>
  implements IEntityProcessor<WageTaxLine, Long> {

  /**
   * <p>Database service.</p>
   **/
  private ISrvDatabase<RS> srvDatabase;

  /**
   * <p>ORM service.</p>
   **/
  private ISrvOrm<RS> srvOrm;

  /**
   * <p>Business service for accounting settings.</p>
   **/
  private ISrvAccSettings srvAccSettings;

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
  public final WageTaxLine process(
    final Map<String, Object> pAddParam,
      final WageTaxLine pEntity,
        final IRequestData pRequestData) throws Exception {
    if (pEntity.getItsTotal().doubleValue() == 0d) {
      throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
        "total_is_0");
    }
    if (pEntity.getItsPercentage().doubleValue() <= 0d) {
      throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
        "percentage_less_eq_0");
    }
    // Beige-Orm refresh:
    pEntity.setItsOwner(getSrvOrm()
      .retrieveEntity(pAddParam, pEntity.getItsOwner()));
    // optimistic locking (dirty check):
    Long ownerVersion = Long.valueOf(pRequestData
      .getParameter(Wage.class.getSimpleName() + ".ownerVersion"));
    pEntity.getItsOwner().setItsVersion(ownerVersion);
    if (pEntity.getItsOwner().getHasMadeAccEntries()) {
      throw new ExceptionWithCode(ExceptionWithCode.FORBIDDEN,
        "attempt_to_change_accounted_document");
    }
    BigDecimal taxDue = pEntity.getItsOwner().getItsTotal()
      .subtract(pEntity.getAllowance()).multiply(pEntity
        .getItsPercentage()).divide(BigDecimal.valueOf(100),
          getSrvAccSettings().lazyGetAccSettings(pAddParam).getPricePrecision(),
            getSrvAccSettings().lazyGetAccSettings(pAddParam).getRoundingMode())
              .add(pEntity.getPlusAmount());
    if (Math.abs(taxDue.doubleValue() - pEntity.getItsTotal()
      .doubleValue()) > 1d) {
      throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
        "total_does_not_conform_percentage");
    }
    if (pEntity.getIsNew()) {
      getSrvOrm().insertEntity(pAddParam, pEntity);
      pEntity.setIsNew(false);
    } else {
      getSrvOrm().updateEntity(pAddParam, pEntity);
    }
    updateOwner(pAddParam, pEntity);
    pAddParam.put("nextEntity", pEntity.getItsOwner());
    pAddParam.put("nameOwnerEntity", Wage.class.getSimpleName());
    return null;
  }

  //Utils:
  /**
   * <p>Insert immutable line into DB.</p>
   * @param pAddParam additional param
   * @param pEntity entity
   * @throws Exception - an exception
   **/
  public final void updateOwner(final Map<String, Object> pAddParam,
    final WageTaxLine pEntity) throws Exception {
    String query =
    "select sum(ITSTOTAL) as ITSTOTAL from WAGETAXLINE"
      + " join TAX on TAX.ITSID = WAGETAXLINE.TAX"
        + " where TAX.ITSTYPE=3 and ITSOWNER="
          + pEntity.getItsOwner().getItsId();
    Double total = getSrvDatabase().evalDoubleResult(query, "ITSTOTAL");
    if (total == null) {
      total = 0d;
    }
    pEntity.getItsOwner().setTotalTaxesEmployee(BigDecimal.valueOf(total)
      .setScale(getSrvAccSettings().lazyGetAccSettings(pAddParam)
        .getPricePrecision(), getSrvAccSettings()
          .lazyGetAccSettings(pAddParam).getRoundingMode()));
    query =
    "select sum(ITSTOTAL) as ITSTOTAL from WAGETAXLINE"
      + " join TAX on TAX.ITSID = WAGETAXLINE.TAX"
        + " where TAX.ITSTYPE=4 and ITSOWNER="
          + pEntity.getItsOwner().getItsId();
    total = getSrvDatabase().evalDoubleResult(query, "ITSTOTAL");
    if (total == null) {
      total = 0d;
    }
    pEntity.getItsOwner().setTotalTaxesEmployer(BigDecimal.valueOf(total)
      .setScale(getSrvAccSettings().lazyGetAccSettings(pAddParam)
        .getPricePrecision(), getSrvAccSettings()
          .lazyGetAccSettings(pAddParam).getRoundingMode()));
    pEntity.getItsOwner().setNetWage(pEntity.getItsOwner()
      .getItsTotal().subtract(pEntity.getItsOwner()
        .getTotalTaxesEmployee()));
    getSrvOrm().updateEntity(pAddParam, pEntity.getItsOwner());
  }

  //Simple getters and setters:
  /**
   * <p>Geter for srvDatabase.</p>
   * @return ISrvDatabase
   **/
  public final ISrvDatabase<RS> getSrvDatabase() {
    return this.srvDatabase;
  }

  /**
   * <p>Setter for srvDatabase.</p>
   * @param pSrvDatabase reference
   **/
  public final void setSrvDatabase(final ISrvDatabase<RS> pSrvDatabase) {
    this.srvDatabase = pSrvDatabase;
  }

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
   * <p>Getter for srvAccSettings.</p>
   * @return ISrvAccSettings
   **/
  public final ISrvAccSettings getSrvAccSettings() {
    return this.srvAccSettings;
  }

  /**
   * <p>Setter for srvAccSettings.</p>
   * @param pSrvAccSettings reference
   **/
  public final void setSrvAccSettings(final ISrvAccSettings pSrvAccSettings) {
    this.srvAccSettings = pSrvAccSettings;
  }
}

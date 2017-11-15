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

import org.beigesoft.exception.ExceptionWithCode;
import org.beigesoft.model.IRequestData;
import org.beigesoft.service.IEntityProcessor;
import org.beigesoft.accounting.persistable.Manufacture;

/**
 * <p>Service that make Manufacture copy from DB for reverse.</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class PrcManufactureGfr<RS>
  implements IEntityProcessor<Manufacture, Long> {

  /**
   * <p>Acc-entity copy delegator.</p>
   **/
  private IEntityProcessor<Manufacture, Long> prcAccDocGetForReverse;

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
  public final Manufacture process(
    final Map<String, Object> pAddParam,
      final Manufacture pEntity,
        final IRequestData pRequestData) throws Exception {
    Manufacture entity = this.prcAccDocGetForReverse
      .process(pAddParam, pEntity, pRequestData);
    if (entity.getItsQuantity().compareTo(entity.getTheRest()) != 0) {
      throw new ExceptionWithCode(ExceptionWithCode
        .WRONG_PARAMETER, "where_is_withdrawals_from_this_source");
    }
    entity.setItsQuantity(entity.getItsQuantity().negate());
    return entity;
  }

  //Simple getters and setters:
  /**
   * <p>Getter for prcAccDocGetForReverse.</p>
   * @return PrcAccDocGetForReverse<RS, Manufacture, Long>
   **/
  public final IEntityProcessor<Manufacture, Long>
    getPrcAccDocGetForReverse() {
    return this.prcAccDocGetForReverse;
  }

  /**
   * <p>Setter for prcAccDocGetForReverse.</p>
   * @param pPrcAccDocGetForReverse reference
   **/
  public final void setPrcAccDocGetForReverse(
    final IEntityProcessor<Manufacture, Long>
      pPrcAccDocGetForReverse) {
    this.prcAccDocGetForReverse = pPrcAccDocGetForReverse;
  }
}

package org.beigesoft.accounting.persistable;

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

import org.beigesoft.persistable.AI18nNameId;

/**
 * <p>
 * ID of I18N name of service.
 * </p>
 *
 * @author Yury Demidenko
 */
public class IdI18nServiceToSale extends AI18nNameId<ServiceToSale> {

  /**
   * <p>Internationalized thing.</p>
   **/
  private ServiceToSale hasName;

 /**
   * <p>Getter for hasName.</p>
   * @return ServiceToSale
   **/
  @Override
  public final ServiceToSale getHasName() {
    return this.hasName;
  }

  /**
   * <p>Setter for hasName.</p>
   * @param pHasName reference
   **/
  @Override
  public final void setHasName(final ServiceToSale pHasName) {
    this.hasName = pHasName;
  }
}

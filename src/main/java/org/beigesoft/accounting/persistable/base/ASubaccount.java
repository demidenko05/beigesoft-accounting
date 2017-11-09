package org.beigesoft.accounting.persistable.base;

/*
 * Copyright (c) 2016 Beigesoft ™
 *
 * Licensed under the GNU General Public License (GPL), Version 2.0
 * (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html
 */

import org.beigesoft.model.IHasVersion;
import org.beigesoft.persistable.AHasNameIdLong;
import org.beigesoft.model.IHasTypeCode;

/**
 * <pre>
 * Model of subaccount.
 * It used for subaccount/filter/business intelligence.
 * Version changed time algorithm.
 * </pre>
 *
 * @author Yury Demidenko
 */
public abstract class ASubaccount extends AHasNameIdLong
  implements IHasTypeCode, IHasVersion {

  /**
   * <p>Version, changed time algorithm cause check dirty of
   * calculated from it (derived) records.</p>
   **/
  private Long itsVersion;

  /**
   * <p>Geter for itsVersion.</p>
   * @return Long
   **/
  @Override
  public final Long getItsVersion() {
    return this.itsVersion;
  }

  /**
   * <p>Setter for itsVersion.</p>
   * @param pItsVersion reference/value
   **/
  @Override
  public final void setItsVersion(final Long pItsVersion) {
    this.itsVersion = pItsVersion;
  }
}

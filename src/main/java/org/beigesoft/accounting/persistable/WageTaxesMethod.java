package org.beigesoft.accounting.persistable;

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

import org.beigesoft.persistable.AHasNameIdLongVersion;

/**
 * <pre>
 * Wage tax table method.
 * Version changed time algorithm.
 * </pre>
 *
 * @author Yury Demidenko
 */
public class WageTaxesMethod extends AHasNameIdLongVersion {

  /**
   * <p>Service name, e.g. SrvWageTaxPercentageTable.</p>
   **/
  private String serviceName;

  //Simple getters and setters:
  /**
   * <p>Getter for serviceName.</p>
   * @return String
   **/
  public final String getServiceName() {
    return this.serviceName;
  }

  /**
   * <p>Setter for serviceName.</p>
   * @param pServiceName reference
   **/
  public final void setServiceName(final String pServiceName) {
    this.serviceName = pServiceName;
  }
}

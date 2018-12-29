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
 * Model of Currency.
 * </pre>
 *
 * @author Yury Demidenko
 */
public class Currency extends AHasNameIdLongVersion {

  /**
   * <p>Sign e.g. $</p>
   **/
  private String itsSign;

  /**
   * <p>String code, e.g. "USD" for integrating with systems
   * that use such currency identifiers instead of "840".</p>
   **/
  private String stCo;

  //Simple getters and setters:

  /**
   * <p>Getter for itsSign.</p>
   * @return String
   **/
  public final String getItsSign() {
    return this.itsSign;
  }

  /**
   * <p>Setter for itsSign.</p>
   * @param pItsSign reference
   **/
  public final void setItsSign(final String pItsSign) {
    this.itsSign = pItsSign;
  }

  /**
   * <p>Getter for stCo.</p>
   * @return String
   **/
  public final String getStCo() {
    return this.stCo;
  }

  /**
   * <p>Setter for stCo.</p>
   * @param pStCo reference
   **/
  public final void setStCo(final String pStCo) {
    this.stCo = pStCo;
  }
}

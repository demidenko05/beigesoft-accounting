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

import org.beigesoft.model.IOwned;
import org.beigesoft.persistable.APersistableBaseVersion;

/**
 * <pre>
 * Model of Wage Type Line of payroll tax table.
 * Version changed time algorithm.
 * </pre>
 *
 * @author Yury Demidenko
 */
public class WageTaxTableType extends APersistableBaseVersion
  implements IOwned<WageTaxTable> {

  /**
   * <p>Wage.</p>
   **/
  private WageTaxTable itsOwner;

  /**
   * <p>Wage Type, not null, e.g. Cooking, Sick compensation.</p>
   **/
  private WageType wageType;

  /**
   * <p>Getter for itsOwner.</p>
   * @return WageTaxTable
   **/
  @Override
  public final WageTaxTable getItsOwner() {
    return this.itsOwner;
  }

  /**
   * <p>Setter for itsOwner.</p>
   * @param pItsOwner reference
   **/
  @Override
  public final void setItsOwner(final WageTaxTable pItsOwner) {
    this.itsOwner = pItsOwner;
  }

  //Simple getters and setters:
  /**
   * <p>Getter for wageType.</p>
   * @return WageType
   **/
  public final WageType getWageType() {
    return this.wageType;
  }

  /**
   * <p>Setter for wageType.</p>
   * @param pWageType reference
   **/
  public final void setWageType(final WageType pWageType) {
    this.wageType = pWageType;
  }
}

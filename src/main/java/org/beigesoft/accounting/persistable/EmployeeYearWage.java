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

import java.math.BigDecimal;

import org.beigesoft.model.IOwned;
import org.beigesoft.persistable.APersistableBaseVersion;

/**
 * <pre>
 * Model of Employee Year Wage Line.
 * Version changed time algorithm.
 * </pre>
 *
 * @author Yury Demidenko
 */
public class EmployeeYearWage extends APersistableBaseVersion
  implements IOwned<Employee> {

  /**
   * <p>Wage.</p>
   **/
  private Employee itsOwner;

  /**
   * <p>Work Type.</p>
   **/
  private WageType wageType;

  /**
   * <p>Not Null, total wage of this type in current year.</p>
   **/
  private BigDecimal totalWageYear = BigDecimal.ZERO;

  /**
   * <p>Getter for itsOwner.</p>
   * @return Employee
   **/
  @Override
  public final Employee getItsOwner() {
    return this.itsOwner;
  }

  /**
   * <p>Setter for itsOwner.</p>
   * @param pItsOwner reference
   **/
  @Override
  public final void setItsOwner(final Employee pItsOwner) {
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

  /**
   * <p>Getter for totalWageYear.</p>
   * @return BigDecimal
   **/
  public final BigDecimal getTotalWageYear() {
    return this.totalWageYear;
  }

  /**
   * <p>Setter for totalWageYear.</p>
   * @param pTotalWageYear reference
   **/
  public final void setTotalWageYear(final BigDecimal pTotalWageYear) {
    this.totalWageYear = pTotalWageYear;
  }
}

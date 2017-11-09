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

import java.util.Date;
import java.util.List;

import org.beigesoft.model.IHasTypeCode;
import org.beigesoft.persistable.APersistableBaseVersion;

/**
 * <pre>
 * Model of Move Items within/between warehouse/s.
 * Version changed time algorithm.
 * </pre>
 *
 * @author Yury Demidenko
 */
public class MoveItems extends APersistableBaseVersion
  implements IHasTypeCode {

  /**
   * <p>Date.</p>
   **/
  private Date itsDate;

  /**
   * <p>Lines.</p>
   **/
  private List<MoveItemsLine> itsLines;

  /**
   * <p>Description.</p>
   **/
  private String description;

  /**
   * <p>OOP friendly Constant of code type 14.</p>
   **/
  @Override
  public final Integer constTypeCode() {
    return 14;
  }

  //Hiding references getters and setters:
  /**
   * <p>Geter for itsDate.</p>
   * @return Date
   **/
  public final Date getItsDate() {
    if (this.itsDate == null) {
      return null;
    }
    return new Date(this.itsDate.getTime());
  }

  /**
   * <p>Setter for itsDate.</p>
   * @param pItsDate reference
   **/
  public final void setItsDate(final Date pItsDate) {
    if (pItsDate == null) {
      this.itsDate = null;
    } else {
      this.itsDate = new Date(pItsDate.getTime());
    }
  }

  //Simple getters and setters:
  /**
   * <p>Getter for description.</p>
   * @return String
   **/
  public final String getDescription() {
    return this.description;
  }

  /**
   * <p>Setter for description.</p>
   * @param pDescription reference
   **/
  public final void setDescription(final String pDescription) {
    this.description = pDescription;
  }

  /**
   * <p>Geter for itsLines.</p>
   * @return List<MoveItemsLine>
   **/
  public final List<MoveItemsLine> getItsLines() {
    return this.itsLines;
  }

  /**
   * <p>Setter for itsLines.</p>
   * @param pItsLines reference
   **/
  public final void setItsLines(final List<MoveItemsLine> pItsLines) {
    this.itsLines = pItsLines;
  }
}

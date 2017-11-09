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
import java.math.BigDecimal;

import org.beigesoft.persistable.IPersistableBase;
import org.beigesoft.model.IHasTypeCode;

/**
 * <pre>
 * Abstract model of document that makes accounting entry,
 * e.g. PurchaseInvoice, Manufacture
 * </pre>
 *
 * @author Yury Demidenko
 */
public interface IDoc extends IPersistableBase, IHasTypeCode {

  /**
   * <p>Geter for hasMadeAccEntries.</p>
   * @return Boolean
   **/
  Boolean getHasMadeAccEntries();

  /**
   * <p>Setter for hasMadeAccEntries.</p>
   * @param pHasMadeAccEntries reference
   **/
  void setHasMadeAccEntries(Boolean pHasMadeAccEntries);

  /**
   * <p>Getter for reversed database Id.</p>
   * @return Integer DB birth ID
   **/
  Integer getReversedIdDatabaseBirth();

  /**
   * <p>Setter for reversed database Id.</p>
   * @param pReversedIdDatabaseBirth reference
   **/
  void setReversedIdDatabaseBirth(Integer pReversedIdDatabaseBirth);

  /**
   * <p>Getter for reversedId.</p>
   * @return Long
   **/
  Long getReversedId();

  /**
   * <p>Setter for reversedId.</p>
   * @param pReversedId reference
   **/
  void setReversedId(Long pReversedId);

  /**
   * <p>Geter for itsDate.</p>
   * @return Date
   **/
  Date getItsDate();

  /**
   * <p>Setter for itsDate.</p>
   * @param pItsDate reference
   **/
  void setItsDate(Date pItsDate);

  /**
   * <p>Setter for itsTotal.</p>
   * @param pItsTotal reference
   **/
  void setItsTotal(BigDecimal pItsTotal);

  /**
   * <p>Getter for itsTotal.</p>
   * @return BigDecimal
   **/
  BigDecimal getItsTotal();

  /**
   * <p>Getter for description.</p>
   * @return String
   **/
  String getDescription();

  /**
   * <p>Setter for description.</p>
   * @param pDescription reference
   **/
  void setDescription(String pDescription);
}

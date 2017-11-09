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
 * Abstract model of entity that makes warehouse entry load(put)
 * or withdrawal, e.g. PurchaseInvoiceLine, SalesInvoiceLine, Manufacture.
 * </pre>
 *
 * @author Yury Demidenko
 */
public interface IMakingWarehouseEntry extends IPersistableBase, IHasTypeCode {

  /**
   * <p>Geter for invItem.</p>
   * @return InvItem
   **/
  InvItem getInvItem();

  /**
   * <p>Setter for invItem.</p>
   * @param pInvItem reference
   **/
  void setInvItem(InvItem pInvItem);

  /**
   * <p>Geter for unitOfMeasure.</p>
   * @return UnitOfMeasure
   **/
  UnitOfMeasure getUnitOfMeasure();

  /**
   * <p>Setter for unitOfMeasure.</p>
   * @param pUnitOfMeasure reference
   **/
  void setUnitOfMeasure(UnitOfMeasure pUnitOfMeasure);

  /**
   * <p>Geter for itsQuantity.</p>
   * @return BigDecimal
   **/
  BigDecimal getItsQuantity();

  /**
   * <p>Setter for itsQuantity.</p>
   * @param pItsQuantity reference
   **/
  void setItsQuantity(BigDecimal pItsQuantity);

  /**
   * <p>Geter for reversedId.</p>
   * @return Long
   **/
  Long getReversedId();

  /**
   * <p>Setter for reversedId.</p>
   * @param pReversedId reference
   **/
  void setReversedId(Long pReversedId);

  /**
   * <p>Get for owner's ID if exist e.g. PurchaseInvoice ID.</p>
   * @return Long
   **/
  Long getOwnerId();

  /**
   * <p>Get Owner Type if exist  e.g. PurchaseInvoice 1.</p>
   * @return Integer
   **/
  Integer getOwnerType();

  /**
   * <p>Get for document Date.</p>
   * @return Date
   **/
  Date getDocumentDate();

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

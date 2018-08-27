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

import java.math.BigDecimal;

import org.beigesoft.model.IOwned;
import org.beigesoft.model.IHasVersion;
import org.beigesoft.persistable.IPersistableBase;

/**
 * <p>
 * Abstraction of Invoice Line.
 * </p>
 *
 * @param <T> invoice type
 * @author Yury Demidenko
 */
public interface IInvoiceLine<T extends IInvoice>
  extends IHasVersion, IPersistableBase, IOwned<T> {

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
   * <p>Getter for totalTaxes.</p>
   * @return BigDecimal
   **/
  BigDecimal getTotalTaxes();

  /**
   * <p>Setter for totalTaxes.</p>
   * @param pTotalTaxes reference
   **/
  void setTotalTaxes(BigDecimal pTotalTaxes);

  /**
   * <p>Getter for taxesDescription.</p>
   * @return String
   **/
  String getTaxesDescription();

  /**
   * <p>Setter for taxesDescription.</p>
   * @param pTaxesDescription reference
   **/
  void setTaxesDescription(String pTaxesDescription);

  /**
   * <p>Getter for itsTotal.</p>
   * @return BigDecimal
   **/
  BigDecimal getItsTotal();

  /**
   * <p>Setter for itsTotal.</p>
   * @param pItsTotal reference
   **/
  void setItsTotal(BigDecimal pItsTotal);

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
   * <p>Geter for subtotal.</p>
   * @return BigDecimal
   **/
  BigDecimal getSubtotal();

  /**
   * <p>Setter for subtotal.</p>
   * @param pSubtotal reference
   **/
  void setSubtotal(BigDecimal pSubtotal);
  /**
   * <p>Getter for foreignPrice.</p>
   * @return BigDecimal
   **/
  BigDecimal getForeignPrice();

  /**
   * <p>Setter for foreignPrice.</p>
   * @param pForeignPrice reference
   **/
  void setForeignPrice(BigDecimal pForeignPrice);

  /**
   * <p>Getter for foreignSubtotal.</p>
   * @return BigDecimal
   **/
  BigDecimal getForeignSubtotal();

  /**
   * <p>Setter for foreignSubtotal.</p>
   * @param pForeignSubtotal reference
   **/
  void setForeignSubtotal(BigDecimal pForeignSubtotal);

  /**
   * <p>Getter for foreignTotalTaxes.</p>
   * @return BigDecimal
   **/
  BigDecimal getForeignTotalTaxes();

  /**
   * <p>Setter for foreignTotalTaxes.</p>
   * @param pForeignTotalTaxes reference
   **/
  void setForeignTotalTaxes(BigDecimal pForeignTotalTaxes);

  /**
   * <p>Getter for foreignTotal.</p>
   * @return BigDecimal
   **/
  BigDecimal getForeignTotal();

  /**
   * <p>Setter for foreignTotal.</p>
   * @param pForeignTotal reference
   **/
  void setForeignTotal(BigDecimal pForeignTotal);

  /**
   * <p>Getter for taxCategory.</p>
   * @return InvItemTaxCategory
   **/
  InvItemTaxCategory getTaxCategory();

  /**
   * <p>Setter for taxCategory.</p>
   * @param pTaxCategory reference
   **/
  void setTaxCategory(InvItemTaxCategory pTaxCategory);
}

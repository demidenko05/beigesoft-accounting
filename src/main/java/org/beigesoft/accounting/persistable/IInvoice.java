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

/**
 * <p>
 * Abstract model of sales/purchase invoice.
 * </p>
 *
 * @author Yury Demidenko
 */
public interface IInvoice extends IDocWarehouse {

  /**
   * <p>Getter for foreignCurrency.</p>
   * @return Currency
   **/
  Currency getForeignCurrency();

  /**
   * <p>Setter for foreignCurrency.</p>
   * @param pForeignCurrency reference
   **/
  void setForeignCurrency(Currency pForeignCurrency);

  /**
   * <p>Getter for exchangeRate.</p>
   * @return BigDecimal
   **/
  BigDecimal getExchangeRate();

  /**
   * <p>Setter for exchangeRate.</p>
   * @param pExchangeRate reference
   **/
  void setExchangeRate(BigDecimal pExchangeRate);

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
   * <p>Getter for priceIncTax.</p>
   * @return Boolean
   **/
  Boolean getPriceIncTax();

  /**
   * <p>Setter for priceIncTax.</p>
   * @param pPriceIncTax reference
   **/
  void setPriceIncTax(Boolean pPriceIncTax);

  /**
   * <p>Getter for omitTaxes.</p>
   * @return Boolean
   **/
  Boolean getOmitTaxes();

  /**
   * <p>Setter for omitTaxes.</p>
   * @param pOmitTaxes reference
   **/
  void setOmitTaxes(Boolean pOmitTaxes);
}

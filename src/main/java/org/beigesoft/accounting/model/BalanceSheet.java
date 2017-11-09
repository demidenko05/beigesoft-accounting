package org.beigesoft.accounting.model;

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
import java.util.ArrayList;
import java.math.BigDecimal;

/**
 * <pre>
 * Balance Sheet.
 * </pre>
 *
 * @author Yury Demidenko
 */
public class BalanceSheet {

  /**
   * <p>Lines.</p>
   **/
  private List<BalanceLine> itsLines = new ArrayList<BalanceLine>();

  /**
   * <p>Total assets.</p>
   **/
  private BigDecimal totalAssets = BigDecimal.ZERO;

  /**
   * <p>Total liabilities.</p>
   **/
  private BigDecimal totalLiabilities = BigDecimal.ZERO;

  /**
   * <p>Total owners equity.</p>
   **/
  private BigDecimal totalOwnersEquity = BigDecimal.ZERO;

  /**
   * <p>Total lines assets.</p>
   **/
  private Integer totalLinesAssets = 0;

  /**
   * <p>Total lines liabilities.</p>
   **/
  private Integer totalLinesLiabilities = 0;

  /**
   * <p>Total lines owners equity.</p>
   **/
  private Integer totalLinesOwnersEquity = 0;

  /**
   * <p>Detail Rows Count (assets vs l&oe).</p>
   **/
  private Integer detailRowsCount = 0;

  /**
   * <p>Date of balance.</p>
   **/
  private Date itsDate;

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
   * <p>Getter for itsLines.</p>
   * @return List<BalanceLine>
   **/
  public final List<BalanceLine> getItsLines() {
    return this.itsLines;
  }

  /**
   * <p>Setter for itsLines.</p>
   * @param pItsLines reference
   **/
  public final void setItsLines(final List<BalanceLine> pItsLines) {
    this.itsLines = pItsLines;
  }

  /**
   * <p>Getter for totalAssets.</p>
   * @return BigDecimal
   **/
  public final BigDecimal getTotalAssets() {
    return this.totalAssets;
  }

  /**
   * <p>Setter for totalAssets.</p>
   * @param pTotalAssets reference
   **/
  public final void setTotalAssets(final BigDecimal pTotalAssets) {
    this.totalAssets = pTotalAssets;
  }

  /**
   * <p>Getter for totalLiabilities.</p>
   * @return BigDecimal
   **/
  public final BigDecimal getTotalLiabilities() {
    return this.totalLiabilities;
  }

  /**
   * <p>Setter for totalLiabilities.</p>
   * @param pTotalLiabilities reference
   **/
  public final void setTotalLiabilities(final BigDecimal pTotalLiabilities) {
    this.totalLiabilities = pTotalLiabilities;
  }

  /**
   * <p>Getter for totalOwnersEquity.</p>
   * @return BigDecimal
   **/
  public final BigDecimal getTotalOwnersEquity() {
    return this.totalOwnersEquity;
  }

  /**
   * <p>Setter for totalOwnersEquity.</p>
   * @param pTotalOwnersEquity reference
   **/
  public final void setTotalOwnersEquity(final BigDecimal pTotalOwnersEquity) {
    this.totalOwnersEquity = pTotalOwnersEquity;
  }

  /**
   * <p>Getter for totalLinesAssets.</p>
   * @return Integer
   **/
  public final Integer getTotalLinesAssets() {
    return this.totalLinesAssets;
  }

  /**
   * <p>Setter for totalLinesAssets.</p>
   * @param pTotalLinesAssets reference
   **/
  public final void setTotalLinesAssets(final Integer pTotalLinesAssets) {
    this.totalLinesAssets = pTotalLinesAssets;
  }

  /**
   * <p>Getter for totalLinesLiabilities.</p>
   * @return Integer
   **/
  public final Integer getTotalLinesLiabilities() {
    return this.totalLinesLiabilities;
  }

  /**
   * <p>Setter for totalLinesLiabilities.</p>
   * @param pTotalLinesLiabilities reference
   **/
  public final void setTotalLinesLiabilities(
    final Integer pTotalLinesLiabilities) {
    this.totalLinesLiabilities = pTotalLinesLiabilities;
  }

  /**
   * <p>Getter for totalLinesOwnersEquity.</p>
   * @return Integer
   **/
  public final Integer getTotalLinesOwnersEquity() {
    return this.totalLinesOwnersEquity;
  }

  /**
   * <p>Setter for totalLinesOwnersEquity.</p>
   * @param pTotalLinesOwnersEquity reference
   **/
  public final void setTotalLinesOwnersEquity(
    final Integer pTotalLinesOwnersEquity) {
    this.totalLinesOwnersEquity = pTotalLinesOwnersEquity;
  }

  /**
   * <p>Getter for detailRowsCount.</p>
   * @return Integer
   **/
  public final Integer getDetailRowsCount() {
    return this.detailRowsCount;
  }

  /**
   * <p>Setter for detailRowsCount.</p>
   * @param pDetailRowsCount reference
   **/
  public final void setDetailRowsCount(final Integer pDetailRowsCount) {
    this.detailRowsCount = pDetailRowsCount;
  }
}

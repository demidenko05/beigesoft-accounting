package org.beigesoft.accounting.persistable;

/*
 * Copyright (c) 2016 Beigesoft™
 *
 * Licensed under the GNU General Public License (GPL), Version 2.0
 * (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html
 */

import java.util.List;
import java.util.Date;
import java.math.RoundingMode;

import org.beigesoft.doc.model.EPageSize;
import org.beigesoft.doc.model.EPageOrientation;
import org.beigesoft.model.EPeriod;
import org.beigesoft.persistable.AHasIdLongVersion;

/**
 * <p>
 * Accounting settings.
 * Version changed time algorithm.
 * </p>
 *
 * @author Yury Demidenko
 */
public class AccSettings extends AHasIdLongVersion {

  /**
   * <p>Date current accounting year to prevent wrong accounting entries.</p>
   **/
  private Date currentAccYear;

  /**
   * <p>Not Null, if sales tax vendor sales taxes fields will be appeared
   * in sales invoice, and taxes will be extracted into SalesTaxPayable.</p>
   **/
  private Boolean isExtractSalesTaxFromSales = false;

  /**
   * <p>Not Null, if sales tax vendor sales taxes fields will
   * be appeared in purchase invoice, and taxes will be extracted
   * into SalesTaxFromPurchase, this is for methods where payed taxes
   * from purchase should be extracted from inventory e.g. VAT
   * or sales taxes that should be capitalized (USA producing).</p>
   **/
  private Boolean isExtractSalesTaxFromPurchase = false;

  /**
   * <p>Organization name.</p>
   **/
  private String organization;

  /**
   * <p>Registered email.</p>
   **/
  private String regEmail;

  /**
   * <p>Registered address1.</p>
   **/
  private String regAddress1;

  /**
   * <p>Registered address2.</p>
   **/
  private String regAddress2;

  /**
   * <p>Registered Zip.</p>
   **/
  private String regZip;

  /**
   * <p>Registered Country.</p>
   **/
  private String regCountry;

  /**
   * <p>Registered State.</p>
   **/
  private String regState;

  /**
   * <p>Registered City.</p>
   **/
  private String regCity;

  /**
   * <p>Registered Phone.</p>
   **/
  private String regPhone;

  /**
   * <p>Tax identification number e.g. SSN for US.</p>
   **/
  private String taxIdentificationNumber;

  /**
   * <p>Cost precision.</p>
   **/
  private Integer costPrecision = 4;

  /**
   * <p>Price precision.</p>
   **/
  private Integer pricePrecision = 2;

  /**
   * <p>Balance precision.</p>
   **/
  private Integer balancePrecision = 1;

  /**
   * <p>Quantity precision.</p>
   **/
  private Integer quantityPrecision = 2;

  /**
   * <p>Tax precision.</p>
   **/
  private Integer taxPrecision = 3;

  /**
   * <p>Rounding mode.</p>
   **/
  private RoundingMode roundingMode = RoundingMode.HALF_UP;

  /**
   * <p>Currency.</p>
   **/
  private Currency currency;

  /**
   * <p>COGS method, e.g. FIFO Perpetual.</p>
   **/
  private CogsMethod cogsMethod;

  /**
   * <p>Balance store period, not null, EPeriod.DAILY/WEEKLY/MONTHLY.</p>
   **/
  private EPeriod balanceStorePeriod = EPeriod.MONTHLY;

  /**
   * <p>Description.</p>
   **/
  private String description;

  /**
   * <p>Accounting entries sources.</p>
   **/
  private List<AccEntriesSourcesLine> accEntriesSources;

  /**
   * <p>Sources for InvItem to be draw (they have theRest>0).</p>
   **/
  private List<CogsItemSourcesLine> cogsItemSources;

  /**
   * <p>Sources for InvItem of type material to be draw by manufacture
   * (they have theRest>0).</p>
   **/
  private List<DrawMaterialSourcesLine> drawMaterialSources;

  /**
   * <p>Method(service in factory app-beans)
   * that fill wage tax lines, not null.</p>
   **/
  private WageTaxesMethod wageTaxesMethod;

  /**
   * <p>Not Null, if  if uses currency sign in reports (e.g. $),
   * otherwise itsName (e.g. USD).</p>
   **/
  private Boolean useCurrencySign = false;

  /**
   * <p>Not Null, if print currency on left of amount
   * e.g. "1,356.12$" or "$1,356.12".</p>
   **/
  private Boolean printCurrencyLeft = false;

  /**
   * <p>Rounding mode for sales taxes.</p>
   **/
  private RoundingMode salTaxRoundMode = RoundingMode.HALF_UP;

  /**
   * <p>Grouping method for sales taxes - false item basis, true - invoice.
   * This is about grouping rounding error:
   * round(2.244 + 2.244) != round(2.244) + round(2.244);
   * 4.49 != 4.48
   * </p>
   **/
  private Boolean salTaxIsInvoiceBase = Boolean.FALSE;

  /**
   * <p>Use aggregate tax rate or only for item basis method.</p>
   **/
  private Boolean salTaxUseAggregItBas = Boolean.FALSE;

  /**
   * <p>TTF file name, DejaVuSerif default.</p>
   **/
  private String ttfFileName = "DejaVuSerif";

  /**
   * <p>TTF bold file name, DejaVuSerif-Bold default.
   * May be empty for hieroglyph's fonts.</p>
   **/
  private String ttfBoldFileName = "DejaVuSerif-Bold";

  /**
   * <p>Invoice, balance reports page size, A4 default.</p>
   **/
  private EPageSize pageSize = EPageSize.A4;

  /**
   * <p>Invoice, balance reports page orientation, PORTRAIT default.</p>
   **/
  private EPageOrientation pageOrientation = EPageOrientation.PORTRAIT;

  /**
   * <p>Invoice, balance reports margin Left,
   * Letter - inch, otherwise millimeters, default 30mm.</p>
   **/
  private Double marginLeft = 30.0;

  /**
   * <p>Invoice, balance reports Margin Right,
   * Letter - inch, otherwise millimeters, default 15mm.</p>
   **/
  private Double marginRight = 15.0;

  /**
   * <p>Invoice, balance reports Margin Top.</p>
   **/
  private Double marginTop = 20.0;

  /**
   * <p>Invoice, balance reports Margin Bottom,
   * Letter - inch, otherwise millimeters, default 20mm.</p>
   **/
  private Double marginBottom = 20.0;

  /**
   * <p>Invoice, balance reports Font size,
   * Letter - inch, otherwise millimeters, default 3.5mm (0.1378INCH).</p>
   **/
  private Double fontSize = 3.5;

  //Hiding references getters and setters:
  /**
   * <p>Getter for currentAccYear.</p>
   * @return Date
   **/
  public final Date getCurrentAccYear() {
    if (this.currentAccYear == null) {
      return null;
    }
    return new Date(this.currentAccYear.getTime());
   }

  /**
   * <p>Setter for currentAccYear.</p>
   * @param pCurrentAccYear reference
   **/
  public final void setCurrentAccYear(
    final Date pCurrentAccYear) {
    if (pCurrentAccYear == null) {
      this.currentAccYear = null;
    } else {
      this.currentAccYear = new Date(pCurrentAccYear.getTime());
    }
  }

  //Simple getters and setters:
  /**
   * <p>Setter for costPrecision.</p>
   * @param pCostPrecision reference
   **/
  public final void setCostPrecision(final Integer pCostPrecision) {
    this.costPrecision = pCostPrecision;
  }

  /**
   * <p>Setter for roundingMode.</p>
   * @param pRoundingMode reference
   **/
  public final void setRoundingMode(final RoundingMode pRoundingMode) {
    this.roundingMode = pRoundingMode;
  }

  /**
   * <p>Setter for pricePrecision.</p>
   * @param pPricePrecision reference
   **/
  public final void setPricePrecision(final Integer pPricePrecision) {
    this.pricePrecision = pPricePrecision;
  }

  /**
   * <p>Setter for balancePrecision.</p>
   * @param pBalancePrecision reference
   **/
  public final void setBalancePrecision(final Integer pBalancePrecision) {
    this.balancePrecision = pBalancePrecision;
  }

  /**
   * <p>Geter for organization.</p>
   * @return String
   **/
  public final String getOrganization() {
    return this.organization;
  }

  /**
   * <p>Setter for organization.</p>
   * @param pOrganization reference
   **/
  public final void setOrganization(final String pOrganization) {
    this.organization = pOrganization;
  }

  /**
   * <p>Getter for taxIdentificationNumber.</p>
   * @return String
   **/
  public final String getTaxIdentificationNumber() {
    return this.taxIdentificationNumber;
  }

  /**
   * <p>Setter for taxIdentificationNumber.</p>
   * @param pTaxIdentificationNumber reference
   **/
  public final void setTaxIdentificationNumber(
    final String pTaxIdentificationNumber) {
    this.taxIdentificationNumber = pTaxIdentificationNumber;
  }

  /**
   * <p>Getter for isExtractSalesTaxFromSales.</p>
   * @return Boolean
   **/
  public final Boolean getIsExtractSalesTaxFromSales() {
    return this.isExtractSalesTaxFromSales;
  }

  /**
   * <p>Setter for isExtractSalesTaxFromSales.</p>
   * @param pIsExtractSalesTaxFromSales reference
   **/
  public final void setIsExtractSalesTaxFromSales(
    final Boolean pIsExtractSalesTaxFromSales) {
    this.isExtractSalesTaxFromSales = pIsExtractSalesTaxFromSales;
  }

  /**
   * <p>Getter for isExtractSalesTaxFromPurchase.</p>
   * @return Boolean
   **/
  public final Boolean getIsExtractSalesTaxFromPurchase() {
    return this.isExtractSalesTaxFromPurchase;
  }

  /**
   * <p>Setter for isExtractSalesTaxFromPurchase.</p>
   * @param pIsExtractSalesTaxFromPurchase reference
   **/
  public final void setIsExtractSalesTaxFromPurchase(
    final Boolean pIsExtractSalesTaxFromPurchase) {
    this.isExtractSalesTaxFromPurchase = pIsExtractSalesTaxFromPurchase;
  }

  /**
   * <p>Geter for currency.</p>
   * @return Currency
   **/
  public final Currency getCurrency() {
    return this.currency;
  }

  /**
   * <p>Setter for currency.</p>
   * @param pCurrency reference
   **/
  public final void setCurrency(final Currency pCurrency) {
    this.currency = pCurrency;
  }

  /**
   * <p>Geter for cogsMethod.</p>
   * @return CogsMethod
   **/
  public final CogsMethod getCogsMethod() {
    return this.cogsMethod;
  }

  /**
   * <p>Setter for cogsMethod.</p>
   * @param pCogsMethod reference
   **/
  public final void setCogsMethod(final CogsMethod pCogsMethod) {
    this.cogsMethod = pCogsMethod;
  }

  /**
   * <p>Getter for balanceStorePeriod.</p>
   * @return EPeriod
   **/
  public final EPeriod getBalanceStorePeriod() {
    return this.balanceStorePeriod;
  }

  /**
   * <p>Setter for balanceStorePeriod.</p>
   * @param pBalanceStorePeriod reference
   **/
  public final void setBalanceStorePeriod(final EPeriod pBalanceStorePeriod) {
    this.balanceStorePeriod = pBalanceStorePeriod;
  }

  /**
   * <p>Geter for description.</p>
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
   * <p>Getter for accEntriesSources.</p>
   * @return List<AccEntriesSourcesLine>
   **/
  public final List<AccEntriesSourcesLine> getAccEntriesSources() {
    return this.accEntriesSources;
  }

  /**
   * <p>Setter for accEntriesSources.</p>
   * @param pAccEntriesSources reference
   **/
  public final void setAccEntriesSources(
    final List<AccEntriesSourcesLine> pAccEntriesSources) {
    this.accEntriesSources = pAccEntriesSources;
  }

  /**
   * <p>Getter for cogsItemSources.</p>
   * @return List<CogsItemSourcesLine>
   **/
  public final List<CogsItemSourcesLine> getCogsItemSources() {
    return this.cogsItemSources;
  }

  /**
   * <p>Setter for cogsItemSources.</p>
   * @param pCogsItemSources reference
   **/
  public final void setCogsItemSources(
    final List<CogsItemSourcesLine> pCogsItemSources) {
    this.cogsItemSources = pCogsItemSources;
  }

  /**
   * <p>Geter for drawMaterialSources.</p>
   * @return List<DrawMaterialSourcesLine>
   **/
  public final List<DrawMaterialSourcesLine> getDrawMaterialSources() {
    return this.drawMaterialSources;
  }

  /**
   * <p>Setter for drawMaterialSources.</p>
   * @param pDrawMaterialSources reference
   **/
  public final void setDrawMaterialSources(
    final List<DrawMaterialSourcesLine> pDrawMaterialSources) {
    this.drawMaterialSources = pDrawMaterialSources;
  }

  /**
   * <p>Getter for roundingMode.</p>
   * @return RoundingMode
   **/
  public final RoundingMode getRoundingMode() {
    return this.roundingMode;
  }

  /**
   * <p>Getter for costPrecision.</p>
   * @return Integer
   **/
  public final Integer getCostPrecision() {
    return this.costPrecision;
  }

  /**
   * <p>Getter for quantityPrecision.</p>
   * @return Integer
   **/
  public final Integer getQuantityPrecision() {
    return this.quantityPrecision;
  }

  /**
   * <p>Setter for quantityPrecision.</p>
   * @param pQuantityPrecision reference
   **/
  public final void setQuantityPrecision(final Integer pQuantityPrecision) {
    this.quantityPrecision = pQuantityPrecision;
  }

  /**
   * <p>Getter for pricePrecision.</p>
   * @return Integer
   **/
  public final Integer getPricePrecision() {
    return this.pricePrecision;
  }

  /**
   * <p>Getter for balancePrecision.</p>
   * @return Integer
   **/
  public final Integer getBalancePrecision() {
    return this.balancePrecision;
  }

  /**
   * <p>Getter for wageTaxesMethod.</p>
   * @return WageTaxesMethod
   **/
  public final WageTaxesMethod getWageTaxesMethod() {
    return this.wageTaxesMethod;
  }

  /**
   * <p>Setter for wageTaxesMethod.</p>
   * @param pWageTaxesMethod reference
   **/
  public final void setWageTaxesMethod(
    final WageTaxesMethod pWageTaxesMethod) {
    this.wageTaxesMethod = pWageTaxesMethod;
  }

  /**
   * <p>Getter for regEmail.</p>
   * @return String
   **/
  public final String getRegEmail() {
    return this.regEmail;
  }

  /**
   * <p>Setter for regEmail.</p>
   * @param pRegEmail reference
   **/
  public final void setRegEmail(final String pRegEmail) {
    this.regEmail = pRegEmail;
  }

  /**
   * <p>Getter for regAddress1.</p>
   * @return String
   **/
  public final String getRegAddress1() {
    return this.regAddress1;
  }

  /**
   * <p>Setter for regAddress1.</p>
   * @param pRegAddress1 reference
   **/
  public final void setRegAddress1(final String pRegAddress1) {
    this.regAddress1 = pRegAddress1;
  }

  /**
   * <p>Getter for regAddress2.</p>
   * @return String
   **/
  public final String getRegAddress2() {
    return this.regAddress2;
  }

  /**
   * <p>Setter for regAddress2.</p>
   * @param pRegAddress2 reference
   **/
  public final void setRegAddress2(final String pRegAddress2) {
    this.regAddress2 = pRegAddress2;
  }

  /**
   * <p>Getter for regZip.</p>
   * @return String
   **/
  public final String getRegZip() {
    return this.regZip;
  }

  /**
   * <p>Setter for regZip.</p>
   * @param pRegZip reference
   **/
  public final void setRegZip(final String pRegZip) {
    this.regZip = pRegZip;
  }

  /**
   * <p>Getter for regCountry.</p>
   * @return String
   **/
  public final String getRegCountry() {
    return this.regCountry;
  }

  /**
   * <p>Setter for regCountry.</p>
   * @param pRegCountry reference
   **/
  public final void setRegCountry(final String pRegCountry) {
    this.regCountry = pRegCountry;
  }

  /**
   * <p>Getter for regState.</p>
   * @return String
   **/
  public final String getRegState() {
    return this.regState;
  }

  /**
   * <p>Setter for regState.</p>
   * @param pRegState reference
   **/
  public final void setRegState(final String pRegState) {
    this.regState = pRegState;
  }

  /**
   * <p>Getter for regCity.</p>
   * @return String
   **/
  public final String getRegCity() {
    return this.regCity;
  }

  /**
   * <p>Setter for regCity.</p>
   * @param pRegCity reference
   **/
  public final void setRegCity(final String pRegCity) {
    this.regCity = pRegCity;
  }

  /**
   * <p>Getter for regPhone.</p>
   * @return Long
   **/
  public final String getRegPhone() {
    return this.regPhone;
  }

  /**
   * <p>Setter for regPhone.</p>
   * @param pRegPhone reference
   **/
  public final void setRegPhone(final String pRegPhone) {
    this.regPhone = pRegPhone;
  }

  /**
   * <p>Getter for useCurrencySign.</p>
   * @return Boolean
   **/
  public final Boolean getUseCurrencySign() {
    return this.useCurrencySign;
  }

  /**
   * <p>Setter for useCurrencySign.</p>
   * @param pUseCurrencySign reference
   **/
  public final void setUseCurrencySign(final Boolean pUseCurrencySign) {
    this.useCurrencySign = pUseCurrencySign;
  }

  /**
   * <p>Getter for printCurrencyLeft.</p>
   * @return Boolean
   **/
  public final Boolean getPrintCurrencyLeft() {
    return this.printCurrencyLeft;
  }

  /**
   * <p>Setter for printCurrencyLeft.</p>
   * @param pPrintCurrencyLeft reference
   **/
  public final void setPrintCurrencyLeft(final Boolean pPrintCurrencyLeft) {
    this.printCurrencyLeft = pPrintCurrencyLeft;
  }

  /**
   * <p>Getter for salTaxRoundMode.</p>
   * @return RoundingMode
   **/
  public final RoundingMode getSalTaxRoundMode() {
    return this.salTaxRoundMode;
  }

  /**
   * <p>Setter for salTaxRoundMode.</p>
   * @param pSalTaxRoundMode reference
   **/
  public final void setSalTaxRoundMode(final RoundingMode pSalTaxRoundMode) {
    this.salTaxRoundMode = pSalTaxRoundMode;
  }

  /**
   * <p>Getter for salTaxIsInvoiceBase.</p>
   * @return Boolean
   **/
  public final Boolean getSalTaxIsInvoiceBase() {
    return this.salTaxIsInvoiceBase;
  }

  /**
   * <p>Setter for salTaxIsInvoiceBase.</p>
   * @param pSalTaxIsInvoiceBase reference
   **/
  public final void setSalTaxIsInvoiceBase(final Boolean pSalTaxIsInvoiceBase) {
    this.salTaxIsInvoiceBase = pSalTaxIsInvoiceBase;
  }

  /**
   * <p>Getter for salTaxUseAggregItBas.</p>
   * @return Boolean
   **/
  public final Boolean getSalTaxUseAggregItBas() {
    return this.salTaxUseAggregItBas;
  }

  /**
   * <p>Setter for salTaxUseAggregItBas.</p>
   * @param pSalTaxUseAggregItBas reference
   **/
  public final void setSalTaxUseAggregItBas(
    final Boolean pSalTaxUseAggregItBas) {
    this.salTaxUseAggregItBas = pSalTaxUseAggregItBas;
  }


  /**
   * <p>Getter for ttfFileName.</p>
   * @return String
   **/
  public final String getTtfFileName() {
    return this.ttfFileName;
  }

  /**
   * <p>Setter for ttfFileName.</p>
   * @param pTtfFileName reference
   **/
  public final void setTtfFileName(final String pTtfFileName) {
    this.ttfFileName = pTtfFileName;
  }

  /**
   * <p>Getter for ttfBoldFileName.</p>
   * @return String
   **/
  public final String getTtfBoldFileName() {
    return this.ttfBoldFileName;
  }

  /**
   * <p>Setter for ttfBoldFileName.</p>
   * @param pTtfBoldFileName reference
   **/
  public final void setTtfBoldFileName(final String pTtfBoldFileName) {
    this.ttfBoldFileName = pTtfBoldFileName;
  }

  /**
   * <p>Getter for pageSize.</p>
   * @return EPageSize
   **/
  public final EPageSize getPageSize() {
    return this.pageSize;
  }

  /**
   * <p>Setter for pageSize.</p>
   * @param pPageSize reference
   **/
  public final void setPageSize(final EPageSize pPageSize) {
    this.pageSize = pPageSize;
  }

  /**
   * <p>Getter for pageOrientation.</p>
   * @return EPageOrientation
   **/
  public final EPageOrientation getPageOrientation() {
    return this.pageOrientation;
  }

  /**
   * <p>Setter for pageOrientation.</p>
   * @param pPageOrientation reference
   **/
  public final void setPageOrientation(
    final EPageOrientation pPageOrientation) {
    this.pageOrientation = pPageOrientation;
  }

  /**
   * <p>Getter for marginLeft.</p>
   * @return Double
   **/
  public final Double getMarginLeft() {
    return this.marginLeft;
  }

  /**
   * <p>Setter for marginLeft.</p>
   * @param pMarginLeft reference
   **/
  public final void setMarginLeft(final Double pMarginLeft) {
    this.marginLeft = pMarginLeft;
  }

  /**
   * <p>Getter for marginRight.</p>
   * @return Double
   **/
  public final Double getMarginRight() {
    return this.marginRight;
  }

  /**
   * <p>Setter for marginRight.</p>
   * @param pMarginRight reference
   **/
  public final void setMarginRight(final Double pMarginRight) {
    this.marginRight = pMarginRight;
  }

  /**
   * <p>Getter for marginTop.</p>
   * @return Double
   **/
  public final Double getMarginTop() {
    return this.marginTop;
  }

  /**
   * <p>Setter for marginTop.</p>
   * @param pMarginTop reference
   **/
  public final void setMarginTop(final Double pMarginTop) {
    this.marginTop = pMarginTop;
  }

  /**
   * <p>Getter for marginBottom.</p>
   * @return Double
   **/
  public final Double getMarginBottom() {
    return this.marginBottom;
  }

  /**
   * <p>Setter for marginBottom.</p>
   * @param pMarginBottom reference
   **/
  public final void setMarginBottom(final Double pMarginBottom) {
    this.marginBottom = pMarginBottom;
  }

  /**
   * <p>Getter for fontSize.</p>
   * @return Double
   **/
  public final Double getFontSize() {
    return this.fontSize;
  }

  /**
   * <p>Setter for fontSize.</p>
   * @param pFontSize reference
   **/
  public final void setFontSize(final Double pFontSize) {
    this.fontSize = pFontSize;
  }

  /**
   * <p>Getter for taxPrecision.</p>
   * @return Integer
   **/
  public final Integer getTaxPrecision() {
    return this.taxPrecision;
  }

  /**
   * <p>Setter for taxPrecision.</p>
   * @param pTaxPrecision reference
   **/
  public final void setTaxPrecision(final Integer pTaxPrecision) {
    this.taxPrecision = pTaxPrecision;
  }
}

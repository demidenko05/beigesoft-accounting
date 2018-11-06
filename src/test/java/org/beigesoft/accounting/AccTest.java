package org.beigesoft.accounting;

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

import java.util.Set;
import java.math.RoundingMode;
import java.math.BigDecimal;
import java.util.Locale;
import java.util.Random;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

import org.beigesoft.service.UtlReflection;
import org.beigesoft.accounting.persistable.Account;
import org.beigesoft.accounting.persistable.AccSettings;

/**
 * <p>Acc tests.
 * </p>
 *
 * @author Yury Demidenko
 */
public class AccTest {

  /**
   * <p>Test of high performance tax calculation.
   * RDBMS performs this query by using float point numbers:
select TAX as TAXID,  sum(SUBTOTAL * ITSPERCENTAGE / 100) as TOTALTAX
from
(
  select INVITEM.TAXCATEGORY as TAXCATEGORY, SUBTOTAL
  from SALESINVOICELINE 
  join INVITEM on INVITEM.ITSID = SALESINVOICELINE.INVITEM
  where SALESINVOICELINE.REVERSEDID is null and SALESINVOICELINE.ITSOWNER = :ITSOWNER

  union all

  select SERVICETOSALE.TAXCATEGORY as TAXCATEGORY, ITSPRICE as SUBTOTAL
  from SALESINVOICESERVICELINE 
  join SERVICETOSALE on SERVICETOSALE.ITSID = SALESINVOICESERVICELINE.SERVICE
  where SALESINVOICESERVICELINE.ITSOWNER = :ITSOWNER
) as ALL_LINES
join INVITEMTAXCATEGORY on INVITEMTAXCATEGORY.ITSID = TAXCATEGORY
join INVITEMTAXCATEGORYLINE on INVITEMTAXCATEGORYLINE.ITSOWNER = INVITEMTAXCATEGORY.ITSID
group by TAX;
   * This test try to simulate RDBMS way calculation to estimate rounding error.
   * Grouping rounding error:
   * Round(12.344 + 12.344) = 24.70 != Round(12.344) + Round(12.344) = 24.68
   * </p>
   **/
  @Test
  public void test1() throws Exception {
    doRoundingCheck(500, 999, new double[] {3.45, 1.12, 9.0});    
    doRoundingCheck(500, 99, new double[] {3.45, 1.12, 9.0});    
    doRoundingCheck(500, 999, new double[] {13.0, 11.13});    
    doRoundingCheck(500, 99, new double[] {13.0, 11.13});
/* (for ordinal invoices (up to 50 lines) grouping rounding error is from 0 to 2 cents)
 * the results shows that there is grouping rounding error is up to 13 cents for 500 lines, tax 9.0% 2281.53 vs 2281.40:
Rounding test arr.size/left dec.pl/tax rates: 500/999/ 3.45, 1.12, 9.0,
sbt[0]=799.49, sbt[1]=334.82, sbt[2]=983.5, sbt[3]=197.11, sbt[4]=889.78, sbt[5]=124.78, sbt[6]=493.14, sbt[7]=737.31, sbt[8]=274.52, sbt[9]=110.29, sbt[10]=584.91, sbt[11]=666.76, sbt[12]=594.9, sbt[13]=367.2, sbt[14]=872.77, sbt[15]=489.91, sbt[16]=631.56, sbt[17]=891.87, sbt[18]=353.12, sbt[19]=336.89, sbt[20]=305.46, sbt[21]=57.78, sbt[22]=917.28, sbt[23]=871.89, sbt[24]=888.38, sbt[25]=787.27, sbt[26]=433.26, sbt[27]=623.93, sbt[28]=19.64, sbt[29]=334.43, sbt[30]=823.18 ...
tax rate/tax by lines/tax by grouping: 3.45/8547.94/8548.01
tax rate/tax by lines/tax by grouping: 1.12/2775.07/2775.00
tax rate/tax by lines/tax by grouping: 9.0/22299.12/22299.15
--------------------------------------------------------------------------------------------------
Rounding test arr.size/left dec.pl/tax rates: 500/99/ 3.45, 1.12, 9.0,
sbt[0]=79.92, sbt[1]=94.28, sbt[2]=28.66, sbt[3]=52.77, sbt[4]=19.29, sbt[5]=45.49, sbt[6]=20.32, sbt[7]=35.67, sbt[8]=70.67, sbt[9]=88.7, sbt[10]=85.46, sbt[11]=11.29, sbt[12]=95.17, sbt[13]=49.0, sbt[14]=27.37, sbt[15]=62.73, sbt[16]=22.71, sbt[17]=19.89, sbt[18]=38.61, sbt[19]=58.95, sbt[20]=18.37, sbt[21]=39.96, sbt[22]=37.23, sbt[23]=1.98, sbt[24]=88.77, sbt[25]=16.1, sbt[26]=58.2, sbt[27]=18.1, sbt[28]=50.28, sbt[29]=69.93, sbt[30]=3.76 ...
tax rate/tax by lines/tax by grouping: 3.45/874.51/874.54
tax rate/tax by lines/tax by grouping: 1.12/283.88/283.91
tax rate/tax by lines/tax by grouping: 9.0/2281.53/2281.40
--------------------------------------------------------------------------------------------------
Rounding test arr.size/left dec.pl/tax rates: 500/999/ 13.0, 11.13,
sbt[0]=628.58, sbt[1]=318.63, sbt[2]=171.6, sbt[3]=870.44, sbt[4]=825.95, sbt[5]=721.64, sbt[6]=414.89, sbt[7]=52.92, sbt[8]=89.27, sbt[9]=987.44, sbt[10]=44.37, sbt[11]=502.14, sbt[12]=526.91, sbt[13]=54.7, sbt[14]=348.66, sbt[15]=270.28, sbt[16]=145.92, sbt[17]=218.6, sbt[18]=555.67, sbt[19]=937.7, sbt[20]=350.55, sbt[21]=649.37, sbt[22]=227.87, sbt[23]=652.76, sbt[24]=304.48, sbt[25]=643.18, sbt[26]=86.95, sbt[27]=409.8, sbt[28]=65.79, sbt[29]=41.92, sbt[30]=148.5 ...
tax rate/tax by lines/tax by grouping: 13.0/32004.95/32005.00
tax rate/tax by lines/tax by grouping: 11.13/27401.17/27401.20
--------------------------------------------------------------------------------------------------
Rounding test arr.size/left dec.pl/tax rates: 500/99/ 13.0, 11.13,
sbt[0]=60.23, sbt[1]=27.85, sbt[2]=4.67, sbt[3]=43.67, sbt[4]=23.18, sbt[5]=63.23, sbt[6]=18.12, sbt[7]=44.82, sbt[8]=32.45, sbt[9]=49.29, sbt[10]=33.66, sbt[11]=37.37, sbt[12]=61.75, sbt[13]=35.51, sbt[14]=41.53, sbt[15]=95.39, sbt[16]=27.86, sbt[17]=58.29, sbt[18]=68.12, sbt[19]=97.37, sbt[20]=72.74, sbt[21]=22.78, sbt[22]=53.77, sbt[23]=63.66, sbt[24]=45.55, sbt[25]=34.37, sbt[26]=95.3, sbt[27]=53.93, sbt[28]=48.83, sbt[29]=92.8, sbt[30]=22.33 ...
tax rate/tax by lines/tax by grouping: 13.0/3263.84/3263.72
tax rate/tax by lines/tax by grouping: 11.13/2794.24/2794.25
 */
  }

  private void doRoundingCheck(int pArrSize, int pLeftWingSize, double[] pTaxRates) throws Exception {
    String txsStr = "";
    for (int j = 0; j < pTaxRates.length; j++) {
      txsStr += " " + pTaxRates[j] + ",";
    }
    System.out.println("--------------------------------------------------------------------------------------------------");
    System.out.println("Rounding test arr.size/left dec.pl/tax rates: " + pArrSize +  "/" + pLeftWingSize + "/" + txsStr);
    double[] subtotals = new double[pArrSize];
    Random random = new Random();
    double totalFromSubtotals = 0.0;
    for (int i = 0; i < subtotals.length; i++) {
      Integer lf = random.nextInt(pLeftWingSize);
      if (lf == 0) {
        lf = 11;
      }
      Integer rt = random.nextInt(99);
      String subtStr = lf.toString() + "." + rt;
      subtotals[i] = Double.parseDouble(subtStr);
      totalFromSubtotals += subtotals[i]; 
      if (i < 30) {
        System.out.print("sbt["+i+"]=" + subtotals[i] + ", ");
      } else if (i == 30) {
        System.out.print("sbt["+i+"]=" + subtotals[i] + " ...");
      }
    }
    System.out.println("");
    //tax by lines:
    BigDecimal[] taxesl = new BigDecimal[pTaxRates.length];
    for (int i = 0; i < subtotals.length; i++) {
      for (int j = 0; j < pTaxRates.length; j++) {
        if (taxesl[j] == null) {
          taxesl[j] = BigDecimal.ZERO;
        }
        taxesl[j] = taxesl[j].add(BigDecimal.valueOf(subtotals[i] * pTaxRates[j] / 100.0).setScale(2, BigDecimal.ROUND_HALF_UP));
      }
    }
    //tax by grouping lines:
    BigDecimal[] taxesg = new BigDecimal[pTaxRates.length];
    for (int j = 0; j < pTaxRates.length; j++) {
      if (taxesg[j] == null) {
        taxesg[j] = BigDecimal.ZERO;
      }
      taxesg[j] = taxesg[j].add(BigDecimal.valueOf(totalFromSubtotals * pTaxRates[j] / 100.0).setScale(2, BigDecimal.ROUND_HALF_UP));
    }
    for (int j = 0; j < pTaxRates.length; j++) {
      System.out.println("tax rate/tax by lines/tax by grouping: " + pTaxRates[j] + "/" + taxesl[j] + "/" + taxesg[j]);
    }
  }

  @Test
  public void test2() throws Exception {
    System.out.println(Locale.getDefault());
    AccSettings accSettings = new AccSettings();
    BigDecimal total = new BigDecimal("0.366");
    accSettings.setCostPrecision(4);
    BigDecimal cost = total.divide(BigDecimal.valueOf(4), accSettings.getCostPrecision(), accSettings.getRoundingMode());
    assertEquals(0.0915, cost.doubleValue(), 0);
    System.out.println(cost);
    accSettings.setCostPrecision(3);
    cost = total.divide(BigDecimal.valueOf(4), accSettings.getCostPrecision(), accSettings.getRoundingMode());
    assertEquals(0.092, cost.doubleValue(), 0);
    System.out.println(cost);
    BigDecimal totalDebit = new BigDecimal("1000.366");
    accSettings.setBalancePrecision(0);
    totalDebit = totalDebit.setScale(accSettings.getBalancePrecision(), accSettings.getRoundingMode());
    assertEquals(1000, totalDebit.doubleValue(), 0);
    System.out.println(totalDebit);
    totalDebit = new BigDecimal("1000.566");
    totalDebit = totalDebit.setScale(accSettings.getBalancePrecision(), accSettings.getRoundingMode());
    assertEquals(1001, totalDebit.doubleValue(), 0);
    System.out.println(totalDebit);
  }

  @Test
  public void test3() throws Exception {
    //Truly rounding is revealing THE nearest half up/down number
    //all online calculators give wrong result, and so does autoNumeric
    BigDecimal taxEt = new BigDecimal("11.215");
    assertEquals(11.22, taxEt.setScale(2, RoundingMode.HALF_UP).doubleValue(), 0);
    assertEquals(11.21, taxEt.setScale(2, RoundingMode.HALF_DOWN).doubleValue(), 0);
    BigDecimal taxEt1 = new BigDecimal("11.21505");
    assertEquals(11.22, taxEt1.setScale(2, RoundingMode.HALF_UP).doubleValue(), 0);
    //(11.210505-11.21)=0.00505>(11.22-11.210505)=0.00495495
    assertEquals(11.22, taxEt1.setScale(2, RoundingMode.HALF_DOWN).doubleValue(), 0);
    taxEt = new BigDecimal("11.2149999");
    //(11.22-11.2149999)=0.0050001>(11.214999-11.21)=0.0049999
    assertEquals(11.21, taxEt.setScale(2, RoundingMode.HALF_UP).doubleValue(), 0);
    BigDecimal total = new BigDecimal("113.17");
    BigDecimal rate = new BigDecimal("11");
    BigDecimal bd100 = new BigDecimal("100.00");
    BigDecimal taxHu =  total.subtract(total.divide(BigDecimal.ONE.add(rate.divide(bd100)), 2, RoundingMode.HALF_UP));
    BigDecimal taxHd =  total.subtract(total.divide(BigDecimal.ONE.add(rate.divide(bd100)), 2, RoundingMode.HALF_DOWN));
    assertEquals(11.22, taxHu.doubleValue(), 0);
    assertEquals(11.22, taxHd.doubleValue(), 0);
    for (RoundingMode rm : RoundingMode.class.getEnumConstants()) {
      System .out.println(rm.name() + " = " + rm.ordinal());
    }
  }
}

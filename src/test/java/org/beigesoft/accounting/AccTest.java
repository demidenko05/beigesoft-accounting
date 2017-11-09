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
import java.math.BigDecimal;
import java.util.Locale;

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
}

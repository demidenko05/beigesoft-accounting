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

import org.beigesoft.accounting.persistable.base.ASubaccount;

/**
 * <pre>
 * Model of Debtor/Creditor category.
 * It used for subaccount/filter/business intelligence.
 * </pre>
 *
 * @author Yury Demidenko
 */
public class DebtorCreditorCategory extends ASubaccount {

  /**
   * <p>OOP friendly Constant of code type.</p>
   * @return 2005
   **/
  @Override
  public final Integer constTypeCode() {
    return 2005;
  }
}

package org.beigesoft.accounting.holder;

/*
 * Copyright (c) 2017 Beigesoft™
 *
 * Licensed under the GNU General Public License (GPL), Version 2.0
 * (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html
 */

import org.beigesoft.holder.IHolderForClassByName;
import org.beigesoft.orm.processor.PrcEntitiesPage;
import org.beigesoft.orm.processor.PrcAbout;
import org.beigesoft.accounting.persistable.Account;
import org.beigesoft.accounting.persistable.AdditionCostLine;
import org.beigesoft.accounting.persistable.SubaccountLine;
import org.beigesoft.accounting.persistable.PrepaymentTo;
import org.beigesoft.accounting.persistable.PrepaymentFrom;
import org.beigesoft.accounting.persistable.PaymentTo;
import org.beigesoft.accounting.persistable.PaymentFrom;
import org.beigesoft.accounting.processor.PrcPageWithSubaccTypes;

/**
 * <p>Generic service that assign processor name for class
 * and action name.</p>
 *
 * @author Yury Demidenko
 */
public class HldAccProcessorNames
  implements IHolderForClassByName<String> {

  /**
   * <p>Get thing for given class and thing name.</p>
   * @param pClass a Class
   * @param pThingName Thing Name
   * @return a thing
   **/
  @Override
  public final String getFor(final Class<?> pClass, final String pThingName) {
    if ("list".equals(pThingName)) {
      if (pClass == PaymentFrom.class || pClass == PaymentTo.class
        || pClass == PrepaymentFrom.class || pClass == PrepaymentTo.class
          || pClass == SubaccountLine.class || pClass == AdditionCostLine.class
            || pClass == Account.class) {
        return PrcPageWithSubaccTypes.class.getSimpleName();
      } else {
        return PrcEntitiesPage.class.getSimpleName();
      }
    } else if ("about".equals(pThingName)) {
      return PrcAbout.class.getSimpleName();
    }
    return null;
  }

  /**
   * <p>Set thing for given class and thing name.</p>
   * @param pThing Thing
   * @param pClass Class
   * @param pThingName Thing Name
   **/
  @Override
  public final void setFor(final String pThing,
    final Class<?> pClass, final String pThingName) {
    //
  }
}

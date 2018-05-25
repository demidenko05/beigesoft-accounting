package org.beigesoft.accounting.holder;

/*
 * Copyright (c) 2017 Beigesoft ™
 *
 * Licensed under the GNU General Public License (GPL), Version 2.0
 * (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html
 */

import java.util.Map;
import java.util.HashMap;

import org.beigesoft.holder.IHolderForClassByName;
import org.beigesoft.orm.processor.PrcEntitiesPage;
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
   * <p>Holder additional processes names, e.g. for webstore.</p>
   **/
  private IHolderForClassByName<String> hldAddProcessorNames;

  /**
   * <p>Processors names map:
   * "key = class simple name + action"-"processor name".</p>
   **/
  private final Map<String, String> processorsNamesMap =
      new HashMap<String, String>();

  /**
   * <p>Get thing for given class and thing name.
   * findbugs: UG_SYNC_SET_UNSYNC_GET - this code is designed
   * for high performance. Getting name is happened very frequency
   * (e.g. 10 per second by multi-threads).
   * Setting is seldom (e.g. hot change configuration to fix program bug)
   * or may not be happen.</p>
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
    }
    if (this.hldAddProcessorNames != null) {
      String name = this.hldAddProcessorNames
        .getFor(pClass, pThingName);
      if (name != null) {
        return name;
      }
    }
    return this.processorsNamesMap
      .get(pClass.getSimpleName() + pThingName);
  }

  /**
   * <p>Set thing for given class and thing name.</p>
   * @param pThing Thing
   * @param pClass Class
   * @param pThingName Thing Name
   **/
  @Override
  public final synchronized void setFor(final String pThing,
    final Class<?> pClass, final String pThingName) {
    if ("list".equals(pThingName)) {
      throw new RuntimeException("Forbidden code!");
    }
    this.processorsNamesMap
      .put(pClass.getSimpleName() + pThingName, pThing);
  }

  //Simple getters and setters:

  /**
   * <p>Getter for hldAddProcessorNames.</p>
   * @return IHolderForClassByName<String>
   **/
  public final IHolderForClassByName<String> getHldAddProcessorNames() {
    return this.hldAddProcessorNames;
  }

  /**
   * <p>Setter for hldAddProcessorNames.</p>
   * @param pHldAddProcessorNames reference
   **/
  public final void setHldAddProcessorNames(
    final IHolderForClassByName<String> pHldAddProcessorNames) {
    this.hldAddProcessorNames = pHldAddProcessorNames;
  }
}

package org.beigesoft.accounting.model;

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

import java.util.Comparator;
import java.io.Serializable;

import org.beigesoft.accounting.persistable.PurchaseReturnLine;

/**
 * <p>Comparator for purchase return line total.</p>
 *
 * @author Yury Demidenko
 */
public class CmprPurchRetLnTotal
  implements Comparator<PurchaseReturnLine>, Serializable {

  /**
   * <p>serialVersionUID.</p>
   **/
  static final long serialVersionUID = 32188718734712L;

  @Override
  public final int compare(final PurchaseReturnLine o1,
          final PurchaseReturnLine o2) {
    return o1.getItsTotal().compareTo(o2.getItsTotal());
  }
}

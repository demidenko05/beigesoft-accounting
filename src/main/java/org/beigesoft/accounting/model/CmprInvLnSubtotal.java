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

import org.beigesoft.accounting.persistable.IInvoiceLine;

/**
 * <p>Comparator for invoice line subtotal.</p>
 *
 * @author Yury Demidenko
 */
public class CmprInvLnSubtotal
  implements Comparator<IInvoiceLine<?>>, Serializable {

  /**
   * <p>serialVersionUID.</p>
   **/
  static final long serialVersionUID = 49731218734712L;

  @Override
  public final int compare(final IInvoiceLine<?> o1,
          final IInvoiceLine<?> o2) {
    return o1.getSubtotal().compareTo(o2.getSubtotal());
  }
}

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

import org.beigesoft.accounting.persistable.Tax;

/**
 * <p>Comparator for tax by rate.</p>
 *
 * @author Yury Demidenko
 */
public class CmprTaxRate implements Comparator<Tax>, Serializable {

  /**
   * <p>serialVersionUID.</p>
   **/
  static final long serialVersionUID = 19678718734712L;

  @Override
  public final int compare(final Tax o1, final Tax o2) {
    return o1.getItsPercentage().compareTo(o2.getItsPercentage());
  }
}

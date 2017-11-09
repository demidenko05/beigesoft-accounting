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

import java.util.Comparator;
import java.io.Serializable;

import org.beigesoft.accounting.persistable.AccEntriesSourcesLine;

/**
 * <p>Comparator for accounting entries source
 * by type DEBIT/DEBIT_CREDIT/CREDIT.</p>
 *
 * @author Yury Demidenko
 */
public class CmprAccSourcesByType
  implements Comparator<AccEntriesSourcesLine>, Serializable {

  /**
   * <p>serialVersionUID.</p>
   **/
  static final long serialVersionUID = 49731247864712L;

  @Override
  public final int compare(final AccEntriesSourcesLine o1,
          final AccEntriesSourcesLine o2) {
    return o1.getEntriesAccountingType()
      .compareTo(o2.getEntriesAccountingType());
  }
}

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

/**
 * <pre>
 * When Make Entries.
 * </pre>
 *
 * @author Yury Demidenko
 */
 public enum EWhenMakeEntries {

  /**
   * <p>When document saved into database.
   * This approach not recommended in "High load" case.</p>
   **/
  IMMEDIATELY,

  /**
   * <p>On demand.</p>
   **/
  ON_DEMAND;
}

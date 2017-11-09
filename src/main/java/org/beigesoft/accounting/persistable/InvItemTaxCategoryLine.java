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

import org.beigesoft.model.IOwned;
import org.beigesoft.accounting.persistable.base.ATaxLine;

/**
 * <pre>
 * Model of inventory item tax category line.
 * </pre>
 *
 * @author Yury Demidenko
 */
public class InvItemTaxCategoryLine extends ATaxLine
  implements IOwned<InvItemTaxCategory> {

  /**
   * <p>Owner.</p>
   **/
  private InvItemTaxCategory itsOwner;

  /**
   * <p>Geter for itsOwner.</p>
   * @return InvItemTaxCategory
   **/
  @Override
  public final InvItemTaxCategory getItsOwner() {
    return this.itsOwner;
  }

  /**
   * <p>Setter for itsOwner.</p>
   * @param pItsOwner reference
   **/
  @Override
  public final void setItsOwner(final InvItemTaxCategory pItsOwner) {
    this.itsOwner = pItsOwner;
  }
}

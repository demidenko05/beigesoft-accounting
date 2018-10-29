package org.beigesoft.accounting.persistable;

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

import org.beigesoft.model.IOwned;
import org.beigesoft.persistable.APersistableBaseVersion;

/**
 * <p>
 * Model of item destination tax line.
 * </p>
 *
 * @author Yury Demidenko
 */
public class DestTaxGoodsLn extends APersistableBaseVersion
  implements IOwned<InvItem> {

  /**
   * <p>Owner.</p>
   **/
  private InvItem itsOwner;

  /**
   * <p>Tax destination, not null.</p>
   **/
  private TaxDestination taxDestination;

  /**
   * <p>Tax category, null if no taxes applies for this place.</p>
   **/
  private InvItemTaxCategory taxCategory;

  /**
   * <p>Geter for itsOwner.</p>
   * @return InvItemTaxCategory
   **/
  @Override
  public final InvItem getItsOwner() {
    return this.itsOwner;
  }

  /**
   * <p>Setter for itsOwner.</p>
   * @param pItsOwner reference
   **/
  @Override
  public final void setItsOwner(final InvItem pItsOwner) {
    this.itsOwner = pItsOwner;
  }

  //Simple getters and setters:
  /**
   * <p>Getter for taxDestination.</p>
   * @return TaxDestination
   **/
  public final TaxDestination getTaxDestination() {
    return this.taxDestination;
  }

  /**
   * <p>Setter for taxDestination.</p>
   * @param pTaxDestination reference
   **/
  public final void setTaxDestination(final TaxDestination pTaxDestination) {
    this.taxDestination = pTaxDestination;
  }

  /**
   * <p>Getter for taxCategory.</p>
   * @return InvItemTaxCategory
   **/
  public final InvItemTaxCategory getTaxCategory() {
    return this.taxCategory;
  }

  /**
   * <p>Setter for taxCategory.</p>
   * @param pTaxCategory reference
   **/
  public final void setTaxCategory(final InvItemTaxCategory pTaxCategory) {
    this.taxCategory = pTaxCategory;
  }
}

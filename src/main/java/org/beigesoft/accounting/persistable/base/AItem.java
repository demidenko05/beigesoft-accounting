package org.beigesoft.accounting.persistable.base;

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

import java.util.List;

import org.beigesoft.persistable.AHasNameIdLongVersion;
import org.beigesoft.accounting.persistable.InvItemTaxCategory;
import org.beigesoft.accounting.persistable.UnitOfMeasure;

/**
 * <p>Model of taxable item.</p>
 *
 * @param <T> item type
 * @param <L> tax destination line type
 * @author Yury Demidenko
 */
public abstract class AItem<T extends AItem<?, ?>, L extends ADestTaxItemLn<T>>
  extends AHasNameIdLongVersion {

  /**
   * <p>Origin tax category e.g. "NY: tax1 10%, tax2 5%".</p>
   **/
  private InvItemTaxCategory taxCategory;

  /**
   * <p>Default unit of measure.</p>
   **/
  private UnitOfMeasure defUnitOfMeasure;

  /**
   * <p>Getter for destinationTaxes.</p>
   * @return List<DestTaxServSelLn>
   **/
  public abstract List<L> getDestinationTaxes();

  /**
   * <p>Setter for destinationTaxes.</p>
   * @param pDestinationTaxes reference
   **/
  public abstract void setDestinationTaxes(final List<L> pDestinationTaxes);

  //Simple getters and setters:
  /**
   * <p>Geter for taxCategory.</p>
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

  /**
   * <p>Geter for defUnitOfMeasure.</p>
   * @return UnitOfMeasure
   **/
  public final UnitOfMeasure getDefUnitOfMeasure() {
    return this.defUnitOfMeasure;
  }

  /**
   * <p>Setter for defUnitOfMeasure.</p>
   * @param pDefUnitOfMeasure reference
   **/
  public final void setDefUnitOfMeasure(final UnitOfMeasure pDefUnitOfMeasure) {
    this.defUnitOfMeasure = pDefUnitOfMeasure;
  }
}

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

import java.util.List;

import org.beigesoft.persistable.APersistableBaseNameVersion;

/**
 * <pre>
 * Model of service purchased, e.g. "Shipping to NY",
 * "Repair carburetor MZX567G".
 * Version changed time algorithm.
 * </pre>
 *
 * @author Yury Demidenko
 */
public class ServicePurchased extends APersistableBaseNameVersion {

  /**
   * <p>Its category.
   * It used to filter list of services and as subaccount.</p>
   **/
  private ServicePurchasedCategory itsCategory;

  /**
   * <p>Tax category e.g. "NY: tax1 10%, tax2 5%".</p>
   **/
  private InvItemTaxCategory taxCategory;

  /**
   * <p>Default unit of measure.</p>
   **/
  private UnitOfMeasure defUnitOfMeasure;

  /**
   * <p>Destination taxes categories and rules.</p>
   **/
  private List<DestTaxServPurchLn> destinationTaxes;

  //Simple getters and setters:
  /**
   * <p>Geter for itsCategory.</p>
   * @return ServicePurchasedCategory
   **/
  public final ServicePurchasedCategory getItsCategory() {
    return this.itsCategory;
  }

  /**
   * <p>Setter for itsCategory.</p>
   * @param pItsCategory reference
   **/
  public final void setItsCategory(
    final ServicePurchasedCategory pItsCategory) {
    this.itsCategory = pItsCategory;
  }

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
   * <p>Getter for destinationTaxes.</p>
   * @return List<DestTaxServPurchLn>
   **/
  public final List<DestTaxServPurchLn> getDestinationTaxes() {
    return this.destinationTaxes;
  }

  /**
   * <p>Setter for destinationTaxes.</p>
   * @param pDestinationTaxes reference
   **/
  public final void setDestinationTaxes(
    final List<DestTaxServPurchLn> pDestinationTaxes) {
    this.destinationTaxes = pDestinationTaxes;
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

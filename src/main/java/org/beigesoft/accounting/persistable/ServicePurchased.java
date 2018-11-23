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

import org.beigesoft.persistable.IPersistableBase;
import org.beigesoft.accounting.persistable.base.AItem;

/**
 * <p>
 * Model of service purchased, e.g. "Shipping to NY",
 * "Repair carburetor MZX567G".
 * Version changed time algorithm.
 * </p>
 *
 * @author Yury Demidenko
 */
public class ServicePurchased extends AItem<ServicePurchased, DestTaxServPurchLn>
  implements IPersistableBase {

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
   * <p>Implicit(there is no database constraints for it)
   * ID database where Entity was born.
   * For replication purpose. Not NULL.</p>
   **/
  private Integer idDatabaseBirth;

  /**
   * <p>Implicit(there is no database constraints for it)
   * ID of this Entity from database where it was born.
   * For replication purpose. NULL if it was born in current database.</p>
   **/
  private Long idBirth;

  /**
   * <p>Destination taxes categories and rules.</p>
   **/
  private List<DestTaxServPurchLn> destinationTaxes;

  /**
   * <p>Getter for destinationTaxes.</p>
   * @return List<DestTaxGoodsLn>
   **/
  @Override
  public final List<DestTaxServPurchLn> getDestinationTaxes() {
    return this.destinationTaxes;
  }

  /**
   * <p>Setter for destinationTaxes.</p>
   * @param pDestinationTaxes reference
   **/
  @Override
  public final void setDestinationTaxes(
    final List<DestTaxServPurchLn> pDestinationTaxes) {
    this.destinationTaxes = pDestinationTaxes;
  }

  /**
   * <p>Geter for idDatabaseBirth.</p>
   * @return Integer
   **/
  @Override
  public final Integer getIdDatabaseBirth() {
    return this.idDatabaseBirth;
  }

  /**
   * <p>Setter for idDatabaseBirth.</p>
   * @param pIdDatabaseBirth reference
   **/
  @Override
  public final void setIdDatabaseBirth(final Integer pIdDatabaseBirth) {
    this.idDatabaseBirth = pIdDatabaseBirth;
  }

  /**
   * <p>Geter for idBirth.</p>
   * @return Long
   **/
  @Override
  public final Long getIdBirth() {
    return this.idBirth;
  }

  /**
   * <p>Setter for idBirth.</p>
   * @param pIdBirth reference
   **/
  @Override
  public final void setIdBirth(final Long pIdBirth) {
    this.idBirth = pIdBirth;
  }

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
}

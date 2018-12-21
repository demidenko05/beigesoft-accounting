package org.beigesoft.accounting.persistable;

/*
 * Copyright (c) 2016 Beigesoft™
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

import org.beigesoft.model.EServTime;
import org.beigesoft.model.IService;
import org.beigesoft.persistable.IPersistableBase;
import org.beigesoft.accounting.persistable.base.AItem;

/**
 * <p>
 * Model of service to sale, e.g. "Shipping to NY",
 * "Repair carburetor MZX567G".
 * Version, changed time algorithm cause check dirty of
 * calculated from it (derived) records.
 * </p>
 *
 * @author Yury Demidenko
 */
public class ServiceToSale extends AItem<ServiceToSale, DestTaxServSelLn>
  implements IService, IPersistableBase {

  /**
   * <p>Its category.
   * It used to filter list of services and as subaccount.</p>
   **/
  private ServiceToSaleCategory itsCategory;

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
  private List<DestTaxServSelLn> destinationTaxes;


  //WEB-STORE fields:
  /**
   * <p>Not null, default TIME, booking time method.</p>
   **/
  private EServTime tmMe = EServTime.TIME;

  /**
   * <p>Additional time method,
   * e.g. step from zero in minutes (5/10/15/20/30) for tmMe=="*TIME*".</p>
   **/
  private Integer tmAd;

  /**
   * <p>Getter for destinationTaxes.</p>
   * @return List<DestTaxGoodsLn>
   **/
  @Override
  public final List<DestTaxServSelLn> getDestinationTaxes() {
    return this.destinationTaxes;
  }

  /**
   * <p>Setter for destinationTaxes.</p>
   * @param pDestinationTaxes reference
   **/
  @Override
  public final void setDestinationTaxes(
    final List<DestTaxServSelLn> pDestinationTaxes) {
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

  /**
   * <p>Getter for tmMe.</p>
   * @return EServTime
   **/
  @Override
  public final EServTime getTmMe() {
    return this.tmMe;
  }

  /**
   * <p>Setter for tmMe.</p>
   * @param pTmMe reference
   **/
  @Override
  public final void setTmMe(final EServTime pTmMe) {
    this.tmMe = pTmMe;
  }

  /**
   * <p>Getter for tmAd.</p>
   * @return Integer
   **/
  @Override
  public final Integer getTmAd() {
    return this.tmAd;
  }

  /**
   * <p>Setter for tmAd.</p>
   * @param pTmAd reference
   **/
  @Override
  public final void setTmAd(final Integer pTmAd) {
    this.tmAd = pTmAd;
  }

  //Simple getters and setters:
  /**
   * <p>Geter for itsCategory.</p>
   * @return ServiceToSaleCategory
   **/
  public final ServiceToSaleCategory getItsCategory() {
    return this.itsCategory;
  }

  /**
   * <p>Setter for itsCategory.</p>
   * @param pItsCategory reference
   **/
  public final void setItsCategory(
    final ServiceToSaleCategory pItsCategory) {
    this.itsCategory = pItsCategory;
  }
}

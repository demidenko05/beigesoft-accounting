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
import java.math.BigDecimal;

import org.beigesoft.persistable.IPersistableBase;
import org.beigesoft.accounting.persistable.base.AItem;

/**
 * <p>
 * Model of invItem.
 * Version, changed time algorithm cause check dirty of
 * calculated from it (derived) records.
 * </p>
 *
 * @author Yury Demidenko
 */
public class InvItem extends AItem<InvItem, DestTaxGoodsLn>
  implements IPersistableBase {

  /**
   * <p>Merchandise or stock in trade type ID.</p>
   **/
  public static final Long MERCHANDISE_ID = 1L;

  /**
   * <p>Raw materials type ID.</p>
   **/
  public static final Long MATERIAL_ID = 2L;

  /**
   * <p>Work in process type ID.</p>
   **/
  public static final Long WORK_IN_PROGRESS_ID = 3L;

  /**
   * <p>Finished products type ID.</p>
   **/
  public static final Long FINISHED_PRODUCT_ID = 4L;

  /**
   * <p>Supplies that physically become a part of
   * the item intended for sale type ID.</p>
   **/
  public static final Long SUPPLIES_PART_OF_PRODUCT_ID = 5L;

  /**
   * <p>Its category.
   * It used for filter list of invItems.</p>
   **/
  private InvItemCategory itsCategory;

  /**
   * <p>Inventory Item Type according the law.
   * For US these are Merchandise or stock in trade,
   * Raw materials, Work in process, Finished products, Supplies that physically
   * become a part of the item intended for sale.</p>
   **/
  private InvItemType itsType;

  /**
   * <p>Known cost used for COGS.
   * It may includes of all direct and indirect
   * costs that allowed in inventory.</p>
   **/
  private BigDecimal knownCost;

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
  private List<DestTaxGoodsLn> destinationTaxes;

  /**
   * <p>Getter for destinationTaxes.</p>
   * @return List<DestTaxGoodsLn>
   **/
  @Override
  public final List<DestTaxGoodsLn> getDestinationTaxes() {
    return this.destinationTaxes;
  }

  /**
   * <p>Setter for destinationTaxes.</p>
   * @param pDestinationTaxes reference
   **/
  @Override
  public final void setDestinationTaxes(
    final List<DestTaxGoodsLn> pDestinationTaxes) {
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
   * <p>Geter for itsType.</p>
   * @return InvItemType
   **/
  public final InvItemType getItsType() {
    return this.itsType;
  }

  /**
   * <p>Setter for itsType.</p>
   * @param pItsType reference
   **/
  public final void setItsType(final InvItemType pItsType) {
    this.itsType = pItsType;
  }

  /**
   * <p>Geter for itsCategory.</p>
   * @return InvItemCategory
   **/
  public final InvItemCategory getItsCategory() {
    return this.itsCategory;
  }

  /**
   * <p>Setter for itsCategory.</p>
   * @param pItsCategory reference
   **/
  public final void setItsCategory(final InvItemCategory pItsCategory) {
    this.itsCategory = pItsCategory;
  }

  /**
   * <p>Geter for knownCost.</p>
   * @return BigDecimal
   **/
  public final BigDecimal getKnownCost() {
    return this.knownCost;
  }

  /**
   * <p>Setter for knownCost.</p>
   * @param pKnownCost reference
   **/
  public final void setKnownCost(final BigDecimal pKnownCost) {
    this.knownCost = pKnownCost;
  }
}

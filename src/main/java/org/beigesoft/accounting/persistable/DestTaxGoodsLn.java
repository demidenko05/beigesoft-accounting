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

import org.beigesoft.persistable.IPersistableBase;
import org.beigesoft.accounting.persistable.base.ADestTaxItemLn;

/**
 * <p>
 * Model of good destination tax line.
 * </p>
 *
 * @author Yury Demidenko
 */
public class DestTaxGoodsLn extends ADestTaxItemLn<InvItem>
  implements IPersistableBase {

  /**
   * <p>Owner.</p>
   **/
  private InvItem itsOwner;

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
}

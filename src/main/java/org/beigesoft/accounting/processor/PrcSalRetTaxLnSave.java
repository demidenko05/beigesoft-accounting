package org.beigesoft.accounting.processor;

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

import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.beigesoft.model.IRequestData;
import org.beigesoft.exception.ExceptionWithCode;
import org.beigesoft.service.IEntityProcessor;
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.service.ISrvDatabase;
import org.beigesoft.accounting.persistable.AccSettings;
import org.beigesoft.accounting.persistable.SalesReturnTaxLine;
import org.beigesoft.accounting.persistable.SalesReturn;
import org.beigesoft.accounting.service.ISrvAccSettings;

/**
 * <p>Service that saves Sales Return Tax Line into DB
 * (only invoice basis!).</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class PrcSalRetTaxLnSave<RS>
  implements IEntityProcessor<SalesReturnTaxLine, Long> {

  /**
   * <p>ORM service.</p>
   **/
  private ISrvOrm<RS> srvOrm;

  /**
   * <p>Database service.</p>
   **/
  private ISrvDatabase<RS> srvDatabase;

  /**
   * <p>Business service for accounting settings.</p>
   **/
  private ISrvAccSettings srvAccSettings;

  /**
   * <p>Query invoice totals.</p>
   **/
  private String queryInvTot;

  /**
   * <p>Process entity request.</p>
   * @param pReqVars additional param, e.g. return this line's
   * document in "nextEntity" for farther process
   * @param pRequestData Request Data
   * @param pEntity Entity to process
   * @return Entity processed for farther process or null
   * @throws Exception - an exception
   **/
  @Override
  public final SalesReturnTaxLine process(
    final Map<String, Object> pReqVars,
      final SalesReturnTaxLine pEntity,
        final IRequestData pRequestData) throws Exception {
    if (pEntity.getItsTotal().compareTo(BigDecimal.ZERO) <= 0) {
      throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
        "total_less_or_eq_zero");
    }
    AccSettings as = getSrvAccSettings().lazyGetAccSettings(pReqVars);
    // Beige-Orm refresh:
    pReqVars.put("DebtorCreditortaxDestinationdeepLevel", 2);
    Set<String> ndFlDc = new HashSet<String>();
    ndFlDc.add("itsId");
    ndFlDc.add("isForeigner");
    ndFlDc.add("taxDestination");
    pReqVars.put("DebtorCreditorneededFields", ndFlDc);
    pEntity.setItsOwner(getSrvOrm()
      .retrieveEntity(pReqVars, pEntity.getItsOwner()));
    pReqVars.remove("DebtorCreditorneededFields");
    pReqVars.remove("DebtorCreditortaxDestinationdeepLevel");
    boolean isTaxable = as.getIsExtractSalesTaxFromSales() && !pEntity
      .getItsOwner().getOmitTaxes() && !pEntity
        .getItsOwner().getCustomer().getIsForeigner();
    if (!isTaxable) {
      throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
        "non_taxable");
    }
    SalesReturnTaxLine oldEntity = getSrvOrm()
      .retrieveEntity(pReqVars, pEntity);
    pEntity.setTax(oldEntity.getTax());
    pEntity.setTaxableInvBas(oldEntity.getTaxableInvBas());
    boolean isItemBasis = !as.getSalTaxIsInvoiceBase();
    RoundingMode rm = as.getSalTaxRoundMode();
    if (pEntity.getItsOwner().getCustomer()
      .getTaxDestination() != null) {
      //override tax method:
      isItemBasis = !pEntity.getItsOwner().getCustomer()
        .getTaxDestination().getSalTaxIsInvoiceBase();
      rm = pEntity.getItsOwner().getCustomer()
        .getTaxDestination().getSalTaxRoundMode();
    }
    if (isItemBasis) {
      throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
        "cant_edit_item_basis_tax");
    }
    //rounding:
    pEntity.setItsTotal(pEntity.getItsTotal().setScale(as
      .getPricePrecision(), rm));
    BigDecimal totalTaxes;
    BigDecimal bd100 = new BigDecimal("100.00");
    totalTaxes = pEntity.getTaxableInvBas().multiply(pEntity.getTax()
      .getItsPercentage()).divide(bd100, as.getPricePrecision(), rm);
    if (pEntity.getItsTotal().compareTo(totalTaxes) != 0) {
      if (pEntity.getItsOwner().getDescription() == null) {
        pEntity.getItsOwner().setDescription(pEntity.getItsTotal().toString()
          + "!=" + totalTaxes + "!");
      } else {
        pEntity.getItsOwner().setDescription(pEntity.getItsOwner()
          .getDescription() + " " + pEntity.getItsTotal().toString()
            + "!=" + totalTaxes + "!");
      }
    }
    getSrvOrm().updateEntity(pReqVars, pEntity);
    // optimistic locking (dirty check):
    Long ownerVersion = Long.valueOf(pRequestData
      .getParameter(SalesReturn.class.getSimpleName() + ".ownerVersion"));
    pEntity.getItsOwner().setItsVersion(ownerVersion);
    String query = lazyGetQueryInvTot();
    query = query.replace(":ITSOWNER", pEntity.getItsOwner().getItsId()
      .toString());
    String[] columns = new String[]{"SUBTOTAL", "TOTALTAXES"};
    Double[] totals = getSrvDatabase().evalDoubleResults(query, columns);
    if (totals[0] == null) {
      totals[0] = 0d;
    }
    if (totals[1] == null) {
      totals[1] = 0d;
    }
    pEntity.getItsOwner().setSubtotal(BigDecimal.valueOf(totals[0]).setScale(
      getSrvAccSettings().lazyGetAccSettings(pReqVars).getPricePrecision(),
        getSrvAccSettings().lazyGetAccSettings(pReqVars).getRoundingMode()));
    pEntity.getItsOwner().setTotalTaxes(BigDecimal.valueOf(totals[1])
      .setScale(getSrvAccSettings().lazyGetAccSettings(pReqVars)
        .getPricePrecision(), getSrvAccSettings()
          .lazyGetAccSettings(pReqVars).getRoundingMode()));
    pEntity.getItsOwner().setItsTotal(pEntity.getItsOwner().getSubtotal().
      add(pEntity.getItsOwner().getTotalTaxes()));
    getSrvOrm().updateEntity(pReqVars, pEntity.getItsOwner());
    pReqVars.put("nextEntity", pEntity.getItsOwner());
    pReqVars.put("nameOwnerEntity", SalesReturn.class.getSimpleName());
    return null;
  }

  /**
   * <p>Lazy Get query invoice totals.</p>
   * @return String
   * @throws Exception - an exception
   **/
  public final String lazyGetQueryInvTot() throws Exception {
    if (this.queryInvTot == null) {
      String flName = "/accounting/trade/salRetTot.sql";
      this.queryInvTot = loadString(flName);
    }
    return this.queryInvTot;
  }

  /**
   * <p>Load string file (usually SQL query).</p>
   * @param pFileName file name
   * @return String usually SQL query
   * @throws IOException - IO exception
   **/
  public final String loadString(final String pFileName)
        throws IOException {
    URL urlFile = PrcSalRetTaxLnSave.class
      .getResource(pFileName);
    if (urlFile != null) {
      InputStream inputStream = null;
      try {
        inputStream = PrcSalRetTaxLnSave.class
          .getResourceAsStream(pFileName);
        byte[] bArray = new byte[inputStream.available()];
        inputStream.read(bArray, 0, inputStream.available());
        return new String(bArray, "UTF-8");
      } finally {
        if (inputStream != null) {
          inputStream.close();
        }
      }
    }
    return null;
  }

  //Simple getters and setters:
  /**
   * <p>Getter for srvOrm.</p>
   * @return ISrvOrm<RS>
   **/
  public final ISrvOrm<RS> getSrvOrm() {
    return this.srvOrm;
  }

  /**
   * <p>Setter for srvOrm.</p>
   * @param pSrvOrm reference
   **/
  public final void setSrvOrm(final ISrvOrm<RS> pSrvOrm) {
    this.srvOrm = pSrvOrm;
  }

  /**
   * <p>Getter for srvAccSettings.</p>
   * @return ISrvAccSettings
   **/
  public final ISrvAccSettings getSrvAccSettings() {
    return this.srvAccSettings;
  }

  /**
   * <p>Setter for srvAccSettings.</p>
   * @param pSrvAccSettings reference
   **/
  public final void setSrvAccSettings(final ISrvAccSettings pSrvAccSettings) {
    this.srvAccSettings = pSrvAccSettings;
  }

  /**
   * <p>Geter for srvDatabase.</p>
   * @return ISrvDatabase
   **/
  public final ISrvDatabase<RS> getSrvDatabase() {
    return this.srvDatabase;
  }

  /**
   * <p>Setter for srvDatabase.</p>
   * @param pSrvDatabase reference
   **/
  public final void setSrvDatabase(final ISrvDatabase<RS> pSrvDatabase) {
    this.srvDatabase = pSrvDatabase;
  }

  /**
   * <p>Setter for queryInvTot.</p>
   * @param pQueryInvTot reference
   **/
  public final void setQueryInvTot(final String pQueryInvTot) {
    this.queryInvTot = pQueryInvTot;
  }
}

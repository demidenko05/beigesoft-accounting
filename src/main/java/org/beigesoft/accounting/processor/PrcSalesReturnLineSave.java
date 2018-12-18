package org.beigesoft.accounting.processor;

/*
 * Copyright (c) 2017 Beigesoft™
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
import java.util.List;
import java.util.Map;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.beigesoft.model.IRequestData;
import org.beigesoft.exception.ExceptionWithCode;
import org.beigesoft.service.IEntityProcessor;
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.accounting.persistable.SalesReturn;
import org.beigesoft.accounting.persistable.SalesReturnLine;
import org.beigesoft.accounting.persistable.SalesReturnTaxLine;
import org.beigesoft.accounting.persistable.SalesReturnGoodsTaxLine;
import org.beigesoft.accounting.persistable.AccSettings;
import org.beigesoft.accounting.persistable.TaxDestination;
import org.beigesoft.accounting.service.ISrvWarehouseEntry;
import org.beigesoft.accounting.service.ISrvAccSettings;

/**
 * <p>Service that save SalesReturnLine into DB.</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class PrcSalesReturnLineSave<RS>
  implements IEntityProcessor<SalesReturnLine, Long> {

  /**
   * <p>ORM service.</p>
   **/
  private ISrvOrm<RS> srvOrm;

  /**
   * <p>Business service for warehouse.</p>
   **/
  private ISrvWarehouseEntry srvWarehouseEntry;

  /**
   * <p>Business service for accounting settings.</p>
   **/
  private ISrvAccSettings srvAccSettings;

  /**
   * <p>It makes line and total for owner.</p>
   **/
  private UtlInvLine<RS, SalesReturn, SalesReturnLine,
    SalesReturnTaxLine, SalesReturnGoodsTaxLine> utlInvLine;

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
  public final SalesReturnLine process(
    final Map<String, Object> pReqVars,
      final SalesReturnLine pEntity,
        final IRequestData pRequestData) throws Exception {
    if (pEntity.getIsNew()) {
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
      AccSettings as = getSrvAccSettings().lazyGetAccSettings(pReqVars);
      TaxDestination txRules = this.utlInvLine.revealTaxRules(pReqVars,
        pEntity.getItsOwner(), as, as.getIsExtractSalesTaxFromSales());
      if (pEntity.getReversedId() != null) {
        SalesReturnLine reversed = getSrvOrm().retrieveEntityById(pReqVars,
          SalesReturnLine.class, pEntity.getReversedId());
        if (reversed.getReversedId() != null) {
          throw new ExceptionWithCode(ExceptionWithCode.FORBIDDEN,
            "Attempt to double reverse" + pReqVars.get("user"));
        }
        if (!reversed.getItsQuantity().equals(reversed.getTheRest())) {
          throw new ExceptionWithCode(ExceptionWithCode
            .WRONG_PARAMETER, "where_is_withdrawals_from_this_source");
        }
        pEntity.setTheRest(BigDecimal.ZERO);
        pEntity.setInvItem(reversed.getInvItem());
        pEntity.setUnitOfMeasure(reversed.getUnitOfMeasure());
        pEntity.setWarehouseSite(reversed.getWarehouseSite());
        pEntity.setTaxesDescription(reversed.getTaxesDescription());
        pEntity.setTotalTaxes(reversed.getTotalTaxes().negate());
        pEntity.setItsQuantity(reversed.getItsQuantity().negate());
        pEntity.setItsCost(reversed.getItsCost());
        pEntity.setItsPrice(reversed.getItsPrice());
        pEntity.setSubtotal(reversed.getSubtotal().negate());
        pEntity.setItsTotal(reversed.getItsTotal().negate());
        getSrvOrm().insertEntity(pReqVars, pEntity);
        pEntity.setIsNew(false);
        reversed.setTheRest(BigDecimal.ZERO);
        reversed.setReversedId(pEntity.getItsId());
        getSrvOrm().updateEntity(pReqVars, reversed);
        SalesReturnGoodsTaxLine pigtlt = new SalesReturnGoodsTaxLine();
        pigtlt.setItsOwner(reversed);
        List<SalesReturnGoodsTaxLine> tls = getSrvOrm()
          .retrieveListForField(pReqVars, pigtlt, "itsOwner");
        for (SalesReturnGoodsTaxLine pigtl : tls) {
          getSrvOrm().deleteEntity(pReqVars, pigtl);
        }
      } else {
        if (pEntity.getItsQuantity().doubleValue() == 0) {
          throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
            "quantity_is_0");
        }
        if (pEntity.getItsCost().doubleValue() <= 0) {
          throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
            "cost_less_or_eq_zero" + pReqVars.get("user"));
        }
        if (pEntity.getForeignPrice().doubleValue() <= 0
          && pEntity.getItsPrice().doubleValue() <= 0) {
          throw new ExceptionWithCode(ExceptionWithCode.WRONG_PARAMETER,
            "price_less_eq_0");
        }
        pEntity.setInvItem(getSrvOrm()
          .retrieveEntity(pReqVars, pEntity.getInvItem()));
        pEntity.setTheRest(pEntity.getItsQuantity());
        //using user passed values:
        if (pEntity.getItsOwner().getForeignCurrency() != null) {
          BigDecimal exchRate = pEntity.getItsOwner().getExchangeRate();
          if (exchRate.compareTo(BigDecimal.ZERO) == -1) {
            exchRate = BigDecimal.ONE.divide(exchRate.negate(), 15,
              RoundingMode.HALF_UP);
          }
          pEntity.setItsPrice(pEntity.getForeignPrice().multiply(exchRate)
            .setScale(as.getPricePrecision(), as.getRoundingMode()));
          if (txRules == null || pEntity.getItsOwner().getPriceIncTax()) {
            pEntity.setItsTotal(pEntity.getForeignTotal().multiply(exchRate)
              .setScale(as.getPricePrecision(), as.getRoundingMode()));
          } else {
            pEntity.setSubtotal(pEntity.getForeignSubtotal().multiply(exchRate)
              .setScale(as.getPricePrecision(), as.getRoundingMode()));
          }
        }
        this.utlInvLine.makeLine(pReqVars, pEntity, as, txRules);
      }
      //draw or reverse warehouse entries:
      srvWarehouseEntry.load(pReqVars, pEntity, pEntity.getWarehouseSite());
      //owner update:
      // optimistic locking (dirty check):
      Long ownerVersion = Long.valueOf(pRequestData
        .getParameter(SalesReturn.class.getSimpleName() + ".ownerVersion"));
      pEntity.getItsOwner().setItsVersion(ownerVersion);
      this.utlInvLine.makeTotals(pReqVars, pEntity, as, txRules);
      pReqVars.put("nextEntity", pEntity.getItsOwner());
      pReqVars.put("nameOwnerEntity", SalesReturn.class.getSimpleName());
      return null;
    } else {
      throw new ExceptionWithCode(ExceptionWithCode.FORBIDDEN,
        "Attempt to update Sales Return line by " + pReqVars.get("user"));
    }
  }

  //Simple getters and setters:
  /**
   * <p>Getter for utlInvLine.</p>
   * @return UtlInvLine<RS, SalesReturn, SalesReturnLine,
   *  SalesReturnTaxLine, SalesReturnGoodsTaxLine>
   **/
  public final UtlInvLine<RS, SalesReturn, SalesReturnLine,
    SalesReturnTaxLine, SalesReturnGoodsTaxLine> getUtlInvLine() {
    return this.utlInvLine;
  }

  /**
   * <p>Setter for utlInvLine.</p>
   * @param pUtlInvLine reference
   **/
  public final void setUtlInvLine(final UtlInvLine<RS, SalesReturn,
    SalesReturnLine, SalesReturnTaxLine,
      SalesReturnGoodsTaxLine> pUtlInvLine) {
    this.utlInvLine = pUtlInvLine;
  }

  /**
   * <p>Geter for srvWarehouseEntry.</p>
   * @return ISrvWarehouseEntry
   **/
  public final ISrvWarehouseEntry getSrvWarehouseEntry() {
    return this.srvWarehouseEntry;
  }

  /**
   * <p>Setter for srvWarehouseEntry.</p>
   * @param pSrvWarehouseEntry reference
   **/
  public final void setSrvWarehouseEntry(
    final ISrvWarehouseEntry pSrvWarehouseEntry) {
    this.srvWarehouseEntry = pSrvWarehouseEntry;
  }

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
}

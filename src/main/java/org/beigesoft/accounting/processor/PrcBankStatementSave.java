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

import java.util.Map;
import java.util.List;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.text.ParsePosition;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import org.beigesoft.exception.ExceptionWithCode;
import org.beigesoft.model.IRequestData;
import org.beigesoft.service.IEntityProcessor;
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.service.ICsvReader;
import org.beigesoft.persistable.CsvColumn;
import org.beigesoft.accounting.model.EBankEntryStatus;
import org.beigesoft.accounting.persistable.BankStatement;
import org.beigesoft.accounting.persistable.BankStatementLine;
import org.beigesoft.accounting.persistable.BankCsvMethod;

/**
 * <p>Service that save BankStatement into DB.</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class PrcBankStatementSave<RS>
    implements IEntityProcessor<BankStatement, Long> {

  /**
   * <p>ORM service.</p>
   **/
  private ISrvOrm<RS> srvOrm;

  /**
   * <p>CSV reader.</p>
   **/
  private ICsvReader csvReader;

  /**
   * <p>Process entity request.</p>
   * @param pAddParam additional param, e.g. return this line's
   * document in "nextEntity" for farther process
   * @param pRequestData Request Data
   * @param pEntity Entity to process
   * @return Entity processed for farther process or null
   * @throws Exception - an exception
   **/
  @Override
  public final BankStatement process(
    final Map<String, Object> pAddParam,
      final BankStatement pEntity,
        final IRequestData pRequestData) throws Exception {
    //if exist file name:
    String fileToUploadName = (String) pRequestData
      .getAttribute("fileToUploadName");
    if (fileToUploadName != null) {
      String bankCsvMethodId = pRequestData.getParameter("bankCsvMethod");
      BankCsvMethod bankCsvMethod = getSrvOrm().retrieveEntityById(pAddParam,
        BankCsvMethod.class, Long.parseLong(bankCsvMethodId));
      bankCsvMethod.getCsvMethod().setColumns(getSrvOrm()
        .retrieveListWithConditions(pAddParam, CsvColumn.class,
          "where ITSOWNER=" + bankCsvMethod.getCsvMethod().getItsId()));
      pEntity.setSourceName(fileToUploadName + "/"
        + bankCsvMethod.getItsName());
      if (pEntity.getIsNew()) {
        getSrvOrm().insertEntity(pAddParam, pEntity);
      } else {
        getSrvOrm().updateEntity(pAddParam, pEntity);
      }
      InputStreamReader reader = null;
      try {
        InputStream ins = (InputStream) pRequestData
          .getAttribute("fileToUploadInputStream");
        reader = new InputStreamReader(ins, Charset
          .forName(bankCsvMethod.getCsvMethod().getCharsetName()).newDecoder());
        List<String> csvRow;
        int r = 0;
        while ((csvRow = this.csvReader.readNextRow(pAddParam, reader,
          bankCsvMethod.getCsvMethod())) != null) {
          r++;
          if (r == 1 && bankCsvMethod.getCsvMethod().getHasHeader()) {
            continue;
          }
          BankStatementLine bsl = new BankStatementLine();
          bsl.setIdDatabaseBirth(getSrvOrm().getIdDatabase());
          bsl.setItsOwner(pEntity);
          String dateStr = csvRow
            .get(bankCsvMethod.getDateCol().getItsIndex() - 1);
          try {
            SimpleDateFormat sdf = new SimpleDateFormat(bankCsvMethod
              .getDateCol().getDataFormat());
            bsl.setItsDate(sdf.parse(dateStr, new ParsePosition(0)));
          } catch (Exception ee) {
            throw new ExceptionWithCode(ExceptionWithCode.CONFIGURATION_MISTAKE,
              "Wrong date or its format! Value/Format: " + dateStr
                + "/" + bankCsvMethod.getDateCol().getDataFormat(), ee);
          }
          String amountStr = csvRow
            .get(bankCsvMethod.getAmountCol().getItsIndex() - 1);
          try {
            if (bankCsvMethod.getAmountCol().getDataFormat() != null) {
              String[] seps = bankCsvMethod.getAmountCol().getDataFormat()
                .split(",");
              for (int i = 0; i < 2; i++) {
                if ("SPACE".equals(seps[i])) {
                  seps[i] = " ";
                } else if ("COMMA".equals(seps[i])) {
                  seps[i] = ",";
                }
              }
              if (!"NONE".equals(seps[0])) {
                amountStr = amountStr.replace(seps[0], ".");
              }
              if (!"NONE".equals(seps[1])) {
                amountStr = amountStr.replace(seps[1], "");
              }
            }
            bsl.setItsAmount(new BigDecimal(amountStr));
          } catch (Exception ee) {
            throw new ExceptionWithCode(ExceptionWithCode.CONFIGURATION_MISTAKE,
              "Wrong amount or its format! Value/Format: " + amountStr
                + "/" + bankCsvMethod.getAmountCol().getDataFormat(), ee);
          }
          String descr = null;
          if (bankCsvMethod.getDescriptionCol() != null) {
            descr = csvRow
              .get(bankCsvMethod.getDescriptionCol().getItsIndex() - 1);
          }
          if (bankCsvMethod.getStatusCol() != null) {
            String statusStr = csvRow
              .get(bankCsvMethod.getStatusCol().getItsIndex() - 1);
            if (descr == null) {
              descr = statusStr;
            } else {
              descr += "/" + statusStr;
            }
            if (bankCsvMethod.getAcceptedWords() != null
              && !bankCsvMethod.getAcceptedWords().contains(statusStr)) {
              bsl.setItsStatus(EBankEntryStatus.OTHER);
            }
            if (bankCsvMethod.getVoidedWords() != null
              && bankCsvMethod.getVoidedWords().contains(statusStr)) {
              bsl.setItsStatus(EBankEntryStatus.VOIDED);
            }
          }
          bsl.setDescriptionStatus(descr);
          getSrvOrm().insertEntity(pAddParam, bsl);
        }
      } finally {
        if (reader != null) {
          reader.close();
        }
      }
    } else {
      if (pEntity.getIsNew()) {
        getSrvOrm().insertEntity(pAddParam, pEntity);
      } else {
        getSrvOrm().updateEntity(pAddParam, pEntity);
      }
    }
    return pEntity;
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
   * <p>Getter for csvReader.</p>
   * @return ICsvReader
   **/
  public final ICsvReader getCsvReader() {
    return this.csvReader;
  }

  /**
   * <p>Setter for csvReader.</p>
   * @param pCsvReader reference
   **/
  public final void setCsvReader(final ICsvReader pCsvReader) {
    this.csvReader = pCsvReader;
  }
}

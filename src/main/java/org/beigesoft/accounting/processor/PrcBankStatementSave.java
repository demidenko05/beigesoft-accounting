package org.beigesoft.webstore.processor;

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

import java.util.Date;
import java.util.Map;
import java.util.List;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import org.beigesoft.model.IRequestData;
import org.beigesoft.service.IEntityProcessor;
import org.beigesoft.service.ISrvOrm;
import org.beigesoft.service.ICsvReader;
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
      String bankCsvMethodId = (String) pRequestData
        .getAttribute("bankCsvMethod");
      BankCsvMethod bankCsvMethod = getSrvOrm().retrieveEntityById(pAddParam,
        BankCsvMethod.class, Long.parseLong(bankCsvMethodId));
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
        while ((csvRow = this.csvReader.readNextRow(pAddParam, reader,
          bankCsvMethod.getCsvMethod())) != null) {
          BankStatementLine bsl = new BankStatementLine();
          bsl.setIdDatabaseBirth(getSrvOrm().getIdDatabase());
          bsl.setItsOwner(pEntity);
          
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

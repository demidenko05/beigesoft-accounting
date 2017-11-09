package org.beigesoft.accounting.service;

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
import java.util.Map;

import org.beigesoft.accounting.persistable.base.ADrawItemSourcesLine;
import org.beigesoft.accounting.persistable.UseMaterialEntry;

/**
 * <p>Business service for draw material for manufacture product
 * or another material.</p>
 *
 * @param <RS> platform dependent record set type
 * @author Yury Demidenko
 */
public class SrvUseMaterialEntry<RS>
  extends ASrvDrawItemEntry<UseMaterialEntry, RS> {

  /**
   * <p>Get draw item entry class.</p>
   * @return draw item entry class
   **/
  @Override
  public final Class<UseMaterialEntry> getDrawItemEntryClass() {
    return UseMaterialEntry.class;
  }

  /**
   * <p>Create draw item entry.</p>
   * @param pAddParam additional param
   * @return draw item entry
   **/
  @Override
  public final UseMaterialEntry createDrawItemEntry(
    final Map<String, Object> pAddParam) {
    return new UseMaterialEntry();
  }

  /**
   * <p>Get draw item sources.</p>
   * @param pAddParam additional param
   * @return draw item sources
   * @throws Exception - an exception
   **/
  @Override
  public final List<? extends ADrawItemSourcesLine>
    getDrawItemSources(
      final Map<String, Object> pAddParam) throws Exception {
    return getSrvAccSettings().lazyGetAccSettings(pAddParam)
      .getDrawMaterialSources();
  }
}

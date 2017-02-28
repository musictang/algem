/*
 * @(#)ActionService.java  2.9.4.14 04/01/16
 * 
 * Copyright (c) 1999-2015 Musiques Tangentes. All Rights Reserved.
 *
 * This file is part of Algem.
 * Algem is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Algem is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Algem. If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package net.algem.planning;

import java.sql.SQLException;
import java.util.List;
import java.util.Vector;
import net.algem.config.*;
import net.algem.util.DataCache;
import net.algem.util.MessageUtil;
import net.algem.util.model.Model;

/**
 * Service class for actions.
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.14
 * @since 2.5.a 27/06/12
 */
public class ActionService
{
  private LevelIO levelIO;
  private StatusIO statusIO;
  private DataCache dataCache;

  public ActionService() {
  }
  
  public ActionService(DataCache dataCache) {
    this.dataCache = dataCache;
    levelIO = (LevelIO) DataCache.getDao(Model.Level);
    statusIO = (StatusIO) DataCache.getDao(Model.Status);
  }

  public String verifyLevel(GemParam n) throws SQLException {
    verify(n);
    Vector<GemParam> nv = levelIO.find("WHERE id != " + n.getId() + " AND code = '" + n.getCode() + "'");
    if (nv != null && nv.size() > 0) {
      return MessageUtil.getMessage("existing.code.warning");
    }
    return null;
  }
  
  public String verifyStatus(GemParam n) throws SQLException {
    verify(n);
    Vector<GemParam> nv = statusIO.find("WHERE id != " + n.getId() + " AND code = '" + n.getCode() + "'");
    if (nv != null && nv.size() > 0) {
      return MessageUtil.getMessage("existing.code.warning");
    }
    return null;
  }
  
  public String verify(GemParam p) throws SQLException {
    if (p.getLabel().length() > GemParamIO.MAX_LABEL) {
      return MessageUtil.getMessage("label.length.error", new Object[] {LevelIO.MAX_LABEL});
    }
    if (!p.getCode().matches("^\\p{Alnum}$")) {
      return MessageUtil.getMessage("code.alphanumeric.error");
    }
    return null;
  }

  public Vector<GemParam> getLevelAll() throws SQLException {
    return new Vector<GemParam>(dataCache.getList(Model.Level).getData());
  }
  
  public void insertLevel(GemParam n) throws SQLException {
    levelIO.insert(n);
  }
  
  public void updateLevel(GemParam n) throws SQLException {
    levelIO.update(n);
  }
  
  public void deleteLevel(GemParam n) throws SQLException {
    levelIO.delete(n);
  }
  
  public Vector<GemParam> getStatusAll() throws SQLException {
    return new Vector<GemParam>(dataCache.getList(Model.Status).getData());
  }
  
  public void insertStatus(GemParam n) throws SQLException {
    statusIO.insert(n);
  }
  
  public void updateStatus(GemParam n) throws SQLException {
    statusIO.update(n);
  }
  
  public void deleteStatus(GemParam n) throws SQLException {
    statusIO.delete(n);
  }
  
  public List<AgeRange> getAgeRangeAll() throws SQLException {
    return dataCache.getList(Model.AgeRange).getData();
  }

}

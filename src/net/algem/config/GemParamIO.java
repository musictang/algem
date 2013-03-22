/*
 * @(#)GemParamIO.java 2.8.a 15/03/13
 * 
 * Copyright (c) 1999-2012 Musiques Tangentes. All Rights Reserved.
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

package net.algem.config;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import net.algem.util.DataConnection;
import net.algem.util.model.TableIO;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.a
 * @since 2.5.a 05/07/12
 */
public abstract class GemParamIO 
  extends TableIO 
{

  protected final static String COLUMNS = "id, code, libelle";
  public final static int MAX_LABEL = 128;
  
  protected DataConnection dc;

  public void insert(GemParam n) throws SQLException {
    int id = nextId(getSequence(), dc);
    String query = "INSERT INTO " + getTable() + " VALUES(" + id + ",'" + n.getCode().trim() + "','" + n.getLabel().trim() + "')";
    dc.executeUpdate(query);
    n.setId(id);
  }

  public void update(GemParam n) throws SQLException {
    String query = "UPDATE " + getTable() + " SET code = '" + n.getCode().trim() + "', libelle='" + n.getLabel().trim() + "' WHERE id = " + n.getId();
    dc.executeUpdate(query);
  }

  public void delete(GemParam n) throws SQLException {
    String query = "DELETE FROM " + getTable() + " WHERE id = " + n.getId();
    dc.executeUpdate(query);
  }

  public Vector<GemParam> find() throws SQLException {
    return find(null);
  }

  public GemParam find(int id) throws SQLException {
    String query = "SELECT " + COLUMNS + " FROM " + getTable() + " WHERE id = " + id;

    ResultSet rs = dc.executeQuery(query);
    if (rs.next()) {
      return new GemParam(rs.getInt(1), rs.getString(2), rs.getString(3));
    }
    return null;
  }
  
  public Vector<GemParam> find(String where) throws SQLException {
    String query = "SELECT " + COLUMNS + " FROM " + getTable();
    if (where != null) {
      query += " " + where;
    }
    query += " ORDER BY id";
    Vector<GemParam> vn = new Vector<GemParam>();
    ResultSet rs = dc.executeQuery(query);
    while (rs.next()) {
      GemParam n = new GemParam(rs.getInt(1), rs.getString(2), rs.getString(3));
      vn.addElement(n);
    }
    return vn;
  }
  
  protected abstract String getSequence();
  
  protected abstract String getTable();

}

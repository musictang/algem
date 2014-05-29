/*
 * @(#)EmployeeTypeIO.java	2.8.v 28/05/14
 *
 * Copyright (c) 1999-2014 Musiques Tangentes. All Rights Reserved.
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

package net.algem.contact;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;
import net.algem.config.GemParam;
import net.algem.config.GemParamIO;
import net.algem.util.DataConnection;
import net.algem.util.model.Cacheable;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.v
 * @since 2.8.v 28/05/14
 */
public class EmployeeTypeIO
  extends GemParamIO
  implements Cacheable
{

  private final static String COLS = "id, libelle";
  private final static String TABLE = "categorie_salarie";
  private final static String SEQUENCE = "categorie_salarie_id_seq";

  public EmployeeTypeIO(DataConnection dc) {
    this.dc = dc;
  }

  @Override
  protected String getSequence() {
    return SEQUENCE;
  }

  @Override
  protected String getTable() {
    return TABLE;
  }

  @Override
  public List<GemParam> load() throws SQLException {
    return find();
  }

  @Override
  public Vector<GemParam> find(String where) throws SQLException {
    String query = "SELECT " + COLS + " FROM " + getTable();
    if (where != null) {
      query += " " + where;
    }
    query += " ORDER BY id";
    Vector<GemParam> vn = new Vector<GemParam>();
    ResultSet rs = dc.executeQuery(query);
    while (rs.next()) {
      GemParam n = new GemParam(rs.getInt(1));
      n.setLabel(rs.getString(2));
      vn.addElement(n);
    }
    return vn;
  }

}

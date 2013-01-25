/*
 * @(#)CategoryOccupIO.java	2.6.a 20/09/12
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
 * Io methods for class {@link net.algem.config.CategoryOccup}.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class CategoryOccupIO
        extends TableIO
{

  public static final String TABLE = "categorie_prof";
  public static final String SEQUENCE = "idcategorieprof";

  public static void insert(CategoryOccup c, DataConnection dc) throws SQLException {
    
    int num = nextId(SEQUENCE, dc);

    String query = "INSERT INTO " + TABLE + " VALUES("
            + "'" + num
            + "','" + c.getLabel()
            + "')";

    dc.executeUpdate(query);
    c.setId(num);
  }

  public static void update(CategoryOccup c, DataConnection dc) throws SQLException {
    String query = "UPDATE " + TABLE + " SET nom='" + c.getLabel() + "'";
    query += " WHERE id=" + c.getId();

    dc.executeUpdate(query);
  }

  public static void delete(CategoryOccup c, DataConnection dc) throws SQLException {
    String query = "DELETE FROM " + TABLE + " WHERE id=" + c.getId();

    dc.executeUpdate(query);
  }

  public static CategoryOccup findId(String n, DataConnection dc) throws SQLException {
    String query = "WHERE id=" + n;
    Vector<CategoryOccup> v = find(query, dc);
    if (v.size() > 0) {
      return v.elementAt(0);
    }
    return null;
  }

  public static Vector<CategoryOccup> find(String where, DataConnection dc) throws SQLException {
    Vector<CategoryOccup> v = new Vector<CategoryOccup>();
    String query = "SELECT * FROM " + TABLE + " " + where;

    ResultSet rs = dc.executeQuery(query);
    while (rs.next()) {
      CategoryOccup c = new CategoryOccup();
      c.setId(rs.getInt(1));
      c.setLabel(rs.getString(2).trim());

      v.addElement(c);
    }
    rs.close();

    return v;
  }
}

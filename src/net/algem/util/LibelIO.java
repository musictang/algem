/*
 * @(#)LibelIO.java	2.8.i 04/07/13
 * 
 * Copyright (c) 1999-2013 Musiques Tangentes. All Rights Reserved.
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
package net.algem.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import net.algem.util.model.TableIO;

/**
 * Io methods for class {@link net.algem.libel.Libel}.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.i
 */
public class LibelIO
        extends TableIO
{

  private static final String TABLE = "libelles";

  public static void insert(Libel b, DataConnection dc) throws SQLException {
    String query = "INSERT INTO " + TABLE + "(code,libelle) VALUES("
            + "'" + b.getCode()
            + "','" + escape(b.getLibelle())
            + "')";

    dc.executeUpdate(query);
  }

  public static void update(Libel b, DataConnection dc) throws SQLException {
    String query = "UPDATE " + TABLE + " SET "
            + "libelle='" + escape(b.getLibelle())
            + "' WHERE code='" + b.getCode() + "'";

    dc.executeUpdate(query);
  }

  public static void delete(Libel b, DataConnection dc) throws SQLException {
  }

  /**
   * Search a libel from code identifier {@code code}.
   *
   * @param dc
   * @param code identifier code
   * @return a libel instance or null
   */
  public static Libel findCode(String code, DataConnection dc) {
    String query = "WHERE code = '" + code + "'";
    List<Libel> v = find(query, dc);
    if (v.size() > 0) {
      return (Libel) v.get(0);
    }
    return null;
  }

  public static List<Libel> find(String where, DataConnection dc) {
    List<Libel> v = new ArrayList<>();
    String query = "SELECT * FROM " + TABLE + " " + where;
    query += " ORDER BY code";
    try (ResultSet rs = dc.executeQuery(query)) {
      while (rs.next()) {
        Libel b = new Libel();
        b.setId(rs.getInt(1));
        b.setCode(rs.getString(2));
        b.setLibelle(rs.getString(3));
        v.add(b);
      }
    } catch (Exception e) {
      GemLogger.logException(query, e);
    }
    return v;
  }
}

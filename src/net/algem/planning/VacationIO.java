/*
 * @(#)VacationIO.java	2.6.a 03/10/12
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
package net.algem.planning;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Vector;
import net.algem.util.DataConnection;
import net.algem.util.GemLogger;
import net.algem.util.model.TableIO;

/**
 * IO methods for class {@link net.algem.planning.Vacation }.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 * @since 1.0a 07/07/1999
 */
public class VacationIO
        extends TableIO
{

  public static final String TABLE = "vacance";
  public static final String COLUMNS = "jour, ptype, vid, libelle";

  public static void insert(Vacation v, DataConnection dc) throws SQLException {
    String query = "INSERT INTO " + TABLE + " VALUES("
            + "'" + v.getDay()
            + "','" + v.getType()
            + "','" + v.getVid()
            + "','" + v.getLabel()
            + "')";

    dc.executeUpdate(query);
  }

  public static void update(Vacation v, DataConnection dc) throws SQLException {
    String query = "UPDATE " + TABLE + " SET "
            + "libelle = '" + v.getLabel() + "'";
    query += " WHERE jour = '" + v.getDay() + "' AND vid = " + v.getVid();

    dc.executeUpdate(query);
  }

  public static void delete(Vacation v, DataConnection dc) throws SQLException {
    String query = "DELETE FROM " + TABLE + " WHERE jour = '" + v.getDay() + "' AND vid = " + v.getVid();// todo ->vid= getId

    dc.executeUpdate(query);
  }

  public static Vacation findDay(Date day, int id, DataConnection dc) {
    DateFr d = new DateFr();
    d.set(day);
    String query = "WHERE jour = '" + d + "' AND vid = " + id;
    Vector<Vacation> v = find(query, dc);
    if (v.size() > 0) {
      return v.elementAt(0);
    }
    return null;
  }

  public static Vector<Vacation> find(String where, DataConnection dc) {
    Vector<Vacation> v = new Vector<Vacation>();
    String query = "SELECT * FROM " + TABLE + " " + where + " ORDER BY jour, vid";
    try {
      ResultSet rs = dc.executeQuery(query);
      while (rs.next()) {
        Vacation j = new Vacation();
        j.setDay(new DateFr(rs.getString(1)));
        j.setType(rs.getInt(2));
        j.setVid(rs.getInt(3));
        j.setLabel(rs.getString(4).trim());

        v.addElement(j);
      }
      rs.close();
    } catch (SQLException e) {
      GemLogger.logException(query, e);
    }
    return v;
  }
}

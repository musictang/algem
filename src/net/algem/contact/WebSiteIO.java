/*
 * @(#)WebSiteIO.java	2.9.7 12/05/16
 *
 * Copyright (c) 1999-2016 Musiques Tangentes. All Rights Reserved.
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
import java.util.Vector;
import net.algem.util.DataConnection;
import net.algem.util.model.TableIO;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.7
 */
public class WebSiteIO
        extends TableIO
{

  public final static String TABLE = "siteweb";
  public final static String COLUMNS = "idx, ipder, url, type, ptype";

  /**
   * 
   * @param w web site
   * @param idx index
   * @param dc data connection
   * @throws SQLException 
   */
  public static void insert(WebSite w, int idx, DataConnection dc) throws SQLException {
    String query = "INSERT INTO " + TABLE + " VALUES("
            + idx
            + "," + w.getIdper()
            + ",'" + w.getUrl()
            + "'," + w.getType()
            + ", " + w.getPtype();
    query += ")";

    dc.executeUpdate(query);
  }

  /**
   * 
   * @param s site
   * @param idx index
   * @param dc data connection
   * @throws SQLException 
   */
  public static void update(WebSite s, int idx, DataConnection dc) throws SQLException {
    String query = "UPDATE " + TABLE
            + " SET url = '" + s.getUrl()
            + "', type = " + s.getType()
            + " WHERE idx = " + idx + " AND idper = " + s.getIdper();
    if (s.getPtype() != 4) { // ptype room bug
      query += " AND ptype = " + s.getPtype();
    }

    dc.executeUpdate(query);
  }

  /**
   * 
   * @param idper contact id
   * @param ptype person type
   * @param dc data connection
   * @throws SQLException 
   */
  public static void delete(int idper, int ptype, DataConnection dc) throws SQLException {
    String query = "DELETE FROM " + TABLE + " WHERE idper = " + idper + " AND ptype = " + ptype;
    dc.executeUpdate(query);
  }

  /**
   * 
   * @param s url
   * @param idx index
   * @param dc data connection
   * @throws SQLException 
   */
  public static void delete(WebSite s, int idx, DataConnection dc) throws SQLException {
    String query = "DELETE FROM " + TABLE + " WHERE idper = " + s.getIdper() + " AND idx = " + idx
            + " AND ptype = " + s.getPtype();
    dc.executeUpdate(query);
  }

  public static Vector<WebSite> find(int idper, int ptype, DataConnection dc) throws SQLException {
    Vector<WebSite> v = new Vector<WebSite>();
    String query = "SELECT * FROM " + TABLE + " WHERE idper=" + idper + " AND ptype = " + ptype + " ORDER BY idx";

    ResultSet rs = dc.executeQuery(query);
    while (rs.next()) {
      WebSite w = new WebSite();
      w.setIdx(rs.getInt(1));
      w.setIdper(rs.getInt(2));
      w.setUrl(rs.getString(3).trim());
      w.setType(rs.getInt(4));
      w.setPtype(rs.getShort(5));

      v.addElement(w);
    }
    rs.close();

    return v;
  }
  
  /**
   * Find by type.
   * @param type category
   * @param dc data connection
   * @return the number of results or 0 if no site found.
   * @throws SQLException 
   */
  public static int find(int type, DataConnection dc) throws SQLException {
    String query = "SELECT * FROM " + TABLE + " WHERE type = " + type; 
    ResultSet rs = dc.executeQuery(query);
    int count = 0;
    while (rs.next()) {
      count ++;
    }
    return count;
  }
  
}

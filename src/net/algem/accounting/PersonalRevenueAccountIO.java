/*
 * @(#)PersonalRevenueAccountIO.java 2.9.4.13 07/10/15
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

package net.algem.accounting;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import net.algem.util.DataConnection;
import net.algem.util.model.TableIO;

/**
 * Persistence of matching between personal and revenue accounts.
 * 
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.13
 * @since 2.2.l 07/12/11
 */
public class PersonalRevenueAccountIO
        extends TableIO
{
  public static final String tableName = "tiersproduit";
  public static final String cols = "tiers, produit";

  public static Map<Account, Account> find(DataConnection dc) throws SQLException {
    String query = "SELECT "+cols+" FROM " +tableName+" ORDER BY tiers";
    ResultSet rs = dc.executeQuery(query);

    Map<Account, Account> map = new HashMap<Account, Account>();
    while (rs.next()) {
      Account c = AccountIO.find(rs.getInt(1), dc);
      Account p = AccountIO.find(rs.getInt(2), dc);
      if (c != null && p != null) {
        map.put(c,p);
      } else delete(rs.getInt(1), dc);
    }
    return map.isEmpty() ? null : map;
  }

  public static int find(int k, DataConnection dc) throws SQLException {

    int p = 0;

    String query = "SELECT produit FROM "+tableName+" WHERE tiers = '"+k+"'";
    ResultSet rs = dc.executeQuery(query);
    while (rs.next()) {
      p = rs.getInt(1);
    }
    return p;
  }

  public static void insert(int t, int p, DataConnection dc) throws SQLException {
    String query = "INSERT INTO "+tableName+" VALUES('"+t+"','"+p+"')";
    dc.executeUpdate(query);
  }

  public static void update(int t, int p, DataConnection dc) throws SQLException {
    String query = "UPDATE "+tableName+" SET produit = '"+p+"' WHERE tiers = '"+t+"'";
    dc.executeUpdate(query);
  }

  public static void delete(int t, DataConnection dc) throws SQLException {
    String query = "DELETE FROM "+tableName+" WHERE tiers = '"+t+"'";
    dc.executeUpdate(query);
  }

}

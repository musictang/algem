/*
 * @(#)AccountIO.java	2.14.0 08/06/17
 *
 * Copyright (c) 1999-2017 Musiques Tangentes. All Rights Reserved.
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
import java.util.List;
import java.util.Vector;
import net.algem.util.DataConnection;
import net.algem.util.model.TableIO;

/**
 * Account persistence.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.14.0
 * @since 2.3.c 08/03/12
 */
public class AccountIO
        extends TableIO
{

  public static final String SEQUENCE = "compte_id_seq";
  public static final String TABLE = "compte";
  public static final String COLUMNS = "id, numero, libelle, actif";
  public static final String ORDER_COLUMN = "libelle";

  /**
   *
   * @param c
   * @param dc
   * @throws SQLException
   */
  public static void insert(Account c, DataConnection dc) throws SQLException {

    int id = TableIO.nextId(SEQUENCE, dc);
    c.setId(id);

    String query = "INSERT INTO " + TABLE + " VALUES ("
            + c.getId() + ", '" + c.getNumber() + "', '" + escape(c.getLabel()) + "', " + c.isActive()
            + ")";
    dc.executeUpdate(query);
  }

  /**
   *
   * @param c
   * @param dc
   * @throws SQLException
   */
  public static void update(Account c, DataConnection dc) throws SQLException {

    String query = "UPDATE " + TABLE
            + " SET numero =  '" + c.getNumber() + "', libelle = '" + escape(c.getLabel()) + "', actif = " + c.isActive()
            + " WHERE id = " + c.getId();
    dc.executeUpdate(query);
  }

  public static void delete(Account c, DataConnection dc) throws SQLException {
    String query = "DELETE FROM " + TABLE + " WHERE id = " + c.getId();
    dc.executeUpdate(query);
  }

  /**
   *
   * @param active if true, only activated accounts are returned
   * @param dc connexion
   *
   * @return a list of accounts
   * @throws SQLException
   */
  public static Vector<Account> find(boolean active, DataConnection dc) throws SQLException {

    String where = "";
    if (active) {
      where = " WHERE actif = true";
    } // all accounts otherwise
    return find(where, ORDER_COLUMN, dc);
  }

  /**
   *
   * @param where restrict expression
   * @param orderColumn the column used to sort the results
   * @param dc connexion
   * @return a list of accounts
   * @throws SQLException
   */
  public static Vector<Account> find(String where, String orderColumn, DataConnection dc) throws SQLException {
    Vector<Account> v = new Vector<Account>();
    String query = "SELECT " + COLUMNS + " FROM " + TABLE;
    if (where != null && !where.isEmpty()) {
      query += " " + where;
    }
    if (orderColumn != null) {
      query += " ORDER BY " + orderColumn;
    }
    try (ResultSet rs = dc.executeQuery(query)) {
      while (rs.next()) {
        Account c = new Account(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getBoolean(4));
        v.addElement(c);
      }
    }

    return v;
  }

  public static Vector<Account> find(String where, DataConnection dc) throws SQLException {
    return find(where, ORDER_COLUMN, dc);
  }

  /**
   *
   * @param id
   * @param dc
   * @return an account
   * @throws SQLException
   */
  public static Account find(int id, DataConnection dc) throws SQLException {
    return find(id, true, dc);
  }

  public static Account find(int id, boolean actif, DataConnection dc) throws SQLException {

    Account c = null;
    String query = "SELECT " + COLUMNS + " FROM " + TABLE + " WHERE id = " + id;
    if (actif) {
      query += " AND actif = " + actif;
    }
    try (ResultSet rs = dc.executeQuery(query)) {
      if (rs.next()) {
        c = new Account(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getBoolean(4));
      }
    }
    return c;
  }

  public static List<Account> load(DataConnection dc) throws SQLException {
    return find(null, ORDER_COLUMN, dc);
  }
}

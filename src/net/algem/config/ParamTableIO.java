/*
 * @(#)ParamTableIO     2.6.a 01/08/2012
 *
 * Copyright (c) 2009 Musiques Tangentes All Rights Reserved.
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
import net.algem.util.GemLogger;
import net.algem.util.model.TableIO;

/**
 * IO methods for class {@link net.algem.config.Param}.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class ParamTableIO
        extends TableIO
{

  public static void insert(String table, String seq, Param param, DataConnection dc) throws SQLException {
    
    int numero = nextId(seq, dc);
    String query = "INSERT INTO " + table + " VALUES('" + numero + "','" + param.getValue() + "')";
    dc.executeUpdate(query);
    param.setKey(String.valueOf(numero));
  }

  public static void insert(String table, Param param, DataConnection dc) throws SQLException {
    String query = "INSERT INTO " + table + " VALUES('" + param.getKey() + "','" + param.getValue() + "')";
    dc.executeUpdate(query);
  }

  public static void update(String table, String columnKey, String colonne, Param param, DataConnection dc) throws SQLException {
    String query = "UPDATE " + table + " SET " + colonne + " = '" + param.getValue()
            + "' WHERE " + columnKey + " = '" + param.getKey() + "'";
    dc.executeUpdate(query);
  }

  /**
   * Updates a param.
   *
   * @param table
   * @param columnKey column key
   * @param cle key
   * @param col column value
   * @param param param
   * @param dc data Connexion
   * @throws SQLException
   */
  public static void update(String table, String columnKey, String cle, String col, Param param, DataConnection dc) throws SQLException {
    String query = "UPDATE " + table + " SET " + columnKey + " = '" + param.getKey()
            + "', " + col + " = '" + param.getValue()
            + "' WHERE " + columnKey + " = '" + cle + "'";

    dc.executeUpdate(query);
  }

  /**
   * Deletes a param.
   *
   * @param table 
   * @param columnKey column key
   * @param param param
	 * @param dc data Connexion
   * @throws SQLException
   */
  public static void delete(String table, String columnKey, Param param, DataConnection dc) throws SQLException {
    String query = "DELETE FROM " + table + " WHERE " + columnKey + " = '" + param.getKey() + "'";
    dc.executeUpdate(query);
  }

  /**
   * Finds a param by criterion.
   *
   * @param table
   * @param where
	 * @param dc data Connexion
   * @return a param
   */
  public static Param findBy(String table, String where, DataConnection dc) {
    Vector<Param> v = find(table, null, where, dc);
    if (v.size() > 0) {
      return v.elementAt(0);
    }
    return null;
  }

  /**
   * Finds a param by key
   *
   * @param _table
   * @param columnKey column key
   * @param key key
   * @param dc data Connexion
   * @return a param
   */
  public static Param findByKey(String _table, String columnKey, String key, DataConnection dc) {
    String where = " WHERE " + columnKey + " = '" + key + "'";
    return findBy(_table, where, dc);
  }

  /**
   * Finds a list of param without criterion.
   * 
   * @param _table 
   * @param _sortColumn column for sorting
   * @param dc data Connexion
   * @return a list of params
   */
  public static Vector<Param> find(String _table, String _sortColumn, DataConnection dc) {
    return find(_table, _sortColumn, null, dc);
  }

  /**
   * Finds a list of params.
   *
   * @param table
   * @param sortColumn column for sorting
   * @param where search expression
   * @param dc data Connexion
   * @return a list of params
   */
  public static Vector<Param> find(String table, String sortColumn, String where, DataConnection dc) {
    Vector<Param> v = new Vector<Param>();
    String query = "SELECT * FROM " + table;
    if (where != null) {
      query += " " + where;
    }
    if (sortColumn != null) {
      query += " ORDER BY " + sortColumn;
    }
    try {
      ResultSet rs = dc.executeQuery(query);
      while (rs.next()) {
        Param p = new Param();
        p.setKey(rs.getString(1).trim());
        p.setValue(rs.getString(2).trim());

        v.addElement(p);
      }
      rs.close();
    } catch (SQLException e) {
      GemLogger.logException(query, e);
    }
    return v;
  }

	/**
     * Gets param values.
	 * 
	 * @param table
	 * @param column
	 * @param dc data Connexion
	 * @return an array of strings
	 */
  public static String[] getValues(String table, String column, DataConnection dc) {
    Vector<? extends Param> v = ParamTableIO.find(table, column, dc);
    String[] values = new String[v.size()];
    for (int i = 0; i < v.size(); i++) {
      values[i] = v.get(i).getValue();
    }
    return values;
  }
}

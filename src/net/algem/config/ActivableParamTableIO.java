/*
 * @(#)ActivableParamTableIO.java	2.6.a 02/08/2012
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
import net.algem.util.GemLogger;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class ActivableParamTableIO {

	/**
	 * 
	 * @param table
	 * @param seq
	 * @param p
	 * @param dc
	 * @throws SQLException 
	 */
	public static void insert(String table, String seq, ActivableParam p, DataConnection dc) throws SQLException {
		int n = 0;

		String query = "SELECT nextval('" + seq + "')";

		ResultSet rs = dc.executeQuery(query);
		rs.next();
		n = rs.getInt(1);

		p.setKey(String.valueOf(n));
		insert(table, p, dc);
	}

	/**
	 * 
	 * @param table
	 * @param p
	 * @param dc
	 * @throws SQLException 
	 */
	public static void insert(String table, ActivableParam p, DataConnection dc) throws SQLException {
		String query = "INSERT INTO " + table + " VALUES('" + p.getKey() + "','" + p.getValue() + "','" + (p.isActive() ? "t" : "f") + "')";
		dc.executeUpdate(query);
	}

	/**
	 * 
	 * @param table
	 * @param columnKey
	 * @param columnName
	 * @param columnFilter
	 * @param p
	 * @param dc
	 * @throws SQLException 
	 */
	public static void update(String table, String columnKey, String columnName, String columnFilter, ActivableParam p, DataConnection dc)
		throws SQLException {
		String query = "UPDATE " + table + " SET " + columnName + " = '" + p.getValue()
			+ "', " + columnFilter + " = '" + (p.isActive() ? "t" : "f")
			+ "' WHERE " + columnKey + " = '" + p.getKey() + "'";

		dc.executeUpdate(query);
	}

	/**
	 * 
	 * @param table
	 * @param columnName
	 * @param columnFilter
	 * @param dc
	 * @return une liste de paramètres
	 */
	public static Vector<ActivableParam> findActive(String table, String columnName, String columnFilter, DataConnection dc) {
		String where = " WHERE " + columnFilter + " = 't'";
		return find(table, columnName, where, dc);
	}

	/**
	 * 
	 * @param _table
	 * @param _sortColumn
	 * @param where
	 * @param dc
	 * @return une liste de paramètres
	 */
	public static Vector<ActivableParam> find(String _table, String _sortColumn, String where, DataConnection dc) {
		Vector<ActivableParam> v = new Vector<ActivableParam>();
		String query = "SELECT * FROM " + _table;
		if (where != null) {
			query += " " + where;
		}
		if (_sortColumn != null) {
			query += " ORDER BY " + _sortColumn;
		}
		try {
			ResultSet rs = dc.executeQuery(query);
			while (rs.next()) {
				ActivableParam p = new ActivableParam();
				p.setKey(rs.getString(1).trim());
				p.setValue(rs.getString(2).trim());
				p.setActive(rs.getBoolean(3));

				v.addElement(p);
			}
			rs.close();
		} catch (Exception e) {
			GemLogger.logException(query, e);
		}
		return v;
	}
}

/*
 * @(#)CityIO.java	2.6.a 03/10/12
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
package net.algem.contact;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import net.algem.util.DataConnection;
import net.algem.util.GemLogger;
import net.algem.util.model.TableIO;

/**
 * IO methods for class {@link net.algem.contact.City}
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class CityIO
	extends TableIO {
    // TODO : plusieurs villes peuvent comporter le même code postal. Utiliser un id.
    public final static String TABLE = "ville";

	public static void insert(City v, DataConnection dc) throws SQLException {
		String query = "INSERT INTO " + TABLE + " VALUES("
			+ "'" + v.getCdp()
			+ "','" + escape(v.getCity().toUpperCase())
			+ "')";

		dc.executeUpdate(query);
	}

	public static void update(City v, DataConnection dc) throws SQLException {
		String query = "UPDATE " + TABLE + " SET nom = '" + escape(v.getCity()) + "' WHERE cdp = '" + v.getCdp() + "'";
		dc.executeUpdate(query);
	}

	public static void delete(City v, DataConnection dc) throws SQLException {
		String query = "DELETE FROM " + TABLE + " WHERE cdp = '" + v.getCdp() + "'";
		dc.executeUpdate(query);
	}

	public static City findCdp(String cdp, DataConnection dc) {
		String query = "WHERE cdp = '" + cdp + "'";
		Vector<City> v = find(query, dc);
		if (v != null && v.size() > 0) {
			return v.elementAt(0);
		}
		return null;
	}

	public static Vector<City> find(String where, DataConnection dc) {
		Vector<City> v = new Vector<City>();
		String query = "SELECT cdp, nom FROM " + TABLE + " " + where + " ORDER BY cdp";
		try {
			ResultSet rs = dc.executeQuery(query);
			while (rs.next()) {
				City n = new City();
				n.setCdp(rs.getString(1));
				n.setCity(unEscape(rs.getString(2).trim()));

				v.addElement(n);
			}
			rs.close();
		} catch (SQLException e) {
			GemLogger.logException(query, e);
		}
		return v;
	}
}

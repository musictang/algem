/*
 * @(#)MaintenanceIO.java	1.0a 7/7/1999
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
package net.algem.opt;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Vector;
import net.algem.planning.DateFr;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;
import net.algem.util.GemLogger;
import net.algem.util.model.TableIO;

/**
 * méthodes IO classe modele.Maintenance
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 * @deprecated 
 */
public class MaintenanceIO
	extends TableIO {

	public static void insert(Maintenance v, DataConnection dc) throws SQLException {
		DateFr jour = new DateFr(new Date());

		String query = "INSERT INTO maintenance VALUES("
			+ "'" + jour
			+ "','" + v.getPersonne()
			+ "'," + v.getType()
			+ ",'" + escape(v.getTexte())
			+ "','f')";

		dc.executeUpdate(query);
	}

	public static void update(Maintenance v, DataConnection dc) throws SQLException {
		/*
		 * String query = "UPDATE ville set " +"nom='"+escape(v.getMaintenance())
		 * +"'"; query += " WHERE cdp='"+v.getCdp()+"'";
		 *
		 * dc.executeUpdate(query);
		 */
	}

	public static void delete(Maintenance v, DataCache dc) throws SQLException {
		/*
		 * String query = "DELETE from ville"; query += " WHERE
		 * cdp='"+v.getCdp()+"'";
		 *
		 * dc.executeUpdate(query);
		 */
	}

	public static Vector find(String where, DataConnection dc) {
		Vector v = new Vector();
		String query = "SELECT * FROM maintenance " + where;
		try {
			ResultSet rs = dc.executeQuery(query);
			while (rs.next()) {
				Maintenance n = new Maintenance();
				n.setJour(new DateFr(rs.getString(1)));
				n.setPersonne(rs.getString(2));
				n.setType(rs.getInt(3));
				n.setTexte(rs.getString(4));
				n.setFait(rs.getBoolean(5));

				v.addElement(n);
			}
			rs.close();
		} catch (SQLException e) {
			GemLogger.logException(query, e);
		}
		return v;
	}
}

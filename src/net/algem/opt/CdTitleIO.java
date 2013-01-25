/*
 * @(#)CdTitleIO.java	2.6.a 25/09/12
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
import java.util.Vector;
import net.algem.util.DataConnection;
import net.algem.util.GemLogger;
import net.algem.util.model.TableIO;

/**
 * méthodes IO classe modele.CdTitle
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 * @deprecated 
 */
public class CdTitleIO
	extends TableIO {

	public static void insert(CdTitle t, DataConnection dc) throws SQLException {
		String query = "INSERT INTO cdtitre VALUES("
			+ "," + t.getCd()
			+ "," + t.getNumber()
			+ ",'" + escape(t.getTitre())
			+ "','" + escape(t.getPerformer())
			+ "')";

		dc.executeUpdate(query);
	}

	public static void update(CdTitle t, DataConnection dc) throws SQLException {
		String query = "UPDATE cdtitre SET "
			+ "titre='" + escape(t.getTitre())
			+ "',interprete='" + escape(t.getPerformer())
			+ "'"
			+ " WHERE cd=" + t.getCd() + " and numero=" + t.getNumber();

		dc.executeUpdate(query);
	}

	public static void delete(CdTitle t, DataConnection dc) throws SQLException {
	}

	public static Vector<CdTitle> findId(int n, DataConnection dc) {
		String query = "WHERE cd = " + n;
		return find(query, dc);
	}

	public static Vector<CdTitle> find(String where, DataConnection dc) {
		Vector<CdTitle> v = new Vector<CdTitle>();
		String query = "SELECT * from cdtitre " + where;
		try {
			ResultSet rs = dc.executeQuery(query);
			while (rs.next()) {
				CdTitle t = new CdTitle();
				t.setCd(rs.getInt(1));
				t.setNumber(rs.getInt(2));
				t.setTitle(rs.getString(3));
				t.setPerformer(rs.getString(4));

				v.addElement(t);
			}
			rs.close();
		} catch (Exception e) {
			GemLogger.logException(query, e);
		}
		return v;
	}
}

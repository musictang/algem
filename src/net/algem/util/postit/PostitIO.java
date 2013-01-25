/*
 * @(#)PostitIO.java	2.6.a 21/09/12
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
package net.algem.util.postit;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import net.algem.planning.DateFr;
import net.algem.util.DataConnection;
import net.algem.util.GemLogger;
import net.algem.util.model.TableIO;

/**
 * IO methods for class {@link net.algem.util.postit.Postit}.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class PostitIO
	extends TableIO {

	private final static String TABLE = "postit";
	private final static String SEQUENCE = "idpostit";

	public static void insert(Postit p, DataConnection dc) throws SQLException {
		int numero = nextId(SEQUENCE, dc);

		if (p.getTerm().equals(DateFr.NULLDATE)) {
			p.setTerm(p.getDay());
		}
		String query = "INSERT INTO " + TABLE + " VALUES("
			+ "'" + numero
			+ "','" + p.getType()
			+ "','" + p.getIssuer()
			+ "','" + p.getReceiver()
			+ "','" + p.getDay()
			+ "','" + p.getTerm()
			+ "','" + escape(p.getText())
			+ "')";

		dc.executeUpdate(query);
		p.setId(numero);
	}

	public static void update(Postit p, DataConnection dc) throws SQLException {
		String query = "UPDATE " + TABLE + " SET "
			+ "ptype='" + p.getType()
			+ "',emet='" + p.getIssuer()
			+ "',dest='" + p.getReceiver()
			+ "',jour='" + p.getDay()
			+ "',echeance='" + p.getTerm()
			+ "',texte='" + escape(p.getText())
			+ "'"
			+ " WHERE id=" + p.getId();

		dc.executeUpdate(query);
	}

	public static void delete(Postit p, DataConnection dc) throws SQLException {
		String query = "DELETE FROM " + TABLE + " WHERE id=" + p.getId();

		dc.executeUpdate(query);
	}

	public static Postit findId(int n, DataConnection dc) {
		String query;
		query = "WHERE id=" + n;
		Vector<Postit> v = find(query, dc);
		if (v.size() > 0) {
			return v.elementAt(0);
		}
		return null;
	}

	public static Vector<Postit> find(String where, DataConnection dc) {
		Vector<Postit> v = new Vector<Postit>();
		String query = "SELECT * FROM " + TABLE + " " + where;
		try {
			ResultSet rs = dc.executeQuery(query);
			while (rs.next()) {
				Postit p = new Postit();
				p.setId(rs.getInt(1));
				p.setType(rs.getInt(2));
				p.setIssuer(rs.getInt(3));
				p.setReceiver(rs.getInt(4));
				p.setDay(new DateFr(rs.getString(5)));
				p.setTerm(new DateFr(rs.getString(6)));
				p.setText(rs.getString(7));

				v.addElement(p);
			}
		} catch (SQLException e) {
			GemLogger.logException(query, e);
		}
		return v;
	}
}

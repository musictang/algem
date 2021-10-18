/*
 * @(#)MajorationIO.java	2.6.a 31/07/12
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
import java.util.ArrayList;
import java.util.List;
import net.algem.util.DataConnection;
import net.algem.util.GemLogger;
import net.algem.util.model.TableIO;

/**
 * méthodes IO classe modele.Majoration
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 * @deprecated 
 */
public class MajorationIO
	extends TableIO {
  
	private static final String TABLE = "majoration";
	private static final String SEQUENCE = "idmajoration";

	public static void insert(Majoration m, DataConnection dc) throws SQLException {
		int numero = nextId(SEQUENCE, dc);

		String query = "INSERT INTO " + TABLE + " VALUES("
			+ "'" + numero
			+ "','" + m.getMode()
			+ "'," + m.getPCent()
			+ ")";

		dc.executeUpdate(query);
		m.setId(numero);
	}

	public static void update(Majoration m, DataConnection dc) throws SQLException {
		String query = "UPDATE " + TABLE + " SET "
			+ "mode = '" + m.getMode()
			+ "',pcent = " + m.getPCent()
			+ "";
		query += " WHERE id = " + m.getId();

		dc.executeUpdate(query);
	}

	public static void delete(Majoration m, DataConnection dc) throws SQLException {
		String query = "DELETE FROM " + TABLE + " WHERE id = " + m.getId();

		dc.executeUpdate(query);
	}

	public static List<Majoration> find(String where, DataConnection dc) {
		List<Majoration> v = new ArrayList<>();
		String query = "SELECT * FROM " + TABLE + " " + where;
		try (ResultSet rs = dc.executeQuery(query)) {
			while (rs.next()) {
				Majoration m = new Majoration();
				m.setId(rs.getInt(1));
				m.setMode(rs.getString(2).trim());
				m.setPCent(rs.getInt(3));

				v.add(m);
			}
                
		} catch (SQLException e) {
			GemLogger.logException(query, e);
		}
		return v;
	}
}

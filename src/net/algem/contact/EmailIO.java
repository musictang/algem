/*
 * @(#)EmailIO.java 2.6.a 12/09/12
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
import java.util.ArrayList;
import java.util.List;
import net.algem.util.DataConnection;
import net.algem.util.GemLogger;
import net.algem.util.model.TableIO;

/**
 * IO methods for class Email.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class EmailIO
	extends TableIO {

	public static final String TABLE = "email";

	/**
	 *
	 * @param e
	 * @param dc
	 * @param idx index number
	 * @throws java.sql.SQLException
	 */
	public static void insert(Email e, int idx, DataConnection dc) throws SQLException {
		String query = "INSERT INTO " + TABLE + " VALUES("
			+ e.getIdper() // id de la personne
			+ ",'" + e.getEmail()
			+ "','" + (e.isArchive() ? "t" : "f")
			+ "'," + idx;
		query += ")";

		dc.executeUpdate(query);
	}

	/**
	 * Updates an email.
	 *
	 * @param e
	 * @param idx
	 * @param dc
	 * @throws java.sql.SQLException
	 */
	public static void update(Email e, int idx, DataConnection dc) throws SQLException {
		String query = "UPDATE " + TABLE + " SET "
			+ "email = '" + e.getEmail()
			+ "',archive = '" + (e.isArchive() ? "t" : "f")
			+ "'"
			+ " WHERE idper = " + e.getIdper() + " AND idx = " + idx;

		dc.executeUpdate(query);
	}

	/**
	 * Email suppression.
	 *
	 * @param idper
	 * @param idx
	 * @param dc
	 * @throws java.sql.SQLException
	 */
	public static void delete(int idper, int idx, DataConnection dc) throws SQLException {
		String query = "DELETE FROM " + TABLE	+ " WHERE idper = " + idper + " AND idx = " + idx;

		dc.executeUpdate(query);
	}

	/**
	 * Suppression of all emails for the person {@code idper}.
	 *
	 * @param idper
	 * @param dc
	 * @throws java.sql.SQLException
	 */
	public static void delete(int idper, DataConnection dc) throws SQLException {
		String query = "DELETE FROM " + TABLE + " WHERE idper = " + idper;
		dc.executeUpdate(query);
	}

	/**
     * Finds one only email for the person {@code id}.
	 *
	 * @param id
     * @param dc
	 * @return an email
	 * @throws java.sql.SQLException
	 */
	public static String findId(int id, DataConnection dc) throws SQLException {
		String mail = null;
		String query = "SELECT email FROM " + TABLE + " WHERE idper = " + id + " LIMIT 1";

		try (ResultSet rs = dc.executeQuery(query)) {
		if (rs.next()) {
			mail = rs.getString(1).trim();
		}
                }

		return mail;
	}

	/**
	 * @param idper
     * @param dc
	 * @return a list of emails
     * @since 2.0jf
	 */
	public static List<Email> find(int idper, DataConnection dc) throws SQLException {
		List<Email> v = new ArrayList<>();
		String query = "SELECT * FROM " + TABLE + " WHERE idper = " + idper + " ORDER BY idx";
		try (ResultSet rs = dc.executeQuery(query)) {
			while (rs.next()) {
				Email e = new Email();
				e.setIdper(rs.getInt(1));//idper
				e.setEmail(rs.getString(2).trim());//email
				e.setArchive(rs.getBoolean(3));//archive
				e.setIdx(rs.getInt(4)); // index
				v.add(e);
			}
		} catch (SQLException e) {
			GemLogger.logException("find Email", e);
		}
		return v;
	}
}

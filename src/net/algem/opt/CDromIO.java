/*
 * @(#)CDromIO.java 2.6.a 31/07/12
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
 * méthodes IO classe modele.CDrom
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * 
 * @version 2.6.a
 * @deprecated 
 */
public class CDromIO
	extends TableIO {

	private static final String TABLE = "cdrom";
	private static final String SEQUENCE = "idcdrom";
	
	public static void insert(CDrom c, DataConnection dc) throws SQLException
	{
		int numero = nextId(SEQUENCE, dc);

		String query = "INSERT INTO " +	TABLE + " VALUES("
			+ numero
			+ ",'" + escape(c.getArtist())
			+ "','" + escape(c.getAlbum())
			+ "','" + escape(c.getLabel())
			+ "','" + c.getRef()
			+ "','" + escape(c.getGenre())
			+ "')";

		dc.executeUpdate(query);
		c.setId(numero);
	}


	public static void update(CDrom c, DataConnection dc) throws SQLException 
	{
		String query = "UPDATE " + TABLE + " SET "
			+ "artiste='" + escape(c.getArtist())
			+ "',album='" + escape(c.getAlbum())
			+ "',label='" + escape(c.getLabel())
			+ "',ref='" + c.getRef()
			+ "',genre='" + escape(c.getGenre())
			+ "'"
			+ " WHERE id=" + c.getId();

		dc.executeUpdate(query);
	}


	public static void delete(CDrom c, DataConnection dc) throws SQLException 
	{
	}


	public static CDrom findId(int n, DataConnection dc) 
	{
		String query = "WHERE id = " + n;
		Vector<CDrom> v = find(query, dc);
		if (v.size() > 0) {
			return v.elementAt(0);
		}
		return null;
	}


	public static Vector<CDrom> find(String where, DataConnection dc) 
	{
		Vector<CDrom> v = new Vector<CDrom>();
		String query = "SELECT * FROM " +	TABLE + " " + where;
		try {
			ResultSet rs = dc.executeQuery(query);
			while (rs.next()) {
				CDrom c = new CDrom();
				c.setId(rs.getInt(1));
				c.setArtist(rs.getString(2));
				c.setAlbum(rs.getString(3));
				c.setLabel(rs.getString(4));
				c.setRef(rs.getString(5));
				c.setGenre(rs.getString(6));

				v.addElement(c);
			}
			rs.close();
		} catch (Exception e) {
			GemLogger.logException(query, e);
		}
		return v;
	}
}

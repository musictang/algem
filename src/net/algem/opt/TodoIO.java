/*
 * @(#)TodoIO.java	2.6.a 25/09/12
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
import net.algem.planning.DateFr;
import net.algem.util.DataConnection;
import net.algem.util.GemLogger;
import net.algem.util.model.TableIO;

/**
 * méthodes IO classe Todo
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 * @deprecated
 */
public class TodoIO
	extends TableIO {


	public static void insert(Todo a, DataConnection dc) throws SQLException
	{
		int numero = nextId("idafaire", dc);

		String query = "INSERT INTO afaire VALUES("
			+ numero
			+ "," + a.getIdPer()
			+ "," + a.getPriorite()
			+ "," + a.getCategorie()
			+ ",'" + escape(a.getTexte())
			+ "','" + a.getEcheance().toString()
			+ "','f'"
			+ ",null"
			+ "," + a.getNote()
			+ ")";

		dc.executeUpdate(query);
		a.setId(numero);
	}


	public static void update(Todo a, DataConnection dc) throws SQLException
	{
		String query = "UPDATE afaire set "
			+ "idper=" + a.getIdPer()
			+ "priorite=" + a.getPriorite()
			+ ",categorie=" + a.getCategorie()
			+ ",texte='" + escape(a.getTexte())
			+ "',fait='" + (a.isFait() ? "t" : "f")
			+ "',note='" + a.getNote();
		if (!a.getEcheance().bufferEquals(DateFr.NULLDATE)) {
			query += ",echeance='" + a.getEcheance().toString() + "'";
		}
		if (!a.getFaitLe().bufferEquals(DateFr.NULLDATE)) {
			query += ",faitle='" + a.getFaitLe().toString() + "'";
		}

		query += " WHERE id=" + a.getId();

		dc.executeUpdate(query);
	}


	public static List findId(int n, DataConnection dc)
	{
		String query;
		query = "WHERE id=" + n;
		return find(query, dc);
	}


	public static List find(String where, DataConnection dc)
	{
		List v = new ArrayList<>();

		String query = "SELECT * from afaire " + where;
		query += " order by priorite,categorie";
		try (ResultSet rs = dc.executeQuery(query)) {
			while (rs.next()) {
				Todo a = new Todo();
				a.setId(rs.getInt(1));
				a.setIdPer(rs.getInt(2));
				a.setPriorite(rs.getShort(3));
				a.setCategorie(rs.getShort(4));
				a.setTexte(rs.getString(5).trim());
				a.setEcheance(new DateFr(rs.getString(6)));
				a.setFait(rs.getBoolean(7));
				a.setFaitLe(new DateFr(rs.getString(8)));
				a.setNote(rs.getInt(9));

				v.add(a);
			}
		} catch (Exception e) {
			GemLogger.logException(query, e);
		}
		return v;
	}
}

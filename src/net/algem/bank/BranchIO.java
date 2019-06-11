/*
 * @(#)BranchIO.java	2.8.r 18/01/14
 * 
 * Copyright (c) 1999-2014 Musiques Tangentes. All Rights Reserved.
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
package net.algem.bank;

import java.sql.SQLException;
import java.util.Vector;
import net.algem.util.DataConnection;
import net.algem.util.model.TableIO;

/**
 * IO methods for class {@link net.algem.bank.BankBranch}.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.r
 */
public class BranchIO
	extends TableIO {

	public static final String TABLE = "guichet";
	private static final String COLUMNS = "banque,code,id,domiciliation,bic";

	public static void insert(BankBranch g, DataConnection dc) throws SQLException {
		String query = "INSERT INTO " + TABLE + " VALUES("
			+ "'" + g.getBank().getCode()
			+ "','" + g.getCode()
			+ "'," + g.getId()
			+ ",'" + escape(g.getDomiciliation())
			+ "','" + g.getBicCode()
			+ "')";

		dc.executeUpdate(query);
	}

	public static void update(BankBranch g, DataConnection dc) throws SQLException {
		String query = "UPDATE " + TABLE + " SET "
			+ "banque = '" + g.getBank().getCode()
			+ "',code = '" + g.getCode()
			+ "',domiciliation = '" + escape(g.getDomiciliation())
			+ "',bic = '" + g.getBicCode()
			+ "' WHERE id = " + g.getId();

		dc.executeUpdate(query);
	}

	public static void update(int branchId, String bicCode, DataConnection dc) throws SQLException {
		String query = "UPDATE " + TABLE + " SET bic = '" + bicCode + "' WHERE id = " + branchId;
		dc.executeUpdate(query);
	}

	public static void delete(BankBranch g, DataConnection dc) throws SQLException {
		String query = "DELETE FROM " + TABLE + " WHERE id = " + g.getId();
		dc.executeUpdate(query);
	}

	public static Vector<Rib> getRibs(int id, DataConnection dc) throws SQLException {
		String query = "WHERE guichetid = " + id;
		return RibIO.find(query, dc);
	}
}

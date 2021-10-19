/*
 * @(#)TelephoneTypeCtrl  2.6.a 06/08/2012
 *
 * Copyright (c) 2011 Musiques Tangentes All Rights Reserved.
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

import java.sql.SQLException;
import net.algem.util.module.GemDesktop;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class TelephoneTypeCtrl extends ParamTableCtrl {

	private static String TABLE = "typetel";
	private static String SEQUENCE = "typetel_id_seq";
	private static String COLUMN_KEY = "id";
	private static String COLUMN_NAME = "type";

	public TelephoneTypeCtrl(GemDesktop _desktop) {
		super(_desktop, "Types téléphone", false);
	}

	@Override
	public void load() {
		load(ParamTableIO.find(TABLE, COLUMN_KEY, dc));
	}

	@Override
	public void modification(Param current, Param p) throws SQLException {
		ParamTableIO.update(TABLE, COLUMN_KEY, COLUMN_NAME, p, dc);
	}

	@Override
	public void insertion(Param p) throws SQLException {
		ParamTableIO.insert(TABLE, SEQUENCE, p, dc);
	}

	@Override
	public void suppression(Param p) throws SQLException {
		ParamTableIO.delete(TABLE, COLUMN_KEY, p, dc);
	}
}

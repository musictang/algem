/*
 * @(#)CategoryWebSiteCtrl.java	2.6.a 03/08/12
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
package net.algem.config;

import java.sql.SQLException;
import net.algem.util.DataCache;
import net.algem.util.module.GemDesktop;

/**
 * comment
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class CategoryWebSiteCtrl
	extends ParamTableCtrl {

	private static final String TABLE = "categorie_siteweb";
	private static final String SEQUENCE = "categorie_siteweb_id_seq";
	private static final String COLUMN_KEY = "id";
	private static final String COLUMN_NAME = "libelle";
	private DataCache dataCache;

	public CategoryWebSiteCtrl(GemDesktop _desktop) {
		super(_desktop, "Catégorie de sites web", false);
		dataCache = _desktop.getDataCache();
	}

	@Override
	public void load() {
		load(ParamTableIO.find(TABLE, COLUMN_NAME, dc).elements());
	}

	@Override
	public void modification(Param _current, Param _p) throws SQLException {
		ParamTableIO.update(TABLE, COLUMN_KEY, COLUMN_NAME, _p, dc);
	}

	@Override
	public void insertion(Param _p) throws SQLException {
		ParamTableIO.insert(TABLE, SEQUENCE, _p, dc);
		dataCache.getWebSiteCat().addElement(_p);
	}

	@Override
	public void suppression(Param _p) throws SQLException {
		ParamTableIO.delete(TABLE, COLUMN_KEY, _p, dc);
		dataCache.getWebSiteCat().remove(_p);
	}
}

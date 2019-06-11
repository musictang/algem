/*
 * @(#)CategoryOccupCtrl.java	2.6.a 20/09/12
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
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.module.GemDesktop;

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class CategoryOccupCtrl
	extends ParamTableCtrl {

	private static final String TABLE = "categorie_prof";
	private static final String SEQUENCE = "idcategorieprof";
	private static final String COLUMN_KEY = "id";
	private static final String COLUMN_NAME = "nom";
	
	private DataCache dataCache;

	public CategoryOccupCtrl(GemDesktop _desktop) {
		super(_desktop, BundleUtil.getLabel("Occupational.category.label"), false);
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
		CategoryOccup cp = new CategoryOccup(Integer.parseInt(_p.getKey()), _p.getValue());
		dataCache.getOccupationalCat().addElement(cp);
	}

	@Override
	public void suppression(Param _p) throws SQLException {
		ParamTableIO.delete(TABLE, COLUMN_KEY, _p, dc);
		CategoryOccup cp = new CategoryOccup(Integer.parseInt(_p.getKey()), _p.getValue());
		dataCache.getOccupationalCat().remove(cp);
	}
}

/*
 * @(#)CategoryVacancyCtrl.java	2.6.a 03/08/12
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
public class CategoryVacancyCtrl
	extends ParamTableCtrl {
    
	private static final String SEQUENCE = "categorie_vacance_id_seq";	
	private static final String COLUMN_NAME = "libelle";
	private DataCache dataCache;

	public CategoryVacancyCtrl(GemDesktop _desktop) {
		super(_desktop, "Catégorie de vacances", false);
		dataCache = _desktop.getDataCache();
	}

	@Override
	public void load() {
		load(ParamTableIO.find(Category.VACANCY.getTable(), COLUMN_NAME, dc).elements());
	}

	@Override
	public void modification(Param _current, Param _p) throws SQLException {
		ParamTableIO.update(Category.VACANCY.getTable(), Category.VACANCY.getCol(), COLUMN_NAME, _p, dc);
	}

	@Override
	public void insertion(Param _p) throws SQLException {
		ParamTableIO.insert(Category.VACANCY.getTable(), SEQUENCE, _p, dc);
		dataCache.getVacancyCat().addElement(_p);
	}

	@Override
	public void suppression(Param _p) throws SQLException {
		ParamTableIO.delete(Category.VACANCY.getTable(), Category.VACANCY.getCol(), _p, dc);
		dataCache.getVacancyCat().remove(_p);
	}
}

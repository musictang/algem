/*
 * @(#)WorkshopSearchDeleteCtrl.java	2.7.a 26/11/12
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
package net.algem.course;

import java.awt.CardLayout;
import java.sql.SQLException;
import java.util.Vector;
import net.algem.util.DataCache;
import net.algem.util.MessageUtil;
import net.algem.util.model.Model;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.SearchCtrl;

/**
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.a
 * @deprecated
 */
public class WorkshopSearchDeleteCtrl
	extends SearchCtrl {

	private GemDesktop desktop;

	public WorkshopSearchDeleteCtrl(GemDesktop _desktop) {
		super(_desktop.getDataCache().getDataConnection(), "Suppression d'un atelier");
		desktop = _desktop;
	}

	@Override
	public void init() {
		searchView = new WorkshopSearchView();
		searchView.addActionListener(this);

		list = new WorkshopListCtrl();
		list.addMouseListener(this);
		list.addActionListener(this);

		mask = new WorkshopDeleteCtrl(desktop);
		mask.addActionListener(this);

		wCard.add("cherche", searchView);
		wCard.add("masque", mask);
		wCard.add("liste", list);

		((CardLayout) wCard.getLayout()).show(wCard, "cherche");
	}

	@Override
	public void search() {
		String query = "WHERE c.code = '"+Course.ATP_CODE+"'";

		String nom;

		int id = getId();
		if (id > 0) {
			query += " AND c.id=" + id;
		} else if ((nom = searchView.getField(1)) != null) {
			query += " AND c.titre ~ '"	+ nom + "'";
		}

		query += " ORDER BY c.titre";
		Vector<Course> v = null;
		try {
			v = ((CourseIO) DataCache.getDao(Model.Course)).find(query);
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		if (v.isEmpty()) {
			setStatus(MessageUtil.getMessage("search.empty.list.status"));
		} else if (v.size() == 1) {
			((CardLayout) wCard.getLayout()).show(wCard, "masque");
			mask.loadCard(v.elementAt(0));
		} else {
			((CardLayout) wCard.getLayout()).show(wCard, "liste");
			list.loadResult(v);
		}
	}
}

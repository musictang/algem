/*
 * @(#)CDromListView.java	2.8.w 08/07/14
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
package net.algem.opt;

import java.awt.BorderLayout;
import java.util.List;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumnModel;
import net.algem.util.DataCache;
import net.algem.util.ui.GemPanel;

/**
 * liste titre d'un cd.
 * 
 * @author Eric
 * @version 2.8.w
 * @deprecated
 */
public class CDromListView
	extends GemPanel {

	DataCache cache;
	CdTitleTableModel titres;
	JTable table;

	public CDromListView(DataCache dc) {
		cache = dc;

		titres = new CdTitleTableModel();
		table = new JTable(titres);

		TableColumnModel cm = table.getColumnModel();
		cm.getColumn(0).setPreferredWidth(40);
		cm.getColumn(1).setPreferredWidth(150);
		cm.getColumn(2).setPreferredWidth(150);

		JScrollPane pm = new JScrollPane(table);

		setLayout(new BorderLayout());
		add("Center", pm);
	}

	public void clear() {
		titres.clear();
	}

	public void load(int id) {
		List v = CdTitleIO.findId(id, DataCache.getDataConnection());
		for (int i = 0; i < v.size(); i++) {
			CdTitle titre = (CdTitle) v.get(i);
			titres.addItem(titre);
		}
	}
}

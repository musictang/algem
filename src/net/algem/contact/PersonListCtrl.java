/*
 * @(#)PersonListCtrl.java	2.6.a 18/09/12
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

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.BevelBorder;
import javax.swing.table.TableColumnModel;
import net.algem.util.ui.ListCtrl;


/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class PersonListCtrl
	extends ListCtrl
{
	public PersonListCtrl()
	{
		tableModel = new PersonTableModel();

		jtable = new JTable(tableModel);
        jtable.setAutoCreateRowSorter(true);

		TableColumnModel cm = jtable.getColumnModel();
		cm.getColumn(0).setPreferredWidth(60);
		cm.getColumn(1).setPreferredWidth(40);
		cm.getColumn(2).setPreferredWidth(250);
		cm.getColumn(3).setPreferredWidth(150);

		JScrollPane p = new JScrollPane(jtable);
		p.setBorder(new BevelBorder(BevelBorder.LOWERED));

		add("Center",p);
	}
}

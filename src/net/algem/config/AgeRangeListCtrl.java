/*
 * @(#)AgeRangeListCtrl.java 2.6.a 24/09/12
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
 */

package net.algem.config;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.BevelBorder;
import javax.swing.table.TableColumnModel;
import net.algem.util.ui.ListCtrl;

/**
 *
 * @author <a href="mailto:nicolasnouet@gmail.com">Nicolas Nouet</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 * @since 2.3.a
 */

public class AgeRangeListCtrl
	extends ListCtrl
{

  public AgeRangeListCtrl() {
    tableModel = new AgeRangeTableModel();

    jtable = new JTable(tableModel);
    jtable.setAutoCreateRowSorter(true);

    TableColumnModel cm = jtable.getColumnModel();
    cm.getColumn(0).setPreferredWidth(2);
    cm.getColumn(1).setPreferredWidth(2);
    cm.getColumn(2).setPreferredWidth(10);
    cm.getColumn(3).setPreferredWidth(10);
    cm.getColumn(4).setPreferredWidth(200);

    JScrollPane p = new JScrollPane(jtable);
    p.setBorder(new BevelBorder(BevelBorder.LOWERED));

    add("Center", p);
  }
}


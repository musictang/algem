/*
 * @(#)RentListCtrl.java	2.17.0t 29/08/19
 * 
 * Copyright (c) 1999-2019 Musiques Tangentes. All Rights Reserved.
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
package net.algem.rental;

import java.awt.BorderLayout;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.BevelBorder;
import javax.swing.table.TableColumnModel;
import net.algem.util.ui.ListCtrl;

/**
 * comment.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @version 2.17.0t
 */
public class RentListCtrl
        extends ListCtrl
{

  public RentListCtrl() {
    tableModel = new RentableObjectTableModel();

    table = new JTable(tableModel);
    table.setAutoCreateRowSorter(true);

    TableColumnModel cm = table.getColumnModel();
    cm.getColumn(0).setPreferredWidth(25);
    cm.getColumn(1).setPreferredWidth(100);
    cm.getColumn(2).setPreferredWidth(100);
    cm.getColumn(3).setPreferredWidth(100);

    JScrollPane p = new JScrollPane(table);
    p.setBorder(new BevelBorder(BevelBorder.LOWERED));

    add(p, BorderLayout.CENTER);
  }
}

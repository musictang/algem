/*
 * @(#)RoomRateListCtrl.java	2.9.4.13 07/10/15
 *
 * Copyright (c) 1999-2015 Musiques Tangentes. All Rights Reserved.
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
package net.algem.room;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.BevelBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;
import net.algem.util.ui.ListCtrl;


/**
 * comment
 * 
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.13
 * @since 2.1a
 */
public class RoomRateListCtrl
        extends ListCtrl
{

  public RoomRateListCtrl()
  {
    tableModel = new RoomRateTableModel();

    table = new JTable(tableModel);
    table.setAutoCreateRowSorter(true);
    DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
    rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
    TableColumnModel cm = table.getColumnModel();
    for (int i= 3 ; i < cm.getColumnCount() ; i++) {
      cm.getColumn(i).setCellRenderer(rightRenderer);
    }

    cm.getColumn(0).setPreferredWidth(25);
    cm.getColumn(1).setPreferredWidth(250);

    JScrollPane p = new JScrollPane(table);
    p.setBorder(new BevelBorder(BevelBorder.LOWERED));

    add("Center", p);
  }
}

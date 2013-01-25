/*
 * @(#)ConflictListView.java	2.6.a 19/09/12
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
package net.algem.planning;

import java.awt.BorderLayout;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumnModel;
import net.algem.util.ui.GemLabel;
import net.algem.util.ui.GemPanel;


/**
 * List of conflicts.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class ConflictListView
        extends GemPanel
{

  private ConflictTableModel tableModel;
  private JTable table;
  private GemLabel status;

  public ConflictListView() {

    tableModel = new ConflictTableModel();
    table = new JTable(tableModel);
    table.setAutoCreateRowSorter(true);

    TableColumnModel cm = table.getColumnModel();
    cm.getColumn(0).setPreferredWidth(70);
    cm.getColumn(1).setPreferredWidth(30);
    cm.getColumn(2).setPreferredWidth(30);
    cm.getColumn(3).setPreferredWidth(30);
    cm.getColumn(4).setPreferredWidth(400);

    JScrollPane pm = new JScrollPane(table);

    status = new GemLabel();
    setLayout(new BorderLayout());
    add(pm, BorderLayout.CENTER);
    add(status, BorderLayout.SOUTH);
  }

  public void clear() {
    tableModel.clear();
    status.setText("");
  }

  public void addConflict(ScheduleTestConflict p) {
    tableModel.addItem(p);
    status.setText("Plannings : " + tableModel.getRowCount());
  }
}

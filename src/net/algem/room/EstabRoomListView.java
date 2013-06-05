/*
 * @(#)EstabRoomListView.java	2.8.e 20/05/13
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
package net.algem.room;

import java.awt.BorderLayout;
import java.util.Vector;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.BevelBorder;
import javax.swing.table.TableColumnModel;
import net.algem.util.DataCache;
import net.algem.util.model.Model;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.JTableModel;

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.e
 * @since 1.0a 07/07/1999
 */
public class EstabRoomListView
        extends GemPanel
{

  private JTableModel tableModel;
  private JTable table;

  public EstabRoomListView() {
    tableModel = new RoomTableModel();
    table = new JTable(tableModel);
    table.setAutoCreateRowSorter(true);

    TableColumnModel cm = table.getColumnModel();
    cm.getColumn(0).setPreferredWidth(40);
    cm.getColumn(1).setPreferredWidth(150);
    cm.getColumn(2).setPreferredWidth(100);
    cm.getColumn(3).setPreferredWidth(60);

    JScrollPane scroll = new JScrollPane(table);
    scroll.setBorder(new BevelBorder(BevelBorder.LOWERED));
    setLayout(new BorderLayout());
    add(scroll, BorderLayout.CENTER);
  }

  public void clear() {
    tableModel.clear();
  }

  public void load(int id) {
    Vector<Room> v = ((RoomIO) DataCache.getDao(Model.Room)).find("WHERE etablissement = " + id);
    for (int i = 0; i < v.size(); i++) {
      tableModel.addItem(v.elementAt(i));
    }
  }
}

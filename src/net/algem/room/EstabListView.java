/*
 * @(#)EstabListView.java	2.7.a 26/11/12
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
 * @version 2.7.a
 * @since 1.0a 07/07/1999
 */
public class EstabListView
        extends GemPanel
{

  private JTableModel table;
  private JTable jtable;

  public EstabListView() {
    table = new RoomTableModel();
    jtable = new JTable(table);
    jtable.setAutoCreateRowSorter(true);

    TableColumnModel cm = jtable.getColumnModel();
    cm.getColumn(0).setPreferredWidth(40);
    cm.getColumn(1).setPreferredWidth(150);
    cm.getColumn(2).setPreferredWidth(100);
    cm.getColumn(3).setPreferredWidth(60);

    JScrollPane scroll = new JScrollPane(jtable);
    scroll.setBorder(new BevelBorder(BevelBorder.LOWERED));
    setLayout(new BorderLayout());
    add(scroll, BorderLayout.CENTER);
  }

  public void clear() {
    table.clear();
  }

  public void load(int id) {
    Vector<Room> v = ((RoomIO) DataCache.getDao(Model.Room)).find("WHERE etablissement = " + id);
    for (int i = 0; i < v.size(); i++) {
      table.addItem(v.elementAt(i));
    }
  }
}

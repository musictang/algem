/*
 * @(#)EstabListCtrl.java	2.11.3 16/11/16
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

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.BevelBorder;
import javax.swing.table.TableColumnModel;
import net.algem.util.event.GemEvent;
import net.algem.util.event.GemEventListener;
import net.algem.util.ui.ListCtrl;

/**
 * Control list of establishments.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.11.3
 */
public class EstabListCtrl
  extends ListCtrl
  implements GemEventListener {

  public EstabListCtrl() {
    tableModel = new EstabTableModel();

    table = new JTable(tableModel);
    table.setAutoCreateRowSorter(true);

    TableColumnModel cm = table.getColumnModel();
    cm.getColumn(0).setPreferredWidth(40);
    cm.getColumn(1).setPreferredWidth(300);
    cm.getColumn(2).setPreferredWidth(40);

    JScrollPane p = new JScrollPane(table);
    p.setBorder(new BevelBorder(BevelBorder.LOWERED));

    add("Center", p);
  }

  @Override
  public void postEvent(GemEvent _evt) {
    if (_evt.getType() == GemEvent.ESTABLISHMENT) {
      Establishment e = (Establishment) _evt.getObject();
      if (e != null && e.getPerson() != null) {
        if (_evt.getOperation() == GemEvent.MODIFICATION) {
          updateRow(e);
        } else if (_evt.getOperation() == GemEvent.SUPPRESSION) {
          deleteRow(e);
        }
      }
    }
  }

  int getSelectedColumn() {
    return table.getSelectedColumn();
  }

  Establishment getActive() {
    if (2 == table.getSelectedColumn()) {
      return ((EstabTableModel) table.getModel()).getItem(table.convertRowIndexToModel(table.getSelectedRow()));
    }
    return null;
  }

}

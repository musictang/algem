/*
 * @(#)RightsTableModel.java	2.13.2 03/05/2017
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
package net.algem.security;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.13.2
 * @since 2.6.a 01/08/2012
 */
public class RightsTableModel
        extends AbstractTableModel
{

  private String[] head = {"Table", "Lecture", "Insertion", "Modification", "Suppression"};
  private List<SQLRights> tuples = new ArrayList<SQLRights>();
  private int userId;
  private UserService service;

  public RightsTableModel(UserService service) {
    this.service = service;
  }

  public void load(int id) {
    userId = id;
    tuples = service.getTableRights(userId);
    fireTableChanged(null);
  }

  // TableModel Interface
  @Override
  public String getColumnName(int column) {
    return head[column];
  }

  @Override
  public Class getColumnClass(int column) {
    switch (column) {
      case 0:
        return String.class;
      case 1:
      case 2:
      case 3:
      case 4:
        return Boolean.class;
      default:
        return Object.class;
    }
  }

  @Override
  public boolean isCellEditable(int row, int column) {
    return column > 0;
  }

  @Override
  public int getColumnCount() {
    return head.length;
  }

  @Override
  public int getRowCount() {
    return tuples.size();
  }

  @Override
  public Object getValueAt(int row, int col) {
    SQLRights tr = tuples.get(row);
    switch(col) {
      case 0:
        return tr.getName();
      case 1:
        return tr.isAuthRead();
      case 2:
        return tr.isAuthInsert();
      case 3:
        return tr.isAuthUpdate();
      case 4:
        return tr.isAuthDelete();
    }
    return null;
  }

  @Override
  public void setValueAt(Object value, int row, int column) {
    if (column < 1) {
      return;
    }
    String col = getColumnName(column).toLowerCase();
    SQLRights tr = tuples.get(row);
    String table = tr.getName();
    boolean auth = (boolean) value;
    switch (column) {
      case 1:
        tr.setAuthRead(auth);
        break;
      case 2:
        tr.setAuthInsert(auth);
        break;
      case 3:
        tr.setAuthUpdate(auth);
        break;
      case 4:
        tr.setAuthDelete(auth);
        break;
    }
    service.updateTableRights(table, col, auth, userId);
    fireTableRowsUpdated(row, row);
  }
}

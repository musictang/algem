/*
 * @(#)RightsTableModel.java	2.6.a 01/08/2012
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

import java.util.Vector;
import javax.swing.table.AbstractTableModel;

/**
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 * @since 2.6.a 01/08/2012
 */
public class RightsTableModel
        extends AbstractTableModel
{

  private String[] head = {"table", "lecture", "insertion", "modification", "suppression"};
  private Vector tuples = new Vector();
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
  public Object getValueAt(int ligne, int colonne) {
    Vector v = (Vector) tuples.elementAt(ligne);
    return v.elementAt(colonne);
  }

  @Override
  public void setValueAt(Object value, int line, int column) {
    if (column < 1) {
      return;
    }
    String col = getColumnName(column);
    String table = getValueAt(line, 1).toString();
    service.updateTableRights(table, col, value, userId);
    Vector dataRow = (Vector) tuples.elementAt(line);
    dataRow.setElementAt(value, column);
  }
}

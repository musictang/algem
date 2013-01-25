/*
 * @(#)JTableModel.java	2.6.a 20/09/12
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
package net.algem.util.ui;

import java.util.Vector;
import javax.swing.table.AbstractTableModel;

/**
 * 
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 * @since 1.0a 07/07/1999
 */
public abstract class JTableModel
        extends AbstractTableModel
{

  protected String[] header;
  protected Vector tuples = new Vector();

  public JTableModel() {

  }

  public void clear() {
    tuples = new Vector();
    fireTableDataChanged();
  }

  public void deleteItem(Object o) {
    int idx = tuples.indexOf(o);
    deleteItem(idx);
  }

  public void deleteItem(int idx) {
    tuples.remove(idx);
    fireTableRowsDeleted(idx, idx);
  }

  public void modItem(int idx, Object o) {
    tuples.setElementAt(o, idx);
    fireTableRowsUpdated(idx, idx);
  }

  public void addItem(Object o) {
    int idx = tuples.size();
    tuples.addElement(o);
    fireTableRowsInserted(idx, idx);
  }

  public Object getItem(int idx) {
    return tuples.elementAt(idx);
  }

  public abstract int getIdFromIndex(int i);

  // TableModel Interface
  @Override
  public String getColumnName(int column) {
    return header[column];
  }

  public Vector getData() {
    return tuples;
  }

  @Override
  public int getColumnCount() {
    return header.length;
  }

  @Override
  public int getRowCount() {
    return tuples.size();
  }

  @Override
  public abstract Object getValueAt(int ligne, int colonne);

  @Override
  public abstract void setValueAt(Object value, int ligne, int column);
}

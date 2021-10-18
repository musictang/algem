/*
 * @(#)ListCtrl.java	2.11.3 17/11/16
 *
 * Copyright (c) 1999-2016 Musiques Tangentes. All Rights Reserved.
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

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.util.Collection;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumnModel;
import net.algem.util.GemCommand;

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.11.3
 * @since 1.0a 07/07/1999
 */
public abstract class ListCtrl
        extends GemPanel
{

  protected JTableModel tableModel;
  protected JTable table;
  protected GemButton back;

  public ListCtrl() {
    setLayout(new BorderLayout());
    addSearchComponent();
  }

  /**
   * Creates a list controller with optional search component.
   *
   * @param searchFlag
   */
  public ListCtrl(boolean searchFlag) {
    setLayout(new BorderLayout());
    if (searchFlag) {
      addSearchComponent();
    }
  }

  @Override
  public synchronized void addMouseListener(MouseListener l) {
    table.addMouseListener(l);
  }

  public void addActionListener(ActionListener l) {
    back.addActionListener(l);
  }

  public <E> void addBlock(List<E> block) {
    for (E element : block) {
      addRow(element);
    }
  }

  public int getSelectedIndex() {
    int idx = table.getSelectedRow();
    return  (idx == -1) ? idx : table.convertRowIndexToModel(idx);
  }

  public int getIdFromIndex(int i) {
    return tableModel.getIdFromIndex(table.convertRowIndexToModel(i));
  }

  public int getSelectedID() {
    return tableModel.getIdFromIndex(getSelectedIndex());
  }

  public <E> void loadResult(List<E> list) {
    clear();
    addBlock(list);
  }

  public int nbLines() {
    return tableModel.getRowCount();
  }

  public void clear() {
    tableModel.clear();
  }

  public void addRow(Object item) {
    tableModel.addItem(item);
  }

  public void updateRow(Object item) {
    int i = getSelectedIndex();
    tableModel.modItem(i, item);
  }

  public void deleteRow(Object item) {
    if (nbLines() > 0) {
      tableModel.deleteItem(item);//XXX
    }
  }

  /**
   * Model content data.
   *
   * @param <T>
   * @return object collection of type T
   */
  public <T> Collection<T> getData() {
    return tableModel.getData();
  }

  /**
   * Sets the column widths of the table.
   * Widths are calculated from index 0.
   *
   * @param cols a list of integers
   * @since 2.3.a 14/02/12
   */
  protected void setColumns(int... cols) {
    TableColumnModel cm = table.getColumnModel();
    for (int i = 0; i < cols.length; i++) {
      cm.getColumn(i).setPreferredWidth(cols[i]);
    }
  }

  protected void stopCellEditing() {
    if (table != null) {
      TableCellEditor tce = table.getCellEditor();
      if (tce != null) {
        tce.stopCellEditing();
      }
    }
  }

  /**
   * Adds a "new search" button.
   */
  private void addSearchComponent() {
    back = new GemButton(GemCommand.NEW_SEARCH_CMD);
    add(back, BorderLayout.SOUTH);
  }

  @Override
  public String toString() {
    return getClass().getName();
  }

}

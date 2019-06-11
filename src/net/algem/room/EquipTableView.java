/*
 * @(#)EquipTableView.java	2.9.4.2 10/04/15
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

import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumnModel;
import net.algem.util.BundleUtil;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.GridBagHelper;

/**
 * List of equipment of the room.
 * Each equipment is identified by its quantity and its name.
 * Cells are editable.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.2
 *
 * @since 1.0a 07/07/1999
 */
public class EquipTableView
        extends GemPanel
{

  private EquipTableModel model;
  private JTable table;
  private GemButton btAdd;
  private GemButton btDelete;

  public EquipTableView() {

    model = new EquipTableModel();
    table = new JTable(model);
    table.setRowSelectionAllowed(true);
    table.setColumnSelectionAllowed(false);
    table.setAutoCreateRowSorter(true);

    TableColumnModel cm = table.getColumnModel();
    cm.getColumn(0).setPreferredWidth(30);
    cm.getColumn(1).setPreferredWidth(700);
    cm.getColumn(2).setPreferredWidth(100);

    JScrollPane pm = new JScrollPane(table);

    btAdd = new GemButton(BundleUtil.getLabel("Action.add.label"));
    btDelete = new GemButton(BundleUtil.getLabel("Action.suppress.label"));
    GemPanel buttons = new GemPanel();
    buttons.setLayout(new GridLayout(1, 3));
    buttons.add(btAdd);
    buttons.add(btDelete);

    this.setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(this);

    gb.add(pm, 0, 0, 3, 1, GridBagHelper.BOTH, 1.0, 1.0);
    gb.add(buttons, 0, 1, 3, 1, GridBagHelper.HORIZONTAL, 1.0, 0.0);
  }

  public void addActionListener(ActionListener l) {
    btAdd.addActionListener(l);
    btDelete.addActionListener(l);
  }

  public Vector<Equipment> getData() {
    TableCellEditor cellEditor = table.getCellEditor();
    if (cellEditor != null) {
      table.getCellEditor().stopCellEditing();
    }
    return model.getData();
  }

  public void modRow(Object o) {
    model.modItem(table.getSelectedRow(), (Equipment) o);
  }

  public void addRow(Object o) {
    model.addItem((Equipment) o);
  }

  public void deleteCurrent() {
    int n = table.convertRowIndexToModel(table.getSelectedRow());
    if (n >= 0) {
      model.deleteItem(n);
    }
  }

  public Object getSelectedRow() {
    int n = table.convertRowIndexToModel(table.getSelectedRow());
    return (n >= 0) ? model.getItem(n) : null;
  }

  public void edit(int row, int col) {
    table.editCellAt(row, col);
    table.changeSelection(row, col, false, false);
    table.requestFocus();
  }

  public void selectNewCell() {
    edit(table.getRowCount()-1, 1);
  }

  public void clear() {
    model.clear();
  }

}

/*
 * @(#)ParamTableView.java	2.14.0 08/06/17
 *
 * Copyright (c) 1999-2017 Musiques Tangentes. All Rights Reserved.
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
package net.algem.config;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;
import net.algem.util.GemCommand;
import net.algem.util.model.GemModel;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.JTableModel;

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.14.0
 */
public class ParamTableView
        extends GemPanel
{

  protected JTable table;
  protected JTableModel tableModel;
  private GemButton btAdd;
  private GemButton btClose;

  public <T extends GemModel> ParamTableView(String title, JTableModel<T> model) {
    this(title, model, 0);
  }

  public <T extends GemModel> ParamTableView(String title, JTableModel<T> model, int sortColumn) {

    this.tableModel = model;
    this.table = new JTable(tableModel);
    //table.setAutoCreateRowSorter(true);
		/* pour le tri automatique */
    TableRowSorter<JTableModel> sorter = new TableRowSorter<JTableModel>(tableModel);
    table.setRowSorter(sorter);
    sorter.setSortsOnUpdates(true); // tri automatique après modification
    sorter.setComparator(sortColumn, new ParamKeyComparator());

    JScrollPane pm = new JScrollPane(table);

    btAdd = new GemButton(GemCommand.ADD_NEWVAL_CMD);
    btAdd.setActionCommand(GemCommand.ADD_CMD);

    btClose = new GemButton(GemCommand.CLOSE_CMD);

    GemPanel buttons = new GemPanel();
    buttons.setLayout(new GridLayout(1, 1));
    buttons.add(btAdd);
    buttons.add(btClose);

    setLayout(new BorderLayout());
    add(pm, BorderLayout.CENTER);
    add(buttons, BorderLayout.SOUTH);
  }

  public void setColumnModel() {
    TableColumnModel cm = table.getColumnModel();
    cm.getColumn(0).setPreferredWidth(80);
    cm.getColumn(1).setPreferredWidth(300);
    if (cm.getColumnCount() > 2) {
      cm.getColumn(2).setPreferredWidth(30);
    }
  }

  @Override
  public synchronized void addMouseListener(MouseListener l) {
    table.addMouseListener(l);
  }

  public void addActionListener(ActionListener l) {
    btAdd.addActionListener(l);
    btClose.addActionListener(l);
  }

  public void modRow(Param p) {
    tableModel.modItem(table.convertRowIndexToModel(table.getSelectedRow()), p);
  }

  public void addRow(Param p) {
    tableModel.addItem(p);
  }

  public void deleteCurrent() {
    tableModel.deleteItem(table.convertRowIndexToModel(table.getSelectedRow()));
  }

  public Param getItem(int n) {
    return (Param) tableModel.getItem(table.convertRowIndexToModel(n));
  }

  public int getSelectedRow() {
    return table.getSelectedRow();
  }
}

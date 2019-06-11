/*
 * @(#)TableView.java	2.6.a 25/09/12
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

import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumnModel;
import net.algem.util.GemCommand;
import net.algem.util.model.GenericTable;

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class TableView
        extends GemPanel
{

  private GenericTableModel modele;
  private JTable table;
  private int id;
  private GemField label;
  private GemButton btAdd;
  private GemButton btModify;
  private GemButton btDelete;

  public TableView(String titre) {
    label = new GemField(30);
    label.setBackground(new Color(255, 246, 143));

    modele = new GenericTableModel();
    table = new JTable(modele);
    table.setAutoCreateRowSorter(true);
    table.addMouseListener(new MouseAdapter()
    {

      public void mouseClicked(MouseEvent e) {
        int n = table.getSelectedRow();
        GenericTable g = (GenericTable) modele.getItem(table.convertRowIndexToModel(n));
        id = g.getId();
        label.setText(g.getLabel());
      }
    });


    TableColumnModel cm = table.getColumnModel();
    cm.getColumn(0).setPreferredWidth(80);
    cm.getColumn(1).setPreferredWidth(300);

    JScrollPane pm = new JScrollPane(table);

    btAdd = new GemButton(GemCommand.ADD_CMD);
    btModify = new GemButton(GemCommand.MODIFY_CMD);
    btDelete = new GemButton(GemCommand.DELETE_CMD);
    GemPanel buttons = new GemPanel();
    buttons.setLayout(new GridLayout(1, 3));
    buttons.add(btAdd);
    buttons.add(btModify);
    buttons.add(btDelete);

    this.setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(this);

    gb.add(pm, 0, 0, 1, 1, GridBagHelper.BOTH, 1.0, 1.0);
    gb.add(label, 0, 1, 1, 1, GridBagHelper.HORIZONTAL, 1.0, 0.0);
    gb.add(buttons, 0, 2, 1, 1, GridBagHelper.HORIZONTAL, 1.0, 0.0);
  }

  public void addActionListener(ActionListener l) {
    btAdd.addActionListener(l);
    btModify.addActionListener(l);
    btDelete.addActionListener(l);
  }

  public int getId() {
    return id;
  }

  public void setId(int i) {
    id = i;
  }

  public String getLabel() {
    return label.getText();
  }

  public void setLabel(String l) {
    label.setText(l);
  }

  public void modRow(Object o) {
    modele.modItem(table.convertRowIndexToModel(table.getSelectedRow()), o);
  }

  public void addRow(Object o) {
    modele.addItem(o);
  }

  public void deleteCurrent() {
    modele.deleteItem(table.convertRowIndexToModel(table.getSelectedRow()));
  }

  public Object getSelectedRow() {
    int n = table.getSelectedRow();
    return modele.getItem(table.convertRowIndexToModel(n));
  }
}

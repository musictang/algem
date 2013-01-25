/*
 * @(#)GenericTableCtrl.java	2.6.a 25/09/12
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

import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import net.algem.util.GemCommand;

/**
 * Generic table controller.
 * Selected line may be deleted or modified.
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 * @since 2.2.a 
 */
public abstract class GenericTableCtrl
        extends GemPanel
        implements ActionListener
{

  protected JTableModel tableModel;
  protected JTable table;
  protected GemButton btAdd;
  protected GemButton btModify;
  protected GemButton btDelete;
  protected GemButton btClose;
  protected GemPanel p1;

  public GenericTableCtrl(JTableModel model) {
    tableModel = model;
    table = new JTable(model);

    JScrollPane pm = new JScrollPane(table);
    table.setAutoCreateRowSorter(true);
    table.addMouseListener(new MouseAdapter()
    {

      public void mouseClicked(MouseEvent e) {
        int n = table.convertRowIndexToModel(table.getSelectedRow());
        GenericTableCtrl.this.select(n);
      }
    });

    btAdd = new GemButton(GemCommand.ADD_CMD);
    btModify = new GemButton(GemCommand.MODIFY_CMD);
    btDelete = new GemButton(GemCommand.DELETE_CMD);
    btClose = new GemButton(GemCommand.CLOSE_CMD);

    GemPanel buttons = new GemPanel(new GridLayout(1, 3));
    buttons.add(btDelete);
    buttons.add(btModify);
    buttons.add(btAdd);
    buttons.add(btClose);

    btAdd.addActionListener(this);
    btModify.addActionListener(this);
    btDelete.addActionListener(this);
    btClose.addActionListener(this);

    p1 = new GemPanel();

    setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(this);

    gb.add(pm, 0, 0, 1, 1, GridBagHelper.BOTH, 1.0, 1.0);
    gb.add(p1, 0, 1, 1, 1, GridBagHelper.HORIZONTAL, 1.0, 0.0);
    gb.add(buttons, 0, 2, 1, 1, GridBagHelper.HORIZONTAL, 1.0, 0.0);

  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    String cmd = evt.getActionCommand();
    if (GemCommand.ADD_CMD.equals(cmd)) {
      insertion();
    } else if (GemCommand.CLOSE_CMD.equals(cmd)) {
      close();
    } else {
      int n = table.convertRowIndexToModel(table.getSelectedRow());
      if (n < 0) {
        return;
      }// N <=0
      if (GemCommand.MODIFY_CMD.equals(cmd)) {
        modification(n);
      } else if (GemCommand.DELETE_CMD.equals(cmd)) {
        suppression(n);
      }
    }
  }

  protected abstract void addControl();

  protected abstract void load();

  protected abstract void select(int n);

  protected abstract void insertion();

  protected abstract void modification(int n);

  protected abstract void suppression(int n);

  protected abstract void clear();

  protected abstract void close();
}

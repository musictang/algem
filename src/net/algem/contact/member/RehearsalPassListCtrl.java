/*
 * @(#)RehearsalPassListCtrl.java 2.9.2 12/01/15
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
package net.algem.contact.member;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.BevelBorder;
import javax.swing.table.TableColumnModel;
import net.algem.util.DataCache;
import net.algem.util.GemCommand;
import net.algem.util.GemLogger;
import net.algem.util.MessageUtil;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.ListCtrl;
import net.algem.util.ui.MessagePopup;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.2
 */
public class RehearsalPassListCtrl
        extends ListCtrl
        implements ActionListener
{

  private GemButton btDelete;
  private GemButton btAdd;
  private GemButton btModify;
  private GemButton btClose;
  private GemDesktop desktop;
  private RehearsalPassCtrl ctrl;

  public RehearsalPassListCtrl(GemDesktop desktop, boolean searchFlag) {
    super(searchFlag);
    this.desktop = desktop;
    tableModel = new RehearsalPassTableModel();

    table = new JTable(tableModel);
    table.setAutoCreateRowSorter(true);
    table.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
          try {
            update();
          } catch (SQLException ex) {
            GemLogger.log(ex.getMessage());
          }
        }
      }
    });

    TableColumnModel cm = table.getColumnModel();
    cm.getColumn(0).setPreferredWidth(25);
    cm.getColumn(1).setPreferredWidth(250);
    JScrollPane p = new JScrollPane(table);
    p.setBorder(new BevelBorder(BevelBorder.LOWERED));

    add(p, BorderLayout.CENTER);

    GemPanel buttons = new GemPanel(new GridLayout(1, 4));
    btDelete = new GemButton(GemCommand.DELETE_CMD);
    btAdd = new GemButton(GemCommand.ADD_CMD);
    btModify = new GemButton(GemCommand.MODIFY_CMD);
    btClose = new GemButton(GemCommand.CLOSE_CMD);
    btDelete.addActionListener(this);
    btAdd.addActionListener(this);
    btModify.addActionListener(this);
    btClose.addActionListener(this);
    buttons.add(btDelete);
    buttons.add(btAdd);
    buttons.add(btModify);
    buttons.add(btClose);
    add(buttons, BorderLayout.SOUTH);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    Object src = e.getSource();
    try {
      if (btAdd == src) {
        add();
      } else if (btDelete == src) {
        RehearsalPass c = (RehearsalPass) tableModel.getItem(getSelectedIndex());
        if (c != null) {
          delete(c);
        }
      } else if (btModify == src) {
        update();
      } else if (btClose == src) {
        clear();
        desktop.removeModule("Menu.card");
      }
    } catch (SQLException ex) {
        GemLogger.log(ex.getMessage());
    }
  }

  private void add() throws SQLException {
    ctrl = new RehearsalPassCtrl(desktop, true);
    ctrl.loadCard(new RehearsalPass(-1));
    ctrl.setVisible(true);
    if (ctrl.isValidation()) {
      RehearsalPass n = ctrl.getCard();
      RehearsalPassIO.insert(n, DataCache.getDataConnection());
      addRow(n);
      desktop.getDataCache().add(n);
    }
  }
  
  private void delete(RehearsalPass c) throws SQLException {
    if (RehearsalPassIO.isActive(c.getId(), DataCache.getDataConnection())) {
      MessagePopup.warning(this, MessageUtil.getMessage("rehearsal.pass.delete.warning"));
    } else if (RehearsalPassIO.delete(c.getId(), DataCache.getDataConnection())) {
        deleteRow(c);
        desktop.getDataCache().remove(c);
      }
  }
  
  private void update() throws SQLException {
    ctrl = new RehearsalPassCtrl(desktop, true);
    RehearsalPass c = (RehearsalPass) tableModel.getItem(getSelectedIndex());
    ctrl.loadCard(c);
    ctrl.setVisible(true);
    if (ctrl.isValidation()) {
      RehearsalPass u = ctrl.getCard();
      RehearsalPassIO.update(u, DataCache.getDataConnection());
      updateRow(u);
      desktop.getDataCache().update(u);
    }
  }

}

/*
 * @(#)StatsFilterDlg.java 2.10.0 13/06/2016
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
 * along with Algem. If not, see http://www.gnu.org/licenses.
 *
 */
package net.algem.edition;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumnModel;
import net.algem.util.BundleUtil;
import net.algem.util.GemCommand;
import net.algem.util.MessageUtil;
import net.algem.util.module.GemModule;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.JTableModel;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.10.0
 * @since 2.10.0 07/06/2016
 */
public class StatsFilterDlg
  extends JDialog
  implements ActionListener {

  private JButton all, none;
  private GemButton btOk;
  private GemButton btCancel;
  private JTable table;
  private JTableModel<StatElement> tableModel;
  private boolean validation;

  public StatsFilterDlg() {
  }

  public StatsFilterDlg(Frame owner, boolean modal) {
    super(owner, modal);
    super.setTitle(BundleUtil.getLabel("Statistics.label"));
  }

  public void createUI(List<StatElement> statList) {
    setLayout(new BorderLayout());
    GemPanel top = new GemPanel();
    all = new JButton(BundleUtil.getLabel("Check.all"));
    all.addActionListener(this);
    none = new JButton(BundleUtil.getLabel("Uncheck.all"));

    none.addActionListener(this);
    top.add(all, BorderLayout.EAST);
    top.add(none, BorderLayout.WEST);
    add(top, BorderLayout.NORTH);
    tableModel = new StatTableModel();
    for (StatElement e : statList) {
      tableModel.addItem(new StatElement(e.getKey(), e.getLabel(), true));
    }
    table = new JTable(tableModel);
    TableColumnModel cm = table.getColumnModel();
    cm.getColumn(0).setPreferredWidth(10);
    cm.getColumn(1).setPreferredWidth(600);
    cm.getColumn(2).setPreferredWidth(20);
    JScrollPane js = new JScrollPane(table);
    add(js, BorderLayout.CENTER);

    GemPanel footer = new GemPanel(new BorderLayout(0,5));
    footer.add(new JLabel(MessageUtil.getMessage("statistics.long.request.info")), BorderLayout.NORTH);

    GemPanel buttons = new GemPanel(new GridLayout(1, 2));
    btOk = new GemButton(GemCommand.VALIDATION_CMD);
    btOk.addActionListener(this);
    btCancel = new GemButton(GemCommand.CANCEL_CMD);
    btCancel.addActionListener(this);
    buttons.add(btOk);
    buttons.add(btCancel);
    footer.add(buttons, BorderLayout.SOUTH);
    add(footer, BorderLayout.SOUTH);
    setSize(GemModule.M_SIZE);
    setVisible(true);
  }

  private void close() {
    setVisible(false);
  }

  public List<StatElement> getSelected() {
    List<StatElement> elements = tableModel.getData();
    List<StatElement> selected = new ArrayList<>();
    if (table.getSelectedRows().length == 0) {
      return elements;
    }
    for (StatElement e : elements) {
      if (e.isActive()) {
        selected.add(e);
      }
    }
    return selected;
  }

  public boolean isValidation() {
    return validation;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    Object src = e.getSource();
    if (src == btOk) {
      validation = true;
      close();
    } else if (src == btCancel) {
      validation = false;
      close();
    } else if (src == all) {
      List<StatElement> elements = tableModel.getData();
      for (StatElement el : elements) {
        el.setActive(true);
      }
      tableModel.fireTableDataChanged();
    } else if (src == none) {
      List<StatElement> elements = tableModel.getData();
      for (StatElement el : elements) {
        el.setActive(false);
      }
      tableModel.fireTableDataChanged();
    }

  }
}

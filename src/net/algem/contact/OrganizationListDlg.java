/*
 * @(#) SetupOrganizationDlg.java Algem 2.15.0 30/07/2017
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
 */
package net.algem.contact;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import net.algem.util.BundleUtil;
import net.algem.util.GemCommand;
import net.algem.util.MessageUtil;
import net.algem.util.module.GemModule;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemPanel;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.15.0
 * @since 2.15.0 30/07/2017
 */
public class OrganizationListDlg
  extends JDialog
  implements ActionListener {

  private OrganizationTableModel model;
  private JTable table;
  private JLabel msgLabel = new JLabel();
  private GemButton btCreate;
  private GemButton btClose;
  private Frame parent;
  private Organization org;
//  private Person referent;
  private PersonView personView;
  private boolean cancelled;

  OrganizationListDlg(Frame owner, boolean modal, PersonView personView) {
    super(owner, BundleUtil.getLabel("Organization.label"), modal);
    this.parent = owner;
    this.personView = personView;
//    this.referent = defRef;
    model = new OrganizationTableModel();
    table = new JTable(model);
  }

  void initUI() {
    table.getColumnModel().getColumn(0).setPreferredWidth(240);
    table.getColumnModel().getColumn(1).setPreferredWidth(80);
    table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent e) {
        setOrg();
        setVisible(false);
      }
    });
    JPanel mainPanel = new JPanel(new BorderLayout());

    JScrollPane scroll = new JScrollPane(table);
    mainPanel.add(scroll, BorderLayout.CENTER);
//    msgLabel = new JLabel("test label");
    mainPanel.add(msgLabel, BorderLayout.SOUTH);

    btClose = new GemButton(GemCommand.CLOSE_CMD);
    btClose.addActionListener(this);

    btCreate = new GemButton(GemCommand.CREATE_CMD);
    btCreate.addActionListener(this);

    GemPanel buttons = new GemPanel(new GridLayout(1,2));
    buttons.add(btCreate);
    buttons.add(btClose);

    add(mainPanel, BorderLayout.CENTER);
    add(buttons, BorderLayout.SOUTH);
    setSize(GemModule.XS_SIZE);
    setLocationRelativeTo(parent);
    setVisible(true);
  }

  void setOrg() {
    int row = table.getSelectedRow();
    if (row >= 0) {
      org = (Organization) model.getItem(row);
    }
  }

  Organization getOrg() {
    return org;
  }

  boolean isCancelled() {
    return cancelled;
  }

  public void loadResult(List<Organization> result) {

    for (Organization c : result) {
      model.addItem(c);
    }
    if (result.isEmpty()) {
      msgLabel.setText(MessageUtil.getMessage("search.empty.list.status"));
    } else {
      msgLabel.setText(MessageUtil.getMessage("search.list.status", result.size()));

    }
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    Object src = e.getSource();
    if (src == btClose) {
      cancelled = true;
      //setCity();
    } else if(src == btCreate) {
//      List<Person> pers = new ArrayList<>();
//      pers.add(personView.get());
      OrganizationCtrl orgCtrl = new OrganizationCtrl(null, true, personView);
      orgCtrl.setIsCreation(true);
      orgCtrl.createUI();
    }
    setVisible(false);
  }
}

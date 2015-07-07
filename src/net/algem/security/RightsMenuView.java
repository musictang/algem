/*
 * @(#)RightsMenuView.java	2.9.4.9 06/07/15
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
package net.algem.security;

import java.awt.BorderLayout;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.table.TableColumn;
import net.algem.util.BundleUtil;
import net.algem.util.ui.GemPanel;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.9
 * @since 2.6.a 01/08/2012
 */
public class RightsMenuView
        extends GemPanel
{

  private User user;
  private JTable table;
  private JTextField login;
  private JTextField id;
  private JComboBox profile;
  private MenuTableModel tableModel;

  public RightsMenuView(final UserService service) {
    table = new JTable();
    table.setAutoCreateRowSorter(true);
    table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

    JScrollPane p = new JScrollPane(table);
    p.setBorder(new BevelBorder(BevelBorder.LOWERED));

    tableModel = new MenuTableModel(service);
    table.setModel(tableModel);

    id = new JTextField(6);
    id.setEnabled(false);
    login = new JTextField(8);
    profile = new JComboBox(UserIO.PROFIL_NAMES);

    JPanel top = new JPanel();
    top.add(new JLabel(BundleUtil.getLabel("Id.label")));
    top.add(id);
    top.add(new JLabel(BundleUtil.getLabel("Login.label")));
    top.add(login);
    top.add(new JLabel(BundleUtil.getLabel("Profile.label")));
    top.add(profile);

    setLayout(new BorderLayout());
    add(top, BorderLayout.NORTH);
    add(p, BorderLayout.CENTER);
  }

  public void clear() {
    id.setText("");
    login.setText("");
  }

  public String getLogin() {
    String prf = login.getText();
    return (prf.equals("") ? user.getLogin() : prf);
  }

  public int getProfile() {
    return profile.getSelectedIndex();
  }

  public void load(User _user) {
    user = _user;
    id.setText(String.valueOf(user.getId()));
    login.setText(user.getLogin());
    tableModel.load(user.getId());
    profile.setSelectedIndex(user.getProfile());
    TableColumn col = table.getColumnModel().getColumn(0);
    col.setMaxWidth(50);
    col = table.getColumnModel().getColumn(2);
    col.setMaxWidth(50);
  }
}

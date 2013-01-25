/*
 * @(#)RightsMenuView.java	2.6.a 01/08/2012
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
package net.algem.security;

import java.awt.BorderLayout;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.table.TableColumn;
import net.algem.util.BundleUtil;
import net.algem.util.DataConnection;
import net.algem.util.ui.GemPanel;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 * @since 2.6.a 01/08/2012
 */
public class RightsMenuView
        extends GemPanel
{

  private DataConnection dc;
  private User user;
  private JTable table;
  private JTextField login;
  private JTextField id;
  private JComboBox profile;
  //String [] ref_profils = {"Basique","Utilisateur","Professeur","Public","Administrateur"};
  private MenuTableModel tmodel;

  public RightsMenuView(UserService service) {
    table = new JTable();
    table.setAutoCreateRowSorter(true);
    table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

    JScrollPane p = new JScrollPane(table);
    p.setBorder(new BevelBorder(BevelBorder.LOWERED));

    tmodel = new MenuTableModel(service);
    table.setModel(tmodel);

    id = new JTextField(6);
    id.setEnabled(false);
    login = new JTextField(8);
    profile = new JComboBox(UserIO.PROFIL_NAMES);

    JPanel haut = new JPanel();
    haut.add(new JLabel(BundleUtil.getLabel("Id.label")));
    haut.add(id);
    haut.add(new JLabel(BundleUtil.getLabel("Login.label")));
    haut.add(login);
    haut.add(new JLabel(BundleUtil.getLabel("Profile.label")));
    haut.add(profile);

    setLayout(new BorderLayout());
    add(haut, BorderLayout.NORTH);
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
    tmodel.load(user.getId());
    profile.setSelectedIndex(user.getProfile());
    TableColumn col = table.getColumnModel().getColumn(0);
    col.setMaxWidth(50);
    col = table.getColumnModel().getColumn(2);
    col.setMaxWidth(50);
    //table.repaint();
  }
}

/*
 * @(#)RightsCardView.java	2.13.2 03/05/2017
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
package net.algem.security;

import java.awt.BorderLayout;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.table.TableColumn;
import net.algem.util.ui.GemPanel;

/**
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.13.2
 */
public class RightsCardView
        extends GemPanel
{

  private User user;
  private JTable table;
	private JTextField id;
  private JTextField login;
  private RightsTableModel tableModel;

  public RightsCardView(UserService service) {

    table = new JTable();
//    table.setAutoCreateRowSorter(false);
    table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

    JScrollPane p = new JScrollPane(table);
    p.setBorder(new BevelBorder(BevelBorder.LOWERED));

    tableModel = new RightsTableModel(service);
    table.setModel(tableModel);

    id = new JTextField(6);
    login = new JTextField(8);

    JPanel haut = new JPanel();
    haut.add(new JLabel("id"));
    haut.add(id);
    haut.add(new JLabel("login"));
    haut.add(login);

    setLayout(new BorderLayout());
    add(haut, BorderLayout.NORTH);
    add(p, BorderLayout.CENTER);
  }

  public void clear() {
    id.setText("");
    login.setText("");
  }

  public void load(User _user) {
    user = _user;
    id.setText(String.valueOf(user.getId()));
    login.setText(user.getLogin());
    tableModel.load(user.getId());
    TableColumn col = table.getColumnModel().getColumn(0);
    col.setMinWidth(200);
    table.repaint();
  }
}

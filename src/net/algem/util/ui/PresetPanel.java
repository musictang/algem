/*
 * @(#)PresetPanel.java 2.9.4.13 21/10/15
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

package net.algem.util.ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import net.algem.util.BundleUtil;
import net.algem.util.GemCommand;
import net.algem.util.menu.MenuPopupListener;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.13
 * @since 2.9.4.13 21/10/15
 */
public class PresetPanel
  extends JPanel
{

  private JButton btDel;
  private JButton btAdd;
  private JPopupMenu popup;
  private JMenuItem miRename;

  public PresetPanel(String t, JList list) {
    setLayout(new BorderLayout());
    GemLabel title = new GemLabel(t);
    add(title, BorderLayout.NORTH);
    JScrollPane scroll = new JScrollPane(list);
    add(scroll, BorderLayout.CENTER);

    GemPanel cmdPanel = new GemPanel(new GridLayout(1,2));
    btDel = new GemButton(GemCommand.DELETE_CMD);
    btAdd = new GemButton(GemCommand.ADD_CMD);
    cmdPanel.add(btDel);
    cmdPanel.add(btAdd);
    add(cmdPanel, BorderLayout.SOUTH);

    popup = new JPopupMenu();
    popup.addSeparator();
    popup.add(miRename = new JMenuItem(BundleUtil.getLabel("Rename.label")));
    list.addMouseListener(new MenuPopupListener(this, popup));

  }

  public PresetPanel(JList list) {
    this("Présélections", list);
  }

  public void addActionListener(ActionListener listener) {
    btDel.addActionListener(listener);
    btAdd.addActionListener(listener);
    miRename.addActionListener(listener);
  }

}

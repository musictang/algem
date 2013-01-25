/*
 * @(#)MaintenanceCtrl.java	1.0b 12/12/2001
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
package net.algem.opt;

import net.algem.util.ui.GemPanel;
import net.algem.util.ui.GridBagHelper;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemField;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.*;
import javax.swing.table.*;

import net.algem.util.DataCache;
import net.algem.util.ui.ErrorDlg;
import net.algem.util.GemCommand;
import net.algem.util.GemLogger;

/**
 * table maintenance
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 * @deprecated 
 */
public class MaintenanceCtrl
        extends GemPanel
        implements ActionListener
{

  DataCache cache;
  GemField texte;
  JComboBox type;
  GemButton ajouter;
  GemButton fermer;
  MaintenanceTableModel afaire;
  MaintenanceTableModel fait;
  JTable tableAFaire;
  JTable tableFait;
  ActionListener actionListener;

  public MaintenanceCtrl(DataCache _dc) {
    cache = _dc;

    texte = new GemField(50);
    type = new JComboBox(Maintenance.types);

    afaire = new MaintenanceTableModel();
    tableAFaire = new JTable(afaire);
    tableAFaire.setAutoCreateRowSorter(true);

    TableColumnModel cm = tableAFaire.getColumnModel();
    cm.getColumn(0).setPreferredWidth(50);
    cm.getColumn(1).setPreferredWidth(40);
    cm.getColumn(2).setPreferredWidth(35);
    cm.getColumn(3).setPreferredWidth(350);
    cm.getColumn(4).setPreferredWidth(30);

    JScrollPane spAFaire = new JScrollPane(tableAFaire);

    fait = new MaintenanceTableModel();
    tableFait = new JTable(fait);
    tableFait.setAutoCreateRowSorter(true);

    cm = tableFait.getColumnModel();
    cm.getColumn(0).setPreferredWidth(50);
    cm.getColumn(1).setPreferredWidth(40);
    cm.getColumn(2).setPreferredWidth(35);
    cm.getColumn(3).setPreferredWidth(350);
    cm.getColumn(4).setPreferredWidth(30);

    JScrollPane spFait = new JScrollPane(tableFait);

    ajouter = new GemButton(GemCommand.ADD_CMD);
    fermer = new GemButton(GemCommand.CLOSE_CMD);
    GemPanel boutons = new GemPanel();
    boutons.setLayout(new GridLayout(1, 2));
    boutons.add(ajouter);
    boutons.add(fermer);

    ajouter.addActionListener(this);
    fermer.addActionListener(this);

    GemPanel p1 = new GemPanel();
    p1.add(type);
    p1.add(texte);

    JTabbedPane tp = new JTabbedPane(JTabbedPane.TOP);
    tp.add("A Faire", spAFaire);
    tp.add("Fait", spFait);

    setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(this);

    gb.add(tp, 0, 0, 1, 1, GridBagHelper.BOTH, 1.0, 1.0);
    gb.add(p1, 0, 1, 1, 1, GridBagHelper.HORIZONTAL, 1.0, 0.0);
    gb.add(boutons, 0, 2, 1, 1, GridBagHelper.HORIZONTAL, 1.0, 0.0);

    load();
  }

  public void removeActionListener(ActionListener l) {
    actionListener = AWTEventMulticaster.remove(actionListener, l);
  }

  public void addActionListener(ActionListener l) {
    actionListener = AWTEventMulticaster.add(actionListener, l);
  }

  public void load() {
    String query = " where fait='f' order by jour, personne";
    Vector v = MaintenanceIO.find(query, cache.getDataConnection());
    for (int i = 0; i < v.size(); i++) {
      afaire.addItem((Maintenance) v.elementAt(i));
    }
    query = " where fait='t' order by jour, personne";
    v = MaintenanceIO.find(query, cache.getDataConnection());
    for (int i = 0; i < v.size(); i++) {
      fait.addItem((Maintenance) v.elementAt(i));
    }
  }

  public void actionPerformed(ActionEvent evt) {
    String cmd = evt.getActionCommand();
    if (cmd.equals(GemCommand.CLOSE_CMD)) {
      if (actionListener != null) {
        actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, GemCommand.CANCEL_CMD));
      }
    } else if (cmd.equals(GemCommand.ADD_CMD)) {
      try {
        insertion();
        clear();
      } catch (Exception e) {
        GemLogger.logException("insertion maintenance", e, this);
      }
    }
    /*
    int n = tableAFaire.getSelectedRow();
    if (n <= 0)
    return;
     */

  }

  void insertion() throws SQLException {
    String s = texte.getText().trim();
    if (s.length() == 0) {
      new ErrorDlg(this, "saisie incorrecte");
      return;
    }
    Maintenance v = new Maintenance(cache.getUser().getLogin(), type.getSelectedIndex(), s);
    MaintenanceIO.insert(v, cache.getDataConnection());

    afaire.addItem(v);
    clear();
  }

  void clear() {
    texte.setText("");
//		liste.clear();
  }
}

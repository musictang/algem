/*
 * @(#)PayeurListView.java	2.8.w 08/07/14
 * 
 * Copyright (c) 1999-2014 Musiques Tangentes. All Rights Reserved.
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
package net.algem.contact;

import java.awt.BorderLayout;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumnModel;
import net.algem.util.DataCache;
import net.algem.util.GemLogger;
import net.algem.util.model.Model;
import net.algem.util.ui.GemPanel;

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.w
 * @deprecated
 */
public class PayerListView
        extends GemPanel
{

  private final PersonTableModel personTableModel;
  private final JTable tableAdherents;

  public PayerListView() {
    personTableModel = new PersonTableModel();
    tableAdherents = new JTable(personTableModel);
    tableAdherents.setAutoCreateRowSorter(true);

    TableColumnModel cm = tableAdherents.getColumnModel();
    cm.getColumn(0).setPreferredWidth(30);
    cm.getColumn(1).setPreferredWidth(40);
    cm.getColumn(2).setPreferredWidth(120);
    cm.getColumn(3).setPreferredWidth(120);

    JScrollPane pm = new JScrollPane(tableAdherents);

    setLayout(new BorderLayout());
    add("Center", pm);
  }

  public void clear() {
    personTableModel.clear();
  }

  public void load(int id) {
    String query = "SELECT idper FROM eleve WHERE payeur=" + id;
    try {
      ResultSet rs = DataCache.getDataConnection().executeQuery(query);
      while (rs.next()) {
        int eid = rs.getInt(1);
        Person p = ((PersonIO) DataCache.getDao(Model.Person)).findById(eid);
        if (p != null) {
          personTableModel.addItem(p);
        } else {
          personTableModel.addItem(new Person("inconnu"));
        }
      }
    } catch (SQLException e) {
      GemLogger.logException(query, e);
    }
  }
}

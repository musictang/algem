/*
 * @(#) ImportCsvTablePreview.java Algem 2.13.0 22/03/2017
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
package net.algem.edition;

import java.awt.Dimension;
import java.util.List;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumnModel;
import net.algem.contact.Contact;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.JTableModel;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.13.0
 * @since 2.13.0 22/03/2017
 */
public class ImportCsvTablePreview
  extends GemPanel {

  private JTable table;
  private final JTableModel<Contact> tableModel;

  public ImportCsvTablePreview(JTableModel<Contact> model) {
    this.tableModel = model;
  }

  public void createUi() {
    table = new JTable(tableModel);
    TableColumnModel cm = table.getColumnModel();
    cm.getColumn(0).setPreferredWidth(10);
    cm.getColumn(1).setPreferredWidth(20);
    for (int i = 2; i < 12; i++) {
      cm.getColumn(i).setPreferredWidth(70);
    }
    JScrollPane scroll = new JScrollPane(table);
    scroll.setPreferredSize(new Dimension(840, 400));
    add(scroll);
  }

  public void load(List<Contact> contacts) {
    tableModel.clear();
    for (Contact c : contacts) {
      tableModel.addItem((Contact) c);
    }
  }
}

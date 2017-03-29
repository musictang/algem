/*
 * @(#) ImportCsvTablePreview.java Algem 2.13.0 28/03/2017
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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import net.algem.contact.Contact;
import net.algem.contact.ContactImport;
import net.algem.util.MessageUtil;
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
  private final JTableModel<ContactImport> tableModel;
  private JLabel status;

  public ImportCsvTablePreview(JTableModel<ContactImport> model) {
    this.tableModel = model;
  }

  public void createUI() {
    setLayout(new BorderLayout());
    table = new JTable(tableModel) {
      protected JTableHeader createDefaultTableHeader() {
        return new JTableHeader(columnModel) {
            public String getToolTipText(MouseEvent e) {
                //String tip = null;
                java.awt.Point p = e.getPoint();
                int index = columnModel.getColumnIndexAtX(p.x);
                int realIndex = columnModel.getColumn(index).getModelIndex();
                return ImportCsvPreview.IMPORT_TIPS[realIndex];
            }
        };
    }
    };
    TableColumnModel cm = table.getColumnModel();
    cm.getColumn(0).setPreferredWidth(10);
    cm.getColumn(1).setPreferredWidth(20);
    cm.getColumn(2).setPreferredWidth(55);
    cm.getColumn(3).setPreferredWidth(55);
    cm.getColumn(4).setPreferredWidth(40);
    cm.getColumn(5).setPreferredWidth(40);
    cm.getColumn(6).setPreferredWidth(10);
    cm.getColumn(7).setPreferredWidth(20);
    for (int i = 7; i < 17; i++) {
      cm.getColumn(i).setPreferredWidth(55);
    }
    JScrollPane scroll = new JScrollPane(table);
    scroll.setPreferredSize(new Dimension(840, 500));
    add(scroll, BorderLayout.CENTER);
    status = new JLabel();
    add(status, BorderLayout.SOUTH);
  }

  public void load(List<ContactImport> contacts, int errors) {
    status.setText(null);
    tableModel.clear();
    for (Contact c : contacts) {
      tableModel.addItem((ContactImport) c);
    }
    if (errors > 0) {
      status.setText(MessageUtil.getMessage("import.contacts.warning", errors));
    }
  }

}

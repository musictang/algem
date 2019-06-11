/*
 * @(#)InvoiceListCtrl.java 2.9.4.7 12/06/15
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
package net.algem.billing;

import java.awt.BorderLayout;
import javax.swing.DropMode;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.border.BevelBorder;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import net.algem.util.ui.ListCtrl;
import net.algem.util.ui.PriceCellRenderer;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.7
 * @since 2.3.a 14/02/12
 */
public class InvoiceListCtrl
        extends ListCtrl
{

  public InvoiceListCtrl(boolean b, BillingService service) {

    super(b);
    this.tableModel = new InvoiceTableModel(service);

    table = new JTable(tableModel);
    table.setAutoCreateRowSorter(true);
    RowSorter<? extends TableModel> sorter = table.getRowSorter();
    if (sorter instanceof TableRowSorter) {
      ((TableRowSorter) sorter).setComparator(0, new InvoiceComparator());
    }
    TableColumnModel cm = table.getColumnModel();
    cm.getColumn(0).setPreferredWidth(14);
    cm.getColumn(1).setPreferredWidth(80);
    cm.getColumn(2).setPreferredWidth(80);
    cm.getColumn(3).setPreferredWidth(18);
    cm.getColumn(4).setPreferredWidth(280);
    cm.getColumn(5).setPreferredWidth(30);
    
    cm.getColumn(5).setCellRenderer(new PriceCellRenderer());
    JScrollPane p = new JScrollPane(table);
    p.setBorder(new BevelBorder(BevelBorder.LOWERED));

    add(p, BorderLayout.CENTER);

  }

  public Quote getElementAt(int n) {
    return (Quote) tableModel.getItem(n);
  }

  <Q extends Quote> void reload(Q v) {
    ((InvoiceTableModel) tableModel).reset(v);
  }

  public int getIdContact() {
    int index = getSelectedIndex();
    int col = table.getSelectedColumn();
    Quote q = (Quote) tableModel.getItem(index);
    if (col == 1) return q.getPayer(); 
    if (col == 2) return q.getMember();
    return 0;
  }
}

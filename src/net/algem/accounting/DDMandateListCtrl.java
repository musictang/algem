/*
 * @(#)DDMandateListCtrl.java 2.8.r 14/01/14
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
package net.algem.accounting;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.BevelBorder;
import net.algem.util.ui.ListCtrl;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">jean-marc gobat</a>
 * @version 2.8.r
 * @since 2.8.r 08/01/14
 */
public class DDMandateListCtrl 
  extends ListCtrl
{
 public DDMandateListCtrl(boolean searchFlag, DirectDebitService service) {

    super(searchFlag);
    this.tableModel = new DDMandateTableModel();

    table = new JTable(tableModel);
    table.setAutoCreateRowSorter(true);

    setColumns(10,25,25,100,130,10);

    JScrollPane p = new JScrollPane(table);
    p.setBorder(new BevelBorder(BevelBorder.LOWERED));

    add(p, BorderLayout.CENTER);

  }

  DDMandate getElementAt(int n) {
    return (DDMandate) tableModel.getItem(table.convertRowIndexToModel(n));
  }
  
  List<DDMandate> getSelected() {
    List<DDMandate> selected = new ArrayList<DDMandate>();
    int[] rows = table.getSelectedRows();
    
    for (int i = 0; i < rows.length; i++) {
      DDMandate dd = getElementAt(rows[i]);
      selected.add(dd);
    }
    return selected;
  }
  
  public DDMandate getMandate() {
    int n = table.getSelectedRow();
    if (n < 0) {
      return null;
    }
    return (DDMandate) tableModel.getItem(table.convertRowIndexToModel(n));
  }

}

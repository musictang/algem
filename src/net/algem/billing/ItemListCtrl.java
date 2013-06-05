/*
 * @(#)ItemListCtrl.java	2.3.d 23/03/12
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
package net.algem.billing;

import java.awt.BorderLayout;
import java.text.NumberFormat;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;
import net.algem.accounting.AccountUtil;
import net.algem.config.Param;
import net.algem.util.ui.ListCtrl;

/**
 * Item list controller.
 * 
 * @author Jean-Marc Gobat <a href="mailto:jmg@musiques-tangentes.asso.fr">jmg@musiques-tangentes.asso.fr</a>
 * @version 2.3.d
 * @since 2.3.a 30/01/12
 */
public class ItemListCtrl
        extends ListCtrl
{

  private static NumberFormat nf = AccountUtil.getDefaultNumberFormat();

  public ItemListCtrl() {
    super(true);
    this.tableModel = new ItemTableModel();
    table = new JTable(tableModel);
    table.setAutoCreateRowSorter(true);

    setColumns(20, 300, 100, 20, 25);
    setColumnsRenderer(2, 3, 4);
    addScrollPane();

  }

  /**
   * Liste d'articles de facturation.
   * @param tableModel
   * @param withSearch
   */
  public ItemListCtrl(InvoiceItemTableModel tableModel, boolean withSearch) {

    super(withSearch);
    this.tableModel = tableModel;

    table = new JTable(tableModel);
    table.setAutoCreateRowSorter(true);

    setColumns(400, 100, 40, 40, 120);
    setColumnsRenderer(1, 2, 3, 4);
    addScrollPane();

  }

  public InvoiceItem getSelectedItem() {
    return (InvoiceItem) tableModel.getItem(table.convertRowIndexToModel(table.getSelectedRow()));
  }

  /**
   * Formats the columns with decimal number.
   *
   * @param cols the cols to format
   */
  private void setColumnsRenderer(int... cols) {
    InvoiceNumberRenderer nr = new InvoiceNumberRenderer();
    TableColumnModel cm = table.getColumnModel();
    for (int i = 0; i < cols.length; i++) {
      cm.getColumn(cols[i]).setCellRenderer(nr);
    }

  }

  private void addScrollPane() {

    JScrollPane p = new JScrollPane(table);
    p.setBorder(new BevelBorder(BevelBorder.LOWERED));
    add(p, BorderLayout.CENTER);
  }

  /**
   * Default renderer for columns with numbers.
   */
  static class InvoiceNumberRenderer extends DefaultTableCellRenderer
  {

    @Override
    public void setValue(Object value) {
      if (value instanceof Param) {
        Param p = (Param) value;
        setText((p.getValue() == null) ? "" : nf.format(Float.parseFloat(p.getValue())));
      } else if (value instanceof Number) {
        if (value instanceof Integer || value instanceof Short) {
          setText((value == null) ? "" : String.valueOf(value));
        } else {
          setText((value == null) ? "" : nf.format(value));
        }
      } else if (value instanceof String) {
        setText((String) ((value == null) ? "" : value));
      }
      setHorizontalAlignment(SwingConstants.RIGHT);
    }
  }
}

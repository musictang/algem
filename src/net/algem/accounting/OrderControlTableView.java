/*
* @(#)OrderControlTableView.java 2.17.1 20/10/19
*
* Copyright (c) 1999-2019 Musiques Tangentes. All Rights Reserved.
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
import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.Comparator;
import javax.swing.RowFilter.Entry;
import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.*;
import javax.swing.text.JTextComponent;
import net.algem.contact.Organization;
import net.algem.contact.Person;
import net.algem.planning.DateFr;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.menu.MenuPopupListener;
import net.algem.util.model.Model;

/**
 * Order line table view.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @version 2.17.1d
 * @since 2.17.1d 20/10/2019
 *
 */
public class OrderControlTableView
  extends JPanel
  implements TableModelListener {

  private OrderControlTableModel tableModel;
  private JTable table;
  private JScrollPane panel;
  private DateFr begin;
  private DateFr end;

  public OrderControlTableView(OrderControlTableModel tableModel, ActionListener al) {

    this.tableModel = tableModel;
    table = new JTable(tableModel);

    table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

    TableColumnModel cm = table.getColumnModel();
    cm.getColumn(0).setPreferredWidth(50);//id payer
    cm.getColumn(1).setPreferredWidth(150);//nom payeur
    cm.getColumn(2).setPreferredWidth(50);//nfacture
    cm.getColumn(3).setPreferredWidth(70);//amount

    DefaultTableCellRenderer rd = new DefaultTableCellRenderer();
    rd.setHorizontalAlignment(SwingConstants.RIGHT);
    table.getColumnModel().getColumn(3).setCellRenderer(rd);
    tableModel.addTableModelListener(this);

    panel = new JScrollPane(table);

    setLayout(new BorderLayout());
    add(panel, BorderLayout.CENTER);
  }

  @Override
  public void tableChanged(TableModelEvent evt) {
  }

  public void removeElementAt(int n) {
    tableModel.removeElementAt(table.convertRowIndexToModel(n));
  }

  public OrderLine getElementAt(int n) {
    return (OrderLine) tableModel.getElementAt(table.convertRowIndexToModel(n));
  }

  public void setElementAt(OrderLine e, int n) {
    tableModel.setElementAt(e, table.convertRowIndexToModel(n));
  }

  public int getSelectedRow() {
    int n = table.getSelectedRow();
    if (table.getSelectedRowCount() < 0 || n < 0 || tableModel.getRowCount() <= n) {
      return -1;
    }
    return n;
  }

  public int[] getSelectedRows() {
    return table.getSelectedRows();
  }

  <T> int getRowIndexByModel(T model) {
    for (int i = 0; i < table.getRowCount(); i++) {
      if (getElementAt(i).equals(model)) {
        // what if value is not unique?
        return i;
      }
    }
    return -1;
  }


  private void setDates(DateFr date) {
    /*Calendar cal = Calendar.getInstance();
    cal.setTime(date.getDate());
    cal.set(Calendar.MONTH, Calendar.SEPTEMBER);// beginning of year for accounting
    cal.set(Calendar.DAY_OF_MONTH, 1);
    begin = new DateFr(cal.getTime());*/
    begin = new DateFr(date);
    end = new DateFr(begin);
    end.incYear(1);//XXX enlarge after : 2 ?
    end.decDay(1);
  }

  /**
   * Retrieves the table view.
   *
   * @return une table
   */
  public JTable getTable() {
    return table;
  }

  /**
   * This rendered is used to show payer and member names when mouse is moved over payer's or member's id.
   * The cell value returned is a String and overrides Integer.class returned by the model.
   */
  class ContactNameTableCellRenderer
    extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(
      JTable table, Object value,
      boolean isSelected, boolean hasFocus,
      int row, int column) {
      JLabel c = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
      String idper = (String) value;
      setHorizontalAlignment(SwingConstants.RIGHT);
      try {
        Person p = (Person) DataCache.findId(Integer.parseInt(idper.trim()), Model.Person);
        //assert(p != null);
        if (p != null) {
          Organization o = p.getOrganization();
          String org = o == null ? null : o.getCompanyName();
          c.setToolTipText(org != null && org.length() > 0 ? org : p.getFirstnameName());
        } else {
          c.setToolTipText(null);
        }
      } catch (SQLException | NumberFormatException e) {
        c.setToolTipText(null);
      }
      return c;
    }
  }


}

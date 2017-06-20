/*
* @(#)OrderLineTableView.java 2.14.0 20/06/17
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
package net.algem.accounting;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import javax.swing.RowFilter.Entry;
import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.*;
import javax.swing.text.JTextComponent;
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
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @author <a href="mailto:damien.loustau@gmail.com">Damien Loustau</a>
 * @version 2.14.0
 * @since 1.0a 07/07/1999
 *
 */
public class OrderLineTableView
        extends JPanel
        implements TableModelListener
{

  private OrderLineTableModel tableModel;
  private JTable table;
  private JScrollPane panel;
  /**
   * Tooltips for columns header.
   */
  private String[] columnToolTips = {
    BundleUtil.getLabel("Payment.schedule.payer.tip"),
    BundleUtil.getLabel("Payment.schedule.member.tip"),
    BundleUtil.getLabel("Group.label"),
    BundleUtil.getLabel("Payment.schedule.date.tip"),
    BundleUtil.getLabel("Payment.schedule.label.tip"),
    BundleUtil.getLabel("Payment.schedule.mode.of.payment.tip"),
    BundleUtil.getLabel("Payment.schedule.amount.tip"),
    BundleUtil.getLabel("Payment.schedule.document.tip"),
    BundleUtil.getLabel("Payment.schedule.account.tip"),
    BundleUtil.getLabel("Payment.schedule.cost.account.tip"),
    BundleUtil.getLabel("Payment.schedule.cashing.tip"),
    BundleUtil.getLabel("Payment.schedule.transfer.tip"),
    BundleUtil.getLabel("Invoice.label")
  };
  private RowFilter<Object, Object> dateFilter;
  private RowFilter<Object, Object> memberShipFilter;
  private RowFilter<Object, Object> unpaidFilter;
  private RowFilter<Object, Object> invoiceFilter;
  private final TableRowSorter<TableModel> sorter;
  private DateFr begin;
  private DateFr end;
  private boolean hideBilling;

  public OrderLineTableView(OrderLineTableModel tableModel, ActionListener al) {

    this.tableModel = tableModel;
    table = new JTable(tableModel)
    {
      //Implements table header tool tips.
      @Override
      protected JTableHeader createDefaultTableHeader() {
        return new JTableHeader(columnModel)
        {

          @Override
          public String getToolTipText(MouseEvent e) {
            java.awt.Point p = e.getPoint();
            int index = columnModel.getColumnIndexAtX(p.x);
            int realIndex = columnModel.getColumn(index).getModelIndex();
            return columnToolTips[realIndex];
          }
        };
      }
    };

    sorter = new TableRowSorter<TableModel>(tableModel);
    table.setRowSorter(sorter);

    dateFilter = new RowFilter<Object, Object>()
    {

      @Override
      public boolean include(Entry<? extends Object, ? extends Object> entry) {
        DateFr date = (DateFr) entry.getValue(3);
        String payment = (String) entry.getValue(5);
        if (hideBilling) {
          return date.afterOrEqual(begin) && date.beforeOrEqual(end) && !payment.equals(ModeOfPayment.FAC.name());
        } else {
          return date.afterOrEqual(begin) && date.beforeOrEqual(end);
        }

      }
    };

    unpaidFilter = new RowFilter<Object, Object>()
    {

      @Override
      public boolean include(Entry<? extends Object, ? extends Object> entry) {
        boolean paid = (Boolean) entry.getValue(10);
        return !paid;
      }
    };

    invoiceFilter = new RowFilter<Object, Object>()
    {
      @Override
      public boolean include(Entry<? extends Object, ? extends Object> entry) {
        DateFr date = (DateFr) entry.getValue(3);
        String payment = (String) entry.getValue(5);
        if (begin != null && end != null) {
          return !payment.equals(ModeOfPayment.FAC.name()) && date.afterOrEqual(begin) && date.beforeOrEqual(end);
        } else {
          return !payment.equals(ModeOfPayment.FAC.name());
        }

      }

    };
    table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

    TableColumnModel cm = table.getColumnModel();
    cm.getColumn(0).setPreferredWidth(50);//payer
    cm.getColumn(1).setPreferredWidth(50);//member
    cm.getColumn(2).setPreferredWidth(30);//group
    cm.getColumn(3).setPreferredWidth(85);//date
    cm.getColumn(4).setPreferredWidth(160);//label
    cm.getColumn(5).setPreferredWidth(30);//mode of payment
    cm.getColumn(6).setPreferredWidth(70);//amount
    cm.getColumn(7).setPreferredWidth(65);//document number
    cm.getColumn(8).setPreferredWidth(160);//account
    cm.getColumn(9).setPreferredWidth(140);//cost account
    cm.getColumn(10).setPreferredWidth(30);//payed
    cm.getColumn(11).setPreferredWidth(30);//transferred
    cm.getColumn(12).setPreferredWidth(40);//invoice

    DefaultTableCellRenderer rd = new DefaultTableCellRenderer();
    rd.setHorizontalAlignment(SwingConstants.RIGHT);
    // alignement à droite de la colonne Montant
    table.getColumnModel().getColumn(6).setCellRenderer(rd);
    //tableVue.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    // la sélection multiple permet le copier-coller d'un ensemble de lignes vers une autre application
    table.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    tableModel.addTableModelListener(this);

    // Display name in tooltip for payer and member columns
    ContactNameTableCellRenderer nameCellRenderer = new ContactNameTableCellRenderer();
    table.getColumnModel().getColumn(0).setCellRenderer(nameCellRenderer);
    table.getColumnModel().getColumn(1).setCellRenderer(nameCellRenderer);
    AccountNameTableCellRenderer accountCellRenderer = new AccountNameTableCellRenderer();
    table.getColumnModel().getColumn(8).setCellRenderer(accountCellRenderer);
    table.getColumnModel().getColumn(9).setCellRenderer(accountCellRenderer);

    panel = new JScrollPane(table);

    setLayout(new BorderLayout());
    add(panel, BorderLayout.CENTER);
  }

  /**
   * Adds right click listener for displaying popup menu. At least one row must
   * be selected and this row must be marked transfered.
   *
   * @param popup
   */
  void addPopupMenuListener(JPopupMenu popup, final DataCache dataCache) {
    table.addMouseListener(new MenuPopupListener(table, popup)
    {

      @Override
      public void maybeShowPopup(MouseEvent e) {
        int[] rows = table.getSelectedRows();
        boolean t = false;
        boolean p = false;
        for (int i = 0; i < rows.length; i++) {
          if (getElementAt(rows[i]).isTransfered()) {
            t = true;
            break;
          }
        }

        for (int i = 0; i < rows.length; i++) {
          if (!getElementAt(rows[i]).isPaid()) {
            p = true;
            break;
          }
        }
        popup.getComponent(0).setEnabled(t && dataCache.authorize("Accounting.transfer.auth"));
        popup.getComponent(1).setEnabled(!t && dataCache.authorize("Accounting.transfer.auth"));

        ActionListener[] listeners = popup.getComponent(2).getListeners(ActionListener.class);
        if (listeners != null && listeners.length > 0) {
          popup.getComponent(2).setEnabled(p && dataCache.authorize("Payment.multiple.modification.auth"));
        }
        super.maybeShowPopup(e);
      }
    });
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

  public void setMemberShipFilter(final String... values) {
    memberShipFilter = new RowFilter<Object, Object>()
    {
      @Override
      public boolean include(Entry<? extends Object, ? extends Object> entry) {
        DateFr date = (DateFr) entry.getValue(3);
        String account = (String) entry.getValue(8);
        boolean has = false;
        for (String a : values) {
          if (account.equals(a) && date.after(begin) && date.before(end)) {
            has = true;
          }
        }
        return has;
      }
    };
  }

  /**
   * Line selection by date.
   *
   * @param date start date
   * @param hideBilling hide billing lines
   */
  public void filterByDate(DateFr date, boolean hideBilling) {
    this.hideBilling = hideBilling;
    if (date == null) {
      begin = null;
      sorter.setRowFilter(hideBilling ? invoiceFilter : null);
    } else {
      setDates(date);
      sorter.setRowFilter(dateFilter);
//      sorter.setRowFilter(RowFilter.regexFilter("^.*"+year+".*$", 2));
//      sorter.setRowFilter(RowFilter.begin(RowFilter.ComparisonType.AFTER, date, 2));
    }
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
   * 
   * @param begin start date
   * @param end end date
   * @param hideBilling hide billing lines
   */
  public void filterByPeriod(DateFr begin, DateFr end, boolean hideBilling) {
    this.hideBilling = hideBilling;
    this.begin = begin;
    this.end = end;
    sorter.setRowFilter(dateFilter);
  }

  public void filterByMemberShip(DateFr begin, DateFr end) {
    this.begin = begin;
    this.end = end;
    sorter.setRowFilter(memberShipFilter);
  }

  public void filterByUnpaid() {
    sorter.setRowFilter(unpaidFilter);
  }

  public void filterByPayment(boolean filter, DateFr d) {
    if (d == null) {
      this.begin = null;
    } else {
      setDates(d);
    }
    if (filter) {
      sorter.setRowFilter(invoiceFilter);
    } else {
      this.hideBilling = false;
      sorter.setRowFilter(d != null ? dateFilter : null);
    }
  }

  /**
   * Activates a listener for rows selection.
   *
   * @param tc text component to update
   */
  public void addListSelectionListener(JTextComponent tc) {
    table.getSelectionModel().addListSelectionListener(new OrderLineSelectionListener(this, tc));
  }

  /**
   * Retrieves the table view.
   *
   * @return une table
   */
  public JTable getTable() {
    return table;
  }

  class ContactNameTableCellRenderer
          extends DefaultTableCellRenderer
  {

    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object value,
            boolean isSelected, boolean hasFocus,
            int row, int column) {
      JLabel c = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
      String idper = (String) value;
      try {
        Person p = (Person) DataCache.findId(Integer.parseInt(idper.trim()), Model.Person);
        //assert(p != null);
        if (p != null) {
          String org = p.getOrganization();
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

  class AccountNameTableCellRenderer
          extends DefaultTableCellRenderer
  {

    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object value,
            boolean isSelected, boolean hasFocus,
            int row, int column) {
      JLabel c = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
      c.setToolTipText((String) value);
      return c;
    }
  }

}

/*
* @(#)OrderLineDlg.java	2.11.3 01/12/16
*
* Copyright (c) 1999-2016 Musiques Tangentes. All Rights Reserved.
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
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.print.PrinterException;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;
import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import net.algem.planning.DateFr;
import net.algem.planning.DateFrField;
import net.algem.util.*;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemField;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.MessagePopup;

/**
 * Global view of orderlines.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @author <a href="mailto:damien.loustau@gmail.com">Damien Loustau</a>
 * @version 2.11.3
 * @since 1.0a 07/07/1999
 */
public class OrderLineDlg
        extends GemPanel
        implements ActionListener, TableModelListener
{

  private Frame parent;
  private final DataConnection dc;
  private final DataCache dataCache;
  private OrderLineTableModel tableModel;
  private DateFrField dateStart;
  private DateFrField dateEnd;
  private OrderLineTableView tableView;
  private GemButton btLoad;
  private GemButton btCurrentMonth;
  private GemButton btPrint;
  private GemButton btCreate;
  private GemButton btSuppress;
  private GemButton btModify;
  private GemPanel buttons;
  private JLabel totalLabel;
  private GemField totalField;
  private final NumberFormat nf = NumberFormat.getInstance(Locale.FRANCE);
  private JPopupMenu popup;
  private JMenuItem miCancelTransfer;
  private JMenuItem miTransfer;
  private JMenuItem miCashing;
  private JCheckBoxMenuItem cbCheckPayment;
  private JMenu menutop;
  private OrderLineView dlg;
  private JCheckBox invoiceLineFilter;
  private OrderLineEditor.InvoiceLinesFilter invoiceFilter;

  /**
   *
   * @param desktop GemDesktop instance
   * @param tableModel
   */
  public OrderLineDlg(GemDesktop desktop, OrderLineTableModel tableModel) {
    parent = desktop.getFrame();
    this.dataCache = desktop.getDataCache();
    this.dc = DataCache.getDataConnection();
    this.tableModel = tableModel;
  }

  public void init() {
    JMenuBar menubar = new JMenuBar();
    menutop = new JMenu("Options");
    menubar.add(menutop);

    menutop.add(cbCheckPayment = new JCheckBoxMenuItem(BundleUtil.getLabel("Payment.multiple.modification.auth")));
    if (!dataCache.authorize("Payment.multiple.modification.auth")) {
      cbCheckPayment.setEnabled(false);
    }
    cbCheckPayment.addActionListener(this);
    popup = new JPopupMenu();
    popup.add(miCancelTransfer = new JMenuItem(BundleUtil.getLabel("Transfer.cancel.label")));
    popup.add(miTransfer = new JMenuItem(BundleUtil.getLabel("Transfer.set.label")));
    miTransfer.setEnabled(false);
    popup.add(miCashing = new JMenuItem(BundleUtil.getLabel("Cashing.multiple.action.label")));
    miCashing.setEnabled(false);
    miCancelTransfer.addActionListener(this);
    miTransfer.addActionListener(this);

    tableView = new OrderLineTableView(tableModel, this);
    tableView.addPopupMenuListener(popup, dataCache);

    btPrint = new GemButton(BundleUtil.getLabel("Action.print.label"));
    btPrint.addActionListener(this);
    btCreate = new GemButton(BundleUtil.getLabel("Action.add.label"));
    btCreate.addActionListener(this);
    btModify = new GemButton(BundleUtil.getLabel("Action.modify.label"));
    btModify.addActionListener(this);
    btSuppress = new GemButton(BundleUtil.getLabel("Action.suppress.label"));
    btSuppress.addActionListener(this);

    dateStart = new DateFrField(new Date());
    dateEnd = new DateFrField(new Date());
    btLoad = new GemButton(BundleUtil.getLabel("Action.load.label"));
    btLoad.setPreferredSize(new Dimension(btLoad.getPreferredSize().width, dateStart.getPreferredSize().height));
    btLoad.addActionListener(this);
    btCurrentMonth = new GemButton(BundleUtil.getLabel("Action.current.month.label"));
    btCurrentMonth.setPreferredSize(new Dimension(btCurrentMonth.getPreferredSize().width, dateStart.getPreferredSize().height));
    btCurrentMonth.addActionListener(this);

    GemPanel header = new GemPanel();

    invoiceLineFilter = new JCheckBox(BundleUtil.getLabel("Invoice.lines.filter.label"));
    invoiceFilter = new OrderLineEditor.InvoiceLinesFilter(tableView);
    if (invoiceFilter.isHidden()) {
      invoiceLineFilter.setSelected(true);
      invoiceFilter.hideInvoiceLines(true, null);
    }
    invoiceLineFilter.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
        boolean h = e.getStateChange() == ItemEvent.SELECTED;
        invoiceFilter.hideInvoiceLines(h, null);
        invoiceFilter.savePrefs(h);
      }
    });

    header.add(new JLabel(BundleUtil.getLabel("Date.From.label")));
    header.add(dateStart);
    header.add(new JLabel(BundleUtil.getLabel("Date.To.label")));
    header.add(dateEnd);
    header.add(btLoad);
    header.add(btCurrentMonth);
    header.add(invoiceLineFilter);

    buttons = new GemPanel(new GridLayout(1, 4));
    buttons.add(btPrint);
    buttons.add(btModify);
    buttons.add(btCreate);
    buttons.add(btSuppress);

    GemPanel top = new GemPanel(new BorderLayout());
    top.add(menubar, BorderLayout.NORTH);
    top.add(header, BorderLayout.SOUTH);

    GemPanel bottom = new GemPanel(new BorderLayout());

    GemPanel pTotal = new GemPanel();
    totalLabel = new JLabel(BundleUtil.getLabel("Total.label"));
    totalField = new GemField(10);
    totalField.setEditable(false);
    tableView.addListSelectionListener(totalField);

    pTotal.add(totalLabel);
    pTotal.add(totalField);

    bottom.add(pTotal, BorderLayout.NORTH);
    bottom.add(buttons, BorderLayout.CENTER);

    setLayout(new BorderLayout());
    add(top, BorderLayout.NORTH);
    add(tableView, BorderLayout.CENTER);
    add(bottom, BorderLayout.SOUTH);
  }

  @Override
  public void tableChanged(TableModelEvent evt) {
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    Object src = evt.getSource();

    if ("orderline.view.cancel".equals(evt.getActionCommand())) {
      closeEditorView();
    } else if (src == cbCheckPayment) {
      setMultipleCashingOption();
    } else if ("orderline.view.validate".equals(evt.getActionCommand())) {
      assert (dlg != null);
      if (dlg.isValidation()) {
        try {
          assert (dlg != null);
          OrderLine e = dlg.getOrderLine();
          if (e.getId() == 0) {
            create(e);
          } else {
            update(e);
          }
        } catch (ParseException ex) {
          GemLogger.log(ex.getMessage());
        } finally {
          closeEditorView();
        }
      }
    }

    // next actions will be disabled if dlg is opened
    if (dlg != null) {
      MessagePopup.warning(this, MessageUtil.getMessage("orderline.editing.warning"));
      return;
    }

    if (src == btPrint) {
      try {
        // printing of all orderlines, regardless of selection
        AccountUtil.print(tableView.getTable());
      } catch (PrinterException ex) {
        System.err.format("Cannot print %s%n", ex.getMessage());
      }
    } else if (src == btCreate) {
      dialogCreation();
    } else if (src == btModify) {
      dialogModification();
    } else if (src == btSuppress) {
      dialogSuppression();
    } else if (src == miCancelTransfer && dataCache.authorize("Accounting.transfer.auth")) {
      cancelTransfer();
    } else if (src == miTransfer && dataCache.authorize("Accounting.transfer.auth")) {
      setTransfer();
    } else if (src == miCashing && dataCache.authorize("Payment.multiple.modification.auth")) {
      multipleCashing(); // encaissement multiple
    } else if (src == btLoad) {
      load();
    } else if (src == btCurrentMonth) {
      loadCurrentMonth();
    }
  }

  public void load() {
    totalField.setText(null);
    DateFr debut = dateStart.getDateFr();
    DateFr fin = dateEnd.getDateFr();
    String query = "WHERE echeance >= '" + debut + "' AND echeance <= '" + fin + "'";
    tableModel.load(OrderLineIO.find(query, dc));
  }

  public void dialogSuppression() {
    int n = tableView.getSelectedRow();
    if (n < 0) {
      JOptionPane.showMessageDialog(this,
              MessageUtil.getMessage("no.payment.selected"),
              MessageUtil.getMessage("payment.delete.exception"),
              JOptionPane.ERROR_MESSAGE);
      return;
    }
    if (!dataCache.authorize("OrderLine.suppression.auth")) {
      MessagePopup.warning(this, MessageUtil.getMessage("rights.exception"));
      return;
    }
    OrderLine e = tableView.getElementAt(n);

    if (e.isTransfered()) {
      JOptionPane.showMessageDialog(this,
              MessageUtil.getMessage("payment.transfer.warning"),
              MessageUtil.getMessage("delete.exception.info"),
              JOptionPane.ERROR_MESSAGE);
      return;
    }
    GemAmount amount = new GemAmount(e.getAmount());
    if (!MessagePopup.confirm(this,
            MessageUtil.getMessage("payment.delete.confirmation",
                    new Object[]{amount, e.getCurrencyCode(), e.getDate()}),
            MessageUtil.getMessage("payment.delete.label"))) {
      return;
    }

    try {
      OrderLineIO.delete(e, dc);
      tableView.removeElementAt(n);
    } catch (SQLException ex) {
      GemLogger.logException(MessageUtil.getMessage("delete.error"), ex, this);
    }
  }

  /**
   * Mark the selected rows as not transfered. Attribute transfer in database is
   * also updated.
   */
  private void cancelTransfer() {
    if (!MessagePopup.confirm(parent, MessageUtil.getMessage("payment.update.confirmation"))) {
      return;
    }
    int[] rows = tableView.getSelectedRows();
    try {
      for (int i = 0; i < rows.length; i++) {
        OrderLine ol = tableView.getElementAt(rows[i]);
        if (ol.isTransfered()) {
          ol.setTransfered(false);
          OrderLineIO.transfer(ol, dc);
          tableView.setElementAt(ol, rows[i]);
        }
      }
    } catch (SQLException e) {
      GemLogger.logException(e);
    }
  }

  /**
   * Sets tranferred selected order line.
   * In most cases, the status "transferred" should not be editable.
   * This method allows to force this option for a single order line.
   */
  private void setTransfer() {
    int row = tableView.getSelectedRow();
    OrderLine ol = tableView.getElementAt(row);
    try {
      if (!ol.isTransfered()) {
        ol.setTransfered(true);
        OrderLineIO.transfer(ol, dc);
        tableView.setElementAt(ol, row);
      }
    } catch (SQLException e) {
      GemLogger.log(e.getMessage());
    }
  }

  /**
   * Update selected rows to "paid".
   */
  private void multipleCashing() {
    if (!MessagePopup.confirm(parent, MessageUtil.getMessage("payment.multiple.update.confirmation"))) {
      return;
    }
    int[] rows = tableView.getSelectedRows();
    try {
      for (int i = 0; i < rows.length; i++) {
        OrderLine ol = tableView.getElementAt(rows[i]);
        if (!ol.isPaid()) {
          ol.setPaid(true);
          OrderLineIO.paid(ol, dc);
          tableView.setElementAt(ol, rows[i]);
        }
      }
    } catch (SQLException e) {
      GemLogger.logException(e);
    }
  }

  public void dialogModification() {
    if (dlg != null) {
      MessagePopup.warning(this, MessageUtil.getMessage("orderline.editing.warning"));
      return;
    }
    int[] rows = tableView.getSelectedRows();
    if (rows.length > 1) {
      modify(rows);
    } else {
      int n = tableView.getSelectedRow();
      if (n < 0) {
        JOptionPane.showMessageDialog(this,
                MessageUtil.getMessage("no.payment.selected"),
                MessageUtil.getMessage("update.error"),
                JOptionPane.ERROR_MESSAGE);
        return;
      }
      OrderLine e = tableView.getElementAt(n);
      if (e.isTransfered()) {
        if (!dataCache.authorize("OrderLine.transferred.modification.auth")) {
          MessagePopup.warning(dlg, MessageUtil.getMessage("rights.exception"));
          return;
        }
      }
      try {
        dlg = new OrderLineView(parent, BundleUtil.getLabel("Order.line.modification"), dataCache, false);
        dlg.addActionListener(this);
        dlg.setOrderLine(e);
        dlg.setIdEditable(true);
        dlg.setVisible(true);
      } catch (SQLException ex) {
        GemLogger.logException(BundleUtil.getLabel("Order.line.modification"), ex, this);
        closeEditorView();
      }
    }
  }

  private void update(OrderLine e) {
    try {
      OrderLineIO.update(e, dc);
      int n = tableView.getRowIndexByModel(e);
      if (n >= 0) {
        tableView.setElementAt(e, n);
      }
    } catch (SQLException ex) {
      GemLogger.logException("modification échéance", ex, this);
    }
  }

  /**
   * One shot modification of several order lines. Modification is active only
   * for date and document number.
   *
   * @param rows
   * @since 2.2.p
   */
  private void modify(int[] rows) {
    OrderLine e = tableView.getElementAt(rows[0]);
    try {
      OrderLineView dlg = new OrderLineView(parent, BundleUtil.getLabel("Order.line.modification"), dataCache, true);
      dlg.setOrderLine(e);
      dlg.setEditable(false);
      dlg.setVisible(true);
      if (dlg.isValidation()) {
        dc.setAutoCommit(false);
        OrderLine u = dlg.getOrderLine();
        for (int i = 0; i < rows.length; i++) {
          OrderLine r = tableView.getElementAt(rows[i]);
          r.setDate(u.getDate());
          r.setDocument(u.getDocument());
          OrderLineIO.update(r, dc);
          tableView.setElementAt(r, rows[i]);
        }
      }
      dc.commit();
      dlg.dispose();
    } catch (SQLException ex) {
      dc.rollback();
      GemLogger.logException(MessageUtil.getMessage("update.error"), ex, this);
    } catch (ParseException pe) {
      System.err.println(pe.getMessage());
    } finally {
      dc.setAutoCommit(true);
    }
  }

  /**
   * Activation / Desactivation of the option "Payment.multiple.modification.auth".
   * Sets the option "Payment.multiple.modification.auth" on menu.
   */
  public void setMultipleCashingOption() {
    if (cbCheckPayment.getState()) {
      miCashing.addActionListener(this);
      miCashing.setEnabled(true);
    } else {
      miCashing.removeActionListener(this);
      miCashing.setEnabled(false);
    }
  }

  public void dialogCreation() {
    OrderLine e = null;
    int n = tableView.getSelectedRow();
    if (n >= 0) {
      e = new OrderLine(tableView.getElementAt(n));
    }
//    OrderLineView dlg = null;
    try {
      if (dlg != null) {
        MessagePopup.warning(this, MessageUtil.getMessage("orderline.editing.warning"));
        return;
      }
      dlg = new OrderLineView(parent, MessageUtil.getMessage("payment.add.label"), dataCache, false);
      dlg.addActionListener(this);
      if (e != null) {
        dlg.setOrderLine(e);
      }
      dlg.setIdEditable(true);
      dlg.setVisible(true);
    } catch (SQLException sqe) {
      GemLogger.logException(sqe);
      closeEditorView();
    }

    //*************************
//      if (dlg.isValidation()) {
//        e = dlg.getOrderLine();
//        OrderLine c = AccountUtil.createEntry(e, dc);
//        tableModel.addElement(e);
//        if (c != null) {
//          tableModel.addElement(c);
//        }
//      }
//
//    } catch (ParseException px) {
//      System.err.println(px.getMessage());
//    } catch (SQLException ex) {
//      GemLogger.logException(MessageUtil.getMessage("payment.add.exception"), ex, this);
//    } finally {
//      if (dlg != null) {
//        dlg.dispose();
//      }
//    }
  }

  private void create(OrderLine e) {
    try {
      dc.setAutoCommit(false);
      OrderLine c = AccountUtil.createEntry(e, dc);
      dc.commit();
      tableModel.addElement(e);
      if (c != null) {
        tableModel.addElement(c);
      }
    } catch (SQLException ex) {
      dc.rollback();
      GemLogger.logException(MessageUtil.getMessage("payment.add.exception"), ex, this);
    } finally {
      dc.setAutoCommit(true);
    }
  }

  /**
   * Loading of the orderlines of current month.
   */
  private void loadCurrentMonth() {

    DateFr b = new DateFr(new Date());
    b.setDay(1);
    dateStart.set(b);
    b.incMonth(1);
    b.decDay(1);
    dateEnd.set(b);
    load();
  }

  private void closeEditorView() {
    if (dlg != null) {
      dlg.dispose();
      dlg = null;
    }
  }

}

/*
 * @(#)OrderLineDlg.java	2.8.a 01/04/13
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
package net.algem.accounting;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import net.algem.util.module.GemModule;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemField;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.MessagePopup;

/**
 * Global view of orderlines.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.a
 * @since 1.0a 07/07/1999
 */
public class OrderLineDlg
        extends JDialog
        implements ActionListener, TableModelListener
{

  private Frame parent;
  private DataConnection dc;
  private DataCache dataCache;
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
  private GemPanel boutons;
  private JLabel totalLabel;
  private GemField totalField;
  private NumberFormat nf = NumberFormat.getInstance(Locale.FRANCE);
  private JPopupMenu popup;
  private JMenuItem miTransfer;

  public OrderLineDlg(GemDesktop desktop, OrderLineTableModel tableModel) {
    super(desktop.getFrame(), BundleUtil.getLabel("Action.schedule.payment.label"));
    parent = desktop.getFrame();
    this.dataCache = desktop.getDataCache();
    this.dc = desktop.getDataCache().getDataConnection();
    this.tableModel = tableModel;
  }

  public void init() {
    
    tableView = new OrderLineTableView(tableModel, this);
    popup = new JPopupMenu();
    popup.add(miTransfer = new JMenuItem(BundleUtil.getLabel("Transfer.cancel.label")));
    miTransfer.addActionListener(this);
    tableView.addPopupMenuListener(popup);

    btPrint = new GemButton(BundleUtil.getLabel("Action.print.label"));
    btPrint.addActionListener(this);
    btCreate = new GemButton(BundleUtil.getLabel("Action.add.label"));
    btCreate.addActionListener(this);
    btModify = new GemButton(BundleUtil.getLabel("Action.modify.label"));
    btModify.addActionListener(this);
    btSuppress = new GemButton(BundleUtil.getLabel("Action.suppress.label"));
    btSuppress.addActionListener(this);

    btLoad = new GemButton(BundleUtil.getLabel("Action.load.label"));
    btLoad.addActionListener(this);
    btCurrentMonth = new GemButton(BundleUtil.getLabel("Action.current.month.label"));
    btCurrentMonth.addActionListener(this);
    dateStart = new DateFrField(new Date());
    dateEnd = new DateFrField(new Date());
    GemPanel header = new GemPanel();
    header.add(new JLabel(BundleUtil.getLabel("Date.From.label")));
    header.add(dateStart);
    header.add(new JLabel(BundleUtil.getLabel("Date.To.label")));
    header.add(dateEnd);
    header.add(btLoad);
    header.add(btCurrentMonth);

    boutons = new GemPanel(new GridLayout(1, 4));
    boutons.add(btPrint);
    boutons.add(btModify);
    boutons.add(btCreate);
    boutons.add(btSuppress);

    GemPanel bottom = new GemPanel(new BorderLayout());

    GemPanel pTotal = new GemPanel();
    totalLabel = new JLabel(BundleUtil.getLabel("Total.label"));
    totalField = new GemField(10);
    totalField.setEditable(false);
    tableView.addListSelectionListener(totalField);

    pTotal.add(totalLabel);
    pTotal.add(totalField);

    bottom.add(pTotal, BorderLayout.NORTH);
    bottom.add(boutons, BorderLayout.CENTER);

    add(header, BorderLayout.NORTH);
    add(tableView, BorderLayout.CENTER);
    add(bottom, BorderLayout.SOUTH);

    setSize(GemModule.XL_SIZE);
    setLocation(70, 30);
  }

  @Override
  public void tableChanged(TableModelEvent evt) {
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    Object src = evt.getSource();

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
    } else if (src == miTransfer && dataCache.authorize("Accounting.transfer")) {
      cancelTransfer();
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
            new Object[] {amount,e.getCurrencyCode(),e.getDate()}),
            MessageUtil.getMessage("payment.delete.label"))) {
      return;
    }

    try {
      OrderLineIO.delete(e, dc);
      tableView.removeElementAt(n);
    } catch (Exception ex) {
      GemLogger.logException(MessageUtil.getMessage("delete.error"), ex, this);
    }
  }
  
  /**
   * Mark the selected rows as not transfered.
   * Attribute transfer in database is also updated.
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

  public void dialogModification() {
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
      try {
        OrderLineView dlg = new OrderLineView(parent, BundleUtil.getLabel("Order.line.modification"), dataCache);
        dlg.setOrderLine(e);
        dlg.setIdEditable(true);
        dlg.setVisible(true);
        if (dlg.isValidation()) {

          e = dlg.getOrderLine();
          OrderLineIO.update(e, dc);
          tableView.setElementAt(e, n);

        }
        dlg.dispose();
      } catch (Exception ex) {
        GemLogger.logException("modification échéance", ex, this);
      }

    }
  }

  /**
   * One shot modification of several order lines.
   * Modification is active only for date and document number.
   * 
   * @param rows
   * @since 2.2.p
   */
  private void modify(int[] rows) {
    OrderLine e = tableView.getElementAt(rows[0]);
    try {
      OrderLineView dlg = new OrderLineView(parent, BundleUtil.getLabel("Order.line.modification"), dataCache);
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

  public void dialogCreation() {
    OrderLine e = null;
    int n = tableView.getSelectedRow();
    if (n >= 0) {
      e = new OrderLine(tableView.getElementAt(n));
    }
    OrderLineView dlg = null;
    try {
      dlg = new OrderLineView(parent, MessageUtil.getMessage("payment.add.label"), dataCache);
      if (e != null) {
        dlg.setOrderLine(e);
      }
      dlg.setIdEditable(true);
      dlg.setVisible(true);
      if (dlg.isValidation()) {
        e = dlg.getOrderLine();
        OrderLine c = AccountUtil.createEntry(e, dc);
        tableModel.addElement(e);
        if (c != null) {
          tableModel.addElement(c);
        }
      }
      
    } catch (ParseException px) {
      System.err.println(px.getMessage());
    } catch (SQLException ex) {
      GemLogger.logException(MessageUtil.getMessage("payment.add.exception"), ex, this);
    } finally {
      if (dlg != null) {
        dlg.dispose();
      }
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

}

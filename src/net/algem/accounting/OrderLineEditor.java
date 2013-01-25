/*
 * @(#)OrderLineEditor.java	2.7.a 05/12/12
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import net.algem.billing.Invoice;
import net.algem.billing.InvoiceCreateEvent;
import net.algem.billing.InvoiceUpdateEvent;
import net.algem.util.BundleUtil;
import net.algem.util.GemCommand;
import net.algem.util.GemLogger;
import net.algem.util.MessageUtil;
import net.algem.util.event.GemEvent;
import net.algem.util.event.GemEventListener;
import net.algem.util.module.FileView;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.*;

/**
 * Editor of order lines.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.a
 * @since 1.0a 07/07/1999
 */
public class OrderLineEditor
        extends FileTab
        implements ActionListener, TableModelListener, GemEventListener {

  private OrderLineTableModel tableModel;
  private int memberId, payerId;
  private JTextField payerName;
  private OrderLineTableView table;
  private GemButton btCreate;
  private GemButton btSuppress;
  private GemButton btModify;
  private GemButton btYear;
  private JLabel totalLabel;
  private GemField totalField;
  private JToggleButton btFilter;
  private GemButton btInvoice;
  private GemButton btQuotation;
  private JPanel buttons;
  private ActionListener actionListener;
  private List<OrderLine> invoiceSelection;
  //private NumberFormat nf = NumberFormat.getInstance(Locale.FRANCE);
  private static final String NO_PAYMENT_SELECTED = MessageUtil.getMessage("no.payment.selected");
  private static final String PAYMENT_UPDATE_EXCEPTION = MessageUtil.getMessage("update.exception.info");
  private static final String PAYMENT_CREATE_EXCEPTION = MessageUtil.getMessage("payment.add.exception");
  private static final String PAYMENT_DELETE_EXCEPTION = MessageUtil.getMessage("payment.delete.exception");

  public OrderLineEditor(GemDesktop _desktop, OrderLineTableModel _tableModel) {
    super(_desktop);
    tableModel = _tableModel;

    table = new OrderLineTableView(tableModel, this);

    btQuotation = new GemButton(BundleUtil.getLabel("Quotation.label"));
    btQuotation.addActionListener(this);
    btInvoice = new GemButton(BundleUtil.getLabel("Invoice.label"));
    btInvoice.addActionListener(this);

    btCreate = new GemButton(GemCommand.ADD_CMD);
    btCreate.addActionListener(this);
    btModify = new GemButton(GemCommand.MODIFY_CMD);
    btModify.addActionListener(this);
    btSuppress = new GemButton(GemCommand.DELETE_CMD);
    btSuppress.addActionListener(this);
    btYear = new GemButton(String.valueOf(dataCache.getStartOfYear().getYear()));
    btYear.addActionListener(this);

    btFilter = new JToggleButton(MessageUtil.getMessage("current.year.label"));
    btFilter.addActionListener(this);

    payerName = new JTextField(30);
    payerName.setEditable(false);
    JPanel entete = new JPanel();
    entete.add(new JLabel(MessageUtil.getMessage("payer.payment.label")));
    entete.add(payerName);

    GemPanel footer = new GemPanel(new BorderLayout());
    buttons = new GemPanel();

    buttons.add(btQuotation);
    buttons.add(btInvoice);
    buttons.add(btFilter);
    buttons.add(btModify);
    buttons.add(btCreate);
    buttons.add(btSuppress);

    GemPanel pTotal = new GemPanel();
    totalLabel = new JLabel(BundleUtil.getLabel("Total.label"));
    totalField = new GemField(10);
    totalField.setEditable(false);
    table.addListSelectionListener(totalField);

    pTotal.add(totalLabel);
    pTotal.add(totalField);
    footer.add(pTotal, BorderLayout.NORTH);
    footer.add(buttons, BorderLayout.CENTER);

    setLayout(new BorderLayout());
    add(entete, BorderLayout.NORTH);
    add(table, BorderLayout.CENTER);
    add(footer, BorderLayout.SOUTH);
    setLocation(70, 30);
  }

  public void setLabel(String s) {
    payerName.setText(s);
  }

  public void setPayerId(int _id) {
    payerId = _id;
  }

  public void setMemberId(int _id) {
    memberId = _id;
  }

  @Override
  public boolean isLoaded() {
    return true;
  }

  @Override
  public void load() {
  }

  @Override
  public void tableChanged(TableModelEvent evt) {
  }

  @Override
  public void actionPerformed(ActionEvent evt) {

    Object src = evt.getSource();

    if (src == btCreate) {
      dialogCreation();
    } else if (src == btModify) {
      dialogModification();
    } else if (src == btSuppress) {
      dialogSuppression();
    } else if (src == btFilter) {
      totalField.setText(null);
      if (btFilter.isSelected()) {
        table.filterByDate(dataCache.getStartOfYear());
      } else {
        table.filterByDate(null);
      }
    } else if (src == btInvoice) {
      createInvoice();
    } else if (src == btQuotation) {
      createQuotation();
    }
  }

  public void addActionListener(ActionListener listener) {
    actionListener = listener;
  }

  public void dialogSuppression() {
    int n = table.getSelectedRow();
    if (n < 0) {
      JOptionPane.showMessageDialog(this,
              NO_PAYMENT_SELECTED,
              MessageUtil.getMessage("delete.error"),
              JOptionPane.ERROR_MESSAGE);
      return;
    }
    OrderLine e = table.getElementAt(n);

    if (e.isTransfered()) {
      JOptionPane.showMessageDialog(this,
              MessageUtil.getMessage("payment.transfer.warning"),
              MessageUtil.getMessage(PAYMENT_DELETE_EXCEPTION),
              JOptionPane.ERROR_MESSAGE);
      return;
    }
    Object params[] = {new Double(e.getAmount() / 100), e.getCurrencyCode(), e.getDate()};
    if (!MessagePopup.confirm(this,
            MessageUtil.getMessage("payment.delete.confirmation", params),
            MessageUtil.getMessage("payment.delete.label"))) {
      return;
    }

    try {
      OrderLineIO.delete(e, dc);
      table.removeElementAt(n);
      // suppression de l'échéance de tiers s'il y a lieu
      if (AccountUtil.isPersonalAccount(e.getAccount())) {
        OrderLine t = OrderLineIO.find(e, dc);
        if (t != null) {
          OrderLineIO.delete(t, dc);
          int idx = tableModel.getData().indexOf(t);
          if (idx != -1) {
            tableModel.removeElementAt(idx);//ici idx est l'index du modele (pas de conversion nécessaire)
          }
        }
      }

    } catch (Exception ex) {
      GemLogger.logException(PAYMENT_DELETE_EXCEPTION, ex, this);
    }
  }

  public void dialogModification() {

    OrderLineView dlg = null;
    int n = table.getSelectedRow();
    if (n < 0) {
      JOptionPane.showMessageDialog(this,
              NO_PAYMENT_SELECTED,
              PAYMENT_UPDATE_EXCEPTION,
              JOptionPane.ERROR_MESSAGE);
      return;
    }

    OrderLine e = table.getElementAt(n);
    if (e.isTransfered()) {
      if (!MessagePopup.confirm(this,
              MessageUtil.getMessage("payment.update.confirmation"),
              BundleUtil.getLabel("Warning.label"))) {
        return;
      }
    }
    try {
      dlg = new OrderLineView(desktop.getFrame(), MessageUtil.getMessage("payment.update.label"), dc);
      dlg.setOrderLine(e);
      dlg.setInvoiceEditable(false);
      dlg.setVisible(true);
      if (dlg.isValidation()) {
        e = dlg.getOrderLine();
        OrderLineIO.update(e, dc);
        table.setElementAt(e, n);
      }
    } catch (Exception ex) {
      GemLogger.logException(PAYMENT_UPDATE_EXCEPTION, ex, this);
    }
    if (dlg != null) {
      dlg.dispose();
    }
  }

  public void dialogCreation() {
    OrderLineView dlg = null;
    OrderLine e = null;
    int n = table.getSelectedRow();
    if (n >= 0) {
      e = new OrderLine(table.getElementAt(n));
      e.setPaid(false); // echeance remise à non payée pour la copie
      e.setInvoice(null);
    }
    try {
      dlg = new OrderLineView(desktop.getFrame(), MessageUtil.getMessage("payment.add.label"), dc);
      if (e != null) {
        dlg.setOrderLine(e);
      } else {
        if (payerId != 0) {
          dlg.setPayerId(payerId);
        }
        if (memberId != 0) {
          dlg.setMemberId(memberId);
        }
      }
      dlg.setVisible(true);
      if (dlg.isValidation()) {
        dc.setAutoCommit(false);
        e = dlg.getOrderLine();
        //les échéances de facturation ont un montant négatif par défaut
        if (ModeOfPayment.FAC.toString().equals(e.getModeOfPayment())) {
          e.setAmount(-Math.abs(e.getAmount()));
        }
        OrderLine c = AccountUtil.createEntry(e, dc);
        dc.commit();
        tableModel.addElement(e);
        if (c != null) {
          tableModel.addElement(c);
        }
      }
    } catch (Exception ex) {
      dc.rollback();
      GemLogger.logException(PAYMENT_CREATE_EXCEPTION, ex, this);
    } finally {
      dc.setAutoCommit(true);
      if (dlg != null) {
        dlg.dispose();
      }
    }
  }

  @Override
  public void postEvent(GemEvent evt) {

    if (evt instanceof InvoiceCreateEvent) {
      Invoice f = ((InvoiceCreateEvent) evt).getInvoice();
      Vector<OrderLine> data = tableModel.getData();
      for (OrderLine c : f.getOrderLines()) {
        if (c.getPayer() != f.getPayer()) {
          continue;
        }
        if (!data.contains(c)) {
          tableModel.addElement(c); // on ajoute l'élément au modèle
        } else {
          for (int i = 0, n = data.size(); i < n; i++) {
            if (data.elementAt(i).equals(c)) {
              data.setElementAt(c, i); // on update l'élément sinon
              break;
            }
          }
        }
      }
      tableModel.fireTableDataChanged();

    } else if (evt instanceof InvoiceUpdateEvent) {
      Invoice f = ((InvoiceUpdateEvent) evt).getInvoice();
      // on récupère les données de l'échéancier
      Vector<OrderLine> data = tableModel.getData();
      // on enlève de l'échéancier les échéances dont le numéro de facture = facture à mettre à jour
      data.removeAll(AccountUtil.getInvoiceOrderLines(data, f.getNumber()));
      // on ajoute à l'échéancier les échéances modifiées de la facture mise à jour
      data.addAll(f.getOrderLines());
      // on met à jour l'affichage de la table
      tableModel.fireTableDataChanged();

    }

  }

  /**
   * Retrieves the current selection of orderlines.
   *
   * @return a list of orderlines
   */
  public List<OrderLine> getInvoiceSelection() {
    return invoiceSelection;
  }

  /**
   * Creates an invoice from a selection of order lines.
   * 
   * Several orderlines may be selected but at least one of them must have a valid
   * mode of payment (generally FAC) and none of them is already associated
   * with an invoice.
   * In case of null selection, a blank invoice is created.
   */
  private void createInvoice() {

    int[] rows = table.getSelectedRows();
    invoiceSelection = new ArrayList<OrderLine>();
    int nullItems = 0;

    for (int i = 0; i < rows.length; i++) {
      OrderLine e = table.getElementAt(rows[i]);
      if (ModeOfPayment.FAC.toString().equals(e.getModeOfPayment())) {
        nullItems++;
      }
      // la création de facture n'est possible que si l'échéance ne comporte encore aucun numéro de facture
      if (e.getInvoice() == null || e.getInvoice().isEmpty()) {
        invoiceSelection.add(e);
      } else {
        MessagePopup.warning(this, MessageUtil.getMessage("existing.invoice.selection.warning"));
        return;
      }
    }

    if (!invoiceSelection.isEmpty() && nullItems == 0) { // aucune échéance de facturation
      MessagePopup.warning(this, MessageUtil.getMessage("invalid.invoice.selection.warning"));
      return;
    }

    if (actionListener != null) {
      actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, FileView.INVOICE_TAB_TITLE));
    }
  }

  /**
   * Creates a quote.
   */
  private void createQuotation() {
    if (actionListener != null) {
      actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, FileView.ESTIMATE_TAB_TITLE));
    }
  }

  /**
   * Creates a quote from a selection of order lines.
   */
  private void createQuotationFromSelection() {

    int[] rows = table.getSelectedRows();
    invoiceSelection = new ArrayList<OrderLine>();

    boolean fact = false;
    boolean other = false;
    for (int i = 0; i < rows.length; i++) {
      OrderLine e = table.getElementAt(rows[i]);
      if (ModeOfPayment.FAC.toString().equals(e.getModeOfPayment())) {
        fact = true;
      } else {
        other = true;
      }
      // la création de facture n'est possible que si l'échéance ne comporte encore aucun numéro de facture
      if (e.getInvoice() == null || e.getInvoice().isEmpty()) {
        invoiceSelection.add(e);
      } else {
        MessagePopup.warning(this, MessageUtil.getMessage("existing.invoice.selection.warning"));
        return;
      }

    }
    // paiement seulement //OU paiement mais pas les deux
    if (!(fact)) {
      if (actionListener != null) {
        actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, FileView.ESTIMATE_TAB_TITLE));
      }
    } else {
      MessagePopup.warning(this, MessageUtil.getMessage("estimate.payment.selection.error"));
    }

  }
}

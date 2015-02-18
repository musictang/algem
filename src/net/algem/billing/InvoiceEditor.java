/*
 * @(#)InvoiceEditor.java 2.9.2.1 09/02/15
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;
import net.algem.accounting.AccountUtil;
import net.algem.accounting.OrderLine;
import net.algem.util.BundleUtil;
import net.algem.util.GemCommand;
import net.algem.util.GemLogger;
import net.algem.util.MessageUtil;
import net.algem.util.event.GemEventListener;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.FileTabDialog;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.MessagePopup;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.2.1
 * @since 2.3.a 07/02/12
 */
public class InvoiceEditor
        extends FileTabDialog
{

  protected BillingService service;
  protected InvoiceView view;
  protected ActionListener listener;
  protected GemEventListener gemListener;
  protected GemButton btPrint;
  protected GemButton btDuplicate;
  
  /** Mock object for edit detection and backup. */
  private Quote old;
  
  public InvoiceEditor(GemDesktop desktop, BillingService service, Quote quote) {

    this(desktop);
    this.service = service;
    old = quote;
    btCancel.setText(btCancel.getText()+"/"+BundleUtil.getLabel("Action.closing.label"));
    view = new InvoiceView(desktop, service);

    setLayout(new BorderLayout());
    addView();
   
  }

  protected void addView() {
    add(view, BorderLayout.CENTER);

    btPrint = new GemButton(GemCommand.PRINT_CMD);
    btPrint.addActionListener(this);
    
    btDuplicate = new GemButton(GemCommand.DUPLICATE_CMD);
    btDuplicate.addActionListener(this);

    buttons.add(btPrint,0);
    buttons.add(btDuplicate,1);

    add(buttons, BorderLayout.SOUTH);
  }

  public InvoiceEditor(GemDesktop desktop) {
    super(desktop);
  }

  public Vector<Invoice> find(int client) {
    return null;
  }
  
  /**
   * Resets items' id to 0 after sql failure.
   * Under some circumstances, sql error may be generated after inserting an
   * item and updating its id in corresponding object instance. So, it may be necessary
   * to reset this id to 0 before recreation. Else, the program will update this
   * item without creating first.
   * @param v invoice
   */
  private void resetItemsId(Invoice v) {
    for (InvoiceItem vi : v.getItems()) {
      Item i = vi.getItem();
      if (!i.isStandard()) {
        i.setId(0);
      }
    }
  }

  @Override
  public void validation() {
    Invoice v = (Invoice) view.get();

    if (v.getNumber() == null || v.getNumber().isEmpty()) {
      try {
        setTransfer(v);
        service.create(v);
        view.setId(v.getNumber()); // rafraîchissement du numéro
        desktop.postEvent(new InvoiceCreateEvent(v));
        
        backup(v);
        btDuplicate.setEnabled(true);
      } catch (SQLException e) {
        resetItemsId(v);
        GemLogger.logException(e);
      } catch (BillingException fe) {
        resetItemsId(v);
        MessagePopup.warning(this, MessageUtil.getMessage("invoicing.create.exception")+"\n"+fe.getMessage());
      }
    } else {
      Collection<OrderLine> col = v.getOrderLines();
      for (OrderLine o : col) {
        // pas de modification si les échéances de facturation (dont le compte est en classe 4) ont été déjà transférées
        if (o.isTransfered() && AccountUtil.isPersonalAccount(o.getAccount())) {
          MessagePopup.warning(this, MessageUtil.getMessage("invoice.modification.warning"));
          return;
        }
      }
      try {
        service.update(v);
        MessagePopup.information(view, MessageUtil.getMessage("modification.success.label"));
        desktop.postEvent(new InvoiceUpdateEvent(v));
        for (InvoiceItem billingItem : v.getItems()) {
          dataCache.update(billingItem.getItem());
        }
        backup(v);
      } catch (BillingException fe) {
        MessagePopup.warning(this, MessageUtil.getMessage("invoicing.update.exception")+"\n"+fe.getMessage());
      }
    }
    //cancel(); // COMMENTÉ POUR LAISSER OUVERTE LA FENETRE

  }

  /**
   * Marquage des échéances de facturation dont le compte est de classe 7.
   * @param inv facture
   */
  private void setTransfer(Invoice inv) {
    Collection<OrderLine> ol = inv.getInvoiceOrderLines();
    if (ol != null) {
      // ci-dessous, afin de ne pas transférer en compta les factures ne correspondant pas à un compte de tiers
      for (OrderLine e : ol) {
        if (AccountUtil.isRevenueAccount(e.getAccount())) {
          e.setTransfered(true);
        }
      }
    }
  }

  @Override
  public void cancel() {
    Invoice v = (Invoice) view.get();
    if (old != null && !old.equiv(v)) {
      if (!MessagePopup.confirm(this, MessageUtil.getMessage("invoice.cancel.editing.warning"))) {
        return;
      } else {
        v = (Invoice) old;
        if (gemListener != null) {
          gemListener.postEvent(new InvoiceUpdateEvent(v));
        }
      }
    }
    if (listener != null) {
      listener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "CtrlAbandonFacture"));
    }
    btDuplicate.setEnabled(true);
    view.reset();
    gemListener = null;
  }

  @Override
  public boolean isLoaded() {
    return old != null;
  }

  @Override
  public void load() {
    if (old == null || old.getNumber() == null) {
      btDuplicate.setEnabled(false);
    } 
    if (old != null) {
      view.set(old, service.getContact(old.getMember()), service.getContact(old.getPayer()));
      backup(old);
    }
  }
  
  private void backup(Quote q) {
    old = new Invoice(q);
    old.setUser(q.getUser());

    Iterator<InvoiceItem> it = q.getItems().iterator();
    while(it.hasNext()) {
      InvoiceItem invoiceItem = it.next();
      InvoiceItem t = new InvoiceItem();
      t.setQuantity(invoiceItem.quantity);
      t.setBillingId(invoiceItem.billingId);
      t.setOrderLine(invoiceItem.getOrderLine());
      Item i = invoiceItem.getItem();
      t.setItem(i.copy());
      old.addItem(t);
    } 
    old.setOrderLines(q.orderLines);
  }

  public void addActionListener(ActionListener listener) {
    this.listener = listener;
  }
  
  public void addGemEventListener(GemEventListener listener) {
    this.gemListener = listener;
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    super.actionPerformed(evt);
    if (evt.getSource() == btPrint) {
      view.print();
    } else if(evt.getSource() == btDuplicate) {
        Quote n = service.duplicate(view.get());
        if (n != null) {
          n.setUser(dataCache.getUser());
          n.setEditable(true);
          view.set(n, service.getContact(n.getMember()), service.getContact(n.getPayer()));
          view.setId("");
          btDuplicate.setEnabled(false);
        } else {
          MessagePopup.error(this, MessageUtil.getMessage("billing.duplication.error"));
        }
    }
  }
  
}

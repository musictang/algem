/*
 * @(#)InvoiceEditor.java 2.5.d 24/07/12
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Vector;
import net.algem.accounting.AccountUtil;
import net.algem.accounting.OrderLine;
import net.algem.util.GemCommand;
import net.algem.util.GemLogger;
import net.algem.util.MessageUtil;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.FileTabDialog;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.MessagePopup;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.5.d
 * @since 2.3.a 07/02/12
 */
public class InvoiceEditor
        extends FileTabDialog
{

  protected BillingServiceI service;
  protected InvoiceView view;
  protected ActionListener listener;
  protected GemButton btPrint;
  
  public InvoiceEditor(GemDesktop desktop, BillingServiceI service, Quote f) {

    this(desktop);
    
    this.service = service;
    view = new InvoiceView(f, desktop, service);
    view.setMember(service.getContact(f.getMember()));
    view.setPayer(service.getContact(f.getPayer()));
    
    setLayout(new BorderLayout());
    addView();
  }

  protected void addView() {
    add(view, BorderLayout.CENTER);

    btPrint = new GemButton(GemCommand.PRINT_CMD);
    btPrint.addActionListener(this);

    buttons.add(btPrint,0);

    add(buttons, BorderLayout.SOUTH);
  }

  public InvoiceEditor(GemDesktop desktop) {
    super(desktop);
  }

  public Vector<Invoice> find(int client) {
    return null;
  }

  @Override
  public void validation() {
    Invoice f = (Invoice) view.get();
    //f.setItems(view.getItems());

    if (f.getNumber() == null || f.getNumber().isEmpty()) {
      try {
        setTransfer(f);
        service.create(f);
        view.setId(f.getNumber()); // rafraîchissement du numéro
        //vueFacture.set(f);
        desktop.postEvent(new InvoiceCreateEvent(f));
      } catch (SQLException e) {
        GemLogger.logException(e);
      } catch (BillingException fe) {
        MessagePopup.warning(this, MessageUtil.getMessage("invoicing.create.exception")+"\n"+fe.getMessage());
      }
    } else {
      //System.out.println("InvoiceEditor update");
      Collection<OrderLine> nf = f.getOrderLines();
      for (OrderLine e : nf) {
        // pas de modification si les échéances de facturation (dont le compte est en classe 4) ont été déjà transférées
        if (e.isTransfered() && AccountUtil.isPersonalAccount(e.getAccount())) {
          MessagePopup.warning(this, MessageUtil.getMessage("invoice.modification.warning"));
          return;
        }
      }
      try {
        service.update(f);
        MessagePopup.information(view, MessageUtil.getMessage("modification.confirmation.label"));
        desktop.postEvent(new InvoiceUpdateEvent(f));
      } catch (BillingException fe) {
        MessagePopup.warning(this, MessageUtil.getMessage("invoicing.update.exception")+"\n"+fe.getMessage());
      }
    }

    //abandon(); // COMMENTÉ POUR LAISSER OUVERTE LA FENETRE

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
    if (listener != null) {
      listener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "CtrlAbandonFacture"));
    }
    view.reset();
  }

  @Override
  public boolean isLoaded() {
    return true;
  }

  @Override
  public void load() {
  }

  public void addActionListener(ActionListener listener) {
    this.listener = listener;
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    super.actionPerformed(evt);
    if (evt.getSource() == btPrint) {
      view.print();
    }
  }
  
}

/*
 * @(#)HistoInvoice 2.7.a 14/01/13
 *
 * Copyright (c) 1999-2012 Musiques Tangentes All Rights Reserved.
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
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.List;
import java.util.Vector;
import net.algem.util.GemCommand;
import net.algem.util.MessageUtil;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.FileTabDialog;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.MessagePopup;

/**
 * Controller for a list of invoices.
 * 
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.a
 * @since 2.3.a 14/02/12
 */
public class HistoInvoice
        extends FileTabDialog
{

  protected InvoiceListCtrl invoiceListCtrl;
  protected CardLayout layout;
  protected ActionListener actionListener;
  protected static final String card0 = "histo";
  protected static final String card1 = "invoice";

  public <F extends Quote> HistoInvoice(GemDesktop desktop, List<F> fcl) {
    super(desktop);
    btValidation.setText(GemCommand.VIEW_EDIT_CMD);
    btCancel.setText(GemCommand.CLOSE_CMD);
    load(fcl);
    setLayout(new CardLayout());
    GemPanel histo = new GemPanel(new BorderLayout());
    
    histo.add(invoiceListCtrl, BorderLayout.CENTER);
    histo.add(buttons, BorderLayout.SOUTH);
    add(histo, card0);
  }


  @Override
  public boolean isLoaded() {
    return invoiceListCtrl != null;
  }

  @Override
  public void load() {
    
  }

  @Override
  public <T extends Object> void load(Collection<T> c) {

    invoiceListCtrl = new InvoiceListCtrl(false, new BillingService(dataCache));
    if (c != null) {
      invoiceListCtrl.loadResult(new Vector<T>(c));
    }
  }

  @Override
  public void validation() {

    int n = 0;
    try {
      n = invoiceListCtrl.getSelectedIndex();
    } catch(IndexOutOfBoundsException ix) {
      MessagePopup.warning(this, MessageUtil.getMessage("no.line.selected"));
      return;
    }
    Invoice f = (Invoice) invoiceListCtrl.getElementAt(n);
    InvoiceEditor fe = new InvoiceEditor(desktop, new BillingService(dataCache), f);
    fe.addActionListener(this);

    add(fe, card1);
    layout = (CardLayout) getLayout();
    layout.show(this, card1);

  }

  @Override
  public void cancel() {
    actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "HistoFacture.Abandon"));
  }
  
  @Override
  public void actionPerformed(ActionEvent e) {
    super.actionPerformed(e);
    if (e.getActionCommand().equals("CtrlAbandonFacture")) {
      layout.show(this, card0);
    }   
  }

  public void addActionListener(ActionListener l) {
    this.actionListener = l;
  }
}

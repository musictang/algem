/*
 * @(#)HistoQuote 2.8.w 05/09/14
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

package net.algem.billing;

import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.util.List;
import net.algem.util.MessageUtil;
import net.algem.util.event.GemEvent;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.MessagePopup;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.w
 * @since 2.4.d 07/06/12
 */
public class HistoQuote 
        extends HistoInvoice
{

  public <F extends Quote> HistoQuote(GemDesktop desktop, List<F> fcl) {
    super(desktop, fcl);
    load();
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
    Quote q = (Quote) invoiceListCtrl.getElementAt(n);
    QuoteEditor qeditor = new QuoteEditor(desktop, new BasicBillingService(dataCache), q);
    qeditor.addActionListener(this);
    qeditor.load();
    add(qeditor, card1);
    layout = (CardLayout) getLayout();
    layout.show(this, card1);
   }

    @Override
  public void cancel() {
    actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "HistoDevis.Abandon"));
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    super.actionPerformed(e);
    if (e.getActionCommand().equals("CtrlAbandonDevis")) {
      layout.show(this, card0);
    }
  }
  
   @Override
  public void postEvent(GemEvent evt) {
    if (evt instanceof InvoiceEvent) {
      if (evt.getOperation() == GemEvent.MODIFICATION) {
        invoiceListCtrl.reload(((QuoteEvent) evt).getQuote());
      } else if (evt.getOperation() == GemEvent.CREATION) {
        invoiceListCtrl.addRow(((QuoteEvent) evt).getQuote());
      }
    }
  }
}

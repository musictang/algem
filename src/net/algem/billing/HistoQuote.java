/*
 * @(#)HistoQuote 2.9.4.7 15/06/15
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

import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.logging.Level;
import net.algem.util.BundleUtil;
import net.algem.util.GemCommand;
import net.algem.util.GemLogger;
import net.algem.util.MessageUtil;
import net.algem.util.event.GemEvent;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.MessagePopup;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.7
 * @since 2.4.d 07/06/12
 */
public class HistoQuote
        extends HistoInvoice
{

  private GemButton btDelete;

  public <Q extends Quote> HistoQuote(GemDesktop desktop, BillingService service) throws SQLException {
    super(desktop, service);
    btDelete = new GemButton(GemCommand.DELETE_CMD);
    btDelete.addActionListener(this);
    buttons.add(btDelete, 0);
  }

  @Override
  public void validation() {
    int n = getSelectedQuoteIndex();
    if (n < 0) {
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

  private int getSelectedQuoteIndex() {
    try {
      return invoiceListCtrl.getSelectedIndex();
    } catch (IndexOutOfBoundsException ix) {
      return -1;
    }
  }

  @Override
  public void cancel() {
    actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "HistoDevis.Abandon"));
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    Object src = e.getSource();
    String cmd = e.getActionCommand();
    if (src == btValidation) {
      validation();
    } else if (src == btCancel) {
      cancel();
    } else if (src == btDelete) {
      int n = getSelectedQuoteIndex();
      if (n >= 0) {
        try {
          service.delete((Quote) invoiceListCtrl.getElementAt(n));
          load(idper);
        } catch (SQLException ex) {
          GemLogger.logException(ex);
        }
      } else {
        MessagePopup.warning(this, MessageUtil.getMessage("no.line.selected"));
      }
    } else if (cmd.equals(BundleUtil.getLabel("Action.load.label"))) {
      load(idper);
    } else if (cmd.equals("CtrlAbandonDevis")) {
      layout.show(this, card0);
    }
  }

  private void load(int idper) {
    try {
      if (idper > 0) {
        load(service.getQuotations(idper, rangePanel.getStart(), rangePanel.getEnd()));
      } else {
        load(service.getQuotations(rangePanel.getStart(), rangePanel.getEnd()));
      }
    } catch (SQLException ex) {
      GemLogger.log(Level.SEVERE, ex.getMessage());
    }
  }

  @Override
  public void postEvent(GemEvent evt) {
    if (evt instanceof QuoteEvent) {
      Quote q = ((QuoteEvent) evt).getQuote();
      if (idper > 0 && !(q.getPayer() == idper || q.getMember() == idper)) {
        return;
      }
      if (evt.getOperation() == GemEvent.MODIFICATION) {
        invoiceListCtrl.reload(q);
      } else if (evt.getOperation() == GemEvent.CREATION) {
        invoiceListCtrl.addRow(q);
      }
    }
  }
}

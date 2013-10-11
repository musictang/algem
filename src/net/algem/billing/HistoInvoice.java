/*
 * @(#)HistoInvoice 2.8.n 26/09/13
 *
 * Copyright (c) 1999-2013 Musiques Tangentes All Rights Reserved.
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Vector;
import net.algem.contact.PersonFile;
import net.algem.contact.PersonFileEditor;
import net.algem.util.DataCache;
import net.algem.util.GemCommand;
import net.algem.util.GemLogger;
import net.algem.util.MessageUtil;
import net.algem.util.event.GemEvent;
import net.algem.util.event.GemEventListener;
import net.algem.util.model.Model;
import net.algem.util.module.GemDesktop;
import net.algem.util.module.GemDesktopCtrl;
import net.algem.util.ui.FileTabDialog;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.MessagePopup;

/**
 * Controller for a list of invoices.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.n
 * @since 2.3.a 14/02/12
 */
public class HistoInvoice
        extends FileTabDialog
implements GemEventListener
{

  protected InvoiceListCtrl invoiceListCtrl;
  protected CardLayout layout;
  protected ActionListener actionListener;
  protected static final String card0 = "histo";
  protected static final String card1 = "invoice";
  protected Quote current;

  public <Q extends Quote> HistoInvoice(final GemDesktop desktop, List<Q> quotes) {
    super(desktop);
    btValidation.setText(GemCommand.VIEW_EDIT_CMD);
    btCancel.setText(GemCommand.CLOSE_CMD);

    load(quotes);
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

    invoiceListCtrl = new InvoiceListCtrl(false, new BasicBillingService(dataCache));
    invoiceListCtrl.addMouseListener(new MouseAdapter()
    {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
          int id = ((InvoiceListCtrl) invoiceListCtrl).getIdContact();
          if (id > 0) {
            PersonFileEditor m = ((GemDesktopCtrl) desktop).getPersonFileEditor(id);
            if (m == null) {
              try {
                desktop.setWaitCursor();
                PersonFile pf = (PersonFile) DataCache.findId(id, Model.PersonFile);
                PersonFileEditor editor = new PersonFileEditor(pf);
                desktop.addModule(editor);
              } catch (SQLException ex) {
                GemLogger.logException(ex);
              } finally {
                desktop.setDefaultCursor();
              }
            } else {
              desktop.setSelectedModule(m);
            }
          }
        }
      }
    });
    if (c != null) {
      invoiceListCtrl.loadResult(new Vector<T>(c));
    }
  }

  @Override
  public void validation() {

    int n = 0;
    try {
      n = invoiceListCtrl.getSelectedIndex();
    } catch (IndexOutOfBoundsException ix) {
      MessagePopup.warning(this, MessageUtil.getMessage("no.line.selected"));
      return;
    }
    Invoice inv = (Invoice) invoiceListCtrl.getElementAt(n);
    current = inv;
    InvoiceEditor editor = new InvoiceEditor(desktop, new BasicBillingService(dataCache), inv);
    editor.addActionListener(this);
    editor.addGemEventListener(this);
    editor.load();
    add(editor, card1);
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

  @Override
  public void postEvent(GemEvent evt) {
    if (evt instanceof InvoiceEvent) {
      if (evt.getOperation() == GemEvent.MODIFICATION) {
        Invoice edit = ((InvoiceEvent) evt).getInvoice();
        if (edit.equals(current)) {
          current = edit;
          invoiceListCtrl.reload(current);
        }
      }
    }
  }
}

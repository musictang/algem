/*
 * @(#)FileEditor.java	2.9.4.13 09/11/15
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
package net.algem.util.module;

import java.sql.SQLException;
import java.util.List;
import net.algem.accounting.OrderLineEditor;
import net.algem.billing.*;
import net.algem.config.ConfigKey;
import net.algem.config.ConfigUtil;
import net.algem.room.Room;
import net.algem.util.GemLogger;

/**
 * Base class for editing dossiers.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.13
 * @since 2.3.c 21/03/12
 */
public class FileEditor
        extends GemModule {

  protected BillingService billingService;

  public FileEditor(String label) {
    super(label);
  }

  @Override
  public void init() {
    billingService = new BasicBillingService(dataCache);
  }

  /**
   * Gets invoice editor for a room.
   *
   * @param source
   * @param s room
   * @return an invoice editor
   */
  protected InvoiceEditor addInvoice(Object source, Room s) {

    Invoice inv = new Invoice(s, dataCache.getUser());
    inv.setEstablishment(Integer.parseInt(ConfigUtil.getConf(ConfigKey.DEFAULT_ESTABLISHMENT.getKey())));
    if (source != null && source instanceof OrderLineEditor) {
      BillingUtil.setInvoiceOrderLines(inv, ((OrderLineEditor) source).getInvoiceSelection());
    }

    InvoiceEditor editor = new InvoiceEditor(desktop, billingService, inv);
    editor.addActionListener(this);
    editor.load();
    return editor;
  }

  /**
   * Gets a quote editor for a room.
   *
   * @param source
   * @param s room
   * @return a quote editor
   */
  protected QuoteEditor addQuotation(Object source, Room s) {
    Quote q = new Quote(s, dataCache.getUser());
    q.setEstablishment(Integer.parseInt(ConfigUtil.getConf(ConfigKey.DEFAULT_ESTABLISHMENT.getKey())));
    if (source != null && source instanceof OrderLineEditor) {
      BillingUtil.setQuoteOrderLines(q, ((OrderLineEditor) source).getInvoiceSelection());
    }

    QuoteEditor editor = new QuoteEditor(desktop, billingService, q);
    editor.addActionListener(this);
    editor.load();
    return editor;
  }

  /**
   * Gets invoice history for {@literal payerId} or {@literal memberId}.
   *
   * @param idper
   * @return a history
   */
  protected HistoInvoice addHistoInvoice(int idper) {
    desktop.setWaitCursor();
    HistoInvoice history = null;
    try {
      List<Invoice> bills = billingService.getInvoices(idper);
      if (bills != null) {
        history = new HistoInvoice(desktop, billingService);
        history.setIdper(idper);
        history.load(bills);
      }
    } catch (SQLException ex) {
      GemLogger.logException(ex);
    } finally {
      desktop.setDefaultCursor();
    }
    return history;
  }

  /**
   * Gets quotation history for {@literal payerId} or {@literal memberId}.
   *
   * @param idper
   * @return a history
   */
  protected HistoQuote getHistoQuotation(int idper) {
    desktop.setWaitCursor();
    HistoQuote hq = null;

    try {
      List<Quote> quotes = billingService.getQuotations(idper);
      if (quotes != null) {
        hq = new HistoQuote(desktop, billingService);
        hq.setIdper(idper);
        hq.load(quotes);
      }
    } catch (SQLException ex) {
      GemLogger.logException(ex);
    } finally {
      desktop.setDefaultCursor();
    }
    return hq;
  }
}

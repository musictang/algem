/*
 * @(#)FileEditor.java	2.7.a 14/01/13
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
package net.algem.util.module;

import java.sql.SQLException;
import java.util.List;
import net.algem.accounting.AccountUtil;
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
 * @version 2.7.a
 * @since 2.3.c 21/03/12
 */
public class FileEditor
        extends GemModule {

  protected BillingServiceI billingService;

  public FileEditor(String _label) {
    super(_label);
  }

  @Override
  public void init() {
    billingService = new BillingService(dataCache);
  }

  /**
   * Gets invoice editor for a room.
   *
   * @param source
   * @param s room
   * @return an invoice editor
   */
  protected InvoiceEditor addInvoice(Object source, Room s) {

    Invoice f = new Invoice(s, dataCache.getUser());
    f.setEstablishment(Integer.parseInt(ConfigUtil.getConf(ConfigKey.DEFAULT_ESTABLISHMENT.getKey(), dataCache.getDataConnection())));
    if (source != null && source instanceof OrderLineEditor) {
      AccountUtil.setInvoiceOrderLines(f, ((OrderLineEditor) source).getInvoiceSelection());
    }

    InvoiceEditor ef = new InvoiceEditor(desktop, billingService, f);
    ef.addActionListener(this);
    return ef;
  }

  /**
   * Gets a quote editor for a room.
   *
   * @param source
   * @param s room
   * @return a quote editor
   */
  protected QuoteEditor addQuotation(Object source, Room s) {
    Quote d = new Quote(s, dataCache.getUser());
    d.setEstablishment(Integer.parseInt(ConfigUtil.getConf(ConfigKey.DEFAULT_ESTABLISHMENT.getKey(), dataCache.getDataConnection())));
    if (source != null && source instanceof OrderLineEditor) {
      AccountUtil.setQuoteOrderLines(d, ((OrderLineEditor) source).getInvoiceSelection());
    }

    QuoteEditor ed = new QuoteEditor(desktop, billingService, d);
    ed.addActionListener(this);
    return ed;
  }

  /**
   * Gets invoice history for {@code payerId} or {@code memberId}.
   *
   * @param payerId
   * @param memberId
   * @return a history
   */
  protected HistoInvoice addHistoInvoice(int payerId, int memberId) {
    HistoInvoice hf = null;
    try {
      List<Invoice> fcl = billingService.getInvoices(payerId, memberId);
      hf = new HistoInvoice(desktop, fcl);

    } catch (SQLException ex) {
      GemLogger.logException(ex);
    }
    return hf;
  }

  /**
   * Gets quotation history for {@code payerId} or {@code memberId}.
   *
   * @param payerId
   * @param memberId
   * @return a history
   */
  protected HistoQuote getHistoQuotation(int payerId, int memberId) {
    HistoQuote hd = null;
    try {
      List<Quote> ld = billingService.getQuotation(payerId, memberId);
      hd = new HistoQuote(desktop, ld);

    } catch (SQLException ex) {
      GemLogger.logException(ex);
    }
    return hd;
  }
}

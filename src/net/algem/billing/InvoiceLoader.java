/*
 * @(#)InvoiceLoader 2.9.4.13 09/11/15
 *
 * Copyright 1999-2015 Musiques Tangentes. All Rights Reserved.
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

import java.awt.Cursor;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;
import net.algem.planning.DateRange;
import net.algem.util.BundleUtil;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.13
 * @since 2.9.4.13 06/11/15
 */
public class InvoiceLoader
  extends SwingWorker<Void, String> {

  private final HistoInvoice histo;
  private final BillingService service;
  private final ProgressMonitor monitor;
  private final DateRange range;
  private final int idper;
  private List<Invoice> invoices = new ArrayList<Invoice>();

  public InvoiceLoader(HistoInvoice histo, BillingService service, DateRange range, int idper, ProgressMonitor monitor) {
    this.service = service;
    this.histo = histo;
    this.monitor = monitor;
    this.range = range;
    this.idper = idper;
  }

  @Override
  protected Void doInBackground() throws Exception {
    histo.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    if (idper > 0) {
      invoices = service.getInvoices(idper, range.getStart().getDate(), range.getEnd().getDate());
    } else {
      invoices = service.getInvoices(range.getStart().getDate(), range.getEnd().getDate());
    }
    int k = 1;
    int size = invoices.size();
    for (Invoice v : invoices) {
      v.setItems(((BasicBillingService) service).findItemsByInvoiceId(v.getNumber()));
      int p = k * 100 / size;
      setProgress(p);
      String m = k++ + " " + BundleUtil.getLabel("Invoice.tab.label").toLowerCase() + "  " + BundleUtil.getLabel("On.label") + " " + size;
      publish(m);
    }
    return null;
  }

  @Override
  public void process(List<String> data) {
    for (String n : data) {
      monitor.setNote(n);
    }
  }

  @Override
  public void done() {
    super.done();
    histo.load(invoices);
    histo.setCursor(Cursor.getDefaultCursor());
  }
}

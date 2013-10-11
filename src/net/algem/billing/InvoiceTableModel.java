/*
 * @(#)InvoiceTableModel.java 2.7.h 22/02/13
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

import net.algem.accounting.AccountUtil;
import net.algem.planning.DateFr;
import net.algem.util.BundleUtil;
import net.algem.util.ui.JTableModel;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.h
 * @since 2.3.a 14/02/12
 */
public class InvoiceTableModel
        extends JTableModel
{

  private BillingService service;

  public InvoiceTableModel(BillingService service) {
    this.service = service;
    header = new String[]{
      BundleUtil.getLabel("Invoice.id.label"),
      BundleUtil.getLabel("Payer.label"),
      BundleUtil.getLabel("Member.label"),
      BundleUtil.getLabel("Date.label"),
      BundleUtil.getLabel("Invoice.description.label"),
      BundleUtil.getLabel("Total.label") + " " + BundleUtil.getLabel("Invoice.ati.label")
    };
  }

  <Q extends Quote> void reset(Q v) {
    int idx = tuples.indexOf(v);
    if (idx > -1) {
      modItem(idx, v);
    }
  }
  
  @Override
  public int getIdFromIndex(int i) {
    return 0;
  }

  @Override
  public Object getValueAt(int line, int col) {
    Quote q = (Quote) tuples.elementAt(line);
    switch (col) {
      case 0:
        return q.getNumber();
      case 1:
        return service.getContact(q.getPayer());
      case 2:
        return service.getContact(q.getMember());
      case 3:
        return q.getDate();
      case 4:
        return q.getDescription();
      case 5:
        return AccountUtil.round(q.getTotalATI());

    }
    return null;
  }

  @Override
  public Class getColumnClass(int column) {
    switch (column) {
      case 0:       
      case 1:
      case 2:
        return String.class;
      case 3:
        return DateFr.class;
      case 4:
        return String.class;
      case 5:
        return Double.class;
      default:
        return Object.class;
    }
  }

  @Override
  public void setValueAt(Object value, int line, int col) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}

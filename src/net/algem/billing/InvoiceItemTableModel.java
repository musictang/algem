/*
 * @(#)InvoiceItemTableModel.java 2.3.g 06/04/12
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

import net.algem.util.BundleUtil;
import net.algem.config.Param;
import net.algem.util.ui.JTableModel;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.3.g
 * @since 2.3.a 07/02/12
 */
public class InvoiceItemTableModel
        extends JTableModel
{

  public InvoiceItemTableModel() {
    header = new String[]{
      BundleUtil.getLabel("Invoice.item.description.label"), 
      BundleUtil.getLabel("Invoice.item.price.label"),
      BundleUtil.getLabel("Invoice.item.vat.label"),
      BundleUtil.getLabel("Invoice.item.quantity.label"),
      BundleUtil.getLabel("Total.label") + " " + BundleUtil.getLabel("Invoice.et.label"),
    };
  }


  @Override
  public int getIdFromIndex(int i) {
    InvoiceItem a = (InvoiceItem) tuples.elementAt(i);
    return a.getItem().getId();
  }

  @Override
  public Object getValueAt(int ligne, int colonne) {
    InvoiceItem a = (InvoiceItem) tuples.elementAt(ligne);
    switch (colonne) {

      case 0:
        return a.getItem().getDesignation();
      case 1:
        return a.getItem().getPrice();
      case 2:
        return a.getItem().getVat();
      case 3:
        return a.getQuantity();
      case 4:
        return a.getTotal(false);

    }
    return null;
  }

  @Override
  public Class getColumnClass(int column)
  {
    switch (column) {
      case 0:
        return String.class;
      case 1:
        return Double.class;
      case 2:
        return Param.class;
      case 3:
        return Float.class;
      case 4:
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

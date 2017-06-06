/*
 * @(#)InvoiceItemTableModel.java 2.9.4.13 01/10/15
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

import net.algem.util.BundleUtil;
import net.algem.config.Param;
import net.algem.util.ui.JTableModel;
import net.algem.util.ui.TableRowTransferHandler;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.2
 * @since 2.3.a 07/02/12
 */
public class InvoiceItemTableModel
        extends JTableModel<InvoiceItem>
implements TableRowTransferHandler.Reorderable
{

  public InvoiceItemTableModel() {
    header = new String[]{
      BundleUtil.getLabel("Invoice.item.description.label"),
      BundleUtil.getLabel("Invoice.item.quantity.label"),
      BundleUtil.getLabel("Invoice.item.price.label"),
      BundleUtil.getLabel("Invoice.item.vat.label"),
      BundleUtil.getLabel("Total.label") + " " + BundleUtil.getLabel("Invoice.et.label"),
    };
  }


  @Override
  public int getIdFromIndex(int i) {
    InvoiceItem ivItem = tuples.elementAt(i);
    return ivItem.getItem().getId();
  }

  @Override
  public Object getValueAt(int line, int col) {
    InvoiceItem ivItem = tuples.elementAt(line);
    switch (col) {
      case 0:
        return ivItem.getItem().getDesignation();
      case 1:
        return ivItem.getQuantity();
      case 2:
        return ivItem.getItem().getPrice();
      case 3:
        return ivItem.getItem().getTax();
      case 4:
        return ivItem.getTotal(false);
    }
    return null;
  }

  @Override
  public Class getColumnClass(int col)
  {
    switch (col) {
      case 0:
        return String.class;
     case 1:
        return Float.class;
      case 2:
        return Double.class;
      case 3:
        return Param.class;
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
  
  
    @Override
    public void reorder(int fromIndex, int toIndex) {
        InvoiceItem ii = tuples.get(fromIndex);
        deleteItem(fromIndex);
        int maxIdx = tuples.size() - 1;
        if (toIndex > maxIdx) {
            toIndex = maxIdx + 1;
        }
        tuples.add(toIndex, ii);
        fireTableRowsInserted(toIndex, toIndex);
    }
}

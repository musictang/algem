/*
 * @(#)OrderControlTableModel.java	2.17.1 20/10/19
 *
 * Copyright (c) 1999-2019 Musiques Tangentes. All Rights Reserved.
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
package net.algem.accounting;

import java.util.ArrayList;
import java.util.List;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import net.algem.util.BundleUtil;
import net.algem.util.ui.TableElementModel;

/**
 * Table model for order balance.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @version 2.17.0
 * @since 2.17.1 20/10/2019
 */
public class OrderControlTableModel
        extends AbstractTableModel
        implements TableElementModel
{

  private List<String[]> payerLines = new ArrayList<String[]>();

  public OrderControlTableModel() {
  }

  /**
   * Loads a list of orderlines.
   * @param lines
   */
  public void load(List<String[]> lines) {
    payerLines = lines;
    fireTableDataChanged();
  }

  public void clear() {
    payerLines.clear();
    fireTableDataChanged();
  }

  @Override
  public void addElement(Object p) {
    payerLines.add((String[]) p);
    //modif 1.2b index -1
    fireTableRowsInserted(payerLines.size() - 1, payerLines.size() - 1);
  }

  public String[] getOrderLineAt(int line) {
    return payerLines.get(line);
  }

  public void setOrderLineAt(String[] p, int line) {
    payerLines.set(line, p);
    fireTableRowsUpdated(line, line);
  }

  public List<String[]> getData() {
    return payerLines;
  }

  @Override
  public int getSize() {
    return payerLines.size();
  }

  @Override
  public void setElementAt(Object o, int line) {
    payerLines.set(line, (String[]) o);
    fireTableRowsUpdated(line, line);
  }

  @Override
  public void removeElementAt(int line) {
    payerLines.remove(line);
    fireTableRowsDeleted(line, line);
  }

  @Override
  public Object getElementAt(int line) {
    return payerLines.get(line);
  }

  @Override
  public int getRowCount() {
    return payerLines.size();
  }

  @Override
  public int getColumnCount() {
    return 4;
  }

  @Override
  public String getColumnName(int col) {
    switch (col) {
      case 0:
        return BundleUtil.getLabel("Payer.label");
      case 1:
        return BundleUtil.getLabel("Payer.label");
      case 2:
        return BundleUtil.getLabel("Payment.schedule.number.label");
      case 3:
        return BundleUtil.getLabel("Amount.label");
      default:
        System.out.println("OrderControlTableModel#getColumnName colonne " + col);
    }
    return "Erreur";
  }

  @Override
  public Class getColumnClass(int col) {
    switch (col) {
      case 0: //payer id
          return Integer.class;
      case 1: //payer name
          return String.class;
      case 2: //nb invoices
        return Integer.class;
      case 3: //amount
        return GemAmount.class;
      default:
        System.out.println("OrderControlTableModel#getColumnClass colonne " + col);
    }
    return Object.class;
  }

  @Override
  public void setValueAt(Object o, int line, int col) {
    fireTableChanged(new TableModelEvent(this, line, line, col));
  }

  @Override
  public Object getValueAt(int line, int col) {
    String[] e = (String[]) payerLines.get(line);
    switch (col) {
      case 0:
      case 1:
      case 2:
      case 3:
        return e[col];
      default:
        System.out.println("OrderControlTableModel.getValueAt colonne " + col);
    }
    return "erreur";
  }
}

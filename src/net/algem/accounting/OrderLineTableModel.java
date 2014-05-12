/*
 * @(#)OrderLineTableModel.java	2.8.t 10/05/14
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
package net.algem.accounting;

import java.util.Vector;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import net.algem.planning.DateFr;
import net.algem.util.BundleUtil;
import net.algem.util.ui.TableElementModel;

/**
 * Table model for orderline.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.t
 * @since 1.0a 18/07/1999
 */
public class OrderLineTableModel
        extends AbstractTableModel
        implements TableElementModel
{

  private Vector<OrderLine> orderLines = new Vector<OrderLine>();

  public OrderLineTableModel() {
  }

  /**
   * Loads a list of orderlines.
   * @param lines
   */
  public void load(Vector<OrderLine> lines) {
    orderLines = lines;
    fireTableDataChanged();
  }

  public void clear() {
    orderLines.removeAllElements();
    fireTableDataChanged();
  }

  @Override
  public void addElement(Object p) {
    orderLines.addElement((OrderLine) p);
    //modif 1.2b index -1
    fireTableRowsInserted(orderLines.size() - 1, orderLines.size() - 1);
  }

  public OrderLine getOrderLineAt(int line) {
    return orderLines.elementAt(line);
  }

  public void setOrderLineAt(OrderLine p, int line) {
    orderLines.setElementAt(p, line);
    fireTableRowsUpdated(line, line);
  }

  public Vector<OrderLine> getData() {
    return orderLines;
  }

  @Override
  public int getSize() {
    return orderLines.size();
  }

  @Override
  public void setElementAt(Object o, int line) {
    orderLines.setElementAt((OrderLine) o, line);
    fireTableRowsUpdated(line, line);
  }

  @Override
  public void removeElementAt(int line) {
    orderLines.remove(line);
    fireTableRowsDeleted(line, line);
  }

  @Override
  public Object getElementAt(int line) {
    return orderLines.elementAt(line);
  }

  @Override
  public int getRowCount() {
    return orderLines.size();
  }

  @Override
  public int getColumnCount() {
    return 13;
  }

  @Override
  public String getColumnName(int col) {
    switch (col) {
      case 0:
        return BundleUtil.getLabel("Payer.label");
      case 1:
        return BundleUtil.getLabel("Member.label");
      case 2:
        return BundleUtil.getLabel("Group.label");
      case 3:
        return BundleUtil.getLabel("Date.label");
      case 4:
        return BundleUtil.getLabel("Label.label");
      case 5:
        return BundleUtil.getLabel("Mode.of.payment.label").substring(0, 5);
      case 6:
        return BundleUtil.getLabel("Amount.label");
      case 7:
        return BundleUtil.getLabel("Document.number.label");
      case 8:
        return BundleUtil.getLabel("Account.label");
      case 9:
        return BundleUtil.getLabel("Cost.account.label");
      case 10:
        return BundleUtil.getLabel("Payment.schedule.cashing.tip");
      case 11:
        return BundleUtil.getLabel("Payment.schedule.transfer.tip").substring(0, 5);
      case 12:
        return BundleUtil.getLabel("Invoice.label").substring(0, 5);
      /*case 11:
        return "Monnaie";*/
      default:
        System.out.println("OrderLineTableModel#getColumnName colonne " + col);
    }
    return "Erreur";
  }

  @Override
  public Class getColumnClass(int col) {
    switch (col) {
      case 0: //payer
      case 1: //member
      case 2: //group
        return Integer.class;
      case 3: //date
        return DateFr.class;
      case 4: //label
      case 5: //mode of payment
        return String.class;
      case 6: //amount
        return GemAmount.class;
      case 7: //document number
        return String.class;
      case 8: //account
        return Account.class;
      case 9: //cost account
        return Account.class;
      case 10: //payed
      case 11: //transfered
        return Boolean.class;
      case 12: //invoice
        return String.class;
      default:
        System.out.println("OrderLineTableModel#getColumnClass colonne " + col);
    }
    return Object.class;
  }

  @Override
  public void setValueAt(Object o, int line, int col) {
    fireTableChanged(new TableModelEvent(this, line, line, col));
  }

  @Override
  public Object getValueAt(int line, int col) {
    OrderLine e = (OrderLine) orderLines.elementAt(line);
    switch (col) {
      case 0:
        return " " + e.getPayer();
      case 1:
        return " " + e.getMember();
      case 2:
        return e.getGroup();
      case 3:
        return e.getDate();
      case 4:
        return e.getLabel();
      case 5:
        return e.getModeOfPayment();
      case 6:
        return new GemAmount(e.getAmount());
      case 7:
        return e.getDocument();
      case 8:
        if (e.getAccount() == null) {
          return null;
        }
        String l = e.getAccount().getLabel();
        return (l == null) ? e.getAccount().getNumber() : l;
      case 9:
        String a = e.getCostAccount().getLabel();
        return (a == null) ? e.getCostAccount().getNumber() : a;
      case 10:
        return e.isPaid();
      case 11:
        return e.isTransfered();
      case 12:
        return e.getInvoice();
      default:
        System.out.println("TableEcheancier.getValueAt colonne " + col);
    }
    return "erreur";
  }
}

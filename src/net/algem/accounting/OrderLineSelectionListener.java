/*
 * @(#)OrderLineSelectionListener.java 2.6.a 14/09/12
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
package net.algem.accounting;

import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.JTextComponent;

/**
 * Listener for multiple selection in orderline view
 * .
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 * @since 2.2.p 19/01/12
 *
 */
public class OrderLineSelectionListener
        implements ListSelectionListener
{

  public final static String COMMAND = "TOTAL";
  private OrderLineTableView table;
  private JTextComponent component;

  public OrderLineSelectionListener(OrderLineTableView table, JTextComponent component) {
    this.table = table;
    this.component = component;
  }

  @Override
  public void valueChanged(ListSelectionEvent lse) {

    if (lse.getValueIsAdjusting()) {
      return;
    }
    ListSelectionModel lsm = (ListSelectionModel) lse.getSource();
    if (!lsm.isSelectionEmpty()) {
      double total = 0d;
      int rows[] = table.getSelectedRows();
      for (int i = 0; i < rows.length; i++) {
        OrderLine e = table.getElementAt(rows[i]);
        total += e.getDoubleAmount();
      }
      component.setText(String.valueOf(AccountUtil.round(total)));
    }
  }

}

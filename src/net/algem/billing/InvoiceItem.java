/*
 * @(#)InvoiceItem.java 2.9.4.13 05/11/15
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

import net.algem.accounting.OrderLine;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.13
 * @since 2.3.a 27/12/11
 */
public class InvoiceItem
        extends BillingItem
{

  private static final long serialVersionUID = 7322513291605129612L;
  private OrderLine orderLine;

  public InvoiceItem() {
  }

  public InvoiceItem(Item item) {
    super(item);
  }

  public InvoiceItem(String id) {
    super(id);
  }

  /**
   * @param billingId invoice number
   * @param i item
   * @param ol order line
   * @param qty quantity
   */
  public InvoiceItem(String billingId, Item i, int qty, OrderLine ol) {
    super(billingId, i, qty);
    this.orderLine = ol;
  }

  /**
   * Invoice item creation from an order line.
   * Invoice number is not yet attributed and default quantity = 1.
   * @param ol order line
   */
  public InvoiceItem(OrderLine ol) {
    
    this.orderLine = ol;
    this.item = new Item(ol, 1);
    this.quantity = 1.0f; // default quantity
    setBillingId(null);

  }

  public void setOrderLine(OrderLine ol) {
    this.orderLine = ol;
  }

  public OrderLine getOrderLine() {
    return orderLine;
  }

}

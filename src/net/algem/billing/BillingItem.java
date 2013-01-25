/*
 * @(#)BillingItem.java 2.4.d 07/06/12
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

/**
 *
 * @author Jean-Marc Gobat <a href="mailto:jmao@free.fr">jmao@free.fr</a>
 * @version 2.4.d
 * @since 2.4.d 07/06/12
 */
public class BillingItem
{

  protected String billingId;
  protected Item item;
  protected float quantity;

  public BillingItem() {
    this.quantity = 1.0f;
  }

  public BillingItem(Item i) {
    this(null, i, 1);
  }

  public BillingItem(String id) {
    this.billingId = id;
  }

  /**
   * @param f invoice number
   * @param i item
   * @param qty quantity
   */
  public BillingItem(String f, Item i, int qty) {
    this.billingId = f;
    this.item = i;
    this.quantity = qty;
  }

  /**
   * Total amount of an invoice item
   *
   * @param ati all taxes included
   * @return a double
   */
  public double getTotal(boolean ati) {

    double t = getTotalET();
    return ati ? t + getTotalVat() : t;
  }

  /** Total amount of VAT. */
  public double getTotalVat() {

    double tva = 0.0;

    if (item.getVat() != null) {
      tva = Double.parseDouble(item.getVat().getValue());
    }
    return (getTotalET() * tva) / 100;
  }

  public void setBillingId(String idFacture) {
    this.billingId = idFacture;
  }

  public Invoice getInvoice(int id) {
    return null;
  }

  public void setItem(Item item) {
    this.item = item;
  }

  public Item getItem() {
    return item;
  }

  public void setQuantity(float q) {
    this.quantity = q;
  }

  public float getQuantity() {
    return quantity;
  }

  @Override
  public String toString() {
    return item + " " + quantity;
  }

  /** Total amount without taxes. */
  protected double getTotalET() {
    return item.getPrice() * quantity;
  }
}

/*
 * @(#)BillingItem.java 2.9.4.13 05/11/15
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

import java.io.Serializable;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.13
 * @since 2.4.d 07/06/12
 */
public class BillingItem
  implements Serializable
{

  protected String billingId;
  protected Item item;
  protected float quantity;
  private static final long serialVersionUID = -5066123755134632021L;

  public BillingItem() {
    this.quantity = 1.0f;
  }

  public BillingItem(Item i) {
    this(null, i, 1);
  }

  public BillingItem(String billingId) {
    this.billingId = billingId;
  }

  /**
   * @param billingId invoice number
   * @param i item
   * @param qty quantity
   */
  public BillingItem(String billingId, Item i, int qty) {
    this.billingId = billingId;
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

  /** 
   * Total amount of VAT.
   * @return total VAT
   */
  public double getTotalVat() {

    double tva = 0.0;

    if (item.getVat() != null) {
      tva = Double.parseDouble(item.getVat().getValue());
    }
    return (getTotalET() * tva) / 100;
  }

  public void setBillingId(String billingId) {
    this.billingId = billingId;
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

  /** 
   * Total amount without taxes.
   * @return a number
   */
  protected double getTotalET() {
    return item.getPrice() * quantity;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final BillingItem other = (BillingItem) obj;
    if (this.item != other.item && (this.item == null || !this.item.equals(other.item))) {
      return false;
    }
    if (Float.floatToIntBits(this.quantity) != Float.floatToIntBits(other.quantity)) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 5;
    hash = 97 * hash + (this.item != null ? this.item.hashCode() : 0);
    hash = 97 * hash + Float.floatToIntBits(this.quantity);
    return hash;
  }

}

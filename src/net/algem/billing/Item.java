/*
 * @(#)Item.java 2.9.4.13 05/11/15
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
import net.algem.config.Param;
import net.algem.util.model.GemModel;

/**
 * Item usable for invoice creation.
 * An item may be created regardless of invoice.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.13
 * @since 2.3.a 22/12/11
 */
public class Item
  implements GemModel
{

 private static final long serialVersionUID = 3875590414115115006L; 
 private int id;

 /** Item name. */
 private String designation;
 
 /** Unit price */
 private double price;

 /** VAT id. */
 private Param vat;
 
 /** Account number. */
 private int account;

 /** A standard item is a recurrent item in billing. */
 private boolean standard;

  public Item() {
  }

  public Item(int id) {
    this.id = id;
  }

  /**
   * Item creation.
   * @param id
   * @param designation
   * @param price
   * @param account
   * @param standard article standard si true
   */
  public Item(int id, String designation, double price, int account, boolean standard) {
    this(id);
    this.designation = designation;
    this.price = price;
    this.account = account;
    this.standard = standard;
  }
  
  public Item copy() {
    Item c = new Item();
    c.id = this.id;
    c.designation = this.designation;
    c.price = this.price;
    c.account = this.account;
    c.standard = this.standard;
    c.vat = new Vat(this.vat);
    
    return c;
  }

  /**
   * Item creation without identification number.
   * @param d designation
   * @param p price unitaire
   * @param c account
   * @param s standard
   */
  public Item(String d, double p, int c, boolean s) {
    this.designation = d;
    this.price = p; 
    this.account = c;
    this.standard = s;
  }

  /**
   * Item creation from an order line.
   * @param e order line
   */
  public Item(OrderLine e, int qty) {
    if (e != null) {
      designation = e.getLabel();
      if (qty > 0) {
        price = (Math.abs(e.getDoubleAmount()) / qty);
      }
      if (e.getAccount() != null) {
        account = e.getAccount().getId();
      }
    }
    vat = new Param("1","0.0"); // vat par défaut à 0.0
    standard = false;

  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final Item other = (Item) obj;
    if (this.id != other.id) {
      return false;
    }
    if ((this.designation == null) ? (other.designation != null) : !this.designation.equals(other.designation)) {
      return false;
    }
    if (this.price != other.price) {
      return false;
    }
    if (this.vat != other.vat && (this.vat == null || !this.vat.equals(other.vat))) {
      return false;
    }
    if (this.account != other.getAccount()) {
      return false;
    }
    if (this.standard != other.standard) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 47 * hash + this.id;
    hash = 47 * hash + (this.designation != null ? this.designation.hashCode() : 0);
    hash = 47 * hash + (int) (Double.doubleToLongBits(this.price) ^ (Double.doubleToLongBits(this.price) >>> 32));
    hash = 47 * hash + (this.vat != null ? this.vat.hashCode() : 0);
    hash = 47 * hash + this.account;
    hash = 47 * hash + (this.standard ? 1 : 0);
    return hash;
  }



  public String getDesignation() {
    return designation;
  }

  public void setDesignation(String designation) {
    this.designation = designation;
  }

  @Override
  public int getId() {
    return id;
  }

  @Override
  public void setId(int id) {
    this.id = id;
  }

  public double getPrice() {
    return price;
  }

  public void setPrice(double p) {
    this.price = p;
  }

  public int getAccount() {
    return account;
  }

  public void setAccount(int account) {
    this.account = account;
  }

  public boolean isStandard() {
    //return this.getId() > 0;
    return standard;
  }

  public void setStandard(boolean standard) {
    this.standard = standard;
  }

  public Param getVat() {
    return vat;
  }

  public void setVat(Param tva) {
    this.vat = tva;
  }

  @Override
  public String toString() {
    return id+" : "+designation;
  }

}

/*
 * @(#)Item.java 2.14.0 12/06/17
 *
 * Copyright (c) 1999-2017 Musiques Tangentes. All Rights Reserved.
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

import java.util.List;
import net.algem.accounting.AccountUtil;
import net.algem.accounting.OrderLine;
import net.algem.util.DataCache;
import net.algem.util.model.GemModel;
import net.algem.util.model.Model;

/**
 * Item usable for invoice creation.
 * An item may be created regardless of invoice.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.14.0
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

 /** VAT tax. */
 private Vat tax;

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
   * Full item creation.
   * @param id
   * @param designation
   * @param price basic price
   * @param account account id
   * @param standard if true, this item is standard
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
    c.tax = new Vat(this.tax);

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
   * @param qty quantity
   */
  public Item(OrderLine e, int qty) {
    float t = 0.0f;
    if (e != null) {
      designation = e.getLabel();
      t = e.getTax();
      if (qty > 0) {
        if (t > 0) {
          double coeff = 100 / (100 + t);
          double exclTax = AccountUtil.round(Math.abs(e.getDoubleAmount()) * coeff);
          price = exclTax / qty;
        } else {
          price = (Math.abs(e.getDoubleAmount()) / qty);
        }
      }
      if (e.getAccount() != null) {
        account = e.getAccount().getId();
      }
    }

    if (t > 0) {
      DataCache cache = DataCache.getInitializedInstance();
      if (cache != null) {
        List<Vat> taxList = cache.getList(Model.Vat).getData();
        for (Vat p : taxList) {
          if (p.getRate() == t) {
            tax = new Vat(p.getId(), p.getKey(), p.getAccount());
            break;
          }
        }
      } else {
        tax = new Vat(1, "0.0", null); // default to 0.0
      }
    } else {
      tax = new Vat(1, "0.0", null); // default to 0.0
    }
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
    if (this.tax != other.tax && (this.tax == null || !this.tax.equals(other.tax))) {
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
    hash = 47 * hash + (this.tax != null ? this.tax.hashCode() : 0);
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

  public Vat getTax() {
    return tax;
  }

  public void setTax(Vat tax) {
    this.tax = tax;
  }

  @Override
  public String toString() {
    return id+" : "+designation;
  }

}

/*
 * @(#)Quote.java 2.14.1 28/05/17
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

import java.io.Serializable;
import java.util.*;
import net.algem.accounting.OrderLine;
import net.algem.config.Param;
import net.algem.contact.PersonFile;
import net.algem.planning.DateFr;
import net.algem.room.Room;
import net.algem.security.User;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.14.1
 * @since 2.4.d 08/06/12
 */
public class Quote
  implements Serializable
{

  private static final long serialVersionUID = -7931741834723333355L;

  /**
   * A quote/invoice number de devis/facture is represented by pattern {@code yymmn}.
   * yy : first 2 digits of current year<br />
   * mm : month digits<br />
   * n : an incremental number
   */
  protected String number;

  /** Invoice/quote label. */
  protected String description;

  /** Establishment id. */
  protected int estab;

  /** Curent user. */
  protected User user;

  /** Invoice issuer id. */
  protected int issuer;

  /** Invoice member id. */
  protected int member;

  /** Client id. Payer must exists in Algem contacts. */
  protected int payer;

  /** Creation date. */
  protected DateFr date;

  protected String reference;

  /** Down payment. */
  protected double downPayment;

  /** Invoice item collection. */
  protected Collection<InvoiceItem> items = new ArrayList<InvoiceItem>();

  /** Order line collection. */
  protected Collection<OrderLine> orderLines = new ArrayList<OrderLine>();

  protected boolean creditNote;

  private boolean editable = true;

  /**
   * Création d'un devis/facture vierge. La date de facturation correspond à la
   * date du jour.
   */
  public Quote() {
    date = new DateFr(new Date());
  }

  /**
   * Creates a quote/invoice from its number.
   *
   * @param number
   */
  public Quote(String number) {
    this.number = number;
  }

  public Quote(Quote d) {
    this(d.getNumber());
    this.date = d.getDate();
    this.estab = d.getEstablishment();
    this.issuer = d.getIssuer();
    this.payer = d.getPayer();
    this.description = d.getDescription();
    this.reference = d.getReference();
    this.member = d.getMember();
    this.downPayment = d.getDownPayment();
  }

  /**
   * Creates a quote/invoice for a person file.
   *
   * @param pf the person file of debitor
   * @param u current user
   */
  public Quote(PersonFile pf, User u) {
    this(u);
    int id = pf.getId();
    this.member = id;

    if (pf.getMember() != null) {
      int p = pf.getMember().getPayer();
      this.payer = (p > 0 && p != id) ? p : id;
    } else {
      this.payer = id;
    }
  }

  public Quote(Room s, User u) {
    this(u);
    this.member = s.getContact().getId();
    this.payer = s.getPayer().getId();
  }

  public Quote(User u) {
    this.date = new DateFr(new Date());
    this.user = u;
    setIssuer();
  }

  /**
   * Two quotes/invoices are equal if they have same number.
   *
   * @param obj
   * @return true if equal
   */
  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (! (obj instanceof Quote)) {
      return false;
    }

    final Quote other = (Quote) obj;
    if ((this.number == null) ? (other.number != null) : !this.number.equals(other.number)) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 37 * hash + (this.number != null ? this.number.hashCode() : 0);
    return hash;
  }

  boolean equiv(Invoice n) {
    if (n == null) {
      return false;
    }
    if ((this.description == null) ? (n.description != null) : !this.description.equals(n.description)) {
      return false;
    }
    if ((this.reference == null) ? (n.reference != null) : !this.reference.equals(n.reference)) {
      return false;
    }
    if ((this.date == null) ? (n.date != null) : !this.date.equals(n.date)) {
      return false;
    }
    if (estab != n.estab) {
      return false;
    }
    if (downPayment != n.downPayment) {
      return false;
    }
    if (getTotalATI() != n.getTotalATI()) {
      return false;
    }
    if (this.items == null) {
      return false;
    }
    if (n.getItems() == null) {
      return false;
    }
    List<InvoiceItem> i1 = new ArrayList<InvoiceItem>(items);
    List<InvoiceItem> i2 = new ArrayList<InvoiceItem>(n.getItems());

    return i1.equals(i2);
  }


  @Override
  public String toString() {
    return getClass().getSimpleName() + " " + number + ", " + description;
  }

  /**
   * Automatic increment of invoice number.
   * Invoice numbers must follow without intervall.
   *
   * @param last last saved invoice number
   */
  void inc(int last) {

    String d = date.toString().substring(8);// les 2 premiers chiffres de l'année
    String m = date.toString().substring(3, 5); // le mois sur 2 chiffres
    //int n = Integer.parseInt(last.substring(4)) + 1; // le numéro incrémental
    //int n = Integer.parseInt(last);
    number = d + m + String.valueOf(last + 1);
  }

  public DateFr getDate() {
    return date;
  }

  void setDate(DateFr date) {
    this.date = date;
  }

  public int getMember() {
    return member;
  }

  void setMember(int m) {
    this.member = m;
  }

  public int getPayer() {
    return payer;
  }

  void setPayer(int p) {
    this.payer = p;
  }

  int getIssuer() {
    return issuer;
  }

  private void setIssuer() {
    this.issuer = (user == null) ? 0 : user.getId();
  }

  void setIssuer(int issuer) {
    this.issuer = issuer;
  }

  User getUser() {
    return user;
  }

  void setUser(User user) {
    this.user = user;
    setIssuer();
  }

  int getEstablishment() {
    return estab;
  }

  public void setEstablishment(int estab) {
    this.estab = estab;
  }

  public String getNumber() {
    return number;
  }

  void setNumber(String n) {
    this.number = n;
  }

  String getDescription() {
    return description;
  }

  void setDescription(String desc) {
    this.description = desc;
  }

  String getReference() {
    return reference;
  }

  void setReference(String ref) {
    this.reference = ref;
  }

  public double getDownPayment() {
    return downPayment;
  }

  void setDownPayment(double ac) {
    this.downPayment = ac;
  }

  /**
   * Retrieves all items for quote/invoice.
   *
   * @return a collection of items
   */
  public Collection<InvoiceItem> getItems() {
    return items;
  }

  protected void setItems(Collection<InvoiceItem> items) {
    this.items = items;
  }

  void setOrderLines(Collection<OrderLine> ol) {
    orderLines = ol;
  }

  public Collection<OrderLine> getOrderLines() {
    return orderLines;
  }

  /**
   * Adds an item to a quote/invoice.
   *
   * @param it the item to add
   */
  void addItem(InvoiceItem it) {
    items.add(it);
  }

  void remove(InvoiceItem it) {
    items.remove(it);
  }

  /**
   * Total amount whithout taxes of a quote/invoice.
   *
   * @return a double
   */
  public double getTotalET() {
    double total = 0.0;
    for (BillingItem a : items) {
      total += a.getTotal(false);
    }
    return total;
  }

  /**
   * Total amount with taxes of a quote/invoice.
   *
   * @return a double
   */
  public double getTotalATI() {
    double total = 0.0;
    for (BillingItem a : items) {
      total += a.getTotal(true);
    }
    return total;
  }

  public double netToPay() {
    return getTotalATI() - downPayment;
  }

  /**
   * Calculates the total VAT amount for each invoice item.
   * Totals are saved in a map with percents of VAT for keys.
   *
   * @return a map[VAT rate -> total]
   */
  public Map<String, Double> getTotalVAT() {

    Map<String, Double> vatMap = new TreeMap<String, Double>(); // Sorted map

    for (BillingItem bi : items) {
      double total = 0.0;
      Param vat = bi.getItem().getTax();
      Double t = vatMap.get(vat.getKey());
      total = (t == null) ? 0.0 : t.doubleValue();
      total += bi.getTotalVat();
      vatMap.put(vat.getKey(), total);
    }

    return vatMap;

  }

  /**
   * Checks if quote/invoice includes a down payment.
   *
   * @return true if downPayment > 0
   */
  public boolean hasDownPayment() {
    return downPayment > 0.0;
  }

  /**
   * Checks if this instance is of type Invoice.
   *
   * @return true if invoice
   */
  boolean isInvoice() {
    return getClass() == Invoice.class;
  }

  boolean isEditable() {
    return editable;
  }

  boolean isCreditNote() {
    return creditNote;
  }

  void setCreditNote(boolean c) {
    creditNote = c;
  }

  void setEditable(boolean editable) {
    this.editable = editable;
  }

}

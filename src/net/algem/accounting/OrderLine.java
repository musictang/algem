/*
 * @(#)OrderLine.java	2.14.0 20/06/17
 *
 * Copyright (c) 1999-20167Musiques Tangentes. All Rights Reserved.
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

import net.algem.billing.Invoice;
import net.algem.billing.InvoiceItem;
import net.algem.enrolment.ModuleOrder;
import net.algem.planning.DateFr;
import net.algem.util.model.GemModel;

/**
 * Order line.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.14.0
 *
 */
public class OrderLine
  extends StandardOrderLine
  implements GemModel {

  private static final long serialVersionUID = -4614812764226507224L;

  private int payer;
  private int member;
  private int order;
  private boolean paid;
  private boolean transfered;
  private String currency = "E";
  /** Invoice number. Numéro de facture. */
  private String invoice;
  private int group;
  private float tax;

  public OrderLine() {
  }

  /**
   * Creates an orderline with payer number, amount, mode of payment and order id.
   *
   * @param m module order
   */
  public OrderLine(ModuleOrder m) {
    payer = m.getPayer();
    amount = AccountUtil.getIntValue(m.getPaymentAmount());
    modeOfPayment = m.getModeOfPayment();
    order = m.getIdOrder();
  }

  public OrderLine(OrderLine e) {
    id = 0;
    date = e.getDate();
    payer = e.getPayer();
    member = e.getMember();
    order = e.getOrder();
    modeOfPayment = e.getModeOfPayment();
    label = e.getLabel();
    amount = ModeOfPayment.FAC.toString().equals(modeOfPayment) ? e.getAmount() : Math.abs(e.getAmount());
    document = e.getDocument();
    school = e.getSchool();
    account = e.getAccount();
    costAccount = e.getCostAccount();
    paid = e.isPaid();
    transfered = false; // non transféré par défaut
    currency = e.getCurrency();
    invoice = e.getInvoice();
    group = e.getGroup();
    tax = e.getTax();

  }

  /**
   * Creates order line from standard one.
   * @param s standard instance
   */
  public OrderLine(StandardOrderLine s) {
    id = s.getId();
    date = new DateFr(s.getDate());//new DateFr();
    payer = 0;
    member = 0;
    order = 0;
    label = s.getLabel();
    modeOfPayment = s.getModeOfPayment();
    amount = s.getAmount();
    document = s.getDocument();
    school = s.getSchool();
    account = s.getAccount();
    costAccount = s.getCostAccount();
    paid = false;
    transfered = false;
    paid = false;
    transfered = false;
    currency = "E";
    group = 0;
    invoice = null;
  }

  public OrderLine(Invoice v, InvoiceItem item) {
    this(v);
    label = item.getItem().getDesignation();
    modeOfPayment = ModeOfPayment.FAC.toString(); // FAC
    amount = -(AccountUtil.getIntValue(item.getTotal(true)));
  }

  public OrderLine(Invoice inv) {
    id = 0;
    date = inv.getDate();
    payer = inv.getPayer();
    member = inv.getMember();
    invoice = inv.getNumber();
    order = 0;
    document = (inv.getNumber() == null) ? "" : inv.getNumber();
    paid = false;
    transfered = false;
    currency = "E";
  }

  @Override
  public String toString() {
    return date.toString() + " " + payer + " " + member + " " + modeOfPayment;// + " " + monnaie;
  }

  public int getMember() {
    return member;
  }

  public void setMember(int i) {
    member = i;
  }

  public int getOrder() {
    return order;
  }

  public void setOrder(int i) {
    order = i;
  }

  public int getPayer() {
    return payer;
  }

  public void setPayer(int i) {
    payer = i;
  }

  /**
   * Retrieves account's label.
   *
   * @return une chaîne de caractères
   */
  public String getAccountLabel() {
    return account.getLabel();
  }

  public Double getDoubleAmount() {
    return (Double) (amount / 100D);
  }

  public void setAmount(Double d) {
    amount = (int) Math.rint(d * 100);
    //montant = (int) (d * 100); // bug arrondi
  }

  public boolean isTransfered() {
    return transfered;
  }

  public void setTransfered(boolean i) {
    transfered = i;
  }

  public boolean isPaid() {
    return paid;
  }

  public void setPaid(boolean i) {
    paid = i;
  }

  public int getGroup() {
    return group;
  }

  public void setGroup(int group) {
    this.group = group;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String s) {
    currency = s;
  }

  /**
   * Returns the string representation of a currency code.
   *
   * @return the currency symbol
   * @since 1.1d
   *
   */
  public String getCurrencyCode() {
    String m = getCurrency();
    switch (m.charAt(0)) {
      case 'E':
        return "€";
      case 'F':
        return "FF";
      case 'D':
        return "$";
      default:
        return "€";
    }

  }

  public float getTax() {
    return tax;
  }

  public void setTax(float tax) {
    this.tax = tax;
  }

  /**
   * Retrieves invoice number associated with this orderline.
   *
   * @return a string representing an invoice number
   * @since 2.3.af
   */
  public String getInvoice() {
    return invoice;
  }

  /**
   * Association d'un numéro de facture.
   *
   * @param invoiceNumber
   * @since 2.3.af
   */
  public void setInvoice(String invoiceNumber) {
    this.invoice = invoiceNumber;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final OrderLine other = (OrderLine) obj;
    if (this.id != other.id) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 61 * hash + this.id;
    return hash;
  }

}

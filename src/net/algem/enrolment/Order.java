/*
 * @(#)Order.java	2.9.4.13 05/11/15
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
package net.algem.enrolment;

import net.algem.accounting.OrderLine;
import net.algem.planning.DateFr;

/**
 * An order may be associated with an enrolment or an invoice order line.
 * When invoice is created, invoice number is updated in order.
 * 
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.13
 * @since 1.0a 07/07/1999
 */
public class Order
        implements java.io.Serializable
{

  private static final long serialVersionUID = -9115549231014294323L;
  
  protected int id;
  protected int member;
  protected int payer;
  protected DateFr creation;
  private String invoice;

  public Order() {
  }

  public Order(OrderLine e) {
    id = e.getOrder();
    creation = e.getDate();
    member = e.getMember();
    payer = e.getPayer();
  }


  @Override
  public String toString() {
    return id + " " + member + " " + payer + " " + creation + " " + invoice;
  }

  public int getId() {
    return id;
  }

  public void setId(int i) {
    id = i;
  }

  public int getMember() {
    return member;
  }

  public void setMember(int i) {
    member = i;
  }

  public int getPayer() {
    return payer;
  }

  public void setPayer(int i) {
    payer = i;
  }

  public DateFr getCreation() {
    return creation;
  }

  public void setCreation(DateFr d) {
    creation = d;
  }

  public String getInvoice() {
    return invoice;
  }

  public void setInvoice(String f) {
    invoice = f;
  }
  
}

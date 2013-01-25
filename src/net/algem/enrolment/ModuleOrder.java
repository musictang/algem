/*
 * @(#)ModuleOrder.java	2.6.a 17/09/12
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
package net.algem.enrolment;

import net.algem.planning.DateFr;

/**
 * Module order object model.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class ModuleOrder
        implements java.io.Serializable
{

  private int oid;
  private int idcmd;
  private int payer;
  
  /** Module id. */
  private int module;
  
  /** Module index. */
  private int index_module;
  
  /**
   * Amount of the order.
   * TODO : s'agit-il du prix de base, du prix total ?
   * Actuellement, aligné sur le montant d'une échéance.
   */
  private double price;
  private DateFr start;
  private DateFr end;
  private String modeOfPayment;
  private String payment;
  private int nOrderLines;
  private String title;

  @Override
  public String toString() {
    return idcmd + "," + module + "," + price + "," + start + "," + end + "," + price + "," + modeOfPayment + "," + payment + "," + nOrderLines;
  }

  public int getId() {
    return idcmd;
  }

  public void setId(int i) {
    idcmd = i;
  }

  public int getOID() {
    return oid;
  }

  public void setOID(int i) {
    oid = i;
  }

  public int getModule() {
    return module;
  }

  public void setModule(int i) {
    module = i;
  }

  public void setSelectedModule(int i) {
    index_module = i;
  }

  public int getSelectedModule() {
    return index_module;
  }

  public double getPrice() {
    return price;
  }

  public void setPrice(double i) {
    price = i;
  }

  public int getPayer() {
    return payer;
  }

  public void setPayer(int i) {
    payer = i;
  }

  public DateFr getStart() {
    return start;
  }

  public void setStart(DateFr d) {
    start = d;
  }

  public DateFr getEnd() {
    return end;
  }

  public void setEnd(DateFr d) {
    end = d;
  }

  public String getModeOfPayment() {
    return modeOfPayment;
  }

  public void setModeOfPayment(String r) {
    modeOfPayment = r;
  }

  public String getPayment() {
    return payment;
  }

  public void setPayment(String r) {
    payment = r;
  }

  public int getNOrderLines() {
    return nOrderLines;
  }

  public void setNOrderLines(int i) {
    nOrderLines = i;
  }

  public void setTitle(String s) {
    title = s;
  }

  public String getTitle() {
    return title;
  }
}

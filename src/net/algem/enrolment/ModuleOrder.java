/*
 * @(#)ModuleOrder.java	2.8.w 23/07/14
 * 
 * Copyright (c) 1999-2014 Musiques Tangentes. All Rights Reserved.
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

import java.util.ArrayList;
import java.util.List;
import net.algem.planning.DateFr;

/**
 * Module order object model.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.w
 */
public class ModuleOrder
        implements java.io.Serializable
{

  private int id;
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
  private PayFrequency payFrequency;
  private int nOrderLines;
  private String title;
  
  private List<CourseOrder> courseOrders = new ArrayList<CourseOrder>();

  @Override
  public String toString() {
    return idcmd + "," + module + "," + price + "," + start + "," + end + "," + price + "," + modeOfPayment + "," + payFrequency + "," + nOrderLines;
  }

  public int getIdOrder() {
    return idcmd;
  }

  public void setIdOrder(int i) {
    idcmd = i;
  }

  public int getId() {
    return id;
  }

  public void setId(int i) {
    id = i;
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

  public PayFrequency getPayment() {
    return payFrequency;
  }

  public void setPayment(PayFrequency p) {
    payFrequency = p;
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

  public List<CourseOrder> getCourseOrders() {
    return courseOrders;
  }

  public void setCourseOrders(List<CourseOrder> courseOrders) {
    this.courseOrders = courseOrders;
  }
  
  public void addCourseOrder(CourseOrder co) {
    courseOrders.add(co);
  }
  
  void clear() {
    courseOrders.clear();
  }
  
}

/*
 * @(#)ExtendedModuleOrder.java	2.9.4.13 08/10/15
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

import java.sql.SQLException;
import net.algem.accounting.ModeOfPayment;
import net.algem.course.Module;
import net.algem.util.DataCache;
import net.algem.util.model.Model;

/**
 * Module order with additional information. 
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.13
 * @since 2.9.2.1 16/02/15
 */
public class ExtendedModuleOrder
  extends ModuleOrder
{

  private int idper;
  private String name;
  private String firstName;
  private String nickName;
  private int completedTime;

  public ExtendedModuleOrder() {
  }

  public ExtendedModuleOrder(ModuleOrder m) {
    this.id = m.getId();
    this.idcmd = m.getIdOrder();
    this.module = m.getModule();
    this.price = m.getPrice();
    this.start = m.getStart();
    this.end = m.getEnd();
    this.modeOfPayment = m.getModeOfPayment();
    this.nOrderLines = m.getNOrderLines();
    this.payFrequency = m.getPayment();
    this.stopped = m.isStopped();
    this.pricing = m.getPricing();
    this.totalTime = m.getTotalTime();
    this.title = m.getTitle();
  }

  public int getIdper() {
    return idper;
  }

  public void setIdper(int idper) {
    this.idper = idper;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getNickName() {
    return nickName;
  }

  public void setNickName(String nickName) {
    this.nickName = nickName;
  }

  public void setCompleted(int min) {   
    completedTime = min;
  }
  
  int getCompleted() {
    return completedTime;
  }
  
  /**
   * Gets the total amount corresponding to the module order.
   * @param min time in minutes
   * @return a total amount minus any deductions
   */
  double getPaymentInfo(int min) {
    if (min >= 0 && PricingPeriod.HOUR.equals(getPricing())) {
      return getPrice() * min / 60;
    }
    //  on suppose ici que le prix est égal au tarif base
    double p = getPriceByPeriod(getPrice(), pricing);
    
    return p - (p * getReduction(getModule(), getPayment()) / 100d);
  }
  
  private double getPriceByPeriod(double p, PricingPeriod pricing) {
    switch(pricing) {
      case MNTH:
        return p * 9;
      case QTER:
        return p * 3;
      default:
        return p;
    }
  }
  
  /**
   * Gets the price reduction applied when direct debit is selected.
   * @param module module Id
   * @param frequency payment frequency
   * @return the value of the reduction
   */
  private double getReduction(int module, PayFrequency frequency) {
    if (!ModeOfPayment.PRL.name().equalsIgnoreCase(getModeOfPayment())) {
      return 0.0;
    }
    try {
      Module m = (Module) DataCache.findId(module, Model.Module);
      switch (frequency) {
        case MONTH:
          return m == null ? 0.0 : m.getMonthReducRate();
        case QUARTER:
          return m == null ? 0.0 : m.getQuarterReducRate();
        default:
          return 0.0;
      }
    } catch (SQLException ex) {
      return 0.0;
    }
  }

}

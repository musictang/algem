/*
 * @(#)ExtendedModuleOrder.java	2.9.2.1 19/02/15
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
import net.algem.course.Module;
import net.algem.util.DataCache;
import net.algem.util.model.Model;

/**
 * Module order with additional information. 
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.2.1
 * @since 2.9.2.1 16/02/15
 */
public class ExtendedModuleOrder
  extends ModuleOrder
{

  private int idper;
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
  
  void setCompleted(int min) {   
    completedTime = min;
  }
  
  int getCompleted() {
    return completedTime;
  }
  
  /**
   * 
   * @param min time in minutes
   * @return 
   */
  double getPaymentInfo(int min) {
    if (min >= 0 && PricingPeriod.HOUR.equals(getPricing())) {
      return getPrice() * min / 60;
    }
    double total = getPrice() * nOrderLines;
    return total - (total * getReduc(module, payFrequency) / 100d);
  }
  
  private double getReduc(int module, PayFrequency frequency) {
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

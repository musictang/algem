/*
 * @(#)ModuleEnrolmentNode.java 2.9.1 18/11/14
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

import java.text.NumberFormat;
import net.algem.accounting.AccountUtil;
import net.algem.planning.Hour;
import net.algem.util.BundleUtil;

/**
 * Tree node for module info.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.1
 */
public class ModuleEnrolmentNode
        extends EnrolmentNode
{

  private ModuleOrder mo;
  private String info;
  private int completed;

  public ModuleEnrolmentNode(Object o) {
    super(o);

    if (o instanceof ModuleOrder) {
      mo = (ModuleOrder) o;
    }
  }

  public ModuleOrder getModule() {
    return mo;
  }

  void setInfo(String i) {
    this.info = i;
  }

  /**
   * Sets the time corresponding to the sessions already consumed.
   * @param m time completed in minutes
   */
  public void setCompleted(int m) {
    this.completed = m;
  }

  @Override
  public String toString() {
    // TODO maybe display module type (L or P)
    return "<html>" + BundleUtil.getLabel("Module.label") + " : " + mo.getTitle()
            + (mo.getTotalTime() > 0
            ? " [" + (completed > 0 ? Hour.format(completed) : 0)
            + "/" + Hour.format(mo.getTotalTime()) + " -> " + Hour.format(mo.getTotalTime() - completed) + "]"
            + " >> [ " + getPaymentInfo(completed) + "/" + getPaymentInfo(mo.getTotalTime()) + " -> <font color='red'>" + getPaymentInfo(mo.getTotalTime() - completed)+ "</font> ]"
            : "")
            + (info != null ? info : "")
            + "</html>";
  }

  @Override
  public boolean isLeaf() {
    return false;
  }

  private String getPaymentInfo(int arg) {
    NumberFormat nf = AccountUtil.getDefaultCurrencyFormat();
    return nf.format(mo.getPrice() * arg / 60);
  }
}

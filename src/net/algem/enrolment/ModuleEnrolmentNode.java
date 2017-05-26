/*
 * @(#)ModuleEnrolmentNode.java 2.14.0 26/05/17
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
package net.algem.enrolment;

import java.text.NumberFormat;
import net.algem.accounting.AccountUtil;
import net.algem.planning.DateFr;
import net.algem.planning.Hour;
import net.algem.util.BundleUtil;

/**
 * Tree node for module info.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.14.0
 */
public class ModuleEnrolmentNode
        extends EnrolmentNode
{

  private ModuleOrder mo;
  private String info;
  private int completed;
  private DateFr lastDate;
  private NumberFormat defCurrencyFormat = AccountUtil.getDefaultCurrencyFormat();//not static here !

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

  void setLastScheduledDate(DateFr last) {
    this.lastDate = last;
  }

  @Override
  public String toString() {
    // TODO maybe display module type (L or P)
    int timeLeft = mo.getTotalTime() - completed;
    StringBuilder sb = new StringBuilder("<html>");
    sb.append(BundleUtil.getLabel("Module.label")).append(" : ").append(mo.getTitle());
    /*if (lastDate != null && !DateFr.NULLDATE.equals(lastDate.toString())) {
      sb.append(" [\u2192").append(lastDate).append("]");
    }*/
    sb.append(getProgress(timeLeft)).append(info != null ? info : "").append("</html>");
    return sb.toString();
  }

  private String getProgress(int timeLeft) {
    StringBuilder sb = new StringBuilder();
    if (mo.getTotalTime() > 0) {
      sb.append(" [")
              .append(completed > 0 ? Hour.format(completed) : 0)
              .append('/')
              .append(Hour.format(mo.getTotalTime()))
              .append(" \u2192 ")
              .append(Hour.format(timeLeft))
              .append("] \u2194 [ ")
              .append(getPaymentInfo(completed))
              .append('/')
              .append(getPaymentInfo(mo.getTotalTime()))
              .append(" \u2192 ")
              .append(getPaymentInfo(timeLeft))
              .append(" ]");
    } else {
      sb.append("");
    }
    return sb.toString();
  }

  @Override
  public boolean isLeaf() {
    return false;
  }

  private String getPaymentInfo(int arg) {
    double m = mo.getPrice() * arg / 60f;
    String p = defCurrencyFormat.format(m);
    if (arg < 0) {
      return "<font color=\"red\">" + p + "</font>";
    }
    return p;
  }
}

/*
 * @(#) SigningSheetDaySchedule.java Algem 2.15.4 18/10/2017
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
 */

package net.algem.enrolment;

import net.algem.planning.DateFr;

/**
 * Periods of absence and presence in day.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.15.4
 * @since 2.15.0 18/10/2017
 */
public class SigningSheetDaySchedule {

  private DateFr day;
  private double totalPreAM;
  private double totalPrePM;
  private double totalAbsAM;
  private double totalAbsPM;

  public DateFr getDay() {
    return day;
  }

  public void setDay(DateFr day) {
    this.day = day;
  }

  public double getTotalPreAM() {
    return totalPreAM;
  }

  public void setTotalPreAM(double totalPreAM) {
    this.totalPreAM = totalPreAM;
  }

  public double getTotalPrePM() {
    return totalPrePM;
  }

  public void setTotalPrePM(double totalPrePM) {
    this.totalPrePM = totalPrePM;
  }

  public double getTotalAbsAM() {
    return totalAbsAM;
  }

  public void setTotalAbsAM(double totalAbsAM) {
    this.totalAbsAM = totalAbsAM;
  }

  public double getTotalAbsPM() {
    return totalAbsPM;
  }

  public void setTotalAbsPM(double totalAbsPM) {
    this.totalAbsPM = totalAbsPM;
  }


}

/*
 * @(#)DailyTimes.java	1.0.4 25/05/15
 *
 * Copyright (c) 2015 Musiques Tangentes. All Rights Reserved.
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
package net.algem.planning;

/**
 * Daily representation of opening and closing times.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 1.0.4
 * @since 1.0.4 25/05/15
 */
public class DailyTimes {

  private int dow;
  private Hour opening;
  private Hour closing;

  public DailyTimes(int dow) {
    this.dow = dow;
  }

  public int getDow() {
    return dow;
  }

  public Hour getOpening() {
    return opening;
  }

  public void setOpening(Hour opening) {
    this.opening = opening;
  }

  public Hour getClosing() {
    return closing;
  }

  public void setClosing(Hour closing) {
    this.closing = closing;
  }

  @Override
  public String toString() {
    return "{" + dow + ", " + opening + ", " + closing + "}";
  }


}

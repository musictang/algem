/*
 * @(#)HourRange.java	2.8.t 15/04/14
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
package net.algem.planning;

/**
 * Time range.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.t
 */
public class HourRange
        implements java.io.Serializable
{

  private Hour start;
  private Hour end;

  public HourRange() {
  }

  public HourRange(Hour start, Hour end) {
    this.start = start;
    this.end = end;
  }

  public HourRange(String s, String e) {
    start = new Hour(s);
    end = new Hour(e);
  }

  public boolean isValid()
  {
     //XXX ajouter test +/- 1 date du jour et end-debut <= 1 an
    return start != null && end != null
            && (end.equals(start) || end.after(start));
  }

  public void setStart(Hour d) {
    start = d;
  }

  public Hour getStart() {
    return start;
  }

  public void setEnd(Hour d) {
    end = d;
  }

  public Hour getEnd() {
    return end;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final HourRange other = (HourRange) obj;
    if (this.start != other.start && (this.start == null || !this.start.equals(other.start))) {
      return false;
    }
    if (this.end != other.end && (this.end == null || !this.end.equals(other.end))) {
      return false;
    }
    return true;
  }

  public boolean overlap(Hour h1, Hour h2) {
    return (start.ge(h1) && end.le(h2))
      || (end.after(h1) && end.le(h2))
      || (start.after(h1) && start.before(h2))
      || (start.before(h1) && end.after(h2));
  }

  @Override
  public int hashCode() {
    int hash = 5;
    hash = 59 * hash + (this.start != null ? this.start.hashCode() : 0);
    hash = 59 * hash + (this.end != null ? this.end.hashCode() : 0);
    return hash;
  }

   @Override
  public String toString() {
    return start + "-" + end;
  }

}

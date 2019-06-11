/*
 * @(#)Vacation.java	2.9.4.13 05/11/15
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
package net.algem.planning;

/**
 * Day of vacation.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.13
 */
public class Vacation
        implements java.io.Serializable
{

  private static final long serialVersionUID = -2602474187968680752L;
  
  private DateFr day;
  private int type;
  private int vid;
  private String label;

  public Vacation() {
  }

  public Vacation(DateFr j, String l) {
    day = j;
    label = l;
    type = 0;
    vid = 0;
  }

  public void setDay(DateFr d) {
    day = d;
  }

  public DateFr getDay() {
    return day;
  }

  public void setLabel(String l) {
    label = l;
  }

  public String getLabel() {
    return label;
  }

  public void setType(int t) {
    type = t;
  }

  public int getType() {
    return type;
  }

  public void setVid(int i) {
    vid = i;
  }

  public int getVid() {
    return vid;
  }

  public boolean equals(DateFr d) {
    return day.equals(d);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final Vacation other = (Vacation) obj;
    if (this.day != other.day && (this.day == null || !this.day.equals(other.day))) {
      return false;
    }
    if (this.type != other.type) {
      return false;
    }
    if (this.vid != other.vid) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 13 * hash + (this.day != null ? this.day.hashCode() : 0);
    hash = 13 * hash + this.type;
    hash = 13 * hash + this.vid;
    return hash;
  }

  @Override
  public String toString() {
    return day + " " + label;
  }
  
}

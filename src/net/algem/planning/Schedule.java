/*
 * @(#)Schedule.java	2.9.4.13 05/11/15
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

import java.util.Objects;

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.13
 */
public class Schedule
        implements java.io.Serializable
{
  
  public static final int COURSE = 1;
  public static final int ACTION = 2;
  public static final int GROUP = 3;
  public static final int MEMBER = 4;
  public static final int WORKSHOP = 5;
  public static final int TRAINING = 6;
  public static final int STUDIO = 7;
  public static final int TECH = 8;
  public static final int ADMINISTRATIVE = 9;
  public static final int BOOKING_GROUP = 13;
  public static final int BOOKING_MEMBER = 14;
  
  private static final long serialVersionUID = 8525739722137960958L;
  
  protected int id;
  protected DateFr date;
  protected Hour start;
  protected Hour end;
  protected int type;
  protected int idper;
  protected int idAction;
  protected int idRoom;
  protected int note;

  @Override
  public boolean equals(Object o) {
                    if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Schedule d = (Schedule) o;
    return (type == d.type
            && idper == d.idper
            && date.equals(d.date)
            && start.equals(d.start)
            && end.equals(d.end));
  }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.date);
        hash = 67 * hash + Objects.hashCode(this.start);
        hash = 67 * hash + Objects.hashCode(this.end);
        hash = 67 * hash + this.type;
        hash = 67 * hash + this.idper;
        hash = 67 * hash + this.idAction;
        hash = 67 * hash + this.idRoom;
        hash = 67 * hash + this.note;
        return hash;
    }

  @Override
  public String toString() {
    return "Planning:" + date + " " + start + " " + end + " " + idper + " " + idAction + " " + idRoom;
  }

  public int getId() {
    return id;
  }

  public void setId(int i) {
    id = i;
  }

  public void setDate(DateFr d) {
    date = d;
  }

  public void setDate(java.util.Date d) {
    date = new DateFr(d);
  }

  public DateFr getDate() {
    return date;
  }

  public void setStart(Hour h) {
    start = h;
  }

  public Hour getStart() {
    return start;
  }

  public void setEnd(Hour h) {
    end = h;
  }

  public Hour getEnd() {
    return end;
  }

  public void setType(int i) {
    type = i;
  }

  public int getType() {
    return type;
  }

  public void setIdPerson(int i) {
    idper = i;
  }

  public int getIdPerson() {
    return idper;
  }

  public void setIdAction(int i) {
    idAction = i;
  }

  public int getIdAction() {
    return idAction;
  }

  /**
   * Sets the schedule location.
   * @param i room id
   */
  public void setIdRoom(int i) {
    idRoom = i;
  }

  /**
   * Gets the schedule location.
   * @return an integer
   */
  public int getIdRoom() {
    return idRoom;
  }

  public void setNote(int i) {
    note = i;
  }

  public int getNote() {
    return note;
  }
  
  /**
   * Gets the schedule length in minutes.
   * @return a length in minutes
   */
  public int getLength() {
    return start.getLength(end);
  }

  public boolean isValid() {
    return true;
  }


  public static String attribFromLabel(String label) {
    String attrib = label;
    switch (label) {
      case "prof":
        attrib = "idper";
        break;
      case "d.prof":
        attrib = "d.idper";
        break;
      case "salle":
        attrib = "lieux";
        break;
      case "d.salle":
        attrib = "d.lieux";
        break;
      case "cours":
        attrib = "action";
        break;
      case "d.cours":
        attrib = "d.action";
        break;
      case "atelier":
        attrib = "atelier";
        break;
      case "d.atelier":
        attrib = "d.atelier";
        break;
    }
    return attrib;
  }
  
}

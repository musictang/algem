/*
 * @(#)Schedule.java	2.8.a 24/04/13
 * 
 * Copyright (c) 1999-2013 Musiques Tangentes. All Rights Reserved.
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
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.a
 */
public class Schedule
        implements java.io.Serializable
{

  public static final int COURSE_SCHEDULE = 1;
  public static final int ACTION_SCHEDULE = 2;
  public static final int GROUP_SCHEDULE = 3;
  public static final int MEMBER_SCHEDULE = 4;
  public static final int WORKSHOP_SCHEDULE = 5;
  protected int id;
  protected DateFr date;
  protected Hour start;
  protected Hour end;
  protected int type;
  protected int idper;
  protected int idAction;
  protected int place;
  protected int note;

  public boolean equals(Schedule d) {
    return (d != null
            && type == d.type
            && idper == d.idper
            && date.equals(d.date)
            && start.equals(d.start)
            && end.equals(d.end));
  }

  @Override
  public String toString() {
    return "Planning:" + date + " " + start + " " + end + " " + idper + " " + idAction + " " + place;
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
  public void setPlace(int i) {
    place = i;
  }

  /**
   * Gets the schedule location.
   * @return an integer
   */
  public int getPlace() {
    return place;
  }

  public void setNote(int i) {
    note = i;
  }

  public int getNote() {
    return note;
  }
  
  /**
   * Gets the schedule length in minutes.
   */
  public int getLength() {
    return start.getLength(end);
  }

  public boolean isValid() {
    return true;
  }


  public static String attribFromLabel(String label) {
    String attrib = label;
    if (label.equals("prof")) {
      attrib = "idper";
    } else if (label.equals("d.prof")) {
      attrib = "d.idper";
    } else if (label.equals("salle")) {
      attrib = "lieux";
    } else if (label.equals("d.salle")) {
      attrib = "d.lieux";
    } else if (label.equals("cours")) {
      attrib = "action";
    } else if (label.equals("d.cours")) {
      attrib = "d.action";
    } else if (label.equals("atelier")) {
      attrib = "atelier";
    } else if (label.equals("d.atelier")) {
      attrib = "d.atelier";
    }
    return attrib;
  }
}

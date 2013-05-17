/*
 * @(#)ScheduleObject.java	2.6.a 19/09/12
 * 
 * Copyright (c) 1999-2012 Musiques Tangentes. All Rights Reserved.
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

import net.algem.contact.Person;
import net.algem.room.Room;

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public abstract class ScheduleObject
        extends Schedule
        implements java.io.Serializable
{

  protected Object activity;	//action
  protected Person person;	//idper
  protected Room room;		//lieux
  protected String followUp;

  public ScheduleObject() {
  }

  public ScheduleObject(Schedule d) {
    id = d.id;
    date = d.date;
    start = d.start;
    end = d.end;
    type = d.type;
    idper = d.idper;
    idAction = d.idAction;
    place = d.place;
    note = d.note;
  }

  public boolean equals(ScheduleObject d) {
    return (d != null && d.id == id);
  }

  public void setPerson(Person p) {
    person = p;
    if (p == null) {
      idper = 0;
    } else {
      idper = p.getId();
    }
  }

  public Person getPerson() {
    return person;
  }

  public void setActivity(Object o) {
    activity = o;
  }

  public Object getActivity() {
    return activity;
  }

  public void setRoom(Room s) {
    room = s;
    if (s == null) {
      place = 0;
    } else {
      place = s.getId();
    }
  }

  public Room getRoom() {
    return room;
  }

   public void setFollowUp(String s) {
    followUp = s;
  }

  public String getFollowUp() {
    return followUp;
  }

  abstract public String getScheduleLabel();

  abstract public String getScheduleDetail();
}

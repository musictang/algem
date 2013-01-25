/*
 * @(#)WorkshopSchedule.java	2.6.a 20/09/12
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
import net.algem.course.Course;

/**
 * Workshop schedule.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class WorkshopSchedule
        extends ScheduleObject
{

  public WorkshopSchedule() {
  }

  public WorkshopSchedule(Schedule d) {
    super(d);
  }

  public void setTeacher(Person p) {
    person = p;
    if (p == null) {
      idper = 0;
    } else {
      idper = p.getId();
    }
  }

  public Person getTeacher() {
    return person;
  }

  public void setWorkshop(Course a) {
    activity = a;
  }

  public Course getWorkshop() {
    return (Course) activity;
  }

  @Override
  public String getScheduleLabel() {
    return ((Course) activity).getTitle();
  }

  @Override
  public String getScheduleDetail() {
    return "Atelier :" + ((Course) activity).getTitle() + "/" + person;
  }
}

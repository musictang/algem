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

import java.util.Calendar;
import java.util.Locale;
import net.algem.contact.Person;
import net.algem.course.Course;

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class CourseSchedule
        extends ScheduleObject
{

  private Action action;
  
  public CourseSchedule() {
  }

  public CourseSchedule(Schedule d) {
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

  public void setCourse(Course c) {
    activity = c;
  }

  public Course getCourse() {
    return (Course) activity;
  }

  public Action getAction() {
    return action;
  }

  public void setAction(Action action) {
    this.action = action;
  }

  @Override
  public String getScheduleLabel() {
    return ((Course) activity).getTitle();
  }

  @Override
  public String getScheduleDetail() {
    return "Cours:" + ((Course) activity).getTitle() + "/" + person;
  }

  public boolean equiv(ScheduleObject d) {
    Calendar cal1 = Calendar.getInstance(Locale.FRANCE);
    cal1.setTime(this.getDay().getDate());
    int this_dow = cal1.get(Calendar.DAY_OF_WEEK);

    Calendar cal2 = Calendar.getInstance(Locale.FRANCE);
    cal2.setTime(d.getDay().getDate());
    int other_dow = cal2.get(Calendar.DAY_OF_WEEK);

    return (d != null
            && type == d.type
            && idper == d.idper
            && place == d.getPlace()
            && ( idAction == d.getIdAction() || ((Course)getActivity()).equals((Course)d.getActivity()))
            && this_dow == other_dow
            && start.le(d.start)
            && end.ge(d.end));//plutot que equals
  }
}

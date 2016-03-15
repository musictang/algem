/*
 * @(#)ScheduleObject.java	2.9.6 15/03/16
 *
 * Copyright (c) 1999-2016 Musiques Tangentes. All Rights Reserved.
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
 * @version 2.9.6
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
    idper = p == null ? 0 : p.getId();
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
    cal1.setTime(this.getDate().getDate());
    int this_dow = cal1.get(Calendar.DAY_OF_WEEK);

    Calendar cal2 = Calendar.getInstance(Locale.FRANCE);
    cal2.setTime(d.getDate().getDate());
    int other_dow = cal2.get(Calendar.DAY_OF_WEEK);

    return (d != null
            && type == d.type
            && idper == d.idper
            && idRoom == d.getIdRoom()
            && idAction == d.getIdAction()
            && ((Course)getActivity()).equals((Course)d.getActivity())
//            && ( idAction == d.getIdAction() || ((Course)getActivity()).equals((Course)d.getActivity()))
            && this_dow == other_dow
            && start.le(d.start)
            && end.ge(d.end));//plutot que bufferEquals
  }
}

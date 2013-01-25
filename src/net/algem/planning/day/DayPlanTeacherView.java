/*
 * @(#)DayPlanTeacherView.java 2.7.a 26/11/12
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
package net.algem.planning.day;

import java.util.Vector;
import net.algem.contact.Person;
import net.algem.contact.teacher.Teacher;
import net.algem.planning.ScheduleObject;
import net.algem.planning.ScheduleRangeObject;
import net.algem.util.BundleUtil;
import net.algem.util.model.GemList;

/**
 * Day view by teacher.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">jean-marc Gobat</a>
 * @version 2.7.a
 * @since 1.0a 07/07/1999
 */
public class DayPlanTeacherView
        extends DayPlanTableView
{

  private GemList<Teacher> teacherList;

  public DayPlanTeacherView(GemList<Teacher> list) {
    super(BundleUtil.getLabel("Teacher.label"));
    teacherList = list;
  }

  @Override
  public void load(java.util.Date d, Vector<ScheduleObject> schedules, Vector<ScheduleRangeObject> ranges) {

    int cpt = 0;

    plan.clear();
    plan.setDate(d);
    date.set(d);

    for (int i = 0; i < teacherList.getSize(); i++) {
      Teacher t = (Teacher) teacherList.getElementAt(i);
      Vector<ScheduleObject> v1 = getSchedule(schedules, t.getId());
      Vector<ScheduleRangeObject> v2 = getSchedule(ranges, t.getId());

      if ((v1.size() + v2.size()) > 0) {
        DayPlan pj = new DayPlan();
        pj.setId(t.getId());
        pj.setLabel(t.getName());
        pj.setSchedule(v1);
        pj.setScheduleRange(v2);

        plan.addCol(pj);

        cpt++;
      }
    }

    plan.repaint();
    setBar();
  }

  public <T extends ScheduleObject> Vector<T> getSchedule(Vector<T> t, int teacherId) {
    Vector<T> v = new Vector<T>();

    for (int i = 0; i < t.size(); i++) {
      Person p = t.elementAt(i).getPerson();
      if (p != null && p.getId() == teacherId) {
        v.addElement(t.elementAt(i));
      }
    }
    return v;
  }
}

/*
 * @(#)MonthPlanCourseView.java	2.6.a 21/09/12
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
package net.algem.planning.month;

import net.algem.course.Course;
import net.algem.planning.CourseSchedule;
import net.algem.planning.ScheduleDetailEvent;
import net.algem.planning.ScheduleObject;
import net.algem.util.ui.GemChoice;

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class MonthPlanCourseView
        extends MonthPlanDetailView
{

  boolean collective;

  public MonthPlanCourseView(GemChoice course, boolean c) {
    super(course);
    collective = c;
  }

  @Override
  public boolean isFiltered(ScheduleObject p) {
    if (!(p instanceof CourseSchedule)) {
      return false;
    }

    Course cs = (Course) choice.getSelectedItem();
    CourseSchedule pc = (CourseSchedule) p;
    return cs != null && pc.getCourse() != null
            && pc.getCourse().getId() == cs.getId()
            && pc.getCourse().isCollective() == collective;
  }

  @Override
  public void detailChange(ScheduleDetailEvent evt) {
    if (evt.getSchedule() instanceof CourseSchedule) {
      CourseSchedule pc = (CourseSchedule) evt.getSchedule();
      choice.setKey(pc.getCourse().getId());
    }
  }
}

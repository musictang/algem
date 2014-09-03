/*
 * @(#)ModifPlanCourseView.java 2.8.w 02/09/14
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
package net.algem.planning.editing;

import java.awt.Dimension;
import java.sql.SQLException;
import java.util.logging.Level;
import net.algem.course.Course;
import net.algem.course.CourseChoice;
import net.algem.planning.DateFr;
import net.algem.planning.PlanningService;
import net.algem.planning.ScheduleObject;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.GemLogger;
import net.algem.util.MessageUtil;
import net.algem.util.model.Model;
import net.algem.util.ui.GemLabel;
import net.algem.util.ui.GridBagHelper;

/**
 * View for course modification.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.w
 * @since 2.4.b 30/05/12
 */
class ModifPlanCourseView
        extends ModifPlanView
{

  private CourseChoice course;

  public ModifPlanCourseView(String label, DataCache dataCache) {
    super(dataCache, label);

    course = new CourseChoice(dataCache.getList(Model.Course));
    Dimension prefSize = new Dimension(courseLabel.getPreferredSize().width, course.getPreferredSize().height);
    course.setPreferredSize(prefSize);
    gb.add(new GemLabel(BundleUtil.getLabel("New.course.label")), 0, 2, 1, 1, GridBagHelper.WEST);
    gb.add(course, 1, 2, 2, 1, GridBagHelper.WEST);
  }

  public DateFr getDateStart() {
    return dateRange.getStartFr();
  }

  public DateFr getDateEnd() {
    return dateRange.getEndFr();
  }

  public int getCourse() {
    return course.getKey();
  }

  public void set(ScheduleObject plan) {
    setTitle(plan.getScheduleLabel());
    setId(((Course) plan.getActivity()).getId());
    dateRange.setStart(plan.getDate());
    try {
      dateRange.setEnd(PlanningService.getLastDate(plan.getIdAction(), DataCache.getDataConnection()));
    } catch (SQLException ex) {
      GemLogger.log(Level.WARNING, ex.getMessage());
      dateRange.setEnd(dataCache.getEndOfYear());//fin du cours par défaut
    }
    dateRange.setEnabled(false, 1);
    dateRange.setToolTipText(MessageUtil.getMessage("end.time.slot.info.tip"));
  }

  @Override
  public void setId(int i) {
    course.setKey(i);
  }

  @Override
  public int getId() {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}

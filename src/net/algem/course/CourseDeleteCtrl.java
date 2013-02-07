/*
 * @(#)CourseDeleteCtrl.java	2.7.e 04/02/13
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
package net.algem.course;

import java.awt.event.ActionEvent;
import java.sql.SQLException;
import net.algem.config.ParamTableIO;
import net.algem.config.SchoolCtrl;
import net.algem.planning.Schedule;
import net.algem.planning.ScheduleIO;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;
import net.algem.util.GemLogger;
import net.algem.util.MessageUtil;
import net.algem.util.model.Model;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.CardCtrl;
import net.algem.util.ui.MessagePopup;

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.e
 */
public class CourseDeleteCtrl
        extends CardCtrl
{

  private DataConnection dc;
  private GemDesktop desktop;
  private CourseView view;
  private Course course;

  public CourseDeleteCtrl(GemDesktop desktop) {
    this.desktop = desktop;
    this.dc = desktop.getDataCache().getDataConnection();
    view = new CourseView(desktop.getDataCache().getModuleType(),
            ParamTableIO.find(SchoolCtrl.TABLE, SchoolCtrl.SORT_COLUMN, dc));

    addCard("FicheCours", view);
    select(0);
  }

  @Override
  public boolean next() {
    switch (step) {
      default:
        select(step + 1);
        break;
    }
    return true;
  }

  @Override
  public boolean cancel() {
    if (actionListener != null) {
      actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "CtrlAbandon"));
    }
    return true;
  }

  @Override
  public boolean prev() {
    switch (step) {
      default:
        select(step - 1);
        break;
    }
    return true;
  }

  @Override
  public boolean validation() {
    if (course == null) {
      return false;
    }

    try {
      dc.setAutoCommit(false);
      String where = ", action a WHERE p.ptype = " + Schedule.COURSE_SCHEDULE + " AND p.action = a.id AND a.cours = " + course.getId();
      if (ScheduleIO.findCourse(where, dc).size() > 0) {
        MessagePopup.warning(this, MessageUtil.getMessage("course.suppression.warning"));
        return false;
      }
      ((CourseIO) DataCache.getDao(Model.Course)).delete(course);
      desktop.getDataCache().remove(course);
      desktop.postEvent(new CourseDeleteEvent(this, course));
//			dc.executeUpdate("DELETE FROM planning WHERE ptype=" + Schedule.COURSE_SCHEDULE + " AND action=" + cours.getId());
//			dc.executeUpdate("delete from plage where cours=" + cours.getId());
      dc.commit();
    } catch (Exception ex) {
      dc.rollback();
      GemLogger.logException("delete cours", ex, this);
      return false;
    } finally {
      dc.setAutoCommit(true);
    }
    if (actionListener != null) {
      actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "CtrlValider"));
    }
    return true;
  }

  public void clear() {
    view.clear();
  }

  @Override
  public boolean loadCard(Object o) {
    clear();
    select(0);
    if (o == null || !(o instanceof Course)) {
      return false;
    }

    course = (Course) o;
    view.set(course);
    return true;
  }

  @Override
  public boolean loadId(int id) {
    try {
      return loadCard(((CourseIO) DataCache.getDao(Model.Course)).findId(id));
    } catch (SQLException ex) {
      GemLogger.log(getClass().getName(), "loadId", ex);
    }
    return false;
  }

  Course getCourse() {
    return view.get();
  }
}

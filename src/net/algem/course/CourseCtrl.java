/*
 * @(#)CourseCtrl.java	2.7.a 26/11/12
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
import net.algem.enrolment.CourseEnrolmentView;
import net.algem.enrolment.EnrolmentService;
import net.algem.util.DataCache;
import net.algem.util.GemLogger;
import net.algem.util.model.Model;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.CardCtrl;

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.a
 */
public class CourseCtrl
        extends CardCtrl {

  private DataCache dataCache;
  private CourseView cv;
  private CourseEnrolmentView iv;
  private Course course;
  private EnrolmentService service;
  private GemDesktop desktop;

  public CourseCtrl(GemDesktop desktop) {
    this.desktop = desktop;
    dataCache = desktop.getDataCache();
    service = new EnrolmentService(dataCache);

    cv = new CourseView(dataCache.getModuleType(), 
			ParamTableIO.find(SchoolCtrl.TABLE, SchoolCtrl.SORT_COLUMN, dataCache.getDataConnection()));
    iv = new CourseEnrolmentView(service);

    addCard("cours", cv);
    addCard("Elèves inscrits", iv);

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
      actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "Abandon"));
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
    course = cv.get();
    if (course == null) {
      return false;
    }

    try {
      ((CourseIO) DataCache.getDao(Model.Course)).update(course);
      dataCache.update(course);
      desktop.postEvent(new CourseUpdateEvent(this, course));
    } catch (SQLException e1) {
      GemLogger.logException("Update cours", e1, this);
      return false;
    }
    if (actionListener != null) {
      actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "Abandon"));
    }
    return true;
  }

  public void clear() {
    cv.clear();
    iv.clear();
  }

  @Override
  public boolean loadCard(Object o) {
    clear();
    select(0);
    if (o == null || !(o instanceof Course)) {
      return false;
    }

    course = (Course) o;

    cv.set(course);
    iv.load(course.getId());
    return true;
  }

  @Override
  public boolean loadId(int id) {
    try {
      return loadCard(((CourseIO) DataCache.getDao(Model.Course)).findId(id));
    } catch (SQLException ex) {
      System.err.println(getClass().getName() + "#loadId :" + ex.getMessage());
    }
    return false;
  }

  Course getCours() {
    return cv.get();
  }
}

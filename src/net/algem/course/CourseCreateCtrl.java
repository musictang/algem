/*
 * @(#)CourseCreateCtrl.java 2.7.a 26/11/12
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
public class CourseCreateCtrl
        extends CardCtrl {

  private GemDesktop desktop;
  private DataCache dataCache;
  private CourseView cv;
  private Course course;

  public CourseCreateCtrl(GemDesktop _desktop) {
    desktop = _desktop;
    dataCache = desktop.getDataCache();
  }

  public void init() {
    cv = new CourseView(dataCache.getModuleType(), 
			ParamTableIO.find(SchoolCtrl.TABLE, SchoolCtrl.SORT_COLUMN, dataCache.getDataConnection()));
    addCard("Descriptif du cours", cv);
    select(0);
  }

  @Override
  public boolean prev() {
    select(step - 1);
    return true;
  }

  @Override
  public boolean next() {
    select(step + 1);
    return true;
  }

  @Override
  public boolean cancel() {
    clear();
    if (actionListener != null) {
      actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "Abandon"));
    }
    return true;
  }

  public void clear() {
    cv.clear();
    select(0);
  }

  @Override
  public boolean loadCard(Object c) {
    return false;
  }

  @Override
  public boolean loadId(int id) {
    return false;
  }

  @Override
  public boolean validation() {
    course = cv.get();

    try {
      ((CourseIO) DataCache.getDao(Model.Course)).insert(course);
      dataCache.add(course);
      desktop.postEvent(new CourseCreateEvent(this, course));
    } catch (SQLException ex) {
      GemLogger.logException("Insertion cours", ex, this);
      return false;
    }
    clear();
    return true;
  }
}

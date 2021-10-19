/*
 * @(#)WorkshopScheduleCtrl.java	2.12.0 08/03/17
 *
 * Copyright (c) 1999-2017 Musiques Tangentes. All Rights Reserved.
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

import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import net.algem.course.Course;
import net.algem.planning.editing.ModifPlanEvent;
import net.algem.util.*;
import net.algem.util.model.Model;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.CardCtrl;
import net.algem.util.ui.MessagePopup;

/**
 * Single workshop scheduling.
 * Only one session is scheduled.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.12.0
 * @since 1.0a 07/07/1999
 */
public class WorkshopScheduleCtrl
        extends CardCtrl
{

  public static final String WORKSHOP_SCHEDULING_KEY = "Workshop.scheduling";
  private WorkshopScheduleView rv;
  private final GemDesktop desktop;
  private final DataConnection dc;
  private final PlanningService service;

  public WorkshopScheduleCtrl(GemDesktop desktop) {
    this.desktop = desktop;
    dc = DataCache.getDataConnection();
    service = new PlanningService(dc);
  }

  public void init() {
    rv = new WorkshopScheduleView(desktop.getDataCache());
    //addCard(MessageUtil.getMessage("workshop.planification"), rv);
    addCard("", rv);
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
      actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, GemCommand.CANCEL_CMD));
    }
    return true;
  }

  public void clear() {
    rv.clear();
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
    try {
      save();
      desktop.postEvent(new ModifPlanEvent(this, rv.getDate(), rv.getDate()));
      return cancel();
    } catch (PlanningException ex) {
      MessagePopup.warning(contentPane, ex.getMessage());
      return false;
    }
  }

  public void save() throws PlanningException {
    Action a = getActionFromView(rv);
    service.plan(a, Schedule.WORKSHOP);
  }

  private Action getActionFromView(WorkshopScheduleView rv) throws PlanningException {
    int w = rv.getWorkshop();
    int r = rv.getRoom();
    int t = rv.getTeacher();
    Hour hStart = rv.getHourStart();
    Hour hEnd = rv.getHourEnd();
    Course c = null;
    try {
      c = (Course) DataCache.findId(w, Model.Course);
    } catch (SQLException ex) {
      GemLogger.logException(ex);
    }
    if (c == null || c.isUndefined() || c.getId() == 0) {
      throw new PlanningException(MessageUtil.getMessage("invalid.course.selection"));
    }
    if (hStart.equals(hEnd) || hEnd.before(hStart)) {
      throw new PlanningException(MessageUtil.getMessage("hour.range.error"));
    }
    if (r == 0) {
      throw new PlanningException(MessageUtil.getMessage("room.invalid.choice"));
    }
    if (t == 0 && !MessagePopup.confirm(contentPane, MessageUtil.getMessage("teacher.undefined.confirmation"))) {
      throw new PlanningException(MessageUtil.getMessage("invalid.teacher"));
    }

    String query = ConflictQueries.getRoomTeacherConflictSelection(rv.getDate().toString(), hStart.toString(), hEnd.toString(), r, t);

    if (ScheduleIO.count(query, dc) > 0) {
      throw new PlanningException(MessageUtil.getMessage("busy.room.teacher.warning"));
    }

    Action a = new Action();
    a.setStartTime(hStart);
    a.setEndTime(hEnd);
    a.setIdper(t);
    a.setRoom(r);
    a.setCourse(w);
    List<DateFr> dates = new ArrayList<DateFr>();
    dates.add(rv.getDate());
    a.setDates(dates);

    return a;
  }

}

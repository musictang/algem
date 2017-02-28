/*
 * @(#)CourseScheduleCtrl.java	2.9.7.2 30/05/16
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

import java.awt.event.ActionEvent;
import java.util.*;
import javax.swing.JOptionPane;
import net.algem.course.Course;
import net.algem.planning.editing.ModifPlanEvent;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;
import net.algem.util.GemCommand;
import net.algem.util.MessageUtil;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.CardCtrl;
import net.algem.util.ui.MessagePopup;

/**
 * Course schedule controller.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.7.2
 * @since 1.0a 07/07/1999
 */
public class CourseScheduleCtrl
        extends CardCtrl {

  public static final String COURSE_SCHEDULING_KEY="Course.scheduling";
  protected GemDesktop desktop;
  protected DataConnection dc;
  protected ActionView av;
  protected ConflictListView conflictsView;
  protected List<Action> actions;
  protected PlanningService service;

  public CourseScheduleCtrl(GemDesktop desktop) {
    this.desktop = desktop;
    dc = DataCache.getDataConnection();
    service = new PlanningService(dc);
  }

  public void init() {
    av = new ActionView(desktop);
    av.init();

    conflictsView = new ConflictListView();

    addCard(MessageUtil.getMessage("planning.session.init"), av);
    addCard(BundleUtil.getLabel("Conflict.verification.label"), conflictsView);
    select(0);
  }

  @Override
  public boolean prev() {
    select(step - 1);
    return true;
  }

  private Action validate(ActionView v) throws PlanningException {

    String msg = "";
    Course c = v.getCourse();
    if (c == null || c.isUndefined()) {
      msg += MessageUtil.getMessage("course.invalid.choice");
    }
    if (c != null && c.isCourseCoInst() && v.getCourseLength() < 15) { // 15 minutes mini
      msg += "\n" + MessageUtil.getMessage("course.length.warning");
    }
    if (!v.hasValidLength()) {
      msg += "\n" + MessageUtil.getMessage("hour.range.error");
    }
    Action a = v.get();
    if (a.getIdper() == 0 && !MessagePopup.confirm(this, MessageUtil.getMessage("teacher.undefined.confirmation"))) {
      msg += "\n" + MessageUtil.getMessage("teacher.invalid.choice");
    } 
    if (a.getRoom() == 0) {
      msg += "\n" + MessageUtil.getMessage("room.invalid.choice");
    }
    //if (action.getNSessions() == 0 || action.getNSessions() > 33)
    if (a.getNSessions() == 0) {
      msg += "\n" + MessageUtil.getMessage("invalid.session.number");
    }
    if (!msg.isEmpty()) {
      throw new PlanningException(msg);
    }
    return a;
  }

  @Override
  public boolean next() {
    desktop.setWaitCursor();
    select(step + 1);
    if (step == 1) {
      Action action = null;
      String t = MessageUtil.getMessage("invalid.choice");
      try {
        action = validate(av);
      } catch (PlanningException pe) {
        desktop.setDefaultCursor();
        JOptionPane.showMessageDialog(this, pe.getMessage(), t, JOptionPane.ERROR_MESSAGE);
        return prev();
      }
      actions = getPlanification(action, av.getIntervall());

      int n = 0;
      conflictsView.clear();
      for (Action a : actions) {
        n += testConflict(a);
      }
      if (n > 0) {
        btNext.setText("");//bouton validation
      }
    }
    desktop.setDefaultCursor();
    return true;
  }

  List<Action> getPlanification(Action action, int interval) {
    Hour end = action.getEndTime();
    List<Action> v = new ArrayList<Action>();

    if (action.getLength() > 15) {
      Hour start = new Hour(action.getStartTime());
      action.setEndTime(start.end(action.getLength()));
      action.setDates(service.generationDate(action));
      v.add(action);
      start = action.getEndTime();
      while (start.before(end)) {
        Action a = new Action(action);
        a.setStartTime(start.end(interval));
        a.setEndTime(a.getStartTime().end(action.getLength()));
        if (a.getEndTime().after(end)) {
          break;
        }
        a.setDates(service.generationDate(a));
        v.add(a);
        start = a.getEndTime();
      }
    } else { // course length is undefined
      action.setDates(service.generationDate(action));
      v.add(action);
    }
    return v;
  }

  @Override
  public boolean cancel() {
    clear();
    if (actionListener != null) {
      cleanUp();
      actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, GemCommand.CANCEL_CMD));
    }
    return true;
  }

  public void clear() {
    av.clear();
    conflictsView.clear();
    select(0);
  }

   private void cleanUp() {
    desktop.removeGemEventListener(av);
    actions = null;
    av = null;
    conflictsView = null;
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
    boolean r = save();
    if (r) {
      desktop.postEvent(new ModifPlanEvent(this,
              actions.get(0).getStartDate(),
              actions.get(actions.size() - 1).getEndDate()));
    }
    clear();
    return r;

  }

  /**
   * Test room or teacher occupation.
   *
   * @param a action to check
   * @return the number of conflicts
   */
  public int testConflict(Action a) {

    int room = a.getRoom();
    int teacher = a.getIdper();

    Hour hStart = a.getStartTime();
    Hour hEnd = a.getEndTime();

    int conflicts = 0;
    for (DateFr d : a.getDates()) {
      ScheduleTestConflict conflict = new ScheduleTestConflict(d, hStart, hEnd);
      // test salles
      String query = ConflictQueries.getRoomConflictSelection(d.toString(), hStart.toString(), hEnd.toString(), room);

      if (ScheduleIO.count(query, dc) > 0) {
        conflict.setRoomFree(false);
        conflicts++;
      }
      // test prof
      if (teacher > 0) {
        query = ConflictQueries.getTeacherConflictSelection(d.toString(), hStart.toString(), hEnd.toString(), teacher);
        if (ScheduleIO.count(query, dc) > 0) {
          conflict.setTeacherFree(false);
          conflicts++;
        }
      }
      conflictsView.addConflict(conflict);

    }
    return conflicts;
  }

  public boolean save() {
    desktop.setWaitCursor();
    for (Action a : actions) {
      if (a.getDates().isEmpty()) {
        MessagePopup.error(this, MessageUtil.getMessage("empty.planning.create.warning"));
        return false;
      }
    }
    try {
      service.plan(actions);
      desktop.setDefaultCursor();
      return true;
    } catch (PlanningException ex) {
      desktop.setDefaultCursor();
      MessagePopup.warning(this,
              MessageUtil.getMessage("planning.course.create.exception") + " :\n" + ex.getMessage());
      return false;
    }

  }

  /**
   * Calendar.DAY_OF_WEEK order : SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY,
   * FRIDAY, and SATURDAY. SUNDAY is représented by 1, SATURDAY by 7.
   *
   * @param jour day of week
   * @param _debut start date
   * @param _fin end date
   * @param _maxi max of sessions
   * @param vtype holydays type
   * @return a list of dates
   */
  Vector<DateFr> generationDate(int jour, DateFr _debut, DateFr _fin, int _maxi, int vid) {

    Vector<DateFr> v = new Vector<DateFr>();

    int i = 0; // nombre de séances
    Calendar start = Calendar.getInstance(Locale.FRANCE);
    start.setTime(_debut.getDate());
    Calendar end = Calendar.getInstance(Locale.FRANCE);
    end.setTime(_fin.getDate());

    while (!start.after(end) && i < _maxi) {
      if (start.get(Calendar.DAY_OF_WEEK) == jour + 1) {// si le jour de la semaine correspond
        if (VacationIO.findDay(start.getTime(), vid, dc) == null) {
          // si cette date ne correspond pas à un jour de vacances du type sélectionné
          i++;
          v.addElement(new DateFr(start.getTime()));
        }
      }
      start.add(Calendar.DATE, 1); // on incrémente d'un jour
    }
    return v;
  }
}

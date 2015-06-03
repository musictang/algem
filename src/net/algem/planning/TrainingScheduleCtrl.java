/*
 * @(#)TrainingScheduleCtrl.java	2.9.4.6 02/06/15
 *
 * Copyright (c) 1999-2015 Musiques Tangentes. All Rights Reserved.
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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import net.algem.course.Course;
import net.algem.planning.editing.ModifPlanEvent;
import net.algem.util.*;
import net.algem.util.model.Model;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.CardCtrl;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.MessagePopup;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.6
 * @since 2.8.t 11/04/14
 */
public class TrainingScheduleCtrl
        extends CardCtrl
{

  public static final String TRAINING_SCHEDULING_KEY="Training.course.scheduling";
  private final GemDesktop desktop;
  private final DataConnection dc;
  private final PlanningService service;
  private TrainingScheduleView trainingView;
  protected ConflictListView conflictsView;
  private Action action;
  private List<GemDateTime> dates;

  public TrainingScheduleCtrl(GemDesktop desktop) {
    this.desktop = desktop;
    dc = DataCache.getDataConnection();
    service = new PlanningService(dc);
  }

  public void init() {
    trainingView = new TrainingScheduleView(desktop.getDataCache());
    JScrollPane scroll = new JScrollPane(trainingView);
    scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
    GemPanel gp = new GemPanel(new BorderLayout());
    gp.add(scroll, BorderLayout.CENTER);

    conflictsView = new ConflictListView();
    addCard(null, gp);
    addCard(BundleUtil.getLabel("Conflict.verification.label"), conflictsView);
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
    if (step == 1) {
      String t = MessageUtil.getMessage("invalid.choice");
      try {
        action = checkAction();
      } catch (PlanningException pe) {
        JOptionPane.showMessageDialog(this, pe.getMessage(), t, JOptionPane.ERROR_MESSAGE);
        return prev();
      }
      conflictsView.clear();
      int n = testConflicts(action);
      if (n > 0) {
        btNext.setText("");//bouton validation
      }
    }
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

  private void clear() {
    action = null;
    trainingView.clear();
    conflictsView.clear();
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
      GemDateTime dts = dates.get(0);
      GemDateTime dte = dates.get(dates.size() - 1);
      desktop.postEvent(new ModifPlanEvent(this, dts.getDate(), dte.getDate()));
    } catch (PlanningException ex) {
      MessagePopup.warning(this, ex.getMessage());
      return false;
    }
    clear();
    return cancel();
  }

  private void save() throws PlanningException {
    service.plan(action, Schedule.TRAINING, dates);
  }

  private Action checkAction() throws PlanningException {
    int course = trainingView.getCourse();
    int room = trainingView.getRoom();
    int teacher = trainingView.getTeacher();
    Course c = null;
    try {
      c = (Course) DataCache.findId(course, Model.Course);
    } catch (SQLException ex) {
      GemLogger.logException(ex);
    }
    if (c == null || c.isUndefined() || c.getId() == 0) {
      throw new PlanningException(MessageUtil.getMessage("invalid.course.selection"));
    }

    dates = trainingView.getDates();
    // check duplicates
    Set<GemDateTime> uniques = new HashSet<GemDateTime>(dates);
    if (uniques.size() < dates.size()) {
      throw new PlanningException(MessageUtil.getMessage("time.duplication"));
    }
    // check overlapping
    if (PlanificationUtil.hasOverlapping(dates)) {
      throw new PlanningException(MessageUtil.getMessage("time.overlapping"));
    }

    for (GemDateTime dt : dates) {
      HourRange hr = dt.getTimeRange();
      if (hr.getStart().equals(hr.getEnd()) || hr.getEnd().before(hr.getStart())) {
        throw new PlanningException(MessageUtil.getMessage("hour.range.error"));
      }
    }
    if (room == 0) {
      throw new PlanningException(MessageUtil.getMessage("room.invalid.choice"));
    }
    if (teacher == 0) {
      throw new PlanningException(MessageUtil.getMessage("invalid.teacher"));
    }
    Action a = new Action();
    a.setIdper(teacher);
    a.setRoom(room);
    a.setCourse(course);

    return a;
  }

  private int testConflicts(Action a) {
    int conflicts = 0;
    for (GemDateTime dt : dates) {
      DateFr d = dt.getDate();
      Hour start = dt.getTimeRange().getStart();
      Hour end = dt.getTimeRange().getEnd();
      ScheduleTestConflict testConflict = new ScheduleTestConflict(d, start, end);
      String query = ConflictQueries.getRoomConflictSelection(d.toString(), start.toString(), end.toString(), a.getRoom());

      if (ScheduleIO.count(query, dc) > 0) {
        testConflict.setRoomFree(false);
        conflicts++;
      }
      // test prof
      query = ConflictQueries.getTeacherConflictSelection(d.toString(), start.toString(), end.toString(), a.getIdper());
      if (ScheduleIO.count(query, dc) > 0) {
        testConflict.setTeacherFree(false);
        conflicts++;
      }
      conflictsView.addConflict(testConflict);
    }
    return conflicts;
  }
}

/*
 * @(#)TrainingScheduleCtrl.java	2.12.0 07/03/17
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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.JOptionPane;
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
 * @version 2.12.0
 * @since 2.8.t 11/04/14
 */
public class TrainingScheduleCtrl
        extends CardCtrl
        implements UpdateConflictListener
{

  public static final String TRAINING_SCHEDULING_KEY = "Training.course.scheduling";
  private final GemDesktop desktop;
  private final DataConnection dc;
  private final PlanningService service;
  private TrainingScheduleView trainingView;
  protected ConflictListView conflictsView;
  private Action action;

  public TrainingScheduleCtrl(GemDesktop desktop) {
    this.desktop = desktop;
    this.dc = DataCache.getDataConnection();
    this.service = new PlanningService(dc);
  }

  public void init() {
    trainingView = new TrainingScheduleView(desktop.getDataCache());
    GemPanel gp = new GemPanel(new BorderLayout());
    gp.add(trainingView, BorderLayout.CENTER);

    conflictsView = new ConflictListView(new ConflictTableModel(service));
    conflictsView.addUpdateConflictListener(this);

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
      conflictsView.clear();
      String t = MessageUtil.getMessage("invalid.choice");
      try {
        action = createAction();
        Set<GemDateTime> checked = checkDates(trainingView.getDates());
        List<DateFr> dateList = new ArrayList<>();
        for (GemDateTime dt : checked) {
          dateList.add(dt.getDate());
        }
        action.setDates(dateList);
        int n = testConflicts(action, checked);
        if (n > 0) {
          btNext.setText("");//lock validation
        }
      } catch (PlanningException pe) {
        JOptionPane.showMessageDialog(this, pe.getMessage(), t, JOptionPane.ERROR_MESSAGE);
        return prev();
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
      DateFr[] fromTo = save();
      desktop.postEvent(new ModifPlanEvent(this, fromTo[0], fromTo[1]));
    } catch (PlanningException ex) {
      MessagePopup.warning(this, ex.getMessage());
      return false;
    }
    clear();
    return cancel();
  }

  @Override
  public void update(boolean unlock) {
    if (unlock) {
      btNext.setText(GemCommand.VALIDATE_CMD);
    } else {
      btNext.setText("");
    }
  }

  private DateFr[] save() throws PlanningException {
    List<ScheduleTestConflict> resolved = conflictsView.getResolvedConflicts();
    List<GemDateTime> dates = new ArrayList<>();
    for (ScheduleTestConflict c : resolved) {
      GemDateTime dt = new GemDateTime(c.getDate(), new HourRange(c.getStart(), c.getEnd()));
      dates.add(dt);
    }
    
    Set<GemDateTime> checked = checkDates(dates);
    List<GemDateTime> toSave = new ArrayList(checked);
    service.plan(action, Schedule.TRAINING, new ArrayList(toSave));
    return new DateFr[]{toSave.get(0).getDate(), toSave.get(toSave.size() - 1).getDate()};
  }

  private Action createAction() throws PlanningException {
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

    if (room == 0) {
      throw new PlanningException(MessageUtil.getMessage("room.invalid.choice"));
    }
    if (teacher == 0 && !MessagePopup.confirm(this, MessageUtil.getMessage("teacher.undefined.confirmation"))) {
      throw new PlanningException(MessageUtil.getMessage("invalid.teacher"));
    }

    Action a = new Action();
    a.setIdper(teacher);
    a.setRoom(room);
    a.setCourse(course);
    
    return a;
  }

  private Set<GemDateTime> checkDates(List<GemDateTime> dates) throws PlanningException {
    // check duplicates
    Set<GemDateTime> uniques = new TreeSet<GemDateTime>(dates);
    if (uniques.size() < dates.size()) {
      throw new PlanningException(MessageUtil.getMessage("time.duplication"));
    }
    // check overlapping
    if (PlanificationUtil.hasOverlapping(dates)) {
      throw new PlanningException(MessageUtil.getMessage("time.overlapping"));
    }

    for (GemDateTime dt : uniques) {
      HourRange hr = dt.getTimeRange();
      if (hr.getStart().equals(hr.getEnd()) || hr.getEnd().before(hr.getStart())) {
        throw new PlanningException(MessageUtil.getMessage("hour.range.error"));
      }
    }
    return uniques;
  }

  private int testConflicts(Action a, Set<GemDateTime> dates) {
    int conflicts = 0;
    int idx = 0;
    for (GemDateTime dt : dates) {
      DateFr d = dt.getDate();
      Hour start = dt.getTimeRange().getStart();
      Hour end = dt.getTimeRange().getEnd();
      ScheduleTestConflict testConflict = new ScheduleTestConflict(d, start, end, a, idx++);
      if (!service.isRoomFree(testConflict, a.getRoom())) {
        testConflict.setRoomFree(false);
        conflicts++;
      }
      if (!service.isTeacherFree(testConflict, a.getIdper())) {
        testConflict.setTeacherFree(false);
        conflicts++;
      }
      conflictsView.addConflict(testConflict);
    }
    return conflicts;
  }
}

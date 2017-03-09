/*
 * @(#)StudioScheduleCtrl.java	2.12.0 08/03/17
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import net.algem.planning.editing.ModifPlanEvent;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;
import net.algem.util.GemCommand;
import net.algem.util.MessageUtil;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.CardCtrl;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.GemScrollPane;
import net.algem.util.ui.MessagePopup;

/**
 * This controller is used to plan one or more rooms at differents times and for different technicians.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.12.0
 * @since 2.8.v 21/05/14
 */
public class StudioScheduleCtrl
  extends CardCtrl
  implements UpdateConflictListener
{

  public static final String STUDIO_SCHEDULING_KEY="Studio.scheduling";
  protected ConflictListView conflictsView;
  private StudioScheduleView studioView;
  private final GemDesktop desktop;
  private final PlanningService service;
  private final DataConnection dc;

  public StudioScheduleCtrl(GemDesktop desktop) {
    this.desktop = desktop;
    this.dc = DataCache.getDataConnection();
    this.service = new PlanningService(dc);
  }

  public void init() {
    studioView = new StudioScheduleView(desktop.getDataCache());

    JScrollPane scroll = new GemScrollPane(studioView);
    scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
    scroll.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

    GemPanel gp = new GemPanel(new BorderLayout());
    gp.add(scroll, BorderLayout.CENTER);

    conflictsView = new ConflictListView(new ConflictTableModel(service) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    });
    addCard(null, gp);
    addCard(BundleUtil.getLabel("Conflict.verification.label"), conflictsView);
    select(0);
  }

  @Override
  public boolean next() {
    select(step + 1);
    if (step == 1) {
      conflictsView.clear();
      String t = MessageUtil.getMessage("invalid.choice");
      try {
        List<GemDateTime> dates = checkAction();
        conflictsView.clear();
        int n = testConflicts(studioView.getGroup(), studioView.getRooms(), dates);
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
  public boolean prev() {
    select(step - 1);
    return true;
  }

  @Override
  public boolean cancel() {
    if (actionListener != null) {
      actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, GemCommand.CANCEL_CMD));
    }
    return true;
  }

  @Override
  public boolean validation() {
    List<ScheduleTestConflict> resolved = conflictsView.getResolvedConflicts();
    if (resolved.isEmpty()) {
      MessagePopup.warning(this, MessageUtil.getMessage("no.schedule.to.plan"));
      return false;
    }
    List<GemDateTime> dates = new ArrayList<>();
    for (ScheduleTestConflict c : resolved) {
      GemDateTime dt = new GemDateTime(c.getDate(), new HourRange(c.getStart(), c.getEnd()));
      dates.add(dt);
    }

    StudioSession session = createSession(studioView, dates);
    try {
      service.planStudio(session);
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

  private StudioSession createSession(StudioScheduleView studioView, List<GemDateTime> dates) {
    StudioSession session = new StudioSession();
    session.setGroup(studioView.getGroup());
    session.setStudio(studioView.getStudio());
    session.setRooms(studioView.getRooms());
    session.setTechnicians(studioView.getEmployees());
    session.setDates(dates);
    session.setCategory(studioView.getCategory());

    return session;
  }

  @Override
  public boolean loadId(int id) {
    return false;
  }

  @Override
  public boolean loadCard(Object p) {
    return false;
  }

  @Override
  public void updateStatus(boolean unlock) {
    btNext.setText(unlock ? GemCommand.VALIDATE_CMD : "");
  }

  private List<GemDateTime> checkAction() throws PlanningException {

    List<GemDateTime> dates = studioView.getDates();
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

    if (!studioView.isStudioOnly()) {
      int [] rooms = studioView.getRooms();
      for (int r : rooms) {
        if (r == 0) {
          throw new PlanningException(MessageUtil.getMessage("room.invalid.choice"));
        }
      }
    }

    int[] employees = studioView.getEmployees();
    List employeeList = new ArrayList<Integer>();
    for (int e : employees) {
      if (e <= 0) {
        throw new PlanningException(MessageUtil.getMessage("invalid.technician"));
      }
      employeeList.add(e);
    }

    // check duplicates in employees
    Set employeeSet = new HashSet(employeeList);
    if (employeeSet.size() < employeeList.size()) {
      throw new PlanningException(MessageUtil.getMessage("invalid.technician.duplicate"));
    }

    if (studioView.getStudio() <= 0) {
      throw new PlanningException(MessageUtil.getMessage("studio.invalid.choice"));
    }
    return dates;
  }

  private int testConflicts(int groupId, int [] rooms, List<GemDateTime> dates) {
    int conflicts = 0;


    for (GemDateTime dt : dates) {
      DateFr d = dt.getDate();
      Hour start = dt.getTimeRange().getStart();
      Hour end = dt.getTimeRange().getEnd();
      ScheduleTestConflict testConflict = new ScheduleTestConflict(d, start, end);
      String query = null;

      if (rooms != null) {
        for (int r : rooms) {
          query = ConflictQueries.getRoomConflictSelection(d.toString(), start.toString(), end.toString(), r);
          if (ScheduleIO.count(query, dc) > 0) {
            testConflict.setRoomFree(false);
            conflicts++;
          }
        }
      }
      // test studio occupation
      query = ConflictQueries.getRoomConflictSelection(d.toString(), start.toString(), end.toString(), studioView.getStudio());
      if (ScheduleIO.count(query, dc) > 0) {
        testConflict.setRoomFree(false);
        conflicts++;
      }
      // test group
      query = ConflictQueries.getGroupConflictSelection(d.toString(), start.toString(), end.toString(), groupId);
      if (ScheduleIO.count(query, dc) > 0) {
        testConflict.setGroupFree(false);
        conflicts++;
      }
      // test employee in schedule range
      for (int e : studioView.getEmployees()) {
        query = ConflictQueries.getMemberScheduleSelection(d.toString(), start.toString(), end.toString(), e);
         if (ScheduleIO.count(query, dc) > 0) {
          testConflict.setMemberFree(false);
          conflicts++;
        }
      }
      // test employee in schedule (teacher or rehearsal)
      for (int e : studioView.getEmployees()) {
        query = ConflictQueries.getTeacherConflictSelection(d.toString(), start.toString(), end.toString(), e);
         if (ScheduleIO.count(query, dc) > 0) {
          testConflict.setMemberFree(false);
          conflicts++;
        }
      }
      conflictsView.addConflict(testConflict);
    }
    return conflicts;
  }

  private void clear() {
    studioView.clear();
    conflictsView.clear();
    select(0);
  }

}

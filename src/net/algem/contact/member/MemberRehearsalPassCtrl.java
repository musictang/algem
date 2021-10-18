/*
 * @(#)MemberRehearsalPassCtrl.java	2.12.0 08/03/17
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
package net.algem.contact.member;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import net.algem.contact.PersonFile;
import net.algem.planning.*;
import net.algem.planning.editing.ModifPlanEvent;
import net.algem.room.RoomService;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.GemLogger;
import net.algem.util.MessageUtil;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.FileTabCard;
import net.algem.util.ui.MessagePopup;

/**
 * Pass rehersal controller for a member.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.12.0
 */
public class MemberRehearsalPassCtrl
        extends FileTabCard
{

  private PersonFile personFile;
  private MemberPassRehearsalView rehearsalView;
  private ConflictListView conflictsView;
  private ActionListener listener;
  private MemberService service;

  public MemberRehearsalPassCtrl(GemDesktop desktop, ActionListener listener, PersonFile pFile) {
    super(desktop);
    this.service = new MemberService(dc);
    this.personFile = pFile;
    this.listener = listener;

    rehearsalView = new MemberPassRehearsalView(dataCache);
    PlanningService planningSrv = new PlanningService(DataCache.getDataConnection());
    conflictsView = new ConflictListView(new ConflictTableModel(planningSrv) {
      @Override
      public boolean isCellEditable(int row, int col) {
        return col == 4;
      }

      @Override
      public void setValueAt(Object value, int row, int col) {
        ScheduleTestConflict c = tuples.get(row);
        boolean checked = (boolean) value;
        c.setActive(c.isConflict() ? false : checked);
        fireTableRowsUpdated(row, row);
      }
    });

    addCard(BundleUtil.getLabel("Person.pass.scheduling.auth"), rehearsalView);
    addCard(BundleUtil.getLabel("Conflict.verification.label"), conflictsView);

    select(0);

    rehearsalView.setMember(personFile.getId() + " " + personFile.getContact().getNameFirstname());
  }

  @Override
  public boolean back() {
    select(step - 1);
    return true;
  }

  @Override
  public boolean next() {
    select(step + 1);
    if (step == 1) {
      if (!isEntryValid()) {
        return back();
      }
      conflictsView.clear();
      List<DateFr> dateList = service.generationDate(rehearsalView.getDay(), rehearsalView.getDateStart(), rehearsalView.getDateEnd());
      int nc = testConflict(dateList, rehearsalView.getHourStart(), rehearsalView.getHourEnd());
      GemLogger.log(Level.WARNING, String.valueOf(nc) + " conflicts");
      /*if (testConflict(dateList, rehearsalView.getHourStart(), rehearsalView.getHourEnd()) > 0) {
        btNext.setText("");
      }*/
    }
    return true;
  }

  private boolean isEntryValid() {
    String wt = BundleUtil.getLabel("Warning.label");
    if (rehearsalView.getRoom() == 0) {
      MessagePopup.error(this, MessageUtil.getMessage("room.invalid.choice"), wt);
      return false;
    }

    DateFr date = rehearsalView.getDateStart();
    if (date.bufferEquals(DateFr.NULLDATE)) {
      MessagePopup.error(this, MessageUtil.getMessage("beginning.date.invalid.choice"), wt);
      return false;
    }
    if (date.before(dataCache.getStartOfPeriod())
            || date.after(dataCache.getEndOfPeriod())) {
      MessagePopup.error(this, MessageUtil.getMessage("beginning.date.out.of.period"), wt);
      return false;
    }
    date = rehearsalView.getDateEnd();
    if (date.bufferEquals(DateFr.NULLDATE)) {
      MessagePopup.error(this, MessageUtil.getMessage("end.date.invalid.choice"), wt);
      return false;
    }
    if (date.before(dataCache.getStartOfPeriod())
            || date.after(dataCache.getEndOfPeriod())) {
      MessagePopup.error(this, MessageUtil.getMessage("end.date.out.of.period"), wt);
      return false;
    }

    Hour hStart = rehearsalView.getHourStart();
    Hour hEnd = rehearsalView.getHourEnd();

    if (hStart.toString().equals("00:00")
            || hEnd.toString().equals("00:00")
            || !(hEnd.after(hStart))) {
      MessagePopup.error(this, MessageUtil.getMessage("hour.range.error"), wt);
      return false;
    }

    if (!RoomService.isOpened(rehearsalView.getRoom(), rehearsalView.getDateStart(), hStart, hEnd)) {
      return false;
    }

    return true;
  }

  @Override
  public void cancel() {
    listener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "AdherentForfaitRepetition.Abandon"));
  }

  private void clear() {
    rehearsalView.clear();
    conflictsView.clear();
    select(0);
  }

  @Override
  public boolean isLoaded() {
    return personFile != null;
  }

  @Override
  public void load() {
    clear();
    rehearsalView.setMember(personFile.getId() + " " + personFile.getContact().getNameFirstname());
  }

  @Override
  public void validation() {

    /*String wt = BundleUtil.getLabel("Warning.label");
    if (dateList.isEmpty()) {
      MessagePopup.error(this, MessageUtil.getMessage("empty.planning.create.warning"), wt);
    }*/

    try {
      if (save()) {
        MessagePopup.information(this, MessageUtil.getMessage("planning.update.info"));
        desktop.postEvent(new ModifPlanEvent(this, rehearsalView.getDateStart(), rehearsalView.getDateEnd()));
        listener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "AdherentForfaitRepetition.Validation"));
      }
    } catch (MemberException ex) {
      GemLogger.logException("Insertion répétition", ex, this);
    }
    clear();
  }

  private boolean save() throws MemberException {
    List<ScheduleTestConflict> enabled = conflictsView.getResolvedConflicts();
    List<DateFr> dates = new ArrayList<>();
    for (ScheduleTestConflict c : enabled) {
      if (!c.isConflict()) {
        dates.add(c.getDate());
      }
    }
    if (dates.isEmpty()) {
      MessagePopup.error(this,MessageUtil.getMessage("empty.planning.create.warning"));
      return false;
    }
    service.savePassRehearsal(dates, rehearsalView.getHourStart(), rehearsalView.getHourEnd(), personFile.getId(), rehearsalView.getRoom());
    return true;
  }

  int testConflict(List<DateFr> dateList, Hour start, Hour end) {
    conflictsView.clear();
    int nc = 0;

    try {
      for (int i = 0; i < dateList.size(); i++) {
        DateFr d = dateList.get(i);
        ScheduleTestConflict conflict = new ScheduleTestConflict(d, start, end);

        Action a = new Action();
        a.setIdper(personFile.getId());
        a.setStartTime(rehearsalView.getHourStart());
        a.setEndTime(rehearsalView.getHourEnd());
        a.setRoom(rehearsalView.getRoom());
        List<ScheduleTestConflict> conflicts = service.testMemberSchedule(d, a);
        if (conflicts.size() > 0) {
          for (ScheduleTestConflict c : conflicts) {
            nc++;
            conflict.setRoomFree(c.isRoomFree());
            conflict.setTeacherFree(c.isTeacherFree());
            conflict.setActive(false);
          }
        }
        // test conflits plage adhérent
        ScheduleObject plan = new MemberRehearsalSchedule();
        plan.setIdPerson(personFile.getId());
        conflicts = service.testRangeSchedule(plan, d, rehearsalView.getHourStart(), rehearsalView.getHourEnd());
        if (conflicts.size() > 0) {
          conflict.setMemberFree(false);
          conflict.setActive(false);
          for (ScheduleTestConflict c : conflicts) {
            nc++;
          }
        }
        conflictsView.addConflict(conflict);

      }
    } catch (SQLException e) {
      GemLogger.logException(e);
    }

    return nc;
  }
}

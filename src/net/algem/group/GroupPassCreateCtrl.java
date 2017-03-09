/*
 * @(#)GroupPassCreateCtrl.java	2.12.0 01/03/17
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
package net.algem.group;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import net.algem.planning.ConflictListView;
import net.algem.planning.ConflictTableModel;
import net.algem.planning.DateFr;
import net.algem.planning.Hour;
import net.algem.planning.PlanningService;
import net.algem.planning.ScheduleTestConflict;
import net.algem.planning.editing.ModifPlanEvent;
import net.algem.room.RoomService;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.GemLogger;
import net.algem.util.MessageUtil;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.CardCtrl;
import net.algem.util.ui.MessagePopup;

/**
 * Controller for group pass rehearsal.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.12.0
 */
public class GroupPassCreateCtrl
        extends CardCtrl
{

  private DataCache dataCache;
  private GemDesktop desktop;
  private GroupPassRehearsalView passView;
  private ConflictListView conflictsView;
  private Group group;
  private GemGroupService service;
  private String wt = BundleUtil.getLabel("Warning.label");

  public GroupPassCreateCtrl(GemDesktop desktop) {
    this.desktop = desktop;
    this.dataCache = desktop.getDataCache();
    passView = new GroupPassRehearsalView(dataCache);

    service = new GemGroupService(DataCache.getDataConnection());
    PlanningService planningSrv = new PlanningService(DataCache.getDataConnection());
    conflictsView = new ConflictListView(new ConflictTableModel(planningSrv) {
      @Override
      public boolean isCellEditable(int row, int col) {
        return col == 4;
      }

      @Override
      public void setValueAt(Object value, int row, int col) {
        ScheduleTestConflict c = tuples.elementAt(row);
        boolean checked = (boolean) value;
        c.setActive(c.isConflict() ? false : checked);
      }
    });

    addCard(BundleUtil.getLabel("Group.pass.scheduling.auth"), passView);
    addCard(BundleUtil.getLabel("Conflict.verification.label"), conflictsView);

    select(0);
  }

   public GroupPassCreateCtrl(GemDesktop d, Group g) {
     this(d);
     this.group = g;
     addActionListener((ActionListener)d);
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
      if (hasErrors()) {
        return prev();
      }

      Hour hStart = passView.getHourStart();
      Hour hEnd = passView.getHourEnd();
      List<DateFr> dateList = service.generateDates(passView.getDay(), passView.getDateStart(), passView.getDateEnd());
      conflictsView.clear();
      List<ScheduleTestConflict> vc = service.testConflict(dateList, hStart, hEnd, group.getId(), passView.getRoom());
      if (vc.size() > 0) {
        for (ScheduleTestConflict pc : vc) {
          conflictsView.addConflict(pc);
        }
        // let button active : permission to save non conflicting schedules
        /*if (hasConflits(vc)) {
          btNext.setText("");
        }*/
      }
    }
    return true;
  }

  private boolean hasErrors() {
    if (passView.getRoom() == 0) {
        MessagePopup.error(this,MessageUtil.getMessage("room.invalid.choice"), wt);
        return true;
      }

      DateFr date = passView.getDateStart();
      if (date.bufferEquals(DateFr.NULLDATE)) {
        MessagePopup.error(this,MessageUtil.getMessage("beginning.date.invalid.choice"), wt);
        return true;
      }
      if (date.before(dataCache.getStartOfPeriod())
              || date.after(dataCache.getEndOfPeriod())) {
        MessagePopup.error(this,MessageUtil.getMessage("beginning.date.out.of.period"), wt);
        return true;
      }
      date = passView.getDateEnd();
      if (date.bufferEquals(DateFr.NULLDATE)) {
        MessagePopup.error(this,MessageUtil.getMessage("end.date.invalid.choice"), wt);
        return true;
      }
      if (date.before(dataCache.getStartOfPeriod())
              || date.after(dataCache.getEndOfPeriod())) {
        MessagePopup.error(this,MessageUtil.getMessage("end.date.out.of.period"), wt);
        return true;
      }
      Hour hStart = passView.getHourStart();
      Hour hEnd = passView.getHourEnd();

      if (hStart.toString().equals("00:00")
              || hEnd.toString().equals("00:00")
              || !(hEnd.after(hStart))) {
        MessagePopup.error(this,MessageUtil.getMessage("hour.range.error"), wt);
        return true;
      }
      if (!RoomService.isOpened(passView.getRoom(), passView.getDateStart(), hStart, hEnd)) {
        return true;
      }
      return false;
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
    passView.clear();
    conflictsView.clear();
    select(0);
  }

  @Override
  public boolean loadCard(Object o) {
    clear();
    return false;
  }

  @Override
  public boolean loadId(int id) {
    return false;
  }

  @Override
  public boolean validation() {

    List<ScheduleTestConflict> enabled = conflictsView.getResolvedConflicts();
    List<DateFr> dates = new ArrayList<>();
    for (ScheduleTestConflict c : enabled) {
      if (!c.isConflict()) {
        dates.add(c.getDate());
      }
    }
    if (dates.isEmpty()) {
      MessagePopup.error(this,MessageUtil.getMessage("empty.planning.create.warning"), wt);
      return false;
    }

    try {
      service.createPassRehearsal(dates, passView.getHourStart(), passView.getHourEnd(), group.getId(), passView.getRoom());
      MessagePopup.information(this,MessageUtil.getMessage("planning.update.info"));
      desktop.postEvent(new ModifPlanEvent(this, passView.getDateStart(), passView.getDateEnd()));
    } catch (GroupException ex) {
      MessagePopup.warning(this, ex.getMessage());
      GemLogger.logException("Insertion répétition", ex, this);
      return false;
    }
    clear();
    return true;
  }


  private boolean hasConflits(List<ScheduleTestConflict> vc) {
    for (ScheduleTestConflict pc : vc) {
      if (pc.isConflict()) {
        return true;
      }
    }
    return false;
  }

}


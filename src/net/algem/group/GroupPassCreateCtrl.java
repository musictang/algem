/*
 * @(#)GroupPassCreateCtrl.java	2.11.0 03/10/2016
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
package net.algem.group;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import net.algem.planning.ConflictListView;
import net.algem.planning.DateFr;
import net.algem.planning.Hour;
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
 * @version 2.11.0
 */
public class GroupPassCreateCtrl
        extends CardCtrl
{

  private DataCache dataCache;
  private GemDesktop desktop;
  private GroupPassRehearsalView view;
  private ConflictListView cfv;
  private Vector<DateFr> dateList;
  private Group group;
  private GemGroupService service;
  private String wt = BundleUtil.getLabel("Warning.label");

  public GroupPassCreateCtrl(GemDesktop d) {
    desktop = d;
    dataCache = d.getDataCache();
    view = new GroupPassRehearsalView(dataCache);
    cfv = new ConflictListView();
    service = new GemGroupService(DataCache.getDataConnection());

    addCard(BundleUtil.getLabel("Group.pass.scheduling.auth"), view);
    addCard(BundleUtil.getLabel("Conflict.verification.label"), cfv);

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
      if (view.getRoom() == 0) {
        MessagePopup.error(this,MessageUtil.getMessage("room.invalid.choice"), wt);
        return prev();
      }

      DateFr date = view.getDateStart();
      if (date.bufferEquals(DateFr.NULLDATE)) {
        MessagePopup.error(this,MessageUtil.getMessage("beginning.date.invalid.choice"), wt);
        return prev();
      }
      if (date.before(dataCache.getStartOfPeriod())
              || date.after(dataCache.getEndOfPeriod())) {
        MessagePopup.error(this,MessageUtil.getMessage("beginning.date.out.of.period"), wt);
        return prev();
      }
      date = view.getDateEnd();
      if (date.bufferEquals(DateFr.NULLDATE)) {
        MessagePopup.error(this,MessageUtil.getMessage("end.date.invalid.choice"), wt);
        return prev();
      }
      if (date.before(dataCache.getStartOfPeriod())
              || date.after(dataCache.getEndOfPeriod())) {
        MessagePopup.error(this,MessageUtil.getMessage("end.date.out.of.period"), wt);
        return prev();
      }
      Hour hStart = view.getHourStart();
      Hour hEnd = view.getHourEnd();
      
      if (hStart.toString().equals("00:00")
              || hEnd.toString().equals("00:00")
              || !(hEnd.after(hStart))) {
        MessagePopup.error(this,MessageUtil.getMessage("hour.range.error"), wt);
        return prev();
      }
      if (!RoomService.acceptWhenClosed(view.getRoom(), view.getDateStart(), hStart, hEnd)) {
        return prev();
      }
      dateList = service.generateDates(view.getDay(), view.getDateStart(), view.getDateEnd());
      cfv.clear();
      Vector<ScheduleTestConflict> vc = service.testConflict(dateList, hStart, hEnd, group.getId(), view.getRoom());
      if (vc.size() > 0) {
        for (ScheduleTestConflict pc : vc) {
          cfv.addConflict(pc);
        }
        if (hasConflits(vc)) {
          btNext.setText("");
        }
      }
    }
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
    view.clear();
    cfv.clear();
    select(0);
  }

  @Override
  public boolean loadCard(Object o) {
    clear();
    /*
    if (o == null || !(o instanceof Group))
    return false;

    group = (Group)o;
    view.setGroupe(group);
    return true;
     */
    return false;
  }

  @Override
  public boolean loadId(int id) {
    return false;
  }

  @Override
  public boolean validation() {

    if (dateList.isEmpty()) {
      MessagePopup.error(this,MessageUtil.getMessage("empty.planning.create.warning"), wt);
      return false;
    }

    try {
      service.createPassRehearsal(dateList, view.getHourStart(), view.getHourEnd(), group.getId(), view.getRoom());
      MessagePopup.information(this,MessageUtil.getMessage("planning.update.info"));
      desktop.postEvent(new ModifPlanEvent(this, view.getDateStart(), view.getDateEnd()));
    } catch (GroupException ex) {
      MessagePopup.warning(this, ex.getMessage());
      GemLogger.logException("Insertion répétition", ex, this);
      return false;
    }
    clear();
    return true;
  }


  private boolean hasConflits(Vector<ScheduleTestConflict> vc) {
    for (ScheduleTestConflict pc : vc) {
      if (!(pc.isMemberFree() && pc.isRoomFree())) {
        return true;
      }
    }
    return false;
  }

}


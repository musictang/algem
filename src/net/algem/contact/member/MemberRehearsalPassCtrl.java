/*
 * @(#)MemberRehearsalPassCtrl.java	2.8.w 24/07/14
 *
 * Copyright (c) 1999-2014 Musiques Tangentes. All Rights Reserved.
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
import java.util.Vector;
import net.algem.contact.PersonFile;
import net.algem.planning.*;
import net.algem.planning.editing.ModifPlanEvent;
import net.algem.room.RoomService;
import net.algem.util.BundleUtil;
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
 * @version 2.8.w
 */
public class MemberRehearsalPassCtrl
        extends FileTabCard
{

  private PersonFile personFile;
  private MemberPassRehearsalView rehearsalView;
  private ConflictListView cfv;
  private Vector<DateFr> dateList;
  private ActionListener listener;
  private MemberService service;

  public MemberRehearsalPassCtrl(GemDesktop desktop, ActionListener listener, PersonFile pFile) {
    super(desktop);
    service = new MemberService(dc);
    personFile = pFile;
    this.listener = listener;

    rehearsalView = new MemberPassRehearsalView(dataCache);
    cfv = new ConflictListView();

    addCard(BundleUtil.getLabel("Person.pass.scheduling.auth"), rehearsalView);
    addCard(BundleUtil.getLabel("Conflict.verification.label"), cfv);

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

      dateList = service.generationDate(rehearsalView.getDay(), rehearsalView.getDateStart(), rehearsalView.getDateEnd());
      if (testConflict(dateList, rehearsalView.getHourStart(), rehearsalView.getHourEnd()) > 0) {
        btNext.setText("");
      }
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

    if (!RoomService.isClosed(rehearsalView.getRoom(), rehearsalView.getDateStart(), hStart, hEnd)) {
      return false;
    }
    
    return true;
  }

  @Override
  public void cancel() {
    listener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "AdherentForfaitRepetition.Abandon"));
  }

  public void clear() {
    rehearsalView.clear();
    cfv.clear();
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

    String wt = BundleUtil.getLabel("Warning.label");
    if (dateList.isEmpty()) {
      MessagePopup.error(this, MessageUtil.getMessage("empty.planning.create.warning"), wt);
    }

    try {
      save();
      MessagePopup.information(this, MessageUtil.getMessage("planning.update.info"));
      desktop.postEvent(new ModifPlanEvent(this, rehearsalView.getDateStart(), rehearsalView.getDateEnd()));
      listener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "AdherentForfaitRepetition.Validation"));
    } catch (MemberException ex) {
      GemLogger.logException("Insertion répétition", ex, this);
    }
    clear();
  }

  public void save() throws MemberException {
    service.savePassRehearsal(dateList, rehearsalView.getHourStart(), rehearsalView.getHourEnd(), personFile.getId(), rehearsalView.getRoom());
  }

  public int testConflict(Vector<DateFr> dateList, Hour start, Hour end) {
    cfv.clear();

    int conflicts = 0;
    Vector<ScheduleTestConflict> vc = new Vector<ScheduleTestConflict>();
    try {
      for (int i = 0; i < dateList.size(); i++) {
        DateFr d = dateList.elementAt(i);
        ScheduleTestConflict conflict = new ScheduleTestConflict(d, start, end);
        
        vc = service.testRoom(d, start, end, rehearsalView.getRoom());
        if (vc.size() > 0) {
          for (ScheduleTestConflict c : vc) {
            conflicts++;
            conflict.setRoomFree(false);
          }
        }

        ScheduleObject plan = new MemberRehearsalSchedule();
        plan.setIdPerson(personFile.getId());

        // test conflits répétitions adhérent (ou cours prof)
        vc = service.testMemberSchedule(plan, d, rehearsalView.getHourStart(), rehearsalView.getHourEnd());
        if (vc.size() > 0) {
          for (ScheduleTestConflict c : vc) {
            conflicts++;
            conflict.setMemberFree(false);
          }
        }
        // test conflits plage adhérent
        vc = service.testRangeSchedule(plan, d, rehearsalView.getHourStart(), rehearsalView.getHourEnd());
        if (vc.size() > 0) {
          for (ScheduleTestConflict c : vc) {
            conflicts++;
            conflict.setMemberFree(false);
          }
        }
        cfv.addConflict(conflict);
      }
    } catch (SQLException e) {
      GemLogger.logException(e);
    }

    return conflicts;
  }
}

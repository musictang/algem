/*
 * @(#)MemberRehearsalPassCtrl.java	2.8.t 16/05/14
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
import javax.swing.JOptionPane;
import net.algem.contact.PersonFile;
import net.algem.planning.*;
import net.algem.planning.editing.MemberPassRehearsalView;
import net.algem.planning.editing.ModifPlanEvent;
import net.algem.util.BundleUtil;
import net.algem.util.GemLogger;
import net.algem.util.MessageUtil;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.FileTabCard;

/**
 * Pass rehersal controller for a member.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.t
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
      if (testConflict() > 0) {
        btNext.setText("");
      }
    }
    return true;
  }

  private boolean isEntryValid() {
    String wt = BundleUtil.getLabel("Warning.label");
    if (rehearsalView.getRoom() == 0) {
      JOptionPane.showMessageDialog(this,
              MessageUtil.getMessage("room.invalid.choice"),
              wt,
              JOptionPane.ERROR_MESSAGE);
      return false;
    }

    DateFr date = rehearsalView.getDateStart();
    if (date.bufferEquals(DateFr.NULLDATE)) {
      JOptionPane.showMessageDialog(this,
              MessageUtil.getMessage("beginning.date.invalid.choice"),
              wt,
              JOptionPane.ERROR_MESSAGE);
      return false;
    }
    if (date.before(dataCache.getStartOfPeriod())
            || date.after(dataCache.getEndOfPeriod())) {
      JOptionPane.showMessageDialog(this,
              MessageUtil.getMessage("beginning.date.out.of.period"),
              wt,
              JOptionPane.ERROR_MESSAGE);
      return false;
    }
    date = rehearsalView.getDateEnd();
    if (date.bufferEquals(DateFr.NULLDATE)) {
      JOptionPane.showMessageDialog(this,
              MessageUtil.getMessage("end.date.invalid.choice"),
              wt,
              JOptionPane.ERROR_MESSAGE);
      return false;
    }
    if (date.before(dataCache.getStartOfPeriod())
            || date.after(dataCache.getEndOfPeriod())) {
      JOptionPane.showMessageDialog(this,
              MessageUtil.getMessage("end.date.out.of.period"),
              wt,
              JOptionPane.ERROR_MESSAGE);
      return false;
    }
    Hour hdeb = rehearsalView.getHourStart();
    Hour hfin = rehearsalView.getHourEnd();
    if (hdeb.toString().equals("00:00")
            || hfin.toString().equals("00:00")
            || !(hfin.after(hdeb))) {
      JOptionPane.showMessageDialog(this,
              MessageUtil.getMessage("hour.range.error"),
              wt,
              JOptionPane.ERROR_MESSAGE);
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
      JOptionPane.showMessageDialog(this,
              MessageUtil.getMessage("empty.planning.create.warning"),
              wt,
              JOptionPane.ERROR_MESSAGE);
    }

    try {
      save();
      JOptionPane.showMessageDialog(this,
              MessageUtil.getMessage("planning.update.info"),
              "",
              JOptionPane.INFORMATION_MESSAGE);
      desktop.postEvent(new ModifPlanEvent(this, rehearsalView.getDateStart(), rehearsalView.getDateEnd()));
      listener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "AdherentForfaitRepetition.Validation"));
    } catch (MemberException ex) {
      GemLogger.logException("Insertion répétition", ex, this);
    }
    clear();
  }

  public void save() throws MemberException {
    service.saveRehearsalPass(dateList, rehearsalView.getHourStart(), rehearsalView.getHourEnd(), personFile.getId(), rehearsalView.getRoom());
  }

  public int testConflict() {
    cfv.clear();

    int conflicts = 0;
    Vector<ScheduleTestConflict> vc = new Vector<ScheduleTestConflict>();
    try {
      for (int i = 0; i < dateList.size(); i++) {
        DateFr d = dateList.elementAt(i);
        vc = service.testRoom(d, rehearsalView.getHourStart(), rehearsalView.getHourEnd(), rehearsalView.getRoom());
        if (vc.size() > 0) {
          for (ScheduleTestConflict c : vc) {
            conflicts++;
            cfv.addConflict(c);
          }
        }

        ScheduleObject plan = new MemberRehearsalSchedule();
        plan.setIdPerson(personFile.getId());

        // test conflits répétitions adhérent (ou cours prof)
        vc = service.testMemberSchedule(plan, d, rehearsalView.getHourStart(), rehearsalView.getHourEnd());
        if (vc.size() > 0) {
          for (ScheduleTestConflict c : vc) {
            conflicts++;
            cfv.addConflict(c);
          }
        }
        // test conflits plage adhérent
        vc = service.testRangeSchedule(plan, d, rehearsalView.getHourStart(), rehearsalView.getHourEnd());
        if (vc.size() > 0) {
          for (ScheduleTestConflict c : vc) {
            conflicts++;
            cfv.addConflict(c);
          }
        }

      }
    } catch (SQLException e) {
      GemLogger.logException(e);
    }

    return conflicts;
  }
}

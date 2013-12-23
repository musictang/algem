/*
 * @(#)GroupPassCreateCtrl.java	2.7.a 03/12/12
 * 
 * Copyright (c) 1999-2012 Musiques Tangentes. All Rights Reserved.
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
import javax.swing.JOptionPane;
import net.algem.planning.ConflictListView;
import net.algem.planning.DateFr;
import net.algem.planning.Hour;
import net.algem.planning.ScheduleTestConflict;
import net.algem.planning.editing.ModifPlanEvent;
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
 * @version 2.7.a
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
    service = new GemGroupService(dataCache.getDataConnection());

    addCard("Saisie forfait répétition groupe", view);
    addCard("Vérification conflit", cfv);

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
        JOptionPane.showMessageDialog(this, MessageUtil.getMessage("room.invalid.choice"), wt,JOptionPane.ERROR_MESSAGE);
        return prev();
      }

      DateFr date = view.getDateStart();
      if (date.equals(DateFr.NULLDATE)) {
        JOptionPane.showMessageDialog(this, MessageUtil.getMessage("beginning.date.invalid.choice"), wt, JOptionPane.ERROR_MESSAGE);
        return prev();
      }
      if (date.before(dataCache.getStartOfPeriod())
              || date.after(dataCache.getEndOfPeriod())) {
        JOptionPane.showMessageDialog(this,MessageUtil.getMessage("beginning.date.out.of.period"), wt,JOptionPane.ERROR_MESSAGE);
        return prev();
      }
      date = view.getDateEnd();
      if (date.equals(DateFr.NULLDATE)) {
        JOptionPane.showMessageDialog(this, MessageUtil.getMessage("end.date.invalid.choice"), wt,JOptionPane.ERROR_MESSAGE);
        return prev();
      }
      if (date.before(dataCache.getStartOfPeriod())
              || date.after(dataCache.getEndOfPeriod())) {
        JOptionPane.showMessageDialog(this, MessageUtil.getMessage("end.date.out.of.period"), wt,JOptionPane.ERROR_MESSAGE);
        return prev();
      }
      Hour hdeb = view.getHourStart();
      Hour hfin = view.getHourEnd();
      if (hdeb.toString().equals("00:00")
              || hfin.toString().equals("00:00")
              || !(hfin.after(hdeb))) {
        JOptionPane.showMessageDialog(this, MessageUtil.getMessage("hour.range.error"), wt,JOptionPane.ERROR_MESSAGE);
        return prev();
      }

      dateList = service.generateDates(view.getDay(), view.getDateStart(), view.getDateEnd());
      cfv.clear();
      Vector<ScheduleTestConflict> vc = service.testConflict(dateList, hdeb, hfin, group.getId(), view.getRoom());
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
      JOptionPane.showMessageDialog(this, MessageUtil.getMessage("empty.planning.create.warning"), wt, JOptionPane.ERROR_MESSAGE);
      return false;
    }

    try {
      service.createPassRehearsal(dateList, view.getHourStart(), view.getHourEnd(), group.getId(), view.getRoom());
      JOptionPane.showMessageDialog(this, MessageUtil.getMessage("planning.update.info"), null, JOptionPane.INFORMATION_MESSAGE);
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


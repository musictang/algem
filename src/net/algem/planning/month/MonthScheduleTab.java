/*
 * @(#)MonthScheduleTab.java	2.8.o 10/10/13
 *
 * Copyright (c) 1999-2013 Musiques Tangentes. All Rights Reserved.
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
package net.algem.planning.month;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Date;
import java.util.Vector;
import net.algem.contact.Contact;
import net.algem.contact.PersonFile;
import net.algem.planning.DateFr;
import net.algem.planning.ScheduleDetailCtrl;
import net.algem.planning.ScheduleObject;
import net.algem.planning.ScheduleRangeObject;
import net.algem.planning.editing.ModifPlanEvent;
import net.algem.util.GemLogger;
import net.algem.util.event.GemEvent;
import net.algem.util.event.GemEventListener;
import net.algem.util.module.GemDesktop;

/**
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.o
 */
public class MonthScheduleTab
        extends AbstractMonthScheduleCtrl
        implements GemEventListener
{

  private PersonFile pFile;
  private MonthPlanTeacherView teacherView;
  private ActionListener listener;
  
  public MonthScheduleTab(GemDesktop desktop, ActionListener listener, PersonFile pf) {
    super(desktop);

    pFile = pf;
    this.listener = listener;

    detailCtrl = new ScheduleDetailCtrl(desktop, modifCtrl, false);

    monthSchedule = new MonthSchedule();
    monthSchedule.addPropertyChangeListener(teacherView);

    teacherView = new MonthPlanTeacherView();
    teacherView.addActionListener(this);

    setLayout(new BorderLayout());
    add(teacherView, BorderLayout.CENTER);

    desktop.addGemEventListener(this);

  }

  @Override
  public void load() {
    load(new Date());
  }

  /**
   * Loads schedules and schedule ranges.
   *
   * @param d a date
   */
  @Override
  public void load(Date d) {

    setMonthRange(d);

    try {
      int type = pFile.getContact().getType();
      String query;
//      boolean isTeacher = pFile.getTeacher() != null;
      if (type != Contact.PERSON) {
        return;
      }
      // recherche des répétitions / plannings prof dans les plannings
      query = " WHERE p.idper = " + pFile.getId()
              + " AND p.jour >= '" + start + "' AND p.jour <= '" + end + "'"
              + " ORDER BY p.jour,p.debut";

      Vector<ScheduleObject> vp1 = planningService.getSchedule(query);

      Vector<ScheduleObject> vp2 = null;

        query = " ,plage WHERE plage.adherent = " + pFile.getId()
                //                + " AND p.jour = plage.jour"
                //                + " AND p.lieux = plage.salle "
                //                + " AND (plage.debut >= p.debut AND plage.fin <= p.fin)"
                + " AND p.id = plage.idplanning"
                + " AND p.jour >= '" + start + "' AND p.jour <= '" + end + "'"
                + " ORDER BY p.jour,p.debut";
        vp2 = planningService.getSchedule(query);

      query = " AND pg.adherent = " + pFile.getId()
                + " AND p.jour >= '" + start + "' AND p.jour <= '" + end + "'"
                + " ORDER BY p.jour, pg.debut";
      Vector<ScheduleRangeObject> vpg1 = planningService.getScheduleRange(query);
      // test correlation plannings/plages
      if (vp2 != null) {
        for (int i = 0; i < vpg1.size(); i++) {
            vp2.elementAt(i).setStart(vpg1.elementAt(i).getStart());
            vp2.elementAt(i).setEnd(vpg1.elementAt(i).getEnd());
        }
        vp1.addAll(vp2);
      }
      
      query = " AND p.idper = " + pFile.getId()
                + " AND p.jour >= '" + start + "' AND p.jour <= '" + end + "'"
                + " ORDER BY p.jour, pg.debut";
      Vector<ScheduleRangeObject> vpg2 = planningService.getScheduleRange(query);
      if (vpg2 != null) {
        vpg1.addAll(vpg2);
      }
      // setup des plannings
      monthSchedule.setSchedule(start.getDate(), end.getDate(), vp1);

      // setup des plages
      monthSchedule.setScheduleRange(start.getDate(), end.getDate(), vpg1);
      teacherView.load(cal.getTime(), vp1, vpg1);
      loaded = true;
    } catch (SQLException e) {
      GemLogger.logException(e);
    }
  }

  @Override
  public void postEvent(GemEvent evt) {
    System.out.println("OngletPlanningMois.postEvent:" + evt);

    if (evt instanceof ModifPlanEvent) {
      DateFr eventStart = ((ModifPlanEvent) evt).getStart();
      DateFr eventEnd = ((ModifPlanEvent) evt).getEnd();
      if (eventEnd.before(start) || eventStart.after(end)) { //TODO a afiner 3 tests de recouvrement
        return;
      }
      EventQueue.invokeLater(new Runnable()
      {
        @Override
        public void run() {
          load(cal.getTime());
        }
      });
    }

  }

}

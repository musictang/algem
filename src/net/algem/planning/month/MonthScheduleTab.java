/*
 * @(#)MonthScheduleTab.java	2.7.e 05/02/13
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
package net.algem.planning.month;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;
import net.algem.contact.Contact;
import net.algem.contact.PersonFile;
import net.algem.planning.*;
import net.algem.planning.editing.ModifPlanEvent;
import net.algem.planning.editing.PlanModifCtrl;
import net.algem.util.GemLogger;
import net.algem.util.event.GemEvent;
import net.algem.util.event.GemEventListener;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.FileTab;

/**
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.e
 */
public class MonthScheduleTab
        extends FileTab
        implements ActionListener, GemEventListener
{

  private PersonFile pFile;
  private MonthSchedule monthSchedule;
  private boolean loaded;
  private MonthPlanTeacherView teacherView;
  private ActionListener listener;
  private PlanModifCtrl modifCtrl;
  private ScheduleDetailCtrl detailCtrl;
  private Calendar cal;
  private DateFr start;
  private DateFr end;
  

  public MonthScheduleTab(GemDesktop _desktop, ActionListener _listener, PersonFile pf) {
    super(_desktop);

    pFile = pf;
    listener = _listener;

    cal = Calendar.getInstance(Locale.FRANCE);

    cal.set(Calendar.YEAR, 1900);
    start = new DateFr(cal.getTime());
    end = new DateFr(cal.getTime());

    modifCtrl = new PlanModifCtrl(desktop);
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
  public void load(Date d) {
    loaded = true;
    System.out.println("OngletPlanningMois.load d:" + d);
    cal.setTime(d);
    // recherche des dates de début et de fin de mois
    cal.set(Calendar.DAY_OF_MONTH, 1);
    start = new DateFr(cal.getTime());
    cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
    end = new DateFr(cal.getTime());

    try {
      int type = pFile.getContact().getType();
      String query;
      boolean isTeacher = pFile.getTeacher() != null;
      if (type != Contact.PERSON) {
        return;
      }
      // recherche des répétitions / plannings prof dans les plannings
      query = " WHERE p.idper = " + pFile.getId()
              + " AND p.jour >= '" + start + "' AND p.jour <= '" + end + "'"
              + " ORDER BY p.jour,p.debut";

      Vector<ScheduleObject> vpl = planningService.getSchedule(query);

      Vector<ScheduleObject> vp2 = null;
      // recherche des plannings cours auxquels est inscrit l'adhérent
      //XXX probleme si plusieurs plannings se chevauchent
      if (!isTeacher) {
        query = " ,plage WHERE plage.adherent = " + pFile.getId()
                //                + " AND p.jour = plage.jour"
                //                + " AND p.lieux = plage.salle "
                //                + " AND (plage.debut >= p.debut AND plage.fin <= p.fin)"
                + " AND p.id = plage.idplanning"
                + " AND p.jour >= '" + start + "' AND p.jour <= '" + end + "'"
                + " ORDER BY p.jour,p.debut";
        vp2 = planningService.getSchedule(query);
        //vpl.addAll(vp2);
      }

      // setup des plannings
      //modele.setSchedule(debut.getDate(), fin.getDate(), vpl);

      // recherche des plages
      if (isTeacher) {
        query = " AND p.idper = " + pFile.getId()
                + " AND p.jour >= '" + start + "' AND p.jour <= '" + end + "'"
                + " ORDER BY p.jour, pg.debut";
      } else {
        query = " AND pg.adherent = " + pFile.getId()
                + " AND p.jour >= '" + start + "' AND p.jour <= '" + end + "'"
                + " ORDER BY p.jour, pg.debut";
      }
      
      Vector<ScheduleRangeObject> vpg = planningService.getScheduleRange(query);
      // test correlation plannings/plages
      if (vp2 != null) {
        for (int i = 0; i < vpg.size(); i++) {
          vp2.elementAt(i).setStart(vpg.elementAt(i).getStart());
          vp2.elementAt(i).setEnd(vpg.elementAt(i).getEnd());
        }
        vpl.addAll(vp2);
      }
      // setup des plannings
      monthSchedule.setSchedule(start.getDate(), end.getDate(), vpl);

      // setup des plages
      monthSchedule.setScheduleRange(start.getDate(), end.getDate(), vpg);
      teacherView.load(cal.getTime(), vpl, vpg);
    } catch (SQLException e) {
      GemLogger.logException(e);
    }
  }

  @Override
  public boolean isLoaded() {
    return loaded;
  }

  @Override
  public void postEvent(GemEvent _evt) {
    System.out.println("OngletPlanningMois.postEvent:" + _evt);

    if (_evt instanceof ModifPlanEvent) {
      DateFr edebut = ((ModifPlanEvent) _evt).getStart();
      DateFr efin = ((ModifPlanEvent) _evt).getEnd();
      if (efin.before(start) || edebut.after(end)) { //TODO a afiner 3 tests de recouvrement
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

  @Override
  public void actionPerformed(ActionEvent _evt) {
    //if (_evt.getSource() instanceof DateBar)
    if (_evt.getActionCommand().equals("date")) {
      Date d = ((DateBar) _evt.getSource()).getDate();
      load(d);
      SelectDateEvent sde = new SelectDateEvent(this, d);
      //desktop.postEvent(sde);
    } else if (_evt.getActionCommand().equals("Click")) {
      ScheduleView v = (ScheduleView) _evt.getSource();
      Schedule p = v.getSchedule();
      ScheduleDetailEvent pde = new ScheduleDetailEvent(this, p);
      pde.setPosition(v.getClickPosition());
      pde.setRanges(v.getScheduleRanges());
      desktop.setWaitCursor();
      detailCtrl.loadSchedule(pde);
      desktop.setDefaultCursor();
    }
    /*
     * else if (_evt.getActionCommand().equals("ClickDate")) // click hors plage
     * { ScheduleView v = (ScheduleView)_evt.getSource(); Schedule p =
     * v.getSchedule(); //System.out.println("PlanningMoisCtrl.ClickDate:"+p);
     *
     * ScheduleDetailEvent pde = new ScheduleDetailEvent(this, p);
     * pde.setPosition(v.getClickPosition()); //desktop.postEvent(pde);
     *
     * //modifCtrl.loadSchedule(p);
     *
     * }
     */
    /*
     * else if (evt.getActionCommand().equals("MenuImprime")) { vue.imprime(); }
     */
  }
}

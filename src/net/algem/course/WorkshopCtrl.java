/*
 * @(#)WorkshopCtrl.java	2.7.a 26/11/12
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
package net.algem.course;

import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.Vector;
import net.algem.contact.Person;
import net.algem.group.Musician;
import net.algem.planning.WorkshopView;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;
import net.algem.util.GemLogger;
import net.algem.util.model.Model;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.CardCtrl;

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.a
 */
public class WorkshopCtrl
        extends CardCtrl
{

  private DataCache dataCache;
  private DataConnection dc;
  private WorkshopView view;
//	AtelierListeView	pv;
  private Course workshop;

  public WorkshopCtrl(GemDesktop _desktop) {
    
    dataCache = _desktop.getDataCache();
    dc = dataCache.getDataConnection();

    view = new WorkshopView(_desktop);

    addCard("ateliers", view);
    select(0);
  }

  @Override
  public boolean next() {
    switch (step) {
      default:
        select(step + 1);
        break;
    }
    return true;
  }

  @Override
  public boolean cancel() {
    if (actionListener != null) {
      actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "CtrlAbandon"));
    }
    return true;
  }

  @Override
  public boolean prev() {
    switch (step) {
      default:
        select(step - 1);
        break;
    }
    return true;
  }

  /**
   *
   * @return true if error free
   */
  @Override
  public boolean validation() {
    try {
      dc.setAutoCommit(false);
      ((CourseIO)DataCache.getDao(Model.Course)).update(get());
      /* Course c = new Course();
       * c.setId(atelier.getId());
       * c.setTitle(av.getName());
       * // TODOGEM c.setProf(av.getTeacherId());
       * c.setCollective(true);
       * c.setLabel(c.getTitle().length() > 15 ? c.getTitle().substring(0,15) : c.getTitle());
       * c.setCode("ATP");
       * dataCache.getCourseIO().update(c); */
      /*dc.executeUpdate("DELETE from atelier_ins where id=" + workshop.getId());
      Vector v = view.getMember();
      for (int i = 0; i < v.size(); i++) {
        Musician in = (Musician) v.elementAt(i);
        dc.executeUpdate("INSERT into atelier_ins values(" + workshop.getId() + "," + in.getId() + ",'" + in.getInstruments() + "')");
      }*/
      dc.commit();
      clear();

    } catch (SQLException e1) {
      GemLogger.logException(getClass().getName()+"#validation", e1, this);
      dc.rollback();
      return false;
    } finally {
      dc.setAutoCommit(true);
    }

    if (actionListener != null) {
      actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "CtrlValider"));
    }
    return true;
  }

  public void clear() {
    view.clear();
//		pv.clear();
  }

  @Override
  public boolean loadCard(Object o) {
    clear();
    if (o == null || !(o instanceof Course)) {
      return false;
    }

    workshop = (Course) o;
    view.set(workshop);
    try {
      Vector<Person> v = WorkshopIO.findMember(workshop, dc);
      for (int i = 0; i < v.size(); i++) {
        view.addRow((Musician) v.elementAt(i));
      }

      select(0);
    } catch (SQLException e) {
      GemLogger.logException("lecture fiche atelier", e, this);
      return false;
    }
    return true;
  }

  @Override
  public boolean loadId(int id) {
    try {
      return loadCard(((CourseIO)DataCache.getDao(Model.Course)).findId(id));
    } catch (SQLException ex) {
      GemLogger.logException(getClass().getName() + "#loadId", ex);
    }
    return false;
  }

  private Course get() {

    Course g = view.get();
    g.setTitle(g.getTitle().length() > 32 ? g.getTitle().substring(0, 32) : g.getTitle());
    g.setLabel(g.getLabel().length() > 16 ? g.getLabel().substring(0, 16) : g.getLabel());
    g.setCollective(true);
    g.setCode(Course.ATP_CODE);
    
    return g;
  }
}

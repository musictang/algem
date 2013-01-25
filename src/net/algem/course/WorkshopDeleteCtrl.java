/*
 * @(#)WorkshopDeleteCtrl.java	2.7.a 26/11/12
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
 * Workshop suppression.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.a
 * @deprecated
 */
public class WorkshopDeleteCtrl
        extends CardCtrl
{

  private DataCache cache;
  private WorkshopView cv;
  private GemDesktop desktop;
  private Course atelier;
  private DataConnection dc;

  public WorkshopDeleteCtrl(GemDesktop _desktop) {
    super();
    desktop = _desktop;
    cache = desktop.getDataCache();
    dc = cache.getDataConnection();
    cv = new WorkshopView(desktop);
//		pv = new AtelierListeView(cache);

    addCard("ateliers", cv);
//		addCard("salles",pv);
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

  @Override
  public boolean validation() {
    try {
      dc.setAutoCommit(false);

      Course a = new Course();
      a.setId(atelier.getId());

      ((CourseIO)DataCache.getDao(Model.Course)).delete(a);
//      dc.executeUpdate("DELETE FROM atelier_ins WHERE id=" + atelier.getId());
//      dc.executeUpdate("DELETE FROM planning WHERE ptype=5 AND action=" + atelier.getId() + " AND jour >= '" + cache.getStartOfYear() + "'");
      dc.commit();
      clear();

    } catch (SQLException e1) {
      GemLogger.logException("update atelier", e1, this);
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
    cv.clear();
//		pv.clear();
  }

  @Override
  public boolean loadCard(Object o) {
    clear();
    if (o == null || !(o instanceof Course)) {
      return false;
    }

    atelier = (Course) o;
    try {
      cv.set(atelier);
//      cv.setId(atelier.getId());
//      cv.setWorkshopName(atelier.getTitle());
      Vector<? extends Person> v = WorkshopIO.findMember(atelier, dc);
      for (int i = 0; i < v.size(); i++) {
        cv.addRow((Musician) v.elementAt(i));
      }

      select(0);
    } catch (SQLException e) {
      System.err.println(e.getMessage());
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
      GemLogger.logException(getClass().getName() + "#loadId :", ex);
    }
    return false;
  }
}

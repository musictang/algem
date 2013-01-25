/*
 * @(#)WorkshopCreateCtrl.java	2.7.a 26/11/12
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

import java.awt.AWTEventMulticaster;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Vector;
import net.algem.contact.Person;
import net.algem.group.Musician;
import net.algem.planning.WorkshopView;
import net.algem.util.DataCache;
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
 * @deprecated 
 */
public class WorkshopCreateCtrl
        extends CardCtrl
{

  private DataCache dataCache;
  private GemDesktop desktop;
  private WorkshopView view;

  //ActionListener	actionListener;
  public WorkshopCreateCtrl(GemDesktop _desktop) {
    //super();
    desktop = _desktop;
    dataCache = desktop.getDataCache();
  }

  public void init() {
    view = new WorkshopView(desktop);
    addCard("Atelier pontuel", view);
    select(0);
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
    clear();
    if (actionListener != null) {
      actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "Abandon"));
    }
    return true;
  }

  @Override
  public void removeActionListener(ActionListener l) {
    actionListener = AWTEventMulticaster.remove(actionListener, l);
  }

  @Override
  public void addActionListener(ActionListener l) {
    actionListener = AWTEventMulticaster.add(actionListener, l);
  }

  @Override
  public boolean validation() {
    try {
      Course p = save();
      select(0);

    } catch (SQLException ex) {
      GemLogger.logException("Insertion atelier", ex, this);
      return false;
    }
    clear();
    return true;
  }

  public void clear() {
    view.clear();
    select(0);
  }

  @Override
  public boolean loadCard(Object p) {
    return false;
  }

  @Override
  public boolean loadId(int id) {
    return false;
  }

  Course save() throws SQLException {

    Course c = view.get();

    c.setCollective(true);
    c.setTitle(c.getTitle().length() > 32 ? c.getTitle().substring(0, 32) : c.getTitle());
    c.setLabel(c.getLabel().length() > 16 ? c.getLabel().substring(0, 16) : c.getLabel());
    c.setCode(Course.ATP_CODE);
    ((CourseIO)DataCache.getDao(Model.Course)).insert(c);

    Vector<Person> v = view.getMember();
    for (int i = 0; i < v.size(); i++) {
      WorkshopIO.insertAdherent(c, (Musician) v.elementAt(i), dataCache.getDataConnection());
    }

    return c;
  }
}


/*
 * @(#)AgendaProfCtrl.java	2.7.a 23/11/12
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
package net.algem.planning.agenda;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.SQLException;
import java.util.Date;
import java.util.Vector;
import net.algem.contact.teacher.TeacherChoice;
import net.algem.planning.DateFr;
import net.algem.planning.PlanningLib;
import net.algem.planning.PlanningLibIO;
import net.algem.util.DataCache;
import net.algem.util.GemLogger;
import net.algem.util.model.Model;
import net.algem.util.ui.GemLabel;
import net.algem.util.ui.GemPanel;

/**
 * comment
 * 
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.a
 * @since 1.0a 07/07/1999
 */
public class AgendaProfCtrl
        extends AgendaCtrl
        implements ItemListener
{

  private TeacherChoice teacher;

  public AgendaProfCtrl(DataCache dataCache, int fmt) {
    super(dataCache, fmt);

    teacher = new TeacherChoice(dataCache.getList(Model.Teacher));
    teacher.addItemListener(this);

    GemPanel p = new GemPanel();
    p.add(new GemLabel("Agenda Prof"));
    p.add(teacher);

    add("North", p);
  }

  public AgendaProfCtrl(DataCache dc) {
    this(dc, SEMAINE);
  }

  @Override
  public void itemStateChanged(ItemEvent e) {
    if (e.getSource() == teacher) {
      load(barre.getDate());
    }
  }

  public void clear() {
    for (int i = 0; i < jours.length; i++) {
      jours[i].getCanvas().clear();
    }
  }

  @Override
  public void load(Date d) {
    clear();

    DateFr deb = new DateFr(d);
    DateFr fin = new DateFr(d);
    fin.incDay(size);
    long start = deb.getDate().getTime() / (60000 * 60 * 24);


    String query = "WHERE jour >= '" + deb + "' AND jour <= '" + fin + "' AND profid = " + teacher.getKey()
            + " ORDER BY jour,debut";
    Vector<PlanningLib> v = new Vector<PlanningLib>();
    try {
      v = PlanningLibIO.find(query, cache.getDataConnection());
    } catch (SQLException e) {
      GemLogger.logException(e);
    }
    if (v.size() < 1) {
      return;
    }
    for (int i = 0; i < v.size(); i++) {
      PlanningLib pl = v.elementAt(i);
      AgendaJourView av = (AgendaJourView) hashdate.get(pl.getDay().toString());
      if (av != null) {
        av.set(pl);
      }
    }
    for (int i = 0; i < jours.length; i++) {
      jours[i].getCanvas().repaint();
    }
//		repaint();
  }
}

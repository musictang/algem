/*
 * @(#)AgendaCtrl.java	2.6.a 20/09/12
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

import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;
import net.algem.planning.DateFr;
import net.algem.util.DataCache;
import net.algem.util.ui.GemBorderPanel;
import net.algem.util.ui.GemLabel;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.GridBagHelper;

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public abstract class AgendaCtrl
        extends GemBorderPanel
        implements ActionListener
{

  static final int JOURS = 1;
  static final int SEMAINE = 2;
  static final int QUINZAINE = 3;
  DataCache cache;
  int format;
  int size = 7;
  int separ = 4;
  AgendaView pages;
  Hashtable hashdate;
  AgendaJourView[] jours;
  AgendaBar barre;
  Calendar cal;

  public AgendaCtrl(DataCache dc, int fmt) {
    cache = dc;
    format = fmt;

    cal = Calendar.getInstance(Locale.FRANCE);

    hashdate = new Hashtable();

    barre = new AgendaBar(14);
    barre.addActionListener(this);

    pages = new AgendaView();

    setLayout(new BorderLayout());
    add("North", new GemLabel("Agenda"));
    add("Center", pages);
    add("South", barre);

    setFormat(pages, fmt);

    setDate(cal.getTime());

  }

  public AgendaCtrl(DataCache dc) {
    this(dc, SEMAINE);
  }

  public void init() {
    /*
     * Frame f = PopupDlg.getTopFrame(this); pop = new PopupPlanning(f, pages,
     * cache); pop.setLibel("prof");
     *
     * pages.addActionListener(pop); pop.addActionListener(this);
     */
  }

  public void setFormat(GemPanel p, int f) {
    format = f;

    switch (f) {
      case JOURS:
        size = 2;
        separ = 1;
        barre.setIncr(2);
        break;
      case SEMAINE:
        size = 7;
        separ = 4;
        barre.setIncr(7);
        break;
      case QUINZAINE:
        size = 12;
        separ = 6;
        barre.setIncr(14);
        break;
    }
//		pages.reset();
    p.removeAll();
    p.setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(p);

    jours = new AgendaJourView[size];
    for (int i = 0; i < separ; i++) {
      jours[i] = new AgendaJourView("East");
      jours[i].getCanvas().addActionListener(pages);
      gb.add(jours[i], 0, i + 1, 1, 1, gb.BOTH, 1.0, 1.0);
    }
    for (int i = 0; i < size - separ; i++) {
      jours[separ + i] = new AgendaJourView("West");
      jours[separ + i].getCanvas().addActionListener(pages);
      gb.add(jours[separ + i], 1, i + 1, 1, 1, gb.BOTH, 1.0, 1.0);
    }

    setDate(barre.getDate());
    validate();
//		doLayout();
//		repaint();
  }

  public void setDate(Date d) {
    cal.setTime(d);
    if (format != JOURS) {
      while (cal.get(Calendar.DAY_OF_WEEK) != cal.getFirstDayOfWeek()) {
        cal.add(Calendar.DATE, -1);
        cal.setTime(cal.getTime());
      }
    }

    barre.setDate(cal.getTime());
    hashdate.clear();

    for (int i = 0; i < separ; i++, cal.add(Calendar.DATE, 1)) {
      jours[i].setDate(cal.getTime());
      hashdate.put(new DateFr(cal.getTime()).toString(), jours[i]);
    }

    if (format == QUINZAINE) {
      cal.add(Calendar.DATE, 1);
    }

    for (int i = separ; i < size; i++, cal.add(Calendar.DATE, 1)) {
      jours[i].setDate(cal.getTime());
      hashdate.put(new DateFr(cal.getTime()).toString(), jours[i]);
    }
  }

  public abstract void load(Date d);

  @Override
  public void actionPerformed(ActionEvent evt) {
    if (evt.getActionCommand().equals("date")
            || evt.getActionCommand().equals("Date")) {
      setDate(barre.getDate());
      load(barre.getDate());
    } else if (evt.getActionCommand().equals("Jours")) {
      setFormat(pages, JOURS);
      load(barre.getDate());
    } else if (evt.getActionCommand().equals("Semaine")) {
      setFormat(pages, SEMAINE);
      load(barre.getDate());
    } else if (evt.getActionCommand().equals("Quinzaine")) {
      setFormat(pages, QUINZAINE);
      load(barre.getDate());
    }
  }
}

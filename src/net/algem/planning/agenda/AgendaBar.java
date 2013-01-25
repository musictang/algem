/*
 * @(#)AgendaBar.java	2.6.a 20/09/12
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

import java.awt.AWTEventMulticaster;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Date;
import net.algem.planning.DateFrField;
import net.algem.util.BundleUtil;
import net.algem.util.GemCommand;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemPanel;

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class AgendaBar
        extends GemPanel
        implements ActionListener
{

  private int incr;
  private DateFrField godate;
  private GemButton prev;
  private GemButton next;
  private GemButton days;
  private GemButton week;
  private GemButton fortnight;
  private Date date;
  private Calendar cal;
  private ActionListener actionListener;
  private static String DAYS = BundleUtil.getLabel("Days.label");
  private static String WEEK = BundleUtil.getLabel("Week.label");
  private static String FORTNIGHT = BundleUtil.getLabel("Fortnight.label");

  public AgendaBar(Date d, int i) {
    date = d;
    cal = Calendar.getInstance();
    cal.setTime(d);
    incr = i;

    setLayout(new GridLayout(1, 6));

    prev = new GemButton(GemCommand.BACK_CMD);
    prev.addActionListener(this);
    next = new GemButton(GemCommand.NEXT_CMD);
    next.addActionListener(this);
    days = new GemButton(DAYS);
    days.addActionListener(this);
    week = new GemButton(WEEK);
    week.addActionListener(this);
    fortnight = new GemButton(FORTNIGHT);
    fortnight.addActionListener(this);
    godate = new DateFrField();
    godate.addActionListener(this);

    add(prev);
    add(godate);
    add(days);
    add(week);
    add(fortnight);
    add(next);
  }

  public AgendaBar(int c) {
    this(new Date(), c);
  }

  public Date getDate() {
    return cal.getTime();
  }

  public void setDate(Date d) {
    date = d;
    cal.setTime(d);
  }

  public void setIncr(int i) {
    incr = i;
  }

  public void removeActionListener(ActionListener l) {
    actionListener = AWTEventMulticaster.remove(actionListener, l);
  }

  public void addActionListener(ActionListener l) {
    actionListener = AWTEventMulticaster.add(actionListener, l);
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    Object c = evt.getSource();
    if (c == days || c == week || c == fortnight) {
      String fmt = FORTNIGHT;
      if (c == days) {
        fmt = DAYS;
      } else if (c == week) {
        fmt = WEEK;
      } else if (c == fortnight) {
        fmt = FORTNIGHT;
      }
      if (actionListener != null) {
        actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, fmt));
      }
      return;
    } else if (c == prev) {
      cal.add(Calendar.DATE, -incr);
    } else if (c == next) {
      cal.add(Calendar.DATE, incr);
    } else if (c == godate) {
      cal.setTime(godate.getDate());
    }
    if (actionListener != null) {
      actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "date"));
    }
  }
}

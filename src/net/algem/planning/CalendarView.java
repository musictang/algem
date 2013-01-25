/*
 * @(#)CalendarView.java	2.6.a 19/09/12
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
package net.algem.planning;

import java.awt.AWTEventMulticaster;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import net.algem.util.BundleUtil;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemField;
import net.algem.util.ui.GemPanel;

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class CalendarView
        extends GemPanel
        implements ActionListener
{

  private GemField dateField;
  private Calendar cal;
  private CanvasCalendar cv;
  private GemButton btNextYear;
  private GemButton btNextMonth;
  private GemButton btPrevMonth;
  private GemButton btPrevYear;
  private GemButton today;
  private boolean holiday[];
  private boolean busy[];
  private ActionListener actionListener;

  public CalendarView() {
    cal = Calendar.getInstance(Locale.FRANCE);

    cv = new CanvasCalendar();
    cv.addActionListener(this);

    holiday = new boolean[31];
    busy = new boolean[31];

    btNextYear = new GemButton(">>>");
    btNextMonth = new GemButton(">>");
    btPrevMonth = new GemButton("<<");
    btPrevYear = new GemButton("<<<");
    today = new GemButton(BundleUtil.getLabel("Action.today.label"));

    btNextYear.addActionListener(this);
    btNextMonth.addActionListener(this);
    btPrevMonth.addActionListener(this);
    btPrevYear.addActionListener(this);
    today.addActionListener(this);

    GemPanel buttons = new GemPanel();
    buttons.setLayout(new FlowLayout());
    buttons.add(btPrevYear);
    buttons.add(btPrevMonth);
    buttons.add(today);
    buttons.add(btNextMonth);
    buttons.add(btNextYear);

    this.setLayout(new BorderLayout());
    add(cv, BorderLayout.CENTER);
    add(buttons, BorderLayout.SOUTH);
  }

  public void removeActionListener(ActionListener l) {
    actionListener = AWTEventMulticaster.remove(actionListener, l);
  }

  public void addActionListener(ActionListener l) {
    actionListener = AWTEventMulticaster.add(actionListener, l);
  }

  public void setBusy(boolean[] d) {
    cv.setOccupation(d);
  }

  public void setHoliday(boolean[] d) {
    cv.setVacance(d);
  }

  public void setDate(Date d) {
    cv.setDate(d);
    cal.setTime(d);
  }

  public Date getDate() {
    return cv.getDate();
  }

  public String getDateLabel() {
    return new DateFr(cv.getDate()).toString();
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    if (actionListener == null) {
      return;
    }

    if (evt.getSource() == btPrevMonth) {
      cal.setTime(cv.getDate());
      cal.add(Calendar.MONTH, -1);
      cv.setDate(cal.getTime());
      actionListener.actionPerformed(new ActionEvent(cal.getTime(), ActionEvent.ACTION_PERFORMED, "date"));
    } else if (evt.getSource() == btNextMonth) {
      cal.setTime(cv.getDate());
      cal.add(Calendar.MONTH, 1);
      cv.setDate(cal.getTime());
      actionListener.actionPerformed(new ActionEvent(cal.getTime(), ActionEvent.ACTION_PERFORMED, "date"));
    } else if (evt.getSource() == btPrevYear) {
      cal.setTime(cv.getDate());
      cal.add(Calendar.YEAR, -1);
      cv.setDate(cal.getTime());
      actionListener.actionPerformed(new ActionEvent(cal.getTime(), ActionEvent.ACTION_PERFORMED, "date"));
    } else if (evt.getSource() == btNextYear) {
      cal.setTime(cv.getDate());
      cal.add(Calendar.YEAR, 1);
      cv.setDate(cal.getTime());
      actionListener.actionPerformed(new ActionEvent(cal.getTime(), ActionEvent.ACTION_PERFORMED, "date"));
    } else if (evt.getSource() == today) {
      cal = Calendar.getInstance(Locale.FRANCE);
      cv.setDate(cal.getTime());
      actionListener.actionPerformed(new ActionEvent(cal.getTime(), ActionEvent.ACTION_PERFORMED, "click"));
    } else if (evt.getSource() instanceof Date) {
      cal.setTime(cv.getDate());
      actionListener.actionPerformed(evt);
    }
  }
}

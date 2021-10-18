/*
 * @(#)MonthSchedule.java	2.6.a 21/09/12
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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.swing.event.EventListenerList;
import net.algem.planning.DateFr;
import net.algem.planning.ScheduleObject;
import net.algem.planning.ScheduleRangeObject;

/**
 * Month schedule.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class MonthSchedule
{

  private Date start = new Date();
  private Date end = new Date();
  private List<ScheduleObject> schedules = new ArrayList<>();
  private List<ScheduleRangeObject> ranges = new ArrayList<>();
  protected EventListenerList listenerList = new EventListenerList();

  public MonthSchedule() {
  }

  @Override
  public String toString() {
    return "PlanningMois: " + start + "-" + end;
  }

  public void setSchedule(Date _start, Date _end, List<ScheduleObject> vs) {
    start = _start;
    end = _end;

    List<ScheduleObject> old = schedules;
    schedules = vs;
    firePropertyChangeEvent(new PropertyChangeEvent(this, "planning", old, schedules));
  }

  public void setScheduleRange(Date _debut, Date _fin, List<ScheduleRangeObject> _plages) {

    List<ScheduleRangeObject> old = ranges;
    ranges = _plages;

    firePropertyChangeEvent(new PropertyChangeEvent(this, "plage", old, ranges));
  }

  public List<ScheduleObject> getSchedule() {
    return schedules;
  }

  public List<ScheduleRangeObject> getScheduleRange() {
    return ranges;
  }

  public Date getStart() {
    return start;
  }

  public Date getEnd() {
    return end;
  }

  public List<ScheduleObject> getDaySchedule(Calendar date) {
    //XXX 	recherche dans plannings
    List<ScheduleObject> v = new ArrayList<>();

    DateFr nd = new DateFr(date.getTime());

    for (int i = 0; i < schedules.size(); i++) {
      ScheduleObject p = (ScheduleObject) schedules.get(i);
      DateFr fd = new DateFr(p.getDate());
      if (fd.equals(nd)) {
        v.add(p);
      }
    }

    return v;
  }

  public List<ScheduleObject> getDayScheduleRange(Calendar date) {
    //XXX 	recherche dans plages
    List<ScheduleObject> v = new ArrayList<>();

    DateFr nd = new DateFr(date.getTime());

    for (int i = 0; i < ranges.size(); i++) {
      ScheduleObject p = (ScheduleObject) ranges.get(i);

      DateFr fd = new DateFr(p.getDate());
      if (fd.equals(nd)) {
        v.add(p);
      }
    }

    return v;
  }

  public void addPropertyChangeListener(PropertyChangeListener l) {
    listenerList.add(PropertyChangeListener.class, l);
  }

  public void removePropertyChangeListener(PropertyChangeListener l) {
    listenerList.remove(PropertyChangeListener.class, l);
  }

  protected void firePropertyChangeEvent(PropertyChangeEvent evt) {
    Object[] listeners = listenerList.getListenerList();

    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == PropertyChangeListener.class) {
        ((PropertyChangeListener) listeners[i + 1]).propertyChange(evt);
      }
    }
  }
}


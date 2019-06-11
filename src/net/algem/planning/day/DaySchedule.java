/*
 * @(#)DaySchedule.java	2.6.a 21/09/12
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
package net.algem.planning.day;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;
import javax.swing.event.EventListenerList;
import net.algem.planning.ScheduleObject;
import net.algem.planning.ScheduleRangeObject;

/**
 * Day schedule model.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">jean-marc gobat</a>
 * @version 2.6.a
 * @since 1.0b 06/10/2001
 */
public class DaySchedule
        implements PropertyChangeListener
{

  private Calendar cal;
  private Vector<ScheduleObject> schedules;
  private Vector<ScheduleRangeObject> ranges;
  protected EventListenerList listenerList = new EventListenerList();

  public DaySchedule() {
    cal = Calendar.getInstance(Locale.FRANCE);
  }

  @Override
  public String toString() {
    return "PlanningJour: " + cal;
  }

  public void setDay(Date d, Vector<ScheduleObject> vs, Vector<ScheduleRangeObject> vr) {
    Date old = cal.getTime();
    cal.setTime(d);

    schedules = vs;
    ranges = vr;

    firePropertyChangeEvent(new PropertyChangeEvent(this, "planning", old, d));
  }

  public Date getDay() {
    return cal.getTime();
  }

  public Vector<ScheduleObject> getSchedules() {
    return schedules;
  }

  public Vector<ScheduleRangeObject> getRanges() {
    return ranges;
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    //System.out.println("DaySchedule.propertyChange:"+evt);
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


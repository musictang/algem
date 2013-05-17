/*
 * @(#)ScheduleDetailEvent.java	2.6.a 19/09/12
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

import java.awt.Point;
import java.util.Date;
import java.util.Vector;
import net.algem.util.event.GemEvent;

/**
 * Schedule detail event.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class ScheduleDetailEvent
        extends GemEvent
{

  private Schedule schedule;
  private Vector<ScheduleRangeObject> ranges;
  private Point position;

  public ScheduleDetailEvent(Object _source, Schedule sched) {
    super(_source, MODIFICATION, DATE);
    schedule = sched;
  }

  public Schedule getSchedule() {
    return schedule;
  }

  public Vector<ScheduleRangeObject> getRanges() {
    return ranges;
  }

  public void setRanges(Vector<ScheduleRangeObject> v) {
    ranges = v;
  }

  public Date getDate() {
    return schedule.getDate().getDate();
  }

  public void setPosition(Point p) {
    position = p;
  }

  public Point getPosition() {
    return position;
  }

  @Override
  public String toString() {
    return "ScheduleDetailEvent:" + type + "," + operation + " " + getDate();
  }
}

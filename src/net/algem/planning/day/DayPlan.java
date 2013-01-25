/*
 * @(#)DayPlan.java	2.6.a 21/09/12
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

import java.util.Vector;
import net.algem.planning.ScheduleObject;
import net.algem.planning.ScheduleRangeObject;

/**
 * Ensemble of schedules and schedule ranges in one column of planning day.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">jean-marc Gobat</a>
 * @version 2.6.a
 * @since 1.0a 07/07/1999
 */
public class DayPlan
{

  private int id;
  private String label;
  private Vector<ScheduleObject> plan;
  private Vector<ScheduleRangeObject> range;

  public DayPlan() {
  }

  public DayPlan(int i, String l, Vector pl, Vector pg) {
    id = i;
    label = l;
    plan = pl;
    range = pg;
  }

  public void setId(int i) {
    id = i;
  }

  public int getId() {
    return id;
  }

  public void setLabel(String l) {
    label = l;
  }

  public String getLabel() {
    return label;
  }

  public void setSchedule(Vector<ScheduleObject> v) {
    plan = v;
  }

  public Vector<ScheduleObject> getSchedule() {
    return plan;
  }

  public void setScheduleRange(Vector<ScheduleRangeObject> v) {
    range = v;
  }

  public Vector<ScheduleRangeObject> getScheduleRange() {
    return range;
  }
}


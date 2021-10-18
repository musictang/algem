/*
 * @(#)DayPlan.java	2.8.w 17/07/14
 * 
 * Copyright (c) 1999-2014 Musiques Tangentes. All Rights Reserved.
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

import java.util.List;
import net.algem.planning.ScheduleObject;
import net.algem.planning.ScheduleRangeObject;
import net.algem.room.DailyTimes;

/**
 * Ensemble of schedules and schedule ranges in one column of planning day.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">jean-marc Gobat</a>
 * @version 2.8.w
 * @since 1.0a 07/07/1999
 */
public class DayPlan
{

  private int id;
  private String label;
  private List<ScheduleObject> schedules;
  private List<ScheduleRangeObject> ranges;
  private DailyTimes [] dailyTimes;

  public DayPlan() {
  }

  public DayPlan(int id, String label, List<ScheduleObject> schedules, List<ScheduleRangeObject> ranges) {
    this.id = id;
    this.label = label;
    this.schedules = schedules;
    this.ranges = ranges;
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

  public void setSchedule(List<ScheduleObject> v) {
    schedules = v;
  }

  public List<ScheduleObject> getSchedule() {
    return schedules;
  }

  public void setScheduleRange(List<ScheduleRangeObject> v) {
    ranges = v;
  }

  public List<ScheduleRangeObject> getScheduleRange() {
    return ranges;
  }

  public DailyTimes getDailyTime(int dow) {
    return dailyTimes == null || dailyTimes.length == 0 ? null : dailyTimes[dow-1];
  }

  public void setDailyTimes(DailyTimes[] dailyTimes) {
    this.dailyTimes = dailyTimes;
  }
  
}


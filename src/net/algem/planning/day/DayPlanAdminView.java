/*
 * @(#)DayPlanAdminView.java 2.11.0 20/09/16
 *
 * Copyright (c) 1999-2016 Musiques Tangentes. All Rights Reserved.
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
 * along with Algem. If not, see http://www.gnu.org/licenses.
 *
 */

package net.algem.planning.day;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import net.algem.contact.Person;
import net.algem.planning.Schedule;
import net.algem.planning.ScheduleIO;
import net.algem.planning.ScheduleObject;
import net.algem.planning.ScheduleRangeObject;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.GemLogger;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.11.0
 * @since 2.9.4.0 25/03/2015
 */
public class DayPlanAdminView
  extends DayPlanTableView
{

  private List<Person> staff;

  public DayPlanAdminView(List<Person> list) {
    super(BundleUtil.getLabel("Person.type.label"));
    this.staff = list;
  }

  @Override
  public void load(Date d, List<ScheduleObject> schedules, List<ScheduleRangeObject> ranges) {
    dayPlanView.clear();
    dayPlanView.setDate(d);
    dayPlanView.setType(Schedule.ADMINISTRATIVE);
    date.set(d);
    dayLabel.setText(date.getDayOfWeek());
    for (Person p : staff) {
      List<ScheduleObject> v1 = getSchedule(schedules, p.getId());
      List<ScheduleRangeObject> v2 = getSchedule(ranges, p.getId());

      if ((v1.size() + v2.size()) > 0) {
        DayPlan pj = new DayPlan();
        pj.setId(p.getId());
        pj.setLabel(p.getFirstName());
        pj.setSchedule(v1);
        pj.setScheduleRange(v2);

        dayPlanView.addCol(pj);
      }
    }
    setScrollBarToZero();

  }

  public <T extends ScheduleObject> List<T> getSchedule(List<T> t, int personId) {
    List<T> v = new ArrayList<>();
    for (int i = 0; i < t.size(); i++) {
      ScheduleObject plan = t.get(i);
      if (plan instanceof ScheduleRangeObject) {
        ScheduleRangeObject range = (ScheduleRangeObject) plan;
        // include course range if any
        if ((range.getMember() != null && range.getMember().getId() == personId)) {
          v.add(t.get(i));
          try {
            range.setFollowUp(ScheduleIO.findFollowUp(range.getNote(), DataCache.getDataConnection()));
          } catch (SQLException ex) {
            GemLogger.log(ex.getMessage());
          }
        }
        else if (range.getPerson() != null && range.getPerson().getId() == personId) {
          v.add(t.get(i));
        }
      } else {
        Person p = plan.getPerson();
        if (p != null && p.getId() == personId) {
          v.add(t.get(i));
        }
      }
    }
    return v;
  }

}

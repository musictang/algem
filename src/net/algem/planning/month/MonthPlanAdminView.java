/*
 * @(#)MonthPlanAdminView.java 2.9.4.0 26/03/2015
 * 
 * Copyright (c) 1999-2015 Musiques Tangentes. All Rights Reserved.
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

package net.algem.planning.month;

import net.algem.contact.Person;
import net.algem.planning.Schedule;
import net.algem.planning.ScheduleDetailEvent;
import net.algem.planning.ScheduleObject;
import net.algem.planning.ScheduleRangeObject;
import net.algem.util.ui.GemChoice;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.0
 * @since 2.9.4.0 26/03/2015
 */
public class MonthPlanAdminView 
  extends MonthPlanDetailView
{

  public MonthPlanAdminView(GemChoice choice) {
    super(choice);
  }

  @Override
  public boolean isNotFiltered(ScheduleObject p) {
    int id = choice.getKey();
    if (p instanceof ScheduleRangeObject) {
      if (Schedule.ADMINISTRATIVE == ((ScheduleRangeObject) p).getType()) {
       return  id > 0 && p.getIdPerson() == id;
      }
//      return Schedule.ADMINISTRATIVE == ((ScheduleRangeObject) p).getType() && id > 0 && p.getIdPerson() == id;
    }

    return Schedule.ADMINISTRATIVE == p.getType() && id > 0 && p.getPerson() != null && p.getPerson().getId() == id;
  }

  @Override
  public void detailChange(ScheduleDetailEvent evt) {
    Person p = ((ScheduleObject) evt.getSchedule()).getPerson();
    if (p != null && choice != null) {
      choice.setKey(p.getId());
    }
  }

}

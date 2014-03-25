/*
 * @(#)PlanningService.java	1.0.2 28/01/14
 *
 * Copyright (c) 2014 Musiques Tangentes. All Rights Reserved.
 *
 * This file is part of Algem Agenda.
 * Algem Agenda is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Algem Agenda is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Algem Agenda. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package net.algem.planning;

import java.util.*;
import net.algem.contact.Person;
import net.algem.contact.PersonIO;
import net.algem.room.Room;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

/**
 * Service class for schedule operations.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 1.0.2
 * @since 1.0.0 11/02/13
 */
@Component
public class PlanningService
{

  @Autowired
  private ScheduleIO scheduleIO;
  @Autowired
  private PersonIO personIO;
  @Autowired
  private MessageSource messageSource;

  public void setScheduleIO(ScheduleIO scheduleIO) {
    this.scheduleIO = scheduleIO;
  }

  public void setPersonIO(PersonIO personIO) {
    this.personIO = personIO;
  }

  /**
   * Returns a map associating room's id with the list of the date's schedules.
   *
   * @param date selected date
   * @param estab establishment number
   * @return a map
   */
  public HashMap<Integer, Collection<ScheduleElement>> getDaySchedule(Date date, int estab) {
    HashMap<Integer, Collection<ScheduleElement>> map = new HashMap<Integer, Collection<ScheduleElement>>();
    int place = -1;
    for (ScheduleElement d : scheduleIO.find(date, estab)) {
      d.setLabel(getHtmlTitle(d));
      if (d.getPlace() != place) {
        place = d.getPlace();
        List<ScheduleElement> elements = new ArrayList<ScheduleElement>();
        elements.add(d);
        map.put(place, elements);
      } else {
        map.get(place).add(d);
      }
    }
    return map;
  }

	/**
	 * Gets the list of free rooms at the date {@code date} in the establishment {@code estab}
	 * @param date date of search
	 * @param estab establishment number
	 * @return a list of rooms
	 */
  public List<Room> getFreeRoom(Date date, int estab) {
    return scheduleIO.getFreeRoom(date, estab);
  }

  /**
   * Gets the list of establishments in the organization.
   *
   * @return a list of persons' instances
   */
  public List<Person> getEstablishments(String where) {
    return personIO.findEstablishments(where);
  }

  /**
   * Gets the label of the schedule {@code e} corresponding to its type.
	 * The label is html formatted.
   *
   * @return a html string representing the schedule element
   */
  private String getHtmlTitle(ScheduleElement e) {
    Locale locale = LocaleContextHolder.getLocale();
    String t = "";
    switch (e.getType()) {
      case Schedule.COURSE_SCHEDULE:
      case Schedule.WORKSHOP_SCHEDULE:
        t = e.getCourseName();
        break;
      case Schedule.GROUP_SCHEDULE:
        t = messageSource.getMessage("group.rehearsal.title", null, locale);
        break;
      case Schedule.MEMBER_SCHEDULE:
        t = messageSource.getMessage("member.rehearsal.title", null, locale);
        break;
      default:
        t = "";
    }
    return t.toUpperCase() + "<br />" + e.getStart() + "-" + e.getEnd();
  }
}

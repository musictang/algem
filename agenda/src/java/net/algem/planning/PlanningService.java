/*
 * @(#)PlanningService.java	1.0.5 14/09/15
 *
 * Copyright (c) 2015 Musiques Tangentes. All Rights Reserved.
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

import java.sql.SQLException;
import java.util.*;
import net.algem.config.Config;
import net.algem.config.ConfigIO;
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
 * @version 1.0.5
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
  private ConfigIO configIO;
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

        List<ScheduleElement> closed = getClosed(place, date);
        if (closed.size() > 0) {
          elements.addAll(closed);
        }
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

  public HashMap<String, Collection<ScheduleElement>> getFreePlace(Date date, int estab) {
    HashMap<String, Collection<ScheduleElement>> map = new HashMap<String, Collection<ScheduleElement>>();
    List<Room> rooms = scheduleIO.getFreeRoom(date, estab);
    for (Room r : rooms) {
      List<ScheduleElement> closed = getClosed(r.getId(), date);
      map.put(r.getName(), closed);
    }
    return map;
  }

  public int getTimeOffset() {
    Config c = configIO.findId("Heure.ouverture");
    return new Hour(c.getValue()).toMinutes();
  }

  private DailyTimes[] findDailyTimes(int roomId) {
    try {
      return scheduleIO.find(roomId);
    } catch (SQLException ex) {
      return getDefaultDailyTimes();
    }
  }

  private List<ScheduleElement> getClosed(int room, int dow) {
    List<ScheduleElement> closed = new ArrayList<ScheduleElement>();
    Hour first = new Hour(getTimeOffset());
    Hour last = new Hour("24:00");

    DailyTimes dt = getDailyTimes(room, dow);
    Hour start = dt.getOpening();
    Hour end = dt.getClosing();

    if (start != null && start.toMinutes() > first.toMinutes()) {
      ScheduleElement s = new ScheduleElement();
      s.setType(Schedule.ROOM);
      s.setStart(first);
      s.setEnd(start);
      closed.add(s);
    }

    if (end != null && end.toMinutes() < 1440) {
      ScheduleElement e = new ScheduleElement();
      e.setType(Schedule.ROOM);
      e.setStart(end.toString().equals(Hour.NULL_HOUR) ? first : end);
      e.setEnd(last);
      closed.add(e);
    }

    return closed;
  }

  private List<ScheduleElement> getClosed(int room, Date date) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    int dow = cal.get(Calendar.DAY_OF_WEEK);
    return getClosed(room, dow);
  }

  private DailyTimes getDailyTimes(int room, int dow) {
    DailyTimes[] dailyTimes = findDailyTimes(room);
    return dailyTimes == null || dailyTimes.length == 0 ? null : dailyTimes[dow-1];
  }

  /**
   * Default opening times.
   * @return an array of daily times
   */
  private DailyTimes[] getDefaultDailyTimes() {
    DailyTimes[] timesArray = new DailyTimes[7];

    for (int i = 0 ; i < 7 ; i++) {
      DailyTimes dt = new DailyTimes(i+1);
      dt.setOpening(new Hour("00:00"));
      dt.setClosing(new Hour("24:00"));
      timesArray[i] = dt;
    }
    return timesArray;
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
      case Schedule.COURSE:
      case Schedule.WORKSHOP:
      case Schedule.TRAINING:
        t = e.getCourseName();
        break;
      case Schedule.GROUP:
        t = messageSource.getMessage("group.rehearsal.title", null, locale);
        break;
      case Schedule.MEMBER:
        t = messageSource.getMessage("member.rehearsal.title", null, locale);
        break;
      default:
        t = "";
    }
    return t.toUpperCase() + "<br />" + e.getStart() + "-" + e.getEnd();
  }


}

/*
 * @(#)TeacherService.java	2.9.4.12 17/09/15
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
 * along with Algem. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package net.algem.contact.teacher;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;
import net.algem.planning.*;
import net.algem.util.DataConnection;
import net.algem.util.GemLogger;

/**
 * Service class for teachers.
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.12
 * @since 2.4.a 22/05/12
 */
public class TeacherService
{

  private DataConnection dc;
  private TeacherIO teacherIO;
  private PlanningService pService;

  public TeacherService(DataConnection dc) {
    this.dc = dc;
    this.teacherIO = new TeacherIO(dc);
  }

  public TeacherService(PlanningService service, DataConnection dc) {
    this(dc);
    pService = service;
  }
  
  /**
   * Finds the schedules of the teacher {@code teacher} between date {@code start}
   * and date {@code end}.
   * @param teacher teacher id
   * @param start date start
   * @param end date end
   * @return a list of schedule ranges
   * @throws java.sql.SQLException
   */
  public Vector<ScheduleRangeObject> getSchedule(int teacher, String start, String end) throws SQLException {
    String query = " SELECT DISTINCT ON(p.jour,pg.debut)" + ScheduleRangeIO.COLUMNS + ", p.jour, p.action, p.idper, p.lieux, p.ptype"
            + " FROM " + ScheduleRangeIO.TABLE + " pg, " + ScheduleIO.TABLE + " p"
            + " WHERE p.jour BETWEEN '" + start + "' AND '" + end 
            + "' AND p.ptype IN (" + Schedule.COURSE + "," + Schedule.TRAINING + "," + Schedule.WORKSHOP + ")"
            + " AND pg.idplanning = p.id"
            + " AND p.idper = " + teacher
            + " ORDER BY p.jour, pg.debut";
    // old query
   /* String query = "WHERE ptype IN (" + Schedule.COURSE + "," + Schedule.TRAINING + "," + Schedule.WORKSHOP + ") AND idper = " + teacher
            + " AND jour >= '" + start + "' AND jour <= '" + end + "' ORDER BY jour,debut";
    return ScheduleIO.find(query, dc); */
    return ScheduleRangeIO.findObject(query, pService, dc);
  }

  /**
   * Finds the schedules of the teacher {@code teacher} between date {@code start}
   * and date {@code end} and in establishment {@code estab}.
   * @param teacher teacher id
   * @param estab establishment id
   * @param start date start
   * @param end date end
   * @return a list of schedules
   */
  public Vector<ScheduleObject> getCourseSchedule(int teacher, int estab, String start, String end) throws SQLException {
    String query = ", salle s, action a WHERE p.ptype = " + Schedule.COURSE + " AND p.idper = " + teacher
              + " AND p.jour>='" + start + "'"
              + " AND p.jour<='" + end + "'"
              + " AND p.lieux = s.id AND s.etablissement=" + estab
              + " AND p.action = a.id"
              + " ORDER BY p.lieux,p.jour,p.debut,a.cours"; //OK
              //+" order by p.idRoom,p.start,p.date,p.action"; //OK mais
              //+" order by extract(dow from p.date),p.idRoom,p.action,p.start"; //OUI MAIS
    return ScheduleIO.findObject(query, dc);
  }

  public Vector<Teacher> findTeachers() throws SQLException {
    return teacherIO.find("");
  }
  
   /**
   * Finds the substitutes for this teacher and for the date selected in schedule.
   * Days of week are represented from index 0 to 6 (from monday to sunday)
   * 
   * @param s schedule object
   * @return a list of teachers
   */
  public Vector<SubstituteTeacher> getSubstitutes(ScheduleObject s) {

    int day = getDayOfWeek(s.getDate().getDate());
    try {
      int c = ActionIO.getCourse(s.getIdAction(), dc);
      // In french calendar, monday is 2. It's the reason why we decrement the date by 2.
      return SubstituteTeacherIO.find(s.getRoom().getEstab(), c, s.getIdPerson(), day - 2, dc);
    } catch (SQLException ex) {
      GemLogger.logException(ex);
      return null;
    }
  }
  
  /**
   * Under postgresql, days of week (only for timestamp values) 
   * are enumerated from 0 to 6 (sunday is 0).
   *
   * @return an integer representing date of week (for FRANCE)
   */
  private int getDayOfWeek(Date date) {

    Calendar cal = Calendar.getInstance(Locale.FRANCE);
    cal.setTime(date);
    return cal.get(Calendar.DAY_OF_WEEK);
  }
}

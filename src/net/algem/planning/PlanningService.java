/*
 * @(#)PlanningService.java	2.12.0 01/03/17
 *
 * Copyright (c) 1999-2017 Musiques Tangentes. All Rights Reserved.
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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormatSymbols;
import java.util.*;
import net.algem.contact.EmployeeIO;
import net.algem.contact.Note;
import net.algem.contact.NoteIO;
import net.algem.contact.Person;
import net.algem.contact.PersonIO;
import net.algem.course.Course;
import net.algem.course.CourseIO;
import net.algem.enrolment.CourseOrder;
import net.algem.enrolment.CourseOrderIO;
import net.algem.enrolment.FollowUp;
import net.algem.room.Room;
import net.algem.util.*;
import net.algem.util.model.Model;
import net.algem.util.ui.MessagePopup;

/**
 * Service class for planning.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.12.0
 * @since 2.4.a 07/05/12
 */
public class PlanningService
{

  public final static String[] WEEK_DAYS = new DateFormatSymbols().getWeekdays();
  private DataConnection dc;
  private ActionIO actionIO;
  private ConflictService conflictService;

  public PlanningService(DataConnection dc) {
    this.dc = dc;
    actionIO = (ActionIO) DataCache.getDao(Model.Action);
    conflictService = new ConflictService(dc);
  }

  /**
   * Utility method to reformat midnight.
   * Postgresql usually represents midnight as "00:00".
   * @param time string-formatted time (hh:mm)
   * @return a string
   */
  public static String getTime(String time) {
    if (time.startsWith("00:00")) {
      return "24:00:00";
    }
    return time;
  }

  public List<DateFr> generationDate(Action a) {
    List<DateFr> v = new ArrayList<DateFr>();

    int i = 0; // sessions number
    Calendar start = Calendar.getInstance(Locale.FRANCE);
    start.setTime(a.getStartDate().getDate());
    Calendar end = Calendar.getInstance(Locale.FRANCE);
    end.setTime(a.getEndDate().getDate());

    while (start.get(Calendar.DAY_OF_WEEK) != a.getDay()) {
      start.add(Calendar.DATE, 1); // day increment
    }
    int dwm = start.get(Calendar.DAY_OF_WEEK_IN_MONTH);
    while (!start.after(end) && i < a.getNSessions()) {
      //sessions.addElement(new DateFr(start.getTime()));
      if (VacationIO.findDay(start.getTime(), a.getVacancy(), dc) == null) {
        v.add(new DateFr(start.getTime()));
        i++;
      }

      switch (a.getPeriodicity()) {
        case WEEK:
          start.add(Calendar.WEEK_OF_YEAR, 1); // week increment
          break;
        case FORTNIGHT:
          start.add(Calendar.WEEK_OF_YEAR, 2); // 2-weeks increment
          break;
        case DAY:
          start.add(Calendar.DATE, 1);
          break;
        case MONTH:
          start.add(Calendar.MONTH, 1);
          start.set(Calendar.DAY_OF_WEEK_IN_MONTH, dwm);
          while (start.get(Calendar.DAY_OF_WEEK) != a.getDay()) {
            start.add(Calendar.DATE, 1);
          }
          break;
      }
    }

    return v;
  }

  void plan(Action a) throws PlanningException {
    try {
      Course c = (Course) DataCache.findId(a.getCourse(), Model.Course);
      int type = 0;
      if (c == null) {
        throw new PlanningException("Course is null");
      }
      switch(c.getCode()) {
        case 11: type = Schedule.WORKSHOP; break;
        case 12: type = Schedule.TRAINING; break;
        default: type = Schedule.COURSE; break;
      }
      actionIO.planify(a, type);
    } catch (SQLException ex) {
      throw new PlanningException(ex.getMessage());
    }

  }

  void plan(Action a, int type) throws PlanningException {
    actionIO.planify(a, type);
  }

  void plan(Action a, int type, List<GemDateTime> dates) throws PlanningException {
    actionIO.planify(a, type, dates);
  }

  void planStudio(StudioSession session) throws PlanningException {
    try {
      dc.setAutoCommit(false);
      Action a = new Action();
      a.setIdper(session.getGroup());
      actionIO.insert(a);
      actionIO.planify(a, Schedule.STUDIO, session.getRooms(), session);
      actionIO.planify(a, Schedule.TECH, new int[] {session.getStudio()}, session);
    } catch (SQLException ex) {
      dc.rollback();
      throw new PlanningException(ex.getMessage());
    } finally {
      dc.setAutoCommit(true);
    }
  }

  void plan(final List<Action> actions) throws PlanningException {
    try {
      dc.withTransaction(new DataConnection.SQLRunnable<Void>()
      {
        @Override
        public Void run(DataConnection conn) throws Exception {
          for (Action a : actions) {
            plan(a);
          }
          return null;
        }

      });
    } catch (Exception e) {
      throw new PlanningException(e.getMessage());
    }
  }

  List<ScheduleTestConflict> planAdministrative(final List<Action> actions) throws PlanningException {
    final List<ScheduleTestConflict> allConflicts = new ArrayList<ScheduleTestConflict>();
    try {
      dc.withTransaction(new DataConnection.SQLRunnable<Void>()
      {
        @Override
        public Void run(DataConnection conn) throws Exception {
          for (final Action a : actions) {
            List<DateFr> dates = generationDate(a);
            for (Iterator<DateFr> it = dates.iterator(); it.hasNext();) {
              DateFr d = it.next();
              List<ScheduleTestConflict> conflicts = getOfficeConflicts(d, a);
              if (conflicts.size() > 0) {
                allConflicts.addAll(conflicts);
                it.remove();
              }
            }
            if (dates.size() > 0) {
              a.setDates(dates);
              plan(a, Schedule.ADMINISTRATIVE);
            } else {
              if (allConflicts.isEmpty()) {
                throw new PlanningException(MessageUtil.getMessage("administrative.schedule.ctrl.no.scheduled.dates"));
              }
          }
          }
          return null;
        }
      });
    } catch (Exception e) {
      throw new PlanningException(e.getMessage());
    }
    return allConflicts;

  }

  public List<Person> getEmployees(Enum type) {
    String where = ", " + EmployeeIO.TYPE_TABLE + " t  WHERE "
      + PersonIO.TABLE + ".id = t.idper AND t.idcat = " + type.ordinal();
    return  PersonIO.find(where, dc);
  }


  /**
   * Replan a course.
   * Planification is allowed only if there are not any members scheduled for
   * this course.
   *
   * @param action
   * @param plan
   * @throws PlanningException
   */
  public void replanify(Action action, ScheduleObject plan) throws PlanningException {
    // TODO reimplement
//    try {
//      dc.setAutoCommit(false);
//      int r = ScheduleIO.deleteSchedule(action, dc);
//      if (r > 0) {// des plages existent encore pour ce planning
//        throw new PlanningException(MessageUtil.getMessage("ranges.deleteById.warning", new Object[]{r}));
//      }
//
//      String query = null;
//
//      for (DateFr d : action.getDates()) {
//        query = "INSERT INTO planning VALUES (DEFAULT"
//                + ",'" + d.toString()
//                + "','" + action.getStartTime() + "','" + action.getEndTime() + "',"
//                + plan.getType() + ","// on ne change pas le type
//                + action.getIdper() + ","
//                + action.getId() + ","
//                + action.getRoom() + ",0)";
//        dc.executeUpdate(query);
//      }
//      dc.commit();
//    } catch (SQLException ex) {
//      dc.rollback();
//      throw new PlanningException(ex.getMessage());
//    }
  }

  /**
   * Removes all schedules sharing the same {@literal action}.
   * Action is also deleted if there are no more schedules in this planning.
   *
   * @param action
   * @throws PlanningException
   */
  public void deletePlanning(Action action) throws PlanningException {

    try {
      int r = ScheduleIO.containRanges(action,  dc);
      if (r == 0) {
        // on vérifie que l'action n'est plus référencée par un planning
        ScheduleIO.deleteSchedule(action, dc);
        List<Schedule> vp = getScheduleByAction(action);
        if (vp == null || vp.isEmpty()) {
          actionIO.delete(action.getId());// on supprime l'action sinon
        }
      } else {
        if (MessagePopup.confirm(null, MessageUtil.getMessage("schedule.suppression.warning", r))) {
          ScheduleIO.deleteSchedule(action, dc);
          ScheduleRangeIO.delete(action, dc);
        } else throw new PlanningException(GemCommand.CANCEL_CMD);
      }

    } catch (SQLException ex) {
      throw new PlanningException(ex.getMessage());
    }
  }

  /**
   * Deletes the schedule {@literal s}.
   * @param s schedule to delete
   * @throws PlanningException if SQLException is caught
   */
  public void deleteSchedule(Schedule s) throws PlanningException {
    try {
      ScheduleIO.delete(s, dc);
      ScheduleRangeIO.delete("idplanning = " + s.getId(), dc);
    } catch (SQLException ex) {
      throw new PlanningException(ex.getMessage());
    }
  }

  /**
   * Removes all schedules sharing the same action {@literal a} and enclosed in the same timeslot.
   * @param a a action
   * @param s selected schedule
   * @throws PlanningException
   */
  public void deleteSchedule(Action a, Schedule s) throws PlanningException {
    try {
      int r = ScheduleIO.containRanges(a, s, dc);
      if (r == 0) {
        ScheduleIO.deleteSchedule(a, s, dc);
      } else {
        if (MessagePopup.confirm(null, MessageUtil.getMessage("schedule.suppression.warning", r))) {
          ScheduleIO.deleteSchedule(a, s, dc);
          ScheduleRangeIO.delete(a, s, dc);
        } else throw new PlanningException(GemCommand.CANCEL_CMD);
      }
    } catch (SQLException ex) {
      throw new PlanningException(ex.getMessage());
    }
  }

   public void deleteScheduleRange(ScheduleRangeObject s) throws PlanningException {
    try {
      ScheduleRangeIO.delete(s, dc);
    } catch (SQLException ex) {
      throw new PlanningException(ex.getMessage());
    }
   }

  /**
   * Gets all schedule sharing the same action {@literal a}.
   * @param a action
   * @return a list of schedules
   */
  private List<Schedule> getScheduleByAction(Action a) {
    return ScheduleIO.find("WHERE action = " + a.getId(), dc);
  }

  /**
   * Checks if several schedules share the same action on the selected date.
   * @param s schedule
   * @return  true if several schedules are found
   */
  public boolean hasSiblings(Schedule s) {
    List<Schedule> schedules = ScheduleIO.find("WHERE action = " + s.getIdAction() + " AND jour = '" + s.getDate() + "'", dc);
    return schedules != null && schedules.size() >= 2;
  }

  /**
   * Checks if other schedules at different times share the same action {@literal a} between 2 dates.
   * @param a action
   * @return true if several schedules are found
   */
   public boolean hasSiblings(Action a) {
    List<Schedule> schedules = ScheduleIO.find("WHERE action = " + a.getId(), dc);
    if (schedules == null || schedules.size() < 2) {
      return false;
    }
    Hour start = schedules.get(0).getStart();
    Hour end = schedules.get(0).getEnd();
    for (Schedule s : schedules) {
      if (! (s.getStart().ge(start) && s.getEnd().le(end))) {
        return true;
      }
    }

    return false;
  }

  /**
   * Modification of schedule time between 2 dates.
   *
   * @param plan schedule instance
   * @param start start date
   * @param end end date
   * @param hStart new start time
   * @param hEnd new end time
   * @throws PlanningException if SQLException or if there is no update
   */
  public void changeHour(ScheduleObject plan, DateFr start, DateFr end, Hour hStart, Hour hEnd) throws PlanningException {
    String query = "UPDATE planning SET debut = '" + hStart + "', fin='" + hEnd + "'"
            + " WHERE action = " + plan.getIdAction()
            + " AND jour >= '" + start + "' AND jour <= '" + end + "'"
            + " AND lieux = " + plan.getIdRoom();
    try {
      if (dc.executeUpdate(query) < 1) {
        throw new PlanningException("PLANNING UPDATE = 0 " + query);
      }
    } catch (SQLException ex) {
      throw new PlanningException(ex.getMessage());
    }
  }

  public boolean isRoomFree(ScheduleTestConflict stc, int room) {
    String query = ConflictQueries.getRoomConflictSelection(stc.getDate().toString(), stc.getStart().toString(), stc.getEnd().toString(), room);
    if (ScheduleIO.count(query, dc) > 0) {
      return false;
    }
    return true;
  }

  /**
   * Changes the schedule location between 2 dates.
   *
   * @param orig initial schedule
   * @param start date start
   * @param end date end
   * @param roomId new idRoom
   * @throws PlanningException
   * @throws SQLException
   */
  public void changeRoom(ScheduleObject orig, DateFr start, DateFr end, int roomId) throws PlanningException, SQLException {

    String query = "UPDATE " + ScheduleIO.TABLE + " SET lieux = " + roomId + " WHERE action = " + orig.getIdAction()
            + " AND jour >= '" + start + "' AND jour <= '" + end + "'"
            + " AND debut >= '" + orig.getStart() + "' AND fin <= '" + orig.getEnd() + "'";
    // le changement de salle est borné à l'heure de début et de fin du planning
    if (dc.executeUpdate(query) < 1) {
      throw new PlanningException("PLANNING UPDATE=0 " + query);
    }
  }

  /**
   * This room modification affects only the selected schedule.
   * This occurs for exemple on studio-type schedules.
   * @param scheduleId
   * @param roomId new room id
   * @throws SQLException
   * @throws PlanningException
   */
  public void changeRoom(int scheduleId, int roomId) throws SQLException, PlanningException {
    String query = "UPDATE " + ScheduleIO.TABLE + " SET lieux = " + roomId + " WHERE id = " + scheduleId;
    if (dc.executeUpdate(query) < 1) {
      throw new PlanningException("PLANNING UPDATE=0 " + query);
    }
  }

  public boolean isTeacherFree(ScheduleTestConflict stc, int teacher) {
    String query = ConflictQueries.getTeacherConflictSelection(stc.getDate().toString(), stc.getStart().toString(), stc.getEnd().toString(), teacher);

    if (ScheduleIO.count(query, dc) > 0) {
      return false;
    }
    return true;
  }

   /**
    * Changes the teacher on selected schedule.
    * @param orig initial schedule
    * @param range replacement schedule
    * @param date start date
    * @throws PlanningException
    */
	 public void changeTeacherForSchedule(ScheduleObject orig, ScheduleObject range, DateFr date) throws PlanningException {

    if (range.getStart().le(orig.getStart()) && range.getEnd().ge(orig.getEnd())) {
      String query = "UPDATE " + ScheduleIO.TABLE + " SET idper = " + range.getIdPerson() + " WHERE id = " + orig.getId();
      try {
        dc.executeUpdate(query);
      } catch (SQLException ex) {
        throw new PlanningException(ex.getMessage());
      }
    } else {
      changeTeacher(orig, range, date, date);
    }
  }

  /**
   * Changes the teacher between 2 dates.
   * If range has not changed,the modification is made for all schedules between 2 dates, else
   * the modification is made only for the selected schedule.
   * Changes may apply on only one part of schedule. In this case, it is necessary to create
   * another planning with same action id.
   *
   * @param orig initial schedule
   * @param start date start
   * @param end date end
   * @param range replacement schedule
   * @throws PlanningException, SQLException
   */
  public void changeTeacher(ScheduleObject orig, ScheduleObject range, DateFr start, DateFr end) throws PlanningException {
    try {
      dc.setAutoCommit(false);
      if (range.getStart().le(orig.getStart()) && range.getEnd().ge(orig.getEnd())) {
        String query = "UPDATE " + ScheduleIO.TABLE + " SET idper = " + range.getIdPerson()
                + " WHERE action = " + orig.getIdAction()
                + " AND jour >= '" + start + "' AND jour <= '" + end + "'";
        if (dc.executeUpdate(query) < 1) {
          throw new PlanningException("PLANNING UPDATE=0 " + query);
        }
      } else if (range.getStart().after(orig.getStart()) && range.getEnd().ge(orig.getEnd())) {
        String query = "UPDATE " + ScheduleIO.TABLE + " SET fin = '" + range.getStart() + "' WHERE id = " + orig.getId();
        dc.executeUpdate(query);
        range.setEnd(orig.getEnd());//possibly greater
        CourseSchedule rc = addSchedule(orig, range);
        ScheduleIO.insert(rc, dc);
        query = "UPDATE " + ScheduleRangeIO.TABLE + " SET idplanning = " + rc.getId()
                + " WHERE idplanning = " + orig.getId() + " AND debut >= '" + rc.getStart() + "'";
        dc.executeUpdate(query);
        orig.setEnd(range.getStart());
      } else if (range.getStart().le(orig.getStart()) && range.getEnd().before(orig.getEnd())) {
        String query = "UPDATE " + ScheduleIO.TABLE + " SET debut = '" + range.getEnd() + "' WHERE id = " + orig.getId();
        dc.executeUpdate(query);
        range.setStart(orig.getStart()); //possibly lower
        CourseSchedule rc = addSchedule(orig, range);
        ScheduleIO.insert(rc, dc);
        query = "UPDATE " + ScheduleRangeIO.TABLE + " SET idplanning = " + rc.getId()
                + " WHERE idplanning = " + orig.getId() + " AND fin <= '" + rc.getEnd() + "'";
        dc.executeUpdate(query);
        orig.setStart(range.getEnd());
      } else {
        String query = "UPDATE " + ScheduleIO.TABLE + " SET fin = '" + range.getStart() + "' WHERE id = " + orig.getId();
        dc.executeUpdate(query);
        CourseSchedule s2 = addSchedule(orig, range);
        ScheduleIO.insert(s2, dc);
        query = "UPDATE " + ScheduleRangeIO.TABLE + " SET idplanning = " + s2.getId()
                + " WHERE idplanning = " + orig.getId() + " AND debut >= '" + s2.getStart() + "' AND fin <= '" + s2.getEnd() + "'";
        dc.executeUpdate(query);
        s2.setStart(range.getEnd());
        s2.setEnd(orig.getEnd());
        s2.setIdPerson(orig.getIdPerson());

        ScheduleIO.insert(s2, dc); //id has changed
        orig.setEnd(range.getStart());
        query = "UPDATE " + ScheduleRangeIO.TABLE + " SET idplanning = " + s2.getId()
                + " WHERE idplanning = " + orig.getId() + " AND debut >= '" + s2.getStart() + "' AND fin <= '" + s2.getEnd() + "'";
        dc.executeUpdate(query);
      }
      dc.commit();
    } catch (SQLException sqe) {
      dc.rollback();
      throw new PlanningException(sqe.getMessage());
    }
  }

  public void addScheduleRange(ScheduleRange sr) throws PlanningException {
    try {
      ScheduleRangeIO.insert(sr, dc);
    } catch (SQLException ex) {
      throw new PlanningException(ex.getMessage());
    }
  }

  public void updateSessionType(Schedule s) throws SQLException {
    ScheduleIO.update(s, dc);
  }

  public Vector<ScheduleTestConflict> checkRange(ScheduleObject orig, ScheduleObject range)
          throws SQLException {
    return conflictService.getRangeConflicts(orig, range, orig.getDate(), orig.getDate());
  }

  /**
   * Gets a schedule object with new teacher id and possibly new beginning and end time.
   *
   * @param s schedule model
   * @param n new schedule
   * @return a schedule object
   * @throws SQLException
   */
  private CourseSchedule addSchedule(ScheduleObject s, ScheduleObject n) throws SQLException {
    CourseSchedule ns = new CourseSchedule(s);
    ns.setStart(n.getStart());
    ns.setEnd(n.getEnd());
    ns.setIdPerson(n.getIdPerson());
    return ns;
  }

  /**
   * Moves a course schedule to another date or time.
   * Room and time may be also modified.
   *
   * @param plan
   * @param newPlan
   *
   * @throws PlanningException if sql exception
   */
  public void postPoneCourse(ScheduleObject plan, ScheduleObject newPlan) throws PlanningException {
    // probleme avec les heures de fin = 24:00 l'update les transforme en 23:59 / erreurs futures dans le décompte des heures
    String query = "UPDATE " + ScheduleIO.TABLE
            + " SET jour = '" + newPlan.getDate()
            + "', debut = '" + newPlan.getStart()
            + "', fin = '" + newPlan.getEnd()
            + "', lieux = " + newPlan.getIdRoom()
            + " WHERE id = " + plan.getId();
    try {
      dc.setAutoCommit(false);
      dc.executeUpdate(query); // update schedule
      int offset = plan.getStart().getLength(newPlan.getStart()); // getLength en minutes entre l'ancienne heure et la nouvelle passée en paramètre.
      query = "UPDATE " + ScheduleRangeIO.TABLE
              + " SET debut = debut + interval '" + offset + " min'"
              // forcer les heures de fin à 24:00 si calcul résultant = 00:00
              + ", fin = (CASE WHEN fin + interval '" + offset + " min' = '00:00:00' THEN '24:00:00' ELSE fin + interval '" + offset + " min' END)"
              //+ ", fin = fin + interval '" + offset + " min'"
              + " WHERE idplanning = " + plan.getId();
      dc.executeUpdate(query); // plage update
      // pour les ateliers ponctuels d'un jour seulement
      if (Schedule.WORKSHOP == plan.getType()) {
        query = "UPDATE " + CourseOrderIO.TABLE
                + " SET debut = '" + newPlan.getStart() + "', fin = '" + newPlan.getEnd()
                + "', datedebut = '" + newPlan.getDate() + "', datefin = '" + newPlan.getDate()
                + "' WHERE idaction = " + plan.getIdAction();
        dc.executeUpdate(query); // commande_cours update
      }
      dc.commit();
    } catch (SQLException ex) {
      dc.rollback();
      throw new PlanningException(ex.getMessage() + " : " + query);
    } finally {
      dc.setAutoCommit(true);
    }
  }

  public void postPoneCourseBefore(ScheduleObject plan, ScheduleObject newPlan, Hour rangeEnd) throws PlanningException {
    String query = "UPDATE " + ScheduleIO.TABLE + " SET debut = '" + rangeEnd + "' WHERE id = " + plan.getId();
    newPlan.setActivity(plan.getActivity());
    newPlan.setIdAction(plan.getIdAction());
    newPlan.setIdPerson(plan.getIdPerson());
    newPlan.setType(plan.getType());
    try {
      dc.setAutoCommit(false);
      dc.executeUpdate(query); // update schedule
      ScheduleIO.insert(newPlan, dc); // create postpone schedule
      int offset = plan.getStart().getLength(newPlan.getStart());
      query = "UPDATE " + ScheduleRangeIO.TABLE
              + " SET idplanning = " + newPlan.getId()
              + " , debut = debut + interval '" + offset + " min'"
              //+ ", fin = fin + interval '" + offset + " min'"
              + ", fin = (CASE WHEN fin + interval '" + offset + " min' = '00:00:00' THEN '24:00:00' ELSE fin + interval '" + offset + " min' END)"
              + " WHERE idplanning = " + plan.getId()
              + " AND fin <= '" + rangeEnd + "'";

      dc.executeUpdate(query);

      dc.commit();
    } catch (SQLException ex) {
      dc.rollback();
      throw new PlanningException(ex.getMessage() + " : " + query);
    } finally {
      dc.setAutoCommit(true);
    }
  }

  public void postPoneCourseAfter(ScheduleObject plan, ScheduleObject newPlan, Hour rangeStart) throws PlanningException {
    String query = "UPDATE " + ScheduleIO.TABLE + " SET fin = '" + rangeStart + "' WHERE id = " + plan.getId();
    newPlan.setActivity(plan.getActivity());
    newPlan.setIdAction(plan.getIdAction());
    newPlan.setIdPerson(plan.getIdPerson());
    newPlan.setType(plan.getType());
    try {
      dc.setAutoCommit(false);
      dc.executeUpdate(query); // update schedule
      ScheduleIO.insert(newPlan, dc); // create postpone schedule
      int offset = rangeStart.getLength(newPlan.getStart());
      query = "UPDATE " + ScheduleRangeIO.TABLE
              + " SET idplanning = " + newPlan.getId()
              + " , debut = debut + interval '" + offset + " min'"
              //+ ", fin = fin + interval '" + offset + " min'"
              + ", fin = (CASE WHEN fin + interval '" + offset + " min' = '00:00:00' THEN '24:00:00' ELSE fin + interval '" + offset + " min' END)"
              + " WHERE idplanning = " + plan.getId()
              + " AND debut >= '" + rangeStart + "'";
      dc.executeUpdate(query);
      dc.commit();
    } catch (SQLException ex) {
      dc.rollback();
      throw new PlanningException(ex.getMessage() + " : " + query);
    } finally {
      dc.setAutoCommit(true);
    }
  }

  public void postPoneCourseBetween(ScheduleObject plan, ScheduleObject newPlan, Hour[] range) throws PlanningException {
    String query = "UPDATE " + ScheduleIO.TABLE + " SET fin = '" + range[0] + "' WHERE id = " + plan.getId();
    newPlan.setActivity(plan.getActivity());
    newPlan.setIdAction(plan.getIdAction());
    newPlan.setIdPerson(plan.getIdPerson());
    newPlan.setType(plan.getType());
    int offset = range[0].getLength(newPlan.getStart());
    try {
      dc.setAutoCommit(false);
      dc.executeUpdate(query); // update schedule
      ScheduleIO.insert(newPlan, dc); // create postpone schedule
      query = "UPDATE " + ScheduleRangeIO.TABLE
              + " SET idplanning = " + newPlan.getId()
              + " , debut = debut + interval '" + offset + " min'"
              + ", fin = fin + interval '" + offset + " min'"
              + " WHERE idplanning = " + plan.getId()
              + " AND debut >= '" + range[0] + "' AND fin <= '" + range[1] + "'";
      dc.executeUpdate(query);
      newPlan.setDate(plan.getDate());//important
      newPlan.setStart(range[1]);
      newPlan.setEnd(plan.getEnd());
      newPlan.setIdRoom(plan.getIdRoom());
      ScheduleIO.insert(newPlan, dc);

      query = "UPDATE " + ScheduleRangeIO.TABLE
              + " SET idplanning = " + newPlan.getId()
              + " WHERE idplanning = " + plan.getId()
              + " AND debut >= '" + range[1] + "'";
      dc.executeUpdate(query);
      dc.commit();
    } catch (SQLException ex) {
      dc.rollback();
      throw new PlanningException(ex.getMessage() + " : " + query);
    } finally {
      dc.setAutoCommit(true);
    }
  }

  public void copySchedule(ScheduleObject model, ScheduleObject copy) throws PlanningException {
    //XXX probleme avec les heures de fin = 24:00 l'update les transforme en 00:00 / erreurs futures dans le décompte des heures
    try {
      dc.setAutoCommit(false);
      ScheduleIO.insert(copy, dc);
      if (Schedule.ADMINISTRATIVE != model.getType()) {
        // getLength en minutes entre l'ancienne heure et la nouvelle passée en paramètre.
        int offset = model.getStart().getLength(copy.getStart());

        String where = "pg WHERE pg.idplanning = " + model.getId();
        Vector<ScheduleRange> vpg = ScheduleRangeIO.find(where, dc);

        for (ScheduleRange pl : vpg) {
          ScheduleRange pg = new ScheduleRange();
          pg.setScheduleId(copy.getId());
          pg.setStart(pl.getStart());
          pg.setEnd(pl.getEnd());
          pg.setMemberId(pl.getMemberId());

          if (offset >= 0) {
            pg.getStart().incMinute(offset);
            pg.getEnd().incMinute(offset);
          } else {
            pg.getStart().decMinute(Math.abs(offset));
            pg.getEnd().decMinute(Math.abs(offset));
          }
          ScheduleRangeIO.insert(pg, dc);
        }
      }
      dc.commit();
    } catch (SQLException ex) {
      dc.rollback();
      throw new PlanningException(ex.getMessage());
    } finally {
      dc.setAutoCommit(true);
    }
  }

  public void changeCourse(Action a) throws PlanningException {
    try {
      dc.setAutoCommit(false);
      //modifier les commande_cours jusqu'à la date de début
      String where = " AND cc.idaction = " + a.getId();
      Vector<CourseOrder> vcc = CourseOrderIO.find(where, dc);

      // selection planning
      Vector<Schedule> vp = ScheduleIO.find("WHERE action = " + a.getId() + " AND jour >= '" + a.getStartDate() + "'", dc);

      //creer nouvelle action
      actionIO.insert(a);

      for (CourseOrder cc : vcc) {

        if (a.getStartDate().after(cc.getDateStart())) {
          // update ancienne commande
          cc.setDateEnd(a.getStartDate());
          CourseOrderIO.update(cc, dc);
          // creation nouvelle commande
          cc.setAction(a.getId()); // l'id a ici été modifiée
          cc.setDateStart(a.getStartDate());
          cc.setDateEnd(a.getEndDate());
          CourseOrderIO.insert(cc, dc);
        } else {
          cc.setAction(a.getId());
          cc.setDateStart(a.getStartDate());
          CourseOrderIO.update(cc, dc);
        }
      }
      // modification plannings
      for (Schedule p : vp) {
        p.setIdAction(a.getId()); // on change le numéro d'action
        ScheduleIO.update(p, dc);
      }
      dc.commit();

    } catch (SQLException ex) {
      dc.rollback();GemLogger.logException(ex);
      throw new PlanningException(ex.getMessage());
    } finally {
      dc.setAutoCommit(true);
    }
  }

  /**
   * Finds the last scheduled date for an action {@literal action}.
   *
   * @param action action id
   * @param dc data connection
   * @return a date
   * @throws SQLException
   */
  public static DateFr getLastDate(int action, DataConnection dc) throws SQLException {

    DateFr date = null;
    String query = "SELECT jour FROM planning WHERE action = " + action + " ORDER BY jour DESC LIMIT 1";
    ResultSet rs = dc.executeQuery(query);
    if (rs.next()) {
      date = new DateFr(rs.getDate(1));
    }
    return date;
  }

  /**
   * Compress or extends a schedule.
   *
   * @param plan the schedule to change
   * @param start start time
   * @param end end time
   * @param endDate last relevant date
   * @throws net.algem.planning.PlanningException
   *
   */
  public void modifyPlanningLength(ScheduleObject plan, Hour start, Hour end, DateFr endDate) throws PlanningException {

    Calendar cal = Calendar.getInstance(Locale.FRANCE);
    cal.setTime(plan.getDate().getDate());
    int dow = cal.get(Calendar.DAY_OF_WEEK) - 1;

    String query = "UPDATE " + ScheduleIO.TABLE + " SET debut = '" + start + "', fin = '" + end + "'"
            + " WHERE jour BETWEEN '" + plan.getDate() + "' AND '" + endDate + "'"
            + " AND date_part('dow', jour) = " + dow
            + " AND debut = '" + plan.getStart() + "' AND fin = '" + plan.getEnd() + "'"
            + " AND action = " + plan.getIdAction() // on ne touche pas aux plannings dont l'action est différente
            + " AND lieux = " + plan.getIdRoom();

    try {
      if (dc.executeUpdate(query) < 1) {
        throw new PlanningException("PLANNING UPDATE = 0 " + query);
      }
      if (plan.getActivity() instanceof Course) {
        Course c = (Course) plan.getActivity();
        if (c.isCollective()) {
          query = "UPDATE " + ScheduleRangeIO.TABLE
                  + " SET debut = '" + start + "', fin = '" + end
                  + "' WHERE idplanning IN ("
                  + " SELECT id FROM " + ScheduleIO.TABLE
                  + " WHERE jour BETWEEN '" + plan.getDate() + "' AND '" + endDate + "'"
                  + " AND date_part('dow', jour) = " + dow
                  + " AND debut = '" + start + "' AND fin = '" + end + "'" // new length
                  + " AND action = " + plan.getIdAction()
                  + " AND lieux = " + plan.getIdRoom()
                  + ")";
          dc.executeUpdate(query);
        }
      }

    } catch (SQLException e) {
      GemLogger.logException("Modification planning", e);
    }
  }

  public void deleteRehearsal(DateFr start, DateFr end, ScheduleObject plan) throws SQLException {
    ScheduleIO.deleteRehearsal(start, end, plan, dc);
    // supprimer action si le planning ne la référence plus
    Vector<Schedule> vp = ScheduleIO.find("WHERE action = " + plan.getIdAction(), dc);
    if (vp == null || vp.isEmpty()) {
      actionIO.delete(plan.getIdAction());
    }
  }

  /**
   * Creates a break for a teacher.
   *
   * @param a action
   * @throws PlanningException if error SQL
   */
  public void createBreak(Action a) throws PlanningException {
    DateFr startDate = a.getStartDate();
    DateFr endDate = a.getEndDate();
    Hour startTime = a.getStartTime();
    Hour endTime = a.getEndTime();
    int course = a.getCourse();
    int room = a.getRoom();
    String where = ", action a WHERE ptype = " + Schedule.COURSE
            + " AND p.action = a.id"
            + " AND a.cours = " + course
            + " AND p.jour >='" + startDate + "' AND p.jour <='" + endDate + "'"
            + " AND extract(dow from timestamp '" + startDate + "') = date_part('dow',jour)"
            + " AND p.idper=" + a.getIdper()
            + " AND p.debut <='" + startTime + "' AND p.fin >='" + endTime + "'"
            + " AND p.lieux=" + room;

    Vector<Schedule> v = ScheduleIO.find(where, dc);
    try {
      dc.setAutoCommit(false);
      for (int i = 0; i < v.size(); i++) {
        Schedule p = v.elementAt(i);
        ScheduleRange pl = new ScheduleRange(p);
        pl.setStart(a.getStartTime());
        pl.setEnd(a.getEndTime());

        String j = p.getDate().toString();
        String hd = startTime.toString();
        String hf = endTime.toString();
        String query = ConflictQueries.getBreakConflict(p.getId(), hd, hf);

        if (ScheduleRangeIO.find(query, dc).size() > 0) {
          throw new SQLException(MessageUtil.getMessage("break.exception.label", new String[]{j, hd, hf}));
        }
        ScheduleRangeIO.insert(pl, dc);
      }
      dc.commit();
    } catch (SQLException e) {
      dc.rollback();
      throw new PlanningException(e.getMessage());
    } finally {
      dc.setAutoCommit(true);
    }
  }

  /**
   * Deletes a break.
   *
   * @param p the break to delete
   * @param start start date
   * @param end end date
   * @throws SQLException
   */
  public void deleteBreak(ScheduleRangeObject p, DateFr start, DateFr end) throws SQLException {
    String where = "adherent = " + Course.BREAK
            + " AND debut ='" + p.getStart() + "' AND fin ='" + p.getEnd() + "'"
            + " AND idplanning IN ("
            + "SELECT id FROM " + ScheduleIO.TABLE + " WHERE action = " + p.getIdAction()
            + " AND jour >='" + start.toString() + "' AND jour <='" + end.toString() + "')";

    ScheduleRangeIO.delete(where, dc);
  }


  /**
   * Modifies an individual followup note.
   *
   * @param range the schedule range
   * @param up monitoring note
   * @throws net.algem.planning.PlanningException
   * @throws SQLException
   */
  public void updateFollowUp(ScheduleRangeObject range, FollowUp up) throws PlanningException, SQLException {
    if (range.getNote() == 0) {
      ScheduleRangeIO.createNote(range, up, dc);
    } else {
      ScheduleRangeIO.updateNote(range.getNote(), up, dc);
    }
  }

  public void deleteFollowUp(ScheduleRangeObject range) throws SQLException {

    if (range.getNote() != 0) {
      ScheduleRangeIO.update("SET note = 0 WHERE id = " + range.getId(), dc);
      ScheduleRangeIO.deleteNote(range.getNote(), dc);
    }
  }

  public void createAdministrativeEvent(final ScheduleRange range,  final FollowUp info, final List<ScheduleRange> attendees) throws PlanningException {
    try {
      dc.withTransaction(new DataConnection.SQLRunnable<Void>()
      {
        @Override
        public Void run(DataConnection conn) throws Exception {
          ScheduleRangeObject rv = new ScheduleRangeObject(range);
          updateFollowUp(rv, info);
          range.setNote(rv.getNote());
          addScheduleRange(range);
          for (ScheduleRange r : attendees) {
            r.setNote(rv.getNote());
            addScheduleRange(r);
          }
          return null;
        }
      });
    } catch (Exception e) {
      throw new PlanningException(e.getMessage());
    }

  }

  /**
   * Gets the list of attendees present in this {@code timeRange}.
   * @param scheduleId
   * @param idper organizer id
   * @param timeRange time range of the event
   * @return a list of persons or an empty list if no person was found
   * @throws SQLException
   */
  public List<Person> getAttendees(int scheduleId, int idper, HourRange timeRange) throws SQLException {
    List<Person> attendees = new ArrayList<>();
    String where = "pg WHERE pg.idplanning = " + scheduleId
      + " AND pg.adherent != " + idper
      + " AND pg.debut = '" + timeRange.getStart()
      + "' AND pg.fin = '" + timeRange.getEnd() + "'";
    List<ScheduleRange> ranges = ScheduleRangeIO.find(where, dc);
    for (ScheduleRange r : ranges) {
      attendees.add((Person) DataCache.findId(r.getMemberId(), Model.Person));
    }
    return attendees;
  }

  /**
   * Gets the contents of the note with id {@code noteId}.
   *
   * @param noteId note Id
   * @return a text or an empty string if no text exists
   * @throws SQLException
   */
  public FollowUp getFollowUp(int noteId) throws SQLException {
    return ScheduleIO.findFollowUp(noteId, dc);
  }

  /**
   * Gets the note contents of the schedule including this {@code rangeId}.
   * The note is common to all persons present in the schedule.
   * @param rangeId range number
   * @return a text or an empty string if no text exists
   * @throws SQLException
   */
  public FollowUp getCollectiveFollowUpByRange(int rangeId) throws SQLException {
    return ScheduleIO.getCollectiveFollowUpByRange(rangeId, dc);
  }

  /**
   * Creates a note for collective follow-up.
   *
   * @param plan the schedule
   * @param text note contents
   * @throws PlanningException if SQL error
   */
  public void createCollectiveFollowUp(ScheduleObject plan, FollowUp up) throws PlanningException {
    ScheduleIO.createCollectiveFollowUp(plan, up, dc);
  }

  /**
   * Creates a note for individual follow-up.
   *
   * @param rangeId range Id
   * @param up follow-up instance
   * @throws PlanningException if SQL error
   */
  public void createIndividualFollowUp(int rangeId, FollowUp up) throws PlanningException {
    ScheduleIO.createIndividualFollowUp(rangeId, up, dc);
  }

  /**
   * Updates a collective follow-up note.
   *
   * @param id note id
   * @param up follow-up instance
   * @throws SQLException
   */
  public void updateFollowUp(int id, FollowUp up) throws SQLException {
    ScheduleIO.updateFollowUp(id, up, dc);
  }

  public Vector<Course> getCourseByTeacher(int teacherId, String dateStart) {
    String query = ", planning p, action a WHERE "
            + "p.action = a.id AND a.cours = c.id"
            + " AND p.idper = " + teacherId
            + " AND p.jour >= '" + dateStart + "'";
    try {
      return ((CourseIO) DataCache.getDao(Model.Course)).find(query);
    } catch (SQLException ex) {
      GemLogger.log(getClass().getName(), "getCourseByTeacher", ex);
    }
    return null;
  }

  public Course getCourseFromAction(int idaction) throws SQLException {
    Action a = (Action) DataCache.findId(idaction, Model.Action);
    if (a != null) {
      return (Course) DataCache.findId(a.getCourse(), Model.Course);
    }
    return ((CourseIO) DataCache.getDao(Model.Course)).findIdByAction(idaction);
  }

  public Course getCourseFromId(int courseId) {
    try {
      return (Course) DataCache.findId(courseId, Model.Course);
    } catch (SQLException ex) {
      GemLogger.logException(ex);
    }
    return null;
  }

  /**
   * Gets all the persons stored in this schedule {@literal id}.
   * @param id schedule id
   * @return a list of persons or an empty list if no one was found
   * @throws SQLException
   */
  public List<Person> getPersons(int id) throws SQLException {
    List<Person> persons = new ArrayList<Person>();
    String query =  "SELECT adherent FROM " + ScheduleRangeIO.TABLE + " WHERE idplanning = " + id;
    ResultSet rs = dc.executeQuery(query);
    while(rs.next()) {
      Person per = (Person) DataCache.findId(rs.getInt(1), Model.Person);
      if (per != null) {
        persons.add(per);
      }
    }
    return persons;
  }

  public Action getAction(int id) throws SQLException {
    return (Action) DataCache.findId(id, Model.Action);
  }

  public void updateAction(final Action a) throws Exception {
    dc.withTransaction(new DataConnection.SQLRunnable<Void>()
    {
      @Override
      public Void run(DataConnection conn) throws Exception {
        actionIO.update(a);
        Note n = a.getNote();
        if (n != null) {
          if (n.getId() == 0) {
            n.setIdPer(a.getId());
            NoteIO.insert(n, dc);
          } else if (n.getIdPer() == 0) {
            NoteIO.delete(n, dc);
          } else {
            NoteIO.update(n, dc);
          }
        }
        return null;
      }
    });

  }

  /**
   *
   * @param range selected time range
   * @param oldTimeRange old time range
   * @param note follow-up content
   * @param attendees list of attendees
   * @throws PlanningException
   */
  public void updateAdministrativeEvent(final ScheduleRangeObject range, final HourRange oldTimeRange, final FollowUp note, final List<Person> attendees) throws PlanningException {
    try {
      dc.withTransaction(new DataConnection.SQLRunnable<Void>() {
        @Override
        public Void run(DataConnection conn) throws Exception {
          ScheduleRangeIO.update(range, dc);
          updateFollowUp(range, note);

          String where = "idplanning = " + range.getScheduleId()
            + " AND adherent != " + range.getMemberId()
            + " AND debut = '" + oldTimeRange.getStart()
            + "' AND fin = '" + oldTimeRange.getEnd() + "'";
          ScheduleRangeIO.delete(where, dc);

          List<ScheduleRange> ranges = new ArrayList<ScheduleRange>();
          if (attendees != null && !attendees.isEmpty()) {
            for (Person p : attendees) {
              ScheduleRange r = new ScheduleRange();
              r.setScheduleId(range.getScheduleId());
              r.setStart(range.getStart());
              r.setEnd(range.getEnd());
              r.setMemberId(p.getId());
              r.setNote(range.getNote());
              ranges.add(r);
            }
          }

          for (ScheduleRange r : ranges) {
            addScheduleRange(r);
          }
          return null;
        }
      });
    } catch (Exception e) {
      throw new PlanningException(e.getMessage());
    }
  }

  /**
   *
   * @param range schedule range to delete
   * @throws PlanningException
   */
  public void deleteAdministrativeEvent(final ScheduleRangeObject range) throws PlanningException {
    try {
      dc.withTransaction(new DataConnection.SQLRunnable<Void>()
      {
        @Override
        public Void run(DataConnection conn) throws Exception {
          deleteFollowUp(range);
          String where = "idplanning = " + range.getScheduleId()
            + " AND debut = '" + range.getStart()
            + "' AND fin = '" + range.getEnd() + "'";
          ScheduleRangeIO.delete(where, dc);
          return null;
        }
      });
    } catch (Exception e) {
      throw new PlanningException(e.getMessage());
    }
  }

  public Vector<ScheduleObject> getSchedule(String where) throws SQLException {
    return ScheduleIO.findObject(where, dc);
  }

  /**
   * Gets a schedule including the range with Id {@code rangeId}.
   * @param rangeId range Id
   * @return a schedule or null if no schedule was found
   */
  public Schedule getScheduleByRange(int rangeId) {
    Vector<Schedule> vp = ScheduleIO.find(", plage pg where p.id = pg.idplanning and pg.id = " + rangeId, dc);
    if (vp.size() > 0) {
      return vp.elementAt(0);
    }
    return null;
  }

  public Vector<ScheduleRangeObject> getScheduleRange(String where) throws SQLException {
    return ScheduleRangeIO.findRangeObject(where, this, dc);
  }

  /**
   * Gets the id of an establishment from an action code.
   * @param action
   * @return an integer or -1 if establishment is not found
   * @throws SQLException
   */
  public int getEstab(int action) throws SQLException {

    Vector<Schedule> v = ScheduleIO.find("WHERE p.action = " + action + " LIMIT 1", dc);

    if (v.isEmpty()) {
      return -1;
    }
    Schedule s = v.elementAt(0);
    Room r = (Room) DataCache.findId(s.getIdRoom(), Model.Room);

    return r == null ? -1 : r.getEstab();

  }

  public Vector<ScheduleTestConflict> testRange(ScheduleObject plan, Hour hStart, Hour hEnd, DateFr lastDate)
          throws SQLException {
    return conflictService.testRange(plan, hStart, hEnd, lastDate);
  }

  public void markPaid(ScheduleObject schedule) throws SQLException {
    String query = "UPDATE planning SET note = 0 WHERE id = " + schedule.getId();
    dc.executeUpdate(query);
  }

  public void markNotPaid(ScheduleObject schedule) throws SQLException {
    String query = "UPDATE planning SET note = -1 WHERE id = " + schedule.getId();
    dc.executeUpdate(query);
  }

  public void cancelBooking(int actionId) throws BookingException {
    ScheduleIO.cancelBooking(actionId, dc);
  }

  public void confirmBooking(Schedule schedule) throws BookingException {
    ScheduleIO.confirmBooking(schedule, dc);
  }

  public Booking getBookingFromAction(int action) throws BookingException {
    return ScheduleIO.findBooking(action, dc);
  }

  public Vector<ScheduleTestConflict> checkHour(ScheduleObject plan, DateFr dateStart, DateFr dateEnd, Hour hStart, Hour hEnd)
          throws SQLException {
    return conflictService.testHourConflict(plan, dateStart, dateEnd, hStart, hEnd);
  }

  public Vector<ScheduleTestConflict> checkChangeRoom(ScheduleObject plan, DateFr dateStart, DateFr dateEnd, int roomId)
          throws SQLException {
    return conflictService.testRoomConflict(plan, dateStart, dateEnd, roomId);
  }

  public Vector<ScheduleTestConflict> checkRoomForScheduleCopy(ScheduleObject newPlan)
          throws SQLException {
    return conflictService.testRoomConflict(newPlan.getDate(), newPlan.getStart(), newPlan.getEnd(), newPlan.getIdRoom());
  }

  public Vector<ScheduleTestConflict> checkRoomForSchedulePostpone(ScheduleObject plan, ScheduleObject newPlan)
          throws SQLException {
    return conflictService.testRoomConflict(plan.getId(), newPlan);
    //return conflictService.testRoomConflict(newPlan.getIdRoom(), plan.getId(), newPlan.getDate(), newPlan.getStart(), newPlan.getEnd());
  }

  public Vector<ScheduleTestConflict> checkRoomForScheduleLengthModif(ScheduleObject plan, Hour hStart, Hour hEnd, DateFr lastDate)
          throws SQLException {
    return conflictService.testRoomConflict(plan, hStart, hEnd, lastDate);
  }

  public Vector<ScheduleTestConflict> checkTeacherForScheduleLengthModif(ScheduleObject plan, DateFr lastDate, Hour hStart, Hour hEnd)
          throws SQLException {
    return conflictService.testTeacherConflictForScheduleLength(plan, lastDate, hStart, hEnd);
  }

  public Vector<ScheduleTestConflict> checkTeacherForScheduleCopy(ScheduleObject plan, ScheduleObject newPlan)
          throws SQLException {
    return conflictService.testTeacherConflict(plan.getIdPerson(), newPlan);
  }

  public Vector<ScheduleTestConflict> checkTeacherForSchedulePostpone(ScheduleObject plan, ScheduleObject newPlan)
          throws SQLException {
    return conflictService.testTeacherConflict(plan, newPlan.getDate(), newPlan.getStart(), newPlan.getEnd());
  }

  public Vector<ScheduleTestConflict> checkChangeTeacher(ScheduleObject orig, ScheduleObject range, DateFr dateStart, DateFr dateEnd)
          throws PlanningException {
    try {
      return conflictService.testTeacherConflict(orig, range, dateStart, dateEnd);
    } catch (SQLException ex) {
      GemLogger.logException(ex);
      throw new PlanningException(ex.getMessage());
    }
  }

  List<ScheduleTestConflict> getOfficeConflicts(DateFr d, Action a) throws SQLException {
    return conflictService.testRoomAndPersonConflicts(d, a);
  }

  private void debug(Vector<DateFr> sessions, int hId) {
    Vector<DateFr> v = new Vector<DateFr>();
    for (DateFr d : sessions) {
      if (VacationIO.findDay(d.getDate(), hId, dc) == null) {
        // si cette date ne correspond pas à un date de vacances du type sélectionné
        v.addElement(d);
      }
    }
    System.out.println("sessions :");
    for (DateFr d : sessions) {
      System.out.println(d.toString());
    }
    System.out.println("planifiées :");
    for (DateFr d : v) {
      System.out.println(d.toString());
    }
  }
}

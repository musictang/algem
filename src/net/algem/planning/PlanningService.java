/*
 * @(#)PlanningService.java	2.8.t 02/05/14
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
package net.algem.planning;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormatSymbols;
import java.util.*;
import net.algem.course.Course;
import net.algem.course.CourseIO;
import net.algem.enrolment.CourseOrder;
import net.algem.enrolment.CourseOrderIO;
import net.algem.room.Room;
import net.algem.util.*;
import net.algem.util.model.Model;
import net.algem.util.ui.MessagePopup;

/**
 * Service class for planning.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.t
 * @since 2.4.a 07/05/12
 */
public class PlanningService
{

  public final static String[] WEEK_DAYS = new DateFormatSymbols(Locale.FRANCE).getWeekdays();
  private DataConnection dc;
  private ActionIO actionIO;
  private ConflictService conflictService;

  public PlanningService(DataConnection dc) {
    this.dc = dc;
    actionIO = (ActionIO) DataCache.getDao(Model.Action);
    conflictService = new ConflictService(dc);
  }

  public List<DateFr> generationDate(Action a) {
    List<DateFr> v = new ArrayList<DateFr>();

    int i = 0; // nombre de séances
    Calendar start = Calendar.getInstance(Locale.FRANCE);
    start.setTime(a.getDateStart().getDate());
    Calendar end = Calendar.getInstance(Locale.FRANCE);
    end.setTime(a.getDateEnd().getDate());

    while (start.get(Calendar.DAY_OF_WEEK) != a.getDay() + 1) {
      start.add(Calendar.DATE, 1); // on incrémente d'un date
    }
    int dwm = start.get(Calendar.DAY_OF_WEEK_IN_MONTH);
    while (!start.after(end) && i < a.getNSessions()) {
      //sessions.addElement(new DateFr(start.getTime()));
      if (VacationIO.findDay(start.getTime(), a.getVacancy(), dc) == null) {
        v.add(new DateFr(start.getTime()));
        i++;
      }

      switch (a.getPeriodicity()) {
        case SEMAINE:
          start.add(Calendar.WEEK_OF_YEAR, 1); // on incrémente d'une semaine
          break;
        case QUINZAINE:
          start.add(Calendar.WEEK_OF_YEAR, 2); // on incrémente de 2 semaines
          break;
        case JOUR:
          start.add(Calendar.DATE, 1);
          break;
        case MOIS:
          start.add(Calendar.MONTH, 1);
          start.set(Calendar.DAY_OF_WEEK_IN_MONTH, dwm);
          while (start.get(Calendar.DAY_OF_WEEK) != a.getDay() + 1) {
            start.add(Calendar.DATE, 1);
          }
          break;
      }
    }
    //debug(v, vid);
    return v;
  }

  public void planify(Action a) throws PlanningException {
    actionIO.planify(a);
  }

  public void planify(Action a, int type) throws PlanningException {
    actionIO.planify(a, type);
  }

  public void planify(Action a, int type, List<GemDateTime> dates) throws PlanningException {
    actionIO.planify(a, type, dates);
  }

  public void planify(List<Action> actions) throws PlanningException {
    try {
      dc.setAutoCommit(false);
      for (Action a : actions) {
        planify(a);
      }
      dc.commit();
    } catch (PlanningException pe) {
      dc.rollback();
      throw pe;
    } catch (SQLException sqe) {
      dc.rollback();
      throw new PlanningException(sqe.getMessage());
    } finally {
      dc.setAutoCommit(true);
    }
  }

  /**
   * Replanify a course.
   * Planification is allowed only if there are not any members scheduled for
   * this course.
   *
   * @param action
   * @param plan
   * @throws PlanningException
   */
  public void replanify(Action action, ScheduleObject plan) throws PlanningException {
//    try {
//      dc.setAutoCommit(false);
//      int r = ScheduleIO.deleteSchedule(action, dc);
//      if (r > 0) {// des plages existent encore pour ce planning
//        throw new PlanningException(MessageUtil.getMessage("ranges.delete.warning", new Object[]{r}));
//      }
//
//      String query = null;
//
//      for (DateFr d : action.getDates()) {
//        query = "INSERT INTO planning VALUES (DEFAULT"
//                + ",'" + d.toString()
//                + "','" + action.getHourStart() + "','" + action.getHourEnd() + "',"
//                + plan.getType() + ","// on ne change pas le type
//                + action.getTeacher() + ","
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
   * Removes all schedules sharing the same {@code action}.
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
   * Removes all schedules sharing the same action {@code a} and enclosed in the same timeslot.
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

  /**
   * Gets all schedule sharing the same action {@code a}.
   * @param a action
   * @return a list of schedules
   */
  private List<Schedule> getScheduleByAction(Action a) {
    return ScheduleIO.find("WHERE action = " + a.getId(), dc);
  }

  /**
   * Indicates if there are multiples time slots for the same planning on the selected date.
   * @param s schedule
   * @return true if multiple time slots are found
   */
  public boolean hasMultipleTimes(Schedule s) {
    List<Schedule> schedules = ScheduleIO.find("WHERE action = " + s.getIdAction() + " AND jour = '" + s.getDate() + "'", dc);
    if (schedules == null || schedules.size() < 2) {
      return false;
    }

    return true;
  }

  /**
   * Indicates if there are multiples time slots for the same planning {@code a} between the selected period.
   * @param a action
   * @return true if multiple time slots are found
   */
   public boolean hasMultipleTimes(Action a) {
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
   * @param plan
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
            + " AND lieux = " + plan.getPlace();
    try {
      if (dc.executeUpdate(query) < 1) {
        throw new PlanningException("PLANNING UPDATE = 0 " + query);
      }
//      if (planCopy instanceof PlanningCours || planCopy instanceof PlanningAtelier) {
//        where = "UPDATE " + ScheduleRangeIO.TABLE + "SET start='" + hdeb + "', end='" + hfin + "' WHERE idplanning = "+planCopy.getId();
//      }
    } catch (SQLException ex) {
      throw new PlanningException(ex.getMessage());
    }
  }

  /**
   * Changes the schedule location between 2 dates.
   *
   * @param orig initial schedule
   * @param start date start
   * @param end date end
   * @param roomId new place
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

	public void changeTeacherForSchedule(ScheduleObject orig, ScheduleObject range, DateFr date) throws PlanningException {
		String query = "UPDATE " + ScheduleIO.TABLE + " SET idper = " + range.getIdPerson()
                + " WHERE id = " + orig.getId()
                + " AND jour = '" + date + "'";
		try {
			dc.executeUpdate(query);
		} catch (SQLException ex) {
			throw new PlanningException(ex.getMessage());
		}
	}

  /**
   * Changes the teacher between 2 dates.
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


//  public Vector<ScheduleTestConflict> testRange(ScheduleObject orig, ScheduleObject range)
//          throws SQLException {
//    return conflictService.getRangeConflicts(orig, range, orig.getDate(), orig.getDate());
//  }
//
//  public Vector<ScheduleTestConflict> testCollectiveRange(ScheduleObject dest, ScheduleObject range)
//          throws SQLException {
//    return conflictService.getRangeConflicts(dest, range, dest.getDate(), dest.getDate());
//  }

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
    // probleme avec les heures de end = 24:00 l'update les transforme en 00:00 / erreurs futures dans le décompte des heures
    String query = "UPDATE " + ScheduleIO.TABLE
            + " SET jour = '" + newPlan.getDate()
            + "', debut = '" + newPlan.getStart()
            + "', fin = '" + newPlan.getEnd()
            + "', lieux = " + newPlan.getPlace()
            + " WHERE id = " + plan.getId();
    try {
      dc.setAutoCommit(false);
      dc.executeUpdate(query); // update schedule
      int offset = plan.getStart().getLength(newPlan.getStart()); // getLength en minutes entre l'ancienne heure et la nouvelle passée en paramètre.
      //XXX probleme avec les heures de fin = 24:00 l'update les transforme en 00:00 / erreurs futures dans le décompte des heures
      query = "UPDATE " + ScheduleRangeIO.TABLE
              + " SET debut = debut + interval '" + offset + " min', fin = fin + interval '" + offset + " min'"
              + " WHERE idplanning = " + plan.getId();
      dc.executeUpdate(query); // plage update
      // pour les ateliers ponctuels d'un jour seulement
      if (Schedule.WORKSHOP_SCHEDULE == plan.getType()) {
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
              + " , debut = debut + interval '" + offset + " min', fin = fin + interval '" + offset + " min'"
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
              + " , debut = debut + interval '" + offset + " min', fin = fin + interval '" + offset + " min'"
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
              + " , debut = debut + interval '" + offset + " min', fin = fin + interval '" + offset + " min'"
              + " WHERE idplanning = " + plan.getId()
              + " AND debut >= '" + range[0] + "' AND fin <= '" + range[1] + "'";
      dc.executeUpdate(query);
      newPlan.setDate(plan.getDate());//important
      newPlan.setStart(range[1]);
      newPlan.setEnd(plan.getEnd());
      newPlan.setPlace(plan.getPlace());
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

  public void copyCourse(ScheduleObject planModel, ScheduleObject planCopy) throws PlanningException {
    //XXX probleme avec les heures de fin = 24:00 l'update les transforme en 00:00 / erreurs futures dans le décompte des heures
    try {
      dc.setAutoCommit(false);
      ScheduleIO.insert(planCopy, dc);
      // getLength en minutes entre l'ancienne heure et la nouvelle passée en paramètre.
      int offset = planModel.getStart().getLength(planCopy.getStart());

      String where = "pg WHERE pg.idplanning = " + planModel.getId();
      Vector<ScheduleRange> vpg = ScheduleRangeIO.find(where, dc);
      for (ScheduleRange pl : vpg) {
        ScheduleRange pg = new ScheduleRange();
        pg.setScheduleId(planCopy.getId());
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
      String where = "AND cc.idaction = " + a.getId();
      Vector<CourseOrder> vcc = CourseOrderIO.find(where, dc);

      // selection planning
      Vector<Schedule> vp = ScheduleIO.find("WHERE action = " + a.getId() + " AND jour >= '" + a.getDateStart() + "'", dc);

      //creer nouvelle action
      actionIO.insert(a);

      for (CourseOrder cc : vcc) {

        if (a.getDateStart().after(cc.getDateStart())) {
          // update ancienne commande
          cc.setDateEnd(a.getDateStart());
          CourseOrderIO.update(cc, dc);
          // creation nouvelle commande
          cc.setAction(a.getId()); // l'id a ici été modifiée
          cc.setDateStart(a.getDateStart());
          cc.setDateEnd(a.getDateEnd());
          CourseOrderIO.insert(cc, dc);
        } else {
          cc.setAction(a.getId());
          cc.setDateStart(a.getDateStart());
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
      dc.rollback();
      throw new PlanningException(ex.getMessage());
    } finally {
      dc.setAutoCommit(true);
    }
  }

  /**
   * Finds the last scheduled date for an action {@code action}.
   *
   * @param action action id
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
   * Compression or extension of a schedule.
   *
   * @version 1.1b
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
            + " AND lieux = " + plan.getPlace();

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
                  + " AND lieux = " + plan.getPlace()
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
    DateFr dateDebut = a.getDateStart();
    DateFr dateFin = a.getDateEnd();
    Hour heureDebut = a.getHourStart();
    Hour heureFin = a.getHourEnd();
    int cours = a.getCourse();
    int salle = a.getRoom();
    String where = ", action a WHERE ptype = " + Schedule.COURSE_SCHEDULE
            + " AND p.action = a.id"
            + " AND a.cours = " + cours
            + " AND p.jour >='" + dateDebut + "' AND p.jour <='" + dateFin + "'"
            + " AND extract(dow from timestamp '" + dateDebut + "') = date_part('dow',jour)"
            + " AND p.idper=" + a.getTeacher()
            + " AND p.debut <='" + heureDebut + "' AND p.fin >='" + heureFin + "'"
            + " AND p.lieux=" + salle;

    Vector<Schedule> v = ScheduleIO.find(where, dc);
    try {
      dc.setAutoCommit(false);
      for (int i = 0; i < v.size(); i++) {
        Schedule p = v.elementAt(i);
        ScheduleRange pl = new ScheduleRange(p);
        pl.setStart(a.getHourStart());
        pl.setEnd(a.getHourEnd());

        String j = p.getDate().toString();
        String hd = heureDebut.toString();
        String hf = heureFin.toString();
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
   * Gets an individual followup note.
   *
   * @param idnote note id
   * @return a string
   * @throws SQLException
   */
  public String getFollowUp(int idnote) throws SQLException {
    return ScheduleRangeIO.findNote(idnote, dc);
  }

  /**
   * Modifies a followup note.
   *
   * @param range the schedule range
   * @param t the note
   * @throws SQLException
   */
  public void updateFollowUp(ScheduleRangeObject range, String t) throws PlanningException, SQLException {
    if (range.getNote() == 0) {
      ScheduleRangeIO.createNote(range, t, dc);
    } else {
      ScheduleRangeIO.updateNote(range.getNote(), t, dc);
    }
  }

  public void deleteFollowUp(ScheduleRangeObject range) throws SQLException {

    if (range.getNote() != 0) {
      ScheduleRangeIO.update("SET note = 0 WHERE id = " + range.getId(), dc);
      ScheduleRangeIO.deleteNote(range.getNote(), dc);
    }
  }

  /**
   * Gets a collective follow-up note.
   *
   * @param idnote note id
   * @return a string annotation
   * @throws SQLException
   */
  public String getCollectiveFollowUp(int idnote) throws SQLException {
    return ScheduleIO.findFollowUp(idnote, dc);
  }

  /**
   * Creates a note for collective follow-up.
   *
   * @param plan the schedule
   * @param text
   * @throws PlanningException if SQL error
   */
  public void createFollowUp(ScheduleObject plan, String text) throws PlanningException {
    ScheduleIO.createFollowUp(plan, text, dc);
  }

  /**
   * Updates a collective follow-up note.
   *
   * @param id note id
   * @param text
   * @throws SQLException
   */
  public void updateFollowUp(int id, String text) throws SQLException {
    ScheduleIO.updateFollowUp(id, text, dc);
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

  public Action getAction(int id) throws SQLException {
    return (Action) DataCache.findId(id, Model.Action);
//    return actionIO.findId(id);
  }

  public void updateAction(Action a) throws SQLException {
    actionIO.update(a);

  }

  public Vector<ScheduleObject> getSchedule(String where) throws SQLException {
    return ScheduleIO.findObject(where, dc);
  }

  public Vector<ScheduleRangeObject> getScheduleRange(String where) throws SQLException {
    return ScheduleRangeIO.findObject(where, this, dc);
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
    Room r = (Room) DataCache.findId(s.getPlace(), Model.Room);

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

  public Vector<ScheduleTestConflict> testHour(ScheduleObject plan, DateFr dateStart, DateFr dateEnd, Hour hStart, Hour hEnd)
          throws SQLException {
    return conflictService.testHourConflict(plan, dateStart, dateEnd, hStart, hEnd);
  }

  public Vector<ScheduleTestConflict> testChangeRoom(ScheduleObject plan, DateFr dateStart, DateFr dateEnd, int roomId)
          throws SQLException {
    return conflictService.testRoomConflict(plan, dateStart, dateEnd, roomId);
  }

  public Vector<ScheduleTestConflict> testRoomForScheduleCopy(ScheduleObject newPlan)
          throws SQLException {
    return conflictService.testRoomConflict(newPlan.getDate(), newPlan.getStart(), newPlan.getEnd(), newPlan.getPlace());
  }

  public Vector<ScheduleTestConflict> testRoomForSchedulePostpone(ScheduleObject plan, ScheduleObject newPlan)
          throws SQLException {
    return conflictService.testRoomConflict(plan.getId(), newPlan);
    //return conflictService.testRoomConflict(newPlan.getPlace(), plan.getId(), newPlan.getDate(), newPlan.getStart(), newPlan.getEnd());
  }

  public Vector<ScheduleTestConflict> testRoomForScheduleLengthModif(ScheduleObject plan, Hour hStart, Hour hEnd, DateFr lastDate)
          throws SQLException {
    return conflictService.testRoomConflict(plan, hStart, hEnd, lastDate);
  }

  public Vector<ScheduleTestConflict> testTeacherForScheduleCopy(ScheduleObject plan, ScheduleObject newPlan)
          throws SQLException {
    return conflictService.testTeacherConflict(plan.getIdPerson(), newPlan);
  }

  public Vector<ScheduleTestConflict> testTeacherForSchedulePostpone(ScheduleObject plan, ScheduleObject newPlan)
          throws SQLException {
    return conflictService.testTeacherConflict(plan, newPlan.getDate(), newPlan.getStart(), newPlan.getEnd());
  }

  public Vector<ScheduleTestConflict> testChangeTeacher(ScheduleObject orig, ScheduleObject range, DateFr dateStart, DateFr dateEnd)
          throws PlanningException {
    try {
      return conflictService.testTeacherConflict(orig, range, dateStart, dateEnd);
    } catch (SQLException ex) {
      GemLogger.logException(ex);
      throw new PlanningException(ex.getMessage());
    }
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

/*
 * @(#)ScheduleIO.java	2.11.0 28/09/16
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
 * along with Algem. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package net.algem.planning;

import java.lang.reflect.Constructor;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import net.algem.config.GemParam;
import net.algem.contact.Person;
import net.algem.course.Course;
import net.algem.enrolment.FollowUp;
import net.algem.group.Group;
import net.algem.room.Room;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;
import net.algem.util.GemLogger;
import net.algem.util.model.Model;
import net.algem.util.model.TableIO;

/**
 * IO methods for class {@link net.algem.planning.Schedule}.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.11.0
 */
public class ScheduleIO
    extends TableIO {

  public final static String TABLE = "planning";
  public final static String SEQUENCE = "planning_id_seq";
  public final static String FOLLOW_UP_TABLE = "suivi";
  public final static String FOLLOW_UP_SEQUENCE = "idsuivi";
  public final static String COLUMNS = "p.id,p.jour,p.debut,p.fin,p.ptype,p.idper,p.action,p.lieux,p.note";
  public static String BUSY_ROOM_STMT = "SELECT count(id) FROM planning WHERE lieux = ? AND jour > '01-01-1999'";
  //private static String findHistoRepet = "SELECT "+COLUMNS+" FROM planning p WHERE p.ptype="+Schedule.MEMBER+" AND p.idper= ? AND (date BETWEEN ? AND ?) ORDER BY date,start";

  public static void insert(Schedule p, DataConnection dc) throws SQLException {

    int id = nextId(SEQUENCE, dc);

    String query = "INSERT INTO " + TABLE + " VALUES("
        + id
        + ",'" + p.getDate().toString() + "'"
        + ",'" + p.getStart() + "'"
        + ",'" + p.getEnd() + "'"
        + "," + p.getType()
        + "," + p.getIdPerson()
        + "," + p.getIdAction()
        + "," + p.getIdRoom()
        + "," + p.getNote()
        + ")";

    dc.executeUpdate(query);
    p.setId(id);
  }

  public static void update(Schedule p, DataConnection dc) throws SQLException {
    String query = "UPDATE " + TABLE + " SET "
        + "jour = '" + p.getDate()
        + "',debut = '" + p.getStart()
        + "',fin = '" + p.getEnd()
        + "',ptype = " + p.getType()
        + ",idper = " + p.getIdPerson()
        + ",action = " + p.getIdAction()
        + ",lieux = " + p.getIdRoom()
        + ",note = " + p.getNote()
        + " WHERE id = " + p.getId();

    dc.executeUpdate(query);
  }

  public static void delete(Schedule p, DataConnection dc) throws SQLException {
    String query = "DELETE FROM " + TABLE + " WHERE id = " + p.getId();
    dc.executeUpdate(query);
  }

  public static void delete(String conditions, DataConnection dc) throws SQLException {
    String query = "DELETE FROM " + TABLE + " WHERE " + conditions;
    dc.executeUpdate(query);
  }

  /**
   * Rehearsal suppression.
   *
   * @param startDate start date of removal
   * @param endDate end date of removal
   * @param sched schedule to remove
   * @param dc dataConnection
   * @throws SQLException
   */
  public static void deleteRehearsal(DateFr startDate, DateFr endDate, ScheduleObject sched, DataConnection dc) throws SQLException {
    String query = "jour >= '" + startDate + "' AND jour <= '" + endDate + "'"
        + " AND (ptype = " + Schedule.GROUP + " OR ptype=" + Schedule.MEMBER + ")"
        + " AND action = " + sched.getIdAction();
    delete(query, dc);
  }

  public static Booking findBooking(int actionId, DataConnection dc) throws BookingException {
    String query = "SELECT id,idper,dateres,pass,statut FROM reservation WHERE idaction = ?";
    ResultSet rs = null;
    try (PreparedStatement ps = dc.prepareStatement(query)) {
      ps.setInt(1, actionId);
      rs = ps.executeQuery();
      while (rs.next()) {
        Booking b = new Booking();
        b.setId(rs.getInt(1));
        b.setAction(actionId);
        b.setPerson(rs.getInt(2));
        b.setBookingDate(rs.getDate(3));
        b.setPass(rs.getBoolean(4));
        b.setStatus(rs.getByte(5));
        return b;
      }
    } catch (Exception ex) {
      throw new BookingException(ex.getMessage());
    } finally {
      if (rs != null) {
        try {
          rs.close();
        } catch (SQLException ex) {
          GemLogger.log(ex.getMessage());
        }
      }
    }
    return null;
  }

  public static void cancelBooking(final int action, DataConnection dc) throws BookingException {
    String sql = "DELETE FROM reservation WHERE idaction = ?";
    try (PreparedStatement ps = dc.prepareStatement(sql)) {
      dc.withTransaction(new DataConnection.SQLRunnable<Void>() {
        @Override
        public Void run(DataConnection conn) throws Exception {
          delete("action = " + action, conn);
          ps.setInt(1, action);
          ps.executeUpdate();
          return null;
        }
      });
    } catch (Exception ex) {
      throw new BookingException(ex.getMessage());
    }
  }

  public static void confirmBooking(final Schedule schedule, DataConnection dc) throws BookingException {
    if (Schedule.BOOKING_GROUP != schedule.getType() && Schedule.BOOKING_MEMBER != schedule.getType()) {
      return;
    }
    try {
      final String sql = "UPDATE " + TABLE + " SET ptype = " + (Schedule.BOOKING_GROUP == schedule.getType() ? Schedule.GROUP : Schedule.MEMBER) + " WHERE id = ?";
      final String sql2 = "UPDATE reservation SET statut = 1 WHERE idaction = ?";
      dc.withTransaction(new DataConnection.SQLRunnable<Void>() {
        @Override
        public Void run(DataConnection conn) throws Exception {
          try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, schedule.getId());
            ps.executeUpdate();
          } catch (SQLException ex) {
            throw new BookingException(ex.getMessage());
          }
          try (PreparedStatement ps = conn.prepareStatement(sql2)) {
            ps.setInt(1, schedule.getIdAction());
            ps.executeUpdate();
          } catch (SQLException ex) {
            throw new BookingException(ex.getMessage());
          }
          return null;
        }
      });
    } catch (Exception e) {
      throw new BookingException(e.getMessage());
    }

  }

  /**
   * Schedule suppression.
   *
   * @param action scheduling link
   * @param dc dataConnection
   *
   * @throws SQLException
   */
  public static void deleteSchedule(Action action, DataConnection dc) throws SQLException {
    String query = getDeleteScheduleQuery(action);
    dc.executeUpdate(query);
  }

  public static void deleteSchedule(Action action, Schedule s, DataConnection dc) throws SQLException {
    String query = getDeleteScheduleQuery(action);
    query += " AND debut = '" + s.getStart() + "' AND fin = '" + s.getEnd() + "'";
    dc.executeUpdate(query);
  }

  public static Vector<Schedule> find(String where, DataConnection dc) {
    return findClass(Schedule.class, where, dc);
  }

  public static Vector<Schedule> findCourse(String where, DataConnection dc) {
    return findClass(CourseScheduleObject.class, where, dc);
  }

  public static Schedule findId(int id, DataConnection dc) {

    String query = "SELECT " + COLUMNS + " FROM " + TABLE + " p WHERE id = " + id;
    Schedule p = null;
    try {
      ResultSet rs = dc.executeQuery(query);
      if (rs.next()) {
        p = new Schedule();
        p.setId(rs.getInt(1));
        p.setDate(new DateFr(rs.getString(2)));
        p.setStart(new Hour(rs.getString(3)));
        p.setEnd(new Hour(PlanningService.getTime(rs.getString(4))));
        p.setType(rs.getInt(5));
        p.setIdPerson(rs.getInt(6));
        p.setIdAction(rs.getInt(7));
        p.setIdRoom(rs.getInt(8));
        p.setNote(rs.getInt(9));
      }
      rs.close();
    } catch (SQLException e) {
      GemLogger.logException(e);
    }
    return p;
  }

  static Vector<Schedule> findClass(Class c, String where, DataConnection dc) {

    Vector<Schedule> v = new Vector<Schedule>();
    String query = "SELECT " + COLUMNS + " FROM " + TABLE + " p " + where;

    try {
      ResultSet rs = dc.executeQuery(query);
      while (rs.next()) {
        Constructor ct = c.getConstructor();
        Schedule p = (Schedule) ct.newInstance();
        p.setId(rs.getInt(1));
        p.setDate(new DateFr(rs.getString(2)));
        p.setStart(new Hour(rs.getString(3)));
        p.setEnd(new Hour(PlanningService.getTime(rs.getString(4))));
        p.setType(rs.getInt(5));
        p.setIdPerson(rs.getInt(6));
        p.setIdAction(rs.getInt(7));
        p.setIdRoom(rs.getInt(8));
        p.setNote(rs.getInt(9));

        v.addElement(p);
      }
      rs.close();
    } catch (Exception e) {
      GemLogger.logException(e);
    }
    return v;
  }

  private static void fillPlanning(ResultSet rs, ScheduleObject p)
      throws SQLException {
    p.setId(rs.getInt(1));
    p.setDate(new DateFr(rs.getString(2)));
    p.setStart(new Hour(rs.getString(3)));
    p.setEnd(new Hour(PlanningService.getTime(rs.getString(4))));
    p.setType(rs.getInt(5));
    p.setIdPerson(rs.getInt(6));
    p.setIdAction(rs.getInt(7));
    p.setIdRoom(rs.getInt(8));
    p.setNote(rs.getInt(9));

    p.setRoom((Room) DataCache.findId(p.getIdRoom(), Model.Room));

  }

  public static Vector<ScheduleObject> getLoadRS(PreparedStatement ps, DataConnection dc)
      throws SQLException {

    Vector<ScheduleObject> v = new Vector<ScheduleObject>();

    try (ResultSet rs = ps.executeQuery()) {
      while (!Thread.interrupted() && rs.next()) {
        ScheduleObject p = planningObjectFactory(rs, dc);
        v.addElement(p);
      }
    }
    return v;
  }

  private static ScheduleObject planningObjectFactory(ResultSet rs, DataConnection dc)
      throws SQLException {

    ScheduleObject p = null;

    switch (rs.getInt(5)) {
      case Schedule.COURSE:
      case Schedule.TRAINING:
        p = new CourseSchedule();
        fillPlanning(rs, p);
        ((CourseSchedule) p).setTeacher((Person) DataCache.findId(p.getIdPerson(), Model.Teacher));
        Action a = (Action) DataCache.findId(p.getIdAction(), Model.Action);
        ((CourseSchedule) p).setAction(a);
        ((CourseSchedule) p).setCourse((Course) DataCache.findId(a.getCourse(), Model.Course));
        break;
      case Schedule.MEMBER:
        p = new MemberRehearsalSchedule();
        fillPlanning(rs, p);
        ((MemberRehearsalSchedule) p).setMember((Person) DataCache.findId(p.getIdPerson(), Model.Person));
        break;
      case Schedule.GROUP:
        p = new GroupRehearsalSchedule();
        fillPlanning(rs, p);
        ((GroupRehearsalSchedule) p).setGroup((Group) DataCache.findId(p.getIdPerson(), Model.Group));
        break;
      case Schedule.STUDIO:
        p = new GroupStudioSchedule();
        fillPlanning(rs, p);
        ((GroupStudioSchedule) p).setGroup((Group) DataCache.findId(p.getIdPerson(), Model.Group));
        ((GroupStudioSchedule) p).setActivity((GemParam) DataCache.findId(p.getNote(), Model.StudioType));
        break;
      case Schedule.TECH:
        p = new TechStudioSchedule();
        fillPlanning(rs, p);
        int tech = getFirstPerson(p.getId(), dc);
        if (tech > 0) {
          Person per = (Person) DataCache.findId(tech, Model.Person);
          ((TechStudioSchedule) p).setTechnicianLabel(per.getAbbrevFirstNameName());
        }
        ((TechStudioSchedule) p).setGroup((Group) DataCache.findId(p.getIdPerson(), Model.Group));
        ((TechStudioSchedule) p).setActivity((GemParam) DataCache.findId(p.getNote(), Model.StudioType));
        break;
      case Schedule.WORKSHOP:
        p = new WorkshopSchedule();
        fillPlanning(rs, p);
        ((CourseSchedule) p).setTeacher((Person) DataCache.findId(p.getIdPerson(), Model.Teacher));
        Action w = (Action) DataCache.findId(p.getIdAction(), Model.Action);
        ((CourseSchedule) p).setAction(w);
        ((CourseSchedule) p).setCourse((Course) DataCache.findId(w.getCourse(), Model.Course));
        break;
      case Schedule.ADMINISTRATIVE:
        p = new AdministrativeSchedule();
        fillPlanning(rs, p);
        p.setPerson((Person) DataCache.findId(p.getIdPerson(), Model.Person));
        break;
      case Schedule.BOOKING_GROUP:
        p = new BookingGroupSchedule();
        fillPlanning(rs, p);
        ((GroupRehearsalSchedule) p).setGroup((Group) DataCache.findId(p.getIdPerson(), Model.Group));
        break;
      case Schedule.BOOKING_MEMBER:
        p = new BookingMemberSchedule();
        fillPlanning(rs, p);
        p.setPerson((Person) DataCache.findId(p.getIdPerson(), Model.Person));
        break;
    }

    return p;
  }

  /**
   * Gets the first person's id stored in this schedule {@literal id}.
   *
   * @param id schedule id
   * @param dc data connection
   * @return an integer >= 0
   * @throws SQLException
   */
  private static int getFirstPerson(int id, DataConnection dc) throws SQLException {
    int t = 0;
    String query = "SELECT adherent FROM " + ScheduleRangeIO.TABLE + " WHERE idplanning = " + id + " ORDER BY id LIMIT 1";
    ResultSet rs = dc.executeQuery(query);
    if (rs.next()) {
      t = rs.getInt(1);
    }
    return t;
  }

  public static Vector<ScheduleObject> findObject(String where, DataConnection dc)
      throws SQLException {
    Vector<ScheduleObject> v = new Vector<ScheduleObject>();

    String query = "SELECT " + COLUMNS + " FROM " + TABLE + " p " + where;
    ResultSet rs = dc.executeQuery(query);

    while (rs.next()) {
      ScheduleObject p = planningObjectFactory(rs, dc);
      v.addElement(p);
    }
    rs.close();
    return v;
  }

  public static int count(String where, DataConnection dc) {
    int n = 1;
    String query = "SELECT count(" + TABLE + ".id) FROM " + TABLE + " " + where;
    try {
      ResultSet rs = dc.executeQuery(query);
      if (rs.next()) {
        n = rs.getInt(1);
      }
    } catch (SQLException e) {
      GemLogger.logException(e);
    }
    return n;
  }

  public static void createCollectiveFollowUp(Schedule s, FollowUp up, DataConnection dc) throws PlanningException {

    try {
      dc.setAutoCommit(false);
      int noteId = createFollowUp(up, dc);
      s.setNote(noteId);
      update(s, dc);
      dc.commit();
    } catch (SQLException ex) {
      dc.rollback();
      throw new PlanningException(ex.getMessage());
    } finally {
      dc.setAutoCommit(true);
    }
  }

  public static void createIndividualFollowUp(int rangeId, FollowUp up, DataConnection dc) throws PlanningException {
    try {
      dc.setAutoCommit(false);
      int noteId = createFollowUp(up, dc);
      Vector<ScheduleRange> ranges = ScheduleRangeIO.find("pg WHERE pg.id = " + rangeId, dc);
      if (ranges.size() > 0) {
        ScheduleRange r = ranges.elementAt(0);
        r.setNote(noteId);
        ScheduleRangeIO.update(r, dc);
      }
      dc.commit();
    } catch (SQLException ex) {
      dc.rollback();
      throw new PlanningException(ex.getMessage());
    } finally {
      dc.setAutoCommit(true);
    }
  }

  private static int createFollowUp(FollowUp up, DataConnection dc) throws SQLException {
    int nextId = nextId(FOLLOW_UP_SEQUENCE, dc);
    String content = up.getContent() == null || up.getContent().isEmpty() ? "NULL," : "'" + escape(up.getContent()) + "',";
    String note = up.getNote() == null || up.getNote().isEmpty() ? "NULL," : "'" + escape(up.getNote()) + "',";
    String query = "INSERT INTO " + FOLLOW_UP_TABLE + " VALUES("
        + nextId + ","
        + content
        + note
        + up.getStatus() + ")";

//    String query = "INSERT INTO " + FOLLOW_UP_TABLE
//            + " VALUES(" + nextId + ",'" + escape(text) + "')";
    dc.executeUpdate(query);
    return nextId;
  }

  public static void updateFollowUp(int noteId, FollowUp up, DataConnection dc) throws SQLException {
    String content = up.getContent() == null || up.getContent().isEmpty() ? "NULL," : "'" + escape(up.getContent()) + "',";
    String n = up.getNote() == null || up.getNote().isEmpty() ? "NULL," : "'" + escape(up.getNote()) + "',";
//    String query = "UPDATE " + FOLLOW_UP_TABLE + " SET texte = '" + escape(text) + "' WHERE id = " + noteId;
    String query = "UPDATE " + FOLLOW_UP_TABLE
        + " SET texte = " + content
        + " note = " + n
        + " statut = " + up.getStatus()
        + " WHERE id = " + noteId;
    dc.executeUpdate(query);
  }

  /**
   * Find the text value of the note with Id {@code noteId}.
   *
   * @param noteId
   * @param dc data Connection
   * @return a text or an empty string if there is no text for this note
   * @throws SQLException
   */
  public static FollowUp findFollowUp(int noteId, DataConnection dc) throws SQLException {

    String query = "SELECT texte,CASE WHEN id = 0 AND note = '0' THEN NULL ELSE note END AS note1,statut FROM " + FOLLOW_UP_TABLE + " WHERE id = " + noteId;

    try (ResultSet rs = dc.executeQuery(query)) {
      if (rs.next()) {
        FollowUp up = new FollowUp();
        up.setContent(unEscape(rs.getString(1)));
        up.setNote(unEscape(rs.getString(2)));
        up.setStatus(rs.getShort(3));
        return up;
      }
      return null;
    }
  }

  /**
   * Find the note value of the schedule including the range {@code rangeId}.
   *
   * @param rangeId range Id
   * @param dc data connection
   * @return a text or an empty string if there is no text for this note
   * @throws SQLException
   */
  public static FollowUp getCollectiveFollowUpByRange(int rangeId, DataConnection dc) throws SQLException {

    FollowUp up = new FollowUp();
    String query = "SELECT s.id, s.texte FROM " + FOLLOW_UP_TABLE + " s, " + ScheduleRangeIO.TABLE + " pg, " + ScheduleIO.TABLE + " p"
        + " WHERE pg.id = " + rangeId + " AND pg.idplanning = p.id AND p.note > 0 AND p.note = s.id ";

    ResultSet rs = dc.executeQuery(query);
    if (rs.next()) {
      up.setId(rs.getInt(1));
      up.setContent(unEscape(rs.getString(2)));
    }
    return up;
  }

  private static String getDeleteScheduleQuery(Action a) {
    String query = "DELETE FROM " + TABLE
        + " WHERE jour >= '" + a.getStartDate() + "' AND jour <= '" + a.getEndDate() + "'"
        + " AND action = " + a.getId();
    return query;
  }

  /**
   * Gets the number of schedule ranges in planning {@literal a}.
   *
   * @param a action
   * @param dc dataConnection
   * @return a number of ranges (may be 0)
   * @throws SQLException
   */
  public static int containRanges(Action a, DataConnection dc) throws SQLException {
    int rows = 0;
    String query = getQueryNumberOfRanges(a);
    ResultSet rs = dc.executeQuery(query);
    if (rs.next()) {
      rows = rs.getInt("nb_cours");
    }
    return rows;
  }

  public static int containRanges(Action a, Schedule s, DataConnection dc) throws SQLException {
    int rows = 0;
    String query = getQueryNumberOfRanges(a);
    query += " AND p.debut >= '" + s.getStart() + "' AND p.fin <= '" + s.getEnd() + "'";
    ResultSet rs = dc.executeQuery(query);
    if (rs.next()) {
      rows = rs.getInt("nb_cours");
    }
    return rows;
  }

  private static String getQueryNumberOfRanges(Action a) {
    return "SELECT COUNT(pg.debut) AS nb_cours FROM " + ScheduleRangeIO.TABLE + " pg, " + TABLE + " p"
        + " WHERE pg.idplanning = p.id AND p.action = " + a.getId()
        + " AND p.jour >= '" + a.getStartDate() + "' AND p.jour <= '" + a.getEndDate() + "'";
  }

  /**
   * Gets a result set listing start and end time of all schedules which type,
   * date and action equal to model {@literal p}.
   *
   * @param p schedule
   * @param dc dataConnection
   * @return a resultSet
   * @throws SQLException
   */
  public static ResultSet getRSCourseRange(Schedule p, DataConnection dc) throws SQLException {

    String query = "SELECT debut, fin FROM " + TABLE + " WHERE ptype = " + p.getType()
        + " AND jour = '" + p.getDate() + "'"
        + " AND action = " + p.getIdAction() + " ORDER BY debut";
    return dc.executeQuery(query);
  }
}

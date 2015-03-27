/*
 * @(#)ScheduleRangeIO.java	2.9.1 27/11/14
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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import net.algem.contact.Person;
import net.algem.course.Course;
import net.algem.room.Room;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;
import net.algem.util.GemLogger;
import net.algem.util.model.Model;
import net.algem.util.model.TableIO;

/**
 * IO methods for class {@link net.algem.planning.ScheduleRange}.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.1
 * @since 1.0a 7/7/1999
 */
public class ScheduleRangeIO
  extends TableIO {

  public final static String TABLE = "plage";
  public final static String COLUMNS = "pg.id, pg.idplanning, pg.debut, pg.fin, pg.adherent, pg.note";
  public final static String SEQUENCE = "plage_id_seq";

  public static void insert(ScheduleRange p, DataConnection dc) throws SQLException {

    int id = nextId(SEQUENCE, dc);
    String query = "INSERT INTO " + TABLE + " VALUES("
      + id
      + ", " + p.getScheduleId()
      + ",'" + p.getStart()
      + "','" + p.getEnd()
      + "'," + p.getMemberId()
      + "," + p.getNote()
      + ")";
    dc.executeUpdate(query);
  }

  public static void update(ScheduleRange p, DataConnection dc) throws SQLException {
    String query = "UPDATE " + TABLE + " SET debut = '" + p.getStart()
      + "', fin = '" + p.getEnd()
      + "', adherent = " + p.getMemberId()
      + ", note = " + p.getNote()
      + " WHERE id =" + p.getId();
    dc.executeUpdate(query);
  }

  public static void update(ScheduleRangeObject p, DataConnection dc) throws SQLException {
    String query = "UPDATE " + TABLE + " SET debut = '" + p.getStart()
      + "', fin = '" + p.getEnd()
      + "', adherent = " + p.getMember().getId()
      + ", note = " + p.getNote()
      + " WHERE id=" + p.getId();
    dc.executeUpdate(query);
  }

  public static void update(String _query, DataConnection dc) throws SQLException {
    String query = "UPDATE " + TABLE + " " + _query;
    dc.executeUpdate(query);
  }

  public static void delete(ScheduleRange p, DataConnection dc) throws SQLException {
    String query = "DELETE FROM " + TABLE + " WHERE id = " + p.getId();
    dc.executeUpdate(query);
  }

  public static void delete(ScheduleRangeObject p, DataConnection dc) throws SQLException {
    String query = "DELETE FROM " + TABLE + " WHERE id = " + p.getId();
    dc.executeUpdate(query);
  }

  /**
   * Suppress ranges by action.
   *
   * @param a action
   * @param dc data connection
   * @throws SQLException
   */
  public static void delete(Action a, DataConnection dc) throws SQLException {
    String query = "DELETE FROM " + TABLE + " WHERE idplanning IN ("
      + "SELECT id FROM " + ScheduleIO.TABLE
      + " WHERE action = " + a.getId()
      + " AND jour >= '" + a.getDateStart() + "' AND jour <= '" + a.getDateEnd() + "')";
    dc.executeUpdate(query);
  }

  /**
   * Deletes all ranges scheduled with the action {@literal a} and enclosed in the selected time slots.
   * @param a action
   * @param s schedule
   * @param dc data connection
   * @throws SQLException
   */
  public static void delete(Action a, Schedule s, DataConnection dc) throws SQLException {
    String query = "DELETE FROM " + TABLE + " WHERE idplanning IN ("
      + "SELECT id FROM " + ScheduleIO.TABLE
      + " WHERE action = " + a.getId()
      + " AND jour >= '" + a.getDateStart() + "' AND jour <= '" + a.getDateEnd()
      + "' AND debut >= '" + s.getStart() + "' AND fin <= '" + s.getEnd() + "')";
    dc.executeUpdate(query);
  }

  public static void delete(String where, DataConnection dc) throws SQLException {
    String query = "DELETE FROM " + TABLE + " WHERE " + where;
    dc.executeUpdate(query);
  }

  public static Vector<ScheduleRange> find(String where, DataConnection dc) throws SQLException {
    String query = "SELECT " + COLUMNS + " FROM " + TABLE + " " + where;

    return ifind(query, dc);
  }

  private static Vector<ScheduleRange> ifind(String query, DataConnection dc) throws SQLException {
    Vector<ScheduleRange> v = new Vector<ScheduleRange>();
    ResultSet rs = dc.executeQuery(query);
    while (rs.next()) {
      ScheduleRange p = new ScheduleRange();
      p.setId(rs.getInt(1));
      p.setScheduleId(rs.getInt(2));
      p.setStart(new Hour(rs.getString(3)));
      p.setEnd(new Hour(rs.getString(4)));
      p.setMemberId(rs.getInt(5));
      p.setNote(rs.getInt(6));

      v.addElement(p);
    }
    rs.close();

    return v;
  }

  private static ScheduleRangeObject rangeObjectFactory(ResultSet rs, PlanningService service)
    throws SQLException {

    ScheduleRangeObject p = new ScheduleRangeObject();
    p.setId(rs.getInt(1));
    p.setScheduleId(rs.getInt(2));
    p.setStart(new Hour(rs.getString(3)));
    p.setEnd(new Hour(rs.getString(4)));
    p.setMember((Person) DataCache.findId(rs.getInt(5), Model.Person));
    p.setNote(rs.getInt(6));

    p.setDate(new DateFr(rs.getString(7)));
    p.setIdAction(rs.getInt(8));
    p.setIdPerson(rs.getInt(9));// id prof
    p.setIdRoom(rs.getInt(10));
    p.setType(rs.getInt(11));

    p.setRoom((Room) DataCache.findId(p.getIdRoom(), Model.Room));
    p.setTeacher((Person) DataCache.findId(p.getIdPerson(), Model.Teacher));

    p.setAction(service.getAction(p.getIdAction()));
    p.setCourse((Course) DataCache.findId(p.getAction().getCourse(), Model.Course));

    return p;
  }

  public static Vector<ScheduleRangeObject> findObject(String and, PlanningService service, DataConnection dc) throws SQLException {
    String query = "SELECT " + COLUMNS + ", p.jour, p.action, p.idper, p.lieux"
      + " FROM " + TABLE + " pg, " + ScheduleIO.TABLE + " p"
      + " WHERE pg.idplanning = p.id " + and;

    Vector<ScheduleRangeObject> v = new Vector<ScheduleRangeObject>();

    ResultSet rs = dc.executeQuery(query);
    while (rs.next()) {
      ScheduleRangeObject p = rangeObjectFactory(rs, service);
      v.addElement(p);
    }
    rs.close();

    return v;
  }

  public static Vector<ScheduleRangeObject> getLoadRS(PreparedStatement ps, DataCache dataCache) {

    Vector<ScheduleRangeObject> v = new Vector<ScheduleRangeObject>();
    PlanningService service = new PlanningService(DataCache.getDataConnection());
    try {
      ResultSet rs = ps.executeQuery();
      while (!Thread.interrupted() && rs.next()) {
        ScheduleRangeObject p = rangeObjectFactory(rs, service);
        v.addElement(p);
      }
      rs.close();
    } catch (SQLException e) {
      GemLogger.logException("plage prepared stmt", e);
    }
    return v;
  }

  public static Vector<ScheduleRangeObject> findFollowUp(String where, boolean action, DataConnection dc) throws SQLException {
    Vector<ScheduleRangeObject> v = new Vector<ScheduleRangeObject>();
    String query = getFollowUpRequest(action);
    query += where;

    PlanningService pService = new PlanningService(dc);

    ResultSet rs = dc.executeQuery(query);
    while (rs.next()) {
      ScheduleRangeObject p = rangeObjectFactory(rs, pService);
      p.setNote1(rs.getString(11));
      p.setNote2(rs.getString(12));
      v.addElement(p);
    }
    rs.close();
    return v;
  }

  private static String getFollowUpRequest(boolean action) {//TODO !!!!
    if (action) {
      return "SELECT " + COLUMNS + ", p.jour, p.action, p.idper, p.lieux, s1.texte, s2.texte FROM " + TABLE + " pg, planning p, action a, suivi s1, suivi s2"
        + " WHERE p.ptype IN (" + Schedule.COURSE + "," + Schedule.WORKSHOP + "," + Schedule.TRAINING
        + ") AND p.id = pg.idplanning";

    } else {
      return "SELECT " + COLUMNS + ", p.jour, p.action, p.idper, p.lieux, s1.texte, s2.texte FROM " + TABLE + " pg, planning p, suivi s1, suivi s2"
        + " WHERE p.ptype IN (" + Schedule.COURSE + "," + Schedule.WORKSHOP + "," + Schedule.TRAINING
        + ") AND p.id = pg.idplanning";
    }
  }

  public static void createNote(ScheduleRangeObject range, String text, DataConnection dc) throws PlanningException {
    try {
      dc.setAutoCommit(false);
      int id = nextId("idsuivi", dc);
      String query = "INSERT INTO suivi VALUES(" + id + ",'" + escape(text) + "')";
      dc.executeUpdate(query);

      range.setNote(id);
      update(range, dc);
    } catch (SQLException sqe) {
      dc.rollback();
      throw new PlanningException(sqe.getMessage());
    } finally {
      dc.setAutoCommit(true);
    }
  }

  public static void updateNote(int note, String text, DataConnection dc) throws SQLException {
    String query = "UPDATE suivi SET texte = '" + escape(text) + "' WHERE id = " + note;
    dc.executeUpdate(query);
  }

  public static void deleteNote(int note, DataConnection dc) throws SQLException {
    String query = "DELETE FROM suivi WHERE id = " + note;
    dc.executeUpdate(query);
  }

  public static String findNote(int note, DataConnection dc) throws SQLException {

    String texte = "";
    String query = "SELECT texte FROM suivi WHERE id = " + note;

    ResultSet rs = dc.executeQuery(query);
    if (rs.next()) {
      texte = unEscape(rs.getString(1));
    }

    return texte;
  }

  public static String getMonthRangeStmt() {
    return "SELECT " + COLUMNS + ", p.jour, p.action, p.idper, p.lieux  FROM " + TABLE + " pg, " + ScheduleIO.TABLE + " p"
      + " WHERE pg.idplanning = p.id"
      + " AND p.jour >= ? AND p.jour <= ? ORDER BY p.jour, pg.debut";
  }

  public static String getDayRangeStmt() {
    return "SELECT " + COLUMNS + ", p.jour, p.action, p.idper, p.lieux, p.ptype FROM " + TABLE + " pg, " + ScheduleIO.TABLE + " p"
      + " WHERE pg.idplanning = p.id"
      + " AND p.jour = ? ORDER BY p.debut, pg.debut"; //?? ou p.action, p.start
  }
}

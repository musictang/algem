/*
 * @(#)ActionIO.java 2.10.0 14/06/2016
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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import net.algem.config.AgeRange;
import net.algem.config.GemParam;
import net.algem.contact.Note;
import net.algem.contact.NoteIO;
import net.algem.contact.Person;
import net.algem.course.Course;
import net.algem.course.CourseModuleInfo;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;
import net.algem.util.GemLogger;
import net.algem.util.model.Cacheable;
import net.algem.util.model.Model;
import net.algem.util.model.TableIO;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.10.0
 * @since 2.4.a 18/04/12
 */
public class ActionIO
        extends TableIO
        implements Cacheable
{

  public static final String TABLE = "action";
  public static final String COLUMNS = "id, cours, niveau, places, tage, statut";
  public static final String SEQUENCE = "action_id_seq";
  private static final String ACTION_COLOR_TABLE = "action_color";
  private DataConnection dc;
  private static final String ACTION_COLOR_QUERY = "SELECT color FROM " + ACTION_COLOR_TABLE + " WHERE idaction = ?";
  private PreparedStatement colorStatement;


  public ActionIO(DataConnection dc) {
    this.dc = dc;
    this.colorStatement = dc.prepareStatement(ACTION_COLOR_QUERY);
  }

  public void planify(final Action a, final int type) throws PlanningException {
    try {
      dc.withTransaction(new DataConnection.SQLRunnable<Void>()
      {
        @Override
        public Void run(DataConnection conn) throws Exception {
          insert(a);
          String query = null;
          for (DateFr d : a.getDates()) {
            query = "INSERT INTO planning VALUES (DEFAULT"
                    + ",'" + d.toString()
                    + "','" + a.getHourStart() + "','" + a.getHourEnd() + "',"
                    + type + ","
                    + a.getIdper() + ","
                    + a.getId() + ","
                    + a.getRoom() + ",0)";
            dc.executeUpdate(query);
          }
          if (a.getColor() != 0) {
            query = "INSERT INTO " + ACTION_COLOR_TABLE + " VALUES(" + a.getId() + "," + a.getColor() + ")";
            dc.executeUpdate(query);
          }
          return null;
        }
      });
    } catch (Exception e) {
      GemLogger.log(e.getMessage());
      throw new PlanningException(e.getMessage());
    }
  }

  /**
   * Planifies an action schedule with type {@code type } at one or more dates and times.
   * @param a action to planify
   * @param type the schedule's type
   * @param dates list of dates and times
   * @throws PlanningException if SQL exception is thrown
   */
  public void planify(Action a, int type, List<GemDateTime> dates) throws PlanningException {
    try {
      dc.setAutoCommit(false);
      insert(a);
      for (GemDateTime dt : dates) {
        String query = "INSERT INTO planning VALUES (DEFAULT"
                + ",'" + dt.getDate()
                + "','" + dt.getTimeRange().getStart() + "','" + dt.getTimeRange().getEnd() + "',"
                + type + ","
                + a.getIdper() + ","
                + a.getId() + ","
                + a.getRoom() + ", 0)";
        dc.executeUpdate(query);
      }
      dc.commit();
    } catch (SQLException ex) {
      dc.rollback();
      GemLogger.log(ex.getMessage());
      throw new PlanningException(ex.getMessage());
    } finally {
      dc.setAutoCommit(true);
    }
  }

  public void planify(Action a, int type, int[] rooms, StudioSession session) throws SQLException, PlanningException {
    if (rooms == null) {
      return;
    }
    for (GemDateTime dt : session.getDates()) {
      for (int r : rooms) {
        Schedule s = new Schedule();
        s.setDate(dt.getDate());
        s.setStart(dt.getTimeRange().getStart());
        s.setEnd(dt.getTimeRange().getEnd());
        s.setType(type);
        s.setIdPerson(a.getIdper());
        s.setIdAction(a.getId());
        s.setIdRoom(r);
        if (session.getCategory() != null) {
          s.setNote(session.getCategory().getId());
        }
        ScheduleIO.insert(s, dc);

        if (type == Schedule.TECH) {
          for (int m : session.getTechnicians()) {
            ScheduleRange sr = new ScheduleRange();
            sr.setScheduleId(s.getId());
            sr.setStart(s.getStart());
            sr.setEnd(s.getEnd());
            sr.setMemberId(m);
            ScheduleRangeIO.insert(sr, dc);
          }
        }
      }
    }
  }

  public void planify(Action a) throws PlanningException {
    planify(a, Schedule.COURSE);
  }

  public void insert(Action a) throws SQLException {
    int nextid = nextId(SEQUENCE, dc);

    String query = "INSERT INTO " + TABLE + " VALUES("
            + nextid
            + "," + a.getCourse()
            + "," + (a.getLevel() == null ? 0 : a.getLevel().getId())
            + "," + a.getPlaces()
            + "," + (a.getAgeRange() == null ? 0 : a.getAgeRange().getId())
            + "," + (a.getStatus() == null ? 0 : a.getStatus().getId())
            + ")";
    dc.executeUpdate(query);
    a.setId(nextid);
  }

  public void update(Action a) throws SQLException {
    String query = "UPDATE " + TABLE
            + " SET cours = " + a.getCourse()
            + ", niveau = " + (a.getLevel() == null ? 0 : a.getLevel().getId())
            + ", places = " + a.getPlaces()
            + ", tage = " + (a.getAgeRange() == null ? 0 : a.getAgeRange().getId())
            + ", statut = " + (a.getStatus() == null ? 0 : a.getStatus().getId())
            + " WHERE id = " + a.getId();
    dc.executeUpdate(query);
    if (a.getColor() != 0) {
      if (getColor(a.getId()) != 0) {
        query = "UPDATE " + ACTION_COLOR_TABLE + " SET color = " + a.getColor() + " WHERE idaction = " + a.getId();
      } else {
        query = "INSERT INTO " + ACTION_COLOR_TABLE + " VALUES(" + a.getId() + "," + a.getColor() + ")";
      }
      dc.executeUpdate(query);
    } else if (a.hasResetDefaultColor()) {
      query = "DELETE FROM " + ACTION_COLOR_TABLE + " WHERE idaction = " + a.getId();
      dc.executeUpdate(query);
    }
  }

  public void delete(int id) throws SQLException {
    if (id > 0) { // don't delete default action
      String query = "DELETE FROM " + TABLE + " WHERE id = " + id;
      dc.executeUpdate(query);
    }
  }

  public static int getCourse(int idaction, DataConnection dc) throws SQLException {

    String query = "SELECT cours FROM " + TABLE + " WHERE id = " + idaction;
    ResultSet rs = dc.executeQuery(query);
    if (rs.next()) {
      return rs.getInt(1);
    }
    return 0;

  }

  public int findId(String where) throws SQLException {
    int action = 0;
    String query = "SELECT DISTINCT action.id FROM " + TABLE + " " + where;
    query += " LIMIT 1";
    ResultSet rs = dc.executeQuery(query);
    if (rs.next()) {
      action = rs.getInt(1);
    }
    return action;
  }

  public Action findId(int id) throws SQLException {
    Action a = null;
    String query = "SELECT DISTINCT " + COLUMNS + " FROM " + TABLE + " WHERE id = " + id;
    try (ResultSet rs = dc.executeQuery(query)) {
      if (rs.next()) {
        a = getFromRS(rs);
      }
    } catch (SQLException ex) {
      GemLogger.log(Level.SEVERE, ex.getMessage());
    }
    return a;
  }

  public Vector<Action> find(String where) {
    Vector<Action> va = new Vector<Action>();
    String query = "SELECT DISTINCT " + TABLE + ".* FROM " + TABLE;
    query += where;
    try (ResultSet rs = dc.executeQuery(query)) {
      while (rs.next()) {
        Action a = getFromRS(rs);
        va.addElement(a);
      }
    } catch (SQLException ex) {
      GemLogger.log(Level.SEVERE, ex.getMessage());
    }

    return va;
  }
  
  public List<CourseSchedule> getAvailableSchedules(CourseModuleInfo cmi, DateRange dates, int action, int estab) throws SQLException {
    String query = "SELECT DISTINCT on (dow,p.jour,p.debut,a.id)"
            + " p.id,p.jour,extract('dow' from p.jour) AS dow,p.debut,p.fin,p.idper,a.id,a.statut,c.titre,per.nom,per.prenom"
            + " FROM planning p JOIN action a ON (p.action = a.id)"
            + " JOIN cours c ON (a.cours = c.id)"
            + " JOIN salle s ON (p.lieux = s.id)"
            + " JOIN personne per ON (p.idper = per.id)"
            + " WHERE p.ptype in(1,6)"
            + " AND p.jour BETWEEN '" + dates.getStart() + "' AND '" + dates.getEnd() + "'"
            + " AND c.code = " + cmi.getIdCode()
            + " AND (p.fin-p.debut) = '" + Hour.getStringFromMinutes(cmi.getTimeLength())
            + "' AND a.id != " + action
            + " AND s.nom NOT LIKE 'RATTRAP%'";
    if (estab > 0) {
      query += " AND s.etablissemnt = " + estab;
    }
    query += " ORDER BY dow,p.jour,p.debut,a.id";
    //System.out.println(query);
    List<CourseSchedule> schedules = new ArrayList<>();
    ResultSet rs = dc.executeQuery(query);
    while (rs.next()) {
      CourseSchedule s = new CourseSchedule();
      s.setId(rs.getInt(1));
      s.setDate(new DateFr(rs.getString(2)));
      s.setStart(new Hour(rs.getString(4)));
      s.setEnd(new Hour(rs.getString(5)));
      s.setIdPerson(rs.getInt(6));
      Action a = new Action(rs.getInt(7));
      GemParam st = (GemParam) DataCache.findId(rs.getInt(8), Model.Status);
      a.setStatus(st);
      s.setAction(a);
      s.setActivity(new Course(rs.getString(9)));
      s.setPerson(new Person(rs.getInt(6), rs.getString(10), rs.getString(11), ""));
      schedules.add(s);
    }
    return schedules;
  }

  private Action getFromRS(ResultSet rs) throws SQLException {
    Action a = new Action();
    a.setId(rs.getInt(1));
    a.setCourse(rs.getInt(2));
    a.setLevel(getLevel(rs.getShort(3)));
    a.setPlaces(rs.getShort(4));
    a.setAgeRange(getAgeRange(rs.getShort(5)));
    a.setStatus(getStatus(rs.getShort(6)));

    return a;
  }

  private GemParam getLevel(int id) throws SQLException {
    GemParam n = (GemParam) DataCache.findId(id, Model.Level);
    return n == null ? new GemParam(0) : n;
  }

  private GemParam getStatus(int id) throws SQLException {
    GemParam n = (GemParam) DataCache.findId(id, Model.Status);
    return n == null ? new GemParam(0) : n;
  }

  public int haveStatus(int status) throws SQLException {
    Vector<Action> va = find(" WHERE statut = " + status);
    return va.size();
  }

  public int haveLevel(int level) throws SQLException {
    Vector<Action> va = find(" WHERE niveau = " + level);
    return va.size();
  }

  private AgeRange getAgeRange(int id) throws SQLException {
    AgeRange ar = (AgeRange) DataCache.findId(id, Model.AgeRange);
    return ar == null ? new AgeRange(0, GemParam.NONE) : ar;
  }

  /**
   * Gets all actions scheduled between the last 9 months and the next 9 months.
   * @return a list of actions
   * @throws SQLException
   */
  @Override
  public List<Action> load() throws SQLException {

    String where = ", " + ScheduleIO.TABLE + " p WHERE p.action = " + TABLE + ".id"
//      + " AND p.jour BETWEEN date_trunc('month', current_date)::date -6 AND date_trunc('month', current_date)::date + 6";
      + " AND p.jour BETWEEN date_trunc('month', current_date)::date - interval '9 months'"
      + " AND date_trunc('month', current_date)::date + interval '9 months'";
    return find(where);
  }

  /**
   * Gets all actions scheduled between {@code start} and {@code end}.
   * @param start start date
   * @param end end date
   * @return a list of actions or an empty list if no action was found
   * @throws SQLException
   */
  public List<Action> load(Date start, Date end) throws SQLException {
    String where = ", " + ScheduleIO.TABLE + " p WHERE p.action = " + TABLE + ".id"
      + " AND p.jour BETWEEN '" + start + "' AND '" + end + "'";
    return find(where);
  }

  /**
   * Loads the colors of the actions scheduled between two dates.
   *
   * If an action has no custom color (null), it is assigned the color 0.
   * @param start start date
   * @param end end date
   * @return a map whose the key = action id and value = color
   * @throws SQLException
   */
  public Map<Integer, Integer> loadColors(Date start, Date end) throws SQLException {
    String query = "SELECT DISTINCT a.id, CASE WHEN c.color IS NULL THEN 0 ELSE c.color END"
            + " FROM " + TABLE + " a JOIN " + ScheduleIO.TABLE + " p ON (a.id = p.action)"
            + " LEFT JOIN " + ACTION_COLOR_TABLE + " c ON (p.action = c.idaction)"
            + " WHERE p.jour BETWEEN ? AND ?"
            + " AND p.ptype IN (" + Schedule.COURSE + "," + Schedule.WORKSHOP + "," + Schedule.TRAINING + ")";
    ResultSet rs = null;
    Map<Integer, Integer> colors = new HashMap<Integer, Integer>();
    try (PreparedStatement ps = dc.prepareStatement(query)) {
      ps.setDate(1, new java.sql.Date(start.getTime()));
      ps.setDate(2, new java.sql.Date(end.getTime()));

      rs = ps.executeQuery();
      while (rs.next()) {
        colors.put(rs.getInt(1), rs.getInt(2));
      }
    } catch (SQLException e) {
      GemLogger.log(e.getMessage());
    } finally {
      closeRS(rs);
    }
    return colors;
  }
  
  /**
   * Load all memos saved, whatever the date.
   * @return a map of notes
   */
  public HashMap<Integer, Note> loadMemos() {
    HashMap<Integer, Note> memos = new HashMap<Integer, Note>();
    String query = "SELECT * FROM " + NoteIO.TABLE + " WHERE ptype = " + Person.ACTION;
    try (ResultSet rs = dc.executeQuery(query)) {
      while (rs.next()) {
        Note n = new Note(rs.getInt(1), rs.getInt(2), rs.getString(3), rs.getShort(4));
        memos.put(rs.getInt(2), n);
      }
    } catch (SQLException ex) {
      GemLogger.logException(ex);
    }
    return memos;
  }

  /**
   * Retrieves the possible custom color of this action {@code id}.
   * @param id action id
   * @return a color or null if no color was defined
   */
  public int getColor(int id) {
    ResultSet rs = null;
    try {
      if (colorStatement == null || colorStatement.isClosed()) {
        colorStatement = dc.prepareStatement(ACTION_COLOR_QUERY);
      }
    } catch (SQLException ex) {
      GemLogger.log(ex.getMessage());
      return 0;
    }
    try {
      colorStatement.setInt(1, id);
      rs = colorStatement.executeQuery();
      if (rs.next()) {
        return rs.getInt(1);
      }
    } catch (SQLException ex) {
      GemLogger.log(ex.getMessage());
    } finally {
      closeRS(rs);
    }
    return 0;
  }
}

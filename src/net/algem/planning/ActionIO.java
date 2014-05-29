/*
 * @(#)ActionIO.java 2.8.t 16/04/14
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
import java.util.List;
import java.util.Vector;
import net.algem.config.AgeRange;
import net.algem.config.GemParam;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;
import net.algem.util.GemLogger;
import net.algem.util.model.Cacheable;
import net.algem.util.model.Model;
import net.algem.util.model.TableIO;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.t
 * @since 2.4.a 18/04/12
 */
public class ActionIO
        extends TableIO
        implements Cacheable
{

  public static final String TABLE = "action";
  public static final String COLUMNS = "id, cours, niveau, places, tage, statut";
  public static final String SEQUENCE = "action_id_seq";
//  private static Hashtable<Integer, Action> cache = new  Hashtable<Integer, Action>();
  private DataConnection dc;

  public ActionIO(DataConnection dc) {
    this.dc = dc;
  }

  public void planify(Action a, int type) throws PlanningException {
    try {
      dc.setAutoCommit(false);
      insert(a);
      for (DateFr d : a.getDates()) {
        String query = "INSERT INTO planning VALUES (DEFAULT"
                + ",'" + d.toString()
                + "','" + a.getHourStart() + "','" + a.getHourEnd() + "',"
                + type + ","
                + a.getTeacher() + ","
                + a.getId() + ","
                + a.getRoom() + ",0)";
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
                + a.getTeacher() + ","
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
//    cache.put(nextid, a);
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
  }

  public void delete(int id) throws SQLException {
    String query = "DELETE FROM " + TABLE + " WHERE id = " + id;
    dc.executeUpdate(query);
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
    String query = "SELECT action.id FROM " + TABLE + " " + where;
    ResultSet rs = dc.executeQuery(query);
    if (rs.next()) {
      action = rs.getInt(1);
    }
    return action;
  }

  public Action findId(int id) throws SQLException {
    Action a = null;
    String query = "SELECT " + COLUMNS + " FROM " + TABLE + " WHERE id = " + id;
    ResultSet rs = dc.executeQuery(query);
    if (rs.next()) {
      a = new Action();
      a.setId(id);
      a.setCourse(rs.getInt(2));
      a.setLevel(getLevel(rs.getShort(3)));
      a.setPlaces(rs.getShort(4));
      a.setAgeRange(getAgeRange(rs.getShort(5)));
      a.setStatus(getStatus(rs.getShort(6)));
    }
    return a;
  }

  public Vector<Action> find(String where) throws SQLException {
    Vector<Action> va = new Vector<Action>();
    String query = "SELECT " + TABLE + ".* FROM " + TABLE;
    query += where;
    ResultSet rs = dc.executeQuery(query);
    while (rs.next()) {
      Action a = new Action();
      a.setId(rs.getInt(1));
      a.setCourse(rs.getInt(2));
      a.setLevel(getLevel(rs.getShort(3)));
      a.setPlaces(rs.getShort(4));
      a.setAgeRange(getAgeRange(rs.getShort(5)));
      a.setStatus(getStatus(rs.getShort(6)));

      va.addElement(a);
    }
    return va;
  }

  private GemParam getLevel(int id) throws SQLException {
//    GemParam n = levelIO.findId(id);
    GemParam n = (GemParam) DataCache.findId(id, Model.Level);
    return n == null ? new GemParam(0) : n;
  }

  private GemParam getStatus(int id) throws SQLException {
//    GemParam n = statusIO.findId(id);
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
//    AgeRange t = AgeRangeIO.findId(id, dc);
    AgeRange ar = (AgeRange) DataCache.findId(id, Model.AgeRange);
    return ar == null ? new AgeRange(0, GemParam.NONE) : ar;
  }

  /**
   * Gets all actions scheduled on the current week.
   * @return a list of actions
   * @throws SQLException
   */
  @Override
  public List<Action> load() throws SQLException {
    String where = ", " + ScheduleIO.TABLE + " p WHERE p.action = " + TABLE
            + ".id AND p.jour BETWEEN date_trunc('week', current_date)::date AND date_trunc('week', current_date)::date + 6";
    return find(where);
  }
}

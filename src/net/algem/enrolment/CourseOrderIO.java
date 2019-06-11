/*
 * @(#)CourseOrderIO.java	2.16.0 05/03/19
 *
 * Copyright (c) 1999-2019 Musiques Tangentes. All Rights Reserved.
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
package net.algem.enrolment;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import net.algem.config.Instrument;
import net.algem.config.InstrumentIO;
import net.algem.contact.PersonIO;
import net.algem.contact.member.MemberIO;
import net.algem.group.Musician;
import net.algem.planning.ActionIO;
import net.algem.planning.DateFr;
import net.algem.planning.Hour;
import net.algem.planning.ScheduleIO;
import net.algem.planning.ScheduleRangeIO;
import net.algem.util.DataConnection;
import net.algem.util.GemLogger;
import net.algem.util.model.TableIO;

/**
 * IO methods for class {@link net.algem.enrolment.CourseOrder}.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.16.0
 * @since 1.0a 07/07/1999
 */
public class CourseOrderIO
  extends TableIO {

  public static final String TABLE = "commande_cours";
  public static final String COLUMNS = "cc.id,cc.idcmd,cc.module,cc.idaction,cc.debut,cc.fin,cc.datedebut,cc.datefin, cc.code";
  public static final String SEQUENCE = "commande_cours_id_seq";

  public static void insert(CourseOrder co, DataConnection dc) throws SQLException {

    int nextval = nextId(SEQUENCE, dc);

    String query = "INSERT INTO " + TABLE + " VALUES(?,?,?,?,?,?,?,?,?)";

    try (PreparedStatement ps = dc.prepareStatement(query)) {
      ps.setInt(1, nextval);
      ps.setInt(2, co.getIdOrder());
      ps.setInt(3, co.getModuleOrder());
      ps.setInt(4, co.getAction());
      ps.setTime(5, java.sql.Time.valueOf(co.getStart().toString() + ":00"));
      ps.setTime(6, java.sql.Time.valueOf(co.getEnd().toString() + ":00"));
      ps.setDate(7, new java.sql.Date(co.getDateStart().getTime()));
      ps.setDate(8, new java.sql.Date(co.getDateEnd().getTime()));
      ps.setInt(9, co.getCode());

      GemLogger.info(ps.toString());
      ps.executeUpdate();
    }

  }

  public static void update(CourseOrder co, DataConnection dc) throws SQLException {
    String query = "UPDATE " + TABLE
      + " SET idcmd=?,module=?,idaction=?,debut=?,fin=?,datedebut=?,datefin=? WHERE id=?";

    try (PreparedStatement ps = dc.prepareStatement(query)) {
      ps.setInt(1, co.getIdOrder());
      ps.setInt(2, co.getModuleOrder());
      ps.setInt(3, co.getAction());
      ps.setTime(4, java.sql.Time.valueOf(co.getStart().toString() + ":00"));
      ps.setTime(5, java.sql.Time.valueOf(co.getEnd().toString() + ":00"));
      ps.setDate(6, new java.sql.Date(co.getDateStart().getTime()));
      ps.setDate(7, new java.sql.Date(co.getDateEnd().getTime()));
      ps.setInt(8, co.getId());

      GemLogger.info(ps.toString());
      ps.executeUpdate();
    }
  }

  /**
   * Deletes the course order with id {@code id}.
   *
   * @param id course order's id
   * @param dc dataConnection
   * @throws SQLException
   */
  public static void deleteById(int id, DataConnection dc) throws SQLException {
    String query = "DELETE FROM " + TABLE + " WHERE id = " + id;
    dc.executeUpdate(query);
  }

  /**
   * Deletes course orders corresponding to the order with id {@code cmd}.
   *
   * @param orderId order's id
   * @param dc dataConnection
   * @throws SQLException
   */
  public static void delete(int orderId, DataConnection dc) throws SQLException {
    String query = "DELETE FROM " + TABLE + " WHERE idcmd = " + orderId;
    dc.executeUpdate(query);
  }

  /**
   * Deletes course orders corresponding to the module order with id {@code id}.
   *
   * @param moduleId module order's id
   * @param dc dataConnection
   * @throws SQLException
   */
  public static void deleteByIdModule(int moduleId, DataConnection dc) throws SQLException {
    String query = "DELETE FROM " + TABLE + " WHERE module = " + moduleId;
    dc.executeUpdate(query);
  }

  /**
   * Retrieves a list of course's orders from order {@literal orderId}.
   *
   * @param orderId order's id
   * @param dc dataConnection
   * @return a list of course orders
   * @throws java.sql.SQLException
   */
  public static Vector<CourseOrder> findId(int orderId, DataConnection dc) throws SQLException {
    String query = "WHERE cc.idcmd = " + orderId;
    return find(query, dc);
  }

  public static Vector<CourseOrder> find(String where, DataConnection dc) throws SQLException {
    String query = "SELECT " + COLUMNS + ", c.titre FROM "
      + TABLE + " cc JOIN " + ActionIO.TABLE + " a ON cc.idaction = a.id"
      + " LEFT JOIN cours c ON a.cours = c.id";

    return fillCourseOrder(query + " " + where, dc);
  }

  /**
   *
   * @param where
   * @param member
   * @param dc
   * @return a vector of course order
   * @throws SQLException
   * @deprecated
   */
  public static Vector<CourseOrder> find(String where, int member, DataConnection dc) throws SQLException {
    String query = "SELECT " + COLUMNS + ", cours.titre FROM " + TABLE + " cc, commande c, "
      + " action LEFT JOIN cours ON action.cours = cours.id"
      + " WHERE cc.idcmd = c.id"
      + " AND c.adh = " + member
      + " AND cc.idaction = action.id";

    return fillCourseOrder(query + where, dc);
  }

  private static Vector<CourseOrder> fillCourseOrder(String query, DataConnection dc) throws SQLException {
    Vector<CourseOrder> courseOrders = new Vector<CourseOrder>();
    try (ResultSet rs = dc.executeQuery(query)) {
      while (rs.next()) {
        CourseOrder c = new CourseOrder();
        c.setId(rs.getInt(1));
        c.setIdOrder(rs.getInt(2));
        c.setModuleOrder(rs.getInt(3));
        c.setAction(rs.getInt(4));
        c.setStart(new Hour(rs.getString(5)));
        c.setEnd(new Hour(rs.getString(6)));
        c.setDateStart(new DateFr(rs.getString(7)));
        c.setDateEnd(new DateFr(rs.getString(8)));
        c.setCode(rs.getInt(9));
        c.setTitle(rs.getString(10));

        courseOrders.addElement(c);
      }
    }
    return courseOrders;
  }

  public static List<Musician> findCourseMembers(int courseId, Date start, Date end, DataConnection dc) throws SQLException {

    List<Musician> members = new ArrayList<Musician>();
    String query = "SELECT DISTINCT p.id, p.nom, p.prenom, pi.instrument, e.datenais FROM "
      + TABLE + " cc JOIN " + ActionIO.TABLE + " a ON cc.idaction = a.id"
      + " JOIN " + OrderIO.TABLE + " c ON cc.idcmd = c.id"
      + " JOIN " + PersonIO.TABLE + " p ON c.adh = p.id"
      + " LEFT JOIN " + InstrumentIO.PERSON_INSTRUMENT_TABLE + " pi ON (p.id = pi.idper AND pi.ptype = " + Instrument.MEMBER + " AND pi.idx = 0)"
      + " JOIN " + MemberIO.TABLE + " e ON p.id = e.idper"
      + " WHERE a.cours = ? AND cc.datedebut BETWEEN ? AND ?";

    try (PreparedStatement ps = dc.prepareStatement(query)) {
      ps.setInt(1, courseId);
      ps.setDate(2, new java.sql.Date(start.getTime()));
      ps.setDate(3, new java.sql.Date(end.getTime()));

      GemLogger.info(ps.toString());

      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          Musician m = new Musician();
          m.setId(rs.getInt(1));
          m.setName(rs.getString(2).trim());
          m.setFirstName(rs.getString(3).trim());
          m.setInstrument(rs.getInt(4));
          m.setAge(rs.getDate(5));

          members.add(m);
        }
      }
    }

    return members;
  }

  /**
   * Gets the date of the last session this student attended.
   *
   * @param idper member's id
   * @param id course order id
   * @param dc data connection
   * @return the date of the last scheduled session or null if none
   * @throws SQLException
   */
  static Date getLastSchedule(int idper, int id, DataConnection dc) throws SQLException {
    String query = "SELECT jour FROM " + ScheduleIO.TABLE + " p"
      + " JOIN " + CourseOrderIO.TABLE + " cc ON (p.action = cc.idaction)"
      + " JOIN " + OrderIO.TABLE + " c ON (cc.idcmd = c.id)"
      + " JOIN " + ScheduleRangeIO.TABLE + " pl ON (p.id = pl.idplanning AND c.adh = pl.adherent)"
      + " WHERE cc.id = " + id
      + " AND c.adh = " + idper
      + " ORDER BY jour DESC LIMIT 1";
    try (ResultSet rs = dc.executeQuery(query)) {
      while (rs.next()) {
        return rs.getDate(1);
      }
    }
    return null;
  }

}

/*
 * @(#)CourseOrderIO.java	2.10.0 14/06/2016
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
package net.algem.enrolment;

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
import net.algem.util.model.TableIO;

/**
 * IO methods for class {@link net.algem.enrolment.CourseOrder}.
 * 
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.10.0
 * @since 1.0a 07/07/1999
 */
public class CourseOrderIO
        extends TableIO
{

  public static final String TABLE = "commande_cours";
  public static final String COLUMNS = "cc.id,cc.idcmd,cc.module,cc.idaction,cc.debut,cc.fin,cc.datedebut,cc.datefin, cc.code";
  public static final String SEQUENCE = "commande_cours_id_seq";

  public static void insert(CourseOrder co, DataConnection dc) throws SQLException {

    int nextval = nextId(SEQUENCE, dc);
    String query = "INSERT INTO " + TABLE + " VALUES("
            + nextval
            + "," + co.getIdOrder()
            + "," + co.getModuleOrder()
            + "," + co.getAction()
            + ",'" + co.getStart()
            + "','" + co.getEnd()
            + "','" + co.getDateStart()
            + "','" + co.getDateEnd()
            + "'," + co.getCode()
            + ")";
    dc.executeUpdate(query);
  }

  public static void update(CourseOrder c, DataConnection dc) throws SQLException {
    String query = "UPDATE " + TABLE + " SET "
            + "idcmd = '" + c.getIdOrder()
            + "',module = '" + c.getModuleOrder()
            + "',idaction = '" + c.getAction()
            + "',debut = '" + c.getStart()
            + "',fin = '" + c.getEnd()
            + "',datedebut = '" + c.getDateStart()
            + "',datefin = '" + c.getDateEnd()
            + "'"
            + " WHERE id = " + c.getId();
    dc.executeUpdate(query);
  }

  /**
   * Deletes the course order with id {@code id}.
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
   * @param orderId order's id
   * @param dc dataConnection
   * @return a list of course orders
   * @throws java.sql.SQLException
   */
  public static Vector<CourseOrder> findId(int orderId, DataConnection dc) throws SQLException {
    String query = " AND cc.idcmd = " + orderId;
    return find(query, dc);
  }

  public static Vector<CourseOrder> find(String where, DataConnection dc) throws SQLException {
    String query = "SELECT " + COLUMNS + ", cours.titre FROM " + TABLE + " cc,"
            + " action LEFT JOIN cours ON action.cours = cours.id"
            + " WHERE cc.idaction = action.id";
      return fillCourseOrder(query + where, dc);
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
    Vector<CourseOrder> v = new Vector<CourseOrder>();
    ResultSet rs = dc.executeQuery(query);
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
        
        v.addElement(c);
      }
      rs.close();
      return v;
  }
  
  public static List<Musician> findCourseMembers(int course, Date start, Date end, DataConnection dc) throws SQLException {

    List<Musician> vm = new ArrayList<Musician>();

    String query = "SELECT DISTINCT p.id, p.nom, p.prenom, pi.instrument FROM "
            + PersonIO.TABLE + " p LEFT JOIN " + InstrumentIO.PERSON_INSTRUMENT_TABLE + " pi ON (p.id = pi.idper AND pi.ptype = " + Instrument.MEMBER + " AND pi.idx = 0), "
            + MemberIO.TABLE + " e, "
            + OrderIO.TABLE + " c, "
            + TABLE + " cc, "
            + ActionIO.TABLE + " a"
            + " WHERE a.cours = " + course + " AND a.id = cc.idaction"
            + " AND cc.datedebut BETWEEN '" + start + "' AND '" + end + "'"
            + " AND cc.idcmd = c.id AND c.adh = p.id"
            + " AND p.id = e.idper"
            + " ORDER BY p.nom,p.prenom";

    ResultSet rs = dc.executeQuery(query);
    for (int i = 0; rs.next(); i++) {
      Musician a = new Musician();
      a.setId(rs.getInt(1));
      a.setName(rs.getString(2).trim());
      a.setFirstName(rs.getString(3).trim());
      a.setInstrument(rs.getInt(4));
      vm.add(a);
    }

    return vm;
  }

  static Date getLastSchedule(int idper, int id, DataConnection dc) throws SQLException {
    String query = "SELECT jour FROM " + ScheduleIO.TABLE + " p"
            + " join " + CourseOrderIO.TABLE + " cc on (p.action = cc.idaction)"
            + " join " + OrderIO.TABLE + " c on (cc.idcmd = c.id)"
            + " join " + ScheduleRangeIO.TABLE + " pl on (p.id = pl.idplanning and c.adh = pl.adherent)"
            + " where cc.id = " + id
            + " and c.adh = " + idper
            + " order by jour desc limit 1";
    ResultSet rs = dc.executeQuery(query);
    while (rs.next()) {
      return rs.getDate(1);
    }
    return null;
  }

}

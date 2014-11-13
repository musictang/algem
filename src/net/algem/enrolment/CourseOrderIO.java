/*
 * @(#)CourseOrderIO.java	2.9.1 13/11/14
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
package net.algem.enrolment;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import net.algem.planning.DateFr;
import net.algem.planning.Hour;
import net.algem.util.DataConnection;
import net.algem.util.model.TableIO;

/**
 * IO methods for class {@link net.algem.enrolment.CourseOrder}.
 * 
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.1
 * @since 1.0a 07/07/1999
 */
public class CourseOrderIO
        extends TableIO
{

  public static final String TABLE = "commande_cours";
  public static final String COLUMNS = "cc.id,cc.idcmd,cc.module,cc.idaction,cc.debut,cc.fin,cc.datedebut,cc.datefin, cc.code";
  public static final String SEQUENCE = "commande_cours_id_seq";

  public static void insert(CourseOrder c, DataConnection dc) throws SQLException {

    int nextval = nextId(SEQUENCE, dc);
    String query = "INSERT INTO " + TABLE + " VALUES("
            + nextval
            + "," + c.getIdOrder()
            + "," + c.getModuleOrder()
            + "," + c.getAction()
            + ",'" + c.getStart()
            + "','" + c.getEnd()
            + "','" + c.getDateStart()
            + "','" + c.getDateEnd()
            + "'," + c.getCode()
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
   * @return
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

}

/*
 * @(#)CourseOrderIO.java	2.6.a 17/09/12
 * 
 * Copyright (c) 1999-2012 Musiques Tangentes. All Rights Reserved.
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
 * @version 2.6.a
 * @since 1.0a 07/07/1999
 */
public class CourseOrderIO
        extends TableIO
{

  public static final String TABLE = "commande_cours";
  public static final String COLUMNS = "cc.id,cc.idcmd,cc.module,cc.idaction,cc.debut,cc.fin,cc.datedebut,cc.datefin";
  public static final String SEQUENCE = "commande_cours_id_seq";

  public static void insert(CourseOrder c, DataConnection dc) throws SQLException {

    int nextval = nextId(SEQUENCE, dc);
    String query = "INSERT INTO " + TABLE + " VALUES("
            + nextval
            + "," + c.getIdOrder()
            + "," + c.getModule()
            + "," + c.getAction()
            + ",'" + c.getStart()
            + "','" + c.getEnd()
            + "','" + c.getDateStart()
            + "','" + c.getDateEnd()
            + "')";
    dc.executeUpdate(query);
  }

  public static void update(CourseOrder c, DataConnection dc) throws SQLException {
    String query = "UPDATE " + TABLE + " SET "
            + "idcmd = '" + c.getIdOrder()
            + "',module = '" + c.getModule()
            + "',idaction = '" + c.getAction()
            + "',debut = '" + c.getStart()
            + "',fin = '" + c.getEnd()
            + "',datedebut = '" + c.getDateStart()
            + "',datefin = '" + c.getDateEnd()
            + "'"
            + " WHERE id = " + c.getId();
    dc.executeUpdate(query);
  }

  public static void deleteOID(CourseOrder c, DataConnection dc) throws SQLException {
    String query = "DELETE FROM " + TABLE + " WHERE id = " + c.getId();

    dc.executeUpdate(query);
  }

  public static void delete(int cmd, DataConnection dc) throws SQLException {
    String query = "DELETE FROM " + TABLE + " WHERE idcmd = " + cmd;
    dc.executeUpdate(query);
  }

  /**
   *
   * @param n order id
   * @param dc DataCache
   * @return a list of course order
   */
  public static Vector<CourseOrder> findId(int n, DataConnection dc) throws SQLException {
    String query = " AND cc.idcmd = " + n;
    return find(query, dc);
  }

  public static Vector<CourseOrder> find(String where, DataConnection dc) throws SQLException {
    
    //String query = "SELECT cc.oid,cc.idcmd,cc.module,cc.cours,cc.debut,cc.end,c.titre,c.code,c.jour,cc.datedebut,cc.datefin from commande_cours cc, cours c WHERE c.id = cc.cours "+where;
    // retrait du cours.jour dans la requête (c.jour)
    //id,idcmd,module,idaction,debut,end,datedebut,datefin
    //String query = "SELECT cc.oid,cc.idcmd,cc.module,cc.cours,cc.debut,cc.end,c.titre,c.code,cc.datedebut,cc.datefin from commande_cours cc, cours c WHERE c.id = cc.cours " + where;
    String query = "SELECT " + COLUMNS + ", cours.titre, cours.code  FROM " + TABLE + " cc, action, cours"
            + " WHERE cc.idaction = action.id AND action.cours = cours.id " + where;
      return fillCommandeCours(query + where, dc);
  }

  public static Vector<CourseOrder> find(String where, int member, DataConnection dc) throws SQLException {
    String query = "SELECT " + COLUMNS + ", cours.titre, cours.code  FROM " + TABLE + " cc, commande c, action, cours"
            + " WHERE cc.idcmd = c.id AND c.adh = "+member
            + " AND cc.idaction = action.id AND action.cours = cours.id ";
      return fillCommandeCours(query + where, dc);
  }

  private static Vector<CourseOrder> fillCommandeCours(String query, DataConnection dc) throws SQLException {
    Vector<CourseOrder> v = new Vector<CourseOrder>();
    ResultSet rs = dc.executeQuery(query);
      while (rs.next()) {
        CourseOrder c = new CourseOrder();
        c.setId(rs.getInt(1));
        c.setIdOrder(rs.getInt(2));
        c.setModule(rs.getInt(3));
        c.setAction(rs.getInt(4));
        c.setStart(new Hour(rs.getString(5)));
        c.setEnd(new Hour(rs.getString(6)));
        c.setDateStart(new DateFr(rs.getString(7)));
        c.setDateEnd(new DateFr(rs.getString(8)));
        c.setTitle(rs.getString(9));
        c.setCode(rs.getInt(10));
        //c.setDay(rs.getInt(9));

        v.addElement(c);
      }
      rs.close();
      return v;
  }

}

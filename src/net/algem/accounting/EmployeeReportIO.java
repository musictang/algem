/*
 * @(#)EmployeeReportIO.java 2.9.4.13 27/10/2015
 * 
 * Copyright (c) 1999-2015 Musiques Tangentes. All Rights Reserved.
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
 * along with Algem. If not, see http://www.gnu.org/licenses.
 * 
 */

package net.algem.accounting;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import net.algem.contact.PersonIO;
import net.algem.course.CourseIO;
import net.algem.course.Module;
import net.algem.course.ModuleIO;
import net.algem.enrolment.CourseOrderIO;
import net.algem.enrolment.ModuleOrderIO;
import net.algem.planning.ActionIO;
import net.algem.planning.Schedule;
import net.algem.planning.ScheduleIO;
import net.algem.planning.ScheduleRangeIO;
import net.algem.room.RoomIO;
import net.algem.util.DataConnection;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.13
 * @since 2.9.4.13 27/10/15
 */
public class EmployeeReportIO 
{
private final DataConnection dc;

  public EmployeeReportIO(DataConnection dc) {
    this.dc = dc;
  }

  public ResultSet getDetailByTechnician(String start, String end, int idper, int type) throws SQLException {
    String query = "SELECT p.jour, p.idper, p.debut, p.fin, (p.fin - p.debut) AS duree, pg.adherent"
            + " FROM " + ScheduleIO.TABLE + " p, " + ScheduleRangeIO.TABLE + " pg"
            + " WHERE pg.idplanning = p.id";
            query += (idper > 0) ? " AND pg.adherent = " + idper : " AND pg.adherent > 0";
            query += " AND p.jour BETWEEN '" + start + "' AND '" + end + "'"
            + " AND p.ptype = " + type
            + " ORDER BY pg.adherent,p.jour,p.debut";
    return dc.executeQuery(query);
  }
  
  public ResultSet getDetailByAdministrator(String start, String end, int idper, int type) throws SQLException {
    String query = "SELECT p.jour, p.idper, p.debut, p.fin, (p.fin - p.debut) AS duree, s.nom"
            + " FROM " + ScheduleIO.TABLE + " p, " + RoomIO.TABLE + " s";
            query += (idper > 0) ? " WHERE p.idper = " + idper : " WHERE p.idper > 0";
            query += " AND p.jour BETWEEN '" + start + "' AND '" + end + "'"
            + " AND p.ptype = " + type
            + " AND p.lieux = s.id"
            + " ORDER BY p.idper,p.jour,p.debut";
    return dc.executeQuery(query);
  }

  public ResultSet getDetailIndTeacherByMember(String start, String end, boolean catchup, int idper, int school, int estab) throws SQLException {
    String query = "SELECT p.idper, pg.adherent, p1.prenom, p1.nom, c.id, c.titre, p2.prenom, p2.nom, p.jour, pg.debut, pg.fin,(pg.fin - pg.debut) AS duree"
            + " FROM " + ScheduleIO.TABLE + " p, " 
            + ScheduleRangeIO.TABLE + " pg, " 
            + ActionIO.TABLE + " a, " 
            + CourseIO.TABLE + " c, " 
            + PersonIO.TABLE + " p1, " 
            + PersonIO.TABLE + " p2, "
            + RoomIO.TABLE + " s";
            query += (idper > 0) ? " WHERE p.idper = " + idper : " WHERE p.idper > 0";
            query += " AND pg.idplanning = p.id"
            + " AND p.jour BETWEEN '" + start + "' AND '" + end + "'"
            + " AND p.lieux = s.id"
            + " AND p.ptype IN (1,5,6)"
            + " AND p.idper = p1.id"
            + " AND pg.adherent = p2.id"
            + " AND pg.adherent > 0"
            + " AND p.action = a.id"
            + " AND a.cours = c.id "
            + " AND c.ecole = " + school
            + " AND (c.collectif = FALSE OR (c.code = 1 AND (SELECT count(id) FROM " + ScheduleRangeIO.TABLE + " WHERE idplanning = p.id) = 1))";
            if (estab > 0) {
              query += " AND s.etablissement = " + estab;
            }
            if(!catchup) {
              query += " AND s.nom !~* 'rattrap'";
            }
            query += " ORDER BY p1.nom, a.cours, pg.adherent, p.jour, p.debut";
    return dc.executeQuery(query);
  }

  public ResultSet getDetailCoTeacherByMember(String start, String end, boolean catchup, int idper, int school, int estab) throws SQLException {
    String query = "SELECT p.idper, p1.prenom, p1.nom, c.id, c.titre, p.jour, p.debut, p.fin,(p.fin - p.debut) AS duree"
            + " FROM " + ScheduleIO.TABLE + " p, " 
            + ActionIO.TABLE + " a, " 
            + CourseIO.TABLE  + " c, " 
            + PersonIO.TABLE + " p1, " 
            + RoomIO.TABLE + " s";
            query += (idper > 0) ? " WHERE p.idper = " + idper : " WHERE p.idper > 0";
            query += " AND p.jour BETWEEN '" + start + "' AND '" + end + "'"
            + " AND p.lieux = s.id"
            + " AND p.ptype IN (1,5,6)"
            + " AND p.idper = p1.id"
            + " AND p.action = a.id"
            + " AND a.cours = c.id "
            + " AND c.ecole = " + school
            + " AND (((c.code IN(2,3,11,12) OR c.code > 12) AND (SELECT count(id) FROM " + ScheduleRangeIO.TABLE + " WHERE idplanning = p.id) > 0)"
            + " OR (c.code = 1 AND (SELECT count(id) FROM " + ScheduleRangeIO.TABLE + " WHERE idplanning = p.id AND debut = p.debut) > 1))";
            if (estab > 0) {
              query += " AND s.etablissement = " + estab;
            }
            if(!catchup) {
              query +=  " AND s.nom !~* 'rattrap'";
            }
            query += " ORDER BY p1.nom, a.cours, p.jour, p.debut";
    return dc.executeQuery(query);
  }
  
/**
 * 
 * @param start start date
 * @param end end date
 * @param catchup include catchup
 * @param idper teacher id
 * @param school school id
 * @param estab establishment id
 * @return a resultset
 * @throws SQLException 
 */
  public ResultSet getDetailTeacherByDate(String start, String end, boolean catchup, int idper, int school, int estab) throws SQLException {
    String query = "SELECT DISTINCT ON (p1.nom, p1.prenom, p.jour, pg.debut)"
      + " p.idper, pg.adherent, p1.prenom, p1.nom, c.id, c.titre, p2.prenom, p2.nom, p.jour, pg.debut, pg.fin, (pg.fin - pg.debut) AS duree, a.id"
      + " FROM " + ScheduleIO.TABLE + " p, " 
            + ScheduleRangeIO.TABLE + " pg, "
            + ActionIO.TABLE + " a, " 
            + CourseIO.TABLE  + " c, " 
            + PersonIO.TABLE + " p1, " + PersonIO.TABLE + " p2, "
            + RoomIO.TABLE + " s";
    query += (idper > 0) ? " WHERE p.idper = " + idper : " WHERE p.idper > 0";
    query += " AND p.jour BETWEEN '" + start + "' AND '" + end + "'"
      + " AND p.ptype IN (1,5,6)" // cours, atelier ponctuel, stage
      + " AND p.lieux = s.id"
      + " AND p.action = a.id"
      + " AND a.cours = c.id"
      + " AND c.ecole = " + school
      + " AND p.idper = p1.id"
      + " AND pg.idplanning = p.id"
      + " AND pg.adherent = p2.id"
      + " AND pg.adherent > 0";
    if (estab > 0) {
      query += " AND s.etablissement = " + estab;
    }
    if (!catchup) {
      query += " AND s.nom !~* 'rattrap'";
    }
    query += " ORDER BY p1.nom, p1.prenom,p.jour,pg.debut,a.cours";

    return dc.executeQuery(query);
  }
  
  /**
   * 
   * @param start start date
   * @param end end date
   * @param catchup include catchup
   * @param idper teacher id
   * @param school school id
   * @param estab establishment id
   * @param modules list of selected modules
   * @return a resultset
   * @throws SQLException 
   */
  public ResultSet getDetailTeacherByModule(String start, String end, boolean catchup, int idper, int school, int estab, List<Module> modules) throws SQLException {
    StringBuilder sb = new StringBuilder();
    for (Module m : modules) {
      sb.append(m.getId()).append(',');
    }
    sb.deleteCharAt(sb.length()-1);
    String query = "SELECT DISTINCT ON (p1.nom, p1.prenom, p.jour, pg.debut,m.id)"
      + " p.idper, p1.prenom, p1.nom, a.id, c.id, c.titre, m.id, m.titre, p.jour, pg.debut, pg.fin, (pg.fin - pg.debut) AS duree"
      + " FROM " + ScheduleIO.TABLE + " p, " 
            + ScheduleRangeIO.TABLE + " pg, "
            + ActionIO.TABLE + " a, " 
            + CourseIO.TABLE  + " c, " 
            + PersonIO.TABLE + " p1, "
            + RoomIO.TABLE + " s,"
            + CourseOrderIO.TABLE + " cc,"
            + ModuleOrderIO.TABLE + " cm,"
            + ModuleIO.TABLE + " m";
    query += (idper > 0) ? " WHERE p.idper = " + idper : " WHERE p.idper > 0";
    query += " AND p.jour BETWEEN '" + start + "' AND '" + end + "'"
      + " AND p.ptype IN ("+Schedule.COURSE+","+Schedule.WORKSHOP+","+Schedule.TRAINING // cours, atelier ponctuel, stage
      + ") AND p.lieux = s.id"
      + " AND p.action = a.id"
      + " AND a.id = cc.idaction"
      + " AND cc.module = cm.id"
      + " AND cm.module IN("+sb.toString()
      + ") AND cm.module = m.id"
      + " AND a.cours = c.id"
      + " AND c.ecole = " + school
      + " AND p.idper = p1.id"
      + " AND pg.idplanning = p.id";
    if (estab > 0) {
      query += " AND s.etablissement = " + estab;
    }
    if (!catchup) {
      query += " AND s.nom !~* 'rattrap'";
    }
    query += " ORDER BY m.id,p1.nom,p1.prenom,p.jour,pg.debut,a.cours";

    return dc.executeQuery(query);
  }
}

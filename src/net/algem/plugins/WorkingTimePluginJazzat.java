/*
 * @(#) WorkingTimePlugin.java Algem 2.10.0 07/06/2016
 *
 * Copyright (c) 1999-2016 Musiques Tangentes. All Rights Reserved.
 *
 * This file is part of Algem.
 * Algem is free software: you can redistribute it AND/or modify it
 * under the terms of the GNU Affero General Public License AS published by
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
 */
package net.algem.plugins;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.algem.accounting.OrderLineIO;
import net.algem.contact.Person;
import net.algem.contact.PersonIO;
import net.algem.course.ModuleIO;
import net.algem.edition.HoursTaskExecutor;
import net.algem.enrolment.CourseOrderIO;
import net.algem.enrolment.ModuleOrderIO;
import net.algem.enrolment.OrderIO;
import net.algem.planning.DateFr;
import net.algem.planning.Hour;
import net.algem.planning.Schedule;
import net.algem.planning.ScheduleIO;
import net.algem.planning.ScheduleRangeIO;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;
import net.algem.util.GemLogger;
import net.algem.util.TextUtil;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.10.0
 * @since 2.10.0 03/06/2016
 */
public class WorkingTimePluginJazzat
  extends HoursTaskExecutor {

  private CustomDAO dao;

  private String [] categories = {
    "DEM + L1 + L2 + Loisirs",
    "Parcours BREVET + MIMA + FP",
    "Réunions"
  };

  public WorkingTimePluginJazzat() {
    dao = new CustomDAO(DataCache.getDataConnection());
  }

  @Override
  public String getLabel() {
    return "Heures Jazz à Tours";
  }

  @Override
  public String getInfo() {
    return "Tri par professeur :\nDEM + L1 + L2 + Loisirs\nParcours BREVET + MIMA + FP\nRéunions";
  }


  @Override
  public void execute() {
    try {
      Map<String, Object> props = getProperties();
      DateFr start = (DateFr) props.get("start");
      DateFr end = (DateFr) props.get("end");
      List<Person> teachers = dao.getTeachers(start, end);
      int p = 0;
      int len = teachers.size();
      StringBuilder sb = new StringBuilder();
      for (Person t : teachers) {
        sb.append(t.getFirstnameName()).append(';').append(TextUtil.LINE_SEPARATOR);
        String total1 = dao.getTotal1(t.getId(), start, end);
        String total2 = dao.getTotal2(t.getId(), start, end);
        String total3 = dao.getTotal3(t.getId(), start, end);
        int t1 = Hour.getMinutesFromString(total1);
        int t2 = Hour.getMinutesFromString(total2);
        int t3 = Hour.getMinutesFromString(total3);
        int total = t1 + t2 + t3;

        sb.append(categories[0]).append(';').append(total1).append(TextUtil.LINE_SEPARATOR);
        sb.append(categories[1]).append(';').append(total2).append(TextUtil.LINE_SEPARATOR);
        sb.append(categories[2]).append(';').append(total3).append(TextUtil.LINE_SEPARATOR);
        sb.append("TOTAL;").append(formatTotal(total)).append(TextUtil.LINE_SEPARATOR);
        out.print(sb.toString());
        worker.setStep(p++ * 100 / len);
        sb.delete(0, sb.length() -1);
      }
    } catch (SQLException ex) {
      GemLogger.logException(ex);
    } finally {
      out.close();
    }

  }

  private List<Person> getTestList() {
    List<Person> t = new ArrayList<>();
    t.add(new Person(106,"ANTOLIN","Elora","Mme"));
    return t;
  }

  private String formatTotal(int min) {
    int h = min / 60;
    int m = min % 60;
    return String.format("%02d:%02d", h,m);
  }

  private class CustomDAO {

    private DataConnection dc;
    private PreparedStatement ps1, ps2, ps3;
    private final String total1Query = "SELECT sum(q1.fin-q1.debut) FROM"
        + " (SELECT DISTINCT ON (p.jour,pl.debut)"
        + " pl.debut AS debut, pl.fin AS fin"
        + " FROM " + ScheduleRangeIO.TABLE + " pl JOIN " + ScheduleIO.TABLE + " p ON (pl.idplanning = p.id)"
        + " JOIN " + PersonIO.TABLE + " per ON (p.idper = per.id)"
        + " WHERE p.ptype IN (" + Schedule.COURSE + ","+ Schedule.WORKSHOP + "," + Schedule.TRAINING + ")"
        + " AND p.idper = ?"
        + " AND p.jour BETWEEN  ? AND ?"
        + " AND pl.adherent in(SELECT adh FROM " + OrderIO.TABLE + " d"
        + " JOIN " + CourseOrderIO.TABLE + " cc ON (d.id =cc.idcmd)"
        + " JOIN " + ModuleOrderIO.TABLE + " cm ON (cc.module = cm.id)"
        + " JOIN " + ModuleIO.TABLE + " m ON (cm.module = m.id)"
        + " JOIN " + OrderLineIO.TABLE + " e ON (cm.idcmd = e.commande)"
        + "  WHERE (m.id in(149,151,157,158) OR m.code like 'L%')"
        + " AND cc.idaction = p.action"
        + " AND e.analytique != '15000')) AS q1";
    private final String total2Query = "SELECT sum(q1.fin-q1.debut) FROM"
        + "(SELECT DISTINCT ON (p.jour,pl.debut) pl.debut AS debut, pl.fin AS fin"
        + " FROM " + ScheduleRangeIO.TABLE + " pl JOIN " + ScheduleIO.TABLE + " p ON (pl.idplanning = p.id)"
        + " JOIN " + PersonIO.TABLE + " per ON (p.idper = per.id)"
        + " WHERE p.ptype IN (" + Schedule.COURSE + ","+ Schedule.WORKSHOP + "," + Schedule.TRAINING + ")"
        + " AND p.idper = ?"
        + " AND p.jour BETWEEN  ? AND ?"
        + " AND pl.adherent IN(SELECT adh FROM " + OrderIO.TABLE + " d"
        + " JOIN " + CourseOrderIO.TABLE + " cc ON (d.id = cc.idcmd)"
        + " JOIN " + ModuleOrderIO.TABLE + " cm ON (cc.module = cm.id)"
        + " JOIN " + ModuleIO.TABLE + " m ON (cm.module = m.id)"
        + " JOIN " + OrderLineIO.TABLE + " e ON (cm.idcmd = e.commande)"
        + " WHERE cc.idaction = p.action"
        + " AND ((m.id BETWEEN 141 AND 148 AND e.analytique != '15000') OR e.analytique = '15000')"
        + ")) AS q1";
    private final String total3Query = "SELECT sum(p.fin-p.debut) FROM " + ScheduleIO.TABLE + " p JOIN " + PersonIO.TABLE + " per ON (p.idper = per.id)"
          + " WHERE p.ptype = " + Schedule.ADMINISTRATIVE
          + " AND p.idper = ?"
      + " AND p.jour BETWEEN  ? AND ?";


    public CustomDAO(DataConnection dc) {
      this.dc = dc;
      ps1 = dc.prepareStatement(total1Query);
      ps2 = dc.prepareStatement(total2Query);
      ps3 = dc.prepareStatement(total3Query);
    }

    public List<Person> getTeachers(DateFr start, DateFr end) throws SQLException {
      List<Person> teachers = new ArrayList<>();

      String query = "SELECT DISTINCT p.idper,per.nom,per.prenom FROM " + ScheduleIO.TABLE + " p JOIN " + PersonIO.TABLE + " per ON (p.idper = per.id)"
        + " WHERE p.jour BETWEEN ? AND ? AND p.ptype IN (1,5,6) ORDER BY per.nom,per.prenom";
      PreparedStatement ps = dc.prepareStatement(query);

      ps.setDate(1, new java.sql.Date(start.getDate().getTime()));
      ps.setDate(2, new java.sql.Date(end.getDate().getTime()));

      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        Person t = new Person(rs.getInt(1));
        t.setName(rs.getString(2));
        t.setFirstName(rs.getString(3));
        teachers.add(t);
      }
      return teachers;
    }

    /**
     * DEM + L1 + L2 + Loisirs.
     * @param idper teacher id
     * @param start start of period
     * @param end end of period
     * @return a time-formatted string
     * @throws SQLException
     */
    public String getTotal1(int idper, DateFr start, DateFr end) throws SQLException {

      ps1.setInt(1, idper);
      ps1.setDate(2, new java.sql.Date(start.getTime()));
      ps1.setDate(3, new java.sql.Date(end.getTime()));
      ResultSet rs = ps1.executeQuery();
      while (rs.next()) {
        String t = rs.getString(1);
        return t == null ? "00:00" : t.substring(0,t.indexOf(':') + 3); // String.valueOf(time).substring(0,5);
      }
      return "00:00";
    }

    /**
     * Parcours BREVET + MIMA + FP (formations financées).
     * @param idper teacher id
     * @param start start of period
     * @param end end of period
     * @return a time-formatted string
     * @throws SQLException
     */
    public String getTotal2(int idper, DateFr start, DateFr end) throws SQLException {
      ps2.setInt(1, idper);
      ps2.setDate(2, new java.sql.Date(start.getTime()));
      ps2.setDate(3, new java.sql.Date(end.getTime()));
      ResultSet rs = ps2.executeQuery();
      while (rs.next()) {
        String t = rs.getString(1);
        return t == null ? "00:00" : t.substring(0,t.indexOf(':') + 3);
      }
      return "00:00";
    }

    /**
     * Réunions.
     * @param idper teacher id
     * @param start start of period
     * @param end end of period
     * @return a time-formatted string
     * @throws SQLException
     */
    public String getTotal3(int idper, DateFr start, DateFr end) throws SQLException {
      ps3.setInt(1, idper);
      ps3.setDate(2, new java.sql.Date(start.getTime()));
      ps3.setDate(3, new java.sql.Date(end.getTime()));
      ResultSet rs = ps3.executeQuery();
        while (rs.next()) {
          String t = rs.getString(1);
          return t == null ? "00:00" : t.substring(0,t.indexOf(':') + 3);
        }
        return "00:00";
      }
    }

}

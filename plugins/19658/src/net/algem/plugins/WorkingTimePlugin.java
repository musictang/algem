/*
 * @(#) WorkingTimePlugin.java Algem 2.10.2 23/06/2016
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
 * @version 2.10.2
 * @since 2.10.0 03/06/2016
 */
public class WorkingTimePlugin
  extends HoursTaskExecutor {

  private final int CODE_LOISIR = 1;
  private CustomDAO dao;

  private String[] categories = {
    "DEM + L1 + L2 + L3 + Loisirs",
    "Parcours BREVET + MIMA + FP",
    "Réunions"
  };

  public WorkingTimePlugin() {
    dao = new CustomDAO(DataCache.getDataConnection());
  }

  @Override
  public String getLabel() {
    return "Heures Jazz à Tours";
  }

  @Override
  public String getInfo() {
    StringBuilder sb = new StringBuilder();
    sb.append("Tri par professeur :\n");
    for (String c : categories) {
      sb.append(c).append('\n');
    }
    return sb.toString();
  }

  private void printResult(Person t, int total1, int total2, int total3) {
    StringBuilder sb = new StringBuilder();
    int total = total1 + total2 + total3;
    sb.append(t.getFirstnameName()).append(';').append(TextUtil.LINE_SEPARATOR);
    sb.append(categories[0]).append(';').append(Hour.getStringFromMinutes(total1)).append(TextUtil.LINE_SEPARATOR);
    sb.append(categories[1]).append(';').append(Hour.getStringFromMinutes(total2)).append(TextUtil.LINE_SEPARATOR);
    sb.append(categories[2]).append(';').append(Hour.getStringFromMinutes(total3)).append(TextUtil.LINE_SEPARATOR);
    sb.append("TOTAL;").append(Hour.getStringFromMinutes(total)).append(TextUtil.LINE_SEPARATOR);
    out.print(sb.toString());
  }

  private void printResult(DateFr d, int total1, int total2, int total3) {
    StringBuilder sb = new StringBuilder();
    int total = total1 + total2 + total3;
    sb.append(d).append(';').append(TextUtil.LINE_SEPARATOR);
    sb.append(categories[0]).append(';').append(Hour.getStringFromMinutes(total1)).append(TextUtil.LINE_SEPARATOR);
    sb.append(categories[1]).append(';').append(Hour.getStringFromMinutes(total2)).append(TextUtil.LINE_SEPARATOR);
    sb.append(categories[2]).append(';').append(Hour.getStringFromMinutes(total3)).append(TextUtil.LINE_SEPARATOR);
    sb.append("TOTAL ").append(d).append(';').append(Hour.getStringFromMinutes(total)).append(TextUtil.LINE_SEPARATOR);
    out.print(sb.toString());
  }

  private void printResult(int total1, int total2, int total3) {
    StringBuilder sb = new StringBuilder();
    int total = total1 + total2 + total3;
    sb.append("TOTAL ").append(categories[0]).append(';').append(Hour.getStringFromMinutes(total1)).append(TextUtil.LINE_SEPARATOR);
    sb.append("TOTAL ").append(categories[1]).append(';').append(Hour.getStringFromMinutes(total2)).append(TextUtil.LINE_SEPARATOR);
    sb.append("TOTAL ").append(categories[2]).append(';').append(Hour.getStringFromMinutes(total3)).append(TextUtil.LINE_SEPARATOR);
    sb.append("TOTAL PERIODE;").append(Hour.getStringFromMinutes(total)).append(TextUtil.LINE_SEPARATOR);
    out.print(sb.toString());
  }

  @Override
  public void execute() {
    try {
      Map<String, Object> props = getProperties();
      DateFr start = (DateFr) props.get("start");
      DateFr end = (DateFr) props.get("end");
      int idper = (int) props.get("idper");
      boolean detail = (boolean) props.get("detail");

      List<CustomSchedule> schedules = dao.getSchedules(idper, start, end);

      int t = 0;
      int totalL = 0;
      int totalP = 0;
      int totalR = 0;

      int totaldL = 0;
      int totaldP = 0;
      int totaldR = 0;
      DateFr d = null;
      Person person = null;
      int len = schedules.size();
      int progress = 0;
      for (CustomSchedule cs : schedules) {
        if (detail && d == null) {
          out.println(cs.getPerson().getFirstnameName());
        }
        if (detail && (!cs.getDate().equals(d) || cs.getPerson().getId() != t)) {
          if (d != null) { // pas au premier tour de boucle
            printResult(d, totaldL, totaldP, totaldR);//XXX cs.getDate()
            totaldL = 0;
            totaldP = 0;
            totaldR = 0;
          }
          d = cs.getDate();
        }
        if (cs.getPerson().getId() != t) {
          if (t > 0) { // pas au premier tour de boucle
            if (detail) {
              out.println();
              printResult(totalL, totalP, totalR);
              out.println();
              out.println(cs.getPerson().getFirstnameName());
            } else {
              printResult(person, totalL, totalP, totalR);
              out.println();
            }
            totalL = 0;
            totalP = 0;
            totalR = 0;
          }
          t = cs.getPerson().getId();
          person = cs.getPerson();
        }
       
        if (Schedule.ADMINISTRATIVE == cs.getType()) {
          totalR += cs.getLength();
          totaldR += cs.getLength();
          continue;
        } else {
          if (cs.isCollective()) {//XXX si aucune plage élève
            if (cs.status == CODE_LOISIR) {
              totalL += cs.getLength();
              totaldL += cs.getLength();
            } else {
              totalP += cs.getLength();
              totaldP += cs.getLength();
            }
          } else {
            List<CustomRange> ranges = dao.getRanges(cs.getId());
            for (CustomRange r : ranges) {
              if (r.isLeisure()) {
                totalL += r.length;
                totaldL += r.length;
              } else {
                totalP += r.length;
                totaldP += r.length;
              }
            }
          }
        }
        worker.setStep(progress++ * 100 / len);
      }
      if (detail) {
        printResult(d, totaldL, totaldP, totaldR);
        out.println();
        printResult(totalL, totalP, totalR);
      } else {
        printResult(person, totalL, totalP, totalR);
      }
    } catch (SQLException ex) {
      GemLogger.logException(ex);
    } finally {
      out.close();
    }
  }

  /*@Override
   public void execute() {
   try {
   Map<String, Object> props = getProperties();
   DateFr start = (DateFr) props.get("start");
   DateFr end = (DateFr) props.get("end");
   int idper = (int) props.get("idper");
   List<Person> teachers = new ArrayList<>();
   if (idper == 0) {
   teachers = dao.getTeachers(start, end);
   } else {
   teachers.add(dao.getTeacher(idper));
   }
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

   }*/
  private List<Person> getTestList() {
    List<Person> t = new ArrayList<>();
    t.add(new Person(106, "ANTOLIN", "Elora", "Mme"));
    return t;
  }

  private class CustomRange {

    private final int[] MOD_LOISIR = {149, 151, 157, 158};
    private final int[] MOD_PRO = {141, 142, 143, 144, 145, 146, 147, 148};
    private final String FP = "15000";

    private int length;
    private int moduleId;
    private String code;
    private String analytique;

    public CustomRange(int length, int moduleId, String code, String analytique) {
      this.length = length;
      this.moduleId = moduleId;
      this.code = code;
      this.analytique = analytique;
    }

    public boolean isLeisure() {
      if (code.startsWith("L")) {
        return true;
      }
      boolean l = false;
      for (int i : MOD_LOISIR) {
        if (moduleId == i) {
          l = true;
          break;
        }
      }
      return l && !FP.equals(analytique);
    }

    public boolean isPro() {
      if (FP.equals(analytique)) {
        return true;
      }
      for (int i : MOD_PRO) {
        if (moduleId == i) {
          return true;
        }
      }
      return false;
    }

  }

  private class CustomSchedule
    extends Schedule {

    private Person person;
    private int length;
    private int status;
    private int action;
    private boolean collective;

    public int getStatus() {
      return status;
    }

    public void setStatus(int status) {
      this.status = status;
    }

    public int getAction() {
      return action;
    }

    public void setAction(int action) {
      this.action = action;
    }

    public boolean isCollective() {
      return collective;
    }

    public void setCollective(boolean collective) {
      this.collective = collective;
    }

    public Person getPerson() {
      return person;
    }

    public void setPerson(Person person) {
      this.person = person;
    }

    public void setLength(int length) {
      this.length = length;
    }

    @Override
    public int getLength() {
      return length;
    }

  }

  private class CustomDAO {

    private DataConnection dc;
    private PreparedStatement ps1, ps2, ps3, ps4;
    private final String total1Query = "SELECT sum(q1.fin-q1.debut) FROM"
      + " (SELECT DISTINCT ON (p.jour,pl.debut)"
      + " pl.debut AS debut, pl.fin AS fin"
      + " FROM " + ScheduleRangeIO.TABLE + " pl JOIN " + ScheduleIO.TABLE + " p ON (pl.idplanning = p.id)"
      + " JOIN " + PersonIO.TABLE + " per ON (p.idper = per.id)"
      + " WHERE p.ptype IN (" + Schedule.COURSE + "," + Schedule.WORKSHOP + "," + Schedule.TRAINING + ")"
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
      + " WHERE p.ptype IN (" + Schedule.COURSE + "," + Schedule.WORKSHOP + "," + Schedule.TRAINING + ")"
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

    private final String rangeQuery = "SELECT DISTINCT ON (pl.id) pl.fin-pl.debut,m.id,m.code,e.analytique"
      + " FROM " + ScheduleRangeIO.TABLE + " pl JOIN " + ScheduleIO.TABLE + " p ON (pl.idplanning = p.id)"
      + " JOIN " + CourseOrderIO.TABLE + " cc ON (cc.idaction = p.action)"
      + " JOIN " + ModuleOrderIO.TABLE + " cm ON (cc.module = cm.id)"
      + " JOIN " + ModuleIO.TABLE + " m ON (cm.module = m.id)"
      //"JOIN commande d ON (cm.idcmd = d.id)\n" +
      + " JOIN " + OrderLineIO.TABLE + " e ON (cm.idcmd = e.commande AND e.adherent = pl.adherent)"
      + " WHERE pl.idplanning = ?";

    public CustomDAO(DataConnection dc) {
      this.dc = dc;
      ps1 = dc.prepareStatement(total1Query);
      ps2 = dc.prepareStatement(total2Query);
      ps3 = dc.prepareStatement(total3Query);

      ps4 = dc.prepareStatement(rangeQuery);
    }

    /**
     * Gets all the teachers scheduled between {@code start} and {@code end}.
     * @param start start of period
     * @param end end of period
     * @return a list of persons
     * @throws SQLException
     * @deprecated
     */
    List<Person> getTeachers(DateFr start, DateFr end) throws SQLException {
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
     * Get the name of the teacher with id {@code idper}.
     * @param idper teacher id
     * @return a person
     * @throws SQLException
     * @deprecated
     */
    Person getTeacher(int idper) throws SQLException {
      String query = "SELECT nom,prenom FROM " + PersonIO.TABLE + " WHERE id = " + idper;
      ResultSet rs = dc.executeQuery(query);
      while (rs.next()) {
        Person t = new Person(idper);
        t.setName(rs.getString(1));
        t.setFirstName(rs.getString(2));
        return t;
      }
      return new Person(0, "Inconnu", "", "");
    }

    private List<CustomSchedule> getSchedules(int idper, DateFr start, DateFr end) throws SQLException {
      List<CustomSchedule> schedules = new ArrayList<>();
      String query = "SELECT p.id,p.jour,p.fin-p.debut,p.ptype,p.idper,per.nom,per.prenom,p.action,c.collectif,a.statut"
        + " FROM " + ScheduleIO.TABLE + " p  JOIN " + PersonIO.TABLE + " per ON (p.idper = per.id)"
        + " JOIN action a ON (p.action = a.id) LEFT JOIN cours c ON (a.cours = c.id)"
        + " WHERE p.ptype IN (" + Schedule.COURSE + "," + Schedule.WORKSHOP + "," + Schedule.TRAINING + "," + Schedule.ADMINISTRATIVE + ")"
        + " AND p.jour BETWEEN '" + start + "' AND '" + end + "'"
        + " AND ((p.id IN (SELECT idplanning FROM " + ScheduleRangeIO.TABLE
        + ")) OR p.ptype = " + Schedule.ADMINISTRATIVE + ")";
      if (idper > 0) {
        query += " AND p.idper = " + idper;
      }
      query += " ORDER BY per.nom,per.prenom,p.jour,p.debut";
      ResultSet rs = dc.executeQuery(query);
      while (rs.next()) {
        CustomSchedule cs = new CustomSchedule();

        cs.setId(rs.getInt(1));
        cs.setDate(new DateFr(rs.getString(2)));
        String len = rs.getString(3);
        cs.setLength(Hour.getMinutesFromString(len));
        cs.setType(rs.getInt(4));
        Person p = new Person(rs.getInt(5), rs.getString(6), rs.getString(7), "");
        cs.setPerson(p);
        cs.setAction(rs.getInt(8));
        cs.setCollective(rs.getBoolean(9));
        cs.setStatus(rs.getInt(10));

        schedules.add(cs);
      }
      return schedules;
    }

    /**
     * Gets individual ranges enclosed in schedule {@code p}.
     * @param p schedule
     * @return a list of ranges
     * @throws SQLException
     */
    private List<CustomRange> getRanges(int p) throws SQLException {
      List<CustomRange> ranges = new ArrayList<>();
      ps4.setInt(1, p);
      ResultSet rs = ps4.executeQuery();
      while (rs.next()) {
        int min = Hour.getMinutesFromString(rs.getString(1));
        CustomRange cr = new CustomRange(min, rs.getInt(2), rs.getString(3), rs.getString(4));
        ranges.add(cr);
      }
      return ranges;
    }

    /**
     * DEM + L1 + L2 + Loisirs.
     *
     * @param idper teacher id
     * @param start start of period
     * @param end end of period
     * @return a time-formatted string
     * @throws SQLException
     * @deprecated
     */
    public String getTotal1(int idper, DateFr start, DateFr end) throws SQLException {

      ps1.setInt(1, idper);
      ps1.setDate(2, new java.sql.Date(start.getTime()));
      ps1.setDate(3, new java.sql.Date(end.getTime()));
      ResultSet rs = ps1.executeQuery();
      while (rs.next()) {
        String t = rs.getString(1);
        return t == null ? "00:00" : t.substring(0, t.indexOf(':') + 3); // String.valueOf(time).substring(0,5);
      }
      return "00:00";
    }

    /**
     * Parcours BREVET + MIMA + FP (formations financées).
     *
     * @param idper teacher id
     * @param start start of period
     * @param end end of period
     * @return a time-formatted string
     * @throws SQLException
     * @deprecated
     */
    public String getTotal2(int idper, DateFr start, DateFr end) throws SQLException {
      ps2.setInt(1, idper);
      ps2.setDate(2, new java.sql.Date(start.getTime()));
      ps2.setDate(3, new java.sql.Date(end.getTime()));
      ResultSet rs = ps2.executeQuery();
      while (rs.next()) {
        String t = rs.getString(1);
        return t == null ? "00:00" : t.substring(0, t.indexOf(':') + 3);
      }
      return "00:00";
    }

    /**
     * Réunions.
     *
     * @param idper teacher id
     * @param start start of period
     * @param end end of period
     * @return a time-formatted string
     * @throws SQLException
     * @deprecated
     */
    public String getTotal3(int idper, DateFr start, DateFr end) throws SQLException {
      ps3.setInt(1, idper);
      ps3.setDate(2, new java.sql.Date(start.getTime()));
      ps3.setDate(3, new java.sql.Date(end.getTime()));
      ResultSet rs = ps3.executeQuery();
      while (rs.next()) {
        String t = rs.getString(1);
        return t == null ? "00:00" : t.substring(0, t.indexOf(':') + 3);
      }
      return "00:00";
    }
  }

}

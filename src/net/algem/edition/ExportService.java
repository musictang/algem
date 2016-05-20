/*
 * @(#)ExportService.java 2.10.0 20/05/16
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
package net.algem.edition;

import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import net.algem.config.ConfigUtil;
import net.algem.config.Instrument;
import net.algem.config.InstrumentIO;
import net.algem.contact.*;
import net.algem.contact.member.MemberIO;
import net.algem.course.ModuleIO;
import net.algem.enrolment.CourseOrderIO;
import net.algem.enrolment.ModuleOrderIO;
import net.algem.enrolment.OrderIO;
import net.algem.group.GroupIO;
import net.algem.planning.*;
import net.algem.room.RoomIO;
import net.algem.security.User;
import net.algem.util.BundleUtil;
import net.algem.util.DataConnection;

/**
 * Service class for export operations.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.10.0
 * @since 2.6.d 06/11/2012
 */
public class ExportService
{

  public static final String[] CSV_HEADER = {
    BundleUtil.getLabel("First.name.label"),
    BundleUtil.getLabel("Name.label"),
    BundleUtil.getLabel("Address1.label"),
    BundleUtil.getLabel("Address2.label"),
    BundleUtil.getLabel("Zipcode.label"),
    BundleUtil.getLabel("City.label"),
    BundleUtil.getLabel("Email.label") + 1,
    BundleUtil.getLabel("Email.label") + 2,
    BundleUtil.getLabel("Telephone.label") + 1,
    BundleUtil.getLabel("Telephone.label") + 2,
    BundleUtil.getLabel("Telephone.label") + 3
  };
  private DataConnection dc;
  private PlanningService service;

  public ExportService(DataConnection dc) {
    this.dc = dc;
    this.service = new PlanningService(dc);
  }

  int printAddressCSV(PrintWriter out, String query) throws SQLException {
    int counter = 0;

    out.println(getHeader(CSV_HEADER));
    ResultSet rs = dc.executeQuery(query);
    while (rs.next()) {
      out.print(rs.getString(1));
      out.printf(";%s", rs.getString(2));
      out.printf(";%s", rs.getString(3));
      out.printf(";%s", rs.getString(4));
      out.printf(";%s", rs.getString(5));
      out.printf(";%s", rs.getString(6));
      out.println();
      counter++;
    }
    return counter;
  }

  int printCSV(PrintWriter out, List<Person> list) throws SQLException {
    out.println(getHeader(CSV_HEADER));
    for (Person p : list) {
      Address a = getAddress(p.getId());
      List<String> emails = getEmails(p.getId());
      List<String> tels = getTels(p.getId());
      out.print(p.getFirstName());
      out.printf(";%s", p.getName());
      out.printf(";%s", a.getAdr1());
      out.printf(";%s", a.getAdr2());
      out.printf(";%s", a.getCdp());
      out.printf(";%s", a.getCity());
      switch (emails.size()) {
        case 0:
          out.print(";;");
          break;
        case 1:
          out.printf(";%s;", emails.get(0));
          break;
        default:
          out.printf(";%s;%s", emails.get(0), emails.get(1));
      }
      switch (tels.size()) {
        case 0:
          out.print(";;;");
          break;
        case 1:
          out.printf(";%s;;", tels.get(0));
          break;
        case 2:
          out.printf(";%s;%s;", tels.get(0), tels.get(1));
          break;
        default:
          out.printf(";%s;%s;%s", tels.get(0), tels.get(1), tels.get(2));// 3 tels
      }
      out.println();
    }
    return list.size();
  }

  private String getHeader(String[] header) {
    StringBuilder h = new StringBuilder();
    for (String s : header) {
      h.append(s).append(";");
    }
    return h.substring(0, h.length() - 1);
  }

  int printEmailCSV(PrintWriter out, String query) throws SQLException {
    int counter = 0;
    String[] headers = {
      BundleUtil.getLabel("First.name.label"),
      BundleUtil.getLabel("Name.label"),
      BundleUtil.getLabel("Email.label")
    };
    out.println(getHeader(headers));
    ResultSet rs = dc.executeQuery(query);
    while (rs.next()) {
      out.print(rs.getString(1));
      out.printf(";%s", rs.getString(2));
      out.printf(";%s", rs.getString(3));
      out.println();
      counter++;
    }
    return counter;
  }

  String getBcc(List<Person> list) throws SQLException {
    StringBuilder bcc = new StringBuilder();
    Set<String> set = new LinkedHashSet<String>(); // insertion-ordered
    for (Person p : list) {
      for (String e : getEmails(p.getId())) {
        set.add(e);
      }
    }

    for (String s : set) {
      bcc.append(s).append(",");
    }
    if (bcc.length() > 0) {
      bcc.deleteCharAt(bcc.length() - 1);
    }
    return bcc.toString();
  }

  String getContactQueryByTeacher(int teacher, Date start, Date end, Boolean pro) {
    String query = "SELECT DISTINCT p.id, p.nom, p.prenom FROM "
            + ScheduleIO.TABLE + " s,"
            + PersonIO.TABLE + " p,"
            + CourseOrderIO.TABLE + " cc,"
            + OrderIO.TABLE + " c,"
            + ModuleOrderIO.TABLE + " cm,"
            + ModuleIO.TABLE + " m"
            + " WHERE s.jour >= '" + start + "' AND s.jour <= '" + end + "'"
            + " AND s.idper = " + teacher
            + " AND s.action = cc.idaction"
            + " AND cc.idcmd = c.id"
            + " AND c.adh = p.id"
            + " AND cc.module = cm.id"
            + " AND cm.module = m.id";
    if (pro != null) {
      query += " AND m.code LIKE '" + (pro ? "P%'" : "L%'");
    }
    query += " ORDER BY p.nom, p.prenom";
    return query;
  }

  String getContactQueryByCourse(int course, Date start, Date end, Boolean pro) {
    String query = "SELECT DISTINCT p.id, p.nom, p.prenom FROM "
            + PersonIO.TABLE + " p,"
            + ActionIO.TABLE + " a,"
            + ScheduleIO.TABLE + " s,"
            + CourseOrderIO.TABLE + " cc,"
            + OrderIO.TABLE + " c,"
            + ModuleOrderIO.TABLE + " cm,"
            + ModuleIO.TABLE + " m"
            + " WHERE s.jour >= '" + start + "' AND s.jour <= '" + end + "'"
            + " AND s.action = a.id"
            + " AND a.cours = " + course
            + " AND a.id = cc.idaction"
            + " AND cc.idcmd = c.id"
            + " AND c.adh = p.id"
            + " AND cc.module = cm.id"
            + " AND cm.module = m.id";
    if (pro != null) {
      query += " AND m.code LIKE '" + (pro ? "P%'" : "L%'");
    }
    query += " ORDER BY p.nom, p.prenom";
    return query;
  }

  String getContactQueryByModule(int module, Date start, Date end, Boolean pro) {
    String query = "SELECT DISTINCT p.id, p.nom, p.prenom FROM "
            + PersonIO.TABLE + " p, "
            + ScheduleIO.TABLE + " s, "
            + OrderIO.TABLE + " c, "
            + CourseOrderIO.TABLE + " cc, "
            + ModuleOrderIO.TABLE + " cm,"
            + ModuleIO.TABLE + " m"
            + " WHERE s.jour >= '" + start + "' AND s.jour <= '" + end + "'"
            + " AND s.action = cc.idaction"
            + " AND cc.module = cm.id"
            + " AND cm.module = " + module
            + " AND cm.module = m.id"
            + " AND cc.idcmd = c.id"
            + " AND c.adh = p.id";

    if (pro != null) {
      query += " AND m.code LIKE '" + (pro ? "P%'" : "L%'");
    }
    query += " ORDER BY p.nom, p.prenom";
    return query;
  }

  /**
   * Search query of people practicing the instrument {@literal instId}.
   *
   * @param instId instrument id
   * @param start start date
   * @param end end date
   * @param pro status
   * @return a SQL-string
   */
  String getContactQueryByInstrument(int instId, Date start, Date end, Boolean pro) {
    String query = "SELECT DISTINCT p.id, p.nom, p.prenom FROM "
            + OrderIO.TABLE + " c,"
            + InstrumentIO.PERSON_INSTRUMENT_TABLE + " i,"
            + CourseOrderIO.TABLE + " cc,"
            + ModuleOrderIO.TABLE + " cm,"
            + ModuleIO.TABLE + " m,"
            + PersonIO.TABLE + " p"
            + " WHERE c.adh = i.idper"
            + " AND i.instrument = " + instId + " AND i.ptype = " + Instrument.MEMBER
            + " AND c.id = cc.idcmd"
            + " AND c.adh = p.id"
            + " AND cc.datedebut >= '" + start + "' AND cc.datefin <= '" + end + "'"
            + " AND cc.module = cm.id"
            + " AND cm.module = m.id";
    if (pro != null) {
      query += " AND m.code LIKE '" + (pro ? "P%'" : "L%'");
    }
    query += " ORDER BY p.nom, p.prenom";
    return query;
  }

  String getStudent(Date start, Date end, Boolean pro, int estab) {
    String query = "SELECT DISTINCT p.id, p.nom, p.prenom"
            + " FROM " + OrderIO.TABLE + " c JOIN " + MemberIO.TABLE + " e ON (c.adh = e.idper)"
            + " JOIN " + PersonIO.TABLE + " p ON (e.idper = p.id)"
            + " JOIN " + CourseOrderIO.TABLE + " cc ON (c.id = cc.idcmd)"
            + " JOIN " + ModuleOrderIO.TABLE + " cm ON (cc.module = cm.id)"
            + " JOIN " + ModuleIO.TABLE + " m ON (cm.module = m.id)"
            + " JOIN " + ScheduleIO.TABLE + " pl ON (cc.idaction = pl.action)"
            + " JOIN " + RoomIO.TABLE + " s ON (pl.lieux = s.id)"
            + " WHERE cc.datedebut BETWEEN '" + start + "' AND '" + end + "'";
    if (pro != null) {
      query += " AND m.code LIKE '" + (pro ? "P%'" : "L%'");
    }
    if (estab > 0) {
      query += " AND s.etablissement = " + estab;
    }
    query += " ORDER BY p.nom, p.prenom";

    return query;
  }

  List<Person> getContacts(String query) throws SQLException {
    List<Person> list = new ArrayList<Person>();
    ResultSet rs = dc.executeQuery(query);
    while (rs.next()) {
      Person p = new Person(rs.getInt(1));
      p.setName(rs.getString(2));
      p.setFirstName(rs.getString(3));
      list.add(p);
    }
    return list;
  }

  Address getAddress(int idper) throws SQLException {
    String query = "SELECT DISTINCT a.adr1, a.adr2, a.cdp, a.ville"
            + " FROM " + AddressIO.TABLE + " a JOIN " + MemberIO.TABLE + " m ON (a.idper = m.idper OR a.idper = m.payeur)"
            + " WHERE m.idper = " + idper
            + " AND a.archive = false";
    ResultSet rs = dc.executeQuery(query);

    Address a = new Address();
    while (rs.next()) {
      a.setAdr1(rs.getString(1));
      a.setAdr2(rs.getString(2));
      a.setCdp(rs.getString(3));
      a.setCity(rs.getString(4));
    }
    return a;

  }

  List<String> getEmails(int idper) throws SQLException {
    String query = "SELECT DISTINCT m.email FROM "
            + EmailIO.TABLE + " m JOIN " + MemberIO.TABLE + " e ON (m.idper = e.idper OR m.idper = e.payeur)"
            + " WHERE e.idper = " + idper
            + " AND m.archive = false";
    ResultSet rs = dc.executeQuery(query);
    List<String> list = new ArrayList<String>();
    while (rs.next()) {
      list.add(rs.getString(1));
    }
    return list;
  }

  List<String> getTels(int idper) throws SQLException {
    String query = "SELECT DISTINCT t.numero FROM "
            + TeleIO.TABLE + " t JOIN " + MemberIO.TABLE + " m ON (t.idper = m.idper OR t.idper = m.payeur)"
            + " WHERE m.idper = " + idper;

    ResultSet rs = dc.executeQuery(query);
    List<String> list = new ArrayList<String>();
    while (rs.next()) {
      list.add(rs.getString(1));
    }
    return list;
  }

  String getMusicianByInstrument(int instrument, Date start, Date end) {
    String query = "SELECT DISTINCT p.id, p.nom, p.prenom"
            + " FROM " + PersonIO.TABLE + " p, "
            + ScheduleIO.TABLE + " s, "
            + GroupIO.TABLE + " g, "
            + GroupIO.TABLE_DETAIL + " d, "
            + InstrumentIO.PERSON_INSTRUMENT_TABLE + " i"
            + " WHERE s.jour >= '" + start + "' AND s.jour <= '" + end + "'"
            + " AND s.ptype = " + Schedule.GROUP
            + " AND s.idper = g.id AND g.id = d.id AND d.musicien = p.id"
            + " AND d.musicien = i.idper AND i.ptype = " + Instrument.MUSICIAN;
    if (instrument > 0) {
      query += " AND i.instrument = " + instrument;
    }
    query += " ORDER by p.nom, p.prenom";

    return query;
  }

  public String getUserEmail(User user) {
    Contact c = new Contact(user);
    ContactIO.complete(c, dc);
    Vector<Email> emails = c.getEmail();
    return (emails == null || emails.isEmpty()) ? user.getFirstName() : emails.elementAt(0).getEmail();
  }

  public Vector<ScheduleObject> getSchedule(DateFr start, DateFr end) throws SQLException {
    return service.getSchedule("WHERE jour >= '" + start + "' AND jour <= '" + end + "'  ORDER BY jour");
  }

  public Vector<ScheduleRangeObject> getScheduleRange(DateFr start, DateFr end) throws SQLException {
    String where = "AND p.jour >= '" + start + "' AND p.jour <= '" + end + "' AND pg.adherent != 0 ORDER BY p.jour";
    return ScheduleRangeIO.findRangeObject(where, service, dc);
  }

  public boolean isPro(int action, int idper) throws SQLException {
    String query = "SELECT m.code FROM "
            + ModuleIO.TABLE + " AS m, " + CourseOrderIO.TABLE + " AS c, " + OrderIO.TABLE + " AS o,"
            + ModuleOrderIO.TABLE + " AS mo"
            + " WHERE o.adh = " + idper
            + " AND o.id = c.idcmd AND c.idaction = " + action
            + " AND c.module = mo.id"
            + " AND mo.module = m.id";

    ResultSet rs = dc.executeQuery(query);
    if (rs.next()) {
      return rs.getString(1).startsWith("P");
    }
    return false;
  }

  public String getPath() {
    return ConfigUtil.getExportPath();
  }

}

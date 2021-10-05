/*
 * @(#)Statistics.java	2.11.0 30/09/16
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

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import javax.accessibility.Accessible;
import javax.swing.JProgressBar;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;
import net.algem.accounting.AccountPrefIO;
import net.algem.config.*;
import net.algem.planning.DateFr;
import net.algem.planning.Hour;
import net.algem.planning.Schedule;
import net.algem.planning.ScheduleIO;
import net.algem.room.Establishment;
import net.algem.util.*;
import net.algem.util.jdesktop.DesktopBrowseHandler;
import net.algem.util.jdesktop.DesktopHandlerException;
import net.algem.util.model.Model;
import net.algem.util.ui.MessagePopup;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.10.0
 * @since 2.6.a 09/10/12
 */
public abstract class Statistics
  extends SwingWorker<Void, Void> {

  protected static Integer MEMBERSHIP_ACCOUNT;
  protected static Integer PRO_MEMBERSHIP_ACCOUNT;
  protected static int MAINTENANCE = 5;

  protected DataCache dataCache;
  protected DataConnection dc;
  protected PrintWriter out;
  private static int year;
  protected DateFr start;
  protected DateFr end;
  protected List<Establishment> estabList;
  protected int navId = 0;
  protected int navCount = 0;
  protected Accessible monitor;
  protected String path;

  protected List<StatElement> statList;

  public Statistics() {
  }

  public void init(DataCache dataCache) {
    this.dataCache = dataCache;
    this.dc = DataCache.getDataConnection();
    estabList = dataCache.getList(Model.Establishment).getData();
  }

  /**
   * Sets the path of the output file and the dates between which query the database.
   *
   * @param path file path
   * @param prefs default membership accounts
   * @param start start of period
   * @param end end of period
   * @throws IOException
   */
  public void setConfig(String path, Preference[] prefs, DateFr start, DateFr end) throws IOException {
    this.start = start;
    this.end = end;
    this.path = path;
    out = new PrintWriter(new File(path), StandardCharsets.UTF_8.name());
    MEMBERSHIP_ACCOUNT = (Integer) prefs[0].getValues()[0];
    PRO_MEMBERSHIP_ACCOUNT = (Integer) prefs[1].getValues()[0];
    
  }

  /**
   * Sets the dates between which query the database.
   *
   * @param start start of period
   * @param end end of period
   * @throws IOException
   * @throws SQLException
   */
  public void setConfig(DateFr start, DateFr end) throws IOException, SQLException {
    this.start = start;
    this.end = end;
    path = ConfigUtil.getExportPath() + FileUtil.FILE_SEPARATOR + "stats.html";
    out = new PrintWriter(new File(path), StandardCharsets.UTF_8.name());
    Preference p1 = AccountPrefIO.find(AccountPrefIO.MEMBERSHIP, dc);
    Preference p2 = AccountPrefIO.find(AccountPrefIO.PRO_MEMBERSHIP, dc);
    MEMBERSHIP_ACCOUNT = (Integer) p1.getValues()[0];
    PRO_MEMBERSHIP_ACCOUNT = (Integer) p2.getValues()[0];
  }

  /**
   * Queries the database and displays the result of the different requests.
   *
   * @throws SQLException
   */
  public void makeStats() throws SQLException {

    header();
    for (StatElement entry : statList) {
      switch (entry.getKey()) {
        case 1:
//          printIntListResult(entry.getLabel(), getQuery("members_without_date_of_birth"));
          printTitle(entry.getLabel());
          listPersons(getQuery("members_without_date_of_birth"));
          break;
        case 2:
          printTitle(entry.getLabel());
          printIntResult(getQuery("total_number_of_students"));
          break;
        case 3:
          printTitle(entry.getLabel());
          List<AgeRange> ages = dataCache.getList(Model.AgeRange).getData();
          out.print("\n\t\t<table class='list'>");
          out.print("\n\t\t\t<tr><td>&nbsp;</td>");
          int totalLeisure = 0;
          int totalPro = 0;
          for (AgeRange a : ages) {
            if (!a.getCode().equals("-")) {
              out.print("<th>" + a.getAgemin() + "-" + a.getAgemax() + " ans</th>");
            }
          }
          out.print("<th>Total</th></tr>\n\t\t\t<tr><th>Amateurs</th>");
          for (AgeRange a : ages) {
            if (!a.getCode().equals("-")) {
              int r1 = getIntResult(getQuery("number_of_amateurs", a.getAgemin(), a.getAgemax()));
              totalLeisure += r1;
              out.print("<td>" + r1 + "</td>");
            }
          }

          out.print("<td>" + totalLeisure + "</td></tr>\n\t\t\t<tr><th>Pro</th>");
          for (AgeRange a : ages) {
            if (!a.getCode().equals("-")) {
              int r2 = getIntResult(getQuery("number_of_pros", a.getAgemin(), a.getAgemax()));
              totalPro += r2;
              out.print("<td>" + getIntResult(getQuery("number_of_pros", a.getAgemin(), a.getAgemax())) + "</td>");
            }
          }

          out.print("<td>" + totalPro + "</td></tr>");
          out.print("\n\t\t\t<tr><td colspan = '" + (ages.size() + 1) + "'>" + (totalLeisure + totalPro) + "</td></tr>\n\t\t</table>");
          break;
        case 4:
          printTitle(entry.getLabel());
          listPersons(getQuery("list_pro_students"));
          break;
        case 5:
          printTitle(entry.getLabel());
          out.println("\n\t\t<table class='list'>");
          ResultSet rs = dc.executeQuery(getQuery("students_by_location"));
          int total = 0;
          while (rs.next()) {
            total += rs.getInt(3);
            out.println("\n\t\t\t<tr><th>" + rs.getString(1) + "</th><td>" + rs.getString(2) + "</td>");
            out.println("<td>" + rs.getInt(3) + "</td></tr>");
          }
          out.println("\n\t\t\t<tr><td colspan='3'>Total : " + total + "</td></tr>");
          out.println("\n\t\t</table>");
          break;
        case 6:
          printTitle(entry.getLabel());
          String hr = getStringResult(getQuery("hours_of_rehearsal", null, Schedule.GROUP));
          String hm = getStringResult(getQuery("hours_of_rehearsal", null, Schedule.MEMBER));
          out.println("\n\t\t<table>");
          out.println("\n\t\t<tr><th>Groupes</th><td>" + parseTimeResult(hr) + "</td></tr>");
          out.println("\n\t\t<tr><th>Adhérents</th><td>" + parseTimeResult(hm) + "</td></tr>");
          out.println("\n\t\t</table>");
          break;
        case 7:
          printTitle(entry.getLabel());
          out.println("\n\t\t<table>");
          out.println("\n\t\t<tr><th>Groupes</th><td>" + getIntResult(getQuery("groups_with_rehearsal")) + "</td></tr>");
          out.println("\n\t\t<tr><th>Adhérents</th><td>" + getIntResult(getQuery("members_with_rehearsal")) + "</td></tr>");
          out.println("\n\t\t</table>");
          break;
      }
    }

  }

  /**
   * Closes monitor and output file.
   */
  public void close() {
    if (monitor != null && monitor instanceof JProgressBar) {
      ((JProgressBar) monitor).setIndeterminate(false);
      ((JProgressBar) monitor).setStringPainted(false);
    } else if (monitor != null && monitor instanceof ProgressMonitor) {
      ((ProgressMonitor) monitor).setProgress(100);
      ((ProgressMonitor) monitor).close();
    }
    if (out != null) {
      out.close();
    }
  }

  public abstract void setStats();

  public void setStats(List<StatElement> filtered) {
    statList = filtered;
  }

  public List<StatElement> getStats() {
    return statList;
  }

  protected void setMonitor(Accessible m) {
    this.monitor = m;
  }

  /**
   * Prints html separator.
   */
  protected void separate() {
    out.println("<div style='height : 20px'></div>");
  }

  /**
   * Prints html header.
   */
  protected void header() {
    String title = MessageUtil.getMessage("statistics.title", ConfigUtil.getConf(ConfigKey.ORGANIZATION_NAME.getKey()));
    String period = MessageUtil.getMessage("statistics.period", new Object[]{start, end});
    out.println("<!DOCTYPE html>\n<html>\n\t<head>\n\t\t<title>" + title + "</title>\n\t\t<meta charset='utf-8' />\n\t\t<style type='text/css'>" + getStyle() + "</style>\n\t</head>\n\t<body>");
    out.println("<h1 id=\"top\">" + title + "</h1>");
    out.println("<h2>" + period + "</h2>");
    setSummary();
  }

  /**
   * Prints html footer.
   */
  protected void footer() {
    String footer = MessageUtil.getMessage("statistics.footer",
      new Object[]{new DateFr(new Date()), dataCache.getUser().getFirstnameName()});
    out.println("<p>" + footer + "</p>\n\t</body>\n</html>");
  }

  protected void setSummaryDetail(StringBuilder nav) {
    for (StatElement v : statList) {
      addEntry(nav, v.getLabel());
    }
  }

  /**
   * Adds entry in summary.
   *
   * @param nav
   * @param title
   */
  protected void addEntry(StringBuilder nav, String title) {
    nav.append("<li><a href = \"#").append(navId).append("\">").append(title).append("</a></li>");
    navId++;
  }

  /**
   * Sets the summary.
   */
  protected void setSummary() {
    StringBuilder summary = new StringBuilder("<header><nav><ul>");
    // to redefine in subclasses
    setSummaryDetail(summary);
    navCount = navId - 2;
    summary.append("</ul></nav></header>");

    out.println(summary.toString());
    navId = 0;
  }

  @Override
  protected Void doInBackground() throws Exception {
    try {
      makeStats();
    } catch(SQLException sqe) {
      GemLogger.logException(sqe);
    } catch(Exception e) {
      GemLogger.logException(e);
    }
    return null;
  }

  @Override
  public void done() {
    close();
    firePropertyChange("done", null, null);
    if (MessagePopup.confirm(null, MessageUtil.getMessage("statistics.completed", path))) {
      DesktopBrowseHandler browser = new DesktopBrowseHandler();
      try {
        browser.browse(new File(path).toURI().toString());
      } catch (DesktopHandlerException ex) {
        GemLogger.log(ex.getMessage());
      }
    }
  }

  /**
   * Prints title of the current topic requested.
   *
   * @param title
   */
  protected void printTitle(String title) {
    out.println("\n\t\t<h3 id=\"" + navId + "\"><a href=\"#top\">::&uarr;::</a>&nbsp;" + title + "</h3>");
    navId++;
  }

  protected void printSecondaryTitle(String t) {
    out.println("\n\t\t<h4>"+t+"</h4>");
  }

  /**
   * Returns the result of a query as an integer.
   *
   * @param query
   * @return an integer
   * @throws SQLException
   */
  protected int getIntResult(String query) throws SQLException {
//System.out.println("Statistics.getIntResult:"+query);
      ResultSet rs = dc.executeQuery(query);
    int n = 0;
    while (rs.next()) {
      n += rs.getInt(1);
    }
    return n;
  }

  /**
   * Returns the result of a query as a string.
   *
   * @param query
   * @return a string optionnaly empty
   * @throws SQLException
   */
  protected String getStringResult(String query) throws SQLException {
    ResultSet rs = dc.executeQuery(query);
    if (rs.next()) {
      return rs.getString(1);
    }
    return "";
  }

  /**
   * Prints a title followed by the integer result of a query.
   *
   * @param query
   * @throws SQLException
   */
  protected void printIntResult(String query) throws SQLException {

//    printTitle(title);
    out.println("\n\t\t<ul class=\"list\">");
    ResultSet rs = dc.executeQuery(query);
    while (rs.next()) {
      out.println("\t\t\t<li>" + rs.getInt(1) + "</li>");
    }
    out.println("\t\t</ul>");
  }

  /**
   * Prints a title followed by the string result of a query.
   *
   * @param query
   * @throws SQLException
   */
  protected void printStringResult(String query) throws SQLException {
//    printTitle(title);
    ResultSet rs = dc.executeQuery(query);
    while (rs.next()) {
      out.println("<p style='font-weight:bold;text-align: right'>" + rs.getString(1) + "</p>");
    }
    out.println();
  }

  /**
   * Converts a string into time representation.
   *
   * @param h string to convert
   * @return a time-formatted string
   */
  protected String parseTimeResult(String h) {
    if (h == null) {
      return "";
    }
    int idx = h.indexOf(":");
    if (idx == -1) {
      return "";
    }
    int hh = Integer.parseInt(h.substring(0, idx));
    int hm = Integer.parseInt(h.substring(idx + 1, idx + 3));

    return String.format("%02d:%02d",hh ,hm);
  }

  /**
   * Prints a title followed by the time-formatted result of a query.
   *
   * @param query
   * @throws SQLException
   */
  protected void printTimeResult(String query) throws SQLException {
    ResultSet rs = dc.executeQuery(query);
    out.println("\t\t<ul class=\"list\">");
    while (rs.next()) {
      out.println("\n\t\t\t<li>" + parseTimeResult(rs.getString(1)) + "</li>");
    }
    out.println("\n\t\t</ul>");
  }

  /**
   * Prints a title followed by an unordered list of integers.
   *
   * @param query
   * @throws SQLException
   */
  protected void printIntListResult(String query) throws SQLException {
    out.println("\t\t<ul class=\"list\">");
    ResultSet rs = dc.executeQuery(query);
    while (rs.next()) {
      out.println("\n\t\t\t<li>" + rs.getInt(1) + "</li>");
    }
    out.println("\n\t\t</ul>");
  }


  protected void printTableResult(String query) throws SQLException {
    ResultSet rs = dc.executeQuery(query);
    if (rs == null) return;
    int cols = rs.getMetaData().getColumnCount();

    out.println("\n\t\t<table class='list'>");
    while (rs.next()) {
      out.print("\n\t\t\t<tr>");
      for (int i = 0; i < cols ; i++) {
        out.print("<td>" + rs.getObject(i+1) + "</td>");
      }
      out.println("</tr>");
    }
    out.println("\n\t\t</table>");
  }
  /**
   * Prints an array of String-integer pairs.
   *
   * @param query SQL query
   * @throws SQLException
   */
  protected void printTableIntResult(String query) throws SQLException {
    out.println("\n\t\t<table class='list'>");
    ResultSet rs = dc.executeQuery(query);
    int total = 0;
    while (rs.next()) {
      total += rs.getInt(2);
      out.println("\n\t\t\t<tr><th>" + rs.getString(1) + "</th>");
      out.println("<td>" + rs.getInt(2) + "</td></tr>");
    }
    out.println("\n\t\t\t<tr><td colspan='2'>Total : " + total + "</td></tr>");
    out.println("\n\t\t</table>");
  }

  /**
   * Prints an array of time-formatted strings.
   *
   * @param query SQL query
   * @throws SQLException
   */
  protected void printTableTimeResult(String query) throws SQLException {
    out.println("\n\t\t<table class='list'>");
    ResultSet rs = dc.executeQuery(query);
    int total = 0;
    Hour hm = new Hour();
    while (rs.next()) {
      String h = parseTimeResult(rs.getString(2));
      if (h != null) {
        hm.set(rs.getString(2));
        total += hm.toMinutes();
      }
      out.println("\n\t\t\t<tr><th>" + rs.getString(1) + "</th>");
      out.println("<td>" + h + "</td></tr>");
    }
    out.println("\n\t\t\t<tr><td colspan='2'>Total : " + getTimeFromMinutes(total) + "</td></tr>");
    out.println("\n\t\t</table>");
  }

  protected String getTimeFromMinutes(int minutes) {
    int h = (minutes / 60);
    int m = minutes % 60;
    return String.format("%02d:%02d",h ,m);
  }

  /**
   * Gets the total in minutes from formatted time string.
   *
   * @param time with this pattern *hh:mm:00
   * @return a total in minutes
   */
//  protected int getMinutesFromString(String time) {
//    if (time == null || time.isEmpty()) {
//      return 0;
//    }
//    try {
//      int h = 0;
//      int m = 0;
//      int firstIdx = time.indexOf(':');
//      int lastIdx = time.lastIndexOf(':');
//      if (firstIdx == -1) {
//        h = Integer.parseInt(time.substring(0));
//        m = 0;
//      } else {
//        h = Integer.parseInt(time.substring(0, firstIdx));
//        if (lastIdx == -1) {
//          m = Integer.parseInt(time.substring(firstIdx + 1));
//        } else {
//          m = Integer.parseInt(time.substring(firstIdx + 1, lastIdx));
//        }
//      }
//      return (h + m) * 60;
//    } catch (NumberFormatException ne) {
//      return 0;
//    }
//
//  }

  /**
   * Returns a query depending on {@literal topic}.
   *
   * @param topic
   * @return a SQL query
   * @throws SQLException
   */
  protected String getQuery(String topic) throws SQLException {
    if (topic.equals("members_without_date_of_birth")) {
      return "SELECT DISTINCT eleve.idper,personne.prenom,personne.nom FROM commande_cours, commande, eleve, personne"
        + " WHERE commande_cours.datedebut >= '" + start + "' AND commande_cours.datedebut <= '" + end + "'"
        + " AND commande_cours.idcmd = commande.id "
        + " AND commande.adh = eleve.idper "
        + " AND eleve.idper = personne.id"
        + " AND (extract(year from age(eleve.datenais)) > 100"
        + " OR extract(year from age(eleve.datenais)) < 1"
        + " OR eleve.datenais is null) ORDER BY personne.nom,personne.prenom";
    }
    if (topic.equals("total_number_of_students")) {
      // on ne tient pas compte des commande_cours à définir
      return "SELECT count(DISTINCT eleve.idper) FROM commande_cours, commande, eleve"
        + " WHERE commande_cours.datedebut >= '" + start + "' AND commande_cours.datedebut <= '" + end + "'"
        + " AND commande_cours.idcmd = commande.id"
        + " AND commande.adh = eleve.idper"
        + " AND commande_cours.debut != '00:00:00'"; // ou commande_cours.idaction = 0
    }
    if (topic.equals("list_pro_students")) {
      return "SELECT DISTINCT(commande.adh), trim(personne.nom) as name, trim(personne.prenom) as firstname"
        + " FROM commande, commande_cours, commande_module, module, eleve, personne"
        + " WHERE commande_cours.module = commande_module.id"
        + " AND commande_module.module = module.id"
        + " AND commande_cours.idcmd = commande.id"
        + " AND eleve.idper = commande.adh"
        + " AND eleve.idper = personne.id"
        + " AND commande_cours.datedebut BETWEEN '" + start + "' AND '" + end + "'"
        + " AND module.code LIKE 'P%'"
        + " ORDER BY name,firstname";
    }
    if (topic.equals("students_by_location")) {
      return "SELECT a.ville, a.cdp, count(distinct e.idper) FROM commande_cours cc JOIN commande c ON cc.idcmd = c.id"
              + " JOIN eleve e ON c.adh = e.idper JOIN adresse a ON e.payeur = a.idper"
        + " WHERE cc.datedebut BETWEEN '" + start + "' AND '" + end + "'"
        + " GROUP BY a.ville, a.cdp ORDER BY a.cdp,a.ville";
    }
    if (topic.equals("groups_with_rehearsal")) {
      return "SELECT count(DISTINCT planning.idper) FROM planning, groupe"
        + " WHERE planning.idper = groupe.id"
        + " AND planning.jour BETWEEN '" + start + "' AND '" + end + "'"
        + " AND planning.ptype = " + Schedule.GROUP
        + " AND planning.lieux <> 8"; // musiques tangentes seulement
    }

    if (topic.equals("members_with_rehearsal")) {
      return "SELECT count(DISTINCT planning.idper) FROM planning, personne"
        + " WHERE planning.idper = personne.id"
        + " AND jour BETWEEN '" + start + "' AND '" + end + "'"
        + " AND planning.ptype = " + Schedule.MEMBER
        + " AND planning.lieux <> 8"; // musiques tangentes seulement
    }

    if (topic.equals("hours_of_pro_lessons")) {
      return "SELECT sum(duree) FROM "
        + "(SELECT DISTINCT plage.adherent, sum(plage.fin - plage.debut) AS duree"
        + " FROM plage, planning, commande, commande_cours, commande_module, module"
        + " WHERE plage.idplanning = planning.id"
        + " AND planning.jour BETWEEN '" + start + "' AND  '" + end + "'"
        + " AND plage.adherent = commande.adh"
        + " AND commande.id = commande_cours.idcmd"
        + " AND commande_cours.datedebut BETWEEN '" + start + "' AND  '" + end + "'"
        + " AND commande_cours.idaction = planning.action"
        + " AND commande_cours.module = commande_module.id"
        + " AND commande_module.module = module.id"
        + " AND module.code LIKE 'P%'"
        + " GROUP BY plage.adherent) AS t1";
    }

    if (topic.equals("hours_of_collective_pro_lessons")) {
      return "SELECT sum(duree) FROM "
        + "(SELECT DISTINCT plage.adherent, sum(plage.fin - plage.debut) AS duree"
        + " FROM plage, planning, action, cours, commande, commande_cours, commande_module, module"
        + " WHERE plage.idplanning = planning.id"
        + " AND planning.jour BETWEEN '" + start + "' AND  '" + end + "'"
        + " AND planning.action = action.id"
        + " AND action.cours = cours.id"
        + " AND cours.collectif = 't'"
        + " AND plage.adherent = commande.adh"
        + " AND commande.id = commande_cours.idcmd"
        + " AND commande_cours.datedebut BETWEEN '" + start + "' AND  '" + end + "'"
        + " AND commande_cours.idaction = planning.action"
        + " AND commande_cours.module = commande_module.id"
        + " AND commande_module.module = module.id"
        + " AND module.code LIKE 'P%'"
        + " GROUP BY plage.adherent) AS t1";

    }
    if (topic.equals("hours_of_private_pro_lessons")) {
      return "SELECT sum(duree) FROM "
        + "(SELECT DISTINCT plage.adherent, sum(plage.fin - plage.debut) AS duree"
        + " FROM plage, planning, action, cours, commande, commande_cours, commande_module, module"
        + " WHERE plage.idplanning = planning.id"
        + " AND planning.jour BETWEEN '" + start + "' AND  '" + end + "'"
        + " AND planning.action = action.id"
        + " AND action.cours = cours.id"
        + " AND cours.collectif = 'f'"
        + " AND plage.adherent = commande.adh"
        + " AND commande.id = commande_cours.idcmd"
        + " AND commande_cours.datedebut BETWEEN '" + start + "' AND  '" + end + "'"
        + " AND commande_cours.idaction = planning.action"
        + " AND commande_cours.module = commande_module.id"
        + " AND commande_module.module = module.id"
        + " AND module.code LIKE 'P%'"
        + " GROUP BY plage.adherent) AS t1";
    }

    if (topic.equals("hours_teacher_of_collective_lessons")) {
      return "SELECT sum(duree) FROM("
        + "SELECT distinct p1.id, sum(p1.fin - p1.debut) as duree"
        + " FROM planning p1, action, cours"
        + " WHERE p1.jour BETWEEN '" + start + "' AND  '" + end + "'"
        + " AND p1.action = action.id"
        + " AND action.cours = cours.id"
        + " AND cours.collectif = 't'"
        + " AND EXISTS("
        + "SELECT plage.idplanning "
        + " FROM plage, commande, commande_cours, commande_module, module"
        + "   WHERE plage.idplanning = p1.id"
        + "	AND plage.adherent = commande.adh"
        + "	AND commande.id = commande_cours.idcmd"
        + "	AND commande_cours.datedebut BETWEEN '" + start + "' AND  '" + end + "'"
        + "	AND commande_cours.module = commande_module.id"
        + "	AND commande_module.module = module.id"
        + "	AND module.code LIKE 'P%') GROUP BY p1.id) AS t1";
    }

    if (topic.equals("total_hours_of_studio")) {
      return "SELECT sum(p.fin - p.debut) FROM " + ScheduleIO.TABLE + " p"
        + " WHERE p.jour >= '" + start + "' AND p.jour <= '" + end + "'"
        + " AND p.ptype = " + Schedule.TECH + " AND p.note != " + MAINTENANCE;
    }
    if (topic.equals("hours_of_studio_by_type")) {
      return "SELECT c.nom, sum(p.fin - p.debut) FROM " + ScheduleIO.TABLE + " p, " + StudioTypeIO.TABLE + " c"
        + " WHERE p.jour >= '" + start + "' AND p.jour <= '" + end + "'"
        + " AND p.ptype = " + Schedule.TECH
        + " AND p.note = c.id"
        + " AND p.note != " + MAINTENANCE
        + " GROUP BY c.nom";
    }
    if (topic.equals("hours_of_training")) {
      return "SELECT sum(p.fin - p.debut) FROM " + ScheduleIO.TABLE + " p"
        + " WHERE p.jour >= '" + start + "' AND p.jour <= '" + end + "'"
        + " AND p.ptype = " + Schedule.TRAINING;
    }

    return null;

  }

  /**
   * Gets a sql query based on {@literal topic} and two parameters {@literal a1}, {@literal a2}.
   *
   * @param topic
   * @param a1 first parameter
   * @param a2 second parameter
   * @return SQL query
   */
  protected String getQuery(String topic, Object a1, Object a2) {
    if ("number_of_amateurs".equals(topic)) {
      return "SELECT count(DISTINCT eleve.idper) "
        + "FROM eleve, commande, commande_cours, commande_module, module"
        + " WHERE eleve.idper = commande.adh"
        + " AND commande.id = commande_cours.idcmd"
        + " AND commande_cours.module = commande_module.id"
        + " AND commande_module.module = module.id"
        + " AND commande_cours.datedebut between '" + start + "' AND '" + end + "'"
        + " AND extract(year from age(commande_cours.datedebut,datenais)) >= " + a1
        + " AND extract(year from age(commande_cours.datedebut,datenais)) <= " + a2 // correction agemax inclus
        + " AND module.code NOT LIKE 'P%'"
        + " AND commande_cours.debut != '00:00:00'";
    }

    if ("number_of_pros".equals(topic)) {
      return "SELECT count(DISTINCT eleve.idper) "
        + "FROM eleve, commande, commande_cours, commande_module, module, action, cours"
        + " WHERE eleve.idper = commande.adh"
        + " AND commande.id = commande_cours.idcmd"
        + " AND commande_cours.module = commande_module.id"
        + " AND commande_module.module = module.id"
        + " AND commande_cours.idaction = action.id"
        + " AND action.cours = cours.id"
        + " AND commande_cours.datedebut between '" + start + "' AND '" + end + "'"
        + " AND extract(year from age(commande_cours.datedebut,eleve.datenais)) >= " + a1
        + " AND extract(year from age(commande_cours.datedebut,eleve.datenais)) <= " + a2 // correction agemax inclus
        + " AND module.code LIKE 'P%'"
        + " AND cours.titre NOT LIKE '%A_D_FINIR%'";
    }

    // élèves par activité
    if ("students_by_instrument".equals(topic)) {
      String query = "SELECT cours.titre, count(distinct plage.adherent) FROM plage, planning, cours, action, salle"
        + " WHERE plage.idplanning = planning.id"
        + " AND planning.jour BETWEEN '" + start + "' AND '" + end + "'"
        + " AND plage.debut >= planning.debut"
        + " AND plage.fin <= planning.fin"
        + " AND planning.action = action.id"
        + " AND action.cours = cours.id"
        + " AND planning.ptype = " + Schedule.COURSE
        + " AND planning.lieux = salle.id"
        + " AND cours.collectif = " + a1;
      if ((Integer) a2 > 0) {
        query += " AND salle.etablissement = " + a2;
      }
      query += " GROUP BY cours.titre";
      return query;
    }

    if ("hours_of_rehearsal".equals(topic)) {
      return "SELECT sum(planning.fin - planning.debut) FROM planning"
        + " WHERE planning.jour BETWEEN '" + start + "' AND '" + end + "'"
        + " AND planning.ptype = " + a2;
    }

    if (topic.equals("hours_of_lessons")) {
//      return "SELECT sum(duree) FROM "
//              + "(SELECT DISTINCT plage.adherent, sum(plage.fin - plage.debut) AS duree"
//              + " FROM plage, planning, action, cours, commande, commande_cours, commande_module, module"
//              + " WHERE plage.idplanning = planning.navId"
//              + " AND planning.jour BETWEEN '" + start + "' AND  '" + end + "'"
//              + " AND planning.action = action.navId"
//              + " AND action.cours = cours.navId"
//              + " AND cours.collectif = " + a1
//              + " AND plage.adherent = commande.adh"
//              + " AND commande.navId = commande_cours.idcmd"
//              + " AND commande_cours.datedebut BETWEEN '" + start + "' AND  '" + end + "'"
//              + " AND commande_cours.idaction = planning.action"
//              + " AND commande_cours.module = commande_module.navId"
//              + " AND commande_module.module = module.navId"
//              + " AND module.code LIKE '" + a2 + "%'"
//              + " GROUP BY plage.adherent) AS t1";
      return "SELECT sum(duree) FROM("
        + "SELECT plage.idplanning, sum(plage.fin - plage.debut) AS duree"
        + " FROM plage, planning p, action, cours"
        + " WHERE plage.idplanning = p.id"
        + " AND p.jour BETWEEN '" + start + "' AND  '" + end + "'"
        + " AND p.action = action.id"
        + " AND p.ptype IN (" + Schedule.COURSE + "," + Schedule.WORKSHOP + "," + Schedule.TRAINING + ")"
        + " AND action.cours = cours.id"
        + " AND cours.collectif = " + a1
        + " AND plage.adherent IN("
        + "SELECT commande.adh FROM commande, commande_cours, commande_module, module"
        + " WHERE commande_cours.idaction = p.action "
        + " AND commande.id = commande_cours.idcmd"
        + " AND commande_cours.datedebut BETWEEN '" + start + "' AND  '" + end + "'"
        + " AND commande_cours.module = commande_module.id"
        + " AND commande_module.module = module.id"
        + " AND module.code LIKE '" + a2 + "%')"
        + " GROUP BY plage.idplanning) AS t1";
    }

    if (topic.equals("hours_of_teacher_lessons")) { // collectif !
      return "SELECT sum(duree) FROM("
        + "SELECT distinct p1.id, sum(p1.fin - p1.debut) as duree"
        + " FROM planning p1, action, cours"
        + " WHERE p1.jour BETWEEN '" + start + "' AND  '" + end + "'"
        + " AND p1.ptype IN (" + Schedule.COURSE + "," + Schedule.WORKSHOP + "," + Schedule.TRAINING + ")"
        + " AND p1.action = action.id"
        + " AND action.cours = cours.id"
        + " AND cours.collectif = " + a1
        + " AND EXISTS("
        + "SELECT plage.idplanning "
        + " FROM plage, commande, commande_cours, commande_module, module"
        + " WHERE plage.idplanning = p1.id"
        + "	AND plage.adherent = commande.adh"
        + "	AND commande.id = commande_cours.idcmd"
        + "	AND commande_cours.datedebut BETWEEN '" + start + "' AND  '" + end + "'"
        + "	AND commande_cours.module = commande_module.id"
        + "	AND commande_module.module = module.id"
        + "	AND module.code LIKE '" + a2 + "%') GROUP BY p1.id) AS t1";
    }

    return null;
  }

  /**
   * Prints the list of pro students.
   *
   * @param query SQL query
   * @throws SQLException
   */
  protected void listPersons(String query) throws SQLException {

    out.println("\t\t<table>");
    ResultSet rs = dc.executeQuery(query);
    int n = 0;
    while (rs.next()) {
      n++;
      out.println("\n\t\t\t<tr><th>" + rs.getInt(1) + "</th>");
      out.println("<td>" + rs.getString(2) + " " + rs.getString(3) + "</td></tr>");
    }
    out.println("\n\t\t\t<tr><td colspan='2'>Total : " + n + "</td></tr>");
    out.println("\n\t\t</table>");
  }

  /**
   * Gets the main css attributes.
   *
   * @return a css string
   */
  private String getStyle() {
    return "\n\t\tbody {font-family:Arial,sans-serif;font-size: 1em;width: 80%;margin: 0 auto;}"
      + "\n\t\th1, h2, h3 {color:white;background: #4096ee;padding:0.2em;margin:1em 0 0.5em 0;border-radius: 4px;background:linear-gradient(to bottom, #4096ee 0%,#60abf8 56%,#7abcff 100%);padding:0.2em}"
      + "\n\t\th4 {font-family: Verdana,sans-serif;font-weight:normal;padding:0;margin:0.5em 0 }"
      + "\n\t\ttable {font-family: Verdana,sans-serif;border: 2px solid #4096ee;border-radius: 4px;border-spacing: 1px}"
      + " table th {text-align: left;padding: 0.2em;} table td {text-align:right;padding: 0.2em;}"
      + "\n\t\ttable.list td {text-align:right}"
      + "\n\t\ttr:nth-child(even) {background-color: #CCE6FF;}"
      + "\n\t\ttr:nth-child(odd) {background-color: #99CDFF;}"
      + "\n\t\tul {list-style-type :none } ul.list {display: inline-block;margin:0;padding:0} ul.list li {text-align: right;font-weight: bold}"
      + "\n\t\tul li {font-family: Verdana,sans-serif;font-weight: normal}"
      + "\n\t\ta:link,a:active {color: #0052CC;} a:visited {color: #666} a:hover {color: #0066ff}"
      + "\n\t\ta[href=\"#top\"] {text-decoration: none;color: white;font-weight: bold}"
      + "\n\t\ta[href=\"#top\"]:hover {color: #0066ff;background-color: white;border-radius: 4px";
  }

  private String getMethodName() {
    return getClass().getEnclosingMethod().getName();
  }
}

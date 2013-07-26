/*
 * @(#)Statistics.java	2.8.k 19/07/13
 * 
 * Copyright (c) 1999-2013 Musiques Tangentes. All Rights Reserved.
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

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import net.algem.accounting.AccountPrefIO;
import net.algem.config.AgeRange;
import net.algem.config.ConfigKey;
import net.algem.config.ConfigUtil;
import net.algem.config.Preference;
import net.algem.planning.DateFr;
import net.algem.planning.Schedule;
import net.algem.room.Establishment;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;
import net.algem.util.FileUtil;
import net.algem.util.MessageUtil;
import net.algem.util.model.Model;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.k
 * @since 2.6.a 09/10/12
 */
public abstract class Statistics
{

  protected DataCache dataCache;
  protected DataConnection dc;
  protected PrintWriter out;
  private static int year;
  protected DateFr start;
  protected DateFr end;
  protected static Integer MEMBERSHIP_ACCOUNT;
  protected List<Establishment> estabList;
  protected int id = 0;
  

  public Statistics() {
  }

  public void init(DataCache dataCache) {
    this.dataCache = dataCache;
    this.dc = dataCache.getDataConnection();
    estabList = dataCache.getList(Model.Establishment).getData();
  }
  
  public void setConfig(String path, Preference p, DateFr start, DateFr end) throws IOException {
    this.start = start;
    this.end = end;
    out = new PrintWriter(new FileWriter(path));
    MEMBERSHIP_ACCOUNT = (Integer) p.getValues()[0];
  }
  
  public void setConfig(DateFr start, DateFr end) throws IOException, SQLException {
    this.start = start;
    this.end = end;
    String path = ConfigUtil.getExportPath(dc) + FileUtil.FILE_SEPARATOR + "stats.html";
    out = new PrintWriter(new FileWriter(path));
    Preference p = AccountPrefIO.find(AccountPrefIO.MEMBER_KEY_PREF, dc);
    MEMBERSHIP_ACCOUNT = (Integer) p.getValues()[0];
  }
  
  public void makeStats() throws SQLException {
    
    header();
    
    printIntListResult(MessageUtil.getMessage("statistics.members.without.date.of.birth"), getQuery("members_without_date_of_birth"));

    printIntResult(MessageUtil.getMessage("statistics.number.of.students"), getQuery("total_number_of_students"));
    
    printTitle(MessageUtil.getMessage("statistics.distribution.between.amateurs.pros"));
    List<AgeRange> ages = dataCache.getList(Model.AgeRange).getData();
    out.print("\n\t\t<table class='list'>");
    out.print("\n\t\t\t<tr><td>&nbsp;</td>");
    int totalLeisure = 0;
    int totalPro = 0;
    for (AgeRange a : ages) {
      if (!a.getCode().equals("-")) {
        out.print("<th>"+a.getAgemin()+"-"+a.getAgemax()+" ans</th>");
      }
    }
    out.print("<th>Total</th></tr>\n\t\t\t<tr><th>Amateurs</th>");
    for (AgeRange a : ages) {
      if (!a.getCode().equals("-")) {
        int r1 = getIntResult(getQuery("number_of_amateurs", a.getAgemin(), a.getAgemax()));
        totalLeisure += r1;
        out.print("<td>"+r1+"</td>");
      }
    }

    out.print("<td>"+totalLeisure+"</td></tr>\n\t\t\t<tr><th>Pro</th>");
    for (AgeRange a : ages) {
      if (!a.getCode().equals("-")) {
        int r2 = getIntResult(getQuery("number_of_pros", a.getAgemin(), a.getAgemax()));
        totalPro += r2;
        out.print("<td>"+getIntResult(getQuery("number_of_pros", a.getAgemin(), a.getAgemax()))+"</td>");
      }
    }
    out.print("<td>"+totalPro+"</td></tr>");
    out.print("\n\t\t\t<tr><td colspan = '"+(ages.size()+1)+"'>"+(totalLeisure+totalPro)+"</td></tr>\n\t\t</table>");
    
    listProStudents(getQuery("list_pro_students"));
    
    printTableIntResult(MessageUtil.getMessage("statistics.city.distribution"), getQuery("students_by_location"));

    printTitle(MessageUtil.getMessage("statistics.number.of.hours.of.rehearsal"));
    String hr = getStringResult(getQuery("hours_of_rehearsal", null, Schedule.GROUP_SCHEDULE));
    String hm = getStringResult(getQuery("hours_of_rehearsal", null, Schedule.MEMBER_SCHEDULE));
    out.println("\n\t\t<table>");
    out.println("\n\t\t<tr><th>Groupes</th><td>"+parseTimeResult(hr)+"</td></tr>");
    out.println("\n\t\t<tr><th>Adhérents</th><td>"+parseTimeResult(hm)+"</td></tr>");
    out.println("\n\t\t</table>");
    
    printTitle(MessageUtil.getMessage("statistics.number.of.rehearsing.people"));
    out.println("\n\t\t<table>");
    out.println("\n\t\t<tr><th>Groupes</th><td>"+getIntResult(getQuery("groups_with_rehearsal"))+"</td></tr>");
    out.println("\n\t\t<tr><th>Adhérents</th><td>"+getIntResult(getQuery("members_with_rehearsal"))+"</td></tr>");
    out.println("\n\t\t</table>");

  }
  
  protected void separate() {
    out.println("<div style='height : 20px'></div>");
  }
  
  private String getStyle() {
    return "\n\t\th1, h2, h3 {background-color: #d2d7ff;}"
            + "\n\t\ttable th, table td {text-align:left}"
            + "\n\t\ttable.list td {text-align:right}"
            + "\n\t\ttr:nth-child(even) {background-color: #EECCCC;}"
            + "\n\t\ttr:nth-child(odd) {background-color: #FFCCCC;}"
            + "\n\t\tul {list-style-type :none }"
            + "\n\t\tul li {font-weight: bold}";
  }
  
  protected void header() {
    String title = MessageUtil.getMessage("statistics.title", ConfigUtil.getConf(ConfigKey.ORGANIZATION_NAME.getKey(), dc));
    String period = MessageUtil.getMessage("statistics.period", new Object[] {start, end});
    out.println("<!DOCTYPE html>\n<html>\n\t<head>\n\t\t<title>"+title+"</title>\n\t\t<meta charset='utf-8' />\n\t\t<style type='text/css'>"+getStyle()+"</style>\n\t</head>\n\t<body>");
    out.println("<h1 id=\"top\">"+title+"</h1>");
    out.println("<h2>"+period+"</h2>");
    setSummary();
  }
  
  protected void footer() {
    String footer = MessageUtil.getMessage("statistics.footer", 
            new Object[] {new DateFr(new Date()), dataCache.getUser().getFirstnameName()});
    out.println("<p>"+footer+"</p>\n\t</body>\n</html>");
  }
  
  protected void addEntry(StringBuilder nav, String title) {
    nav.append("<li><a href = \"#").append(id).append("\">").append(title).append("</a></li>");
    id ++;
  }
  
  protected void setSummary() {
    StringBuilder nav = new StringBuilder("<header><nav><ul>");
    // to redefine in subclasses
    setSummaryDetail(nav);
    nav.append("</ul></nav></header>");
    
    out.println(nav.toString());
    id = 0;
  }
  
  abstract protected void setSummaryDetail(StringBuilder nav);
  
  protected void printTitle(String title) {
    out.println("\n\t\t<h3 id=\""+ id + "\"><a href=\"#top\">^ </a>"+title+"</h3>");
    id ++;
  }

  protected int getIntResult(String query) throws SQLException {
    ResultSet rs = dc.executeQuery(query);
    int n = 0;
    while (rs.next()) {
      n += rs.getInt(1);
    }
    return n;
  }
  
  protected String getStringResult(String query) throws SQLException {
    ResultSet rs = dc.executeQuery(query);
    while (rs.next()) {
      return rs.getString(1);
    }
    return "";
  }

  protected void printIntResult(String title, String query) throws SQLException {

    printTitle(title);
    out.println("\n\t\t<ul>");
    ResultSet rs = dc.executeQuery(query);
    while (rs.next()) {
      out.println("\t\t\t<li>" + rs.getInt(1) + "</li>"); 
    }
    out.println("\t\t</ul>");
  }
   
  protected void printStringResult(String title, String query) throws SQLException {
    printTitle(title);
    ResultSet rs = dc.executeQuery(query);
    while (rs.next()) {
      out.println("<p style='font-weight:bold'>"+rs.getString(1)+"</p>");
    }
    out.println();
  }
  
  protected String parseTimeResult(String h) {
    if (h == null) {
      return "";
    }
    int idx = h.indexOf(":");
    if (idx == -1) {
      return "";
    }
    int hh = Integer.parseInt(h.substring(0, idx));
    int hm = Integer.parseInt(h.substring(idx +1 , idx + 3));
    
    return hh + ":" + hm;
  }
  
  protected void printTimeResult(String title, String query) throws SQLException {
    printTitle(title);
    ResultSet rs = dc.executeQuery(query);
    while (rs.next()) {
      out.println("<p style='font-weight:bold'>"+parseTimeResult(rs.getString(1))+"</p>");
    }
    out.println();
  }
  
  protected void printIntListResult(String title, String query) throws SQLException {

    printTitle(title);
    out.println("\t\t<ul>");
    ResultSet rs = dc.executeQuery(query);
    while (rs.next()) {
      out.println("\n\t\t\t<li>" + rs.getInt(1) + "</li>"); 
    }
    out.println("\n\t\t</ul>");
  }

  protected void printTableIntResult(String title, String query) throws SQLException {

    printTitle(title);
    out.println("\n\t\t<table class='list'>");
    ResultSet rs = dc.executeQuery(query);
    int total = 0;
    while (rs.next()) {
      total += rs.getInt(2);
      out.println("\n\t\t\t<tr><th>" + rs.getString(1) + "</th>");
      out.println("<td>" + rs.getInt(2) + "</td></tr>"); 
    }
    out.println("\n\t\t\t<tr><td colspan='2'>Total : " + total +"</td></tr>");
    out.println("\n\t\t</table>");
  }
  
  protected void printTableTimeResult(String title, String query) throws SQLException {
    printTitle(title);
    out.println("\n\t\t<table class='list'>");
    ResultSet rs = dc.executeQuery(query);
    int total = 0;
    while (rs.next()) {
      String h = parseTimeResult(rs.getString(2));
       if (h != null) {
        int t = Integer.parseInt(h.substring(0, h.indexOf(":")));
        total += t;
      }
      out.println("\n\t\t\t<tr><th>" + rs.getString(1) + "</th>");
      out.println("<td>" + h + "</td></tr>");
    }
    out.println("\n\t\t\t<tr><td colspan='2'>Total : " + (total) +"</td></tr>");
    out.println("\n\t\t</table>");
  }

  private void listProStudents(String query) throws SQLException {
    printTitle(MessageUtil.getMessage("statistics.list.of.pro.students"));
    out.println("\t\t<table>");
    ResultSet rs = dc.executeQuery(query);
    int n = 0;
    while (rs.next()) {
      n++;
      out.println("\n\t\t\t<tr><th>" + rs.getInt(1) + "</th>");
      out.println("<td>" + rs.getString(3) + " " + rs.getString(2) + "</td></tr>"); 
    }
     out.println("\n\t\t\t<tr><td colspan='2'>Total : " + n +"</td></tr>");
     out.println("\n\t\t</table>");
  }

  protected String getQuery(String m) throws SQLException {
    if (m.equals("members_without_date_of_birth")) {
      return "SELECT DISTINCT (eleve.idper) FROM commande_cours, commande, eleve "
              + "WHERE commande_cours.datedebut >= '" + start + "' AND commande_cours.datedebut <= '" + end + "'"
              + "AND commande_cours.idcmd = commande.id "
              + "AND commande.adh = eleve.idper "
//              + "AND to_char(eleve.datenais, 'HH12') = '12' ";
              + " AND (extract(year from age(eleve.datenais)) > 100"
              + " OR extract(year from age(eleve.datenais)) < 1"
              + " OR eleve.datenais is null)";
    }
     if (m.equals("total_number_of_students")) { 
      // on ne tient pas compte des commande_cours à définir
      return "SELECT count(DISTINCT eleve.idper) FROM commande_cours, commande, eleve"
              + " WHERE commande_cours.datedebut >= '" + start + "' AND commande_cours.datedebut <= '" + end + "'"
              + " AND commande_cours.idcmd = commande.id"
              + " AND commande.adh = eleve.idper"
              + " AND commande_cours.debut != '00:00:00'"; // ou commande_cours.idaction = 0
    }
    if (m.equals("list_pro_students")) { 
      return "SELECT DISTINCT(commande.adh), trim(personne.nom), trim(personne.prenom)"
              + " FROM commande, commande_cours, commande_module, module, eleve, personne"
              + " WHERE commande_cours.module = commande_module.id"
              + " AND commande_module.module = module.id"
              + " AND commande_cours.idcmd = commande.id"
              + " AND eleve.idper = commande.adh"
              + " AND eleve.idper = personne.id"
              + " AND commande_cours.datedebut BETWEEN '" + start + "' AND '" + end + "'"
              + " AND module.code LIKE 'P%'";
    }
    if (m.equals("students_by_location")) { 
      return "SELECT adresse.ville, count(distinct eleve.idper) FROM commande_cours,commande, eleve, adresse"
              + " WHERE commande_cours.datedebut >= '" + start + "' AND commande_cours.datedebut <= '" + end + "'"
              + " AND commande_cours.idcmd = commande.id and commande.adh = eleve.idper"
              //+ " AND to_char(eleve.datenais, 'HH12') = '12'"
              + " AND adresse.idper = eleve.payeur"
              + " GROUP BY adresse.ville";
    }
     if (m.equals("groups_with_rehearsal")) { 
      return "SELECT count(DISTINCT planning.idper) FROM planning, groupe"
              + " WHERE planning.idper = groupe.id"
              + " AND planning.jour BETWEEN '" + start + "' AND '" + end + "'"
              + " AND planning.ptype = " + Schedule.GROUP_SCHEDULE
              + " AND planning.lieux <> 8"; // musiques tangentes seulement
    }

    if (m.equals("members_with_rehearsal")) { 
      return "SELECT count(DISTINCT planning.idper) FROM planning, personne"
              + " WHERE planning.idper = personne.id"
              + " AND jour BETWEEN '" + start + "' AND '" + end + "'"
              + " AND planning.ptype = " + Schedule.MEMBER_SCHEDULE
              + " AND planning.lieux <> 8"; // musiques tangentes seulement
    }

    if (m.equals("hours_of_pro_lessons")) {
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
    
    if (m.equals("hours_of_collective_pro_lessons")) {
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
    if (m.equals("hours_of_private_pro_lessons")) {    
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
    
    if (m.equals("hours_teacher_of_collective_lessons")) {
      String query = "SELECT sum(duree) FROM("
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
      return query;
    }
    
    return null;

  }
  
  protected String getQuery(String m, Object a1, Object a2) {
    if ("number_of_amateurs".equals(m)) { 
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

    if ("number_of_pros".equals(m)) {
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
    if ("students_by_instrument".equals(m)) {
      String query = "SELECT cours.titre, count(distinct plage.adherent) FROM plage, planning, cours, action, salle"
              + " WHERE plage.idplanning = planning.id"
              + " AND planning.jour BETWEEN '" + start + "' AND '" + end + "'"
              + " AND plage.debut >= planning.debut"
              + " AND plage.fin <= planning.fin"
              + " AND planning.action = action.id"
              + " AND action.cours = cours.id"
              + " AND planning.ptype = " + Schedule.COURSE_SCHEDULE
              + " AND planning.lieux = salle.id"
              + " AND cours.collectif = " + a1;
      if ((Integer) a2 > 0) {
        query += " AND salle.etablissement = " + a2;
      }
      query += " GROUP BY cours.titre";
      return query;
    }

    
    if ("hours_of_rehearsal".equals(m)) {
//      return "SELECT extract(hour FROM sum(planning.fin - planning.debut)) FROM planning"
        return "SELECT sum(planning.fin - planning.debut) FROM planning"
              + " WHERE planning.jour BETWEEN '" + start + "' AND '" + end + "'"
              + " AND planning.ptype = " + a2;
    }
    
    if (m.equals("hours_of_lessons")) {
//      return "SELECT sum(duree) FROM "
//              + "(SELECT DISTINCT plage.adherent, sum(plage.fin - plage.debut) AS duree"
//              + " FROM plage, planning, action, cours, commande, commande_cours, commande_module, module"
//              + " WHERE plage.idplanning = planning.id"
//              + " AND planning.jour BETWEEN '" + start + "' AND  '" + end + "'"
//              + " AND planning.action = action.id"
//              + " AND action.cours = cours.id"
//              + " AND cours.collectif = " + a1
//              + " AND plage.adherent = commande.adh"
//              + " AND commande.id = commande_cours.idcmd"
//              + " AND commande_cours.datedebut BETWEEN '" + start + "' AND  '" + end + "'"
//              + " AND commande_cours.idaction = planning.action"
//              + " AND commande_cours.module = commande_module.id"
//              + " AND commande_module.module = module.id"
//              + " AND module.code LIKE '" + a2 + "%'"
//              + " GROUP BY plage.adherent) AS t1";
			return "SELECT sum(duree) FROM("
				+ "SELECT plage.idplanning, sum(plage.fin - plage.debut) AS duree"
				+ " FROM plage, planning p, action, cours"
				+ " WHERE plage.idplanning = p.id"
				+ " AND p.jour BETWEEN '" + start + "' AND  '" + end + "'"
				+ " AND p.action = action.id"
				+ " AND p.ptype = " + Schedule.COURSE_SCHEDULE
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
		
    if (m.equals("hours_of_teacher_lessons")) {
      String query = "SELECT sum(duree) FROM("
              + "SELECT distinct p1.id, sum(p1.fin - p1.debut) as duree"
              + " FROM planning p1, action, cours"
              + " WHERE p1.jour BETWEEN '" + start + "' AND  '" + end + "'"
							+ " AND p1.ptype = " + Schedule.COURSE_SCHEDULE
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
      return query;
    }
    return null;
  }

  public void close() {
    if (out != null) {
      out.close();
    }
  }

  private String getMethodName() {
    return getClass().getEnclosingMethod().getName();
  }
}

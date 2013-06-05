/*
 * @(#)Statistics.java	2.8.h 03/06/13
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
 * @version 2.8.h
 * @since 2.6.a 09/10/12
 */
public class Statistics
{

  protected DataCache dataCache;
  protected DataConnection dc;
  protected PrintWriter out;
  private static int year;
  protected DateFr start;
  protected DateFr end;
  protected static Integer MEMBERSHIP_ACCOUNT;
  protected List<Establishment> estabList;

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
    String path = ConfigUtil.getExportPath(dc) + FileUtil.FILE_SEPARATOR + "stats.txt";
    out = new PrintWriter(new FileWriter(path));
    Preference p = AccountPrefIO.find(AccountPrefIO.MEMBER_KEY_PREF, dc);
    MEMBERSHIP_ACCOUNT = (Integer) p.getValues()[0];
  }

  
  public void makeStats() throws SQLException {
    header();
    printIntListResult(MessageUtil.getMessage("statistics.members.without.date.of.birth"), getQuery("members_without_date_of_birth"));
    separate();
    printIntResult(MessageUtil.getMessage("statistics.number.of.students"), getQuery("total_number_of_students"));
    separate();
    List<AgeRange> ages = dataCache.getList(Model.AgeRange).getData();
    for (AgeRange a : ages) {
      if (!a.getCode().equals("-")) {
        printIntResult(
                MessageUtil.getMessage("statistics.number.of.amateur.students", 
                new Object[] {a.getAgemin(), a.getAgemax()}),
                getQuery("number_of_amateurs", a.getAgemin(), a.getAgemax()));
      }
    }
    separate();
    
    for (AgeRange a : ages) {
      if (!a.getCode().equals("-")) {
        printIntResult(
                MessageUtil.getMessage("statistics.number.of.pro.students",
                new Object[] {a.getAgemin(), a.getAgemax()}),
                getQuery("number_of_pros", a.getAgemin(), a.getAgemax()));
      }
    }
    listProStudents(getQuery("list_pro_students"));
    separate();
    printListResult(MessageUtil.getMessage("statistics.city.distribution"), getQuery("students_by_location"));
    
    separate();
    printIntResult(MessageUtil.getMessage("statistics.number.of.bands.with.rehearsal"), getQuery("groups_with_rehearsal"));
    printIntResult(MessageUtil.getMessage("statistics.number.of.members.with.rehearsal"), getQuery("members_with_rehearsal"));
    printIntResult(MessageUtil.getMessage("statistics.number.of.band.rehearsal.hours"), getQuery("hours_of_rehearsal", null, Schedule.GROUP_SCHEDULE));
    printIntResult(MessageUtil.getMessage("statistics.number.of.member.rehearsal.hours"), getQuery("hours_of_rehearsal", null, Schedule.MEMBER_SCHEDULE));

  }
  
  protected void separate() {
    out.println("================================================================================");
  }
  
  protected void header() {
    separate();
    out.println("STATISTIQUES " + ConfigUtil.getConf(ConfigKey.ORGANIZATION_NAME.getKey(), dc) + " — PÉRIODE du " + start + " au " + end);
    separate();
    out.println();
  }
  
  protected void footer() {
    out.println();
    separate();
    out.println("Généré le " + new DateFr(new Date()) + " par " + dataCache.getUser().getFirstnameName());
    separate();
  }

  protected int getIntResult(String query) throws SQLException {
    ResultSet rs = dc.executeQuery(query);
    int n = 0;
    while (rs.next()) {
      n += rs.getInt(1);
    }
    return n;
  }

  protected void printIntResult(String title, String query) throws SQLException {
    out.print(title + " : ");
    ResultSet rs = dc.executeQuery(query);
    while (rs.next()) {
//      out.println("\t" + rs.getInt(1));
      out.println(rs.getInt(1));
    }
    out.println();
  }
  
  protected void printIntListResult(String title, String query) throws SQLException {
    out.println(title + " :");
    ResultSet rs = dc.executeQuery(query);
    while (rs.next()) {
      out.println("\t" + rs.getInt(1));
    }
    out.println();
  }

  protected void printListResult(String title, String query) throws SQLException {
    out.println(title);
    ResultSet rs = dc.executeQuery(query);
    int total = 0;
    while (rs.next()) {
      total += rs.getInt(2);
      out.print("\t" + rs.getString(1));
      out.println(": " + rs.getInt(2));
    }
    out.println("Total : " + total);
    out.println();
  }

  private void listProStudents(String query) throws SQLException {

    out.println("Liste des élèves en formation professionnelle");
    ResultSet rs = dc.executeQuery(query);
    int n = 0;
    while (rs.next()) {
      n++;
      out.print("\t" + rs.getInt(1));
      out.print(": " + rs.getString(2));
      out.println(", " + rs.getString(3));
    }
    out.println("Total : " + n);
    out.println();
  }

  protected String getQuery(String m) throws SQLException {
    if (m.equals("members_without_date_of_birth")) {
      return "SELECT DISTINCT (eleve.idper) FROM commande_cours, commande, eleve "
              + "WHERE commande_cours.datedebut >= '" + start + "' AND commande_cours.datedebut <= '" + end + "'"
              + "AND commande_cours.idcmd = commande.id "
              + "AND commande.adh = eleve.idper "
//              + "AND to_char(eleve.datenais, 'HH12') = '12' ";
              + " AND (extract(year from age(datenais)) > 100 OR extract(year from age(datenais)) < 1 OR datenais is null)";
    }
     if (m.equals("total_number_of_students")) { 
      // on ne tient pas compte des commande_cours à définir
      return "SELECT count(DISTINCT eleve.idper) FROM commande_cours, commande, eleve"
              + " WHERE datedebut >= '" + start + "' AND datedebut <= '" + end + "'"
              + " AND commande_cours.idcmd = commande.id AND commande.adh = eleve.idper and commande_cours.debut != '00:00:00'";
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
              + " WHERE datedebut >= '" + start + "' AND datedebut <= '" + end + "'"
              + " AND commande_cours.idcmd = commande.id and commande.adh = eleve.idper"
              //+ " AND to_char(eleve.datenais, 'HH12') = '12'"
              + " AND adresse.idper = eleve.payeur"
              + " GROUP BY adresse.ville";
    }
     if (m.equals("groups_with_rehearsal")) { 
      return "SELECT count(DISTINCT planning.idper) FROM planning, groupe"
              + " WHERE planning.idper = groupe.id"
              + " AND jour BETWEEN '" + start + "' AND '" + end + "'"
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
              + "(SELECT distinct plage.adherent, extract(hour FROM sum(plage.fin - plage.debut)) AS duree FROM plage, planning"
              + " WHERE plage.idplanning = planning.id"
              + " AND planning.jour BETWEEN '" + start + "' AND  '" + end + "'"
              + " AND plage.adherent IN ("
              + " SELECT distinct adh FROM commande_cours, commande_module, module, commande"
              + " WHERE commande_cours.datedebut >= '" + start + "' AND commande_cours.datedebut <= '" + end + "'"
              + " AND commande_cours.module = commande_module.id"
              + " AND commande_module.module = module.id"
              + " AND commande.id = commande_cours.idcmd"
              + " AND module.code LIKE 'P%') GROUP BY plage.adherent) AS t1";
    }
    if (m.equals("hours_of_collective_pro_lessons")) {
      return "SELECT sum(duree) FROM "
              + "(SELECT distinct plage.adherent, extract(hour FROM sum(plage.fin - plage.debut)) AS duree FROM plage, planning, action, cours"
              + " WHERE plage.idplanning = planning.id"
              + " AND planning.jour BETWEEN '" + start + "' AND  '" + end + "'"
              + " AND planning.action = action.id AND action.cours = cours.id AND cours.collectif = 't'"
              + " AND plage.adherent IN ("
              + " SELECT distinct adh FROM commande, commande_cours, commande_module, module"
              + " WHERE commande_cours.datedebut >= '" + start + "' AND commande_cours.datedebut <= '" + end + "'"
              + " AND commande_cours.idcmd = commande.id"
              + " AND commande_cours.module = commande_module.id"
              + " AND commande_module.module = module.id"
              + " AND module.code LIKE 'P%') GROUP BY plage.adherent) AS t1";
    }
    if (m.equals("hours_of_private_pro_lessons")) {
      String query = "SELECT sum(duree) FROM "
              + "(SELECT distinct plage.adherent, extract(hour FROM sum(plage.fin - plage.debut)) AS duree FROM plage, planning, action, cours"
              + " WHERE plage.idplanning = planning.id"
              + " AND planning.jour BETWEEN '" + start + "' AND  '" + end + "'"
              + " AND planning.action = action.id and action.cours = cours.id and cours.collectif = 'f'"
              + " AND plage.adherent IN ("
              + " SELECT distinct adh FROM commande, commande_cours, commande_module, module"
              + " WHERE commande.id = commande_cours.idcmd"
              + " AND commande_cours.datedebut >= '" + start + "' AND commande_cours.datedebut <= '" + end + "'"             
              + " AND commande_cours.module = commande_module.id"
              + " AND commande_module.module = module.id"
              + " AND module.code LIKE 'P%') GROUP BY plage.adherent) AS t1";
      System.out.println(query);
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
              + " AND extract(year from age(commande_cours.datedebut,datenais)) >= " + a1
              + " AND extract(year from age(commande_cours.datedebut,datenais)) <= " + a2 // correction agemax inclus
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
      return "SELECT extract(hour FROM sum(planning.fin - planning.debut)) FROM planning"
              + " WHERE planning.jour BETWEEN '" + start + "' AND '" + end + "'"
              + " AND planning.ptype = " + a2;
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

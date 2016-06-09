/*
 * @(#)StatisticsPluginMusichalle.java	2.10.0 08/06/2016
 *
 * Copyright (c) 1999-2016 Musiques Tangentes. All Rights Reserved.
 *
 */
package net.algem.plugins;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import net.algem.edition.StatElement;
import net.algem.edition.Statistics;
import net.algem.planning.Schedule;
import net.algem.util.MessageUtil;

/**
 * Data extraction class for Music Halle's statistics.
 * (Algem à partir de la version 2.10.0)
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.10.0
 * @since 1.0.0 11/10/12
 */
public class StatisticsPluginMusichalle
  extends Statistics {

  private static final int STUDIO = 8;
  private static final int LEISURE_STATUS = 1;
  private static final int PRO_STATUS = 2;
  private static final int MEMBERSHIP_PRICE = 1000;
  private static final String MEMBERSHIP_ACCOUNTS = "19,20,21,22";

  public StatisticsPluginMusichalle() {
  }

  @Override
  public void setStats() {
    statList = new ArrayList<StatElement>();
    statList.add(new StatElement(1, MessageUtil.getMessage("statistics.members.without.date.of.birth"), true));
    statList.add(new StatElement(2, MessageUtil.getMessage("statistics.number.of.students"), true));
    statList.add(new StatElement(3, MessageUtil.getMessage("statistics.distribution.between.amateurs.pros"), true));
    statList.add(new StatElement(4, MessageUtil.getMessage("statistics.list.of.pro.students"), true));
    statList.add(new StatElement(5, MessageUtil.getMessage("statistics.city.distribution"), true));
    statList.add(new StatElement(6, MessageUtil.getMessage("statistics.number.of.hours.of.rehearsal"), true));
    statList.add(new StatElement(7, MessageUtil.getMessage("statistics.number.of.rehearsing.people"), true));
    statList.add(new StatElement(8, "Payeurs sans adresse", true));
    statList.add(new StatElement(9, "Adhérents débiteurs", true));
    statList.add(new StatElement(10, "Nombre d'adhérents", true));
    statList.add(new StatElement(11, "Nombre de participants par module", true));
    statList.add(new StatElement(12, "Nombre d'heures par module", true));
    statList.add(new StatElement(13, "Nombre d'heures pro", true));
    statList.add(new StatElement(14, "Nombre d'heures de cours (tous modules confondus)", true));
    statList.add(new StatElement(15, "Nombre d'heures de cours individuels (CYCLE COURT) par activité", true));
    statList.add(new StatElement(16, "Nombre d'heures de cours individuels (CYCLE LONG) par activité", true));
    statList.add(new StatElement(17, "Nombre d'heures de cours collectifs (CYCLE COURT) par activité", true));
    statList.add(new StatElement(18, "Nombre d'heures de cours collectifs (CYCLE LONG) par activité", true));
    statList.add(new StatElement(19, "Répartition des adhérents par catégorie professionnelle", true));
    statList.add(new StatElement(20, "Modules par adhérent", true));
  }

  @Override
  public void makeStats() throws SQLException {
    super.makeStats();
    out.println("<h2>©  STATISTIQUES PERSONNALISÉES  ©</h2>");
    for (StatElement entry : statList) {
      switch (entry.getKey()) {
        case 8:
          printTitle(entry.getLabel());
          listPersons(getQuery("payers_without_address"));
          break;
        case 9:
          printTitle(entry.getLabel());
          listPersons(getQuery("debtors"));
          break;
        case 10:
          printTitle(entry.getLabel());
          out.println("\n\t\t<table>");
          out.println("\n\t\t<tr><th>Hommes</th><td>" + getIntResult(getQuery("number_of_men_members")) + "</td></tr>");
          out.println("\n\t\t<tr><th>Femmes</th><td>" + getIntResult(getQuery("number_of_women_members")) + "</td></tr>");
          out.println("\n\t\t<tr><th>Total</th><td>" + getIntResult(getQuery("total_number_of_members")) + "</td></tr>");
          out.println("\n\t\t</table>");
          break;
        case 11:
          // CALCUL DU NOMBRE DE PARTICIPANTS
          printTitle(entry.getLabel());
          out.println("\n\t\t<table>");
          int mod1 = getIntResult(getQueryInd(7));
          int mod2 = getIntResult(getQueryInd(40));
          int mod3 = getIntResult(getQueryInd(1)); // la perte en collectif est compensée ici
          int mod4 = getIntResult(getQueryInd(38));// et ici

          out.print("<tr><th>TECH. INSTRUMENTALE INDIVIDUELLE module 60 € (modules 7, 40 | 1, 38)</th>");
          out.println("<td>" + (mod1 + mod2 + mod3 + mod4) + "</td></tr>");

          mod1 = getIntResult(getQueryInd(28));
          mod2 = getIntResult(getQueryInd(37));
          mod3 = getIntResult(getQueryInd(39));
          mod4 = getIntResult(getQueryInd(41));
          out.print("<tr><th>TECH. INSTRUMENTALE INDIVIDUELLE module 35 € (modules 28, 37, 39, 41)</th>");
          out.println("<td>" + (mod1 + mod2 + mod3 + mod4) + "</td></tr>");

          mod1 = getIntResult(getQueryCo(1));
          mod2 = getIntResult(getQueryCo(38));
          mod3 = getIntResult(getQueryCo(40)); // la perte en collectif est compensée ici
          mod4 = getIntResult(getQueryCo(41));// et ici
          out.print("<tr><th>TECH. INSTRUMENTALE COLLECTIVE module 60 € (modules 40, 41 | 1, 38)</th>");
          out.println("<td>" + (mod1 + mod2 + mod3 + mod4) + "</td></tr>");

          mod1 = getIntResult(getQueryCo(28));
          mod2 = getIntResult(getQueryCo(37));
          mod3 = getIntResult(getQueryCo(39));
          mod4 = getIntResult(getQueryCo(41));
          out.print("<th>TECH. INSTRUMENTALE COLLECTIVE module 35 € (modules  28, 37, 39, 41)</th>");
          out.println("<td>" + (mod1 + mod2 + mod3 + mod4) + "</td></tr>");

          mod1 = getIntResult(getQuery(21));
          mod2 = getIntResult(getQuery(43));
          out.print("<th>ATELIERS 2H module 60 € (modules 21, 43)</th>");
          out.println("<td>" + (mod1 + mod2) + "</td></tr>");

          mod1 = getIntResult(getQuery(27));
          mod2 = getIntResult(getQuery(42));
          mod3 = getIntResult(getQuery(44));
          out.print("<th>ATELIERS 2H module 35 € (modules 27, 42, 44)</th>");
          out.println("<td>" + (mod1 + mod2 + mod3) + "</td></tr>");

          mod1 = getIntResult(getQuery(25));
          out.print("<th>FM module 35 € (modules 25)</th>");
          out.println("<td>" + mod1 + "</td></tr>");

          mod1 = getIntResult(getQuery(24));
          out.print("<th>FM module 10 € (modules 24)</th>");
          out.println("<td>" + mod1 + "</td></tr>");

          mod1 = getIntResult(getQuery(22));
          out.print("<th>ATELIER ORCHESTRE module 35 € (modules 22)</th>");
          out.println("<td>" + mod1 + "</td></tr>");

          mod1 = getIntResult(getQuery(23));
          mod2 = getIntResult(getQuery(45));
          mod3 = getIntResult(getQuery(46));
          mod4 = getIntResult(getQuery(47));
          out.print("<th>ATELIER ORCHESTRE module 25 € (modules 23, 45, 46, 47)</th>");
          out.println("<td>" + (mod1 + mod2 + mod3 + mod4) + "</td></tr>");

          mod1 = getIntResult(getQuery(48));
          out.print("<th>HARMONIE module 10 € (modules 48)</th>");
          out.println("<td>" + mod1 + "</td></tr>");

          mod1 = getIntResult(getQuery(20));
          out.print("<th>TECHNIQUE VOCALE module 80 € (modules 20)</th>");
          out.println("<td>" + mod1 + "</td></tr>");

          mod1 = getIntResult(getQuery(49));
          out.print("<th>CYCLE 1 (modules 49)</th>");
          out.println("<td>" + mod1 + "</td></tr>");

          mod1 = getIntResult(getQuery(51));
          out.print("<th>CYCLE 2 (modules 51)</th>");
          out.println("<td>" + mod1 + "</td></tr>");
          out.println("\n\t\t</table>");
          break;
        case 12:
          separate();
          // CALCUL DU NOMBRE D'HEURES
          printTitle("Nombre d'heures par module");
          mod1 = getIntResult(getHourQueryInd(7));
          mod2 = getIntResult(getHourQueryInd(40));
          mod3 = getIntResult(getHourQueryInd(1)); // la perte en collectif est compensée ici
          mod4 = getIntResult(getHourQueryInd(38));// et ici

          out.println("\n\t\t<table>");
          out.print("<tr><th>TECH. INSTRUMENTALE INDIVIDUELLE module 60 € (modules 7, 40 | 1, 38)</th>");
          out.println("<td>" + (mod1 + mod2 + mod3 + mod4) + "</td></tr>");

          mod1 = getIntResult(getHourQueryInd(39));
          mod2 = getIntResult(getHourQueryInd(28));
          mod3 = getIntResult(getHourQueryInd(37));
          mod4 = getIntResult(getHourQueryInd(41));
          out.print("<tr><th>TECH. INSTRUMENTALE INDIVIDUELLE module 35 € (modules 39, 41 | 28, 37)</th>");
          out.println("<td>" + (mod1 + mod2 + mod3 + mod4) + "</td></tr>");

          mod1 = getIntResult(getHourQueryCo(1));
          mod2 = getIntResult(getHourQueryCo(38));
          mod3 = getIntResult(getHourQueryCo(7));
          mod4 = getIntResult(getHourQueryCo(40)); // la perte en collectif est compensée ici
          out.print("<tr><th>TECH. INSTRUMENTALE COLLECTIVE module 60 € (modules 7, 40 | 1, 38)</th>");
          out.println("<td>" + (mod1 + mod2 + mod3 + mod4) + "</td></tr>");
          out.println();

          mod1 = getIntResult(getHourQueryCo(28));
          mod2 = getIntResult(getHourQueryCo(37));
          mod3 = getIntResult(getHourQueryCo(39));
          mod4 = getIntResult(getHourQueryCo(41));
          out.print("<tr><th>TECH. INSTRUMENTALE COLLECTIVE module 35 € (modules 39, 41 | 28, 37)</th>");
          out.println("<td>" + (mod1 + mod2 + mod3 + mod4) + "</td></tr>");

          mod1 = getIntResult(getHourQuery(21, 43));
          out.print("<th>ATELIERS 2H module 60 € (modules 21, 43)</th>");
          out.println("<td>" + mod1 + "</td></tr>");

          mod1 = getIntResult(getHourQuery(27, 42, 44));
          out.print("<th>ATELIERS 2H module 35 € (modules 27, 42, 44)</th>");
          out.println("<td>" + mod1 + "</td></tr>");

          mod1 = getIntResult(getHourQuery(25));
          out.print("<th>FM module 35 € (modules 25)</th>");
          out.println("<td>" + mod1 + "</td></tr>");

          mod1 = getIntResult(getHourQuery(24));
          out.print("<th>FM module 10 € (modules 24)</th>");
          out.println("<td>" + mod1 + "</td></tr>");

          mod1 = getIntResult(getHourQuery(22));
          out.print("<th>ATELIER ORCHESTRE module 35 € (modules 22)</th>");
          out.println("<td>" + mod1 + "</td></tr>");

          mod1 = getIntResult(getHourQuery(23, 45, 46, 47));
          out.print("<th>ATELIER ORCHESTRE module 25 € (modules 23, 45, 46, 47)</th>");
          out.println("<td>" + mod1 + "</td></tr>");

          mod1 = getIntResult(getHourQuery(48));
          out.print("<th>HARMONIE module 10 € (modules 48)</th>");
          out.println("<td>" + mod1 + "</td></tr>");

          mod1 = getIntResult(getHourQuery(20));
          out.print("<th>TECHNIQUE VOCALE module 80 € (modules 20)</th>");
          out.println("<td>" + mod1 + "</td></tr>");

          mod1 = getIntResult(getHourQuery(49));
          out.print("<th>CYCLE 1 (modules 49)</th>");
          out.println("<td>" + mod1 + "</td></tr>");

          mod1 = getIntResult(getHourQuery(51));
          out.print("<th>CYCLE 2 (modules 51)</th>");
          out.println("<td>" + mod1 + "</td></tr>");
          out.println("\n\t\t</table>");
          separate();
          break;
        case 13:

          int nb_hours_individual_pro = getIntResult(getQuery("hours_of_private_lessons", PRO_STATUS, null));
          int nb_hours_collective_pro = getIntResult(getQuery("hours_of_collective_lessons", PRO_STATUS, null));

          printTitle(entry.getLabel());
          out.println("\n\t\t<table>");
          out.print("<tr><th>Individuelles (<= à 2 élèves par plage)</th>");
          out.println("<td>" + nb_hours_individual_pro + "</td></tr>");
          out.print("<tr><th>Collectives (> à 2 élèves par plage)</th>");
          out.println("<td>" + nb_hours_collective_pro + "</td></tr>");
          out.println("<tr><th>Total</th><td>" + (nb_hours_individual_pro + nb_hours_collective_pro) + "</td></tr>");
          out.println("\n\t\t</table>");
          break;
        case 14:

          int nb_hours_individual = getIntResult(getQuery("hours_of_private_lessons", null, null));
          int nb_hours_collective = getIntResult(getQuery("hours_of_collective_lessons", null, null));

          printTitle(entry.getLabel());
          out.println("\n\t\t<table>");
          out.print("<tr><th>Individuelles</th>");
          out.println("<td>" + nb_hours_individual + "</td></tr>");
          out.print("<tr><th>Collectives</th>");
          out.println("<td>" + nb_hours_collective + "</td></tr>");
          out.println("<tr><th>Total</th><td>" + (nb_hours_individual + nb_hours_collective) + "</td></tr>");
          out.println("\n\t\t</table>");
          break;

        /*if (estabList.size() > 1) {
         for (Establishment e : estabList) {
         printIntResult("Nombre d'heures de cours individuels sur " + e.getName(), getQuery("hours_of_private_lessons", null, e.getId()));
         }
         }*/
        case 15:
          printTitle(entry.getLabel());
          printTableTimeResult(getQuery("hours_of_private_lessons_by_activity", LEISURE_STATUS, null));
          break;
        case 16:
          printTitle(entry.getLabel());
          printTableTimeResult(getQuery("hours_of_private_lessons_by_activity", PRO_STATUS, null));
          break;
        /*if (estabList.size() > 1) {
         for (Establishment e : estabList) {
         printListResult("Nombre d'heures de cours individuels par activité sur " + e.getName(), getQuery("hours_of_private_lessons_by_activity", null, e.getId()));
         }
         }*/

        /*if (estabList.size() > 1) {
         for (Establishment e : estabList) {
         printIntResult("Nombre d'heures de cours collectifs sur " + e.getName(), getQuery("hours_of_collective_lessons", null, e.getId()));
         }
         }*/
        case 17:
          printTitle(entry.getLabel());
          printTableTimeResult(getQuery("hours_of_collective_lessons_by_activity", LEISURE_STATUS, null));
          break;
        case 18:
          printTitle(entry.getLabel());
          printTableTimeResult(getQuery("hours_of_collective_lessons_by_activity", PRO_STATUS, null));
          break;
        /*if (estabList.size() > 1) {
         for (Establishment e : estabList) {
         printListResult("Nombre d'heures de cours collectifs (CYCLE LONG) par activité sur " + e.getName(), getQuery("hours_of_collective_lessons_by_activity", null, e.getId()));
         }
         }*/
        case 19:
          printTitle(entry.getLabel());
          printTableIntResult(getQuery("members_by_occupational"));
          break;
        case 20:
          printTitle(entry.getLabel());
          printMemberModule(getQuery("modules_by_member"));
          break;
      }
    }
    //printListResult("Situation géographique des adhérents", getQuery("members_by_location"));
//    printIntResult("Nombre d'heures studio", getQuery("hours_of_studio"));
    footer();
  }

  protected void printMemberModule(String query) throws SQLException {
    ResultSet rs = dc.executeQuery(query);
    out.println("\n\t\t<table>");
    out.println("<tr><th>id</th><th>nom</th><th>prenom</th><th>module</th><th>date_inscription</th><th>cours</th><th>date_inscription_cours</th></tr>");
    while (rs.next()) {
      out.print("<tr><td>" + rs.getInt(1) + "</td>");
      out.print("<td>" + rs.getString(2) + "</td>");
      out.print("<td>" + rs.getString(3) + "</td>");
      out.print("<td>" + rs.getString(4) + "</td>");
      out.print("<td>" + rs.getString(5) + "</td>");
      out.print("<td>" + rs.getString(6) + "</td>");
      out.print("<td>" + rs.getString(7));
      out.println("</td></tr>");
    }
    out.println("\n\t\t</table>");
    out.println();
  }

  @Override
  protected String getQuery(String m) throws SQLException {
    if (m.equals("members_without_date_of_birth")
      || m.equals("total_number_of_students")
      || m.equals("list_pro_students")
      || m.equals("students_by_location")
      || m.equals("groups_with_rehearsal")
      || m.equals("members_with_rehearsal")) {
      return super.getQuery(m);
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
        + " commande_module.module = module.id"
        + " AND commande.id = commande_cours.idcmd"
        + " AND module.code LIKE 'P%') GROUP BY plage.adherent) AS t1";
    }
    if (m.equals("hours_of_private_pro_lessons")) {
      return "SELECT sum(duree) FROM "
        + "(SELECT distinct plage.adherent, extract(hour FROM sum(plage.fin - plage.debut)) AS duree FROM plage, planning, action"
        + " WHERE plage.idplanning = planning.id"
        + " AND planning.jour BETWEEN '" + start + "' AND  '" + end + "'"
        + " AND planning.action = action.id AND action.places <= 2"
        + " AND plage.adherent IN ("
        + " SELECT distinct adh FROM commande, commande_cours, commande_module, module"
        + " WHERE commande_cours.datedebut >= '" + start + "' AND commande_cours.datedebut <= '" + end + "'"
        + " AND commande_cours.idcmd = commande.id"
        + " AND commande_cours.module = commande_module.id"
        + " commande_module.module = module.id"
        + " AND module.code LIKE 'P%') GROUP BY plage.adherent) AS t1";
    }
    if (m.equals("hours_of_collective_pro_lessons")) {
      return "SELECT sum(duree) FROM "
        + "(SELECT distinct plage.adherent, extract(hour FROM sum(plage.fin - plage.debut)) AS duree FROM plage, planning, action"
        + " WHERE plage.idplanning = planning.id"
        + " AND planning.jour BETWEEN '" + start + "' AND  '" + end + "'"
        + " AND planning.action = action.id AND action.places > 2"
        + " AND plage.adherent IN ("
        + " SELECT distinct adh FROM commande, commande_cours, commande_module, module"
        + " WHERE commande_cours.datedebut >= '" + start + "' AND commande_cours.datedebut <= '" + end + "'"
        + " AND commande_cours.idcmd = commande.id"
        + " AND commande_cours.module = commande_module.id"
        + " commande_module.module = module.id"
        + " AND module.code LIKE 'P%') GROUP BY plage.adherent) AS t1";
    }
    if (m.equals("payers_without_address")) {
      return "SELECT DISTINCT e.payeur,p.prenom,p.nom FROM echeancier2 e JOIN personne p ON (e.payeur=p.id)"
        + " WHERE e.echeance BETWEEN '" + start + "' AND  '" + end + "'"
        //+ " AND e.montant = " + MEMBERSHIP_PRICE
        + " AND e.compte IN (" + MEMBERSHIP_ACCOUNT + "," + MEMBERSHIP_ACCOUNTS +")"
        + " AND e.payeur NOT IN (SELECT idper FROM adresse) ORDER BY p.nom,p.prenom";
    }
    if (m.equals("debtors")) {
      return "SELECT DISTINCT e.adherent,p.prenom,p.nom FROM echeancier2 e JOIN personne p ON (e.adherent=p.id)"
        + " WHERE e.echeance BETWEEN '" + start + "' AND '" + end + "'"
        //+ " AND e.montant = " + MEMBERSHIP_PRICE
        + " AND e.compte IN (" + MEMBERSHIP_ACCOUNT + "," + MEMBERSHIP_ACCOUNTS +")"
        + " AND e.paye = FALSE"
        + " ORDER BY p.nom,p.prenom";
    }
    if (m.equals("total_number_of_members")) {
      return "SELECT count(DISTINCT adherent) FROM echeancier2"
        + " WHERE echeance BETWEEN '" + start + "' AND '" + end + "'"
        //+ " AND echeancier2.montant = " + MEMBERSHIP_PRICE
        + " AND compte IN (" + MEMBERSHIP_ACCOUNT + "," + MEMBERSHIP_ACCOUNTS +")";
    }
    if (m.equals("number_of_men_members")) {
      return "SELECT count(DISTINCT adherent) FROM echeancier2, personne"
        + " WHERE personne.id = echeancier2.adherent"
        + " AND echeance BETWEEN '" + start + "' AND '" + end + "'"
        //+ " AND echeancier2.montant = " + MEMBERSHIP_PRICE
        + " AND compte IN (" + MEMBERSHIP_ACCOUNT + "," + MEMBERSHIP_ACCOUNTS +")"
        + " AND (trim(personne.civilite) = 'M' OR personne.civilite = '')";
    }
    if (m.equals("number_of_women_members")) {
      return "SELECT count(DISTINCT adherent) FROM echeancier2, personne"
        + " WHERE personne.id = echeancier2.adherent"
        + " AND echeance BETWEEN '" + start + "' AND '" + end + "'"
        //+ " AND echeancier2.montant= " + MEMBERSHIP_PRICE
        + " AND compte IN (" + MEMBERSHIP_ACCOUNT + "," + MEMBERSHIP_ACCOUNTS +")"
        + " AND (trim(personne.civilite) = 'Mme' OR personne.civilite = 'Mlle')";
    }
    if (m.equals("members_by_occupational")) {
      return "SELECT profession, count(DISTINCT adherent) FROM echeancier2,eleve"
        + " WHERE eleve.idper=echeancier2.adherent"
        + " AND echeance BETWEEN '" + start + "' AND '" + end + "'"
        //+ " AND montant = " + MEMBERSHIP_PRICE
        + " AND compte  IN (" + MEMBERSHIP_ACCOUNT + "," + MEMBERSHIP_ACCOUNTS +")"
        + " GROUP BY profession";
    }
    if (m.equals("modules_by_member")) {
      return "SELECT DISTINCT p.id, p.nom, p.prenom, m.titre, to_char(c.creation::date, 'DD-MM-YYYY'), cours.titre, to_char(cc.datedebut::date, 'DD-MM-YYYY')"
        + " FROM personne p, module m, commande c, commande_cours cc, commande_module cm, action a, cours "
        + " WHERE cc.datedebut >= '" + start + "' AND cc.datefin <= '" + end + "'"
        + " AND c.id = cc.idcmd"
        + " AND cc.module = cm.id"
        + " AND cm.module = m.id"
        + " AND cc.idaction = a.id"
        + " AND a.cours = cours.id"
        + " AND c.adh = p.id"
        + " ORDER BY p.nom,p.prenom";
    }

    /*if (m.equals("hours_of_studio")) {
     return "SELECT extract(hour FROM sum(planning.fin - planning.debut)) FROM planning"
     + " WHERE planning.jour >= '" + start + "' AND planning.jour <= '" + end + "'"
     + " AND planning.lieux = " + STUDIO;
     }*/
    //nombre d'élèves ayant payé une formule à 60 € inscrits à un cours individuel (<= 2 personnes par cours)
    return null;
  }

  @Override
  protected String getQuery(String m, Object a1, Object a2) {
    if ("number_of_amateurs".equals(m)
      || "number_of_pros".equals(m)
      || "students_by_instrument".equals(m)
      || "hours_of_rehearsal".equals(m)) {
      return super.getQuery(m, a1, a2);
    }

    if ("hours_of_private_lessons".equals(m)) {
      String query = "SELECT extract(hour FROM sum(p.fin-p.debut))"
        + " FROM(SELECT planning.id,  count(DISTINCT plage.id) AS nb_plages"
        + " FROM planning, plage, action, salle"
        + " WHERE planning.jour >= '" + start + "' AND planning.jour <= '" + end + "'"
        + " AND planning.id = plage.idplanning AND plage.debut >= planning.debut AND plage.fin <= planning.fin"
        + " AND planning.ptype = " + Schedule.COURSE + " AND planning.action = action.id"
        + " AND planning.lieux = salle.id";
      if (a1 != null) {
        query += " AND action.statut = " + a1;
      }
      if (a2 != null) { // par établissement
        query += " AND salle.etablissement = " + a2;
      }

      query += " GROUP BY planning.id) AS t1, planning p WHERE t1.nb_plages <= 2 AND p.id = t1.id";
      return query;
    }

    if ("hours_of_collective_lessons".equals(m)) {
      String query = "SELECT extract(hour FROM sum(p.fin-p.debut))"
        + " FROM(SELECT planning.id,  count(DISTINCT plage.id) AS nb_plages"
        + " FROM planning, plage, action, salle"
        + " WHERE planning.jour  >= '" + start + "' AND planning.jour <= '" + end + "'"
        + " AND planning.id = plage.idplanning AND plage.debut >= planning.debut AND plage.fin <= planning.fin"
        + " AND planning.ptype IN (" + Schedule.COURSE + "," + Schedule.WORKSHOP + "," + Schedule.TRAINING + ")"
        + " AND planning.action = action.id"
        + " AND planning.lieux = salle.id";
      if (a1 != null) {
        query += " AND action.statut = " + a1;
      }
      if (a2 != null) { // par établissement
        query += " AND salle.etablissement = " + a2;
      }

      query += " GROUP BY planning.id) AS t1, planning p WHERE t1.nb_plages > 2 AND p.id = t1.id";

      return query;
    }

    if ("hours_of_private_lessons_by_activity".equals(m)) {

//      String query = "SELECT cours.titre, extract(hour FROM sum(duree)) FROM"
      String query = "SELECT cours.titre, sum(duree) FROM"
        + "(SELECT planning.id AS idplanning, (planning.fin-planning.debut) AS duree, count(DISTINCT plage.id) AS nb_plages FROM planning, plage, action, salle"
        + " WHERE planning.jour >= '" + start + "' AND planning.jour <= '" + end + "'"
        + " AND planning.id = plage.idplanning"
        + " AND plage.debut >= planning.debut AND plage.fin <= planning.fin"
        + " AND planning.ptype = " + Schedule.COURSE
        + " AND planning.action = action.id"
        + " AND planning.lieux = salle.id";

      if (a1 != null) {
        query += " AND action.statut = " + a1; // distinction cycle court, cycle long
      }
      if (a2 != null) { // par établissement
        query += " AND salle.etablissement = " + a2;
      }
      query += " GROUP BY planning.id, duree) AS t1, cours, planning, action"
        + " WHERE nb_plages <= 2"
        + " AND idplanning = planning.id AND planning.action = action.id"
        + " AND action.cours = cours.id"
        + " GROUP BY cours.titre ORDER BY cours.titre";
      return query;

    }

    if ("hours_of_collective_lessons_by_activity".equals(m)) {
//      String query = "SELECT cours.titre, extract(hour FROM sum(duree)) FROM"
      String query = "SELECT cours.titre, sum(duree) FROM"
        + "(SELECT planning.id AS idplanning, (planning.fin-planning.debut) AS duree, count(DISTINCT plage.id) AS nb_plages FROM planning, plage, action, salle"
        + " WHERE planning.jour >= '" + start + "' AND planning.jour <= '" + end + "'"
        + " AND planning.id = plage.idplanning"
        + " AND plage.debut >= planning.debut AND plage.fin <= planning.fin"
        + " AND planning.ptype IN (" + Schedule.COURSE + "," + Schedule.WORKSHOP + "," + Schedule.TRAINING + ")"
        + " AND planning.action = action.id"
        + " AND planning.lieux = salle.id";

      if (a1 != null) {
        query += " AND action.statut = " + a1;
      }
      if (a2 != null) { // par établissement
        query += " AND salle.etablissement = " + a2;
      }
      query += " GROUP BY planning.id, duree) AS t1, cours, planning, action"
        + " WHERE nb_plages > 2"
        + " AND idplanning = planning.id AND planning.action = action.id"
        + " AND action.cours = cours.id"
        + " GROUP BY cours.titre ORDER BY cours.titre";
      return query;
    }
    return null;
  }

  protected String getQuery(int module) {
    return "SELECT count(DISTINCT c.adh)"
      + " FROM  planning p, plage pl, commande_cours cc, commande_module cm, commande c"
      + " WHERE p.jour BETWEEN '" + start + "' AND '" + end + "'"
      + " AND p.id = pl.idplanning"
      + " AND p.action = cc.idaction"
      + " AND cc.idcmd = c.id"
      + " AND cc.module = cm.id"
      + " AND cm.module = " + module
      + " AND c.adh = pl.adherent";
  }

  protected String getQueryInd(int module) {
    String query = "SELECT count(DISTINCT pl.adherent) FROM"
      + "(SELECT DISTINCT pl.id AS plage FROM"
      + " (SELECT p.id AS idplanning, count(DISTINCT pl.id) AS nb_plages FROM planning p, plage pl"
      + " WHERE p.jour BETWEEN '" + start + "' AND '" + end + "' AND p.id = pl.idplanning GROUP BY p.id) AS t1, plage pl"
      + " WHERE t1.nb_plages <= 2 AND t1.idplanning = pl.idplanning)"
      + " AS t2, planning p, plage pl, commande_cours cc, commande_module cm, commande c"
      + " WHERE pl.id = t2.plage"
      + " AND pl.idplanning = p.id"
      + " AND p.action = cc.idaction"
      + " AND cc.idcmd = c.id"
      + " AND cc.module = cm.id"
      + " AND cm.module = " + module
      + " AND c.adh = pl.adherent";
    return query;
  }

  protected String getQueryCo(int module) {
    String query = "SELECT count(DISTINCT pl.adherent) FROM"
      + "(SELECT DISTINCT pl.id AS plage FROM"
      + " (SELECT p.id AS idplanning, count(DISTINCT pl.id) AS nb_plages FROM planning p, plage pl"
      + " WHERE p.jour BETWEEN '" + start + "' AND '" + end + "' AND p.id = pl.idplanning GROUP BY p.id) AS t1, plage pl"
      + " WHERE t1.nb_plages > 2 AND t1.idplanning = pl.idplanning)"
      + " AS t2, planning p, plage pl, commande_cours cc, commande_module cm, commande c"
      + " WHERE pl.id = t2.plage"
      + " AND pl.idplanning = p.id"
      + " AND p.action = cc.idaction"
      + " AND cc.idcmd = c.id"
      + " AND cc.module = cm.id"
      + " AND cm.module = " + module
      + " AND c.adh = pl.adherent";
    return query;
  }

  protected String getQueryInd(int... module) {
    String query
      = "SELECT count(DISTINCT pl.adherent)"
      + " FROM (SELECT p.id AS pid, count(DISTINCT pl.id) AS nb_plages FROM planning p, plage pl, commande_cours cc, commande_module cm, commande c"
      + " WHERE p.jour BETWEEN '" + start + "' AND '" + end + "'"
      + " AND p.id = pl.idplanning"
      + " AND p.action = cc.idaction"
      + " AND cc.idcmd = c.id AND c.adh = pl.adherent"
      + " AND cc.module = cm.id"
      + " AND (cm.module = " + module[0];
    for (int i = 1; i < module.length; i++) {
      query += " OR cm.module = " + module[i];
    }

    query += ") GROUP BY p.id) as t1, plage pl"
      + " WHERE t1.nb_plages <= 2 AND pid = pl.idplanning";
    return query;
  }

  protected String getHourQuery(int module) {
    String query = "SELECT extract(hour FROM sum(p.fin-p.debut))"
      + " FROM (SELECT DISTINCT planning.id, planning.debut, planning.fin"
      + " FROM planning, action, commande_cours, commande_module, plage pl"
      + " WHERE planning.jour >= '" + start + "' AND planning.jour <= '" + end + "'"
      + " AND planning.ptype IN (" + Schedule.COURSE + "," + Schedule.WORKSHOP + "," + Schedule.TRAINING + ")"
      + " AND pl.idplanning = planning.id"
      + " AND planning.action = action.id"
      + " AND commande_cours.idaction = action.id"
      + " AND commande_cours.module = commande_module.id"
      + " AND commande_module.module = " + module + ") as p";
    return query;
  }

  protected String getHourQuery(int... module) {
    String query = "SELECT extract(hour FROM sum(p.fin-p.debut))"
      + " FROM (SELECT DISTINCT planning.id, planning.debut, planning.fin"
      + " FROM planning, action, commande_cours cc, commande_module cm, plage pl"
      + " WHERE planning.jour >= '" + start + "' AND planning.jour <= '" + end + "'"
      + " AND planning.ptype IN (" + Schedule.COURSE + "," + Schedule.WORKSHOP + "," + Schedule.TRAINING + ")"
      + " AND pl.idplanning = planning.id"
      + " AND planning.action = action.id"
      + " AND cc.idaction = action.id"
      + " AND cc.module = cm.id"
      + " AND (cm.module = " + module[0];
    for (int i = 1; i < module.length; i++) {
      query += " OR cm.module = " + module[i];
    }

    query += ")) as p";

    return query;
  }

  protected String getHourQueryInd(int module) {
    String query = "SELECT extract(hour FROM sum(p.fin-p.debut)) AS heures FROM"
      + " (SELECT DISTINCT pl.id AS plage FROM"
      + " (SELECT p.id AS idplanning, count(DISTINCT pl.id) AS nb_plages FROM planning p, plage pl"
      + " WHERE p.jour BETWEEN '" + start + "' AND '" + end + "' AND p.id = pl.idplanning group by p.id) as t1, plage pl"
      + " WHERE t1.nb_plages <= 2 AND t1.idplanning = pl.idplanning)"
      + " AS t2, planning p, plage pl, commande_cours cc, commande_module cm, commande c"
      + " WHERE pl.id = t2.plage AND pl.idplanning = p.id"
      + " AND p.action = cc.idaction AND cc.idcmd = c.id"
      + " AND cc.module = cm.id"
      + " AND cm.module = " + module
      + " AND c.adh = pl.adherent";
    return query;
  }

  protected String getHourQueryCo(int module) {
    String query = "SELECT extract(hour FROM sum(t3.fin-t3.debut)) AS heures FROM"
      + " (select distinct p.* from"
      + " (SELECT DISTINCT pl.id AS plage FROM"
      + " (SELECT p.id AS idplanning, count(DISTINCT pl.id) AS nb_plages FROM planning p, plage pl"
      + " WHERE p.jour BETWEEN '" + start + "' AND '" + end + "' AND p.id = pl.idplanning group by p.id) as t1, plage pl"
      + " WHERE t1.nb_plages > 2 AND t1.idplanning = pl.idplanning)"
      + " AS t2, planning p, plage pl, commande_cours cc, commande_module cm, commande c"
      + " WHERE pl.id = t2.plage AND pl.idplanning = p.id"
      + " AND p.action = cc.idaction AND cc.idcmd = c.id"
      + " AND cc.module = cm.id"
      + " AND cm.module = " + module
      + " AND c.adh = pl.adherent) as t3 ";
    return query;
  }

}

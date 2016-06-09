/*
 * @(#)StatisticsPlugin.java    2.10.0 08/06/2016
 *
 * Copyright (c) 1999-2016 Musiques Tangentes. All Rights Reserved.
 *
 */
package net.algem.plugins;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import net.algem.edition.StatElement;
import net.algem.edition.Statistics;
import net.algem.edition.StatisticsDefault;
import net.algem.edition.StatisticsFactory;
import net.algem.planning.DateFr;
import net.algem.planning.Hour;
import net.algem.planning.Schedule;
import net.algem.room.Establishment;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;
import net.algem.util.GemLogger;
import net.algem.util.MessageUtil;

/**
 * Statistics file export for Musiques Tangentes.
 * For Algem >= 2.8.v
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.10.0
 * @since 2.6.g 20/11/12
 */
public class StatisticsPlugin
  extends Statistics {

  private static final int PRO_MEMBER_ACCOUNT = 14;
  private static final int STUDIO = 8;
  private static final int MONTANT_ADHESION = 1000;

  public StatisticsPlugin() {
  }

  @Override
  public void makeStats() throws SQLException {
    super.makeStats();

    for (StatElement entry : statList) {
      switch (entry.getKey()) {
        case 8:
          out.println("<h2>©  STATISTIQUES PERSONNALISÉES  ©</h2>");
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
          printTitle(entry.getLabel());
          printTableIntResult(getQuery("students_by_instrument", false, 0));
          if (estabList.size() > 1) {
            for (Establishment e : estabList) {
              if (!isActiveEstab(e.getId())) {
                continue;
              }
              printSecondaryTitle("Répartition des élèves en cours individuels sur " + e.getName());
              printTableIntResult(getQuery("students_by_instrument", false, e.getId()));
            }
          }
          break;
        case 12:
          printTitle(entry.getLabel());
          printTableIntResult(getQuery("students_by_instrument", true, 0));
          for (Establishment e : estabList) {
            if (!isActiveEstab(e.getId())) {
              continue;
            }
            printSecondaryTitle("Répartition des élèves en cours collectifs sur " + e.getName());
            printTableIntResult(getQuery("students_by_instrument", true, e.getId()));
          }
          break;
        case 13:
          printTitle(entry.getLabel());
          printTableIntResult(getQuery("members_by_occupational"));
          break;
        case 14:
          printTitle(entry.getLabel());
          printTableIntResult(getQuery("members_by_location"));
          break;
        case 15:
          printTitle(entry.getLabel());
          String nb_hours_individual = getStringResult(getQuery("hours_of_private_lessons", null, null));
          String nb_hours_collective = getStringResult(getQuery("hours_of_collective_lessons", null, null));

          out.print("\n\t\t<table class='list'>");
          out.print("\n\t\t\t<tr><td>&nbsp;</td>");
          int offset = 0;
          for (Establishment e : estabList) {
            if (e.getId() != 3501 && e.getId() != 21617) {
              continue;
            }
            offset++;
            out.print("<th>" + e.getName() + "</th>");
          }
          out.print("<th>Total</th></tr>\n\t\t\t<tr><th>Cours individuels</th>");

          for (Establishment e : estabList) {
            if (e.getId() != 3501 && e.getId() != 21617) {
              continue;
            }
            String r1 = getStringResult(getQuery("hours_of_private_lessons", null, e.getId()));
            out.print("<td>" + parseTimeResult(r1) + "</td>");

          }
          out.print("<td>" + parseTimeResult(nb_hours_individual) + "</td></tr>\n\t\t\t<tr><th>Cours collectifs</th>");
          for (Establishment e : estabList) {
            if (!isActiveEstab(e.getId())) {
              continue;
            }
            String r2 = getStringResult(getQuery("hours_of_collective_lessons", null, e.getId()));
            out.print("<td>" + parseTimeResult(r2) + "</td>");
          }
          out.print("<td>" + parseTimeResult(nb_hours_collective) + "</td></tr>");

          int hi = Hour.getMinutesFromString(nb_hours_individual);
          int hc = Hour.getMinutesFromString(nb_hours_collective);
          out.print("\n\t\t\t<tr><td colspan = '" + (offset + 2) + "'>" + getTimeFromMinutes(hi + hc) + "</td></tr>\n\t\t</table>");
          break;
        case 16:
          printTitle(entry.getLabel());
          String hl1 = getStringResult(getQuery("hours_of_lessons", false, 'L'));
          String hl2 = getStringResult(getQuery("hours_of_lessons", true, 'L'));
          String hl3 = getStringResult(getQuery("hours_of_teacher_lessons", true, 'L'));

          int htl1 = Hour.getMinutesFromString(hl1);
          int htl2 = Hour.getMinutesFromString(hl2);
          int htl3 = Hour.getMinutesFromString(hl3);

          out.print("\n\t\t<table class='list'>");
          out.print("\n\t\t\t<tr><td>&nbsp;</td><th>Heures élèves</th><th>Heures profs</th></tr>");
          out.print("\n\t\t\t<tr><th>Cours individuels</th><td>" + parseTimeResult(hl1) + "</td><td>" + parseTimeResult(hl1) + "</td></tr>");
          out.print("\n\t\t\t<tr><th>Cours collectifs</th><td>" + parseTimeResult(hl2) + "</td><td>" + parseTimeResult(hl3) + "</td></tr>");
          out.print("\n\t\t\t<tr><th>Total</th><td>" + getTimeFromMinutes(htl1 + htl2) + "</td><td>" + getTimeFromMinutes(htl1 + htl3) + "</td></tr>");
          out.println("\n\t\t</table>");
          break;
        case 17:
          printTitle(entry.getLabel());
          String hp1 = getStringResult(getQuery("hours_of_lessons", false, 'P'));
          String hp2 = getStringResult(getQuery("hours_of_lessons", true, 'P'));
          String hp3 = getStringResult(getQuery("hours_of_teacher_lessons", true, 'P'));

          int htp1 = Hour.getMinutesFromString(hp1);
          int htp2 = Hour.getMinutesFromString(hp2);
          int htp3 = Hour.getMinutesFromString(hp3);

          out.print("\n\t\t<table class='list'>");
          out.print("\n\t\t\t<tr><td>&nbsp;</td><th>Heures élèves</th><th>Heures profs</th></tr>");
          out.print("\n\t\t\t<tr><th>Cours individuels</th><td>" + parseTimeResult(hp1) + "</td><td>" + parseTimeResult(hp1) + "</td></tr>");
          out.print("\n\t\t\t<tr><th>Cours collectifs</th><td>" + parseTimeResult(hp2) + "</td><td>" + parseTimeResult(hp3) + "</td></tr>");
          out.print("\n\t\t\t<tr><th>Total</th><td>" + getTimeFromMinutes(htp1 + htp2) + "</td><td>" + getTimeFromMinutes(htp1 + htp3) + "</td></tr>");
          out.println("\n\t\t</table>");
          break;
        case 18:
          printTitle(entry.getLabel());
          printTableTimeResult(getQuery("hours_of_private_lessons_by_activity", null, null));
          for (Establishment e : estabList) {
            if (!isActiveEstab(e.getId())) {
              continue;
            }
            printSecondaryTitle("Nombre d'heures (élèves) de cours particuliers par instrument sur " + e.getName());
            printTableTimeResult(getQuery("hours_of_private_lessons_by_activity", null, e.getId()));
          }
          break;
        case 19:
          printTitle(entry.getLabel());
          printTableTimeResult(getQuery("hours_of_collective_lessons_by_activity", null, null));
          //if (estabList.size() > 1) {
            for (Establishment e : estabList) {
              if (!isActiveEstab(e.getId())) {
                continue;
              }
              printSecondaryTitle("Nombre d'heures (élèves) de cours collectifs par activité sur " + e.getName());
              printTableTimeResult(getQuery("hours_of_collective_lessons_by_activity", null, e.getId()));
            }
          //}
          break;
        case 20:
          printTitle(entry.getLabel());
          printIntResult(getQuery("number_of_external_classrooms"));
          break;
        case 21:
          printTitle(entry.getLabel());
          printTimeResult(getQuery("external_hours"));
          break;
        case 22:
          printTitle(entry.getLabel());
          if (start.afterOrEqual(new DateFr("01-09-2014"))) {
            printTimeResult(getQuery("total_hours_of_studio"));
            printSecondaryTitle(MessageUtil.getMessage("statistics.hours.of.studio.by.type"));
            printTableTimeResult(getQuery("hours_of_studio_by_type"));
          } else {
            printTimeResult(getQuery("hours_of_studio"));
          }
          break;
        case 23:
          printTitle(entry.getLabel());
          printTimeResult(getQuery("hours_of_training"));
          footer();
          break;
      }
    }
  }

  @Override
  public void setStats() {
    statList = new ArrayList<StatElement>();
    statList.add(new StatElement(1, MessageUtil.getMessage("statistics.members.without.date.of.birth"), true));
    statList.add(new StatElement(2, MessageUtil.getMessage("statistics.number.of.students"), true));
    statList.add(new StatElement(3, "Répartition des élèves entre amateurs et pros", true));
    statList.add(new StatElement(4, "Liste des élèves en formation professionnelle", true));
    statList.add(new StatElement(5, MessageUtil.getMessage("statistics.city.distribution"), true));
    statList.add(new StatElement(6, "Nombre d'heures de répétition", true));
    statList.add(new StatElement(7, "Nombre de répétiteurs", true));
    statList.add(new StatElement(8, "Payeurs sans adresse", true));
    statList.add(new StatElement(9, "Adhérents débiteurs", true));
    statList.add(new StatElement(10, "Nombre d'adhérents", true));
    statList.add(new StatElement(11, "Répartition des élèves en cours individuels", true));
    statList.add(new StatElement(12, "Répartition des élèves en cours collectifs", true));
    statList.add(new StatElement(13, "Répartition des adhérents par catégorie professionnelle", true));
    statList.add(new StatElement(14, "Situation géographique des adhérents", true));
    statList.add(new StatElement(15, "Nombre d'heures de cours (heures élèves loisir et pro)", true));
    statList.add(new StatElement(16, "Nombre d'heures Loisir *", true));
    statList.add(new StatElement(17, "Nombre d'heures For Pro *", true));
    statList.add(new StatElement(18, "Nombre d'heures (élèves) de cours particuliers par instrument", true));
    statList.add(new StatElement(19, "Nombre d'heures (élèves) de cours collectifs par activité", true));
    statList.add(new StatElement(20, "Nombre de classes extérieur", true));
    statList.add(new StatElement(21, "Nombre d'heures extérieur", true));
    statList.add(new StatElement(22, "Nombre d'heures studio", true));
    statList.add(new StatElement(23, MessageUtil.getMessage("statistics.hours.of.training"), true));
  }

  private void incId() {
    int nbEstab = estabList.size();
    if (nbEstab > 1) {
      navId += nbEstab;
    }
  }

  @Override
  protected String getQuery(String m) throws SQLException {
    if (m.equals("members_without_date_of_birth")
      || m.equals("total_number_of_students")
      || m.equals("list_pro_students")
      || m.equals("students_by_location")
      || m.equals("groups_with_rehearsal")
      || m.equals("members_with_rehearsal")
      || m.equals("hours_of_pro_lessons")
      || m.equals("hours_of_collective_pro_lessons")
      || m.equals("hours_of_private_pro_lessons")
      || m.equals("hours_teacher_of_collective_lessons")
      || m.equals("total_hours_of_studio")
      || m.equals("hours_of_studio_by_type")
      || m.equals("hours_of_training")) {
      return super.getQuery(m);
    }

    if (m.equals("payers_without_address")) {
      return "SELECT DISTINCT payeur,personne.prenom,personne.nom FROM echeancier2,personne"
        + " WHERE echeance >= '" + start + "' AND echeance <= '" + end + "'"
        + " AND echeancier2.montant = " + MONTANT_ADHESION
        + " AND echeancier2.payeur = personne.id"
        + " AND (compte = " + MEMBERSHIP_ACCOUNT + " OR compte = " + PRO_MEMBER_ACCOUNT + ")"
        + " AND payeur NOT IN (SELECT idper FROM adresse) ORDER BY personne.nom,personne.prenom";
    }
    if (m.equals("debtors")) {
      return "SELECT DISTINCT adherent,prenom,nom FROM echeancier2,personne"
        + " WHERE echeance BETWEEN '" + start + "' AND '" + end + "'"
        + " AND echeancier2.montant = " + MONTANT_ADHESION
        + " AND (compte = " + MEMBERSHIP_ACCOUNT + " OR compte = " + PRO_MEMBER_ACCOUNT + ")"
        + " AND echeancier2.paye = false"
        + " AND echeancier2.adherent = personne.id ORDER BY nom,prenom";
    }
    if (m.equals("total_number_of_members")) {
      return "SELECT count(DISTINCT adherent) FROM echeancier2"
        + " WHERE echeance BETWEEN '" + start + "' AND '" + end + "'"
        + " AND echeancier2.montant = " + MONTANT_ADHESION
        + " AND (compte = " + MEMBERSHIP_ACCOUNT + " OR compte = " + PRO_MEMBER_ACCOUNT + ")";
    }
    if (m.equals("number_of_men_members")) {
      return "SELECT count(DISTINCT adherent) FROM echeancier2, personne"
        + " WHERE personne.id = echeancier2.adherent"
        + " AND echeance BETWEEN '" + start + "' AND '" + end + "'"
        + " AND echeancier2.montant=" + MONTANT_ADHESION
        + " AND (compte = " + MEMBERSHIP_ACCOUNT + " OR compte = " + PRO_MEMBER_ACCOUNT + ")"
        + " AND (trim(personne.civilite) = 'M' OR personne.civilite = '')";
    }
    if (m.equals("number_of_women_members")) {
      return "SELECT count(DISTINCT adherent) FROM echeancier2, personne"
        + " WHERE personne.id = echeancier2.adherent"
        + " AND echeance BETWEEN '" + start + "' AND '" + end + "'"
        + " AND echeancier2.montant=" + MONTANT_ADHESION
        + " AND (compte = " + MEMBERSHIP_ACCOUNT + " OR compte = " + PRO_MEMBER_ACCOUNT + ")"
        + " AND (trim(personne.civilite) = 'Mme' OR personne.civilite = 'Mlle')";
    }

    if (m.equals("members_by_occupational")) {
      return "SELECT profession, count(DISTINCT adherent) FROM echeancier2,eleve"
        + " WHERE eleve.idper=echeancier2.adherent"
        + " AND echeance BETWEEN '" + start + "' AND '" + end + "'"
        + " AND montant = " + MONTANT_ADHESION
        + " AND (compte  = " + MEMBERSHIP_ACCOUNT + " OR compte = " + PRO_MEMBER_ACCOUNT + ")"
        + " GROUP BY profession";
    }

    if (m.equals("members_by_location")) {
      return "SELECT adresse.ville, count(DISTINCT echeancier2.adherent) FROM echeancier2, adresse"
        + " WHERE echeancier2.echeance BETWEEN '" + start + "' AND '" + end + "'"
        + " AND echeancier2.montant = " + MONTANT_ADHESION
        + " AND (echeancier2.compte = " + MEMBERSHIP_ACCOUNT + " OR echeancier2.compte = " + PRO_MEMBER_ACCOUNT + ")"
        + " AND (echeancier2.payeur = adresse.idper OR echeancier2.adherent = adresse.idper)"
        + " GROUP BY adresse.ville ORDER BY adresse.ville";
    }

    if (m.equals("number_of_external_classrooms")) {
      return "SELECT count(DISTINCT planning.lieux) FROM planning, salle"
        + " WHERE planning.jour >= '" + start + "' AND planning.jour < '" + end + "'"
        + " AND planning.lieux = salle.id"
        + " AND salle.etablissement <> 3501" // spécifique MUSTANG
        + " AND salle.etablissement <> 3502";
    }
    if (m.equals("external_hours")) {
      return "SELECT sum(planning.fin - planning.debut) FROM planning, salle"
        + " WHERE planning.jour >= '" + start + "' AND planning.jour <= '" + end + "'"
        + " AND planning.lieux = salle.id"
        + " AND salle.etablissement <> 3501"
        + " AND salle.etablissement <> 3502";
    }
    if (m.equals("hours_of_studio")) {
      return "SELECT sum(planning.fin - planning.debut) FROM planning"
        + " WHERE planning.jour >= '" + start + "' AND planning.jour <= '" + end + "'"
        + " AND planning.lieux = " + STUDIO;
    }
    return null;
  }

  @Override
  protected String getQuery(String m, Object a1, Object a2) {
    if ("number_of_amateurs".equals(m)
      || "number_of_pros".equals(m)
      || "students_by_instrument".equals(m)
      || "hours_of_rehearsal".equals(m)
      || "hours_of_lessons".equals(m)
      || "hours_of_teacher_lessons".equals(m)) {
      return super.getQuery(m, a1, a2);
    }

    if ("hours_of_private_lessons".equals(m)) {
      String query = "SELECT sum(plage.fin-plage.debut) FROM planning, plage, action, cours, salle"
        + " WHERE plage.idplanning = planning.id"
        + " AND planning.jour >= '" + start + "' AND planning.jour <= '" + end + "'"
        + " AND plage.debut >= planning.debut AND plage.fin <= planning.fin"
        + " AND planning.ptype = " + Schedule.COURSE
        + " AND planning.action = action.id"
        + " AND action.cours = cours.id"
        + " AND cours.collectif = FALSE"
        + " AND planning.lieux = salle.id";

      if (a2 != null) { // par établissement
        query += " AND salle.etablissement = " + a2;
      }
      return query;
    }
    if ("hours_of_collective_lessons".equals(m)) {
      String query = "SELECT sum(planning.fin-planning.debut) FROM planning, plage, action, cours, salle"
        + " WHERE plage.idplanning = planning.id"
        + " AND planning.jour >= '" + start + "' AND planning.jour <= '" + end + "'"
        + " AND plage.debut >= planning.debut AND plage.fin <= planning.fin"
        + " AND planning.ptype IN (" + Schedule.COURSE + "," + Schedule.WORKSHOP + "," + Schedule.TRAINING + ")"
        + " AND planning.action = action.id"
        + " AND action.cours = cours.id"
        + " AND cours.collectif =  TRUE"
        + " AND planning.lieux = salle.id";

      if (a2 != null) { // par établissement
        query += " AND salle.etablissement = " + a2;
      }
      return query;
    }
    if ("hours_of_private_lessons_by_activity".equals(m)) {
      String query = "SELECT cours.titre, sum(plage.fin-plage.debut) FROM planning, plage, action, cours, salle"
        + " WHERE plage.idplanning = planning.id"
        + " AND planning.jour >= '" + start + "' AND planning.jour <= '" + end + "'"
        + " AND plage.debut >= planning.debut AND plage.fin <= planning.fin"
        + " AND planning.ptype = " + Schedule.COURSE
        + " AND planning.action = action.id"
        + " AND action.cours = cours.id"
        + " AND cours.collectif =  FALSE"
        + " AND planning.lieux = salle.id";

      if (a2 != null) { // par établissement
        query += " AND salle.etablissement = " + a2;
      }
      query += " GROUP BY cours.titre ORDER BY cours.titre";
      return query;
    }

    if ("hours_of_collective_lessons_by_activity".equals(m)) {
      String query = "SELECT cours.titre, sum(planning.fin-planning.debut) FROM planning, plage, action, cours, salle"
        + " WHERE plage.idplanning = planning.id"
        + " AND planning.jour >= '" + start + "' AND planning.jour <= '" + end + "'"
        + " AND plage.debut >= planning.debut AND plage.fin <= planning.fin"
        + " AND planning.ptype IN (" + Schedule.COURSE + "," + Schedule.WORKSHOP + "," + Schedule.TRAINING + ")"
        + " AND planning.action = action.id"
        + " AND action.cours = cours.id"
        + " AND cours.collectif = TRUE"
        + " AND planning.lieux = salle.id";

      if (a2 != null) { // par établissement
        query += " AND salle.etablissement = " + a2;
      }
      query += " GROUP BY cours.titre ORDER BY cours.titre";

      return query;
    }
    return null;
  }

  private boolean isActiveEstab(int id) {
    return id == 3501 || id == 21617;
  }

  public static void main(String[] args) throws SQLException {
    if (args.length != 1) {
      System.err.println(args.length + " error 1");
      System.exit(1);
    }
    String y = args[0].trim();
    if (y.length() != 4) {
      System.err.println("error 2");
      System.exit(2);
    }
    int year = Integer.parseInt(y);
    DataConnection dc = new DataConnection("localhost", 5434, "mustang", null);

    dc.connect();

//    DataCache cache = DataCache.getInstance(dc, System.getProperty("user.name"));
    DataCache cache = DataCache.getInstance(dc, "admin");
    cache.load(null);

    Statistics st = null;
    try {
      //st = StatisticsFactory.getStatistics(ConfigUtil.getConf(ConfigKey.ORGANIZATION_NAME.getKey(), dc), cache);
      st = StatisticsFactory.getInstance();
      if (st == null) {
        st = new StatisticsDefault();
      }
      st.init(cache);
      st.setConfig(new DateFr("01-09-" + year), new DateFr("31-08-" + (year + 1)));
//      st.setConfig(new DateFr("01-10-"+year), new DateFr("01-10-"+(year)));
      st.makeStats();
      System.out.println(MessageUtil.getMessage("statistics.completed", ""));
    } catch (IOException ex) {
      GemLogger.logException(ex);
    } catch (SQLException sqe) {
      GemLogger.logException(sqe);
    } finally {
      if (st != null) {
        st.close();
      }
    }
  }

}

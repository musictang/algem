/*
 * @(#)StatisticsPlugin.java	2.10.0 08/06/2016
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
package net.algem.plugins;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import net.algem.config.AgeRange;
import net.algem.edition.StatElement;
import net.algem.edition.Statistics;
import net.algem.edition.StatisticsDefault;
import net.algem.edition.StatisticsFactory;
import net.algem.planning.DateFr;
import net.algem.planning.Schedule;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;
import net.algem.util.MessageUtil;
import net.algem.util.model.Model;

/**
 * Stats plugin Jazz A Tours.
 * Pour Algem à partir de la version 2.10.0
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.10.0
 * @since 1.0.0 28/02/2013
 */
public class StatisticsPlugin
  extends Statistics {

  private static final int FIRST_MEMBERSHIP_ACCOUNT = 7;
  private static final int MEMBERSHIP_PRICE = 1500;
  private static final int MEMBERSHIP_N_ACCOUNT = 15;
  private static final int MEMBERSHIP_NN_ACCOUNT = 16;

  @Override
  public void makeStats() throws SQLException {
//    super.makeStats();
    header();
    for (StatElement entry : statList) {
      switch (entry.getKey()) {
        case 1:
          printTitle(entry.getLabel());
          listPersons(getQuery("members_without_date_of_birth"));
          break;
        case 2:
          printTitle(entry.getLabel());
          listPersons(getQuery("debtors"));
          break;
        case 3:
          printTitle(entry.getLabel());
          printTableIntResult(getQuery("members_by_occupational"));
          break;
        case 4:
          out.println("<h2>©  STATISTIQUES PERSONNALISÉES  ©</h2>");
          printTitle(entry.getLabel());
          listPersons(getQuery("erreurs adhesions"));
          break;
        case 5:
          printTitle(entry.getLabel());
          out.println("\n\t\t<table class='list'>");
          out.println("\n\t<tr><th>Adhérents ne prenant pas de cours</th><td>" + getIntResult(getQuery("total_number_of_members_not_students")) + "</td></tr>");
          out.println("\n\t<tr><th>Adhérents prenant des cours</th><td>" + getIntResult(getQuery("total_number_of_students")) + "</td></tr>");
          out.println("\n\t<tr><th>Total (élèves ou non)</th><td>" + getIntResult(getQuery("total_number_of_members")) + "</td></tr>");
          out.println("\n\t\t</table>");
          break;
        case 6:
          printTitle(entry.getLabel());
          List<AgeRange> ages = dataCache.getList(Model.AgeRange).getData();
          out.print("\n\t\t<table class='list'>");
          if (ages != null) {
            for (AgeRange a : ages) {
              if (!a.getCode().equals("-")) {
                out.print("<tr><th>" + a.getAgemin() + "-" + a.getAgemax() + " ans</th>");
                out.print("<td>" + getIntResult(getQuery("eleves_par_age", a.getAgemin(), a.getAgemax())) + "</td></tr>");
              }
            }
            out.print("\n\t\t</table>");
          }
          break;
        case 7:
          printTitle(entry.getLabel());
          out.print("\n\t\t<table class='list'>");
          out.print("<tr><th>Hommes</th><td>" + getIntResult(getQuery("number_of_men_students")) + "</td></tr>");
          out.print("<tr><th>Femmes</th><td>" + getIntResult(getQuery("number_of_women_students")) + "</td></tr>");
          out.println("\n\t\t</table>");
          break;
        case 8:
          printTitle(entry.getLabel());
          out.println("\n\t\t<table class='list'>");
          out.print("<tr><th>Tours</th><td>" + getIntResult(getQuery("number_of_students_in_Tours")) + "</td></tr>");
          out.print("<tr><th>Agglomération de Tours</th><td>" + getIntResult(getQuery("number_of_students_in_Tours_suburbs")) + "</td></tr>");
          out.print("<tr><th>Hors agglo Tours</th><td>" + getIntResult(getQuery("number_of_students_outside")) + "</td></tr>");
          out.println("\n\t\t</table>");
          break;
        case 9:
          printTitle(entry.getLabel());
          printSecondaryTitle("Loisir");//"Nombre d'élèves par instrument pour les cours individuels (Loisir)"
          printTableIntResult(super.getQuery("students_by_instrument", false, 0));
          printSecondaryTitle("Pro");//"Nombre d'élèves par instrument pour les cours individuels (Pro)"
          printTableIntResult(super.getQuery("students_by_instrument", true, 0));
          /*if (estabList.size() > 1) {
           for (Establishment e : estabList) {
           printTableIntResult("Nombre d'élèves par instrument pour les cours individuels sur " + e.getName(), super.getQuery("students_by_instrument", false, e.getId()));
           }
           }*/
          break;

        /*printTableIntResult("Nombre d'élèves par activité pour les cours collectifs", super.getQuery("students_by_instrument", true, 0));
         if (estabList.size() > 1) {
         for (Establishment e : estabList) {
         printTableIntResult("Nombre d'élèves par activité pour les cours collectifs sur " + e.getName(), super.getQuery("students_by_instrument", true, e.getId()));
         }
         }
         separate();*/
        case 10:
          printTitle(entry.getLabel());
          out.println("<cite>(seuls les élèves effectivement présents sur une plage de cours sont comptabilisés)</cite>");
          out.println("\n\t\t<table class='list'>");

          out.println("\t\t\t<tr><th>Cours 30 mn + Atelier Jazz Ado (formule 101)</th><td>" + getIntResult(getQuery(101)) + "</td></tr>");
          out.println("\t\t\t<tr><th>Cours 45 mn (formule 103)</th><td>" + getIntResult(getQuery(103)) + "</td></tr>");
          out.println("\t\t\t<tr><th>Cours 30 mn + Atelier 1h30 (formule 104)</th><td>" + getIntResult(getQuery(104)) + "</td></tr>");
          out.println("\t\t\t<tr><th>Cours 45 mn + Atelier 1h30 (formule 105)</th><td>" + getIntResult(getQuery(105)) + "</td></tr>");
          out.println("\t\t\t<tr><th>Atelier 2h (formule 111)</th><td>" + getIntResult(getQuery(111)) + "</td></tr>");
          out.println("\t\t\t<tr><th>Atelier Jazz Ado (formule 112)</th><td>" + getIntResult(getQuery(112)) + "</td></tr>");
          out.println("\t\t\t<tr><th>Atelier 1h30 (formule 113)</th><td>" + getIntResult(getQuery(113)) + "</td></tr>");

          int mod1 = getIntResult(getQuery(115));
          int mod2 = getIntResult(getQuery(130));
          out.println("\t\t\t<tr><th>Atelier Gospel (formule 115, 130)</th><td>" + (mod1 + mod2) + "</td></tr>");
          out.println("\t\t\t<tr><th>Cours 30 mn (formule 116)</th><td>" + getIntResult(getQuery(116)) + "</td></tr>");
          out.println("\t\t\t<tr><th>Cours 45 mn + Atelier Jazz Ado (formule 127)</th><td>" + getIntResult(getQuery(127)) + "</td></tr>");
          out.println("\t\t\t<tr><th>Cours 30 mn + Atelier 2h (formule 128)</th><td>" + getIntResult(getQuery(128)) + "</td></tr>");
          out.println("<tr><th>Cours 45 mn + Atelier 2h (formule 129)</th><td>" + getIntResult(getQuery(129)) + "</td></tr>");

          out.println("<tr><th>Parcours Intensif + Cours individuel 30mn (formule 159)</th><td>" + getIntResult(getQuery(159)) + "</td></tr>");
          out.println("<tr><th>Parcours Intensif + Cours Individuel 45 mn (formule 160)</th><td>" + getIntResult(getQuery(160)) + "</td></tr>");
          out.print("\n\t\t</table>");
          break;
        case 11:
          printTitle(entry.getLabel());
          out.println("\n\t\t<table class='list'>");
          out.println("<tr><th>PARCOURS BREVET Jazz 1/2h cours instrument (141)</th><td>" + getIntResult(getQuery(141)) + "</td></tr>");
          out.println("<tr><th>PARCOURS BREVETJazz 3/4h cours instrument (142)</th><td>" + getIntResult(getQuery(142)) + "</td></tr>");
          out.println("<tr><th>Parcours MIMA Jazz (143)</th><td>" + getIntResult(getQuery(143)) + "</td></tr>");
          out.println("<tr><th>Parcours MIMA Jazz (144)</th><td>" + getIntResult(getQuery(144)) + "</td></tr>");
          out.println("<tr><th>Dem jazz (149)</th><td>" + getIntResult(getQuery(149)) + "</td></tr>");
          out.println("<tr><th>PARCOURS BREVET ½ h cours instrument (147)</th><td>" + getIntResult(getQuery(147)) + "</td></tr>");
          out.println("<tr><th>PARCOURS BREVET 3/4h instrument (148)</th><td>" + getIntResult(getQuery(148)) + "</td></tr>");
          out.println("<tr><th>Parcours MIMA MAA(145)</th><td>" + getIntResult(getQuery(145)) + "</td></tr>");
          out.println("<tr><th>Parcours MIMA MAA(146)</th><td>" + getIntResult(getQuery(146)) + "</td></tr>");
          out.println("<tr><th>Licence 1 musicologie (151)</th><td>" + getIntResult(getQuery(151)) + "</td></tr>");
          out.println("<tr><th>Licence 2 musicologie (157)</th><td>" + getIntResult(getQuery(157)) + "</td></tr>");
          out.println("<tr><th>Licence 3 musicologie (158)</th><td>" + getIntResult(getQuery(158)) + "</td></tr>");

          out.println("<tr><th>Le musicien et son Corps (163)</th><td>" + getIntResult(getQuery(163)) + "</td></tr>");
          out.println("<tr><th>Coaching Personnalisé (164)</th><td>" + getIntResult(getQuery(164)) + "</td></tr>");
          out.println("<tr><th>Coaching de groupe (165)</th><td>" + getIntResult(getQuery(165)) + "</td></tr>");
          out.println("<tr><th>Composition musicale (162)</th><td>" + getIntResult(getQuery(162)) + "</td></tr>");
          out.println("<tr><th>Atelier Ecriture Textes (133)</th><td>" + getIntResult(getQuery(133)) + "</td></tr>");
          out.print("\n\t\t</table>");
          break;

//    printTimeResult(MessageUtil.getMessage("statistics.hours.of.training"), getQuery("hours_of_training"));
//    printTimeResult(MessageUtil.getMessage("statistics.total.hours.of.studio"), getQuery("total_hours_of_studio"));
//    printTableTimeResult(MessageUtil.getMessage("statistics.hours.of.studio.by.type"), getQuery("hours_of_studio_by_type"));
      } // end switch

    } // end for loop
    footer();
  }

  @Override
  protected String getQuery(String m) throws SQLException {
    if (m.equals("members_without_date_of_birth")
      || m.equals("groups_with_rehearsal")
      || m.equals("members_with_rehearsal")) //        || m.equals("total_hours_of_studio")
    //        || m.equals("hours_of_studio_by_type")
    //        || m.equals("hours_of_training"))
    {
      return super.getQuery(m);

    } else if (m.equals("erreurs adhesions")) {
      return "SELECT DISTINCT eleve.idper, personne.prenom, personne.nom"
        + " FROM commande_cours, commande, eleve, personne"
        + " WHERE datedebut BETWEEN '" + start + "' AND '" + end + "'"
        + " AND commande_cours.idcmd = commande.id"
        + " AND commande.adh = eleve.idper"
        + " AND commande_cours.debut != '00:00:00'"
        + " AND eleve.idper = personne.id"
        + " AND eleve.idper NOT IN("
        + " SELECT adherent FROM echeancier2"
        + " WHERE echeance BETWEEN '" + start + "' AND '" + end + "'"
        + " AND compte IN (" + FIRST_MEMBERSHIP_ACCOUNT + "," + MEMBERSHIP_N_ACCOUNT + "," + MEMBERSHIP_NN_ACCOUNT
        + ") AND montant = " + MEMBERSHIP_PRICE + ")"
        + " ORDER BY personne.nom,personne.prenom";
    } else if (m.equals("members_by_occupational")) {
      return "SELECT profession, count(DISTINCT adherent) FROM echeancier2,eleve"
        + " WHERE eleve.idper=echeancier2.adherent"
        + " AND echeance BETWEEN '" + start + "' AND '" + end + "'"
        + " AND montant = " + MEMBERSHIP_PRICE
        + " AND compte IN (" + FIRST_MEMBERSHIP_ACCOUNT + "," + MEMBERSHIP_N_ACCOUNT + "," + MEMBERSHIP_NN_ACCOUNT
        + ") GROUP BY profession";
    } else if (m.equals("total_number_of_members")) {
      return "SELECT count(DISTINCT adherent) FROM echeancier2"
        + " WHERE echeance BETWEEN '" + start + "' AND '" + end + "'"
        + " AND echeancier2.montant = " + MEMBERSHIP_PRICE
        + " AND adherent != 0"
        + " AND compte IN (" + FIRST_MEMBERSHIP_ACCOUNT + "," + MEMBERSHIP_N_ACCOUNT + "," + MEMBERSHIP_NN_ACCOUNT + ")";
    } else if (m.equals("total_number_of_members_not_students")) {
      return "SELECT count(DISTINCT adherent) FROM echeancier2"
        + " WHERE echeance BETWEEN '" + start + "' AND '" + end + "'"
        + " AND echeancier2.montant = " + MEMBERSHIP_PRICE
        + " AND compte IN (" + FIRST_MEMBERSHIP_ACCOUNT + "," + MEMBERSHIP_N_ACCOUNT + "," + MEMBERSHIP_NN_ACCOUNT + ")"
        + " AND adherent != 0"
        + " AND adherent NOT IN ("
        + " SELECT eleve.idper FROM commande_cours, commande, eleve"
        + " WHERE datedebut >= '" + start + "' AND datedebut <= '" + end + "'"
        + " AND commande_cours.idcmd = commande.id"
        + " AND commande.adh = eleve.idper"
        + " AND commande_cours.debut != '00:00:00')";
    } else if (m.equals("total_number_of_students")) {
      return "SELECT count(DISTINCT adherent) FROM echeancier2"
        + " WHERE echeance BETWEEN '" + start + "' AND '" + end + "'"
        + " AND echeancier2.montant = " + MEMBERSHIP_PRICE
        + " AND compte IN (" + FIRST_MEMBERSHIP_ACCOUNT + "," + MEMBERSHIP_N_ACCOUNT + "," + MEMBERSHIP_NN_ACCOUNT + ")"
        + " AND adherent != 0"
        + " AND adherent IN ("
        + " SELECT eleve.idper FROM commande_cours, commande, eleve"
        + " WHERE datedebut >= '" + start + "' AND datedebut <= '" + end + "'"
        + " AND commande_cours.idcmd = commande.id"
        + " AND commande.adh = eleve.idper"
        + " AND commande_cours.debut != '00:00:00')";
    } else if (m.equals("number_of_men_students")) {
      return "SELECT count(DISTINCT eleve.idper) FROM commande_cours, commande, eleve, personne"
        + " WHERE datedebut >= '" + start + "' AND datedebut <= '" + end + "'"
        + " AND commande_cours.idcmd = commande.id"
        + " AND commande.adh = eleve.idper"
        + " AND commande_cours.debut != '00:00:00'"
        + " AND personne.id = eleve.idper"
        + " AND (trim(personne.civilite) = 'M' OR personne.civilite = '')";

    } else if (m.equals("number_of_women_students")) {
      return "SELECT count(DISTINCT eleve.idper) FROM commande_cours, commande, eleve, personne"
        + " WHERE datedebut >= '" + start + "' AND datedebut <= '" + end + "'"
        + " AND commande_cours.idcmd = commande.id"
        + " AND commande.adh = eleve.idper"
        + " AND commande_cours.debut != '00:00:00'"
        + " AND personne.id = eleve.idper"
        + " AND (trim(personne.civilite) = 'Mme' OR personne.civilite = 'Mlle')";
    } else if (m.equals("number_of_students_in_Tours")) {
      return "SELECT count(DISTINCT eleve.idper) FROM commande_cours, commande, eleve, adresse"
        + " WHERE datedebut >= '" + start + "' AND datedebut <= '" + end + "'"
        + " AND commande_cours.idcmd = commande.id"
        + " AND commande.adh = eleve.idper"
        + " AND commande_cours.debut != '00:00:00'"
        + " AND (eleve.idper = adresse.idper OR eleve.payeur = adresse.idper)"
        + " AND adresse.cdp IN ('37000','37100')";
    } else if (m.equals("number_of_students_in_Tours_suburbs")) {
      return "SELECT count(DISTINCT eleve.idper) FROM commande_cours, commande, eleve, adresse"
        + " WHERE datedebut >= '" + start + "' AND datedebut <= '" + end + "'"
        + " AND commande_cours.idcmd = commande.id"
        + " AND commande.adh = eleve.idper"
        + " and commande_cours.debut != '00:00:00'"
        + " AND (eleve.idper = adresse.idper OR eleve.payeur = adresse.idper)"
        + " AND adresse.cdp IN ('37170','37190','37230','37300','37390','37510','37520','37540','37550','37700')";
    } else if (m.equals("number_of_students_outside")) {
      return "SELECT count(DISTINCT eleve.idper) FROM commande_cours, commande, eleve, adresse"
        + " WHERE datedebut >= '" + start + "' AND datedebut <= '" + end + "'"
        + " AND commande_cours.idcmd = commande.id"
        + " AND commande.adh = eleve.idper"
        + " AND commande_cours.debut != '00:00:00'"
        + " AND (eleve.idper = adresse.idper OR eleve.payeur = adresse.idper)"
        + " AND adresse.cdp NOT IN ('37000','37100','37170','37190','37230','37300','37390','37510','37520','37540','37550','37700')";
    } else if (m.equals("debtors")) {
      return "SELECT DISTINCT e.adherent,p.prenom,p.nom FROM echeancier2 e JOIN personne p ON (e.adherent = p.id)"
        + " WHERE e.echeance BETWEEN '" + start + "' AND '" + end + "'"
        + " AND e.montant = " + MEMBERSHIP_PRICE
        + " AND e.compte IN (" + FIRST_MEMBERSHIP_ACCOUNT + "," + MEMBERSHIP_N_ACCOUNT + "," + MEMBERSHIP_NN_ACCOUNT + ")"
        + " AND e.paye = FALSE"
        + " ORDER BY p.nom,p.prenom";
    }
    return null;
  }

  @Override
  public String getQuery(String m, Object a1, Object a2) {

    if ("eleves_par_age".equals(m)) {
      return "SELECT count(DISTINCT eleve.idper) "
        + "FROM eleve, commande, commande_cours, commande_module, module"
        + " WHERE eleve.idper = commande.adh"
        + " AND commande.id = commande_cours.idcmd"
        + " AND commande_cours.module = commande_module.id"
        + " AND commande_module.module = module.id"
        + " AND commande_cours.datedebut between '" + start + "' AND '" + end + "'"
        + " AND extract(year from age(commande_cours.datedebut,datenais)) >= " + a1
        + " AND extract(year from age(commande_cours.datedebut,datenais)) <= " + a2 // correction agemax inclus
        + " AND commande_cours.debut != '00:00:00'";
    } else if ("students_by_instrument".equals(m)) {
      String query = "SELECT cours.titre, count(distinct plage.adherent) "
        + " FROM plage, planning, cours, action,commande_cours,commande_module,module"
        + " WHERE plage.idplanning = planning.id"
        + " AND planning.action = commande_cours.idaction"
        + " AND commande_cours.module = commande_module.id"
        + " AND module.code LIKE " + ((boolean) a1 ? "'P%'" : "'L%'")
        + " AND planning.jour BETWEEN '" + start + "' AND '" + end + "'"
        + " AND plage.debut >= planning.debut"
        + " AND plage.fin <= planning.fin"
        + " AND planning.action = action.id"
        + " AND action.cours = cours.id"
        + " AND planning.ptype IN(" + Schedule.COURSE + "," +Schedule.WORKSHOP + "," +Schedule.TRAINING+")"
//        + " AND planning.lieux = salle.id"
        + " AND cours.collectif = false";

      query += " GROUP BY cours.titre";
      return query;
    }
    return null;
  }

  protected String getQuery(int formule) {
    return "SELECT count(DISTINCT c.adh)"
      + " FROM  planning p, plage pl, commande_cours cc, commande_module cm, commande c"
      + " WHERE p.jour BETWEEN '" + start + "' AND '" + end + "'"
      + " AND p.id = pl.idplanning"
      + " AND p.action = cc.idaction"
      + " AND cc.idcmd = c.id"
      + " AND cc.module = cm.id"
      + " AND cm.module = " + formule
      + " AND c.adh = pl.adherent";
  }

  void printErrors(String m, String query) throws SQLException {
    out.println("<h3 id=\"" + navId + "\"><a href=\"#top\">^ </a>" + m + "</h3>");
    ResultSet rs = dc.executeQuery(query);
    int n = 0;
    out.println("<table>");
    while (rs.next()) {
      n++;
      out.print("<tr><th>" + rs.getInt(1) + "</th>");
      out.print("<td>" + rs.getString(2) + "</td>");
      out.println("<td>" + rs.getString(3) + "</td></tr>");
    }
    out.println("<tr><th colspan=\"2\">Total</th><td>" + n + "<td></tr>");
    out.println("</table>");
    out.println();
    navId++;
  }

  /**
   * Run statistics.
   * The beginning year must be included in argument (ex. : 2014 for 2014-2015)
   *
   * @param args
   * @throws SQLException
   */
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
//    int year = 2012;
    DataConnection dc = new DataConnection("localhost", 5432, "jazzat", null);

    dc.connect();

//    DataCache cache = DataCache.getInstance(dc, System.getProperty("user.name"));
    DataCache cache = DataCache.getInstance(dc, "admin");
    cache.setUser("admin");
    cache.load(null);

    Statistics st = null;
    try {
      //st = StatisticsFactory.getStatistics(ConfigUtil.getConf(ConfigKey.ORGANIZATION_NAME.getKey(), dc), cache);
      st = StatisticsFactory.getInstance();
      if (st == null) {
        st = new StatisticsDefault();
      }
      st.init(cache);
//      st.setConfig(new DateFr("01-07-"+year), new DateFr("31-08-"+(year+1)));
      st.setConfig(new DateFr("01-07-2015"), new DateFr("01-08-2016"));
      st.makeStats();
      System.out.println(MessageUtil.getMessage("statistics.completed", ""));
    } catch (IOException ex) {
      System.err.println(ex);
    } catch (SQLException sqe) {
      System.err.println(sqe);
      sqe.printStackTrace();
    } finally {
      if (st != null) {
        st.close();
      }
    }

  }

  @Override
  public void setStats() {
    statList = new ArrayList<StatElement>();
    statList.add(new StatElement(1, MessageUtil.getMessage("statistics.members.without.date.of.birth"), true));
    statList.add(new StatElement(2, "Adhésions non payées", true));
    statList.add(new StatElement(3,"Répartition des adhérents par catégorie professionnelle", true));
    statList.add(new StatElement(4, "Personnes avec erreur adhésion ou ligne d'adhésion incorrecte", true));
    statList.add(new StatElement(5, "Nombre d'adhérents", true));
    statList.add(new StatElement(6, "Répartition des élèves par âge", true));
    statList.add(new StatElement(7, "Répartition des élèves par sexe", true));
    statList.add(new StatElement(8, "Répartition géographique des élèves", true));
    statList.add(new StatElement(9, "Nombre d'élèves par instrument pour les cours individuels", true));
//    "Nombre d'élèves par activité pour les cours collectifs");
    statList.add(new StatElement(10, "Répartition des élèves par formule (formule)", true));
    statList.add(new StatElement(11, "Répartition par niveau des élèves en formation pro", true));

  }

  private void incId() {
    int nbEstab = estabList.size();
    if (nbEstab > 1) {
      navId += nbEstab;
    }
  }

}

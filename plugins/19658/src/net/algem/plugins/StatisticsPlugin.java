/*
 * @(#)StatisticsPlugin.java	1.0.6 03/07/15
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
 * along with Algem. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package net.algem.plugins;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import net.algem.config.AgeRange;
import net.algem.edition.Statistics;
import net.algem.edition.StatisticsDefault;
import net.algem.edition.StatisticsFactory;
import net.algem.planning.DateFr;
import net.algem.room.Establishment;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;
import net.algem.util.MessageUtil;
import net.algem.util.model.Model;

/**
 * Stats plugin for Jazz A Tours.
 * Pour Algem à partir de la version 2.8.v
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 1.0.6
 * @since 1.0.0 28/02/2013
 */
public class StatisticsPlugin
  extends Statistics
{

  private static final int FIRST_MEMBERSHIP_ACCOUNT = 7;
  private static final int MEMBERSHIP_PRICE = 1500;
  private static final int MEMBERSHIP_N_ACCOUNT = 15;
  private static final int MEMBERSHIP_NN_ACCOUNT = 16;
  @Override
  public void makeStats() throws SQLException {
//    super.makeStats();
    header();
    printIntListResult(MessageUtil.getMessage("statistics.members.without.date.of.birth"), getQuery("members_without_date_of_birth"));
    printErrors("Adhésions non payées", getQuery("debtors"));
    printTableIntResult("Répartition des adhérents par catégorie professionnelle", getQuery("members_by_occupational"));

    out.println("<h2>©  STATISTIQUES PERSONNALISÉES  ©</h2>");
    printErrors("Personnes avec erreur adhésion ou ligne d'adhésion incorrecte", getQuery("erreurs adhesions"));
    printTitle("Nombre d'adhérents");
    out.println("\n\t\t<table class='list'>");
    out.println("\n\t<tr><th>Adhérents ne prenant pas de cours</th><td>" + getIntResult(getQuery("total_number_of_members_not_students")) + "</td></tr>");
    out.println("\n\t<tr><th>Adhérents prenant des cours</th><td>" + getIntResult(getQuery("total_number_of_students")) + "</td></tr>");
    out.println("\n\t<tr><th>Total (élèves ou non)</th><td>" + getIntResult(getQuery("total_number_of_members")) + "</td></tr>");
    out.println("\n\t\t</table>");

    printTitle("Répartition des élèves par age");
    List<AgeRange> ages = dataCache.getList(Model.AgeRange).getData();
    out.print("\n\t\t<table class='list'>");
    if(ages != null) {
      for (AgeRange a : ages) {
        if (!a.getCode().equals("-")) {
          out.print("<tr><th>"+a.getAgemin()+"-"+a.getAgemax()+" ans</th>");
          out.print("<td>" + getIntResult(getQuery("eleves_par_age", a.getAgemin(), a.getAgemax())) + "</td></tr>");
        }
      }
      out.print("\n\t\t</table>");
    }

    printTitle("Répartition des élèves par sexe");
    out.print("\n\t\t<table class='list'>");
    out.print("<tr><th>Hommes</th><td>"+getIntResult(getQuery("number_of_men_students"))+"</td></tr>");
    out.print("<tr><th>Femmes</th><td>"+getIntResult(getQuery("number_of_women_students"))+"</td></tr>");
    out.println("\n\t\t</table>");

    printTitle("Répartition géographique des élèves");
    out.println("\n\t\t<table class='list'>");
    out.print("<tr><th>Tours</th><td>"+getIntResult(getQuery("number_of_students_in_Tours"))+"</td></tr>");
    out.print("<tr><th>Agglomération de Tours</th><td>"+getIntResult(getQuery("number_of_students_in_Tours_suburbs"))+"</td></tr>");
    out.print("<tr><th>Hors agglo Tours</th><td>"+getIntResult(getQuery("number_of_students_outside"))+"</td></tr>");
    out.println("\n\t\t</table>");

    printTableIntResult("Nombre d'élèves par instrument pour les cours individuels", super.getQuery("students_by_instrument", false, 0));
    if (estabList.size() > 1) {
      for (Establishment e : estabList) {
        printTableIntResult("Nombre d'élèves par instrument pour les cours individuels sur " + e.getName(), super.getQuery("students_by_instrument", false, e.getId()));
      }
    }
    separate();
    printTableIntResult("Nombre d'élèves par activité pour les cours collectifs", super.getQuery("students_by_instrument", true, 0));
    if (estabList.size() > 1) {
      for (Establishment e : estabList) {
        printTableIntResult("Nombre d'élèves par activité pour les cours collectifs sur " + e.getName(), super.getQuery("students_by_instrument", true, e.getId()));
      }
    }
    separate();
    printTitle("Répartition des élèves par formule (module)");
    out.println("<cite>(seuls les élèves effectivement présents sur une plage de cours sont comptabilisés)</cite>");
    out.println("\n\t\t<table class='list'>");

    out.println("\t\t\t<tr><th>Cours 30 mn + Atelier Jazz Ado (module 101)</th><td>" +getIntResult(getQuery(101)) + "</td></tr>");
    out.println("\t\t\t<tr><th>Cours 45 mn (module 103)</th><td>" +getIntResult(getQuery(103)) + "</td></tr>");
    out.println("\t\t\t<tr><th>Cours 30 mn + Atelier 1h30 (module 104)</th><td>" +getIntResult(getQuery(104)) + "</td></tr>");
    out.println("\t\t\t<tr><th>Cours 45 mn + Atelier 1h30 (module 105)</th><td>" +getIntResult(getQuery(105)) + "</td></tr>");
    out.println("\t\t\t<tr><th>Atelier 2h (module 111)</th><td>" +getIntResult(getQuery(111)) + "</td></tr>");
    out.println("\t\t\t<tr><th>Atelier Jazz Ado (module 112)</th><td>" +getIntResult(getQuery(112)) + "</td></tr>");
    out.println("\t\t\t<tr><th>Atelier 1h30 (module 113)</th><td>" +getIntResult(getQuery(113)) + "</td></tr>");

    int mod1 = getIntResult(getQuery(115));
    int mod2 = getIntResult(getQuery(130));
    out.println("\t\t\t<tr><th>Atelier Gospel (module 115, 130)</th><td>" + (mod1 + mod2) + "</td></tr>");
    out.println("\t\t\t<tr><th>Cours 30 mn (module 116)</th><td>" +getIntResult(getQuery(116)) + "</td></tr>");
    out.println("\t\t\t<tr><th>Cours 45 mn + Atelier Jazz Ado (module 127)</th><td>" +getIntResult(getQuery(127)) + "</td></tr>");
    out.println("\t\t\t<tr><th>Cours 30 mn + Atelier 2h (module 128)</th><td>" +getIntResult(getQuery(128)) + "</td></tr>");
    out.println("<tr><th>Cours 45 mn + Atelier 2h (module 129)</th><td>" +getIntResult(getQuery(129)) + "</td></tr>");

    out.print("\n\t\t</table>");

    printTitle("Répartition par niveau des élèves en formation pro");
    out.println("\n\t\t<table class='list'>");
    out.println("<tr><th>PARCOURS BREVET Jazz 1/2h cours instrument (141)</th><td>" +getIntResult(getQuery(141)) + "</td></tr>");
    out.println("<tr><th>PARCOURS BREVETJazz 3/4h cours instrument (142)</th><td>" +getIntResult(getQuery(142)) + "</td></tr>");
    out.println("<tr><th>Parcours MIMA Jazz (143)</th><td>" +getIntResult(getQuery(143)) + "</td></tr>");
    out.println("<tr><th>Parcours MIMA Jazz (144)</th><td>" +getIntResult(getQuery(144)) + "</td></tr>");
    out.println("<tr><th>Dem jazz (149)</th><td>" +getIntResult(getQuery(149)) + "</td></tr>");
    out.println("<tr><th>PARCOURS BREVET ½ h cours instrument (147)</th><td>" +getIntResult(getQuery(147)) + "</td></tr>");
    out.println("<tr><th>PARCOURS BREVET 3/4h instrument (148)</th><td>" +getIntResult(getQuery(148)) + "</td></tr>");
    out.println("<tr><th>Parcours MIMA MAA(145)</th><td>" +getIntResult(getQuery(145)) + "</td></tr>");
    out.println("<tr><th>>Parcours MIMA MAA(146)</th><td>" +getIntResult(getQuery(146)) + "</td></tr>");
    out.println("<tr><th>Licence 1 musicologie (151)</th><td>" +getIntResult(getQuery(151)) + "</td></tr>");
    out.println("<tr><th>Licence 2 musicologie (157)</th><td>" +getIntResult(getQuery(157)) + "</td></tr>");
    out.println("<tr><th>Licence 3 musicologie (158)</th><td>" +getIntResult(getQuery(158)) + "</td></tr>");

    out.println("<tr><th>Le musicien et son Corps (163)</th><td>" +getIntResult(getQuery(163)) + "</td></tr>");
    out.println("<tr><th>Coaching Personnalisé (164)</th><td>" + getIntResult(getQuery(164)) + "</td></tr>");
    out.println("<tr><th>Coaching de groupe (165)</th><td>" + getIntResult(getQuery(165)) + "</td></tr>");
    out.println("<tr><th>Composition musicale (162)</th><td>" + getIntResult(getQuery(162)) + "</td></tr>");
    out.println("<tr><th>Atelier Ecriture Textes (133)</th><td>" +getIntResult(getQuery(133)) + "</td></tr>");
    out.print("\n\t\t</table>");

//    printTimeResult(MessageUtil.getMessage("statistics.hours.of.training"), getQuery("hours_of_training"));
//    printTimeResult(MessageUtil.getMessage("statistics.total.hours.of.studio"), getQuery("total_hours_of_studio"));
//    printTableTimeResult(MessageUtil.getMessage("statistics.hours.of.studio.by.type"), getQuery("hours_of_studio_by_type"));

    footer();
  }

  @Override
  protected String getQuery(String m) throws SQLException {
    if (m.equals("members_without_date_of_birth")
//            || m.equals("students_by_instrument")
            || m.equals("groups_with_rehearsal")
            || m.equals("members_with_rehearsal")
            || m.equals("total_hours_of_studio")
            || m.equals("hours_of_studio_by_type")
            || m.equals("hours_of_training")) {
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
              + " AND compte IN ("+FIRST_MEMBERSHIP_ACCOUNT+","+ MEMBERSHIP_N_ACCOUNT+","+ MEMBERSHIP_NN_ACCOUNT
              +") AND montant = " + MEMBERSHIP_PRICE + ")";
    } else if (m.equals("members_by_occupational")) {
      return "SELECT profession, count(DISTINCT adherent) FROM echeancier2,eleve"
              + " WHERE eleve.idper=echeancier2.adherent"
              + " AND echeance BETWEEN '" + start + "' AND '" + end + "'"
              + " AND montant = " + MEMBERSHIP_PRICE
              + " AND compte IN ("+FIRST_MEMBERSHIP_ACCOUNT+","+ MEMBERSHIP_N_ACCOUNT+","+ MEMBERSHIP_NN_ACCOUNT
              + ") GROUP BY profession";
    }
      else if (m.equals("total_number_of_members")) {
      return "SELECT count(DISTINCT adherent) FROM echeancier2"
              + " WHERE echeance BETWEEN '" + start + "' AND '" + end + "'"
              + " AND echeancier2.montant = " + MEMBERSHIP_PRICE
              + " AND adherent != 0"
              + " AND compte IN ("+FIRST_MEMBERSHIP_ACCOUNT+","+ MEMBERSHIP_N_ACCOUNT+","+ MEMBERSHIP_NN_ACCOUNT+")";
    } else if (m.equals("total_number_of_members_not_students")) {
      return "SELECT count(DISTINCT adherent) FROM echeancier2"
              + " WHERE echeance BETWEEN '" + start + "' AND '" + end + "'"
              + " AND echeancier2.montant = " + MEMBERSHIP_PRICE
              + " AND compte IN ("+FIRST_MEMBERSHIP_ACCOUNT+","+ MEMBERSHIP_N_ACCOUNT+","+ MEMBERSHIP_NN_ACCOUNT+")"
              + " AND adherent != 0"
              + " AND adherent NOT IN ("
              + " SELECT eleve.idper FROM commande_cours, commande, eleve"
              + " WHERE datedebut >= '" + start + "' AND datedebut <= '" + end + "'"
              + " AND commande_cours.idcmd = commande.id"
              + " AND commande.adh = eleve.idper"
              + " AND commande_cours.debut != '00:00:00')";
    } else  if (m.equals("total_number_of_students")) {
      return "SELECT count(DISTINCT adherent) FROM echeancier2"
              + " WHERE echeance BETWEEN '" + start + "' AND '" + end + "'"
              + " AND echeancier2.montant = " + MEMBERSHIP_PRICE
              + " AND compte IN ("+FIRST_MEMBERSHIP_ACCOUNT+","+ MEMBERSHIP_N_ACCOUNT+","+ MEMBERSHIP_NN_ACCOUNT+")"
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
      return "SELECT DISTINCT adherent, prenom, nom FROM echeancier2, personne"
              + " WHERE echeance BETWEEN '" + start + "' AND '" + end + "'"
              + " AND echeancier2.montant = " + MEMBERSHIP_PRICE
              + " AND echeancier2.compte IN ("+FIRST_MEMBERSHIP_ACCOUNT+","+ MEMBERSHIP_N_ACCOUNT+","+ MEMBERSHIP_NN_ACCOUNT+")"
              + " AND echeancier2.paye = false"
              + " AND echeancier2.adherent = personne.id";
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


  void printErrors(String m, String query) throws SQLException {
    out.println("<h3 id=\""+ navId + "\"><a href=\"#top\">^ </a>"+m+"</h3>");
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
   * @param args
   * @throws SQLException
   */
  public static void main(String[] args) throws SQLException {
    if (args.length != 1) {
      System.err.println(args.length +" error 1");
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
  protected void setSummaryDetail(StringBuilder nav) {
    addEntry(nav, MessageUtil.getMessage("statistics.members.without.date.of.birth"));
    addEntry(nav, "Adhésions non payées");
    addEntry(nav, "Personnes avec erreur adhésion ou ligne d'adhésion incorrecte");
    addEntry(nav, "Nombre d'adhérents");
    addEntry(nav, "Répartition des élèves par age");
    addEntry(nav, "Répartition des élèves par sexe");
    addEntry(nav, "Répartition géographique des élèves");
    addEntry(nav, "Nombre d'élèves par instrument pour les cours individuels");
    incId();
    addEntry(nav, "Nombre d'élèves par activité pour les cours collectifs");
    incId();
    addEntry(nav, "Répartition des élèves par formule (module)");
    addEntry(nav, "Répartition par niveau des élèves en formation pro");
    addEntry(nav, MessageUtil.getMessage("statistics.hours.of.training"));
    addEntry(nav, MessageUtil.getMessage("statistics.total.hours.of.studio"));
    addEntry(nav, MessageUtil.getMessage("statistics.hours.of.studio.by.type"));

  }

  private void incId() {
    int nbEstab = estabList.size();
    if (nbEstab > 1) {
      navId += nbEstab;
    }
  }

}

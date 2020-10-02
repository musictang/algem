/*
 * @(#)StatisticsPlugin.java	2.17.3 0/06/2020
 *
 * Copyright (c) 1999-2020 Musiques Tangentes. All Rights Reserved.
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
import java.util.Map;
import net.algem.accounting.OrderLineIO;
import net.algem.config.AgeRange;
import net.algem.contact.AddressIO;
import net.algem.contact.PersonIO;
import net.algem.contact.member.MemberIO;
import net.algem.course.Module;
import net.algem.course.ModuleIO;
import net.algem.edition.StatElement;
import net.algem.edition.Statistics;
import net.algem.edition.StatisticsDefault;
import net.algem.edition.StatisticsFactory;
import net.algem.planning.DateFr;
import net.algem.planning.Schedule;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;
import net.algem.util.MessageUtil;
import net.algem.util.model.GemList;
import net.algem.util.model.Model;

/**
 * Stats plugin CC-MDL. Pour Algem à partir de la version 2.10.0
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @version 2.17.3
 * @since 1.0.0 28/02/2013
 */
public class StatisticsPlugin
        extends Statistics {

//  private static final int FIRST_MEMBERSHIP_ACCOUNT = 7;
    private static final int MEMBERSHIP_PRICE = 1500;
//  private static final int MEMBERSHIP_N_ACCOUNT = 15;
//  private static final int MEMBERSHIP_NN_ACCOUNT = 16;

    @Override
    public void makeStats() throws SQLException {

        out.println("<h2>©  STATISTIQUES PERSONNALISÉES  ©</h2>");
        super.makeStats();
//    header();

        for (StatElement entry : statList) {

            switch (entry.getKey()) {
                case 8:
                    printTitle(entry.getLabel());
                    printTableIntResult(getQuery("students_by_activity"));
                    break;
                case 9:
                    printTitle(entry.getLabel());
                    listPersons(getQuery("payers_without_address"));
                    break;
                case 10:
                    printTitle(entry.getLabel());
                    listPersons(getQuery("debtors"));
                    break;
                case 11:
                    printTitle(entry.getLabel());
                    out.println("\n\t\t<table>");
                    out.println("\n\t\t<tr><th>Hommes</th><td>" + getIntResult(getQuery("number_of_men_members")) + "</td></tr>");
                    out.println("\n\t\t<tr><th>Femmes</th><td>" + getIntResult(getQuery("number_of_women_members")) + "</td></tr>");
                    out.println("\n\t\t<tr><th>Total</th><td>" + getIntResult(getQuery("total_number_of_students")) + "</td></tr>");
                    out.println("\n\t\t</table>");
                    break;
                case 12:
                    /* catégorie prof */
                    printTitle(entry.getLabel());
                    printTableIntResult(getQuery("members_by_occupational"));
                    break;
                case 13:
                    printTitle(entry.getLabel());
                    out.println("\n\t\t<table class='list'>");
                    ResultSet rs = dc.executeQuery(getQuery("members_by_location"));
                    int total = 0;
                    while (rs.next()) {
                        total += rs.getInt(3);
                        out.println("\n\t\t\t<tr><th>" + rs.getString(1) + "</th><td>" + rs.getString(2) + "</td>");
                        out.println("<td>" + rs.getInt(3) + "</td></tr>");
                    }
                    out.println("\n\t\t\t<tr><td colspan='3'>Total : " + total + "</td></tr>");
                    out.println("\n\t\t</table>");
                    break;

                case 14:
                    printTitle(entry.getLabel());
                    printTimeResult(getQuery("total_hours_of_studio"));
                    break;
                case 15:
                    printTitle(entry.getLabel());
                    printTableTimeResult(getQuery("hours_of_studio_by_type"));
                    break;
                case 16:
                    printTitle(entry.getLabel());
                    printTimeResult(getQuery("hours_of_training"));
                    break;
                case 17:
                    /* ex 4 erreur adhésions */
                    printTitle(entry.getLabel());
                    listPersons(getQuery("erreurs adhesions"));
                    break;
                case 18:
                    /* ex 5 nombre élèves */
                    printTitle(entry.getLabel());
                    List<AgeRange> ages = dataCache.getList(Model.AgeRange).getData();
                    out.println("\n\t\t<table class='list'>");
                    out.println("\n\t<tr><th>Adhérents ne prenant pas de cours</th><td>" + getIntResult(getQuery("total_number_of_members_not_students")) + "</td></tr>");
                    out.println("\n\t<tr><th>Adhérents prenant des cours</th><td>" + getIntResult(getQuery("total_number_of_students")) + "</td></tr>");
                    if (ages != null) {
                        for (AgeRange a : ages) {
                            if (!a.getCode().equals("-")) {
                                out.print("<tr><th>" + a.getAgemin() + "-" + a.getAgemax() + " ans</th>");

                                out.print("<td>" + getIntResult(getQuery("eleves_par_age", a.getAgemin(), a.getAgemax())) + "</td></tr>");
                            }
                        }
                    }
                    out.println("\n\t<tr><th>Total (élèves ou non)</th><td>" + getIntResult(getQuery("total_number_of_members")) + "</td></tr>");
                    out.println("\n\t\t</table>");
                    break;
                case 19:
                    printTitle(entry.getLabel());
                    listPersons(getQuery("date naissance nulle"));
                    break;
                case 20:
                    /* ex 7 répartition par sexe */
                    printTitle(entry.getLabel());
                    out.print("\n\t\t<table class='list'>");
                    out.print("<tr><th>Hommes</th><td>" + getIntResult(getQuery("number_of_men_students")) + "</td></tr>");
                    out.print("<tr><th>Femmes</th><td>" + getIntResult(getQuery("number_of_women_students")) + "</td></tr>");
                    out.println("\n\t\t<tr><th>Total</th><td>" + getIntResult(getQuery("total_number_of_students")) + "</td></tr>");
                    out.println("\n\t\t</table>");
                    break;
                case 21:
                    /* ex 8 répartition géographique */
                    int limite_ccmdl = getIntResult(getQuery("number_of_students_limite_ccmdl"));
                    printTitle(entry.getLabel());
                    out.println("\n\t\t<table class='list'>");
                    out.print("<tr><th>Intercommunalité</th><td>" + (getIntResult(getQuery("number_of_students_in_ccmdl")) - limite_ccmdl) + "</td></tr>");
                    out.print("<tr><th>Hors intercommunalité</th><td>" + (getIntResult(getQuery("number_of_students_outside")) + limite_ccmdl) + "</td></tr>");
                    out.print("<tr><th>Hors intercom mais département Rhone</th><td>" + getIntResult(getQuery("number_of_students_outside_but_rhone")) + "</td></tr>");
                    out.println("\n\t\t</table>");
                    break;
                case 22:
                    /* ex 9 par instrument */
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

                case 23:
                    /* ex 10 par formule */
                    GemList modules = dataCache.getList(Model.Module);
                    printTitle(entry.getLabel());
                    out.println("<cite>(seuls les élèves effectivement présents sur une plage de cours sont comptabilisés)</cite>");
                    out.println("\n\t\t<table class='list'>");

                    for (int i = 0; i < modules.getSize(); i++) {
                        Module m = (Module) modules.getElementAt(i);
                        out.println("\t\t\t<tr><th>" + m.getTitle() + "</th><td>" + getIntResult(getQuery(m.getId())) + "</td></tr>");
                    }
                    out.print("\n\t\t</table>");
                    break;
                case 24:
                    /* ex 11 par niveau formation pro */
                    printTitle(entry.getLabel());
                    out.println("\n\t\t<table class='list'>");
                    /*
                     */
                    out.print("\n\t\t</table>");
                    break;

                case 25:
                    /* par asso */
                    printTitle(entry.getLabel());
                    out.println("\n\t\t<table class='list'>");
                    ResultSet rs2 = dc.executeQuery(getQuery("members_by_association"));
                    String curAsso = "";
                    int asso = 0;
                    total = 0;
                    int totalasso = 0;
                    while (rs2.next()) {
                        if (asso != 0 && asso != rs2.getInt(4)) {
                            out.println("\n\t\t\t<tr><th style='text-align:right' colspan='2'>" + totalasso + "</th>");
                            out.println("\n\t\t\t<tr><th style='text-align:center' colspan='2'>" + rs2.getString(5) + "</th>");
                            totalasso = 0;
                        } else if (asso == 0) {
                            out.println("\n\t\t\t<tr><th style='text-align:center' colspan='2'>" + rs2.getString(5) + "</th>");
                        }
                        out.println("\n\t\t\t<tr><th>" + rs2.getInt(1) + "</th><td>" + rs2.getString(3) + " " + rs2.getString(2) + "</td>");

                        asso = rs2.getInt(4);
                        totalasso += 1;
                        total++;
                    }
                    out.println("\n\t\t\t<tr><th>Total</th><td>" + totalasso + "</td>");
                    out.println("\n\t\t\t<tr><td colspan='2'>Total : " + total + "</td></tr>");
                    out.println("\n\t\t</table>");
                    break;
                case 26: /*  par cycles */
                    printTitle(entry.getLabel());
                    out.println("\n\t\t<table class='list'>");
                    out.println("\t\t\t<tr><th>Curus</th><th>&lt; 18 ans</th><th>&ge; 18 ans</th></tr>");
                    out.println("\t\t\t<tr><th>EVEIL</th><td>" + getIntResult(getQuery("eveil",null, 0)) + "</td><td>" + getIntResult(getQuery("eveil", null, 18)) + "</td></tr>");
                    out.println("\t\t\t<tr><th>CYCLE 1</th><td>" + getIntResult(getQuery("cycle",1, 0)) + "</td><td>" + getIntResult(getQuery("cycle", 1, 18)) + "</td></tr>");
                    out.println("\t\t\t<tr><th>CYCLE 2</th><td>" + getIntResult(getQuery("cycle",2, 0)) + "</td><td>" + getIntResult(getQuery("cycle", 2, 18)) + "</td></tr>");
                    out.println("\t\t\t<tr><th>CYCLE 3</th><td>" + getIntResult(getQuery("cycle",3, 0)) + "</td><td>" + getIntResult(getQuery("cycle", 3, 18)) + "</td></tr>");
                    out.println("\t\t\t<tr><th>Hors cursus (avec Eveil)</th><td>" + getIntResult(getQuery("horscycle",3, 0)) + "</td><td>" + getIntResult(getQuery("horscycle", 3, 18)) + "</td></tr>");
                    /*
                     */
                    out.print("\n\t\t</table>");
                    break;

            } // end switch

        } // end for loop
        footer();
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
//defaults
        } else if (m.equals("payers_without_address")) {
            return "SELECT DISTINCT e.payeur,p.prenom,p.nom "
                    + " FROM " + OrderLineIO.TABLE + " e JOIN " + PersonIO.TABLE + " p ON (e.payeur = p.id)"
                    + " WHERE e.echeance BETWEEN '" + start + "' AND '" + end + "'"
                    //+ " AND e.compte IN (" + MEMBERSHIP_ACCOUNT + "," + PRO_MEMBERSHIP_ACCOUNT + ")"
                    + " AND p.organisation = 0"
                    + " AND e.payeur NOT IN (SELECT idper FROM adresse) ORDER BY p.nom,p.prenom";
        } else if (m.equals("debtors")) {
            return "SELECT DISTINCT e.adherent,p.prenom,p.nom"
                    + " FROM " + OrderLineIO.TABLE + " e  JOIN " + PersonIO.TABLE + " p ON (e.adherent = p.id)"
                    + " WHERE e.echeance BETWEEN '" + start + "' AND '" + end + "'"
                    //+ " AND e.compte IN (" + MEMBERSHIP_ACCOUNT + "," + PRO_MEMBERSHIP_ACCOUNT + ")"
                    + " AND e.paye = false ORDER BY p.nom,p.prenom";
        } else if (m.equals("total_number_of_members")) {
            return "SELECT count(DISTINCT adherent) FROM " + OrderLineIO.TABLE
                    + " WHERE echeance BETWEEN '" + start + "' AND '" + end + "'";
            //+ " AND compte IN (" + MEMBERSHIP_ACCOUNT + "," + PRO_MEMBERSHIP_ACCOUNT + ")";
        } else if (m.equals("number_of_men_members")) {
            return "SELECT count(DISTINCT e.adherent) FROM " + OrderLineIO.TABLE + " e, " + PersonIO.TABLE + " p"
                    + " WHERE p.id = e.adherent"
                    + " AND e.echeance BETWEEN '" + start + "' AND '" + end + "'"
                    //+ " AND e.compte IN (" + MEMBERSHIP_ACCOUNT + "," + PRO_MEMBERSHIP_ACCOUNT + ")"
                    + " AND (trim(p.civilite) = 'M' OR p.civilite = '')";
        } else if (m.equals("number_of_women_members")) {
            return "SELECT count(DISTINCT e.adherent) FROM " + OrderLineIO.TABLE + " e, " + PersonIO.TABLE + " p"
                    + " WHERE p.id = e.adherent"
                    + " AND e.echeance BETWEEN '" + start + "' AND '" + end + "'"
                    //+ " AND e.compte IN (" + MEMBERSHIP_ACCOUNT + "," + PRO_MEMBERSHIP_ACCOUNT + ")"
                    + " AND (trim(p.civilite) = 'Mme' OR p.civilite = 'Mlle')";
        } else if (m.equals("members_by_occupational")) {
            return "SELECT m.profession, count(DISTINCT e.adherent) FROM " + OrderLineIO.TABLE + " e, " + MemberIO.TABLE + " m"
                    + " WHERE m.idper = e.adherent"
                    + " AND e.echeance BETWEEN '" + start + "' AND '" + end + "'"
                    //+ " AND e.compte  IN (" + MEMBERSHIP_ACCOUNT + "," + PRO_MEMBERSHIP_ACCOUNT + ")"
                    + " GROUP BY m.profession";
        } else if (m.equals("members_by_location")) {
            return "SELECT a.ville, a.cdp, count(DISTINCT e.adherent) FROM " + OrderLineIO.TABLE + " e, " + AddressIO.TABLE + " a"
                    + " WHERE e.echeance BETWEEN '" + start + "' AND '" + end + "'"
                    //+ " AND e.compte IN (" + MEMBERSHIP_ACCOUNT + "," + PRO_MEMBERSHIP_ACCOUNT + ")"
                    + " AND (e.payeur = a.idper OR e.adherent = a.idper)"
                    + " GROUP BY a.cdp,a.ville ORDER BY a.cdp,a.ville";
        } else if ("students_by_activity".equals(m)) {
            return "SELECT c.titre, count(distinct pg.adherent) FROM plage pg JOIN planning p ON (pg.idplanning = p.id)"
                    + " JOIN action a ON (p.action = a.id) JOIN cours c ON (a.cours = c.id)"
                    + " WHERE p.jour BETWEEN '" + start + "' AND '" + end + "'"
                    + " AND pg.debut >= p.debut"
                    + " AND pg.fin <= p.fin"
                    + " AND p.ptype IN (" + Schedule.COURSE + "," + Schedule.TRAINING + "," + Schedule.WORKSHOP + ")"
                    + " GROUP BY c.titre";
//defaults
        } else if (m.equals("erreurs adhesions")) {
            return "SELECT DISTINCT eleve.idper, personne.prenom, personne.nom"
                    + " FROM commande_cours, commande, eleve, personne"
                    + " WHERE datedebut BETWEEN '" + start + "' AND '" + end + "'"
                    + " AND commande_cours.idcmd = commande.id"
                    + " AND commande.adh = eleve.idper"
                    //        + " AND commande_cours.debut != '00:00:00'"
                    + " AND commande_cours.debut = '00:00:00'"
                    + " AND eleve.idper = personne.id"
                    //        + " AND eleve.idper NOT IN("
                    //        + " SELECT adherent FROM echeancier2"
                    //        + " WHERE echeance BETWEEN '" + start + "' AND '" + end + "'"
                    //        //+ " AND compte  IN (" + MEMBERSHIP_ACCOUNT + "," + PRO_MEMBERSHIP_ACCOUNT + ")"
                    //        + " AND montant = " + MEMBERSHIP_PRICE + ")"
                    + " ORDER BY personne.nom,personne.prenom";
        } else if (m.equals("total_number_of_members_not_students")) {
            return "SELECT count(DISTINCT adherent) FROM echeancier2"
                    + " WHERE echeance BETWEEN '" + start + "' AND '" + end + "'"
                    + " AND echeancier2.montant = " + MEMBERSHIP_PRICE
                    //+ " AND compte  IN (" + MEMBERSHIP_ACCOUNT + "," + PRO_MEMBERSHIP_ACCOUNT + ")"
                    + " AND adherent != 0"
                    + " AND adherent NOT IN ("
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
                    + " AND (trim(personne.civilite) = 'M' OR personne.civilite = '' OR personne.civilite is NULL)";

        } else if (m.equals("number_of_women_students")) {
            return "SELECT count(DISTINCT eleve.idper) FROM commande_cours, commande, eleve, personne"
                    + " WHERE datedebut >= '" + start + "' AND datedebut <= '" + end + "'"
                    + " AND commande_cours.idcmd = commande.id"
                    + " AND commande.adh = eleve.idper"
                    + " AND commande_cours.debut != '00:00:00'"
                    + " AND personne.id = eleve.idper"
                    + " AND (trim(personne.civilite) = 'Mme' OR personne.civilite = 'Mlle')";
        } else if (m.equals("number_of_students_in_ccmdl")) {
            return "SELECT count(DISTINCT eleve.idper) FROM commande_cours, commande, eleve, adresse"
                    + " WHERE datedebut >= '" + start + "' AND datedebut <= '" + end + "'"
                    + " AND commande_cours.idcmd = commande.id"
                    + " AND commande.adh = eleve.idper"
                    + " AND commande_cours.debut != '00:00:00'"
                    + " AND (eleve.idper = adresse.idper OR eleve.famille = adresse.idper)"
                    + " AND adresse.cdp IN ('42140','69440','69590','69610','69690','69770','69850','69930')";
        } else if (m.equals("number_of_students_limite_ccmdl")) {
            return "SELECT count(DISTINCT eleve.idper) FROM commande_cours, commande, eleve, adresse"
                    + " WHERE datedebut >= '" + start + "' AND datedebut <= '" + end + "'"
                    + " AND commande_cours.idcmd = commande.id"
                    + " AND commande.adh = eleve.idper"
                    + " AND commande_cours.debut != '00:00:00'"
                    + " AND (eleve.idper = adresse.idper OR eleve.famille = adresse.idper)"
                    //+ " AND adresse.cdp IN ('42140','69690')"
                    + " AND (adresse.ville = 'COURZIEU' or adresse.ville = 'ST JULIEN SUR BIBOST' or adresse.ville = 'CHAZELLES SUR LYON')";
        } else if (m.equals("number_of_students_outside")) {
            String query = "SELECT count(DISTINCT eleve.idper) FROM commande_cours, commande, eleve, adresse"
                    + " WHERE datedebut >= '" + start + "' AND datedebut <= '" + end + "'"
                    + " AND commande_cours.idcmd = commande.id"
                    + " AND commande.adh = eleve.idper"
                    + " and commande_cours.debut != '00:00:00'"
                    + " AND (eleve.idper = adresse.idper OR eleve.famille = adresse.idper)"
                    + " AND adresse.cdp NOT IN ('42140','69440','69590','69610','69690','69770','69850','69930')";
            System.out.println("eleve hors inerco query=" + query);
            return query;
        } else if (m.equals("number_of_students_outside_but_rhone")) {
            return "SELECT count(DISTINCT eleve.idper) FROM commande_cours, commande, eleve, adresse"
                    + " WHERE datedebut >= '" + start + "' AND datedebut <= '" + end + "'"
                    + " AND commande_cours.idcmd = commande.id"
                    + " AND commande.adh = eleve.idper"
                    + " AND commande_cours.debut != '00:00:00'"
                    + " AND (eleve.idper = adresse.idper OR eleve.famille = adresse.idper)"
                    + " AND adresse.cdp NOT IN ('42140','69440','69590','69610','69690','69770','69850','69930')"
                    + " AND SUBSTRING(adresse.cdp,1,2) = '69'";
        } else if (m.equals("members_by_association")) {
            return "select DISTINCT p.id, p.nom, p.prenom, o.idper, o.nom from commande c, commande_cours cc, personne p, organisation o, eleve e"
                    + " WHERE datedebut >= '" + start + "' AND datedebut <= '" + end + "'"
                    + " AND cc.idcmd = c.id"
                    + " AND c.adh = e.idper"
                    + " AND cc.debut != '00:00:00'"
                    + " AND e.idper = p.id AND o.idper = e.payeur"
                    + " ORDER by o.nom, p.nom";
        } else if (m.equals("date naissance nulle")) {
            return "SELECT DISTINCT eleve.idper, personne.prenom, personne.nom"
                    + " FROM eleve, personne"
                    + " WHERE eleve.idper = personne.id"
                    + " AND datenais is null"
                    + " ORDER BY personne.nom,personne.prenom";
        }
        return null;
    }

    @Override
    public String getQuery(String m, Object a1, Object a2) {
        if (m.equals("hours_of_rehearsal")) {
            return super.getQuery(m, a1, a2);
        }

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
                    + " AND planning.ptype IN(" + Schedule.COURSE + "," + Schedule.WORKSHOP + "," + Schedule.TRAINING + ")"
                    //        + " AND planning.lieux = salle.id"
                    + " AND cours.collectif = false";

            query += " GROUP BY cours.titre";
            return query;
        } else if ("eveil".equals(m)) {
            int min = (a2.toString().equals("0")) ? 0 : 18;
            int max = (a2.toString().equals("0")) ? 17 : 100;
            
            return "SELECT count(DISTINCT eleve.idper) "
                    + "FROM eleve, commande, commande_cours, commande_module, module"
                    + " WHERE eleve.idper = commande.adh"
                    + " AND commande.id = commande_cours.idcmd"
                    + " AND commande_cours.module = commande_module.id"
                    + " AND commande_module.module = module.id"
                    + " AND commande_cours.datedebut between '" + start + "' AND '" + end + "'"
                    + " AND extract(year from age(commande_cours.datedebut,datenais)) >= " + min
                    + " AND extract(year from age(commande_cours.datedebut,datenais)) <= " + max
                    + " AND (module.id = 11 OR module.id=24)"
                    + " AND commande_cours.debut != '00:00:00'";
        } else if ("cycle".equals(m)) {
            int min = (a2.toString().equals("0")) ? 0 : 18;
            int max = (a2.toString().equals("0")) ? 17 : 100;
            return "SELECT count(DISTINCT eleve.idper) "
                    + "FROM eleve, commande, commande_cours, commande_module, module"
                    + " WHERE eleve.idper = commande.adh"
                    + " AND commande.id = commande_cours.idcmd"
                    + " AND commande_cours.module = commande_module.id"
                    + " AND commande_module.module = module.id"
                    + " AND commande_cours.datedebut between '" + start + "' AND '" + end + "'"
                    + " AND extract(year from age(commande_cours.datedebut,datenais)) >= " + min
                    + " AND extract(year from age(commande_cours.datedebut,datenais)) <= " + max
                    + " AND eleve.niveau = " + a1
                    + " AND commande_cours.debut != '00:00:00'";
            
        } else if ("horscycle".equals(m)) {
            int min = (a2.toString().equals("0")) ? 0 : 18;
            int max = (a2.toString().equals("0")) ? 17 : 100;
            return "SELECT count(DISTINCT eleve.idper) "
                    + "FROM eleve, commande, commande_cours, commande_module, module"
                    + " WHERE eleve.idper = commande.adh"
                    + " AND commande.id = commande_cours.idcmd"
                    + " AND commande_cours.module = commande_module.id"
                    + " AND commande_module.module = module.id"
                    + " AND commande_cours.datedebut between '" + start + "' AND '" + end + "'"
                    + " AND extract(year from age(commande_cours.datedebut,datenais)) >= " + min
                    + " AND extract(year from age(commande_cours.datedebut,datenais)) <= " + max
                    + " AND eleve.niveau = 0 AND eleve.pratique = 0"
                    + " AND commande_cours.debut != '00:00:00'";
            
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
     * Run statistics. The beginning year must be included in argument (ex. :
     * 2014 for 2014-2015)
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
        //from Default
        statList.add(new StatElement(2, MessageUtil.getMessage("statistics.number.of.students"), true));
        //statList.add(new StatElement(3, MessageUtil.getMessage("statistics.distribution.between.amateurs.pros"), true));
        //statList.add(new StatElement(4, MessageUtil.getMessage("statistics.list.of.pro.students"), true));
        statList.add(new StatElement(5, MessageUtil.getMessage("statistics.students.by.location"), true));
        statList.add(new StatElement(6, MessageUtil.getMessage("statistics.number.of.hours.of.rehearsal"), true));
        statList.add(new StatElement(7, MessageUtil.getMessage("statistics.number.of.rehearsing.people"), true));
        statList.add(new StatElement(8, MessageUtil.getMessage("statistics.students.by.activity"), true));
        statList.add(new StatElement(9, MessageUtil.getMessage("statistics.payers.without.address"), true));
        statList.add(new StatElement(10, MessageUtil.getMessage("statistics.debtors"), true));
//    statList.add(new StatElement(11, MessageUtil.getMessage("statistics.total.number.of.members"), true));
        statList.add(new StatElement(12, MessageUtil.getMessage("statistics.members.by.occupational"), true));
        //statList.add(new StatElement(13, MessageUtil.getMessage("statistics.members.by.location"), true));
        statList.add(new StatElement(14, MessageUtil.getMessage("statistics.total.hours.of.studio"), true));
        statList.add(new StatElement(15, MessageUtil.getMessage("statistics.hours.of.studio.by.type"), true));
        statList.add(new StatElement(16, MessageUtil.getMessage("statistics.hours.of.training"), true));

        statList.add(new StatElement(17 /* 4 */, "Personnes avec adhésion incomplète", true));
        statList.add(new StatElement(18 /* 5 */, "Nombre d'adhérents", true));
        statList.add(new StatElement(19, "Elèves sans date de naissance", true));
        statList.add(new StatElement(20 /* 7 */, "Répartition des élèves par sexe", true));
        statList.add(new StatElement(21 /* 8 */, "Répartition géographique des élèves", true));
        statList.add(new StatElement(22 /* 9 */, "Nombre d'élèves par instrument pour les cours individuels", true));
        statList.add(new StatElement(23 /* 10 */, "Répartition des élèves par formule (formule)", true));
        statList.add(new StatElement(24 /* 11 */, "Répartition par niveau des élèves en formation pro", true));
        statList.add(new StatElement(25, "Elèves par association", true));
        statList.add(new StatElement(26, "Répartition par cycles", true));

    }

    private void incId() {
        int nbEstab = estabList.size();
        if (nbEstab > 1) {
            navId += nbEstab;
        }
    }

}

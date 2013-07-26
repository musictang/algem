/*
 * @(#)StatisticsDefault.java	2.8.k 19/07/13
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

import java.sql.SQLException;
import net.algem.util.MessageUtil;

/**
 * Default file export for statistics.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.k
 * @since 2.6.a 12/10/2012
 */
public class StatisticsDefault
        extends Statistics
{

  public StatisticsDefault() {
  }

  @Override
  public void makeStats() throws SQLException {
    super.makeStats();
    separate();
    printIntResult(MessageUtil.getMessage("statistics.payers.without.address"), getQuery("payers_without_address"));
    printIntResult(MessageUtil.getMessage("statistics.debtors"), getQuery("debtors"));
    separate();
    printIntResult(MessageUtil.getMessage("statistics.total.number.of.members"), getQuery("total_number_of_members"));
    printIntResult(MessageUtil.getMessage("statistics.number.of.men.members"), getQuery("number_of_men_members"));
    printIntResult(MessageUtil.getMessage("statistics.number.of.women.members"), getQuery("number_of_women_members"));
    separate();
    printTableIntResult(MessageUtil.getMessage("statistics.members.by.occupational"), getQuery("members_by_occupational"));
    separate();
    printTableIntResult(MessageUtil.getMessage("statistics.members.by.location"), getQuery("members_by_location"));
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
            || m.equals("hours_teacher_of_collective_lessons")) {
      return super.getQuery(m);
    }
    if (m.equals("payers_without_address")) {
      return "SELECT DISTINCT payeur FROM echeancier2"
              + " WHERE echeance >= '" + start + "' AND echeance <= '" + end + "'"
//              + " AND echeancier2.montant = 1000"
              + " AND compte = " + MEMBERSHIP_ACCOUNT
              + " AND payeur NOT IN (SELECT idper FROM adresse)";
    }
    if (m.equals("debtors")) {
      return "SELECT DISTINCT adherent, nom, prenom FROM echeancier2,personne"
              + " WHERE echeance BETWEEN '" + start + "' AND '" + end + "'"
//              + " AND echeancier2.montant = 1000"
              + " AND compte = " + MEMBERSHIP_ACCOUNT
              + " AND echeancier2.paye = false"
              + " AND echeancier2.adherent = personne.id";
    }
    if (m.equals("total_number_of_members")) {
      return "SELECT count(DISTINCT adherent) FROM echeancier2"
              + " WHERE echeance BETWEEN '" + start + "' AND '" + end + "'"
//              + " AND echeancier2.montant = 1000"
              + " AND compte = " + MEMBERSHIP_ACCOUNT;
    }
    if (m.equals("number_of_men_members")) {
      return "SELECT count(DISTINCT adherent) FROM echeancier2, personne"
              + " WHERE personne.id = echeancier2.adherent"
              + " AND echeance BETWEEN '" + start + "' AND '" + end + "'"
//              + " AND echeancier2.montant=1000"
              + " AND compte = " + MEMBERSHIP_ACCOUNT
              + " AND (trim(personne.civilite) = 'M' OR personne.civilite = '')";
    }
    if (m.equals("number_of_women_members")) {
      return "SELECT count(DISTINCT adherent) FROM echeancier2, personne"
              + " WHERE personne.id = echeancier2.adherent"
              + " AND echeance BETWEEN '" + start + "' AND '" + end + "'"
//              + " AND echeancier2.montant=1000"
              + " AND compte = " + MEMBERSHIP_ACCOUNT
              + " AND (trim(personne.civilite) = 'Mme' OR personne.civilite = 'Mlle')";
    }
    if (m.equals("members_by_occupational")) {
      return "SELECT profession, count(DISTINCT adherent) FROM echeancier2,eleve"
              + " WHERE eleve.idper=echeancier2.adherent"
              + " AND echeance BETWEEN '" + start + "' AND '" + end + "'"
//              + " AND montant = 1000"
              + " AND compte  = " + MEMBERSHIP_ACCOUNT
              + " GROUP BY profession";
    }
    if (m.equals("members_by_location")) {
      return "SELECT adresse.ville, count(DISTINCT echeancier2.adherent) FROM echeancier2, adresse"
              + " WHERE echeancier2.echeance BETWEEN '" + start + "' AND '" + end + "'"
//              + " AND echeancier2.montant = 1000"
              + " AND echeancier2.compte = " + MEMBERSHIP_ACCOUNT
              + " AND (echeancier2.payeur = adresse.idper OR echeancier2.adherent = adresse.idper)"
              + " GROUP BY adresse.ville ORDER BY adresse.ville";
    }
    return null;
  }

  @Override
  protected String getQuery(String m, Object a1, Object a2) {
    return super.getQuery(m, a1, a2);
  }

  @Override
  protected void setSummaryDetail(StringBuilder nav) {
    addEntry(nav, MessageUtil.getMessage("statistics.members.without.date.of.birth"));
    addEntry(nav, MessageUtil.getMessage("statistics.number.of.students"));
    addEntry(nav, MessageUtil.getMessage("statistics.distribution.between.amateurs.pros"));
    addEntry(nav, MessageUtil.getMessage("statistics.list.of.pro.students"));
    addEntry(nav, MessageUtil.getMessage("statistics.city.distribution"));
    addEntry(nav, MessageUtil.getMessage("statistics.number.of.hours.of.rehearsal"));
    addEntry(nav, MessageUtil.getMessage("statistics.number.of.rehearsing.people"));
    addEntry(nav, MessageUtil.getMessage("statistics.payers.without.address"));
    addEntry(nav, MessageUtil.getMessage("statistics.debtors"));
    addEntry(nav, MessageUtil.getMessage("statistics.total.number.of.members"));
    addEntry(nav, MessageUtil.getMessage("statistics.number.of.men.members"));
    addEntry(nav, MessageUtil.getMessage("statistics.number.of.women.members"));
    addEntry(nav, MessageUtil.getMessage("statistics.members.by.occupational"));
    addEntry(nav, MessageUtil.getMessage("statistics.members.by.location"));
  }
}

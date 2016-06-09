/*
 * @(#)StatisticsDefault.java	2.10.0 08/06/2016
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

import java.sql.SQLException;
import java.util.ArrayList;
import net.algem.accounting.OrderLineIO;
import net.algem.contact.AddressIO;
import net.algem.contact.PersonIO;
import net.algem.contact.member.MemberIO;
import net.algem.util.MessageUtil;

/**
 * Default file export for statistics.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.10.0
 * @since 2.6.a 12/10/2012
 */
public class StatisticsDefault
  extends Statistics {

  public StatisticsDefault() {
  }

  @Override
  public void makeStats() throws SQLException {
    super.makeStats();
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
          printTitle(entry.getLabel());
          printTableIntResult(getQuery("members_by_occupational"));
          break;
        case 12:
          printTitle(entry.getLabel());
          printTableIntResult(getQuery("members_by_location"));
          break;
        case 13:
          printTitle(entry.getLabel());
          printTimeResult(getQuery("total_hours_of_studio"));
          break;
        case 14:
          printTitle(entry.getLabel());
          printTableTimeResult(getQuery("hours_of_studio_by_type"));
          break;
        case 15:
          printTitle(entry.getLabel());
          printTimeResult(getQuery("hours_of_training"));
          break;
      }
    }

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
    }
    if (m.equals("payers_without_address")) {
      return "SELECT DISTINCT e.payeur,p.prenom,p.nom "
        + " FROM " + OrderLineIO.TABLE + " e JOIN " + PersonIO.TABLE + " p ON (e.payeur = p.id)"
        + " WHERE e.echeance BETWEEN '" + start + "' AND '" + end + "'"
        + " AND e.compte = " + MEMBERSHIP_ACCOUNT
        + " AND e.payeur NOT IN (SELECT idper FROM adresse) ORDER BY p.nom,p.prenom";
    }
    if (m.equals("debtors")) {
      return "SELECT DISTINCT e.adherent,p.prenom,p.nom"
        + " FROM " + OrderLineIO.TABLE + " e  JOIN " + PersonIO.TABLE + " p ON (e.adherent = p.id)"
        + " WHERE e.echeance BETWEEN '" + start + "' AND '" + end + "'"
        + " AND e.compte = " + MEMBERSHIP_ACCOUNT
        + " AND e.paye = false ORDER BY p.nom,p.prenom";
    }
    if (m.equals("total_number_of_members")) {
      return "SELECT count(DISTINCT adherent) FROM " + OrderLineIO.TABLE
        + " WHERE echeance BETWEEN '" + start + "' AND '" + end + "'"
        + " AND compte = " + MEMBERSHIP_ACCOUNT;
    }
    if (m.equals("number_of_men_members")) {
      return "SELECT count(DISTINCT e.adherent) FROM " + OrderLineIO.TABLE + " e, " + PersonIO.TABLE + " p"
        + " WHERE p.id = e.adherent"
        + " AND e.echeance BETWEEN '" + start + "' AND '" + end + "'"
        + " AND e.compte = " + MEMBERSHIP_ACCOUNT
        + " AND (trim(p.civilite) = 'M' OR p.civilite = '')";
    }
    if (m.equals("number_of_women_members")) {
      return "SELECT count(DISTINCT e.adherent) FROM " + OrderLineIO.TABLE + " e, " + PersonIO.TABLE + " p"
        + " WHERE p.id = e.adherent"
        + " AND e.echeance BETWEEN '" + start + "' AND '" + end + "'"
        + " AND e.compte = " + MEMBERSHIP_ACCOUNT
        + " AND (trim(p.civilite) = 'Mme' OR p.civilite = 'Mlle')";
    }
    if (m.equals("members_by_occupational")) {
      return "SELECT m.profession, count(DISTINCT e.adherent) FROM " + OrderLineIO.TABLE + " e, " + MemberIO.TABLE + " m"
        + " WHERE m.idper = e.adherent"
        + " AND e.echeance BETWEEN '" + start + "' AND '" + end + "'"
        + " AND e.compte  = " + MEMBERSHIP_ACCOUNT
        + " GROUP BY m.profession";
    }
    if (m.equals("members_by_location")) {
      return "SELECT a.ville, count(DISTINCT e.adherent) FROM " + OrderLineIO.TABLE + " e, " + AddressIO.TABLE + " a"
        + " WHERE e.echeance BETWEEN '" + start + "' AND '" + end + "'"
        + " AND e.compte = " + MEMBERSHIP_ACCOUNT
        + " AND (e.payeur = a.idper OR e.adherent = a.idper)"
        + " GROUP BY a.ville ORDER BY a.ville";
    }
    return null;
  }

  @Override
  protected String getQuery(String m, Object a1, Object a2) {
    return super.getQuery(m, a1, a2);
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
    statList.add(new StatElement(8, MessageUtil.getMessage("statistics.payers.without.address"), true));
    statList.add(new StatElement(9, MessageUtil.getMessage("statistics.debtors"), true));
    statList.add(new StatElement(10, MessageUtil.getMessage("statistics.total.number.of.members"), true));
    statList.add(new StatElement(11, MessageUtil.getMessage("statistics.members.by.occupational"), true));
    statList.add(new StatElement(12, MessageUtil.getMessage("statistics.members.by.location"), true));
    statList.add(new StatElement(13, MessageUtil.getMessage("statistics.total.hours.of.studio"), true));
    statList.add(new StatElement(14, MessageUtil.getMessage("statistics.hours.of.studio.by.type"), true));
    statList.add(new StatElement(15, MessageUtil.getMessage("statistics.hours.of.training"), true));
  }

}

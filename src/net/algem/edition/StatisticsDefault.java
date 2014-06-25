/*
 * @(#)StatisticsDefault.java	2.8.v 26/06/14
 * 
 * Copyright (c) 1999-2014 Musiques Tangentes. All Rights Reserved.
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
import net.algem.accounting.OrderLineIO;
import net.algem.contact.AddressIO;
import net.algem.contact.PersonIO;
import net.algem.contact.member.MemberIO;
import net.algem.util.MessageUtil;

/**
 * Default file export for statistics.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.v
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
    
    printTimeResult(MessageUtil.getMessage("statistics.total.hours.of.studio"), getQuery("total_hours_of_studio"));
    printTableTimeResult(MessageUtil.getMessage("statistics.hours.of.studio.by.type"), getQuery("hours_of_studio_by_type"));
    printTimeResult(MessageUtil.getMessage("statistics.hours.of.training"), getQuery("hours_of_training"));
    
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
      return "SELECT DISTINCT payeur FROM " + OrderLineIO.TABLE
              + " WHERE echeance >= '" + start + "' AND echeance <= '" + end + "'"
              + " AND compte = " + MEMBERSHIP_ACCOUNT
              + " AND payeur NOT IN (SELECT idper FROM adresse)";
    }
    if (m.equals("debtors")) {
      return "SELECT DISTINCT e.adherent, p.nom, p.prenom FROM " + OrderLineIO.TABLE + " e, " + PersonIO.TABLE + " p"
              + " WHERE e.echeance BETWEEN '" + start + "' AND '" + end + "'"
              + " AND e.compte = " + MEMBERSHIP_ACCOUNT
              + " AND e.paye = false"
              + " AND e.adherent = p.id";
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
    addEntry(nav, MessageUtil.getMessage("statistics.total.hours.of.studio"));
    addEntry(nav, MessageUtil.getMessage("statistics.hours.of.studio.by.type"));
    addEntry(nav, MessageUtil.getMessage("statistics.hours.of.training"));
  }

}

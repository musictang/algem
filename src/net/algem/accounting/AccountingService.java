/*
 * @(#)AccountingService.java	2.9.4.11 22/07/2015
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

package net.algem.accounting;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import net.algem.billing.ItemIO;
import net.algem.config.ActivableParam;
import net.algem.config.ActivableParamTableIO;
import net.algem.config.Param;
import net.algem.config.ParamTableIO;
import net.algem.config.Preference;
import net.algem.contact.PersonIO;
import net.algem.course.CourseIO;
import net.algem.course.Module;
import net.algem.course.ModuleIO;
import net.algem.enrolment.CourseOrderIO;
import net.algem.enrolment.ModuleOrderIO;
import net.algem.planning.*;
import net.algem.room.RoomIO;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;
import net.algem.util.GemLogger;
import net.algem.util.MessageUtil;

/**
 * Service class for accounting.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.11
 * @since 2.4.a 21/05/12
 */
public class AccountingService {

  private final DataConnection dc;

  public AccountingService(DataConnection dc) {
    this.dc = dc;
  }

  public Vector<PlanningLib> getPlanningLib(String start, String end, int school, boolean catchup) throws SQLException {
    String where = "WHERE jour >= '" + start + "' AND jour <= '" + end + "' AND ecole = " + school;
    if(!catchup) {
      where += " AND salle !~* 'rattrap'";
    }
    where  += " ORDER BY nomprof, jour, debut, coursid";
    return PlanningLibIO.find(where, dc);
  }

  public Vector<PlanningLib> getPlanningLib(String start, String end, int school, int teacherId, boolean catchup) throws SQLException {
    String where = "WHERE jour >= '" + start + "' AND jour <= '" + end + "' AND ecole =" + school + " AND profid = " + teacherId;
    if(!catchup) {
      where += " AND salle !~* 'rattrap'";
    }
    where += " ORDER BY jour, debut, coursid";
    return PlanningLibIO.find(where, dc);
  }

  public Vector<ScheduleRange> getCourseScheduleRange(int idplanning) throws SQLException {
    // on ne comptabilise pas les plages de pause (adherent = 0)
    return ScheduleRangeIO.find("pg WHERE pg.idplanning = " + idplanning + " AND pg.adherent > 0 ORDER BY pg.debut", dc);
  }

  public ResultSet getDetailByTechnician(String start, String end, int type) throws SQLException {
    String query = "SELECT p.jour, p.idper, p.debut, p.fin, (p.fin - p.debut) AS duree, pg.adherent"
            + " FROM " + ScheduleIO.TABLE + " p, " + ScheduleRangeIO.TABLE + " pg"
            + " WHERE pg.idplanning = p.id"
            + " AND p.jour BETWEEN '" + start + "' AND '" + end + "'"
            + " AND p.ptype = " + type
            + " ORDER BY pg.adherent,p.jour,p.debut";
    return dc.executeQuery(query);
  }
  
  public ResultSet getDetailByAdministrator(String start, String end, int type) throws SQLException {
    String query = "SELECT p.jour, p.idper, p.debut, p.fin, (p.fin - p.debut) AS duree, s.nom"
            + " FROM " + ScheduleIO.TABLE + " p, " + RoomIO.TABLE + " s"
            + " WHERE p.jour BETWEEN '" + start + "' AND '" + end + "'"
            + " AND p.ptype = " + type
            + " AND p.lieux = s.id"
            + " ORDER BY p.idper,p.jour,p.debut";
    return dc.executeQuery(query);
  }

  public ResultSet getDetailIndTeacherByMember(String start, String end, boolean catchup, int idper, int school) throws SQLException {
    String query = "SELECT p.idper, pg.adherent, p1.prenom, p1.nom, c.id, c.titre, p2.prenom, p2.nom, p.jour, pg.debut, pg.fin,(pg.fin - pg.debut) AS duree"
            + " FROM " + ScheduleIO.TABLE + " p, " 
            + ScheduleRangeIO.TABLE + " pg, " 
            + ActionIO.TABLE + " a, " 
            + CourseIO.TABLE + " c, " 
            + PersonIO.TABLE + " p1, " 
            + PersonIO.TABLE + " p2, "
            + RoomIO.TABLE + " s";
            query += (idper > 0) ? " WHERE p.idper = " + idper : " WHERE p.idper > 0";
            query += " AND pg.idplanning = p.id"
            + " AND p.jour BETWEEN '" + start + "' AND '" + end + "'"
            + " AND p.lieux = s.id"
            + " AND p.ptype IN (1,5,6)"
            + " AND p.idper = p1.id"
            + " AND pg.adherent = p2.id"
            + " AND pg.adherent > 0"
            + " AND p.action = a.id"
            + " AND a.cours = c.id "
            + " AND c.ecole = " + school
            + " AND (c.collectif = FALSE OR (c.code = 1 AND (SELECT count(id) FROM " + ScheduleRangeIO.TABLE + " WHERE idplanning = p.id) = 1))";
            if(!catchup) {
              query += " AND s.nom !~* 'rattrap'";
            }
            query += " ORDER BY p1.nom, a.cours, pg.adherent, p.jour, p.debut";
    return dc.executeQuery(query);
  }

  public ResultSet getDetailCoTeacherByMember(String start, String end, boolean catchup, int idper, int school) throws SQLException {
    String query = "SELECT p.idper, p1.prenom, p1.nom, c.id, c.titre, p.jour, p.debut, p.fin,(p.fin - p.debut) AS duree"
            + " FROM " + ScheduleIO.TABLE + " p, " 
            + ActionIO.TABLE + " a, " 
            + CourseIO.TABLE  + " c, " 
            + PersonIO.TABLE + " p1, " 
            + RoomIO.TABLE + " s";
            query += (idper > 0) ? " WHERE p.idper = " + idper : " WHERE p.idper > 0";
            query += " AND p.jour BETWEEN '" + start + "' AND '" + end + "'"
            + " AND p.lieux = s.id"
            + " AND p.ptype IN (1,5,6)"
            + " AND p.idper = p1.id"
            + " AND p.action = a.id"
            + " AND a.cours = c.id "
            + " AND c.ecole = " + school
            + " AND (((c.code IN(2,3,11,12) OR c.code > 12) AND (SELECT count(id) FROM " + ScheduleRangeIO.TABLE + " WHERE idplanning = p.id) > 0)"
            + " OR (c.code = 1 AND (SELECT count(id) FROM " + ScheduleRangeIO.TABLE + " WHERE idplanning = p.id AND debut = p.debut) > 1))";
            if(!catchup) {
              query +=  " AND s.nom !~* 'rattrap'";
            }
            query += " ORDER BY p1.nom, a.cours, p.jour, p.debut";
    return dc.executeQuery(query);
  }

  public ResultSet getDetailTeacherByDate(String start, String end, boolean catchup, int idper, int school) throws SQLException {
    String query = "SELECT DISTINCT ON (p1.nom, p1.prenom, p.jour, pg.debut)"
      + " p.idper, pg.adherent, p1.prenom, p1.nom, c.id, c.titre, p2.prenom, p2.nom, p.jour, pg.debut, pg.fin, (pg.fin - pg.debut) AS duree, a.id"
      + " FROM " + ScheduleIO.TABLE + " p, " 
            + ScheduleRangeIO.TABLE + " pg, "
            + ActionIO.TABLE + " a, " 
            + CourseIO.TABLE  + " c, " 
            + PersonIO.TABLE + " p1, " + PersonIO.TABLE + " p2, "
            + RoomIO.TABLE + " s";
    query += (idper > 0) ? " WHERE p.idper = " + idper : " WHERE p.idper > 0";
    query += " AND p.jour BETWEEN '" + start + "' AND '" + end + "'"
      + " AND p.ptype IN (1,5,6)" // cours, atelier ponctuel, stage
      + " AND p.lieux = s.id"
      + " AND p.action = a.id"
      + " AND a.cours = c.id"
      + " AND c.ecole = " + school
      + " AND p.idper = p1.id"
      + " AND pg.idplanning = p.id"
      + " AND pg.adherent = p2.id"
      + " AND pg.adherent > 0";
    if (!catchup) {
      query += " AND s.nom !~* 'rattrap'";
    }
    query += " ORDER BY p1.nom, p1.prenom,p.jour,pg.debut,a.cours";

    return dc.executeQuery(query);
  }
  
  public ResultSet getDetailTeacherByModule(String start, String end, boolean catchup, int idper, int school, List<Module> modules) throws SQLException {
    StringBuilder sb = new StringBuilder();
    for (Module m : modules) {
      sb.append(m.getId()).append(',');
    }
    sb.deleteCharAt(sb.length()-1);
    String query = "SELECT DISTINCT ON (p1.nom, p1.prenom, p.jour, pg.debut,m.id)"
      + " p.idper, p1.prenom, p1.nom, a.id, c.id, c.titre, m.id, m.titre, p.jour, pg.debut, pg.fin, (pg.fin - pg.debut) AS duree"
      + " FROM " + ScheduleIO.TABLE + " p, " 
            + ScheduleRangeIO.TABLE + " pg, "
            + ActionIO.TABLE + " a, " 
            + CourseIO.TABLE  + " c, " 
            + PersonIO.TABLE + " p1, "
            + RoomIO.TABLE + " s,"
            + CourseOrderIO.TABLE + " cc,"
            + ModuleOrderIO.TABLE + " cm,"
            + ModuleIO.TABLE + " m";
    query += (idper > 0) ? " WHERE p.idper = " + idper : " WHERE p.idper > 0";
    query += " AND p.jour BETWEEN '" + start + "' AND '" + end + "'"
      + " AND p.ptype IN ("+Schedule.COURSE+","+Schedule.WORKSHOP+","+Schedule.TRAINING // cours, atelier ponctuel, stage
      + ") AND p.lieux = s.id"
      + " AND p.action = a.id"
      + " AND a.id = cc.idaction"
      + " AND cc.module = cm.id"
      + " AND cm.module IN("+sb.toString()
      + ") AND cm.module = m.id"
      + " AND a.cours = c.id"
      + " AND c.ecole = " + school
      + " AND p.idper = p1.id"
      + " AND pg.idplanning = p.id";
    if (!catchup) {
      query += " AND s.nom !~* 'rattrap'";
    }
    query += " ORDER BY m.id,p1.nom,p1.prenom,p.jour,pg.debut,a.cours";

    return dc.executeQuery(query);
  }
  
  /**
   * Gets active accounts.
   * @return a list of accounts
   * @throws SQLException 
   */
  public List<Account> getAccounts() throws SQLException {
    return AccountIO.find(true, dc);
  }
  
  /**
   * Gets active cost accounts.
   * @return a list of cost accounts
   */
  public List<Param> getActiveCostAccounts() {
    List<ActivableParam> actives = ActivableParamTableIO.find(CostAccountCtrl.tableName, CostAccountCtrl.columnName, " WHERE " + CostAccountCtrl.columnFilter + " = TRUE", dc);
    return new ArrayList<Param>(actives);
  }
  
  /**
   * Gets categories of accounts.
   * @return a list of strings
   * @throws SQLException 
   */
  public String[] getAccountTypes() throws SQLException {
    return AccountPrefIO.findKeys(dc);
  }
  
  /**
   * Updates a preferred account.
   * @param pref the preference's key is the category to be updated
   * @throws SQLException 
   */
  public void updateAccountPref(Preference pref) throws SQLException {
    AccountPrefIO.update(pref, DataCache.getDataConnection());
  }
  
  /**
   * Checks if an account is still used elsewhere.
   * @param c account
   * @return a message if account is still used and null otherwise
   * @throws AccountDeleteException
   * @throws SQLException 
   */
  private String isAccountUsed(Param c) throws AccountDeleteException, SQLException {
    String where = "WHERE " + OrderLineIO.ACCOUNT_COLUMN + " = '" + c.getId() + "'";
    Vector<OrderLine> e = OrderLineIO.find(where, 1, dc);
    // order lines
    if (e != null && e.size() > 0) {
      return MessageUtil.getMessage("account.delete.exception.orderline");
    }
    // preferred accounts
    if (AccountPrefIO.containsAccount(c.getId(), dc)) {
      return MessageUtil.getMessage("account.delete.exception.preference");
    }
    // billing items
    if (new ItemIO(dc).find("WHERE compte = " + c.getId()).size() > 0) {
      return MessageUtil.getMessage("account.delete.exception.billing");
    }
    // journal
    if (JournalAccountIO.find(c.getId(), dc) != null) {
      return MessageUtil.getMessage("account.delete.exception.journal");
    }
    return null;
  }
  
  private String isCostAccountUsed(Param p) throws AccountDeleteException, SQLException {
    String where = "WHERE " + OrderLineIO.COST_COLUMN + " = '" + p.getKey() + "'";
    Vector<OrderLine> e = OrderLineIO.find(where, 1, dc);
     if (e != null && e.size() > 0) {
      return MessageUtil.getMessage("account.delete.exception.orderline");
    }
      // preferred accounts
    if (AccountPrefIO.containsCostAccount(p.getKey(), dc)) {
      return MessageUtil.getMessage("account.delete.exception.preference");
    }
    return null;
  }
  
  public void delete(Account c) throws AccountDeleteException {
    try {
      String used = isAccountUsed(c);
      if (used != null) {
        throw new AccountDeleteException(used);
      } else {
        AccountIO.delete(c, dc);
      }
    } catch (SQLException ex) {
      GemLogger.logException(ex);
      throw new AccountDeleteException(ex.getMessage());
    }
  }
  
  /**
   * Deletes a cost account.
   * @param c cost account param instance
   * @throws AccountDeleteException 
   */
  public void delete(Param c) throws AccountDeleteException {
    try {
      String used = isCostAccountUsed(c);
      if (used != null) {
        throw new AccountDeleteException(used);
      } else {
        ParamTableIO.delete(CostAccountCtrl.tableName, CostAccountCtrl.columnKey, c, dc);
      }
    } catch (SQLException ex) {
      GemLogger.logException(ex);
      throw new AccountDeleteException(ex.getMessage());
    }
  }

}

/*
 * @(#)AccountingService.java	2.15.9 04/06/18
 *
 * Copyright (c) 1999-2018 Musiques Tangentes. All Rights Reserved.
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
import java.util.Date;
import java.util.List;
import net.algem.billing.ItemIO;
import net.algem.config.ActivableParam;
import net.algem.config.ActivableParamTableIO;
import net.algem.config.Param;
import net.algem.config.ParamTableIO;
import net.algem.config.Preference;
import net.algem.course.Module;
import net.algem.planning.*;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;
import net.algem.util.GemLogger;
import net.algem.util.MessageUtil;

/**
 * Service class for accounting.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.15.9
 * @since 2.4.a 21/05/12
 */
public class AccountingServiceImpl implements AccountingService {

  private DataConnection dc;
  private EmployeeReportIO employeeReport;
  private StandardOrderLineIO stdOrderLineIO;

  public AccountingServiceImpl() {
  }

  public AccountingServiceImpl(final DataConnection dc) {
    this.dc = dc;
    employeeReport = new EmployeeReportIO(dc);
    stdOrderLineIO = new StandardOrderLineIO(dc);
  }

  @Override
  public List<PlanningLib> getPlanningLib(String start, String end, int school, boolean catchup) throws SQLException {
    String where = "WHERE jour >= '" + start + "' AND jour <= '" + end + "' AND ecole = " + school;
    if(!catchup) {
      where += " AND salle !~* 'rattrap'";
    }
    where  += " ORDER BY nomprof, jour, debut, coursid";
    return PlanningLibIO.find(where, dc);
  }

  @Override
  public List<PlanningLib> getPlanningLib(String start, String end, int school, int teacherId, boolean catchup) throws SQLException {
    String where = "WHERE jour >= '" + start + "' AND jour <= '" + end + "' AND ecole =" + school + " AND profid = " + teacherId;
    if(!catchup) {
      where += " AND salle !~* 'rattrap'";
    }
    where += " ORDER BY jour, debut, coursid";
    return PlanningLibIO.find(where, dc);
  }

  @Override
  public List<ScheduleRange> getCourseScheduleRange(int idplanning) throws SQLException {
    // on ne comptabilise pas les plages de pause (adherent = 0)
    return ScheduleRangeIO.find("pg WHERE pg.idplanning = " + idplanning + " AND pg.adherent > 0 ORDER BY pg.debut", dc);
  }

  /**
   *
   * @param start start of selected period
   * @param end end of selected period
   * @param catchup include catchup
   * @param idper teacher id
   * @param school school id
   * @param estab establishment id
   * @return a resultset
   * @throws SQLException
   */
  @Override
  public ResultSet getReportByDate(String start, String end, boolean catchup, int idper, int school, int estab) throws SQLException {
    return employeeReport.getDetailTeacherByDate(start, end, catchup, idper, school, estab);
  }

  /**
   *
   * @param start start of selected period
   * @param end end of selected period
   * @param catchup include catchup
   * @param collective course type
   * @param idper teacher id
   * @param school school id
   * @param estab establishment id
   * @return a resultset
   * @throws SQLException
   */
  @Override
  public ResultSet getReportByMember(String start, String end, boolean catchup, boolean collective, int idper, int school, int estab) throws SQLException {
    if (collective) {
      return employeeReport.getDetailCoTeacherByMember(start, end, catchup, idper, school, estab);
    } else {
      return employeeReport.getDetailIndTeacherByMember(start, end, catchup, idper, school, estab);
    }
  }

  /**
   *
   * @param start start of selected period
   * @param end end of selected period
   * @param catchup include catchup
   * @param idper teacher id
   * @param school school id
   * @param estab establishment id
   * @param modules list of selected modules
   * @return a resultset
   * @throws SQLException
   */
  @Override
  public ResultSet getReportByModule(String start, String end, boolean catchup, int idper, int school, int estab, List<Module> modules) throws SQLException {
    return employeeReport.getDetailTeacherByModule(start, end, catchup, idper, school, estab, modules);
  }

  /**
   *
   * @param start start of selected period
   * @param end end of selected period
   * @param idper person id
   * @param ptype planning type
   * @return a resultset
   * @throws SQLException
   */
  @Override
  public ResultSet getReportByEmployee(String start, String end, int idper, int ptype) throws SQLException {
    switch(ptype) {
      case Schedule.TECH :
        return employeeReport.getDetailByTechnician(start, end, idper, ptype);
      case Schedule.ADMINISTRATIVE:
        return employeeReport.getDetailByAdministrator(start, end, idper, ptype);
    }
    return null;
  }

  /**
   * Gets active accounts.
   * @return a list of accounts
   * @throws SQLException
   */
  @Override
  public List<Account> getAccounts() throws SQLException {
    return AccountIO.find(true, dc);
  }

  /**
   * Gets active cost accounts.
   * @return a list of cost accounts
   */
  @Override
  public List<Param> getActiveCostAccounts() {
    List<ActivableParam> actives = ActivableParamTableIO.find(CostAccountCtrl.tableName, CostAccountCtrl.columnName, " WHERE " + CostAccountCtrl.columnFilter + " = TRUE", dc);
    return new ArrayList<Param>(actives);
  }

  /**
   * Gets categories of accounts.
   * @return a list of strings
   * @throws SQLException
   */
  @Override
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
    List<OrderLine> e = OrderLineIO.find(where, 1, dc);
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
    List<OrderLine> e = OrderLineIO.find(where, 1, dc);
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
  @Override
  public List<OrderLine> findStandardOrderLines() throws SQLException {
    return stdOrderLineIO.find();
  }

  public void createStandardOrderLine(OrderLine ol) throws SQLException {
    stdOrderLineIO.insert(ol);
  }

  public void updateStandardOrderLine(OrderLine ol) throws SQLException {
    stdOrderLineIO.update(ol);
  }

  public void deleteStandardOrderLine(OrderLine ol) throws SQLException {
    stdOrderLineIO.delete(ol.getId());
  }

  @Override
  public boolean exists(OrderLine o, String startDate, int member) {
    try {
      DataCache cache = DataCache.getInstance(dc, null);
      Date start = null;
      if (startDate == null || DateFr.NULLDATE.equals(startDate)) {
        start = cache.getStartOfYear().getDate();
      } else {
        start = new DateFr(startDate).getDate();
      }
      return stdOrderLineIO.exists(o, start, cache.getEndOfYear().getDate(), member);
    } catch (SQLException ex) {
      GemLogger.log(ex.getMessage());
      return false;
    }
  }

}

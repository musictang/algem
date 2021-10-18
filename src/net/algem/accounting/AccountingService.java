/*
 * @(#) NewInterface.java Algem 2.15.9 04/06/2018
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
 */

package net.algem.accounting;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import net.algem.config.Param;
import net.algem.course.Module;
import net.algem.planning.PlanningLib;
import net.algem.planning.ScheduleRange;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.15.9
 * @since 2.15.8 04/06/2018
 */
public interface AccountingService {

  boolean exists(OrderLine o, String startDate, int member);

  List<OrderLine> findStandardOrderLines() throws SQLException;

  /**
   * Gets categories of accounts.
   * @return a list of strings
   * @throws SQLException
   */
  String[] getAccountTypes() throws SQLException;

  /**
   * Gets active accounts.
   * @return a list of accounts
   * @throws SQLException
   */
  List<Account> getAccounts() throws SQLException;

  /**
   * Gets active cost accounts.
   * @return a list of cost accounts
   */
  List<Param> getActiveCostAccounts();

  List<ScheduleRange> getCourseScheduleRange(int idplanning) throws SQLException;

  List<PlanningLib> getPlanningLib(String start, String end, int school, boolean catchup) throws SQLException;

  List<PlanningLib> getPlanningLib(String start, String end, int school, int teacherId, boolean catchup) throws SQLException;

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
  ResultSet getReportByDate(String start, String end, boolean catchup, int idper, int school, int estab) throws SQLException;

  /**
   *
   * @param start start of selected period
   * @param end end of selected period
   * @param idper person id
   * @param ptype planning type
   * @return a resultset
   * @throws SQLException
   */
  ResultSet getReportByEmployee(String start, String end, int idper, int ptype) throws SQLException;

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
  ResultSet getReportByMember(String start, String end, boolean catchup, boolean collective, int idper, int school, int estab) throws SQLException;

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
  ResultSet getReportByModule(String start, String end, boolean catchup, int idper, int school, int estab, List<Module> modules) throws SQLException;

}

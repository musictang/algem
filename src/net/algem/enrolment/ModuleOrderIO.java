/*
 * @(#)ModuleOrderIO.java	2.16.0 05/03/19
 *
 * Copyright (c) 1999-2019 Musiques Tangentes. All Rights Reserved.
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
package net.algem.enrolment;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import net.algem.accounting.AccountUtil;
import net.algem.config.Instrument;
import net.algem.config.InstrumentIO;
import net.algem.contact.PersonIO;
import net.algem.contact.member.MemberIO;
import net.algem.course.CourseIO;
import net.algem.course.Module;
import net.algem.course.ModuleIO;
import net.algem.group.Musician;
import net.algem.planning.ActionIO;
import net.algem.planning.DateFr;
import net.algem.planning.ScheduleIO;
import net.algem.planning.ScheduleRangeIO;
import net.algem.util.DataConnection;
import net.algem.util.GemLogger;
import net.algem.util.model.TableIO;

/**
 * IO methods for class {@link net.algem.enrolment.ModuleOrder}.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.16.0
 */
public class ModuleOrderIO
  extends TableIO {

  public static final String TABLE = "commande_module";
  public static final String SEQUENCE = "commande_module_id_seq";
  private static final String EXTENDED_MODULE_TIME_STMT = "SELECT EXTRACT(epoch FROM sum(fin-debut)::interval)/60 AS duree FROM " + ScheduleRangeIO.TABLE + " pl"
    + " WHERE adherent = ?"
    + " AND idplanning IN("
    + "SELECT p.id FROM " + ScheduleIO.TABLE + " p, " + CourseOrderIO.TABLE + " cc, " + ActionIO.TABLE + " a, " + CourseIO.TABLE + " c"
    + " WHERE p.jour BETWEEN ? AND ? AND p.action = cc.idaction"
    + " AND cc.idaction = a.id"
    + " AND a.cours = c.id"
    + " AND cc.datedebut <= p.jour"
    //            + " AND cc.datefin >= p.jour"
    + " AND cc.module = ?"
    + " AND CASE" // if not collective, filter by time length
    + " WHEN c.collectif = false THEN (cc.fin - cc.debut) = (pl.fin - pl.debut)"
    + " ELSE TRUE"
    + " END)";

  private static final String EXTENDED_MODULE_TIME_STMT_2 = "SELECT EXTRACT(epoch FROM sum(fin-debut)::interval)/60 AS duree FROM " + ScheduleRangeIO.TABLE + " pl"
    + " WHERE adherent = ?"
    + " AND idplanning IN("
    + "SELECT p.id FROM " + ScheduleIO.TABLE + " p, " + CourseOrderIO.TABLE + " cc, " + ActionIO.TABLE + " a, " + CourseIO.TABLE + " c"
    + " WHERE p.jour >= ?"
    + " AND p.action = cc.idaction"
    + " AND cc.module = ?"
    + " AND cc.idaction = a.id"
    + " AND a.cours = c.id"
    + " AND cc.datedebut <= p.jour"
    //            + " AND cc.datefin >= p.jour"
    + " AND CASE" // if not collective, filter by time length
    + " WHEN c.collectif = false THEN (cc.fin - cc.debut) = (pl.fin - pl.debut)"
    + " ELSE TRUE"
    + " END)";

  public static void insert(ModuleOrder c, DataConnection dc) throws SQLException {
    int next = nextId(SEQUENCE, dc);
    String query = "INSERT INTO " + TABLE + " VALUES(?,?,?,?,?,?,?,?,?,?,?::priperiod,?)";
    try (PreparedStatement ps = dc.prepareStatement(query)) {
      ps.setInt(1, next);
      ps.setInt(2, c.getIdOrder());
      ps.setInt(3, c.getModule());
      ps.setInt(4, AccountUtil.getIntValue(c.getPrice()));
      ps.setDate(5, new java.sql.Date(c.getStart().getTime()));
      ps.setDate(6, new java.sql.Date(c.getEnd().getTime()));
      ps.setString(7, c.getModeOfPayment());
      ps.setInt(8, c.getNOrderLines());
      ps.setString(9, c.getPayment().getName());
      ps.setBoolean(10, c.isStopped());
      ps.setString(11, c.getPricing().name());
      ps.setInt(12, c.getTotalTime());

      GemLogger.info(ps.toString());
      ps.executeUpdate();
      c.setId(next);
    }

  }

  public static void update(ModuleOrder mo, DataConnection dc) throws SQLException {
    String query = "UPDATE " + TABLE
      + " SET module = ?, debut=?, fin=?, reglement=?, necheance=?, paiement=?, arret=?, tarification=?::priperiod, duree=?"
      + " WHERE id=?";
    try (PreparedStatement ps = dc.prepareStatement(query)) {
      ps.setInt(1, mo.getModule());
      ps.setDate(2, new java.sql.Date(mo.getStart().getTime()));
      ps.setDate(3, new java.sql.Date(mo.getEnd().getTime()));
      ps.setString(4, mo.getModeOfPayment());
      ps.setInt(5, mo.getNOrderLines());
      ps.setString(6, mo.getPayment().getName());
      ps.setBoolean(7, mo.isStopped());
      ps.setString(8, mo.getPricing().name());
      ps.setInt(9, mo.getTotalTime());

      ps.setInt(10, mo.getId());

      GemLogger.info(ps.toString());
      ps.executeUpdate();

    }

  }

  public static void deleteByOrder(int order, DataConnection dc) throws SQLException {
    String query = "DELETE FROM " + TABLE + " WHERE idcmd = " + order;
    dc.executeUpdate(query);
  }

  public static void delete(int moduleOrder, DataConnection dc) throws SQLException {
    String query = "DELETE FROM " + TABLE + " WHERE id = " + moduleOrder;
    dc.executeUpdate(query);
  }

  public static ModuleOrder findId(int id, DataConnection dc) throws SQLException {
    String query = "SELECT * FROM " + TABLE + " WHERE id = " + id;
    try (ResultSet rs = dc.executeQuery(query)) {
      while (rs.next()) {
        return getFromRs(rs);
      }
      return null;
    }
  }

  public static Vector<ModuleOrder> findByIdOrder(int n, DataConnection dc) throws SQLException {
    String query = "WHERE cm.idcmd = " + n;
    return find(query, dc);
  }

  public static Vector<ModuleOrder> find(String where, DataConnection dc) throws SQLException {
    Vector<ModuleOrder> moduleOrders = new Vector<ModuleOrder>();
    String query = "SELECT cm.id, cm.idcmd, cm.module, cm.prix, cm.debut, cm.fin, cm.reglement, cm.necheance, cm.paiement, cm.arret, cm.tarification,cm.duree, m.titre"
      + " FROM " + TABLE + " cm JOIN " + ModuleIO.TABLE + " m ON cm.module = m.id"
      + " " + where;
    GemLogger.info(query);

    try (ResultSet rs = dc.executeQuery(query)) {
      while (rs.next()) {
        moduleOrders.addElement(getFromRs(rs));
      }
    }

    return moduleOrders;
  }

  static List<Musician> findModuleMembers(int moduleId, Date start, Date end, DataConnection dc) throws SQLException {
    List<Musician> members = new ArrayList<Musician>();

    String query = "SELECT DISTINCT p.id, p.nom, p.prenom, pi.instrument, e.datenais FROM "
      + ModuleOrderIO.TABLE + " cm JOIN " + CourseOrderIO.TABLE + " cc ON cm.id = cc.module"
      + " JOIN " + OrderIO.TABLE + " c on cc.idcmd = c.id"
      + " JOIN " + MemberIO.TABLE + " e on c.adh = e.idper"
      + " JOIN " + PersonIO.TABLE + " p ON e.idper = p.id"
      + " LEFT JOIN " + InstrumentIO.PERSON_INSTRUMENT_TABLE
      + " pi ON (p.id = pi.idper AND pi.ptype = " + Instrument.MEMBER + " AND pi.idx = 0)"
      + " WHERE cm.module = ? AND cc.datedebut BETWEEN ? AND ?"
      + " ORDER BY p.nom,p.prenom";

    try (PreparedStatement ps = dc.prepareStatement(query)) {
      ps.setInt(1, moduleId);
      ps.setDate(2, new java.sql.Date(start.getTime()));
      ps.setDate(3, new java.sql.Date(end.getTime()));

      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          Musician m = new Musician();
          m.setId(rs.getInt(1));
          m.setName(rs.getString(2).trim());
          m.setFirstName(rs.getString(3).trim());
          m.setInstrument(rs.getInt(4));
          m.setAge(rs.getDate(5));

          members.add(m);
        }
      }
    }

    return members;
  }

  public static List<Module> findModules(int member, Date start, Date end, DataConnection dc) throws SQLException {
    List<Module> modules = new ArrayList<Module>();
    String query = "SELECT DISTINCT m.titre FROM " + ModuleIO.TABLE + " m"
      + " JOIN " + TABLE + " cm on (cm.module = m.id)"
      + " JOIN " + CourseOrderIO.TABLE + " cc on (cm.id = cc.module)"
      + " JOIN " + OrderIO.TABLE + " c on (cc.idcmd = c.id)"
      + " JOIN " + ScheduleRangeIO.TABLE + " pl on (c.adh = pl.adherent)"
      + " JOIN " + ScheduleIO.TABLE + " p on (pl.idplanning = p.id AND p.action = cc.idaction)"
      + " WHERE c.adh = " + member
      + " AND p.jour BETWEEN '" + start + "' AND '" + end + "'";

    try (ResultSet rs = dc.executeQuery(query)) {
      while (rs.next()) {
        Module m = new Module();
        m.setTitle(rs.getString(1));
        modules.add(m);
      }
    }

    return modules;
  }

  static List<ExtendedModuleOrder> findExtendedModuleList(Date start, Date end, DataConnection dc) throws SQLException {
    String query = "SELECT cm.id,cm.prix,cm.debut,cm.fin,cm.reglement,cm.paiement,cm.tarification,cm.duree,m.titre"
      + ",p.id,p.nom,p.prenom,p.pseudo"
      + " FROM " + TABLE + " cm, " + OrderIO.TABLE + " c, " + PersonIO.TABLE + " p, " + ModuleIO.TABLE + " m"
      + " WHERE cm.module = m.id"
      + " AND cm.idcmd = c.id"
      + " AND c.adh = p.id"
      + " AND cm.debut BETWEEN '" + start + "' AND '" + end + "'";
    List<ExtendedModuleOrder> list = new ArrayList<>();
    ResultSet rs = dc.executeQuery(query);
    while (rs.next()) {
      ExtendedModuleOrder em = new ExtendedModuleOrder();
      em.setId(rs.getInt(1));
      em.setPrice(rs.getInt(2) / 100d);
      em.setStart(new DateFr(rs.getString(3)));
      em.setEnd(new DateFr(rs.getString(4)));
      em.setModeOfPayment(rs.getString(5));
      em.setPayment(PayFrequency.getValue(rs.getString(6)));
      em.setPricing(PricingPeriod.valueOf(rs.getString(7)));
      em.setTotalTime(rs.getInt(8));
      em.setTitle(rs.getString(9));
      em.setIdper(rs.getInt(10));
      em.setName(rs.getString(11));
      em.setFirstName(rs.getString(12));
      em.setNickName(rs.getString(13));
      list.add(em);
    }
    return list;
  }

  /**
   * Gets the time spent by the student {@code idper} in the module {@code mOrderId} between {@code start} and {@code end} dates.
   *
   * @param idper student id
   * @param mOrderId module id
   * @param start start date
   * @param end end date
   * @param dc data connection
   * @return a length in minutes
   * @throws SQLException
   */
  static int getCompletedTime(int idper, int mOrderId, Date start, Date end, DataConnection dc) throws SQLException {
    PreparedStatement ps = dc.prepareStatement(EXTENDED_MODULE_TIME_STMT);
    ps.setInt(1, idper);
    ps.setDate(2, new java.sql.Date(start.getTime()));
    ps.setDate(3, new java.sql.Date(end.getTime()));
    ps.setInt(4, mOrderId);

    try (ResultSet rs = ps.executeQuery()) {
      while (rs.next()) {
        return rs.getInt(1);
      }
    } catch (SQLException ex) {
      GemLogger.logException(ex);
    }
    return 0;
  }

  /**
   * Gets the time spent by the student {@code idper} in the module {@code mOrderId} from {@code start} date.
   *
   * @param idper student id
   * @param mOrderId module id
   * @param start start date
   * @param dc data connection
   * @return a length in minutes
   * @throws SQLException
   */
  static int getCompletedTime(int idper, int mOrderId, Date start, DataConnection dc) throws SQLException {
    PreparedStatement ps = dc.prepareStatement(EXTENDED_MODULE_TIME_STMT_2);
    ps.setInt(1, idper);
    ps.setDate(2, new java.sql.Date(start.getTime()));
    ps.setInt(3, mOrderId);
    try (ResultSet rs = ps.executeQuery()) {
      while (rs.next()) {
        return rs.getInt(1);
      }
    } catch (SQLException ex) {
      GemLogger.logException(ex);
    }
    return 0;
  }

  static Date getLastSchedule(int idper, int mOrderId, DataConnection dc) throws SQLException {
    String query = "SELECT jour FROM " + ScheduleIO.TABLE + " p"
      + " join " + CourseOrderIO.TABLE + " cc on (p.action = cc.idaction)"
      + " join " + ModuleOrderIO.TABLE + " cm on (cc.module = cm.id)"
      + " join " + OrderIO.TABLE + " c on (cm.idcmd = c.id)"
      + " join " + ScheduleRangeIO.TABLE + " pl on (p.id = pl.idplanning and c.adh = pl.adherent)"
      + " where cm.id = " + mOrderId
      + " and c.adh = " + idper
      + " order by jour desc limit 1";
    try (ResultSet rs = dc.executeQuery(query)) {
      while (rs.next()) {
        return rs.getDate(1);
      }
    }
    return null;
  }

  private static ModuleOrder getFromRs(ResultSet rs) throws SQLException {
    ModuleOrder m = new ModuleOrder();
    m.setId(rs.getInt(1));
    m.setIdOrder(rs.getInt(2));
    m.setModule(rs.getInt(3));
    m.setPrice(rs.getInt(4) / 100d);
    m.setStart(new DateFr(rs.getString(5)));
    m.setEnd(new DateFr(rs.getString(6)));
    m.setModeOfPayment(rs.getString(7));
    m.setNOrderLines(rs.getInt(8));
    m.setPayment(PayFrequency.getValue(rs.getString(9)));
    m.setStopped(rs.getBoolean(10));
    m.setPricing(PricingPeriod.valueOf(rs.getString(11)));
    m.setTotalTime(rs.getInt(12));
    m.setTitle(rs.getString(13));

    return m;
  }

}

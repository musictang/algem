/*
 * @(#)OrderIO.java	2.9.4.3 21/04/15
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
package net.algem.enrolment;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import net.algem.accounting.AccountPrefIO;
import net.algem.accounting.OrderLineIO;
import net.algem.config.ConfigKey;
import net.algem.config.ConfigUtil;
import net.algem.config.Preference;
import net.algem.contact.PersonIO;
import net.algem.planning.DateFr;
import net.algem.planning.ScheduleIO;
import net.algem.planning.ScheduleRangeIO;
import net.algem.util.DataConnection;
import net.algem.util.GemLogger;
import net.algem.util.model.TableIO;

/**
 * IO methods for class {@link net.algem.enrolment.Order}.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.3
 * @since 1.0a 07/07/1999
 */
public class OrderIO
        extends TableIO
{

  public static final String TABLE = "commande";
  private static final String SEQUENCE = "idcommande";

  public static void insert(Order c, DataConnection dc) throws SQLException {

    int n = nextId(SEQUENCE, dc);
    String query = "INSERT INTO " + TABLE + " VALUES("
            + "'" + n
            + "','" + c.getMember()
            + "','" + c.getPayer()
            + "','" + c.getCreation().toString()
            + "'," + ((c.getInvoice() == null) ? "NULL" : "'" + c.getInvoice() + "'")
            + ")";
    dc.executeUpdate(query);
    c.setId(n);
  }

  public static void update(Order c, DataConnection dc) throws SQLException {
    String query = "UPDATE " + TABLE + " SET "
            + "adh = '" + c.getMember()
            + "',payeur = '" + c.getPayer()
            + "',creation = " + c.getCreation().toString()
            + "',facture =  " + ((c.getInvoice() == null) ? "NULL" : "'" + c.getInvoice() + "'")
            + " WHERE id = " + c.getId();

    dc.executeUpdate(query);
  }

  /**
   * Deletes an order.
   * The scheduled courses are also deleteByIdd.
   *
   * @param c the order
   * @param dc dataConnection
   * @throws Exception
   */
  public static void delete(Order c, DataConnection dc) throws Exception {

    try {
      dc.setAutoCommit(false);
      // suppression de la commande_module
      ModuleOrderIO.deleteByOrder(c.getId(), dc);
      Vector<CourseOrder> cours = CourseOrderIO.findId(c.getId(), dc);
      for (int i = 0; i < cours.size(); i++) {
        CourseOrder cc = cours.elementAt(i);
        // suppression des plages de cours
        String query = "idplanning IN (SELECT id FROM " + ScheduleIO.TABLE + " WHERE action = " + cc.getAction() + ")"
                + " AND adherent = " + c.getMember();
        ScheduleRangeIO.delete(query, dc);
      }
      // suppression de la commande_cours
      CourseOrderIO.delete(c.getId(), dc);

      // suppression de la commande
      String query = "DELETE FROM " + TABLE + " WHERE id = " + c.getId();
      dc.executeUpdate(query);

      // suppression des échéances
      int memberAccount = 0;
      // on ne supprime pas les échéances correspondant à des adhésions
      Preference p = AccountPrefIO.find(AccountPrefIO.MEMBERSHIP, dc);
      if (p != null && p.getValues() != null && p.getValues().length > 0) {
        memberAccount = (Integer) p.getValues()[0];
      }
      query = "DELETE FROM " + OrderLineIO.TABLE + " WHERE commande = " + c.getId() + " AND compte != '" + memberAccount + "' AND transfert = 'f'";
      dc.executeUpdate(query);

      dc.commit();
    } catch (SQLException e1) {
      dc.rollback();
      throw e1;
    } finally {
      dc.setAutoCommit(true);
    }
  }

  public static Order findId(int n, DataConnection dc) throws SQLException {
    String query = "WHERE id = " + n;
    Vector<Order> v = find(query, dc);
    if (v.size() > 0) {
      return v.elementAt(0);
    }
    return null;
  }

  public static Vector<Order> find(String where, DataConnection dc) throws SQLException {
    Vector<Order> v = new Vector<Order>();
    String query = "SELECT * FROM " + TABLE + " " + where;

    ResultSet rs = dc.executeQuery(query);
    while (rs.next()) {
      Order c = new Order();
      c.setId(rs.getInt(1));
      c.setMember(rs.getInt(2));
      c.setPayer(rs.getInt(3));
      c.setCreation(new DateFr(rs.getString(4)));
      c.setInvoice(rs.getString(5));

      v.addElement(c);
    }
    rs.close();

    return v;
  }

  public static Vector<MemberOrder> findMemberOrders(DataConnection dc) {

    String start = ConfigUtil.getConf(ConfigKey.BEGINNING_PERIOD.getKey());
    String end = ConfigUtil.getConf(ConfigKey.END_PERIOD.getKey());
    Vector<MemberOrder> v = new Vector<MemberOrder>();
    String query = "SELECT c.id,c.adh,c.payeur,c.creation,c.facture,p.nom,p.prenom"
            + " FROM " + TABLE + " c, " + PersonIO.TABLE + " p"
            + " WHERE c.adh = p.id"
            + " AND c.creation >= '" + start + "' AND c.creation <= '" + end + "'"
            + " ORDER BY c.id DESC";
    try {
      ResultSet rs = dc.executeQuery(query);
      while (rs.next()) {
        MemberOrder c = new MemberOrder();
        c.setId(rs.getInt(1));
        c.setMember(rs.getInt(2));
        c.setPayer(rs.getInt(3));
        c.setCreation(new DateFr(rs.getString(4)));
        c.setInvoice(rs.getString(5));
        c.setMemberName(rs.getString(6));
        c.setMemberFirstname(rs.getString(7));

        v.addElement(c);
      }
      rs.close();
    } catch (SQLException e) {
      GemLogger.logException(query, e);
    }
    return v;
  }
}

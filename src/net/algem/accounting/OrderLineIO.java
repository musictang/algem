/*
 * @(#)OrderLineIO.java	2.8.t 10/05/14
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
package net.algem.accounting;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.TreeMap;
import java.util.Vector;
import net.algem.config.Param;
import net.algem.config.ParamTableIO;
import net.algem.config.Preference;
import net.algem.planning.DateFr;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;
import net.algem.util.GemLogger;
import net.algem.util.model.Model;
import net.algem.util.model.TableIO;

/**
 * IO Methods class {@link net.algem.accounting.OrderLine}.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.t
 *
 */
public class OrderLineIO
        extends TableIO
{

  public static final String TABLE = "echeancier2";
  public static final String COLUMNS = "oid,echeance,payeur,adherent,commande,libelle,reglement,montant,piece,ecole,compte,paye,transfert,monnaie,analytique,facture,groupe";
  public final static String ACCOUNT_COLUMN = "compte";
  public final static String COST_COLUMN = "analytique";

  /** Max label length. */
  public static final int MAX_CHARS_LABEL = 50;

  private static final String SEQUENCE = "echeancier2_oid_seq";

  /**
   * OrderLine insertion.
   *
   * @param e orderLine
   * @param dc dataConnection instance
   * @throws SQLException
   */
  public static void insert(OrderLine e, DataConnection dc) throws SQLException {

    int nextid = TableIO.nextId(SEQUENCE, dc);

    String query = "INSERT INTO " + TABLE + " VALUES("
            + nextid
            + ",'" + e.getDate().toString()
            + "'," + e.getPayer()
            + "," + e.getMember()
            + "," + e.getOrder()
            + ",'" + e.getModeOfPayment()
            + "','" + escape(e.getLabel())
            + "'," + e.getAmount()
            + ",'" + e.getDocument()
            + "','" + e.getSchool()
            + "','" + e.getAccount().getId() //@since 2.3.c
            + "','" + (e.isPaid() ? "t" : "f")
            + "','f'" // transfer false by default
            + ",'" + e.getCurrency()
            + "','" + e.getCostAccount().getNumber()
            + "'," + ((e.getInvoice() == null || e.getInvoice().isEmpty()) ? "NULL" : "'" + e.getInvoice() + "'") //@since 2.3.a
            + ", " + e.getGroup()
            + ")";

    dc.executeUpdate(query);

    e.setId(nextid);

  }

  /**
   * OrderLine update.
   *
   * @param e orderLine to update
   * @param dc dataConnection instance
   * @throws SQLException
   */
  public static void update(OrderLine e, DataConnection dc) throws SQLException {
    String query = "UPDATE " + TABLE + " SET"
            + " echeance = '" + e.getDate().toString()
            + "', payeur = " + e.getPayer()
            + ", adherent = " + e.getMember()
            + ", commande = " + e.getOrder()
            + ", libelle = '" + escape(e.getLabel())
            + "', reglement = '" + e.getModeOfPayment()
            + "', montant = " + e.getAmount()
            + ", piece = '" + e.getDocument()
            + "', ecole = '" + e.getSchool()
            + "', compte = '" + e.getAccount().getId() // int
            + "', paye = '" + (e.isPaid() ? "t" : "f")
            //+"', transfer='"+ (e.isTransfered() ? "t" : "f") // on ne modifie pas le champ transfer
            + "', monnaie = '" + e.getCurrency()
            + "', analytique = '" + e.getCostAccount().getNumber()
            + "', facture = " + ((e.getInvoice() == null || e.getInvoice().isEmpty()) ? "NULL" : "'" + e.getInvoice() + "'")
            + ", groupe = " + e.getGroup();
    query += " WHERE oid = " + e.getId();
    dc.executeUpdate(query);

  }

  /**
   * Update of column transfer for one orderline.
   *
   * @param e orderLine to transfer
   * @param dc dataConnection instance
   * @throws SQLException
   */
  public static void transfer(OrderLine e, DataConnection dc) throws SQLException {

    String query = "UPDATE " + TABLE + " SET transfert = '" + (e.isTransfered() ? "t" : "f") + "'";
    query += " WHERE oid = " + e.getId();

    dc.executeUpdate(query);
  }
  
  /**
   * Update paid.
   * Allow to update, in the databse, the colum 'paye' in the eheancier 2 table
   * @param e selected line
   * @param dc data connection
   * @throws SQLException 
   */
  public static void paid(OrderLine e, DataConnection dc) throws SQLException {
    
    String query = "UPDATE " + TABLE + " SET paye = '" + (e.isPaid() ? "t" : "f") + "'";
    query += " WHERE oid = " +e.getId();
    
    dc.executeUpdate(query);
  }

  public static void delete(OrderLine e, DataConnection dc) throws SQLException {
    String query = "DELETE FROM " + TABLE + " WHERE oid = " + e.getId();
    dc.executeUpdate(query);
  }

  /**
   * Search of orderlines for member {@code m} and whose payer id is {@code p} or {@code m}
   *
   * @param m member id
   * @param p payer id
   * @param dc dataConnection instance
   * @return a list of order lines
   */
  public static Vector<OrderLine> findByMember(int m, int p, DataConnection dc) {
    String where = "WHERE (adherent = " + m + " OR payeur = " + p + ") AND adherent in(SELECT adherent FROM echeancier2 WHERE adherent = " + m + ")";
    return find(where, dc);
  }

  /**
   * Search of orderlines by member {@code m} or payer {@code p}.
   *
   * @param m member id
   * @param p payer id
   * @param dc dataConnection instance
   * @return une liste d'échéances
   */
  public static Vector<OrderLine> findByMemberOrPayer(int m, int p, DataConnection dc) {
    return find("WHERE adherent = " + m + " OR payeur = " + p, dc);
  }

  /**
   * Search of orderlines.
   * By default, orderlines are sorted by date asc.
   *
   * @param where sql expression
   * @param dc dataConnection instance
   * @return a list of orderlines
   */
  public static Vector<OrderLine> find(String where, DataConnection dc) {
    String query = getSelectWhereExpression(where, 0);
    return getResult(query, dc);
  }

  public static OrderLine find(int id, DataConnection dc) {
    String query = getSelectWhereExpression("WHERE oid = " + id, 1);
    Vector<OrderLine> ve = getResult(query, dc);
    if (ve != null && ve.size() > 0) {
      return ve.elementAt(0);
    }
    return null;
  }

  /**
   * Search of orderlines with same order id and same payer that {@code e}.
   *
   * @param e order line instance
   * @param dc dataConnection instance
   * @return an orderline
   */
  public static OrderLine find(OrderLine e, DataConnection dc) {

    String fac = ModeOfPayment.FAC.toString();
    String query = "SELECT * FROM " + TABLE + " WHERE reglement";
    query += fac.equals(e.getModeOfPayment()) ? " != '" + fac + "'" : " = '" + fac + "'";
    query += " AND (commande >  0 AND commande = " + e.getOrder() + ")"
            + " AND payeur = " + e.getPayer()
            + " AND echeance = '" + e.getDate() + "'";
    Vector<OrderLine> r = getResult(query, dc);
    return (r == null || r.isEmpty()) ? null : r.elementAt(0);
  }

  /**
   * Search of orderlines with rows limit.
   *
   * @param where sql expression
   * @param limit number of rows returned (if 0, no limit)
   * @param dc dataConnection instance
   * @return une liste d'échéances
   */
  public static Vector<OrderLine> find(String where, int limit, DataConnection dc) {
    String query = getSelectWhereExpression(where, limit);
    return getResult(query, dc);
  }

  /**
   * Associates amount with date for the selected period and payer in {@code where}.
   *
   * @param where sql expression
   * @param dc dataConnection instance
   * @return a sorted map
   */
  public static TreeMap<DateFr, String> findPrl(String where, DataConnection dc) {
    TreeMap<DateFr, String> prl = new TreeMap<DateFr, String>();
    String query = "SELECT echeance, sum(montant) AS total FROM " + TABLE + " " + where;
    try {
      ResultSet rs = dc.executeQuery(query);
      while (rs.next()) {
        prl.put(new DateFr(rs.getString(1)), rs.getString(2));
      }

      rs.close();
    } catch (Exception e) {
      GemLogger.logException(query, e);
    }
    return prl;
  }

  /**
   * Specifies if cost account is of type pro during the period.
   * This pro status definition is not valid for the calculation of teachers' hours.
   *
   * @param adh member id
   * @param dc dataConnection instance
   * @return true if pro
   * @deprecated use instead {@link net.algem.edition.ExportService#isPro(int, int) }
   */
  public static boolean isPro(int adh, DataCache dc) {
    String query = "SELECT analytique FROM " + TABLE + " WHERE echeance >= '" + dc.getStartOfPeriod() + "' AND adherent=" + adh;
    //String query = "SELECT analytique FROM echeancier2 WHERE echeance >= '"+dc.getStartOfYear()+"' AND adherent="+adh;
    String a = "";
    try {
      ResultSet rs = dc.getDataConnection().executeQuery(query);
      //if (rs.next()) {
      while (rs.next()) {
        a = rs.getString(1);
        if (a.indexOf("PROFES") != -1) {
          return true;
        }
      }
      rs.close();
    } catch (Exception e) {
      GemLogger.logException(query, e);
    }
    return false;
  }

  /**
   * Retrieves the number of memberships for the member {@code m}.
   * A default account for the membership must be configured.
   * A problem arises when several accounts may be used for membership.
   * In this case, the default one may not correspond to the current account.
   *
   * @param m member id
   * @param dc dataConnection instance
   * @return an integer
   * @throws SQLException
   */
  public static int countMemberShip(int m, DataConnection dc) throws SQLException {
    Preference p = AccountPrefIO.find(AccountPrefIO.MEMBER_KEY_PREF, dc);
    //String query = "SELECT count(echeance) FROM echeancier2 WHERE adherent=" + m + " AND compte like '" + p.getValues()[0].substring(0, 8) + "%'";
    String query = "SELECT count(echeance) FROM " + TABLE + " WHERE adherent = " + m + " AND compte = '" + p.getValues()[0] + "'";
    ResultSet rs = dc.executeQuery(query);
    if (rs.next()) {
      return rs.getInt(1);
    }
    return 1;
  }

  /**
   * Gets a string selection.
   *
   * @param where sql expression
   * @param limit when 0 : no limit, else limit the number of rows returned
   * @return a query string
   */
  private static String getSelectWhereExpression(String where, int limit) {
    String query = "SELECT " + COLUMNS + " FROM " + TABLE;
    query += " " + where;
    query += " ORDER BY echeance, oid";
    if (limit > 0) {
      query += " LIMIT " + limit;
    }
    return query;
  }

  /**
   * Utility method used where joint expression is needed.
   * @param query sql query
   * @param dc
   * @return a list of order lines
   */
  public static Vector<OrderLine> getOrderLines(String query, DataConnection dc) {
    return getResult(query, dc);
  }

  /**
   *
   * @param key column key
   * @param dc
   * @return an account
   * @deprecated from 2.3.a
   */
  public static Account findAccount(int key, DataConnection dc) {

    Account c = null;
    try {
      /* String num = code; if (code != null && code.matches("^[0-9].*$")) { num =
       * format(code);// problème pour la recherche de compte
      } */
      c = AccountIO.find(key, dc);
      if (c == null) {
        c = new Account(key);
        c.setLabel("INEXISTANT");
      }

    } catch (SQLException ex) {
      System.err.println(ex.getMessage());
    }
    return c;
  }

  /**
   * Retrieves an account by its number (not its id).
   *
   * @param table
   * @param columnkey
   * @param code
   * @param dc
   * @return un compte
   */
  public static Account findAccount(String table, String columnkey, String code, DataConnection dc) {
    Account c = null;
    /* String num = code; if (code != null && code.matches("^[0-9].*$")) { num =
     * format(code);// problème pour la recherche de compte
    } */
    Param p = ParamTableIO.findByKey(table, columnkey, code, dc);
    if (p != null) {
      c = new Account(p);
      c.setLabel(p.getValue());
    } else {
      c = new Account(code);
      c.setLabel(code);
      //c.setLabel(MessageUtil.getMessage("account.null.label"));
    }

    return c;
  }

  /**
   * Format account number on ten digits.
   *
   * @param code the number
   * @deprecated from 2.3.a
   */
  private static String format(String code) {
    String num = code;
    if (num.length() < 10) {
      for (int i = num.length(); i < 10; i++) {
        num += "0"; // le caractère zéro
      }
    }
    return num;
  }

  private static Vector<OrderLine> getResult(String query, DataConnection dc) {
    Vector<OrderLine> v = new Vector<OrderLine>();
    try {
      ResultSet rs = dc.executeQuery(query);
      while (rs.next()) {
        OrderLine e = new OrderLine();
        e.setId(rs.getInt(1));
        e.setDate(new DateFr(rs.getString(2)));
        e.setPayer(rs.getInt(3));
        e.setMember(rs.getInt(4));
        e.setOrder(rs.getInt(5));
        e.setLabel(rs.getString(6));
        e.setModeOfPayment(rs.getString(7).trim());
        e.setAmount(rs.getInt(8));
        e.setDocument(rs.getString(9));
        e.setSchool(rs.getInt(10));
        Account c = (Account) DataCache.findId(rs.getInt(11), Model.Account);
        e.setAccount(c);
        e.setPaid(rs.getBoolean(12));
        e.setTransfered(rs.getBoolean(13));
        e.setCurrency(rs.getString(14).trim());

        String code = rs.getString(15);
        Param p = DataCache.getCostAccount(code);
        Account a = null;
        if (p != null) {
          a = new Account(p);
          a.setLabel(p.getValue());
        } else {
          a = new Account(code);
          a.setLabel(code);
        }

        e.setCostAccount(a);
        e.setInvoice(rs.getString(16));

        e.setGroup(rs.getInt(17));

        v.addElement(e);
      }
      rs.close();
    } catch (SQLException e) {
      GemLogger.logException(query, e);
    }
    return v;
  }

  public static Vector<OrderLine> getBillingOrderLines(DataConnection dc) {
//    String where = "oid IN (SELECT id_echeancier FROM " + InvoiceIO.JOIN_TABLE + ")";
    String where = "WHERE facture IS NOT NULL";
    return find(where, dc);
  }
}

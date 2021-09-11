/*
 * @(#)QuoteIO.java 2.14.0 30/05/17
 *
 * Copyright (c) 1999-2017 Musiques Tangentes. All Rights Reserved.
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
package net.algem.billing;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.algem.planning.DateFr;
import net.algem.security.User;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;
import net.algem.util.GemLogger;
import net.algem.util.model.Model;

/**
 * Quote persistence {@link net.algem.billing.Quote}.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.14.0
 * @since 2.4.d 07/06/12
 */
public class QuoteIO
        extends BillingIO
{

  private DataConnection dc;
  private ItemIO itemIO;
  private static final String TABLE = "devis";
  private static final String COLUMNS = "numero, date_emission, etablissement, emetteur, debiteur, prestation, reference, adherent, editable";
  private static final String ITEM_TABLE = "article_devis";
  private static final String JOIN_COLUMNS = "id_article, quantite";

  public QuoteIO(DataConnection dc) {
    this.dc = dc;
    itemIO = new ItemIO(dc);
  }

  public static boolean isQuotationItem(int id, DataConnection dc) throws SQLException {
    String query = "SELECT id_article FROM " + ITEM_TABLE + " WHERE id_article = " + id;
    ResultSet rs = dc.executeQuery(query);
    return rs.next();
  }

  /**
   * Transaction for quote creation.
   *
   * @param <T>
   * @param q the quote to store
   * @throws SQLException
   * @throws BillingException
   */
  public <T extends Quote> void insert(final T q) throws SQLException, BillingException {

    int last = getLastId(TABLE, dc);
    if (last == -1) {
      throw new BillingException("NULL INVOICE LAST ID");
    }
    q.inc(last);
    // editable is true by default
    final String cq = "INSERT INTO " + TABLE + "(" + COLUMNS + ")" + " VALUES(?,?,?,?,?,?,?,?,?)";
    try {
      dc.withTransaction(new DataConnection.SQLRunnable<Void>()
      {
        @Override
        public Void run(DataConnection conn) throws Exception {
          try (PreparedStatement ps = dc.prepareStatement(cq)) {
            ps.setString(1, q.getNumber());
            ps.setDate(2, new java.sql.Date(q.getDate().getDate().getTime()));
            ps.setInt(3, q.getEstablishment());
            ps.setInt(4, q.getIssuer());
            ps.setInt(5, q.getPayer());
            ps.setString(6, q.getDescription().trim());
            ps.setString(7, q.getReference().trim());
            ps.setInt(8, q.getMember());
            ps.setBoolean(9, true);
            GemLogger.info(ps.toString());
            ps.executeUpdate();
          }
          //items
          for (InvoiceItem vItem : q.getItems()) {
            insert(vItem, q.getNumber());
          }
          return null;
        }
      });

    } catch (Exception sqe) {
      throw new BillingException(sqe.getMessage());
    }

  }

  /**
   * Insertion of a quote item.
   *
   * @param it item
   * @param in quote number
   * @throws SQLException
   */
  protected void insert(InvoiceItem it, String in) throws SQLException {

    String q = "INSERT INTO " + ITEM_TABLE + " VALUES(?,?,?)";
    Item a = it.getItem();
    try (PreparedStatement ps = dc.prepareStatement(q)) {
      if (a.getId() == 0 || a.isStandard()) {
        a.setStandard(false);
        itemIO.insert(a);
      } else {
        itemIO.update(a);
      }
      ps.setString(1, in);
      ps.setInt(2, a.getId());
      ps.setDouble(3, it.getQuantity());
      
      GemLogger.info(ps.toString());
      ps.executeUpdate();
    }
  }

  public List<Quote> find(String where) throws SQLException {

    List<Quote> ld = new ArrayList<Quote>();
    String query = "SELECT " + COLUMNS + " FROM " + TABLE + where;
    query += " ORDER BY numero";
    ResultSet rs = dc.executeQuery(query);
    while (rs.next()) {
      Quote d = new Quote(rs.getString(1));
      d.setDate(new DateFr(rs.getString(2)));
      d.setEstablishment(rs.getInt(3));
      d.setIssuer(rs.getInt(4));
      d.setUser((User) DataCache.findId(rs.getInt(4), Model.User));
      d.setPayer(rs.getInt(5));
      d.setDescription(rs.getString(6));
      d.setReference(rs.getString(7));
      d.setMember(rs.getInt(8));
      d.setEditable(rs.getBoolean(9));

      d.setItems(findItems(d));

      ld.add(d);
    }
    return ld;
  }

  public void update(final Quote quote) throws BillingException {

    final String q = "UPDATE " + TABLE + " SET date_emission =?,etablissement=?,debiteur=?,prestation=?,reference=?,editable=? WHERE numero =?";
    try {
      dc.withTransaction(new DataConnection.SQLRunnable<Void>()
      {
        @Override
        public Void run(DataConnection conn) throws Exception {
          try (PreparedStatement ps = dc.prepareStatement(q)) {
            ps.setDate(1, new java.sql.Date(quote.getDate().getDate().getTime()));
            ps.setInt(2, quote.getEstablishment());
            ps.setInt(3, quote.getPayer());
            ps.setString(4, quote.getDescription().trim());
            ps.setString(5, quote.getReference().trim());
            ps.setBoolean(6, quote.isEditable());
            ps.setString(7, quote.getNumber());
            GemLogger.info(ps.toString());
            ps.executeUpdate();
            
            String q2 = "DELETE FROM " + ITEM_TABLE + " WHERE id_devis = '" + quote.getNumber() + "'";
            dc.executeUpdate(q2);
            for (InvoiceItem it : quote.getItems()) {
              insert(it, quote.getNumber());
            }
          }
          return null;
        }
      });
    } catch (Exception sqe) {
      throw new BillingException(sqe.getMessage());
    }
  }

  public void delete(Quote d) throws SQLException {
    String query = "DELETE FROM " + TABLE + " WHERE numero = '" + d.getNumber() + "'";
    dc.executeUpdate(query);
  }

  /**
   * Search by invoice number.
   *
   * @param number
   * @return a quote
   * @throws SQLException
   */
  public Quote findBy(String number) throws SQLException {
    List<Quote> lf = find(" WHERE numero = " + number);
    return lf == null ? null : lf.get(0);
  }

  public List<Quote> findBy(int idper, String andPeriod) throws SQLException {
    String where = "  WHERE (debiteur = " + idper + " OR adherent = " + idper + ")";
    if (andPeriod != null) {
      where += andPeriod;
    }
    return find(where);

  }

  public Collection<InvoiceItem> findItems(Quote d) throws SQLException {

    List<InvoiceItem> list = new ArrayList<InvoiceItem>();
    String query = "SELECT " + JOIN_COLUMNS + " FROM " + ITEM_TABLE + " WHERE id_devis = '" + d.getNumber() + "'";
    ResultSet rs = dc.executeQuery(query);
    while (rs.next()) {
      Item a = null;
      int id_article = rs.getInt(1);//id_article

      if (id_article > 0) {
        a = itemIO.findId(id_article);
      }
      if (a == null || id_article == 0) {
        a = new Item();
      }

      InvoiceItem af = new InvoiceItem(d.getNumber());
      af.setItem(a);
      af.setQuantity(rs.getFloat(2));

      list.add(af);
    }
    return list;
  }
}

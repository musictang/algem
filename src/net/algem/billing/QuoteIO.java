/*
 * @(#)QuoteIO.java 2.9.4.7 15/06/15
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
package net.algem.billing;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.algem.planning.DateFr;
import net.algem.security.User;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;
import net.algem.util.model.Model;

/**
 * Quote persistence {@link net.algem.billing.Quote}.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.7
 * @since 2.4.d 07/06/12
 */
public class QuoteIO
        extends BillingIO
{

  private DataConnection dc;
  private ItemIO itemIO;
  private static final String TABLE = "devis";
  private static final String COLUMNS = "numero, date_emission, etablissement, emetteur, debiteur, prestation, reference, adherent, editable";
  private static final String JOIN_TABLE = "article_devis";
  private static final String JOIN_COLUMNS = "id_article, quantite";

  public QuoteIO(DataConnection dc) {
    this.dc = dc;
    itemIO = new ItemIO(dc);
  }

  public static boolean isQuotationItem(int id, DataConnection dc) throws SQLException {
    String query = "SELECT id_article FROM " + JOIN_TABLE + " WHERE id_article = " + id;
    ResultSet rs = dc.executeQuery(query);
    if (rs.next()) {
      return true;
    }
    return false;
  }

  /**
   * Transaction for quote creation.
   *
   * @param <T>
   * @param q
   * @throws SQLException
   * @throws BillingException
   */
  public <T extends Quote> void insert(T q) throws SQLException, BillingException {

    int last = getLastId(TABLE, dc);

    if (last == -1) {
      throw new BillingException("NULL INVOICE LAST ID");
    }
    q.inc(last);
    // la colonne editable prend la valeur true par défaut
    String query = "INSERT INTO " + TABLE + " VALUES('" + q.getNumber()
            + "','" + q.getDate()
            + "'," + q.getEstablishment()
            + "," + q.getIssuer()
            + "," + q.getPayer()
            + ",'" + escape(q.getDescription().trim())
            + "','" + escape(q.getReference().trim())
            + "'," + q.getMember()
            + ")";

    try {
      dc.setAutoCommit(false);
      dc.executeUpdate(query);// insertion facture

      // insertion lignes facture
      for (InvoiceItem vItem : q.getItems()) {
        insert(vItem, q.getNumber());
      }

      dc.commit();
    } catch (SQLException sqe) {
      dc.rollback();
      throw new BillingException(sqe.getMessage());
    } finally {
      dc.setAutoCommit(true);
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

    String query = "INSERT INTO " + JOIN_TABLE + " VALUES('" + in + "',";
    Item a = it.getItem();

    // on n'ajoute un article que s'il n'existe pas ou que s'il provient d'un article standard
    if (a.getId() == 0 || a.isStandard()) {
      a.setStandard(false);
      itemIO.insert(a);//article
    } else {
      itemIO.update(a);
    }
    //int id_echeancier = (it.getOrderLine() == null) ? 0 : it.getOrderLine().getOID();
    //query += a.getId() + "," + id_echeancier + "," + it.getQuantity() + ")";
    query += a.getId() + "," + it.getQuantity() + ")";

    dc.executeUpdate(query);//jointure
  }

  public List<Quote> find(String where) throws SQLException {

    List<Quote> ld = new ArrayList<Quote>();
    String query = "SELECT " + COLUMNS + " FROM " + TABLE + where;
    query += " ORDER BY numero";
    //System.out.println(query);
    ResultSet rs = dc.executeQuery(query);
    while (rs.next()) {
      Quote d = new Quote(rs.getString(1));
      d.setDate(new DateFr(rs.getString(2)));
      d.setEstablishment(rs.getInt(3));
      d.setIssuer(rs.getInt(4));
      d.setUser((User) DataCache.findId(rs.getInt(4), Model.User));
      d.setPayer(rs.getInt(5));
      d.setDescription(unEscape(rs.getString(6)));
      d.setReference(unEscape(rs.getString(7)));
      d.setMember(rs.getInt(8));
      d.setEditable(rs.getBoolean(9));

      d.setItems(findItems(d));

      ld.add(d);
    }
    return ld;
  }

  public void update(Quote d) throws BillingException {

    String query = "UPDATE " + TABLE + " SET "
            + " date_emission = '" + d.getDate()
            + "', etablissement = " + d.getEstablishment()
            //+ ", emetteur = " + d.getIssuer()
            + ", debiteur = " + d.getPayer()
            + ", prestation = '" + escape(d.getDescription().trim())
            + "', reference = '" + escape(d.getReference().trim())
            + "', editable = " + d.isEditable()
            + " WHERE numero = '" + d.getNumber() + "'";
    try {
      dc.setAutoCommit(false);
      dc.executeUpdate(query); // update devis

      // supression des articles dans la table article_devis
      String q2 = "DELETE FROM " + JOIN_TABLE + " WHERE id_devis = '" + d.getNumber() + "'";
      dc.executeUpdate(q2);

      for (InvoiceItem af : d.getItems()) {
        insert(af, d.getNumber());
      }

      dc.commit();
    } catch (SQLException sqe) {
      dc.rollback();
      throw new BillingException(sqe.getMessage());
    } finally {
      dc.setAutoCommit(true);
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
    String query = "SELECT " + JOIN_COLUMNS + " FROM " + JOIN_TABLE + " WHERE id_devis = '" + d.getNumber() + "'";
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

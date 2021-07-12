/*
 * @(#)InvoiceIO.java 2.14.3 07/07/17
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
import net.algem.accounting.*;
import net.algem.planning.DateFr;
import net.algem.security.User;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;
import net.algem.util.GemLogger;
import net.algem.util.model.Model;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.14.3
 * @since 2.3.a 22/12/11
 */
public class InvoiceIO
        extends BillingIO
{

  public static final String TABLE = "facture";
  public static final String ITEM_TABLE = "article_facture";
  private static final String COLUMNS = "numero,date_emission,etablissement,emetteur,debiteur,prestation,reference,adherent,acompte";
  private static final String ITEM_STATEMENT = "SELECT id_echeancier, id_article, quantite FROM " + ITEM_TABLE + " WHERE id_facture = ?";

  private DataConnection dc;
  private ItemIO itemIO;

  public InvoiceIO(DataConnection dc) {
    this.dc = dc;
    itemIO = new ItemIO(dc);
  }

  /**
   * Transaction for the creation of an invoice.
   * An invoice may include an orderline with a personal account
   * and does include one item at least {@link InvoiceItem}.
   *
   * @param <T>
   * @param inv new invoice
   * @throws SQLException
   * @throws BillingException if transaction failed
   */
  public <T extends Invoice> void insert(final T inv) throws SQLException, BillingException {

    int last = getLastId(TABLE, dc);

    if (last == -1) {
      throw new BillingException("NULL INVOICE LAST ID");
    }
    inv.inc(last);

    final String q = "INSERT INTO " + TABLE + "(" + COLUMNS + ")" + " VALUES(?,?,?,?,?,?,?,?,?)";
    try {
      dc.withTransaction(new DataConnection.SQLRunnable<Void>()
      {
        @Override
        public Void run(DataConnection conn) throws Exception {
          try (PreparedStatement ps = dc.prepareStatement(q)) {
            ps.setString(1, inv.getNumber());
            ps.setDate(2, new java.sql.Date(inv.getDate().getDate().getTime()));
            ps.setInt(3, inv.getEstablishment());
            ps.setInt(4, inv.getIssuer());
            ps.setInt(5, inv.getPayer());
            ps.setString(6, inv.getDescription().trim());
            ps.setString(7, inv.getReference().trim());
            ps.setInt(8, inv.getMember());
            ps.setDouble(9, inv.getDownPayment());
            GemLogger.info(ps.toString());
            ps.executeUpdate();// create invoice
            // insert/update order lines
            setOrderLines(inv);
            // insertion lignes facture
            for (InvoiceItem item : inv.getItems()) {
              insert(item, inv.getNumber());
            }
            return null;
          }
        }

      });
    } catch (Exception sqe) {
      throw new BillingException(sqe.getMessage());
    }

  }

  /**
   * Updates invoice order lines.
   * @param <T>
   * @param inv invoice instance
   * @throws SQLException
   */
  protected <T extends Invoice> void setOrderLines(T inv) throws SQLException {
    // Insertion échéances
    for (OrderLine ol : inv.getOrderLines()) {
      ol.setInvoice(inv.getNumber());
      boolean billing = ModeOfPayment.FAC.toString().equals(ol.getModeOfPayment());
      if (billing) {
        ol.setDocument(inv.getNumber()); // par défaut, numéro de pièce = numéro de facture
        ol.setPaid(true);// encaissé par défaut pour les échéances de type "FAC"
        ol.setDate(inv.getDate());// la date des échéances de facturation reprend celle de la facture
      }
      if (inv.isCreditNote()) {
        ol.setAmount(-ol.getAmount());// reverse
      }
      if (ol.getId() == 0) {
        OrderLineIO.insert(ol, dc);
      } else {
        OrderLineIO.update(ol, dc);// update echeancier
      }
      if (billing && AccountUtil.isRevenueAccount(ol.getAccount())) {
        OrderLineIO.transfer(ol, dc);// on force la mise à jour du transfer pour les échéances de classe 7
      }
    }
  }

  public void update(final Invoice inv) throws BillingException {
    final String q = "UPDATE " + TABLE + " SET date_emission = ?,etablissement =?,debiteur =?,prestation =?,reference =?,acompte =? WHERE numero =?";
    try {
      dc.withTransaction(new DataConnection.SQLRunnable<Void>()
      {
        @Override
        public Void run(DataConnection conn) throws Exception {
          try (PreparedStatement ps = dc.prepareStatement(q)) {
            ps.setDate(1, new java.sql.Date(inv.getDate().getDate().getTime()));
            ps.setInt(2, inv.getEstablishment());
            ps.setInt(3, inv.getPayer());
            ps.setString(4, inv.getDescription());
            ps.setString(5, inv.getReference());
            ps.setDouble(6, inv.getDownPayment());
            ps.setString(7, inv.getNumber());
            GemLogger.info(ps.toString());
            ps.executeUpdate(); // update
            // cleaning items
            String q2 = "DELETE FROM " + ITEM_TABLE + " WHERE id_facture = '" + inv.getNumber() + "'";
            dc.executeUpdate(q2);
            for (InvoiceItem invoiceItem : inv.getItems()) {
              insert(invoiceItem, inv.getNumber());
            }
          }
          return null;
        }
      });

    } catch (Exception sqe) {
      throw new BillingException(sqe.getMessage());
    }

  }

  public void delete(Invoice inv) throws SQLException {
    String query = "DELETE FROM " + TABLE + " WHERE numero = '" + inv.getNumber() + "'";
    dc.executeUpdate(query);
  }

  /**
   * Search by invoice number.
   *
   * @param number invoice id
   * @return an invoice
   * @throws SQLException
   */
  public Invoice findBy(String number) throws SQLException {
    List<Invoice> invoices = find(" WHERE numero = " + number);
    return invoices == null ? null : invoices.get(0);
  }

  /**
   * Search invoices by {@literal payer} or {@literal idper}.
   *
   * @param idper payer's (or member's) id
   * @param andPeriod optional period query
   * @return a list of invoices
   * @throws SQLException
   */
  public List<Invoice> findBy(int idper, String andPeriod) throws SQLException {
    String where = " WHERE (debiteur = " + idper + " OR adherent = " + idper + ")";
    if (andPeriod != null) {
      where += andPeriod;
    }
    return find(where);
  }

  /**
   * Search for invoices on jointure 2.17
   *
   * @param where
   * @return a list of invoices
   * @throws SQLException
   */
  //FIXME ERIC TODO 
  public static ResultSet invoiceJoinSelect(String where) throws SQLException {
      String query = "select * from article_facture join facture f on f.NUMERO=id_facture join echeancier2 e on e.oid=id_echeancier "+where+" order by id_facture,id_echeancier";
      
      return DataCache.getDataConnection().executeQuery(query);
  }
  
  public static int countInvoice(String where) {
//      String query = "select count(distinct numero) from facture  "+where; 877 avec jointure = 843
      String query = "select count(distinct numero) from article_facture join facture f on f.NUMERO=id_facture join echeancier2 e on e.oid=id_echeancier "+where;
      
    try {
        ResultSet rs = DataCache.getDataConnection().executeQuery(query);
        if (rs.next()) {
            return rs.getInt(1);
        }
    } catch (Exception ex) {}
    return 0;
  }
  
  /**
   * Search for invoices.
   *
   * @param where
   * @return a list of invoices
   * @throws SQLException
   */
  public List<Invoice> find(String where) throws SQLException {

    List<Invoice> lv = new ArrayList<Invoice>();
    String query = "SELECT " + COLUMNS + " FROM " + TABLE + where;
    query += " ORDER BY substring(numero, 5)::integer";

    try (ResultSet rs = dc.executeQuery(query)) {
      while (rs.next()) {
        Invoice inv = new Invoice(rs.getString(1));
        inv.setDate(new DateFr(rs.getString(2)));
        inv.setEstablishment(rs.getInt(3));
        inv.setIssuer(rs.getInt(4));
        inv.setUser((User) DataCache.findId(rs.getInt(4), Model.User));
        inv.setPayer(rs.getInt(5));
        inv.setDescription(unEscape(rs.getString(6)));
        inv.setReference(unEscape(rs.getString(7)));
        inv.setMember(rs.getInt(8));
        inv.setDownPayment(rs.getDouble(9));
  //      inv.setItems(findItems(inv.getNumber()));//
        inv.setOrderLines(findOrderLines(inv));//

        lv.add(inv);
      }
    }
    return lv;
  }

  /**
   * Search for all items in invoice.
   *
   * @param invoice invoice number
   * @return a collection of invoice items
   * @throws SQLException
   */
  public Collection<InvoiceItem> findItems(String invoice) throws SQLException {

    List<InvoiceItem> items = new ArrayList<InvoiceItem>();
    try (PreparedStatement ps = dc.prepareStatement(ITEM_STATEMENT)) {
      ps.setString(1, invoice);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          OrderLine ol = (OrderLine) DataCache.findId(rs.getInt(1), Model.OrderLine);
          if (ol == null) {
            ol = OrderLineIO.find(rs.getInt(1), dc);
          }
          int itemId = rs.getInt(2);
          int qty = rs.getInt(3);

          Item item = null;
          if (itemId > 0) {
            item = (Item) DataCache.findId(itemId, Model.Item);
          }
          if (item == null || itemId == 0) {
            item = new Item(ol, qty);
          }

          InvoiceItem invoiceItem = new InvoiceItem(invoice);
          invoiceItem.setItem(item);
          invoiceItem.setOrderLine(ol);
          invoiceItem.setQuantity(rs.getFloat(3));

          items.add(invoiceItem);
        }
      }
    }
    return items;
  }

  private Collection<OrderLine> findOrderLines(Invoice inv) {
    /*Collection<OrderLine> cl = DataCache.findOrderLines(inv.getNumber());
    if (cl.isEmpty()) {
      String query = "WHERE facture = '" + inv.getNumber() + "'";
      cl =  OrderLineIO.find(query, dc);
    }
    return cl;*/
    return DataCache.findOrderLines(inv.getNumber());
  }

  /**
   * Insertion of an item.
   *
   * @param invItem invoice item
   * @param n invoice number
   * @throws SQLException
   */
  protected void insert(InvoiceItem invItem, String n) throws SQLException {
    String q = "INSERT INTO " + ITEM_TABLE + " VALUES(?,?,?,?)";
    try (PreparedStatement ps = dc.prepareStatement(q)) {
      Item a = invItem.getItem();
      // on n'ajoute un article que s'il n'existe pas ou que s'il provient d'un article standard
      if (a.getId() == 0 || a.isStandard()) {
        a.setStandard(false);
        itemIO.insert(a);//article
      } else {
        itemIO.update(a);
      }
      int olId = (invItem.getOrderLine() == null) ? 0 : invItem.getOrderLine().getId();
      ps.setString(1, n);
      ps.setInt(2, olId);
      ps.setInt(3, a.getId());
      ps.setDouble(4, invItem.getQuantity());
      GemLogger.info(ps.toString());
      ps.executeUpdate();
    }
  }

}

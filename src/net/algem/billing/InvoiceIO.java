/*
 * @(#)InvoiceIO.java 2.7.a 10/01/13
 * 
 * Copyright (c) 1999-2012 Musiques Tangentes. All Rights Reserved.
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
import net.algem.accounting.*;
import net.algem.config.Preference;
import net.algem.planning.DateFr;
import net.algem.security.User;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;
import net.algem.util.model.Model;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.a
 * @since 2.3.a 22/12/11
 */
public class InvoiceIO
        extends BillingIO
{

  public static final String TABLE = "facture";
  public static final String JOIN_TABLE = "article_facture";
  private static final String COLUMNS = "numero, date_emission, etablissement, emetteur, debiteur, prestation, reference, adherent, acompte";
  private static final String KEY = "numero";
  private DataConnection dc;
  private ItemIO itemIO;
  
  private static final String JOIN_COLUMNS = "id_echeancier, id_article, quantite";

  public InvoiceIO(DataConnection dc) {
    this.dc = dc;
    itemIO = new ItemIO(dc);
  }

  /**
   * Transaction for the creation of an invoice.
   * An invoice may include an orderline with a personal account
   * and does include one item at least {@link InvoiceItem}.
   *
   * @param inv new invoice
   * @throws SQLException
   * @throws BillingException if transaction failed
   */
  public <T extends Invoice> void insert(T inv) throws SQLException, BillingException {

    int last = getLastId(TABLE, dc);

    if (last == -1) {
      throw new BillingException("NULL INVOICE LAST ID");
    }
    inv.inc(last);

    String query = "INSERT INTO " + TABLE + " VALUES('" + inv.getNumber()
            + "','" + inv.getDate()
            + "'," + inv.getEstablishment()
            + "," + inv.getIssuer()
            + "," + inv.getPayer()
            + ",'" + escape(inv.getDescription().trim())
            + "','" + escape(inv.getReference().trim())
            + "'," + inv.getMember()
            + "," + inv.getDownPayment()
            + ")";
    //System.out.println(query);

    try {
      dc.setAutoCommit(false);
      dc.executeUpdate(query);// insertion facture
      // insertion/mise à jour des échéances
      setOrderLines(inv);

      // insertion lignes facture
      for (InvoiceItem item : inv.getItems()) {
        insert(item, inv.getNumber());
      }

      dc.commit();
    } catch (SQLException sqe) {
      dc.rollback();
      //System.err.println(sqe.getMessage());
      throw new BillingException(sqe.getMessage());
    } finally {
      dc.setAutoCommit(true);
    }

  }

  /**
   * Updates invoice order lines.
   * @param <T>
   * @param inv invoice instance
   * @throws SQLException 
   */
  protected <T extends Invoice> void setOrderLines(T inv) throws SQLException {
    Preference p = AccountPrefIO.find(AccountPrefIO.PERSONAL_ACCOUNT, dc);
    // Insertion échéances
    for (OrderLine ol : inv.getOrderLines()) {
      ol.setInvoice(inv.getNumber());
      boolean invoiceMp = ModeOfPayment.FAC.toString().equals(ol.getModeOfPayment());
      if (invoiceMp) {
        ol.setDocument(inv.getNumber()); // par défaut, numéro de pièce = numéro de facture
        ol.setPaid(true);// encaissé par défaut pour les échéances de type "FAC"
        ol.setDate(inv.getDate());// la date des échéances de facturation reprend celle de la facture
      }
      if (ol.getId() == 0) { // in fact, invoice lines only
        if (!invoiceMp) {
          Account c = AccountPrefIO.getAccount(p, dc);//
          ol.setAccount(c);//XXX compte de tiers par défaut //compte de l'article plutôt
          Account a = AccountPrefIO.getCostAccount(p, dc);
          ol.setCostAccount(a); // analytique par défaut
        }
        OrderLineIO.insert(ol, dc);
      } else {
        OrderLineIO.update(ol, dc);// update echeancier
      }
      if (invoiceMp && AccountUtil.isRevenueAccount(ol.getAccount())) {
        OrderLineIO.transfer(ol, dc);// on force la mise à jour du transfer pour les échéances de classe 7
      }
    }
  }

  public void update(Invoice inv) throws BillingException {

    String query = "UPDATE " + TABLE + " SET "
            + " date_emission = '" + inv.getDate()
            + "', etablissement = " + inv.getEstablishment()
            //+ ", emetteur = " + f.getIssuer()
            //+ ", debiteur = " + f.getPayer()
            + ", prestation = '" + escape(inv.getDescription().trim())
            + "', reference = '" + escape(inv.getReference().trim())
            + "', acompte = " + inv.getDownPayment()
            + " WHERE numero = '" + inv.getNumber() + "'";
    try {
      dc.setAutoCommit(false);
      dc.executeUpdate(query); // update facture

      // supression des articles dans la table article_facture
      String q2 = "DELETE FROM " + JOIN_TABLE + " WHERE id_facture = '" + inv.getNumber() + "'";
      dc.executeUpdate(q2);

      Collection<OrderLine> old_echeances = findOrderLines(inv);
      Collection<OrderLine> echeances = inv.getOrderLines();

      for (OrderLine o : old_echeances) {
        if (!echeances.contains(o)) {
          OrderLineIO.delete(o, dc);
        }
      }
      for (OrderLine n : echeances) {
        if (n.getId() == 0) {
          OrderLineIO.insert(n, dc);
        } else {
          OrderLineIO.update(n, dc);
        }
      }

      for (InvoiceItem af : inv.getItems()) {
        insert(af, inv.getNumber());
      }

      dc.commit();
    } catch (SQLException sqe) {
      dc.rollback();
      throw new BillingException(sqe.getMessage());
    } finally {
      dc.setAutoCommit(true);
    }

  }

  public void delete(Invoice inv) throws SQLException {
    String query = "DELETE FROM " + TABLE + " WHERE numero = '" + inv.getNumber() + "'";
    dc.executeUpdate(query);
  }

  /**
   * Search by invoice number.
   *
   * @param number
   * @return une facture
   * @throws SQLException
   */
  public Invoice findBy(String number) throws SQLException {
    List<Invoice> invoices = find(" WHERE numero = " + number);
    return invoices == null ? null : invoices.get(0);
  }

  /**
   * Recherche de factures par {@code payer} ou {@code member}.
   *
   * @param payer
   * @param member
   * @return une liste de factures
   * @throws SQLException
   */
  public List<Invoice> findBy(int payer, int member) throws SQLException {
    String where = " WHERE numero IN (SELECT facture FROM echeancier2 WHERE payeur = " + payer + " OR adherent = " + member + ")";
    return find(where);

  }

  /**
   * Search a list of invoices.
   *
   * @param where
   * @return a list
   * @throws SQLException
   */
  public List<Invoice> find(String where) throws SQLException {

    List<Invoice> lv = new ArrayList<Invoice>();
    String query = "SELECT " + COLUMNS + " FROM " + TABLE + where;
    query += " ORDER BY substring(numero, 5)::integer";

    ResultSet rs = dc.executeQuery(query);
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

      inv.setItems(findItems(inv));//
      inv.setOrderLines(findOrderLines(inv));//

      lv.add(inv);
    }

    return lv.isEmpty() ? null : lv;
  }

  /**
   * Search for all items in invoice.
   *
   * @param inv invoice instance
   * @return a collection of invoice items
   * @throws SQLException
   */
  public Collection<InvoiceItem> findItems(Invoice inv) throws SQLException {

    List<InvoiceItem> items = new ArrayList<InvoiceItem>();
    String query = "SELECT " + JOIN_COLUMNS + " FROM " + JOIN_TABLE + " WHERE id_facture = '" + inv.getNumber() + "'";
    //System.out.println(query);
    ResultSet rs = dc.executeQuery(query);
    while (rs.next()) {
      
      OrderLine ol = (OrderLine) DataCache.findId(rs.getInt(1), Model.OrderLine);
      if (ol == null) {
        ol = OrderLineIO.find(rs.getInt(1), dc);
      }
      int itemId = rs.getInt(2);//id_article
      int qty = rs.getInt(3);
      
      Item item = null;
      if (itemId > 0) {
        item = (Item) DataCache.findId(itemId, Model.Item);
//        item = itemIO.findId(itemId);
      }
      if (item == null || itemId == 0) {
        item = new Item(ol, qty);
      }

      InvoiceItem invoiceItem = new InvoiceItem(inv.getNumber());
      invoiceItem.setItem(item);
      invoiceItem.setOrderLine(ol);
      invoiceItem.setQuantity(rs.getFloat(3));

      items.add(invoiceItem);
    }
    return items;
  }

  private Collection<OrderLine> findOrderLines(Invoice inv) {
    Collection<OrderLine> cl = DataCache.findOrderLines(inv.getNumber());
    if (cl.isEmpty()) {
      String query = "facture = '" + inv.getNumber() + "'";
      cl =  OrderLineIO.find(query, dc);
    }
    return cl;
  }

  /**
   * Insertion of an item.
   *
   * @param invItem
   * @param n invoice number
   * @throws SQLException
   */
  protected void insert(InvoiceItem invItem, String n) throws SQLException {

    String query = "INSERT INTO " + JOIN_TABLE + " VALUES('" + n + "',";
    Item a = invItem.getItem();

    // on n'ajoute un article que s'il n'existe pas ou que s'il provient d'un article standard
    if (a.getId() == 0 || a.isStandard()) {
      a.setStandard(false);
      itemIO.insert(a);//article
    } else {
      itemIO.update(a);
    }
    int id_echeancier = (invItem.getOrderLine() == null) ? 0 : invItem.getOrderLine().getId();
    query += id_echeancier + "," + a.getId() + "," + invItem.getQuantity() + ")";
    dc.executeUpdate(query);//jointure
  }
}

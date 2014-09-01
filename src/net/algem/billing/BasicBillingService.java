/*
 * @(#)BasicBillingService 2.8.w 09/07/14
 *
 * Copyright 1999-2013 Musiques Tangentes. All Rights Reserved.
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

import java.sql.SQLException;
import java.util.*;
import net.algem.accounting.*;
import net.algem.config.ConfigKey;
import net.algem.config.ConfigUtil;
import net.algem.config.Param;
import net.algem.contact.Person;
import net.algem.planning.DateFr;
import net.algem.util.DataCache;
import net.algem.util.GemLogger;
import net.algem.util.GemService;
import net.algem.util.MessageUtil;
import net.algem.util.model.GemList;
import net.algem.util.model.Model;

/**
 * Service class for billing.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.w
 * @since 2.3.a 06/02/12
 */
public class BasicBillingService
        extends GemService
        implements BillingService
{

  private static int DEFAULT_SCHOOL;
  private final ItemIO itemIO;
  private final InvoiceIO invoiceIO;
  private final QuoteIO quotationIO;
  private final DataCache dataCache;

  /**
   * Creates an instance of BasicBillingService.
   * @param dataCache
   */
  public BasicBillingService(DataCache dataCache) {
    this.dataCache = dataCache;
    this.dc = DataCache.getDataConnection();
    itemIO = new ItemIO(dc);
    invoiceIO = new InvoiceIO(dc);
    quotationIO = new QuoteIO(dc);
    String s = ConfigUtil.getConf(ConfigKey.DEFAULT_SCHOOL.getKey());
    DEFAULT_SCHOOL = Integer.parseInt(s);
  }

  @Override
  public List<Invoice> getInvoices(int idper) throws SQLException {
    return invoiceIO.findBy(idper);
  }

  @Override
  public List<Invoice> getInvoices() throws SQLException {
    return invoiceIO.find("");
  }

  @Override
  public List<Quote> getQuotations() throws SQLException {
    return quotationIO.find("");
  }

  @Override
  public List<Quote> getQuotations(int idper) throws SQLException {
    return quotationIO.findBy(idper);
  }

  @Override
  public void create(Invoice inv) throws SQLException, BillingException {
    setOrderLine(inv);
    invoiceIO.insert(inv);
  }

  @Override
  public Invoice createInvoiceFrom(Quote q) throws BillingException {

    Invoice iv = new Invoice(q);
    iv.setDate(new DateFr(new Date()));// important
    iv.setItems(q.getItems());
    try {
      q.setEditable(false);
      update(q);
      create(iv);
      return iv;
    } catch(SQLException ex) {
      throw new BillingException(MessageUtil.getMessage("invoicing.create.exception") + "\n" + ex.getMessage());
    }
    
  }

  @Override
  public void create(Quote q) throws SQLException, BillingException {
    quotationIO.insert(q);
  }

  @Override
  public String getContact(int id)  {
    try {
      Person p = (Person) DataCache.findId(id, Model.Person);
      if (p.getOrganization() != null && !p.getOrganization().isEmpty()) {
        return p.getOrganization();
      }
      return p != null ? p.getFirstnameName() : "";
    } catch (SQLException ex) {
       GemLogger.logException(ex);
       return "";
    }
  }

  /**
   * Optionnaly adds order lines when creating an invoice (from scratch or from order line selection).
   * @param inv invoice
   * @throws SQLException 
   */
  private void setOrderLine(Invoice inv) throws SQLException {
    Map<Integer,List<InvoiceItem>> map = mapInvoiceItemsByAccount(inv.getItems());
    for(List<InvoiceItem> items : map.values()) {
      addOrderLines(inv, items);
    }
    // on n'ajoute une ligne de paiement qu'à partir d'une facture générée sans
    // sélection d'échéances
    if (inv.isFromScratch()) {
      inv.addOrderLine(getTotalOrderLine(inv));//ligne de paiement total
    }
  }
  
  /**
   * Map invoice items by account.
   * Items with the same account are put together.
   * @param items
   * @return a map
   */
  private Map<Integer,List<InvoiceItem>> mapInvoiceItemsByAccount(Collection<InvoiceItem> items) {
    Map<Integer, List<InvoiceItem>> map = new HashMap<Integer, List<InvoiceItem>>();
    for (InvoiceItem item : items) {
      if (item.getOrderLine() == null && item.getTotal(true) != 0.0) {
        int c = item.getItem().getAccount();
        if (c == 0) continue;
        List<InvoiceItem> list = map.get(c);
        if (list == null) {
          list = new ArrayList<InvoiceItem>();
        }
        list.add(item);
        map.put(c, list);
      }
    }
    return map;
  }

  /**
   * Adds order lines when creating an invoice.
   * @param inv invoice
   * @param items invoice items
   * @throws SQLException 
   */
  private void addOrderLines(Invoice inv, List<InvoiceItem> items) throws SQLException {
    
    assert(items.size() > 0);
    
    double total = getTotaByAccount(items);
    
    InvoiceItem item = items.get(0);
    Account c = (Account) DataCache.findId(item.getItem().getAccount(), Model.Account);
    Account a = AccountPrefIO.getCostAccount(c.getId(), dc);//analytics
    if (!AccountUtil.isPersonalAccount(c)) {
      // échéance de facturation pour les comptes de classe 7
      OrderLine p = new OrderLine(inv);
      p.setLabel(inv.getDescription());
      p.setAmount(-Math.abs(total));
      p.setModeOfPayment(ModeOfPayment.FAC.toString());
      
      p.setSchool(DEFAULT_SCHOOL);
      p.setAccount(c);
      p.setCostAccount(a);
      p.setPaid(true); // IMPORTANT
      p.setTransfered(true); // IMPPORTANT

      inv.addOrderLine(p);
      for(InvoiceItem i : items) {
        i.setOrderLine(p);// lien avec l'article
      }
      return;
    }
    
    OrderLine n = new OrderLine(inv);
    n.setLabel(inv.getDescription());
    n.setAmount(-Math.abs(total));
    n.setModeOfPayment(ModeOfPayment.FAC.toString()); // F
    
    n.setSchool(DEFAULT_SCHOOL);
    n.setAccount(c);
    n.setCostAccount(a);
    n.setPaid(true); // payé par défaut pour les échéances de facturation

    // échéance de contrepartie (d'encaissement)
    /*OrderLine cp = new OrderLine(inv);
    cp.setLabel("p" + cp.getPayer() + " a" + cp.getMember());
    cp.setModeOfPayment(ModeOfPayment.CHQ.toString());
    cp.setAmount(-n.getAmount()); // en positif
    cp.setAccount(n.getAccount());
    cp.setCostAccount(a);
    cp.setSchool(DEFAULT_SCHOOL);*/

    for(InvoiceItem i : items) {
      i.setOrderLine(n);// lien avec l'article
    }

    inv.addOrderLine(n);
//    inv.addOrderLine(cp);
    
  }
  
  private OrderLine getTotalOrderLine(Invoice inv) {
    OrderLine cp = new OrderLine(inv);
    cp.setLabel("p" + cp.getPayer() + " a" + cp.getMember());
    cp.setModeOfPayment(ModeOfPayment.CHQ.toString());
    cp.setAmount(inv.getTotalATI());
//    cp.setAccount(n.getAccount());
//    cp.setCostAccount(n.getCostAccount());
    cp.setSchool(DEFAULT_SCHOOL);
    
    return cp;

  }
  
  /**
   * Gets the total amount of items in the list.
   * @param items
   * @return a total
   */
  private Double getTotaByAccount(List<InvoiceItem> items) {
    double total = 0.0;
    for(InvoiceItem i : items) {
      total += i.getTotal(true);
    }
    return total;
  }

  /**
   * 
   * @param inv
   * @param it
   * @throws SQLException
   * @deprecated since 2.8.n
   */
  private void addOrderLine(Invoice inv, InvoiceItem it) throws SQLException {
    Account c = (Account) DataCache.findId(it.getItem().getAccount(), Model.Account);
    Account a = AccountPrefIO.getCostAccount(c.getId(), dc);//analytics
    if (!AccountUtil.isPersonalAccount(c)) {
      // échéance de facturation pour les comptes de classe 7
      
      OrderLine pe = new OrderLine(inv, it);
      pe.setSchool(DEFAULT_SCHOOL);
      pe.setAccount(c);
      pe.setCostAccount(a);
      pe.setPaid(true); // IMPORTANT
      pe.setTransfered(true); // IMPPORTANT
      it.setOrderLine(pe); // lien avec l'article
      inv.addOrderLine(pe);
      return;
    }
    // échéance de facturation
    OrderLine ne = new OrderLine(inv, it);
    ne.setSchool(DEFAULT_SCHOOL);
    ne.setAccount(c);
    ne.setCostAccount(a);
    ne.setPaid(true); // payé par défaut pour les échéances de facturation

    // échéance de contrepartie (d'encaissement)
    OrderLine cp = new OrderLine(inv);
    cp.setLabel("p" + cp.getPayer() + " a" + cp.getMember());
    cp.setModeOfPayment(ModeOfPayment.CHQ.toString());
    cp.setAmount(-ne.getAmount()); // en positif
    cp.setAccount(ne.getAccount());
    cp.setCostAccount(a);
    cp.setSchool(DEFAULT_SCHOOL);

    it.setOrderLine(ne); // lien avec l'article

    inv.addOrderLine(ne);
    inv.addOrderLine(cp);
  }
  
  @Override
  public void update(Invoice inv) throws BillingException {
//      setOrderLine(inv);
      invoiceIO.update(inv);
  }

  @Override
  public void update(Quote d) throws BillingException {
    quotationIO.update(d);
  }

  @Override
  public void delete(Invoice inv) throws SQLException {
    invoiceIO.delete(inv);
  }

  @Override
  public Item getItem(int id) throws SQLException {
    return itemIO.findId(id);
  }

  @Override
  public Vector<Item> getItems(String where) throws SQLException {
    return itemIO.find(where);
  }

  @Override
  /**
   * Création d'articles standards.
   */
  public void create(Item it) throws SQLException {
    it.setStandard(true); // article standard par défaut
    itemIO.insert(it);
    dataCache.add(it);
  }

  @Override
  public void update(Item it) throws SQLException {
    itemIO.update(it);
    dataCache.update(it);
  }
  

  @Override
  public Quote duplicate(Quote v){
    Invoice n = new Invoice();
    n.date = new DateFr(new Date());
    n.estab = v.getEstablishment();
    n.issuer = dataCache.getUser().getId();
    n.payer = v.getPayer();
    n.description = v.getDescription();
    n.reference = v.getReference();
    n.member = v.getMember();
    n.downPayment = v.getDownPayment();

    n.items = new ArrayList<InvoiceItem>(v.getItems());
    for(InvoiceItem it : n.getItems()) {
      Item c = copy(it.getItem());
      it.setOrderLine(null);// important
      it.setItem(c);// replace item
    }
    return n;
//    create(n);
  }
  
   /**
   * Returns an item with the same properties that {@code i} except its id.
   * @param orig model
   * @return a copy of model
   */
  private Item copy(final Item orig) {

      Item ni = new Item();
      
      ni.setAccount(orig.getAccount());
      ni.setDesignation(orig.getDesignation());
      ni.setPrice(orig.getPrice());
      ni.setStandard(false);
      ni.setVat(orig.getVat());
      
      return ni;
  }

  @Override
  public void delete(Item it) throws SQLException {
    itemIO.delete(it);
    dataCache.remove(it);
  }
	
  @Override
  public GemList<Account> getAccounts() {
    return dataCache.getList(Model.Account);
  }
	
  @Override
	public Collection<Param> getVat() {
      return dataCache.getList(Model.Vat).getData();
	}
}

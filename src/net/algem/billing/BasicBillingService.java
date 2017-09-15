/*
 * @(#)BasicBillingService 2.15.0 14/09/17
 *
 * Copyright 1999-2017 Musiques Tangentes. All Rights Reserved.
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
import net.algem.planning.DateRange;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;
import net.algem.util.GemLogger;
import net.algem.util.GemService;
import net.algem.util.MessageUtil;
import net.algem.util.model.GemList;
import net.algem.util.model.Model;

/**
 * Service class for billing.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.15.0
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
    // no filter by default for person's history
    List<Invoice> invoices = invoiceIO.findBy(idper, null);
    for (Invoice i : invoices) {
      i.setItems(findItemsByInvoiceId(i.getNumber()));
    }
    return invoices;
  }

  @Override
  public List<Invoice> getInvoices(int idper, Date start, Date end) throws SQLException {
    List<Invoice> invoices = invoiceIO.findBy(idper, " AND date_emission BETWEEN '" + start + "' AND '" + end + "'");
    return invoices;
  }

  @Override
  public List<Invoice> getInvoices() throws SQLException {
    //restrict to financial year
    DateRange range = getFinancialYear();
    return invoiceIO.find(" WHERE date_emission BETWEEN '" + range.getStart() + "' AND '" + range.getEnd() + "'");
  }

  @Override
  public List<Invoice> getInvoices(Date start, Date end) throws SQLException {
    return invoiceIO.find(" WHERE date_emission BETWEEN '" + start + "' AND '" + end + "'");
  }

  @Override
  public Collection<InvoiceItem> findItemsByInvoiceId(String invNumber) throws SQLException {
    return invoiceIO.findItems(invNumber);
  }

  @Override
  public List<Quote> getQuotations() throws SQLException {
    DateRange range = getFinancialYear();
    return quotationIO.find(" WHERE date_emission BETWEEN '" + range.getStart() + "' AND '" + range.getEnd() + "'");
  }

  @Override
  public List<Quote> getQuotations(Date start, Date end) throws SQLException {
    return quotationIO.find(" WHERE date_emission BETWEEN '" + start + "' AND '" + end + "'");
  }

  @Override
  public List<Quote> getQuotations(int idper) throws SQLException {
    // no filter by default for person's history
    return quotationIO.findBy(idper, null);
  }

  @Override
  public List<Quote> getQuotations(int idper, Date start, Date end) throws SQLException {
    return quotationIO.findBy(idper, " AND date_emission BETWEEN '" + start + "' AND '" + end + "'");
  }

  @Override
  public DateRange getFinancialYear() {
    /*Calendar cal = Calendar.getInstance();
    cal.setTime(new Date());
    cal.set(Calendar.MONTH, cal.getActualMinimum(Calendar.MONTH));
    cal.set(Calendar.DAY_OF_YEAR, 1);
    DateRange range = new DateRange();
    range.setStart(new DateFr(cal.getTime()));
    cal.set(Calendar.MONTH, cal.getActualMaximum(Calendar.MONTH));
    cal.set(Calendar.DAY_OF_YEAR, cal.getActualMaximum(Calendar.DAY_OF_YEAR));
    range.setEnd(new DateFr(cal.getTime()));*/
    DateRange range = new DateRange();
    String y = ConfigUtil.getConf(ConfigKey.FINANCIAL_YEAR_START.getKey());
    range.setStart(new DateFr(y));
    Date end = new DateFr(y).getDate();
    Calendar cal = Calendar.getInstance();
    cal.setTime(end);
    cal.add(Calendar.YEAR, 1);
    cal.add(Calendar.DATE, -1);
    range.setEnd(new DateFr(cal.getTime()));
    return range;
  }

  @Override
  public void create(Invoice inv) throws SQLException, BillingException {
    setOrderLine(inv);
    invoiceIO.insert(inv);
  }

  @Override
  public Invoice createInvoiceFrom(final Quote q) throws BillingException {

    final Invoice iv = new Invoice(q);
    iv.setUser(dataCache.getUser());//change to current issuer
    iv.setDate(new DateFr(new Date()));// important

    iv.setItems(new ArrayList<InvoiceItem>());
    for (InvoiceItem it : q.getItems()) {
      InvoiceItem iic = new InvoiceItem();
      iic.setQuantity(it.getQuantity());
      iic.setItem(copy(it.getItem()));
      iv.addItem(iic);
    }
    try {
      return dc.withTransaction(new DataConnection.SQLRunnable<Invoice>() {

        @Override
        public Invoice run(DataConnection conn) throws Exception {
          q.setEditable(false);
          update(q);
          create(iv);
          return iv;
        }
      });
    } catch (Exception ex) {
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
      if (p == null) {
        return "";
      }
      String org = p.getCompanyName();
      return org != null ? org : p.getFirstnameName();
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
  private void setOrderLine(Invoice inv) throws SQLException, BillingException {
    Map<Integer,List<InvoiceItem>> map = mapInvoiceItemsByAccount(inv.getItems(), true);
    for(List<InvoiceItem> items : map.values()) {
      addInvoiceOrderLines(inv, items);
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
  private Map<Integer, List<InvoiceItem>> mapInvoiceItemsByAccount(Collection<InvoiceItem> items, boolean checkLine) throws BillingException, SQLException {
    Map<Integer, List<InvoiceItem>> map = new HashMap<Integer, List<InvoiceItem>>();
    for (InvoiceItem item : items) {
      if (item.getTotal(true) != 0.0 && (item.getOrderLine() == null || !checkLine)) {
        int c = item.getItem().getAccount();
        if (c == 0) {
          continue;
        }
        List<InvoiceItem> list = map.get(c);
        if (list == null) {
          list = new ArrayList<InvoiceItem>();
        }
        // check if vat matches
        if (list.size() > 0) {
          Param v = list.get(0).getItem().getTax();
          if (v.getId() != item.getItem().getTax().getId()) {
            Account acc = (Account) DataCache.findId(c, Model.Account);
            throw new BillingException(MessageUtil.getMessage("invoice.item.matching.vat.exception", new Object[]{acc.getLabel(), v.getValue()}));
          }
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
  private void addInvoiceOrderLines(Invoice inv, List<InvoiceItem> items) throws SQLException {

    assert(items.size() > 0);

    double total = getTotaByAccount(items);
if (total <= 0 || (total - inv.getDownPayment()) <= 0) return; //XXX TEST
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
    Vat vat = item.getItem().getTax();
    n.setTax(vat == null ? 0.0f : vat.getRate());

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

    if (inv.getOrderLines() != null && inv.getOrderLines().size() > 0) {
      List<OrderLine> olines = new ArrayList<OrderLine>(inv.getOrderLines());
      cp.setAccount(olines.get(0).getAccount());
      cp.setCostAccount(olines.get(0).getCostAccount());
    }
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
      invoiceIO.update(inv);
  }

  @Override
  public void update(Quote d) throws BillingException {
    quotationIO.update(d);
  }

  @Override
  public void delete(Quote q) throws SQLException {
    quotationIO.delete(q);
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
  public Quote duplicate(Quote v) {

    Quote n;
    try {
      n = v.getClass().newInstance();
    } catch (ReflectiveOperationException ex) {
      GemLogger.log(ex.getMessage());
      return null;
    }

    n.date = new DateFr(new Date());
    n.estab = v.getEstablishment();
    n.issuer = dataCache.getUser().getId();
    n.payer = v.getPayer();
    n.description = v.getDescription();
    n.reference = v.getReference();
    n.member = v.getMember();
    n.downPayment = v.getDownPayment();

    n.items = new ArrayList<InvoiceItem>();

    for(InvoiceItem it : v.getItems()) {
      InvoiceItem vItem = new InvoiceItem(it.billingId); // orderLine is null
      vItem.setQuantity(it.quantity);
      Item c = copy(it.getItem());
      vItem.setItem(c);
      n.items.add(vItem);
    }
    return n;
  }

  @Override
  public Invoice createCreditNote(Quote source) throws BillingException, SQLException {
    Invoice n = new Invoice();
    n.setDate(new DateFr(new Date()));
    n.setDescription(BundleUtil.getLabel("Credit.note.label") + " " + source.getDescription());
    n.setEstablishment(source.getEstablishment());
    n.issuer = dataCache.getUser().getId();
    n.setPayer(source.getPayer());
    n.setMember(source.getMember());
    n.setReference(source.getNumber());
    n.items = new ArrayList<InvoiceItem>();

    Map<Integer,List<InvoiceItem>> map = mapInvoiceItemsByAccount(source.getItems(), false);
    for (List<InvoiceItem> items : map.values()) {
      InvoiceItem vItem = new InvoiceItem();
      vItem.setQuantity(1); //XXX
      Item e = null;
      double excTotal = 0.0;
      for (InvoiceItem it : items) {
        e = copy(it.getItem());
        excTotal += e.getPrice();
      }

      if (e != null && excTotal > 0.0) {
        e.setPrice(excTotal);
        vItem.setItem(e);
        n.items.add(vItem);
      }

    }

    return n;
  }

   /**
   * Returns an item with the same properties that {@literal orig} except its id.
   * This method must be called when new invoice is created from an existing quote
   * or when a quote is duplicated.
   * @param orig model
   * @return a copy of model
   */
  private Item copy(final Item orig) {

      Item ni = new Item();

      ni.setAccount(orig.getAccount());
      ni.setDesignation(orig.getDesignation());
      ni.setPrice(orig.getPrice());
      ni.setStandard(false);
      ni.setTax(orig.getTax());

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

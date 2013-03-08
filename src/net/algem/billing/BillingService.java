/*
 * @(#)BillingService 2.7.h 22/02/13
 *
 * Copyright 1999-2012 Musiques Tangentes. All Rights Reserved.
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
 * @version 2.7.h
 * @since 2.3.a 06/02/12
 */
public class BillingService
        extends GemService
        implements BillingServiceI
{

  private static String DEFAULT_SCHOOL;
  private ItemIO itemIO;
  private InvoiceIO invoiceIO;
  private QuoteIO quotationIO;
  private DataCache dataCache;

  /**
   * Creates an instance of BillingService.
   */
  public BillingService(DataCache dataCache) {
    this.dataCache = dataCache;
    this.dc = dataCache.getDataConnection();
    itemIO = new ItemIO(dc);
    invoiceIO = new InvoiceIO(dc);
    quotationIO = new QuoteIO(dc);
    DEFAULT_SCHOOL = ConfigUtil.getConf(ConfigKey.DEFAULT_SCHOOL.getKey(), dc);
  }

  @Override
  public List<Invoice> getInvoices(int payer, int member) throws SQLException {
    return invoiceIO.findBy(payer, member);
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
  public List<Quote> getQuotation(int payeur, int adherent) throws SQLException {
    return quotationIO.findBy(payeur, adherent);
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

  private void setOrderLine(Invoice inv) throws SQLException {
    for (InvoiceItem af : inv.getItems()) {
      if (af.getOrderLine() == null && af.getTotal(true) != 0.0) {
        addOrderLine(inv, af);
      }
    }
  }

  private void addOrderLine(Invoice inv, InvoiceItem it) throws SQLException {
    //String ecole = ConfigUtil.getConf(dc, ConfigKey.DEFAULT_SCHOOL.getKey());
    //boolean other = true;
//    Account cc = AccountIO.findId(it.getItem().getAccount(), dc);
    Account a = (Account) DataCache.findId(it.getItem().getAccount(), Model.Account);
    Account ca = AccountPrefIO.getCostAccount(a.getId(), dc);
    if (!AccountUtil.isPersonalAccount(a)) {
      // échéance de facturation pour les comptes de classe 7
      OrderLine pe = new OrderLine(inv, it);
      pe.setSchool(DEFAULT_SCHOOL);
      pe.setAccount(a);
      pe.setCostAccount(ca);
      pe.setPaid(true); // IMPORTANT
      pe.setTransfered(true); // IMPPORTANT
      it.setOrderLine(pe); // lien avec l'article
      inv.addOrderLine(pe);
      return;
    }
    //Collection<Echeancier> facEcheances = d.getInvoiceOrderLines();
    // recherche des échéances dont le compte est égal à celui de l'article
    // // NE PAS SUPPRIMER LE CODE CI-DESSOUS : FONCTIONNALITÉ OPTIONNELLE A METTRE AU POINT LE CAS ECHEANT//
    /*if (facEcheances != null) {
    for (OrderLine e : facEcheances) {
    if (e.getDownPayment().getId() == it.getItem().getDownPayment()) {
    // calculer sous-total pour compte si un article de même compte existe déjà
    e.setAmount(e.getDoubleAmount() - it.getTotal(true)); // on additionne négativement
    //e.setSchool(ecole);
    //af.setOrderLine(e);// pas bon pour les éventuelles suppressions de l'article
    // on ajoute à l'échéancier la contrepartie seulement
    OrderLine cp = new OrderLine(e);
    cp.setModeOfPayment("CHQ");// DEFAULT REGLEMENT
    cp.setAmount(it.getTotal(true));
    cp.setCostAccount(analytique);

    d.addOrderLine(cp); // échéance d'encaissement
    other = false;
    break;
    }
    }
    }*/
//    if (other) { // NE PAS SUPPRIMER
    // échéance de facturation
    OrderLine ne = new OrderLine(inv, it);
    ne.setSchool(DEFAULT_SCHOOL);
    ne.setAccount(a);
    ne.setCostAccount(ca);
    ne.setPaid(true); // payé par défaut pour les échéances de facturation

    // échéance de contrepartie (d'encaissement)
    OrderLine cp = new OrderLine(inv);
    cp.setLabel("p" + cp.getPayer() + " a" + cp.getMember());
    cp.setModeOfPayment(ModeOfPayment.CHQ.toString());
    cp.setAmount(-ne.getAmount()); // en positif
    cp.setAccount(ne.getAccount());
    cp.setCostAccount(ca);
    cp.setSchool(DEFAULT_SCHOOL);

    it.setOrderLine(ne); // lien avec l'article

    inv.addOrderLine(ne);
    inv.addOrderLine(cp);
    /*} else { // NE PAS SUPPRIMER
    d.getOrderLines().removeAll(facEcheances);
    d.getOrderLines().addAll(facEcheances);
    }*/
  }

  @Override
  public void update(Invoice inv) throws BillingException {

    try {
      setOrderLine(inv);
      // le compte, la désignation et le montant d'un article peuvent être modifiés.
      // l'échéancier doit le refléter pour la mise à jour dynamique
      Collection<OrderLine> toUpdate = new ArrayList<OrderLine>();

      for (InvoiceItem af : inv.getItems()) {
        OrderLine u = af.getOrderLine();
        if (u == null || u.getId() == 0) { // on ne met pas à jour les nouvelles échéances
          continue;
        }
//        Account c = AccountIO.findId(af.getItem().getAccount(), dc);
        Account c = (Account) DataCache.findId(af.getItem().getAccount(), Model.Account);
        u.setAccount((c == null ? new Account(af.getItem().getAccount()) : c));
        //u.setDate(d.getDate());
        u.setLabel(af.getItem().getDesignation());
        u.setAmount(-AccountUtil.getIntValue(af.getTotal(true))); // echéances de facturation montant négatif
        toUpdate.add(u);
      }

      if (!toUpdate.isEmpty()) {
        inv.getOrderLines().removeAll(toUpdate);
        inv.getOrderLines().addAll(toUpdate);
      }

      invoiceIO.update(inv);
    } catch (SQLException ex) {
      throw new BillingException(ex.getMessage());
    }
  }

  @Override
  public void update(Quote d) throws BillingException {

    //setEcheancier(d);
    // le compte, la désignation et le montant d'un article peuvent être modifiés.
    // l'échéancier doit le refléter pour la mise à jour dynamique
    //Collection<Echeancier> echeancesToUpdate = new ArrayList<Echeancier>();

    /*for(InvoiceItem af : d.getItems()) {
    OrderLine u = af.getOrderLine();
    if (u == null || u.getId() == 0) { // on ne met pas à jour les nouvelles échéances
    continue;
    }
    Account c = AccountIO.findId(dc, af.getItem().getDownPayment());
    u.setDownPayment((c == null ? new Account(af.getItem().getDownPayment()) : c));
    //u.setDate(d.getDate());
    u.setLabel(af.getItem().getDesignation());
    u.setAmount(-AccountUtil.getIntValue(af.getTotal(true))); // echéances de facturation montant négatif
    echeancesToUpdate.add(u);
    }*/

    /*if (!echeancesToUpdate.isEmpty()) {
    d.getOrderLines().removeAll(echeancesToUpdate);
    d.getOrderLines().addAll(echeancesToUpdate);
    }*/

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

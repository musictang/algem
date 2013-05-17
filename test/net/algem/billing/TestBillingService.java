/*
 * @(#)TestBillingService.java 2.8.a 01/04/13
 *
 * Copyright (c) 1999-2013 Musiques Tangentes. All Rights Reserved.
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import junit.framework.TestCase;
import net.algem.TestProperties;
import net.algem.accounting.Account;
import net.algem.accounting.ModeOfPayment;
import net.algem.accounting.OrderLine;
import net.algem.planning.DateFr;
import net.algem.util.DataConnection;
import net.algem.util.module.GemDesktop;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.a
 * @since 2.3.d
 */
public class TestBillingService extends TestCase
{

  private DataConnection dc;
  private GemDesktop desktop;
  private BillingService service;

  public TestBillingService(String testName) {
    super(testName);
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    dc = TestProperties.getDataConnection();
    service = new BillingService(TestProperties.getDataCache(dc));

  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }

  /*public void testUpdateArticleFacture() throws SQLException, FacturationException {

  int compte_forpro = 13;
  int compte_adhesion = 26;
  //    String idFacture = "12031000";
  int adherent = 1234;
  int debiteur = 1234;
  int emetteur = 16094;
  Param defaut_tva = new Param("1","0.0");

  Facture f = new Facture();
  f.setMember(adherent);
  f.setDebiteur(debiteur);
  f.setPrestation("Test Facture");
  f.setDate(new DateFr(new Date()));
  //    f.setNumero(idFacture);
  f.setEmetteur(emetteur);
  f.setEtablissement(1);
  f.setReference("");

  Account c1 = CompteIO.find(dc, compte_forpro);
  Account c2 = CompteIO.find(dc, compte_adhesion);

  Article a1 = new Article(0, "Article 1", 400, compte_forpro, false);//cot.pro
  a1.setTva(defaut_tva);
  Article a2 = new Article(0, "Article 2", 10, compte_adhesion, false);// adhesion
  a2.setTva(defaut_tva);

  OrderLine e1 = new OrderLine(f);
  e1.setLabel("Article 1 fac");

  e1.setModeOfPayment("FAC");
  e1.setAmount(-a1.getPrix());
  e1.setAccount(c1);
  e1.setCostAccount(new Account(""));

  OrderLine e1b = new OrderLine(e1);
  e1b.setLabel("Article 1 chq");
  e1b.setAmount(-e1.getAmount());
  e1b.setModeOfPayment("CHQ");

  OrderLine e2 = new OrderLine(f);
  e2.setLabel("Article 2 fac");
  e2.setModeOfPayment("FAC");
  e2.setAmount(-a2.getPrix());
  e2.setAccount(c2);
  e2.setCostAccount(new Account(""));

  OrderLine e2b = new OrderLine(e2);
  e2b.setLabel("Article 2 chq");
  e2b.setAmount(-e2.getAmount());
  e2b.setModeOfPayment("CHQ");

  f.addEcheancier(e1);
  f.addEcheancier(e1b);
  f.addEcheancier(e2);
  f.addEcheancier(e2b);

  ArticleFacture af1 = new ArticleFacture(null, a1, e1, 1);
  ArticleFacture af2 = new ArticleFacture(null, a2, e2, 1);

  List<ArticleFacture> articles = new ArrayList<ArticleFacture>();
  articles.add(af1);
  articles.add(af2);
  //    f.addArticle(af1);
  //    f.addArticle(af2);

  service.create(f, articles);

  List<Facture> lf = service.getInvoices(debiteur);
  Facture fc = lf.get(0);
  for (ArticleFacture af : fc.getItems()) {
  if (af.getItem().getPrix() == 10.0) {
  af.setQuantite(2);
  }
  }
  
  //af2.setQuantite(2.0f);

  service.update(f);

  double total = 0.0;
  for (OrderLine e : f.getEcheancesFacturation()) {
  total += e.getDoubleMontant();
  }

  // NETTOYAGE
  for (OrderLine e : f.getEcheances()) {
  EcheancierIO.delete(e, dc);
  }
  for (ArticleFacture a : f.getItems()) {
  service.delete(a.getItem());
  }
  service.delete(f);

  assertTrue("Total ? "+total+" != "+ (-f.getTotalTTC()), total == -f.getTotalTTC());

  }*/
  
	
  public void testCollection() {
    OrderLine e1 = new OrderLine();
    e1.setMember(1234);
    e1.setPayer(1234);
    e1.setDate(new DateFr(new Date()));
    e1.setSchool(0);
    e1.setCurrency("E");
    e1.setLabel("Article 1 fac");
    e1.setModeOfPayment(ModeOfPayment.FAC.toString());
    e1.setAmount(-10);
    e1.setAccount(new Account(""));
    e1.setCostAccount(new Account(""));

    OrderLine e2 = new OrderLine();
    e2.setMember(1234);
    e2.setPayer(1234);
    e2.setDate(new DateFr(new Date()));
    e2.setSchool(0);
    e2.setCurrency("E");
    e2.setLabel("Article 2 fac");
    e2.setModeOfPayment(ModeOfPayment.FAC.toString());
    e2.setAmount(-200);
    e2.setAccount(new Account(""));
    e2.setCostAccount(new Account(""));

    OrderLine e3 = new OrderLine();
    e3.setMember(1234);
    e3.setPayer(1234);
    e3.setDate(new DateFr(new Date()));
    e3.setSchool(0);
    e3.setCurrency("E");
    e3.setLabel("Article 1 chq");
    e3.setModeOfPayment("CHQ");
    e3.setAmount(200);
    e3.setAccount(new Account(""));
    e3.setCostAccount(new Account(""));

    OrderLine e4 = new OrderLine();
    e4.setMember(1234);
    e4.setPayer(1234);
    e4.setDate(new DateFr(new Date()));
    e4.setSchool(0);
    e4.setCurrency("E");
    e4.setLabel("Article 2 chq");
    e4.setModeOfPayment("CHQ");
    e4.setAmount(10);
    e4.setAccount(new Account(""));
    e4.setCostAccount(new Account(""));

    Collection<OrderLine> c1 = new ArrayList<OrderLine>();
    c1.add(e1);
    c1.add(e2);
    c1.add(e3);
    c1.add(e4);

    List<OrderLine> c2 = new ArrayList<OrderLine>();
    for (OrderLine e : c1) {
      if (e.getModeOfPayment().equals(ModeOfPayment.FAC.toString())) {
        c2.add(e);
      }
    }

    Collection<OrderLine> c3 = new ArrayList<OrderLine>(c1);

    OrderLine u = c2.get(0);
    u.setAmount(-20);

    double total = 0.0;

    System.out.println("u" + u.getAmount());
    for (OrderLine c : c3) {
      if (c.getModeOfPayment().equals(ModeOfPayment.FAC.toString())) {
        total += c.getAmount();
      }

    }

    assertTrue("total = " + total, total == -220.00);

  }
	
}

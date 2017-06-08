/*
 * @(#)TestQuotationIO.java 2.14.0 07/06/17
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

import java.sql.SQLException;
import java.util.Date;
import junit.framework.TestCase;
import net.algem.TestProperties;
import net.algem.planning.DateFr;
import net.algem.util.DataConnection;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.14.0 07/06/17
 * @since 2.6.a
 */
public class TestQuotationIO extends TestCase
{

  private DataConnection dc;

  public TestQuotationIO(String testName) {
    super(testName);
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    dc = TestProperties.getDataConnection();
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }

  public void testCreateQuotation() {
    try {
      int account = 1; //cotisation aux cours
      //    String idFacture = "12031000";
      int member = 1234;
      int payer = 1234;
      int issuer = 16094;
      Vat default_vat = new Vat(1, "0.0", null);
      Quote d = new Quote();
      d.setMember(member);
      d.setPayer(payer);
      d.setDescription("Test Devis 1");
      d.setDate(new DateFr(new Date()));
      //    f.setNumber(idFacture);
      d.setIssuer(issuer);
      d.setEstablishment(3501);
      d.setReference("");
      QuoteIO quotationIO = new QuoteIO(dc);
      Item a1 = new Item(0, "Article test", 400d, account, false);
      a1.setTax(default_vat);
      InvoiceItem af = new InvoiceItem(a1);
      d.addItem(af);
      quotationIO.insert(d);
    } catch (SQLException | BillingException ex) {
      fail(ex.getMessage());
    }

  }
}

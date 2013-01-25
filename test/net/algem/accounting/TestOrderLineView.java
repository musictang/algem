/*
 * @(#)TestOrderLineView.java 2.7.a 05/12/12
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
package net.algem.accounting;

import java.awt.Frame;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.ParseException;
import javax.swing.JFormattedTextField;
import junit.framework.TestCase;
import net.algem.TestProperties;
import net.algem.config.ParamTableIO;
import net.algem.config.Preference;
import net.algem.planning.DateFr;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;
import net.algem.util.module.GemDesktop;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.a
 */
public class TestOrderLineView
        extends TestCase
{

  private JFormattedTextField amount;
  private NumberFormat amountFormat;
  private DataCache dataCache;
  private GemDesktop desktop;
  private DataConnection dc;
  private OrderLineView view;

  public TestOrderLineView(String testName) {
    super(testName);
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    dc = TestProperties.getDataConnection();
    dataCache = DataCache.getInstance(dc, System.getProperty("user.name"));
    amountFormat = NumberFormat.getNumberInstance();
    amountFormat.setMaximumFractionDigits(2);
    amountFormat.setMinimumFractionDigits(2);
    amount = new JFormattedTextField(amountFormat);
    view = new OrderLineView(new Frame(), "", dc);
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }

  public void testGetAmount() throws ParseException {
    double excepted = 144.92;
    OrderLine e = new OrderLine();
    e.setAmount(14491);
    amount.setValue(e.getDoubleAmount());
    amount.setText("144,92");

    amount.commitEdit();
    Double m = ((Number) amount.getValue()).doubleValue();
    e.setAmount(m);
    assertTrue(excepted == m);
    assertTrue(excepted == e.getDoubleAmount());

    excepted = 144.00;
    amount.setText("144,00");
    amount.commitEdit();
    m = ((Number) amount.getValue()).doubleValue();
    assertTrue(excepted == m);
    //m = (Double) montant.getValue();// class cast exception Long -> Double

  }

  public void testSetOrderLine() throws SQLException {
    OrderLine e = new OrderLine();
    int member = 1234;
    String prefkey = AccountPrefIO.MEMBER_KEY_PREF;
    Preference p = AccountPrefIO.find(prefkey, dc);

    Account c = AccountIO.find((Integer) p.getValues()[0], dc);
    Account a = new Account(ParamTableIO.findByKey(CostAccountCtrl.tableName, CostAccountCtrl.columnKey, (String) p.getValues()[1], dc));
    e.setAccount(c);
    e.setCostAccount(a);
    e.setMember(member);
    e.setPayer(member);
    e.setDate(new DateFr());
    e.setAmount(0);
    e.setModeOfPayment("CHQ");

    e.setSchool("");
    e.setDocument("");
    e.setLabel("");
    e.setPaid(false);
    e.setTransfered(false);

    view.setOrderLine(e);

    Account expected = view.getAccount();
    assertEquals("compte equality " + c.getKey() + ":" + expected.getKey(), expected, c);

  }
}

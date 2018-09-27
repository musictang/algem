/*
 * @(#)TestOrderLineView.java 2.15.9 02/06/18
 *
 * Copyright (c) 1999-2018 Musiques Tangentes. All Rights Reserved.
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

import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Vector;
import javax.swing.JFormattedTextField;
import net.algem.planning.DateFr;
import net.algem.util.MessageUtil;
import net.algem.util.ui.GemChoice;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Ignore;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.15.9
 */
public class TestOrderLineView {

  private static JFormattedTextField amount;
  private static NumberFormat amountFormat;

  @BeforeClass
  public static void setUp() throws Exception {
    amountFormat = NumberFormat.getNumberInstance();
    amountFormat.setMaximumFractionDigits(2);
    amountFormat.setMinimumFractionDigits(2);
    amount = new JFormattedTextField(amountFormat);
  }

  @Test
  @Ignore
  public void testGetAmount() throws ParseException {
    double expected = 144.92;
    OrderLine e = new OrderLine();
    e.setAmount(14491);
    amount.setValue(e.getDoubleAmount());
    amount.setText("144,92");

    amount.commitEdit();
    Double m = ((Number) amount.getValue()).doubleValue();
    e.setAmount(m);
    assertTrue(expected == m);
    assertTrue(expected == e.getDoubleAmount());

    expected = 144.00;
    amount.setText("144,00");
    amount.commitEdit();
    m = ((Number) amount.getValue()).doubleValue();
    assertTrue(expected == m);

    expected = -1144.57;
    amount.setText("-1144,57");
    amount.commitEdit();
    m = ((Number) amount.getValue()).doubleValue();
    assertTrue("excepted = " + expected + ", value = " + m, expected == m);
    //m = (Double) montant.getValue();// class cast exception Long -> Double

  }

  @Test
  @Ignore
  public void testSetOrderLine() throws SQLException {
    OrderLineView view = mock(OrderLineView.class);
    Account c = new Account(1, "706", "Adhésions", true);
    Account a = new Account(2, "ADH", "Adhésions loisir", true);
    List<Account> accounts = new Vector<>();
    accounts.add(new Account(1, "70602", "Cotisations", true));
    accounts.add(c);
    GemChoice accountChoice = new AccountChoice((Vector<Account>) accounts);

    OrderLine e = new OrderLine();
    int member = 1234;
    e.setAccount(c);
    e.setCostAccount(a);
    e.setMember(member);
    e.setPayer(member);
    e.setDate(new DateFr());
    e.setAmount(0);
    e.setModeOfPayment("CHQ");
    e.setSchool(0);
    e.setDocument("");
    e.setLabel("");
    e.setPaid(false);
    e.setTransfered(false);

    view.setOrderLine(e);
    accountChoice.setSelectedItem(c);
    when(view.getAccount()).thenReturn((Account) accountChoice.getSelectedItem());

    Account expected = view.getAccount();
    assertEquals("compte equality " + c.getKey() + ":" + expected.getKey(), expected, c);

  }

  @Test
  @Ignore
  public void getOrderLinePaidStatus() throws ParseException {
    OrderLineView view = spy(OrderLineView.class);
    OrderLine e = new OrderLine();
    int member = 1234;
    Account revenueAccount = new Account(1, "706", "Prd Adhésions", true);
    Account personalAccount = new Account(10, "411000", "Adhésions", true);
    Account costAccount = new Account(2, "ADH", "Adhésions loisir", true);
    e.setAccount(revenueAccount);
    e.setCostAccount(costAccount);
    e.setMember(member);
    e.setPayer(member);
    e.setDate(new DateFr());
    e.setAmount(0);
    e.setModeOfPayment("FAC");
    e.setSchool(0);
    e.setDocument("");
    e.setLabel("");

    e.setPaid(false);
    e.setTransfered(false);

    e.setPaid(false);
    assertTrue(view.checkPaid(true, e.getModeOfPayment()));

    e.setModeOfPayment("CHQ");
    assertFalse(view.checkPaid(false, e.getModeOfPayment()));
    assertTrue(view.checkPaid(true, e.getModeOfPayment()));
    assertFalse(view.checkTransfered(e));
    // e.paid must not be modified here
    assertFalse(e.isPaid());

    // test personal account
    // when personal account and ModeOfPayment.FAC, paid only must be true
    e.setAccount(personalAccount);
    e.setModeOfPayment("FAC");
    e.setPaid(view.checkPaid(false, e.getModeOfPayment()));
    assertTrue(e.isPaid());
    assertFalse(view.checkTransfered(e));

    e.setTransfered(true);
    assertFalse(view.checkTransfered(e));
    e.setPaid(view.checkPaid(false, e.getModeOfPayment()));
    assertTrue(e.isPaid());

    e.setModeOfPayment("CHQ");
    e.setPaid(false);
    assertFalse(view.checkPaid(false, e.getModeOfPayment()));
    assertTrue(view.checkPaid(true, e.getModeOfPayment()));
    assertFalse(view.checkTransfered(e));
    // e.paid must not be modified here
    assertFalse(e.isPaid());

  }

  @Rule
  public ExpectedException documentRefException = ExpectedException.none();

  //@Test(expected=IllegalArgumentException.class)
  @Test
  public void tooLongDocumentReference() {
    int maxLength = 10;
    final String message = MessageUtil.getMessage("document.number.length.warning", maxLength);
    documentRefException.expect(IllegalArgumentException.class);
    documentRefException.expectMessage(message);
    // alternative : specify a custom matcher
    /*documentRefException.expectMessage(new BaseMatcher<String>() {
      @Override
      public boolean matches(Object o) {
          return ((String)o).startsWith(message);
      }

      @Override
      public void describeTo(Description d) {
        
      }
    }
    );*/
    OrderLineView view = spy(OrderLineView.class);
    view.checkDocumentRef("REF123456789B", maxLength, AccountingExportFormat.DVLOG.getLabel());

  }
  
  @Test
  @Ignore
  public void testValidation() {
    
  }
}

/*
 * @(#)TestEnrolmentOrderUtil.java 2.15.9 04/06/18
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
package net.algem.enrolment;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import net.algem.TestProperties;
import net.algem.accounting.*;
import net.algem.config.Param;
import net.algem.planning.DateFr;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;
import net.algem.util.GemLogger;
import static org.junit.Assert.assertTrue;
import org.junit.*;
import org.mockito.Matchers;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.15.9
 * @since 2.8.n 25/09/13
 */
public class TestEnrolmentOrderUtil {

  private DataConnection dc;
  private DataCache dataCache;
  private Vector<OrderLine> bdLines;

  public TestEnrolmentOrderUtil() {
  }

  @BeforeClass
  public static void setUpClass() throws Exception {
  }

  @AfterClass
  public static void tearDownClass() throws Exception {
  }

  @Before
  public void setUp() throws Exception {
//      dc = TestProperties.getDataConnection();
//      dataCache = TestProperties.getDataCache(dc);
//      dataCache.load(null);
  }

  @After
  public void tearDown() throws SQLException {
//    cleanUp(bdLines);
  }

  @Test
  public void testFractionalPortion() {
    int[] numbers = {14699, 14699, 14699};
    int[] result = new int[numbers.length];
    int[] fractions = new int[numbers.length];
    int total = 3 * 99;
    for (int i = 0; i < numbers.length; i++) {
      int r = numbers[i] % 100;
      fractions[i] = r;
      result[i] = numbers[i] - r;
    }
    int expected[] = {14600, 14600, 14600};
    for (int i = 0; i < numbers.length; i++) {
      assertTrue("" + result[i], expected[i] == result[i]);
    }
    int totalF = 0;
    for (int f : fractions) {
      totalF += f;
    }
    assertTrue("" + totalF, total == totalF);

    int last = 14600 + totalF;
    assertTrue("" + totalF, 14897 == last);

  }

  @Test
  public void testCompletedStandardOrderLines() throws SQLException {
    List<OrderLine> stdLines = new ArrayList<>();
    OrderLine std1 = createOrderLine(1, 1000, "Test 1", "CHQ", new Account(1, "7560", "Adhésion"), new Account(10, "LOISIR", "Formation Loisir"));
    OrderLine std2 = createOrderLine(2, 8000, "Test 2", "CHQ", new Account(2, "70631", "Frais de dossier"), new Account(10, "LOISIR", "Formation Loisir"));
    stdLines.add(std1);
    stdLines.add(std2);
    AccountingServiceImpl service = mock(AccountingServiceImpl.class);
    when(service.exists(Matchers.any(OrderLine.class), anyString(), anyInt())).thenReturn(false);
    ModuleOrder moduleOrder = new ModuleOrder();
    moduleOrder.setPayer(1234);
    moduleOrder.setIdOrder(1000);

    EnrolmentOrderUtil orderUtil = new EnrolmentOrderUtil();
    List<OrderLine> completed = orderUtil.getCompletedStandardOrderLines(moduleOrder, 580, stdLines, service, "01-06-2017", false);
    assertTrue(completed.size() == 2);

    completed = orderUtil.getCompletedStandardOrderLines(moduleOrder, 580, stdLines, service, "01-06-2017", true);
    assertTrue("Should be 4 " + completed.size(), completed.size() == 4);

    stdLines.clear();
    completed = orderUtil.getCompletedStandardOrderLines(moduleOrder, 580, stdLines, service, "01-06-2017", false);
    assertTrue("Should be empty " + completed.size(), completed.isEmpty());

    std1 = createOrderLine(1, 1000, "Test 1", "CHQ", new Account(1, "41100", "Adhésion"), new Account(10, "LOISIR", "Formation Loisir"));
    std2 = createOrderLine(2, 8000, "Test 2", "CHQ", new Account(2, "70631", "Frais de dossier"), new Account(10, "LOISIR", "Formation Loisir"));
    stdLines.add(std1);
    stdLines.add(std2);
    completed = orderUtil.getCompletedStandardOrderLines(moduleOrder, 580, stdLines, service, "01-06-2017", false);
    assertTrue("Should be 3 " + completed.size(), completed.size() == 3);
    OrderLine expected = createOrderLine(1, 1000, "Test 1", "CHQ", new Account(1, "41100", "Adhésion"), new Account(10, "LOISIR", "Formation Loisir"));
    expected.setModeOfPayment(ModeOfPayment.FAC.name());
    expected.setAmount(-10);
    expected.setPaid(true);
    assertTrue("Has not counterpart ? ",completed.contains(expected));


   stdLines.clear();
    std1 = createOrderLine(1, 1000, "Test 1", "CHQ", new Account(1, "41100", "Adhésion"), new Account(10, "LOISIR", "Formation Loisir"));
    std2 = createOrderLine(2, 8000, "Test 2", "CHQ", new Account(2, "41100", "Frais de dossier"), new Account(10, "LOISIR", "Formation Loisir"));
    stdLines.add(std1);
    stdLines.add(std2);
    completed = orderUtil.getCompletedStandardOrderLines(moduleOrder, 580, stdLines, service, "01-06-2017", false);
    assertTrue("Should be 4 " + completed.size(), completed.size() == 4);


stdLines.clear();
    std1 = createOrderLine(1, 1000, "Test 1", "CHQ", new Account(1, "41100", "Cours"), new Account(10, "LOISIR", "Formation Loisir"));
    std2 = createOrderLine(2, 8000, "Test 2", "CHQ", new Account(1, "41100", "Répétitions"), new Account(10, "LOISIR", "Formation Loisir"));
    stdLines.add(std1);
    stdLines.add(std2);
    completed = orderUtil.getCompletedStandardOrderLines(moduleOrder, 580, stdLines, service, "01-06-2017", false);
    assertTrue("Should be 3 " + completed.size(), completed.size() == 3);

    when(service.exists(Matchers.any(OrderLine.class), anyString(), anyInt())).thenReturn(true);
    completed = orderUtil.getCompletedStandardOrderLines(moduleOrder, 580, stdLines, service, "01-06-2017", false);
    assertTrue("Should be empty " + completed.size(), completed.isEmpty());

  }

  private OrderLine createOrderLine(int id, int amount, String label, String modeOfPayment, Account c, Account a) {
    StandardOrderLine def = new StandardOrderLine();
    def.setId(id);
    def.setLabel(label);
    def.setModeOfPayment(modeOfPayment);
    def.setAmount(amount);
    def.setDocument("");
    def.setSchool(1);
    def.setAccount(c);
    def.setCostAccount(a);
//    def.setDate(new Date());
    return new OrderLine(def);
  }

  @Ignore
  public void testOrderLineCreationForQuarterPayment() {

    int orderId = 50000;
    int payer = 1234;
    List<DateFr> dates = new ArrayList<DateFr>();
    dates.add(new DateFr("15-10-2013"));
    dates.add(new DateFr("15-01-2014"));
    dates.add(new DateFr("15-04-2014"));

    OrderLine ol = new OrderLine();
    ol.setMember(payer);
    ol.setLabel("test");
    ol.setAccount(new Account(13, "4110000000", "Cotis. Formation professionnelle / tiers"));
    ol.setPayer(payer);
    ol.setAmount(300);
    ol.setModeOfPayment(ModeOfPayment.FAC.toString());
    ol.setOrder(orderId);
    EnrolmentOrderUtil util = new EnrolmentOrderUtil(null, dc);
    util.setTotalOrderLine(600); // total des modules au trimestre

    ModuleOrder mo = new ModuleOrder();
    mo.setStart(new DateFr("16-09-2013"));
    mo.setEnd(new DateFr("26-06-2014"));
    mo.setModeOfPayment(ModeOfPayment.FAC.toString());
    mo.setPayment(PayFrequency.QUARTER);
    mo.setPrice(300);
    try {
      List<OrderLine> lines = util.getOrderLines(mo, ol);
      assertTrue(lines.size() == 3);

      for (OrderLine o : lines) {
        AccountUtil.createEntry(o, dc);
      }

      bdLines = OrderLineIO.find("WHERE commande = " + orderId + " AND payeur = " + payer, dc);
      assertTrue(bdLines != null && bdLines.size() == 6);
//      assertTrue(bdLines.get(1).getAmount() == 60000);
//      assertTrue("CHQ".bufferEquals(bdLines.get(1).getModeOfPayment()));
//      assertFalse(bdLines.get(1).isPaid());
//      assertFalse(bdLines.get(1).isTransfered());

      cleanUp(bdLines);

      // compte de produit
      ol.setAccount(new Account(1, "7061100000", "Cotisation aux cours"));
      ol.setModeOfPayment(ModeOfPayment.PRL.toString());
      mo.setModeOfPayment(ModeOfPayment.PRL.toString());
      lines = util.getOrderLines(mo, ol);
      assertTrue(lines.size() == 3);

      bdLines = OrderLineIO.find("WHERE commande = " + orderId + " AND payeur = " + payer, dc);
      assertTrue(bdLines == null || bdLines.isEmpty());

      for (OrderLine o : lines) {
        AccountUtil.createEntry(o, dc);
      }

      bdLines = OrderLineIO.find("WHERE commande = " + orderId + " AND payeur = " + payer, dc);
      assertTrue(bdLines != null && bdLines.size() == 3);
//      assertTrue(bdLines.get(1).getAmount() == 60000);
//      assertTrue("PRL".bufferEquals(bdLines.get(1).getModeOfPayment()));
//      assertFalse(bdLines.get(1).isPaid());
//      assertFalse(bdLines.get(1).isTransfered());

      cleanUp(bdLines);

    } catch (SQLException ex) {
      GemLogger.logException(ex);
    }
  }

  @Ignore
  public void testOrderLineCreationForMonthPayment() {

    int orderId = 50000;
    int payer = 1234;
//    List<DateFr> dates = new ArrayList<DateFr>();
//    dates.add(new DateFr("15-10-2013"));
//    dates.add(new DateFr("15-01-2014"));
//    dates.add(new DateFr("15-04-2014"));

    OrderLine ol = new OrderLine();
    ol.setMember(payer);
    ol.setLabel("test");
    ol.setAccount(new Account(13, "4110000000", "Cotis. Formation professionnelle / tiers"));
    ol.setPayer(payer);
    ol.setAmount(300);
    ol.setModeOfPayment(ModeOfPayment.FAC.toString());
    ol.setOrder(orderId);
    EnrolmentOrderUtil util = new EnrolmentOrderUtil(null, dc);
    util.setTotalOrderLine(300); // total des modules au mois
    ModuleOrder mo = new ModuleOrder();
    mo.setStart(new DateFr("16-09-2013"));
    mo.setEnd(new DateFr("26-06-2014"));
    mo.setModeOfPayment(ModeOfPayment.FAC.toString());
    mo.setPayment(PayFrequency.MONTH);
    mo.setPrice(100);
    try {
      List<OrderLine> lines = util.getOrderLines(mo, ol);
      assertTrue(lines.size() == 9);

      bdLines = OrderLineIO.find("WHERE commande = " + orderId + " AND payeur = " + payer, dc);
//      assertTrue(bdLines != null && bdLines.size() == 1);
//      assertTrue(bdLines.get(0).getAmount() == -270000);
//      assertTrue(bdLines.get(0).isPaid());
//      assertFalse(bdLines.get(0).isTransfered());
      for (OrderLine o : lines) {
        AccountUtil.createEntry(o, dc);
      }

      bdLines = OrderLineIO.find("WHERE commande = " + orderId + " AND payeur = " + payer, dc);
      assertTrue(bdLines != null && bdLines.size() == 18);
//      assertTrue(bdLines.get(1).getAmount() == 30000);
//      assertTrue("CHQ".bufferEquals(bdLines.get(1).getModeOfPayment()));
//      assertFalse(bdLines.get(1).isPaid());
//      assertFalse(bdLines.get(1).isTransfered());
//      cleanUp(lines);
    } catch (SQLException ex) {
      GemLogger.logException(ex);
    }
  }

  private void cleanUp(List<OrderLine> bdLines) throws SQLException {
    if (bdLines == null) {
      return;
    }
    for (OrderLine ol : bdLines) {
      OrderLineIO.delete(ol, dc);
    }
  }

}

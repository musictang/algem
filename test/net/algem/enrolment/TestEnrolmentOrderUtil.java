/*
 * @(#)TestEnrolmentOrderUtil.java 2.9.1 14/11/14
 *
 * Copyright (c) 1999-2014 Musiques Tangentes. All Rights Reserved.
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
import java.util.List;
import java.util.Vector;
import net.algem.TestProperties;
import net.algem.accounting.*;
import net.algem.planning.DateFr;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;
import net.algem.util.GemLogger;
import static org.junit.Assert.assertTrue;
import org.junit.*;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.1
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

      dc = TestProperties.getDataConnection();
      dataCache = TestProperties.getDataCache(dc);
      dataCache.load(null);

    }

    @After
    public void tearDown() throws SQLException {
      cleanUp(bdLines);
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
    util.setTotalBase(600); // total des modules au trimestre

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
      ol.setAccount(new Account(1,"7061100000", "Cotisation aux cours"));
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
    util.setTotalBase(300); // total des modules au mois
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
    if (bdLines == null) return;
    for (OrderLine ol : bdLines) {
      OrderLineIO.delete(ol, dc);
    }
  }


}
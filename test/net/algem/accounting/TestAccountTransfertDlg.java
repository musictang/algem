/*
 * @(#)TestAccountTransfertDlg.java 2.15.9 07/06/18
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

import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.Vector;
import javax.swing.JFrame;
import junit.framework.TestCase;
import net.algem.TestProperties;
import net.algem.config.ConfigUtil;
import net.algem.planning.DateFr;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;
import net.algem.util.FileUtil;
import net.algem.util.model.ModelException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Ignore;
import org.junit.Test;
import static org.mockito.Mockito.spy;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.15.9
 */
public class TestAccountTransfertDlg {

  private DataConnection dc;
  private DataCache dataCache;
  private JFrame frame;
  private String path;
  /** compte sans correspondance */
  private static String account1 = "4100000100"; // pas de correspondances pour ce compte
  private static Account account2 = new Account(); // ce compte doit exister 4110000000
  private static Account account3 = new Account(); // compte non tiers 7110000000

  private Account t;

  public void setUp() throws Exception {
//        dc = TestProperties.getDataConnection();
//        dataCache = TestProperties.getDataCache(dc);
//        path = ConfigUtil.getExportPath();
//        t = new Account(account1);
//        t.setLabel("compte test");
//        AccountIO.insert(t, dc);
//        account2.setNumber("4110000000");
//
//        account2 = new Account(14, "4110000000", "Adhésions / tiers");
//        account3 = new Account(1,"7110000000", "Cotisations aux cours");
  }

  public void tearDown() throws Exception {
//        AccountIO.delete(t, dc);
  }

  @Ignore
  public void testTiersExport() throws IOException, SQLException, ModelException {

    System.out.println("testTiersExport");
    String file = path + FileUtil.FILE_SEPARATOR + "export";

    DateFr day = new DateFr("15-12-2012");
    int payer = 12;
    int member = 12;
    AccountExportService exportService = new ExportDvlogPGI(dc);
    CommonAccountTransferDlg dlg = new CommonAccountTransferDlg(new JFrame(), dataCache, exportService);
    Vector<OrderLine> orderLines = new Vector<OrderLine>();

    // Détection d'erreurs de correspondance de comptes
    orderLines.add(buildInvoiceOrderLine(day, payer, member, -50000, t));
    orderLines.add(buildInvoiceOrderLine(day, payer, member, -30000, t));

    orderLines.add(buildInvoiceOrderLine(day, payer, member, -30000, account2));
    orderLines.add(buildInvoiceOrderLine(day, payer, member, -25855, account2));

    int err = exportService.tiersExport(file, orderLines);
    int expected = 2;
    assertTrue("Pas d'erreurs détectées ?", err > 0);
    assertTrue("Le nombre d'erreurs est faux", expected == err);

    // Détection d'erreurs de compte
    // Un règlement de type FAC entraîne un compte de tiers (4 ou C)
    orderLines.add(buildInvoiceOrderLine(day, payer, member, -30000, account3));
    expected = 3;
    err = exportService.tiersExport(file, orderLines);
    assertTrue("Le nombre d'erreurs est faux", expected == err);

    FileReader fr = new FileReader(file + ".log");
    assertTrue(fr != null);

    // vérification du fichier de log
    int nl = FileUtil.getNumberOfLines(file + ".log");

    assertTrue(nl == expected);

  }

  /**
   * Creates an invoice orderline instance.
   *
   * @param day orderLine date
   * @param payer
   * @param member
   * @param amount
   * @param c account
   * @return a new orderline
   */
  private OrderLine buildInvoiceOrderLine(DateFr day, int payer, int member, int amount, Account c) {
    OrderLine e = new OrderLine();
    e.setPayer(payer);
    e.setMember(member);
    e.setDate(day);
    e.setAccount(c);
    e.setAmount(amount);

    e.setSchool(0);
    e.setOrder(0);
    e.setLabel("pay " + payer + " adh" + member);

    e.setDocument("");
    e.setModeOfPayment("FAC");
    e.setPaid(true);
    e.setTransfered(false);

    return e;
  }

  /**
   * Test the exclusion of orderlines with a personal account not associated with a billing.
   */
  @Test
  public void testFilter() {
    CommonAccountTransferDlg mockTransferDlg = spy(CommonAccountTransferDlg.class);
    Vector<OrderLine> lines = new Vector<>();
    lines.add(buildOrderLine(ModeOfPayment.CHQ, "CHQ1", 20.0, new Account(1, "411000", "Formation"), ""));
    lines.add(buildOrderLine(ModeOfPayment.CHQ, "CHQ2", 30.0, new Account(1, "411000", "Formation"), null));
    lines.add(buildOrderLine(ModeOfPayment.CHQ, "CHQ3", 10.0, new Account(1, "411000", "Formation"), "181024"));

    Vector<OrderLine> expected = mockTransferDlg.filter(lines);
    assertTrue(expected.size() == 1);
    assertEquals("CHQ3", expected.get(0).getDocument());

    lines.clear();

    lines.add(buildOrderLine(ModeOfPayment.CHQ, "CHQ1", 20.0, new Account(1, "70600", "Formation"), null));
    lines.add(buildOrderLine(ModeOfPayment.CHQ, "CHQ2", 100.0, new Account(1, "411000", "Formation"), "181024"));
    lines.add(buildOrderLine(ModeOfPayment.PRL, "PRL2", 200.0, new Account(1, "70600", "Formation"), "181025"));
    lines.add(buildOrderLine(ModeOfPayment.CHQ, "CHQ3", 10.0, new Account(1, "75600", "Adhésion"), null));
    expected = mockTransferDlg.filter(lines);
    assertTrue(expected.size() == 4);
    assertEquals("CHQ2", expected.get(1).getDocument());

  }

  /**
   *
   * @param mop mode of payment
   * @param document document number
   * @param amount total price
   * @param account main account
   * @param invoiceNumber
   * @return an orderline
   */
  private OrderLine buildOrderLine(ModeOfPayment mop, String document, double amount, Account account, String invoiceNumber) {
    int payer = 1234;
    int member = 1234;
    Account costAccount = new Account(1, "TEST", "");
    OrderLine ol = new OrderLine();
    ol.setDate(new Date());
    ol.setPayer(payer);
    ol.setMember(member);
    ol.setOrder(0);
    ol.setLabel("a" + member + " p" + payer);
    ol.setModeOfPayment(mop.name());
    ol.setAmount(amount);
    ol.setDocument(document);
    ol.setSchool(1);
    ol.setAccount(account);
    ol.setPaid(true);
    ol.setTransfered(false);
    ol.setCurrency("E");
    ol.setCostAccount(costAccount);
    ol.setInvoice(invoiceNumber);
    ol.setGroup(0);
    ol.setTax(0.0F);

    return ol;

  }

}

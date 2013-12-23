/*
 * @(#)TestAccountTransfertDlg.java 2.8.a 01/04/13
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

package net.algem.accounting;

import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
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

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.a
 */
public class TestAccountTransfertDlg extends TestCase {
    
    public TestAccountTransfertDlg(String testName) {
        super(testName);
    }

    private DataConnection dc;
    private DataCache dataCache;
    private JFrame frame;
    private String path;
    /** compte sans correspondance */
    private static String account1 = "4100000100"; // pas de correspondances pour ce compte
    private static Account account2 = new Account(); // ce compte doit exister 4110000000
    private static Account account3 = new Account(); // compte non tiers 7110000000
    
    private Account t;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        dc = TestProperties.getDataConnection();
        dataCache = TestProperties.getDataCache(dc);
        path = ConfigUtil.getExportPath(dc);
        t = new Account(account1);
        t.setLabel("compte test");
        AccountIO.insert(t, dc);
        account2.setNumber("4110000000");
        
        account2 = new Account(14, "4110000000", "Adhésions / tiers");
        account3 = new Account(1,"7110000000", "Cotisations aux cours");
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        AccountIO.delete(t, dc);
    }


  /**
   * Test 
   */
  public void testTiersExport() throws IOException, SQLException, ModelException {

    System.out.println("testTiersExport");
    String file = path + FileUtil.FILE_SEPARATOR + "export";
    
    DateFr day = new DateFr("15-12-2012");
    int payer = 12;
    int member = 12;
AccountExportService exportService = new ExportDvlogPGI(dc);
    CommunAccountTransferDlg dlg = new CommunAccountTransferDlg(new JFrame(), dataCache, exportService);
    Vector<OrderLine> orderLines = new Vector<OrderLine>();

    // Détection d'erreurs de correspondance de comptes
    orderLines.add(getOrderLine(day, payer, member, -50000, t));
    orderLines.add(getOrderLine(day, payer, member, -30000, t));

    orderLines.add(getOrderLine(day, payer, member, -30000, account2));
    orderLines.add(getOrderLine(day, payer, member, -25855, account2));

    int err = exportService.tiersExport(file, orderLines);
    int expected = 2;
    assertTrue("Pas d'erreurs détectées ?", err > 0);
    assertTrue("Le nombre d'erreurs est faux", expected == err);

    // Détection d'erreurs de compte
    // Un règlement de type FAC entraîne un compte de tiers (4 ou C)
    orderLines.add(getOrderLine(day, payer, member, -30000, account3));
    expected = 3;
    err = exportService.tiersExport(file,orderLines);
    assertTrue("Le nombre d'erreurs est faux", expected == err);

    FileReader fr = new FileReader(file+".log");
    assertTrue(fr != null);

    // vérification du fichier de log
    int nl = FileUtil.getNumberOfLines(file+".log");

    assertTrue(nl == expected);

  }


  /**
   * Création d'une échéance.
   * @param day date échéance
   * @param payer
   * @param member adhérent
   * @param amount
   * @param c numéro de compte
   * @return une échéance
   */
  private OrderLine getOrderLine(DateFr day, int payer, int member, int amount, Account c) {
    OrderLine e = new OrderLine();
    e.setPayer(payer);
    e.setMember(member);
    e.setDate(day);
    e.setAccount(c);
    e.setAmount(amount);
    
    e.setSchool(0);
    e.setOrder(0);
    e.setLabel("pay "+payer+" adh"+member);
    
    e.setDocument("");
    e.setModeOfPayment("FAC");
    e.setPaid(true);
    e.setTransfered(false);
    
    return e;
  }

}

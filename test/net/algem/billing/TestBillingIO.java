/*
 * @(#)TestBillingIO.java 2.9.3.2 12/03/15
 * 
 * Copyright (c) 1999-2015 Musiques Tangentes. All Rights Reserved.
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.algem.TestProperties;
import net.algem.accounting.ModeOfPayment;
import net.algem.accounting.OrderLine;
import net.algem.planning.DateFr;
import net.algem.util.DataConnection;
import static org.junit.Assert.*;
import org.junit.*;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.3.2
 * @since 2.5.a 09/07/12
 */
public class TestBillingIO 
  
{
  
  private DataConnection dc;
  
  public TestBillingIO() {
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
  }
  
  @After
  public void tearDown() {
  }

   @Test
   public void testIncLast() {
     String n1 = "12079";
     String n2 = "120710";
     
     List<String> ft = new ArrayList<String>();
     ft.add(n1);
     ft.add(n2);
     
     Collections.sort(ft);
     assertEquals("12079", ft.get(1));
     Collections.sort(ft, new InvoiceComparator());

     assertEquals(n2, ft.get(1));
     
     ft = new ArrayList<String>();
     ft.add("1206001");
     ft.add("12062");
     ft.add("12063");
     ft.add(n2);
     ft.add("12074");
     ft.add("12075");
     ft.add("12076");
     ft.add("12077");
     ft.add("12078");
     ft.add("12079");
     
     Collections.sort(ft, new InvoiceComparator());
     assertEquals(n2, ft.get(ft.size() -1));
     
     Invoice f = new Invoice();
     f.setDate(new DateFr("25-07-2012"));
     f.inc(Integer.parseInt(n2.substring(4)));
     assertEquals("120711", f.getNumber());
     
     f.inc(99);
     assertEquals("1207100", f.getNumber());
     
     f.inc(9);
     assertEquals("120710", f.getNumber());
     
     f.setDate(new DateFr("25-09-2012"));
     f.inc(999);
     
     assertEquals("12091000", f.getNumber());
   }
   
   @Ignore
   public void testGetLastId() throws SQLException {
     String query = "DROP TABLE IF EXISTS fact"; 
     dc.executeUpdate(query);
     query = "UPDATE config set valeur = '1' where clef='Compta.Numero.Facture'";
     dc.executeUpdate(query);
     query = "CREATE TABLE fact (numero varchar(10))";
     dc.executeUpdate(query);
     int last = BillingIO.getLastId("fact", dc);

     assertTrue(1 == last);
     Invoice f = new Invoice();
     f.setDate(new DateFr("25-07-2012"));
     f.inc(last);
     assertEquals("12072", f.getNumber());
     
     query = "INSERT INTO fact VALUES('12061');";
     dc.executeUpdate(query);
     last = BillingIO.getLastId("fact", dc);
     assertTrue(1 == last);
     f.inc(last);
     assertEquals("12072", f.getNumber());
     
     query += "INSERT INTO fact VALUES('12072');";
     query += "INSERT INTO fact VALUES('12079');";
     query += "INSERT INTO fact VALUES('120710');";
     query += "INSERT INTO fact VALUES('120299');";
     query += "INSERT INTO fact VALUES('1301100');";
     dc.executeUpdate(query);
     last = BillingIO.getLastId("fact", dc);
     assertTrue(100 == last);

     f.setDate(new DateFr("25-01-2013"));
     f.inc(last);
     
     assertEquals("1301101", f.getNumber());
     
   }
   
   @Test
   public void testOrderLine() {
     OrderLine e1 = new OrderLine();
     e1.setLabel("e1");
     e1.setModeOfPayment(ModeOfPayment.FAC.toString());
     
     OrderLine e2 = new OrderLine(e1);
     
     e2.setLabel("e2");
     assertFalse(e1.getLabel().equals(e2.getLabel()));
     
   }
}

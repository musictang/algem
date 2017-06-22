/*
 * @(#) TestOrderLine.java Algem 2.14.0 21/06/17
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
 */
package net.algem.accounting;

import net.algem.planning.DateFr;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 */
public class TestOrderLine {

  public TestOrderLine() {
  }

  @BeforeClass
  public static void setUpClass() {
  }

  @AfterClass
  public static void tearDownClass() {
  }

  @Before
  public void setUp() {
  }

  @After
  public void tearDown() {
  }

  // TODO add test methods here.
  // The methods must be annotated with annotation @Test. For example:
  //
   @Test
  public void testVat() {
    int a = 21100;
    float t = 5.5f;
    double coeff = 100d / (100 + t);

    double ht = (Math.abs(a) /100d) * coeff;
    System.out.println("TTC = " + Math.abs(a) /100d);
    System.out.println("HT =" + ht);
    int v = AccountUtil.getIntValue(ht);
    System.out.println(v);
    assertTrue(20000 == v);
    System.out.println("==========");
    a = 20005;
    ht = (Math.abs(a) /100d) * coeff;
    System.out.println("TTC = " + Math.abs(a) /100d);
    System.out.println("HT =" +ht);
    v = AccountUtil.getIntValue(ht);
    System.out.println(v);
    assertTrue(18962 == v);
  }

  @Test
  public void testToString() {
    OrderLine ol = new OrderLine();
    ol.setPayer(1234);
    ol.setMember(22250);
    ol.setDate(new DateFr("02-11-2017").getDate());
    ol.setModeOfPayment("CHQ");
    ol.setLabel("Test p1234 a22250");
    ol.setAmount(125.50);
    ol.setDocument("");
    ol.setTax(0.0f);

    System.out.println(ol.toString());
  }
}

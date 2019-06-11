/*
 * @(#) TestItem.java Algem 2.14.2 29/06/17
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
package net.algem.billing;

import java.util.Locale;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.14.2
 * @since 2.14.2 29/06/17
 */
public class TestItem
{
  
  public TestItem() {
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
  
  @Test
  public void testKey() {
    Item i = new Item();
    i.setTax(new Vat(1,"0.0",null));
    String expected = String.format(Locale.US,"%4.2f",0.0);
    assertEquals(expected,expected,"0.00");
    expected = String.format(Locale.US,"%4.2f",0f);
    assertEquals(expected,expected,"0.00");
    expected = String.format(Locale.FRANCE,"%4.2f",0f);
    assertEquals(expected,expected,"0,00");
  }
  
}

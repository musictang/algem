/*
 * @(#) TestGemDateTime.java Algem 2.12.0 07/03/2017
 *
 * Copyright (c) 1999-2016 Musiques Tangentes. All Rights Reserved.
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
package net.algem.planning;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @since 2.12.0 07/03/2017
 */
public class TestGemDateTime
{
  
  public TestGemDateTime() {
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
  public void testEquality() {
    Hour h1 = new Hour("10:00");
    Hour h2 = new Hour("12:00");
    Hour h3 = new Hour("13:00");
    Hour h4 = new Hour("15:00");
    DateFr d1 = new DateFr("07-03-2017");
    DateFr d2 = new DateFr("07-03-2017");
    DateFr d3 = new DateFr("08-03-2017");
    GemDateTime dt1 = new GemDateTime(d1, new HourRange(h1, h2));
    GemDateTime dt2 = new GemDateTime(d2, new HourRange(h3, h4));
    GemDateTime dt3 = new GemDateTime(d3, new HourRange(h3, h4));
    assertNotEquals(dt1, dt2);

    assertTrue(dt1.hashCode() != dt2.hashCode());
    assertFalse(h1.hashCode() == h2.hashCode());
    assertFalse(h1.hashCode() == h3.hashCode());
    assertTrue(h1.hashCode() == new Hour("10:00").hashCode());
    
    Set<GemDateTime> hSet = new HashSet<>();
    hSet.add(dt1);
    hSet.add(dt2);
    hSet.add(dt3);
    assertTrue(hSet.size() == 3);
    
    Set<GemDateTime> tSet = new TreeSet<>();
    tSet.add(dt1);
    tSet.add(dt2);
    tSet.add(dt3);
    System.out.println(tSet);
    assertTrue(tSet.size() == 3);
  }
}

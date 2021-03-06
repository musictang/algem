/*
 * @(#)TestHour.java	2.15.0 30/08/2017
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
 *
 */
package net.algem.planning;

import static org.junit.Assert.*;
import org.junit.*;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.15.0
 * @since 2.7.a 11/06/2013
 */
public class TestHour
{

  public TestHour() {
  }

  @BeforeClass
  public static void setUpClass() throws Exception {
  }

  @AfterClass
  public static void tearDownClass() throws Exception {
  }

  @Before
  public void setUp() {
  }

  @After
  public void tearDown() {
  }

  @Test
  public void testParseAndDisplayTime() {
    Hour h = new Hour(360);

    assertTrue(6 == h.getHour());
    assertEquals("06:00", h.toString());

    String[] hs = {"25:00:00", "40:00:00"};
    int total = 0;
    h = new Hour();
    for (String hh : hs) {
      h.set(hh);
      total += h.toMinutes();

    }
    assertTrue(total == 3900);
//      Hour t = new Hour(total);
//      System.out.print(t.toString() + " " + t.toMinutes());
    assertTrue(65 == total / 60);

    String s = "09:45:00";
    int hh = Integer.parseInt(s.substring(0, 2));
    int hm = Integer.parseInt(s.substring(3, 5));

    assertTrue(9 == hh);
    assertTrue(45 == hm);

    s = "25:30:00";
    h.set(s);
    assertEquals("25:30", h.toString());
    s = "250:30:00";
    int idx = s.indexOf(":");
    hh = Integer.parseInt(s.substring(0, idx));
    hm = Integer.parseInt(s.substring(idx + 1, idx + 3));
    assertEquals("250:30", hh + ":" + hm);

    hm = -10;
    h = new Hour(hm);
    //System.out.println(h);
    assertEquals("00:00", h.toString());
  }

  @Test
  public void testParseTimeString() {
    String t = "328:00:00";
    int expected = Hour.getMinutesFromString(t);
    assertTrue(String.valueOf(expected), expected == 19680);
    t = "328";
    expected = Hour.getMinutesFromString(t);
    assertTrue(String.valueOf(expected), expected == 19680);
    t = null;
    expected = Hour.getMinutesFromString(t);
    assertTrue(String.valueOf(expected), expected == 0);
    t = "";
    expected = Hour.getMinutesFromString(t);
    assertTrue(String.valueOf(expected), expected == 0);

    t = "328:15:00";
    expected = Hour.getMinutesFromString(t);
    assertTrue(String.valueOf(expected), expected == 19695);
  }



  @Test
  public void testConstructor() {
    Hour h = new Hour("388:30:00");
    String t = "388:30:00";;
    int min = Hour.getMinutesFromString(t);
    assertTrue("expected :" + 23310 + ", result :" + min, min == 23310);
    assertFalse(h.toMinutes() == 23310);

    h = new Hour(23310);
    assertFalse(h.toMinutes() == 23310);
    h = new Hour("99:30:00");
    assertTrue(h.toMinutes() == 5970);
  }
}
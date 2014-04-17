/*
 * @(#)TestDateTime.java	2.8.t 11/04/14
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
package net.algem.planning;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import static org.junit.Assert.*;
import org.junit.*;

/**
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.t
 * @since 2.8.t 11/04/14
 */
public class TestDateTime {

  public TestDateTime() {
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
  public void uniqueness() {
    DateFr d1 = new DateFr("14-04-2014");
    DateFr d2 = new DateFr("14-04-2014");
    Hour s1 = new Hour("10:00");
    Hour s2 = new Hour("10:00");

    Hour e1 = new Hour("10:00");
    Hour e2 = new Hour("10:00");

    HourRange hr1 = new HourRange(s1, e1);
    HourRange hr2 = new HourRange(s2, e2);

    GemDateTime dt1 = new GemDateTime(d1, hr1);
    GemDateTime dt2 = new GemDateTime(d2, hr2);

    assertEquals(d1, d2);
    assertEquals(s1, s2);
    assertEquals(e1, e2);
    assertEquals(hr1, hr2);
    assertEquals(dt1, dt2);
    Set<Integer> uni = new HashSet<Integer>();
    assertTrue(uni.add(new Integer(1)) == true);
    assertTrue(uni.add(new Integer(1)) == false);

    Set<Hour> uniHour = new HashSet<Hour>();
    assertTrue(uniHour.add(s1) == true);
    assertTrue(uniHour.add(s2) == false);

    Set<HourRange> uniRange = new HashSet<HourRange>();
    assertTrue(uniRange.add(hr1) == true);
    assertTrue(uniRange.add(hr2) == false);

    Set<DateFr> unidates = new HashSet<DateFr>();
    assertTrue(unidates.add(d1) == true);
    assertTrue(unidates.add(d2) == false);

    Set<GemDateTime> uniques = new HashSet<GemDateTime>();

    assertTrue(uniques.add(dt1) == true);
    assertTrue(uniques.contains(dt1));
    assertTrue(uniques.add(dt2) == false);

    assertTrue(uniques.size() == 1);

    dt2 = new GemDateTime(d1, new HourRange("15:00", "18:00"));
    uniques.clear();
    uniques.add(dt1);
    uniques.add(dt2);

    assertTrue(uniques.size() == 2);
  }

  @Test
  public void testCollisions() {
    DateFr d1 = new DateFr("14-04-2014");
    DateFr d2 = new DateFr("14-04-2014");
    Hour h1 = new Hour("10:00");
    Hour h2 = new Hour("12:00");

    Hour h3 = new Hour("11:00");
    Hour h4 = new Hour("14:00");

    HourRange hr1 = new HourRange(h1, h2);
    HourRange hr2 = new HourRange(h3, h4);

    assertTrue(hr2.overlap(hr1.getStart(), hr2.getEnd()));

    GemDateTime dt1 = new GemDateTime(d1, hr1);
    GemDateTime dt2 = new GemDateTime(d2, hr2);

    List<GemDateTime> orig = new ArrayList<GemDateTime>();
    orig.add(dt1);
    orig.add(dt2);

    assertTrue(overlap(orig));

    orig.clear();
    d2 = new DateFr("15-04-2014");
    dt2 = new GemDateTime(d2, hr2);
    orig.add(dt1);
    orig.add(dt2);

    assertFalse(overlap(orig));
  }

  private boolean overlap(List<GemDateTime> orig) {
    List<GemDateTime> dup = new ArrayList<GemDateTime>(orig);
    for (int i = 0; i < orig.size(); i++) {
      DateFr d = orig.get(i).getDate();
      HourRange h = orig.get(i).getTimeRange();
      for(int j = 0; j < dup.size(); j++) {
        if (j != i && dup.get(j).getDate().equals(d)) {
          if (dup.get(j).getTimeRange().overlap(h.getStart(), h.getEnd())) {
            return true;
          }
        }
      }
    }
    return false;
  }
}

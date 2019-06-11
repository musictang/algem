/*
 * @(#)TestDateFr.java 2.9.2 30/01/15
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
package net.algem.planning;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import net.algem.config.ConfigKey;
import net.algem.config.ConfigUtil;
import static org.junit.Assert.*;
import org.junit.*;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.2
 */
public class TestDateFr

{

  private Calendar cal;

  public TestDateFr() {
  }

  @BeforeClass
  public static void setUpClass() throws Exception {
  }

  @AfterClass
  public static void tearDownClass() throws Exception {
  }

  @Before
  public void setUp() {
    cal = Calendar.getInstance(Locale.FRANCE);
  }

  @After
  public void tearDown() {
  }

  @Test
  public void testEqualsDateFr() {
    DateFr d1 = new DateFr("10-10-2010");
    DateFr d2 = new DateFr("10-10-2010");

    assertTrue("time not equals ?", d1.getTime() == d2.getTime());
    assertEquals("datefr not equals ?", d1, d2);

    d2.decYear(1);
    assertEquals("10-10-2009", d2.toString());
    assertFalse("datefr equals ?", d1.equals(d2));
  }

  @Test
  public void testDateFormat() {
    Format f1 = new SimpleDateFormat("MMM yyyy", Locale.FRANCE);
    Format f2 = new SimpleDateFormat("EEEE dd-MM-yyyy", Locale.FRANCE);
    DateFr d1 = new DateFr("11-06-2014");
    String fmt1 = f1.format(d1.getDate());
    String fmt2 = f2.format(d1.getDate());
    System.out.println(fmt1);
    assertTrue(fmt1, fmt1.equals("juin 2014"));
    assertTrue(fmt2, fmt2.equals("mercredi 11-06-2014"));
    f2 = new SimpleDateFormat("EEE dd/MM/yyyy");
    fmt2 = f2.format(d1.getDate());
    assertTrue(fmt2, fmt2.equals("mer. 11/06/2014"));
    f2 = new SimpleDateFormat("EEEE dd MMM yyyy");
    fmt2 = f2.format(d1.getDate());
    assertTrue(fmt2, fmt2.equals("mercredi 11 juin 2014"));
  }
  
  @Test
  public void testTimeOffset() {
    Hour startTime = new Hour("22:00");
    Hour endTime = new Hour("23:00");
    int offset = startTime.getLength(endTime);
    assertTrue(offset == 60);

    HourRangePanel rangePanel = new HourRangePanel(startTime, endTime);
    Hour end2 = endTime.end(rangePanel.getLength());
    assertEquals("24:00", end2.toString());
    
    Hour defStartTime = new Hour("09:00");

    int startPlan = defStartTime.getHour();
    int totalh = 24 - startPlan;
    assertTrue(startPlan == 9);
    assertTrue(totalh == 15);
  }
  
}

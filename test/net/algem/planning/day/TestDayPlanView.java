/*
 * @(#)TestDayPlanView.java 2.6.a 08/10/12
 * 
 * Copyright (c) 1999-2012 Musiques Tangentes. All Rights Reserved.
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
package net.algem.planning.day;

import static org.junit.Assert.assertTrue;
import org.junit.*;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 * @since 2.5.a 29/06/12
 */
public class TestDayPlanView {

  private DayPlanView vue;

  public TestDayPlanView() {
  }

  @BeforeClass
  public static void setUpClass() throws Exception {
  }

  @AfterClass
  public static void tearDownClass() throws Exception {
  }

  @Before
  public void setUp() {
    vue = new DayPlanView();
  }

  @After
  public void tearDown() {
  }

  @Test
  public void testLargeurPlage() {
    // pas_x = 100;
    int p = 4;

    int n = 2;
    int l = vue.getScheduleRangeWidth(p, n);
    //(pas_x / 100) * ((n * 100) / p);
    assertTrue("50 != " + l, l == 50);
    p = 3;
    l = vue.getScheduleRangeWidth(p, n);
    assertTrue(l == 66);
    n = 10;
    l = vue.getScheduleRangeWidth(p, n);
    assertTrue(l == 100);
    p = 1;
    n = 1;
    l = vue.getScheduleRangeWidth(p, n);
    assertTrue(l == 100);
  }
}

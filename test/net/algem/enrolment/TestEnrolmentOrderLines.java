/*
 * @(#)TestEnrolmentOrderLines.java 2.7.a 22/11/12
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
package net.algem.enrolment;

import net.algem.TestProperties;
import net.algem.planning.DateFr;
import net.algem.util.DataConnection;
import static org.junit.Assert.assertTrue;
import org.junit.*;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.a
 * @since 2.5.a 11/07/12
 */
public class TestEnrolmentOrderLines {
  
  private DataConnection dc;
  
  public TestEnrolmentOrderLines() {
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
  public void testCalcNumberOfMonths() {
    DateFr d = new DateFr("17-09-2012");
    DateFr f = new DateFr("30-06-2013");
    int expected = 10;
    int n = MemberEnrolment.calcNumberOfMonths(d, f);
    assertTrue(expected == n);
    
    f.incMonth(1);
    f.setDay(2);
    n = MemberEnrolment.calcNumberOfMonths(d, f);
    assertTrue(expected == n);
  }
}

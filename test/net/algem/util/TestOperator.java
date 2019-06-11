/*
 * @(#) TestOperator.java Algem 2.6.a 08/10/12
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
 */
package net.algem.util;

import org.junit.*;
import static junit.framework.Assert.*;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class TestOperator
{

  public TestOperator() {
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
  public void xOr() {
    assertTrue((1 ^ 1) == 0);
    assertTrue((0 ^ 0) == 0);
    assertTrue((1 ^ 0) == 1);
    assertTrue((0 ^ 1) == 1);

    assertTrue((true ^ false));
    assertTrue((false ^ true));
    assertTrue((true ^ true) == false);
    assertTrue((false ^ false) == false);
  }

  @Test
  @Ignore
  public void testSubstring() {
    String s = "chaine";
    String sb = s.substring(0, 50);
    assertEquals("chaine", s);
  }
}

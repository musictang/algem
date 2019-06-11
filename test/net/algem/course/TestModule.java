/*
 * @(#)TestModule.java	 2.8.a 19/04/13
 * 
 * Copyright (c) 1999-2013 Musiques Tangentes. All Rights Reserved.
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

package net.algem.course;

import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.a
 * @since 2.8.a 19/04/13
 */
public class TestModule {

    public TestModule() {
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
    public void testModuleOrder() {
      ModuleComparator mc = new ModuleComparator();
      String t1 = "CFPM - M.A.O.";
      String t2 = "Chorale, Fanfare, Big Band";
      Module m1 = new Module(t1);
      m1.setCode("P");
      Module m2 = new Module(t2);
      m2.setCode("L");

      assertTrue(mc.compare(m1, m2) > 0);
      assertTrue(m1.toString().compareTo(m2.toString()) > 0);
    }
}
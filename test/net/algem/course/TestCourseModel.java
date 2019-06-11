/*
 * @(#)TestCourseModel.java	2.8.t 15/04/14
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

package net.algem.course;

import junit.framework.TestCase;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.t
 * @since 2.4.a 20/04/12
 */
public class TestCourseModel extends TestCase {

    public TestCourseModel(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testUndefined() {
      Course c = new Course("Inst. A DEFINIR");
      assertTrue(c.isUndefined());
      c.setTitle("Inst. à définir");
      assertTrue(c.isUndefined());
      c.setTitle("Inst. a definir");
      assertTrue(c.isUndefined());
      c.setTitle("Inst. N/D");
      assertTrue(c.isUndefined());
      c.setTitle("Inst. N\\D");
      assertTrue(c.isUndefined());
      c.setTitle("Inst. non défini");
      assertTrue(c.isUndefined());
      c.setTitle("Undefined Inst.");
      assertTrue(c.isUndefined());
      c.setTitle("Inst. A DEFI");
      assertFalse(c.isUndefined());
      c.setTitle("");
      assertFalse(c.isUndefined());
      c.setTitle("GUITARE");
      assertFalse(c.isUndefined());
    }

}

/*
 * @(#)TestRehearsalUtil.java 2.6.a 08/10/12
 * 
 * Copyright (c) 1999-2010 Musiques Tangentes. All Rights Reserved.
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

import junit.framework.TestCase;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class TestRehearsalUtil
        extends TestCase
{

  public TestRehearsalUtil(String testName) {
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

  public void testIsAnnulationBefore() {
    int delay = 72;
    java.util.Date today = new java.util.Date();
    DateFr date = new DateFr(today);
    date.incDay(3);
    assertTrue(RehearsalUtil.isCancelledBefore(date, delay));

    date.decDay(1);
    assertFalse(RehearsalUtil.isCancelledBefore(date, delay));

    delay = 48;
    assertTrue(RehearsalUtil.isCancelledBefore(date, delay));
    date.decDay(2);
    assertFalse(RehearsalUtil.isCancelledBefore(date, delay));

  }
}

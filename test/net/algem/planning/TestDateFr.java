/*
 * @(#)TestDateFr.java 2.6.a 08/10/12
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

import java.util.Calendar;
import java.util.Locale;
import junit.framework.TestCase;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class TestDateFr
        extends TestCase
{

  private Calendar cal;

  public TestDateFr(String testName) {
    super(testName);
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    cal = Calendar.getInstance(Locale.FRANCE);
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }

  public void testEqualsDateFr() {
    DateFr d1 = new DateFr("10-10-2010");
    DateFr d2 = new DateFr("10-10-2010");

    assertTrue("time not equals ?", d1.getTime() == d2.getTime());
    assertEquals("datefr not equals ?", d1, d2);

    d2.decYear(1);
    assertEquals("10-10-2009", d2.toString());
    assertFalse("datefr equals ?", d1.equals(d2));

  }
}

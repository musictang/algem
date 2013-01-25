/*
 * @(#)TestParamKeyComparator.java	2.6.a 08/10/12
 *
 * Copyright (c) 1999-2011 Musiques Tangentes. All Rights Reserved.
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

package net.algem.util.param;

import junit.framework.TestCase;
import net.algem.config.ParamKeyComparator;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class TestParamKeyComparator extends TestCase {

  public TestParamKeyComparator(String testName) {
    super(testName);
  }

  @Override
  protected void setUp() throws Exception {

  }

  public void testCompare() {
    Object o1 = new String("ASTVUZZZ");
    Object o2 = new String("ASTVUZZZ");

    ParamKeyComparator pk = new ParamKeyComparator();

    int i = pk.compare(o1, o2);
    assertTrue(i == 0);

    o1 = new String("1");
    o2 = new String("11");

    i = pk.compare(o1, o2);
    assertTrue(i == -1);

  }

}

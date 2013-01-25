/*
 * @(#)TestPlanningService.java	2.6.a 08/10/12
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

package net.algem.planning;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 * @since 2.4.b 30/05/12
 */
public class TestPlanningService extends TestCase {
    
    public TestPlanningService(String testName) {
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

    public void testHourOffset() {
      Hour ha = new Hour("10:00");
      Hour hb = new Hour("14:00");
      Hour hc = new Hour("10:00");
      int offset = ha.getDuration(hb);

      ha.incMinute(offset);
      assertEquals("positif offset", "14:00", ha.toString());
      
      hc.decMinute(offset);
      assertEquals("negatif offset", "06:00", hc.toString());

    }

}

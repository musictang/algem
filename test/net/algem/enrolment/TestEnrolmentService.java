/*
 * @(#)TestEnrolmentService.java	2.8.a 10/04/2013
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
package net.algem.enrolment;

import net.algem.TestProperties;
import net.algem.planning.Hour;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;
import org.junit.*;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.a
 * @since 2.8.a 10/04/2013
 */
public class TestEnrolmentService
{
  
  private static DataCache dataCache;
  private EnrolmentService service;

  public TestEnrolmentService() {
  }

  @BeforeClass
  public static void setUpClass() throws Exception {
    DataConnection dc = TestProperties.getDataConnection();
    dataCache = TestProperties.getDataCache(dc);
    
  }

  @AfterClass
  public static void tearDownClass() throws Exception {
  }

  @Before
  public void setUp() {
    service = new EnrolmentService(dataCache);
  }

  @After
  public void tearDown() {
  }
  
  @Ignore
  public void testRangeOffset() {
    Hour r1 = new Hour("08:00");
    Hour p1 = new Hour("14:00");
    
  }
}
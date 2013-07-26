/*
 * @(#)TestMemberService.java	2.8.j 12/07/13
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
package net.algem.contact.member;

import java.util.Vector;
import net.algem.TestProperties;
import net.algem.planning.DateFr;
import net.algem.util.DataConnection;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.*;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.j
 * @since 2.8.j 12/07/13
 */
public class TestMemberService
{

  private static DataConnection dc;
  private MemberService service;

  public TestMemberService() {
  }

  @BeforeClass
  public static void setUpClass() throws Exception {
    dc = TestProperties.getDataConnection();
  }

  @AfterClass
  public static void tearDownClass() throws Exception {
  }

  @Before
  public void setUp() {
    service = new MemberService(dc);
  }

  @After
  public void tearDown() {
  }

  @Test
  public void testGenerationDate() {

    DateFr start = new DateFr("07-01-2013");
    String initialStart = start.toString();
    DateFr end = new DateFr("26-02-2013");
    String expectedEnd = "25-02-2013";
    int day = 1; // monday

    Vector<DateFr> v = service.generationDate(day, start, end);

    assertTrue(v.size() > 0);
    assertTrue(8 == v.size());

    assertEquals(initialStart, v.get(0).toString());
    assertEquals(expectedEnd, v.get(v.size() - 1).toString());

    v.clear();

  }
}
/*
 * @(#)PersonTest.java	2.8.a 11/04/13
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
package net.algem.contact;

import java.util.Collections;
import java.util.Vector;
import net.algem.planning.ScheduleRangeObject;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.*;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.a
 * @since 2.8.a 11/04/2013
 */
public class PersonTest
{

  public PersonTest() {
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
  public void testComparator() {
    PersonScheduleComparator psComparator = new PersonScheduleComparator();
    String f1 = "Freddy (Mangu) GARCIA";
    String f2 = "Eric CLAUDEON";
    Person p1 = new Person();
    Person p2 = new Person();
    p1.setFirstName(f1);
    p2.setFirstName(f2);
    
    int c = p1.compareTo(p2);
    assertTrue(c == 1);
    c = p2.compareTo(p1);
    assertTrue(c < 0);
    p1.setFirstName("éric");
    c = p1.compareTo(p2);
    assertTrue(c > 0);
    p1.setFirstName(f1);
    
    ScheduleRangeObject s1 = new ScheduleRangeObject();
    ScheduleRangeObject s2 = new ScheduleRangeObject();
    s1.setMember(p1);
    s2.setMember(p2);
    Vector<ScheduleRangeObject> v = new Vector<ScheduleRangeObject> ();
    v.add(s1);
    v.add(s2);
    //['Freddy','Eric']
    assertEquals(f1, v.elementAt(0).getMember().getFirstName());
    
    Collections.sort(v, psComparator);    
    assertEquals(f2, v.elementAt(0).getMember().getFirstName());
  }
}
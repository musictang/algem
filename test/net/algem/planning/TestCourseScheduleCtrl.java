/*
 * @(#) TestCourseScheduleCtrl.java 2.8.w 22/07/14
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
 */
package net.algem.planning;

import java.util.List;
import java.util.Properties;
import javax.swing.JFrame;
import net.algem.TestProperties;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;
import net.algem.util.module.GemDesktop;
import net.algem.util.module.GemDesktopCtrl;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.*;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.w
 */
public class TestCourseScheduleCtrl
{

  private DataCache dataCache;
  private DataConnection dc;
  private GemDesktop desktop;
  private CourseScheduleCtrl ctrl;

  public TestCourseScheduleCtrl() {
  }

  @BeforeClass
  public static void setUpClass() throws Exception {
  }

  @AfterClass
  public static void tearDownClass() throws Exception {
  }

  @Before
  public void setUp() throws Exception {
    dc = TestProperties.getDataConnection();
    dataCache = TestProperties.getDataCache(dc);
    dataCache.load(null);
    desktop = new GemDesktopCtrl(new JFrame(), dataCache, new Properties());
    ctrl = new CourseScheduleCtrl(desktop);
  }

  @After
  public void tearDown() {
  }

  @Test
  public void planification() {

    Hour start = new Hour("14:00");
    Hour end = new Hour("18:00");

    Action a = new Action();
    a.setCourse(4);
    a.setDateStart(new DateFr("17-09-2012"));
    a.setDateEnd(new DateFr("17-09-2012"));
    a.setHourStart(start);
    a.setHourEnd(end);
    a.setLength(60);
    a.setDay(1);
    a.setPeriodicity(Periodicity.WEEK);
    a.setNSessions((short) 1);
    a.setRoom(1);
    a.setTeacher(16094);

    List<Action> la = ctrl.getPlanification(a, 0);
    assertTrue(la.size() == 4);

    int duration = 45;
    int intervall = 15;

    a.setLength(duration);

    a.setHourEnd(end);
    la = ctrl.getPlanification(a, 0);
    assertTrue(la.size() == 5);
    assertEquals(start, la.get(0).getHourStart());
    assertEquals(new Hour("17:45"), la.get(la.size() - 1).getHourEnd());

    a.setHourEnd(end);
    la = ctrl.getPlanification(a, intervall);
    assertTrue(la.size() == 4);
    assertEquals(start, la.get(0).getHourStart());
    assertEquals(new Hour("17:00"), la.get(la.size() - 1).getHourStart());
    assertEquals(new Hour("17:45"), la.get(la.size() - 1).getHourEnd());

    a.setHourEnd(end);
    a.setLength(55);
    intervall = 5;
    la = ctrl.getPlanification(a, intervall);
    assertTrue(la.size() == 4);
    assertEquals(start, la.get(0).getHourStart());
    assertEquals(new Hour("17:00"), la.get(la.size() - 1).getHourStart());
    assertEquals(new Hour("17:55"), la.get(la.size() - 1).getHourEnd());

    a.setHourEnd(end);
    a.setLength(40);
    la = ctrl.getPlanification(a, intervall);
    assertTrue(la.size() == 5);
    assertEquals(start, la.get(0).getHourStart());
    assertEquals(new Hour("16:15"), la.get(la.size() - 2).getHourStart());
    assertEquals(new Hour("16:55"), la.get(la.size() - 2).getHourEnd());
    assertEquals(new Hour("17:00"), la.get(la.size() - 1).getHourStart());
    assertEquals(new Hour("17:40"), la.get(la.size() - 1).getHourEnd());
  }

//  @Test
//  public void duration() {
//    Course c = new Course();
//    c.setCode("AT090");
//    c.setCollective(true);
//    assertTrue(90 == c.getLength());
//
//    c.setCode("AT");
//    assertTrue(60 == c.getLength());
//
//    c.setCode("F.M.");
//    assertTrue(60 == c.getLength());
//
//    c.setCode("FM");
//    assertTrue(0 == c.getLength());
//
//    c.setCode("AT006");
//    assertTrue(6 == c.getLength());
//
//    String code = "at123";
//    c.setCode(code);
//    assertEquals("123", code.substring(code.length() - 3));
//  }
}

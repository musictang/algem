/*
 * @(#)TestConflictService.java 2.8.w 22/07/14
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
package net.algem.planning;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import junit.framework.TestCase;
import net.algem.TestProperties;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;

/**
 * Classe de test pour la détection de conflits lors de modification de
 * plannings.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.w
 * @since 2.4.a 09/05/12
 */
public class TestConflictService
        extends TestCase
{

  private DataCache dataCache;
  private DataConnection dc;
  private PlanningService planningService;
  private ConflictService service;

  public TestConflictService(String testName) {
    super(testName);
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();

    dc = TestProperties.getDataConnection();
    dataCache = TestProperties.getDataCache(dc);

    service = new ConflictService(dc);
    planningService = new PlanningService(dc);
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }

  public void testRoomConflict() throws PlanningException, SQLException {

    int room1 = 3;
    int room2 = 4;
    int room3 = 5;
    int teacher1 = 16094;
    int teacher2 = 18468;

    DateFr start =  new DateFr("07-01-2020");
    DateFr end = new DateFr("14-01-2020");
    Hour hd = new Hour("10:00");
    Hour hf = new Hour("12:00");

    Action a1 = createAction(start, end, hd, hf, teacher1, room1);
    Action a2 = createAction(start, end, hd, hf, teacher2, room2);
    List<DateFr> dates = new ArrayList<DateFr>();
    dates.add(start);
    dates.add(end);
    a1.setDates(dates);
    a2.setDates(dates);
    planningService.plan(a1);
    planningService.plan(a2);
    Vector<Schedule> vp = ScheduleIO.findCourse("WHERE jour = '" + start + "' AND lieux = " + room1, dc);
    Schedule p = vp.elementAt(0);
    CourseSchedule pc = new CourseSchedule(p);

    Vector<ScheduleTestConflict> conflicts = service.testRoomConflict(pc, start, end, room2);
    assertTrue(conflicts != null && conflicts.size() > 0);
    conflicts = service.testRoomConflict(pc, start, end, room3);
    assertFalse(conflicts.size() > 0);

    clear(a1);
    clear(a2);
  }
  

  private Action createAction(DateFr d, DateFr f, Hour hd, Hour hf, int prof, int salle) {
    Action a = new Action();

    a.setCourse(514);// mao
    a.setStartDate(d);
    a.setEndDate(f);
    a.setStartTime(hd);
    a.setEndTime(hf);
    a.setIdper(prof);
    a.setRoom(salle);
    a.setDay(1);
    a.setVacancy(3);// sans vacances
    a.setPeriodicity(Periodicity.WEEK);
    a.setNSessions((short) 1);

    return a;
  }

  private void clear(Action a) throws PlanningException {
    planningService.deletePlanning(a);
  }
}

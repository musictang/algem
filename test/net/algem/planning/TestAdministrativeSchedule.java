/*
 * @(#)TestAdministrativeSchedule.java 2.9.4.3 16/04/15
 * 
 * Copyright (c) 1999-2015 Musiques Tangentes. All Rights Reserved.
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
 * along with Algem. If not, see http://www.gnu.org/licenses.
 * 
 */

package net.algem.planning;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import net.algem.room.Room;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.3
 * @since 2.9.4.1
 */
public class TestAdministrativeSchedule
{

  public TestAdministrativeSchedule() {
  }
  
  @BeforeClass
  public static void setUpClass() {
    
    
  }
  
  @AfterClass
  public static void tearDownClass() {
  }
  
  @Before
  public void setUp() {
  }
  
  @After
  public void tearDown() {
  }

  @Test
  public void testEditedRows() {

    int vacancy = 0;
    List<AdministrativeActionModel> result = new ArrayList<>();
    result.add(createAction(new DayOfWeek(Calendar.MONDAY, PlanningService.getWeekDays()[Calendar.MONDAY]), "10:00", "17:00", 1));
    result.add(createAction(new DayOfWeek(Calendar.MONDAY, PlanningService.getWeekDays()[Calendar.MONDAY]), "10:00", "17:00", 1));
    result.add(createAction(new DayOfWeek(Calendar.TUESDAY, PlanningService.getWeekDays()[Calendar.TUESDAY]), "10:00", "17:00", 1));
    result.add(createAction(new DayOfWeek(Calendar.WEDNESDAY, PlanningService.getWeekDays()[Calendar.WEDNESDAY]), "10:00", "10:00", 1));
    result.add(createAction(new DayOfWeek(Calendar.WEDNESDAY, PlanningService.getWeekDays()[Calendar.WEDNESDAY]), "10:00", "14:00", 1));//
    result.add(createAction(new DayOfWeek(Calendar.THURSDAY, PlanningService.getWeekDays()[Calendar.THURSDAY]), "10:00", "14:00", 2));
    result.add(createAction(new DayOfWeek(Calendar.THURSDAY, PlanningService.getWeekDays()[Calendar.THURSDAY]), "14:00", "17:00", 3));

    List<Action> actions = AdministrativeScheduleCtrl.createActions(result, 1234, new DateFr("16-09-2014"), new DateFr("28-06-2015"), vacancy);
    assertTrue(5 == actions.size());
    for (Action a : actions) {
      System.out.println(a + " : " + PlanningService.getWeekDays()[a.getDay()]);
    }
    assertTrue(new DayOfWeek(Calendar.TUESDAY, PlanningService.getWeekDays()[Calendar.TUESDAY]).getIndex() == actions.get(1).getDay());
    assertEquals(new Hour("10:00"), actions.get(1).getStartTime());
    assertEquals(new Hour("17:00"), actions.get(1).getEndTime());
    assertTrue(1 == actions.get(1).getRoom());

    assertTrue(new DayOfWeek(Calendar.THURSDAY, PlanningService.getWeekDays()[Calendar.THURSDAY]).getIndex() == actions.get(4).getDay());
    assertEquals(new Hour("14:00"), actions.get(4).getStartTime());
    assertEquals(new Hour("17:00"), actions.get(4).getEndTime());
    assertTrue(3 == actions.get(4).getRoom());
    
  }
  
  private AdministrativeActionModel createAction(DayOfWeek dow, String start, String end, int room) {
    AdministrativeActionModel a = new AdministrativeActionModel();
    a.setDay(dow);
    a.setStart(new Hour(start));
    a.setEnd(new Hour(end));
    a.setRoom(new Room(room));
    return a;
  }
}

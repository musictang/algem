/*
 * @(#) TestScheduleTestConflict.java Algem 2.11.0 28/02/2017
 *
 * Copyright (c) 1999-2016 Musiques Tangentes. All Rights Reserved.
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 */
public class TestScheduleTestConflict
{
  
  public TestScheduleTestConflict() {
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
   public void testActionAndDates() {
    int r1 = 3;
    int r2 = 4;
    int r3 = 5;
    int t1 = 16094;
    int t2 = 18468;

    DateFr start =  new DateFr("19-09-2016");
    DateFr end = new DateFr("01-07-2017");
    Hour startTime = new Hour("10:00");
    Hour endTime = new Hour("11:00");
    DateFr[] tDates = {new DateFr("19-09-2016"),new DateFr("26-09-2016")};
    List<DateFr> dates = Arrays.asList(tDates);

     Action a = createAction(start, end, startTime, endTime, t1, r1);
     a.setDates(dates);
     
     List<ScheduleTestConflict> conflicts = new ArrayList<>();
     List<DateFr> aDates = new ArrayList<DateFr>(a.getDates());
     for (int i=0; i < aDates.size(); i++) {
       DateFr d = aDates.get(i);
       conflicts.add(new ScheduleTestConflict(d, startTime, endTime, 0, i));
     }
     ScheduleTestConflict c0 = conflicts.get(0);
     ScheduleTestConflict c1 = conflicts.get(1);
     c0.setRoomFree(false);
     c1.setTeacherFree(false);
     
     assertTrue("c0.getDate() : " +c0.getDate()+ " c1.getDate() " + c1.getDate(), c0.getDate() == aDates.get(0));
     assertTrue(c1.getDate() == aDates.get(1));
     
     
     c0.setDate(new DateFr("20-09-2016"));
     assertFalse(c0.getDate() == aDates.get(0));
     assertNotEquals(aDates.get(0), c0.getDate());
     
     aDates.set(c0.getDateIndex(), c0.getDate());
     assertEquals(aDates.get(0), c0.getDate());
     assertTrue(c0.getDate() == aDates.get(0));
   }
   
   private Action createAction(DateFr start, DateFr end, Hour startTime, Hour endTime, int teacher, int room) {
    
    Action a = new Action();
    
    a.setCourse(514);// mao
    a.setStartDate(start);
    a.setEndDate(end);
    a.setStartTime(startTime);
    a.setEndTime(endTime);
    a.setIdper(teacher);
    a.setRoom(room);
    a.setDay(1);
    a.setVacancy(3);// sans vacances
    a.setPeriodicity(Periodicity.WEEK);
    a.setNSessions((short) 2);

    return a;
  }

}

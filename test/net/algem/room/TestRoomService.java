/*
 * @(#) TestRoomService.java Algem 2.11.0 03/10/2016
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
package net.algem.room;

import net.algem.planning.DateFr;
import net.algem.planning.Hour;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.11.0
 * @since 2.11.0 03/10/2016
 */
public class TestRoomService
{
  
  public TestRoomService() {
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
   public void testIsClosed() {
      DailyTimes [] times = {
        new DailyTimes(1,new Hour(), new Hour()),// sunday
        new DailyTimes(2,new Hour("13:00"), new Hour("22:00")),
        new DailyTimes(3,new Hour("13:00"), new Hour("22:00")),
        new DailyTimes(4,new Hour("13:00"), new Hour("22:00")),
        new DailyTimes(5,new Hour("13:00"), new Hour("22:00")),
        new DailyTimes(6,new Hour("13:00"), new Hour("22:00")),
        new DailyTimes(7,new Hour("11:00"), new Hour("18:00")),
        
      };
      DateFr date = new DateFr("02-10-2016");// dimanche/sunday
      Hour s = new Hour("10:00");
      Hour e = new Hour("12:00");
      
      Hour n = new Hour();
      Hour[] result = isClosed(date,s,times);
      assertTrue(n.equals(result[0]) && n.equals(result[1]));//closed
      
      date = new DateFr("03-10-2016");
      result = isClosed(date,s,times);
      assertTrue(result != null);
      assertEquals(result[0], new Hour("13:00"));
      assertEquals(result[1], new Hour("22:00"));
      
      
      result = isClosed(date,e,times);
      assertTrue(result != null);
      assertEquals(result[0], new Hour("13:00"));
      assertEquals(result[1], new Hour("22:00"));
      
      s = new Hour("13:00");
      e = new Hour("22:00");
      result = isClosed(date,s,times);
      assertTrue(result == null ? "" : result[0].toString()+"-"+result[1].toString(),result == null);
      
      result = isClosed(date,e,times);
      assertTrue(result == null ? "" : result[0].toString()+"-"+result[1].toString(),result == null);
      
      s = new Hour("12:00");
      e = new Hour("14:00");
      result = isClosed(date,s,times);
      assertTrue(result != null);
      assertEquals(result[0], new Hour("13:00"));
      assertEquals(result[1], new Hour("22:00"));
      
      result = isClosed(date,e,times);
      assertTrue(result == null ? "" : result[0].toString()+"-"+result[1].toString(),result == null);
      
      s = new Hour("13:00");
      e = new Hour("22:01");
      result = isClosed(date,e,times);
      assertTrue(result != null);
      assertEquals(result[0], new Hour("13:00"));
      assertEquals(result[1], new Hour("22:00"));
      
      s = new Hour("13:01");
      e = new Hour("21:59:59");
      result = isClosed(date,s,times);
      assertTrue(result == null ? "" : result[0].toString()+"-"+result[1].toString(),result == null);
      result = isClosed(date,e,times);
      assertTrue(result == null ? "" : result[0].toString()+"-"+result[1].toString(),result == null);
     
      s = new Hour("22:15");
      e = new Hour("23:15");
      result = isClosed(date,e,times);
      assertTrue(result != null);
      assertEquals(result[0], new Hour("13:00"));
      assertEquals(result[1], new Hour("22:00"));
      
   }
   private  Hour[] isClosed(DateFr date, Hour h, DailyTimes [] times) {
    int dow = date.getDayOfWeek();
    Hour t[] = new Hour[2];
    t[0] = times[dow - 1].getOpening();
    t[1] = times[dow - 1].getClosing();
    if (h.before(t[0]) || h.after(t[1])) {
      return t;
    }
    return null;

  }
}

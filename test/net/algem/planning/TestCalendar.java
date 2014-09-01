/*
 * @(#)TestCalendar.java	2.8.w 08/07/14
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import static org.junit.Assert.*;
import org.junit.*;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">jean-marc Gobat</a>
 * @version 2.8.w
 * @since 2.8.w 08/07/14
 */
public class TestCalendar
{

  @Test
  public void testDaysInYear() {
    int year = 1998;

    Calendar cal = Calendar.getInstance(Locale.FRANCE);
    cal.set(Calendar.YEAR, year);
    cal.set(Calendar.MONTH, 0);
    cal.set(Calendar.DAY_OF_MONTH, 1);

    Date d = cal.getTime();
    DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
    String start = df.format(d);
    assertEquals("01-01-" + year + " == " + start, "01-01-" + year, start);

    Date[] dates = new Date[cal.getActualMaximum(Calendar.DAY_OF_YEAR)];
    int i = 0;

    while (cal.get(Calendar.YEAR) == year) {
      dates[i++] = cal.getTime();
      cal.add(Calendar.DAY_OF_YEAR, 1);
    }
    String end = df.format(dates[dates.length - 1]);

    assertEquals("31-12-" + year + " == " + end, "31-12-" + year, end);
    
  }

  @Test
  public void setRoomAvailability() {
     int year = 2014;
    DayTimes [] dayTimes = new DayTimes[7];
    String start = "10:00";
    String  end = "22:00";
    for (int i = 0 ; i < 7 ; i++) {
      Hour hs = new Hour(start);
      hs.incHour(i*1);
      dayTimes[i] = new DayTimes(i, hs, new Hour(end));
      
    }
    Calendar cal = Calendar.getInstance(Locale.FRANCE);
    cal.set(Calendar.YEAR, year);
    cal.set(Calendar.MONTH, 5);
    cal.set(Calendar.DAY_OF_MONTH, 30);
    assertTrue(cal.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY);
    System.out.println(cal.get(Calendar.DAY_OF_WEEK));
    for (DayTimes dt : dayTimes) {
      System.out.println(dt);
    }
    
    assertEquals(dayTimes[0].getOpen().toString(), start);
    assertEquals(dayTimes[6].getOpen(),new Hour("16:00"));
    
  }
  
  @Test
  public void testHour2int() {
    Hour h = new Hour("09:00");
    int hi = Integer.parseInt(h.toString().substring(0, h.toString().indexOf(':')));
    
    assertTrue(9 == hi);
    
  }

  class DayTimes
  {

    private int dayOfWeek;
    private Hour open;
    private Hour closed;

    public DayTimes(int dayOfWeek, Hour open, Hour closed) {
      this.dayOfWeek = dayOfWeek;
      this.open = open;
      this.closed = closed;
    }

    public int getDayOfWeek() {
      return dayOfWeek;
    }

    public Hour getOpen() {
      return open;
    }

    public Hour getClosed() {
      return closed;
    }

    @Override
    public String toString() {
      return dayOfWeek + open.toSimpleString() + closed.toSimpleString();
    }

  }

}

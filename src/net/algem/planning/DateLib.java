/*
 * @(#)DateLib.java	2.6.a 21/09/12
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

import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">jean-marc gobat</a>
 * @version 2.6.a
 */
public class DateLib
{

  static int[] MonthLen = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
  final public static long DayMillis = 86400000L;
  final public static long HourMillis = 3600000L;
  final public static long MinuteMillis = 60000L;

  public static int daysInMonth(Calendar d) {
    return daysInMonth(d.get(Calendar.MONTH) + 1, d.get(Calendar.YEAR));
  }

  public static int daysInMonth(Date d) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(d);
    return daysInMonth(cal);
  }

  public static int daysInMonth(int mn, int ye) {
    int mof = 0;
    if ((mn < 1) || (mn > 12)) {
      return 0;
    }
    if ((mn == 2) && (isLeapYear(ye))) {
      mof = 1;
    }
    return MonthLen[mn - 1] + mof;
  }

  public static boolean isLeapYear(int year) {
    if (year % 400 == 0) {
      return true;
    }
    if (year % 100 == 0) {
      return false;
    }

    return (year % 4) == 0;
  }
}

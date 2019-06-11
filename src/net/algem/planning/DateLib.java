/*
 * @(#)DateLib.java	2.16.0 05/03/19
 *
 * Copyright (c) 1999-2019 Musiques Tangentes. All Rights Reserved.
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
import java.util.Locale;

/**
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">jean-marc gobat</a>
 * @version 2.16.0
 */
public class DateLib
{

  static int[] MonthLen = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
  final public static long DayMillis = 86400000L;
  final public static long HourMillis = 3600000L;
  final public static long MinuteMillis = 60000L;

  public static int daysInMonth(Calendar d) {
    return daysInMonth(d.get(Calendar.MONTH), d.get(Calendar.YEAR));
  }

  public static int daysInMonth(Date d) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(d);
    return daysInMonth(cal);
  }

  /**
   * Gets the number of days of the month {@code m} in year {@code y}.
   * @param m month
   * @param y year
   * @return a number of days
   */
  public static int daysInMonth(int m, int y) {
    int extra = 0;
    if ((m < 0) || (m > 11)) {
      return 0;
    }
    if ((m == 1) && (isLeapYear(y))) {
      extra = 1;
    }
    return MonthLen[m] + extra;
  }

  /**
   * Indicates if the {@code year} is leap.
   * @param year
   * @return true if leap
   */
  public static boolean isLeapYear(int year) {
    if (year % 400 == 0) {
      return true;
    }
    if (year % 100 == 0) {
      return false;
    }

    return (year % 4) == 0;
  }

  /**
   * Converts a birthdate into an integer representing an age.
   * @param birth birthDate french-formatted
   * @return an age in number of years
   */
  public static int getAge(DateFr birth) {
    Calendar cal = Calendar.getInstance(Locale.FRANCE);
    int a = cal.get(Calendar.YEAR) - birth.getYear();
    int m = birth.getMonth() -1;
    if (m  > cal.get(Calendar.MONTH)
      || (m == cal.get(Calendar.MONTH) && birth.getDay() > cal.get(Calendar.DATE))) {
      a--;
    }
    return a;
  }
}

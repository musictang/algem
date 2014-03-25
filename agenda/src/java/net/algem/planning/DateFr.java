/*
 * @(#)DateFr.java	1.0.1 08/03/13
 * 
 * Copyright (c) 2013 Musiques Tangentes. All Rights Reserved.
 *
 * This file is part of Algem web module.
 * Algem web module is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Algem web module is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Algem web module. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package net.algem.planning;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Date in french format.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 1.0.1 08/03/13
 * @since 1.0.0
 */
public class DateFr
        implements java.io.Serializable, Comparable
{

  public static String NULLDATE = "00-00-0000";
  private StringBuffer buf;
  //XXXtransient Calendar	cal;
  private Calendar cal;

  public DateFr() {
    cal = Calendar.getInstance(Locale.FRANCE);
    buf = new StringBuffer(NULLDATE);
    //setBuf(cal);
  }

  public DateFr(DateFr h) {
    this();
    if (h != null) {
      buf = new StringBuffer(h.toString());
    }
  }

  public DateFr(String h) {
    this();
    if (h != null && h.length() >= 6) {
      /* pb date postgresql7 */
      if (h.indexOf('-') == 4) {
        buf = new StringBuffer(h.substring(8, 10));
        buf.append('-');
        buf.append(h.substring(5, 7));
        buf.append('-');
        buf.append(h.substring(0, 4));
      } else {
        buf = new StringBuffer(h);
      }
    } else {
      buf = new StringBuffer(NULLDATE);
    }
  }

  public DateFr(Date d) {
    this();
    cal.setTime(d);
    setYear(cal.get(Calendar.YEAR));
    setMonth(cal.get(Calendar.MONTH) + 1);
    setDay(cal.get(Calendar.DAY_OF_MONTH));
  }

  public DateFr(int j, int m, int a) {
    this();
    setYear(a);
    setMonth(m);
    setDay(j);
  }

  public Date getDate() {
    cal.set(Calendar.YEAR, getYear());
    cal.set(Calendar.MONTH, getMonth() - 1);
    cal.set(Calendar.DATE, getDay());
    calibre();
    return cal.getTime();
  }

  private void calibre() {
    cal.set(Calendar.HOUR, 2);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);
  }

  public long getTime() {
    cal.set(getYear(), getMonth() - 1, getDay());
    calibre();
    return cal.getTime().getTime();
  }

  private void setBuf(Calendar d) {
    setYear(d.get(Calendar.YEAR));
    setMonth(d.get(Calendar.MONTH) + 1);
    setDay(d.get(Calendar.DAY_OF_MONTH));
  }

  private void setYear(int a) {
    buf.setCharAt(9, (char) ((a % 10) + '0'));
    a /= 10;
    buf.setCharAt(8, (char) ((a % 10) + '0'));
    a /= 10;
    buf.setCharAt(7, (char) ((a % 10) + '0'));
    a /= 10;
    buf.setCharAt(6, (char) ((a % 10) + '0'));
    a /= 10;
  }

  public int getYear() {
    int a;
    a = (buf.charAt(6) - '0') * 1000;
    a += (buf.charAt(7) - '0') * 100;
    a += (buf.charAt(8) - '0') * 10;
    a += buf.charAt(9) - '0';
    return a;
  }

  private void setMonth(int m) {
    buf.setCharAt(3, (char) ((m / 10) + '0'));
    buf.setCharAt(4, (char) ((m % 10) + '0'));
  }

  public int getMonth() {
    int m;
    m = (buf.charAt(3) - '0') * 10;
    m += buf.charAt(4) - '0';
    return m;
  }

  public void setDay(int j) {
    buf.setCharAt(0, (char) ((j / 10) + '0'));
    buf.setCharAt(1, (char) ((j % 10) + '0'));
  }

  /**
   * Retourne le jour du mois
   * @return un entier
   */
  public int getDay() {
    int j;
    j = (buf.charAt(0) - '0') * 10;
    j += buf.charAt(1) - '0';
    return j;
  }

  int digitAt(int pos) {
    return buf.charAt(pos) - '0';
  }

  void setDigit(int pos, char d) {
    buf.setCharAt(pos, d);
  }

  public void incMonth(int n) {
    cal.setTime(getDate());
    cal.add(Calendar.MONTH, n);
    setBuf(cal);
  }

  public void decMonth(int n) {
    cal.setTime(getDate());
    cal.add(Calendar.MONTH, -n);
    setBuf(cal);
  }

  public void incDay(int n) {
    cal.setTime(getDate());
    cal.add(Calendar.DATE, n);
    setBuf(cal);
  }

  public void decDay(int n) {
    cal.setTime(getDate());
    cal.add(Calendar.DATE, -n);
    setBuf(cal);
  }

  public void incYear(int n) {
    cal.setTime(getDate());
    cal.add(Calendar.YEAR, n);
    setBuf(cal);
  }

  public void decYear(int n) {
    cal.setTime(getDate());
    cal.add(Calendar.YEAR, -n);
    setBuf(cal);
  }

  public boolean before(DateFr h) {
    return getTime() < h.getTime();
  }

  public boolean after(DateFr h) {
    return getTime() > h.getTime();
  }

  public boolean beforeOrEqual(DateFr h) {
    return getTime() <= h.getTime();
  }

  public boolean afterOrEqual(DateFr h) {
    return getTime() >= h.getTime();
  }

  public void set(Date d) {
    if (d != null) {
      cal.setTime(d);
      setDay(cal.get(Calendar.DATE));
      setMonth(cal.get(Calendar.MONTH) + 1);
      setYear(cal.get(Calendar.YEAR));
    } else {
      buf = new StringBuffer(NULLDATE);
    }
  }

  public void set(String s) {
    if (s.length() == 10
            && (s.charAt(2) == '/' || s.charAt(2) == '-')
            && (s.charAt(5) == '/' || s.charAt(5) == '-')) {
      buf = new StringBuffer(s);
    } else {
      buf = new StringBuffer(NULLDATE);
    }
  }

  @Override
  public boolean equals(Object obj) {

    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final DateFr other = (DateFr) obj;
    if (this.buf != other.buf && (this.buf == null || !this.buf.toString().equals(other.buf.toString()))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 3;
    hash = 73 * hash + (this.buf != null ? this.buf.hashCode() : 0);
    hash = 73 * hash + (this.cal != null ? this.cal.hashCode() : 0);
    return hash;
  }

  /*public boolean equals(DateFr d)
  {
  return (d != null && buf.toString().equals(d.buf.toString()));//??
  }*/
  public boolean equals(String d) {
    return buf.toString().equals(d);
  }

  @Override
  public String toString() {
    return buf.toString();
  }

  /**
   * Gets the date on 5 digits, year on one digit only.
   */
  public String toStringShort() {
    String s = buf.toString();
    String prefix = s.substring(0, 2) + s.substring(3, 5);

    if (s.length() >= 10) {
      return prefix + s.charAt(9);
    }

    return prefix + 0;
  }

  public String toStringShort2() {
    String s = buf.toString();
    return s.substring(0, 6) + s.substring(8, 10);
  }

  @Override
  /**
   * Used for sorting in table.
   */
  public int compareTo(Object o) {
    if (before((DateFr) o)) {
      return -1;
    }
    if (after((DateFr) o)) {
      return 1;
    }
    return 0;

  }

  /**
   * Checks if the frenche date {@code d} is valid.
   * Vérification de validité d'une date au format français.
   * @param d a date
   * @return false if invalid or not correctly formatted
   */
  public static boolean isValid(DateFr d) {
    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
    sdf.setLenient(false);
    try {
      sdf.parse(d.toString());
    } catch (ParseException ex) {
      return false;
    }
    return true;

  }
}

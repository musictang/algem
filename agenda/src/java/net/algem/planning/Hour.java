/*
 * @(#)Hour.java	1.0.1 08/03/13
 *
 * Copyright (c) 2013 Musiques Tangentes. All Rights Reserved.
 *
 * This file is part of Algem Agenda.
 * Algem Agenda is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Algem Agenda is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Algem Agenda. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package net.algem.planning;

/**
 * Hour model.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 1.0.1
 * @since 1.0.0 11/02/13
 */
public class Hour
        implements java.io.Serializable
{

  public static String NULL_HOUR = "00:00";
  StringBuffer buf;

  public Hour() {
    buf = new StringBuffer(NULL_HOUR);
  }

  public Hour(Hour h) {
    buf = new StringBuffer(h.toString());
  }

  public Hour(int m) {
    this();
    incMinute(m);
  }

  public Hour(String h) {
    this();
    set(h);
  }

  public Hour(int h, int m) {
    this();
    setHour(h);
    setMinute(m);
  }

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Hour other = (Hour) obj;
		return toMinutes() == other.toMinutes();
	}

	@Override
	public int hashCode() {
		int hash = 7;
		return hash;
	}

  public void setHour(int h) {
    buf.setCharAt(0, (char) ((h / 10) + '0'));
    buf.setCharAt(1, (char) ((h % 10) + '0'));
  }

  public int getHour() {
    int h;
    h = (buf.charAt(0) - '0') * 10;
    h += buf.charAt(1) - '0';
    return h;
  }

  public void setMinute(int m) {
    buf.setCharAt(3, (char) ((m / 10) + '0'));
    buf.setCharAt(4, (char) ((m % 10) + '0'));
  }

  public int getMinute() {
    int m;
    m = (buf.charAt(3) - '0') * 10;
    m += buf.charAt(4) - '0';
    return m;
  }

  public int toMinutes() {
    return ((getHour() * 60) + getMinute());
  }

  public void setDigit(int pos, char d) {
    buf.setCharAt(pos, d);
  }

  public void incMinute(int n) {
    //if (n >= 60)
    //{
    //	incHour(n/60);
    //	n = n%60;
    //}
    int m = getMinute();
    m += n;
    while (m > 59) {
      m -= 60;
      incHour(1);
    }
    setMinute(m);
  }

  public void decMinute(int n) {
    if (n >= 60) {
      decHour(n / 60);
      n = n % 60;
    }
    int m = getMinute();
    m -= n;
    if (m < 0) {
      m += 60;
      decHour(1);
    }
    setMinute(m);
  }

  public void incHour(int n) {
    int h = getHour();
    h += n;
    if (h > 23) {
      h -= 24;
    }
    setHour(h);
  }

  public void decHour(int n) {
    int h = getHour();
    h -= n;
    if (h < 0) {
      h += 24;
    }
    setHour(h);
  }

  /**
   * Gets end time from a length in minutes.
   * @param length
   * @return a time
   */
  public Hour end(int length) {
    Hour f = new Hour(this);
    f.incHour(length / 60);
    f.incMinute(length % 60);
    if (f.toString().equals("00:00")) {
      return new Hour("24:00");
    }
    return f;
  }

  /**
   * Gets end time from a length in hours.
   * @param d time's length
   * @return a time
   */
  public Hour end(Hour d) {
    Hour h = end(d.toMinutes());
    //modification pour les incrémentations tombant sur 00:00
    if (h.toString().equals("00:00")) {
      return new Hour("24:00");
    }
    return h;

  }

  /**
   * Convert 24:00 to 23:59.
   * Used to by-pass default jdbc value for midnight (00:00).
   */
  public void decMidnight() {
    if ("24:00".equals(this.toString())) {
      decMinute(1);
    }
  }

  /**
   * Gets a length from an ending time.
   * @param end
   * @return a length in minutes
   */
  public int getLength(Hour end) {
    int t1, t2;
      // modifier pour transformer les heures 00:00 en 24:00
    t1 = (getHour() * 60) + getMinute();
    t2 = (end.getHour() * 60) + end.getMinute();

    return t2 - t1;
  }

  /**
   * Lower than.
   * @param h
   * @return true if before
   */
  public boolean lt(Hour h) {
    return this.toMinutes() < h.toMinutes();
  }

  /**
   * Lower or equal than.
   * @param h
   * @return true if before or equal
   */
  public boolean le(Hour h) {
    return this.toMinutes() <= h.toMinutes();
  }

  /**
   * Greater than.
   * @param h
   * @return true if after
   */
  public boolean gt(Hour h) {
    return this.toMinutes() > h.toMinutes();
  }

  /**
   * Greater or equal.
   * @param h
   * @return true if after or equal
   */
  public boolean ge(Hour h) {
    return this.toMinutes() >= h.toMinutes();
  }

  public boolean before(Hour h) {
    return this.toMinutes() < h.toMinutes();
  }

  public boolean after(Hour h) {
    return this.toMinutes() > h.toMinutes();
  }

  /**
   * Specifies if this hour is between {@code d} and {@code f}.
   * @param d
   * @param f
   * @return true if between
   */
  public boolean between(Hour d, Hour f) {
    return ge(d) && le(f);
  }

  public void set(String s) {
    if (s != null && s.length() >= 5 && s.charAt(2) == ':') {
      buf = new StringBuffer(s.substring(0, 5));
    }
  }

  public String get() {
    return buf.toString();
  }

  @Override
  public String toString() {
    return buf.toString();
  }

  public String toSimpleString() {
    String min = getMinute() == 0 ? "" : String.valueOf(getMinute());
    return String.valueOf(getHour()) + "h " + min;
  }
}

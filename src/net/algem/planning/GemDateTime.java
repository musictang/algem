/*
 * @(#)GemDateTime.java	2.12.0 07/03/17
 *
 * Copyright (c) 1999-2017 Musiques Tangentes. All Rights Reserved.
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

/**
 * This king of object is used to store one date and one schedule between a start time and end time.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.12.0
 * @since 2.8.t 14/04/14
 */
public class GemDateTime
        implements Comparable
{

  private DateFr date;
  private HourRange timeRange;

  public GemDateTime() {
  }

  /**
   * Constructs a GemDateTime instance with a date and a time range.
   * @param date French-formatted date (dd-mm-yyyy)
   * @param timeRange time range
   */
  public GemDateTime(DateFr date, HourRange timeRange) {
    this.date = date;
    this.timeRange = timeRange;
  }

  public DateFr getDate() {
    return date;
  }

  public void setDate(DateFr date) {
    this.date = date;
  }

  public HourRange getTimeRange() {
    return timeRange;
  }

  public void setTimeRange(HourRange timeRange) {
    this.timeRange = timeRange;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final GemDateTime other = (GemDateTime) obj;
    if (this.date != other.date && (this.date == null || !this.date.equals(other.date))) {
      return false;
    }
    if (this.timeRange != other.timeRange && (this.timeRange == null || !this.timeRange.equals(other.timeRange))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 5;
    hash = 59 * hash + (this.date != null ? this.date.hashCode() : 0);
    hash = 59 * hash + (this.timeRange != null ? this.timeRange.hashCode() : 0);
    return hash;
  }

  @Override
  public String toString() {
    return date + " - " + timeRange;
  }

  @Override
  public int compareTo(Object o) {
    if (o.getClass() != GemDateTime.class) {
      return 1;
    }
    DateFr d1 = this.getDate();
    
    GemDateTime dt = (GemDateTime) o;
    if (d1.before(dt.getDate())) {
      return -1;
    } else if (d1.equals(dt.getDate())) {
        return getTimeRange().equals(dt.getTimeRange()) ? 0 : 1;
    }
    return 1;
  }

}

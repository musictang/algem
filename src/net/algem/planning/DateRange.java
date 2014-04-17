/*
 * @(#)DateRange.java	2.6.a 21/09/12
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

/**
 * Date range field.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">jean-marc gobat</a>
 * @version 2.6.a
 * @since 1.0a 07/07/1999
 */
public class DateRange
        implements java.io.Serializable
{

  private DateFr start;
  private DateFr end;

  public DateRange() {
  }

  public DateRange(DateFr start, DateFr end) {
    this.start = start;
    this.end = end;
  }

  public DateRange(String _start, String _end) {
    start = new DateFr(_start);
    end = new DateFr(_end);
  }

  public boolean isValid()
  {
    //XXX ajouter test +/- 1 an date du jour et fin-debut <= 1 an
    return start != null && end != null
            && !start.bufferEquals(DateFr.NULLDATE)
            && !end.bufferEquals(DateFr.NULLDATE)
            && (end.equals(start) || end.after(start));
  }

  public void setStart(DateFr d) {
    start = d;
  }

  public DateFr getStart() {
    return start;
  }

  public void setEnd(DateFr d) {
    end = d;
  }

  public DateFr getEnd() {
    return end;
  }
}

/*
 * @(#) DateTimeActionModel.java Algem 2.12.0 06/03/17
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
 */

package net.algem.planning;

import java.util.Date;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.12.0
 * @since 2.12.0 01/03/17
 */
public class DateTimeActionModel {

  private DateFr date;
  private Hour start;
  private Hour end;
  private boolean active;

  public DateTimeActionModel() {
    this.date = new DateFr(new Date());
    this.start = new Hour();
    this.end = new Hour();
    this.active = true;
  }

  
  public DateFr getDate() {
    return date;
  }

  public void setDate(DateFr date) {
    this.date = date;
  }

  public Hour getStart() {
    return start;
  }

  public void setStart(Hour start) {
    this.start = start;
  }

  public Hour getEnd() {
    return end;
  }

  public void setEnd(Hour end) {
    this.end = end;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }
  
}

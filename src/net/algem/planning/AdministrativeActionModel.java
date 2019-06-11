/*
 * @(#)AdministrativeActionModel.java	2.9.4.0 18/03/15
 *
 * Copyright (c) 1999-2015 Musiques Tangentes. All Rights Reserved.
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

import java.util.Objects;
import net.algem.room.Room;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.0
 * @since 2.9.4.0 18/03/15
 */
public class AdministrativeActionModel {

  private DayOfWeek day;
  private Hour start;
  private Hour end;
  private Room room;

  public AdministrativeActionModel() {
  }

  public DayOfWeek getDay() {
    return day;
  }

  public void setDay(DayOfWeek day) {
    this.day = day;
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

  public Room getRoom() {
    return room;
  }

  public void setRoom(Room room) {
    this.room = room;
  }

  @Override
  public int hashCode() {
    int hash = 3;
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final AdministrativeActionModel other = (AdministrativeActionModel) obj;
    if (!Objects.equals(this.day, other.day)) {
      return false;
    }
    if (!Objects.equals(this.start, other.start)) {
      return false;
    }
    if (!Objects.equals(this.end, other.end)) {
      return false;
    }
    if (!Objects.equals(this.room, other.room)) {
      return false;
    }
    return true;
  }

}

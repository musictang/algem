/*
 * @(#)PersonalCardSession.java 2.9.2 19/12/14
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

package net.algem.contact.member;

import net.algem.planning.Hour;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.2
 * @since 2.9.2 19/12/14
 */
class PersonalCardSession
{

    private int id;
    private int cardId;
    private int scheduleId;
    private Hour start;
    private Hour end;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getCardId() {
    return cardId;
  }

  public void setCardId(int cardId) {
    this.cardId = cardId;
  }

  public int getScheduleId() {
    return scheduleId;
  }

  public void setScheduleId(int scheduleId) {
    this.scheduleId = scheduleId;
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

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 41 * hash + this.id;
    hash = 41 * hash + this.cardId;
    hash = 41 * hash + this.scheduleId;
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
    final PersonalCardSession other = (PersonalCardSession) obj;
    if (this.id != other.id) {
      return false;
    }
    if (this.cardId != other.cardId) {
      return false;
    }
    if (this.scheduleId != other.scheduleId) {
      return false;
    }
    return true;
  }

}

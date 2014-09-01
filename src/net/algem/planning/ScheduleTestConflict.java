/*
 * @(#)ScheduleTestConflict.java	2.8.v 03/06/14
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

import java.util.Objects;

/**
 * This class is used in the detection of conflicts when creating a schedule.
 * 
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.v
 * @since 1.0a 07/07/1999
 */
public class ScheduleTestConflict
        implements java.io.Serializable
{

  private DateFr date;
  private Hour hStart;
  private Hour hEnd;
  private boolean teacherFree;
  private boolean roomFree;
  private boolean memberFree;
  private boolean groupFree;
  private String detail;

  
  public ScheduleTestConflict(DateFr date, Hour start, Hour end) {
    this.date = date;
    this.hStart = start;
    this.hEnd = end;
    this.teacherFree = true;
    this.roomFree = true;
    this.memberFree = true;
    this.groupFree = true;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final ScheduleTestConflict other = (ScheduleTestConflict) obj;
    if (this.date != other.date && (this.date == null || !this.date.equals(other.date))) {
      return false;
    }
    if (this.hStart != other.hStart && (this.hStart == null || !this.hStart.equals(other.hStart))) {
      return false;
    }
    if (this.hEnd != other.hEnd && (this.hEnd == null || !this.hEnd.equals(other.hEnd))) {
      return false;
    }
    if (this.teacherFree != other.teacherFree) {
      return false;
    }
    if (this.roomFree != other.roomFree) {
      return false;
    }
    if (this.memberFree != other.memberFree) {
      return false;
    }
    if (this.groupFree != other.groupFree) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 3;
    hash = 67 * hash + Objects.hashCode(this.date);
    hash = 67 * hash + Objects.hashCode(this.hStart);
    hash = 67 * hash + Objects.hashCode(this.hEnd);
    hash = 67 * hash + (this.teacherFree ? 1 : 0);
    hash = 67 * hash + (this.roomFree ? 1 : 0);
    hash = 67 * hash + (this.memberFree ? 1 : 0);
    hash = 67 * hash + (this.groupFree ? 1 : 0);
    return hash;
  }

  @Override
  public String toString() {
    return "Conflit :" + date + " " + teacherFree + " " + roomFree;
  }

  public void setDate(DateFr d) {
    date = d;
  }

  public void setDate(java.util.Date d) {
    date = new DateFr(d);
  }

  public DateFr getDate() {
    return date;
  }

  public void setStart(Hour h) {
    hStart = h;
  }

  public Hour getStart() {
    return hStart;
  }

  public void setEnd(Hour h) {
    hEnd = h;
  }

  public Hour getEnd() {
    return hEnd;
  }

  public void setTeacherFree(boolean b) {
    teacherFree = b;
  }

  public boolean isTeacherFree() {
    return teacherFree;
  }

  public void setRoomFree(boolean b) {
    roomFree = b;
  }

  public boolean isRoomFree() {
    return roomFree;
  }

  public void setMemberFree(boolean b) {
    memberFree = b;
  }

  public boolean isMemberFree() {
    return memberFree;
  }

  public void setGroupFree(boolean groupFree) {
    this.groupFree = groupFree;
  }

  public boolean isGroupFree() {
    return groupFree;
  }

  public void setDetail(String s) {
    detail = s;
  }

  public String getDetail() {
    return detail;
  }

  public boolean isConflict() {
    return !(memberFree && roomFree && teacherFree && groupFree);
  }
}

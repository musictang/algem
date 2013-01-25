/*
 * @(#)ScheduleTestConflict.java	2.6.a 20/09/12
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
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 * @since 1.0a 07/07/1999
 */
public class ScheduleTestConflict
        implements java.io.Serializable
{

  private DateFr day;
  private Hour hStart;
  private Hour hEnd;
  private boolean teacherFree;
  private boolean roomFree;
  private boolean memberFree;
  private String detail;

  public ScheduleTestConflict(DateFr _jour, Hour _debut, Hour _fin, boolean _prof, boolean _salle, boolean _adh) {
    day = _jour;
    hStart = _debut;
    hEnd = _fin;
    teacherFree = _prof;
    roomFree = _salle;
    memberFree = _adh;
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
		if (this.day != other.day && (this.day == null || !this.day.equals(other.day))) {
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
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		return hash;
	}

  public ScheduleTestConflict(DateFr day, Hour start, Hour end) {
    this(day, start, end, true, true, true);
  }

  @Override
  public String toString() {
    return "Conflit :" + day + " " + teacherFree + " " + roomFree;
  }

  public void setDay(DateFr d) {
    day = d;
  }

  public void setDay(java.util.Date d) {
    day = new DateFr(d);
  }

  public DateFr getDay() {
    return day;
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

  public void setDetail(String s) {
    detail = s;
  }

  public String getDetail() {
    return detail;
  }

  public boolean isConflict() {
    return !(memberFree && roomFree && teacherFree);
  }
}

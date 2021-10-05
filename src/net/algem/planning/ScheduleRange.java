/*
 * @(#)ScheduleRange.java 2.9.4.13 05/11/15
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

/**
 * Schedule range entity. A range is included in a schedule. In UML, it's a
 * composition. A range is linked to a schedule and a schedule may include
 * several ranges. To facilitate certain operations, some schedule
 * characteristics are retrieved at the range level (teacher, room, course,...).
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.13
 * @since 1.0a 07/07/1999
 */
public class ScheduleRange
        implements java.io.Serializable {

    private static final long serialVersionUID = -8876849805933645098L;

    protected int id;
    protected DateFr day;
    protected Hour start;
    protected Hour end;
    protected int scheduleId;
    protected int courseId;
    protected int memberId;
    protected int teacherId;
    protected int roomId;
    protected int note;

    public ScheduleRange() {
    }

    public ScheduleRange(Schedule p) {
        scheduleId = p.getId();
        memberId = 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ScheduleRange d = (ScheduleRange) o;
        return (day.equals(d.day)
                && courseId == d.courseId
                && memberId == d.memberId
                && start.equals(d.start)
                && end.equals(d.end));
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.day);
        hash = 97 * hash + Objects.hashCode(this.start);
        hash = 97 * hash + Objects.hashCode(this.end);
        hash = 97 * hash + this.courseId;
        hash = 97 * hash + this.memberId;
        hash = 97 * hash + this.teacherId;
        hash = 97 * hash + this.roomId;
        return hash;
    }

    @Override
    public String toString() {
        return start + " " + end;
    }

    public String toLongString() {
        return day + " " + start + " " + end + " (" + roomId + "/" + courseId + "/" + teacherId + "/" + memberId + ")";
    }

    public int getId() {
        return id;
    }

    public void setId(int i) {
        id = i;
    }

    public void setDay(DateFr d) {
        day = d;
    }

    public DateFr getDay() {
        return day;
    }

    public void setStart(Hour h) {
        start = h;
    }

    public Hour getStart() {
        return start;
    }

    public void setEnd(Hour h) {
        end = h;
    }

    public Hour getEnd() {
        return end;
    }

    public void setMemberId(int p) {
        memberId = p;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setCourseId(int c) {
        courseId = c;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setTeacherId(int p) {
        teacherId = p;
    }

    public int getTeacherId() {
        return teacherId;
    }

    public void setRoomId(int s) {
        roomId = s;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setNote(int i) {
        note = i;
    }

    public int getNote() {
        return note;
    }

    public void setScheduleId(int id) {
        this.scheduleId = id;
    }

    public int getScheduleId() {
        return scheduleId;
    }

}

/*
 * @(#)PlanningLib.java	2.6.a 20/09/12
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
 */
public class PlanningLib
        implements java.io.Serializable
{

  private int id;
  private DateFr day;
  private Hour start;
  private Hour end;
  private int courseId;
  private int action;
  private String courseName;
  private int teacherId;
  private String teacherName;
  private int roomId;
  private String roomName;

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final PlanningLib other = (PlanningLib) obj;
    if (this.day != other.day && (this.day == null || !this.day.equals(other.day))) {
      return false;
    }
    if (this.start != other.start && (this.start == null || !this.start.equals(other.start))) {
      return false;
    }
    if (this.end != other.end && (this.end == null || !this.end.equals(other.end))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 17 * hash + (this.day != null ? this.day.hashCode() : 0);
    hash = 17 * hash + (this.start != null ? this.start.hashCode() : 0);
    hash = 17 * hash + (this.end != null ? this.end.hashCode() : 0);
    return hash;
  }

  @Override
  public String toString() {
    return day + " " + start + " " + end;
  }

  public int getID() {
    return id;
  }

  public void setID(int i) {
    id = i;
  }

  public int getAction() {
    return action;
  }

  public void setAction(int action) {
    this.action = action;
  }

  public int getType() {
    return Schedule.COURSE;
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

  public void setTeacherId(int p) {
    teacherId = p;
  }

  public int getTeacherId() {
    return teacherId;
  }

  public void setTeacher(String p) {
    teacherName = p;
  }

  public String getTeacher() {
    return teacherName;
  }

  public void setCourseId(int c) {
    courseId = c;
  }

  public int getCourseId() {
    return courseId;
  }

  public void setCourse(String p) {
    courseName = p;
  }

  public String getCourse() {
    return courseName;
  }

  public void setRoomId(int s) {
    roomId = s;
  }

  public int getRoomId() {
    return roomId;
  }

  public void setRoom(String p) {
    roomName = p;
  }

  public String getRoom() {
    return roomName;
  }
}

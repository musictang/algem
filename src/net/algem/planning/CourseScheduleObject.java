/*
 * @(#)CourseScheduleObject.java	2.9.4.13 05/11/15
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

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.13
 */
public class CourseScheduleObject
        extends Schedule
        implements java.io.Serializable
{

  private static final long serialVersionUID = 3399465614682929593L;
  
  public CourseScheduleObject() {
    type = 1;
  }

  public void setTeacher(int p) {
    idper = p;
  }

  public int getTeacher() {
    return idper;
  }

  public void setCourse(int c) {
    idAction = c;
  }

  public int getCourse() {
    return idAction;
  }
  
  public void setRoom(int s) {
    idRoom = s;
  }

  public int getRoom() {
    return idRoom;
  }

  @Override
  public String toString() {
    return "CourseScheduleObject:" + date + " " + start + " " + end + " " + idAction;
  }
  
}

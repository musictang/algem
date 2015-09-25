/*
 * @(#)ScheduleElement.java	1.0.5 14/09/15
 *
 * Copyright (c) 2015 Musiques Tangentes. All Rights Reserved.
 *
 * This file is part of Algem Agenda.
 * Algem Agenda is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Algem Agenda is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Algem Agenda. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package net.algem.planning;

import java.util.Collection;

/**
 * Schedule element representation.
 * This class is used in calendar to display a time slot with label, position and time.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 1.0.5
 * @since 1.0.0 11/02/13
 */
public class ScheduleElement
        extends Schedule
{

  private String courseName;
  private String personName;
  private String roomName;
  private boolean collective;
  private Collection<ScheduleRange> ranges;
  private String label;

  public String getCourseName() {
    return courseName;
  }

  public void setCourseName(String course) {
    this.courseName = course;
  }

  /**
   * Gets the name of person in the schedule.
   * This person may depend on the type of schedule : teacher's name if it's a course,
   * group's name if it's a band rehearsal or member's name if it's a person rehearsal.
   * @return a name
   */
  public String getPersonName() {
    return personName;
  }

  /**
   * Sets the name of person in the schedule.
   * @param firstname
   * @param name
   */
  public void setPersonName(String firstname, String name) {
    this.personName = firstname == null ? (name == null ? "" : name) : firstname + " " + name;
  }

  /**
   * Gets the name of the room.
   * @return a string representing the name of the room
   */
  public String getRoomName() {
    return roomName;
  }

  /**
   * Sets the name of the room.
   * @param roomName
   */
  public void setRoomName(String roomName) {
    this.roomName = roomName;
  }

  /**
   * Specifies whether the schedule is collective.
   * @return true if collective
   */
  public boolean isCollective() {
    return collective;
  }

  /**
   * Sets the collective status of schedule.
   * @param collective
   */
  public void setCollective(boolean collective) {
    this.collective = collective;
  }

  /**
   * Gets the schedule's label.
   * @return a string
   */
  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final ScheduleElement other = (ScheduleElement) obj;
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 3;
    return hash;
  }

  /**
   * Gets the differents time slots included in schedule (if not collective).
   *
   * @return a collection of time slots
   */
  public Collection<ScheduleRange> getRanges() {
    return ranges;
  }

  /**
   * Sets the differents time slots included in schedule (if not collective).
   *
   * @param ranges
   */
  public void setRanges(Collection<ScheduleRange> ranges) {
    this.ranges = ranges;
  }

  /**
   * Gets the beginning time in minutes.
   * @return a number of minutes
   */
  public int getMinutes() {
    return start.toMinutes();
  }

  /**
   * Gets the element's length.
   *
   * @return a duration in minutes
   */
  public int getLength() {
    return start.getLength(end);
  }

  /**
   * Gets a color value corresponding to the schedule's type.
   *
   * @return a string in hex format
   */
  public String getHtmlColor() {
    String prefix = "#";
    //plage rgb(252,211,0)
    //instrument collectif	rgb(255,128,25)
    switch (type) {
      case Schedule.ROOM:
        return prefix + "CCCCCC";
      case Schedule.COURSE:
        if (isCollective()) {
          return prefix + "FF5252"; //#FF3333 rgb(255,51,51)
        } else {
          return prefix + "00D059"; //#00D059 rgb(0,208,89)
        }
      case Schedule.GROUP:
        return prefix + "2158FF"; //#2158FF rgb(33,88,255)
      case Schedule.MEMBER:
        return prefix + "3399FF"; //#3399FF rgb(51,153,255)
      case Schedule.WORKSHOP:
        return prefix + "F7F7AC"; //#F7F7AC rgb(247,247,172)
      case Schedule.TRAINING:
        return prefix + "F7F77C"; //#F7F7AC rgb(247,247,172)
      default:
        return "#FFFFFF";

    }
  }

}

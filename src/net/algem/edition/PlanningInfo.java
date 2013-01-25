/*
 * @(#)PlanningInfo.java 2.6.a 17/09/12
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

package net.algem.edition;

/**
 * 
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class PlanningInfo {

  private String course;
  private String teacher;
  private String day;
  private String start;
  private String end;

  public PlanningInfo() {
  }

  public PlanningInfo(String course, String teacher, String day, String start, String end) {
    this.course = course;
    this.teacher = teacher;
    this.day = day;
    this.start = start;
    this.end = end;
  }

  public String getTeacher() {
    return teacher;
  }

  public void setTeacher(String t) {
    this.teacher = t;
  }

  public String getCourse() {
    return course;
  }

  public void setCourse(String course) {
    this.course = course;
  }

  public String getStart() {
    return start;
  }

  public void setStart(String start) {
    this.start = start;
  }

  public String getEnd() {
    return end;
  }

  public void setEnd(String end) {
    this.end = end;
  }

  public String getDay() {
    return day;
  }

  public void setDay(String day) {
    this.day = day;
  }


}

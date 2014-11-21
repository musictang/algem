/*
 * @(#)CourseEnrolmentNode.java 2.9.1 18/11/14
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
package net.algem.enrolment;

import net.algem.planning.PlanningService;

/**
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.1
 */
public class CourseEnrolmentNode
        extends EnrolmentNode
{

  private final String[] dayNames = PlanningService.WEEK_DAYS;
  private CourseOrder cc;
  private int day;

  /**
   *
   * @param o
   * @param dayindex
   */
  public CourseEnrolmentNode(Object o, int dayindex) {
    super(o);
    this.day = dayindex;

    if (o instanceof CourseOrder) {
      cc = (CourseOrder) o;
    }

  }

  /**
   *
   * @return a course order
   */
  public CourseOrder getCourseOrder() {
    return cc;
  }

  /**
   * Used to displaying rows in tree.
   * @return a string representing a course order
   */
  @Override
  public String toString() {
    return cc.getTitle() + " " + cc.getStart() + "-" + cc.getEnd() + " " + dayNames[day] + " (" + cc.getDateStart() + "/" + cc.getDateEnd() + ")";
  }

  @Override
  public boolean isLeaf() {
    return true;
  }
  
}

/*
 * @(#)CourseEnrolmentNode.java 2.8.a 04/04/13
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
package net.algem.enrolment;

import net.algem.planning.PlanningService;

/**
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.a
 */
public class CourseEnrolmentNode
        extends EnrolmentNode
{

  private String[] dayNames;
  private CourseOrder cc;
  private int day;

  /**
   *
   * @param o
   * @param _jj
   */
  public CourseEnrolmentNode(Object o, int _jj) {
    super(o);

    day = _jj;

    if (o instanceof CourseOrder) {
      cc = (CourseOrder) o;
    }

    dayNames = PlanningService.WEEK_DAYS;
  }

  /**
   *
   * @return a course order
   */
  public CourseOrder getCourseOrder() {
    return cc;
  }

  /**
   * Used for displaying lines in tree.
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

/*
 * @(#)CourseCodeType.java	2.8.t 11/04/14
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
package net.algem.course;

/**
 * Enumeration of the types of courses defined in enrollment and planning.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.t
 * @since 2.8.t 11/04/14
 */
public enum CourseCodeType {

  ATP(11),
  ATL(3),
  EXT(9),
  FMU(2),
  INS(1),
  FPR(7),
  STG(12);

  private int id;

  private CourseCodeType(int id) {
    this.id = id;
  }

  public int getId() {
    return id;
  }

}

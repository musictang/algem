/*
 * @(#)TeacherComparator.java	2.9.4.0 06/04/15
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

package net.algem.contact.teacher;

import java.util.Comparator;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.0
 * @since 2.7.a 26/11/2012
 */
public class TeacherComparator
  implements Comparator<Teacher>
{

  private char sort = 'n';

  public TeacherComparator() {
  }

  public TeacherComparator(String sort) {
    if (sort != null && sort.length() > 0) {
      this.sort = sort.toLowerCase().charAt(0);
    }
  }

  @Override
  public int compare(Teacher t1, Teacher t2) {
    return 'n' == sort ? t1.getName().compareTo(t2.getName()) : t1.toString().compareTo(t2.toString());
  }

}

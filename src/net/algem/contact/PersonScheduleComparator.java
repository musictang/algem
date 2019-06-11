/*
 * @(#)PersonScheduleComparator.java	2.9.4.8 24/06/15
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

package net.algem.contact;

import java.util.Comparator;
import net.algem.planning.ScheduleRangeObject;

/**
 * Person's comparator.
 * Usually, 2 persons are compared by their firstnames.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.8
 * @since 2.8.a 11/04/13
 */
public class PersonScheduleComparator
  implements Comparator<ScheduleRangeObject>
{

  private char sort = 'n';

  public PersonScheduleComparator() {
  }

  public PersonScheduleComparator(String sort) {
    if (sort != null && sort.length() > 0) {
      this.sort = sort.toLowerCase().charAt(0);
    }
  }

  @Override
  public int compare(ScheduleRangeObject o1, ScheduleRangeObject o2) {
    Person p1 = o1.getMember();
    Person p2 = o2.getMember();
    if (p1 == null) {
      return p2 == null ? 0 : -1;
    } else {
      return p2 == null ? 1 : ('n' == sort ? p1.getNameFirstname().compareToIgnoreCase(p2.getNameFirstname()) : p1.compareTo(p2));
    }

  }

}

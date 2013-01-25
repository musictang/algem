/*
 * @(#)AgeRangeComparator     2.7.a 10/01/13
 *
 * Copyright (c) 2009 Musiques Tangentes All Rights Reserved.
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
package net.algem.config;

import java.util.Comparator;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc</a>
 * @version 2.7.a
 * @since 2.7.a 10/01/13
 */
public class AgeRangeComparator
        implements Comparator
{

  @Override
  public int compare(Object o1, Object o2) {
    Integer a1 = ((AgeRange) o1).getAgemin();
    Integer a2 = ((AgeRange) o1).getAgemax();
    return a1.compareTo(a2);
  }
}

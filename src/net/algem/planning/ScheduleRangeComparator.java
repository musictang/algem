/*
 * @(#)ScheduleRangeComparator.java 2.6.a 19/09/12
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

import java.util.Comparator;

/**
 * Comparateur for schedule ranges.
 * Comparison applies to schedule id.
 * 
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 * @since 2.5.a 10/07/12
 */
public class ScheduleRangeComparator
        implements Comparator<ScheduleRangeObject> {

  @Override
  public int compare(ScheduleRangeObject o1, ScheduleRangeObject o2) {
    Integer i1 = o1.getScheduleId();
    Integer i2 = o2.getScheduleId();
    return i1.compareTo(i2);
  }
}

/*
 * @(#)MusicStyleComparator     2.0l 03/02/10 17:19
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
 */
public class MusicStyleComparator
        implements Comparator
{

  @Override
  public int compare(Object o1, Object o2) {
    MusicStyle g1 = (MusicStyle) o1;
    MusicStyle g2 = (MusicStyle) o2;
    return g1.getLabel().compareTo(g2.getLabel());
  }
}

/*
 * @(#)ModuleComparator.java 2.5.a 03/07/12
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
package net.algem.course;

import java.util.Comparator;

/**
 * Module comparator.
 * The comparison applies to title.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.5.a
 * @since 2.0pc
 */
public class ModuleComparator implements Comparator {

  @Override
  public int compare(Object o1, Object o2) {
    Module m1 = (Module) o1;
    Module m2 = (Module) o2;
    return m1.getTitle().compareTo(m2.getTitle());
  }
}

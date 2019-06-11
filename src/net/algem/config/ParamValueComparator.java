/*
 * @(#)ParamValueComparator.java 2.6.a 06/08/2012
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

package net.algem.config;

import java.util.Comparator;
import java.util.Map;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class ParamValueComparator implements Comparator {
  
  private Map map;

  public ParamValueComparator(Map<String, Param> map) {
    this.map = map;
  }

  @Override
  public int compare(Object o1, Object o2) {
    Param p1 = (Param)map.get(o1);
    Param p2 = (Param)map.get(o2);
    return p1.getValue().compareTo(p2.getValue());
  }

}

/*
 * @(#)AgeRangeList.java 2.6.a 24/09/12
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
 */
package net.algem.config;

import java.util.List;
import net.algem.util.model.GemList;

/**
 * GemList extension for age ranges.
 * 
 * @author <a href="mailto:nicolasnouet@gmail.com">Nicolas Nouet</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 * @since 2.3.a
 */
public class AgeRangeList
        extends GemList {


  public AgeRangeList(List<AgeRange> liste) {
    super(liste);
  }

  public AgeRange getTrancheAge(int id) {
    return (AgeRange) getItem(id);
  }

  public Object getLibelleAt(int index) {
    return ((AgeRange) list.get(index)).getLabel();
  }
  
  /**
   * 
   * @param t
   */
  public void updateElement(AgeRange t) {
    int index = list.indexOf(t);
    if (index >= 0) {
      setElementAt(t, index);
    }
  }

}

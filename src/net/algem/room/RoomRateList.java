/*
 * @(#)RoomRateList.java	2.6.a 24/09/12
 *
 * Copyright (c) 1998-2010 Musiques Tangentes. All Rights Reserved.
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
package net.algem.room;

import java.util.Vector;
import net.algem.util.model.GemList;

/**
 * GemList extension for room rates.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 * @since 2.1a 05/10/10
 */
public class RoomRateList extends GemList {

  public RoomRateList(Vector<RoomRate> list) {
    super(list);
  }

  public RoomRate getRoomRate(int id) {
    return (RoomRate) getItem(id);
  }

  public Object getLibelleAt(int index) {
    return ((RoomRate) list.get(index)).getLabel();
  }

  public Vector<RoomRate> getList() {
    return new Vector<RoomRate>(getData());
  }

  /**
   * Modification d'un élément dans la list des tarifs.
   *
   * @param t
   */
  public void updateElement(RoomRate t) {
    int index = list.indexOf(t);
    if (index >= 0) {
      setElementAt(t, index);
    }
  }

  public boolean removeElement(RoomRate tarif) {
    return list.remove(tarif);
  }
}

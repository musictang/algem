/*
 * @(#)RoomChoice.java	2.7.a 17/01/13
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
package net.algem.room;

import net.algem.util.model.GemList;
import net.algem.util.model.GemModel;
import net.algem.util.ui.GemChoice;
import net.algem.util.ui.GemChoiceFilterModel;
import net.algem.util.ui.GemChoiceModel;

/**
 * GemChoice extension for rooms.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.a
 */
public class RoomChoice
        extends GemChoice
{

  public RoomChoice(GemChoiceModel model) {
    super(model);
  }
  
   public RoomChoice(GemChoiceFilterModel filterModel) {
    super(filterModel);
  }

  public RoomChoice(GemList<Room> list, boolean active) {
    this(active ? new RoomActiveChoiceModel(list, true) : new RoomChoiceModel(list));
  }

  public RoomChoice(GemList<Room> list, int estab) {
    this(new RoomChoiceEstabModel(list, estab));
  }

  public RoomChoice(GemList<Room> list) {
    this(list, true);
  }

  @Override
  public int getKey() {
    return ((GemModel) getSelectedItem()).getId();
  }

  @Override
  public void setKey(int k) {
    ((GemChoiceModel)getModel()).setSelectedItem(k);
  }
  
//  public String toString() {
//    return getKey() > 0 ? ((Room) getSelectedItem()).getName() : "";
//  }

}

/*
 * @(#)ModifPlanRoomView.java	2.7.a 23/11/12
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
package net.algem.planning.editing;

import net.algem.room.Room;
import net.algem.room.RoomChoice;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.model.Model;
import net.algem.util.ui.GemField;
import net.algem.util.ui.GemLabel;
import net.algem.util.ui.GridBagHelper;

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.a
 * @since 1.0a 07/07/1999
 */
public class ModifPlanRoomView
        extends ModifPlanView
{

  private GemField before;
  private RoomChoice after;

  public ModifPlanRoomView(DataCache _dc, String label) {
    super(_dc, label);

    before = new GemField(20);
    before.setEditable(false);
    after = new RoomChoice(dataCache.getList(Model.Room));

    gb.add(new GemLabel(BundleUtil.getLabel("Current.room.label")), 0, 2, 1, 1, GridBagHelper.WEST);
    gb.add(before, 1, 2, 3, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("New.room.label")), 0, 3, 1, 1, GridBagHelper.WEST);
    gb.add(after, 1, 3, 1, 1, GridBagHelper.WEST);
  }

  @Override
  public void setId(int sid) {
    id = sid;
    after.setKey(id);
    before.setText(((Room) after.getSelectedItem()).getName());
  }

  @Override
  public int getId() {
    return after.getKey();
  }

}

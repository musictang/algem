/*
 * @(#)RoomPanel.java	2.8.v 21/05/14
 *
 * Copyright (c) 1999-2014 Musiques Tangentes. All Rights Reserved.
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

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.BorderFactory;
import net.algem.util.model.GemList;
import net.algem.util.ui.RemovablePanel;

/**
 * Room selection panel with removing button.
 * 
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.v
 * @since 2.8.v 21/05/14
 */
public class RoomPanel
        extends RemovablePanel
{

  private RoomChoice room;

  public RoomPanel() {
  }

  public RoomPanel(GemList<Room> roomList) {
    removeBt.setBorder(null);
    room = new RoomChoice(new RoomActiveChoiceModel(roomList, true));
    setLayout(new BorderLayout());
    add(room, BorderLayout.WEST);
    add(removeBt, BorderLayout.EAST);
    setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true)); 
  }

  int getRoom() {
    return room.getKey();
  }

  void setRoom(int r) {
    room.setKey(r);
  }
}

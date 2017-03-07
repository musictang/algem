/*
 * @(#)RoomPanelCtrl.java	2.9.4.3 22/04/15
 *
 * Copyright (c) 1999-2015 Musiques Tangentes. All Rights Reserved.
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
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Box;
import javax.swing.BoxLayout;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.GemCommand;
import net.algem.util.model.Model;
import net.algem.util.ui.AbstractComponentCtrl;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemLabel;
import net.algem.util.ui.GemPanel;

/**
 * This controller is used to add or remove DateTimePanel components.
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.3
 * @since 2.8.v 21/05/14
 */
public class RoomPanelCtrl
        extends AbstractComponentCtrl
{

  private List<RoomPanel> panels;
  private final static int SPACING = 4;
  private DataCache dataCache;

  public RoomPanelCtrl(DataCache dataCache) {
    this.dataCache = dataCache;
    plus = new GemButton("+");
    plus.setMargin(new Insets(0, 4, 0, 4)); //reduction de la taille du bouton
    plus.addActionListener(this);
    plus.setToolTipText(GemCommand.ADD_CMD);
    GemPanel top = new GemPanel(new BorderLayout());
    top.add(new GemLabel(BundleUtil.getLabel("Room.label")), BorderLayout.WEST);
    top.add(plus, BorderLayout.EAST);

    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    add(top);
    add(Box.createVerticalStrut(SPACING));
    panels = new ArrayList<RoomPanel>();
    add();
  }

  public int[] getRooms() {
    int rooms[] = new int[panels.size()];
    for (int i = 0; i < panels.size(); i++) {
      rooms[i] = panels.get(i).getRoom();
    }
    return rooms;
  }

  @Override
  public void add() {
    RoomPanel p = new RoomPanel(dataCache.getList(Model.Room));
    p.addActionListener(this);
    panels.add(p);
    add(panels.get(panels.size() - 1));
    add(Box.createVerticalStrut(SPACING));
  }

  public void remove(GemPanel panel) {
    panels.remove((RoomPanel) panel);
    ((RoomPanel) panel).removeActionListener(this);
    remove(panel);
    revalidate();
  }
  
  @Override
  public void setEnabled(boolean enabled) {
    plus.setEnabled(enabled);
    for(RoomPanel p : panels) {
      p.setEnabled(enabled);
    }
  }

  @Override
  public void clear() {
    for (int i = 1; i < panels.size(); i++) {
      RoomPanel rp = panels.get(i);
      panels.remove(rp);
      remove(rp);
    }
//    panels.get(0).reset();
    revalidate();
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }
}

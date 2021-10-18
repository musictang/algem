/*
 * @(#)RoomEquipEditor.java	2.8.v 27/06/14
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import net.algem.util.BundleUtil;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.FileTab;

/**
 * Equipment editor for a room.
 * 
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.v
 * @since 1.0a 07/07/1999
 */
public class RoomEquipEditor
        extends FileTab
        implements ActionListener
{

  private EquipTableView table;

  public RoomEquipEditor(GemDesktop desktop) {
    super(desktop);

    table = new EquipTableView();
    table.addActionListener(this);

    this.setLayout(new BorderLayout());
    add(table, BorderLayout.CENTER);

  }

  public List<Equipment> getData() {
    return table.getData();
    }
    
  public void clear() {
    table.clear();
  }

  public void load(List<Equipment> ve) {
    for (Equipment e : ve) {
      table.addRow(e);
    }
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    String cmd = evt.getActionCommand();

    if (BundleUtil.getLabel("Action.suppress.label").equals(cmd)) {
      table.deleteCurrent();
    } else if (BundleUtil.getLabel("Action.add.label").equals(cmd)) {
      table.addRow(new Equipment(1, ""));
      table.selectNewCell();
    }
  }

  @Override
  public boolean isLoaded() {
    return true;
  }

  @Override
  public void load() {
    
  }
}

/*
 * @(#)RoomTableModel.java	2.9.4.13 15/10/15
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

import net.algem.util.BundleUtil;
import net.algem.util.ui.JTableModel;

/**
 * Room table model.
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.13
 */
public class RoomTableModel
        extends JTableModel<Room>
{

  private boolean editable;
  
  public RoomTableModel() {
    this(false);
  }
  
  public RoomTableModel(boolean editable) {
     this.editable = editable;
     header = new String[]{
      BundleUtil.getLabel("Id.label"),
      BundleUtil.getLabel("Name.label"),
      BundleUtil.getLabel("Function.label"),
      BundleUtil.getLabel("Place.number.label"),
      BundleUtil.getLabel("Room.active.label"),
      BundleUtil.getLabel("Room.public.label")
    };
  }

  @Override
  public int getIdFromIndex(int i) {
    Room s = tuples.elementAt(i);
    return s.getId();
  }

  @Override
  public Class getColumnClass(int column) {
    switch (column) {
      case 0:
        return Integer.class;
      case 1:
      case 2:
        return String.class;
      case 3:
        return Integer.class;
      case 4:
      case 5:
        return Boolean.class;
      default:
        return Object.class;
    }
  }

  @Override
  public boolean isCellEditable(int row, int column) {
    return editable && column >= 4;
  }

  @Override
  public Object getValueAt(int line, int col) {
    Room r = tuples.elementAt(line);
    switch (col) {
      case 0:
        return Integer.valueOf(r.getId());
      case 1:
        return r.getName();
      case 2:
        return r.getFunction();
      case 3:
        return Integer.valueOf(r.getNPers());
      case 4:
        return r.isActive();
      case 5:
        return r.isAvailable();
    }
    return null;
  }

  @Override
  public void setValueAt(Object value, int line, int col) {
    Room r = tuples.elementAt(line);
    switch(col) {
      case 4:
        r.setActive((boolean) value);
        break;
      case 5:
        r.setAvailable((boolean) value);
        break;
    }
    modItem(line, r);
  }
}

/*
 * @(#)EquipTableModel.java	2.8.m 06/09/13
 * 
 * Copyright (c) 1999-2013 Musiques Tangentes. All Rights Reserved.
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
 * 
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.m
 *
 * @since 1.0a 07/07/1999
 */
public class EquipTableModel
        extends JTableModel
{

  public EquipTableModel() {
    header = new String[]{
      BundleUtil.getLabel("Quantity.abbrev.label"),
      BundleUtil.getLabel("Label.label")
    };
  }

  @Override
  public int getIdFromIndex(int i) {
    return 0;
  }

  @Override
  public Class getColumnClass(int column) {
    switch (column) {
      case 0:
        return Integer.class;
      case 1:
        return String.class;
      default:
        return Object.class;
    }
  }

  @Override
  public boolean isCellEditable(int row, int column) {
    return true;
  }

  @Override
  public Object getValueAt(int ligne, int colonne) {

    Equipment v = (Equipment) tuples.elementAt(ligne);
    switch (colonne) {
      case 0:
        return new Integer(v.getQuantity());
      case 1:
        return v.getLabel();
    }
    return null;
  }

  @Override
  public void setValueAt(Object e, int line, int col) {
    Equipment v = (Equipment) tuples.elementAt(line);
    switch (col) {
      case 0:
        v.setQuantity((Integer) e);
        break;
      case 1:
        v.setLabel((String) e);
        break;
    }
    modItem(line, v);
  }
}

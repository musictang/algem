/*
 * @(#)EquipTableModel.java	2.14.0 13/06/17
 *
 * Copyright (c) 1999-2017 Musiques Tangentes. All Rights Reserved.
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
 * @version 2.14.0
 *
 * @since 1.0a 07/07/1999
 */
public class EquipTableModel
        extends JTableModel<Equipment>
{

  public EquipTableModel() {
    header = new String[]{
      BundleUtil.getLabel("Quantity.abbrev.label"),
      BundleUtil.getLabel("Label.label"),
      BundleUtil.getLabel("Fix.asset.number.label"),
      BundleUtil.getLabel("Public.label")
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
      case 2:
        return String.class;
      case 3:
        return Boolean.class;
      default:
        return Object.class;
    }
  }

  @Override
  public boolean isCellEditable(int row, int column) {
    return true;
  }

  @Override
  public Object getValueAt(int line, int col) {

    Equipment e = tuples.get(line);
    switch (col) {
      case 0:
        return e.getQuantity();
      case 1:
        return e.getLabel();
      case 2:
        return e.getFixedAssetNumber();
      case 3:
        return e.isVisible();
    }
    return null;
  }

  @Override
  public void setValueAt(Object e, int line, int col) {
    Equipment equip = tuples.get(line);
    switch (col) {
      case 0:
        equip.setQuantity((Integer) e);
        break;
      case 1:
        equip.setLabel((String) e);
        break;
      case 2:
        String asset = (String)e;
        equip.setFixedAssetNumber(asset.isEmpty() ? null : asset);
        break;
      case 3:
        equip.setVisible((Boolean) e);
        break;
    }
    modItem(line, equip);
  }
}

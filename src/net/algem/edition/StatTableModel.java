/*
 * @(#)StatTableModel.java 2.10.0 07/06/2016
 *
 * Copyright (c) 1999-2016 Musiques Tangentes. All Rights Reserved.
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
 * along with Algem. If not, see http://www.gnu.org/licenses.
 *
 */
package net.algem.edition;

import net.algem.util.BundleUtil;
import net.algem.util.ui.JTableModel;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.10.0
 * @since 2.10.0 07/06/2016
 */
public class StatTableModel
        extends JTableModel<StatElement>
{

  public StatTableModel() {
    header = new String[]{
      BundleUtil.getLabel("Id.label"),
      BundleUtil.getLabel("Label.label"),
      BundleUtil.getLabel("Action.activate.label")
    };
  }

  @Override
  public int getIdFromIndex(int i) {
    return ((StatElement) getItem(i)).getKey();
  }

  @Override
  public boolean isCellEditable(int row, int column) {
    return column == 2;
  }

  @Override
  public Object getValueAt(int line, int col) {
    StatElement e = tuples.get(line);
    switch (col) {
      case 0:
        return e.getKey();
      case 1:
        return e.getLabel();
      case 2:
        return e.isActive();

    }
    return null;
  }

  @Override
  public Class getColumnClass(int column) {
    switch (column) {
      case 0:
        return Integer.class;
      case 1:
        return String.class;
      case 2:
        return Boolean.class;
      default:
        return Object.class;
    }
  }

  @Override
  public void setValueAt(Object value, int line, int column) {
    StatElement e = tuples.get(line);
    if (column == 2) {
      e.setActive((boolean) value);
    }
  }

}

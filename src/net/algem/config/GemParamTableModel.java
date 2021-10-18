/*
 * @(#)GemParamTableModel.java 2.9.2 26/01/15
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

package net.algem.config;

import net.algem.util.BundleUtil;
import net.algem.util.ui.JTableModel;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.2
 * @since 2.5.a 22/06/2012
 */
public class GemParamTableModel<T extends GemParam>
  extends JTableModel<GemParam>
{

  public GemParamTableModel() {
    header = new String[]{BundleUtil.getLabel("Id.label"),
                          BundleUtil.getLabel("Code.label"),
                          BundleUtil.getLabel("Label.label")};

  }

  @Override
  public int getIdFromIndex(int i) {
    return ((GemParam) getItem(i)).getId();
  }

  @Override
  public Class getColumnClass(int column) {
    switch (column) {
      case 0:
        return Integer.class;
      case 1:
      case 2:
        return String.class;
      default:
        return Object.class;
    }
  }

  @Override
  public Object getValueAt(int line, int col) {
    GemParam n = tuples.get(line);
    switch (col) {
      case 0:
        return n.getId();
      case 1:
        return n.getCode();
      case 2:
        return n.getLabel();
    }
    return null;
  }

  @Override
  public void setValueAt(Object value, int line, int column) {

  }
}

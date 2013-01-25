/*
 * @(#)VacationTableModel.java	2.6.a 20/09/12
 *
 * Copyright (c) 1999 Musiques Tangentes. All Rights Reserved.
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
package net.algem.planning;

import net.algem.util.BundleUtil;
import net.algem.util.ui.JTableModel;

/**
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 * 
 */
public class VacationTableModel
        extends JTableModel
{

  public VacationTableModel() {
    header = new String[]{
      BundleUtil.getLabel("Day.label"), 
      BundleUtil.getLabel("Label.label"),
      BundleUtil.getLabel("Type.label")
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
        return DateFr.class;
      case 1:
        return String.class;
      case 2:
        return Integer.class;
      default:
        return Object.class;
    }
  }

  @Override
  public boolean isCellEditable(int row, int column) {
    return false;
  }

  @Override
  public Object getValueAt(int line, int col) {
    Vacation v = (Vacation) tuples.elementAt(line);
    switch (col) {
      case 0:
        return v.getDay();
      case 1:
        return v.getLabel();
      case 2:
        return new Integer(v.getVid());
    }
    return null;
  }

  @Override
  public void setValueAt(Object value, int line, int col) {
  }
}

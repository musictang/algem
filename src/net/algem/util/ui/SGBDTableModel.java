/*
 * @(#)SGBDTableModel.java	2.6.a 25/09/12
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
package net.algem.util.ui;

import net.algem.util.model.TableSGBD;

/**
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class SGBDTableModel
        extends JTableModel
{

  public SGBDTableModel() {
    header = new String[]{"Table", "Proprio", "tuples", "index", "type", "Atts"};
  }

  @Override
  public int getIdFromIndex(int i) {
    //Champs m = (User)tuples.elementAt(i;
    //return m.getId();
    return -1;
  }

  @Override
  public Class getColumnClass(int column) {
    switch (column) {
      case 0:
      case 1:
        return String.class;
      case 2:
        return Integer.class;
      case 3:
      case 4:
        return String.class;
      case 5:
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
  public Object getValueAt(int ligne, int colonne) {
    TableSGBD m = (TableSGBD) tuples.elementAt(ligne);
    switch (colonne) {
      case 0:
        return m.getName();
      case 1:
        return m.getOwner();
      case 2:
        return Integer.valueOf(m.getNTuples());
      case 3:
        return m.getIndex();
      case 4:
        return m.getType();
      case 5:
        return Integer.valueOf(m.getNAtts());
    }
    return null;
  }

  @Override
  public void setValueAt(Object value, int ligne, int column) {
  }
}

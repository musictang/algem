/*
 * @(#)PersonFileMusicianTableModel.java 2.6.a 31/07/12
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
package net.algem.group;

import net.algem.util.BundleUtil;
import net.algem.util.ui.JTableModel;

/**
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class PersonFileMusicianTableModel
        extends JTableModel
{

  public PersonFileMusicianTableModel() {
    super();
    header = new String[]{"id", 
                          BundleUtil.getLabel("Group.label"),
                          BundleUtil.getLabel("Group.instrument.label")
    };
  }

	@Override
  public int getIdFromIndex(int i) {
    Musician m = (Musician) tuples.elementAt(i);
    return m.getGroup().getId();
  }

  public Musician getMusicien(int i) {
    return (Musician) tuples.elementAt(i);
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
  public boolean isCellEditable(int row, int column) {
    return false;
  }

	@Override
  public Object getValueAt(int line, int col) {
    Musician g = (Musician) tuples.elementAt(line);
    switch (col) {
      case 0:
        return g.getGroup().getId();
      case 1:
        return g.getGroup().getName();
      case 2:
        return g.getInstrument();
    }
    return null;

  }

	@Override
  public void setValueAt(Object value, int line, int col) {
  }
}

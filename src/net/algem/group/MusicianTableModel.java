/*
 * @(#)MusicianTableModel.java	2.16.0 05/03/19
 *
 * Copyright (c) 1999-2019 Musiques Tangentes. All Rights Reserved.
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
import net.algem.util.DataCache;
import net.algem.util.ui.JTableModel;

/**
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.16.0
 */
public class MusicianTableModel
  extends JTableModel<Musician> {

  private DataCache dataCache;

  /**
   * Musician table model, generally speaking.
   * A member is also a musician and this class may be used in contexts where member is involved.
   *
   * @param dataCache cache
   * @param ageIncluded is age included in model
   */
  public MusicianTableModel(DataCache dataCache, boolean ageIncluded) {
    this.dataCache = dataCache;
    if (ageIncluded) {
      header = new String[5];
    } else {
      header = new String[4];
    }
    header[0] = BundleUtil.getLabel("Id.label");
    header[1] = BundleUtil.getLabel("Name.label");
    header[2] = BundleUtil.getLabel("First.name.label");
    header[3] = BundleUtil.getLabel("Instrument.label");
    if (ageIncluded) {
      header[4] = BundleUtil.getLabel("Age.label");
    }
  }

  @Override
  public int getIdFromIndex(int i) {
    Musician p = tuples.get(i);
    return p.getId();
  }

  @Override
  public Class getColumnClass(int column) {
    switch (column) {
      case 0:
        return Integer.class;
      case 1:
      case 2:
      case 3:
        return String.class;
      case 4:
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
    Musician m = tuples.get(ligne);
    switch (colonne) {
      case 0:
        return m.getId();
      case 1:
        return m.getName();
      case 2:
        return m.getFirstName();
      case 3:
        return dataCache.getInstrumentName(m.getInstrument());
      case 4:
        return m.getAge();
    }
    return null;
  }

  @Override
  public void setValueAt(Object value, int ligne, int column) {
  }
}

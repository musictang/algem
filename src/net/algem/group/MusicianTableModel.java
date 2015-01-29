/*
 * @(#)MusicianTableModel.java	2.9.2 26/01/15
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
package net.algem.group;

import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.ui.JTableModel;

/**
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.2
 */
public class MusicianTableModel
        extends JTableModel<Musician>
{

  private DataCache dataCache;

  public MusicianTableModel(DataCache dataCache) {
    this.dataCache = dataCache;
    header = new String[]{
            BundleUtil.getLabel("Id.label"),
			BundleUtil.getLabel("Name.label"),
			BundleUtil.getLabel("First.name.label"),
			BundleUtil.getLabel("Instrument.label")
		};
  }

  @Override
  public int getIdFromIndex(int i) {
    Musician p = tuples.elementAt(i);
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
      default:
        return Object.class;
    }
  }

  @Override
  public boolean isCellEditable(int row, int column) {
    return column > 0;
  }

  @Override
  public Object getValueAt(int ligne, int colonne) {
    Musician p = tuples.elementAt(ligne);
    switch (colonne) {
      case 0:
        return new Integer(p.getId());
      case 1:
        return p.getName();
      case 2:
        return p.getFirstName();
      case 3:
        return dataCache.getInstrumentName(p.getInstrument());
      /*
      if (p instanceof Adherent)
      return ((Adherent)p).getInstrument1();
      else if (p instanceof Enseignant)
      return ((Enseignant)p).getInstrument1();
      else
      return "???";
       */
    }
    return null;
  }

  @Override
  public void setValueAt(Object value, int ligne, int column) {
  }
}

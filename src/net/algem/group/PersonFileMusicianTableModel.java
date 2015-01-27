/*
 * @(#)PersonFileMusicianTableModel.java 2.9.2 26/01/15
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

import java.sql.SQLException;
import net.algem.config.Instrument;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.GemLogger;
import net.algem.util.model.Model;
import net.algem.util.ui.JTableModel;

/**
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.2
 */
public class PersonFileMusicianTableModel
        extends JTableModel<Musician>
{

  public PersonFileMusicianTableModel() {
    super();
    header = new String[]{
      BundleUtil.getLabel("Id.label"),
      BundleUtil.getLabel("Group.label"),
      BundleUtil.getLabel("Instrument.label")
    };
  }

	@Override
  public int getIdFromIndex(int i) {
    Musician m = tuples.elementAt(i);
    return m.getGroup().getId();
  }

  public Musician getMusician(int i) {
    return tuples.elementAt(i);
  }

  @Override
  public Class getColumnClass(int column) {
    switch (column) {
      case 0:
        return Integer.class;
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
    Musician m = tuples.elementAt(line);
    switch (col) {
      case 0:
        return m.getGroup().getId();
      case 1:
        return m.getGroup().getName();
      case 2:
        try {
          Instrument i = (Instrument) DataCache.findId(m.getInstrument(), Model.Instrument);
          return i == null ? "" : i.getName();
        } catch (SQLException ex) {
          GemLogger.logException(ex);
        }
    }
    return null;

  }

	@Override
  public void setValueAt(Object value, int line, int col) {
  }
}

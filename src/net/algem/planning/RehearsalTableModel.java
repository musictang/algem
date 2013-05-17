/*
 * @(#)RehearsalTableModel.java	2.7.d 24/01/13
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
package net.algem.planning;

import java.sql.SQLException;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.GemLogger;
import net.algem.util.model.Model;
import net.algem.util.ui.JTableModel;

/**
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.d
 */
public class RehearsalTableModel
        extends JTableModel
{

  public RehearsalTableModel() {
    header = new String[]{
      BundleUtil.getLabel("Date.label"),
      BundleUtil.getLabel("Start.label"),
      BundleUtil.getLabel("End.label"),
      BundleUtil.getLabel("Room.label")
    };
  }

  @Override
  public int getIdFromIndex(int i) {
    //Plage m = (Plage)tuples.elementAt(i);
    //return m.getId();
    return -1;
  }

  @Override
  public Class getColumnClass(int column) {
    switch (column) {
      case 0:
        return DateFr.class;
      case 1:
        return String.class;
      case 2:
        return String.class;
      case 3:
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
  public Object getValueAt(int ligne, int colonne) {
    Schedule p = (Schedule) tuples.elementAt(ligne);
    switch (colonne) {
      case 0:
        return p.getDate();
      case 1:
        return p.getStart().toString();
      case 2:
        return p.getEnd().toString();
      case 3:
        try {
          return DataCache.findId(p.getPlace(), Model.Room).toString();
        } catch (SQLException ex) {
          GemLogger.logException(ex);
        }
    }
    return null;
  }

  @Override
  public void setValueAt(Object value, int ligne, int column) {
  }
}

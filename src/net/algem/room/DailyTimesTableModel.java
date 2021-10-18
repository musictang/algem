/*
 * @(#)DailyTimesTableModel.java	2.9.2 26/01/15
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

package net.algem.room;

import java.text.DateFormatSymbols;
import java.util.Locale;
import net.algem.planning.Hour;
import net.algem.util.BundleUtil;
import net.algem.util.ui.JTableModel;

/**
 * Daily times table model.
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.2
 * @since 2.8.w 16/07/14
 */
public class DailyTimesTableModel
  extends JTableModel<DailyTimes>
{

  private static final String [] WEEKDAYS = new DateFormatSymbols(Locale.getDefault()).getWeekdays();

  public DailyTimesTableModel() {
    header = new String[]{
      BundleUtil.getLabel("Day.label"),
      BundleUtil.getLabel("Opening.label"),
      BundleUtil.getLabel("Closing.label"),
    };

  }

   @Override
  public boolean isCellEditable(int row, int column) {
    return column > 0;
  }

   @Override
  public Class getColumnClass(int column) {
    switch (column) {
      case 0:
        return String.class;
      case 1:
        return Hour.class;
      case 2:
        return Hour.class;
      default:
        return Object.class;
    }
  }

  @Override
  public Object getValueAt(int line, int col) {
    DailyTimes dt = tuples.get(line);
    if (dt == null) {
      return null;
    }
    switch (col) {
      case 0:
        return WEEKDAYS[dt.getDow()];
      case 1:
        return dt.getOpening();
      case 2:
        return dt.getClosing();
    }
    return null;
  }

  @Override
  public int getIdFromIndex(int i) {
    return 0;
  }

  @Override
  public void setValueAt(Object value, int row, int col) {
    DailyTimes dt = tuples.get(row);
    Hour h = (Hour) value;
    if (col == 1) {
      dt.setOpening(h);
    } else if (col == 2) {
      dt.setClosing(h);
    }
    modItem(row, dt);
  }

}

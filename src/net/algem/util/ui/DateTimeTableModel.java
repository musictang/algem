/*
 * @(#) DateTimeTableModel.java Algem 2.12.0 06/03/17
 *
 * Copyright (c) 1999-2017 Musiques Tangentes. All Rights Reserved.
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
 */

package net.algem.util.ui;

import net.algem.planning.DateTimeActionModel;
import net.algem.planning.DateFr;
import net.algem.planning.Hour;
import net.algem.util.BundleUtil;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.12.0
 * @since 2.12.0 06/03/17
 */
public class DateTimeTableModel 
        extends JTableModel<DateTimeActionModel>
{

  public DateTimeTableModel() {
     header = new String[]{
      BundleUtil.getLabel("Date.label"),
      BundleUtil.getLabel("Start.label"),
      BundleUtil.getLabel("End.label"),
      BundleUtil.getLabel("Active.label")
    };
  }

  @Override
  public boolean isCellEditable(int rowIndex, int columnIndex) {
    return true;
  }

   @Override
  public Class getColumnClass(int col) {
   switch (col) {
      case 0:
        return DateFr.class;
      case 1: 
      case 2:
        return Hour.class;
      case 3:
        return Boolean.class;
      default:
        return Object.class;
    }
  }
  @Override
  public Object getValueAt(int row, int col) {
    DateTimeActionModel a = tuples.elementAt(row);
    switch(col) {
      case 0:
        return a.getDate();
      case 1:
        return a.getStart();
      case 2:
        return a.getEnd();
      case 3:
        return a.isActive();
      default:
        return null;
    }
  }

  @Override
  public int getIdFromIndex(int i) {
    return 0;
  }

  @Override
  public void setValueAt(Object value, int row, int col) {
    DateTimeActionModel a = tuples.elementAt(row);
    switch(col) {
      case 0:
        a.setDate((DateFr) value);
        break;
      case 1:
        a.setStart((Hour) value);
        break;
      case 2:
        Hour end = (Hour) value;
         if (a.getStart() != null && end.le(a.getStart())) {
          end = new Hour(a.getStart());
        }
        a.setEnd(end);
        break;
      case 3:
        a.setActive((Boolean) value);
        break;
    }
  }

}

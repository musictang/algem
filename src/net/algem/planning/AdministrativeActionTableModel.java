/*
 * @(#)AdministrativeActionTableModel.java	2.9.4.0 18/03/15
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

package net.algem.planning;

import java.util.Arrays;
import net.algem.room.Room;
import net.algem.util.BundleUtil;
import net.algem.util.ui.JTableModel;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.0
 * @since 2.9.4.0 18/03/15
 */
public class AdministrativeActionTableModel 
extends JTableModel<AdministrativeActionModel>
{

  public AdministrativeActionTableModel() {
    header = new String[]{
      BundleUtil.getLabel("Day.label"),
      BundleUtil.getLabel("Start.label"),
      BundleUtil.getLabel("End.label"),
      BundleUtil.getLabel("Place.label")
    };
  }

   @Override
  public boolean isCellEditable(int row, int column) {
    return true;
  }
  @Override
  public Class getColumnClass(int col) {
   switch (col) {
      case 0:
        return DayOfWeek.class;
      case 1: 
      case 2:
        return Hour.class;
      case 3:
        return Room.class;
      default:
        return Object.class;
    }
  }
  
  @Override
  public int getIdFromIndex(int i) {
    AdministrativeActionModel a = tuples.get(i);
    return Arrays.asList(PlanningService.getWeekDays()).indexOf(a.getDay());
  }

  @Override
  public Object getValueAt(int line, int col) {
    AdministrativeActionModel a = tuples.get(line);
    switch (col) {
      case 0:
        return a.getDay();
      case 1:
        return a.getStart();
      case 2:
        return a.getEnd();
      case 3:
        return a.getRoom();
      default:
        return null;
    }
  }

  @Override
  public void setValueAt(Object value, int line, int column) {
    AdministrativeActionModel a = tuples.get(line);
    switch(column) {
      case 0:
        a.setDay((DayOfWeek) value);
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
        a.setRoom((Room) value);
        break;
    }
  }

}

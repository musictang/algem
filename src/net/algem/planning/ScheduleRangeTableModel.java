/*
 * @(#)ScheduleRangeTableModel.java	2.11.5 16/01/17
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
 *
 */
package net.algem.planning;

import net.algem.enrolment.FollowUp;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.ui.JTableModel;

/**
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.11.5
 */
public class ScheduleRangeTableModel
        extends JTableModel<ScheduleRangeObject>
{

  protected DataCache dataCache;

  public ScheduleRangeTableModel(DataCache cache) {
    this.dataCache = cache;
    header = new String[]{
      BundleUtil.getLabel("Date.label"),
      BundleUtil.getLabel("Start.label"),
      BundleUtil.getLabel("End.label"),
      BundleUtil.getLabel("Activity.label"),
      BundleUtil.getLabel("Room.label"),
      BundleUtil.getLabel("Teacher.label"),
      BundleUtil.getLabel("Status.label"),
      BundleUtil.getLabel("Note.label"),
      BundleUtil.getLabel("Individual.monitoring.label"),
      BundleUtil.getLabel("Follow.up.label") + " " + BundleUtil.getLabel("Collective.label")
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
      case 2:
      case 3:
      case 4:
      case 5:
      case 6:
      case 7:
      case 8:
      case 9:
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
    ScheduleRangeObject sro = tuples.elementAt(line);
    FollowUp up = sro.getFollowUp();
    switch (col) {
      case 0:
        return sro.getDate();
      case 1:
        return sro.getStart().toString();
      case 2:
        return sro.getEnd().toString();
      case 3:
        return sro.getCourse().getTitle() + sro.getAction().getCodeLabel();
      case 4:
        return sro.getRoom().getName();
      case 5:
        return sro.getPerson().getFirstnameName();
      case 6:
        return up == null || up.getStatus() <= 0 ? "" : up.getStatusFromResult().name();
      case 7:
        return up == null || (up.getNote() == null || "0".equals(up.getNote())) ? "" : up.getNote();
      case 8:
        return up == null || up.toString() == null ? "" : up.toString().replaceAll(System.lineSeparator(), " ");
      case 9:
        return sro.getNote2() == null ? "" : sro.getNote2().replaceAll(System.lineSeparator(), " ");
    }
    return null;
  }

  @Override
  public void setValueAt(Object value, int line, int column) {
  }
}

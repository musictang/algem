/*
 * @(#)CourseTeacherTableModel.java	2.11.5 16/01/17
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
package net.algem.course;

import net.algem.enrolment.FollowUp;
import net.algem.planning.CourseSchedule;
import net.algem.planning.DateFr;
import net.algem.util.BundleUtil;
import net.algem.util.ui.JTableModel;

/**
 * Table model for teacher follow-up.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.11.5
 */
public class CourseTeacherTableModel
        extends JTableModel<CourseSchedule>
{

  public CourseTeacherTableModel() {
    header = new String[]{
      BundleUtil.getLabel("Date.label"),
      BundleUtil.getLabel("Start.label"),
      BundleUtil.getLabel("End.label"),
      BundleUtil.getLabel("Course.label"),
      BundleUtil.getLabel("Member.label"),
      BundleUtil.getLabel("Status.label"),
      BundleUtil.getLabel("Note.label"),
      BundleUtil.getLabel("Follow.up.label")
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
      case 4:
      case 5:
      case 6:
      case 7:
        return String.class;
      case 3:
        return Course.class;
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
    CourseSchedule p = tuples.elementAt(line);
    FollowUp up = p.getFollowUp();
    switch (col) {
      case 0:
        return p.getDate();
      case 1:
        return p.getStart().toString();
      case 2:
        return p.getEnd().toString();
      case 3:
        return p.getCourse();
      case 4:
        return p.getMember() != null ? p.getMember().getFirstnameName() : null;
      case 5:
        return up == null || up.getStatus() <= 0 ? "" : up.getStatusFromResult().name();
      case 6:
        return up == null || (up.getNote() == null || "0".equals(up.getNote())) ? "" : up.getNote();
      case 7:
        return up == null || up.toString() == null ? "" : up.toString().replaceAll(System.lineSeparator(), " ");
    }
    return null;
  }

  @Override
  public void setValueAt(Object value, int line, int column) {
  }
}

/*
 * @(#)CourseOrderTableModel.java	2.9.2 26/01/15
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
package net.algem.enrolment;

import net.algem.planning.Hour;
import net.algem.planning.PlanningService;
import net.algem.util.BundleUtil;
import net.algem.util.ui.JTableModel;

/**
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">jean-marc gobat</a>
 * @version 2.9.2
 */
public class CourseOrderTableModel
        extends JTableModel<CourseOrder>
{

  String[] weekDays;

  public CourseOrderTableModel() {
    weekDays = PlanningService.WEEK_DAYS;
    header = new String[]{
      BundleUtil.getLabel("Type.label"),
      BundleUtil.getLabel("Course.label"),
      BundleUtil.getLabel("Day.label"),
      BundleUtil.getLabel("Hour.label"),
      BundleUtil.getLabel("Duration.label")
    };
  }

  @Override
  public int getIdFromIndex(int i) {
    CourseOrder m = (CourseOrder) tuples.elementAt(i);
    return m.getIdOrder();
  }

  @Override
  public Class getColumnClass(int column) {
    switch (column) {
      case 0:
      case 1:
      case 2:
      case 3:
      case 4:
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
    CourseOrder m = tuples.elementAt(ligne);
    switch (colonne) {
      case 0:
        return m.getCourseModuleInfo().getCode().getLabel();
      case 1:
        return m.getTitle();
      case 2:
        return weekDays[m.getDay() + 1];
      case 3:
        return m.getStart().toString();
      case 4:
        return new Hour(m.getStart().getLength(m.getEnd())).toString();
    }
    return null;
  }

  @Override
  public void setValueAt(Object value, int line, int column) {
  }
}

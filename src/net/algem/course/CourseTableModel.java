/*
 * @(#)CourseTableModel.java	2.6.a 17/09/12
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
package net.algem.course;

import net.algem.planning.Hour;
import net.algem.planning.PlanningService;
import net.algem.util.BundleUtil;
import net.algem.util.ui.JTableModel;

/**
 * 
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class CourseTableModel
        extends JTableModel
{

  static String[] dayNames = PlanningService.WEEK_DAYS;

  public CourseTableModel() {
    header = new String[]{
      "id",
      BundleUtil.getLabel("Title.label"),
      BundleUtil.getLabel("Type.label"), 
      BundleUtil.getLabel("Day.label"),
      BundleUtil.getLabel("Hour.start.label"), 
      BundleUtil.getLabel("Hour.end.label")
    };
  }

  @Override
  public int getIdFromIndex(int i) {
    Course p = (Course) tuples.elementAt(i);
    return p.getId();
  }

  // TableModel Interface
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
      case 5:
        return Hour.class;
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
    Course c = (Course) tuples.elementAt(ligne);
    switch (colonne) {
      case 0:
        return new Integer(c.getId());
      case 1:
        return c.getTitle();
      case 2:
        return c.getCode();
      /* TODOGEM
      case 3:
      return dayNames[c.getJour()+1];
      case 4:
      return c.getHeureDebut();
      case 5:
      return c.getHeureFin();
       */
    }
    return null;
  }

  @Override
  public void setValueAt(Object value, int ligne, int column) {
  }
}

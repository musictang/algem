/*
 * @(#)SubstituteTeacherTableModel.java	2.9.2 26/01/15
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
package net.algem.contact.teacher;

import java.sql.SQLException;
import net.algem.contact.Person;
import net.algem.course.Course;
import net.algem.room.Establishment;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.GemLogger;
import net.algem.util.model.Model;
import net.algem.util.ui.JTableModel;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.2
 * @since 2.0n
 */
public class SubstituteTeacherTableModel
        extends JTableModel<SubstituteTeacher>
{

  public SubstituteTeacherTableModel() {
    header = new String[]{
      BundleUtil.getLabel("Establishment.label"),
      BundleUtil.getLabel("Course.label"),
      BundleUtil.getLabel("Substituted.label"),
      BundleUtil.getLabel("Substitute.label"),
      BundleUtil.getLabel("Favorite.label"),
      "Lu", // TODO i18n
      "Ma",
      "Me",
      "Je",
      "Ve",
      "Sa",
      "Di"
    };
  }

  @Override
  public Class getColumnClass(int column) {
    if (column < 4) {
      return String.class;
    } else {
      return Boolean.class;
    }
  }

  @Override
  public int getIdFromIndex(int i) {
    return 0;
  }

  @Override
  public Object getValueAt(int line, int col) {
    if (tuples.isEmpty()) {
      return null;
    }
    SubstituteTeacher r = tuples.get(line);
    boolean days[] = r.daysToArray();
    switch (col) {
      case 0:
        Establishment e;
        try {
          e = (Establishment) DataCache.findId(r.getEstablishment(), Model.Establishment);
          return e.getName();
        } catch (SQLException ex) {
          GemLogger.logException(ex);
          return "";
        }
      case 1:
        Course c = r.getCourse();
        return c == null ? null : c.getTitle();
      case 2:
        Person t = r.getTeacher();
        return t == null ? null : t.getFirstnameName();
      case 3:
        Person s = r.getSubstitute();
        return s == null ? null : s.getFirstnameName();
      case 4:
        return r.isFavorite();
      case 5:
        return days[0];
      case 6:
        return days[1];
      case 7:
        return days[2];
      case 8:
        return days[3];
      case 9:
        return days[4];
      case 10:
        return days[5];
      case 11:
        return days[6];
    }
    return null;
  }

  @Override
  public void setValueAt(Object value, int line, int col) {
  }

  @Override
  public boolean isCellEditable(int row, int col) {
    return false;
  }
}

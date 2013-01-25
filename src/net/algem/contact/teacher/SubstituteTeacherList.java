/*
 * @(#)SubstituteTeacherList.java	2.6.a 19/09/12
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
package net.algem.contact.teacher;

import java.util.Vector;
import javax.swing.AbstractListModel;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class SubstituteTeacherList
    extends AbstractListModel
{

  private Vector<SubstituteTeacher> list;

  /**
   *
   * @param list
   */
  public SubstituteTeacherList(Vector<SubstituteTeacher> list ){
    this.list = list;
  }

  /**
   *
   * @return an integer
   */
  @Override
  public int getSize() {
    return list.size();
  }

  @Override
  public Object getElementAt(int index) {
    return list.elementAt(index);
  }

  /**
   * Gets the substitute for this teacher {@code id}.
   * @param id
   * @return a substitute or null
   */
  public SubstituteTeacher getTeacher(int id) {
    for (int i = 0; i < list.size(); i++) {
      if (list.elementAt(i).getSubstitute().getId() == id) {
        return list.elementAt(i);
      }
    }
    return null;
  }

  /**
   * Gets the teacher id.
   * @param teacher
   * @return an integer
   */
  public int getId(Object teacher) {
    int index = list.indexOf(teacher);
    if (index >= 0) {
      return list.elementAt(index).getSubstitute().getId();
    }
    return index;
  }
}

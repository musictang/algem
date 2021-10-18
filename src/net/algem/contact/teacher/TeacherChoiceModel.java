/*
 * @(#)TeacherChoiceModel.java	2.7.a 26/11/12
 * 
 * Copyright (c) 1998 Musiques Tangentes. All Rights Reserved.
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

import java.util.List;
import net.algem.util.model.GemList;
import net.algem.util.ui.GemChoiceModel;

/**
 * ComboxBox choice model for teachers.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.a
 */
public class TeacherChoiceModel
        extends GemChoiceModel {

  public TeacherChoiceModel(GemList<Teacher> list) {
    super(list);
  }

  public TeacherChoiceModel(List<Teacher> list) {
    this(new GemList<Teacher>(list));
  }

  public Teacher getTeacher(int id) {
    return (Teacher) list.getItem(id);
  }

}

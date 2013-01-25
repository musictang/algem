/*
 * @(#)TeacherChoice.java	2.7.c 23/01/13
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

import net.algem.util.model.GemList;
import net.algem.util.ui.GemChoice;
import net.algem.util.ui.GemChoiceModel;

/**
 * Combobox for teachers.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.c
 */
public class TeacherChoice
        extends GemChoice {

  public TeacherChoice(GemChoiceModel model) {
    super(model);
    setSelectedIndex(0);
  }

  /**
   * Model is defined by the active parameter.
   *
   * @param list
   * @param active
   */
  public TeacherChoice(GemList<Teacher> list, boolean active) {
    this(active ? new TeacherActiveChoiceModel(list, true) : new TeacherChoiceModel(list));
  }

  public TeacherChoice(GemList<Teacher> list) {
    this(list, true);
  }

  @Override
  public int getKey() {
    Teacher d = (Teacher) getSelectedItem();
    return d == null ? 0 : d.getId();
  }

  @Override
  public void setKey(int k) {
    ((GemChoiceModel)getModel()).setSelectedItem(k);
  }

  /**
   * Gets the person file for this teacher {@code id}.
   *
   * @param id
   * @return a person file
   */
  public Teacher getTeacher(int id) {
    return ((TeacherChoiceModel) getModel()).getTeacher(id);
  }

}
